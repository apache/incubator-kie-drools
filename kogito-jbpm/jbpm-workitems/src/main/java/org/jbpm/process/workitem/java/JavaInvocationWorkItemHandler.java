/*
 * Copyright 2015 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.jbpm.process.workitem.java;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jbpm.process.workitem.AbstractLogOrThrowWorkItemHandler;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JavaInvocationWorkItemHandler extends AbstractLogOrThrowWorkItemHandler {
    
    private static final Logger logger = LoggerFactory.getLogger(JavaInvocationWorkItemHandler.class);

	@SuppressWarnings("unchecked")
	public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
		String className = (String) workItem.getParameter("Class");
		String methodName = (String) workItem.getParameter("Method");
		Object object = workItem.getParameter("Object");
		List<String> paramTypes = (List<String>) workItem.getParameter("ParameterTypes");
		List<Object> params = (List<Object>) workItem.getParameter("Parameters");
		Object result = null;
		try {
            Class<?> c = Class.forName(className);
            Class<?>[] classes = null;
            Method method = null;
            if (params == null) {
            	params = new ArrayList<Object>();
            }
            if (paramTypes == null) {
        		classes = new Class<?>[0];
        		try {
        			method = c.getMethod(methodName, classes);
        		} catch (NoSuchMethodException e) {
        			for (Method m: c.getMethods()) {
        				if (m.getName().equals(methodName)
        						&& (m.getParameterTypes().length == params.size())) {
        					method = m;
        					break;
        				}
        			}
        			if (method == null) {
        				throw new NoSuchMethodException(className + "." + methodName + "(..)");
        			}
        		}
        	} else {
                List<Class<?>> classesList = new ArrayList<Class<?>>();
        		for (String paramType: paramTypes) {
                    classesList.add(Class.forName(paramType));
                }
        		classes = classesList.toArray(new Class<?>[classesList.size()]);
        		method = c.getMethod(methodName, classes);
    		}            		
            if (!Modifier.isStatic(method.getModifiers())) {
            	if (object == null) {
            		object = c.newInstance();
            	}
            }
            result = method.invoke(object, params.toArray());
            Map<String, Object> results = new HashMap<String, Object>();
            results.put("Result", result);
            manager.completeWorkItem(workItem.getId(), results);
    		return;
        } catch (ClassNotFoundException e) {
            handleException(e);
        } catch (InstantiationException e) {
            handleException(e);
        } catch (IllegalAccessException e) {
            handleException(e);
        } catch (NoSuchMethodException e) {
            handleException(e);
        } catch (InvocationTargetException e) {
            handleException(e);
        }

	}

	public void abortWorkItem(WorkItem arg0, WorkItemManager arg1) {
		// Do nothing
	}

}
