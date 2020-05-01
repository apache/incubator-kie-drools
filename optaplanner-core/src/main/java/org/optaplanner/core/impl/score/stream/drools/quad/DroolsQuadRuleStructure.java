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

package org.optaplanner.core.impl.score.stream.drools.quad;

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

public class DroolsQuadRuleStructure<A, B, C, D, PatternVar> extends DroolsRuleStructure<PatternVar> {

    private final Variable<A> a;
    private final Variable<B> b;
    private final Variable<C> c;
    private final Variable<D> d;
    private final DroolsPatternBuilder<PatternVar> primaryPattern;
    private final List<ViewItemBuilder<?>> shelved;
    private final List<ViewItemBuilder<?>> prerequisites;
    private final List<ViewItemBuilder<?>> dependents;

    /**
     * Builds a final version of the ABC pattern as it will no longer be mutated, and turns the D pattern into the new
     * primary pattern.
     *
     * @param abcRuleStructure
     * @param dRuleStructure
     * @param variableIdSupplier
     */
    public <AbcPatternVar> DroolsQuadRuleStructure(DroolsTriRuleStructure<A, B, C, AbcPatternVar> abcRuleStructure,
            DroolsUniRuleStructure<D, PatternVar> dRuleStructure, LongSupplier variableIdSupplier) {
        super(variableIdSupplier);
        this.a = abcRuleStructure.getA();
        this.b = abcRuleStructure.getB();
        this.c = abcRuleStructure.getC();
        this.d = dRuleStructure.getA();
        this.primaryPattern = dRuleStructure.getPrimaryPatternBuilder();
        List<ViewItemBuilder<?>> newShelved = new ArrayList<>(abcRuleStructure.getShelvedRuleItems());
        newShelved.addAll(dRuleStructure.getShelvedRuleItems());
        this.shelved = Collections.unmodifiableList(newShelved);
        List<ViewItemBuilder<?>> newPrerequisites = new ArrayList<>(abcRuleStructure.getPrerequisites());
        newPrerequisites.add(abcRuleStructure.getPrimaryPatternBuilder().build());
        newPrerequisites.addAll(abcRuleStructure.getDependents());
        newPrerequisites.addAll(dRuleStructure.getPrerequisites());
        this.prerequisites = Collections.unmodifiableList(newPrerequisites);
        this.dependents = Collections.unmodifiableList(dRuleStructure.getDependents());
    }

    public DroolsQuadRuleStructure(Variable<A> aVariable, Variable<B> bVariable, Variable<C> cVariable,
            Variable<D> dVariable, DroolsPatternBuilder<PatternVar> primaryPattern, List<ViewItemBuilder<?>> shelved,
            List<ViewItemBuilder<?>> prerequisites, List<ViewItemBuilder<?>> dependents,
            LongSupplier variableIdSupplier) {
        super(variableIdSupplier);
        this.a = aVariable;
        this.b = bVariable;
        this.c = cVariable;
        this.d = dVariable;
        this.primaryPattern = primaryPattern;
        this.shelved = Collections.unmodifiableList(shelved);
        this.prerequisites = Collections.unmodifiableList(prerequisites);
        this.dependents = Collections.unmodifiableList(dependents);
    }

    public <E> DroolsQuadRuleStructure<A, B, C, D, PatternVar> existsOrNot(PatternDSL.PatternDef<E> existencePattern,
            boolean shouldExist) {
        ExprViewItem item = DSL.exists(existencePattern);
        if (!shouldExist) {
            item = DSL.not(item);
        }
        return new DroolsQuadRuleStructure<>(a, b, c, d, primaryPattern, shelved, prerequisites, mergeDependents(item),
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

    public Variable<D> getD() {
        return d;
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
        return Stream.of(a, b, c, d).map(Argument::getType).toArray(Class[]::new);
    }

}
