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

package org.optaplanner.constraint.streams.bavet.uni;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.ToIntFunction;
import java.util.function.ToLongFunction;

import org.optaplanner.constraint.streams.bavet.BavetConstraintFactory;
import org.optaplanner.constraint.streams.bavet.bi.BavetGroupBiConstraintStream;
import org.optaplanner.constraint.streams.bavet.bi.BavetJoinBiConstraintStream;
import org.optaplanner.constraint.streams.bavet.bi.BiTuple;
import org.optaplanner.constraint.streams.bavet.common.BavetAbstractConstraintStream;
import org.optaplanner.constraint.streams.bavet.quad.BavetGroupQuadConstraintStream;
import org.optaplanner.constraint.streams.bavet.quad.QuadTuple;
import org.optaplanner.constraint.streams.bavet.tri.BavetGroupTriConstraintStream;
import org.optaplanner.constraint.streams.bavet.tri.TriTuple;
import org.optaplanner.constraint.streams.common.RetrievalSemantics;
import org.optaplanner.constraint.streams.common.ScoreImpactType;
import org.optaplanner.constraint.streams.common.bi.BiJoinerComber;
import org.optaplanner.constraint.streams.common.uni.InnerUniConstraintStream;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.stream.Constraint;
import org.optaplanner.core.api.score.stream.bi.BiConstraintStream;
import org.optaplanner.core.api.score.stream.bi.BiJoiner;
import org.optaplanner.core.api.score.stream.quad.QuadConstraintStream;
import org.optaplanner.core.api.score.stream.tri.TriConstraintStream;
import org.optaplanner.core.api.score.stream.uni.UniConstraintCollector;
import org.optaplanner.core.api.score.stream.uni.UniConstraintStream;

