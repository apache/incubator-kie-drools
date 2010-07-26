/**
 * Copyright 2010 JBoss Inc
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

package org.drools.process.instance.context;

import java.io.Serializable;

import org.drools.process.core.Context;
import org.drools.process.instance.ContextInstance;
import org.drools.process.instance.ContextInstanceContainer;
import org.drools.process.instance.ProcessInstance;

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
