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

package org.optaplanner.constraint.streams.bavet.quad;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import org.optaplanner.constraint.streams.bavet.BavetConstraintFactory;
import org.optaplanner.constraint.streams.bavet.bi.BavetGroupBiConstraintStream;
import org.optaplanner.constraint.streams.bavet.bi.BiTuple;
import org.optaplanner.constraint.streams.bavet.common.BavetAbstractConstraintStream;
import org.optaplanner.constraint.streams.bavet.tri.BavetGroupTriConstraintStream;
import org.optaplanner.constraint.streams.bavet.tri.TriTuple;
import org.optaplanner.constraint.streams.bavet.uni.BavetAbstractUniConstraintStream;
import org.optaplanner.constraint.streams.bavet.uni.BavetGroupUniConstraintStream;
import org.optaplanner.constraint.streams.bavet.uni.BavetIfExistsBridgeUniConstraintStream;
import org.optaplanner.constraint.streams.bavet.uni.UniTuple;
import org.optaplanner.constraint.streams.common.RetrievalSemantics;
import org.optaplanner.constraint.streams.common.ScoreImpactType;
import org.optaplanner.constraint.streams.penta.PentaJoinerComber;
import org.optaplanner.constraint.streams.quad.InnerQuadConstraintStream;
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

public abstract class BavetAbstractQuadConstraintStream<Solution_, A, B, C, D>
        extends BavetAbstractConstraintStream<Solution_>
        implements InnerQuadConstraintStream<A, B, C, D> {

    protected final List<BavetAbstractQuadConstraintStream<Solution_, A, B, C, D>> childStreamList = new ArrayList<>(2);

    public BavetAbstractQuadConstraintStream(BavetConstraintFactory<Solution_> constraintFactory,
                                             RetrievalSemantics retrievalSemantics) {
        super(constraintFactory, retrievalSemantics);
    }

    public List<BavetAbstractQuadConstraintStream<Solution_, A, B, C, D>> getChildStreamList() {
        return childStreamList;
    }

    // ************************************************************************
    // Stream builder methods
    // ************************************************************************

    public <Stream_ extends BavetAbstractQuadConstraintStream<Solution_, A, B, C, D>> Stream_ shareAndAddChild(
            Stream_ stream) {
        return constraintFactory.share(stream, childStreamList::add);
    }

    // ************************************************************************
    // Filter
    // ************************************************************************

    @Override
    public BavetAbstractQuadConstraintStream<Solution_, A, B, C, D> filter(QuadPredicate<A, B, C, D> predicate) {
        return shareAndAddChild(
                new BavetFilterQuadConstraintStream<>(constraintFactory, this, predicate));
    }

    // ************************************************************************
    // If (not) exists
    // ************************************************************************

    @SafeVarargs
    @Override
    public final <E> QuadConstraintStream<A, B, C, D> ifExists(Class<E> otherClass, PentaJoiner<A, B, C, D, E>... joiners) {
        if (getRetrievalSemantics() == RetrievalSemantics.STANDARD) {
            return ifExists(constraintFactory.forEach(otherClass), joiners);
        } else {
            // Calls fromUnfiltered() for backward compatibility only
            return ifExists(constraintFactory.fromUnfiltered(otherClass), joiners);
        }
    }

    @SafeVarargs
    @Override
    public final <E> QuadConstraintStream<A, B, C, D> ifExistsIncludingNullVars(Class<E> otherClass,
            PentaJoiner<A, B, C, D, E>... joiners) {
        if (getRetrievalSemantics() == RetrievalSemantics.STANDARD) {
            return ifExists(constraintFactory.forEachIncludingNullVars(otherClass), joiners);
        } else {
            return ifExists(constraintFactory.fromUnfiltered(otherClass), joiners);
        }
    }

    @SafeVarargs
    public final <E> QuadConstraintStream<A, B, C, D> ifExists(UniConstraintStream<E> otherStream,
            PentaJoiner<A, B, C, D, E>... joiners) {
        return ifExistsOrNot(true, otherStream, joiners);
    }

    @SafeVarargs
    @Override
    public final <E> QuadConstraintStream<A, B, C, D> ifNotExists(Class<E> otherClass, PentaJoiner<A, B, C, D, E>... joiners) {
        if (getRetrievalSemantics() == RetrievalSemantics.STANDARD) {
            return ifNotExists(constraintFactory.forEach(otherClass), joiners);
        } else {
            // Calls fromUnfiltered() for backward compatibility only
            return ifNotExists(constraintFactory.fromUnfiltered(otherClass), joiners);
        }
    }

    @SafeVarargs
    @Override
    public final <E> QuadConstraintStream<A, B, C, D> ifNotExistsIncludingNullVars(Class<E> otherClass,
            PentaJoiner<A, B, C, D, E>... joiners) {
        if (getRetrievalSemantics() == RetrievalSemantics.STANDARD) {
            return ifNotExists(constraintFactory.forEachIncludingNullVars(otherClass), joiners);
        } else {
            return ifNotExists(constraintFactory.fromUnfiltered(otherClass), joiners);
        }
    }

    @SafeVarargs
    public final <E> QuadConstraintStream<A, B, C, D> ifNotExists(UniConstraintStream<E> otherStream,
            PentaJoiner<A, B, C, D, E>... joiners) {
        return ifExistsOrNot(false, otherStream, joiners);
    }

    private <E> QuadConstraintStream<A, B, C, D> ifExistsOrNot(boolean shouldExist,
            UniConstraintStream<E> otherStream, PentaJoiner<A, B, C, D, E>[] joiners) {
        BavetAbstractUniConstraintStream<Solution_, E> other = assertBavetUniConstraintStream(otherStream);
        PentaJoinerComber<A, B, C, D, E> joinerComber = PentaJoinerComber.comb(joiners);
        BavetIfExistsBridgeUniConstraintStream<Solution_, E> parentBridgeD = other.shareAndAddChild(
                new BavetIfExistsBridgeUniConstraintStream<>(constraintFactory, other));
        return constraintFactory.share(
                new BavetIfExistsQuadConstraintStream<>(constraintFactory, this, parentBridgeD,
                        shouldExist, joinerComber.getMergedJoiner(), joinerComber.getMergedFiltering()),
                childStreamList::add);
    }

    // ************************************************************************
    // Group by
    // ************************************************************************

    @Override
    public <ResultContainer_, Result_> UniConstraintStream<Result_> groupBy(
            QuadConstraintCollector<A, B, C, D, ResultContainer_, Result_> collector) {
        QuadGroupNodeConstructor<A, B, C, D, UniTuple<Result_>> nodeConstructor =
                (int inputStoreIndex, Consumer<UniTuple<Result_>> insert, Consumer<UniTuple<Result_>> retract,
                 int outputStoreSize) -> new Group0Mapping1CollectorQuadNode<>(inputStoreIndex, collector, insert,
                                retract, outputStoreSize);
        return buildUniGroupBy(nodeConstructor);
    }

    private <NewA> UniConstraintStream<NewA>
            buildUniGroupBy(QuadGroupNodeConstructor<A, B, C, D, UniTuple<NewA>> nodeConstructor) {
        BavetUniGroupBridgeQuadConstraintStream<Solution_, A, B, C, D, NewA> bridge = shareAndAddChild(
                new BavetUniGroupBridgeQuadConstraintStream<>(constraintFactory, this, nodeConstructor));
        return constraintFactory.share(
                new BavetGroupUniConstraintStream<>(constraintFactory, bridge),
                bridge::setGroupStream);
    }

    @Override
    public <ResultContainerA_, ResultA_, ResultContainerB_, ResultB_> BiConstraintStream<ResultA_, ResultB_> groupBy(
            QuadConstraintCollector<A, B, C, D, ResultContainerA_, ResultA_> collectorA,
            QuadConstraintCollector<A, B, C, D, ResultContainerB_, ResultB_> collectorB) {
        QuadGroupNodeConstructor<A, B, C, D, BiTuple<ResultA_, ResultB_>> nodeConstructor =
                (int inputStoreIndex, Consumer<BiTuple<ResultA_, ResultB_>> insert,
                        Consumer<BiTuple<ResultA_, ResultB_>> retract,
                        int outputStoreSize) -> new Group0Mapping2CollectorQuadNode<>(inputStoreIndex, collectorA, collectorB,
                                insert, retract, outputStoreSize);
        return buildBiGroupBy(nodeConstructor);
    }

    private <NewA, NewB> BiConstraintStream<NewA, NewB>
            buildBiGroupBy(QuadGroupNodeConstructor<A, B, C, D, BiTuple<NewA, NewB>> nodeConstructor) {
        BavetBiGroupBridgeQuadConstraintStream<Solution_, A, B, C, D, NewA, NewB> bridge = shareAndAddChild(
                new BavetBiGroupBridgeQuadConstraintStream<>(constraintFactory, this, nodeConstructor));
        return constraintFactory.share(
                new BavetGroupBiConstraintStream<>(constraintFactory, bridge),
                bridge::setGroupStream);
    }

    @Override
    public <ResultContainerA_, ResultA_, ResultContainerB_, ResultB_, ResultContainerC_, ResultC_>
            TriConstraintStream<ResultA_, ResultB_, ResultC_>
            groupBy(QuadConstraintCollector<A, B, C, D, ResultContainerA_, ResultA_> collectorA,
                    QuadConstraintCollector<A, B, C, D, ResultContainerB_, ResultB_> collectorB,
                    QuadConstraintCollector<A, B, C, D, ResultContainerC_, ResultC_> collectorC) {
        QuadGroupNodeConstructor<A, B, C, D, TriTuple<ResultA_, ResultB_, ResultC_>> nodeConstructor =
                (int inputStoreIndex, Consumer<TriTuple<ResultA_, ResultB_, ResultC_>> insert,
                        Consumer<TriTuple<ResultA_, ResultB_, ResultC_>> retract,
                        int outputStoreSize) -> new Group0Mapping3CollectorQuadNode<>(inputStoreIndex, collectorA, collectorB,
                                collectorC, insert, retract, outputStoreSize);
        return buildTriGroupBy(nodeConstructor);
    }

    private <NewA, NewB, NewC> TriConstraintStream<NewA, NewB, NewC>
            buildTriGroupBy(QuadGroupNodeConstructor<A, B, C, D, TriTuple<NewA, NewB, NewC>> nodeConstructor) {
        BavetTriGroupBridgeQuadConstraintStream<Solution_, A, B, C, D, NewA, NewB, NewC> bridge = shareAndAddChild(
                new BavetTriGroupBridgeQuadConstraintStream<>(constraintFactory, this, nodeConstructor));
        return constraintFactory.share(
                new BavetGroupTriConstraintStream<>(constraintFactory, bridge),
                bridge::setGroupStream);
    }

    @Override
    public <ResultContainerA_, ResultA_, ResultContainerB_, ResultB_, ResultContainerC_, ResultC_, ResultContainerD_, ResultD_>
            QuadConstraintStream<ResultA_, ResultB_, ResultC_, ResultD_>
            groupBy(QuadConstraintCollector<A, B, C, D, ResultContainerA_, ResultA_> collectorA,
                    QuadConstraintCollector<A, B, C, D, ResultContainerB_, ResultB_> collectorB,
                    QuadConstraintCollector<A, B, C, D, ResultContainerC_, ResultC_> collectorC,
                    QuadConstraintCollector<A, B, C, D, ResultContainerD_, ResultD_> collectorD) {
        QuadGroupNodeConstructor<A, B, C, D, QuadTuple<ResultA_, ResultB_, ResultC_, ResultD_>> nodeConstructor =
                (int inputStoreIndex, Consumer<QuadTuple<ResultA_, ResultB_, ResultC_, ResultD_>> insert,
                        Consumer<QuadTuple<ResultA_, ResultB_, ResultC_, ResultD_>> retract,
                        int outputStoreSize) -> new Group0Mapping4CollectorQuadNode<>(inputStoreIndex, collectorA, collectorB,
                                collectorC, collectorD, insert, retract, outputStoreSize);
        return buildQuadGroupBy(nodeConstructor);
    }

    private <NewA, NewB, NewC, NewD> QuadConstraintStream<NewA, NewB, NewC, NewD>
            buildQuadGroupBy(QuadGroupNodeConstructor<A, B, C, D, QuadTuple<NewA, NewB, NewC, NewD>> nodeConstructor) {
        BavetQuadGroupBridgeQuadConstraintStream<Solution_, A, B, C, D, NewA, NewB, NewC, NewD> bridge = shareAndAddChild(
                new BavetQuadGroupBridgeQuadConstraintStream<>(constraintFactory, this, nodeConstructor));
        return constraintFactory.share(
                new BavetGroupQuadConstraintStream<>(constraintFactory, bridge),
                bridge::setGroupStream);
    }

    @Override
    public <GroupKey_> UniConstraintStream<GroupKey_> groupBy(QuadFunction<A, B, C, D, GroupKey_> groupKeyMapping) {
        QuadGroupNodeConstructor<A, B, C, D, UniTuple<GroupKey_>> nodeConstructor =
                (int inputStoreIndex, Consumer<UniTuple<GroupKey_>> insert, Consumer<UniTuple<GroupKey_>> retract,
                 int outputStoreSize) -> new Group1Mapping0CollectorQuadNode<>(groupKeyMapping, inputStoreIndex, insert,
                                retract, outputStoreSize);
        return buildUniGroupBy(nodeConstructor);
    }

    @Override
    public <GroupKey_, ResultContainerB_, ResultB_, ResultContainerC_, ResultC_>
            TriConstraintStream<GroupKey_, ResultB_, ResultC_> groupBy(QuadFunction<A, B, C, D, GroupKey_> groupKeyMapping,
                    QuadConstraintCollector<A, B, C, D, ResultContainerB_, ResultB_> collectorB,
                    QuadConstraintCollector<A, B, C, D, ResultContainerC_, ResultC_> collectorC) {
        QuadGroupNodeConstructor<A, B, C, D, TriTuple<GroupKey_, ResultB_, ResultC_>> nodeConstructor =
                (int inputStoreIndex, Consumer<TriTuple<GroupKey_, ResultB_, ResultC_>> insert,
                        Consumer<TriTuple<GroupKey_, ResultB_, ResultC_>> retract,
                        int outputStoreSize) -> new Group1Mapping2CollectorQuadNode<>(groupKeyMapping, inputStoreIndex,
                                collectorB, collectorC, insert, retract, outputStoreSize);
        return buildTriGroupBy(nodeConstructor);
    }

    @Override
    public <GroupKey_, ResultContainerB_, ResultB_, ResultContainerC_, ResultC_, ResultContainerD_, ResultD_>
            QuadConstraintStream<GroupKey_, ResultB_, ResultC_, ResultD_>
            groupBy(QuadFunction<A, B, C, D, GroupKey_> groupKeyMapping,
                    QuadConstraintCollector<A, B, C, D, ResultContainerB_, ResultB_> collectorB,
                    QuadConstraintCollector<A, B, C, D, ResultContainerC_, ResultC_> collectorC,
                    QuadConstraintCollector<A, B, C, D, ResultContainerD_, ResultD_> collectorD) {
        QuadGroupNodeConstructor<A, B, C, D, QuadTuple<GroupKey_, ResultB_, ResultC_, ResultD_>> nodeConstructor =
                (int inputStoreIndex, Consumer<QuadTuple<GroupKey_, ResultB_, ResultC_, ResultD_>> insert,
                        Consumer<QuadTuple<GroupKey_, ResultB_, ResultC_, ResultD_>> retract,
                        int outputStoreSize) -> new Group1Mapping3CollectorQuadNode<>(groupKeyMapping, inputStoreIndex,
                                collectorB, collectorC, collectorD, insert, retract, outputStoreSize);
        return buildQuadGroupBy(nodeConstructor);
    }

    @Override
    public <GroupKey_, ResultContainer_, Result_> BiConstraintStream<GroupKey_, Result_> groupBy(
            QuadFunction<A, B, C, D, GroupKey_> groupKeyMapping,
            QuadConstraintCollector<A, B, C, D, ResultContainer_, Result_> collector) {
        QuadGroupNodeConstructor<A, B, C, D, BiTuple<GroupKey_, Result_>> nodeConstructor =
                (int inputStoreIndex, Consumer<BiTuple<GroupKey_, Result_>> insert,
                        Consumer<BiTuple<GroupKey_, Result_>> retract,
                        int outputStoreSize) -> new Group1Mapping1CollectorQuadNode<>(groupKeyMapping, inputStoreIndex,
                                collector, insert, retract, outputStoreSize);
        return buildBiGroupBy(nodeConstructor);
    }

    @Override
    public <GroupKeyA_, GroupKeyB_> BiConstraintStream<GroupKeyA_, GroupKeyB_> groupBy(
            QuadFunction<A, B, C, D, GroupKeyA_> groupKeyAMapping,
            QuadFunction<A, B, C, D, GroupKeyB_> groupKeyBMapping) {
        QuadGroupNodeConstructor<A, B, C, D, BiTuple<GroupKeyA_, GroupKeyB_>> nodeConstructor =
                (int inputStoreIndex, Consumer<BiTuple<GroupKeyA_, GroupKeyB_>> insert,
                        Consumer<BiTuple<GroupKeyA_, GroupKeyB_>> retract,
                        int outputStoreSize) -> new Group2Mapping0CollectorQuadNode<>(groupKeyAMapping, groupKeyBMapping,
                                inputStoreIndex,
                                insert, retract, outputStoreSize);
        return buildBiGroupBy(nodeConstructor);
    }

    @Override
    public <GroupKeyA_, GroupKeyB_, ResultContainer_, Result_> TriConstraintStream<GroupKeyA_, GroupKeyB_, Result_> groupBy(
            QuadFunction<A, B, C, D, GroupKeyA_> groupKeyAMapping,
            QuadFunction<A, B, C, D, GroupKeyB_> groupKeyBMapping,
            QuadConstraintCollector<A, B, C, D, ResultContainer_, Result_> collector) {
        QuadGroupNodeConstructor<A, B, C, D, TriTuple<GroupKeyA_, GroupKeyB_, Result_>> nodeConstructor =
                (int inputStoreIndex, Consumer<TriTuple<GroupKeyA_, GroupKeyB_, Result_>> insert,
                        Consumer<TriTuple<GroupKeyA_, GroupKeyB_, Result_>> retract,
                        int outputStoreSize) -> new Group2Mapping1CollectorQuadNode<>(groupKeyAMapping, groupKeyBMapping,
                                inputStoreIndex, collector, insert, retract, outputStoreSize);
        return buildTriGroupBy(nodeConstructor);
    }

    @Override
    public <GroupKeyA_, GroupKeyB_, ResultContainerC_, ResultC_, ResultContainerD_, ResultD_>
            QuadConstraintStream<GroupKeyA_, GroupKeyB_, ResultC_, ResultD_> groupBy(
                    QuadFunction<A, B, C, D, GroupKeyA_> groupKeyAMapping,
                    QuadFunction<A, B, C, D, GroupKeyB_> groupKeyBMapping,
                    QuadConstraintCollector<A, B, C, D, ResultContainerC_, ResultC_> collectorC,
                    QuadConstraintCollector<A, B, C, D, ResultContainerD_, ResultD_> collectorD) {
        QuadGroupNodeConstructor<A, B, C, D, QuadTuple<GroupKeyA_, GroupKeyB_, ResultC_, ResultD_>> nodeConstructor =
                (int inputStoreIndex, Consumer<QuadTuple<GroupKeyA_, GroupKeyB_, ResultC_, ResultD_>> insert,
                        Consumer<QuadTuple<GroupKeyA_, GroupKeyB_, ResultC_, ResultD_>> retract,
                        int outputStoreSize) -> new Group2Mapping2CollectorQuadNode<>(groupKeyAMapping, groupKeyBMapping,
                                inputStoreIndex, collectorC, collectorD, insert, retract, outputStoreSize);
        return buildQuadGroupBy(nodeConstructor);
    }

    @Override
    public <GroupKeyA_, GroupKeyB_, GroupKeyC_> TriConstraintStream<GroupKeyA_, GroupKeyB_, GroupKeyC_> groupBy(
            QuadFunction<A, B, C, D, GroupKeyA_> groupKeyAMapping, QuadFunction<A, B, C, D, GroupKeyB_> groupKeyBMapping,
            QuadFunction<A, B, C, D, GroupKeyC_> groupKeyCMapping) {
        QuadGroupNodeConstructor<A, B, C, D, TriTuple<GroupKeyA_, GroupKeyB_, GroupKeyC_>> nodeConstructor =
                (int inputStoreIndex, Consumer<TriTuple<GroupKeyA_, GroupKeyB_, GroupKeyC_>> insert,
                        Consumer<TriTuple<GroupKeyA_, GroupKeyB_, GroupKeyC_>> retract,
                        int outputStoreSize) -> new Group3Mapping0CollectorQuadNode<>(groupKeyAMapping, groupKeyBMapping,
                                groupKeyCMapping,
                                inputStoreIndex, insert, retract, outputStoreSize);
        return buildTriGroupBy(nodeConstructor);
    }

    @Override
    public <GroupKeyA_, GroupKeyB_, GroupKeyC_, ResultContainerD_, ResultD_>
            QuadConstraintStream<GroupKeyA_, GroupKeyB_, GroupKeyC_, ResultD_>
            groupBy(QuadFunction<A, B, C, D, GroupKeyA_> groupKeyAMapping,
                    QuadFunction<A, B, C, D, GroupKeyB_> groupKeyBMapping,
                    QuadFunction<A, B, C, D, GroupKeyC_> groupKeyCMapping,
                    QuadConstraintCollector<A, B, C, D, ResultContainerD_, ResultD_> collectorD) {
        QuadGroupNodeConstructor<A, B, C, D, QuadTuple<GroupKeyA_, GroupKeyB_, GroupKeyC_, ResultD_>> nodeConstructor =
                (int inputStoreIndex, Consumer<QuadTuple<GroupKeyA_, GroupKeyB_, GroupKeyC_, ResultD_>> insert,
                        Consumer<QuadTuple<GroupKeyA_, GroupKeyB_, GroupKeyC_, ResultD_>> retract,
                        int outputStoreSize) -> new Group3Mapping1CollectorQuadNode<>(groupKeyAMapping, groupKeyBMapping,
                                groupKeyCMapping, inputStoreIndex, collectorD, insert, retract, outputStoreSize);
        return buildQuadGroupBy(nodeConstructor);
    }

    @Override
    public <GroupKeyA_, GroupKeyB_, GroupKeyC_, GroupKeyD_> QuadConstraintStream<GroupKeyA_, GroupKeyB_, GroupKeyC_, GroupKeyD_>
            groupBy(QuadFunction<A, B, C, D, GroupKeyA_> groupKeyAMapping,
                    QuadFunction<A, B, C, D, GroupKeyB_> groupKeyBMapping,
                    QuadFunction<A, B, C, D, GroupKeyC_> groupKeyCMapping,
                    QuadFunction<A, B, C, D, GroupKeyD_> groupKeyDMapping) {
        QuadGroupNodeConstructor<A, B, C, D, QuadTuple<GroupKeyA_, GroupKeyB_, GroupKeyC_, GroupKeyD_>> nodeConstructor =
                (int inputStoreIndex, Consumer<QuadTuple<GroupKeyA_, GroupKeyB_, GroupKeyC_, GroupKeyD_>> insert,
                        Consumer<QuadTuple<GroupKeyA_, GroupKeyB_, GroupKeyC_, GroupKeyD_>> retract,
                        int outputStoreSize) -> new Group4Mapping0CollectorQuadNode<>(groupKeyAMapping, groupKeyBMapping,
                                groupKeyCMapping, groupKeyDMapping, inputStoreIndex, insert, retract, outputStoreSize);
        return buildQuadGroupBy(nodeConstructor);
    }

    // ************************************************************************
    // Operations with duplicate tuple possibility
    // ************************************************************************

    @Override
    public <ResultA_> UniConstraintStream<ResultA_> map(QuadFunction<A, B, C, D, ResultA_> mapping) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <ResultD_> QuadConstraintStream<A, B, C, ResultD_> flattenLast(Function<D, Iterable<ResultD_>> mapping) {
        throw new UnsupportedOperationException();
    }

    // ************************************************************************
    // Penalize/reward
    // ************************************************************************

    @Override
    public final Constraint impactScore(String constraintPackage, String constraintName, Score<?> constraintWeight,
            ScoreImpactType impactType) {
        BavetScoringQuadConstraintStream<Solution_, A, B, C, D> stream = shareAndAddChild(
                new BavetScoringQuadConstraintStream<>(constraintFactory, this));
        return buildConstraint(constraintPackage, constraintName, constraintWeight,
                impactType, stream);
    }

    @Override
    public final Constraint impactScore(String constraintPackage, String constraintName, Score<?> constraintWeight,
            ToIntQuadFunction<A, B, C, D> matchWeigher, ScoreImpactType impactType) {
        BavetScoringQuadConstraintStream<Solution_, A, B, C, D> stream = shareAndAddChild(
                new BavetScoringQuadConstraintStream<>(constraintFactory, this, matchWeigher));
        return buildConstraint(constraintPackage, constraintName, constraintWeight,
                impactType, stream);
    }

    @Override
    public final Constraint impactScoreLong(String constraintPackage, String constraintName,
            Score<?> constraintWeight, ToLongQuadFunction<A, B, C, D> matchWeigher, ScoreImpactType impactType) {
        BavetScoringQuadConstraintStream<Solution_, A, B, C, D> stream = shareAndAddChild(
                new BavetScoringQuadConstraintStream<>(constraintFactory, this, matchWeigher));
        return buildConstraint(constraintPackage, constraintName, constraintWeight,
                impactType, stream);
    }

    @Override
    public final Constraint impactScoreBigDecimal(String constraintPackage, String constraintName,
            Score<?> constraintWeight, QuadFunction<A, B, C, D, BigDecimal> matchWeigher, ScoreImpactType impactType) {
        BavetScoringQuadConstraintStream<Solution_, A, B, C, D> stream = shareAndAddChild(
                new BavetScoringQuadConstraintStream<>(constraintFactory, this, matchWeigher));
        return buildConstraint(constraintPackage, constraintName, constraintWeight,
                impactType, stream);
    }

    @Override
    public final Constraint impactScoreConfigurable(String constraintPackage, String constraintName,
            ScoreImpactType impactType) {
        BavetScoringQuadConstraintStream<Solution_, A, B, C, D> stream = shareAndAddChild(
                new BavetScoringQuadConstraintStream<>(constraintFactory, this));
        return buildConstraintConfigurable(constraintPackage, constraintName,
                impactType, stream);
    }

    @Override
    public final Constraint impactScoreConfigurable(String constraintPackage, String constraintName,
            ToIntQuadFunction<A, B, C, D> matchWeigher, ScoreImpactType impactType) {
        BavetScoringQuadConstraintStream<Solution_, A, B, C, D> stream = shareAndAddChild(
                new BavetScoringQuadConstraintStream<>(constraintFactory, this, matchWeigher));
        return buildConstraintConfigurable(constraintPackage, constraintName,
                impactType, stream);
    }

    @Override
    public final Constraint impactScoreConfigurableLong(String constraintPackage, String constraintName,
            ToLongQuadFunction<A, B, C, D> matchWeigher, ScoreImpactType impactType) {
        BavetScoringQuadConstraintStream<Solution_, A, B, C, D> stream = shareAndAddChild(
                new BavetScoringQuadConstraintStream<>(constraintFactory, this, matchWeigher));
        return buildConstraintConfigurable(constraintPackage, constraintName,
                impactType, stream);
    }

    @Override
    public final Constraint impactScoreConfigurableBigDecimal(String constraintPackage, String constraintName,
            QuadFunction<A, B, C, D, BigDecimal> matchWeigher, ScoreImpactType impactType) {
        BavetScoringQuadConstraintStream<Solution_, A, B, C, D> stream = shareAndAddChild(
                new BavetScoringQuadConstraintStream<>(constraintFactory, this, matchWeigher));
        return buildConstraintConfigurable(constraintPackage, constraintName,
                impactType, stream);
    }

}
