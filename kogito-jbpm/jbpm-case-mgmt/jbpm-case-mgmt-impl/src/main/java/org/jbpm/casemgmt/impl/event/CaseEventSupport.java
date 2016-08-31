/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.jbpm.casemgmt.impl.event;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.drools.core.event.AbstractEventSupport;
import org.jbpm.casemgmt.api.event.CaseCancelEvent;
import org.jbpm.casemgmt.api.event.CaseCommentEvent;
import org.jbpm.casemgmt.api.event.CaseDataEvent;
import org.jbpm.casemgmt.api.event.CaseDestroyEvent;
import org.jbpm.casemgmt.api.event.CaseDynamicSubprocessEvent;
import org.jbpm.casemgmt.api.event.CaseDynamicTaskEvent;
import org.jbpm.casemgmt.api.event.CaseEventListener;
import org.jbpm.casemgmt.api.event.CaseRoleAssignmentEvent;
import org.jbpm.casemgmt.api.event.CaseStartEvent;
import org.jbpm.casemgmt.api.model.instance.CaseFileInstance;
import org.jbpm.casemgmt.api.model.instance.CommentInstance;
import org.kie.api.task.model.OrganizationalEntity;

/**
 * Responsible for firing case related events to notify registered CaseEventListeners 
 *
 */
public class CaseEventSupport extends AbstractEventSupport<CaseEventListener> {

    public CaseEventSupport(List<CaseEventListener> caseEventListeners) {
        if (caseEventListeners != null) {
            caseEventListeners.forEach( cvl -> addEventListener(cvl));
        }
    }
    
    /*
     * fire*CaseStarted
     */
    
    public void fireBeforeCaseStarted(String caseId, String deploymentId, String caseDefinitionId, CaseFileInstance caseFile) {
        final Iterator<CaseEventListener> iter = getEventListenersIterator();

        if (iter.hasNext()) {
            final CaseStartEvent event = new CaseStartEvent(caseId, deploymentId, caseDefinitionId, caseFile);

            do{
                iter.next().beforeCaseStarted(event);
            } while (iter.hasNext());
        }
    }

    public void fireAfterCaseStarted(String caseId, String deploymentId, String caseDefinitionId, CaseFileInstance caseFile, long processInstanceId) {
        final Iterator<CaseEventListener> iter = getEventListenersIterator();

        if (iter.hasNext()) {
            final CaseStartEvent event = new CaseStartEvent(caseId, deploymentId, caseDefinitionId, caseFile, processInstanceId);

            do {
                iter.next().afterCaseStarted(event);
            } while (iter.hasNext());
        }
    }
    
    /*
     * fire*CaseCancelled
     */
    public void fireBeforeCaseCancelled(String caseId, List<Long> processInstanceIds) {
        final Iterator<CaseEventListener> iter = getEventListenersIterator();

        if (iter.hasNext()) {
            final CaseCancelEvent event = new CaseCancelEvent(caseId, processInstanceIds);

            do{
                iter.next().beforeCaseCancelled(event);
            } while (iter.hasNext());
        }
    }

    public void fireAfterCaseCancelled(String caseId, List<Long> processInstanceIds) {
        final Iterator<CaseEventListener> iter = getEventListenersIterator();

        if (iter.hasNext()) {
            final CaseCancelEvent event = new CaseCancelEvent(caseId, processInstanceIds);

            do {
                iter.next().afterCaseCancelled(event);
            } while (iter.hasNext());
        }
    }
    
    /*
     * fire*CaseDestroyed
     */
    public void fireBeforeCaseDestroyed(String caseId, List<Long> processInstanceIds) {
        final Iterator<CaseEventListener> iter = getEventListenersIterator();

        if (iter.hasNext()) {
            final CaseDestroyEvent event = new CaseDestroyEvent(caseId, processInstanceIds);

            do{
                iter.next().beforeCaseDestroyed(event);
            } while (iter.hasNext());
        }
    }

    public void fireAfterCaseDestroyed(String caseId, List<Long> processInstanceIds) {
        final Iterator<CaseEventListener> iter = getEventListenersIterator();

        if (iter.hasNext()) {
            final CaseDestroyEvent event = new CaseDestroyEvent(caseId, processInstanceIds);

            do {
                iter.next().afterCaseDestroyed(event);
            } while (iter.hasNext());
        }
    }   
    
