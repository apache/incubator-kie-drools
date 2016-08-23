/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
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
package org.kie.internal.runtime.manager.context;

import org.kie.api.runtime.manager.Context;

/**
 * Process instance id aware implementation of the <code>Context</code> interface.
 * It's main responsibility is to be the data holder between caller and runtime manager
 * to obtain proper instances of <code>RuntimeEngine</code>.<br>
 * Used by strategy:
 * <ul>
 *  <li>PerProcessInstance</li>
 * </ul>
 * To obtain instances of this context use one of the following static methods:
 * <ul>
 *  <li><code>get()</code> to get empty context when starting process instances</li>
 *  <li><code>get(Long)</code> to get context for specific process instance</li>
 * </ul>
 */
public class ProcessInstanceIdContext implements Context<Long> {

    private Long processInstanceId;

    public ProcessInstanceIdContext(Long processInstanceId) {
        this.processInstanceId = processInstanceId;
    }

    @Override
    public Long getContextId() {
        return processInstanceId;
    }

    public void setContextId(long id) {
        this.processInstanceId = id;
    }

    /**
     * Returns new instance of <code>ProcessInstanceIdContext</code> without process instance id.
     * Used for starting new instances of the process.
     * @return
     */
    public static ProcessInstanceIdContext get() {
        return new ProcessInstanceIdContext(null);
    }

    /**
     * Returns new instance of <code>ProcessInstanceIdContext</code> with id of already existing process instance
     * @param processInstanceId actual identifier of process instance
     * @return
     */
    public static ProcessInstanceIdContext get(Long processInstanceId) {
        return new ProcessInstanceIdContext(processInstanceId);
    }

}
