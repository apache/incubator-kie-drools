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

package org.jbpm.integration.console;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.jboss.bpm.console.client.model.TaskRef;
import org.jbpm.task.AccessType;
import org.jbpm.task.Status;
import org.jbpm.task.Task;
import org.jbpm.task.TaskService;
import org.jbpm.task.query.TaskSummary;
import org.jbpm.task.service.ContentData;
import org.jbpm.task.service.responsehandlers.BlockingTaskOperationResponseHandler;

public class TaskManagement implements org.jboss.bpm.console.server.integration.TaskManagement {
	
	private static int clientCounter = 0;
    
	private TaskService service;

	private Map<String, List<String>> groupListMap = new HashMap<String, List<String>>();
	
	public void connect() {
	    if (service == null) {
	        
    	    Properties jbpmConsoleProperties = StatefulKnowledgeSessionUtil.getJbpmConsoleProperties();   
            service = TaskClientFactory.newInstance(jbpmConsoleProperties, "org.jbpm.integration.console.TaskManagement"+clientCounter);
            clientCounter++;
       
    	    loadUserGroups();
	    }
		
	}
	
	private void loadUserGroups() {
		try {
			ClassLoader loader = Thread.currentThread().getContextClassLoader();
			URL url = null;
			String propertyName = "roles.properties";

			if (loader instanceof URLClassLoader) {
				URLClassLoader ucl = (URLClassLoader) loader;
				url = ucl.findResource(propertyName);
			}
			if (url == null) {
				url = loader.getResource(propertyName);
			}
			if (url == null) {
				System.out.println("No properties file: " + propertyName + " found");
			} else {
				Properties bundle = new Properties();
				InputStream is = url.openStream();
				if (is != null) {
					bundle.load(is);
					is.close();
				} else {
					throw new IOException("Properties file " + propertyName	+ " not available");
				}
				Enumeration<?> propertyNames = bundle.propertyNames();
				while (propertyNames.hasMoreElements()) {
					String key = (String) propertyNames.nextElement();
					String value = bundle.getProperty(key);
					groupListMap.put(key, Arrays.asList(value.split(",")));
				}
			}
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}
	
	public TaskRef getTaskById(long taskId) {
		connect();
		Task task = service.getTask(taskId);
		
        return Transform.task(task);
	}

	public void assignTask(long taskId, String idRef, String userId) {
		connect(); 
		
		if (idRef == null) {
			service.release(taskId, userId);
		} else if (idRef.equals(userId)) {
			List<String> roles = groupListMap.get(userId);
			if (roles == null) {
				service.claim(taskId, idRef);
			} else {
				service.claim(taskId, idRef, roles);
			}
		} else {
			service.delegate(taskId, userId, idRef);
		}
		
	}

	public void completeTask(long taskId, Map data, String userId) {
		connect();
		
		service.start(taskId, userId);
		
		ContentData contentData = null;
		if (data != null) {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ObjectOutputStream out;
			try {
				out = new ObjectOutputStream(bos);
				out.writeObject(data);
				out.close();
				contentData = new ContentData();
				contentData.setContent(bos.toByteArray());
				contentData.setAccessType(AccessType.Inline);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
  
		service.complete(taskId, userId, contentData);
		
	}

	@SuppressWarnings("unchecked")
	public void completeTask(long taskId, String outcome, Map data, String userId) {
	    if ("jbpm_skip_task".equalsIgnoreCase(outcome)) {
	        skipTask(taskId, userId);
	    } else {
    		data.put("outcome", outcome);
    		completeTask(taskId, data, userId);
	    }
	}

	public void releaseTask(long taskId, String userId) {
		// TODO: this method is not being invoked, it's using
		// assignTask with null parameter instead
		connect(); 
		service.release(taskId, userId);
		 
	}

	public List<TaskRef> getAssignedTasks(String idRef) {
		connect();
        List<TaskRef> result = new ArrayList<TaskRef>();
		try {
			List<TaskSummary> tasks = service.getTasksOwned(idRef, "en-UK");
			
	        for (TaskSummary task: tasks) {
	        	if (task.getStatus() == Status.Reserved) {
	        		result.add(Transform.task(task));
	        	}
	        }
		} catch (Throwable t) {
			t.printStackTrace();
		}
		return result;
	}

	public List<TaskRef> getUnassignedTasks(String idRef, String participationType) {
		// TODO participationType ?
		connect();
        List<TaskRef> result = new ArrayList<TaskRef>();
		try {
			List<String> roles = groupListMap.get(idRef);
			List<TaskSummary> tasks = null;
			
			if (roles == null) {
				tasks = service.getTasksAssignedAsPotentialOwner(idRef, "en-UK");
			} else {
				tasks = service.getTasksAssignedAsPotentialOwner(idRef, roles, "en-UK");
			}
			
			
	        for (TaskSummary task: tasks) {
	        	result.add(Transform.task(task));
	        }
		} catch (Throwable t) {
			t.printStackTrace();
		}
		return result;
	}
	
	public void skipTask(long taskId, String userId) {
	    connect();
        service.skip(taskId, userId);
	}

}
