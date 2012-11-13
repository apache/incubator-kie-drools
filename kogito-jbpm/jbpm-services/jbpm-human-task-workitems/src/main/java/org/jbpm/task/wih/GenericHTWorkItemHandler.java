/*
 * Copyright 2012 JBoss by Red Hat.
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
package org.jbpm.task.wih;

import java.util.Date;

import org.jbpm.task.utils.OnErrorAction;
import org.kie.runtime.KnowledgeRuntime;
import org.jbpm.task.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.kie.runtime.process.WorkItem;
import org.kie.runtime.process.WorkItemManager;
import org.jbpm.task.ContentData;
import org.jbpm.task.api.TaskServiceEntryPoint;
import org.jbpm.task.exception.PermissionDeniedException;
import org.jbpm.task.impl.factories.TaskFactory;
import org.jbpm.task.lifecycle.listeners.TaskLifeCycleEventListener;

public class GenericHTWorkItemHandler extends AbstractHTWorkItemHandler {

    private static final Logger logger = LoggerFactory.getLogger(GenericHTWorkItemHandler.class);
    private TaskServiceEntryPoint client;
    private String ipAddress;
    private int port;
    private boolean local = false;
    private boolean connected = false;
    private ClassLoader classLoader;
    private TaskLifeCycleEventListener taskLifeCycleEventListener;

    public GenericHTWorkItemHandler(KnowledgeRuntime session, OnErrorAction action) {
        super(session, action);
    }

    public GenericHTWorkItemHandler(TaskServiceEntryPoint client, KnowledgeRuntime session, OnErrorAction action) {
        super(session, action);
        this.client = client;
    }

    public GenericHTWorkItemHandler(TaskServiceEntryPoint client, KnowledgeRuntime session, OnErrorAction action, ClassLoader classLoader) {
        super(session, action);
        this.client = client;
        this.classLoader = classLoader;
    }

    public GenericHTWorkItemHandler(TaskServiceEntryPoint client, KnowledgeRuntime session) {
        super(session);
        this.client = client;
    }

    public GenericHTWorkItemHandler(KnowledgeRuntime session) {
        super(session);
    }

    public TaskServiceEntryPoint getClient() {
        return client;
    }

    public void setClient(TaskServiceEntryPoint client) {
        this.client = client;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setLocal(boolean local) {
        this.local = local;
    }

    public boolean isConnected() {
        return connected;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public boolean isLocal() {
        return local;
    }

    public int getPort() {
        return port;
    }

    public ClassLoader getClassLoader() {
        return classLoader;
    }

    public void setClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

   

    public void init() {
//    public void connect() {
//        if (client == null) {
//            throw new IllegalStateException("You must set the Task Service Client to the work item to work");
//        }
//        if (ipAddress == null || ipAddress.equals("") || port <= 0) {
//            throw new IllegalStateException("You must set the IP and Port to the work item to work");
//        }
//        if (client != null) {
//            if(!connected){
//                connected = client.connect(ipAddress, port);
//                if (!connected) {
//                    throw new IllegalArgumentException("Could not connect task client: on ip: "+ipAddress +" - port: "+port);
//                }
        //registerTaskEvents();
        taskLifeCycleEventListener = client.getTaskLifeCycleEventListener();
        ((ExternalTaskEventListener)taskLifeCycleEventListener).setSession(session);
        ((ExternalTaskEventListener)taskLifeCycleEventListener).setTaskService(client);
        //            }
        //        }

    }

    public void dispose() throws Exception {
        
//        if (client != null) {
//            client.disconnect();
//        }

    }

    @Override
    public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
        Task task = createTaskBasedOnWorkItemParams(workItem);
        TaskFactory.initializeTask(task);
        ContentData content = createTaskContentBasedOnWorkItemParams(workItem);
//        connect();
        try {
            client.addTask(task, content);
        } catch (Exception e) {
            if (action.equals(OnErrorAction.ABORT)) {
                manager.abortWorkItem(workItem.getId());
            } else if (action.equals(OnErrorAction.RETHROW)) {
                if (e instanceof RuntimeException) {
                    throw (RuntimeException) e;
                } else {
                    throw new RuntimeException(e);
                }
            } else if (action.equals(OnErrorAction.LOG)) {
                StringBuffer logMsg = new StringBuffer();
                logMsg.append(new Date() + ": Error when creating task on task server for work item id " + workItem.getId());
                logMsg.append(". Error reported by task server: " + e.getMessage());
                logger.error(logMsg.toString(), e);
            }
        }
    }

    @Override
    public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
        Task task = client.getTaskByWorkItemId(workItem.getId());
        if (task != null) {
            try {
                client.exit(task.getId(), "Administrator");
            } catch (PermissionDeniedException e) {
                logger.info(e.getMessage());
            }
        }
    }

    
}
