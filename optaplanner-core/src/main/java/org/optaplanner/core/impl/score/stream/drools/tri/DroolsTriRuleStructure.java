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

package org.optaplanner.core.impl.score.stream.drools.tri;

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
import org.optaplanner.core.impl.score.stream.drools.bi.DroolsBiRuleStructure;
import org.optaplanner.core.impl.score.stream.drools.common.DroolsPatternBuilder;
import org.optaplanner.core.impl.score.stream.drools.common.DroolsRuleStructure;
import org.optaplanner.core.impl.score.stream.drools.uni.DroolsUniRuleStructure;

public class DroolsTriRuleStructure<A, B, C, PatternVar> extends DroolsRuleStructure<PatternVar> {

    private final Variable<A> a;
    private final Variable<B> b;
    private final Variable<C> c;
    private final DroolsPatternBuilder<PatternVar> primaryPattern;
    private final List<ViewItemBuilder<?>> shelved;
    private final List<ViewItemBuilder<?>> prerequisites;
    private final List<ViewItemBuilder<?>> dependents;

    /**
     * Builds a final version of the AB pattern as it will no longer be mutated, and turns the C pattern into the new
     * primary pattern.
     *
     * @param abRuleStructure
     * @param cRuleStructure
     * @param variableIdSupplier
     */
    public <AbPatternVar> DroolsTriRuleStructure(DroolsBiRuleStructure<A, B, AbPatternVar> abRuleStructure,
            DroolsUniRuleStructure<C, PatternVar> cRuleStructure, LongSupplier variableIdSupplier) {
        super(variableIdSupplier);
        this.a = abRuleStructure.getA();
        this.b = abRuleStructure.getB();
        this.c = cRuleStructure.getA();
        this.primaryPattern = cRuleStructure.getPrimaryPatternBuilder();
        List<ViewItemBuilder<?>> newShelved = new ArrayList<>(abRuleStructure.getShelvedRuleItems());
        newShelved.addAll(cRuleStructure.getShelvedRuleItems());
        this.shelved = Collections.unmodifiableList(newShelved);
        List<ViewItemBuilder<?>> newPrerequisites = new ArrayList<>(abRuleStructure.getPrerequisites());
        newPrerequisites.add(abRuleStructure.getPrimaryPatternBuilder().build());
        newPrerequisites.addAll(abRuleStructure.getDependents());
        newPrerequisites.addAll(cRuleStructure.getPrerequisites());
        this.prerequisites = Collections.unmodifiableList(newPrerequisites);
        this.dependents = Collections.unmodifiableList(cRuleStructure.getDependents());
    }

    public DroolsTriRuleStructure(Variable<A> aVariable, Variable<B> bVariable, Variable<C> cVariable,
            DroolsPatternBuilder<PatternVar> primaryPattern, List<ViewItemBuilder<?>> shelved,
            List<ViewItemBuilder<?>> prerequisites, List<ViewItemBuilder<?>> dependents,
            LongSupplier variableIdSupplier) {
        super(variableIdSupplier);
        this.a = aVariable;
        this.b = bVariable;
        this.c = cVariable;
        this.primaryPattern = primaryPattern;
        this.shelved = Collections.unmodifiableList(shelved);
        this.prerequisites = Collections.unmodifiableList(prerequisites);
        this.dependents = Collections.unmodifiableList(dependents);
    }

    public <D> DroolsTriRuleStructure<A, B, C, PatternVar> existsOrNot(PatternDSL.PatternDef<D> existencePattern,
            boolean shouldExist) {
        ExprViewItem item = DSL.exists(existencePattern);
        if (!shouldExist) {
            item = DSL.not(item);
        }
        return new DroolsTriRuleStructure<>(a, b, c, primaryPattern, shelved, prerequisites, mergeDependents(item),
                getVariableIdSupplier());
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
    public DroolsPatternBuilder<PatternVar> getPrimaryPatternBuilder() {
        return primaryPattern;
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
    public List<ViewItemBuilder<?>> getDependents() {
        return dependents;
    }

    @Override
    protected Class[] getVariableTypes() {
        return Stream.of(a, b, c).map(Argument::getType).toArray(Class[]::new);
    }

}
