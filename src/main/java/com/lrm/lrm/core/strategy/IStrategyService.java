package com.lrm.lrm.core.strategy;

import org.springframework.beans.factory.InitializingBean;

/**
 * 仅仅只做了自动注册策略类的功能。
 * 默认通过子类的全类名作为策略的key，因此优雅实现策略的方式建议如下
 * 1、子类实现IStrategyService。
 * 2、子类被容器扫描上
 * 3、子类的名字为策略name+StrategyServiceImpl,获取实例时如果获取不到会默认通过获取子类的名字获取实现类。
 * 如果觉得该注册方法不满足要求的，子类可以重写，afterPropertiesSet()方法
 */
public interface IStrategyService extends InitializingBean {

    /**
     * 想要为指定的key设置策略name解析器可以直接重写该方法
     * 如果不指定默认是originKey
     * @return 返回需要设置的策略key解析器
     */
    default IStrategyNameResolver getStrategyNameResolver(){
        return SubMiddleStrategyNameResolver.getInstance();
    }

    /**
     * 如果想要指定不通过实现类的全类名作为原始被处理的输入key,可以重写该方法
     * @return 想要用来被处理的候选key
     */
    default String getOriginKey(){
        return this.getClass().getName();
    }


    /**
     * 其实我们有很多方法可以实现注册一个策略，列举部分如下
     * 1、监听spring内置的ApplicationReadyEvent事件,实现自己的Aware
     * 2、@PostConstract
     * 3、implements InitializingBean，实现afterPropertiesSet方法
     * 4、静态代码块
     * 5、……
     * 注意：这种写法需要JDK1.8
     * 这里默认类全名注册
     */
    @Override
    default void afterPropertiesSet() {
        StrategyTemplate.registerStrategy(getOriginKey(), getStrategyNameResolver(),this, this.getClass());
    }
}
