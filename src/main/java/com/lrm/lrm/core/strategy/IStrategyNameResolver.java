package com.lrm.lrm.core.strategy;

/**
 * 策略类策略名解析器
 */
public interface IStrategyNameResolver {

    /**
     * 解析策略的key，可以自行定义，用于把传入的key，解析为放入容器的key
     * @param name 传入的key
     * @return 解析后的key
     */
    public String resolveName(String name);

}
