/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.constraint.streams.drools.tri;

import static org.optaplanner.constraint.streams.common.RetrievalSemantics.STANDARD;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.function.Function;

import org.optaplanner.constraint.streams.common.RetrievalSemantics;
import org.optaplanner.constraint.streams.common.ScoreImpactType;
import org.optaplanner.constraint.streams.drools.DroolsConstraintFactory;
import org.optaplanner.constraint.streams.drools.bi.DroolsGroupingBiConstraintStream;
import org.optaplanner.constraint.streams.drools.common.DroolsAbstractConstraintStream;
import org.optaplanner.constraint.streams.drools.common.RuleBuilder;
import org.optaplanner.constraint.streams.drools.common.TriLeftHandSide;
import org.optaplanner.constraint.streams.drools.quad.DroolsAbstractQuadConstraintStream;
import org.optaplanner.constraint.streams.drools.quad.DroolsGroupingQuadConstraintStream;
import org.optaplanner.constraint.streams.drools.quad.DroolsJoinQuadConstraintStream;
import org.optaplanner.constraint.streams.drools.uni.DroolsAbstractUniConstraintStream;
import org.optaplanner.constraint.streams.drools.uni.DroolsGroupingUniConstraintStream;
import org.optaplanner.constraint.streams.drools.uni.DroolsMappingUniConstraintStream;
import org.optaplanner.constraint.streams.quad.QuadJoinerComber;
import org.optaplanner.constraint.streams.tri.InnerTriConstraintStream;
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

