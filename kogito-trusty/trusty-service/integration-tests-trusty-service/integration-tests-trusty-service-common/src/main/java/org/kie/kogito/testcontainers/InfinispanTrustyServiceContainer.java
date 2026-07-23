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

import org.testcontainers.containers.wait.strategy.Wait;

public class InfinispanTrustyServiceContainer extends KogitoGenericContainer<InfinispanTrustyServiceContainer> {

    public InfinispanTrustyServiceContainer(String infinispanServerList, String kafkaBootstrapServer,
            boolean explainabilityEnabled) {
        super("trusty-service-infinispan");
        addEnv("INFINISPAN_SERVER_LIST", infinispanServerList);
        addEnv("KAFKA_BOOTSTRAP_SERVERS", kafkaBootstrapServer);
        addEnv("TRUSTY_EXPLAINABILITY_ENABLED", String.valueOf(explainabilityEnabled));
        addExposedPort(8080);
        waitingFor(Wait.forListeningPort()).withStartupTimeout(Constants.CONTAINER_START_TIMEOUT);
    }
}
