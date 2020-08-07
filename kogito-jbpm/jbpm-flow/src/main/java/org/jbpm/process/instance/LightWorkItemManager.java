/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.process.instance;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.drools.core.KogitoWorkItemHandlerNotFoundException;
import org.drools.core.event.KogitoProcessEventSupport;
import org.drools.core.event.ProcessEventSupport;
import org.drools.core.process.instance.KogitoWorkItem;
import org.drools.core.process.instance.KogitoWorkItemManager;
import org.drools.core.process.instance.WorkItem;
import org.drools.core.process.instance.impl.KogitoWorkItemImpl;
import org.jbpm.process.instance.impl.workitem.Abort;
import org.jbpm.process.instance.impl.workitem.Active;
import org.jbpm.process.instance.impl.workitem.Complete;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemNotFoundException;
import org.kie.internal.runtime.Closeable;
import org.kie.kogito.process.workitem.NotAuthorizedException;
import org.kie.kogito.process.workitem.Policy;
import org.kie.kogito.process.workitem.Transition;
import org.kie.kogito.signal.SignalManager;

import static org.jbpm.process.instance.impl.workitem.Abort.ID;
import static org.jbpm.process.instance.impl.workitem.Abort.STATUS;
import static org.kie.api.runtime.process.WorkItem.ABORTED;
import static org.kie.api.runtime.process.WorkItem.COMPLETED;

public class LightWorkItemManager implements KogitoWorkItemManager {
 
    private Map<String, KogitoWorkItem> workItems = new ConcurrentHashMap<>();
    private Map<String, WorkItemHandler> workItemHandlers = new HashMap<>();

    private final ProcessInstanceManager processInstanceManager;
    private final SignalManager signalManager;
    private final KogitoProcessEventSupport eventSupport;
    
    private Complete completePhase = new Complete();
    private Abort abortPhase = new Abort();

    public LightWorkItemManager(ProcessInstanceManager processInstanceManager, SignalManager signalManager, ProcessEventSupport eventSupport) {
        this.processInstanceManager = processInstanceManager;
        this.signalManager = signalManager;
        this.eventSupport = (KogitoProcessEventSupport) eventSupport;
    }

    public void internalExecuteWorkItem( KogitoWorkItem workItem) {
        (( KogitoWorkItemImpl ) workItem).setId(UUID.randomUUID().toString());
        internalAddWorkItem(workItem);
        WorkItemHandler handler = this.workItemHandlers.get(workItem.getName());
        if (handler != null) {
            ProcessInstance processInstance = processInstanceManager.getProcessInstance(workItem.getProcessInstanceId());
            Transition<?> transition = new TransitionToActive();
            eventSupport.fireBeforeWorkItemTransition(processInstance, workItem, transition, null);
            
            handler.executeWorkItem(workItem, this);

            eventSupport.fireAfterWorkItemTransition(processInstance, workItem, transition, null);
        } else throw new KogitoWorkItemHandlerNotFoundException(workItem.getName() );
    }    

    public void internalAddWorkItem( KogitoWorkItem workItem) {
        workItems.put(workItem.getId(), workItem);  
    }

    public void internalAbortWorkItem(String id) {
        KogitoWorkItemImpl workItem = ( KogitoWorkItemImpl ) workItems.get(id);
        // work item may have been aborted
        if (workItem != null) {
            workItem.setCompleteDate(new Date());
            WorkItemHandler handler = this.workItemHandlers.get(workItem.getName());
            if (handler != null) {
                
                ProcessInstance processInstance = processInstanceManager.getProcessInstance(workItem.getProcessInstanceId());
                Transition<?> transition = new TransitionToAbort(Collections.emptyList());
                eventSupport.fireBeforeWorkItemTransition(processInstance, workItem, transition, null);
                
                handler.abortWorkItem(workItem, this);
                workItem.setPhaseId(ID);
                workItem.setPhaseStatus(STATUS);
                eventSupport.fireAfterWorkItemTransition(processInstance, workItem, transition, null);
            } else {
                workItems.remove( workItem.getId() );
                throw new KogitoWorkItemHandlerNotFoundException(workItem.getName() );
            }
            workItems.remove(workItem.getId());
        }
    }

    public void retryWorkItem(String workItemId) {
    	KogitoWorkItem workItem = workItems.get(workItemId);
    	retryWorkItem(workItem);
    }

    public void retryWorkItemWithParams(String workItemId,Map<String,Object> map) {
        KogitoWorkItem workItem = workItems.get(workItemId);
        
        if ( workItem != null ) {
            workItem.setParameters( map );
            
            retryWorkItem( workItem );
        }
    }
    
    private void retryWorkItem( KogitoWorkItem workItem) {
        if (workItem != null) {
            WorkItemHandler handler = this.workItemHandlers.get(workItem.getName());
            if (handler != null) {
                handler.executeWorkItem(workItem, this);
            } else throw new KogitoWorkItemHandlerNotFoundException(workItem.getName() );
        }
    }
    
    public KogitoWorkItem getWorkItem( String id) {
        return workItems.get(id);
    }

    public void completeWorkItem(String id, Map<String, Object> results, Policy<?>... policies) {
        transitionWorkItem(id, new TransitionToComplete(results, Arrays.asList(policies)));
    }
    
