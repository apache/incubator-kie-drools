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

package org.kie.flyway.quarkus;

import java.util.Map;

import org.kie.flyway.integration.KieFlywayConfiguration;
import org.kie.flyway.integration.KieFlywayNamedModule;

import io.quarkus.runtime.annotations.*;

/**
 * Configuration for the Kie Flyway initializer
 */
@ConfigRoot(prefix = "kie", name = "flyway", phase = ConfigPhase.RUN_TIME)
public class KieFlywayQuarkusRuntimeConfig implements KieFlywayConfiguration<KieFlywayQuarkusRuntimeConfig.KieQuarkusFlywayNamedModule> {

    /**
     * Enables the execution of the Flyway initializer during the application startup
     */
    @ConfigItem(name = "enabled", defaultValue = "true")
    boolean enabled;

    /**
     * List of {@link KieQuarkusFlywayNamedModule} that allow to enable or disable a given modul
     */
    @ConfigItem(name = "modules")
    Map<String, KieQuarkusFlywayNamedModule> modules;

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public Map<String, KieQuarkusFlywayNamedModule> getModules() {
        return modules;
    }

    @ConfigGroup
    public static class KieQuarkusFlywayNamedModule implements KieFlywayNamedModule {

        /**
         * Enables the execution of the Flyway initializer for a specific Kie module
         */
        @ConfigItem(name = "enabled", defaultValue = "true")
        boolean enabled;

        @Override
        public boolean isEnabled() {
            return enabled;
        }
    }
}
