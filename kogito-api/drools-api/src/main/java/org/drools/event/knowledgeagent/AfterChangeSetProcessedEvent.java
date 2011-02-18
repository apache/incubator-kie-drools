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

package org.drools.event.knowledgeagent;

import java.util.List;
import java.util.Map;
import java.util.Set;
import org.drools.ChangeSet;
import org.drools.definition.KnowledgeDefinition;
import org.drools.io.Resource;

/**
 *
 * @author esteban
 */
public class AfterChangeSetProcessedEvent extends ChangeSetProcessingEvent {

    private static final long serialVersionUID = 510l;
    private final List<Resource> addedResources;
    private final Map<Resource, Set<KnowledgeDefinition>> modifiedResourceMappings;
    private final Map<Resource, Set<KnowledgeDefinition>> removedResourceMappings;

    public AfterChangeSetProcessedEvent(ChangeSet changeSet, List<Resource> addedResources, Map<Resource, Set<KnowledgeDefinition>> modifiedResourceMappings, Map<Resource, Set<KnowledgeDefinition>> removedResourceMappings) {
        super(changeSet);
        this.addedResources = addedResources;
        this.modifiedResourceMappings = modifiedResourceMappings;
        this.removedResourceMappings = removedResourceMappings;
    }

    public List<Resource> getAddedResources() {
        return addedResources;
    }

    public Map<Resource, Set<KnowledgeDefinition>> getModifiedResourceMappings() {
        return modifiedResourceMappings;
    }

    public Map<Resource, Set<KnowledgeDefinition>> getRemovedResourceMappings() {
        return removedResourceMappings;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    @Override
    public String toString() {
        return "==>[AfterChangeSetProcessedEvent: " + getChangeSet() + "]";
    }
}
