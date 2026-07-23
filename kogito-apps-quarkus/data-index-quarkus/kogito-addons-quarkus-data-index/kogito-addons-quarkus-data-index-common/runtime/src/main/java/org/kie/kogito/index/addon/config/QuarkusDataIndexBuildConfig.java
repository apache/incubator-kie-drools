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
package org.kie.kogito.index.addon.config;

import io.quarkus.runtime.annotations.ConfigPhase;
import io.quarkus.runtime.annotations.ConfigRoot;
import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithDefault;
import io.smallrye.config.WithName;

/**
 * We keep this class for backward compatibility purposes.
 * Avoid using quarkus.kogito.data-index prefixed properties unless there's a strong reason.
 * see: DataIndexBuildConfig.
 */
@ConfigMapping(prefix = "quarkus.kogito.data-index")
@ConfigRoot(phase = ConfigPhase.BUILD_AND_RUN_TIME_FIXED)
public interface QuarkusDataIndexBuildConfig {

    /**
     * If GraphQL UI should be enabled. By default, this is only included when the application is running in dev mode.
     */
    @WithName("graphql.ui.always-include")
    @WithDefault("false")
    boolean graphqlUiAlwaysInclude();

}
