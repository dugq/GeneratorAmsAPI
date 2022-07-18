package com.dugq.service.config.impl;

import com.dugq.pojo.KeyValueBean;
import com.dugq.service.config.AbstractMultiValueConfigService;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.project.Project;
import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author dugq
 * @date 2021/7/20 1:58 下午
 */
public abstract class KeyValueConfigService extends AbstractMultiValueConfigService<KeyValueBean> implements Disposable {

    private Set<KeyValueBean> keyValueBeanList;

    public KeyValueConfigService(Project project,String fileName) {
        super(fileName, project);
        final List<KeyValueBean> keyValueBeans = readList(KeyValueBean.class);
        if (CollectionUtils.isEmpty(keyValueBeans)){
            this.keyValueBeanList = new HashSet<>();
        }else{
            this.keyValueBeanList = new HashSet<>(keyValueBeans);
        }
    }

    @Override
    public void save(KeyValueBean obj) {
        keyValueBeanList.add(obj);
    }

    @Override
    public void delete(KeyValueBean obj) {
        this.keyValueBeanList.remove(obj);
    }

    @Override
    public void write() {
        if (CollectionUtils.isNotEmpty(keyValueBeanList)){
            writeList(getList());
        }
    }

    @Override
    public List<KeyValueBean> getList() {
        return new ArrayList<>(this.keyValueBeanList);
    }

    @Override
    public void dispose() {
        write();
    }

}
