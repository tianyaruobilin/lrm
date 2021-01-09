package com.lrm.lrm.core.service;

import com.lrm.lrm.core.strategy.IStrategyNameResolver;
import com.lrm.lrm.core.strategy.IStrategyService;
import org.springframework.stereotype.Service;

@Service
public class ATestServiceImpl implements ITestService, IStrategyService {

    @Override
    public IStrategyNameResolver getStrategyNameResolver() {
        return name -> name;
    }

    @Override
    public void test() {
        System.out.println(this.getClass().getName());
    }


}
