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

package org.jbpm.process.workitem.bpmn2;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import javax.xml.namespace.QName;

import org.apache.cxf.endpoint.Client;
import org.apache.cxf.endpoint.ClientCallback;
import org.apache.cxf.jaxws.endpoint.dynamic.JaxWsDynamicClientFactory;
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

public class ServiceTaskHandler extends AbstractLogOrThrowWorkItemHandler implements Cacheable {

    public static final String WSDL_IMPORT_TYPE = "http://schemas.xmlsoap.org/wsdl/";

    private static final Logger logger = LoggerFactory.getLogger(ServiceTaskHandler.class);

    private ConcurrentHashMap<String, Client> clients = new ConcurrentHashMap<String, Client>();
    private JaxWsDynamicClientFactory dcf;
    private KieSession ksession;
    private int asyncTimeout = 10;
    private ClassLoader classLoader;

    enum WSMode {
        SYNC,
        ASYNC,
        ONEWAY;

    }

    public ServiceTaskHandler() {
        this.dcf = JaxWsDynamicClientFactory.newInstance();
    }

    public ServiceTaskHandler(KieSession ksession) {
        this.dcf = JaxWsDynamicClientFactory.newInstance();
        this.ksession = ksession;
    }

    public ServiceTaskHandler(KieSession ksession,
                              ClassLoader classloader) {
        this.dcf = JaxWsDynamicClientFactory.newInstance();
        this.ksession = ksession;
        this.classLoader = classloader;
    }

    public ServiceTaskHandler(KieSession ksession,
                              int timeout) {
        this.dcf = JaxWsDynamicClientFactory.newInstance();
        this.ksession = ksession;
        this.asyncTimeout = timeout;
    }

