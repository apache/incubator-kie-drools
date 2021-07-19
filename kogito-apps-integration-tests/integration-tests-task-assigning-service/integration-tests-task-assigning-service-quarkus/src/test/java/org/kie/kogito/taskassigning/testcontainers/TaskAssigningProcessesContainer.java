/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.kogito.taskassigning.testcontainers;

import org.kie.kogito.resources.TestResource;
import org.kie.kogito.testcontainers.KogitoGenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;

public class TaskAssigningProcessesContainer extends KogitoGenericContainer<TaskAssigningProcessesContainer> implements TestResource {

    public static final String NAME = "integration-tests-task-assigning-service-processes";

    public static final int PORT = 8580;

    public TaskAssigningProcessesContainer() {
        super(NAME);
        addExposedPort(PORT);
        waitingFor(Wait.forListeningPort());
        addEnv("QUARKUS_HTTP_PORT", Integer.toString(PORT));
    }

    public void setKogitoServiceURL(String kogitoServiceURL) {
        addEnv("KOGITO_SERVICE_URL", kogitoServiceURL);
    }

    public void setKafkaURL(String kafkaURL) {
        addEnv("KAFKA_BOOTSTRAP_SERVERS", kafkaURL);
    }

    @Override
    public String getResourceName() {
        return NAME;
    }

    @Override
    public int getMappedPort() {
        return getMappedPort(PORT);
    }
}
