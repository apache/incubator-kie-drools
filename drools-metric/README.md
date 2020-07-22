Drools Metric Analysis module
==================================

This module is developed to help drools metric/performance analysis.

Metric Logging
===================

To enable metric logging,

- Add this module to dependency
- Set system property -Ddrools.metric.logger.enabled=true
- Enable trace level logging for org.drools.metric.util.MetricLogUtils
```
<logger name="org.drools.metric.util.MetricLogUtils" level="trace"/>
```

When you run drools, you will see logging like this.

```
2020-06-16 14:26:21,596 [main] TRACE [JoinNode(6) - [ClassObjectType class=com.sample.Order]], evalCount:1000, elapsedMicro:5962
2020-06-16 14:26:21,693 [main] TRACE [JoinNode(7) - [ClassObjectType class=com.sample.Order]], evalCount:100000, elapsedMicro:95553
2020-06-16 14:26:23,866 [main] TRACE [ AccumulateNode(8) ], evalCount:4999500, elapsedMicro:2172836
2020-06-16 14:26:23,885 [main] TRACE [EvalConditionNode(9)]: cond=com.sample.Rule_Collect_expensive_orders_combination930932360Eval1Invoker@ee2a6922], evalCount:49500, elapsedMicro:18787
```

- evalCount : The number of times that constrains are evaluated in the node
- elapsedMicro : The elapsed time of the node execution (microsecond)

You can also use ReteDumper to match the node in problem so that you can locate the inefficient rule/condition.
