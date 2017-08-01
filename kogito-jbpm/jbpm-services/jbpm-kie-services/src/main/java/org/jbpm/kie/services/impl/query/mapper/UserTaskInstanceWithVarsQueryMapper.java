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
import org.jbpm.services.api.model.UserTaskInstanceWithVarsDesc;
import org.jbpm.services.api.query.QueryResultMapper;

/**
 * Dedicated mapper that transforms data set to list of UserTaskInstanceWithVars
 *
 */
public class UserTaskInstanceWithVarsQueryMapper extends AbstractQueryMapper<UserTaskInstanceWithVarsDesc> implements QueryResultMapper<List<UserTaskInstanceWithVarsDesc>> {

    private static final long serialVersionUID = 5935133069234696719L;
    /**
     * Dedicated for ServiceLoader to create instance, use <code>get()</code> method instead 
     */
    public UserTaskInstanceWithVarsQueryMapper() {
        super();
    }
    
    public static UserTaskInstanceWithVarsQueryMapper get() {
        return new UserTaskInstanceWithVarsQueryMapper();
    }
    
    @Override
    public List<UserTaskInstanceWithVarsDesc> map(Object result) {
        if (result instanceof DataSet) {
            DataSet dataSetResult = (DataSet) result;
            List<UserTaskInstanceWithVarsDesc> mappedResult = new ArrayList<UserTaskInstanceWithVarsDesc>();
            
            if (dataSetResult != null) {
                Map<Long, UserTaskInstanceWithVarsDesc> tmp = new HashMap<Long, UserTaskInstanceWithVarsDesc>();
                
                for (int i = 0; i < dataSetResult.getRowCount(); i++) {
                    
                    Long taskId = getColumnLongValue(dataSetResult, COLUMN_TASKID, i);
                    UserTaskInstanceWithVarsDesc ut = tmp.get(taskId);
                    if (ut == null) {

                        ut = buildInstance(dataSetResult, i);
                        mappedResult.add(ut);    
                        
                        tmp.put(taskId, ut);
                    }
                    // now add variable
                    String varName = getColumnStringValue(dataSetResult, COLUMN_TASK_VAR_NAME, i);
                    String varValue = getColumnStringValue(dataSetResult, COLUMN_TASK_VAR_VALUE, i);
                    
                    ((org.jbpm.kie.services.impl.model.UserTaskInstanceWithVarsDesc)ut).addVariable(varName, varValue);
                                    
                }
            }
            
            return mappedResult;
        }
        
        throw new IllegalArgumentException("Unsupported result for mapping " + result);
    }

    @Override
    protected UserTaskInstanceWithVarsDesc buildInstance(DataSet dataSetResult, int index) {
        UserTaskInstanceWithVarsDesc userTask = new org.jbpm.kie.services.impl.model.UserTaskInstanceWithVarsDesc(
                getColumnLongValue(dataSetResult, COLUMN_TASKID, index),//taskId,
                getColumnStringValue(dataSetResult, COLUMN_TASK_STATUS, index),//status,
                getColumnDateValue(dataSetResult, COLUMN_ACTIVATIONTIME, index),//activationTime,
                getColumnStringValue(dataSetResult, COLUMN_NAME, index),//name,
                getColumnStringValue(dataSetResult, COLUMN_DESCRIPTION, index),//description,
                getColumnIntValue(dataSetResult, COLUMN_PRIORITY, index),//priority,
                getColumnStringValue(dataSetResult, COLUMN_ACTUALOWNER, index),//actualOwner,
                getColumnStringValue(dataSetResult, COLUMN_CREATEDBY, index),//createdBy,
                getColumnStringValue(dataSetResult, COLUMN_DEPLOYMENTID, index),//deploymentId,
                getColumnStringValue(dataSetResult, COLUMN_TASK_PROCESSID, index),//processId,
                getColumnLongValue(dataSetResult, COLUMN_TASK_PROCESSINSTANCEID, index),//processInstanceId,
                getColumnDateValue(dataSetResult, COLUMN_CREATEDON, index),//createdOn,
                getColumnDateValue(dataSetResult, COLUMN_DUEDATE, index)//dueDate
                );
        return userTask;
    }
    
    @Override
    public String getName() {
        return "UserTasksWithVariables";
    }

    @Override
    public Class<?> getType() {
        return UserTaskInstanceWithVarsDesc.class;
    }

    @Override
    public QueryResultMapper<List<UserTaskInstanceWithVarsDesc>> forColumnMapping(Map<String, String> columnMapping) {
        return new UserTaskInstanceWithVarsQueryMapper();
    }

}
