package com.dugq.service.config;

/**
 * @author dugq
 * @date 2021/8/11 6:53 下午
 */
public interface SingleConfigService<T> {

    /**
     * 读取对象
     */
    T read();
    /**
     * 保存单个对象
     */
    void save(T obj);

    /**
     * 删除
     */
    void delete();

}
