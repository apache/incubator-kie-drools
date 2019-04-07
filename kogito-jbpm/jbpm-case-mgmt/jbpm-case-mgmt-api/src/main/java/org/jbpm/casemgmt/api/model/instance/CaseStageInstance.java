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

import org.jbpm.casemgmt.api.model.AdHocFragment;
import org.jbpm.services.api.model.NodeInstanceDesc;

/**
 * Represents stage within case on runtime, provides information about 
 * active tasks, subprocesses etc.
 *
 */
public interface CaseStageInstance {

    /**
     * Returns id of this stage.
     */
    String getId();
    
    /**
     * Returns name of this stage.
     */
    String getName();
    
    /**
     * Returns names of the adhoc fragments within this stage that can be triggered.
     */
    Collection<AdHocFragment> getAdHocFragments();
    
    /**
     * Returns currently active nodes within this stage
     */
    Collection<NodeInstanceDesc> getActiveNodes();
    
    /**
     * Returns status of this stage
     */
    StageStatus getStatus();
}
