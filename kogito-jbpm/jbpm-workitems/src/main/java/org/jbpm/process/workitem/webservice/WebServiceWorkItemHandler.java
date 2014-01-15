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

package org.jbpm.process.workitem.webservice;

import java.lang.reflect.Array;
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
import org.jbpm.process.workitem.AbstractLogOrThrowWorkItemHandler;
import org.jbpm.workflow.core.impl.WorkflowProcessImpl;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WebServiceWorkItemHandler extends AbstractLogOrThrowWorkItemHandler {
    
    public static final String WSDL_IMPORT_TYPE = "http://schemas.xmlsoap.org/wsdl/";
    
    private static Logger logger = LoggerFactory.getLogger(WebServiceWorkItemHandler.class);
    
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
    
    public WebServiceWorkItemHandler(KieSession ksession) {
        this.dcf = JaxWsDynamicClientFactory.newInstance();
        this.ksession = ksession;
    }
    
    public WebServiceWorkItemHandler(KieSession ksession, ClassLoader classloader) {
        this.dcf = JaxWsDynamicClientFactory.newInstance();
        this.ksession = ksession;
        this.classLoader = classloader;
    }
    
    public WebServiceWorkItemHandler(KieSession ksession, int timeout) {
        this.dcf = JaxWsDynamicClientFactory.newInstance();
        this.ksession = ksession;
        this.asyncTimeout = timeout;
    }

    public void executeWorkItem(WorkItem workItem, final WorkItemManager manager) {
    	Object[] parameters = null;
        String interfaceRef = (String) workItem.getParameter("Interface");
        String operationRef = (String) workItem.getParameter("Operation");
        if ( workItem.getParameter("Parameter") instanceof Object[]) {
        	parameters =  (Object[]) workItem.getParameter("Parameter");
        } else if (workItem.getParameter("Parameter") != null && workItem.getParameter("Parameter").getClass().isArray()) {
        	int length = Array.getLength(workItem.getParameter("Parameter"));
            parameters = new Object[length];
            for(int i = 0; i < length; i++) {
            	parameters[i] = Array.get(workItem.getParameter("Parameter"), i);
            }            
        } else {
        	parameters = new Object[]{ workItem.getParameter("Parameter")};
        }
        
        String modeParam = (String) workItem.getParameter("Mode");
        WSMode mode = WSMode.valueOf(modeParam == null ? "SYNC" : modeParam.toUpperCase());
            
        try {
             Client client = getWSClient(workItem, interfaceRef);
             if (client == null) {
                 throw new IllegalStateException("Unable to create client for web service " + interfaceRef + " - " + operationRef);
             }
             switch (mode) {
                case SYNC:
                    Object[] result = client.invoke(operationRef, parameters);
                    
                    Map<String, Object> output = new HashMap<String, Object>();          
   
                    if (result == null || result.length == 0) {
                      output.put("Result", null);
                    } else {
                        output.put("Result", result[0]);
                    }
                    logger.debug("Received sync response {} completeing work item {}", result, workItem.getId());
                    manager.completeWorkItem(workItem.getId(), output);
                    break;
                case ASYNC:
                    final ClientCallback callback = new ClientCallback();
                    final long workItemId = workItem.getId();
                    client.invoke(callback, operationRef, parameters);
                    new Thread(new Runnable() {
                       
                       public void run() {
                           
                           try {
                              
                               Object[] result = callback.get(asyncTimeout, TimeUnit.SECONDS);
                               Map<String, Object> output = new HashMap<String, Object>();          
                               if (callback.isDone()) {
                                   if (result == null) {
                                     output.put("Result", null);
                               } else {
                                 output.put("Result", result[0]);
                               }
                           }
                           logger.debug("Received async response {} completeing work item {}", result, workItemId);
                           ksession.getWorkItemManager().completeWorkItem(workItemId, output);
                       } catch (Exception e) {
                    	   e.printStackTrace();
                           throw new RuntimeException("Error encountered while invoking ws operation asynchronously", e);
                       }
                       
                       
                   }
               }).start();
                break;
            case ONEWAY:
                ClientCallback callbackFF = new ClientCallback();
                
                client.invoke(callbackFF, operationRef, parameters);
                logger.debug("One way operation, not going to wait for response, completing work item {}", workItem.getId());
                manager.completeWorkItem(workItem.getId(),  new HashMap<String, Object>());
                break;
            default:
                break;
            }

         } catch (Exception e) {
             handleException(e);
         }
    }
    
    @SuppressWarnings("unchecked")
    protected synchronized Client getWSClient(WorkItem workItem, String interfaceRef) {
        if (clients.containsKey(interfaceRef)) {
            return clients.get(interfaceRef);
        }
        
        String importLocation = (String) workItem.getParameter("Url");
        String importNamespace = (String) workItem.getParameter("Namespace");
        if (importLocation != null && importLocation.trim().length() > 0 
        		&& importNamespace != null && importNamespace.trim().length() > 0) {
        	Client client = dcf.createClient(importLocation, new QName(importNamespace, interfaceRef), getInternalClassLoader(), null);
            clients.put(interfaceRef, client);
            return client;
        }
        
        
        long processInstanceId = ((WorkItemImpl) workItem).getProcessInstanceId();
        WorkflowProcessImpl process = ((WorkflowProcessImpl) ksession.getProcessInstance(processInstanceId).getProcess());
        List<Bpmn2Import> typedImports = (List<Bpmn2Import>)process.getMetaData("Bpmn2Imports");
        
        if (typedImports != null ){
            Client client = null;
            for (Bpmn2Import importObj : typedImports) {
                if (WSDL_IMPORT_TYPE.equalsIgnoreCase(importObj.getType())) {
                    try {
                        client = dcf.createClient(importObj.getLocation(), new QName(importObj.getNamespace(), interfaceRef), getInternalClassLoader(), null);
                        clients.put(interfaceRef, client);
                        return client;
                    } catch (Exception e) {
                    	logger.error("Error when creating WS Client", e);
                        continue;
                    }
                }
            }
        }
        return null;
    }

    public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
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
}
