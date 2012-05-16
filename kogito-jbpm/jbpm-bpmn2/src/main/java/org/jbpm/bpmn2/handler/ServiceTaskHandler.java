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

package org.jbpm.bpmn2.handler;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.xml.namespace.QName;

import org.apache.cxf.endpoint.Client;
import org.apache.cxf.jaxws.endpoint.dynamic.JaxWsDynamicClientFactory;
import org.drools.process.instance.impl.WorkItemImpl;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.process.WorkItem;
import org.drools.runtime.process.WorkItemHandler;
import org.drools.runtime.process.WorkItemManager;
import org.jbpm.bpmn2.core.Bpmn2Import;
import org.jbpm.workflow.core.impl.WorkflowProcessImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServiceTaskHandler implements WorkItemHandler {
    
public static final String WSDL_IMPORT_TYPE = "http://schemas.xmlsoap.org/wsdl/";
    
    private static Logger logger = LoggerFactory.getLogger(ServiceTaskHandler.class);
    
    private ConcurrentHashMap<String, Client> clients = new ConcurrentHashMap<String, Client>();
    private JaxWsDynamicClientFactory dcf;
    private StatefulKnowledgeSession ksession;
    
    public ServiceTaskHandler() {
        this.dcf = JaxWsDynamicClientFactory.newInstance();
    }
    
    public ServiceTaskHandler(StatefulKnowledgeSession ksession) {
        this.dcf = JaxWsDynamicClientFactory.newInstance();
        this.ksession = ksession;
    }

    public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
        String implementation = (String) workItem.getParameter("implementation");
        if ("##WebService".equalsIgnoreCase(implementation)) {
            String interfaceRef = (String) workItem.getParameter("interfaceImplementationRef");
            String operationRef = (String) workItem.getParameter("operationImplementationRef");
            Object parameter = workItem.getParameter("Parameter");
            
            
            try {
                 Client client = getWSClient(workItem, interfaceRef);
                
                 Object[] result = client.invoke(operationRef, parameter);

                 Map<String, Object> output = new HashMap<String, Object>();          

                 if (result == null) {
                   output.put("Result", null);
                 } else {
                   output.put("Result", result[0]);
                 }

                 manager.completeWorkItem(workItem.getId(), output);
             } catch (Exception e) {
                 logger.error("Error when executing work item", e);
             }
        } else {
            executeJavaWorkItem(workItem, manager);
        }
        

    }
    
    @SuppressWarnings("unchecked")
    protected synchronized Client getWSClient(WorkItem workItem, String interfaceRef) {
        if (clients.containsKey(interfaceRef)) {
            return clients.get(interfaceRef);
        }
        
        long processInstanceId = ((WorkItemImpl) workItem).getProcessInstanceId();
        WorkflowProcessImpl process = ((WorkflowProcessImpl) ksession.getProcessInstance(processInstanceId).getProcess());
        List<Bpmn2Import> typedImports = (List<Bpmn2Import>)process.getMetaData("Bpmn2Imports");
        
        
        
        if (typedImports != null ){
            Client client = null;
            for (Bpmn2Import importObj : typedImports) {
                
                if (WSDL_IMPORT_TYPE.equalsIgnoreCase(importObj.getType())) {
                
                    client = dcf.createClient(importObj.getLocation(), new QName(importObj.getNamespace(), interfaceRef));
                    clients.put(interfaceRef, client);
                    
                    return client;
                }
            }
        }
        
        return null;

    }

    public void executeJavaWorkItem(WorkItem workItem, WorkItemManager manager) {
        String i = (String) workItem.getParameter("Interface");
        String operation = (String) workItem.getParameter("Operation");
        String parameterType = (String) workItem.getParameter("ParameterType");
        Object parameter = workItem.getParameter("Parameter");
        try {
            Class<?> c = Class.forName(i);
            Object instance = c.newInstance();
            Class<?>[] classes = null;
            Object[] params = null;
            if (parameterType != null) {
                classes = new Class<?>[] {
                    Class.forName(parameterType)
                };
                params = new Object[] {
                    parameter
                };
            }
            Method method = c.getMethod(operation, classes);
            Object result = method.invoke(instance, params);
            Map<String, Object> results = new HashMap<String, Object>();
            results.put("Result", result);
            manager.completeWorkItem(workItem.getId(), results);
        } catch (ClassNotFoundException e) {
            System.err.println(e);
        } catch (InstantiationException e) {
            System.err.println(e);
        } catch (IllegalAccessException e) {
            System.err.println(e);
        } catch (NoSuchMethodException e) {
            System.err.println(e);
        } catch (InvocationTargetException e) {
            System.err.println(e);
        }
    }

    public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
        // Do nothing, cannot be aborted
    }

}
