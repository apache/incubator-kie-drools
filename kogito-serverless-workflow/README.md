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

# Kogito Serverless Workflow

Kogito is an implementation of the [CNCF Serverless Workflow Specification](https://serverlessworkflow.io/).

## Current Status

Currently, Kogito implements the [**version
0.8**](https://github.com/serverlessworkflow/specification/blob/0.8.x/specification.md) of the specification.

The following table lists the current status of the features as defined by the specification:

| Feature       | Status               |
| ------------- | -------------------- |
| [States](https://github.com/serverlessworkflow/specification/blob/0.8.x/specification.md#State-Definition) | :first_quarter_moon: |
| [Functions](https://github.com/serverlessworkflow/specification/blob/0.8.x/specification.md#Function-Definition) | :first_quarter_moon: |
| [Events](https://github.com/serverlessworkflow/specification/blob/0.8.x/specification.md#Event-Definition) | :first_quarter_moon: |
| [Retries](https://github.com/serverlessworkflow/specification/blob/0.8.x/specification.md#Retry-Definition) | :construction:       |
| [Workflow Data](https://github.com/serverlessworkflow/specification/blob/0.8.x/specification.md#Workflow-Data) | :first_quarter_moon:          |
| [Expressions](https://github.com/serverlessworkflow/specification/blob/0.8.x/specification.md#Workflow-Expressions) | :full_moon:          |
| [Error Handling](https://github.com/serverlessworkflow/specification/blob/0.8.x/specification.md#Workflow-Error-Handling) | :full_moon:         |
| [Compensation](https://github.com/serverlessworkflow/specification/blob/0.8.x/specification.md#Workflow-Compensation) | :full_moon:         |

Legend:

| Symbol        | Meaning              |
| ------------- | -------------------- |
| :full_moon: | Fully implemented |
| :first_quarter_moon: | Partially implemented |
| :construction: | To be implemented |

The sections below describe in detail the current status of the supported features.

### Workflow Model - States

| State         | Status      |
| ------------- | ----------- |
| Event         | :first_quarter_moon: |
| Operation     | :full_moon: |
| Switch        | :full_moon: |
| Delay         | :full_moon: |
| Parallel      | :full_moon: |
| Inject        | :full_moon: |
| ForEach       | :full_moon: |
| Callback      | :full_moon: |

> Event state is not supported as starting state if exclusive flag is set to false.
> ⚠️ Quarkus is the only supported runtime for Kogito Serverless Workflow.

#### Examples

1. [Event Example](https://github.com/kiegroup/kogito-examples/tree/main/serverless-workflow-examples/serverless-workflow-events-quarkus)
2. [Operation Example](https://github.com/kiegroup/kogito-examples/tree/main/serverless-workflow-examples/serverless-workflow-functions-quarkus)
3. [Switch, Parallel, and SubFlow Example](https://github.com/kiegroup/kogito-examples/tree/main/serverless-workflow-examples/serverless-workflow-order-processing)
4. [Inject and Switch Example](https://github.com/kiegroup/kogito-examples/tree/main/serverless-workflow-examples/serverless-workflow-greeting-quarkus)
5. [Callback Example](https://github.com/kiegroup/kogito-examples/tree/main/serverless-workflow-examples/serverless-workflow-callback-quarkus)

### Workflow Model - Functions

| Function Type | Status             | Obs |
| ------------- | ------------------ | --- |
| rest          | :full_moon: | You can find more details about the Kogito OpenAPI implementation [here](../kogito-codegen-modules/kogito-codegen-openapi) |
| rpc           | :full_moon: | |
| expression    | :full_moon: | Either `jq` or `jsonpath` |
| asyncapi      | :construction: | |
| graphql       | :construction: | |
| odata         | :construction: | |
| custom        | :full_moon: | |

Additionally, even though they are not defined in the specification, Kogito also supports `sysout` and `java` functions.

#### Sysout Functions

This function support can be used for debugging reasons:

```json
{
  "functions": [
    {
      "name": "printMessage",
      "metadata": {
        "type": "sysout"
      }
    }
  ]
}
```

Later in your State definition you can call it with:

```json
{
  "states": [
    {
      "name": "myState",
      "type": "operation",
      "actions": [
        {
          "name": "printAction",
          "functionRef": {
            "refName": "printMessage",
            "arguments": {
              "message": "."
            }
          }
        }
      ]
    }
  ]
}
```

You should see the data output in your console.

#### Java Functions

Kogito also supports calling Java functions within the maven project which the workflow is defined. You can declare your
functions like this:

```json
{
  "functions": [
    {
      "name": "myFunction",
      "type": "custom",
      "operation": "service:java:com.acme.MyInterfaceOrClass::myMethod"
    }
  ]
}
```

Your method's interface **must** receive a Jackson's `JsonNode` object and return either `void` or another `JsonNode`.
For example:

```java
public class MyInterfaceOrClass {

    public void myMethod(JsonNode workflowData) {
        // do whatever I want with the JsonNode:
        // { "workflowdata": {} }
    }

    public JsonNode myMethod(JsonNode workflowData) {
        // do whatever I want with the JsonNode:
        // { "workflowdata": {} }
        // return the modified content:
        return workflowData;
    }
}
```

To call this function within your workflow you can extract the json value you need via a `jq` expression or pass it
without any arguments. In this case the whole payload is sent.

For example:

```json
{
  "states": [
    {
      "name": "myState",
      "type": "operation",
      "actions": [
        {
          "name": "callJavaFunctionAction",
          "functionRef": {
            "refName": "myFunction"
          }
        }
      ]
    }
  ]
}
```

Or, if you prefer you can pass only the necessary data:

```json
{
  "states": [
    {
      "name": "myState",
      "type": "operation",
      "actions": [
        {
          "name": "callJavaFunctionAction",
          "functionRef": {
            "refName": "myFunction",
            "arguments": {
              "data": ".my.path.to.data"
            }
          }
        }
      ]
    }
  ]
}
```

#### Examples

1. [Functions With Quarkus](https://github.com/kiegroup/kogito-examples/tree/main/serverless-workflow-examples/serverless-workflow-functions-quarkus)
2. [Funqy](https://github.com/kiegroup/kogito-examples/tree/main/serverless-workflow-examples/serverless-workflow-functions-quarkus)
3. [The GitHub Showcase](https://github.com/kiegroup/kogito-examples/tree/main/serverless-workflow-examples/serverless-workflow-github-showcase)
4. [Greetings Example With Quarkus](https://github.com/kiegroup/kogito-examples/tree/main/serverless-workflow-examples/serverless-workflow-greeting-quarkus)
5. [Temperature Conversion Example](https://github.com/kiegroup/kogito-examples/tree/main/serverless-workflow-examples/serverless-workflow-temperature-conversion)

### Workflow Model - Events

| Definition | Status             | Obs |
| ---------- | ------------------ | --- |
| Name       | :full_moon: |
| Source     | :full_moon: |
| Type       | :full_moon: |
| Kind       | :full_moon: |
| Correlation | :construction:  |
| Metadata    | :full_moon: |
| Data only  | :first_quarter_moon: | Default is `"dataOnly": true`. `"dataOnly": false` is ignored|

#### Examples

1. [Events With Quarkus](https://github.com/kiegroup/kogito-examples/tree/main/serverless-workflow-examples/serverless-workflow-events-quarkus)
2. [Functions With Events](https://github.com/kiegroup/kogito-examples/tree/main/serverless-workflow-examples/serverless-workflow-functions-events-quarkus)
3. [Order Processing](https://github.com/kiegroup/kogito-examples/tree/main/serverless-workflow-examples/serverless-workflow-order-processing)

### Workflow Model - Retries

[Retries](https://github.com/serverlessworkflow/specification/blob/0.8.x/specification.md#Retry-Definition) hasn't been
implemented yet, but it's in our roadmap for the future versions.

Alternatively to retries, you can use our [error handling](#Workflow-Error-Handling) feature.

### Workflow Data

Data manipulation (transformation) on Kogito is fully implemented and can be used either `jq`
or `jsonpath`. [State](https://github.com/serverlessworkflow/specification/blob/0.8.x/specification.md#state-data-filters)
and [Action](https://github.com/serverlessworkflow/specification/blob/0.8.x/specification.md#action-data-filters) data
filtering is also supported.

### Workflow Expressions

Kogito supports either `jq` or `jsonpath` to define workflow expressions. As defined in the specification, `jq` is the
default expression language. If you wish to use `jsonpath` instead, set the attribute `expressionLang` in the workflow
definition:

```json
{
  "id": "myworkflow",
  "version": "1.0",
  "expressionLang": "jsonpath",
  "name": "Workflow example",
  "description": "An example of how to use workflows"
}
```

#### Example

1. [Expressions With Quarkus](https://github.com/kiegroup/kogito-examples/tree/main/serverless-workflow-examples/serverless-workflow-expression-quarkus)

### Workflow Error Handling

Kogito supports error handling. Find more details about this implementation on
our [documentation](https://docs.jboss.org/kogito/release/latest/html_single/#con-serverless-workflow-error-handling_kogito-developing-decision-services).

1. [Error Handling With Quarkus](https://github.com/kiegroup/kogito-examples/tree/main/serverless-workflow-examples/serverless-workflow-error-quarkus)

### Workflow Compensation

Kogito supports workflow compensation as described in
the [specification](https://github.com/serverlessworkflow/specification/blob/0.8.x/specification.md#Workflow-Compensation).

#### Examples

1. [Workflow Compensation With Quarkus](https://github.com/kiegroup/kogito-examples/tree/main/serverless-workflow-examples/serverless-workflow-compensation-quarkus)
