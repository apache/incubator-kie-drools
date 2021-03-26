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

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.kie.kogito.persistence.redis.index.RedisCreateIndexEvent;
import org.kie.kogito.persistence.redis.index.RedisIndexManager;
import org.kie.kogito.trusty.storage.api.model.Execution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.quarkus.runtime.Startup;
import io.redisearch.Schema;

import static org.kie.kogito.trusty.storage.common.TrustyStorageService.DECISIONS_STORAGE;
import static org.kie.kogito.trusty.storage.common.TrustyStorageService.EXPLAINABILITY_RESULTS_STORAGE;
import static org.kie.kogito.trusty.storage.common.TrustyStorageService.MODELS_STORAGE;

@Singleton
@Startup
public class IndexProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(IndexProvider.class);

    private RedisIndexManager indexManager;

    @Inject
    public IndexProvider(RedisIndexManager redisIndexManager) {
        this.indexManager = redisIndexManager;
    }

    @PostConstruct
    public void createIndexes() {
        LOGGER.debug("Creating redis indexes for Trusty Service.");
        createDecisionStorageIndex();

        createModelsStorageIndex();

        createExplainabilityResultsStorageIndex();
        LOGGER.debug("Creation of redis indexes completed.");
    }

    private void createDecisionStorageIndex() {
        RedisCreateIndexEvent decisionIndexEvent = new RedisCreateIndexEvent(DECISIONS_STORAGE);
        decisionIndexEvent.withField(new Schema.Field(Execution.EXECUTION_ID_FIELD, Schema.FieldType.FullText, false));
        decisionIndexEvent.withField(new Schema.Field(Execution.EXECUTION_TIMESTAMP_FIELD, Schema.FieldType.Numeric, true));
        indexManager.createIndex(decisionIndexEvent);
    }

    private void createModelsStorageIndex() {
        RedisCreateIndexEvent modelIndexEvent = new RedisCreateIndexEvent(MODELS_STORAGE);
        indexManager.createIndex(modelIndexEvent);
    }

    private void createExplainabilityResultsStorageIndex() {
        RedisCreateIndexEvent explainabilityIndexEvent = new RedisCreateIndexEvent(EXPLAINABILITY_RESULTS_STORAGE);
        indexManager.createIndex(explainabilityIndexEvent);
    }
}
