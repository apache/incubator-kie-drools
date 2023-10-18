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

import java.util.Map;

import org.jbpm.workflow.instance.node.HumanTaskNodeInstance;
import org.kie.api.event.usertask.UserTaskDeadlineEvent;
import org.kie.api.runtime.KieRuntime;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.kogito.process.workitem.HumanTaskWorkItem;

public class UserTaskDeadlineEventImpl extends UserTaskEventImpl implements UserTaskDeadlineEvent {
    private static final long serialVersionUID = 510l;

    private HumanTaskWorkItem workItem;
    private Map<String, Object> notification;
    private DeadlineType type;

    public UserTaskDeadlineEventImpl(ProcessInstance instance, HumanTaskNodeInstance humanTaskNodeInstance, HumanTaskWorkItem workItem,
            Map<String, Object> notification, DeadlineType type, KieRuntime kruntime, String identity) {
        super(instance, humanTaskNodeInstance, kruntime, identity);
        this.workItem = workItem;
        this.notification = notification;
        this.type = type;
    }

    @Override
    public Map<String, Object> getNotification() {
        return notification;
    }

    @Override
    public DeadlineType getType() {
        return type;
    }

    @Override
    public String toString() {
        return "HumanTaskDeadlineEventImpl [workItem=" + workItem + ", notification=" + notification + ", type=" +
                type + "]";
    }
}
