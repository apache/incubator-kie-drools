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

package org.optaplanner.core.impl.score.stream.drools.quad;

import java.math.BigDecimal;

import org.optaplanner.core.api.function.QuadFunction;
import org.optaplanner.core.api.function.QuadPredicate;
import org.optaplanner.core.api.function.ToIntQuadFunction;
import org.optaplanner.core.api.function.ToLongQuadFunction;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.stream.Constraint;
import org.optaplanner.core.api.score.stream.bi.BiConstraintStream;
import org.optaplanner.core.api.score.stream.penta.PentaJoiner;
import org.optaplanner.core.api.score.stream.quad.QuadConstraintCollector;
import org.optaplanner.core.api.score.stream.quad.QuadConstraintStream;
import org.optaplanner.core.api.score.stream.tri.TriConstraintStream;
import org.optaplanner.core.api.score.stream.uni.UniConstraintStream;
import org.optaplanner.core.impl.score.stream.common.ScoreImpactType;
import org.optaplanner.core.impl.score.stream.drools.DroolsConstraintFactory;
import org.optaplanner.core.impl.score.stream.drools.bi.DroolsGroupingBiConstraintStream;
import org.optaplanner.core.impl.score.stream.drools.common.DroolsAbstractConstraintStream;
import org.optaplanner.core.impl.score.stream.drools.common.consequences.ConstraintConsequence;
import org.optaplanner.core.impl.score.stream.drools.common.nodes.QuadConstraintGraphNode;
import org.optaplanner.core.impl.score.stream.drools.tri.DroolsGroupingTriConstraintStream;
import org.optaplanner.core.impl.score.stream.drools.uni.DroolsGroupingUniConstraintStream;
import org.optaplanner.core.impl.score.stream.quad.InnerQuadConstraintStream;

