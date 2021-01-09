package com.lrm.lrm.core.strategy;

import org.springframework.util.Assert;

public class StrategyTemplate {

    public static <T> void registerStrategy(String name, IStrategyNameResolver strategyNameResolver, Object object, Class<T> clazz){
        StrategyContext.registStrategy2Context(name,strategyNameResolver,object,clazz);
    }

    public static <T> T getStrategyService(String name, Class<T> clazz,T defaultStrategyService){
        return StrategyContext.getStrategyByName(name, null,clazz,defaultStrategyService);
    }

    public static <T> T getStrategyService(String name, IStrategyNameResolver strategyNameResolver,Class<T> clazz,T defaultStrategyService){
        Class<?> classByStrategyName = getClassByStrategyName(name, strategyNameResolver);
        Assert.isAssignable(clazz,classByStrategyName,"cannot cast class");//TODO: 优化提示
        return StrategyContext.getStrategyByName(name, strategyNameResolver,clazz,defaultStrategyService);
    }
    public static Class<?> getClassByStrategyName(String name, IStrategyNameResolver strategyNameResolver){
        return StrategyContext.getFullStrategyClass(name, strategyNameResolver);
    }
}
