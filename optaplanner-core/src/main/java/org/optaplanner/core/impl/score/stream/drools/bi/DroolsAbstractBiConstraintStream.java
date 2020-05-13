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

import java.math.BigDecimal;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.ToIntBiFunction;
import java.util.function.ToLongBiFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.stream.Constraint;
import org.optaplanner.core.api.score.stream.bi.BiConstraintCollector;
import org.optaplanner.core.api.score.stream.bi.BiConstraintStream;
import org.optaplanner.core.api.score.stream.quad.QuadConstraintStream;
import org.optaplanner.core.api.score.stream.tri.TriConstraintStream;
import org.optaplanner.core.api.score.stream.tri.TriJoiner;
import org.optaplanner.core.api.score.stream.uni.UniConstraintStream;
import org.optaplanner.core.impl.score.stream.bi.InnerBiConstraintStream;
import org.optaplanner.core.impl.score.stream.common.ScoreImpactType;
import org.optaplanner.core.impl.score.stream.drools.DroolsConstraintFactory;
import org.optaplanner.core.impl.score.stream.drools.common.DroolsAbstractConstraintStream;
import org.optaplanner.core.impl.score.stream.drools.quad.DroolsGroupingQuadConstraintStream;
import org.optaplanner.core.impl.score.stream.drools.tri.DroolsAbstractTriConstraintStream;
import org.optaplanner.core.impl.score.stream.drools.tri.DroolsGroupingTriConstraintStream;
import org.optaplanner.core.impl.score.stream.drools.tri.DroolsJoinTriConstraintStream;
import org.optaplanner.core.impl.score.stream.drools.uni.DroolsAbstractUniConstraintStream;
import org.optaplanner.core.impl.score.stream.drools.uni.DroolsFromUniConstraintStream;
import org.optaplanner.core.impl.score.stream.drools.uni.DroolsGroupingUniConstraintStream;
import org.optaplanner.core.impl.score.stream.tri.FilteringTriJoiner;

