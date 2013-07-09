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
import java.util.Map;

import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServiceTaskHandler implements WorkItemHandler {

    private static final Logger logger = LoggerFactory.getLogger(ServiceTaskHandler.class);
    
    private boolean logThrownException = true;
    
    public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
        String service = (String) workItem.getParameter("Interface");
        String operation = (String) workItem.getParameter("Operation");
        String parameterType = (String) workItem.getParameter("ParameterType");
        Object parameter = workItem.getParameter("Parameter");
        try {
            Class<?> c = Class.forName(service);
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
        } catch (ClassNotFoundException cnfe) {
            handleException(cnfe, service, operation, parameterType, parameter);
        } catch (InstantiationException ie) {
            handleException(ie, service, operation, parameterType, parameter);
        } catch (IllegalAccessException iae) {
            handleException(iae, service, operation, parameterType, parameter);
        } catch (NoSuchMethodException nsme) {
            handleException(nsme, service, operation, parameterType, parameter);
        } catch (InvocationTargetException ite) {
            handleException(ite, service, operation, parameterType, parameter);
        } catch( Throwable cause ) { 
            handleException(cause, service, operation, parameterType, parameter);
        }
    }

    private void handleException(Throwable cause, String service, String operation, String paramType, Object param) { 
        if( this.logThrownException ) {
            String message = this.getClass().getSimpleName() + " failed when calling " + service + "." + operation;
            logger.error(message, cause);
        } else { 
            WorkItemHandlerRuntimeException wihRe;
            if( cause instanceof InvocationTargetException ) { 
                Throwable realCause = cause.getCause();
                wihRe = new WorkItemHandlerRuntimeException(realCause);
                wihRe.setStackTrace(realCause.getStackTrace());
            } else { 
                wihRe = new WorkItemHandlerRuntimeException(cause);
                wihRe.setStackTrace(cause.getStackTrace());
            }
            wihRe.setInformation("Interface", service);
            wihRe.setInformation("Operation", operation);
            wihRe.setInformation("ParameterType", paramType);
            wihRe.setInformation("Parameter", param);
            wihRe.setInformation(WorkItemHandlerRuntimeException.WORKITEMHANDLERTYPE, this.getClass().getSimpleName());
            throw wihRe;
        }
    }
    
    public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
        // Do nothing, cannot be aborted
    }

    public void setLogThrownException(boolean logException) { 
        this.logThrownException = logException;
    }
}
