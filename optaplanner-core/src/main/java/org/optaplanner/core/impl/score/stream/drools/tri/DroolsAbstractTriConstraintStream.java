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

package org.optaplanner.core.impl.score.stream.drools.tri;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.optaplanner.core.api.function.ToIntTriFunction;
import org.optaplanner.core.api.function.ToLongTriFunction;
import org.optaplanner.core.api.function.TriFunction;
import org.optaplanner.core.api.function.TriPredicate;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.stream.Constraint;
import org.optaplanner.core.api.score.stream.bi.BiConstraintStream;
import org.optaplanner.core.api.score.stream.quad.QuadConstraintStream;
import org.optaplanner.core.api.score.stream.quad.QuadJoiner;
import org.optaplanner.core.api.score.stream.tri.TriConstraintCollector;
import org.optaplanner.core.api.score.stream.tri.TriConstraintStream;
import org.optaplanner.core.api.score.stream.uni.UniConstraintStream;
import org.optaplanner.core.impl.score.stream.common.ScoreImpactType;
import org.optaplanner.core.impl.score.stream.drools.DroolsConstraintFactory;
import org.optaplanner.core.impl.score.stream.drools.bi.DroolsGroupingBiConstraintStream;
import org.optaplanner.core.impl.score.stream.drools.common.DroolsAbstractConstraintStream;
import org.optaplanner.core.impl.score.stream.drools.quad.DroolsAbstractQuadConstraintStream;
import org.optaplanner.core.impl.score.stream.drools.quad.DroolsGroupingQuadConstraintStream;
import org.optaplanner.core.impl.score.stream.drools.quad.DroolsJoinQuadConstraintStream;
import org.optaplanner.core.impl.score.stream.drools.uni.DroolsAbstractUniConstraintStream;
import org.optaplanner.core.impl.score.stream.drools.uni.DroolsFromUniConstraintStream;
import org.optaplanner.core.impl.score.stream.drools.uni.DroolsGroupingUniConstraintStream;
import org.optaplanner.core.impl.score.stream.quad.FilteringQuadJoiner;
import org.optaplanner.core.impl.score.stream.tri.InnerTriConstraintStream;

