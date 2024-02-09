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
package org.kie.kogito.index.storage;

import org.kie.kogito.event.process.ProcessInstanceDataEvent;
import org.kie.kogito.event.process.ProcessInstanceErrorDataEvent;
import org.kie.kogito.event.process.ProcessInstanceNodeDataEvent;
import org.kie.kogito.event.process.ProcessInstanceSLADataEvent;
import org.kie.kogito.event.process.ProcessInstanceStateDataEvent;
import org.kie.kogito.event.process.ProcessInstanceVariableDataEvent;
import org.kie.kogito.index.model.ProcessInstance;
import org.kie.kogito.index.storage.merger.ProcessInstanceErrorDataEventMerger;
import org.kie.kogito.index.storage.merger.ProcessInstanceEventMerger;
import org.kie.kogito.index.storage.merger.ProcessInstanceNodeDataEventMerger;
import org.kie.kogito.index.storage.merger.ProcessInstanceSLADataEventMerger;
import org.kie.kogito.index.storage.merger.ProcessInstanceStateDataEventMerger;
import org.kie.kogito.index.storage.merger.ProcessInstanceVariableDataEventMerger;
import org.kie.kogito.persistence.api.Storage;

public class ModelProcessInstanceStorage extends ModelStorageFetcher<String, ProcessInstance> implements ProcessInstanceStorage {
    private final ProcessInstanceErrorDataEventMerger errorMerger = new ProcessInstanceErrorDataEventMerger();
    private final ProcessInstanceNodeDataEventMerger nodeMerger = new ProcessInstanceNodeDataEventMerger();
    private final ProcessInstanceSLADataEventMerger slaMerger = new ProcessInstanceSLADataEventMerger();
    private final ProcessInstanceStateDataEventMerger stateMerger = new ProcessInstanceStateDataEventMerger();
    private final ProcessInstanceVariableDataEventMerger variableMerger = new ProcessInstanceVariableDataEventMerger();

    public ModelProcessInstanceStorage(Storage<String, ProcessInstance> storage) {
        super(storage);
    }

    @Override
    public void indexError(ProcessInstanceErrorDataEvent event) {
        index(event, errorMerger);
    }

    @Override
    public void indexNode(ProcessInstanceNodeDataEvent event) {
        index(event, nodeMerger);
    }

    @Override
    public void indexSLA(ProcessInstanceSLADataEvent event) {
        index(event, slaMerger);
    }

    @Override
    public void indexState(ProcessInstanceStateDataEvent event) {
        index(event, stateMerger);
    }

    @Override
    public void indexVariable(ProcessInstanceVariableDataEvent event) {
        index(event, variableMerger);
    }

    private <T extends ProcessInstanceDataEvent<?>> void index(T event, ProcessInstanceEventMerger merger) {
        ProcessInstance processInstance = storage.get(event.getKogitoProcessInstanceId());
        if (processInstance == null) {
            processInstance = new ProcessInstance();
            processInstance.setId(event.getKogitoProcessInstanceId());
            processInstance.setProcessId(event.getKogitoProcessId());
        }
        storage.put(event.getKogitoProcessInstanceId(), merger.merge(processInstance, event));
    }
}
