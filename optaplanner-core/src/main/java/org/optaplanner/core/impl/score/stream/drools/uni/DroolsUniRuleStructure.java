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
import org.optaplanner.core.impl.score.stream.drools.bi.DroolsBiRuleStructure;
import org.optaplanner.core.impl.score.stream.drools.common.DroolsPatternBuilder;
import org.optaplanner.core.impl.score.stream.drools.common.DroolsRuleStructure;

public final class DroolsUniRuleStructure<A, PatternVar> extends DroolsRuleStructure<PatternVar> {

    private final Variable<A> a;
    private final DroolsPatternBuilder<PatternVar> aPattern;
    private final List<ViewItemBuilder<?>> shelved;
    private final List<ViewItemBuilder<?>> prerequisites;
    private final List<ViewItemBuilder<?>> dependents;

    public DroolsUniRuleStructure(Variable<A> aVariable, DroolsPatternBuilder<PatternVar> aPattern,
            List<ViewItemBuilder<?>> shelved, List<ViewItemBuilder<?>> prerequisites,
            List<ViewItemBuilder<?>> dependents, LongSupplier variableIdSupplier) {
        super(variableIdSupplier);
        this.a = aVariable;
        this.aPattern = aPattern;
        this.shelved = Collections.unmodifiableList(shelved);
        this.prerequisites = Collections.unmodifiableList(prerequisites);
        this.dependents = Collections.unmodifiableList(dependents);
    }

    public DroolsUniRuleStructure(DroolsBiRuleStructure<A, ?, PatternVar> biRuleStructure) {
        super(biRuleStructure.getVariableIdSupplier());
        this.a = biRuleStructure.getA();
        this.aPattern = biRuleStructure.getPrimaryPatternBuilder();
        this.shelved = biRuleStructure.getShelvedRuleItems();
        this.prerequisites = biRuleStructure.getPrerequisites();
        this.dependents = biRuleStructure.getDependents();
    }

    public DroolsUniRuleStructure(Class<A> aClass, LongSupplier varialeIdSupplier) {
        super(varialeIdSupplier);
        this.a = (Variable<A>) createVariable(aClass, "base");
        this.aPattern = new DroolsPatternBuilder<>((Variable<PatternVar>) a);
        this.shelved = Collections.emptyList();
        this.prerequisites = Collections.emptyList();
        this.dependents = Collections.emptyList();
    }

    public <B> DroolsUniRuleStructure<A, PatternVar> existsOrNot(PatternDef<B> existencePattern, boolean shouldExist) {
        ExprViewItem item = PatternDSL.exists(existencePattern);
        if (!shouldExist) {
            item = PatternDSL.not(item);
        }
        return new DroolsUniRuleStructure<>(a, aPattern, shelved, prerequisites, mergeDependents(item),
                getVariableIdSupplier());
    }

    public DroolsUniRuleStructure<A, PatternVar> amend(UnaryOperator<PatternDef<PatternVar>> expander) {
        return new DroolsUniRuleStructure<>(a, getPrimaryPatternBuilder().expand(expander), shelved, prerequisites,
                dependents, getVariableIdSupplier());
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
    public DroolsPatternBuilder<PatternVar> getPrimaryPatternBuilder() {
        return aPattern;
    }

    @Override
    public List<ViewItemBuilder<?>> getDependents() {
        return dependents;
    }

    @Override
    protected Class[] getVariableTypes() {
        return new Class[] { a.getType() };
    }

}
