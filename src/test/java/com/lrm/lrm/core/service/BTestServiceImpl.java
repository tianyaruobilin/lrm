package com.lrm.lrm.core.service;

import com.lrm.lrm.core.strategy.IStrategyService;
import org.springframework.stereotype.Service;

@Service
public class BTestServiceImpl implements ITestService, IStrategyService {

    @Override
    public void test() {
        System.out.println(this.getClass().getName());
    }
}