    public void internalCompleteWorkItem( KogitoWorkItem workItem) {
        ProcessInstance processInstance = processInstanceManager.getProcessInstance(workItem.getProcessInstanceId());                    
        workItem.setState(COMPLETED);
        workItem.setCompleteDate(new Date());
                
        // process instance may have finished already
        if (processInstance != null) {
            processInstance.signalEvent("workItemCompleted", workItem);
        }
        workItems.remove(workItem.getId());
 
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public void transitionWorkItem(String id, Transition<?> transition) {
        KogitoWorkItem workItem = workItems.get(id);
        // work item may have been aborted
        if (workItem != null) {
                        
            WorkItemHandler handler = this.workItemHandlers.get(workItem.getName());
            if (handler != null) {
                ProcessInstance processInstance = processInstanceManager.getProcessInstance(workItem.getProcessInstanceId());
                eventSupport.fireBeforeWorkItemTransition(processInstance, workItem, transition, null);
                
                try {
                    handler.transitionToPhase(workItem, this, transition);
                } catch (UnsupportedOperationException e) {
                    workItem.setResults((Map<String, Object>)transition.data()); 
                    workItem.setPhaseId(Complete.ID);
                    workItem.setPhaseStatus(Complete.STATUS);
                    completePhase.apply(workItem, transition);
                    internalCompleteWorkItem(workItem);                                        
                }

                eventSupport.fireAfterWorkItemTransition(processInstance, workItem, transition, null);
            } else {
                throw new KogitoWorkItemHandlerNotFoundException(workItem.getName() );
            }
                
        } else {
            throw new WorkItemNotFoundException("Work Item (" + id + ") does not exist", id);
        }
    }

    public void abortWorkItem(String id, Policy<?>... policies) {
        KogitoWorkItemImpl workItem = ( KogitoWorkItemImpl ) workItems.get(id);
        // work item may have been aborted
        if (workItem != null) {
            if (!workItem.enforce(policies)) {
                throw new NotAuthorizedException("Work item can be aborted as it does not fulfil policies (e.g. security)");
            }
            ProcessInstance processInstance = processInstanceManager.getProcessInstance(workItem.getProcessInstanceId());
            Transition<?> transition = new TransitionToAbort(Arrays.asList(policies));
            eventSupport.fireBeforeWorkItemTransition(processInstance, workItem, transition, null);
            workItem.setState(ABORTED);
            abortPhase.apply(workItem, transition);
            
            // process instance may have finished already
            if (processInstance != null) {
                processInstance.signalEvent("workItemAborted", workItem);
            }
            workItem.setPhaseId(ID);
            workItem.setPhaseStatus(STATUS);
            eventSupport.fireAfterWorkItemTransition(processInstance, workItem, transition, null);
            workItems.remove(id);
        }
    }

    public void registerWorkItemHandler(String workItemName, WorkItemHandler handler) {
        this.workItemHandlers.put(workItemName, handler);
    }

    @Override
    public void internalExecuteWorkItem( WorkItem workItem ) {
        throw new UnsupportedOperationException();

    }

    @Override
    public void internalAddWorkItem( WorkItem workItem ) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void internalAbortWorkItem( long id ) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Set<WorkItem> getWorkItems() {
        throw new UnsupportedOperationException();
    }

    @Override
    public WorkItem getWorkItem( long id ) {
        throw new UnsupportedOperationException();
    }

    public void clear() {
        this.workItems.clear();
    }
    
    public void signalEvent(String type, Object event) { 
        this.signalManager.signalEvent(type, event);
    }

    @Override
    public void signalEvent( String type, Object event, long processInstanceId ) {
        throw new UnsupportedOperationException( "org.jbpm.process.instance.LightWorkItemManager.signalEvent -> TODO" );

    }

    public void signalEvent(String type, Object event, String processInstanceId) { 
        this.signalManager.signalEvent(processInstanceId, type, event);
    }

    @Override
    public void dispose() {
        if (workItemHandlers != null) {
            for (Map.Entry<String, WorkItemHandler> handlerEntry : workItemHandlers.entrySet()) {
                if (handlerEntry.getValue() instanceof Closeable) {
                    ((Closeable) handlerEntry.getValue()).close();
                }
            }
        }
    }

    @Override
    public void retryWorkItem( Long workItemID, Map<String, Object> params ) {
        throw new UnsupportedOperationException( "org.jbpm.process.instance.LightWorkItemManager.retryWorkItem -> TODO" );

    }

    @Override
    public void retryWorkItem( String workItemID, Map<String, Object> params ) {
       if(params==null || params.isEmpty()){
           retryWorkItem(workItemID);
       }else{
           this.retryWorkItemWithParams( workItemID, params );
       }
        
    }
    
    private static class TransitionToActive implements Transition<Void> {

        @Override
        public String phase() {
            return Active.ID;
        }

        @Override
        public Void data() {
            return null;
        }

        @Override
        public List<Policy<?>> policies() {
            return Collections.emptyList();
        }       
    }
    
    private static class TransitionToAbort implements Transition<Void> {

        private List<Policy<?>> policies;
        
        TransitionToAbort(List<Policy<?>> policies) {
            this.policies = policies;
        }
        
        @Override
        public String phase() {
            return ID;
        }

        @Override
        public Void data() {
            return null;
        }

        @Override
        public List<Policy<?>> policies() {
            return policies;
        }       
    }
    
    private static class TransitionToComplete implements Transition<Map<String, Object>> {
        private Map<String, Object> data;
        private List<Policy<?>> policies;
        
        TransitionToComplete(Map<String, Object> data, List<Policy<?>> policies) {
            this.data = data;
            this.policies = policies;
        }
        @Override
        public String phase() {
            return Complete.ID;
        }

        @Override
        public Map<String, Object> data() {
            return data;
        }

        @Override
        public List<Policy<?>> policies() {
            return policies;
        }       
    }
}
