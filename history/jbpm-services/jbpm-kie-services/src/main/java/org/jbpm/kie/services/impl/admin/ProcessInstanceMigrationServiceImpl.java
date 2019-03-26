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

package org.jbpm.kie.services.impl.admin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.jbpm.runtime.manager.impl.migration.MigrationException;
import org.jbpm.runtime.manager.impl.migration.MigrationManager;
import org.jbpm.runtime.manager.impl.migration.MigrationSpec;
import org.jbpm.services.api.admin.MigrationEntry;
import org.jbpm.services.api.admin.MigrationReport;
import org.jbpm.services.api.admin.ProcessInstanceMigrationService;
import org.jbpm.services.api.service.ServiceRegistry;

/**
 * Default implementation of <code>org.jbpm.services.api.admin.ProcessInstanceMigrationService</code>
 * that delegates complete migration to <code>org.jbpm.runtime.manager.impl.migration.MigrationManager</code>
 *
 */
public class ProcessInstanceMigrationServiceImpl implements ProcessInstanceMigrationService {

    
    public ProcessInstanceMigrationServiceImpl() {
        ServiceRegistry.get().register(ProcessInstanceMigrationService.class.getSimpleName(), this);
    }
    
    @Override
    public MigrationReport migrate(String sourceDeploymentId, Long processInstanceId, String targetDeploymentId, String targetProcessId) {
        MigrationSpec migrationSpec = new MigrationSpec(sourceDeploymentId, processInstanceId, targetDeploymentId, targetProcessId);        
        MigrationManager migrationManager = new MigrationManager(migrationSpec);
        
        org.jbpm.runtime.manager.impl.migration.MigrationReport report = null;
        try {
            report = migrationManager.migrate();
        
        } catch (MigrationException e) {
            report = e.getReport();
        }
        return convert(report);
    }

    @Override
    public MigrationReport migrate(String sourceDeploymentId, Long processInstanceId, String targetDeploymentId, String targetProcessId, Map<String, String> nodeMapping) {
        MigrationSpec migrationSpec = new MigrationSpec(sourceDeploymentId, processInstanceId, targetDeploymentId, targetProcessId);        
        MigrationManager migrationManager = new MigrationManager(migrationSpec);
        
        org.jbpm.runtime.manager.impl.migration.MigrationReport report = null;
        try {
            report = migrationManager.migrate(nodeMapping);
        
        } catch (MigrationException e) {
            report = e.getReport();
        }
        
        return convert(report);
    }

    @Override
    public List<MigrationReport> migrate(String sourceDeploymentId, List<Long> processInstanceIds, String targetDeploymentId, String targetProcessId) {
        
        return migrate(sourceDeploymentId, processInstanceIds, targetDeploymentId, targetProcessId, Collections.emptyMap());
    }

    @Override
    public List<MigrationReport> migrate(String sourceDeploymentId, List<Long> processInstanceIds, String targetDeploymentId, String targetProcessId, Map<String, String> nodeMapping) {
        List<MigrationReport> reports = new ArrayList<MigrationReport>();
        
        for (Long pId : processInstanceIds) {
            MigrationReport report = migrate(sourceDeploymentId, pId, targetDeploymentId, targetProcessId, nodeMapping);  
            reports.add(report);
        }
        
        return reports;
    }
    
    /*
     * Helper methods
     */    

    protected MigrationReport convert(org.jbpm.runtime.manager.impl.migration.MigrationReport report) {
        List<MigrationEntry> logs = new ArrayList<MigrationEntry>();
        
        for (org.jbpm.runtime.manager.impl.migration.MigrationEntry orig : report.getEntries()) {
            logs.add(new MigrationEntryImpl(orig.getTimestamp(), orig.getMessage(), orig.getType().toString()));
        }
        return new MigrationReportImpl(report.getMigrationSpec().getProcessInstanceId(), report.isSuccessful(), report.getStartDate(), report.getEndDate(), logs);
    }


}
