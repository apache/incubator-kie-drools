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

package org.optaplanner.core.impl.score.stream.drools.quad;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.optaplanner.core.api.function.QuadFunction;
import org.optaplanner.core.api.function.QuadPredicate;
import org.optaplanner.core.api.function.ToIntQuadFunction;
import org.optaplanner.core.api.function.ToLongQuadFunction;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.stream.Constraint;
import org.optaplanner.core.api.score.stream.quad.QuadConstraintStream;
import org.optaplanner.core.impl.score.stream.drools.DroolsConstraintFactory;
import org.optaplanner.core.impl.score.stream.drools.common.DroolsAbstractConstraintStream;
import org.optaplanner.core.impl.score.stream.drools.uni.DroolsFromUniConstraintStream;
import org.optaplanner.core.impl.score.stream.quad.InnerQuadConstraintStream;

public abstract class DroolsAbstractQuadConstraintStream<Solution_, A, B, C, D>
        extends DroolsAbstractConstraintStream<Solution_> implements InnerQuadConstraintStream<A, B, C, D> {

    protected final DroolsAbstractQuadConstraintStream<Solution_, A, B, C, D> parent;

    public DroolsAbstractQuadConstraintStream(DroolsConstraintFactory<Solution_> constraintFactory,
            DroolsAbstractQuadConstraintStream<Solution_, A, B, C, D> parent) {
        super(constraintFactory);
        if (parent == null && !(this instanceof DroolsJoinQuadConstraintStream)) {
            throw new IllegalArgumentException("The stream (" + this + ") must have a parent (null), " +
                    "unless it's a join stream.");
        }
        this.parent = parent;
    }

    @Override
    public QuadConstraintStream<A, B, C, D> filter(QuadPredicate<A, B, C, D> predicate) {
        DroolsAbstractQuadConstraintStream<Solution_, A, B, C, D> stream =
                new DroolsFilterQuadConstraintStream<>(constraintFactory, this, predicate);
        addChildStream(stream);
        return stream;
    }

    @Override
    protected Constraint impactScore(String constraintPackage, String constraintName, Score<?> constraintWeight,
            boolean positive) {
        DroolsScoringQuadConstraintStream<Solution_, A, B, C, D> stream =
                new DroolsScoringQuadConstraintStream<>(constraintFactory, this);
        addChildStream(stream);
        return buildConstraint(constraintPackage, constraintName, constraintWeight, positive, stream);
    }

    @Override
    public Constraint impactScore(String constraintPackage, String constraintName, Score<?> constraintWeight,
            ToIntQuadFunction<A, B, C, D> matchWeigher, boolean positive) {
        DroolsScoringQuadConstraintStream<Solution_, A, B, C, D> stream =
                new DroolsScoringQuadConstraintStream<>(constraintFactory, this, matchWeigher);
        addChildStream(stream);
        return buildConstraint(constraintPackage, constraintName, constraintWeight, positive, stream);
    }

    @Override
    public Constraint impactScoreLong(String constraintPackage, String constraintName, Score<?> constraintWeight,
            ToLongQuadFunction<A, B, C, D> matchWeigher, boolean positive) {
        DroolsScoringQuadConstraintStream<Solution_, A, B, C, D> stream =
                new DroolsScoringQuadConstraintStream<>(constraintFactory, this, matchWeigher);
        addChildStream(stream);
        return buildConstraint(constraintPackage, constraintName, constraintWeight, positive, stream);
    }

    @Override
    public Constraint impactScoreBigDecimal(String constraintPackage, String constraintName, Score<?> constraintWeight,
            QuadFunction<A, B, C, D, BigDecimal> matchWeigher, boolean positive) {
        DroolsScoringQuadConstraintStream<Solution_, A, B, C, D> stream =
                new DroolsScoringQuadConstraintStream<>(constraintFactory, this, matchWeigher);
        addChildStream(stream);
        return buildConstraint(constraintPackage, constraintName, constraintWeight, positive, stream);
    }

    @Override
    protected Constraint impactScoreConfigurable(String constraintPackage, String constraintName, boolean positive) {
        DroolsScoringQuadConstraintStream<Solution_, A, B, C, D> stream =
                new DroolsScoringQuadConstraintStream<>(constraintFactory, this);
        addChildStream(stream);
        return buildConstraintConfigurable(constraintPackage, constraintName, positive, stream);
    }

    @Override
    public Constraint impactScoreConfigurable(String constraintPackage, String constraintName,
            ToIntQuadFunction<A, B, C, D> matchWeigher, boolean positive) {
        DroolsScoringQuadConstraintStream<Solution_, A, B, C, D> stream =
                new DroolsScoringQuadConstraintStream<>(constraintFactory, this, matchWeigher);
        addChildStream(stream);
        return buildConstraintConfigurable(constraintPackage, constraintName, positive, stream);
    }

    @Override
    public Constraint impactScoreConfigurableLong(String constraintPackage, String constraintName,
            ToLongQuadFunction<A, B, C, D> matchWeigher, boolean positive) {
        DroolsScoringQuadConstraintStream<Solution_, A, B, C, D> stream =
                new DroolsScoringQuadConstraintStream<>(constraintFactory, this, matchWeigher);
        addChildStream(stream);
        return buildConstraintConfigurable(constraintPackage, constraintName, positive, stream);
    }

    @Override
    public Constraint impactScoreConfigurableBigDecimal(String constraintPackage, String constraintName,
            QuadFunction<A, B, C, D, BigDecimal> matchWeigher, boolean positive) {
        DroolsScoringQuadConstraintStream<Solution_, A, B, C, D> stream =
                new DroolsScoringQuadConstraintStream<>(constraintFactory, this, matchWeigher);
        addChildStream(stream);
        return buildConstraintConfigurable(constraintPackage, constraintName, positive, stream);
    }

    // ************************************************************************
    // Pattern creation
    // ************************************************************************

    @Override
    public List<DroolsFromUniConstraintStream<Solution_, Object>> getFromStreamList() {
        if (parent == null) {
            DroolsJoinQuadConstraintStream<Solution_, A, B, C, D> joinStream =
                    (DroolsJoinQuadConstraintStream<Solution_, A, B, C, D>) this;
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

    public abstract DroolsQuadCondition<A, B, C, D> getCondition();
}
