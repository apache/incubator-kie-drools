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
import org.jbpm.services.task.query.TaskSummaryImpl;
import org.kie.api.task.model.Status;
import org.kie.api.task.model.TaskSummary;

/**
 * Dedicated mapper that transform data set into List of TaskSummary
 *
 */
public class TaskSummaryQueryMapper extends AbstractQueryMapper<TaskSummary> implements QueryResultMapper<List<TaskSummary>> {

    private static final long serialVersionUID = 5935133069234696712L;
    /**
     * Dedicated for ServiceLoader to create instance, use <code>get()</code> method instead 
     */
    public TaskSummaryQueryMapper() {
        super();
    }
    
    public static TaskSummaryQueryMapper get() {
        return new TaskSummaryQueryMapper();
    }
    
    @Override
    public List<TaskSummary> map(Object result) {
        if (result instanceof DataSet) {
            DataSet dataSetResult = (DataSet) result;
            List<TaskSummary> mappedResult = new ArrayList<TaskSummary>();
            
            if (dataSetResult != null) {
                
                for (int i = 0; i < dataSetResult.getRowCount(); i++) {
                    TaskSummary ut = buildInstance(dataSetResult, i);
                    mappedResult.add(ut);                
                }
            }
            
            return mappedResult;
        }
        
        throw new IllegalArgumentException("Unsupported result for mapping " + result);
    }

    @Override
    protected TaskSummary buildInstance(DataSet dataSetResult, int index) {        
        TaskSummary userTask = new TaskSummaryImpl(
                getColumnLongValue(dataSetResult, COLUMN_TASKID, index),//taskId,
                getColumnStringValue(dataSetResult, COLUMN_NAME, index),//name,
                getColumnStringValue(dataSetResult, COLUMN_SUBJECT, index),//subject,
                getColumnStringValue(dataSetResult, COLUMN_DESCRIPTION, index),//description,
                Status.valueOf(getColumnStringValue(dataSetResult, COLUMN_TASK_STATUS, index)),//status,
                getColumnIntValue(dataSetResult, COLUMN_PRIORITY, index),//priority,
                getColumnStringValue(dataSetResult, COLUMN_ACTUALOWNER, index),//actualOwner,
                getColumnStringValue(dataSetResult, COLUMN_CREATEDBY, index),//createdBy,
                getColumnDateValue(dataSetResult, COLUMN_CREATEDON, index),//createdOn,                
                getColumnDateValue(dataSetResult, COLUMN_ACTIVATIONTIME, index),//activationTime,
                getColumnDateValue(dataSetResult, COLUMN_DUEDATE, index),//dueDate
                getColumnStringValue(dataSetResult, COLUMN_TASK_PROCESSID, index),//processId,
                getColumnLongValue(dataSetResult, COLUMN_TASK_PROCESSINSTANCEID, index),//processInstanceId,                                
                -1l,
                getColumnStringValue(dataSetResult, COLUMN_DEPLOYMENTID, index),//deploymentId,
                false                
                );
        
        return userTask;
    }
    
    @Override
    public String getName() {
        return "TaskSummaries";
    }

    @Override
    public Class<?> getType() {
        return TaskSummary.class;
    }

    @Override
    public QueryResultMapper<List<TaskSummary>> forColumnMapping(Map<String, String> columnMapping) {
        return new TaskSummaryQueryMapper();
    }

}
