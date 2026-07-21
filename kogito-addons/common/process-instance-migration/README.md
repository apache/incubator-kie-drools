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

# KIE Process Instance Migration Add-on

The KIE Process Instance Migration (PIM) Add-on adds functionality for migrating active processes.This functionality is exposed through management REST endpoints after adding the platform specific dependencies as specified below.

Following two endpoints can be used to migrate active process instances:

1. Endpoint to migrate all active process instances of a given processId to a new target process

Endpoint : POST   /management/processes/{processId}/migrate:

Request body :

```json
{
"targetProcessId": "<>",
"targetProcessVersion": "<>"
}
```

Input parameters:
1. processId: source process identifier (path parameter)
2. targetProcessId : target process identifier(query parameter)
3. targetProcessVersion : target process version(query parameter)

Output:

 The response JSON Schema includes details of processInstanceId migrated to new process:

```json
{
"message": <>,
"numberOfProcessInstanceMigrated":  <>
 }
```
Sample JSON Response :

```json
{
"message": "All instances migrated",
"numberOfProcessInstanceMigrated": 1
 }
```

2. Endpoint to migrate all active process instances of a given processId to a new target process

Endpoint. : POST  /management/processes/{processId}/instances/{processInstanceId}/migrate:

Request body :
```json
{
"targetProcessId": "<>",
"targetProcessVersion": "<>"
}
```
Input parameters:

1. processId: source process identifier (path parameter)
2. processInstanceId: source process instance identifier (path parameter)
3. targetProcessId : target process identifier(query parameter)
4. targetProcessVersion : target process version(query parameter)

Output:

The response JSON Schema includes details of the processInstanceId migrated to new  process
```json
{
"processInstanceId": <>,
"message": <>
}
```
Sample JSON Response Example :

```json
{
"processInstanceId": "1c67ac14-e26a-4f5f-8d42-f4c3bdb691e5",
"message": "1c67ac14-e26a-4f5f-8d42-f4c3bdb691e5 instance migrated"
}
```
### Usage

Add this section to your `pom.xml` file:

1. Quarkus:
```xml
  <dependency>
     <groupId>org.kie</groupId>
     <artifactId>kie-addons-quarkus-process-instance-migration</artifactId>
  </dependency>
```
2. Springboot:
```xml
 <dependency>
     <groupId>org.kie</groupId>
      <artifactId>kie-addons-springboot-process-instance-migration</artifactId>
</dependency>
```

## Examples

See the following examples to explore more about this capability:
  [process-instance-migration-quarkus](https://github.com/kiegroup/kogito-examples/tree/main/kogito-quarkus-examples/process-instance-migration-quarkus)
