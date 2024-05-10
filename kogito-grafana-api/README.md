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

Grafana API
==============

This repository contains the library to create and customize grafana dashboards.

- `GrafanaConfigurationWriter` is the class that implements the logic to build dashboards for a specific DRL or DMN endpoint.
    
    The following method will simply customize the template for the endpoint
    ```java
    public static String generateDashboardForEndpoint(String templatePath, String handlerName);
    ```
    
    The following method instead will also add some panels depending on the decisions included in the DMN model.
    ```java
    public static String generateDashboardForDMNEndpoint(String templatePath, String endpoint, List<Decision> decisions);
    ```

- For full customization it is possible to use the class `JGrafana`, that contains all the properties to fully customize a dashboard.

- Kogito might generate json files to populate grafana. If you need these files on production enviromment, please remember to include ```kogito.quarkus.codegen.dumpFiles=true``` either on your application.properties or maven pom file