/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.core.impl.score.stream.drools.uni;

import java.util.Collections;
import java.util.List;
import java.util.function.LongSupplier;
import java.util.function.UnaryOperator;

import org.drools.model.PatternDSL;
import org.drools.model.RuleItemBuilder;
import org.drools.model.Variable;
import org.optaplanner.core.impl.score.stream.drools.common.DroolsPatternBuilder;
import org.optaplanner.core.impl.score.stream.drools.common.DroolsRuleStructure;

public class DroolsUniRuleStructure<A> extends DroolsRuleStructure {

    private final Variable<A> a;
    private final DroolsPatternBuilder<?> aPattern;
    private final List<RuleItemBuilder<?>> openRuleItems;
    private final List<RuleItemBuilder<?>> closedRuleItems;

    public DroolsUniRuleStructure(Variable<A> aVariable, DroolsPatternBuilder<?> aPattern,
            List<RuleItemBuilder<?>> openRuleItems, List<RuleItemBuilder<?>> closedRuleItems,
            LongSupplier variableIdSupplier) {
        super(variableIdSupplier);
        this.a = aVariable;
        this.aPattern = aPattern;
        this.openRuleItems = Collections.unmodifiableList(openRuleItems);
        this.closedRuleItems = Collections.unmodifiableList(closedRuleItems);
    }

    public DroolsUniRuleStructure(Class<A> aClass, LongSupplier varialeIdSupplier) {
        super(varialeIdSupplier);
        this.a = (Variable<A>) createVariable(aClass,"base");
        this.aPattern = new DroolsPatternBuilder<>(a);
        this.openRuleItems = Collections.emptyList();
        this.closedRuleItems = Collections.emptyList();
    }

    public Variable<A> getA() {
        return a;
    }

    @Override
    public DroolsPatternBuilder<Object> getPrimaryPattern() {
        return (DroolsPatternBuilder<Object>) aPattern;
    }

    @Override
    public List<RuleItemBuilder<?>> getOpenRuleItems() {
        return openRuleItems;
    }

    @Override
    public List<RuleItemBuilder<?>> getClosedRuleItems() {
        return closedRuleItems;
    }

    /**
     * Add another condition on the primary pattern without changing anything else about the rule.
     *
     * @param expander
     * @return
     */
    public DroolsUniRuleStructure<A> amend(UnaryOperator<PatternDSL.PatternDef<Object>> expander) {
        return new DroolsUniRuleStructure<>(a, getPrimaryPattern().expand(expander), openRuleItems, closedRuleItems,
                getVariableIdSupplier());
    }

}
