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

package org.jbpm.casemgmt.api.model;

import java.util.Collection;

/**
 * Represents self contained part of case that does include
 * number of other activities and potentially other stages
 *
 */
public interface CaseStage {

    /**
     * Returns id of the stage
     * @return
     */
    String getId();
    
    /**
     * Returns name of the stage
     * @return
     */
    String getName();
    
    /**
     * Returns names of the adhoc fragments within this stage that can be triggered.
     * @return
     */
    Collection<AdHocFragment> getAdHocFragments();
}
