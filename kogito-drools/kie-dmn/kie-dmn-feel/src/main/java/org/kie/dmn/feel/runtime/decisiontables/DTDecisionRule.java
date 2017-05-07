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

package org.kie.dmn.feel.runtime.decisiontables;

import org.kie.dmn.feel.lang.CompiledExpression;
import org.kie.dmn.feel.runtime.UnaryTest;
import org.kie.dmn.model.v1_1.LiteralExpression;

import java.util.ArrayList;
import java.util.List;

/**
8.3.3 Decision Rule metamodel

The class DecisionRule is used to model the rules in a decision table (see 8.2 Notation).

An instance of DecisionRule has an ordered list of inputEntry instances which are instances of UnaryTests,
and an ordered list of outputEntry instances, which are instances of LiteralExpression.

By definition, a DecisionRule element that has no inputEntrys is always applicable. Otherwise, an instance of
DecisionRule is said to be applicable if and only if, at least one of the rule's inputEntrys match their
corresponding inputExpression value.

The inputEntrys are matched in arbitrary order.

The inputEntry elements SHALL be in the same order as the containing DecisionTable's inputs.
The i
th inputExpression must satisfy the i
th inputEntry for all inputEntrys in order for the
DecisionRule to match, as defined in 8.1 Introduction.

The outputEntry elements SHALL be in the same order as the containing DecisionTable's outputs.

     The i
th outputEntry SHALL be consistent with the typeRef of the i
th OutputClause.
 */
public class DTDecisionRule {
    private int                      index;
    private List<UnaryTest>          inputEntry;
    private List<CompiledExpression> outputEntry;

    public DTDecisionRule(int index) {
        this.index = index;
    }

    /**
The instances of UnaryTests that specify the input conditions
that this DecisionRule must match for the corresponding (by
index) inputExpression. 
     */
    public List<UnaryTest> getInputEntry() {
        if ( inputEntry == null ) {
            inputEntry = new ArrayList<>();
        }
        return this.inputEntry;
    }

    /**
A list of the instances of LiteralExpression that compose
the output components of this DecisionRule. 
     * @return
     */
    public List<CompiledExpression> getOutputEntry() {
        if ( outputEntry == null ) {
            outputEntry = new ArrayList<>();
        }
        return this.outputEntry;
    }

    public int getIndex() {
        return index;
    }
}
