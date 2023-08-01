/*
 * Copyright 2005 Red Hat, Inc. and/or its affiliates.
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

import java.util.Date;
import java.util.EventObject;

import org.kie.api.runtime.KieRuntime;
import org.kie.api.runtime.process.ProcessInstance;

public class ProcessEvent extends EventObject {

    private static final long serialVersionUID = 510l;

    private final KieRuntime kruntime;
    private final Date eventDate;
    private final String eventIdentity;

    public ProcessEvent(final ProcessInstance instance, final KieRuntime kruntime) {
        this(instance, kruntime, null);
    }

    public ProcessEvent(final ProcessInstance instance, final KieRuntime kruntime, final String identity) {
        super(instance);
        this.kruntime = kruntime;
        this.eventDate = new Date();
        this.eventIdentity = identity;
    }

    public ProcessInstance getProcessInstance() {
        return (ProcessInstance) getSource();
    }

    public KieRuntime getKieRuntime() {
        return kruntime;
    }

    public Date getEventDate() {
        return this.eventDate;
    }

    public String getEventIdentity() {
        return eventIdentity;
    }
}
