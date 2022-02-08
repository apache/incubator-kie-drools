/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.core.impl.score.stream.bavet.bi;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.ToIntBiFunction;
import java.util.function.ToLongBiFunction;

import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.stream.Constraint;
import org.optaplanner.core.api.score.stream.ConstraintFactory;
import org.optaplanner.core.api.score.stream.bi.BiConstraintCollector;
import org.optaplanner.core.api.score.stream.bi.BiConstraintStream;
import org.optaplanner.core.api.score.stream.quad.QuadConstraintStream;
import org.optaplanner.core.api.score.stream.tri.TriConstraintStream;
import org.optaplanner.core.api.score.stream.tri.TriJoiner;
import org.optaplanner.core.api.score.stream.uni.UniConstraintStream;
import org.optaplanner.core.impl.score.stream.bavet.BavetConstraint;
import org.optaplanner.core.impl.score.stream.bavet.BavetConstraintFactory;
import org.optaplanner.core.impl.score.stream.bavet.common.BavetAbstractConstraintStream;
import org.optaplanner.core.impl.score.stream.bavet.common.BavetNodeBuildPolicy;
import org.optaplanner.core.impl.score.stream.bavet.common.index.BavetIndexFactory;
import org.optaplanner.core.impl.score.stream.bavet.tri.BavetJoinTriConstraintStream;
import org.optaplanner.core.impl.score.stream.bavet.uni.BavetAbstractUniConstraintStream;
import org.optaplanner.core.impl.score.stream.bavet.uni.BavetJoinBridgeUniConstraintStream;
import org.optaplanner.core.impl.score.stream.bi.InnerBiConstraintStream;
import org.optaplanner.core.impl.score.stream.common.RetrievalSemantics;
import org.optaplanner.core.impl.score.stream.common.ScoreImpactType;
import org.optaplanner.core.impl.score.stream.tri.AbstractTriJoiner;

