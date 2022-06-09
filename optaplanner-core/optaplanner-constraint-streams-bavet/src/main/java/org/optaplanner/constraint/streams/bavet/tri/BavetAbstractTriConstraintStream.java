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

package org.optaplanner.constraint.streams.bavet.tri;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import org.optaplanner.constraint.streams.bavet.BavetConstraintFactory;
import org.optaplanner.constraint.streams.bavet.bi.BavetGroupBiConstraintStream;
import org.optaplanner.constraint.streams.bavet.bi.BiTuple;
import org.optaplanner.constraint.streams.bavet.common.BavetAbstractConstraintStream;
import org.optaplanner.constraint.streams.bavet.quad.BavetGroupQuadConstraintStream;
import org.optaplanner.constraint.streams.bavet.quad.BavetJoinQuadConstraintStream;
import org.optaplanner.constraint.streams.bavet.quad.QuadTuple;
import org.optaplanner.constraint.streams.bavet.uni.BavetAbstractUniConstraintStream;
import org.optaplanner.constraint.streams.bavet.uni.BavetGroupUniConstraintStream;
import org.optaplanner.constraint.streams.bavet.uni.BavetIfExistsBridgeUniConstraintStream;
import org.optaplanner.constraint.streams.bavet.uni.BavetJoinBridgeUniConstraintStream;
import org.optaplanner.constraint.streams.bavet.uni.UniTuple;
import org.optaplanner.constraint.streams.common.RetrievalSemantics;
import org.optaplanner.constraint.streams.common.ScoreImpactType;
import org.optaplanner.constraint.streams.common.quad.QuadJoinerComber;
import org.optaplanner.constraint.streams.common.tri.InnerTriConstraintStream;
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

