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

package org.jbpm.casemgmt.impl.util;

import java.util.LinkedList;
import java.util.Queue;
import org.jbpm.casemgmt.api.event.CaseCancelEvent;
import org.jbpm.casemgmt.api.event.CaseCloseEvent;
import org.jbpm.casemgmt.api.event.CaseCommentEvent;
import org.jbpm.casemgmt.api.event.CaseDataEvent;
import org.jbpm.casemgmt.api.event.CaseDestroyEvent;
import org.jbpm.casemgmt.api.event.CaseDynamicSubprocessEvent;
import org.jbpm.casemgmt.api.event.CaseDynamicTaskEvent;
import org.jbpm.casemgmt.api.event.CaseEvent;
import org.jbpm.casemgmt.api.event.CaseEventListener;
import org.jbpm.casemgmt.api.event.CaseReopenEvent;
import org.jbpm.casemgmt.api.event.CaseRoleAssignmentEvent;
import org.jbpm.casemgmt.api.event.CaseStartEvent;
import org.jbpm.casemgmt.impl.util.RecordingCaseEventListener.CaseEventInfo.EventFired;

public class RecordingCaseEventListener implements CaseEventListener {

    private Queue<CaseEventInfo> events = new LinkedList<>();

    public Queue<CaseEventInfo> getEvents() {
        return events;
    }

    @Override
    public void beforeCaseStarted(final CaseStartEvent event) {
        events.add(new CaseEventInfo(EventFired.BEFORE, event));
    }

    @Override
    public void afterCaseStarted(final CaseStartEvent event) {
        events.add(new CaseEventInfo(EventFired.AFTER, event));
    }

    @Override
    public void beforeCaseClosed(final CaseCloseEvent event) {
        events.add(new CaseEventInfo(EventFired.BEFORE, event));
    }

    @Override
    public void afterCaseClosed(final CaseCloseEvent event) {
        events.add(new CaseEventInfo(EventFired.AFTER, event));
    }

    @Override
    public void beforeCaseCancelled(final CaseCancelEvent event) {
        events.add(new CaseEventInfo(EventFired.BEFORE, event));
    }

    @Override
    public void afterCaseCancelled(final CaseCancelEvent event) {
        events.add(new CaseEventInfo(EventFired.AFTER, event));
    }

    @Override
    public void beforeCaseDestroyed(final CaseDestroyEvent event) {
        events.add(new CaseEventInfo(EventFired.BEFORE, event));
    }

    @Override
    public void afterCaseDestroyed(final CaseDestroyEvent event) {
        events.add(new CaseEventInfo(EventFired.AFTER, event));
    }

    @Override
    public void beforeCaseReopen(final CaseReopenEvent event) {
        events.add(new CaseEventInfo(EventFired.BEFORE, event));
    }

    @Override
    public void afterCaseReopen(final CaseReopenEvent event) {
        events.add(new CaseEventInfo(EventFired.AFTER, event));
    }

    @Override
    public void beforeCaseCommentAdded(final CaseCommentEvent event) {
        events.add(new CaseEventInfo(EventFired.BEFORE, event));
    }

    @Override
    public void afterCaseCommentAdded(final CaseCommentEvent event) {
        events.add(new CaseEventInfo(EventFired.AFTER, event));
    }

    @Override
    public void beforeCaseCommentUpdated(final CaseCommentEvent event) {
        events.add(new CaseEventInfo(EventFired.BEFORE, event));
    }

    @Override
    public void afterCaseCommentUpdated(final CaseCommentEvent event) {
        events.add(new CaseEventInfo(EventFired.AFTER, event));
    }

    @Override
    public void beforeCaseCommentRemoved(final CaseCommentEvent event) {
        events.add(new CaseEventInfo(EventFired.BEFORE, event));
    }

    @Override
    public void afterCaseCommentRemoved(final CaseCommentEvent event) {
        events.add(new CaseEventInfo(EventFired.AFTER, event));
    }

    @Override
    public void beforeCaseRoleAssignmentAdded(final CaseRoleAssignmentEvent event) {
        events.add(new CaseEventInfo(EventFired.BEFORE, event));
    }

    @Override
    public void afterCaseRoleAssignmentAdded(final CaseRoleAssignmentEvent event) {
        events.add(new CaseEventInfo(EventFired.AFTER, event));
    }

    @Override
    public void beforeCaseRoleAssignmentRemoved(final CaseRoleAssignmentEvent event) {
        events.add(new CaseEventInfo(EventFired.BEFORE, event));
    }

    @Override
    public void afterCaseRoleAssignmentRemoved(final CaseRoleAssignmentEvent event) {
        events.add(new CaseEventInfo(EventFired.AFTER, event));
    }

    @Override
    public void beforeCaseDataAdded(final CaseDataEvent event) {
        events.add(new CaseEventInfo(EventFired.BEFORE, event));
    }

    @Override
    public void afterCaseDataAdded(final CaseDataEvent event) {
        events.add(new CaseEventInfo(EventFired.AFTER, event));
    }

    @Override
    public void beforeCaseDataRemoved(final CaseDataEvent event) {
        events.add(new CaseEventInfo(EventFired.BEFORE, event));
    }

    @Override
    public void afterCaseDataRemoved(final CaseDataEvent event) {
        events.add(new CaseEventInfo(EventFired.AFTER, event));
    }

    @Override
    public void beforeDynamicTaskAdded(final CaseDynamicTaskEvent event) {
        events.add(new CaseEventInfo(EventFired.BEFORE, event));
    }

    @Override
    public void afterDynamicTaskAdded(final CaseDynamicTaskEvent event) {
        events.add(new CaseEventInfo(EventFired.AFTER, event));
    }

    @Override
    public void beforeDynamicProcessAdded(final CaseDynamicSubprocessEvent event) {
        events.add(new CaseEventInfo(EventFired.BEFORE, event));
    }

    @Override
    public void afterDynamicProcessAdded(final CaseDynamicSubprocessEvent event) {
        events.add(new CaseEventInfo(EventFired.AFTER, event));
    }
    
    public static class CaseEventInfo {
        
        public enum EventFired {
            BEFORE,
            AFTER
        }
        
        public CaseEventInfo(EventFired fired, CaseEvent caseEvent) {
            this.fired = fired;
            this.caseEvent = caseEvent;
        }
        
        public EventFired fired;
        public CaseEvent caseEvent;
    }
}
