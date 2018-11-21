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

package org.jbpm.casemgmt.impl.audit;

import static org.jbpm.casemgmt.impl.audit.CaseInstanceAuditConstants.DELETE_CASE_DATA_BY_NAME_QUERY;
import static org.jbpm.casemgmt.impl.audit.CaseInstanceAuditConstants.DELETE_CASE_ROLE_ASSIGNMENT_QUERY;
import static org.jbpm.casemgmt.impl.audit.CaseInstanceAuditConstants.FIND_CASE_DATA_BY_NAME_QUERY;
import static org.jbpm.casemgmt.impl.audit.CaseInstanceAuditConstants.FIND_CASE_DATA_QUERY;
import static org.jbpm.casemgmt.impl.audit.CaseInstanceAuditConstants.FIND_CASE_PROCESS_INST_ID_QUERY;
import static org.jbpm.casemgmt.impl.audit.CaseInstanceAuditConstants.UPDATE_CASE_PROCESS_INST_ID_QUERY;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jbpm.casemgmt.api.audit.CaseFileData;
import org.jbpm.casemgmt.api.auth.AuthorizationManager;
import org.jbpm.casemgmt.api.event.CaseDataEvent;
import org.jbpm.casemgmt.api.event.CaseEvent;
import org.jbpm.casemgmt.api.event.CaseEventListener;
import org.jbpm.casemgmt.api.event.CaseReopenEvent;
import org.jbpm.casemgmt.api.event.CaseRoleAssignmentEvent;
import org.jbpm.casemgmt.api.event.CaseStartEvent;
import org.jbpm.casemgmt.api.model.instance.CaseFileInstance;
import org.jbpm.casemgmt.api.model.instance.CaseRoleInstance;
import org.jbpm.casemgmt.impl.model.instance.CaseFileInstanceImpl;
import org.jbpm.shared.services.impl.TransactionalCommandService;
import org.jbpm.shared.services.impl.commands.MergeObjectCommand;
import org.jbpm.shared.services.impl.commands.PersistObjectCommand;
import org.jbpm.shared.services.impl.commands.QueryStringCommand;
import org.jbpm.shared.services.impl.commands.UpdateStringCommand;
import org.kie.internal.runtime.Cacheable;
import org.kie.internal.task.api.TaskModelProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class CaseInstanceAuditEventListener implements CaseEventListener, Cacheable {

    private static final Logger logger = LoggerFactory.getLogger(CaseInstanceAuditEventListener.class);
    
    private TransactionalCommandService commandService;
    
    private CaseIndexerManager indexManager = CaseIndexerManager.get();
    
    public CaseInstanceAuditEventListener(TransactionalCommandService commandService) {
        this.commandService = commandService;
    }

    @Override
    public void afterCaseStarted(CaseStartEvent event) {
        
        CaseFileInstance caseFile = event.getCaseFile();
        if (caseFile == null) {
            return;
        }
        
        Collection<CaseRoleInstance> caseRoleAssignments = ((CaseFileInstanceImpl)caseFile).getAssignments();
        if (caseRoleAssignments != null && !caseRoleAssignments.isEmpty()) {
            for (CaseRoleInstance roleAssignment : caseRoleAssignments) {
                logger.debug("Role {} has following assignments {}", roleAssignment.getRoleName(), roleAssignment.getRoleAssignments());
                
                if (roleAssignment.getRoleAssignments() != null && !roleAssignment.getRoleAssignments().isEmpty()) {
                    List<CaseRoleAssignmentLog> objects = new ArrayList<>();
                    roleAssignment.getRoleAssignments().forEach(entity -> {
                        CaseRoleAssignmentLog assignmentLog = new CaseRoleAssignmentLog(event.getProcessInstanceId(), event.getCaseId(), roleAssignment.getRoleName(), entity);
                        
                        objects.add(assignmentLog);
                    });
                    
                    commandService.execute(new PersistObjectCommand(objects.toArray()));
                }
            }
        } else {
            // add public role so it can be found by queries that take assignments into consideration
            CaseRoleAssignmentLog assignmentLog = new CaseRoleAssignmentLog(event.getProcessInstanceId(), event.getCaseId(), "*", TaskModelProvider.getFactory().newGroup(AuthorizationManager.PUBLIC_GROUP));
            commandService.execute(new PersistObjectCommand(assignmentLog));
        }
        
        Map<String, Object> initialData = caseFile.getData();
        if (initialData.isEmpty()) {
            return;
        }
        List<CaseFileData> insert = new ArrayList<>();
        initialData.forEach((name, value) -> {
            
            if (value != null) {
                
                List<CaseFileData> indexedValues = indexManager.index(event, name, value);               
                insert.addAll(indexedValues);
                
            }
        });
        commandService.execute(new PersistObjectCommand(insert.toArray()));

    }
    
    @Override
    public void afterCaseReopen(CaseReopenEvent event) {
        logger.debug("Updating process instance id ({})in case assignment log for case id {}", event.getProcessInstanceId(), event.getCaseId());
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("piID", event.getProcessInstanceId());
        parameters.put("caseId", event.getCaseId());
        UpdateStringCommand updateCommand = new UpdateStringCommand(UPDATE_CASE_PROCESS_INST_ID_QUERY, parameters);
        int updated = commandService.execute(updateCommand);
        logger.debug("Updated {} role assignment entries for case id {}", updated, event.getCaseId());
        
        updateCaseFileItems(event, event.getData(), event.getCaseId(), event.getCaseDefinitionId(), event.getUser());
    }    

    @Override
    public void afterCaseRoleAssignmentAdded(CaseRoleAssignmentEvent event) {
        
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("caseId", event.getCaseId());
        
        QueryStringCommand<List<Long>> queryCommand = new QueryStringCommand<List<Long>>(FIND_CASE_PROCESS_INST_ID_QUERY, parameters);
        List<Long> processInstanceId = commandService.execute(queryCommand);
        if (processInstanceId.isEmpty()) {
            return;
        }
        CaseRoleAssignmentLog assignmentLog = new CaseRoleAssignmentLog(processInstanceId.get(0), event.getCaseId(), event.getRole(), event.getEntity());
        commandService.execute(new PersistObjectCommand(assignmentLog));

    }

    @Override
    public void afterCaseRoleAssignmentRemoved(CaseRoleAssignmentEvent event) {
        
        
        Map<String, Object> parameters = new HashMap<>();
        
        parameters.put("caseId", event.getCaseId());
        parameters.put("role", event.getRole());
        parameters.put("entity", event.getEntity().getId());
        UpdateStringCommand updateCommand = new UpdateStringCommand(DELETE_CASE_ROLE_ASSIGNMENT_QUERY, parameters);
        commandService.execute(updateCommand);
        logger.debug("Removed {} role assignment for entity {} for case id {}", event.getRole(), event.getEntity(), event.getCaseId());
    }   

    @Override
    public void afterCaseDataAdded(CaseDataEvent event) {
        updateCaseFileItems(event, event.getData(), event.getCaseId(), event.getDefinitionId(), event.getUser());
    }

    @Override
    public void afterCaseDataRemoved(CaseDataEvent event) {
        Map<String, Object> parameters = new HashMap<>();
        
        parameters.put("caseId", event.getCaseId());
        parameters.put("itemNames", new ArrayList<>(event.getData().keySet()));
        UpdateStringCommand updateCommand = new UpdateStringCommand(DELETE_CASE_DATA_BY_NAME_QUERY, parameters);
        commandService.execute(updateCommand);
    }

    @Override
    public void close() {
        // no-op
    }

    
    /*
     * Helper methods
     */
    
    protected List<String> currentCaseData(String caseId) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("caseId", caseId);
        
        QueryStringCommand<List<String>> queryCommand = new QueryStringCommand<List<String>>(FIND_CASE_DATA_QUERY, parameters);
        List<String> caseDataLog = commandService.execute(queryCommand);
        
        return caseDataLog;
    }
    
    protected CaseFileDataLog caseFileDataByName(String caseId, String name) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("caseId", caseId);
        parameters.put("itemName", name);
        
        QueryStringCommand<List<CaseFileDataLog>> queryCommand = new QueryStringCommand<List<CaseFileDataLog>>(FIND_CASE_DATA_BY_NAME_QUERY, parameters);
        List<CaseFileDataLog> caseDataLog = commandService.execute(queryCommand);
        
        return caseDataLog.get(0);
    }
    
    protected void updateCaseFileItems(CaseEvent event, Map<String, Object> addedData, String caseId, String caseDefinitionId, String user) {
        
        if (addedData.isEmpty()) {
            return;
        }
        List<CaseFileDataLog> insert = new ArrayList<>();
        List<CaseFileDataLog> update = new ArrayList<>();
        List<String> currentCaseData = currentCaseData(caseId);
        addedData.forEach((name, value) -> {
            
            if (value != null) {
                List<CaseFileData> indexedValues = indexManager.index(event, name, value);
                
                for (CaseFileData item : indexedValues) {
                
                    CaseFileDataLog caseFileDataLog = null;
                    if (currentCaseData.contains(item.getItemName())) {
                        logger.debug("Case instance {} has already stored log value for {} thus it's going to be updated", caseId, item.getItemName());
                        caseFileDataLog = caseFileDataByName(caseId, item.getItemName());
                        update.add(caseFileDataLog);
                    } else {
                        logger.debug("Case instance {} has no log value for {} thus it's going to be inserted", caseId, item.getItemName());                
                        caseFileDataLog = new CaseFileDataLog(caseId, caseDefinitionId, item.getItemName());                
                        insert.add(caseFileDataLog);
                    }
                    caseFileDataLog.setItemType(item.getItemType());
                    caseFileDataLog.setItemValue(item.getItemValue());
                    caseFileDataLog.setLastModified(item.getLastModified());
                    caseFileDataLog.setLastModifiedBy(item.getLastModifiedBy());
                }                
            }
        });
        commandService.execute(new PersistObjectCommand(insert.toArray()));
        commandService.execute(new MergeObjectCommand(update.toArray()));
    }
    
    
}
