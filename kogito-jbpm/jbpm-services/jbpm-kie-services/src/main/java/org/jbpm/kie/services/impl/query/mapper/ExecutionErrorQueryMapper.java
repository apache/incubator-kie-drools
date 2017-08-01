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
import java.util.List;
import java.util.Map;

import org.dashbuilder.dataset.DataSet;
import org.jbpm.services.api.query.QueryResultMapper;
import org.kie.internal.runtime.error.ExecutionError;

/**
 * Dedicated mapper that transforms DataSet to ExecutionError.
 *
 */
public class ExecutionErrorQueryMapper extends AbstractQueryMapper<ExecutionError> implements QueryResultMapper<List<ExecutionError>> {
    
    private static final long serialVersionUID = 5935133069234696714L;

    /**
     * Dedicated for ServiceLoader to create instance, use <code>get()</code> method instead 
     */
    public ExecutionErrorQueryMapper() {
    }
    
    /**
     * Default access to get instance of the mapper
     * @return
     */
    public static ExecutionErrorQueryMapper get() {
        return new ExecutionErrorQueryMapper();
    }

    @Override
    public List<ExecutionError> map(Object result) {
        if (result instanceof DataSet) {
            DataSet dataSetResult = (DataSet) result;
            List<ExecutionError> mappedResult = new ArrayList<ExecutionError>();
            
            if (dataSetResult != null) {
                
                for (int i = 0; i < dataSetResult.getRowCount(); i++) {
                    ExecutionError pi = buildInstance(dataSetResult, i);
                    mappedResult.add(pi);
                
                }
            }
            
            return mappedResult;
        }
        
        throw new IllegalArgumentException("Unsupported result for mapping " + result);
    }
    
    protected ExecutionError buildInstance(DataSet dataSetResult, int index) {
        ExecutionError error = new ExecutionError(
                getColumnStringValue(dataSetResult, COLUMN_ERROR_ID, index),
                getColumnStringValue(dataSetResult, COLUMN_ERROR_TYPE, index),
                getColumnStringValue(dataSetResult, COLUMN_ERROR_DEPLOYMENT_ID, index),
                getColumnLongValue(dataSetResult, COLUMN_ERROR_PROCESS_INST_ID, index),
                getColumnStringValue(dataSetResult, COLUMN_ERROR_PROCESS_ID, index),
                getColumnLongValue(dataSetResult, COLUMN_ERROR_ACTIVITY_ID, index),
                getColumnStringValue(dataSetResult, COLUMN_ERROR_ACTIVITY_NAME, index),
                getColumnLongValue(dataSetResult, COLUMN_ERROR_JOB_ID, index),
                getColumnStringValue(dataSetResult, COLUMN_ERROR_MSG, index),
                Integer.valueOf(getColumnIntValue(dataSetResult, COLUMN_ERROR_ACK, index)).shortValue(), 
                getColumnStringValue(dataSetResult, COLUMN_ERROR_ACK_BY, index),
                getColumnDateValue(dataSetResult, COLUMN_ERROR_ACK_AT, index), 
                getColumnDateValue(dataSetResult, COLUMN_ERROR_DATE, index)
                );
        return error;
    }

    @Override
    public String getName() {
        return "ExecutionErrors";
    }

    @Override
    public Class<?> getType() {
        return ExecutionError.class;
    }

    @Override
    public QueryResultMapper<List<ExecutionError>> forColumnMapping(Map<String, String> columnMapping) {
        return new ExecutionErrorQueryMapper();
    }

}
