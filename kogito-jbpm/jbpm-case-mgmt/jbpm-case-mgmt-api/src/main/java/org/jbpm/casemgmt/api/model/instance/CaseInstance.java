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

package org.jbpm.casemgmt.api.model.instance;

import java.util.Collection;
import java.util.Date;

import org.jbpm.casemgmt.api.CaseService;

/**
 * Describes case structure and requirements.
 *
 */
public interface CaseInstance {

    /**
     * Returns case identifier of this case.
     */
    String getCaseId();
    
    /**
     * Returns case description
     */
    String getCaseDescription();
    
    /**
     * Returns owner of the case - usually one who started the case
     */
    String getOwner();
    
    /**
     * Returns available case stages in this case.
     */
    Collection<CaseStageInstance> getCaseStages();
    
    /**
     * Returns available case milestones for this case.
     */
    Collection<CaseMilestoneInstance> getCaseMilestones();
    
    /**
     * Returns case roles for this case.
     */
    Collection<CaseRoleInstance> getCaseRoles();
    
    /**
     * Returns case file associated with this case.
     * <p>
     *     Note: {@link CaseInstance#getCaseFile()} will always be empty.
     *     Refer to using {@link CaseService#getCaseFileInstance(String)} when access to case file information is needed.
     *  </p>
     */
    CaseFileInstance getCaseFile();
    
    /**
     * Returns status of the case
     */
    Integer getStatus();
    
    /**
     * Returns case definition id
     */
    String getCaseDefinitionId();

    /**
     * Returns deployment id
     */
    String getDeploymentId();
    
    /**
     * Returns start date of this case
     */
    Date getStartedAt();
    
    /**
     * Returns completion date of this case
     */
    Date getCompletedAt();
    
    /**
     * Returns completion message (if any) of this case
     */
    String getCompletionMessage();    
    
    /**
     * Returns SLA due date if any is set on case instance
     */
    Date getSlaDueDate();
    
    /**
     * Returns up to date SLA compliance level for case instance
     */
    Integer getSlaCompliance();
}