    /*
     * fire*CaseCommentAdded
     */
    public void fireBeforeCaseCommentAdded(String caseId, CommentInstance commentInstance) {
        final Iterator<CaseEventListener> iter = getEventListenersIterator();

        if (iter.hasNext()) {
            final CaseCommentEvent event = new CaseCommentEvent(caseId, commentInstance);

            do {
                iter.next().beforeCaseCommentAdded(event);
            } while (iter.hasNext());
        }        
    }
    
    public void fireAfterCaseCommentAdded(String caseId, CommentInstance commentInstance) {
        final Iterator<CaseEventListener> iter = getEventListenersIterator();

        if (iter.hasNext()) {
            final CaseCommentEvent event = new CaseCommentEvent(caseId, commentInstance);

            do {
                iter.next().beforeCaseCommentAdded(event);
            } while (iter.hasNext());
        }        
    }
    
    /*
     * fire*CaseCommentUpdated
     */
    public void fireBeforeCaseCommentUpdated(String caseId, CommentInstance commentInstance) {
        final Iterator<CaseEventListener> iter = getEventListenersIterator();

        if (iter.hasNext()) {
            final CaseCommentEvent event = new CaseCommentEvent(caseId, commentInstance);

            do {
                iter.next().beforeCaseCommentUpdated(event);
            } while (iter.hasNext());
        }        
    }
    
    public void fireAfterCaseCommentUpdated(String caseId, CommentInstance commentInstance) {
        final Iterator<CaseEventListener> iter = getEventListenersIterator();

        if (iter.hasNext()) {
            final CaseCommentEvent event = new CaseCommentEvent(caseId, commentInstance);

            do {
                iter.next().beforeCaseCommentUpdated(event);
            } while (iter.hasNext());
        }        
    }
    
    /*
     * fire*CaseCommentRemoved
     */
    public void fireBeforeCaseCommentRemoved(String caseId, CommentInstance commentInstance) {
        final Iterator<CaseEventListener> iter = getEventListenersIterator();

        if (iter.hasNext()) {
            final CaseCommentEvent event = new CaseCommentEvent(caseId, commentInstance);

            do {
                iter.next().beforeCaseCommentRemoved(event);
            } while (iter.hasNext());
        }        
    }
    
    public void fireAfterCaseCommentRemoved(String caseId, CommentInstance commentInstance) {
        final Iterator<CaseEventListener> iter = getEventListenersIterator();

        if (iter.hasNext()) {
            final CaseCommentEvent event = new CaseCommentEvent(caseId, commentInstance);

            do {
                iter.next().beforeCaseCommentRemoved(event);
            } while (iter.hasNext());
        }        
    }
    
    /*
     * fire*CaseRoleAssignmentAdded
     */
    public void fireBeforeCaseRoleAssignmentAdded(String caseId, String role, OrganizationalEntity entity) {
        final Iterator<CaseEventListener> iter = getEventListenersIterator();

        if (iter.hasNext()) {
            final CaseRoleAssignmentEvent event = new CaseRoleAssignmentEvent(caseId, role, entity);

            do {
                iter.next().beforeCaseRoleAssignmentAdded(event);
            } while (iter.hasNext());
        }        
    }
    
    public void fireAfterCaseRoleAssignmentAdded(String caseId, String role, OrganizationalEntity entity) {
        final Iterator<CaseEventListener> iter = getEventListenersIterator();

        if (iter.hasNext()) {
            final CaseRoleAssignmentEvent event = new CaseRoleAssignmentEvent(caseId, role, entity);

            do {
                iter.next().afterCaseRoleAssignmentAdded(event);
            } while (iter.hasNext());
        }        
    }
    
    /*
     * fire*CaseRoleAssignmentRemoved
     */
    public void fireBeforeCaseRoleAssignmentRemoved(String caseId, String role, OrganizationalEntity entity) {
        final Iterator<CaseEventListener> iter = getEventListenersIterator();

        if (iter.hasNext()) {
            final CaseRoleAssignmentEvent event = new CaseRoleAssignmentEvent(caseId, role, entity);

            do {
                iter.next().beforeCaseRoleAssignmentRemoved(event);
            } while (iter.hasNext());
        }        
    }
    
