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
package org.kie.kogito.process.handler;

import org.jbpm.workflow.instance.impl.WorkflowProcessInstanceImpl;
import org.kie.kogito.Model;
import org.kie.kogito.handler.ExceptionHandler;
import org.kie.kogito.process.MutableProcessInstances;
import org.kie.kogito.process.ProcessInstanceExecutionException;
import org.kie.kogito.process.Processes;
import org.kie.kogito.process.impl.AbstractProcessInstance;
import org.kie.kogito.services.uow.UnitOfWorkExecutor;
import org.kie.kogito.uow.UnitOfWorkManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
public class ExceptionHandlerTransaction implements ExceptionHandler {

    private static final Logger LOG = LoggerFactory.getLogger(ExceptionHandlerTransaction.class);

    @Autowired
    UnitOfWorkManager unitOfWorkManager;

    @Autowired(required = false)
    Processes processes;

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handle(Exception th) {
        if (processes == null) {
            return;
        }
        if (th instanceof ProcessInstanceExecutionException) {
            ProcessInstanceExecutionException processInstanceExecutionException = (ProcessInstanceExecutionException) th;
            LOG.info("handling exception {} by the handler {}", th, this.getClass().getName());
            UnitOfWorkExecutor.executeInUnitOfWork(unitOfWorkManager, () -> {
                String processInstanceId = processInstanceExecutionException.getProcessInstanceId();
                processes.processByProcessInstanceId(processInstanceId).ifPresent(processDefinition -> {
                    processDefinition.instances().findById(processInstanceId).ifPresent(instance -> {
                        AbstractProcessInstance<? extends Model> processInstance = ((AbstractProcessInstance<? extends Model>) instance);
                        ((WorkflowProcessInstanceImpl) processInstance.internalGetProcessInstance()).internalSetError(processInstanceExecutionException);
                        ((MutableProcessInstances) processDefinition.instances()).update(processInstanceId, processInstance);
                    });

                });

                return null;
            });
        }
    }

}