public abstract class BavetAbstractBiConstraintStream<Solution_, A, B> extends BavetAbstractConstraintStream<Solution_>
        implements InnerBiConstraintStream<A, B> {

    protected final List<BavetAbstractBiConstraintStream<Solution_, A, B>> childStreamList = new ArrayList<>(2);

    public BavetAbstractBiConstraintStream(BavetConstraintFactory<Solution_> constraintFactory,
            RetrievalSemantics retrievalSemantics) {
        super(constraintFactory, retrievalSemantics);
    }

    // ************************************************************************
    // Stream builder methods
    // ************************************************************************

    protected void addChildStream(BavetAbstractBiConstraintStream<Solution_, A, B> childStream) {
        childStreamList.add(childStream);
    }

    // ************************************************************************
    // Filter
    // ************************************************************************

    @Override
    public BavetAbstractBiConstraintStream<Solution_, A, B> filter(BiPredicate<A, B> predicate) {
        BavetFilterBiConstraintStream<Solution_, A, B> stream = new BavetFilterBiConstraintStream<>(constraintFactory, this,
                predicate);
        addChildStream(stream);
        return stream;
    }

    // ************************************************************************
    // Join
    // ************************************************************************

    @Override
    public <C> TriConstraintStream<A, B, C> join(UniConstraintStream<C> otherStream, TriJoiner<A, B, C> joiner) {
        if (!(otherStream instanceof BavetAbstractUniConstraintStream)) {
            throw new IllegalStateException("The streams (" + this + ", " + otherStream
                    + ") are not build from the same " + ConstraintFactory.class.getSimpleName() + ".");
        }
        BavetAbstractUniConstraintStream<Solution_, C> other = (BavetAbstractUniConstraintStream<Solution_, C>) otherStream;
        if (constraintFactory != other.getConstraintFactory()) {
            throw new IllegalStateException("The streams (" + this + ", " + other
                    + ") are build from different constraintFactories (" + constraintFactory + ", "
                    + other.getConstraintFactory()
                    + ").");
        }
        if (!(joiner instanceof AbstractTriJoiner)) {
            throw new IllegalArgumentException("The joiner class (" + joiner.getClass() + ") is not supported.");
        }
        AbstractTriJoiner<A, B, C> castedJoiner = (AbstractTriJoiner<A, B, C>) joiner;
        BavetIndexFactory indexFactory = new BavetIndexFactory(castedJoiner);
        BavetJoinBridgeBiConstraintStream<Solution_, A, B> leftBridge = new BavetJoinBridgeBiConstraintStream<>(
                constraintFactory, this, true, castedJoiner.getLeftCombinedMapping(), indexFactory);
        addChildStream(leftBridge);
        BavetJoinBridgeUniConstraintStream<Solution_, C> rightBridge = new BavetJoinBridgeUniConstraintStream<>(
                constraintFactory, other, false, castedJoiner.getRightCombinedMapping(), indexFactory);
        other.addChildStream(rightBridge);
        BavetJoinTriConstraintStream<Solution_, A, B, C> joinStream = new BavetJoinTriConstraintStream<>(constraintFactory,
                leftBridge, rightBridge);
        leftBridge.setJoinStream(joinStream);
        rightBridge.setJoinStream(joinStream);
        return joinStream;
    }

    @Override
    public <C> TriConstraintStream<A, B, C> join(Class<C> otherClass, TriJoiner<A, B, C>... joiners) {
        if (getRetrievalSemantics() == RetrievalSemantics.STANDARD) {
            return join(constraintFactory.forEach(otherClass), joiners);
        } else {
            return join(constraintFactory.from(otherClass), joiners);
        }
    }

    // ************************************************************************
    // If (not) exists
    // ************************************************************************

    @SafeVarargs
    @Override
    public final <C> BiConstraintStream<A, B> ifExists(Class<C> otherClass, TriJoiner<A, B, C>... joiners) {
        throw new UnsupportedOperationException();
    }

    @SafeVarargs
    @Override
    public final <C> BiConstraintStream<A, B> ifExistsIncludingNullVars(Class<C> otherClass, TriJoiner<A, B, C>... joiners) {
        throw new UnsupportedOperationException();
    }

    @SafeVarargs
    @Override
    public final <C> BiConstraintStream<A, B> ifNotExists(Class<C> otherClass, TriJoiner<A, B, C>... joiners) {
        throw new UnsupportedOperationException();
    }

    @SafeVarargs
    @Override
    public final <C> BiConstraintStream<A, B> ifNotExistsIncludingNullVars(Class<C> otherClass, TriJoiner<A, B, C>... joiners) {
        throw new UnsupportedOperationException();
    }

    // ************************************************************************
    // Group by
    // ************************************************************************

    @Override
    public <ResultContainer_, Result_> UniConstraintStream<Result_> groupBy(
            BiConstraintCollector<A, B, ResultContainer_, Result_> collector) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <ResultContainerA_, ResultA_, ResultContainerB_, ResultB_> BiConstraintStream<ResultA_, ResultB_> groupBy(
            BiConstraintCollector<A, B, ResultContainerA_, ResultA_> collectorA,
            BiConstraintCollector<A, B, ResultContainerB_, ResultB_> collectorB) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <ResultContainerA_, ResultA_, ResultContainerB_, ResultB_, ResultContainerC_, ResultC_>
            TriConstraintStream<ResultA_, ResultB_, ResultC_>
            groupBy(BiConstraintCollector<A, B, ResultContainerA_, ResultA_> collectorA,
                    BiConstraintCollector<A, B, ResultContainerB_, ResultB_> collectorB,
                    BiConstraintCollector<A, B, ResultContainerC_, ResultC_> collectorC) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <ResultContainerA_, ResultA_, ResultContainerB_, ResultB_, ResultContainerC_, ResultC_, ResultContainerD_, ResultD_>
            QuadConstraintStream<ResultA_, ResultB_, ResultC_, ResultD_>
            groupBy(BiConstraintCollector<A, B, ResultContainerA_, ResultA_> collectorA,
                    BiConstraintCollector<A, B, ResultContainerB_, ResultB_> collectorB,
                    BiConstraintCollector<A, B, ResultContainerC_, ResultC_> collectorC,
                    BiConstraintCollector<A, B, ResultContainerD_, ResultD_> collectorD) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <GroupKey_> UniConstraintStream<GroupKey_> groupBy(BiFunction<A, B, GroupKey_> groupKeyMapping) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <GroupKey_, ResultContainerB_, ResultB_, ResultContainerC_, ResultC_>
            TriConstraintStream<GroupKey_, ResultB_, ResultC_> groupBy(BiFunction<A, B, GroupKey_> groupKeyMapping,
                    BiConstraintCollector<A, B, ResultContainerB_, ResultB_> collectorB,
                    BiConstraintCollector<A, B, ResultContainerC_, ResultC_> collectorC) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <GroupKey_, ResultContainerB_, ResultB_, ResultContainerC_, ResultC_, ResultContainerD_, ResultD_>
            QuadConstraintStream<GroupKey_, ResultB_, ResultC_, ResultD_> groupBy(BiFunction<A, B, GroupKey_> groupKeyMapping,
                    BiConstraintCollector<A, B, ResultContainerB_, ResultB_> collectorB,
                    BiConstraintCollector<A, B, ResultContainerC_, ResultC_> collectorC,
                    BiConstraintCollector<A, B, ResultContainerD_, ResultD_> collectorD) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <GroupKey_, ResultContainer_, Result_> BiConstraintStream<GroupKey_, Result_> groupBy(
            BiFunction<A, B, GroupKey_> groupKeyMapping,
            BiConstraintCollector<A, B, ResultContainer_, Result_> collector) {
        BavetGroupBridgeBiConstraintStream<Solution_, A, B, GroupKey_, ResultContainer_, Result_> bridge =
                new BavetGroupBridgeBiConstraintStream<>(constraintFactory, this, groupKeyMapping, collector);
        childStreamList.add(bridge);
        BavetGroupBiConstraintStream<Solution_, GroupKey_, ResultContainer_, Result_> groupStream =
                new BavetGroupBiConstraintStream<>(constraintFactory, bridge, collector.finisher());
        bridge.setGroupStream(groupStream);
        return groupStream;
    }

    @Override
    public <GroupKeyA_, GroupKeyB_> BiConstraintStream<GroupKeyA_, GroupKeyB_> groupBy(
            BiFunction<A, B, GroupKeyA_> groupKeyAMapping, BiFunction<A, B, GroupKeyB_> groupKeyBMapping) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <GroupKeyA_, GroupKeyB_, ResultContainer_, Result_> TriConstraintStream<GroupKeyA_, GroupKeyB_, Result_> groupBy(
            BiFunction<A, B, GroupKeyA_> groupKeyAMapping, BiFunction<A, B, GroupKeyB_> groupKeyBMapping,
            BiConstraintCollector<A, B, ResultContainer_, Result_> collector) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <GroupKeyA_, GroupKeyB_, ResultContainerC_, ResultC_, ResultContainerD_, ResultD_>
            QuadConstraintStream<GroupKeyA_, GroupKeyB_, ResultC_, ResultD_> groupBy(
                    BiFunction<A, B, GroupKeyA_> groupKeyAMapping, BiFunction<A, B, GroupKeyB_> groupKeyBMapping,
                    BiConstraintCollector<A, B, ResultContainerC_, ResultC_> collectorC,
                    BiConstraintCollector<A, B, ResultContainerD_, ResultD_> collectorD) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <GroupKeyA_, GroupKeyB_, GroupKeyC_> TriConstraintStream<GroupKeyA_, GroupKeyB_, GroupKeyC_> groupBy(
            BiFunction<A, B, GroupKeyA_> groupKeyAMapping, BiFunction<A, B, GroupKeyB_> groupKeyBMapping,
            BiFunction<A, B, GroupKeyC_> groupKeyCMapping) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <GroupKeyA_, GroupKeyB_, GroupKeyC_, ResultContainerD_, ResultD_>
            QuadConstraintStream<GroupKeyA_, GroupKeyB_, GroupKeyC_, ResultD_>
            groupBy(BiFunction<A, B, GroupKeyA_> groupKeyAMapping, BiFunction<A, B, GroupKeyB_> groupKeyBMapping,
                    BiFunction<A, B, GroupKeyC_> groupKeyCMapping,
                    BiConstraintCollector<A, B, ResultContainerD_, ResultD_> collectorD) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <GroupKeyA_, GroupKeyB_, GroupKeyC_, GroupKeyD_> QuadConstraintStream<GroupKeyA_, GroupKeyB_, GroupKeyC_, GroupKeyD_>
            groupBy(BiFunction<A, B, GroupKeyA_> groupKeyAMapping, BiFunction<A, B, GroupKeyB_> groupKeyBMapping,
                    BiFunction<A, B, GroupKeyC_> groupKeyCMapping, BiFunction<A, B, GroupKeyD_> groupKeyDMapping) {
        throw new UnsupportedOperationException();
    }

    // ************************************************************************
    // Operations with duplicate tuple possibility
    // ************************************************************************

    @Override
    public <ResultA_> UniConstraintStream<ResultA_> map(BiFunction<A, B, ResultA_> mapping) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <ResultB_> BiConstraintStream<A, ResultB_> flattenLast(Function<B, Iterable<ResultB_>> mapping) {
        throw new UnsupportedOperationException();
    }

    // ************************************************************************
    // Penalize/reward
    // ************************************************************************

    @Override
    public final Constraint impactScore(String constraintPackage, String constraintName, Score<?> constraintWeight,
            ScoreImpactType impactType) {
        BavetConstraint<Solution_> constraint = buildConstraint(constraintPackage, constraintName, constraintWeight,
                impactType);
        BavetScoringBiConstraintStream<Solution_, A, B> stream = new BavetScoringBiConstraintStream<>(constraintFactory,
                this, constraint);
        childStreamList.add(stream);
        return constraint;
    }

    @Override
    public final Constraint impactScore(String constraintPackage, String constraintName, Score<?> constraintWeight,
            ToIntBiFunction<A, B> matchWeigher, ScoreImpactType impactType) {
        BavetConstraint<Solution_> constraint = buildConstraint(constraintPackage, constraintName, constraintWeight,
                impactType);
        BavetScoringBiConstraintStream<Solution_, A, B> stream = new BavetScoringBiConstraintStream<>(constraintFactory,
                this, constraint, matchWeigher);
        childStreamList.add(stream);
        return constraint;
    }

    @Override
    public final Constraint impactScoreLong(String constraintPackage, String constraintName,
            Score<?> constraintWeight, ToLongBiFunction<A, B> matchWeigher, ScoreImpactType impactType) {
        BavetConstraint<Solution_> constraint = buildConstraint(constraintPackage, constraintName, constraintWeight,
                impactType);
        BavetScoringBiConstraintStream<Solution_, A, B> stream = new BavetScoringBiConstraintStream<>(constraintFactory,
                this, constraint, matchWeigher);
        childStreamList.add(stream);
        return constraint;
    }

    @Override
    public final Constraint impactScoreBigDecimal(String constraintPackage, String constraintName,
            Score<?> constraintWeight, BiFunction<A, B, BigDecimal> matchWeigher, ScoreImpactType impactType) {
        BavetConstraint<Solution_> constraint = buildConstraint(constraintPackage, constraintName, constraintWeight,
                impactType);
        BavetScoringBiConstraintStream<Solution_, A, B> stream = new BavetScoringBiConstraintStream<>(constraintFactory,
                this, constraint, matchWeigher);
        childStreamList.add(stream);
        return constraint;
    }

    @Override
    public final Constraint impactScoreConfigurable(String constraintPackage, String constraintName,
            ScoreImpactType impactType) {
        BavetConstraint<Solution_> constraint = buildConstraintConfigurable(constraintPackage, constraintName,
                impactType);
        BavetScoringBiConstraintStream<Solution_, A, B> stream = new BavetScoringBiConstraintStream<>(constraintFactory,
                this, constraint);
        childStreamList.add(stream);
        return constraint;
    }

    @Override
    public final Constraint impactScoreConfigurable(String constraintPackage, String constraintName,
            ToIntBiFunction<A, B> matchWeigher, ScoreImpactType impactType) {
        BavetConstraint<Solution_> constraint = buildConstraintConfigurable(constraintPackage, constraintName,
                impactType);
        BavetScoringBiConstraintStream<Solution_, A, B> stream = new BavetScoringBiConstraintStream<>(constraintFactory,
                this, constraint, matchWeigher);
        childStreamList.add(stream);
        return constraint;
    }

    @Override
    public final Constraint impactScoreConfigurableLong(String constraintPackage, String constraintName,
            ToLongBiFunction<A, B> matchWeigher, ScoreImpactType impactType) {
        BavetConstraint<Solution_> constraint = buildConstraintConfigurable(constraintPackage, constraintName,
                impactType);
        BavetScoringBiConstraintStream<Solution_, A, B> stream = new BavetScoringBiConstraintStream<>(constraintFactory,
                this, constraint, matchWeigher);
        childStreamList.add(stream);
        return constraint;
    }

    @Override
    public final Constraint impactScoreConfigurableBigDecimal(String constraintPackage, String constraintName,
            BiFunction<A, B, BigDecimal> matchWeigher, ScoreImpactType impactType) {
        BavetConstraint<Solution_> constraint = buildConstraintConfigurable(constraintPackage, constraintName,
                impactType);
        BavetScoringBiConstraintStream<Solution_, A, B> stream = new BavetScoringBiConstraintStream<>(constraintFactory,
                this, constraint, matchWeigher);
        childStreamList.add(stream);
        return constraint;
    }

    // ************************************************************************
    // Node creation
    // ************************************************************************

    public BavetAbstractBiNode<A, B> createNodeChain(BavetNodeBuildPolicy<Solution_> buildPolicy,
            Score<?> constraintWeight, BavetAbstractBiNode<A, B> parentNode) {
        BavetAbstractBiNode<A, B> node = createNode(buildPolicy, constraintWeight, parentNode);
        node = processNode(buildPolicy, parentNode, node);
        createChildNodeChains(buildPolicy, constraintWeight, node);
        return node;
    }

    protected BavetAbstractBiNode<A, B> processNode(BavetNodeBuildPolicy<Solution_> buildPolicy,
            BavetAbstractBiNode<A, B> parentNode, BavetAbstractBiNode<A, B> node) {
        BavetAbstractBiNode<A, B> sharedNode = buildPolicy.retrieveSharedNode(node);
        if (sharedNode != node) { // Share node
            return sharedNode;
        }
        if (parentNode != null) { // TODO remove null check and don't go through this for from and joins
            parentNode.addChildNode(node);
        }
        return node;
    }

    protected void createChildNodeChains(BavetNodeBuildPolicy<Solution_> buildPolicy, Score<?> constraintWeight,
            BavetAbstractBiNode<A, B> node) {
        if (childStreamList.isEmpty()) {
            throw new IllegalStateException("The stream (" + this + ") leads to nowhere.\n"
                    + "Maybe don't create it.");
        }
        for (BavetAbstractBiConstraintStream<Solution_, A, B> childStream : childStreamList) {
            childStream.createNodeChain(buildPolicy, constraintWeight, node);
        }
    }

    protected abstract BavetAbstractBiNode<A, B> createNode(BavetNodeBuildPolicy<Solution_> buildPolicy,
            Score<?> constraintWeight, BavetAbstractBiNode<A, B> parentNode);
}
