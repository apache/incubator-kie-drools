/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.jbpm.process.workitem.webservice;

import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import javax.xml.namespace.QName;

import org.apache.cxf.configuration.security.AuthorizationPolicy;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.endpoint.ClientCallback;
import org.apache.cxf.endpoint.dynamic.DynamicClientFactory;
import org.apache.cxf.jaxws.endpoint.dynamic.JaxWsDynamicClientFactory;
import org.apache.cxf.message.Message;
import org.apache.cxf.transport.http.HTTPConduit;
import org.drools.core.process.instance.impl.WorkItemImpl;
import org.jbpm.bpmn2.core.Bpmn2Import;
import org.jbpm.process.workitem.core.AbstractLogOrThrowWorkItemHandler;
import org.jbpm.workflow.core.impl.WorkflowProcessImpl;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemManager;
import org.kie.internal.runtime.Cacheable;
import org.kie.internal.runtime.manager.RuntimeManagerRegistry;
import org.kie.internal.runtime.manager.context.ProcessInstanceIdContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WebServiceWorkItemHandler extends AbstractLogOrThrowWorkItemHandler implements Cacheable {

    public static final String WSDL_IMPORT_TYPE = "http://schemas.xmlsoap.org/wsdl/";

    private static Logger logger = LoggerFactory.getLogger(WebServiceWorkItemHandler.class);

    private ConcurrentHashMap<String, Client> clients = new ConcurrentHashMap<String, Client>();
    private DynamicClientFactory dcf = null;
    private KieSession ksession;
    private int asyncTimeout = 10;
    private ClassLoader classLoader;
    private String username;
    private String password;

    enum WSMode {
        SYNC,
        ASYNC,
        ONEWAY;
    }

    /**
     * Used when no authentication is required
     * @param ksession - kie session
     */
    public WebServiceWorkItemHandler(KieSession ksession) {
        this(ksession, null, null);
    }

    /**
     * Dedicated constructor when BASIC authentication method shall be used
     * @param kieSession - kie session
     * @param username - basic auth username
     * @param password - basic auth password
     */
    public WebServiceWorkItemHandler(KieSession kieSession,
                                     String username,
                                     String password) {
        this.ksession = kieSession;
        this.username = username;
        this.password = password;
    }

    /**
     * Used when no authentication is required
     * @param ksession - kie session
     * @param classloader - classloader to use
     */
    public WebServiceWorkItemHandler(KieSession ksession,
                                     ClassLoader classloader) {
        this(ksession, classloader, null, null);
    }

    /**
     * Dedicated constructor when BASIC authentication method shall be used
     * @param ksession - kie session
     * @param classloader - classloader to use
     * @param username - basic auth username
     * @param password - basic auth password
     */
    public WebServiceWorkItemHandler(KieSession ksession,
                                     ClassLoader classloader,
                                     String username,
                                     String password) {
        this.ksession = ksession;
        this.classLoader = classloader;
        this.username = username;
        this.password = password;
    }

    /**
     * Used when no authentication is required
     * @param ksession - kie session
     * @param timeout - connection timeout
     */
    public WebServiceWorkItemHandler(KieSession ksession,
                                     int timeout) {
        this(ksession, timeout, null, null);
    }

    /**
     * Dedicated constructor when BASIC authentication method shall be used
     * @param ksession - kie session
     * @param timeout - connection timeout
     * @param username - basic auth username
     * @param password - basic auth password
     */
    public WebServiceWorkItemHandler(KieSession ksession,
                                     int timeout,
                                     String username,
                                     String password) {
        this.ksession = ksession;
        this.asyncTimeout = timeout;
        this.username = username;
        this.password = password;
    }

    public void executeWorkItem(WorkItem workItem,
                                final WorkItemManager manager) {

        // since JaxWsDynamicClientFactory will change the TCCL we need to restore it after creating client
        ClassLoader origClassloader = Thread.currentThread().getContextClassLoader();

        Object[] parameters = null;
        String interfaceRef = (String) workItem.getParameter("Interface");
        String operationRef = (String) workItem.getParameter("Operation");
        String endpointAddress = (String) workItem.getParameter("Endpoint");
        if (workItem.getParameter("Parameter") instanceof Object[]) {
            parameters = (Object[]) workItem.getParameter("Parameter");
        } else if (workItem.getParameter("Parameter") != null && workItem.getParameter("Parameter").getClass().isArray()) {
            int length = Array.getLength(workItem.getParameter("Parameter"));
            parameters = new Object[length];
            for (int i = 0; i < length; i++) {
                parameters[i] = Array.get(workItem.getParameter("Parameter"),
                                          i);
            }
        } else {
            parameters = new Object[]{workItem.getParameter("Parameter")};
        }

        String modeParam = (String) workItem.getParameter("Mode");
        WSMode mode = WSMode.valueOf(modeParam == null ? "SYNC" : modeParam.toUpperCase());

        try {
            Client client = getWSClient(workItem,
                                        interfaceRef);
            if (client == null) {
                throw new IllegalStateException("Unable to create client for web service " + interfaceRef + " - " + operationRef);
            }

            //Override endpoint address if configured.
            if (endpointAddress != null && !"".equals(endpointAddress)) {
                client.getRequestContext().put(Message.ENDPOINT_ADDRESS,
                                               endpointAddress);
            }

            // apply authorization if needed
            applyAuthorization(username, password, client);

            switch (mode) {
                case SYNC:
                    Object[] result = client.invoke(operationRef,
                                                    parameters);

                    Map<String, Object> output = new HashMap<String, Object>();

                    if (result == null || result.length == 0) {
                        output.put("Result",
                                   null);
                    } else {
                        output.put("Result",
                                   result[0]);
                    }
                    logger.debug("Received sync response {} completeing work item {}",
                                 result,
                                 workItem.getId());
                    manager.completeWorkItem(workItem.getId(),
                                             output);
                    break;
                case ASYNC:
                    final ClientCallback callback = new ClientCallback();
                    final long workItemId = workItem.getId();
                    final String deploymentId = nonNull(((WorkItemImpl) workItem).getDeploymentId());
                    final long processInstanceId = workItem.getProcessInstanceId();

                    client.invoke(callback,
                                  operationRef,
                                  parameters);
                    new Thread(new Runnable() {

                        public void run() {

                            try {

                                Object[] result = callback.get(asyncTimeout,
                                                               TimeUnit.SECONDS);
                                Map<String, Object> output = new HashMap<String, Object>();
                                if (callback.isDone()) {
                                    if (result == null) {
                                        output.put("Result",
                                                   null);
                                    } else {
                                        output.put("Result",
                                                   result[0]);
                                    }
                                }
                                logger.debug("Received async response {} completeing work item {}",
                                             result,
                                             workItemId);

                                RuntimeManager manager = RuntimeManagerRegistry.get().getManager(deploymentId);
                                if (manager != null) {
                                    RuntimeEngine engine = manager.getRuntimeEngine(ProcessInstanceIdContext.get(processInstanceId));

                                    engine.getKieSession().getWorkItemManager().completeWorkItem(workItemId,
                                                                                                 output);

                                    manager.disposeRuntimeEngine(engine);
                                } else {
                                    // in case there is no RuntimeManager available use available ksession,
                                    // as it might be used without runtime manager at all
                                    ksession.getWorkItemManager().completeWorkItem(workItemId,
                                                                                   output);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                throw new RuntimeException("Error encountered while invoking ws operation asynchronously",
                                                           e);
                            }
                        }
                    }).start();
                    break;
                case ONEWAY:
                    ClientCallback callbackFF = new ClientCallback();

                    client.invoke(callbackFF,
                                  operationRef,
                                  parameters);
                    logger.debug("One way operation, not going to wait for response, completing work item {}",
                                 workItem.getId());
                    manager.completeWorkItem(workItem.getId(),
                                             new HashMap<String, Object>());
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            handleException(e);
        } finally {
            Thread.currentThread().setContextClassLoader(origClassloader);
        }
    }

    @SuppressWarnings("unchecked")
    protected Client getWSClient(WorkItem workItem,
                                              String interfaceRef) {
        if (clients.containsKey(interfaceRef)) {
            return clients.get(interfaceRef);
        }

        synchronized (this) {

        	if (clients.containsKey(interfaceRef)) {
            	return clients.get(interfaceRef);
        	}

	        String importLocation = (String) workItem.getParameter("Url");
	        String importNamespace = (String) workItem.getParameter("Namespace");
	        if (importLocation != null && importLocation.trim().length() > 0
	                && importNamespace != null && importNamespace.trim().length() > 0) {
	            Client client = getDynamicClientFactory().createClient(importLocation,
	                                                                   new QName(importNamespace,
	                                                                             interfaceRef),
	                                                                   getInternalClassLoader(),
	                                                                   null);
	            clients.put(interfaceRef,
	                        client);
	            return client;
	        }

	        long processInstanceId = ((WorkItemImpl) workItem).getProcessInstanceId();
	        WorkflowProcessImpl process = ((WorkflowProcessImpl) ksession.getProcessInstance(processInstanceId).getProcess());
	        List<Bpmn2Import> typedImports = (List<Bpmn2Import>) process.getMetaData("Bpmn2Imports");

	        if (typedImports != null) {
	            Client client = null;
	            for (Bpmn2Import importObj : typedImports) {
	                if (WSDL_IMPORT_TYPE.equalsIgnoreCase(importObj.getType())) {
	                    try {
	                        client = getDynamicClientFactory().createClient(importObj.getLocation(),
	                                                                        new QName(importObj.getNamespace(),
	                                                                                  interfaceRef),
	                                                                        getInternalClassLoader(),
	                                                                        null);
	                        clients.put(interfaceRef,
	                                    client);
	                        return client;
	                    } catch (Exception e) {
	                        logger.error("Error when creating WS Client",
	                                     e);
	                        continue;
	                    }
	                }
	            }
	        }
        }
        return null;
    }

    protected synchronized DynamicClientFactory getDynamicClientFactory() {
        if (this.dcf == null) {
            this.dcf = JaxWsDynamicClientFactory.newInstance();
        }
        return this.dcf;
    }

    public void abortWorkItem(WorkItem workItem,
                              WorkItemManager manager) {
        // Do nothing, cannot be aborted
    }

    private ClassLoader getInternalClassLoader() {
        if (this.classLoader != null) {
            return this.classLoader;
        }

        return Thread.currentThread().getContextClassLoader();
    }

    public ClassLoader getClassLoader() {
        return classLoader;
    }

    public void setClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    protected String nonNull(String value) {
        if (value == null) {
            return "";
        }
        return value;
    }

    @Override
    public void close() {
        if (clients != null) {
            for (Client client : clients.values()) {
                client.destroy();
            }
        }
    }

    protected void applyAuthorization(String userName, String password, Client client) {
        if(userName != null && password != null) {
            HTTPConduit httpConduit = (HTTPConduit) client.getConduit();
            AuthorizationPolicy authorizationPolicy = new AuthorizationPolicy();
            authorizationPolicy.setUserName(userName);
            authorizationPolicy.setPassword(password);

            authorizationPolicy.setAuthorizationType("Basic");
            httpConduit.setAuthorization(authorizationPolicy);
        } else {
            logger.warn("UserName and Password must be provided to set the authorization policy.");
        }
    }

    public void setClients(ConcurrentHashMap<String, Client> clients) {
        this.clients = clients;
    }
}