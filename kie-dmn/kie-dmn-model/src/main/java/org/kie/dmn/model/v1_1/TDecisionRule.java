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
package org.kie.dmn.model.v1_1;

import java.util.ArrayList;
import java.util.List;

import org.kie.dmn.model.api.DecisionRule;
import org.kie.dmn.model.api.LiteralExpression;
import org.kie.dmn.model.api.RuleAnnotation;
import org.kie.dmn.model.api.UnaryTests;

public class TDecisionRule extends TDMNElement implements DecisionRule {

    private List<UnaryTests> inputEntry;
    private List<LiteralExpression> outputEntry;

    @Override
    public List<UnaryTests> getInputEntry() {
        if ( inputEntry == null ) {
            inputEntry = new ArrayList<>();
        }
        return this.inputEntry;
    }

    @Override
    public List<LiteralExpression> getOutputEntry() {
        if ( outputEntry == null ) {
            outputEntry = new ArrayList<>();
        }
        return this.outputEntry;
    }

    @Override
    public List<RuleAnnotation> getAnnotationEntry() {
        throw new UnsupportedOperationException("Not on DMN v1.1");
    }

}
