/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jbpm.process.instance.event;

import java.util.Map;

import org.drools.core.event.ProcessEvent;
import org.kie.api.runtime.KieRuntime;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.kogito.internal.process.event.HumanTaskDeadlineEvent;
import org.kie.kogito.process.workitem.HumanTaskWorkItem;

public class HumanTaskDeadlineEventImpl extends ProcessEvent
        implements HumanTaskDeadlineEvent {
    private static final long serialVersionUID = 510l;

    private HumanTaskWorkItem workItem;
    private Map<String, Object> notification;
    private DeadlineType type;

    public HumanTaskDeadlineEventImpl(final ProcessInstance instance, HumanTaskWorkItem workItem,
            Map<String, Object> notification, DeadlineType type, KieRuntime kruntime) {
        super(instance, kruntime);
        this.workItem = workItem;
        this.notification = notification;
        this.type = type;
    }

    @Override
    public HumanTaskWorkItem getWorkItem() {
        return workItem;
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