public abstract class DroolsAbstractBiConstraintStream<Solution_, A, B>
        extends DroolsAbstractConstraintStream<Solution_>
        implements InnerBiConstraintStream<A, B> {

    public DroolsAbstractBiConstraintStream(DroolsConstraintFactory<Solution_> constraintFactory) {
        super(constraintFactory);
    }

    @Override
    public int getCardinality() {
        return 2;
    }

    @Override
    public BiConstraintStream<A, B> filter(BiPredicate<A, B> predicate) {
        DroolsAbstractBiConstraintStream<Solution_, A, B> stream = new DroolsFilterBiConstraintStream<>(constraintFactory, this,
                predicate);
        addChildStream(stream);
        return stream;
    }

    @Override
    public <C> TriConstraintStream<A, B, C> join(UniConstraintStream<C> otherStream, TriJoiner<A, B, C> joiner) {
        if (joiner instanceof FilteringTriJoiner) {
            return join(otherStream)
                    .filter(((FilteringTriJoiner<A, B, C>) joiner).getFilter());
        }
        DroolsAbstractTriConstraintStream<Solution_, A, B, C> stream = new DroolsJoinTriConstraintStream<>(constraintFactory,
                this,
                (DroolsAbstractUniConstraintStream<Solution_, C>) otherStream, joiner);
        addChildStream(stream);
        return stream;
    }

    // ************************************************************************
    // If (Not) Exists
    // ************************************************************************

    @SafeVarargs
    @Override
    public final <C> BiConstraintStream<A, B> ifExists(Class<C> otherClass, TriJoiner<A, B, C>... joiners) {
        return ifExistsOrNot(true, otherClass, joiners);
    }

    @SafeVarargs
    @Override
    public final <C> BiConstraintStream<A, B> ifNotExists(Class<C> otherClass, TriJoiner<A, B, C>... joiners) {
        return ifExistsOrNot(false, otherClass, joiners);
    }

    @SafeVarargs
    private final <C> BiConstraintStream<A, B> ifExistsOrNot(boolean shouldExist, Class<C> otherClass,
            TriJoiner<A, B, C>... joiners) {
        DroolsExistsBiConstraintStream<Solution_, A, B> stream = new DroolsExistsBiConstraintStream<>(constraintFactory, this,
                shouldExist, otherClass, joiners);
        addChildStream(stream);
        return stream;
    }

    // ************************************************************************
    // Group by
    // ************************************************************************

    @Override
    public <ResultContainer_, Result_> UniConstraintStream<Result_> groupBy(
            BiConstraintCollector<A, B, ResultContainer_, Result_> collector) {
        DroolsGroupingUniConstraintStream<Solution_, Result_> stream = new DroolsGroupingUniConstraintStream<>(
                constraintFactory, this, collector);
        addChildStream(stream);
        return stream;
    }

    @Override
    public <GroupKey_> UniConstraintStream<GroupKey_> groupBy(BiFunction<A, B, GroupKey_> groupKeyMapping) {
        DroolsGroupingUniConstraintStream<Solution_, GroupKey_> stream = new DroolsGroupingUniConstraintStream<>(
                constraintFactory, this, groupKeyMapping);
        addChildStream(stream);
        return stream;
    }

    @Override
    public <GroupKey_, __, Result_> BiConstraintStream<GroupKey_, Result_> groupBy(
            BiFunction<A, B, GroupKey_> groupKeyMapping,
            BiConstraintCollector<A, B, __, Result_> collector) {
        DroolsGroupingBiConstraintStream<Solution_, GroupKey_, Result_> stream = new DroolsGroupingBiConstraintStream<>(
                constraintFactory, this, groupKeyMapping, collector);
        addChildStream(stream);
        return stream;
    }

    @Override
    public <GroupKeyA_, GroupKeyB_> BiConstraintStream<GroupKeyA_, GroupKeyB_> groupBy(
            BiFunction<A, B, GroupKeyA_> groupKeyAMapping, BiFunction<A, B, GroupKeyB_> groupKeyBMapping) {
        DroolsGroupingBiConstraintStream<Solution_, GroupKeyA_, GroupKeyB_> stream = new DroolsGroupingBiConstraintStream<>(
                constraintFactory, this, groupKeyAMapping, groupKeyBMapping);
        addChildStream(stream);
        return stream;
    }

    @Override
    public <GroupKeyA_, GroupKeyB_, ResultContainer_, Result_> TriConstraintStream<GroupKeyA_, GroupKeyB_, Result_> groupBy(
            BiFunction<A, B, GroupKeyA_> groupKeyAMapping, BiFunction<A, B, GroupKeyB_> groupKeyBMapping,
            BiConstraintCollector<A, B, ResultContainer_, Result_> collector) {
        DroolsGroupingTriConstraintStream<Solution_, GroupKeyA_, GroupKeyB_, Result_> stream =
                new DroolsGroupingTriConstraintStream<>(constraintFactory, this, groupKeyAMapping, groupKeyBMapping, collector);
        addChildStream(stream);
        return stream;
    }

    @Override
    public <GroupKeyA_, GroupKeyB_, ResultContainerC_, ResultC_, ResultContainerD_, ResultD_>
            QuadConstraintStream<GroupKeyA_, GroupKeyB_, ResultC_, ResultD_> groupBy(
                    BiFunction<A, B, GroupKeyA_> groupKeyAMapping, BiFunction<A, B, GroupKeyB_> groupKeyBMapping,
                    BiConstraintCollector<A, B, ResultContainerC_, ResultC_> collectorC,
                    BiConstraintCollector<A, B, ResultContainerD_, ResultD_> collectorD) {
        DroolsGroupingQuadConstraintStream<Solution_, GroupKeyA_, GroupKeyB_, ResultC_, ResultD_> stream =
                new DroolsGroupingQuadConstraintStream<>(constraintFactory, this, groupKeyAMapping, groupKeyBMapping,
                        collectorC, collectorD);
        addChildStream(stream);
        return stream;
    }

    // ************************************************************************
    // Penalize/reward
    // ************************************************************************

    @Override
    public final Constraint impactScore(String constraintPackage, String constraintName, Score<?> constraintWeight,
            ScoreImpactType impactType) {
        DroolsScoringBiConstraintStream<Solution_, A, B> stream = new DroolsScoringBiConstraintStream<>(constraintFactory,
                this);
        addChildStream(stream);
        return buildConstraint(constraintPackage, constraintName, constraintWeight, impactType, stream);
    }

    @Override
    public final Constraint impactScore(String constraintPackage, String constraintName,
            Score<?> constraintWeight, ToIntBiFunction<A, B> matchWeigher, ScoreImpactType impactType) {
        DroolsScoringBiConstraintStream<Solution_, A, B> stream = new DroolsScoringBiConstraintStream<>(constraintFactory, this,
                matchWeigher);
        addChildStream(stream);
        return buildConstraint(constraintPackage, constraintName, constraintWeight, impactType, stream);
    }

    @Override
    public final Constraint impactScoreLong(String constraintPackage, String constraintName,
            Score<?> constraintWeight, ToLongBiFunction<A, B> matchWeigher, ScoreImpactType impactType) {
        DroolsScoringBiConstraintStream<Solution_, A, B> stream = new DroolsScoringBiConstraintStream<>(constraintFactory, this,
                matchWeigher);
        addChildStream(stream);
        return buildConstraint(constraintPackage, constraintName, constraintWeight, impactType, stream);
    }

    @Override
    public final Constraint impactScoreBigDecimal(String constraintPackage, String constraintName,
            Score<?> constraintWeight, BiFunction<A, B, BigDecimal> matchWeigher, ScoreImpactType impactType) {
        DroolsScoringBiConstraintStream<Solution_, A, B> stream = new DroolsScoringBiConstraintStream<>(constraintFactory, this,
                matchWeigher);
        addChildStream(stream);
        return buildConstraint(constraintPackage, constraintName, constraintWeight, impactType, stream);
    }

    @Override
    public final Constraint impactScoreConfigurable(String constraintPackage, String constraintName,
            ScoreImpactType impactType) {
        DroolsScoringBiConstraintStream<Solution_, A, B> stream = new DroolsScoringBiConstraintStream<>(constraintFactory,
                this);
        addChildStream(stream);
        return buildConstraintConfigurable(constraintPackage, constraintName, impactType, stream);
    }

    @Override
    public final Constraint impactScoreConfigurable(String constraintPackage, String constraintName,
            ToIntBiFunction<A, B> matchWeigher, ScoreImpactType impactType) {
        DroolsScoringBiConstraintStream<Solution_, A, B> stream = new DroolsScoringBiConstraintStream<>(constraintFactory, this,
                matchWeigher);
        addChildStream(stream);
        return buildConstraintConfigurable(constraintPackage, constraintName, impactType, stream);
    }

    @Override
    public final Constraint impactScoreConfigurableLong(String constraintPackage, String constraintName,
            ToLongBiFunction<A, B> matchWeigher, ScoreImpactType impactType) {
        DroolsScoringBiConstraintStream<Solution_, A, B> stream = new DroolsScoringBiConstraintStream<>(constraintFactory, this,
                matchWeigher);
        addChildStream(stream);
        return buildConstraintConfigurable(constraintPackage, constraintName, impactType, stream);
    }

    @Override
    public final Constraint impactScoreConfigurableBigDecimal(String constraintPackage, String constraintName,
            BiFunction<A, B, BigDecimal> matchWeigher, ScoreImpactType impactType) {
        DroolsScoringBiConstraintStream<Solution_, A, B> stream = new DroolsScoringBiConstraintStream<>(constraintFactory, this,
                matchWeigher);
        addChildStream(stream);
        return buildConstraintConfigurable(constraintPackage, constraintName, impactType, stream);
    }

    // ************************************************************************
    // Pattern creation
    // ************************************************************************

    @Override
    public List<DroolsFromUniConstraintStream<Solution_, Object>> getFromStreamList() {
        DroolsAbstractConstraintStream<Solution_> parent = getParent();
        if (parent == null) {
            DroolsJoinBiConstraintStream<Solution_, A, B> joinStream = (DroolsJoinBiConstraintStream<Solution_, A, B>) this;
            List<DroolsFromUniConstraintStream<Solution_, Object>> leftParentFromStreamList = joinStream.getLeftParentStream()
                    .getFromStreamList();
            List<DroolsFromUniConstraintStream<Solution_, Object>> rightParentFromStreamList = joinStream.getRightParentStream()
                    .getFromStreamList();
            return Stream.concat(leftParentFromStreamList.stream(), rightParentFromStreamList.stream())
                    .collect(Collectors.toList()); // TODO Should we distinct?
        } else {
            return parent.getFromStreamList();
        }
    }

    protected abstract DroolsAbstractConstraintStream<Solution_> getParent();

    public abstract DroolsBiCondition<A, B, ?> getCondition();

}
