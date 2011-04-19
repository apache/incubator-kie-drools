/*
 * Copyright 2011 JBoss Inc..
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
package org.drools.agent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import org.drools.builder.KnowledgeBuilderError;
import org.drools.builder.KnowledgeBuilderErrors;
import org.drools.event.knowledgeagent.AfterChangeSetAppliedEvent;
import org.drools.event.knowledgeagent.AfterChangeSetProcessedEvent;
import org.drools.event.knowledgeagent.AfterResourceProcessedEvent;
import org.drools.event.knowledgeagent.BeforeChangeSetAppliedEvent;
import org.drools.event.knowledgeagent.BeforeChangeSetProcessedEvent;
import org.drools.event.knowledgeagent.BeforeResourceProcessedEvent;
import org.drools.event.knowledgeagent.KnowledgeAgentEventListener;
import org.drools.event.knowledgeagent.KnowledgeBaseUpdatedEvent;
import org.drools.event.knowledgeagent.ResourceCompilationFailedEvent;
import org.drools.io.Resource;
import org.drools.io.impl.ChangeSetImpl;

/**
 * Helper class to programmatically add and remove Resources from a change-set.
 * This class also has the possibility to apply its internal change-set to
 * a kagent.
 */
public class ChangeSetHelperImpl {
    
    /**
     * The internal ChangeSet
     */
    private ChangeSetImpl changeSet;

    /**
     * Creates a new instance of <code>ChangeSetHelperImpl</code> 
     */
    public ChangeSetHelperImpl() {
        this.changeSet = new ChangeSetImpl();
    }
    
    /**
     * Adds a new Resource to the internal ChangeSet
     * @param resource the resource to be added as New
     */
    public void addNewResource(Resource resource){
        this.changeSet.setResourcesAdded(
                this.addResourceToCollection(
                this.changeSet.getResourcesAdded(), resource));
    }
    
    /** 
     * Adds a Resource as modified. This method should only be used if 
     * ResourceChangeScanner is not running. 
     * If ResourceChangeScanner and ResourceChangeNotifier are running, 
     * the kagent is going to be automatically notified when the resource 
     * changes.
     * @param resource the resource to be added as Modified
     */
    public void addModifiedResource(Resource resource){
        this.changeSet.setResourcesModified(
                this.addResourceToCollection(
                this.changeSet.getResourcesModified(), resource));
    }
    
    
    /**
     * Adds a Resource as removed. Use this method if you want to de-subscribe
     * an agent from a Resource.
     * @param resource the resource to be added as Removed
     */
    public void addRemovedResource(Resource resource){
        this.changeSet.setResourcesRemoved(
                this.addResourceToCollection(
                this.changeSet.getResourcesRemoved(), resource));
    }
    
    /**
     * Synchronously apply the internal ChangeSet to a KnowledgeAgent.
     * If the internal ChangeSet is empty, the agent is not even bothered.
     * If the agent fails while compiling the resources of the ChangeSet, a 
     * RuntimeException is thrown.
     * After the ChangeSet is applied, it is reseted. The Knowledge Agent
     * should have added the corresponding listeners to monitor the resources by
     * its own.
     * @param kagent 
     * @throws RuntimeException if the agent fails while compiling the resources.
     */
    public void applyChangeSet(KnowledgeAgent kagent){
        
        if (changeSet.isEmpty()){
            return;
        }
        
        MyKnowledgeAgentEventListener kagentEventListener = new MyKnowledgeAgentEventListener();
        try{
            kagent.addEventListener(kagentEventListener);
            kagent.applyChangeSet(changeSet);
            if (kagentEventListener.hasCompilationErrors()){
                throw new RuntimeException(kagentEventListener.getCompilationErrorsMessage());
            }
        } finally{
            kagent.removeEventListener(kagentEventListener);
        }
        
        //the internal change-set is reset
        this.reset();
    }
    
    /**
     * Reset the internal ChangeSet
     */
    public void reset(){
        this.changeSet = new ChangeSetImpl();
    }

    /**
     * Returns the internal ChangeSet
     * @return the internal ChangeSet
     */
    public ChangeSetImpl getChangeSet() {
        return changeSet;
    }

    /**
     * Creates a new Collection containing all elements of resources and
     * resource. It checks if resources is null.
     * @param resources
     * @param resource 
     */
    private Collection<Resource> addResourceToCollection(Collection<Resource> resources, Resource resource){
        Collection<Resource> newCollection = new ArrayList<Resource>();
        if (resources != null){
            newCollection.addAll(resources); 
        }
        newCollection.add(resource);
        
        return newCollection;
    }
    
    private class MyKnowledgeAgentEventListener implements KnowledgeAgentEventListener{

        private List<KnowledgeBuilderErrors> compilationErrors = new ArrayList<KnowledgeBuilderErrors>();
        
        public void beforeChangeSetApplied(BeforeChangeSetAppliedEvent event) {
        }

        public void afterChangeSetApplied(AfterChangeSetAppliedEvent event) {
        }

        public void beforeChangeSetProcessed(BeforeChangeSetProcessedEvent event) {
        }

        public void afterChangeSetProcessed(AfterChangeSetProcessedEvent event) {
        }

        public void beforeResourceProcessed(BeforeResourceProcessedEvent event) {
        }

        public void afterResourceProcessed(AfterResourceProcessedEvent event) {
        }

        public void knowledgeBaseUpdated(KnowledgeBaseUpdatedEvent event) {
        }

        public void resourceCompilationFailed(ResourceCompilationFailedEvent event) {
            this.compilationErrors.add(event.getKnowledgeBuilder().getErrors());
        }
        
        public boolean hasCompilationErrors(){
            return !this.compilationErrors.isEmpty();
        }
        
        public String getCompilationErrorsMessage(){
            StringBuilder message = new StringBuilder("");
            if (this.hasCompilationErrors()){
                for (KnowledgeBuilderErrors knowledgeBuilderErrors : compilationErrors) {
                    Iterator<KnowledgeBuilderError> iterator = knowledgeBuilderErrors.iterator();
                    while (iterator.hasNext()) {
                        KnowledgeBuilderError error = iterator.next();
                        message.append("Compilation error: ");
                        message.append(error.getMessage());
                        message.append("\n");
                    }
                }
            }
            return message.toString();
        }
    }
    
}
