/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.dmn.core.impl;

import org.kie.dmn.api.core.DMNResult;
import org.kie.dmn.api.core.ast.DecisionNode;
import org.kie.dmn.api.core.event.AfterEvaluateDecisionEvent;
import org.kie.dmn.api.core.event.BeforeEvaluateDecisionEvent;

public class AfterEvaluateDecisionEventImpl
        implements AfterEvaluateDecisionEvent {

    private DecisionNode decision;
    private DMNResult result;
    private BeforeEvaluateDecisionEvent before;


    public AfterEvaluateDecisionEventImpl(DecisionNode decision, DMNResult result, BeforeEvaluateDecisionEvent beforeEvent) {
        this.decision = decision;
        this.result = result;
        this.before = beforeEvent;
    }

    @Override
    public DecisionNode getDecision() {
        return this.decision;
    }

    @Override
    public DMNResult getResult() {
        return this.result;
    }

    public BeforeEvaluateDecisionEvent getBeforeEvent() {
        return before;
    }

    @Override
    public String toString() {
        return "AfterEvaluateDecisionEvent{ name='"+decision.getName()+"' id='"+decision.getId()+"' }";
    }

}
