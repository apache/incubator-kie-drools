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

package org.jbpm.casemgmt.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.jbpm.casemgmt.api.event.CaseCommentEvent;
import org.jbpm.casemgmt.api.event.CaseEvent;
import org.jbpm.casemgmt.api.event.CaseStartEvent;
import org.jbpm.casemgmt.api.model.instance.CommentInstance;
import org.jbpm.casemgmt.impl.util.AbstractCaseServicesBaseTest;
import org.jbpm.casemgmt.impl.util.RecordingCaseEventListener;
import org.jbpm.casemgmt.impl.util.RecordingCaseEventListenerFactory;
import org.jbpm.casemgmt.impl.util.RecordingCaseEventListener.CaseEventInfo;
import org.jbpm.casemgmt.impl.util.RecordingCaseEventListener.CaseEventInfo.EventFired;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.runtime.query.QueryContext;

public class CaseEventEmittingTest extends AbstractCaseServicesBaseTest {

    private List<String> caseIds = new ArrayList<>();

    private RecordingCaseEventListener caseEventListener =
            RecordingCaseEventListenerFactory.get(getClass().getSimpleName());

    @Override
    protected List<String> getProcessDefinitionFiles() {
        return Arrays.asList(
            "cases/EmptyCase.bpmn2"
        );
    }

    @Before
    public void setUp() throws Exception {
        registerListenerMvelDefinition("org.jbpm.casemgmt.impl.util.RecordingCaseEventListenerFactory.get(\""
                + getClass().getSimpleName() + "\")");
        super.setUp();
    }

    @Test
    public void testCaseCommentEventsEmitting() {
        String caseId = caseService.startCase(deploymentUnit.getIdentifier(), EMPTY_CASE_P_ID);
        caseIds.add(caseId);

        assertNextEvent(EventFired.BEFORE, CaseStartEvent.class);
        assertNextEvent(EventFired.AFTER, CaseStartEvent.class);

        caseService.addCaseComment(caseId, "no one", "My little comment");
        String commentId = getFirstCommentId(caseId);
        assertNextEvent(EventFired.BEFORE, CaseCommentEvent.class);
        assertNextEvent(EventFired.AFTER, CaseCommentEvent.class);

        caseService.updateCaseComment(caseId, commentId, "no one","My new comment text");
        assertNextEvent(EventFired.BEFORE, CaseCommentEvent.class);
        assertNextEvent(EventFired.AFTER, CaseCommentEvent.class);

        caseService.removeCaseComment(caseId, commentId);
        assertNextEvent(EventFired.BEFORE, CaseCommentEvent.class);
        assertNextEvent(EventFired.AFTER, CaseCommentEvent.class);
    }

    private String getFirstCommentId(String caseId) {
        return caseService.getCaseComments(caseId, new QueryContext())
                .stream()
                .findFirst()
                .map(CommentInstance::getId)
                .orElseThrow(() -> new IllegalArgumentException("Single comment expected to be retrieved"));
    }

    private void assertNextEvent(EventFired expectedToBeFired, Class<? extends CaseEvent> eventType) {
        CaseEventInfo caseEventInfo = caseEventListener.getEvents().remove();

        Assertions.assertThat(caseEventInfo.fired).isEqualTo(expectedToBeFired);
        Assertions.assertThat(caseEventInfo.caseEvent).isInstanceOf(eventType);
    }
}
