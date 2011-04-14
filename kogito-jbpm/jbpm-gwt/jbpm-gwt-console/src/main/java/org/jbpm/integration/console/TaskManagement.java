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

import org.drools.SystemEventListenerFactory;
import org.jboss.bpm.console.client.model.TaskRef;
import org.jbpm.process.workitem.wsht.BlockingGetTaskResponseHandler;
import org.jbpm.task.AccessType;
import org.jbpm.task.Status;
import org.jbpm.task.Task;
import org.jbpm.task.query.TaskSummary;
import org.jbpm.task.service.ContentData;
import org.jbpm.task.service.TaskClient;
import org.jbpm.task.service.mina.MinaTaskClientConnector;
import org.jbpm.task.service.mina.MinaTaskClientHandler;
import org.jbpm.task.service.responsehandlers.BlockingTaskOperationResponseHandler;
import org.jbpm.task.service.responsehandlers.BlockingTaskSummaryResponseHandler;

public class TaskManagement implements org.jboss.bpm.console.server.integration.TaskManagement {
	
	// TODO: make this configurable
	private String ipAddress = "127.0.0.1";
	private int port = 9123;
	private TaskClient client;
	private Map<String, List<String>> groupListMap = new HashMap<String, List<String>>();

	public void setConnection(String ipAddress, int port) {
		this.ipAddress = ipAddress;
		this.port = port;
	}
	
	public void connect() {
		if (client == null) {
			client = new TaskClient(new MinaTaskClientConnector("org.drools.process.workitem.wsht.WSHumanTaskHandler",
									new MinaTaskClientHandler(SystemEventListenerFactory.getSystemEventListener())));
			boolean connected = client.connect(ipAddress, port);
			if (!connected) {
				throw new IllegalArgumentException(
					"Could not connect task client");
			}
		}
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
					System.out.print("Loaded user " + key + ":");
					for (String role: groupListMap.get(key)) {
						System.out.print(" " + role);
					}
					System.out.println();
				}
			}
		} catch (Throwable t) {
			t.printStackTrace();
		}

	}
	
	public TaskRef getTaskById(long taskId) {
		connect();
		BlockingGetTaskResponseHandler responseHandler = new BlockingGetTaskResponseHandler();
		client.getTask(taskId, responseHandler);
		Task task = responseHandler.getTask();
        return Transform.task(task);
	}

	public void assignTask(long taskId, String idRef, String userId) {
		connect();
		BlockingTaskOperationResponseHandler responseHandler = new BlockingTaskOperationResponseHandler();
		if (idRef == null) {
			client.release(taskId, userId, responseHandler);
		} else if (idRef.equals(userId)) {
			List<String> roles = groupListMap.get(userId);
			if (roles == null) {
				client.claim(taskId, idRef, responseHandler);
			} else {
				client.claim(taskId, idRef, roles, responseHandler);
			}
		} else {
			client.delegate(taskId, userId, idRef, responseHandler);
		}
		responseHandler.waitTillDone(5000);
	}

	@SuppressWarnings("unchecked")
	public void completeTask(long taskId, Map data, String userId) {
		connect();
		BlockingTaskOperationResponseHandler responseHandler = new BlockingTaskOperationResponseHandler();
		client.start(taskId, userId, responseHandler);
		responseHandler.waitTillDone(5000);
		responseHandler = new BlockingTaskOperationResponseHandler();
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
		client.complete(taskId, userId, contentData, responseHandler);
		responseHandler.waitTillDone(5000);
	}

	@SuppressWarnings("unchecked")
	public void completeTask(long taskId, String outcome, Map data, String userId) {
		data.put("outcome", outcome);
		completeTask(taskId, data, userId);
	}

	public void releaseTask(long taskId, String userId) {
		// TODO: this method is not being invoked, it's using
		// assignTask with null parameter instead
		connect();
		BlockingTaskOperationResponseHandler responseHandler = new BlockingTaskOperationResponseHandler();
		client.release(taskId, userId, responseHandler);
		responseHandler.waitTillDone(5000);
	}

	public List<TaskRef> getAssignedTasks(String idRef) {
		connect();
        List<TaskRef> result = new ArrayList<TaskRef>();
		try {
			BlockingTaskSummaryResponseHandler responseHandler = new BlockingTaskSummaryResponseHandler();
			client.getTasksOwned(idRef, "en-UK", responseHandler);
	        List<TaskSummary> tasks = responseHandler.getResults();
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
			BlockingTaskSummaryResponseHandler responseHandler = new BlockingTaskSummaryResponseHandler();
			List<String> roles = groupListMap.get(idRef);
			if (roles == null) {
				client.getTasksAssignedAsPotentialOwner(idRef, "en-UK", responseHandler);
			} else {
				client.getTasksAssignedAsPotentialOwner(idRef, roles, "en-UK", responseHandler);
			}
	        List<TaskSummary> tasks = responseHandler.getResults();
	        for (TaskSummary task: tasks) {
	        	result.add(Transform.task(task));
	        }
		} catch (Throwable t) {
			t.printStackTrace();
		}
		return result;
	}

}
