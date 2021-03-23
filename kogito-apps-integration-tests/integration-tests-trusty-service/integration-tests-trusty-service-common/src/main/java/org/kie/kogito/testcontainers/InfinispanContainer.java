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

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;

import static org.kie.kogito.testcontainers.TestContainersUtils.getImageName;

public class InfinispanContainer extends GenericContainer<InfinispanContainer> {

    public InfinispanContainer() {
        super(getImageName("infinispan"));
        addEnv("USER", "admin");
        addEnv("PASS", "admin");
        addExposedPort(11222);
        waitingFor(Wait.forLogMessage(".*ISPN080001.*", 1));
    }
}