public abstract class DroolsAbstractTriConstraintStream<Solution_, A, B, C>
        extends DroolsAbstractConstraintStream<Solution_> implements InnerTriConstraintStream<A, B, C> {

    protected final DroolsAbstractConstraintStream<Solution_> parent;

    public DroolsAbstractTriConstraintStream(DroolsConstraintFactory<Solution_> constraintFactory,
            DroolsAbstractConstraintStream<Solution_> parent) {
        super(constraintFactory);
        if (parent == null && !(this instanceof DroolsJoinTriConstraintStream)) {
            throw new IllegalArgumentException("The stream (" + this + ") must have a parent (null), " +
                    "unless it's a join stream.");
        }
        this.parent = parent;
    }

    @Override
    public int getCardinality() {
        return 3;
    }

    @Override
    public TriConstraintStream<A, B, C> filter(TriPredicate<A, B, C> predicate) {
        DroolsAbstractTriConstraintStream<Solution_, A, B, C> stream = new DroolsFilterTriConstraintStream<>(constraintFactory,
                this, predicate);
        addChildStream(stream);
        return stream;
    }

    @Override
    public <D> QuadConstraintStream<A, B, C, D> join(UniConstraintStream<D> otherStream,
            QuadJoiner<A, B, C, D> joiner) {
        if (joiner instanceof FilteringQuadJoiner) {
            return join(otherStream)
                    .filter(((FilteringQuadJoiner<A, B, C, D>) joiner).getFilter());
        }
        DroolsAbstractQuadConstraintStream<Solution_, A, B, C, D> stream = new DroolsJoinQuadConstraintStream<>(
                constraintFactory, this,
                (DroolsAbstractUniConstraintStream<Solution_, D>) otherStream, joiner);
        addChildStream(stream);
        return stream;
    }

    // ************************************************************************
    // If (not) exists
    // ************************************************************************

    @SafeVarargs
    @Override
    public final <D> TriConstraintStream<A, B, C> ifExists(Class<D> otherClass, QuadJoiner<A, B, C, D>... joiners) {
        return ifExistsOrNot(true, otherClass, joiners);
    }

    @SafeVarargs
    @Override
    public final <D> TriConstraintStream<A, B, C> ifNotExists(Class<D> otherClass, QuadJoiner<A, B, C, D>... joiners) {
        return ifExistsOrNot(false, otherClass, joiners);
    }

    @SafeVarargs
    private final <D> TriConstraintStream<A, B, C> ifExistsOrNot(boolean shouldExist, Class<D> otherClass,
            QuadJoiner<A, B, C, D>... joiners) {
        DroolsExistsTriConstraintStream<Solution_, A, B, C> stream = new DroolsExistsTriConstraintStream<>(constraintFactory,
                this, shouldExist, otherClass, joiners);
        addChildStream(stream);
        return stream;
    }

    // ************************************************************************
    // Group by
    // ************************************************************************

    @Override
    public <ResultContainer_, Result_> UniConstraintStream<Result_> groupBy(
            TriConstraintCollector<A, B, C, ResultContainer_, Result_> collector) {
        DroolsGroupingUniConstraintStream<Solution_, Result_> stream = new DroolsGroupingUniConstraintStream<>(
                constraintFactory, this, collector);
        addChildStream(stream);
        return stream;
    }

    @Override
    public <GroupKey_> UniConstraintStream<GroupKey_> groupBy(TriFunction<A, B, C, GroupKey_> groupKeyMapping) {
        DroolsGroupingUniConstraintStream<Solution_, GroupKey_> stream = new DroolsGroupingUniConstraintStream<>(
                constraintFactory, this, groupKeyMapping);
        addChildStream(stream);
        return stream;
    }

    @Override
    public <GroupKey_, ResultContainer_, Result_> BiConstraintStream<GroupKey_, Result_> groupBy(
            TriFunction<A, B, C, GroupKey_> groupKeyMapping,
            TriConstraintCollector<A, B, C, ResultContainer_, Result_> collector) {
        DroolsGroupingBiConstraintStream<Solution_, GroupKey_, Result_> stream = new DroolsGroupingBiConstraintStream<>(
                constraintFactory, this, groupKeyMapping, collector);
        addChildStream(stream);
        return stream;
    }

    @Override
    public <GroupKeyA_, GroupKeyB_> BiConstraintStream<GroupKeyA_, GroupKeyB_> groupBy(
            TriFunction<A, B, C, GroupKeyA_> groupKeyAMapping, TriFunction<A, B, C, GroupKeyB_> groupKeyBMapping) {
        DroolsGroupingBiConstraintStream<Solution_, GroupKeyA_, GroupKeyB_> stream = new DroolsGroupingBiConstraintStream<>(
                constraintFactory, this, groupKeyAMapping,
                groupKeyBMapping);
        addChildStream(stream);
        return stream;
    }

    @Override
    public <GroupKeyA_, GroupKeyB_, ResultContainer_, Result_> TriConstraintStream<GroupKeyA_, GroupKeyB_, Result_> groupBy(
            TriFunction<A, B, C, GroupKeyA_> groupKeyAMapping, TriFunction<A, B, C, GroupKeyB_> groupKeyBMapping,
            TriConstraintCollector<A, B, C, ResultContainer_, Result_> collector) {
        DroolsGroupingTriConstraintStream<Solution_, GroupKeyA_, GroupKeyB_, Result_> stream =
                new DroolsGroupingTriConstraintStream<>(constraintFactory, this, groupKeyAMapping, groupKeyBMapping, collector);
        addChildStream(stream);
        return stream;
    }

    @Override
    public <GroupKeyA_, GroupKeyB_, ResultContainerC_, ResultC_, ResultContainerD_, ResultD_>
            QuadConstraintStream<GroupKeyA_, GroupKeyB_, ResultC_, ResultD_> groupBy(
                    TriFunction<A, B, C, GroupKeyA_> groupKeyAMapping,
                    TriFunction<A, B, C, GroupKeyB_> groupKeyBMapping,
                    TriConstraintCollector<A, B, C, ResultContainerC_, ResultC_> collectorC,
                    TriConstraintCollector<A, B, C, ResultContainerD_, ResultD_> collectorD) {
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
    protected Constraint impactScore(String constraintPackage, String constraintName, Score<?> constraintWeight,
            ScoreImpactType impactType) {
        DroolsScoringTriConstraintStream<Solution_, A, B, C> stream = new DroolsScoringTriConstraintStream<>(constraintFactory,
                this);
        addChildStream(stream);
        return buildConstraint(constraintPackage, constraintName, constraintWeight, impactType, stream);
    }

    @Override
    public Constraint impactScore(String constraintPackage, String constraintName, Score<?> constraintWeight,
            ToIntTriFunction<A, B, C> matchWeigher, ScoreImpactType impactType) {
        DroolsScoringTriConstraintStream<Solution_, A, B, C> stream = new DroolsScoringTriConstraintStream<>(constraintFactory,
                this, matchWeigher);
        addChildStream(stream);
        return buildConstraint(constraintPackage, constraintName, constraintWeight, impactType, stream);
    }

    @Override
    public Constraint impactScoreLong(String constraintPackage, String constraintName, Score<?> constraintWeight,
            ToLongTriFunction<A, B, C> matchWeigher, ScoreImpactType impactType) {
        DroolsScoringTriConstraintStream<Solution_, A, B, C> stream = new DroolsScoringTriConstraintStream<>(constraintFactory,
                this, matchWeigher);
        addChildStream(stream);
        return buildConstraint(constraintPackage, constraintName, constraintWeight, impactType, stream);
    }

    @Override
    public Constraint impactScoreBigDecimal(String constraintPackage, String constraintName, Score<?> constraintWeight,
            TriFunction<A, B, C, BigDecimal> matchWeigher, ScoreImpactType impactType) {
        DroolsScoringTriConstraintStream<Solution_, A, B, C> stream = new DroolsScoringTriConstraintStream<>(constraintFactory,
                this, matchWeigher);
        addChildStream(stream);
        return buildConstraint(constraintPackage, constraintName, constraintWeight, impactType, stream);
    }

    @Override
    protected Constraint impactScoreConfigurable(String constraintPackage, String constraintName,
            ScoreImpactType impactType) {
        DroolsScoringTriConstraintStream<Solution_, A, B, C> stream = new DroolsScoringTriConstraintStream<>(constraintFactory,
                this);
        addChildStream(stream);
        return buildConstraintConfigurable(constraintPackage, constraintName, impactType, stream);
    }

    @Override
    public Constraint impactScoreConfigurable(String constraintPackage, String constraintName,
            ToIntTriFunction<A, B, C> matchWeigher, ScoreImpactType impactType) {
        DroolsScoringTriConstraintStream<Solution_, A, B, C> stream = new DroolsScoringTriConstraintStream<>(constraintFactory,
                this, matchWeigher);
        addChildStream(stream);
        return buildConstraintConfigurable(constraintPackage, constraintName, impactType, stream);
    }

    @Override
    public Constraint impactScoreConfigurableLong(String constraintPackage, String constraintName,
            ToLongTriFunction<A, B, C> matchWeigher, ScoreImpactType impactType) {
        DroolsScoringTriConstraintStream<Solution_, A, B, C> stream = new DroolsScoringTriConstraintStream<>(constraintFactory,
                this, matchWeigher);
        addChildStream(stream);
        return buildConstraintConfigurable(constraintPackage, constraintName, impactType, stream);
    }

    @Override
    public Constraint impactScoreConfigurableBigDecimal(String constraintPackage, String constraintName,
            TriFunction<A, B, C, BigDecimal> matchWeigher, ScoreImpactType impactType) {
        DroolsScoringTriConstraintStream<Solution_, A, B, C> stream = new DroolsScoringTriConstraintStream<>(constraintFactory,
                this, matchWeigher);
        addChildStream(stream);
        return buildConstraintConfigurable(constraintPackage, constraintName, impactType, stream);
    }

    // ************************************************************************
    // Pattern creation
    // ************************************************************************

    @Override
    public List<DroolsFromUniConstraintStream<Solution_, Object>> getFromStreamList() {
        if (parent == null) {
            DroolsJoinTriConstraintStream<Solution_, A, B, C> joinStream =
                    (DroolsJoinTriConstraintStream<Solution_, A, B, C>) this;
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

    public abstract DroolsTriCondition<A, B, C, ?> getCondition();

}
