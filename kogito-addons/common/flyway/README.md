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

# KIE Flyway Add-On

## Goal

This add-on is a utility intended to help bootstrapping the application Data Base when running in a `compact` setup,
when more than one`extension` or `add-on` that require persistence co-exist in the same application.

This add-on will be using a managed Flyway to initialize and upgrade each `extension`/`add-on` DB 
instead of using the Platform (Quarkus/Springboot) specific Flyway integration, by this we achieve:
* Simple setups in the `application.properties`. No need to configure Flyway unless the customer requires for its 
    own needs.
* Component-Based DB Management: the DB management (via Flyway `migrations`) will be executed independently by component 
   (not globally), keeping a separate index for each component to avoid collisions and conflict between different component versions.
* Multi DB Support: a single module can provide SQL scripts for different DB vendors (and default as a fallback) that
    will be loaded depending on the application configuration

> *IMPORTANT*: The usage of this add-on should be reserved only for development/test/examples purposes, and it is not recommended
> using it in productive environments.



## KIE Flyway Module Configuration

In order to allow the *KIE Flyway Initializer* identify the DB needs of a specific component (`extensions` or `add-on`)
the component has to meet the following requirements:

* Provide a `kie-flyway.properties` descriptor file in `/src/main/resources/META-INF/kie-flyway.properties`. The file 
  should provide the following information:
  * `module.name`: logic name that identifies the module. This identifier will be used during the Data Base initialization process
    to generate the index table (`kie_flyway_history_<module_name>`) that will keep track of the modules table changes.
    If there are multiple implementations of the same module (ej: `kie-addons-persistence-jdbc` / `kie-addons-persistence-postgresql`) 
    they should use the same name.
  * `module.locations.<db>`: map containing the module `.sql` scripts location paths organized by database type (`postgresql`, `h2`...) to initialize the DB 
    (ej: `module.locations.postgresql=classpath:kie-flyway/db/test-module/postgresql`), the locations can be a comma-separated list to use multiple `.sql` locations in a single migration.
    It's also possible using a `default` locations (`module.locations.default=...`) as a fallback to provide a default initialization
    if no vendor-specific isn't available. It is important to avoid using the default flyway location (`src/main/resourcs/db/migrations`) to avoid
    collisions with the Platform Flyway integration.
    
Example of `kie-flyway.properties` file:
```properties
# Name that identifies the module
module.name=runtime-persistence

# Script locations for the current module
module.locations.h2=classpath:kie-flyway/db/persistence-jdbc/h2
module.locations.postgresql=classpath:kie-flyway/db/persistence-jdbc/postgresql
# Default sql locations if the application db type isn't none of the above (ej: oracle)
module.locations.default=classpath:kie-flyway/db/persistence-jdbc/ansi
```

* SQL Migration files: the needed SQL files to initialize the module DB. They should be stored in a unique path for the 
  component inside `src/main/resources` (ej: `src/main/resources/kie-flyway/db/<module_name>`), and grouped by database type.

Example of folder structure:

```
kie-persistence-jdbc
└─── src/main/resources
              └─── META-INF
              │    └─── kie-flyway.properties
              └─── kie-flyway/db/persistence-jdbc
                                  └─── ansi
                                  │      V1.35.0__create_runtime_ansi.sql
                                  │      V10.0.0__add_business_key_ansi.sql
                                  │      V10.0.1__create_correlation_ansi.sql
                                  └─── postgresql
                                         V1.35.0__create_runtime_PostgreSQL.sql
                                         V10.0.0__add_business_key_PostgreSQL.sql
                                         V10.0.1__create_correlation_PostgreSQL.sql  
```

## Application configurations

### Enabling Kie Flyway Migration
KIE Flyway is disabled by default (in Quarkus is enabled in `test`/`dev` profiles) and can be enabled / disabled with 
`kie.flyway.enabled` property in the `application.properties` (by default is false)

### Excluding specific KIE Modules via configuration.
In some cases you may want to exclude the KIE Modules present in your application from the KIE Flyway initialization, to do so you can use 
`kie.flyway.modules.<module-name>.enabled` property in the `application.properties`

Example of `application.properties`
```properties
...

# KIE Flyway setup
kie.flyway.enabled=true
kie.flyway.modules."data-index".enabled=false
kie.flyway.modules."jobs-service".enabled=false
```

## Usage
KIE Flyway exposes the `KieFlywayInitializer` as entry point of the add-on and exposes a Fluent Api to configure it 
and run it. This component will be in charge of loading all the `kie-flyway.properties` available in the application and run
migrations for each of them.

The required information that must be provided to the initializer is:
* DataSource (`java.sql.DataSource`) where the initialization should be executed. It should be the default application Data Source

```java
import org.kie.flyway.initializer.KieFlywayInitializer;

...
        KieFlywayInitializer.builder()
                .withDatasource(dataSource)
                .build()
                .migrate();
```

Additional Parameters that can be used:
* Custom ClassLoader to load the `kie-flyway.properties` from.
* Module Exclusions (`Collection<String>`) t

```java
import org.kie.flyway.initializer.KieFlywayInitializer;

...
        KieFlywayInitializer.builder()
                .withDatasource(dataSource)
                .withClassLoader(this.getClass().getClassLoader())
                .withModuleExclusions(List.of("data-index", "jobs-service"))
                .build()
                .migrate();
```

> NOTE: The platform-specific add-ons (Quarkus/Spring-Boot) will be in charge to obtain the DataSource and DataBase type 
> and correctly configure the `KieFlywayInitializer` according to the `application.properties` and use it on during the application startup.





