To use MongoDB as storage for data index service in Quarkus dev mode:
1. MongoDB application properties in the `application.properties` of data index service:
    ```
        # Set data index to use MongoDB for storage
        kogito.apps.persistence.type=mongodb
        # MongoDB server address
        quarkus.mongodb.connection-string=mongodb://localhost:27017
        # MongoDB database name for data index 
        quarkus.mongodb.database=kogito   
        # Enable health check for MongoDB
        quarkus.mongodb.health.enabled=true
        # Enable metrics for MongoDB
        quarkus.mongodb.metrics.enabled=true
    ```
2. Build this module and run in dev mode:
    ```
        mvn clean compile quarkus:dev
    ```