    public void fireAfterCaseRoleAssignmentRemoved(String caseId, String role, OrganizationalEntity entity) {
        final Iterator<CaseEventListener> iter = getEventListenersIterator();

        if (iter.hasNext()) {
            final CaseRoleAssignmentEvent event = new CaseRoleAssignmentEvent(caseId, role, entity);

            do {
                iter.next().afterCaseRoleAssignmentRemoved(event);
            } while (iter.hasNext());
        }        
    }

    /*
     * fire*CaseDataAdded
     */
    public void fireBeforeCaseDataAdded(String caseId, Map<String, Object> data) {
        final Iterator<CaseEventListener> iter = getEventListenersIterator();

        if (iter.hasNext()) {
            final CaseDataEvent event = new CaseDataEvent(caseId, data);

            do {
                iter.next().beforeCaseDataAdded(event);
            } while (iter.hasNext());
        }        
    }
    
    public void fireAfterCaseDataAdded(String caseId, Map<String, Object> data) {
        final Iterator<CaseEventListener> iter = getEventListenersIterator();

        if (iter.hasNext()) {
            final CaseDataEvent event = new CaseDataEvent(caseId, data);

            do {
                iter.next().afterCaseDataAdded(event);
            } while (iter.hasNext());
        }        
    }
    
    /*
     * fire*CaseDataRemoved
     */
    public void fireBeforeCaseDataRemoved(String caseId, Map<String, Object> data) {
        final Iterator<CaseEventListener> iter = getEventListenersIterator();

        if (iter.hasNext()) {
            final CaseDataEvent event = new CaseDataEvent(caseId, data);

            do {
                iter.next().beforeCaseDataRemoved(event);
            } while (iter.hasNext());
        }        
    }
    
    public void fireAfterCaseDataRemoved(String caseId, Map<String, Object> data) {
        final Iterator<CaseEventListener> iter = getEventListenersIterator();

        if (iter.hasNext()) {
            final CaseDataEvent event = new CaseDataEvent(caseId, data);

            do {
                iter.next().afterCaseDataRemoved(event);
            } while (iter.hasNext());
        }        
    }
    
    /*
     * fire*CaseDynamicTaskAdded
     */
    public void fireBeforeDynamicTaskAdded(String caseId, long processInstanceId, String nodeType, Map<String, Object> parameters) {
        final Iterator<CaseEventListener> iter = getEventListenersIterator();

        if (iter.hasNext()) {
            final CaseDynamicTaskEvent event = new CaseDynamicTaskEvent(caseId, nodeType, parameters, processInstanceId);

            do {
                iter.next().beforeDynamicTaskAdded(event);
            } while (iter.hasNext());
        }        
    }
    
    public void fireAfterDynamicTaskAdded(String caseId, long processInstanceId, String nodeType, Map<String, Object> parameters) {
        final Iterator<CaseEventListener> iter = getEventListenersIterator();

        if (iter.hasNext()) {
            final CaseDynamicTaskEvent event = new CaseDynamicTaskEvent(caseId, nodeType, parameters, processInstanceId);

            do {
                iter.next().afterDynamicTaskAdded(event);
            } while (iter.hasNext());
        }        
    }
    
    /*
     * fire*CaseDynamicProcessAdded
     */
    public void fireBeforeDynamicProcessAdded(String caseId, long processInstanceId, String processId, Map<String, Object> parameters) {
        final Iterator<CaseEventListener> iter = getEventListenersIterator();

        if (iter.hasNext()) {
            final CaseDynamicSubprocessEvent event = new CaseDynamicSubprocessEvent(caseId, processId, parameters, processInstanceId);

            do {
                iter.next().beforeDynamicProcessAdded(event);
            } while (iter.hasNext());
        }        
    }
    
    public void fireAfterDynamicProcessAdded(String caseId, long processInstanceId, String processId, Map<String, Object> parameters, long subProcessInstanceId) {
        final Iterator<CaseEventListener> iter = getEventListenersIterator();

        if (iter.hasNext()) {
            final CaseDynamicSubprocessEvent event = new CaseDynamicSubprocessEvent(caseId, processId, parameters, processInstanceId, subProcessInstanceId);

            do {
                iter.next().afterDynamicProcessAdded(event);
            } while (iter.hasNext());
        }        
    }  
    public void reset() {
        this.clear();
    }
}
