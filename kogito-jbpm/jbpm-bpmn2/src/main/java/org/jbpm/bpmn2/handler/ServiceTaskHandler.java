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

import org.kie.runtime.process.WorkItem;
import org.kie.runtime.process.WorkItemHandler;
import org.kie.runtime.process.WorkItemManager;

public class ServiceTaskHandler implements WorkItemHandler {

    public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
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
