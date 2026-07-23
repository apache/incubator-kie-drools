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

import java.util.Optional;

import io.quarkus.runtime.annotations.ConfigPhase;
import io.quarkus.runtime.annotations.ConfigRoot;
import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithDefault;
import io.smallrye.config.WithName;

@ConfigMapping(prefix = "kogito.data-index")
@ConfigRoot(phase = ConfigPhase.RUN_TIME)
public interface DataIndexRuntimeConfig {

    /**
     * Data Index URL.
     */
    Optional<String> url();

    /**
     * Path to the Data Index web application if any.
     */
    @WithName("ui.path")
    Optional<String> uiPath();

    /**
     * Path for publishing the Graphql UI.
     */
    @WithName("vertx-graphql.ui.path")
    @WithDefault("/graphiql")
    String graphqlUIPath();

    /**
     * Graphql UI tenant.
     */
    @WithName("vertx-graphql.ui.tenant")
    @WithDefault("web-app-tenant")
    String graphqlUITenant();

    /**
     * Domain Objects indexing.
     */
    @WithDefault("true")
    boolean domainIndexing();

    /**
     * Availability health check enabling.
     */
    Optional<Boolean> healthEnabled();

}
