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

import java.util.UUID;

import org.drools.model.Declaration;
import org.drools.model.PatternDSL;
import org.optaplanner.core.api.function.TriPredicate;
import org.optaplanner.core.impl.score.stream.drools.DroolsConstraintFactory;

public final class DroolsFilterTriConstraintStream<Solution_, A, B, C>
        extends DroolsAbstractTriConstraintStream<Solution_, A, B, C> {

    private final PatternDSL.PatternDef<C> cPattern;

    public DroolsFilterTriConstraintStream(DroolsConstraintFactory<Solution_> constraintFactory,
            DroolsAbstractTriConstraintStream<Solution_, A, B, C> parent, TriPredicate<A, B, C> triPredicate) {
        super(constraintFactory, parent);
        this.cPattern = parent.getCPattern().expr("triFilter-" + UUID.randomUUID(), getAVariableDeclaration(),
                getBVariableDeclaration(), (c, a, b) -> triPredicate.test(a, b, c));
    }


    @Override
    public Declaration<A> getAVariableDeclaration() {
        return parent.getAVariableDeclaration();
    }

    @Override
    public PatternDSL.PatternDef<A> getAPattern() {
        return parent.getAPattern();
    }

    @Override
    public Declaration<B> getBVariableDeclaration() {
        return parent.getBVariableDeclaration();
    }

    @Override
    public PatternDSL.PatternDef<B> getBPattern() {
        return parent.getBPattern();
    }

    @Override
    public Declaration<C> getCVariableDeclaration() {
        return parent.getCVariableDeclaration();
    }

    @Override
    public PatternDSL.PatternDef<C> getCPattern() {
        return cPattern;
    }

    @Override
    public String toString() {
        return "TriFilter() with " + childStreamList.size() + " children";
    }

}
