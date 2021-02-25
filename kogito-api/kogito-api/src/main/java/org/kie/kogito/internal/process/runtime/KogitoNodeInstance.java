/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.kogito.internal.process.runtime;

import java.util.Date;

import org.kie.api.runtime.process.NodeInstance;

public interface KogitoNodeInstance extends NodeInstance {

    /**
     * The id of the node instance. This is unique within the
     * node instance container this node instance lives in.
     *
     * @return the id of the node instance
     */
    String getStringId();

    /**
     * The id of the node definition this node instance refers to. The node
     * represents the definition that this node instance was based
     * on.
     *
     * @return the definition id of the node this node instance refers to
     */
    String getNodeDefinitionId();

    /**
     * Returns the time when this node instance was triggered
     * 
     * @return actual trigger time
     */
    Date getTriggerTime();

    /**
     * Returns the time when this node instance was left, might be null if node instance is still active
     * 
     * @return actual leave time
     */
    Date getLeaveTime();

    default KogitoProcessInstance getKogitoProcessInstance() {
        return (KogitoProcessInstance) getProcessInstance();
    }
}
