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

package org.kie.kogito.trusty.service;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.kie.kogito.persistence.api.Storage;
import org.kie.kogito.persistence.api.query.AttributeFilter;
import org.kie.kogito.persistence.api.query.QueryFilterFactory;
import org.kie.kogito.trusty.storage.api.TrustyStorageService;
import org.kie.kogito.trusty.storage.api.model.Decision;
import org.kie.kogito.trusty.storage.api.model.Execution;

@ApplicationScoped
public class TrustyService implements ITrustyService {

    private TrustyStorageService storageService;

    TrustyService() {
        // dummy constructor needed
    }

    @Inject
    public TrustyService(TrustyStorageService storageService) {
        this.storageService = storageService;
    }

    @Override
    public List<Execution> getExecutionHeaders(OffsetDateTime from, OffsetDateTime to, int limit, int offset, String prefix) {
        Storage<String, Decision> storage = storageService.getDecisionsStorage();
        List<AttributeFilter<?>> filters = new ArrayList<>();
        filters.add(QueryFilterFactory.like(Execution.EXECUTION_ID_FIELD, prefix + "*"));
        filters.add(QueryFilterFactory.greaterThanEqual(Execution.EXECUTION_TIMESTAMP_FIELD, from.toInstant().toEpochMilli()));
        filters.add(QueryFilterFactory.lessThanEqual(Execution.EXECUTION_TIMESTAMP_FIELD, to.toInstant().toEpochMilli()));
        return new ArrayList<>(storage.query().limit(limit).offset(offset).filter(filters).execute());
    }

    @Override
    public Decision getDecisionById(String executionId) {
        Storage<String, Decision> storage = storageService.getDecisionsStorage();
        if (!storage.containsKey(executionId)) {
            throw new IllegalArgumentException(String.format("A decision with ID %s does not exist in the storage.", executionId));
        }
        return storage.get(executionId);
    }

    @Override
    public void storeDecision(String executionId, Decision decision) {
        Storage<String, Decision> storage = storageService.getDecisionsStorage();
        if (storage.containsKey(executionId)) {
            throw new IllegalArgumentException(String.format("A decision with ID %s is already present in the storage.", executionId));
        }
        storage.put(executionId, decision);
    }

    @Override
    public void updateDecision(String executionId, Decision decision) {
        storageService.getDecisionsStorage().put(executionId, decision);
    }
}
