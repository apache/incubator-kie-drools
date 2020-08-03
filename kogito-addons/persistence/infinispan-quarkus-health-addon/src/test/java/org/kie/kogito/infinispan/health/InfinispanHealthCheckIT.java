/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.kogito.infinispan.health;

import javax.annotation.Resource;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.junit.jupiter.api.Test;
import org.kie.kogito.testcontainers.quarkus.InfinispanQuarkusTestResource;

import static org.assertj.core.api.Assertions.assertThat;

@QuarkusTest
@QuarkusTestResource(InfinispanQuarkusTestResource.class)
public class InfinispanHealthCheckIT {

    private InfinispanHealthCheck healthCheck;

    @Inject
    Instance<RemoteCacheManager> instance;

    @Resource
    InfinispanQuarkusTestResource resource;

    @Test
    void testCall() throws Exception {
        resource.start();

        this.healthCheck = new InfinispanHealthCheck(instance);

        //testing Up
        HealthCheckResponse response = healthCheck.call();
        assertThat(response.getState()).isEqualTo(HealthCheckResponse.State.UP);

        resource.stop();

        //testing Down
        HealthCheckResponse response2 = healthCheck.call();
        assertThat(response2.getState()).isEqualTo(HealthCheckResponse.State.DOWN);
    }
}