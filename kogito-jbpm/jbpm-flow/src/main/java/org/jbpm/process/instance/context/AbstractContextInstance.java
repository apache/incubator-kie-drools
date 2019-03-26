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

package org.jbpm.process.instance.context;

import java.io.Serializable;

import org.jbpm.process.core.Context;
import org.jbpm.process.instance.ContextInstance;
import org.jbpm.process.instance.ContextInstanceContainer;
import org.jbpm.process.instance.ProcessInstance;

public abstract class AbstractContextInstance implements ContextInstance, Serializable {

    private long contextId;
    private ContextInstanceContainer contextInstanceContainer;
    private ProcessInstance processInstance;
    
    public long getContextId() {
        return contextId;
    }

    public void setContextId(long contextId) {
        this.contextId = contextId;
    }

    public ContextInstanceContainer getContextInstanceContainer() {
        return contextInstanceContainer;
    }

    public void setContextInstanceContainer(ContextInstanceContainer contextInstanceContainer) {
        this.contextInstanceContainer = contextInstanceContainer;
    }
    
    public Context getContext() {
        return getContextInstanceContainer().getContextContainer().getContext(getContextType(), getContextId());
    }

	public ProcessInstance getProcessInstance() {
		return processInstance;
	}

	public void setProcessInstance(ProcessInstance processInstance) {
		this.processInstance = processInstance;
	}
    
}
