/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jbpm.process.instance.context.exception;

import org.jbpm.process.core.context.exception.ActionExceptionHandler;
import org.jbpm.process.core.context.exception.ExceptionHandler;
import org.jbpm.process.core.context.exception.ExceptionScope;
import org.jbpm.process.instance.ContextInstanceContainer;
import org.jbpm.process.instance.KogitoProcessContextImpl;
import org.jbpm.process.instance.ProcessInstance;
import org.jbpm.process.instance.impl.Action;
import org.jbpm.workflow.instance.NodeInstance;
import org.kie.kogito.internal.process.runtime.KogitoProcessContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultExceptionScopeInstance extends ExceptionScopeInstance {
    private static Logger LOG = LoggerFactory.getLogger(DefaultExceptionScopeInstance.class);
    private static final long serialVersionUID = 510l;

    @Override
    public String getContextType() {
        return ExceptionScope.EXCEPTION_SCOPE;
    }

    public void handleException(ExceptionHandler handler, String exception, KogitoProcessContext params) {
        LOG.debug("exception handler {}", exception);
        if (handler instanceof ActionExceptionHandler) {
            ActionExceptionHandler exceptionHandler = (ActionExceptionHandler) handler;
            Action action = (Action) exceptionHandler.getAction().getMetaData("Action");
            try {
                ProcessInstance processInstance = getProcessInstance();
                KogitoProcessContextImpl processContext = new KogitoProcessContextImpl(processInstance.getKnowledgeRuntime());
                ContextInstanceContainer contextInstanceContainer = getContextInstanceContainer();
                if (contextInstanceContainer instanceof NodeInstance) {
                    processContext.setNodeInstance((NodeInstance) contextInstanceContainer);
                } else {
                    processContext.setNodeInstance((params).getNodeInstance());
                }
                processContext.setProcessInstance(processInstance);
                String faultVariable = exceptionHandler.getFaultVariable();
                if (faultVariable != null) {
                    processContext.setVariable(faultVariable, (params).getContextData().get("Exception"));
                }

                action.execute(processContext);
            } catch (Exception e) {
                throw new RuntimeException("unable to execute Action", e);
            }
        } else {
            throw new IllegalArgumentException("Unknown exception handler " + handler);
        }
    }

}
