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

package org.kie.kogito.jobs.service.repository.infinispan;

import javax.inject.Inject;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInstance;
import org.kie.kogito.jobs.service.repository.ReactiveJobRepository;
import org.kie.kogito.jobs.service.repository.impl.BaseJobRepositoryTest;
import org.kie.kogito.jobs.service.resource.InfinispanServerTestResource;
import org.kie.kogito.jobs.service.stream.JobStreams;

import static org.mockito.Mockito.mock;

@QuarkusTest
@QuarkusTestResource(InfinispanServerTestResource.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class InfinispanJobRepositoryIT extends BaseJobRepositoryTest {

    private InfinispanJobRepository tested;

    @Inject
    RemoteCacheManager remoteCacheManager;

    @BeforeEach
    public void setUp() {
        remoteCacheManager
                .administration()
                .getOrCreateCache(InfinispanConfiguration.Caches.SCHEDULED_JOBS, (String) null)
                .clear();
        tested = new InfinispanJobRepository(mockVertx(), mockJobStreams(), remoteCacheManager);
        super.setUp();
    }

    @Override
    public ReactiveJobRepository tested() {
        return tested;
    }
}