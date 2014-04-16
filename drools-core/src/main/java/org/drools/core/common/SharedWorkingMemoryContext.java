/*
 * Copyright 2010 JBoss Inc
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

package org.drools.core.common;

import java.util.LinkedList;
import java.util.List;

import org.drools.core.base.MapGlobalResolver;
import org.drools.core.event.AgendaEventSupport;
import org.drools.core.event.RuleRuntimeEventSupport;
import org.drools.core.process.instance.WorkItemManager;
import org.drools.core.spi.FactHandleFactory;
import org.drools.core.spi.GlobalResolver;

public class SharedWorkingMemoryContext {
    protected FactHandleFactory                 handleFactory;

    /** Global values which are associated with this memory. */
    protected GlobalResolver                    globalResolver;

    /** The eventSupport */
    protected RuleRuntimeEventSupport workingMemoryEventSupport;

    protected AgendaEventSupport                agendaEventSupport;

    protected List                              __ruleBaseEventListeners;

    protected long                              propagationIdCounter;

    private WorkItemManager                     workItemManager;

    public SharedWorkingMemoryContext(FactHandleFactory handleFactory) {
        this.handleFactory = handleFactory;

        this.globalResolver = new MapGlobalResolver();

        this.workingMemoryEventSupport = new RuleRuntimeEventSupport();
        this.agendaEventSupport = new AgendaEventSupport();
        this.__ruleBaseEventListeners = new LinkedList();
    }

    public WorkItemManager getWorkItemManager() {
        return workItemManager;
    }

    public void setWorkItemManager(WorkItemManager workItemManager) {
        this.workItemManager = workItemManager;
    }

    public FactHandleFactory getHandleFactory() {
        return handleFactory;
    }

    public GlobalResolver getGlobalResolver() {
        return globalResolver;
    }

    public RuleRuntimeEventSupport getWorkingMemoryEventSupport() {
        return workingMemoryEventSupport;
    }

    public AgendaEventSupport getAgendaEventSupport() {
        return agendaEventSupport;
    }

    public List get__ruleBaseEventListeners() {
        return __ruleBaseEventListeners;
    }

}
