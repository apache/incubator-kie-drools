/*
 *  Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.kie.kogito.trusty.service.messaging;

import org.junit.jupiter.api.Test;
import org.kie.kogito.tracing.decision.event.trace.TraceEvent;
import org.kie.kogito.trusty.storage.api.model.Decision;

import static org.kie.kogito.trusty.service.TrustyServiceTestUtils.buildCorrectDecision;
import static org.kie.kogito.trusty.service.TrustyServiceTestUtils.buildCorrectTraceEvent;
import static org.kie.kogito.trusty.service.TrustyServiceTestUtils.buildDecisionWithErrors;
import static org.kie.kogito.trusty.service.TrustyServiceTestUtils.buildDecisionWithNullFields;
import static org.kie.kogito.trusty.service.TrustyServiceTestUtils.buildTraceEventWithErrors;
import static org.kie.kogito.trusty.service.TrustyServiceTestUtils.buildTraceEventWithNullFields;

class TraceEventConverterTest {

    @Test
    void testCorrectTraceEvent() {
        doTest(buildCorrectTraceEvent(), buildCorrectDecision());
    }

    @Test
    void testTraceEventWithError() {
        doTest(buildTraceEventWithErrors(), buildDecisionWithErrors());
    }

    @Test
    void testTraceEventWithNullFields() {
        doTest(buildTraceEventWithNullFields(), buildDecisionWithNullFields());
    }

    private static void doTest(TraceEvent traceEvent, Decision expectedDecision) {
        Decision actualDecision = TraceEventConverter.toDecision(traceEvent);
        TraceEventTestUtils.assertDecision(expectedDecision, actualDecision);
    }
}
