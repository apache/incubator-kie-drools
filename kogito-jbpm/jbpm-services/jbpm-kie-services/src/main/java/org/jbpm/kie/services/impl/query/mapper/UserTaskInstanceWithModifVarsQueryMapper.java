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
import org.jbpm.services.api.model.UserTaskInstanceWithPotOwnerDesc;
import org.jbpm.services.api.query.QueryResultMapper;

public class UserTaskInstanceWithModifVarsQueryMapper extends AbstractQueryMapper<UserTaskInstanceWithPotOwnerDesc> implements QueryResultMapper<List<UserTaskInstanceWithPotOwnerDesc>> {

    private static final long serialVersionUID = 970076943653545833L;

    /**
     * Dedicated for ServiceLoader to create instance, use <code>get()</code> method instead 
     */
    public UserTaskInstanceWithModifVarsQueryMapper() {
        super();
    }
    
    public static UserTaskInstanceWithModifVarsQueryMapper get() {
        return new UserTaskInstanceWithModifVarsQueryMapper();
    }

    @Override
    public List<UserTaskInstanceWithPotOwnerDesc> map(Object result) {
        if (result instanceof DataSet) {
            DataSet dataSetResult = (DataSet) result;
            List<UserTaskInstanceWithPotOwnerDesc> mappedResult = new ArrayList<UserTaskInstanceWithPotOwnerDesc>();
                                                           
            if (dataSetResult != null) {
                Map<Long, UserTaskInstanceWithPotOwnerDesc> tmp = new HashMap<Long, UserTaskInstanceWithPotOwnerDesc>();
                
                for (int i = 0; i < dataSetResult.getRowCount(); i++) {
                    Long taskId = getColumnLongValue(dataSetResult, COLUMN_TASKID, i);
                    UserTaskInstanceWithPotOwnerDesc ut = tmp.get(taskId);
                    if (ut == null) {
                        ut = buildInstance(dataSetResult, i);
                        mappedResult.add(ut);    
                        
                        tmp.put(taskId, ut);
                    }else if(!tmp.get(taskId).getPotentialOwners().contains(getColumnStringValue(dataSetResult, COLUMN_POTOWNER, i))){
                        
                        ((org.jbpm.kie.services.impl.model.UserTaskInstanceWithPotOwnerDesc)ut).addPotOwner(getColumnStringValue(dataSetResult, COLUMN_POTOWNER, i));
                    }
                     
                    String varName = getColumnStringValue(dataSetResult, COLUMN_TASK_VAR_NAME, i);
                    String varValue = getColumnStringValue(dataSetResult, COLUMN_TASK_VAR_VALUE, i);
                    
                    if(getColumnIntValue(dataSetResult, COLUMN_TASK_VAR_TYPE, i) == 0) {
                        ((org.jbpm.kie.services.impl.model.UserTaskInstanceWithPotOwnerDesc)ut).addInputdata(varName, varValue);
                    }else {
                        ((org.jbpm.kie.services.impl.model.UserTaskInstanceWithPotOwnerDesc)ut).addOutputdata(varName, varValue);
                    }             
                }
            }
            
            return mappedResult;
        }
        
        throw new IllegalArgumentException("Unsupported result for mapping " + result);
    }

    @Override
    public String getName() {
        return "UserTasksWithModifications";
    }

    @Override
    public Class<?> getType() {
        return UserTaskInstanceWithPotOwnerDesc.class;
    }

    @Override
    public QueryResultMapper<List<UserTaskInstanceWithPotOwnerDesc>> forColumnMapping(Map<String, String> columnMapping) {
        return new UserTaskInstanceWithModifVarsQueryMapper();
    }

    @Override
    protected UserTaskInstanceWithPotOwnerDesc buildInstance(DataSet dataSetResult, int index) {
       
        UserTaskInstanceWithPotOwnerDesc customUserTask = new org.jbpm.kie.services.impl.model.UserTaskInstanceWithPotOwnerDesc(
            getColumnLongValue(dataSetResult, COLUMN_TASKID, index),//taskId,
            getColumnStringValue(dataSetResult, COLUMN_NAME, index),//name,
            getColumnStringValue(dataSetResult, COLUMN_FORM_NAME, index),//forname,
            getColumnStringValue(dataSetResult, COLUMN_SUBJECT, index),//subject,
            getColumnStringValue(dataSetResult, COLUMN_ACTUALOWNER, index),//actualOwner,
            getColumnStringValue(dataSetResult, COLUMN_POTOWNER, index),//potowner,
            getColumnStringValue(dataSetResult, COLUMN_CORRELATIONKEY, index),//correlationkey,
            getColumnDateValue(dataSetResult, COLUMN_CREATEDON, index),//createdOn,
            getColumnStringValue(dataSetResult, COLUMN_CREATEDBY, index),//createdBy,
            getColumnDateValue(dataSetResult, COLUMN_EXPIRATIONTIME, index),//expirationtime,
            getColumnDateValue(dataSetResult, COLUMN_LASTMODIFICATION_DATE, index),//lastmodificationdate,
            getColumnStringValue(dataSetResult, COLUMN_LASTMODIFICATION_USER, index),//lastmodificationUser,
            getColumnIntValue(dataSetResult, COLUMN_PRIORITY, index),//priority,
            getColumnStringValue(dataSetResult, COLUMN_TASK_STATUS, index),//status,
            getColumnLongValue(dataSetResult, COLUMN_TASK_PROCESSINSTANCEID, index),//processInstanceId,
            getColumnStringValue(dataSetResult, COLUMN_TASK_PROCESSID, index),//processId,
            getColumnStringValue(dataSetResult, COLUMN_DEPLOYMENTID, index),//deploymentId
            getColumnStringValue(dataSetResult, COLUMN_PROCESSINSTANCEDESCRIPTION, index)//processInstanceDescription
            );
             
      return customUserTask;    
    }

}
