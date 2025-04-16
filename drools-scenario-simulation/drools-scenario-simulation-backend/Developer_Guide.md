<!--
  Licensed to the Apache Software Foundation (ASF) under one
  or more contributor license agreements.  See the NOTICE file
  distributed with this work for additional information
  regarding copyright ownership.  The ASF licenses this file
  to you under the Apache License, Version 2.0 (the
  "License"); you may not use this file except in compliance
  with the License.  You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing,
  software distributed under the License is distributed on an
  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
  KIND, either express or implied.  See the License for the
  specific language governing permissions and limitations
  under the License.
  -->
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





