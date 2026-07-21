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

# Kogito Gradle Plugin Test

This module represent a kogito spring-boot application containing rules, decisions, and processes models, managed by gradle.

To directly start it from command line, it is enough to issue
```bash
./gradlew clean bootRun
```

while the following command could be used to build it
```bash
./gradlew clean jar
```

## HOT-RELOAD
That module feature the "org.springframework.boot:spring-boot-devtools" plugin to enable hot reload on source change (additional dependency):

```kts
  developmentOnly 'org.springframework.boot:spring-boot-devtools'
```

To achieve that:
1. in one terminal, issue the command that will listen for code change and, eventually, rebuilt the application on-demand
```bash
./gradlew clean compileSecondaryJava --continuous --parallel --build-cache
```
2. inside another terminal, issue the command that actually start the application
```bash
./gradlew bootRun
```

Whenever a source is modified, the code will be rebuilt and the application re-started with the modifications.


## CI Integration
This module also contains a pom.xml so that it may be built and verified inside CI pipelines.

The maven compilation of java sources is "disabled" inside the pom.xml, whose only scope is to execute the "gradle clean test" tasks (featuring the "exec-maven-plugin") 


