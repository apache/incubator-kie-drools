/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.kogito.tracing.decision.event.evaluate;

import java.util.List;

import org.kie.dmn.api.core.DMNResult;
import org.kie.kogito.tracing.decision.event.EventUtils;
import org.kie.kogito.tracing.event.message.Message;

import static org.kie.kogito.tracing.decision.event.EventUtils.map;

public class EvaluateResult {

    private List<EvaluateDecisionResult> decisionResults;
    private List<Message> messages;

    public EvaluateResult(List<EvaluateDecisionResult> decisionResults, List<Message> messages) {
        this.decisionResults = decisionResults;
        this.messages = messages;
    }

    private EvaluateResult() {
    }

    public List<EvaluateDecisionResult> getDecisionResults() {
        return decisionResults;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public static EvaluateResult from(DMNResult result) {
        return new EvaluateResult(
                map(result.getDecisionResults(), EvaluateDecisionResult::from),
                map(result.getMessages(), EventUtils::messageFrom));
    }
}
