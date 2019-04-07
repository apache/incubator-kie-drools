/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.persistence.processinstance.objects;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.jbpm.persistence.api.integration.EventCollection;
import org.jbpm.persistence.api.integration.EventEmitter;
import org.jbpm.persistence.api.integration.InstanceView;
import org.jbpm.persistence.api.integration.base.BaseEventCollection;


public class TestEventEmitter implements EventEmitter {


    private static Set<InstanceView<?>> events = new LinkedHashSet<>();
    
    @Override
    public void deliver(Collection<InstanceView<?>> data) {
        // no-op here as it is used to hook into active transaction
    }

    @Override
    public void apply(Collection<InstanceView<?>> data) {
        // store events generated
        events.addAll(data);
    }

    @Override
    public void drop(Collection<InstanceView<?>> data) {
        // no-op here are this impl reacts only on the apply method

    }

    @Override
    public EventCollection newCollection() {
        return new BaseEventCollection();
    }
    
    public static List<InstanceView<?>> getEvents() {
        List<InstanceView<?>> currentEvents = new ArrayList<>(events);
        events.clear();
        return currentEvents;
    }
    
    public static void clear() {
        events.clear();
    }

    @Override
    public void close() {
        // no-op
    }

}
