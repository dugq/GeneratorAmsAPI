package com.dugq.service.ams;

import com.dugq.pojo.ams.SimpleApiVo;
import com.dugq.util.RestfulHelper;
import com.intellij.openapi.project.Project;
import org.apache.commons.collections.MapUtils;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author dugq
 * @date 2021/8/15 11:06 下午
 */
public class ApiService {

    private static Map<String,SimpleApiVo> map ;

   public static Map<String,SimpleApiVo> apiList(Project project){
       if (MapUtils.isNotEmpty(map)){
           return map;
       }
       LoginService.login(project);
       final List<SimpleApiVo> simpleApiVos = ApiEditorService.amsAllApi();
       return map = simpleApiVos.stream().collect(Collectors.toMap(api-> RestfulHelper.addPreSeparatorAndRmLastSeparator(api.getApiURI()), Function.identity(),(left, right)->{
           System.out.println("重复的URI："+left.getApiURI()+" ids="+left.getApiID()+","+right.getApiID());
           return left.getApiID()>right.getApiID()?left:right;
       }));
   }

}
