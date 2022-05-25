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

package org.optaplanner.constraint.streams.bavet.bi;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.ToIntBiFunction;
import java.util.function.ToLongBiFunction;

import org.optaplanner.constraint.streams.bavet.BavetConstraintFactory;
import org.optaplanner.constraint.streams.bavet.common.BavetAbstractConstraintStream;
import org.optaplanner.constraint.streams.bavet.quad.BavetGroupQuadConstraintStream;
import org.optaplanner.constraint.streams.bavet.quad.QuadTuple;
import org.optaplanner.constraint.streams.bavet.tri.BavetGroupTriConstraintStream;
import org.optaplanner.constraint.streams.bavet.tri.BavetJoinTriConstraintStream;
import org.optaplanner.constraint.streams.bavet.tri.TriTuple;
import org.optaplanner.constraint.streams.bavet.uni.BavetAbstractUniConstraintStream;
import org.optaplanner.constraint.streams.bavet.uni.BavetGroupUniConstraintStream;
import org.optaplanner.constraint.streams.bavet.uni.BavetIfExistsBridgeUniConstraintStream;
import org.optaplanner.constraint.streams.bavet.uni.BavetJoinBridgeUniConstraintStream;
import org.optaplanner.constraint.streams.bavet.uni.UniTuple;
import org.optaplanner.constraint.streams.bi.InnerBiConstraintStream;
import org.optaplanner.constraint.streams.common.RetrievalSemantics;
import org.optaplanner.constraint.streams.common.ScoreImpactType;
import org.optaplanner.constraint.streams.tri.TriJoinerComber;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.stream.Constraint;
import org.optaplanner.core.api.score.stream.bi.BiConstraintCollector;
import org.optaplanner.core.api.score.stream.bi.BiConstraintStream;
import org.optaplanner.core.api.score.stream.quad.QuadConstraintStream;
import org.optaplanner.core.api.score.stream.tri.TriConstraintStream;
import org.optaplanner.core.api.score.stream.tri.TriJoiner;
import org.optaplanner.core.api.score.stream.uni.UniConstraintStream;