    public void executeWorkItem(WorkItem workItem,
                                final WorkItemManager manager) {
        String implementation = (String) workItem.getParameter("implementation");
        if ("##WebService".equalsIgnoreCase(implementation)) {
            // since JaxWsDynamicClientFactory will change the TCCL we need to restore it after creating client
            ClassLoader origClassloader = Thread.currentThread().getContextClassLoader();

            String interfaceRef = (String) workItem.getParameter("interfaceImplementationRef");
            String operationRef = (String) workItem.getParameter("operationImplementationRef");
            Object parameter = workItem.getParameter("Parameter");
            WSMode mode = WSMode.valueOf(workItem.getParameter("mode") == null ? "SYNC" : ((String) workItem.getParameter("mode")).toUpperCase());

            try {
                Client client = getWSClient(workItem,
                                            interfaceRef);
                if (client == null) {
                    throw new IllegalStateException("Unable to create client for web service " + interfaceRef + " - " + operationRef);
                }
                switch (mode) {
                    case SYNC:
                        Object[] result = client.invoke(operationRef,
                                                        parameter);

                        Map<String, Object> output = new HashMap<String, Object>();

                        if (result == null || result.length == 0) {
                            output.put("Result",
                                       null);
                        } else {
                            output.put("Result",
                                       result[0]);
                        }

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
                                      parameter);
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
                                    logger.error("Error encountered while invoking ws operation asynchronously ",
                                                 e);
                                }
                            }
                        }).start();
                        break;
                    case ONEWAY:
                        ClientCallback callbackFF = new ClientCallback();

                        client.invoke(callbackFF,
                                      operationRef,
                                      parameter);
                        manager.completeWorkItem(workItem.getId(),
                                                 new HashMap<String, Object>());
                        break;
                    default:
                        break;
                }
            } catch (Exception e) {
                handleException(e,
                                interfaceRef,
                                "",
                                operationRef,
                                parameter.getClass().getName(),
                                parameter);
            } finally {
                Thread.currentThread().setContextClassLoader(origClassloader);
            }
        } else {
            executeJavaWorkItem(workItem,
                                manager);
        }
    }

    @SuppressWarnings("unchecked")
    protected synchronized Client getWSClient(WorkItem workItem,
                                              String interfaceRef) {
        if (clients.containsKey(interfaceRef)) {
            return clients.get(interfaceRef);
        }

        long processInstanceId = ((WorkItemImpl) workItem).getProcessInstanceId();
        WorkflowProcessImpl process = ((WorkflowProcessImpl) ksession.getProcessInstance(processInstanceId).getProcess());
        List<Bpmn2Import> typedImports = (List<Bpmn2Import>) process.getMetaData("Bpmn2Imports");

        if (typedImports != null) {
            Client client = null;
            for (Bpmn2Import importObj : typedImports) {

                if (WSDL_IMPORT_TYPE.equalsIgnoreCase(importObj.getType())) {

                    try {
                        client = dcf.createClient(importObj.getLocation(),
                                                  new QName(importObj.getNamespace(),
                                                            interfaceRef),
                                                  getInternalClassLoader(),
                                                  null);
                        clients.put(interfaceRef,
                                    client);
                        logger.info("WS Client is created for {" + importObj.getNamespace() + "}" + interfaceRef);
                        return client;
                    } catch (Exception e) {
                        logger.info("Error when creating WS Client. You can ignore this error as long as a client is eventually created",
                                    e);
                        continue;
                    }
                }
            }
        }

        return null;
    }

    private ClassLoader getInternalClassLoader() {
        if (this.classLoader != null) {
            return this.classLoader;
        }

        return Thread.currentThread().getContextClassLoader();
    }

    public void executeJavaWorkItem(WorkItem workItem,
                                    WorkItemManager manager) {
        String i = (String) workItem.getParameter("Interface");
        String iImplementationRef = (String) workItem.getParameter("interfaceImplementationRef");
        String operation = (String) workItem.getParameter("Operation");
        String parameterType = (String) workItem.getParameter("ParameterType");
        Object parameter = workItem.getParameter("Parameter");

        String[] interfaces = {i, iImplementationRef};
        Class<?> c = null;

        for (String interf : interfaces) {
            try {
                c = Class.forName(interf,
                                  true,
                                  getInternalClassLoader());
                break;
            } catch (ClassNotFoundException cnfe) {
                if (interf.compareTo(interfaces[interfaces.length - 1]) == 0) {
                    handleException(cnfe,
                                    i,
                                    iImplementationRef,
                                    operation,
                                    parameterType,
                                    parameter);
                }
            }
        }

        try {
            Object instance = c.newInstance();
            Class<?>[] classes = null;
            Object[] params = null;
            if (parameterType != null) {
                classes = new Class<?>[]{
                        Class.forName(parameterType,
                                      true,
                                      getInternalClassLoader())
                };
                params = new Object[]{
                        parameter
                };
            }
            Method method = c.getMethod(operation,
                                        classes);
            Object result = method.invoke(instance,
                                          params);
            Map<String, Object> results = new HashMap<String, Object>();
            results.put("Result",
                        result);
            manager.completeWorkItem(workItem.getId(),
                                     results);
        } catch (ClassNotFoundException e) {
            handleException(e,
                            i,
                            iImplementationRef,
                            operation,
                            parameterType,
                            parameter);
        } catch (InstantiationException e) {
            handleException(e,
                            i,
                            iImplementationRef,
                            operation,
                            parameterType,
                            parameter);
        } catch (IllegalAccessException e) {
            handleException(e,
                            i,
                            iImplementationRef,
                            operation,
                            parameterType,
                            parameter);
        } catch (NoSuchMethodException e) {
            handleException(e,
                            i,
                            iImplementationRef,
                            operation,
                            parameterType,
                            parameter);
        } catch (InvocationTargetException e) {
            handleException(e,
                            i,
                            iImplementationRef,
                            operation,
                            parameterType,
                            parameter);
        }
    }

    private void handleException(Throwable cause,
                                 String service,
                                 String iImplementationRef,
                                 String operation,
                                 String paramType,
                                 Object param) {
        logger.debug("Handling exception {} inside service {} or {} and operation {} with param type {} and value {}",
                     cause.getMessage(),
                     service,
                     operation,
                     paramType,
                     param);
        Map<String, Object> data = new HashMap<String, Object>();

        data.put("Interface",
                 service);
        data.put("InterfaceImplementationRef",
                 iImplementationRef);
        data.put("Operation",
                 operation);
        data.put("ParameterType",
                 paramType);
        data.put("Parameter",
                 param);

        handleException(cause,
                        data);
    }

    public void abortWorkItem(WorkItem workItem,
                              WorkItemManager manager) {
        // Do nothing, cannot be aborted
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
}
