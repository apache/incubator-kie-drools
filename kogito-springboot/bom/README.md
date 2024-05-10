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

# Kogito Spring Boot BOM

In this module you will find the `kogito-spring-boot-bom`
BOM ([Bill of Materials](https://maven.apache.org/guides/introduction/introduction-to-dependency-mechanism.html#bill-of-materials-bom-poms))
.

This BOM goal is to include all Kogito Core and Spring Boot dependencies in one single file.

Users are encouraged to use this BOM with the [jBPM with Drools Spring Boot Starter](../starters/jbpm-with-drools-spring-boot-starter).

## How to use it

Simply add the BOM in your module's `pom.xml` file:

```xml

<dependencyManagement>
  <dependencies>
    <dependency>
      <groupId>org.kie.kogito</groupId>
      <artifactId>kogito-spring-boot-bom</artifactId>
      <version>${project.version}</version>
      <type>pom</type>
      <scope>import</scope>
    </dependency>
  </dependencies>
</dependencyManagement>
```

If your project has a parent module, add the BOM in the parent module instead of adding in every child module.

> **Maintainers**: The relevant parent modules within the project already have it like [`addons`](../addons) and [`starters`](../starters).

## Adding new dependencies

When a new dependency is needed only by Spring Boot modules add it directly here instead
of [`kogito-build-parent`](../../kogito-build/kogito-build-parent). But remember to keep the **properties** there. It
will be easier to update the versions having only one place to look.

Use the same approach you would for adding a new dependency to Kogito Build Parent
by [following our guidelines](../../CONTRIBUTING.md#requirements-for-dependencies).
