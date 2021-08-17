package com.dugq.service.config;

import java.util.List;

/**
 * @author dugq
 * @date 2021/7/8 11:57 下午
 */
public interface MultiValueConfigService<T> {

    /**
     * 保存单个对象
     */
    void save(T obj);

    /**
     * 删除
     */
    void delete(T obj);

    /**
     * 将内存中数据写入到文件
     */
    void write();

    /**
     * 获取所有对象
     */
    List<T> getList();
}
