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
package org.jbpm.kie.services.impl.query.mapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dashbuilder.dataset.DataSet;
import org.jbpm.services.api.model.ProcessInstanceCustomDesc;
import org.jbpm.services.api.query.QueryResultMapper;

public class ProcessInstanceCustomQueryMapper  extends AbstractQueryMapper<ProcessInstanceCustomDesc> implements QueryResultMapper<List<ProcessInstanceCustomDesc>> {

    private static final long serialVersionUID = 5459479952842203157L;
    
    public ProcessInstanceCustomQueryMapper() {
        super();
    }
    
    public static ProcessInstanceCustomQueryMapper get() {
        return new ProcessInstanceCustomQueryMapper();
    }

    @Override
    public List<ProcessInstanceCustomDesc> map(Object result) {
        if (result instanceof DataSet) {
            DataSet dataSetResult = (DataSet) result;
            List<ProcessInstanceCustomDesc> mappedResult = new ArrayList<ProcessInstanceCustomDesc>();
            
            Map<Long, ProcessInstanceCustomDesc> tmp = new HashMap<Long, ProcessInstanceCustomDesc>();
            
            if (dataSetResult != null) {
                
                for (int i = 0; i < dataSetResult.getRowCount(); i++) {
                    Long processInstanceId = getColumnLongValue(dataSetResult, COLUMN_PROCESSINSTANCEID, i);
                    ProcessInstanceCustomDesc pi = tmp.get(processInstanceId);
                    if (pi == null) {
                        pi = buildInstance(dataSetResult, i);                        
                        mappedResult.add(pi);
                        
                        tmp.put(processInstanceId, pi);
                    }
                    // now add variable
                    String varName = getColumnStringValue(dataSetResult, COLUMN_VAR_NAME, i);
                    String varValue = getColumnStringValue(dataSetResult, COLUMN_VAR_VALUE, i);
                    
                    if (varName != null) {
                        ((org.jbpm.kie.services.impl.model.ProcessInstanceCustomDesc) pi).addVariable(varName, varValue);
                    }
                }
            }
            tmp = null;
            return mappedResult;
        }
        throw new IllegalArgumentException("Unsupported result for mapping " + result);
    }
    
    protected ProcessInstanceCustomDesc buildInstance(DataSet dataSetResult, int index) {
        ProcessInstanceCustomDesc pi = new org.jbpm.kie.services.impl.model.ProcessInstanceCustomDesc(
                getColumnLongValue(dataSetResult, COLUMN_PROCESSINSTANCEID, index),
                getColumnStringValue(dataSetResult, COLUMN_PROCESSID, index),
                getColumnStringValue(dataSetResult, COLUMN_PROCESSNAME, index),
                getColumnStringValue(dataSetResult, COLUMN_PROCESSVERSION, index),
                getColumnIntValue(dataSetResult, COLUMN_STATUS, index),
                getColumnStringValue(dataSetResult, COLUMN_EXTERNALID, index),
                getColumnDateValue(dataSetResult, COLUMN_START, index),
                getColumnStringValue(dataSetResult, COLUMN_IDENTITY, index),
                getColumnStringValue(dataSetResult, COLUMN_PROCESSINSTANCEDESCRIPTION, index),
                getColumnStringValue(dataSetResult, COLUMN_CORRELATIONKEY, index), 
                getColumnLongValue(dataSetResult, COLUMN_PARENTPROCESSINSTANCEID, index),
                getColumnDateValue(dataSetResult, COLUMN_PROCESS_LASTMODIFICATIONDATE, index)
                );
        return pi;
    }
    
    @Override
    public String getName() {
        return "ProcessInstancesCustom";
    }

    @Override
    public Class<?> getType() {
        return ProcessInstanceCustomDesc.class;
    }

    @Override
    public QueryResultMapper<List<ProcessInstanceCustomDesc>> forColumnMapping(Map<String, String> columnMapping) {
        return new ProcessInstanceCustomQueryMapper();
    }
}
