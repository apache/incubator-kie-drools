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

import org.drools.model.Variable;
import org.drools.model.view.ViewItemBuilder;
import org.optaplanner.core.impl.score.stream.drools.common.DroolsPatternBuilder;
import org.optaplanner.core.impl.score.stream.drools.common.DroolsRuleStructure;
import org.optaplanner.core.impl.score.stream.drools.uni.DroolsUniRuleStructure;

public class DroolsBiRuleStructure<A, B> extends DroolsRuleStructure {

    private final Variable<A> a;
    private final Variable<B> b;
    private final DroolsPatternBuilder<?> targetPattern;
    private final List<ViewItemBuilder<?>> shelved;
    private final List<ViewItemBuilder<?>> prerequisites;
    private final List<ViewItemBuilder<?>> dependents;

    public DroolsBiRuleStructure(DroolsUniRuleStructure<A> aRuleStructure, DroolsUniRuleStructure<B> bRuleStructure,
            LongSupplier variableIdSupplier) {
        super(variableIdSupplier);
        this.a = aRuleStructure.getA();
        this.b = bRuleStructure.getA();
        this.targetPattern = bRuleStructure.getPrimaryPatternBuilder();
        List<ViewItemBuilder<?>> newShelved = new ArrayList<>(aRuleStructure.getShelvedRuleItems());
        newShelved.addAll(bRuleStructure.getShelvedRuleItems());
        this.shelved = Collections.unmodifiableList(newShelved);
        List<ViewItemBuilder<?>> newOpenItems = new ArrayList<>(aRuleStructure.getPrerequisites());
        newOpenItems.add(aRuleStructure.getPrimaryPatternBuilder().build());
        newOpenItems.addAll(aRuleStructure.getDependents());
        newOpenItems.addAll(bRuleStructure.getPrerequisites());
        this.prerequisites = Collections.unmodifiableList(newOpenItems);
        this.dependents = Collections.unmodifiableList(bRuleStructure.getDependents());
    }

    public DroolsBiRuleStructure(Variable<A> aVariable, Variable<B> bVariable, DroolsPatternBuilder<?> targetPattern,
            List<ViewItemBuilder<?>> shelved, List<ViewItemBuilder<?>> prerequisites,
            List<ViewItemBuilder<?>> dependents, LongSupplier variableIdSupplier) {
        super(variableIdSupplier);
        this.a = aVariable;
        this.b = bVariable;
        this.targetPattern = targetPattern;
        this.shelved = Collections.unmodifiableList(shelved);
        this.prerequisites = Collections.unmodifiableList(prerequisites);
        this.dependents = Collections.unmodifiableList(dependents);
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
    public DroolsPatternBuilder<Object> getPrimaryPatternBuilder() {
        return (DroolsPatternBuilder<Object>) targetPattern;
    }

    @Override
    public List<ViewItemBuilder<?>> getDependents() {
        return dependents;
    }
}
