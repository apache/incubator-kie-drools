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

import org.drools.model.Declaration;
import org.drools.model.PatternDSL;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.stream.Constraint;
import org.optaplanner.core.api.score.stream.bi.BiConstraintStream;
import org.optaplanner.core.api.score.stream.bi.BiJoiner;
import org.optaplanner.core.api.score.stream.tri.TriConstraintStream;
import org.optaplanner.core.api.score.stream.uni.UniConstraintCollector;
import org.optaplanner.core.api.score.stream.uni.UniConstraintStream;
import org.optaplanner.core.impl.score.stream.drools.DroolsConstraintFactory;
import org.optaplanner.core.impl.score.stream.drools.bi.DroolsAbstractBiConstraintStream;
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
        childStreamList.add(stream);
        return stream;
    }

    // ************************************************************************
    // Join
    // ************************************************************************

    @Override
    public <B> BiConstraintStream<A, B> join(UniConstraintStream<B> otherStream, BiJoiner<A, B> joiner) {
        DroolsAbstractBiConstraintStream<Solution_, A, B> stream = new DroolsJoinBiConstraintStream<>(constraintFactory,
                this, (DroolsAbstractUniConstraintStream<Solution_, B>) otherStream, joiner);
        childStreamList.add(stream);
        return stream;
    }

    // ************************************************************************
    // Group by
    // ************************************************************************

    @Override
    public <GroupKey_, ResultContainer_, Result_> UniConstraintStream<Result_> groupBy(UniConstraintCollector<A, ResultContainer_, Result_> collector) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <GroupKey_> UniConstraintStream<GroupKey_> groupBy(Function<A, GroupKey_> groupKeyMapping) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <GroupKey_, ResultContainer_, Result_> BiConstraintStream<GroupKey_, Result_> groupBy(Function<A, GroupKey_> groupKeyMapping, UniConstraintCollector<A, ResultContainer_, Result_> collector) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <GroupKeyA_, GroupKeyB_> BiConstraintStream<GroupKeyA_, GroupKeyB_> groupBy(Function<A, GroupKeyA_> groupKeyAMapping, Function<A, GroupKeyB_> groupKeyBMapping) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <GroupKeyA_, GroupKeyB_, ResultContainer_, Result_> TriConstraintStream<GroupKeyA_, GroupKeyB_, Result_> groupBy(Function<A, GroupKeyA_> groupKeyAMapping, Function<A, GroupKeyB_> groupKeyBMapping, UniConstraintCollector<A, ResultContainer_, Result_> collector) {
        throw new UnsupportedOperationException();
    }

    // ************************************************************************
    // Penalize/reward
    // ************************************************************************

    @Override
    public final Constraint impactScore(String constraintPackage, String constraintName, Score<?> constraintWeight,
            boolean positive) {
        DroolsScoringUniConstraintStream<Solution_, A> stream =
                new DroolsScoringUniConstraintStream<>(constraintFactory, this);
        childStreamList.add(stream);
        return buildConstraint(constraintPackage, constraintName, constraintWeight, positive);
    }

    @Override
    public final Constraint impactScore(String constraintPackage, String constraintName, Score<?> constraintWeight,
            ToIntFunction<A> matchWeigher, boolean positive) {
        DroolsScoringUniConstraintStream<Solution_, A> stream =
                new DroolsScoringUniConstraintStream<>(constraintFactory, this, matchWeigher);
        childStreamList.add(stream);
        return buildConstraint(constraintPackage, constraintName, constraintWeight, positive);
    }

    @Override
    public final Constraint impactScoreLong(String constraintPackage, String constraintName,
            Score<?> constraintWeight, ToLongFunction<A> matchWeigher, boolean positive) {
        DroolsScoringUniConstraintStream<Solution_, A> stream =
                new DroolsScoringUniConstraintStream<>(constraintFactory, this, matchWeigher);
        childStreamList.add(stream);
        return buildConstraint(constraintPackage, constraintName, constraintWeight, positive);
    }

    @Override
    public final Constraint impactScoreBigDecimal(String constraintPackage, String constraintName,
            Score<?> constraintWeight, Function<A, BigDecimal> matchWeigher, boolean positive) {
        DroolsScoringUniConstraintStream<Solution_, A> stream =
                new DroolsScoringUniConstraintStream<>(constraintFactory, this, matchWeigher);
        childStreamList.add(stream);
        return buildConstraint(constraintPackage, constraintName, constraintWeight, positive);
    }

    @Override
    public final Constraint impactScoreConfigurable(String constraintPackage, String constraintName,
            boolean positive) {
        DroolsScoringUniConstraintStream<Solution_, A> stream =
                new DroolsScoringUniConstraintStream<>(constraintFactory, this);
        childStreamList.add(stream);
        return buildConstraintConfigurable(constraintPackage, constraintName, positive);
    }

    @Override
    public final Constraint impactScoreConfigurable(String constraintPackage, String constraintName,
            ToIntFunction<A> matchWeigher, boolean positive) {
        DroolsScoringUniConstraintStream<Solution_, A> stream =
                new DroolsScoringUniConstraintStream<>(constraintFactory, this, matchWeigher);
        childStreamList.add(stream);
        return buildConstraintConfigurable(constraintPackage, constraintName, positive);
    }

    @Override
    public final Constraint impactScoreConfigurableLong(String constraintPackage, String constraintName,
            ToLongFunction<A> matchWeigher, boolean positive) {
        DroolsScoringUniConstraintStream<Solution_, A> stream =
                new DroolsScoringUniConstraintStream<>(constraintFactory, this, matchWeigher);
        childStreamList.add(stream);
        return buildConstraintConfigurable(constraintPackage, constraintName, positive);
    }

    @Override
    public final Constraint impactScoreConfigurableBigDecimal(String constraintPackage, String constraintName,
            Function<A, BigDecimal> matchWeigher, boolean positive) {
        DroolsScoringUniConstraintStream<Solution_, A> stream =
                new DroolsScoringUniConstraintStream<>(constraintFactory, this, matchWeigher);
        childStreamList.add(stream);
        return buildConstraintConfigurable(constraintPackage, constraintName, positive);
    }

    public abstract Declaration<A> getVariableDeclaration();

    public abstract PatternDSL.PatternDef<A> getPattern();

}
