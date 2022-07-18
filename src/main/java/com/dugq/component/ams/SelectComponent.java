package com.dugq.component.ams;

import com.dugq.exception.StopException;
import com.dugq.pojo.ams.GroupVo;
import org.apache.commons.collections.CollectionUtils;

import java.util.List;

/**
 * @author dugq
 * @date 2021/7/7 2:09 下午
 */
public class SelectComponent {

    public static GroupVo getGroupVo(List<GroupVo> groupVos, String uri) {
        SelectInputComponent comboBox = new SelectInputComponent(groupVos,uri);
        if(!comboBox.showAndGet()){
            throw new StopException();
        }
        GroupVo groupVo = groupVos.get(comboBox.getBox().getSelectedIndex());
        if(CollectionUtils.isNotEmpty(groupVo.getChildGroupList())){
            return getGroupVo(groupVo.getChildGroupList(), uri);
        }
        return groupVo;
    }
}
