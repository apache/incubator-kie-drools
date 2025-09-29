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

package org.kie.kogito.addon.quarkus.jobs.health;

import java.util.Set;

import org.kie.kogito.addon.quarkus.common.config.AbstractAliasConfigSource;

public class JobsServiceAvailabilityHealthCheckConfigSource extends AbstractAliasConfigSource {

    public JobsServiceAvailabilityHealthCheckConfigSource() {
        super(Set.of(
                "io.smallrye.health.check." + JobsServiceAvailabilityHealthCheck.class.getName() + ".enabled",
                JobsServiceAvailabilityHealthCheck.CONFIG_ALIAS));
    }

    @Override
    public String getConfigAlias() {
        return JobsServiceAvailabilityHealthCheck.CONFIG_ALIAS;
    }

    @Override
    public String getName() {
        return JobsServiceAvailabilityHealthCheckConfigSource.class.getSimpleName();
    }
}
