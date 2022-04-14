# JSON Schema Support For Serverless Workflow

When specifying a `dataInputSchema` in the workflow definition, Kogito will try to load it using the URI. By default, if the scheme is not defined, the file is expected to be in the project's
classpath.

In build time, Kogito will generate a partial `openapi.json` file with the contents of the given JSON Schema. In runtime, Quarkus will use it to generate the final OpenAPI spec for the service.
The `workflowdata` input model then will reflect the JSON Schema in the workflow definition.

For this to work, you need to have the SmallRye OpenAPI extension in your classpath:

````xml

<dependency>
  <groupId>io.quarkus</groupId>
  <artifactId>quarkus-smallrye-openapi</artifactId>
</dependency>
````
