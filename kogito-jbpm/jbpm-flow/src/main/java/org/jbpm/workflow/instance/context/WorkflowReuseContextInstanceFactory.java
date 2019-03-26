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

package org.jbpm.workflow.instance.context;

import org.jbpm.process.core.Context;
import org.jbpm.process.instance.ContextInstance;
import org.jbpm.process.instance.ContextInstanceContainer;
import org.jbpm.process.instance.ProcessInstance;
import org.jbpm.process.instance.context.AbstractContextInstance;
import org.jbpm.process.instance.impl.ContextInstanceFactory;
import org.jbpm.workflow.instance.NodeInstanceContainer;

public class WorkflowReuseContextInstanceFactory implements ContextInstanceFactory {
    
    public final Class<? extends ContextInstance> cls;
    
    public WorkflowReuseContextInstanceFactory(Class<? extends ContextInstance> cls){
        this.cls = cls;
    }

	public ContextInstance getContextInstance(Context context, ContextInstanceContainer contextInstanceContainer, ProcessInstance processInstance) {    	
        ContextInstance result = contextInstanceContainer.getContextInstance( context.getType(), context.getId() );
        if (result != null) {
            return result;
        }
        try {
            AbstractContextInstance contextInstance = (AbstractContextInstance) cls.newInstance();
            contextInstance.setContextId(context.getId());
            contextInstance.setContextInstanceContainer(contextInstanceContainer);
            contextInstance.setProcessInstance(processInstance);
            contextInstanceContainer.addContextInstance(context.getType(), contextInstance);
            NodeInstanceContainer nodeInstanceContainer = null;
            if (contextInstanceContainer instanceof NodeInstanceContainer) {
                nodeInstanceContainer = (NodeInstanceContainer) contextInstanceContainer;
            } else if (contextInstanceContainer instanceof ContextInstance) {
                ContextInstanceContainer parent = ((ContextInstance) contextInstanceContainer).getContextInstanceContainer();
                while (parent != null) {
                    if (parent instanceof NodeInstanceContainer) {
                        nodeInstanceContainer = (NodeInstanceContainer) parent;
                    } else if (contextInstanceContainer instanceof ContextInstance) {
                        parent = ((ContextInstance) contextInstanceContainer).getContextInstanceContainer();
                    } else {
                        parent = null;
                    }
                }
            }
            ((WorkflowContextInstance) contextInstance).setNodeInstanceContainer(nodeInstanceContainer);
            return contextInstance;
        } catch (Exception e) {
            throw new RuntimeException("Unable to instantiate context '"
                + this.cls.getName() + "': " + e.getMessage());
        }
	}

}
