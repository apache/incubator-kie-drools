# Kogito Serverless Workflow

Kogito is an implementation of the [CNCF Serverless Workflow Specification](https://serverlessworkflow.io/).

## Current Status

Currently, Kogito implements the [**version
0.6**](https://github.com/serverlessworkflow/specification/blob/0.6.x/specification.md) of the specification. 

The following table lists the current status of the features as defined by the specification:

| Feature       | Status               |
| ------------- | -------------------- |
| States        | :first_quarter_moon: |
| Functions     | :first_quarter_moon: |
| Events        | :first_quarter_moon: |
| Retries       | :new_moon:           |
| Workflow Data | :full_moon:          |
| Expressions   | :full_moon:          |
| Error Handling | :full_moon:         |
| Compensation   | :full_moon:         |

Legend:

| Symbol        | Meaning              |
| ------------- | -------------------- |
| :full_moon: | Fully implemented |
| :first_quarter_moon: | Partially implemented |
| :new_moon: | Not Implemented |

The sections below describe in detail the current status of the supported features.

### Workflow Model - States

| State         | Status      |
| ------------- | ----------- |
| Event         | :full_moon: |
| Operation     | :full_moon: |
| Switch        | :full_moon: |
| Delay         | :full_moon: |
| Parallel      | :full_moon: |
| SubFlow       | :full_moon: |
| Inject        | :full_moon: |
| ForEach       | :new_moon:  |
| Callback      | :full_moon: |

### Workflow Model - Functions

| Function Type | Status             | Obs |
| ------------- | ------------------ | --- |
| rest          | :full_moon: | You can find more details about the Kogito OpenAPI implementation [here](../kogito-codegen-modules/kogito-codegen-openapi) |
| rpc           | :new_moon:                | |
| expression    | :full_moon: | Either `jq` or `jsonpath` |

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
      "metadata": {
        "interface": "com.acme.MyInterfaceOrClass",
        "operation": "myMethod",
        "type": "service"
      }
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

### Workflow Model - Events

| Definition | Status             |
| ---------- | ------------------ |
| Name       | :full_moon: |
| Source     | :full_moon: |
| Type       | :full_moon: |
| Kind       | :full_moon: |
| Correlation | :new_moon:  |
| Metadata    | :full_moon: |

### Workflow Model - Retries

Kogito **does not**
support [retries](https://github.com/serverlessworkflow/specification/blob/0.6.x/specification.md#Retry-Definition) just
yet.

### Workflow Data

Data manipulation (filtering and transformation) on Kogito is fully implemented and can be used either `jq`
or `jsonpath`.

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

### Workflow Error Handling

Kogito supports error handling. Find more details about this implementation on
our [documentation](https://docs.jboss.org/kogito/release/latest/html_single/#con-serverless-workflow-error-handling_kogito-developing-decision-services).

### Workflow Compensation

Kogito supports workflow compensation as described in
the [specification](https://github.com/serverlessworkflow/specification/blob/0.6.x/specification.md#Workflow-Compensation).
