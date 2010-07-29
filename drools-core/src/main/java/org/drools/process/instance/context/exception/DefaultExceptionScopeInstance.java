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

package org.drools.process.instance.context.exception;

import org.drools.WorkingMemory;
import org.drools.base.DefaultKnowledgeHelper;
import org.drools.base.SequentialKnowledgeHelper;
import org.drools.common.InternalRuleBase;
import org.drools.process.core.context.exception.ActionExceptionHandler;
import org.drools.process.core.context.exception.ExceptionHandler;
import org.drools.process.instance.ContextInstanceContainer;
import org.drools.process.instance.ProcessInstance;
import org.drools.spi.Action;
import org.drools.spi.KnowledgeHelper;
import org.drools.spi.ProcessContext;
import org.drools.workflow.instance.NodeInstance;

public class DefaultExceptionScopeInstance extends ExceptionScopeInstance {

	private static final long serialVersionUID = 510l;

	public void handleException(ExceptionHandler handler, String exception, Object params) {
		
		if (handler instanceof ActionExceptionHandler) {
			Action action = (Action) ((ActionExceptionHandler) handler).getAction().getMetaData("Action");
			try {
			    KnowledgeHelper knowledgeHelper = createKnowledgeHelper();
			    ProcessContext context = new ProcessContext();
		    	ProcessInstance processInstance = getProcessInstance();
			    ContextInstanceContainer contextInstanceContainer = getContextInstanceContainer();
			    if (contextInstanceContainer instanceof NodeInstance) {
			    	context.setNodeInstance((NodeInstance) contextInstanceContainer);
			    } else {
			    	context.setProcessInstance(processInstance);
			    }
			    String faultVariable = handler.getFaultVariable();
			    if (faultVariable != null) {
			    	context.setVariable(faultVariable, params);
			    }
		        action.execute(knowledgeHelper, ((ProcessInstance) processInstance).getWorkingMemory(), context);
			} catch (Exception e) {
			    throw new RuntimeException("unable to execute Action", e);
			}
		} else {
			throw new IllegalArgumentException("Unknown exception handler " + handler);
		}
	}

    private KnowledgeHelper createKnowledgeHelper() {
        WorkingMemory workingMemory = ((ProcessInstance) getProcessInstance()).getWorkingMemory();
        if ( ((InternalRuleBase) workingMemory.getRuleBase()).getConfiguration().isSequential() ) {
            return new SequentialKnowledgeHelper( workingMemory );
        } else {
            return new DefaultKnowledgeHelper( workingMemory );
        }
    }

}
