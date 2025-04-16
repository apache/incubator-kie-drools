# SCESIM BACKEND

Currently, SCESIM engine is based on JUnit 4, and it is fired annotating a test class with

```java
@org.junit.runner.RunWith(ScenarioJunitActivator.class)
```

It provides two runners, one for Decision engine and one for Rules engine.

For both runners there is `RunnerHelper` implementing `AbstractRunnerHelper`, namely:

1. `DMNScenarioRunnerHelper`
2. `RuleScenarioRunnerHelper`

Scope of the `RunnerHelper`s is to 
1. instantiate an `ExecutableBuilder`, that contains all the required data to execute a model
2. invoke methods on the above, to fire engine execution and retrieve results

For `ExecutableBuilder` instantiation, the `RuleScenarioRunnerHelper` uses the tradition approach, based on KieContainer and related APIs.
`DMNScenarioRunnerHelper`, on the other side, features the Efesto APIs, thus not requiring the KieContainer instantiation and usage.





