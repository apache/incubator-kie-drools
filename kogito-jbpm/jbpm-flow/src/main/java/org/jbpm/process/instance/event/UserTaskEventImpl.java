/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.jbpm.process.instance.event;

import java.util.Date;
import java.util.EventObject;

import org.jbpm.process.instance.impl.humantask.InternalHumanTaskWorkItem;
import org.jbpm.workflow.instance.node.HumanTaskNodeInstance;
import org.kie.api.event.usertask.UserTaskEvent;
import org.kie.api.runtime.KieRuntime;
import org.kie.api.runtime.process.ProcessInstance;

public class UserTaskEventImpl extends EventObject implements UserTaskEvent {

    private static final long serialVersionUID = 510l;

    private final KieRuntime kruntime;
    private final Date eventDate;
    private final String eventUser;

    private HumanTaskNodeInstance humanTaskNodeInstance;

    public UserTaskEventImpl(ProcessInstance instance, HumanTaskNodeInstance nodeInstance, KieRuntime kruntime) {
        this(instance, nodeInstance, kruntime, null);
    }

    public UserTaskEventImpl(ProcessInstance instance, HumanTaskNodeInstance nodeInstance, KieRuntime kruntime, String user) {
        super(instance);
        this.humanTaskNodeInstance = nodeInstance;
        this.kruntime = kruntime;
        this.eventDate = new Date();
        this.eventUser = user;
    }

    public ProcessInstance getProcessInstance() {
        return (ProcessInstance) getSource();
    }

    @Override
    public HumanTaskNodeInstance getNodeInstance() {
        return humanTaskNodeInstance;
    }

    @Override
    public InternalHumanTaskWorkItem getWorkItem() {
        return humanTaskNodeInstance.getWorkItem();
    }

    @Override
    public String getUserTaskId() {
        return getWorkItem().getStringId();
    }

    @Override
    public KieRuntime getKieRuntime() {
        return kruntime;
    }

    public HumanTaskNodeInstance getHumanTaskNodeInstance() {
        return humanTaskNodeInstance;
    }

    @Override
    public Date getEventDate() {
        return this.eventDate;
    }

    @Override
    public String getEventUser() {
        return eventUser;
    }

    @Override
    public String getUserTaskDefinitionId() {
        return getHumanTaskNodeInstance().getNodeDefinitionId();
    }

}
