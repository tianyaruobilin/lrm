package com.lrm.lrm.core.strategy;

import com.google.common.base.Splitter;
import org.springframework.util.ClassUtils;

/**
 * 为了节省内存，所以用作单例，不能被序列化，序列化单例可能就没了，也没人序列化这玩意儿
 */
public class SubMiddleStrategyNameResolver implements IStrategyNameResolver {

    private SubMiddleStrategyNameResolver() {
    }

    private static class SubMiddleStrategyNameResolverHolder {
        private static SubMiddleStrategyNameResolver INSTANCE = new SubMiddleStrategyNameResolver();
    }

    public static SubMiddleStrategyNameResolver getInstance(){
        return SubMiddleStrategyNameResolverHolder.INSTANCE;
    }


    @SuppressWarnings("UnstableApiUsage")
    @Override
    public String resolveName(String name) {
        String packageName = ClassUtils.getPackageName(name);
        String s = Splitter.on(packageName + ".").omitEmptyStrings().splitToList(name).get(0);
        return Splitter.on("ServiceImpl").omitEmptyStrings().splitToList(s).get(0);
    }
}