public abstract class DroolsAbstractQuadConstraintStream<Solution_, A, B, C, D>
        extends DroolsAbstractConstraintStream<Solution_> implements InnerQuadConstraintStream<A, B, C, D> {

    public DroolsAbstractQuadConstraintStream(DroolsConstraintFactory<Solution_> constraintFactory) {
        super(constraintFactory);
    }

    @Override
    public int getCardinality() {
        return 4;
    }

    @Override
    public QuadConstraintStream<A, B, C, D> filter(QuadPredicate<A, B, C, D> predicate) {
        DroolsAbstractQuadConstraintStream<Solution_, A, B, C, D> stream = new DroolsFilterQuadConstraintStream<>(
                constraintFactory, this, predicate);
        addChildStream(stream);
        return stream;
    }

    // ************************************************************************
    // If (not) exists
    // ************************************************************************

    @SafeVarargs
    @Override
    public final <E> QuadConstraintStream<A, B, C, D> ifExists(Class<E> otherClass,
            PentaJoiner<A, B, C, D, E>... joiners) {
        return ifExistsOrNot(true, otherClass, joiners);
    }

    @SafeVarargs
    @Override
    public final <E> QuadConstraintStream<A, B, C, D> ifNotExists(Class<E> otherClass,
            PentaJoiner<A, B, C, D, E>... joiners) {
        return ifExistsOrNot(false, otherClass, joiners);
    }

    @SafeVarargs
    private final <E> QuadConstraintStream<A, B, C, D> ifExistsOrNot(boolean shouldExist, Class<E> otherClass,
            PentaJoiner<A, B, C, D, E>... joiners) {
        getConstraintFactory().assertValidFromType(otherClass);
        DroolsExistsQuadConstraintStream<Solution_, A, B, C, D> stream = new DroolsExistsQuadConstraintStream<>(
                constraintFactory, this, shouldExist, otherClass, joiners);
        addChildStream(stream);
        return stream;
    }

    // ************************************************************************
    // Group by
    // ************************************************************************

    @Override
    public <ResultContainer_, Result_> UniConstraintStream<Result_> groupBy(
            QuadConstraintCollector<A, B, C, D, ResultContainer_, Result_> collector) {
        DroolsGroupingUniConstraintStream<Solution_, Result_> stream = new DroolsGroupingUniConstraintStream<>(
                constraintFactory, this, collector);
        addChildStream(stream);
        return stream;
    }

    @Override
    public <GroupKey_> UniConstraintStream<GroupKey_> groupBy(QuadFunction<A, B, C, D, GroupKey_> groupKeyMapping) {
        DroolsGroupingUniConstraintStream<Solution_, GroupKey_> stream = new DroolsGroupingUniConstraintStream<>(
                constraintFactory, this, groupKeyMapping);
        addChildStream(stream);
        return stream;
    }

    @Override
    public <GroupKey_, ResultContainer_, Result_> BiConstraintStream<GroupKey_, Result_> groupBy(
            QuadFunction<A, B, C, D, GroupKey_> groupKeyMapping,
            QuadConstraintCollector<A, B, C, D, ResultContainer_, Result_> collector) {
        DroolsGroupingBiConstraintStream<Solution_, GroupKey_, Result_> stream = new DroolsGroupingBiConstraintStream<>(
                constraintFactory, this, groupKeyMapping, collector);
        addChildStream(stream);
        return stream;
    }

    @Override
    public <GroupKeyA_, GroupKeyB_> BiConstraintStream<GroupKeyA_, GroupKeyB_> groupBy(
            QuadFunction<A, B, C, D, GroupKeyA_> groupKeyAMapping,
            QuadFunction<A, B, C, D, GroupKeyB_> groupKeyBMapping) {
        DroolsGroupingBiConstraintStream<Solution_, GroupKeyA_, GroupKeyB_> stream = new DroolsGroupingBiConstraintStream<>(
                constraintFactory, this, groupKeyAMapping,
                groupKeyBMapping);
        addChildStream(stream);
        return stream;
    }

    @Override
    public <GroupKeyA_, GroupKeyB_, ResultContainer_, Result_> TriConstraintStream<GroupKeyA_, GroupKeyB_, Result_> groupBy(
            QuadFunction<A, B, C, D, GroupKeyA_> groupKeyAMapping,
            QuadFunction<A, B, C, D, GroupKeyB_> groupKeyBMapping,
            QuadConstraintCollector<A, B, C, D, ResultContainer_, Result_> collector) {
        DroolsGroupingTriConstraintStream<Solution_, GroupKeyA_, GroupKeyB_, Result_> stream =
                new DroolsGroupingTriConstraintStream<>(constraintFactory, this, groupKeyAMapping, groupKeyBMapping, collector);
        addChildStream(stream);
        return stream;
    }

    @Override
    public <GroupKeyA_, GroupKeyB_, ResultContainerC_, ResultC_, ResultContainerD_, ResultD_>
            QuadConstraintStream<GroupKeyA_, GroupKeyB_, ResultC_, ResultD_> groupBy(
                    QuadFunction<A, B, C, D, GroupKeyA_> groupKeyAMapping,
                    QuadFunction<A, B, C, D, GroupKeyB_> groupKeyBMapping,
                    QuadConstraintCollector<A, B, C, D, ResultContainerC_, ResultC_> collectorC,
                    QuadConstraintCollector<A, B, C, D, ResultContainerD_, ResultD_> collectorD) {
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
        ConstraintConsequence<QuadConstraintGraphNode> consequence =
                constraintFactory.getConstraintGraph().impact(getConstraintGraphNode());
        return buildConstraint(constraintPackage, constraintName, constraintWeight, impactType, consequence);
    }

    @Override
    public Constraint impactScore(String constraintPackage, String constraintName, Score<?> constraintWeight,
            ToIntQuadFunction<A, B, C, D> matchWeigher, ScoreImpactType impactType) {
        ConstraintConsequence<QuadConstraintGraphNode> consequence =
                constraintFactory.getConstraintGraph().impact(getConstraintGraphNode(), matchWeigher);
        return buildConstraint(constraintPackage, constraintName, constraintWeight, impactType, consequence);
    }

    @Override
    public Constraint impactScoreLong(String constraintPackage, String constraintName, Score<?> constraintWeight,
            ToLongQuadFunction<A, B, C, D> matchWeigher, ScoreImpactType impactType) {
        ConstraintConsequence<QuadConstraintGraphNode> consequence =
                constraintFactory.getConstraintGraph().impact(getConstraintGraphNode(), matchWeigher);
        return buildConstraint(constraintPackage, constraintName, constraintWeight, impactType, consequence);
    }

    @Override
    public Constraint impactScoreBigDecimal(String constraintPackage, String constraintName, Score<?> constraintWeight,
            QuadFunction<A, B, C, D, BigDecimal> matchWeigher, ScoreImpactType impactType) {
        ConstraintConsequence<QuadConstraintGraphNode> consequence =
                constraintFactory.getConstraintGraph().impact(getConstraintGraphNode(), matchWeigher);
        return buildConstraint(constraintPackage, constraintName, constraintWeight, impactType, consequence);
    }

    @Override
    protected Constraint impactScoreConfigurable(String constraintPackage, String constraintName,
            ScoreImpactType impactType) {
        ConstraintConsequence<QuadConstraintGraphNode> consequence =
                constraintFactory.getConstraintGraph().impact(getConstraintGraphNode());
        return buildConstraintConfigurable(constraintPackage, constraintName, impactType, consequence);
    }

    @Override
    public Constraint impactScoreConfigurable(String constraintPackage, String constraintName,
            ToIntQuadFunction<A, B, C, D> matchWeigher, ScoreImpactType impactType) {
        ConstraintConsequence<QuadConstraintGraphNode> consequence =
                constraintFactory.getConstraintGraph().impact(getConstraintGraphNode(), matchWeigher);
        return buildConstraintConfigurable(constraintPackage, constraintName, impactType, consequence);
    }

    @Override
    public Constraint impactScoreConfigurableLong(String constraintPackage, String constraintName,
            ToLongQuadFunction<A, B, C, D> matchWeigher, ScoreImpactType impactType) {
        ConstraintConsequence<QuadConstraintGraphNode> consequence =
                constraintFactory.getConstraintGraph().impact(getConstraintGraphNode(), matchWeigher);
        return buildConstraintConfigurable(constraintPackage, constraintName, impactType, consequence);
    }

    @Override
    public Constraint impactScoreConfigurableBigDecimal(String constraintPackage, String constraintName,
            QuadFunction<A, B, C, D, BigDecimal> matchWeigher, ScoreImpactType impactType) {
        ConstraintConsequence<QuadConstraintGraphNode> consequence =
                constraintFactory.getConstraintGraph().impact(getConstraintGraphNode(), matchWeigher);
        return buildConstraintConfigurable(constraintPackage, constraintName, impactType, consequence);
    }

    // ************************************************************************
    // Pattern creation
    // ************************************************************************

    public abstract QuadConstraintGraphNode getConstraintGraphNode();

}
