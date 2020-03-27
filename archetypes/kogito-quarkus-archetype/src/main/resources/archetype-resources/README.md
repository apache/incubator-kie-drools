# ${groupId}.${artifactId} - ${version} #

# Running

- Compile and Run

    ```
     mvn clean package quarkus:dev
    ```

- Native Image (requires JAVA_HOME to point to a valid GraalVM)

    ```
    mvn clean package -Pnative
    ```
  
  native executable (and runnable jar) generated in `target/`

# Test your application

Generated application comes with sample test process that allows you to verify if the application is working as expected. Simply execute following command to try it out

```sh
curl -d '{}' -H "Content-Type: application/json" -X POST http://localhost:8080/greetings
```

Once successfully invoked you should see "Hello World" in the console of the running application.

# Developing

Add your business assets resources (process definition, rules, decisions) into src/main/resources.

Add your java classes (data model, utilities, services) into src/main/java.

Then just build the project and run.


# Swagger documentation

The exposed service [OpenAPI specification](https://swagger.io/docs/specification) is generated at 
[/docs/openapi.json](http://localhost:8080/docs/openapi.json).

You can visualize and interact with the generated specification using the embbeded [Swagger UI](http://localhost:8080/swagger-ui) or importing the generated specification file on [Swagger Editor](https://editor.swagger.io).

In addition client application can be easily generated from the swagger definition to interact with this service.