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

# Jobs Addon

job service represents a subsystem within kogito workflow domain. This component is responsible for scheduling jobs. In the case of workflow this module takes care of timers such from boundary events, SLA, throw events relevant to timers. Also is used for things like human tasks notifications.

The system supports only different types of deployment:

*   Compact Architecture: as a component deployed within the application.

The current support storage is only:

*   jpa

At present Addons jobs supports quarkus and spring boot

The properties supported are:
* kogito.jobs-service.numberOfWorkerThreads: maximum of number of worker thread to execute timeouts (default is 10)
* kogito.jobs-service.maxNumberOfRetries: numbers of retry of a failred job. After this number is reached the job will be set to failure. (default is 3 times)
* kogito.jobs-service.retryMillis: interval used to retry the new job (default 60 seconds)
* kogito.jobs-service.schedulerChunkInMinutes: max window minutes from actual date to the future to load timers in memory (default is 10 minutes)
* kogito.service.url: url service is this collocated service. (default is localhost:8080)


## Using job service as Compact architecture

For using in your project this you need first to include the dependency related to the transport tier. in our case for in-vm we use 

    <dependency>
      <groupId>org.kie</groupId>
      <artifactId>kogito-addons-quarkus-embedded-jobs</artifactId>
    </dependency>


after that we need to include the storage we want to use. For instance we are using postgresql

    <dependency>
      <groupId>org.kie</groupId>
      <artifactId>kogito-addons-quarkus-embedded-jobs-jpa</artifactId>
    </dependency>

In this case for ansi in-vm it will use automatically your main data source available.
Here you have an example of this configuration:


	kogito.persistence.type=jdbc
	quarkus.datasource.db-kind=postgresql
	quarkus.datasource.username=kogito-user
	quarkus.datasource.password=kogito-pass
	quarkus.datasource.jdbc.url=${QUARKUS_DATASOURCE_JDBC_URL:jdbc:postgresql://localhost:5432/kogito}


## Distributed deployment

There is no distributed deployment


