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

import org.kie.kogito.test.resources.TestResource;
import org.testcontainers.containers.wait.strategy.Wait;

public class KogitoRedisSearchContainer extends KogitoGenericContainer<KogitoRedisSearchContainer> implements TestResource {

    public static final String NAME = "redis";
    public static final int PORT = 6379;

    public KogitoRedisSearchContainer() {
        super(NAME);
        addExposedPort(PORT);
        waitingFor(Wait.forLogMessage(".*Ready to accept connections.*\\s", 1).withStartupTimeout(Constants.CONTAINER_START_TIMEOUT));
    }

    @Override
    public int getMappedPort() {
        return getMappedPort(PORT);
    }

    @Override
    public String getResourceName() {
        return NAME;
    }
}