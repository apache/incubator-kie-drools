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
package org.kie.kogito.index.test.containers;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.kie.kogito.test.resources.TestResource;
import org.kie.kogito.testcontainers.KogitoGenericContainer;
import org.testcontainers.containers.BindMode;
import org.testcontainers.containers.wait.strategy.Wait;

/**
 * This container wraps Data Index Service container
 */
public abstract class AbstractDataIndexContainer extends KogitoGenericContainer<AbstractDataIndexContainer>
        implements TestResource {

    public static final int PORT = 8080;

    public AbstractDataIndexContainer(String containerName) {
        super(containerName);
        addExposedPort(PORT);
        waitingFor(Wait.forListeningPort());
        addEnv("KOGITO_PROTOBUF_FOLDER", "/home/kogito/data/protobufs/");
        withAccessToHost(true);
    }

    public void setKafkaURL(String kafkaURL) {
        addEnv("KAFKA_BOOTSTRAP_SERVERS", kafkaURL);
    }

    public void addProtoFileFolder() {
        String pathStr = "target/classes/META-INF/resources/persistence/protobuf/";
        String absolutePath = new File(pathStr).getAbsolutePath();
        createIfNotExists(absolutePath);
        withFileSystemBind(absolutePath, "/home/kogito/data/protobufs/", BindMode.READ_ONLY);
    }

    public Path createIfNotExists(String absolutePath) {
        Path path = Paths.get(absolutePath);
        try {
            Files.createDirectories(path);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        return path;
    }

    @Override
    public int getMappedPort() {
        return getMappedPort(PORT);
    }

}
