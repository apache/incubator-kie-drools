/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.kie.kogito.persistence.infinispan.cache;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.kogito.persistence.api.StorageService;

import static org.kie.kogito.persistence.infinispan.Constants.INFINISPAN_STORAGE;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class InfinispanCacheShutdownObserverTest {

    StorageService nonInfinispanStorageService = mock(StorageService.class);

    InfinispanStorageService infinispanStorageService = mock(InfinispanStorageService.class);

    Optional<String> nonInfinispanStorageType = Optional.of("testStorage");

    InfinispanCacheShutdownObserver observer = new InfinispanCacheShutdownObserver();

    @BeforeEach
    void setup() {
        reset(nonInfinispanStorageService, infinispanStorageService);
    }

    @Test
    void testStop_nonInfinispanStorageType() {
        observer.cacheService = infinispanStorageService;
        observer.storageType = nonInfinispanStorageType;

        observer.stop(null);

        verify(infinispanStorageService, never()).destroy();
    }

    @Test
    void testStop_nonInfinispanStorageService() {
        observer.cacheService = nonInfinispanStorageService;
        observer.storageType = Optional.of(INFINISPAN_STORAGE);

        observer.stop(null);

        verify(infinispanStorageService, never()).destroy();
    }

    @Test
    void testStop_infinispanStorageService() {
        observer.cacheService = infinispanStorageService;
        observer.storageType = Optional.of(INFINISPAN_STORAGE);

        observer.stop(null);

        verify(infinispanStorageService, times(1)).destroy();
    }
}