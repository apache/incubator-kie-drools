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

package org.kie.kogito.addon.quarkus.index.health;

import java.time.Duration;
import java.util.Optional;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.health.Startup;
import org.kie.kogito.addon.quarkus.common.health.AbstractAvailabilityHealthCheck;

import io.vertx.mutiny.core.Vertx;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@Startup
@ApplicationScoped
public class DataIndexAvailabilityHealthCheck extends AbstractAvailabilityHealthCheck {

    private static final String KOGITO_DATA_INDEX_URL = "kogito.data-index.url";

    public static final String CONFIG_ALIAS = "kogito.data-index.health-enabled";

    @Inject
    public DataIndexAvailabilityHealthCheck(@ConfigProperty(name = KOGITO_DATA_INDEX_URL) Optional<String> dataIndexUrl,
            Vertx vertx) {
        super("Data Index", dataIndexUrl.orElse(null), "/q/health/ready", "startup check", vertx, Duration.ofMillis(500));
    }
}
