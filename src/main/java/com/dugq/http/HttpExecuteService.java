package com.dugq.http;

import com.dugq.bean.ResponseBean;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultConnectionKeepAliveStrategy;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.protocol.HttpContext;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.Map;

/**
 * Created by dugq on 2021/4/8.
 */
public class HttpExecuteService {

    private static CloseableHttpClient httpClient;

    static {
        httpClient = HttpClientBuilder.create()
                .setDefaultRequestConfig(RequestConfig.custom()
                        .setConnectTimeout(5000)
                        .setSocketTimeout(60000)
                        .setConnectionRequestTimeout(10).build())
                .setMaxConnPerRoute(100)
                .setMaxConnTotal(5000)//一定要设置maxConnTotal，不然默认是10
                .disableAutomaticRetries()//禁止重试
                .disableCookieManagement()
                .disableRedirectHandling()
                .useSystemProperties()//for proxy
                .setKeepAliveStrategy(new DefaultConnectionKeepAliveStrategy() {
                    @Override
                    public long getKeepAliveDuration(HttpResponse response, HttpContext context) {
                        long time = super.getKeepAliveDuration(response, context);
                        if (time == -1) {
                            time = 30000;//链接最多空闲30秒
                        }
                        return time;
                    }
                }).build();
    }

    public static ResponseBean doSendRequest(HttpRequestBase httpRequestBase) throws IOException {

        try (
                CloseableHttpResponse response = httpClient.execute(httpRequestBase);
            ){
            StatusLine statusLine = response.getStatusLine();
            if (statusLine.getStatusCode()>=200 && statusLine.getStatusCode()<300){
                return new ResponseBean(statusLine.getStatusCode(), IOUtils.toString(response.getEntity().getContent(),"UTF-8"),response.getAllHeaders());

            }else{
                return new ResponseBean(response.getStatusLine().getStatusCode(),response.getAllHeaders());
            }
        }
    }

    public static ResponseBean sendGet(String url, Map<String,String> headers, Map<String,String> paramMap) throws IOException {
        if (MapUtils.isNotEmpty(paramMap)){
            url = appendParams(url,paramMap);
        }
        HttpGet httpGet = new HttpGet(url);
        if (MapUtils.isNotEmpty(headers)){
            headers.forEach(httpGet::addHeader);
        }
        return doSendRequest(httpGet);
    }

    public static ResponseBean doPost(String url, Map<String,String> headers, String requestBody) throws IOException {
        HttpPost httpPost = new HttpPost(url);
        if (MapUtils.isNotEmpty(headers)){
            headers.forEach(httpPost::addHeader);
        }
        if (StringUtils.isNotBlank(requestBody)){
            httpPost.setEntity(new StringEntity(requestBody, ContentType.APPLICATION_JSON));
        }
        return doSendRequest(httpPost);
    }

    public static String appendParams(String url, Map<String,String> params){
        StringBuilder sb = new StringBuilder(url);
        if(sb.indexOf("?") != -1){
            sb.append("&");
            sb.append(buildUrlParams(params));
        }else{
            sb.append("?");
            sb.append(buildUrlParams(params));
        }
        return sb.toString();
    }

    public static String buildUrlParams(Map<String,String> params) {
        if (MapUtils.isEmpty(params)) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        params.forEach((key,value)-> {
            String encode = URLEncoder.encode(value);
            sb.append(key).append("=").append(encode).append("&");
        });
        sb.deleteCharAt(sb.length()-1);
        return sb.toString();
    }

}
