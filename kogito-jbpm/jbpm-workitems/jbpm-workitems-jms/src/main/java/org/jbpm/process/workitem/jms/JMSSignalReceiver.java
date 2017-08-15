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

package org.jbpm.process.workitem.jms;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;

import javax.jms.BytesMessage;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;

import org.apache.commons.io.input.ClassLoaderObjectInputStream;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.internal.runtime.manager.InternalRuntimeManager;
import org.kie.internal.runtime.manager.RuntimeManagerIdFilter;
import org.kie.internal.runtime.manager.RuntimeManagerRegistry;
import org.kie.internal.runtime.manager.context.ProcessInstanceIdContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class JMSSignalReceiver implements MessageListener {

    private static final Logger logger = LoggerFactory.getLogger(JMSSignalReceiver.class);

    private static final ServiceLoader<RuntimeManagerIdFilter> runtimeManagerIdFilters = ServiceLoader.load(RuntimeManagerIdFilter.class);

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public void onMessage(Message message) {
        if (message instanceof BytesMessage) {

            String deploymentId;
            Long processInstanceId;
            String signal;
            Long workItemId;

            Object data;

            BytesMessage bytesMessage = (BytesMessage) message;

            RuntimeManager runtimeManager = null;
            RuntimeEngine engine = null;
            try {
                deploymentId = (String) bytesMessage.getObjectProperty("KIE_SignalDeploymentId");
                if (deploymentId == null) {
                    deploymentId = (String) bytesMessage.getObjectProperty("KIE_DeploymentId");
                }
                signal = (String) bytesMessage.getObjectProperty("KIE_Signal");
                processInstanceId = (Long) bytesMessage.getObjectProperty("KIE_SignalProcessInstanceId");
                workItemId = (Long) bytesMessage.getObjectProperty("KIE_SignalWorkItemId");

                logger.debug("Deployment id '{}', signal '{}', processInstanceId '{}', workItemId '{}'", deploymentId, signal, processInstanceId, workItemId);

                Collection<String> availableRuntimeManagers = matchDeployments(deploymentId, RuntimeManagerRegistry.get().getRegisteredIdentifiers());

                for (String matchedDeploymentId : availableRuntimeManagers) {
                    try {
                        runtimeManager = RuntimeManagerRegistry.get().getManager(matchedDeploymentId);

                        if (runtimeManager == null) {
                            throw new IllegalStateException("There is no runtime manager for deployment " + matchedDeploymentId);
                        }
                        logger.debug("RuntimeManager found for deployment id {}, reading message content with custom class loader of the deployment", matchedDeploymentId);
                        data = readData(bytesMessage, ((InternalRuntimeManager)runtimeManager).getEnvironment().getClassLoader());
                        logger.debug("Data read successfully with output {}", data);
                        engine = runtimeManager.getRuntimeEngine(ProcessInstanceIdContext.get(processInstanceId));

                        // perform operation either signal or complete work item
                        if (workItemId != null) {
                            Map<String, Object> results = new HashMap<String, Object>();
                            if (data != null) {
                                if (data instanceof Map) {
                                    results.putAll((Map) data);
                                } else {
                                    results.put("Data", data);
                                }
                            }
                            logger.debug("About to complete work item with id {} and data {}", workItemId, results);
                            engine.getKieSession().getWorkItemManager().completeWorkItem(workItemId, results);
                            logger.debug("Successfully completed work item with id {}", workItemId);
                        } else if (signal != null) {
                            if (processInstanceId != null) {
                                logger.debug("About to signal process instance with id {} and event data {} with signal {}", processInstanceId, data, signal);
                                engine.getKieSession().signalEvent(signal, data, processInstanceId);
                            } else {
                                logger.debug("About to broadcast signal {} and event data {}", signal, data);
                                runtimeManager.signalEvent(signal, data);
                            }
                            logger.debug("Signal completed successfully for signal {} with data {}", signal, data);
                        } else {
                            logger.warn("No signal or workitem id is given, skipping this message");
                        }
                    } catch (Exception e) {
                        logger.error("Unexpected exception while signaling: {}", e.getMessage(), e);
                    } finally {
                        if (runtimeManager != null && engine != null) {
                            runtimeManager.disposeRuntimeEngine(engine);
                        }
                    }
                }
            } catch (Exception e) {
                logger.error("Unexpected exception while processing signal JMS message: {}", e.getMessage(), e);
            }
        }
    }

    protected Object readData(BytesMessage message, ClassLoader cl) throws JMSException, Exception {
        Object data = null;
        if (message.getBodyLength() > 0) {
            byte[] reqData = new byte[(int) message.getBodyLength()];

            message.readBytes(reqData);
            if (reqData != null) {
                ObjectInputStream in = null;
                try {
                    in = new ClassLoaderObjectInputStream(cl, new ByteArrayInputStream(reqData));
                    data = in.readObject();
                } catch (IOException e) {
                    logger.warn("Exception while serializing context data", e);

                } finally {
                    if (in != null) {
                        in.close();
                    }
                }
            }
        }
        return data;
    }

    protected Collection<String> matchDeployments(String deploymentId, Collection<String> availableDeployments) {
        if (availableDeployments == null || availableDeployments.isEmpty()) {
            return Collections.emptyList();
        }
        Collection<String> matched = new ArrayList<String>();
        for (RuntimeManagerIdFilter filter : runtimeManagerIdFilters) {
            matched = filter.filter(deploymentId, availableDeployments);

            if (matched != null && !matched.isEmpty()) {
                return matched;
            }
        }

        // nothing matched return given deployment id
        return Collections.singletonList(deploymentId);
    }

}
