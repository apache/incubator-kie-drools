/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.kie.kogito.test.resources;

import java.util.HashMap;
import java.util.Map;

import org.kie.kogito.index.test.containers.DataIndexPostgreSqlContainer;
import org.kie.kogito.testcontainers.JobServiceContainer;
import org.kie.kogito.testcontainers.KogitoKafkaContainer;
import org.kie.kogito.testcontainers.KogitoPostgreSqlContainer;

import io.quarkus.test.common.QuarkusTestResourceConfigurableLifecycleManager;

public class JobServiceCompositeQuarkusTestResource implements QuarkusTestResourceConfigurableLifecycleManager<JobServiceTestResource> {

    public static final String JOBS_SERVICE_URL = "kogito.jobs-service.url";
    public static final String DATA_INDEX_SERVICE_URL = "kogito.data-index.url";

    private JobServiceTestResource annotation;

    private CompositeTestResource resource;

    public JobServiceCompositeQuarkusTestResource() {
        resource = new CompositeTestResource(new JobServiceContainer());
    }

    @Override
    public void init(JobServiceTestResource annotation) {
        this.annotation = annotation;
        switch (annotation.persistence()) {
            case POSTGRESQL:
                resource.withDependencyToService(CompositeTestResource.MAIN_SERVICE_ID, new KogitoPostgreSqlContainer());
                //resource.withSharedDependencyContainer("postgres", new KogitoPostgreSqlContainer());
                break;
            case IN_MEMORY:
            default:
                //no persistence
        }
        if (annotation.kafkaEnabled()) {
            resource.withSharedDependencyContainer("kafka", new KogitoKafkaContainer());
            resource.getServiceContainers(JobServiceContainer.class).forEach(c -> c.addEnv("QUARKUS_PROFILE", "kafka-events-support"));
        }
        if (annotation.knativeEventingEnabled()) {
            resource.getServiceContainers(JobServiceContainer.class).forEach(c -> {
                c.addEnv("QUARKUS_PROFILE", "http-events-support");
                c.addEnv("KOGITO_JOBS_SERVICE_HTTP_JOB_STATUS_CHANGE_EVENTS", "false");
            });
        }
        if (annotation.dataIndexEnabled()) {
            DataIndexPostgreSqlContainer container = new DataIndexPostgreSqlContainer();
            container.addProtoFileFolder();
            container.addEnv("QUARKUS_PROFILE", "kafka-events-support");
            KogitoPostgreSqlContainer postgresql = new KogitoPostgreSqlContainer();
            resource.withServiceContainer("data-index", container, postgresql);
        }
    }

    @Override
    public void stop() {
        resource.stop();
    }

    @Override
    public Map<String, String> start() {
        resource.start();
        Map<String, String> properties = new HashMap<>(resource.getProperties());
        if (annotation.dataIndexEnabled()) {
            DataIndexPostgreSqlContainer dataIndexContainer = resource.getServiceContainer("data-index");
            System.setProperty(DATA_INDEX_SERVICE_URL, "http://" + dataIndexContainer.getHost() + ":" + dataIndexContainer.getMappedPort());
            properties.put(DATA_INDEX_SERVICE_URL, "http://" + dataIndexContainer.getHost() + ":" + dataIndexContainer.getMappedPort());
        }
        System.setProperty(JOBS_SERVICE_URL, "http://localhost:" + resource.getMappedPort());
        properties.put(JOBS_SERVICE_URL, "http://localhost:" + resource.getMappedPort());

        return properties;
    }
}
