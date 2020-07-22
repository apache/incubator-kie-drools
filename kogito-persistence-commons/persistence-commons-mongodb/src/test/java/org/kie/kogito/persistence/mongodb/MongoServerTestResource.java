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

package org.kie.kogito.persistence.mongodb;

import java.util.Collections;
import java.util.Map;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;
import org.testcontainers.containers.MongoDBContainer;

public class MongoServerTestResource implements QuarkusTestResourceLifecycleManager {

    private static final String MONGODB_CONNECTION_PROPERTY = "quarkus.mongodb.connection-string";

    private MongoDBContainer mongoDBContainer;

    @Override
    public Map<String, String> start() {
        mongoDBContainer = new MongoDBContainer();
        mongoDBContainer.start();
        System.setProperty(MONGODB_CONNECTION_PROPERTY, mongoDBContainer.getReplicaSetUrl());
        return Collections.emptyMap();
    }

    @Override
    public void stop() {
        mongoDBContainer.stop();
    }
}
