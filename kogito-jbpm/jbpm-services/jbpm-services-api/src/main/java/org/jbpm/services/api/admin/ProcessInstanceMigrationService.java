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

package org.jbpm.services.api.admin;

import java.util.List;
import java.util.Map;

/**
 * Provides migration service for process instances. Migration usually is
 * required when new version of the process definition is deployed and active 
 * instances should be moved to it instead of staying at the current one.
 *
 */
public interface ProcessInstanceMigrationService {

    /**
     * Migrates given process instance that belongs to source deployment, into target process id that belongs to target deployment.
     * Following rules are enforced:
     * <ul>
     *  <li>source deployment id must be there</li>
     *  <li>process instance id must point to existing and active process instance</li>
     *  <li>target deployment must exist</li>
     *  <li>target process id must exist in target deployment</li>
     * </ul>
     * Migration returns migration report regardless of migration being successful or not that needs to be examined for migration outcome.
     * @param sourceDeploymentId deployment that process instance to be migrated belongs to
     * @param processInstanceId id of the process instance to be migrated
     * @param targetDeploymentId id of deployment that target process belongs to
     * @param targetProcessId id of the process process instance should be migrated to
     * @return returns complete migration report
     */
    MigrationReport migrate(String sourceDeploymentId, Long processInstanceId, String targetDeploymentId, String targetProcessId);
    
    /**
     * Migrates given process instance (with node mapping) that belongs to source deployment, into target process id that belongs to target deployment.
     * Following rules are enforced:
     * <ul>
     *  <li>source deployment id must be there</li>
     *  <li>process instance id must point to existing and active process instance</li>
     *  <li>target deployment must exist</li>
     *  <li>target process id must exist in target deployment</li>
     * </ul>
     * Migration returns migration report regardless of migration being successful or not that needs to be examined for migration outcome.
     * @param sourceDeploymentId deployment that process instance to be migrated belongs to
     * @param processInstanceId id of the process instance to be migrated
     * @param targetDeploymentId id of deployment that target process belongs to
     * @param targetProcessId id of the process process instance should be migrated to
     * @param nodeMapping node mapping - source and target unique ids of nodes to be mapped - from process instance active nodes to new process nodes
     * @return returns complete migration report
     */
    MigrationReport migrate(String sourceDeploymentId, Long processInstanceId, String targetDeploymentId, String targetProcessId, Map<String, String> nodeMapping);
    
    /**
     * Migrates given process instances that belong to source deployment, into target process id that belongs to target deployment.
     * Following rules are enforced:
     * <ul>
     *  <li>source deployment id must be there</li>
     *  <li>process instance id must point to existing and active process instance</li>
     *  <li>target deployment must exist</li>
     *  <li>target process id must exist in target deployment</li>
     * </ul>
     * Migration returns list of migration report - one per process instance, regardless of migration being successful or not that needs to be examined for migration outcome.
     * @param sourceDeploymentId deployment that process instance to be migrated belongs to
     * @param processInstanceIds list of process instance id to be migrated
     * @param targetDeploymentId id of deployment that target process belongs to
     * @param targetProcessId id of the process process instance should be migrated to
     * @return returns complete migration report
     */
    List<MigrationReport> migrate(String sourceDeploymentId, List<Long> processInstanceIds, String targetDeploymentId, String targetProcessId);
    
    /**
     * Migrates given process instances (with node mapping) that belong to source deployment, into target process id that belongs to target deployment.
     * Following rules are enforced:
     * <ul>
     *  <li>source deployment id must be there</li>
     *  <li>process instance id must point to existing and active process instance</li>
     *  <li>target deployment must exist</li>
     *  <li>target process id must exist in target deployment</li>
     * </ul>
     * Migration returns list of migration report - one per process instance, regardless of migration being successful or not that needs to be examined for migration outcome.
     * @param sourceDeploymentId deployment that process instance to be migrated belongs to
     * @param processInstanceIds list of process instance id to be migrated
     * @param targetDeploymentId id of deployment that target process belongs to
     * @param targetProcessId id of the process process instance should be migrated to
     * @param nodeMapping node mapping - source and target unique ids of nodes to be mapped - from process instance active nodes to new process nodes
     * @return returns list of migration reports one per each process instance
     */
    List<MigrationReport> migrate(String sourceDeploymentId, List<Long> processInstanceIds, String targetDeploymentId, String targetProcessId, Map<String, String> nodeMapping);
    
}
