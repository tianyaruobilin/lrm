package com.lrm.lrm.core.strategy;

import com.test.lrm.service.ATestServiceImpl;
import com.test.lrm.service.BTestServiceImpl;
import com.test.lrm.service.ITestService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.util.Asserts;
import org.springframework.util.Assert;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 哈哈哈哈，简单的万能的策略上下文，只需要传入的name唯一,其实就是一个类似于IOC容器的东西，小单例，不能实例化。用于简化if-else啥的都是洒洒水。
 * 也可以扩展在没有注册指定策略时，通过bean名字和class简称获取bean实例作为策略类……但是因为有多实现的情况，需要使用beanname为子类的简称。
 * 如果想要实现这么自动注册，建议监听spring的ApplicationReadyEvent事件或者在容器refresh调用后，实现自动注册
 *
 * @author 李润民
 */
@Slf4j
class StrategyContext {

    private static IStrategyNameResolver defaultStrategyNameResolver = s -> s;

    /**
     * 其实这个容器也不大好，但是简单的一个策略上下文，我认为这样就差不多了……缓存太多也不好不是嘛，还有就是内存别占用太多了，能不实例化就不实例化，
     * 至于存储到数据库吧，如果对于性能没有要求，请君自便，map就搁在这了，自己写方法，但不建议，影响效率，原因如下：
     * 1、存在网络传输性能消耗
     * 2、数据库中不能存储对象，存储的肯定是实现类的全类名。需要通过反射，不能很好的利用spring bean的单例
     */
    private static Map<String, Object> map = new ConcurrentHashMap<>();

    private StrategyContext() {
        //私有构造方法内抛出异常保证了单例不会被实例化
        throw new UnsupportedOperationException("CAN NOT INSTANCE");
    }

    /**
     * 注册策略通过自定义的名字
     *
     * @param name   策略名字
     * @param object 策略对象
     * @param clazz  策略对象类型，可以用子类，也可以用父类，看自己需要，这里填写啥就会转换成啥
     * @param <T>    参数clazz的泛型
     */
    public static <T> void registStrategy2Context(String name, IStrategyNameResolver strategyNameResolver, Object object, Class<T> clazz) {
        if (strategyNameResolver == null) {
            //如果没有设置策略，使用默认策略
            log.debug("use default strategyName resolver");
            strategyNameResolver = defaultStrategyNameResolver;
        } else {
            log.debug("use [{}] to resolve strategy name", strategyNameResolver.getClass().getName());
        }
        Asserts.notBlank(name, "regist strategy name can not be blank");
        String resolvedKey = strategyNameResolver.resolveName(name);
        Assert.isTrue(!map.containsKey(StringUtils.deleteWhitespace(resolvedKey)), "regist strategy name[" + resolvedKey + "] duplicate");
        Asserts.notNull(object, "cannot regist strategy for name " + resolvedKey + " because is null");
        Assert.isAssignable(clazz, object.getClass(), object.getClass().getName() + " cannot cast to " + clazz.getName());
        map.put(resolvedKey, clazz.cast(object));
        log.info("regist strategy[{}] to strategyContext use object[{}] and class[{}]", resolvedKey, object, clazz);
    }

    /**
     * 注册策略通过object的类全类名，用于后续可能存在的扩展为当策略类不存在时，通过spring容器获取对应的bean
     *
     * @param object 策略对象
     * @param clazz  策略对象类型，这里一般填写子类，如果填写父类，在多实现的情况下可能会出现key被占用的情况
     * @param <T>    参数clazz的泛型
     */
    public static <T> void registStrategy2Context(Object object, IStrategyNameResolver strategyNameResolver, Class<T> clazz) {
        registStrategy2Context(object.getClass().getName(), strategyNameResolver, object, clazz);
    }

    /**
     * 获取指定策略名的策略,没有策略配置时返回给定的默认策略
     *
     * @param name            指定的策略名
     * @param clazz           策略类
     * @param defaultStrategy 默认策略对象
     * @param <T>             参数clazz的泛型
     * @return 响应策略类
     */
    public static <T> T getStrategyByName(String name, IStrategyNameResolver strategyNameResolver, Class<T> clazz, T defaultStrategy) {
        Asserts.notBlank(name, "regist strategy name can not be blank");
        if (strategyNameResolver == null) {
            //如果没有设置策略，使用默认策略
            log.debug("use default strategyName resolver");
            strategyNameResolver = defaultStrategyNameResolver;
        } else {
            log.debug("use [{}] to resolve strategy name", strategyNameResolver.getClass().getName());
        }
        String resolvedName = strategyNameResolver.resolveName(name);
        Object strategyObject = map.getOrDefault(resolvedName, defaultStrategy);
        Assert.isAssignable(clazz, strategyObject.getClass(), String.format("%s cannot cast to %s", strategyObject.getClass().getName(), clazz.getName()));
        return clazz.cast(strategyObject);
    }

    /**
     * 是否包含指定名字的策略
     *
     * @param name 指定名字
     * @return true/false
     */
    public static boolean containsName(String name) {
        Asserts.notBlank(name, "regist strategy name can not be blank");
        return map.containsKey(name);
    }


    public static Class<?> getFullStrategyClass(String name,IStrategyNameResolver strategyNameResolver) {
        Asserts.notBlank(name, "name can not be blank");
        IStrategyNameResolver iStrategyNameResolver = Optional.ofNullable(strategyNameResolver).orElse(defaultStrategyNameResolver);
        String key = iStrategyNameResolver.resolveName(name);
        Object o = map.get(key);
        Asserts.notNull(o,"strategy is null");
        return o.getClass();
    }

    @SneakyThrows
    public static void main(String[] args) {
        ATestServiceImpl Astrategy = new ATestServiceImpl();
        BTestServiceImpl Bstrategy = new BTestServiceImpl();
        ITestService strategyByName = new ITestService() {
            @Override
            public void test() {
                System.out.println(this.getClass().getName());
            }
        };
        Astrategy.afterPropertiesSet();
        Bstrategy.afterPropertiesSet();
        ITestService strategyByName1 = StrategyContext.getStrategyByName(Astrategy.getClass().getName(), null,ITestService.class, null);
        ITestService strategyByName2 = StrategyContext.getStrategyByName(Bstrategy.getClass().getName(), null,ITestService.class, null);
        ITestService strategyByName3 = StrategyContext.getStrategyByName(strategyByName.getClass().getName(), null,ITestService.class, null);
        System.out.println("strategyByName3 = " + strategyByName3);
        System.out.println("strategyByName2 = " + strategyByName2);
        System.out.println("strategyByName1 = " + strategyByName1);
        //所以我们可以从类名上面下手，做成默认的策略类，命名规范的重要性。

    }
}
