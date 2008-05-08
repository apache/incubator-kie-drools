package org.drools.workflow.instance.node;

/*
 * Copyright 2005 JBoss Inc
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

import org.drools.process.core.context.exception.ExceptionScope;
import org.drools.process.instance.ProcessInstance;
import org.drools.process.instance.context.exception.ExceptionScopeInstance;
import org.drools.workflow.core.Node;
import org.drools.workflow.core.node.FaultNode;
import org.drools.workflow.instance.NodeInstance;
import org.drools.workflow.instance.impl.NodeInstanceImpl;

/**
 * Runtime counterpart of a fault node.
 * 
 * @author <a href="mailto:kris_verlaenen@hotmail.com">Kris Verlaenen</a>
 */
public class FaultNodeInstance extends NodeInstanceImpl {

    private static final long serialVersionUID = 400L;
    
    protected FaultNode getFaultNode() {
        return (FaultNode) getNode();
    }
    
    public void internalTrigger(final NodeInstance from, String type) {
        if (!Node.CONNECTION_DEFAULT_TYPE.equals(type)) {
            throw new IllegalArgumentException(
                "A FaultNode only accepts default incoming connections!");
        }
        FaultNode faultNode = getFaultNode();
        ExceptionScopeInstance exceptionScopeInstance = (ExceptionScopeInstance)
            resolveContextInstance(ExceptionScope.EXCEPTION_SCOPE, faultNode.getFaultName());
        if (exceptionScopeInstance != null) {
            exceptionScopeInstance.handleException(faultNode.getFaultName(), null);
        } else {
        	getProcessInstance().setState(ProcessInstance.STATE_ABORTED);
        }
    }

}