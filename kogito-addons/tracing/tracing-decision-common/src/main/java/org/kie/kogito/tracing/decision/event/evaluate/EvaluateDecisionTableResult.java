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

import org.kie.dmn.api.core.event.AfterEvaluateDecisionTableEvent;
import org.kie.dmn.api.core.event.BeforeEvaluateDecisionTableEvent;

public class EvaluateDecisionTableResult {

    private String decisionTableName;
    private List<Integer> matches;
    private List<Integer> selected;

    private EvaluateDecisionTableResult(String decisionTableName, List<Integer> matches, List<Integer> selected) {
        this.decisionTableName = decisionTableName;
        this.matches = matches;
        this.selected = selected;
    }

    private EvaluateDecisionTableResult() {
    }

    public String getDecisionTableName() {
        return decisionTableName;
    }

    public List<Integer> getMatches() {
        return matches;
    }

    public List<Integer> getSelected() {
        return selected;
    }

    public static EvaluateDecisionTableResult from(BeforeEvaluateDecisionTableEvent event) {
        return new EvaluateDecisionTableResult(event.getDecisionTableName(), null, null);
    }

    public static EvaluateDecisionTableResult from(AfterEvaluateDecisionTableEvent event) {
        return new EvaluateDecisionTableResult(event.getDecisionTableName(), event.getMatches(), event.getSelected());
    }
}
