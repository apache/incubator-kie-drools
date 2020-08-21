/**
 *  Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.kie.kogito.testcontainers;

import org.kie.kogito.resources.TestResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;

/**
 * MongoDB Container for Kogito examples.
 */
public class KogitoMongoDBContainer extends MongoDBContainer implements TestResource {

    public static final String NAME = "mongodb";
    public static final int MONGODB_INTERNAL_PORT = 27018;

    private static final Logger LOGGER = LoggerFactory.getLogger(KogitoMongoDBContainer.class);

    public KogitoMongoDBContainer() {
        withLogConsumer(new Slf4jLogConsumer(LOGGER));
        addFixedExposedPort(MONGODB_INTERNAL_PORT, MONGODB_INTERNAL_PORT);
    }

    @Override
    public int getMappedPort() {
        return getMappedPort(MONGODB_INTERNAL_PORT);
    }

    @Override
    public String getResourceName() {
        return NAME;
    }
}
