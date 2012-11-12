package org.jbpm.process.workitem.java;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kie.runtime.process.WorkItem;
import org.kie.runtime.process.WorkItemHandler;
import org.kie.runtime.process.WorkItemManager;

public class JavaInvocationWorkItemHandler implements WorkItemHandler {

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
        manager.abortWorkItem(workItem.getId());
	}

	public void abortWorkItem(WorkItem arg0, WorkItemManager arg1) {
		// Do nothing
	}

}
