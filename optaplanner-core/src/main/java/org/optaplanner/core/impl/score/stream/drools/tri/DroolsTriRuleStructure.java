/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.optaplanner.core.impl.score.stream.drools.tri;

import java.util.Collections;
import java.util.List;
import java.util.function.LongSupplier;

import org.drools.model.RuleItemBuilder;
import org.drools.model.Variable;
import org.optaplanner.core.impl.score.stream.drools.bi.DroolsBiRuleStructure;
import org.optaplanner.core.impl.score.stream.drools.common.DroolsPatternBuilder;
import org.optaplanner.core.impl.score.stream.drools.common.DroolsRuleStructure;
import org.optaplanner.core.impl.score.stream.drools.uni.DroolsUniRuleStructure;

public class DroolsTriRuleStructure<A, B, C> extends DroolsRuleStructure {

    private final Variable<A> a;
    private final Variable<B> b;
    private final Variable<C> c;
    private final DroolsPatternBuilder<?> primaryPattern;
    private final List<RuleItemBuilder<?>> supportingRuleItems;

    /**
     * Builds a final version of the AB pattern as it will no longer be mutated, and turns the C pattern into the new
     * primary pattern.
     * @param abRuleStructure
     * @param cRuleStructure
     * @param variableIdSupplier
     */
    public DroolsTriRuleStructure(DroolsBiRuleStructure<A, B> abRuleStructure,
            DroolsUniRuleStructure<C> cRuleStructure, LongSupplier variableIdSupplier) {
        super(variableIdSupplier);
        this.a = abRuleStructure.getA();
        this.b = abRuleStructure.getB();
        this.c = cRuleStructure.getA();
        this.primaryPattern = cRuleStructure.getPrimaryPattern();
        /*
         * Assemble the new rule structure in the following order:
         * - First, the supporting rule items from abRuleStructure.
         * - Second, the primary pattern from abRuleStructure.
         * - And finally, the supporting rule items from cRuleStructure.
         *
         * This makes sure that left-hand side of the rule represented by this object is properly ordered.
         */
        List<RuleItemBuilder<?>> ruleItems =
                abRuleStructure.rebuildSupportingRuleItems(abRuleStructure.getPrimaryPattern().build());
        ruleItems.addAll(cRuleStructure.getSupportingRuleItems());
        this.supportingRuleItems = Collections.unmodifiableList(ruleItems);
    }

    public DroolsTriRuleStructure(Variable<A> aVariable, Variable<B> bVariable, final Variable<C> cVariable,
            DroolsPatternBuilder<?> primaryPattern, List<RuleItemBuilder<?>> supportingRuleItems,
            LongSupplier variableIdSupplier) {
        super(variableIdSupplier);
        this.a = aVariable;
        this.b = bVariable;
        this.c = cVariable;
        this.primaryPattern = primaryPattern;
        this.supportingRuleItems = supportingRuleItems;
    }

    public Variable<A> getA() {
        return a;
    }

    public Variable<B> getB() {
        return b;
    }

    public Variable<C> getC() {
        return c;
    }

    @Override
    public DroolsPatternBuilder<Object> getPrimaryPattern() {
        return (DroolsPatternBuilder<Object>) primaryPattern;
    }

    @Override
    public List<RuleItemBuilder<?>> getSupportingRuleItems() {
        return supportingRuleItems;
    }
}
