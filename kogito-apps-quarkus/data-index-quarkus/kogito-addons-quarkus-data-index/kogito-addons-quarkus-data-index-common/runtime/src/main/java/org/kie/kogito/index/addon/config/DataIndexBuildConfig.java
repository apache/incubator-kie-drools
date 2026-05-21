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

@ConfigMapping(prefix = "kogito.data-index")
@ConfigRoot(phase = ConfigPhase.BUILD_TIME)
public interface DataIndexBuildConfig {

    /**
     * Configures whether to use Reactive or Blocking behaviour for the RouterProducer and EventConsumer components.
     * If the property is set, and has the value true, blocking behaviour is configured, and the
     * BlockingGraphqlRouterProducer and BlockingMessagingEventConsumer are used.
     * In any other case, the ReactiveGraphqlRouterProducer and ReactiveMessagingEventConsumer are used.
     */
    Optional<Boolean> blocking();

}