public abstract class BavetAbstractUniConstraintStream<Solution_, A> extends BavetAbstractConstraintStream<Solution_>
        implements InnerUniConstraintStream<A> {

    protected final List<BavetAbstractUniConstraintStream<Solution_, A>> childStreamList = new ArrayList<>(2);

    public BavetAbstractUniConstraintStream(BavetConstraintFactory<Solution_> constraintFactory,
            RetrievalSemantics retrievalSemantics) {
        super(constraintFactory, retrievalSemantics);
    }

    public List<BavetAbstractUniConstraintStream<Solution_, A>> getChildStreamList() {
        return childStreamList;
    }

    // ************************************************************************
    // Stream builder methods
    // ************************************************************************

    public <Stream_ extends BavetAbstractUniConstraintStream<Solution_, A>> Stream_ shareAndAddChild(
            Stream_ stream) {
        return constraintFactory.share(stream, childStreamList::add);
    }

    // ************************************************************************
    // Filter
    // ************************************************************************

    @Override
    public BavetAbstractUniConstraintStream<Solution_, A> filter(Predicate<A> predicate) {
        return shareAndAddChild(
                new BavetFilterUniConstraintStream<>(constraintFactory, this, predicate));
    }

    // ************************************************************************
    // Join
    // ************************************************************************

    @Override
    @SafeVarargs
    public final <B> BiConstraintStream<A, B> join(UniConstraintStream<B> otherStream,
            BiJoiner<A, B>... joiners) {
        BiJoinerComber<A, B> joinerComber = BiJoinerComber.comb(joiners);
        return join(otherStream, joinerComber);
    }

    @Override
    public final <B> BiConstraintStream<A, B> join(UniConstraintStream<B> otherStream, BiJoinerComber<A, B> joinerComber) {
        BavetAbstractUniConstraintStream<Solution_, B> other = assertBavetUniConstraintStream(otherStream);

        BavetJoinBridgeUniConstraintStream<Solution_, A> leftBridge =
                new BavetJoinBridgeUniConstraintStream<>(constraintFactory, this, true);
        BavetJoinBridgeUniConstraintStream<Solution_, B> rightBridge =
                new BavetJoinBridgeUniConstraintStream<>(constraintFactory, other, false);
        BavetJoinBiConstraintStream<Solution_, A, B> joinStream =
                new BavetJoinBiConstraintStream<>(constraintFactory, leftBridge, rightBridge,
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
    public final <B> UniConstraintStream<A> ifExists(Class<B> otherClass, BiJoiner<A, B>... joiners) {
        if (getRetrievalSemantics() == RetrievalSemantics.STANDARD) {
            return ifExists(constraintFactory.forEach(otherClass), joiners);
        } else {
            // Calls fromUnfiltered() for backward compatibility only
            return ifExists(constraintFactory.fromUnfiltered(otherClass), joiners);
        }
    }

    @SafeVarargs
    @Override
    public final <B> UniConstraintStream<A> ifExistsIncludingNullVars(Class<B> otherClass, BiJoiner<A, B>... joiners) {
        if (getRetrievalSemantics() == RetrievalSemantics.STANDARD) {
            return ifExists(constraintFactory.forEachIncludingNullVars(otherClass), joiners);
        } else {
            return ifExists(constraintFactory.fromUnfiltered(otherClass), joiners);
        }
    }

    @SafeVarargs
    public final <B> UniConstraintStream<A> ifExists(UniConstraintStream<B> otherStream, BiJoiner<A, B>... joiners) {
        return ifExistsOrNot(true, otherStream, joiners);
    }

    @SafeVarargs
    @Override
    public final <B> UniConstraintStream<A> ifNotExists(Class<B> otherClass, BiJoiner<A, B>... joiners) {
        if (getRetrievalSemantics() == RetrievalSemantics.STANDARD) {
            return ifNotExists(constraintFactory.forEach(otherClass), joiners);
        } else {
            // Calls fromUnfiltered() for backward compatibility only
            return ifNotExists(constraintFactory.fromUnfiltered(otherClass), joiners);
        }
    }

    @SafeVarargs
    @Override
    public final <B> UniConstraintStream<A> ifNotExistsIncludingNullVars(Class<B> otherClass, BiJoiner<A, B>... joiners) {
        if (getRetrievalSemantics() == RetrievalSemantics.STANDARD) {
            return ifNotExists(constraintFactory.forEachIncludingNullVars(otherClass), joiners);
        } else {
            return ifNotExists(constraintFactory.fromUnfiltered(otherClass), joiners);
        }
    }

    @SafeVarargs
    public final <B> UniConstraintStream<A> ifNotExists(UniConstraintStream<B> otherStream, BiJoiner<A, B>... joiners) {
        return ifExistsOrNot(false, otherStream, joiners);
    }

    private <B> UniConstraintStream<A> ifExistsOrNot(boolean shouldExist, UniConstraintStream<B> otherStream,
            BiJoiner<A, B>[] joiners) {
        BavetAbstractUniConstraintStream<Solution_, B> other = assertBavetUniConstraintStream(otherStream);
        BiJoinerComber<A, B> joinerComber = BiJoinerComber.comb(joiners);
        BavetIfExistsBridgeUniConstraintStream<Solution_, B> parentBridgeB = other.shareAndAddChild(
                new BavetIfExistsBridgeUniConstraintStream<>(constraintFactory, other));
        return constraintFactory.share(
                new BavetIfExistsUniConstraintStream<>(constraintFactory, this, parentBridgeB,
                        shouldExist, joinerComber.getMergedJoiner(), joinerComber.getMergedFiltering()),
                childStreamList::add);
    }

    // ************************************************************************
    // Group by
    // ************************************************************************

    @Override
    public <ResultContainer_, Result_> UniConstraintStream<Result_> groupBy(
            UniConstraintCollector<A, ResultContainer_, Result_> collector) {
        UniGroupNodeConstructor<A, UniTuple<Result_>> nodeConstructor =
                (int inputStoreIndex, Consumer<UniTuple<Result_>> insert, Consumer<UniTuple<Result_>> retract,
                        int outputStoreSize) -> new Group0Mapping1CollectorUniNode<>(inputStoreIndex, collector, insert,
                                retract, outputStoreSize);
        return buildUniGroupBy(nodeConstructor);
    }

    private <NewA> UniConstraintStream<NewA> buildUniGroupBy(UniGroupNodeConstructor<A, UniTuple<NewA>> nodeConstructor) {
        BavetUniGroupBridgeUniConstraintStream<Solution_, A, NewA> bridge = shareAndAddChild(
                new BavetUniGroupBridgeUniConstraintStream<>(constraintFactory, this, nodeConstructor));
        return constraintFactory.share(
                new BavetGroupUniConstraintStream<>(constraintFactory, bridge),
                bridge::setGroupStream);
    }

    @Override
    public <ResultContainerA_, ResultA_, ResultContainerB_, ResultB_> BiConstraintStream<ResultA_, ResultB_> groupBy(
            UniConstraintCollector<A, ResultContainerA_, ResultA_> collectorA,
            UniConstraintCollector<A, ResultContainerB_, ResultB_> collectorB) {
        UniGroupNodeConstructor<A, BiTuple<ResultA_, ResultB_>> nodeConstructor =
                (int inputStoreIndex, Consumer<BiTuple<ResultA_, ResultB_>> insert,
                        Consumer<BiTuple<ResultA_, ResultB_>> retract,
                        int outputStoreSize) -> new Group0Mapping2CollectorUniNode<>(inputStoreIndex, collectorA, collectorB,
                                insert, retract, outputStoreSize);
        return buildBiGroupBy(nodeConstructor);
    }

    private <NewA, NewB> BiConstraintStream<NewA, NewB>
            buildBiGroupBy(UniGroupNodeConstructor<A, BiTuple<NewA, NewB>> nodeConstructor) {
        BavetBiGroupBridgeUniConstraintStream<Solution_, A, NewA, NewB> bridge = shareAndAddChild(
                new BavetBiGroupBridgeUniConstraintStream<>(constraintFactory, this, nodeConstructor));
        return constraintFactory.share(
                new BavetGroupBiConstraintStream<>(constraintFactory, bridge),
                bridge::setGroupStream);
    }

    @Override
    public <ResultContainerA_, ResultA_, ResultContainerB_, ResultB_, ResultContainerC_, ResultC_>
            TriConstraintStream<ResultA_, ResultB_, ResultC_>
            groupBy(UniConstraintCollector<A, ResultContainerA_, ResultA_> collectorA,
                    UniConstraintCollector<A, ResultContainerB_, ResultB_> collectorB,
                    UniConstraintCollector<A, ResultContainerC_, ResultC_> collectorC) {
        UniGroupNodeConstructor<A, TriTuple<ResultA_, ResultB_, ResultC_>> nodeConstructor =
                (int inputStoreIndex, Consumer<TriTuple<ResultA_, ResultB_, ResultC_>> insert,
                        Consumer<TriTuple<ResultA_, ResultB_, ResultC_>> retract,
                        int outputStoreSize) -> new Group0Mapping3CollectorUniNode<>(inputStoreIndex, collectorA, collectorB,
                                collectorC, insert, retract, outputStoreSize);
        return buildTriGroupBy(nodeConstructor);
    }

    private <NewA, NewB, NewC> TriConstraintStream<NewA, NewB, NewC>
            buildTriGroupBy(UniGroupNodeConstructor<A, TriTuple<NewA, NewB, NewC>> nodeConstructor) {
        BavetTriGroupBridgeUniConstraintStream<Solution_, A, NewA, NewB, NewC> bridge = shareAndAddChild(
                new BavetTriGroupBridgeUniConstraintStream<>(constraintFactory, this, nodeConstructor));
        return constraintFactory.share(
                new BavetGroupTriConstraintStream<>(constraintFactory, bridge),
                bridge::setGroupStream);
    }

    @Override
    public <ResultContainerA_, ResultA_, ResultContainerB_, ResultB_, ResultContainerC_, ResultC_, ResultContainerD_, ResultD_>
            QuadConstraintStream<ResultA_, ResultB_, ResultC_, ResultD_>
            groupBy(UniConstraintCollector<A, ResultContainerA_, ResultA_> collectorA,
                    UniConstraintCollector<A, ResultContainerB_, ResultB_> collectorB,
                    UniConstraintCollector<A, ResultContainerC_, ResultC_> collectorC,
                    UniConstraintCollector<A, ResultContainerD_, ResultD_> collectorD) {
        UniGroupNodeConstructor<A, QuadTuple<ResultA_, ResultB_, ResultC_, ResultD_>> nodeConstructor =
                (int inputStoreIndex, Consumer<QuadTuple<ResultA_, ResultB_, ResultC_, ResultD_>> insert,
                        Consumer<QuadTuple<ResultA_, ResultB_, ResultC_, ResultD_>> retract,
                        int outputStoreSize) -> new Group0Mapping4CollectorUniNode<>(inputStoreIndex, collectorA, collectorB,
                                collectorC, collectorD, insert, retract, outputStoreSize);
        return buildQuadGroupBy(nodeConstructor);
    }

    private <NewA, NewB, NewC, NewD> QuadConstraintStream<NewA, NewB, NewC, NewD>
            buildQuadGroupBy(UniGroupNodeConstructor<A, QuadTuple<NewA, NewB, NewC, NewD>> nodeConstructor) {
        BavetQuadGroupBridgeUniConstraintStream<Solution_, A, NewA, NewB, NewC, NewD> bridge = shareAndAddChild(
                new BavetQuadGroupBridgeUniConstraintStream<>(constraintFactory, this, nodeConstructor));
        return constraintFactory.share(
                new BavetGroupQuadConstraintStream<>(constraintFactory, bridge),
                bridge::setGroupStream);
    }

    @Override
    public <GroupKey_> UniConstraintStream<GroupKey_> groupBy(Function<A, GroupKey_> groupKeyMapping) {
        UniGroupNodeConstructor<A, UniTuple<GroupKey_>> nodeConstructor =
                (int inputStoreIndex, Consumer<UniTuple<GroupKey_>> insert, Consumer<UniTuple<GroupKey_>> retract,
                        int outputStoreSize) -> new Group1Mapping0CollectorUniNode<>(groupKeyMapping, inputStoreIndex, insert,
                                retract, outputStoreSize);
        return buildUniGroupBy(nodeConstructor);
    }

    @Override
    public <GroupKey_, ResultContainerB_, ResultB_, ResultContainerC_, ResultC_>
            TriConstraintStream<GroupKey_, ResultB_, ResultC_> groupBy(Function<A, GroupKey_> groupKeyMapping,
                    UniConstraintCollector<A, ResultContainerB_, ResultB_> collectorB,
                    UniConstraintCollector<A, ResultContainerC_, ResultC_> collectorC) {
        UniGroupNodeConstructor<A, TriTuple<GroupKey_, ResultB_, ResultC_>> nodeConstructor =
                (int inputStoreIndex, Consumer<TriTuple<GroupKey_, ResultB_, ResultC_>> insert,
                        Consumer<TriTuple<GroupKey_, ResultB_, ResultC_>> retract,
                        int outputStoreSize) -> new Group1Mapping2CollectorUniNode<>(groupKeyMapping, inputStoreIndex,
                                collectorB, collectorC, insert, retract, outputStoreSize);
        return buildTriGroupBy(nodeConstructor);
    }

    @Override
    public <GroupKey_, ResultContainerB_, ResultB_, ResultContainerC_, ResultC_, ResultContainerD_, ResultD_>
            QuadConstraintStream<GroupKey_, ResultB_, ResultC_, ResultD_>
            groupBy(Function<A, GroupKey_> groupKeyMapping,
                    UniConstraintCollector<A, ResultContainerB_, ResultB_> collectorB,
                    UniConstraintCollector<A, ResultContainerC_, ResultC_> collectorC,
                    UniConstraintCollector<A, ResultContainerD_, ResultD_> collectorD) {
        UniGroupNodeConstructor<A, QuadTuple<GroupKey_, ResultB_, ResultC_, ResultD_>> nodeConstructor =
                (int inputStoreIndex, Consumer<QuadTuple<GroupKey_, ResultB_, ResultC_, ResultD_>> insert,
                        Consumer<QuadTuple<GroupKey_, ResultB_, ResultC_, ResultD_>> retract,
                        int outputStoreSize) -> new Group1Mapping3CollectorUniNode<>(groupKeyMapping, inputStoreIndex,
                                collectorB, collectorC, collectorD, insert, retract, outputStoreSize);
        return buildQuadGroupBy(nodeConstructor);
    }

    @Override
    public <GroupKey_, ResultContainer_, Result_> BiConstraintStream<GroupKey_, Result_> groupBy(
            Function<A, GroupKey_> groupKeyMapping,
            UniConstraintCollector<A, ResultContainer_, Result_> collector) {
        UniGroupNodeConstructor<A, BiTuple<GroupKey_, Result_>> nodeConstructor =
                (int inputStoreIndex, Consumer<BiTuple<GroupKey_, Result_>> insert,
                        Consumer<BiTuple<GroupKey_, Result_>> retract,
                        int outputStoreSize) -> new Group1Mapping1CollectorUniNode<>(groupKeyMapping, inputStoreIndex,
                                collector,
                                insert, retract, outputStoreSize);
        return buildBiGroupBy(nodeConstructor);
    }

    @Override
    public <GroupKeyA_, GroupKeyB_> BiConstraintStream<GroupKeyA_, GroupKeyB_> groupBy(
            Function<A, GroupKeyA_> groupKeyAMapping, Function<A, GroupKeyB_> groupKeyBMapping) {
        UniGroupNodeConstructor<A, BiTuple<GroupKeyA_, GroupKeyB_>> nodeConstructor =
                (int inputStoreIndex, Consumer<BiTuple<GroupKeyA_, GroupKeyB_>> insert,
                        Consumer<BiTuple<GroupKeyA_, GroupKeyB_>> retract,
                        int outputStoreSize) -> new Group2Mapping0CollectorUniNode<>(groupKeyAMapping, groupKeyBMapping,
                                inputStoreIndex,
                                insert, retract, outputStoreSize);
        return buildBiGroupBy(nodeConstructor);
    }

    @Override
    public <GroupKeyA_, GroupKeyB_, ResultContainer_, Result_> TriConstraintStream<GroupKeyA_, GroupKeyB_, Result_> groupBy(
            Function<A, GroupKeyA_> groupKeyAMapping, Function<A, GroupKeyB_> groupKeyBMapping,
            UniConstraintCollector<A, ResultContainer_, Result_> collector) {
        UniGroupNodeConstructor<A, TriTuple<GroupKeyA_, GroupKeyB_, Result_>> nodeConstructor =
                (int inputStoreIndex, Consumer<TriTuple<GroupKeyA_, GroupKeyB_, Result_>> insert,
                        Consumer<TriTuple<GroupKeyA_, GroupKeyB_, Result_>> retract,
                        int outputStoreSize) -> new Group2Mapping1CollectorUniNode<>(groupKeyAMapping, groupKeyBMapping,
                                inputStoreIndex, collector, insert, retract, outputStoreSize);
        return buildTriGroupBy(nodeConstructor);
    }

    @Override
    public <GroupKeyA_, GroupKeyB_, ResultContainerC_, ResultC_, ResultContainerD_, ResultD_>
            QuadConstraintStream<GroupKeyA_, GroupKeyB_, ResultC_, ResultD_> groupBy(
                    Function<A, GroupKeyA_> groupKeyAMapping, Function<A, GroupKeyB_> groupKeyBMapping,
                    UniConstraintCollector<A, ResultContainerC_, ResultC_> collectorC,
                    UniConstraintCollector<A, ResultContainerD_, ResultD_> collectorD) {
        UniGroupNodeConstructor<A, QuadTuple<GroupKeyA_, GroupKeyB_, ResultC_, ResultD_>> nodeConstructor =
                (int inputStoreIndex, Consumer<QuadTuple<GroupKeyA_, GroupKeyB_, ResultC_, ResultD_>> insert,
                        Consumer<QuadTuple<GroupKeyA_, GroupKeyB_, ResultC_, ResultD_>> retract,
                        int outputStoreSize) -> new Group2Mapping2CollectorUniNode<>(groupKeyAMapping, groupKeyBMapping,
                                inputStoreIndex, collectorC, collectorD, insert, retract, outputStoreSize);
        return buildQuadGroupBy(nodeConstructor);
    }

    @Override
    public <GroupKeyA_, GroupKeyB_, GroupKeyC_> TriConstraintStream<GroupKeyA_, GroupKeyB_, GroupKeyC_> groupBy(
            Function<A, GroupKeyA_> groupKeyAMapping, Function<A, GroupKeyB_> groupKeyBMapping,
            Function<A, GroupKeyC_> groupKeyCMapping) {
        UniGroupNodeConstructor<A, TriTuple<GroupKeyA_, GroupKeyB_, GroupKeyC_>> nodeConstructor =
                (int inputStoreIndex, Consumer<TriTuple<GroupKeyA_, GroupKeyB_, GroupKeyC_>> insert,
                        Consumer<TriTuple<GroupKeyA_, GroupKeyB_, GroupKeyC_>> retract,
                        int outputStoreSize) -> new Group3Mapping0CollectorUniNode<>(groupKeyAMapping, groupKeyBMapping,
                                groupKeyCMapping,
                                inputStoreIndex, insert, retract, outputStoreSize);
        return buildTriGroupBy(nodeConstructor);
    }

    @Override
    public <GroupKeyA_, GroupKeyB_, GroupKeyC_, ResultContainerD_, ResultD_>
            QuadConstraintStream<GroupKeyA_, GroupKeyB_, GroupKeyC_, ResultD_>
            groupBy(Function<A, GroupKeyA_> groupKeyAMapping, Function<A, GroupKeyB_> groupKeyBMapping,
                    Function<A, GroupKeyC_> groupKeyCMapping,
                    UniConstraintCollector<A, ResultContainerD_, ResultD_> collectorD) {
        UniGroupNodeConstructor<A, QuadTuple<GroupKeyA_, GroupKeyB_, GroupKeyC_, ResultD_>> nodeConstructor =
                (int inputStoreIndex, Consumer<QuadTuple<GroupKeyA_, GroupKeyB_, GroupKeyC_, ResultD_>> insert,
                        Consumer<QuadTuple<GroupKeyA_, GroupKeyB_, GroupKeyC_, ResultD_>> retract,
                        int outputStoreSize) -> new Group3Mapping1CollectorUniNode<>(groupKeyAMapping, groupKeyBMapping,
                                groupKeyCMapping, inputStoreIndex, collectorD, insert, retract, outputStoreSize);
        return buildQuadGroupBy(nodeConstructor);
    }

    @Override
    public <GroupKeyA_, GroupKeyB_, GroupKeyC_, GroupKeyD_> QuadConstraintStream<GroupKeyA_, GroupKeyB_, GroupKeyC_, GroupKeyD_>
            groupBy(Function<A, GroupKeyA_> groupKeyAMapping, Function<A, GroupKeyB_> groupKeyBMapping,
                    Function<A, GroupKeyC_> groupKeyCMapping, Function<A, GroupKeyD_> groupKeyDMapping) {
        UniGroupNodeConstructor<A, QuadTuple<GroupKeyA_, GroupKeyB_, GroupKeyC_, GroupKeyD_>> nodeConstructor =
                (int inputStoreIndex, Consumer<QuadTuple<GroupKeyA_, GroupKeyB_, GroupKeyC_, GroupKeyD_>> insert,
                        Consumer<QuadTuple<GroupKeyA_, GroupKeyB_, GroupKeyC_, GroupKeyD_>> retract,
                        int outputStoreSize) -> new Group4Mapping0CollectorUniNode<>(groupKeyAMapping, groupKeyBMapping,
                                groupKeyCMapping, groupKeyDMapping, inputStoreIndex, insert, retract, outputStoreSize);
        return buildQuadGroupBy(nodeConstructor);
    }

    // ************************************************************************
    // Operations with duplicate tuple possibility
    // ************************************************************************

    @Override
    public <ResultA_> UniConstraintStream<ResultA_> map(Function<A, ResultA_> mapping) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <ResultA_> UniConstraintStream<ResultA_> flattenLast(Function<A, Iterable<ResultA_>> mapping) {
        throw new UnsupportedOperationException();
    }

    // ************************************************************************
    // Penalize/reward
    // ************************************************************************

    @Override
    public final Constraint impactScore(String constraintPackage, String constraintName, Score<?> constraintWeight,
            ScoreImpactType impactType) {
        BavetScoringUniConstraintStream<Solution_, A> stream = shareAndAddChild(
                new BavetScoringUniConstraintStream<>(constraintFactory, this));
        return buildConstraint(constraintPackage, constraintName, constraintWeight,
                impactType, stream);
    }

    @Override
    public final Constraint impactScore(String constraintPackage, String constraintName, Score<?> constraintWeight,
            ToIntFunction<A> matchWeigher, ScoreImpactType impactType) {
        BavetScoringUniConstraintStream<Solution_, A> stream = shareAndAddChild(
                new BavetScoringUniConstraintStream<>(constraintFactory, this, matchWeigher));
        return buildConstraint(constraintPackage, constraintName, constraintWeight,
                impactType, stream);
    }

    @Override
    public final Constraint impactScoreLong(String constraintPackage, String constraintName, Score<?> constraintWeight,
            ToLongFunction<A> matchWeigher, ScoreImpactType impactType) {
        BavetScoringUniConstraintStream<Solution_, A> stream = shareAndAddChild(
                new BavetScoringUniConstraintStream<>(constraintFactory, this, matchWeigher));
        return buildConstraint(constraintPackage, constraintName, constraintWeight,
                impactType, stream);
    }

    @Override
    public final Constraint impactScoreBigDecimal(String constraintPackage, String constraintName,
            Score<?> constraintWeight, Function<A, BigDecimal> matchWeigher, ScoreImpactType impactType) {
        BavetScoringUniConstraintStream<Solution_, A> stream = shareAndAddChild(
                new BavetScoringUniConstraintStream<>(constraintFactory, this, matchWeigher));
        return buildConstraint(constraintPackage, constraintName, constraintWeight,
                impactType, stream);
    }

    @Override
    public final Constraint impactScoreConfigurable(String constraintPackage, String constraintName,
            ScoreImpactType impactType) {
        BavetScoringUniConstraintStream<Solution_, A> stream = shareAndAddChild(
                new BavetScoringUniConstraintStream<>(constraintFactory, this));
        return buildConstraintConfigurable(constraintPackage, constraintName,
                impactType, stream);
    }

    @Override
    public final Constraint impactScoreConfigurable(String constraintPackage, String constraintName,
            ToIntFunction<A> matchWeigher, ScoreImpactType impactType) {
        BavetScoringUniConstraintStream<Solution_, A> stream = shareAndAddChild(
                new BavetScoringUniConstraintStream<>(constraintFactory, this, matchWeigher));
        return buildConstraintConfigurable(constraintPackage, constraintName,
                impactType, stream);
    }

    @Override
    public final Constraint impactScoreConfigurableLong(String constraintPackage, String constraintName,
            ToLongFunction<A> matchWeigher, ScoreImpactType impactType) {
        BavetScoringUniConstraintStream<Solution_, A> stream = shareAndAddChild(
                new BavetScoringUniConstraintStream<>(constraintFactory, this, matchWeigher));
        return buildConstraintConfigurable(constraintPackage, constraintName,
                impactType, stream);
    }

    @Override
    public final Constraint impactScoreConfigurableBigDecimal(String constraintPackage, String constraintName,
            Function<A, BigDecimal> matchWeigher, ScoreImpactType impactType) {
        BavetScoringUniConstraintStream<Solution_, A> stream = shareAndAddChild(
                new BavetScoringUniConstraintStream<>(constraintFactory, this, matchWeigher));
        return buildConstraintConfigurable(constraintPackage, constraintName,
                impactType, stream);
    }

}
