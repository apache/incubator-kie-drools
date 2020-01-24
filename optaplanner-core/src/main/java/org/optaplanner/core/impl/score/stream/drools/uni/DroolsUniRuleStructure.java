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
import org.drools.model.PatternDSL.PatternDef;
import org.drools.model.Variable;
import org.drools.model.view.ExprViewItem;
import org.drools.model.view.ViewItemBuilder;
import org.optaplanner.core.impl.score.stream.drools.common.DroolsPatternBuilder;
import org.optaplanner.core.impl.score.stream.drools.common.DroolsRuleStructure;

public class DroolsUniRuleStructure<A> extends DroolsRuleStructure {

    private final Variable<A> a;
    private final DroolsPatternBuilder<?> aPattern;
    private final List<ViewItemBuilder<?>> shelved;
    private final List<ViewItemBuilder<?>> prerequisites;
    private final List<ViewItemBuilder<?>> dependents;

    public DroolsUniRuleStructure(Variable<A> aVariable, DroolsPatternBuilder<?> aPattern,
            List<ViewItemBuilder<?>> shelved, List<ViewItemBuilder<?>> prerequisites,
            List<ViewItemBuilder<?>> dependents, LongSupplier variableIdSupplier) {
        super(variableIdSupplier);
        this.a = aVariable;
        this.aPattern = aPattern;
        this.shelved = Collections.unmodifiableList(prerequisites);
        this.prerequisites = Collections.unmodifiableList(shelved);
        this.dependents = Collections.unmodifiableList(dependents);
    }

    public DroolsUniRuleStructure(Class<A> aClass, LongSupplier varialeIdSupplier) {
        super(varialeIdSupplier);
        this.a = (Variable<A>) createVariable(aClass,"base");
        this.aPattern = new DroolsPatternBuilder<>(a);
        this.shelved = Collections.emptyList();
        this.prerequisites = Collections.emptyList();
        this.dependents = Collections.emptyList();
    }

    public Variable<A> getA() {
        return a;
    }

    @Override
    public List<ViewItemBuilder<?>> getShelvedRuleItems() {
        return shelved;
    }

    @Override
    public List<ViewItemBuilder<?>> getPrerequisites() {
        return prerequisites;
    }

    @Override
    public DroolsPatternBuilder<Object> getPrimaryPatternBuilder() {
        return (DroolsPatternBuilder<Object>) aPattern;
    }

    @Override
    public List<ViewItemBuilder<?>> getDependents() {
        return dependents;
    }

    public <B> DroolsUniRuleStructure<A> exists(PatternDef<B> existencePattern) {
        ExprViewItem item = PatternDSL.exists(existencePattern);
        return new DroolsUniRuleStructure<>(a, aPattern, shelved, prerequisites, mergeDependents(item),
                getVariableIdSupplier());
    }

    public DroolsUniRuleStructure<A> amend(UnaryOperator<PatternDef<Object>> expander) {
        return new DroolsUniRuleStructure<>(a, getPrimaryPatternBuilder().expand(expander), prerequisites, shelved,
                dependents, getVariableIdSupplier());
    }

}
