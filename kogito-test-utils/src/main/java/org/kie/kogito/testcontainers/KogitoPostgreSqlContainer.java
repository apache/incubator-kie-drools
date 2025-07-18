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
package org.kie.kogito.testcontainers;

import java.text.MessageFormat;
import java.util.function.Consumer;

import org.kie.kogito.test.resources.TestResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.output.OutputFrame;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.containers.wait.strategy.HostPortWaitStrategy;
import org.testcontainers.containers.wait.strategy.WaitAllStrategy;
import org.testcontainers.utility.DockerImageName;

import static org.kie.kogito.testcontainers.KogitoGenericContainer.getImageName;

/**
 * PostgreSQL Container for Kogito examples.
 */
public class KogitoPostgreSqlContainer extends PostgreSQLContainer<KogitoPostgreSqlContainer> implements TestResource {

    public static final String NAME = "postgres";
    public static final String POSTGRESQL_CONNECTION_URI = "kogito.persistence.postgresql.connection.uri";
    private static final Logger LOGGER = LoggerFactory.getLogger(KogitoPostgreSqlContainer.class);

    public KogitoPostgreSqlContainer() {
        super(DockerImageName.parse(getImageName(NAME)).asCompatibleSubstituteFor("postgres"));
        withLogConsumer(getLogger());
        withLogConsumer(new Slf4jLogConsumer(LOGGER));
        withStartupTimeout(Constants.CONTAINER_START_TIMEOUT);
        withUrlParam("stringtype", "unspecified"); // temporary workaround to overcome problems with JPA and JsonB columns

        /*
         * Overriding default waitStrategy (LogMessageWaitStrategy) added by the parent to also wait for the mapped port to be available. This ensures that the PostgreSQLContainer is running
         * and the mapped ports are ready before running any test.
         * This avoids connection issues with the container when using container managers that do the port mapping after the container has started.
         */
        this.waitStrategy = new WaitAllStrategy()
                .withStrategy(this.waitStrategy)
                .withStrategy(new HostPortWaitStrategy());
    }

    private Consumer<OutputFrame> getLogger() {
        return f -> System.out.print(f.getUtf8String());
    }

    @Override
    public void start() {
        super.start();
        LOGGER.info("PostgreSql server: {}", this.getContainerIpAddress() + ":" + this.getMappedPort(POSTGRESQL_PORT));
    }

    @Override
    public int getMappedPort() {
        return getMappedPort(POSTGRESQL_PORT);
    }

    @Override
    public String getResourceName() {
        return "postgresql";
    }

    public String getReactiveUrl() {
        final String connectionTemplate = "postgresql://{0}:{1}@{2}:{3}/{4}?search_path={5}";
        final String user = getUsername();
        final String server = getHost();
        final String secret = getPassword();
        final String port = String.valueOf(getMappedPort());
        final String database = getDatabaseName();
        final String schema = "public";
        return MessageFormat.format(connectionTemplate, user, secret, server, port, database, schema);
    }
}
