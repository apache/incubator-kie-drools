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

package org.kie.kogito.swf.tools.dataindex.config;

import io.quarkus.runtime.annotations.ConfigItem;
import io.quarkus.runtime.annotations.ConfigPhase;
import io.quarkus.runtime.annotations.ConfigRoot;

@ConfigRoot(name = "", prefix = "kogito", phase = ConfigPhase.BUILD_AND_RUN_TIME_FIXED)
public class DevConsoleRuntimeConfig {

    /**
     * Configuration for Serverless Workflow DevConsole services. It should keep data-index url to initialize DataIndexClient accordingly
     */
    @ConfigItem(name = "data-index.url", defaultValue = "http://localhost:8180")
    public String dataIndexUrl;
}
