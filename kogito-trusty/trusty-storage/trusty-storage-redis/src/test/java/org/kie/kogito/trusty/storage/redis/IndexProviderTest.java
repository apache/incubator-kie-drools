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

package org.kie.kogito.trusty.storage.redis;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.kie.kogito.persistence.redis.RedisClientManager;
import org.mockito.Mockito;

import static org.kie.kogito.trusty.storage.common.TrustyStorageService.DECISIONS_STORAGE;
import static org.kie.kogito.trusty.storage.common.TrustyStorageService.EXPLAINABILITY_RESULTS_STORAGE;
import static org.kie.kogito.trusty.storage.common.TrustyStorageService.MODELS_STORAGE;

public class IndexProviderTest {

    @Test
    public void indexesAreCreated() {
        RedisIndexManagerMock redisIndexManager = new RedisIndexManagerMock(Mockito.mock(RedisClientManager.class));
        IndexProvider indexProvider = new IndexProvider(redisIndexManager);

        indexProvider.createIndexes();

        Assertions.assertEquals(3, redisIndexManager.getIndexNames().size());
        Assertions.assertTrue(redisIndexManager.getIndexNames().contains(DECISIONS_STORAGE));
        Assertions.assertTrue(redisIndexManager.getIndexNames().contains(MODELS_STORAGE));
        Assertions.assertTrue(redisIndexManager.getIndexNames().contains(EXPLAINABILITY_RESULTS_STORAGE));
    }
}
