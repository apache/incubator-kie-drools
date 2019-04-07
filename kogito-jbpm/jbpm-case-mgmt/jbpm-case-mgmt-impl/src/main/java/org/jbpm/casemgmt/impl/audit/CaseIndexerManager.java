/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.jbpm.casemgmt.impl.audit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.ServiceLoader;

import org.jbpm.casemgmt.api.audit.CaseFileData;
import org.jbpm.casemgmt.api.audit.CaseVariableIndexer;
import org.jbpm.casemgmt.api.event.CaseEvent;

/**
 * Represents logic behind mechanism to index task variables.
 * Supports custom indexers to be loaded dynamically via JDK ServiceLoader
 * 
 * Adds default indexer (org.jbpm.casemgmt.impl.audit.variable.StringTaskVariableIndexer) as the last indexer
 * as it accepts all types
 *
 */
public class CaseIndexerManager {
    
    private static ServiceLoader<CaseVariableIndexer> caseVariableIndexers = ServiceLoader.load(CaseVariableIndexer.class);
    
    private static CaseIndexerManager INSTANCE;
    
    private List<CaseVariableIndexer> indexers = new ArrayList<>();
    
    private CaseIndexerManager() {
        for (CaseVariableIndexer indexer : caseVariableIndexers) {
            indexers.add(indexer);
        }
        
        // always add at the end the default one
        indexers.add(new StringCaseVariableIndexer());
    }
    
    public List<CaseFileData> index(CaseEvent caseEvent, String variableName, Object variable) {        
        for (CaseVariableIndexer indexer : indexers) {
            if (indexer.accept(variable)) {
                List<CaseFileData> indexed = indexer.index(variableName, variable);
                
                if (indexed != null) {
                    List<CaseFileData> dataItems = new ArrayList<>();              
                    
                    for (CaseFileData caseVariable : indexed) {
                        CaseFileDataLog caseFileDataLog = new CaseFileDataLog(caseEvent.getCaseId(), caseEvent.getCaseFile().getDefinitionId(), caseVariable.getItemName());                                        
                        
                        caseFileDataLog.setItemType(caseVariable.getItemType());
                        caseFileDataLog.setItemValue(caseVariable.getItemValue());
                        caseFileDataLog.setLastModified(new Date());
                        caseFileDataLog.setLastModifiedBy(caseEvent.getUser());
                        
                        dataItems.add(caseFileDataLog);
                    }
                    
                    return dataItems;

                }
            }
        }
        
        return null;
    }
    
    public List<String> getIndexNames(String variableName, Object variable) {
        if (variable == null) {
            return Collections.singletonList(variableName);
        }
        for (CaseVariableIndexer indexer : indexers) {
            if (indexer.accept(variable)) {
                List<String> indexed = indexer.getIndexNames(variableName);
                
                return indexed;                
            }
        }
        
        return null;
    }
    
    public static CaseIndexerManager get() {
        if (INSTANCE == null) {
            INSTANCE = new CaseIndexerManager();
        }
        
        return INSTANCE;
    }
}
