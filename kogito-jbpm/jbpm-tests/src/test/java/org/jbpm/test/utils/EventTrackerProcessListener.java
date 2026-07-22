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
package org.jbpm.test.utils;

import java.util.ArrayList;
import java.util.List;

import org.kie.api.event.process.ProcessNodeEvent;
import org.kie.api.event.process.ProcessNodeLeftEvent;
import org.kie.api.event.process.ProcessNodeTriggeredEvent;
import org.kie.kogito.internal.process.event.DefaultKogitoProcessEventListener;

public class EventTrackerProcessListener extends DefaultKogitoProcessEventListener {

    List<ProcessNodeEvent> nodeEvents;

    public EventTrackerProcessListener() {
        this.nodeEvents = new ArrayList<>();
    }

    @Override
    public void afterNodeTriggered(ProcessNodeTriggeredEvent event) {
        nodeEvents.add(event);
    }

    @Override
    public void afterNodeLeft(ProcessNodeLeftEvent event) {
        nodeEvents.add(event);
    }

    public List<ProcessNodeEvent> tracked() {
        return nodeEvents;
    }
}
