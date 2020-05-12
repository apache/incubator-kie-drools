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

package org.optaplanner.core.impl.score.stream.drools.bi;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.LongSupplier;
import java.util.stream.Stream;

import org.drools.model.Argument;
import org.drools.model.DSL;
import org.drools.model.PatternDSL;
import org.drools.model.Variable;
import org.drools.model.view.ExprViewItem;
import org.drools.model.view.ViewItemBuilder;
import org.optaplanner.core.impl.score.stream.drools.common.DroolsPatternBuilder;
import org.optaplanner.core.impl.score.stream.drools.common.DroolsRuleStructure;
import org.optaplanner.core.impl.score.stream.drools.tri.DroolsTriRuleStructure;
import org.optaplanner.core.impl.score.stream.drools.uni.DroolsUniRuleStructure;

public final class DroolsBiRuleStructure<A, B, PatternVar> extends DroolsRuleStructure<PatternVar> {

    private final Variable<A> a;
    private final Variable<B> b;
    private final DroolsPatternBuilder<PatternVar> primaryPattern;
    private final List<ViewItemBuilder<?>> shelved;
    private final List<ViewItemBuilder<?>> prerequisites;
    private final List<ViewItemBuilder<?>> dependents;

    public <APatternVar> DroolsBiRuleStructure(DroolsUniRuleStructure<A, APatternVar> aRuleStructure,
            DroolsUniRuleStructure<B, PatternVar> bRuleStructure, LongSupplier variableIdSupplier) {
        super(variableIdSupplier);
        this.a = aRuleStructure.getA();
        this.b = bRuleStructure.getA();
        this.primaryPattern = bRuleStructure.getPrimaryPatternBuilder();
        List<ViewItemBuilder<?>> newShelved = new ArrayList<>(aRuleStructure.getShelvedRuleItems());
        newShelved.addAll(bRuleStructure.getShelvedRuleItems());
        this.shelved = Collections.unmodifiableList(newShelved);
        List<ViewItemBuilder<?>> newPrerequisites = new ArrayList<>(aRuleStructure.getPrerequisites());
        newPrerequisites.add(aRuleStructure.getPrimaryPatternBuilder().build());
        newPrerequisites.addAll(aRuleStructure.getDependents());
        newPrerequisites.addAll(bRuleStructure.getPrerequisites());
        this.prerequisites = Collections.unmodifiableList(newPrerequisites);
        this.dependents = Collections.unmodifiableList(bRuleStructure.getDependents());
    }

    public DroolsBiRuleStructure(DroolsTriRuleStructure<A, B, ?, PatternVar> biRuleStructure) {
        super(biRuleStructure.getVariableIdSupplier());
        this.a = biRuleStructure.getA();
        this.b = biRuleStructure.getB();
        this.primaryPattern = biRuleStructure.getPrimaryPatternBuilder();
        this.shelved = biRuleStructure.getShelvedRuleItems();
        this.prerequisites = biRuleStructure.getPrerequisites();
        this.dependents = biRuleStructure.getDependents();
    }

    public DroolsBiRuleStructure(Variable<A> aVariable, Variable<B> bVariable,
            DroolsPatternBuilder<PatternVar> primaryPattern, List<ViewItemBuilder<?>> shelved,
            List<ViewItemBuilder<?>> prerequisites, List<ViewItemBuilder<?>> dependents,
            LongSupplier variableIdSupplier) {
        super(variableIdSupplier);
        this.a = aVariable;
        this.b = bVariable;
        this.primaryPattern = primaryPattern;
        this.shelved = Collections.unmodifiableList(shelved);
        this.prerequisites = Collections.unmodifiableList(prerequisites);
        this.dependents = Collections.unmodifiableList(dependents);
    }

    public <C> DroolsBiRuleStructure<A, B, PatternVar> existsOrNot(PatternDSL.PatternDef<C> existencePattern,
            boolean shouldExist) {
        ExprViewItem item = DSL.exists(existencePattern);
        if (!shouldExist) {
            item = DSL.not(item);
        }
        return new DroolsBiRuleStructure<>(a, b, primaryPattern, shelved, prerequisites, mergeDependents(item),
                getVariableIdSupplier());
    }

    public Variable<A> getA() {
        return a;
    }

    public Variable<B> getB() {
        return b;
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
        return primaryPattern;
    }

    @Override
    public List<ViewItemBuilder<?>> getDependents() {
        return dependents;
    }

    @Override
    protected Class[] getVariableTypes() {
        return Stream.of(a, b).map(Argument::getType).toArray(Class[]::new);
    }

}