public abstract class BavetAbstractTriConstraintStream<Solution_, A, B, C> extends BavetAbstractConstraintStream<Solution_>
        implements InnerTriConstraintStream<A, B, C> {

    protected final List<BavetAbstractTriConstraintStream<Solution_, A, B, C>> childStreamList = new ArrayList<>(2);

    public BavetAbstractTriConstraintStream(BavetConstraintFactory<Solution_> constraintFactory,
            RetrievalSemantics retrievalSemantics) {
        super(constraintFactory, retrievalSemantics);
    }

    public List<BavetAbstractTriConstraintStream<Solution_, A, B, C>> getChildStreamList() {
        return childStreamList;
    }

    // ************************************************************************
    // Stream builder methods
    // ************************************************************************

    public <Stream_ extends BavetAbstractTriConstraintStream<Solution_, A, B, C>> Stream_ shareAndAddChild(
            Stream_ stream) {
        return constraintFactory.share(stream, childStreamList::add);
    }

    // ************************************************************************
    // Filter
    // ************************************************************************

    @Override
    public BavetAbstractTriConstraintStream<Solution_, A, B, C> filter(TriPredicate<A, B, C> predicate) {
        return shareAndAddChild(
                new BavetFilterTriConstraintStream<>(constraintFactory, this, predicate));
    }

    // ************************************************************************
    // Join
    // ************************************************************************

    @Override
    @SafeVarargs
    public final <D> QuadConstraintStream<A, B, C, D> join(UniConstraintStream<D> otherStream,
            QuadJoiner<A, B, C, D>... joiners) {
        BavetAbstractUniConstraintStream<Solution_, D> other = assertBavetUniConstraintStream(otherStream);
        QuadJoinerComber<A, B, C, D> joinerComber = QuadJoinerComber.comb(joiners);

        BavetJoinBridgeTriConstraintStream<Solution_, A, B, C> leftBridge =
                new BavetJoinBridgeTriConstraintStream<>(constraintFactory, this, true);
        BavetJoinBridgeUniConstraintStream<Solution_, D> rightBridge =
                new BavetJoinBridgeUniConstraintStream<>(constraintFactory, other, false);
        BavetJoinQuadConstraintStream<Solution_, A, B, C, D> joinStream =
                new BavetJoinQuadConstraintStream<>(constraintFactory, leftBridge, rightBridge,
                        joinerComber.getMergedJoiner());
        leftBridge.setJoinStream(joinStream);
        rightBridge.setJoinStream(joinStream);

        joinStream = constraintFactory.share(joinStream, joinStream_ -> {
            // Connect the bridges upstream, as it is an actual new join.
            getChildStreamList().add(leftBridge);
            other.getChildStreamList().add(rightBridge);
        });
        if (joinerComber.getMergedFiltering() == null) {
            return joinStream;
        } else {
            return joinStream.filter(joinerComber.getMergedFiltering());
        }
    }

    // ************************************************************************
    // If (not) exists
    // ************************************************************************

    @SafeVarargs
    @Override
    public final <D> TriConstraintStream<A, B, C> ifExists(Class<D> otherClass, QuadJoiner<A, B, C, D>... joiners) {
        if (getRetrievalSemantics() == RetrievalSemantics.STANDARD) {
            return ifExists(constraintFactory.forEach(otherClass), joiners);
        } else {
            // Calls fromUnfiltered() for backward compatibility only
            return ifExists(constraintFactory.fromUnfiltered(otherClass), joiners);
        }
    }

    @SafeVarargs
    @Override
    public final <D> TriConstraintStream<A, B, C> ifExistsIncludingNullVars(Class<D> otherClass,
            QuadJoiner<A, B, C, D>... joiners) {
        if (getRetrievalSemantics() == RetrievalSemantics.STANDARD) {
            return ifExists(constraintFactory.forEachIncludingNullVars(otherClass), joiners);
        } else {
            return ifExists(constraintFactory.fromUnfiltered(otherClass), joiners);
        }
    }

    @SafeVarargs
    public final <D> TriConstraintStream<A, B, C> ifExists(UniConstraintStream<D> otherStream,
            QuadJoiner<A, B, C, D>... joiners) {
        return ifExistsOrNot(true, otherStream, joiners);
    }

    @SafeVarargs
    @Override
    public final <D> TriConstraintStream<A, B, C> ifNotExists(Class<D> otherClass, QuadJoiner<A, B, C, D>... joiners) {
        if (getRetrievalSemantics() == RetrievalSemantics.STANDARD) {
            return ifNotExists(constraintFactory.forEach(otherClass), joiners);
        } else {
            // Calls fromUnfiltered() for backward compatibility only
            return ifNotExists(constraintFactory.fromUnfiltered(otherClass), joiners);
        }
    }

    @SafeVarargs
    @Override
    public final <D> TriConstraintStream<A, B, C> ifNotExistsIncludingNullVars(Class<D> otherClass,
            QuadJoiner<A, B, C, D>... joiners) {
        if (getRetrievalSemantics() == RetrievalSemantics.STANDARD) {
            return ifNotExists(constraintFactory.forEachIncludingNullVars(otherClass), joiners);
        } else {
            return ifNotExists(constraintFactory.fromUnfiltered(otherClass), joiners);
        }
    }

    @SafeVarargs
    public final <D> TriConstraintStream<A, B, C> ifNotExists(UniConstraintStream<D> otherStream,
            QuadJoiner<A, B, C, D>... joiners) {
        return ifExistsOrNot(false, otherStream, joiners);
    }

    private <D> TriConstraintStream<A, B, C> ifExistsOrNot(boolean shouldExist, UniConstraintStream<D> otherStream,
            QuadJoiner<A, B, C, D>[] joiners) {
        BavetAbstractUniConstraintStream<Solution_, D> other = assertBavetUniConstraintStream(otherStream);
        QuadJoinerComber<A, B, C, D> joinerComber = QuadJoinerComber.comb(joiners);
        BavetIfExistsBridgeUniConstraintStream<Solution_, D> parentBridgeD = other.shareAndAddChild(
                new BavetIfExistsBridgeUniConstraintStream<>(constraintFactory, other));
        return constraintFactory.share(
                new BavetIfExistsTriConstraintStream<>(constraintFactory, this, parentBridgeD,
                        shouldExist, joinerComber.getMergedJoiner(), joinerComber.getMergedFiltering()),
                childStreamList::add);
    }

    // ************************************************************************
    // Group by
    // ************************************************************************

    @Override
    public <ResultContainer_, Result_> UniConstraintStream<Result_> groupBy(
            TriConstraintCollector<A, B, C, ResultContainer_, Result_> collector) {
        TriGroupNodeConstructor<A, B, C, UniTuple<Result_>> nodeConstructor =
                (inputStoreIndex, tupleLifecycle, outputStoreSize) -> new Group0Mapping1CollectorTriNode<>(inputStoreIndex,
                        collector, tupleLifecycle, outputStoreSize);
        return buildUniGroupBy(nodeConstructor);
    }

    private <NewA> UniConstraintStream<NewA> buildUniGroupBy(TriGroupNodeConstructor<A, B, C, UniTuple<NewA>> nodeConstructor) {
        BavetUniGroupBridgeTriConstraintStream<Solution_, A, B, C, NewA> bridge = shareAndAddChild(
                new BavetUniGroupBridgeTriConstraintStream<>(constraintFactory, this, nodeConstructor));
        return constraintFactory.share(
                new BavetGroupUniConstraintStream<>(constraintFactory, bridge),
                bridge::setGroupStream);
    }

    @Override
    public <ResultContainerA_, ResultA_, ResultContainerB_, ResultB_> BiConstraintStream<ResultA_, ResultB_> groupBy(
            TriConstraintCollector<A, B, C, ResultContainerA_, ResultA_> collectorA,
            TriConstraintCollector<A, B, C, ResultContainerB_, ResultB_> collectorB) {
        TriGroupNodeConstructor<A, B, C, BiTuple<ResultA_, ResultB_>> nodeConstructor =
                (inputStoreIndex, tupleLifecycle, outputStoreSize) -> new Group0Mapping2CollectorTriNode<>(inputStoreIndex,
                        collectorA, collectorB, tupleLifecycle, outputStoreSize);
        return buildBiGroupBy(nodeConstructor);
    }

    private <NewA, NewB> BiConstraintStream<NewA, NewB>
            buildBiGroupBy(TriGroupNodeConstructor<A, B, C, BiTuple<NewA, NewB>> nodeConstructor) {
        BavetBiGroupBridgeTriConstraintStream<Solution_, A, B, C, NewA, NewB> bridge = shareAndAddChild(
                new BavetBiGroupBridgeTriConstraintStream<>(constraintFactory, this, nodeConstructor));
        return constraintFactory.share(
                new BavetGroupBiConstraintStream<>(constraintFactory, bridge),
                bridge::setGroupStream);
    }

    @Override
    public <ResultContainerA_, ResultA_, ResultContainerB_, ResultB_, ResultContainerC_, ResultC_>
            TriConstraintStream<ResultA_, ResultB_, ResultC_>
            groupBy(TriConstraintCollector<A, B, C, ResultContainerA_, ResultA_> collectorA,
                    TriConstraintCollector<A, B, C, ResultContainerB_, ResultB_> collectorB,
                    TriConstraintCollector<A, B, C, ResultContainerC_, ResultC_> collectorC) {
        TriGroupNodeConstructor<A, B, C, TriTuple<ResultA_, ResultB_, ResultC_>> nodeConstructor =
                (inputStoreIndex, tupleLifecycle, outputStoreSize) -> new Group0Mapping3CollectorTriNode<>(inputStoreIndex,
                        collectorA, collectorB, collectorC, tupleLifecycle, outputStoreSize);
        return buildTriGroupBy(nodeConstructor);
    }

    private <NewA, NewB, NewC> TriConstraintStream<NewA, NewB, NewC>
            buildTriGroupBy(TriGroupNodeConstructor<A, B, C, TriTuple<NewA, NewB, NewC>> nodeConstructor) {
        BavetTriGroupBridgeTriConstraintStream<Solution_, A, B, C, NewA, NewB, NewC> bridge = shareAndAddChild(
                new BavetTriGroupBridgeTriConstraintStream<>(constraintFactory, this, nodeConstructor));
        return constraintFactory.share(
                new BavetGroupTriConstraintStream<>(constraintFactory, bridge),
                bridge::setGroupStream);
    }

    @Override
    public <ResultContainerA_, ResultA_, ResultContainerB_, ResultB_, ResultContainerC_, ResultC_, ResultContainerD_, ResultD_>
            QuadConstraintStream<ResultA_, ResultB_, ResultC_, ResultD_>
            groupBy(TriConstraintCollector<A, B, C, ResultContainerA_, ResultA_> collectorA,
                    TriConstraintCollector<A, B, C, ResultContainerB_, ResultB_> collectorB,
                    TriConstraintCollector<A, B, C, ResultContainerC_, ResultC_> collectorC,
                    TriConstraintCollector<A, B, C, ResultContainerD_, ResultD_> collectorD) {
        TriGroupNodeConstructor<A, B, C, QuadTuple<ResultA_, ResultB_, ResultC_, ResultD_>> nodeConstructor =
                (inputStoreIndex, tupleLifecycle, outputStoreSize) -> new Group0Mapping4CollectorTriNode<>(inputStoreIndex,
                        collectorA, collectorB, collectorC, collectorD, tupleLifecycle, outputStoreSize);
        return buildQuadGroupBy(nodeConstructor);
    }

    private <NewA, NewB, NewC, NewD> QuadConstraintStream<NewA, NewB, NewC, NewD>
            buildQuadGroupBy(TriGroupNodeConstructor<A, B, C, QuadTuple<NewA, NewB, NewC, NewD>> nodeConstructor) {
        BavetQuadGroupBridgeTriConstraintStream<Solution_, A, B, C, NewA, NewB, NewC, NewD> bridge = shareAndAddChild(
                new BavetQuadGroupBridgeTriConstraintStream<>(constraintFactory, this, nodeConstructor));
        return constraintFactory.share(
                new BavetGroupQuadConstraintStream<>(constraintFactory, bridge),
                bridge::setGroupStream);
    }

    @Override
    public <GroupKey_> UniConstraintStream<GroupKey_> groupBy(TriFunction<A, B, C, GroupKey_> groupKeyMapping) {
        TriGroupNodeConstructor<A, B, C, UniTuple<GroupKey_>> nodeConstructor =
                (inputStoreIndex, tupleLifecycle, outputStoreSize) -> new Group1Mapping0CollectorTriNode<>(groupKeyMapping,
                        inputStoreIndex, tupleLifecycle, outputStoreSize);
        return buildUniGroupBy(nodeConstructor);
    }

    @Override
    public <GroupKey_, ResultContainerB_, ResultB_, ResultContainerC_, ResultC_>
            TriConstraintStream<GroupKey_, ResultB_, ResultC_> groupBy(TriFunction<A, B, C, GroupKey_> groupKeyMapping,
                    TriConstraintCollector<A, B, C, ResultContainerB_, ResultB_> collectorB,
                    TriConstraintCollector<A, B, C, ResultContainerC_, ResultC_> collectorC) {
        TriGroupNodeConstructor<A, B, C, TriTuple<GroupKey_, ResultB_, ResultC_>> nodeConstructor =
                (inputStoreIndex, tupleLifecycle, outputStoreSize) -> new Group1Mapping2CollectorTriNode<>(groupKeyMapping,
                        inputStoreIndex, collectorB, collectorC, tupleLifecycle, outputStoreSize);
        return buildTriGroupBy(nodeConstructor);
    }

    @Override
    public <GroupKey_, ResultContainerB_, ResultB_, ResultContainerC_, ResultC_, ResultContainerD_, ResultD_>
            QuadConstraintStream<GroupKey_, ResultB_, ResultC_, ResultD_>
            groupBy(TriFunction<A, B, C, GroupKey_> groupKeyMapping,
                    TriConstraintCollector<A, B, C, ResultContainerB_, ResultB_> collectorB,
                    TriConstraintCollector<A, B, C, ResultContainerC_, ResultC_> collectorC,
                    TriConstraintCollector<A, B, C, ResultContainerD_, ResultD_> collectorD) {
        TriGroupNodeConstructor<A, B, C, QuadTuple<GroupKey_, ResultB_, ResultC_, ResultD_>> nodeConstructor =
                (inputStoreIndex, tupleLifecycle, outputStoreSize) -> new Group1Mapping3CollectorTriNode<>(groupKeyMapping,
                        inputStoreIndex, collectorB, collectorC, collectorD, tupleLifecycle, outputStoreSize);
        return buildQuadGroupBy(nodeConstructor);
    }

    @Override
    public <GroupKey_, ResultContainer_, Result_> BiConstraintStream<GroupKey_, Result_> groupBy(
            TriFunction<A, B, C, GroupKey_> groupKeyMapping,
            TriConstraintCollector<A, B, C, ResultContainer_, Result_> collector) {
        TriGroupNodeConstructor<A, B, C, BiTuple<GroupKey_, Result_>> nodeConstructor =
                (inputStoreIndex, tupleLifecycle, outputStoreSize) -> new Group1Mapping1CollectorTriNode<>(groupKeyMapping,
                        inputStoreIndex, collector, tupleLifecycle, outputStoreSize);
        return buildBiGroupBy(nodeConstructor);
    }

    @Override
    public <GroupKeyA_, GroupKeyB_> BiConstraintStream<GroupKeyA_, GroupKeyB_> groupBy(
            TriFunction<A, B, C, GroupKeyA_> groupKeyAMapping,
            TriFunction<A, B, C, GroupKeyB_> groupKeyBMapping) {
        TriGroupNodeConstructor<A, B, C, BiTuple<GroupKeyA_, GroupKeyB_>> nodeConstructor =
                (inputStoreIndex, tupleLifecycle, outputStoreSize) -> new Group2Mapping0CollectorTriNode<>(groupKeyAMapping,
                        groupKeyBMapping, inputStoreIndex, tupleLifecycle, outputStoreSize);
        return buildBiGroupBy(nodeConstructor);
    }

    @Override
    public <GroupKeyA_, GroupKeyB_, ResultContainer_, Result_> TriConstraintStream<GroupKeyA_, GroupKeyB_, Result_> groupBy(
            TriFunction<A, B, C, GroupKeyA_> groupKeyAMapping,
            TriFunction<A, B, C, GroupKeyB_> groupKeyBMapping,
            TriConstraintCollector<A, B, C, ResultContainer_, Result_> collector) {
        TriGroupNodeConstructor<A, B, C, TriTuple<GroupKeyA_, GroupKeyB_, Result_>> nodeConstructor =
                (inputStoreIndex, tupleLifecycle, outputStoreSize) -> new Group2Mapping1CollectorTriNode<>(groupKeyAMapping,
                        groupKeyBMapping, inputStoreIndex, collector, tupleLifecycle, outputStoreSize);
        return buildTriGroupBy(nodeConstructor);
    }

    @Override
    public <GroupKeyA_, GroupKeyB_, ResultContainerC_, ResultC_, ResultContainerD_, ResultD_>
            QuadConstraintStream<GroupKeyA_, GroupKeyB_, ResultC_, ResultD_> groupBy(
                    TriFunction<A, B, C, GroupKeyA_> groupKeyAMapping,
                    TriFunction<A, B, C, GroupKeyB_> groupKeyBMapping,
                    TriConstraintCollector<A, B, C, ResultContainerC_, ResultC_> collectorC,
                    TriConstraintCollector<A, B, C, ResultContainerD_, ResultD_> collectorD) {
        TriGroupNodeConstructor<A, B, C, QuadTuple<GroupKeyA_, GroupKeyB_, ResultC_, ResultD_>> nodeConstructor =
                (inputStoreIndex, tupleLifecycle, outputStoreSize) -> new Group2Mapping2CollectorTriNode<>(groupKeyAMapping,
                        groupKeyBMapping, inputStoreIndex, collectorC, collectorD, tupleLifecycle, outputStoreSize);
        return buildQuadGroupBy(nodeConstructor);
    }

    @Override
    public <GroupKeyA_, GroupKeyB_, GroupKeyC_> TriConstraintStream<GroupKeyA_, GroupKeyB_, GroupKeyC_> groupBy(
            TriFunction<A, B, C, GroupKeyA_> groupKeyAMapping, TriFunction<A, B, C, GroupKeyB_> groupKeyBMapping,
            TriFunction<A, B, C, GroupKeyC_> groupKeyCMapping) {
        TriGroupNodeConstructor<A, B, C, TriTuple<GroupKeyA_, GroupKeyB_, GroupKeyC_>> nodeConstructor =
                (inputStoreIndex, tupleLifecycle, outputStoreSize) -> new Group3Mapping0CollectorTriNode<>(groupKeyAMapping,
                        groupKeyBMapping, groupKeyCMapping, inputStoreIndex, tupleLifecycle, outputStoreSize);
        return buildTriGroupBy(nodeConstructor);
    }

    @Override
    public <GroupKeyA_, GroupKeyB_, GroupKeyC_, ResultContainerD_, ResultD_>
            QuadConstraintStream<GroupKeyA_, GroupKeyB_, GroupKeyC_, ResultD_>
            groupBy(TriFunction<A, B, C, GroupKeyA_> groupKeyAMapping,
                    TriFunction<A, B, C, GroupKeyB_> groupKeyBMapping,
                    TriFunction<A, B, C, GroupKeyC_> groupKeyCMapping,
                    TriConstraintCollector<A, B, C, ResultContainerD_, ResultD_> collectorD) {
        TriGroupNodeConstructor<A, B, C, QuadTuple<GroupKeyA_, GroupKeyB_, GroupKeyC_, ResultD_>> nodeConstructor =
                (inputStoreIndex, tupleLifecycle, outputStoreSize) -> new Group3Mapping1CollectorTriNode<>(groupKeyAMapping,
                        groupKeyBMapping, groupKeyCMapping, inputStoreIndex, collectorD, tupleLifecycle, outputStoreSize);
        return buildQuadGroupBy(nodeConstructor);
    }

    @Override
    public <GroupKeyA_, GroupKeyB_, GroupKeyC_, GroupKeyD_> QuadConstraintStream<GroupKeyA_, GroupKeyB_, GroupKeyC_, GroupKeyD_>
            groupBy(TriFunction<A, B, C, GroupKeyA_> groupKeyAMapping,
                    TriFunction<A, B, C, GroupKeyB_> groupKeyBMapping,
                    TriFunction<A, B, C, GroupKeyC_> groupKeyCMapping,
                    TriFunction<A, B, C, GroupKeyD_> groupKeyDMapping) {
        TriGroupNodeConstructor<A, B, C, QuadTuple<GroupKeyA_, GroupKeyB_, GroupKeyC_, GroupKeyD_>> nodeConstructor =
                (inputStoreIndex, tupleLifecycle, outputStoreSize) -> new Group4Mapping0CollectorTriNode<>(groupKeyAMapping,
                        groupKeyBMapping, groupKeyCMapping, groupKeyDMapping, inputStoreIndex, tupleLifecycle, outputStoreSize);
        return buildQuadGroupBy(nodeConstructor);
    }

    // ************************************************************************
    // Operations with duplicate tuple possibility
    // ************************************************************************

    @Override
    public <ResultA_> UniConstraintStream<ResultA_> map(TriFunction<A, B, C, ResultA_> mapping) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <ResultC_> TriConstraintStream<A, B, ResultC_> flattenLast(Function<C, Iterable<ResultC_>> mapping) {
        throw new UnsupportedOperationException();
    }

    // ************************************************************************
    // Penalize/reward
    // ************************************************************************

    @Override
    public final Constraint impactScore(String constraintPackage, String constraintName, Score<?> constraintWeight,
            ScoreImpactType impactType) {
        BavetScoringTriConstraintStream<Solution_, A, B, C> stream = shareAndAddChild(
                new BavetScoringTriConstraintStream<>(constraintFactory, this));
        return buildConstraint(constraintPackage, constraintName, constraintWeight,
                impactType, stream);
    }

    @Override
    public final Constraint impactScore(String constraintPackage, String constraintName, Score<?> constraintWeight,
            ToIntTriFunction<A, B, C> matchWeigher, ScoreImpactType impactType) {
        BavetScoringTriConstraintStream<Solution_, A, B, C> stream = shareAndAddChild(
                new BavetScoringTriConstraintStream<>(constraintFactory, this, matchWeigher));
        return buildConstraint(constraintPackage, constraintName, constraintWeight,
                impactType, stream);
    }

    @Override
    public final Constraint impactScoreLong(String constraintPackage, String constraintName,
            Score<?> constraintWeight, ToLongTriFunction<A, B, C> matchWeigher, ScoreImpactType impactType) {
        BavetScoringTriConstraintStream<Solution_, A, B, C> stream = shareAndAddChild(
                new BavetScoringTriConstraintStream<>(constraintFactory, this, matchWeigher));
        return buildConstraint(constraintPackage, constraintName, constraintWeight,
                impactType, stream);
    }

    @Override
    public final Constraint impactScoreBigDecimal(String constraintPackage, String constraintName,
            Score<?> constraintWeight, TriFunction<A, B, C, BigDecimal> matchWeigher, ScoreImpactType impactType) {
        BavetScoringTriConstraintStream<Solution_, A, B, C> stream = shareAndAddChild(
                new BavetScoringTriConstraintStream<>(constraintFactory, this, matchWeigher));
        return buildConstraint(constraintPackage, constraintName, constraintWeight,
                impactType, stream);
    }

    @Override
    public final Constraint impactScoreConfigurable(String constraintPackage, String constraintName,
            ScoreImpactType impactType) {
        BavetScoringTriConstraintStream<Solution_, A, B, C> stream = shareAndAddChild(
                new BavetScoringTriConstraintStream<>(constraintFactory, this));
        return buildConstraintConfigurable(constraintPackage, constraintName,
                impactType, stream);
    }

    @Override
    public final Constraint impactScoreConfigurable(String constraintPackage, String constraintName,
            ToIntTriFunction<A, B, C> matchWeigher, ScoreImpactType impactType) {
        BavetScoringTriConstraintStream<Solution_, A, B, C> stream = shareAndAddChild(
                new BavetScoringTriConstraintStream<>(constraintFactory, this, matchWeigher));
        return buildConstraintConfigurable(constraintPackage, constraintName,
                impactType, stream);
    }

    @Override
    public final Constraint impactScoreConfigurableLong(String constraintPackage, String constraintName,
            ToLongTriFunction<A, B, C> matchWeigher, ScoreImpactType impactType) {
        BavetScoringTriConstraintStream<Solution_, A, B, C> stream = shareAndAddChild(
                new BavetScoringTriConstraintStream<>(constraintFactory, this, matchWeigher));
        return buildConstraintConfigurable(constraintPackage, constraintName,
                impactType, stream);
    }

    @Override
    public final Constraint impactScoreConfigurableBigDecimal(String constraintPackage, String constraintName,
            TriFunction<A, B, C, BigDecimal> matchWeigher, ScoreImpactType impactType) {
        BavetScoringTriConstraintStream<Solution_, A, B, C> stream = shareAndAddChild(
                new BavetScoringTriConstraintStream<>(constraintFactory, this, matchWeigher));
        return buildConstraintConfigurable(constraintPackage, constraintName,
                impactType, stream);
    }

}
