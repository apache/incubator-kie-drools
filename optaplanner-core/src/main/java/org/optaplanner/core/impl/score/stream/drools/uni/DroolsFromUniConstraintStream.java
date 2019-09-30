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

package org.optaplanner.core.impl.score.stream.drools.uni;

import java.util.Collections;
import java.util.List;

import org.drools.model.Declaration;
import org.drools.model.Global;
import org.drools.model.PatternDSL;
import org.drools.model.RuleItemBuilder;
import org.optaplanner.core.api.score.holder.AbstractScoreHolder;
import org.optaplanner.core.impl.score.stream.drools.DroolsConstraintFactory;

public final class DroolsFromUniConstraintStream<Solution_, A> extends DroolsAbstractUniConstraintStream<Solution_, A> {

    private final Class<A> fromClass;

    public DroolsFromUniConstraintStream(DroolsConstraintFactory<Solution_> constraintFactory, Class<A> fromClass) {
        super(constraintFactory);
        this.fromClass = fromClass;
        if (fromClass == null) {
            throw new IllegalArgumentException("The fromClass (null) cannot be null.");
        }
    }

    // ************************************************************************
    // Pattern creation
    // ************************************************************************

    @Override
    public List<DroolsFromUniConstraintStream<Solution_, Object>> getFromStreamList() {
        return Collections.singletonList((DroolsFromUniConstraintStream<Solution_, Object>) this);
    }

    @Override
    public void createRuleItemBuilders(List<RuleItemBuilder<?>> ruleItemBuilderList,
            Global<? extends AbstractScoreHolder> scoreHolderGlobal,
            Declaration<A> aVar, PatternDSL.PatternDef<A> parentPattern) {
        if (aVar != null || parentPattern != null) {
            throw new IllegalStateException("Impossible state: the stream (" + this
                    + ") cannot have an aVar (" + aVar + ") or a parentPattern (" + parentPattern + ").");
        }
        aVar = PatternDSL.declarationOf(fromClass);
        PatternDSL.PatternDef<A> pattern = PatternDSL.pattern(aVar);
        for (DroolsAbstractUniConstraintStream<Solution_, A> childStream : childStreamList) {
            childStream.createRuleItemBuilders(ruleItemBuilderList, scoreHolderGlobal, aVar, pattern);
        }
    }

    @Override
    public String toString() {
        return "From(" + fromClass.getSimpleName() + ") with " + childStreamList.size()  + " children";
    }

    // ************************************************************************
    // Getters/setters
    // ************************************************************************

    public Class<A> getFromClass() {
        return fromClass;
    }

}
