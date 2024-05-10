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

## Kogito Quarkus extensions

This module contains all the code of the extensions that Kogito provides
for Quarkus platform. It applies Quarkus guidelines so please refer to [Quarkus extension guide](https://quarkus.io/guides/writing-extensions)
for more information

### Common module
Most of the code of the extension is shared so that each extension should just provide the specific behavior. Also common
code is divided in runtime and deployment:
- `kogito-quarkus-common`: common runtime code, it only contains substitution required for native compilation for now
- `kogito-quarkus-common-deployment`: most of the extension code is here

### Structure of an extension
Each extension has a common structure:
- `kogito-quarkus-*-extension`: root module of an extension
- `kogito-quarkus-*`: runtime part of the extension, this is the only dependency
the user has to use
- `kogito-quarkus-*-deployment`: compile side of the extension
- `kogito-quarkus-*-integration-test`: this module is intended to contain one or more integration tests
  for the extension. **NOTE**: full integration test coverage should be implemented in the [`integration-tests`](https://github.com/kiegroup/kogito-runtimes/tree/main/integration-tests)
  module while this module is intended to perform a quick sanity check. They are executed by `quarkus-platform` pipelines 
  to make sure the extension works after the inclusion in the platform.
- `kogito-quarkus-*-integration-test-hot-reload` (optional): if the extension supports hot reload feature, it module contains
  integration test of this feature
  
### Create an extension
To add a new extension you should replicate the same structure.

#### Runtime module
- Add quarkus plugins to module `pom.xml`
```xml
  <build>
    <plugins>
      <plugin>
        <groupId>io.quarkus</groupId>
        <artifactId>quarkus-bootstrap-maven-plugin</artifactId>
        <configuration>
          <annotationProcessorPaths>
            <path>
              <groupId>io.quarkus</groupId>
              <artifactId>quarkus-extension-processor</artifactId>
              <version>${version.io.quarkus}</version>
            </path>
          </annotationProcessorPaths>
        </configuration>
        <executions>
          <execution>
            <goals>
              <goal>extension-descriptor</goal>
            </goals>
            <phase>compile</phase>
            <configuration>
              <deployment>${project.groupId}:${project.artifactId}-deployment:${project.version}
              </deployment>
            </configuration>
          </execution>
        </executions>
      </plugin>
    </plugins>
  </build>
```
- Add dependency to `kogito-quarkus-common`
```xml
    <dependency>
      <groupId>org.kie.kogito</groupId>
      <artifactId>kogito-quarkus-common</artifactId>
    </dependency>
```

#### Deployment module
- Add quarkus plugins to module `pom.xml`
```xml
  <build>
    <plugins>
      <plugin>
        <groupId>io.quarkus</groupId>
        <artifactId>quarkus-bootstrap-maven-plugin</artifactId>
        <configuration>
          <annotationProcessorPaths>
            <path>
              <groupId>io.quarkus</groupId>
              <artifactId>quarkus-extension-processor</artifactId>
              <version>${version.io.quarkus}</version>
            </path>
          </annotationProcessorPaths>
        </configuration>
        <executions>
          <execution>
            <goals>
              <goal>extension-descriptor</goal>
            </goals>
            <phase>compile</phase>
            <configuration>
              <deployment>${project.groupId}:${project.artifactId}-deployment:${project.version}
              </deployment>
            </configuration>
          </execution>
        </executions>
      </plugin>
    </plugins>
  </build>
```
- Add dependencies
```xml
    <dependency>
      <groupId>org.kie.kogito</groupId>
      <artifactId>kogito-quarkus-common-deployment</artifactId>
      <version>${project.version}</version>
    </dependency>

    <dependency>
      <groupId>org.kie.kogito</groupId>
      <artifactId>kogito-*</artifactId>
    </dependency>
```

### Testing an extension
Use `kogito-quarkus-*-integration-test` and `kogito-quarkus-*-integration-test-hot-reload` modules to implement simple 
integration tests.

**NOTE:** each extension has a runtime dependency that is declared in integration test `pom.xml` but also an "implicit" 
deployment, if you want to prevent integration tests to run if deployment compilation failed you can add the following dependency

```xml
    <!-- this is used implicitly by quarkus tests so let's make Maven aware of it -->
    <dependency>
      <groupId>org.kie.kogito</groupId>
      <artifactId>kogito-quarkus-*-deployment</artifactId>
      <version>${project.version}</version>
      <type>pom</type>
      <scope>test</scope>
      <exclusions>
        <exclusion>
          <groupId>*</groupId>
          <artifactId>*</artifactId>
        </exclusion>
      </exclusions>
    </dependency>
```