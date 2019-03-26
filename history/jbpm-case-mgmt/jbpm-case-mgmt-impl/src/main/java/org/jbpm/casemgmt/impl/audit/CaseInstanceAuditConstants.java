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


public class CaseInstanceAuditConstants {


    public static final int AFTER_CASE_STARTED_EVENT_TYPE = 0;
    public static final int AFTER_CASE_REOPEN_EVENT_TYPE = 1;
    public static final int AFTER_CASE_ROLE_ASSIGNMENT_ADDED_EVENT_TYPE = 2;
    public static final int AFTER_CASE_ROLE_ASSIGNMENT_REMOVED_EVENT_TYPE = 3;
    public static final int AFTER_CASE_DATA_ADDED_EVENT_TYPE = 4;
    public static final int AFTER_CASE_DATA_REMOVED_EVENT_TYPE = 5;
    
    public static final String UPDATE_CASE_PROCESS_INST_ID_QUERY = "update CaseRoleAssignmentLog set processInstanceId =:piID where caseId =:caseId";
    public static final String DELETE_CASE_ROLE_ASSIGNMENT_QUERY = "delete from CaseRoleAssignmentLog where caseId =:caseId and roleName =:role and entityId =:entity";
    public static final String FIND_CASE_PROCESS_INST_ID_QUERY = "select processInstanceId from ProcessInstanceLog where correlationKey =:caseId";
    
    public static final String FIND_CASE_DATA_QUERY = "select itemName from CaseFileDataLog where caseId =:caseId";
    public static final String FIND_CASE_DATA_BY_NAME_QUERY = "select cfdl from CaseFileDataLog cfdl where cfdl.caseId =:caseId and cfdl.itemName =:itemName";
    public static final String DELETE_CASE_DATA_BY_NAME_QUERY = "delete from CaseFileDataLog where caseId =:caseId and itemName in (:itemNames)";
}
