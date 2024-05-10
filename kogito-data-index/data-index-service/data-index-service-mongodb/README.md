<!---
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