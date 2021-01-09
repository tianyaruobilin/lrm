# lrm
一些比较杂的工具类和自己的小封装
## 策略上下文-配合Spring。
```com.lrm.lrm.core.strategy包```

把spring的bean通过类名作为策略上下文的key，使用的时候直接通过`com.lrm.lrm.core.strategy.StrategyTemplate`获取对应策略类。

```java
//com.lrm.lrm.core.service.ITestService
ITestService testService=StrategyTemplate.getStrategyService("ATest",ITestService.class,null);
Optional.ofNullable(testService).orElseThrow(()->new RuntimeException("no suitable strategy serviceImpl found"));
testService.test();
```
