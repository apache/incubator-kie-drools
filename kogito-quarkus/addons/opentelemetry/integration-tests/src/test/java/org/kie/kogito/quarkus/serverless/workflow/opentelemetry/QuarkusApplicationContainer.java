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
package org.kie.kogito.quarkus.serverless.workflow.opentelemetry;

import java.nio.file.Path;
import java.time.Duration;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.HttpWaitStrategy;
import org.testcontainers.utility.MountableFile;

public class QuarkusApplicationContainer extends GenericContainer<QuarkusApplicationContainer> {

    private static final String BASE_IMAGE = "eclipse-temurin:17-jre";
    private static final int QUARKUS_HTTP_PORT = 8080;
    private static final Duration STARTUP_TIMEOUT = Duration.ofSeconds(120);

    private final String otlpEndpoint;
    private final String postgresJdbcUrl;
    private final String postgresUser;
    private final String postgresPassword;

    public QuarkusApplicationContainer(
            String otlpEndpoint,
            String postgresJdbcUrl,
            String postgresUser,
            String postgresPassword) {
        super(BASE_IMAGE);
        this.otlpEndpoint = otlpEndpoint;
        this.postgresJdbcUrl = postgresJdbcUrl;
        this.postgresUser = postgresUser;
        this.postgresPassword = postgresPassword;

        configureContainer();
    }

    private void configureContainer() {
        Path quarkusAppPath = Path.of("target", "quarkus-app");

        this.withExposedPorts(QUARKUS_HTTP_PORT)
                .withCopyFileToContainer(
                        MountableFile.forHostPath(quarkusAppPath),
                        "/app")
                .withCommand("java", "-jar", "/app/quarkus-run.jar")
                .withExtraHost("host.docker.internal", "host-gateway")
                .withEnv("QUARKUS_HTTP_PORT", String.valueOf(QUARKUS_HTTP_PORT))
                .withEnv("QUARKUS_OTEL_EXPORTER_OTLP_ENDPOINT", otlpEndpoint)
                .withEnv("QUARKUS_DATASOURCE_JDBC_URL", postgresJdbcUrl)
                .withEnv("QUARKUS_DATASOURCE_USERNAME", postgresUser)
                .withEnv("QUARKUS_DATASOURCE_PASSWORD", postgresPassword)
                .withEnv("KOGITO_PERSISTENCE_TYPE", "jdbc")
                .waitingFor(new HttpWaitStrategy()
                        .forPort(QUARKUS_HTTP_PORT)
                        .forPath("/q/health")
                        .withStartupTimeout(STARTUP_TIMEOUT));
    }

    public String getApplicationUrl() {
        return String.format("http://%s:%d", getHost(), getApplicationPort());
    }

    public Integer getApplicationPort() {
        return getMappedPort(QUARKUS_HTTP_PORT);
    }
}
