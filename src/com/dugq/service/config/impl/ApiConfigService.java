package com.dugq.service.config.impl;

import com.dugq.pojo.TestApiBean;
import com.dugq.service.config.AbstractMultiValueConfigService;
import com.intellij.openapi.project.Project;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author dugq
 * @date 2021/7/8 3:11 下午
 */
public class ApiConfigService extends AbstractMultiValueConfigService<TestApiBean> {
    private static final String API_LIST_FILE = "/apiList.txt";
    private final Map<String,TestApiBean> apiBeans;

    Project project;

    public ApiConfigService(Project project) {
        super(API_LIST_FILE,project);
        this.project = project;
        final List<TestApiBean> apiBeans = readList(TestApiBean.class);
        if (CollectionUtils.isEmpty(apiBeans)){
            this.apiBeans = new HashMap<>();
        }else{
            this.apiBeans = apiBeans.stream().collect(Collectors.toMap(TestApiBean::getUri, Function.identity(),(left,right)->left));
        }
    }

    public static ApiConfigService getInstance(Project project) {
        return project.getService(ApiConfigService.class);
    }

    @Override
    public void save(TestApiBean apiBean) {
        apiBeans.put(apiBean.getUri(),apiBean);
        write();
    }

    @Override
    public void delete(TestApiBean obj) {
        apiBeans.remove(obj.getUri());
    }

    @Override
    public void write() {
        writeList(getList());
    }

    @Override
    public List<TestApiBean> getList(){
        return new ArrayList<>(apiBeans.values());
    }

    public List<TestApiBean> findByUri(String uri){
        if (StringUtils.isBlank(uri)){
            return getList();
        }
        return apiBeans.values().stream().filter(bean->bean.getUri().contains(uri)).collect(Collectors.toList());
    }

    public TestApiBean findOneByUri(String uri){
        return apiBeans.get(uri);
    }

}
