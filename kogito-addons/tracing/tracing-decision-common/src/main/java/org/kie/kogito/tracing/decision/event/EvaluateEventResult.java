/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.kie.kogito.tracing.decision.event;

import java.util.List;

import org.kie.dmn.api.core.DMNResult;

import static org.kie.kogito.tracing.decision.event.EvaluateEventUtils.map;

public class EvaluateEventResult {

    private final List<EvaluateEventDecisionResult> decisionResults;
    private final List<EvaluateEventMessage> messages;

    public EvaluateEventResult(List<EvaluateEventDecisionResult> decisionResults, List<EvaluateEventMessage> messages) {
        this.decisionResults = decisionResults;
        this.messages = messages;
    }

    public static EvaluateEventResult from(DMNResult result) {
        return new EvaluateEventResult(
                map(result.getDecisionResults(), EvaluateEventDecisionResult::from),
                map(result.getMessages(), EvaluateEventMessage::from)
        );
    }

    public List<EvaluateEventDecisionResult> getDecisionResults() {
        return decisionResults;
    }

    public List<EvaluateEventMessage> getMessages() {
        return messages;
    }

}
