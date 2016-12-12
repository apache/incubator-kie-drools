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

package org.kie.dmn.core.ast;

import org.kie.dmn.core.api.DMNType;
import org.kie.dmn.feel.model.v1_1.Decision;

public class DecisionNode
        extends DMNBaseNode {

    private Decision               decision;
    private DMNExpressionEvaluator evaluator;
    private DMNType                resultType;

    public DecisionNode() {
    }

    public DecisionNode(Decision decision) {
        this( decision, null );
    }

    public DecisionNode(Decision decision, DMNType resultType) {
        super( decision );
        this.decision = decision;
        this.resultType = resultType;
    }

    public Decision getDecision() {
        return decision;
    }

    public void setDecision(Decision decision) {
        this.decision = decision;
    }

    public DMNExpressionEvaluator getEvaluator() {
        return evaluator;
    }

    public void setEvaluator(DMNExpressionEvaluator evaluator) {
        this.evaluator = evaluator;
    }

    public DMNType getResultType() {
        return resultType;
    }

    public void setResultType(DMNType resultType) {
        this.resultType = resultType;
    }
}
