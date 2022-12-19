/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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

package org.kie.kogito.quarkus.workflow.config;

import java.util.Optional;

import io.quarkus.runtime.annotations.ConfigGroup;
import io.quarkus.runtime.annotations.ConfigItem;

@ConfigGroup
public class KogitoPersistenceRuntimeConfig {

    /**
     * Persistence DB type
     */
    @ConfigItem
    public Optional<PersistenceType> type;

    /**
     * Automatically apply database schema changes
     */
    @ConfigItem(name = "auto.ddl", defaultValue = "true")
    public boolean autoDDL;

    /**
     * Use optimistic locking
     */
    @ConfigItem(name = "optimistic.lock", defaultValue = "false")
    public boolean optimisticLock;

    /**
     * Query execution timeout
     */
    @ConfigItem(name = "query.timeout.millis", defaultValue = "10000")
    public long queryTimeout;

}
