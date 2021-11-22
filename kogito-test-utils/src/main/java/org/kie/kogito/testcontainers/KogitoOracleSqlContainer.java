/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.testcontainers;

import java.text.MessageFormat;
import java.util.function.Consumer;

import org.kie.kogito.test.resources.TestResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.OracleContainer;
import org.testcontainers.containers.output.OutputFrame;
import org.testcontainers.containers.output.Slf4jLogConsumer;

/**
 * OracleXE Container for Kogito examples.
 */
public class KogitoOracleSqlContainer extends OracleContainer implements TestResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(KogitoOracleSqlContainer.class);

    public static final String ORACLE_CONNECTION_URI = "kogito.persistence.oracle.connection.uri";

    public KogitoOracleSqlContainer() {
        withLogConsumer(getLogger());
        withLogConsumer(new Slf4jLogConsumer(LOGGER));
        withStartupTimeout(Constants.CONTAINER_START_TIMEOUT);
    }

    private Consumer<OutputFrame> getLogger() {
        return f -> System.out.print(f.getUtf8String());
    }

    @Override
    public void start() {
        super.start();
        LOGGER.info("Oracle server: {}", this.getContainerIpAddress() + ":" + this.getOraclePort());
    }

    @Override
    public int getMappedPort() {
        return getOraclePort();
    }

    @Override
    public String getResourceName() {
        return "oracle";
    }

    public String getReactiveUrl() {
        final String connectionTemplate = "oracle://{0}:{1}@{2}:{3}/{4}?search_path={5}";
        final String user = getUsername();
        final String server = getHost();
        final String secret = getPassword();
        final String port = String.valueOf(getMappedPort());
        final String database = getDatabaseName();
        final String schema = "public";
        return MessageFormat.format(connectionTemplate, user, secret, server, port, database, schema);
    }

    @Override
    public void stop() {
        super.stop();
    }
}
