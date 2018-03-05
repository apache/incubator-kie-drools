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

package org.jbpm.kie.services.impl.query.mapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dashbuilder.dataset.DataSet;
import org.jbpm.services.api.model.ProcessInstanceWithVarsDesc;
import org.jbpm.services.api.query.QueryResultMapper;

/**
 * Dedicated mapper to transform data set into list of ProcessInstanceWithVarsDesc
 *
 */
public class ProcessInstanceWithVarsQueryMapper extends AbstractQueryMapper<ProcessInstanceWithVarsDesc> implements QueryResultMapper<List<ProcessInstanceWithVarsDesc>> {
    
    private static final long serialVersionUID = 5935133069234696715L;
    /**
     * Dedicated for ServiceLoader to create instance, use <code>get()</code> method instead 
     */
    public ProcessInstanceWithVarsQueryMapper() {
        super();
    }
    
    public static ProcessInstanceWithVarsQueryMapper get() {
        return new ProcessInstanceWithVarsQueryMapper();
    }

    @Override
    public List<ProcessInstanceWithVarsDesc> map(Object result) {
        if (result instanceof DataSet) {
            DataSet dataSetResult = (DataSet) result;
            List<ProcessInstanceWithVarsDesc> mappedResult = new ArrayList<ProcessInstanceWithVarsDesc>();
            
            Map<Long, ProcessInstanceWithVarsDesc> tmp = new HashMap<Long, ProcessInstanceWithVarsDesc>();
            
            if (dataSetResult != null) {
                
                for (int i = 0; i < dataSetResult.getRowCount(); i++) {
                    Long processInstanceId = getColumnLongValue(dataSetResult, COLUMN_PROCESSINSTANCEID, i);
                    ProcessInstanceWithVarsDesc pi = tmp.get(processInstanceId);
                    if (pi == null) {
                        pi = buildInstance(dataSetResult, i);                        
                        mappedResult.add(pi);
                        
                        tmp.put(processInstanceId, pi);
                    }
                    // now add variable
                    String varName = getColumnStringValue(dataSetResult, COLUMN_VAR_NAME, i);
                    String varValue = getColumnStringValue(dataSetResult, COLUMN_VAR_VALUE, i);
                    
                    ((org.jbpm.kie.services.impl.model.ProcessInstanceWithVarsDesc)pi).addVariable(varName, varValue);
                }
            }
            tmp = null;
            return mappedResult;
        }
        
        throw new IllegalArgumentException("Unsupported result for mapping " + result);
    }
    
    protected ProcessInstanceWithVarsDesc buildInstance(DataSet dataSetResult, int index) {
        ProcessInstanceWithVarsDesc pi = new org.jbpm.kie.services.impl.model.ProcessInstanceWithVarsDesc(
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
                getColumnDateValue(dataSetResult, COLUMN_SLA_DUE_DATE, index),
                getColumnIntValue(dataSetResult, COLUMN_SLA_COMPLIANCE, index)
                );
        return pi;
    }
    
    @Override
    public String getName() {
        return "ProcessInstancesWithVariables";
    }

    @Override
    public Class<?> getType() {
        return ProcessInstanceWithVarsDesc.class;
    }

    @Override
    public QueryResultMapper<List<ProcessInstanceWithVarsDesc>> forColumnMapping(Map<String, String> columnMapping) {
        return new ProcessInstanceWithVarsQueryMapper();
    }

}
