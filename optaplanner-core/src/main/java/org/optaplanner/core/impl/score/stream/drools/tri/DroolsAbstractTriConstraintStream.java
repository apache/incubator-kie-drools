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

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.drools.model.Declaration;
import org.drools.model.PatternDSL;
import org.optaplanner.core.api.function.ToIntTriFunction;
import org.optaplanner.core.api.function.ToLongTriFunction;
import org.optaplanner.core.api.function.TriFunction;
import org.optaplanner.core.api.function.TriPredicate;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.stream.Constraint;
import org.optaplanner.core.api.score.stream.tri.TriConstraintStream;
import org.optaplanner.core.impl.score.stream.drools.DroolsConstraintFactory;
import org.optaplanner.core.impl.score.stream.drools.common.DroolsAbstractConstraintStream;
import org.optaplanner.core.impl.score.stream.drools.uni.DroolsFromUniConstraintStream;
import org.optaplanner.core.impl.score.stream.tri.InnerTriConstraintStream;

public abstract class DroolsAbstractTriConstraintStream<Solution_, A, B, C>
        extends DroolsAbstractConstraintStream<Solution_> implements InnerTriConstraintStream<A, B, C> {

    protected final DroolsAbstractTriConstraintStream<Solution_, A, B, C> parent;

    public DroolsAbstractTriConstraintStream(DroolsConstraintFactory<Solution_> constraintFactory,
            DroolsAbstractTriConstraintStream<Solution_, A, B, C> parent) {
        super(constraintFactory);
        if (parent == null && !(this instanceof DroolsJoinTriConstraintStream)) {
            throw new IllegalArgumentException("The stream (" + this + ") must have a parent (null), " +
                    "unless it's a join stream.");
        }
        this.parent = parent;
    }

    @Override
    public TriConstraintStream<A, B, C> filter(TriPredicate<A, B, C> predicate) {
        DroolsAbstractTriConstraintStream<Solution_, A, B, C> stream =
                new DroolsFilterTriConstraintStream<>(constraintFactory, this, predicate);
        childStreamList.add(stream);
        return stream;
    }

    @Override
    protected Constraint impactScore(String constraintPackage, String constraintName, Score<?> constraintWeight,
            boolean positive) {
        DroolsScoringTriConstraintStream<Solution_, A, B, C> stream =
                new DroolsScoringTriConstraintStream<>(constraintFactory, this);
        childStreamList.add(stream);
        return buildConstraint(constraintPackage, constraintName, constraintWeight, positive);
    }

    @Override
    public Constraint impactScore(String constraintPackage, String constraintName, Score<?> constraintWeight,
            ToIntTriFunction<A, B, C> matchWeigher, boolean positive) {
        DroolsScoringTriConstraintStream<Solution_, A, B, C> stream =
                new DroolsScoringTriConstraintStream<>(constraintFactory, this, matchWeigher);
        childStreamList.add(stream);
        return buildConstraint(constraintPackage, constraintName, constraintWeight, positive);
    }

    @Override
    public Constraint impactScoreLong(String constraintPackage, String constraintName, Score<?> constraintWeight,
            ToLongTriFunction<A, B, C> matchWeigher, boolean positive) {
        DroolsScoringTriConstraintStream<Solution_, A, B, C> stream =
                new DroolsScoringTriConstraintStream<>(constraintFactory, this, matchWeigher);
        childStreamList.add(stream);
        return buildConstraint(constraintPackage, constraintName, constraintWeight, positive);
    }

    @Override
    public Constraint impactScoreBigDecimal(String constraintPackage, String constraintName, Score<?> constraintWeight,
            TriFunction<A, B, C, BigDecimal> matchWeigher, boolean positive) {
        DroolsScoringTriConstraintStream<Solution_, A, B, C> stream =
                new DroolsScoringTriConstraintStream<>(constraintFactory, this, matchWeigher);
        childStreamList.add(stream);
        return buildConstraint(constraintPackage, constraintName, constraintWeight, positive);
    }

    @Override
    protected Constraint impactScoreConfigurable(String constraintPackage, String constraintName, boolean positive) {
        DroolsScoringTriConstraintStream<Solution_, A, B, C> stream =
                new DroolsScoringTriConstraintStream<>(constraintFactory, this);
        childStreamList.add(stream);
        return buildConstraintConfigurable(constraintPackage, constraintName, positive);
    }

    @Override
    public Constraint impactScoreConfigurable(String constraintPackage, String constraintName,
            ToIntTriFunction<A, B, C> matchWeigher, boolean positive) {
        DroolsScoringTriConstraintStream<Solution_, A, B, C> stream =
                new DroolsScoringTriConstraintStream<>(constraintFactory, this, matchWeigher);
        childStreamList.add(stream);
        return buildConstraintConfigurable(constraintPackage, constraintName, positive);
    }

    @Override
    public Constraint impactScoreConfigurableLong(String constraintPackage, String constraintName,
            ToLongTriFunction<A, B, C> matchWeigher, boolean positive) {
        DroolsScoringTriConstraintStream<Solution_, A, B, C> stream =
                new DroolsScoringTriConstraintStream<>(constraintFactory, this, matchWeigher);
        childStreamList.add(stream);
        return buildConstraintConfigurable(constraintPackage, constraintName, positive);
    }

    @Override
    public Constraint impactScoreConfigurableBigDecimal(String constraintPackage, String constraintName,
            TriFunction<A, B, C, BigDecimal> matchWeigher, boolean positive) {
        DroolsScoringTriConstraintStream<Solution_, A, B, C> stream =
                new DroolsScoringTriConstraintStream<>(constraintFactory, this, matchWeigher);
        childStreamList.add(stream);
        return buildConstraintConfigurable(constraintPackage, constraintName, positive);
    }

    // ************************************************************************
    // Pattern creation
    // ************************************************************************

    @Override
    public List<DroolsFromUniConstraintStream<Solution_, Object>> getFromStreamList() {
        if (parent == null) {
            DroolsJoinTriConstraintStream<Solution_, A, B, C> joinStream =
                    (DroolsJoinTriConstraintStream<Solution_, A, B, C>) this;
            List<DroolsFromUniConstraintStream<Solution_, Object>> leftParentFromStreamList =
                    joinStream.getLeftParentStream().getFromStreamList();
            List<DroolsFromUniConstraintStream<Solution_, Object>> rightParentFromStreamList =
                    joinStream.getRightParentStream().getFromStreamList();
            return Stream.concat(leftParentFromStreamList.stream(), rightParentFromStreamList.stream())
                    .collect(Collectors.toList()); // TODO Should we distinct?
        } else {
            return parent.getFromStreamList();
        }
    }

    public abstract Declaration<A> getAVariableDeclaration();

    public abstract PatternDSL.PatternDef<A> getAPattern();

    public abstract Declaration<B> getBVariableDeclaration();

    public abstract PatternDSL.PatternDef<B> getBPattern();

    public abstract Declaration<C> getCVariableDeclaration();

    public abstract PatternDSL.PatternDef<C> getCPattern();

}
