/*
 * Copyright 2015 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.kie.internal.query;

import java.util.concurrent.atomic.AtomicInteger;

public interface QueryParameterIdentifiers {

    // meta identifiers
    
    public final static String FIRST_RESULT = "firstResult";
    public final static String MAX_RESULTS = "maxResults";
    public final static String FLUSH_MODE = "flushMode";
    
    public static final String PAGE_NUMBER = "page number";
    public static final String PAGE_SIZE = "page size";
    
    public static final String FILTER = "filter";
    public static final String ORDER_BY = "orderby";
    public static final String ORDER_TYPE = "orderType";
    public static final String ASCENDING_VALUE = "ASC";
    public static final String DESCENDING_VALUE = "DESC";

    /**
     * *All* Query parameter identifiers MUST have the following format: 
     * 
     * - If a query can be executed on multiple values of the parameter ( for example, process instance id), 
     *   => Then the constant name should end with "_LIST". 
     * 
     * - If a query can only contain one value of the parameter (for example, language for task queries), 
     *   => Then the constant name should only be the parameter name.
     */
    
    // general identifiers
   
    static AtomicInteger idGen = new AtomicInteger(1);

    // general (multiple entities)
    public static final String PROCESS_ID_LIST = String.valueOf(idGen.getAndIncrement());
    public static final String PROCESS_INSTANCE_ID_LIST = String.valueOf(idGen.getAndIncrement());
    public static final String PROCESS_SESSION_ID_LIST = String.valueOf(idGen.getAndIncrement());
    public static final String WORK_ITEM_ID_LIST = String.valueOf(idGen.getAndIncrement());
    public static final String DEPLOYMENT_ID_LIST = String.valueOf(idGen.getAndIncrement());
    
    public static final String START_DATE_LIST = String.valueOf(idGen.getAndIncrement());
    public static final String END_DATE_LIST = String.valueOf(idGen.getAndIncrement());
    public static final String DATE_LIST = String.valueOf(idGen.getAndIncrement());
    
    public static final String ID_LIST = String.valueOf(idGen.getAndIncrement());
    public static final String TYPE_LIST = String.valueOf(idGen.getAndIncrement());
    
    // audit identifiers

    // - (process instance log) 
    public static final String DURATION_LIST = String.valueOf(idGen.getAndIncrement());
    public static final String IDENTITY_LIST = String.valueOf(idGen.getAndIncrement());
    public static final String PROCESS_NAME_LIST = String.valueOf(idGen.getAndIncrement());
    public static final String PROCESS_VERSION_LIST = String.valueOf(idGen.getAndIncrement());
    public static final String PROCESS_INSTANCE_STATUS_LIST = String.valueOf(idGen.getAndIncrement());
    public static final String OUTCOME_LIST = String.valueOf(idGen.getAndIncrement());
    public static final String CORRELATION_KEY_LIST = String.valueOf(idGen.getAndIncrement());
    
    // - (node instance log) 
    public static final String NODE_ID_LIST = String.valueOf(idGen.getAndIncrement());
    public static final String NODE_INSTANCE_ID_LIST = String.valueOf(idGen.getAndIncrement());
    public static final String NODE_NAME_LIST = String.valueOf(idGen.getAndIncrement());
    public static final String PROCESS_INSTANCE_PARENT_ID_LIST = String.valueOf(idGen.getAndIncrement());
    
    // - (variable instance log) 
    public static final String VARIABLE_ID_LIST = String.valueOf(idGen.getAndIncrement());
    public static final String VARIABLE_INSTANCE_ID_LIST = String.valueOf(idGen.getAndIncrement());
    public static final String VAR_VALUE_ID_LIST = String.valueOf(idGen.getAndIncrement());
    public static final String VALUE_LIST = String.valueOf(idGen.getAndIncrement());
    public static final String OLD_VALUE_LIST = String.valueOf(idGen.getAndIncrement());
    public static final String EXTERNAL_ID_LIST = String.valueOf(idGen.getAndIncrement());
    public static final String LAST_VARIABLE_LIST = String.valueOf(idGen.getAndIncrement());
   
    public static final String VAR_VAL_SEPARATOR = ":";
    
    // task identifiers
    public static final String TASK_ID_LIST = String.valueOf(idGen.getAndIncrement());
    public static final String BUSINESS_ADMIN_ID_LIST = String.valueOf(idGen.getAndIncrement());
    public static final String POTENTIAL_OWNER_ID_LIST = String.valueOf(idGen.getAndIncrement());
    public static final String STAKEHOLDER_ID_LIST = String.valueOf(idGen.getAndIncrement());
    public static final String ACTUAL_OWNER_ID_LIST = String.valueOf(idGen.getAndIncrement());
    public static final String EXCLUDED_OWNER_ID_LIST = String.valueOf(idGen.getAndIncrement());
    public static final String CREATED_BY_LIST = String.valueOf(idGen.getAndIncrement());
    public static final String TASK_STATUS_LIST = String.valueOf(idGen.getAndIncrement());
    public static final String LANGUAGE = String.valueOf(idGen.getAndIncrement());
    public static final String CREATED_ON_LIST = String.valueOf(idGen.getAndIncrement());
    public static final String TASK_NAME_LIST = String.valueOf(idGen.getAndIncrement());
    public static final String TASK_PARENT_ID_LIST = String.valueOf(idGen.getAndIncrement());
    public static final String TASK_ACTIVATION_TIME_LIST = String.valueOf(idGen.getAndIncrement());
    public static final String TASK_DESCRIPTION_LIST = String.valueOf(idGen.getAndIncrement());
    public static final String TASK_PRIORITY_LIST = String.valueOf(idGen.getAndIncrement());
    public static final String TASK_PROCESS_SESSION_ID_LIST = String.valueOf(idGen.getAndIncrement());
    public static final String TASK_DUE_DATE_LIST = String.valueOf(idGen.getAndIncrement());
    public static final String ARCHIVED = String.valueOf(idGen.getAndIncrement());
    public static final String EXPIRATION_TIME_LIST = String.valueOf(idGen.getAndIncrement());
    public static final String TASK_FORM_NAME_LIST = String.valueOf(idGen.getAndIncrement());
    public static final String SKIPPABLE = String.valueOf(idGen.getAndIncrement());
    public static final String TASK_SUBJECT_LIST = String.valueOf(idGen.getAndIncrement());
    public static final String SUB_TASKS_STRATEGY = String.valueOf(idGen.getAndIncrement());
    
    public static final String TASK_USER_ROLES_LIMIT_LIST = String.valueOf(idGen.getAndIncrement());
    
    // task audit
    public static final String TASK_EVENT_DATE_ID_LIST = String.valueOf(idGen.getAndIncrement());
    public static final String USER_ID_LIST = String.valueOf(idGen.getAndIncrement());
    public static final String TASK_EVENT_MESSAGE_LIST = String.valueOf(idGen.getAndIncrement());
    public static final String TASK_EVENT_LOG_TIME_LIST = String.valueOf(idGen.getAndIncrement());
    public static final String MESSAGE_LIST = String.valueOf(idGen.getAndIncrement());
    
    // executor identifiers
    public static final String EXECUTOR_STATUS_LIST = String.valueOf(idGen.getAndIncrement());
    public static final String EXECUTOR_TIME_LIST = String.valueOf(idGen.getAndIncrement());
    public static final String STACK_TRACE_LIST = String.valueOf(idGen.getAndIncrement());
    public static final String COMMAND_NAME_LIST = String.valueOf(idGen.getAndIncrement());
    public static final String EXECUTOR_EXECUTIONS_LIST = String.valueOf(idGen.getAndIncrement());
    public static final String EXECUTOR_KEY_LIST = String.valueOf(idGen.getAndIncrement());
    public static final String EXECUTOR_OWNER_LIST = String.valueOf(idGen.getAndIncrement());
    public static final String EXECUTOR_RETRIES_LIST = String.valueOf(idGen.getAndIncrement());

}