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
package org.kie.kogito.index.mongodb.storage;

import org.kie.kogito.index.storage.DataIndexStorageService;
import org.kie.kogito.persistence.mongodb.index.IndexCreateOrUpdateEvent;
import org.kie.kogito.persistence.mongodb.index.ProcessIndexEvent;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Event;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;

import static org.kie.kogito.index.mongodb.Constants.getDomainCollectionName;

@ApplicationScoped
public class ProcessIndexObserver {

    @Inject
    DataIndexStorageService dataIndexStorageService;

    @Inject
    Event<IndexCreateOrUpdateEvent> indexCreateOrUpdateEvent;

    public void onProcessIndexEvent(@Observes ProcessIndexEvent event) {
        String processId = event.getProcessDescriptor().getProcessId();
        String processType = event.getProcessDescriptor().getProcessType();
        dataIndexStorageService.getProcessIdModelCache().put(processId, processType);

        indexCreateOrUpdateEvent.fire(new IndexCreateOrUpdateEvent(getDomainCollectionName(processId), processType));
    }
}