public abstract class DroolsAbstractTriConstraintStream<Solution_, A, B, C>
        extends DroolsAbstractConstraintStream<Solution_> implements InnerTriConstraintStream<A, B, C> {

    public DroolsAbstractTriConstraintStream(DroolsConstraintFactory<Solution_> constraintFactory,
            RetrievalSemantics retrievalSemantics) {
        super(constraintFactory, retrievalSemantics);
    }

    @Override
    public TriConstraintStream<A, B, C> filter(TriPredicate<A, B, C> predicate) {
        DroolsAbstractTriConstraintStream<Solution_, A, B, C> stream =
                new DroolsFilterTriConstraintStream<>(constraintFactory, this, predicate);
        addChildStream(stream);
        return stream;
    }

    @Override
    @SafeVarargs
    public final <D> QuadConstraintStream<A, B, C, D> join(UniConstraintStream<D> otherStream,
            QuadJoiner<A, B, C, D>... joiners) {
        QuadJoinerComber<A, B, C, D> joinerComber = QuadJoinerComber.comb(joiners);
        DroolsAbstractQuadConstraintStream<Solution_, A, B, C, D> stream =
                new DroolsJoinQuadConstraintStream<>(constraintFactory, this,
                        (DroolsAbstractUniConstraintStream<Solution_, D>) otherStream,
                        joinerComber.getMergedJoiner());
        addChildStream(stream);
        if (joinerComber.getMergedFiltering() == null) {
            return stream;
        } else {
            return stream.filter(joinerComber.getMergedFiltering());
        }
    }

    @SafeVarargs
    @Override
    public final <D> TriConstraintStream<A, B, C> ifExists(Class<D> otherClass, QuadJoiner<A, B, C, D>... joiners) {
        return ifExistsOrNot(true, getRetrievalSemantics() != STANDARD, otherClass, joiners);
    }

    @SafeVarargs
    @Override
    public final <D> TriConstraintStream<A, B, C> ifExistsIncludingNullVars(Class<D> otherClass,
            QuadJoiner<A, B, C, D>... joiners) {
        return ifExistsOrNot(true, true, otherClass, joiners);
    }

    @SafeVarargs
    @Override
    public final <D> TriConstraintStream<A, B, C> ifNotExists(Class<D> otherClass, QuadJoiner<A, B, C, D>... joiners) {
        return ifExistsOrNot(false, getRetrievalSemantics() != STANDARD, otherClass, joiners);
    }

    @SafeVarargs
    @Override
    public final <D> TriConstraintStream<A, B, C> ifNotExistsIncludingNullVars(Class<D> otherClass,
            QuadJoiner<A, B, C, D>... joiners) {
        return ifExistsOrNot(false, true, otherClass, joiners);
    }

    @SafeVarargs
    private <D> TriConstraintStream<A, B, C> ifExistsOrNot(boolean shouldExist, boolean shouldIncludeNullVars,
            Class<D> otherClass, QuadJoiner<A, B, C, D>... joiners) {
        getConstraintFactory().assertValidFromType(otherClass);
        DroolsExistsTriConstraintStream<Solution_, A, B, C> stream = new DroolsExistsTriConstraintStream<>(constraintFactory,
                this, shouldExist, shouldIncludeNullVars, otherClass, joiners);
        addChildStream(stream);
        return stream;
    }

    @Override
    public <ResultContainer_, Result_> UniConstraintStream<Result_> groupBy(
            TriConstraintCollector<A, B, C, ResultContainer_, Result_> collector) {
        DroolsGroupingUniConstraintStream<Solution_, Result_> stream = new DroolsGroupingUniConstraintStream<>(
                constraintFactory, this, collector);
        addChildStream(stream);
        return stream;
    }

    @Override
    public <ResultContainerA_, ResultA_, ResultContainerB_, ResultB_> BiConstraintStream<ResultA_, ResultB_> groupBy(
            TriConstraintCollector<A, B, C, ResultContainerA_, ResultA_> collectorA,
            TriConstraintCollector<A, B, C, ResultContainerB_, ResultB_> collectorB) {
        DroolsGroupingBiConstraintStream<Solution_, ResultA_, ResultB_> stream =
                new DroolsGroupingBiConstraintStream<>(constraintFactory, this, collectorA, collectorB);
        addChildStream(stream);
        return stream;
    }

    @Override
    public <ResultContainerA_, ResultA_, ResultContainerB_, ResultB_, ResultContainerC_, ResultC_>
            TriConstraintStream<ResultA_, ResultB_, ResultC_>
            groupBy(TriConstraintCollector<A, B, C, ResultContainerA_, ResultA_> collectorA,
                    TriConstraintCollector<A, B, C, ResultContainerB_, ResultB_> collectorB,
                    TriConstraintCollector<A, B, C, ResultContainerC_, ResultC_> collectorC) {
        DroolsGroupingTriConstraintStream<Solution_, ResultA_, ResultB_, ResultC_> stream =
                new DroolsGroupingTriConstraintStream<>(constraintFactory, this, collectorA, collectorB, collectorC);
        addChildStream(stream);
        return stream;
    }

    @Override
    public <ResultContainerA_, ResultA_, ResultContainerB_, ResultB_, ResultContainerC_, ResultC_, ResultContainerD_, ResultD_>
            QuadConstraintStream<ResultA_, ResultB_, ResultC_, ResultD_>
            groupBy(TriConstraintCollector<A, B, C, ResultContainerA_, ResultA_> collectorA,
                    TriConstraintCollector<A, B, C, ResultContainerB_, ResultB_> collectorB,
                    TriConstraintCollector<A, B, C, ResultContainerC_, ResultC_> collectorC,
                    TriConstraintCollector<A, B, C, ResultContainerD_, ResultD_> collectorD) {
        DroolsGroupingQuadConstraintStream<Solution_, ResultA_, ResultB_, ResultC_, ResultD_> stream =
                new DroolsGroupingQuadConstraintStream<>(constraintFactory, this, collectorA, collectorB, collectorC,
                        collectorD);
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
    public <GroupKey_, ResultContainerB_, ResultB_, ResultContainerC_, ResultC_>
            TriConstraintStream<GroupKey_, ResultB_, ResultC_> groupBy(TriFunction<A, B, C, GroupKey_> groupKeyMapping,
                    TriConstraintCollector<A, B, C, ResultContainerB_, ResultB_> collectorB,
                    TriConstraintCollector<A, B, C, ResultContainerC_, ResultC_> collectorC) {
        DroolsGroupingTriConstraintStream<Solution_, GroupKey_, ResultB_, ResultC_> stream =
                new DroolsGroupingTriConstraintStream<>(constraintFactory, this, groupKeyMapping, collectorB, collectorC);
        addChildStream(stream);
        return stream;
    }

    @Override
    public <GroupKey_, ResultContainerB_, ResultB_, ResultContainerC_, ResultC_, ResultContainerD_, ResultD_>
            QuadConstraintStream<GroupKey_, ResultB_, ResultC_, ResultD_>
            groupBy(TriFunction<A, B, C, GroupKey_> groupKeyMapping,
                    TriConstraintCollector<A, B, C, ResultContainerB_, ResultB_> collectorB,
                    TriConstraintCollector<A, B, C, ResultContainerC_, ResultC_> collectorC,
                    TriConstraintCollector<A, B, C, ResultContainerD_, ResultD_> collectorD) {
        DroolsGroupingQuadConstraintStream<Solution_, GroupKey_, ResultB_, ResultC_, ResultD_> stream =
                new DroolsGroupingQuadConstraintStream<>(constraintFactory, this, groupKeyMapping, collectorB, collectorC,
                        collectorD);
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

    @Override
    public <GroupKeyA_, GroupKeyB_, GroupKeyC_> TriConstraintStream<GroupKeyA_, GroupKeyB_, GroupKeyC_> groupBy(
            TriFunction<A, B, C, GroupKeyA_> groupKeyAMapping, TriFunction<A, B, C, GroupKeyB_> groupKeyBMapping,
            TriFunction<A, B, C, GroupKeyC_> groupKeyCMapping) {
        DroolsGroupingTriConstraintStream<Solution_, GroupKeyA_, GroupKeyB_, GroupKeyC_> stream =
                new DroolsGroupingTriConstraintStream<>(constraintFactory, this, groupKeyAMapping, groupKeyBMapping,
                        groupKeyCMapping);
        addChildStream(stream);
        return stream;
    }

    @Override
    public <GroupKeyA_, GroupKeyB_, GroupKeyC_, ResultContainerD_, ResultD_>
            QuadConstraintStream<GroupKeyA_, GroupKeyB_, GroupKeyC_, ResultD_>
            groupBy(TriFunction<A, B, C, GroupKeyA_> groupKeyAMapping, TriFunction<A, B, C, GroupKeyB_> groupKeyBMapping,
                    TriFunction<A, B, C, GroupKeyC_> groupKeyCMapping,
                    TriConstraintCollector<A, B, C, ResultContainerD_, ResultD_> collectorD) {
        DroolsGroupingQuadConstraintStream<Solution_, GroupKeyA_, GroupKeyB_, GroupKeyC_, ResultD_> stream =
                new DroolsGroupingQuadConstraintStream<>(constraintFactory, this, groupKeyAMapping, groupKeyBMapping,
                        groupKeyCMapping, collectorD);
        addChildStream(stream);
        return stream;
    }

    @Override
    public <GroupKeyA_, GroupKeyB_, GroupKeyC_, GroupKeyD_> QuadConstraintStream<GroupKeyA_, GroupKeyB_, GroupKeyC_, GroupKeyD_>
            groupBy(TriFunction<A, B, C, GroupKeyA_> groupKeyAMapping, TriFunction<A, B, C, GroupKeyB_> groupKeyBMapping,
                    TriFunction<A, B, C, GroupKeyC_> groupKeyCMapping, TriFunction<A, B, C, GroupKeyD_> groupKeyDMapping) {
        DroolsGroupingQuadConstraintStream<Solution_, GroupKeyA_, GroupKeyB_, GroupKeyC_, GroupKeyD_> stream =
                new DroolsGroupingQuadConstraintStream<>(constraintFactory, this, groupKeyAMapping, groupKeyBMapping,
                        groupKeyCMapping, groupKeyDMapping);
        addChildStream(stream);
        return stream;
    }

    @Override
    public <ResultA_> UniConstraintStream<ResultA_> map(TriFunction<A, B, C, ResultA_> mapping) {
        DroolsMappingUniConstraintStream<Solution_, ResultA_> stream =
                new DroolsMappingUniConstraintStream<>(constraintFactory, this, Objects.requireNonNull(mapping));
        addChildStream(stream);
        return stream;
    }

    @Override
    public <ResultC_> TriConstraintStream<A, B, ResultC_> flattenLast(Function<C, Iterable<ResultC_>> mapping) {
        DroolsFlatteningTriConstraintStream<Solution_, A, B, ResultC_> stream =
                new DroolsFlatteningTriConstraintStream<>(constraintFactory, this, Objects.requireNonNull(mapping));
        addChildStream(stream);
        return stream;
    }

    @Override
    protected Constraint impactScore(String constraintPackage, String constraintName, Score<?> constraintWeight,
            ScoreImpactType impactType) {
        RuleBuilder<Solution_> ruleBuilder = getLeftHandSide().andTerminate();
        return buildConstraint(constraintPackage, constraintName, constraintWeight, impactType, ruleBuilder);
    }

    @Override
    public Constraint impactScore(String constraintPackage, String constraintName, Score<?> constraintWeight,
            ToIntTriFunction<A, B, C> matchWeigher, ScoreImpactType impactType) {
        RuleBuilder<Solution_> ruleBuilder = getLeftHandSide().andTerminate(matchWeigher);
        return buildConstraint(constraintPackage, constraintName, constraintWeight, impactType, ruleBuilder);
    }

    @Override
    public Constraint impactScoreLong(String constraintPackage, String constraintName, Score<?> constraintWeight,
            ToLongTriFunction<A, B, C> matchWeigher, ScoreImpactType impactType) {
        RuleBuilder<Solution_> ruleBuilder = getLeftHandSide().andTerminate(matchWeigher);
        return buildConstraint(constraintPackage, constraintName, constraintWeight, impactType, ruleBuilder);
    }

    @Override
    public Constraint impactScoreBigDecimal(String constraintPackage, String constraintName, Score<?> constraintWeight,
            TriFunction<A, B, C, BigDecimal> matchWeigher, ScoreImpactType impactType) {
        RuleBuilder<Solution_> ruleBuilder = getLeftHandSide().andTerminate(matchWeigher);
        return buildConstraint(constraintPackage, constraintName, constraintWeight, impactType, ruleBuilder);
    }

    @Override
    protected Constraint impactScoreConfigurable(String constraintPackage, String constraintName,
            ScoreImpactType impactType) {
        RuleBuilder<Solution_> ruleBuilder = getLeftHandSide().andTerminate();
        return buildConstraintConfigurable(constraintPackage, constraintName, impactType, ruleBuilder);
    }

    @Override
    public Constraint impactScoreConfigurable(String constraintPackage, String constraintName,
            ToIntTriFunction<A, B, C> matchWeigher, ScoreImpactType impactType) {
        RuleBuilder<Solution_> ruleBuilder = getLeftHandSide().andTerminate(matchWeigher);
        return buildConstraintConfigurable(constraintPackage, constraintName, impactType, ruleBuilder);
    }

    @Override
    public Constraint impactScoreConfigurableLong(String constraintPackage, String constraintName,
            ToLongTriFunction<A, B, C> matchWeigher, ScoreImpactType impactType) {
        RuleBuilder<Solution_> ruleBuilder = getLeftHandSide().andTerminate(matchWeigher);
        return buildConstraintConfigurable(constraintPackage, constraintName, impactType, ruleBuilder);
    }

    @Override
    public Constraint impactScoreConfigurableBigDecimal(String constraintPackage, String constraintName,
            TriFunction<A, B, C, BigDecimal> matchWeigher, ScoreImpactType impactType) {
        RuleBuilder<Solution_> ruleBuilder = getLeftHandSide().andTerminate(matchWeigher);
        return buildConstraintConfigurable(constraintPackage, constraintName, impactType, ruleBuilder);
    }

    public abstract TriLeftHandSide<A, B, C> getLeftHandSide();

}
