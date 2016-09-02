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

package org.jbpm.casemgmt.api.model;

import java.util.Collection;

/**
 * Describes case structure and requirements.
 *
 */
public interface CaseDefinition {
    
    public static final String DEFAULT_PREFIX = "CASE";

    /**
     * Returns id of the case that shall be used to instantiate new instance of this case
     * @return
     */
    String getId();
    
    /**
     * Returns version of this case
     * @return
     */
    String getVersion();
    
    /**
     * Returns case name of this case.
     * @return
     */
    String getName();
    
    /**
     * Returns case identifier prefix to be used for every instance of this case.
     * @return
     */
    String getIdentifierPrefix();

    /**
     * Returns deployment id
     */
    String getDeploymentId();
    
    /**
     * Returns available case stages in this case.
     * @return
     */
    Collection<CaseStage> getCaseStages();
    
    /**
     * Returns available case milestones for this case.
     * @return
     */
    Collection<CaseMilestone> getCaseMilestones();
    
    /**
     * Returns case roles for this case.
     * @return
     */
    Collection<CaseRole> getCaseRoles();
    
    /**
     * Returns available adhoc fragments that can be signaled.
     * @return
     */
    Collection<AdHocFragment> getAdHocFragments();
}
