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

package org.drools.io.impl;

import java.util.Collection;
import java.util.Collections;

import org.drools.ChangeSet;
import org.drools.io.Resource;

public class ChangeSetImpl implements ChangeSet {
    private Collection<Resource> resourcesRemoved = Collections.<Resource>emptyList();
    private Collection<Resource> resourcesAdded = Collections.<Resource>emptyList();
    private Collection<Resource> resourcesModified = Collections.<Resource>emptyList();

    //Map of removed kdefinitions. The key is the resource and the string is
    //the knowledgeDefinition's name.
    private Collection<String>  knowledgeDefinitionsRemoved = Collections.<String>emptyList();
    
    public ChangeSetImpl() {
        
    }
    
    public void setResourcesRemoved(Collection<Resource> resourcesRemoved) {
        this.resourcesRemoved = resourcesRemoved;
    }

    public Collection<Resource> getResourcesRemoved() {
        return resourcesRemoved;
    }
    
    public void setResourcesAdded(Collection<Resource> resourcesAdded) {
        this.resourcesAdded = resourcesAdded;
    }

    public Collection<Resource> getResourcesAdded() {
        return resourcesAdded;
    }

    public Collection<Resource> getResourcesModified() {
        return resourcesModified;
    }

    public void setResourcesModified(Collection<Resource> resourcesModified) {
        this.resourcesModified = resourcesModified;
    }

    public Collection<String> getKnowledgeDefinitionsRemoved() {
        return knowledgeDefinitionsRemoved;
    }

    public void setKnowledgeDefinitionsRemoved(Collection<String> knowledgeDefinitionsRemoved) {
        this.knowledgeDefinitionsRemoved = knowledgeDefinitionsRemoved;
    }

    public boolean isEmpty(){
        return this.resourcesAdded.isEmpty() && 
                this.resourcesModified.isEmpty() && 
                this.resourcesRemoved.isEmpty() && 
                this.knowledgeDefinitionsRemoved.isEmpty();
    }
}
