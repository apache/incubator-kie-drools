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
package org.jbpm.workflow.core.node;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import org.jbpm.process.core.event.EventTypeFilter;
import org.jbpm.process.core.timer.Timer;
import org.jbpm.workflow.core.DroolsAction;
import org.kie.api.definition.process.Node;

public class EventSubProcessNode extends CompositeContextNode {

    private static final long serialVersionUID = 2200928773922042238L;

    private List<String> events = new ArrayList<String>();
    private List<EventTypeFilter> eventTypeFilters = new ArrayList<EventTypeFilter>();
    private boolean keepActive = true;
    
    public void addEvent(EventTypeFilter filter) {
        String type = filter.getType();
        this.events.add(type);
        this.eventTypeFilters.add(filter);
    }
    
    public List<String> getEvents() {
        return events;
    }

    public boolean isKeepActive() {
        return keepActive;
    }

    public void setKeepActive(boolean triggerOnActivation) {
        this.keepActive = triggerOnActivation;
    }
    
    public StartNode findStartNode() {
        for (Node node: getNodes()) {
            if (node instanceof StartNode) {
                StartNode startNode = (StartNode) node;                                    
                return startNode;                           
            }
        }
        return null;
    }

    @Override
    public void addTimer(Timer timer, DroolsAction action) {
        super.addTimer(timer, action);
        if (timer.getTimeType() == Timer.TIME_CYCLE) {
            setKeepActive(false);
        }
    }

    @Override
    public boolean acceptsEvent(String type, Object event) { 
        for( EventTypeFilter filter : this.eventTypeFilters ) { 
            if( filter.acceptsEvent(type, event) ) { 
                return true;
            }
        }
        return super.acceptsEvent(type, event);
    }

    @Override
    public boolean acceptsEvent(String type, Object event, Function<String, String> resolver) {
        if (resolver == null) {
            return acceptsEvent(type, event);
        }
        
        for( EventTypeFilter filter : this.eventTypeFilters ) { 
            if( filter.acceptsEvent(type, event, resolver) ) { 
                return true;
            }
        }
        return super.acceptsEvent(type, event);
    }
    
}

