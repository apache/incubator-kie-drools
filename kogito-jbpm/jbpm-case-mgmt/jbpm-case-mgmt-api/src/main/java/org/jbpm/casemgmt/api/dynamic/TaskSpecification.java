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

package org.jbpm.casemgmt.api.dynamic;

import java.util.Map;

/**
 * Provides all required information about a dynamic task so it can be represented as correct
 * node type when added to case/process instance.
 *
 */
public interface TaskSpecification {

    /**
     * Returns the type of the node that will be added. Usually it corresponds to the 
     * name used when registering work item handler. 
     * @return
     */
    String getNodeType();
    
    /**
     * Optional set of parameters to be given to created dynamic task.
     * @return
     */
    Map<String, Object> getParameters(); 
}
