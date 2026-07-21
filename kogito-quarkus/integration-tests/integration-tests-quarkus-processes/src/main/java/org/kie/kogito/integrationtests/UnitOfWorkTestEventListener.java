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
package org.kie.kogito.integrationtests;

import java.util.ArrayList;
import java.util.List;

import org.kie.kogito.uow.events.UnitOfWorkAbortEvent;
import org.kie.kogito.uow.events.UnitOfWorkEndEvent;
import org.kie.kogito.uow.events.UnitOfWorkEventListener;
import org.kie.kogito.uow.events.UnitOfWorkStartEvent;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class UnitOfWorkTestEventListener implements UnitOfWorkEventListener {

    private List<UnitOfWorkStartEvent> startEvents = new ArrayList<>();
    private List<UnitOfWorkEndEvent> endEvents = new ArrayList<>();
    private List<UnitOfWorkAbortEvent> abortEvents = new ArrayList<>();

    @Override
    public void onBeforeStartEvent(UnitOfWorkStartEvent event) {
        this.startEvents.add(event);
    }

    @Override
    public void onAfterEndEvent(UnitOfWorkEndEvent event) {
        this.endEvents.add(event);
    }

    @Override
    public void onAfterAbortEvent(UnitOfWorkAbortEvent event) {
        this.abortEvents.add(event);
    }

    public void reset() {
        startEvents.clear();
        endEvents.clear();
        abortEvents.clear();
    }

    public List<UnitOfWorkStartEvent> getStartEvents() {
        return startEvents;
    }

    public List<UnitOfWorkEndEvent> getEndEvents() {
        return endEvents;
    }

    public List<UnitOfWorkAbortEvent> getAbortEvents() {
        return abortEvents;
    }
}
