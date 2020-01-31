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

import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import io.quarkus.test.junit.QuarkusTest;
import org.assertj.core.api.Assertions;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.kogito.infinispan.InfinispanServerTestResource;

@QuarkusTest
public class InfinispanHealthCheckTest {

    private InfinispanHealthCheck healthCheck;

    @Inject
    Instance<RemoteCacheManager> instance;

    @Test
    void testCall() throws Exception{
        InfinispanServerTestResource resource = new InfinispanServerTestResource();
        resource.start();

        this.healthCheck = new InfinispanHealthCheck(instance);

        //testing Up
        HealthCheckResponse response = healthCheck.call();
        Assertions.assertThat(response.getState()).isEqualTo(HealthCheckResponse.State.UP);

        resource.stop();

        //testing Down
        HealthCheckResponse response2 = healthCheck.call();
        Assertions.assertThat(response2.getState()).isEqualTo(HealthCheckResponse.State.DOWN);
    }
}