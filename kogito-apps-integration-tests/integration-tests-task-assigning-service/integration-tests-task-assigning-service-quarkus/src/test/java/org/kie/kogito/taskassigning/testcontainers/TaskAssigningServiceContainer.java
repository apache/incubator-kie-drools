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

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

import org.kie.kogito.resources.TestResource;
import org.kie.kogito.testcontainers.KogitoGenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;

public class TaskAssigningServiceContainer extends KogitoGenericContainer<TaskAssigningServiceContainer> implements TestResource {

    public static final String NAME = "task-assigning-service";
    public static final int PORT = 8680;

    public TaskAssigningServiceContainer() {
        super(NAME);
        addExposedPort(PORT);
        waitingFor(Wait.forListeningPort());
        addEnv("QUARKUS_LOG_CATEGORY__ORG_KIE_KOGITO_TASKASSIGNING__LEVEL", "DEBUG");
        addEnv("CONFLUENT_SUPPORT_METRICS_ENABLE", "false");
        addEnv("QUARKUS_HTTP_PORT", Integer.toString(PORT));
    }

    public void setKafkaURL(String kafkaURL) {
        addEnv("KAFKA_BOOTSTRAP_SERVERS", kafkaURL);
    }

    public void setDataIndexUrl(String dataIndexUrl) {
        addEnv("KOGITO_TASK_ASSIGNING_DATA_INDEX_SERVER_URL", dataIndexUrl);
    }

    public void setUsersFromResource(String resource) {
        addUsersToEnv(resource);
    }

    @Override
    public String getResourceName() {
        return NAME;
    }

    @Override
    public int getMappedPort() {
        return getMappedPort(PORT);
    }

    private void addUsersToEnv(String usersResource) {
        try (InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(usersResource)) {
            Properties properties = new Properties();
            properties.load(inputStream);
            for (Map.Entry<Object, Object> userDefEntry : properties.entrySet()) {
                addEnv(userDefEntry.getKey().toString(), userDefEntry.getValue().toString());
            }
        } catch (IOException e) {
            throw new IllegalArgumentException("Users definition loading failed " + usersResource + ": " + e.getMessage(), e);
        }
    }
}