public abstract class BavetAbstractBiConstraintStream<Solution_, A, B> extends BavetAbstractConstraintStream<Solution_>
        implements InnerBiConstraintStream<A, B> {

    protected final List<BavetAbstractBiConstraintStream<Solution_, A, B>> childStreamList = new ArrayList<>(2);

    public BavetAbstractBiConstraintStream(BavetConstraintFactory<Solution_> constraintFactory,
                                           RetrievalSemantics retrievalSemantics) {
        super(constraintFactory, retrievalSemantics);
    }

    public List<BavetAbstractBiConstraintStream<Solution_, A, B>> getChildStreamList() {
        return childStreamList;
    }

    // ************************************************************************
    // Stream builder methods
    // ************************************************************************

    public <Stream_ extends BavetAbstractBiConstraintStream<Solution_, A, B>> Stream_ shareAndAddChild(
            Stream_ stream) {
        return constraintFactory.share(stream, childStreamList::add);
    }

    // ************************************************************************
    // Filter
    // ************************************************************************

    @Override
    public BavetAbstractBiConstraintStream<Solution_, A, B> filter(BiPredicate<A, B> predicate) {
        return shareAndAddChild(
                new BavetFilterBiConstraintStream<>(constraintFactory, this, predicate));
    }

    // ************************************************************************
    // Join
    // ************************************************************************

    @Override
    @SafeVarargs
    public final <C> TriConstraintStream<A, B, C> join(UniConstraintStream<C> otherStream,
            TriJoiner<A, B, C>... joiners) {
        BavetAbstractUniConstraintStream<Solution_, C> other = assertBavetUniConstraintStream(otherStream);
        TriJoinerComber<A, B, C> joinerComber = TriJoinerComber.comb(joiners);

        BavetJoinBridgeBiConstraintStream<Solution_, A, B> leftBridge =
                new BavetJoinBridgeBiConstraintStream<>(constraintFactory, this, true);
        BavetJoinBridgeUniConstraintStream<Solution_, C> rightBridge =
                new BavetJoinBridgeUniConstraintStream<>(constraintFactory, other, false);
        BavetJoinTriConstraintStream<Solution_, A, B, C> joinStream =
                new BavetJoinTriConstraintStream<>(constraintFactory, leftBridge, rightBridge,
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
    public final <C> BiConstraintStream<A, B> ifExists(Class<C> otherClass, TriJoiner<A, B, C>... joiners) {
        if (getRetrievalSemantics() == RetrievalSemantics.STANDARD) {
            return ifExists(constraintFactory.forEach(otherClass), joiners);
        } else {
            // Calls fromUnfiltered() for backward compatibility only
            return ifExists(constraintFactory.fromUnfiltered(otherClass), joiners);
        }
    }

    @SafeVarargs
    @Override
    public final <C> BiConstraintStream<A, B> ifExistsIncludingNullVars(Class<C> otherClass, TriJoiner<A, B, C>... joiners) {
        if (getRetrievalSemantics() == RetrievalSemantics.STANDARD) {
            return ifExists(constraintFactory.forEachIncludingNullVars(otherClass), joiners);
        } else {
            return ifExists(constraintFactory.fromUnfiltered(otherClass), joiners);
        }
    }

    @SafeVarargs
    public final <C> BiConstraintStream<A, B> ifExists(UniConstraintStream<C> otherStream, TriJoiner<A, B, C>... joiners) {
        return ifExistsOrNot(true, otherStream, joiners);
    }

    @SafeVarargs
    @Override
    public final <C> BiConstraintStream<A, B> ifNotExists(Class<C> otherClass, TriJoiner<A, B, C>... joiners) {
        if (getRetrievalSemantics() == RetrievalSemantics.STANDARD) {
            return ifNotExists(constraintFactory.forEach(otherClass), joiners);
        } else {
            // Calls fromUnfiltered() for backward compatibility only
            return ifNotExists(constraintFactory.fromUnfiltered(otherClass), joiners);
        }
    }

    @SafeVarargs
    @Override
    public final <C> BiConstraintStream<A, B> ifNotExistsIncludingNullVars(Class<C> otherClass, TriJoiner<A, B, C>... joiners) {
        if (getRetrievalSemantics() == RetrievalSemantics.STANDARD) {
            return ifNotExists(constraintFactory.forEachIncludingNullVars(otherClass), joiners);
        } else {
            return ifNotExists(constraintFactory.fromUnfiltered(otherClass), joiners);
        }
    }

    @SafeVarargs
    public final <C> BiConstraintStream<A, B> ifNotExists(UniConstraintStream<C> otherStream, TriJoiner<A, B, C>... joiners) {
        return ifExistsOrNot(false, otherStream, joiners);
    }

    private <C> BiConstraintStream<A, B> ifExistsOrNot(boolean shouldExist, UniConstraintStream<C> otherStream,
            TriJoiner<A, B, C>[] joiners) {
        BavetAbstractUniConstraintStream<Solution_, C> other = assertBavetUniConstraintStream(otherStream);
        TriJoinerComber<A, B, C> joinerComber = TriJoinerComber.comb(joiners);
        BavetIfExistsBridgeUniConstraintStream<Solution_, C> parentBridgeC = other.shareAndAddChild(
                new BavetIfExistsBridgeUniConstraintStream<>(constraintFactory, other));
        return constraintFactory.share(
                new BavetIfExistsBiConstraintStream<>(constraintFactory, this, parentBridgeC,
                        shouldExist, joinerComber.getMergedJoiner(), joinerComber.getMergedFiltering()),
                childStreamList::add);
    }

    // ************************************************************************
    // Group by
    // ************************************************************************

    @Override
    public <ResultContainer_, Result_> UniConstraintStream<Result_> groupBy(
            BiConstraintCollector<A, B, ResultContainer_, Result_> collector) {
        BiGroupNodeConstructor<A, B, UniTuple<Result_>> nodeConstructor =
                (int inputStoreIndex, Consumer<UniTuple<Result_>> insert, Consumer<UniTuple<Result_>> retract,
                 int outputStoreSize) -> new Group0Mapping1CollectorBiNode<>(inputStoreIndex, collector, insert,
                                retract, outputStoreSize);
        return buildUniGroupBy(nodeConstructor);
    }

    private <NewA> UniConstraintStream<NewA> buildUniGroupBy(BiGroupNodeConstructor<A, B, UniTuple<NewA>> nodeConstructor) {
        BavetUniGroupBridgeBiConstraintStream<Solution_, A, B, NewA> bridge = shareAndAddChild(
                new BavetUniGroupBridgeBiConstraintStream<>(constraintFactory, this, nodeConstructor));
        return constraintFactory.share(
                new BavetGroupUniConstraintStream<>(constraintFactory, bridge),
                bridge::setGroupStream);
    }

    @Override
    public <ResultContainerA_, ResultA_, ResultContainerB_, ResultB_> BiConstraintStream<ResultA_, ResultB_> groupBy(
            BiConstraintCollector<A, B, ResultContainerA_, ResultA_> collectorA,
            BiConstraintCollector<A, B, ResultContainerB_, ResultB_> collectorB) {
        BiGroupNodeConstructor<A, B, BiTuple<ResultA_, ResultB_>> nodeConstructor =
                (int inputStoreIndex, Consumer<BiTuple<ResultA_, ResultB_>> insert,
                        Consumer<BiTuple<ResultA_, ResultB_>> retract,
                        int outputStoreSize) -> new Group0Mapping2CollectorBiNode<>(inputStoreIndex, collectorA, collectorB,
                                insert, retract, outputStoreSize);
        return buildBiGroupBy(nodeConstructor);
    }

    private <NewA, NewB> BiConstraintStream<NewA, NewB>
            buildBiGroupBy(BiGroupNodeConstructor<A, B, BiTuple<NewA, NewB>> nodeConstructor) {
        BavetBiGroupBridgeBiConstraintStream<Solution_, A, B, NewA, NewB> bridge = shareAndAddChild(
                new BavetBiGroupBridgeBiConstraintStream<>(constraintFactory, this, nodeConstructor));
        return constraintFactory.share(
                new BavetGroupBiConstraintStream<>(constraintFactory, bridge),
                bridge::setGroupStream);
    }

    @Override
    public <ResultContainerA_, ResultA_, ResultContainerB_, ResultB_, ResultContainerC_, ResultC_>
            TriConstraintStream<ResultA_, ResultB_, ResultC_>
            groupBy(BiConstraintCollector<A, B, ResultContainerA_, ResultA_> collectorA,
                    BiConstraintCollector<A, B, ResultContainerB_, ResultB_> collectorB,
                    BiConstraintCollector<A, B, ResultContainerC_, ResultC_> collectorC) {
        BiGroupNodeConstructor<A, B, TriTuple<ResultA_, ResultB_, ResultC_>> nodeConstructor =
                (int inputStoreIndex, Consumer<TriTuple<ResultA_, ResultB_, ResultC_>> insert,
                        Consumer<TriTuple<ResultA_, ResultB_, ResultC_>> retract,
                        int outputStoreSize) -> new Group0Mapping3CollectorBiNode<>(inputStoreIndex, collectorA, collectorB,
                                collectorC, insert, retract, outputStoreSize);
        return buildTriGroupBy(nodeConstructor);
    }

    private <NewA, NewB, NewC> TriConstraintStream<NewA, NewB, NewC>
            buildTriGroupBy(BiGroupNodeConstructor<A, B, TriTuple<NewA, NewB, NewC>> nodeConstructor) {
        BavetTriGroupBridgeBiConstraintStream<Solution_, A, B, NewA, NewB, NewC> bridge = shareAndAddChild(
                new BavetTriGroupBridgeBiConstraintStream<>(constraintFactory, this, nodeConstructor));
        return constraintFactory.share(
                new BavetGroupTriConstraintStream<>(constraintFactory, bridge),
                bridge::setGroupStream);
    }

    @Override
    public <ResultContainerA_, ResultA_, ResultContainerB_, ResultB_, ResultContainerC_, ResultC_, ResultContainerD_, ResultD_>
            QuadConstraintStream<ResultA_, ResultB_, ResultC_, ResultD_>
            groupBy(BiConstraintCollector<A, B, ResultContainerA_, ResultA_> collectorA,
                    BiConstraintCollector<A, B, ResultContainerB_, ResultB_> collectorB,
                    BiConstraintCollector<A, B, ResultContainerC_, ResultC_> collectorC,
                    BiConstraintCollector<A, B, ResultContainerD_, ResultD_> collectorD) {
        BiGroupNodeConstructor<A, B, QuadTuple<ResultA_, ResultB_, ResultC_, ResultD_>> nodeConstructor =
                (int inputStoreIndex, Consumer<QuadTuple<ResultA_, ResultB_, ResultC_, ResultD_>> insert,
                        Consumer<QuadTuple<ResultA_, ResultB_, ResultC_, ResultD_>> retract,
                        int outputStoreSize) -> new Group0Mapping4CollectorBiNode<>(inputStoreIndex, collectorA, collectorB,
                                collectorC, collectorD, insert, retract, outputStoreSize);
        return buildQuadGroupBy(nodeConstructor);
    }

    private <NewA, NewB, NewC, NewD> QuadConstraintStream<NewA, NewB, NewC, NewD>
            buildQuadGroupBy(BiGroupNodeConstructor<A, B, QuadTuple<NewA, NewB, NewC, NewD>> nodeConstructor) {
        BavetQuadGroupBridgeBiConstraintStream<Solution_, A, B, NewA, NewB, NewC, NewD> bridge = shareAndAddChild(
                new BavetQuadGroupBridgeBiConstraintStream<>(constraintFactory, this, nodeConstructor));
        return constraintFactory.share(
                new BavetGroupQuadConstraintStream<>(constraintFactory, bridge),
                bridge::setGroupStream);
    }

    @Override
    public <GroupKey_> UniConstraintStream<GroupKey_> groupBy(BiFunction<A, B, GroupKey_> groupKeyMapping) {
        BiGroupNodeConstructor<A, B, UniTuple<GroupKey_>> nodeConstructor =
                (int inputStoreIndex, Consumer<UniTuple<GroupKey_>> insert, Consumer<UniTuple<GroupKey_>> retract,
                 int outputStoreSize) -> new Group1Mapping0CollectorBiNode<>(groupKeyMapping, inputStoreIndex, insert,
                                retract, outputStoreSize);
        return buildUniGroupBy(nodeConstructor);
    }

    @Override
    public <GroupKey_, ResultContainerB_, ResultB_, ResultContainerC_, ResultC_>
            TriConstraintStream<GroupKey_, ResultB_, ResultC_> groupBy(BiFunction<A, B, GroupKey_> groupKeyMapping,
                    BiConstraintCollector<A, B, ResultContainerB_, ResultB_> collectorB,
                    BiConstraintCollector<A, B, ResultContainerC_, ResultC_> collectorC) {
        BiGroupNodeConstructor<A, B, TriTuple<GroupKey_, ResultB_, ResultC_>> nodeConstructor =
                (int inputStoreIndex, Consumer<TriTuple<GroupKey_, ResultB_, ResultC_>> insert,
                        Consumer<TriTuple<GroupKey_, ResultB_, ResultC_>> retract,
                        int outputStoreSize) -> new Group1Mapping2CollectorBiNode<>(groupKeyMapping, inputStoreIndex,
                                collectorB, collectorC, insert, retract, outputStoreSize);
        return buildTriGroupBy(nodeConstructor);
    }

    @Override
    public <GroupKey_, ResultContainerB_, ResultB_, ResultContainerC_, ResultC_, ResultContainerD_, ResultD_>
            QuadConstraintStream<GroupKey_, ResultB_, ResultC_, ResultD_>
            groupBy(BiFunction<A, B, GroupKey_> groupKeyMapping,
                    BiConstraintCollector<A, B, ResultContainerB_, ResultB_> collectorB,
                    BiConstraintCollector<A, B, ResultContainerC_, ResultC_> collectorC,
                    BiConstraintCollector<A, B, ResultContainerD_, ResultD_> collectorD) {
        BiGroupNodeConstructor<A, B, QuadTuple<GroupKey_, ResultB_, ResultC_, ResultD_>> nodeConstructor =
                (int inputStoreIndex, Consumer<QuadTuple<GroupKey_, ResultB_, ResultC_, ResultD_>> insert,
                        Consumer<QuadTuple<GroupKey_, ResultB_, ResultC_, ResultD_>> retract,
                        int outputStoreSize) -> new Group1Mapping3CollectorBiNode<>(groupKeyMapping, inputStoreIndex,
                                collectorB, collectorC, collectorD, insert, retract, outputStoreSize);
        return buildQuadGroupBy(nodeConstructor);
    }

    @Override
    public <GroupKey_, ResultContainer_, Result_> BiConstraintStream<GroupKey_, Result_> groupBy(
            BiFunction<A, B, GroupKey_> groupKeyMapping,
            BiConstraintCollector<A, B, ResultContainer_, Result_> collector) {
        BiGroupNodeConstructor<A, B, BiTuple<GroupKey_, Result_>> nodeConstructor =
                (int inputStoreIndex, Consumer<BiTuple<GroupKey_, Result_>> insert,
                        Consumer<BiTuple<GroupKey_, Result_>> retract,
                        int outputStoreSize) -> new Group1Mapping1CollectorBiNode<>(groupKeyMapping, inputStoreIndex, collector,
                                insert, retract, outputStoreSize);
        return buildBiGroupBy(nodeConstructor);
    }

    @Override
    public <GroupKeyA_, GroupKeyB_> BiConstraintStream<GroupKeyA_, GroupKeyB_> groupBy(
            BiFunction<A, B, GroupKeyA_> groupKeyAMapping, BiFunction<A, B, GroupKeyB_> groupKeyBMapping) {
        BiGroupNodeConstructor<A, B, BiTuple<GroupKeyA_, GroupKeyB_>> nodeConstructor =
                (int inputStoreIndex, Consumer<BiTuple<GroupKeyA_, GroupKeyB_>> insert,
                        Consumer<BiTuple<GroupKeyA_, GroupKeyB_>> retract,
                        int outputStoreSize) -> new Group2Mapping0CollectorBiNode<>(groupKeyAMapping, groupKeyBMapping,
                                inputStoreIndex,
                                insert, retract, outputStoreSize);
        return buildBiGroupBy(nodeConstructor);
    }

    @Override
    public <GroupKeyA_, GroupKeyB_, ResultContainer_, Result_> TriConstraintStream<GroupKeyA_, GroupKeyB_, Result_> groupBy(
            BiFunction<A, B, GroupKeyA_> groupKeyAMapping, BiFunction<A, B, GroupKeyB_> groupKeyBMapping,
            BiConstraintCollector<A, B, ResultContainer_, Result_> collector) {
        BiGroupNodeConstructor<A, B, TriTuple<GroupKeyA_, GroupKeyB_, Result_>> nodeConstructor =
                (int inputStoreIndex, Consumer<TriTuple<GroupKeyA_, GroupKeyB_, Result_>> insert,
                        Consumer<TriTuple<GroupKeyA_, GroupKeyB_, Result_>> retract,
                        int outputStoreSize) -> new Group2Mapping1CollectorBiNode<>(groupKeyAMapping, groupKeyBMapping,
                                inputStoreIndex, collector, insert, retract, outputStoreSize);
        return buildTriGroupBy(nodeConstructor);
    }

    @Override
    public <GroupKeyA_, GroupKeyB_, ResultContainerC_, ResultC_, ResultContainerD_, ResultD_>
            QuadConstraintStream<GroupKeyA_, GroupKeyB_, ResultC_, ResultD_> groupBy(
                    BiFunction<A, B, GroupKeyA_> groupKeyAMapping, BiFunction<A, B, GroupKeyB_> groupKeyBMapping,
                    BiConstraintCollector<A, B, ResultContainerC_, ResultC_> collectorC,
                    BiConstraintCollector<A, B, ResultContainerD_, ResultD_> collectorD) {
        BiGroupNodeConstructor<A, B, QuadTuple<GroupKeyA_, GroupKeyB_, ResultC_, ResultD_>> nodeConstructor =
                (int inputStoreIndex, Consumer<QuadTuple<GroupKeyA_, GroupKeyB_, ResultC_, ResultD_>> insert,
                        Consumer<QuadTuple<GroupKeyA_, GroupKeyB_, ResultC_, ResultD_>> retract,
                        int outputStoreSize) -> new Group2Mapping2CollectorBiNode<>(groupKeyAMapping, groupKeyBMapping,
                                inputStoreIndex, collectorC, collectorD, insert, retract, outputStoreSize);
        return buildQuadGroupBy(nodeConstructor);
    }

    @Override
    public <GroupKeyA_, GroupKeyB_, GroupKeyC_> TriConstraintStream<GroupKeyA_, GroupKeyB_, GroupKeyC_> groupBy(
            BiFunction<A, B, GroupKeyA_> groupKeyAMapping, BiFunction<A, B, GroupKeyB_> groupKeyBMapping,
            BiFunction<A, B, GroupKeyC_> groupKeyCMapping) {
        BiGroupNodeConstructor<A, B, TriTuple<GroupKeyA_, GroupKeyB_, GroupKeyC_>> nodeConstructor =
                (int inputStoreIndex, Consumer<TriTuple<GroupKeyA_, GroupKeyB_, GroupKeyC_>> insert,
                        Consumer<TriTuple<GroupKeyA_, GroupKeyB_, GroupKeyC_>> retract,
                        int outputStoreSize) -> new Group3Mapping0CollectorBiNode<>(groupKeyAMapping, groupKeyBMapping,
                                groupKeyCMapping,
                                inputStoreIndex, insert, retract, outputStoreSize);
        return buildTriGroupBy(nodeConstructor);
    }

    @Override
    public <GroupKeyA_, GroupKeyB_, GroupKeyC_, ResultContainerD_, ResultD_>
            QuadConstraintStream<GroupKeyA_, GroupKeyB_, GroupKeyC_, ResultD_>
            groupBy(BiFunction<A, B, GroupKeyA_> groupKeyAMapping, BiFunction<A, B, GroupKeyB_> groupKeyBMapping,
                    BiFunction<A, B, GroupKeyC_> groupKeyCMapping,
                    BiConstraintCollector<A, B, ResultContainerD_, ResultD_> collectorD) {
        BiGroupNodeConstructor<A, B, QuadTuple<GroupKeyA_, GroupKeyB_, GroupKeyC_, ResultD_>> nodeConstructor =
                (int inputStoreIndex, Consumer<QuadTuple<GroupKeyA_, GroupKeyB_, GroupKeyC_, ResultD_>> insert,
                        Consumer<QuadTuple<GroupKeyA_, GroupKeyB_, GroupKeyC_, ResultD_>> retract,
                        int outputStoreSize) -> new Group3Mapping1CollectorBiNode<>(groupKeyAMapping, groupKeyBMapping,
                                groupKeyCMapping, inputStoreIndex, collectorD, insert, retract, outputStoreSize);
        return buildQuadGroupBy(nodeConstructor);
    }

    @Override
    public <GroupKeyA_, GroupKeyB_, GroupKeyC_, GroupKeyD_> QuadConstraintStream<GroupKeyA_, GroupKeyB_, GroupKeyC_, GroupKeyD_>
            groupBy(BiFunction<A, B, GroupKeyA_> groupKeyAMapping, BiFunction<A, B, GroupKeyB_> groupKeyBMapping,
                    BiFunction<A, B, GroupKeyC_> groupKeyCMapping, BiFunction<A, B, GroupKeyD_> groupKeyDMapping) {
        BiGroupNodeConstructor<A, B, QuadTuple<GroupKeyA_, GroupKeyB_, GroupKeyC_, GroupKeyD_>> nodeConstructor =
                (int inputStoreIndex, Consumer<QuadTuple<GroupKeyA_, GroupKeyB_, GroupKeyC_, GroupKeyD_>> insert,
                        Consumer<QuadTuple<GroupKeyA_, GroupKeyB_, GroupKeyC_, GroupKeyD_>> retract,
                        int outputStoreSize) -> new Group4Mapping0CollectorBiNode<>(groupKeyAMapping, groupKeyBMapping,
                                groupKeyCMapping, groupKeyDMapping, inputStoreIndex, insert, retract, outputStoreSize);
        return buildQuadGroupBy(nodeConstructor);
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
        BavetScoringBiConstraintStream<Solution_, A, B> stream = shareAndAddChild(
                new BavetScoringBiConstraintStream<>(constraintFactory, this));
        return buildConstraint(constraintPackage, constraintName, constraintWeight,
                impactType, stream);
    }

    @Override
    public final Constraint impactScore(String constraintPackage, String constraintName, Score<?> constraintWeight,
            ToIntBiFunction<A, B> matchWeigher, ScoreImpactType impactType) {
        BavetScoringBiConstraintStream<Solution_, A, B> stream = shareAndAddChild(
                new BavetScoringBiConstraintStream<>(constraintFactory, this, matchWeigher));
        return buildConstraint(constraintPackage, constraintName, constraintWeight,
                impactType, stream);
    }

    @Override
    public final Constraint impactScoreLong(String constraintPackage, String constraintName,
            Score<?> constraintWeight, ToLongBiFunction<A, B> matchWeigher, ScoreImpactType impactType) {
        BavetScoringBiConstraintStream<Solution_, A, B> stream = shareAndAddChild(
                new BavetScoringBiConstraintStream<>(constraintFactory, this, matchWeigher));
        return buildConstraint(constraintPackage, constraintName, constraintWeight,
                impactType, stream);
    }

    @Override
    public final Constraint impactScoreBigDecimal(String constraintPackage, String constraintName,
            Score<?> constraintWeight, BiFunction<A, B, BigDecimal> matchWeigher, ScoreImpactType impactType) {
        BavetScoringBiConstraintStream<Solution_, A, B> stream = shareAndAddChild(
                new BavetScoringBiConstraintStream<>(constraintFactory, this, matchWeigher));
        return buildConstraint(constraintPackage, constraintName, constraintWeight,
                impactType, stream);
    }

    @Override
    public final Constraint impactScoreConfigurable(String constraintPackage, String constraintName,
            ScoreImpactType impactType) {
        BavetScoringBiConstraintStream<Solution_, A, B> stream = shareAndAddChild(
                new BavetScoringBiConstraintStream<>(constraintFactory, this));
        return buildConstraintConfigurable(constraintPackage, constraintName,
                impactType, stream);
    }

    @Override
    public final Constraint impactScoreConfigurable(String constraintPackage, String constraintName,
            ToIntBiFunction<A, B> matchWeigher, ScoreImpactType impactType) {
        BavetScoringBiConstraintStream<Solution_, A, B> stream = shareAndAddChild(
                new BavetScoringBiConstraintStream<>(constraintFactory, this, matchWeigher));
        return buildConstraintConfigurable(constraintPackage, constraintName,
                impactType, stream);
    }

    @Override
    public final Constraint impactScoreConfigurableLong(String constraintPackage, String constraintName,
            ToLongBiFunction<A, B> matchWeigher, ScoreImpactType impactType) {
        BavetScoringBiConstraintStream<Solution_, A, B> stream = shareAndAddChild(
                new BavetScoringBiConstraintStream<>(constraintFactory, this, matchWeigher));
        return buildConstraintConfigurable(constraintPackage, constraintName,
                impactType, stream);
    }

    @Override
    public final Constraint impactScoreConfigurableBigDecimal(String constraintPackage, String constraintName,
            BiFunction<A, B, BigDecimal> matchWeigher, ScoreImpactType impactType) {
        BavetScoringBiConstraintStream<Solution_, A, B> stream = shareAndAddChild(
                new BavetScoringBiConstraintStream<>(constraintFactory, this, matchWeigher));
        return buildConstraintConfigurable(constraintPackage, constraintName,
                impactType, stream);
    }

}
