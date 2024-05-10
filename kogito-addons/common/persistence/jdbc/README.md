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

# Kogito JDBC Persistence Add-on

The Kogito JDBC Persistence Add-on adds persistence capability to Kogito projects. See the [official documentation](https://docs.jboss.org/kogito/release/latest/html_single/#con-persistence_kogito-developing-process-services) to find out more.

Currently tested for Postgres and Oracle. Other database will automatically use ANSI standard SQL.

To enable JDBC persistence set the following value:
```
kogito.persistence.type=jdbc
```

Example configuration for postgres
```
# On Quarkus
quarkus.datasource.db-kind=postgresql
quarkus.datasource.username=postgres
quarkus.datasource.password=changeme
quarkus.datasource.jdbc.url=jdbc:postgresql://localhost:5432/kogito

# On Spring Boot
spring.datasource.username=kogito-user
spring.datasource.password=kogito-pass
spring.datasource.url=jdbc:postgresql://localhost:5432/kogito
```

Example configuration for oracle
```
# On Quarkus
quarkus.datasource.db-kind=oracle
quarkus.datasource.username=kogito-user
quarkus.datasource.password=kogito-user
quarkus.datasource.jdbc.url=jdbc:oracle:thin:@localhost:1521:kogito

# On Spring Boot
spring.datasource.username=workflow
spring.datasource.password=workflow
spring.datasource.url=jdbc:oracle:thin:@localhost:1521:kogito
```

## Auto DLL creation
JDBC Persistence Add-on will attempt to automatically generate the necessary database objects if you enable the `autoDLL` property.
```
kogito.persistence.auto.ddl=true
```
This settings is defaulted to true.
