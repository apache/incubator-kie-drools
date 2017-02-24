/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.services.api.query;

import java.io.Serializable;
import java.util.Map;

/**
 * Maps raw data set into object instances
 *
 * @param <T> type of the object this mapper will produce
 */
public interface QueryResultMapper<T> extends Serializable {

    /**
     * Based on raw data set returns mapped/transformed data. Usually it will get set of raw data
     * and return list (or collection in general) of custom object like ProcessInstance or UserTaskInstance
     * @param result raw data set in custom format
     * @return mapped result of raw data set
     */
    T map(Object result);
    
    /**
     * Returns unique name of this query result mapper implementation
     * @return
     */
    String getName();
    
    /**
     * Returns type of the data produced by this mapper. 
     * If the type is collection then the returned type should 
     * be type of the elements in collection e.g.:<br/>
     * <code>
     *  List&lt;ProcessInstanceDesc&gt; returned type should be ProcessInstanceDesc.class
     * </code>
     * @return
     */
    Class<?> getType();
    
    /**
     * Returns new instance of the mapper for given column mapping
     * @param columnMapping provides column mapping (name to type) that can be 
     * shipped to mapper for improved transformation - can be null (accepted types: string, long, integer, date, double)
     * @return new instance of the mapper configured with column mapping
     */
    QueryResultMapper<T> forColumnMapping(Map<String, String> columnMapping);
    
    // process instance related
    public static final String COLUMN_PROCESSINSTANCEID = "PROCESSINSTANCEID";
    public static final String COLUMN_PROCESSID = "PROCESSID";
    public static final String COLUMN_START = "START_DATE";
    public static final String COLUMN_END = "END_DATE";
    public static final String COLUMN_STATUS = "STATUS";
    public static final String COLUMN_PARENTPROCESSINSTANCEID = "PARENTPROCESSINSTANCEID";
    public static final String COLUMN_OUTCOME = "OUTCOME";
    public static final String COLUMN_DURATION = "DURATION";
    public static final String COLUMN_IDENTITY = "USER_IDENTITY";
    public static final String COLUMN_PROCESSVERSION = "PROCESSVERSION";
    public static final String COLUMN_PROCESSNAME = "PROCESSNAME";
    public static final String COLUMN_CORRELATIONKEY = "CORRELATIONKEY";
    public static final String COLUMN_EXTERNALID = "EXTERNALID";
    public static final String COLUMN_PROCESSINSTANCEDESCRIPTION = "PROCESSINSTANCEDESCRIPTION";
    
    // process variable related
    public static final String COLUMN_VAR_NAME = "VARIABLEID";
    public static final String COLUMN_VAR_VALUE = "VALUE";
    
    // task related
    public static final String COLUMN_ACTIVATIONTIME = "ACTIVATIONTIME";
    public static final String COLUMN_ACTUALOWNER = "ACTUALOWNER";
    public static final String COLUMN_CREATEDBY = "CREATEDBY";
    public static final String COLUMN_CREATEDON = "CREATEDON";
    public static final String COLUMN_DEPLOYMENTID = "DEPLOYMENTID";
    public static final String COLUMN_DESCRIPTION = "DESCRIPTION";
    public static final String COLUMN_DUEDATE = "DUEDATE";
    public static final String COLUMN_NAME = "NAME";
    public static final String COLUMN_SUBJECT = "SUBJECT";
    public static final String COLUMN_PARENTID = "PARENTID";
    public static final String COLUMN_PRIORITY = "PRIORITY";
    public static final String COLUMN_TASK_PROCESSID = "PROCESSID";
    public static final String COLUMN_TASK_PROCESSINSTANCEID = "PROCESSINSTANCEID";
    public static final String COLUMN_TASK_STATUS = "STATUS";
    public static final String COLUMN_TASKID = "TASKID";
    public static final String COLUMN_WORKITEMID = "WORKITEMID";
    public static final String COLUMN_ORGANIZATIONAL_ENTITY = "ID";
    
    // task variables related
    public static final String COLUMN_TASK_VAR_NAME = "TVNAME";
    public static final String COLUMN_TASK_VAR_VALUE = "TVVALUE";
    public static final String COLUMN_TASK_VAR_TYPE = "TVTYPE";
    
    //jobs related
    public static final String COLUMN_JOB_ID = "id";
    public static final String COLUMN_JOB_TIMESTAMP = "timestamp";
    public static final String COLUMN_JOB_STATUS = "status";
    public static final String COLUMN_JOB_COMMANDNAME = "commandName";
    public static final String COLUMN_JOB_MESSAGE = "message";
    public static final String COLUMN_JOB_BUSINESSKEY = "businessKey";
   
}
