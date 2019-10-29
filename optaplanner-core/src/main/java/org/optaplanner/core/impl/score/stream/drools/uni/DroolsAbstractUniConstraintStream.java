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

import java.math.BigDecimal;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.ToIntFunction;
import java.util.function.ToLongFunction;

import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.stream.Constraint;
import org.optaplanner.core.api.score.stream.bi.BiConstraintStream;
import org.optaplanner.core.api.score.stream.bi.BiJoiner;
import org.optaplanner.core.api.score.stream.uni.UniConstraintCollector;
import org.optaplanner.core.api.score.stream.uni.UniConstraintStream;
import org.optaplanner.core.impl.score.stream.drools.DroolsConstraintFactory;
import org.optaplanner.core.impl.score.stream.drools.bi.DroolsAbstractBiConstraintStream;
import org.optaplanner.core.impl.score.stream.drools.bi.DroolsGroupingBiConstraintStream;
import org.optaplanner.core.impl.score.stream.drools.bi.DroolsJoinBiConstraintStream;
import org.optaplanner.core.impl.score.stream.drools.common.DroolsAbstractConstraintStream;
import org.optaplanner.core.impl.score.stream.uni.InnerUniConstraintStream;

public abstract class DroolsAbstractUniConstraintStream<Solution_, A> extends DroolsAbstractConstraintStream<Solution_>
        implements InnerUniConstraintStream<A> {

    public DroolsAbstractUniConstraintStream(DroolsConstraintFactory<Solution_> constraintFactory) {
        super(constraintFactory);
    }

    // ************************************************************************
    // Filter
    // ************************************************************************

    @Override
    public DroolsAbstractUniConstraintStream<Solution_, A> filter(Predicate<A> predicate) {
        DroolsFilterUniConstraintStream<Solution_, A> stream = new DroolsFilterUniConstraintStream<>(constraintFactory, this, predicate);
        addChildStream(stream);
        return stream;
    }

    // ************************************************************************
    // Join
    // ************************************************************************

    @Override
    public <B> BiConstraintStream<A, B> join(UniConstraintStream<B> otherStream, BiJoiner<A, B> joiner) {
        DroolsAbstractUniConstraintStream<Solution_, B> castOtherStream =
                (DroolsAbstractUniConstraintStream<Solution_, B>) otherStream;
        DroolsAbstractBiConstraintStream<Solution_, A, B> stream = new DroolsJoinBiConstraintStream<>(constraintFactory,
                this, castOtherStream, joiner);
        addChildStream(stream);
        castOtherStream.addChildStream(stream);
        return stream;
    }

    // ************************************************************************
    // Group by
    // ************************************************************************

    @Override
    public <ResultContainer_, Result_> UniConstraintStream<Result_> groupBy(
            UniConstraintCollector<A, ResultContainer_, Result_> collector) {
        DroolsGroupingUniConstraintStream<Solution_, A, Result_> stream =
                new DroolsGroupingUniConstraintStream<>(constraintFactory, this, collector);
        addChildStream(stream);
        return stream;
    }

    @Override
    public <GroupKey_> UniConstraintStream<GroupKey_> groupBy(Function<A, GroupKey_> groupKeyMapping) {
        DroolsGroupingUniConstraintStream<Solution_, A, GroupKey_> stream =
                new DroolsGroupingUniConstraintStream<>(constraintFactory, this, groupKeyMapping);
        addChildStream(stream);
        return stream;
    }

    @Override
    public <GroupKey_, ResultContainer_, Result_> BiConstraintStream<GroupKey_, Result_> groupBy(
            Function<A, GroupKey_> groupKeyMapping, UniConstraintCollector<A, ResultContainer_, Result_> collector) {
        DroolsGroupingBiConstraintStream<Solution_, A, GroupKey_, ResultContainer_, Result_> stream =
                new DroolsGroupingBiConstraintStream<>(constraintFactory, this, groupKeyMapping, collector);
        addChildStream(stream);
        return stream;
    }

    // ************************************************************************
    // Penalize/reward
    // ************************************************************************

    @Override
    public final Constraint impactScore(String constraintPackage, String constraintName, Score<?> constraintWeight,
            boolean positive) {
        DroolsScoringUniConstraintStream<Solution_, A> stream =
                new DroolsScoringUniConstraintStream<>(constraintFactory, this);
        addChildStream(stream);
        return buildConstraint(constraintPackage, constraintName, constraintWeight, positive, stream);
    }

    @Override
    public final Constraint impactScore(String constraintPackage, String constraintName, Score<?> constraintWeight,
            ToIntFunction<A> matchWeigher, boolean positive) {
        DroolsScoringUniConstraintStream<Solution_, A> stream =
                new DroolsScoringUniConstraintStream<>(constraintFactory, this, matchWeigher);
        addChildStream(stream);
        return buildConstraint(constraintPackage, constraintName, constraintWeight, positive, stream);
    }

    @Override
    public final Constraint impactScoreLong(String constraintPackage, String constraintName,
            Score<?> constraintWeight, ToLongFunction<A> matchWeigher, boolean positive) {
        DroolsScoringUniConstraintStream<Solution_, A> stream =
                new DroolsScoringUniConstraintStream<>(constraintFactory, this, matchWeigher);
        addChildStream(stream);
        return buildConstraint(constraintPackage, constraintName, constraintWeight, positive, stream);
    }

    @Override
    public final Constraint impactScoreBigDecimal(String constraintPackage, String constraintName,
            Score<?> constraintWeight, Function<A, BigDecimal> matchWeigher, boolean positive) {
        DroolsScoringUniConstraintStream<Solution_, A> stream =
                new DroolsScoringUniConstraintStream<>(constraintFactory, this, matchWeigher);
        addChildStream(stream);
        return buildConstraint(constraintPackage, constraintName, constraintWeight, positive, stream);
    }

    @Override
    public final Constraint impactScoreConfigurable(String constraintPackage, String constraintName,
            boolean positive) {
        DroolsScoringUniConstraintStream<Solution_, A> stream =
                new DroolsScoringUniConstraintStream<>(constraintFactory, this);
        addChildStream(stream);
        return buildConstraintConfigurable(constraintPackage, constraintName, positive, stream);
    }

    @Override
    public final Constraint impactScoreConfigurable(String constraintPackage, String constraintName,
            ToIntFunction<A> matchWeigher, boolean positive) {
        DroolsScoringUniConstraintStream<Solution_, A> stream =
                new DroolsScoringUniConstraintStream<>(constraintFactory, this, matchWeigher);
        addChildStream(stream);
        return buildConstraintConfigurable(constraintPackage, constraintName, positive, stream);
    }

    @Override
    public final Constraint impactScoreConfigurableLong(String constraintPackage, String constraintName,
            ToLongFunction<A> matchWeigher, boolean positive) {
        DroolsScoringUniConstraintStream<Solution_, A> stream =
                new DroolsScoringUniConstraintStream<>(constraintFactory, this, matchWeigher);
        addChildStream(stream);
        return buildConstraintConfigurable(constraintPackage, constraintName, positive, stream);
    }

    @Override
    public final Constraint impactScoreConfigurableBigDecimal(String constraintPackage, String constraintName,
            Function<A, BigDecimal> matchWeigher, boolean positive) {
        DroolsScoringUniConstraintStream<Solution_, A> stream =
                new DroolsScoringUniConstraintStream<>(constraintFactory, this, matchWeigher);
        addChildStream(stream);
        return buildConstraintConfigurable(constraintPackage, constraintName, positive, stream);
    }

    public abstract DroolsUniCondition<A> createCondition();
}
