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

package org.jbpm.persistence.api.integration.base;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

import org.jbpm.persistence.api.integration.EventCollection;
import org.jbpm.persistence.api.integration.InstanceView;

/**
 * Base event collection that collects all events in LinkedHashSet to eliminate duplicates.
 * No extra filtering is performed.
 *
 */
public class BaseEventCollection implements EventCollection {

    private static final long serialVersionUID = -5241582057875657702L;
    private Set<InstanceView<?>> events = new LinkedHashSet<>();
    
    @Override
    public void update(InstanceView<?> item) {
        this.events.add(item);
    }
    
    @Override
    public void remove(InstanceView<?> item) {
        this.events.add(item);
    }
    
    @Override
    public Collection<InstanceView<?>> getEvents() {                
        return this.events;
    }
    
    @Override
    public void add(InstanceView<?> event) {
        this.events.add(event);
    }

}
