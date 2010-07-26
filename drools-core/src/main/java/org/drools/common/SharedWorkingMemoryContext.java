/**
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

package org.drools.common;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.drools.base.MapGlobalResolver;
import org.drools.event.AgendaEventSupport;
import org.drools.event.RuleFlowEventSupport;
import org.drools.event.WorkingMemoryEventSupport;
import org.drools.process.instance.ProcessInstanceFactory;
import org.drools.process.instance.WorkItemManager;
import org.drools.rule.TimeMachine;
import org.drools.spi.FactHandleFactory;
import org.drools.spi.GlobalResolver;

public class SharedWorkingMemoryContext {
    protected InternalRuleBase                  ruleBase;

    protected FactHandleFactory                 handleFactory;

    /** Global values which are associated with this memory. */
    protected GlobalResolver                    globalResolver;

    /** The eventSupport */
    protected WorkingMemoryEventSupport         workingMemoryEventSupport;

    protected AgendaEventSupport                agendaEventSupport;

    protected RuleFlowEventSupport              workflowEventSupport;

    protected List                              __ruleBaseEventListeners;

    protected long                              propagationIdCounter;

    private Map                                 processInstances;

    private int                                 processCounter;

    private WorkItemManager                     workItemManager;

    private Map<String, ProcessInstanceFactory> processInstanceFactories;

    private TimeMachine                         timeMachine;

    public SharedWorkingMemoryContext(FactHandleFactory handleFactory) {
        this.handleFactory = handleFactory;

        this.globalResolver = new MapGlobalResolver();

        this.workingMemoryEventSupport = new WorkingMemoryEventSupport();
        this.agendaEventSupport = new AgendaEventSupport();
        this.workflowEventSupport = new RuleFlowEventSupport();
        this.__ruleBaseEventListeners = new LinkedList();

        processInstanceFactories = new HashMap();

        timeMachine = new TimeMachine();
    }

    public Map getProcessInstances() {
        return processInstances;
    }

    public void setProcessInstances(Map processInstances) {
        this.processInstances = processInstances;
    }

    public WorkItemManager getWorkItemManager() {
        return workItemManager;
    }

    public void setWorkItemManager(WorkItemManager workItemManager) {
        this.workItemManager = workItemManager;
    }

    public TimeMachine getTimeMachine() {
        return timeMachine;
    }

    public void setTimeMachine(TimeMachine timeMachine) {
        this.timeMachine = timeMachine;
    }

    public FactHandleFactory getHandleFactory() {
        return handleFactory;
    }

    public GlobalResolver getGlobalResolver() {
        return globalResolver;
    }

    public WorkingMemoryEventSupport getWorkingMemoryEventSupport() {
        return workingMemoryEventSupport;
    }

    public AgendaEventSupport getAgendaEventSupport() {
        return agendaEventSupport;
    }

    public RuleFlowEventSupport getWorkflowEventSupport() {
        return workflowEventSupport;
    }

    public List get__ruleBaseEventListeners() {
        return __ruleBaseEventListeners;
    }

    public int getProcessCounter() {
        return processCounter;
    }

    public Map<String, ProcessInstanceFactory> getProcessInstanceFactories() {
        return processInstanceFactories;
    }

}
