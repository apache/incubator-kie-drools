To use MongoDB as storage for data index service:
1. Add MongoDB application properties in the `application.properties` of data index service, and use `mongodb` as the configuration profile:
    ```
        # MongoDB server address
        %mongodb.quarkus.mongodb.connection-string=mongodb://localhost:27017
        # MongoDB database name for data index 
        %mongodb.quarkus.mongodb.database=kogito
        # Set data index to use MongoDB for storage
        %mongodb.kogito.persistence.type=mongodb
        # Enable health check for MongoDB
        %mongodb.quarkus.mongodb.health.enabled=true
    ```
2. Run data index service with the `mongodb` configuration profile activated. For example, run data index service in dev mode:
    ```
        mvn clean compile quarkus:dev -Dquarkus.profile=mongodb
    ```