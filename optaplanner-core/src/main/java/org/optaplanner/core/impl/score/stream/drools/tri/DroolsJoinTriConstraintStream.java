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
import org.optaplanner.core.api.score.stream.tri.TriJoiner;
import org.optaplanner.core.impl.score.stream.common.JoinerType;
import org.optaplanner.core.impl.score.stream.drools.DroolsConstraintFactory;
import org.optaplanner.core.impl.score.stream.drools.bi.DroolsAbstractBiConstraintStream;
import org.optaplanner.core.impl.score.stream.drools.uni.DroolsAbstractUniConstraintStream;
import org.optaplanner.core.impl.score.stream.tri.AbstractTriJoiner;

public final class DroolsJoinTriConstraintStream<Solution_, A, B, C>
        extends DroolsAbstractTriConstraintStream<Solution_, A, B, C> {

    private final DroolsAbstractBiConstraintStream<Solution_, A, B> leftParentStream;
    private final DroolsAbstractUniConstraintStream<Solution_, C> rightParentStream;
    private final AbstractTriJoiner<A, B, C> triJoiner;
    private final PatternDSL.PatternDef<C> cPattern;

    public DroolsJoinTriConstraintStream(DroolsConstraintFactory<Solution_> constraintFactory,
            DroolsAbstractBiConstraintStream<Solution_, A, B> parent,
            DroolsAbstractUniConstraintStream<Solution_, C> otherStream, TriJoiner<A, B, C> triJoiner) {
        super(constraintFactory, null);
        this.leftParentStream = parent;
        this.rightParentStream = otherStream;
        this.triJoiner = (AbstractTriJoiner<A, B, C>) triJoiner;
        this.cPattern = otherStream.getPattern().expr("triJoin-" + UUID.randomUUID(), getAVariableDeclaration(),
                getBVariableDeclaration(), (c, a, b) -> matches(a, b, c));
    }

    // ************************************************************************
    // Getters/setters
    // ************************************************************************

    @Override
    public DroolsAbstractBiConstraintStream<Solution_, A, B> getLeftParentStream() {
        return leftParentStream;
    }

    @Override
    public DroolsAbstractUniConstraintStream<Solution_, C> getRightParentStream() {
        return rightParentStream;
    }

    @Override
    public Declaration<A> getAVariableDeclaration() {
        return leftParentStream.getLeftVariableDeclaration();
    }

    @Override
    public PatternDSL.PatternDef<A> getAPattern() {
        return leftParentStream.getLeftPattern();
    }

    @Override
    public Declaration<B> getBVariableDeclaration() {
        return leftParentStream.getRightVariableDeclaration();
    }

    @Override
    public PatternDSL.PatternDef<B> getBPattern() {
        return leftParentStream.getRightPattern();
    }

    @Override
    public Declaration<C> getCVariableDeclaration() {
        return rightParentStream.getVariableDeclaration();
    }

    @Override
    public PatternDSL.PatternDef<C> getCPattern() {
        return cPattern;
    }

    private boolean matches(A a, B b, C c) {
        Object[] leftMappings = triJoiner.getLeftCombinedMapping().apply(a, b);
        Object[] rightMappings = triJoiner.getRightCombinedMapping().apply(c);
        JoinerType[] joinerTypes = triJoiner.getJoinerTypes();
        for (int i = 0; i < joinerTypes.length; i++) {
            JoinerType joinerType = joinerTypes[i];
            if (!joinerType.matches(leftMappings[i], rightMappings[i])) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String toString() {
        return "TriJoin() with " + childStreamList.size()  + " children";
    }


}
