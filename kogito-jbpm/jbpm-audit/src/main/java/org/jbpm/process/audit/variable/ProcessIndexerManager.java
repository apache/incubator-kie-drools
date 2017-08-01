/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.jbpm.process.audit.variable;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

import org.jbpm.process.audit.event.AuditEventBuilder;
import org.kie.api.event.process.ProcessVariableChangedEvent;
import org.kie.api.runtime.manager.audit.VariableInstanceLog;
import org.kie.internal.process.ProcessVariableIndexer;

/**
 * Represents logic behind mechanism to index task variables.
 * Supports custom indexers to be loaded dynamically via JDK ServiceLoader
 * 
 * Adds default indexer (org.jbpm.services.task.audit.variable.StringTaskVariableIndexer) as the last indexer
 * as it accepts all types
 *
 */
public class ProcessIndexerManager {
    
    private static ServiceLoader<ProcessVariableIndexer> taskVariableIndexers = ServiceLoader.load(ProcessVariableIndexer.class);
    
    private static ProcessIndexerManager INSTANCE;
    
    private List<ProcessVariableIndexer> indexers = new ArrayList<ProcessVariableIndexer>();
    
    private ProcessIndexerManager() {
        for (ProcessVariableIndexer indexer : taskVariableIndexers) {
            indexers.add(indexer);
        }
        
        // always add at the end the default one
        indexers.add(new StringProcessVariableIndexer());
    }
    
    public List<VariableInstanceLog> index(AuditEventBuilder builder, ProcessVariableChangedEvent event) {
        String variableName = event.getVariableId();
        Object variable = event.getNewValue();
        
        
        for (ProcessVariableIndexer indexer : indexers) {
            if (indexer.accept(variable)) {
                List<VariableInstanceLog> indexed = indexer.index(variableName, variable);
                
                if (indexed != null) {
                                   
                    // populate all indexed variables with task information
                    for (VariableInstanceLog processVariable : indexed) {
                        VariableInstanceLog log = (VariableInstanceLog) builder.buildEvent(event);
                        
                        ((org.jbpm.process.audit.VariableInstanceLog)processVariable).setProcessId(log.getProcessId());
                        ((org.jbpm.process.audit.VariableInstanceLog)processVariable).setProcessInstanceId(log.getProcessInstanceId());
                        ((org.jbpm.process.audit.VariableInstanceLog)processVariable).setDate(log.getDate());
                        ((org.jbpm.process.audit.VariableInstanceLog)processVariable).setExternalId(log.getExternalId());
                        ((org.jbpm.process.audit.VariableInstanceLog)processVariable).setOldValue(log.getOldValue());
                        ((org.jbpm.process.audit.VariableInstanceLog)processVariable).setVariableInstanceId(log.getVariableInstanceId());
                    }
                    
                    return indexed;

                }
            }
        }
        
        return null;
    }
    
    public static ProcessIndexerManager get() {
        if (INSTANCE == null) {
            INSTANCE = new ProcessIndexerManager();
        }
        
        return INSTANCE;
    }
}
