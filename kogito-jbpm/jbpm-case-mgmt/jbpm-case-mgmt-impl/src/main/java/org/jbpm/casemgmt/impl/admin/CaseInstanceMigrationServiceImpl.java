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

package org.jbpm.casemgmt.impl.admin;

import static java.util.stream.Collectors.toMap;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.jbpm.casemgmt.api.CaseNotFoundException;
import org.jbpm.casemgmt.api.CaseRuntimeDataService;
import org.jbpm.casemgmt.api.admin.CaseInstanceMigrationService;
import org.jbpm.casemgmt.api.admin.CaseMigrationReport;
import org.jbpm.casemgmt.impl.command.UpdateCaseFileInstanceCommand;
import org.jbpm.services.api.ProcessService;
import org.jbpm.services.api.admin.MigrationReport;
import org.jbpm.services.api.admin.ProcessInstanceMigrationService;
import org.jbpm.services.api.model.ProcessInstanceDesc;
import org.jbpm.shared.services.impl.TransactionalCommandService;
import org.jbpm.shared.services.impl.commands.CompositeCommand;
import org.jbpm.shared.services.impl.commands.UpdateStringCommand;
import org.kie.api.runtime.query.QueryContext;
import org.kie.internal.runtime.manager.context.ProcessInstanceIdContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class CaseInstanceMigrationServiceImpl implements CaseInstanceMigrationService {

    private static final Logger logger = LoggerFactory.getLogger(CaseInstanceMigrationServiceImpl.class);
    
    private ProcessService processService;
    private CaseRuntimeDataService caseRuntimeDataService;    
    private ProcessInstanceMigrationService processInstanceMigrationService;
    private TransactionalCommandService commandService;  
    
    public void setProcessService(ProcessService processService) {
        this.processService = processService;
    }

    public void setCaseRuntimeDataService(CaseRuntimeDataService caseRuntimeDataService) {
        this.caseRuntimeDataService = caseRuntimeDataService;
    }
    
    public void setProcessInstanceMigrationService(ProcessInstanceMigrationService processInstanceMigrationService) {
        this.processInstanceMigrationService = processInstanceMigrationService;
    }
    
    public void setCommandService(TransactionalCommandService commandService) {
        this.commandService = commandService;
    }

    @Override
    public CaseMigrationReport migrate(String caseId, String targetDeploymentId, Map<String, String> processMapping) {
        return migrate(caseId, targetDeploymentId, processMapping, Collections.emptyMap());
    }

    @Override
    public CaseMigrationReport migrate(String caseId, String targetDeploymentId, Map<String, String> processMapping, Map<String, String> nodeMapping) {
        Collection<ProcessInstanceDesc> caseProcesses = caseRuntimeDataService.getProcessInstancesForCase(caseId, new QueryContext("Id", true));
        
        if (caseProcesses.isEmpty()) {
            throw new CaseNotFoundException("Case " + caseId + " does not exist or is not active, cannot be migrated");
        }
        
        // validate that all active process instances have mapped process id
        if (caseProcesses.stream().anyMatch(pi -> !processMapping.containsKey(pi.getProcessId()))) {
            throw new RuntimeException("Not possible to migrate case instance " + caseId+ " due to missing process mapping");
        }
        logger.debug("About to migrate {} process instances that are active for case instance {}", caseProcesses, caseId);           
        
        CaseMigrationReport report = new CaseMigrationReport();
        ProcessInstanceDesc caseProcessInstance = null;
        String caseDefinitionId = null;
        for (ProcessInstanceDesc processInstance : caseProcesses) {
            String targetProcessId = processMapping.get(processInstance.getProcessId());
            if (processInstance.getCorrelationKey().equals(caseId)) {
                caseProcessInstance = processInstance;
                caseDefinitionId = targetProcessId;
            }
            
            logger.debug("Migrating process instance {} to {} in deployment {}", processInstance.getId(), targetProcessId, targetDeploymentId);
            MigrationReport individualReport = processInstanceMigrationService.migrate(processInstance.getDeploymentId(),
                                                                                       processInstance.getId(), 
                                                                                       targetDeploymentId, 
                                                                                       targetProcessId,
                                                                                       nodeMapping);            
            report.addReport(individualReport);
            
            if (!individualReport.isSuccessful()) {
                logger.debug("Process instance {} failed during migration, aborting (with compensation)", processInstance.getId());
                break;
            }
            logger.debug("Process instance {} was migrated with status {} as part of case instance {} migration", processInstance.getId(), individualReport.isSuccessful(), caseId);
        }
        report.complete();
        if (report.isSuccessful()) {
            Map<String, Object> parameters = new HashMap<>();            
            parameters.put("caseId", caseId);
            parameters.put("owner", targetDeploymentId);
            UpdateStringCommand updateCommand = new UpdateStringCommand("update ContextMappingInfo set ownerId = :owner where contextId = :caseId", parameters);
            
            Map<String, Object> parametersLog = new HashMap<>();            
            parametersLog.put("caseId", caseId);
            parametersLog.put("caseDefId", caseDefinitionId);
            UpdateStringCommand updateCommandLog = new UpdateStringCommand("update CaseFileDataLog set caseDefId = :caseDefId where caseId = :caseId", parametersLog);
            commandService.execute(new CompositeCommand(updateCommand, updateCommandLog));
            
            processService.execute(targetDeploymentId, ProcessInstanceIdContext.get(caseProcessInstance.getId()), new UpdateCaseFileInstanceCommand(caseDefinitionId));
            
            logger.info("Migration of case instance {} completed successfully - number of migrated process instances {}", caseId, report.getReports().size());
        } else {
            // requires compensation, meaning all successfully migrated instances must be migrated back to previous version
            logger.info("Migration of case instance {} failed due to some instances were not migrated, reverting to previous version", caseId);                 
            Map<String, String> inversedNodeMapping = 
                    nodeMapping.entrySet()
                       .stream()
                       .collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));
            
            Map<Long, ProcessInstanceDesc> instances = mapProcessInstances(caseProcesses);
            final AtomicInteger revertCounter = new AtomicInteger(0);
            report.getReports().forEach(r -> {
                if (r.isSuccessful()) {
                    ProcessInstanceDesc source = instances.get(r.getProcessInstanceId());// get the information about given process instance before migration and use it to revert
                    MigrationReport individualReportRevert = processInstanceMigrationService.migrate(targetDeploymentId,
                                                                                               r.getProcessInstanceId(), 
                                                                                               source.getDeploymentId(), 
                                                                                               source.getProcessId(),
                                                                                               inversedNodeMapping);                    
                    if (individualReportRevert.isSuccessful()) {
                        revertCounter.incrementAndGet();
                        logger.info("Reverted process instance migration for {} successfully completed", individualReportRevert.getProcessInstanceId());
                    } else {
                        logger.info("Reverted process instance migration for {} failed", individualReportRevert.getProcessInstanceId());
                    }
                }
            });
            logger.info("Revert of migration of case instance {} completed with reverted processes {}", caseId, revertCounter.get());
            
        }
        
        return report;
    }
    
    protected Map<Long, ProcessInstanceDesc> mapProcessInstances(Collection<ProcessInstanceDesc> processes) {
        return processes.stream().collect(toMap(ProcessInstanceDesc::getId, p -> p));
    }

}
