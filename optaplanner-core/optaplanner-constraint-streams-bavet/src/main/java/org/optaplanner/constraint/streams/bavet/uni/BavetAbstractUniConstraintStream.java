package org.optaplanner.constraint.streams.bavet.uni;

import static org.optaplanner.constraint.streams.bavet.common.GroupNodeConstructor.of;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.ToIntFunction;
import java.util.function.ToLongFunction;

import org.optaplanner.constraint.streams.bavet.BavetConstraintFactory;
import org.optaplanner.constraint.streams.bavet.bi.BavetGroupBiConstraintStream;
import org.optaplanner.constraint.streams.bavet.bi.BavetJoinBiConstraintStream;
import org.optaplanner.constraint.streams.bavet.bi.BiTuple;
import org.optaplanner.constraint.streams.bavet.common.BavetAbstractConstraintStream;
import org.optaplanner.constraint.streams.bavet.common.BavetScoringConstraintStream;
import org.optaplanner.constraint.streams.bavet.common.GroupNodeConstructor;
import org.optaplanner.constraint.streams.bavet.quad.BavetGroupQuadConstraintStream;
import org.optaplanner.constraint.streams.bavet.quad.QuadTuple;
import org.optaplanner.constraint.streams.bavet.tri.BavetGroupTriConstraintStream;
import org.optaplanner.constraint.streams.bavet.tri.TriTuple;
import org.optaplanner.constraint.streams.common.RetrievalSemantics;
import org.optaplanner.constraint.streams.common.ScoreImpactType;
import org.optaplanner.constraint.streams.common.bi.BiJoinerComber;
import org.optaplanner.constraint.streams.common.uni.InnerUniConstraintStream;
import org.optaplanner.constraint.streams.common.uni.UniConstraintBuilderImpl;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.stream.DefaultConstraintJustification;
import org.optaplanner.core.api.score.stream.bi.BiConstraintStream;
import org.optaplanner.core.api.score.stream.bi.BiJoiner;
import org.optaplanner.core.api.score.stream.quad.QuadConstraintStream;
import org.optaplanner.core.api.score.stream.tri.TriConstraintStream;
import org.optaplanner.core.api.score.stream.uni.UniConstraintBuilder;
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
                        joinerComber.getMergedJoiner(), joinerComber.getMergedFiltering());
        leftBridge.setJoinStream(joinStream);
        rightBridge.setJoinStream(joinStream);

        return constraintFactory.share(joinStream, joinStream_ -> {
            // Connect the bridges upstream, as it is an actual new join.
            getChildStreamList().add(leftBridge);
            other.getChildStreamList().add(rightBridge);
        });
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
        GroupNodeConstructor<UniTuple<Result_>> nodeConstructor =
                of((groupStoreIndex, undoStoreIndex, tupleLifecycle, outputStoreSize) -> new Group0Mapping1CollectorUniNode<>(
                        groupStoreIndex, undoStoreIndex, collector, tupleLifecycle, outputStoreSize));
        return buildUniGroupBy(nodeConstructor);
    }

    private <NewA> UniConstraintStream<NewA> buildUniGroupBy(GroupNodeConstructor<UniTuple<NewA>> nodeConstructor) {
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
        GroupNodeConstructor<BiTuple<ResultA_, ResultB_>> nodeConstructor =
                of((groupStoreIndex, undoStoreIndex, tupleLifecycle, outputStoreSize) -> new Group0Mapping2CollectorUniNode<>(
                        groupStoreIndex, undoStoreIndex, collectorA, collectorB, tupleLifecycle, outputStoreSize));
        return buildBiGroupBy(nodeConstructor);
    }

    private <NewA, NewB> BiConstraintStream<NewA, NewB>
            buildBiGroupBy(GroupNodeConstructor<BiTuple<NewA, NewB>> nodeConstructor) {
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
        GroupNodeConstructor<TriTuple<ResultA_, ResultB_, ResultC_>> nodeConstructor =
                of((groupStoreIndex, undoStoreIndex, tupleLifecycle, outputStoreSize) -> new Group0Mapping3CollectorUniNode<>(
                        groupStoreIndex, undoStoreIndex, collectorA, collectorB, collectorC, tupleLifecycle, outputStoreSize));
        return buildTriGroupBy(nodeConstructor);
    }

    private <NewA, NewB, NewC> TriConstraintStream<NewA, NewB, NewC>
            buildTriGroupBy(GroupNodeConstructor<TriTuple<NewA, NewB, NewC>> nodeConstructor) {
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
        GroupNodeConstructor<QuadTuple<ResultA_, ResultB_, ResultC_, ResultD_>> nodeConstructor =
                of((groupStoreIndex, undoStoreIndex, tupleLifecycle, outputStoreSize) -> new Group0Mapping4CollectorUniNode<>(
                        groupStoreIndex, undoStoreIndex, collectorA, collectorB, collectorC, collectorD, tupleLifecycle,
                        outputStoreSize));
        return buildQuadGroupBy(nodeConstructor);
    }

    private <NewA, NewB, NewC, NewD> QuadConstraintStream<NewA, NewB, NewC, NewD>
            buildQuadGroupBy(GroupNodeConstructor<QuadTuple<NewA, NewB, NewC, NewD>> nodeConstructor) {
        BavetQuadGroupBridgeUniConstraintStream<Solution_, A, NewA, NewB, NewC, NewD> bridge = shareAndAddChild(
                new BavetQuadGroupBridgeUniConstraintStream<>(constraintFactory, this, nodeConstructor));
        return constraintFactory.share(
                new BavetGroupQuadConstraintStream<>(constraintFactory, bridge),
                bridge::setGroupStream);
    }

    @Override
    public <GroupKey_> UniConstraintStream<GroupKey_> groupBy(Function<A, GroupKey_> groupKeyMapping) {
        GroupNodeConstructor<UniTuple<GroupKey_>> nodeConstructor =
                of((groupStoreIndex, tupleLifecycle, outputStoreSize) -> new Group1Mapping0CollectorUniNode<>(groupKeyMapping,
                        groupStoreIndex, tupleLifecycle, outputStoreSize));
        return buildUniGroupBy(nodeConstructor);
    }

    @Override
    public <GroupKey_, ResultContainerB_, ResultB_, ResultContainerC_, ResultC_>
            TriConstraintStream<GroupKey_, ResultB_, ResultC_> groupBy(Function<A, GroupKey_> groupKeyMapping,
                    UniConstraintCollector<A, ResultContainerB_, ResultB_> collectorB,
                    UniConstraintCollector<A, ResultContainerC_, ResultC_> collectorC) {
        GroupNodeConstructor<TriTuple<GroupKey_, ResultB_, ResultC_>> nodeConstructor =
                of((groupStoreIndex, undoStoreIndex, tupleLifecycle, outputStoreSize) -> new Group1Mapping2CollectorUniNode<>(
                        groupKeyMapping, groupStoreIndex, undoStoreIndex, collectorB, collectorC, tupleLifecycle,
                        outputStoreSize));
        return buildTriGroupBy(nodeConstructor);
    }

    @Override
    public <GroupKey_, ResultContainerB_, ResultB_, ResultContainerC_, ResultC_, ResultContainerD_, ResultD_>
            QuadConstraintStream<GroupKey_, ResultB_, ResultC_, ResultD_>
            groupBy(Function<A, GroupKey_> groupKeyMapping,
                    UniConstraintCollector<A, ResultContainerB_, ResultB_> collectorB,
                    UniConstraintCollector<A, ResultContainerC_, ResultC_> collectorC,
                    UniConstraintCollector<A, ResultContainerD_, ResultD_> collectorD) {
        GroupNodeConstructor<QuadTuple<GroupKey_, ResultB_, ResultC_, ResultD_>> nodeConstructor =
                of((groupStoreIndex, undoStoreIndex, tupleLifecycle, outputStoreSize) -> new Group1Mapping3CollectorUniNode<>(
                        groupKeyMapping, groupStoreIndex, undoStoreIndex, collectorB, collectorC, collectorD, tupleLifecycle,
                        outputStoreSize));
        return buildQuadGroupBy(nodeConstructor);
    }

    @Override
    public <GroupKey_, ResultContainer_, Result_> BiConstraintStream<GroupKey_, Result_> groupBy(
            Function<A, GroupKey_> groupKeyMapping,
            UniConstraintCollector<A, ResultContainer_, Result_> collector) {
        GroupNodeConstructor<BiTuple<GroupKey_, Result_>> nodeConstructor =
                of((groupStoreIndex, undoStoreIndex, tupleLifecycle, outputStoreSize) -> new Group1Mapping1CollectorUniNode<>(
                        groupKeyMapping, groupStoreIndex, undoStoreIndex, collector, tupleLifecycle, outputStoreSize));
        return buildBiGroupBy(nodeConstructor);
    }

    @Override
    public <GroupKeyA_, GroupKeyB_> BiConstraintStream<GroupKeyA_, GroupKeyB_> groupBy(
            Function<A, GroupKeyA_> groupKeyAMapping, Function<A, GroupKeyB_> groupKeyBMapping) {
        GroupNodeConstructor<BiTuple<GroupKeyA_, GroupKeyB_>> nodeConstructor =
                of((groupStoreIndex, tupleLifecycle, outputStoreSize) -> new Group2Mapping0CollectorUniNode<>(groupKeyAMapping,
                        groupKeyBMapping, groupStoreIndex, tupleLifecycle, outputStoreSize));
        return buildBiGroupBy(nodeConstructor);
    }

    @Override
    public <GroupKeyA_, GroupKeyB_, ResultContainer_, Result_> TriConstraintStream<GroupKeyA_, GroupKeyB_, Result_> groupBy(
            Function<A, GroupKeyA_> groupKeyAMapping, Function<A, GroupKeyB_> groupKeyBMapping,
            UniConstraintCollector<A, ResultContainer_, Result_> collector) {
        GroupNodeConstructor<TriTuple<GroupKeyA_, GroupKeyB_, Result_>> nodeConstructor =
                of((groupStoreIndex, undoStoreIndex, tupleLifecycle, outputStoreSize) -> new Group2Mapping1CollectorUniNode<>(
                        groupKeyAMapping, groupKeyBMapping, groupStoreIndex, undoStoreIndex, collector, tupleLifecycle,
                        outputStoreSize));
        return buildTriGroupBy(nodeConstructor);
    }

    @Override
    public <GroupKeyA_, GroupKeyB_, ResultContainerC_, ResultC_, ResultContainerD_, ResultD_>
            QuadConstraintStream<GroupKeyA_, GroupKeyB_, ResultC_, ResultD_> groupBy(
                    Function<A, GroupKeyA_> groupKeyAMapping, Function<A, GroupKeyB_> groupKeyBMapping,
                    UniConstraintCollector<A, ResultContainerC_, ResultC_> collectorC,
                    UniConstraintCollector<A, ResultContainerD_, ResultD_> collectorD) {
        GroupNodeConstructor<QuadTuple<GroupKeyA_, GroupKeyB_, ResultC_, ResultD_>> nodeConstructor =
                of((groupStoreIndex, undoStoreIndex, tupleLifecycle, outputStoreSize) -> new Group2Mapping2CollectorUniNode<>(
                        groupKeyAMapping, groupKeyBMapping, groupStoreIndex, undoStoreIndex, collectorC, collectorD,
                        tupleLifecycle, outputStoreSize));
        return buildQuadGroupBy(nodeConstructor);
    }

    @Override
    public <GroupKeyA_, GroupKeyB_, GroupKeyC_> TriConstraintStream<GroupKeyA_, GroupKeyB_, GroupKeyC_> groupBy(
            Function<A, GroupKeyA_> groupKeyAMapping, Function<A, GroupKeyB_> groupKeyBMapping,
            Function<A, GroupKeyC_> groupKeyCMapping) {
        GroupNodeConstructor<TriTuple<GroupKeyA_, GroupKeyB_, GroupKeyC_>> nodeConstructor =
                of((groupStoreIndex, tupleLifecycle, outputStoreSize) -> new Group3Mapping0CollectorUniNode<>(groupKeyAMapping,
                        groupKeyBMapping, groupKeyCMapping, groupStoreIndex, tupleLifecycle, outputStoreSize));
        return buildTriGroupBy(nodeConstructor);
    }

    @Override
    public <GroupKeyA_, GroupKeyB_, GroupKeyC_, ResultContainerD_, ResultD_>
            QuadConstraintStream<GroupKeyA_, GroupKeyB_, GroupKeyC_, ResultD_>
            groupBy(Function<A, GroupKeyA_> groupKeyAMapping, Function<A, GroupKeyB_> groupKeyBMapping,
                    Function<A, GroupKeyC_> groupKeyCMapping,
                    UniConstraintCollector<A, ResultContainerD_, ResultD_> collectorD) {
        GroupNodeConstructor<QuadTuple<GroupKeyA_, GroupKeyB_, GroupKeyC_, ResultD_>> nodeConstructor =
                of((groupStoreIndex, undoStoreIndex, tupleLifecycle, outputStoreSize) -> new Group3Mapping1CollectorUniNode<>(
                        groupKeyAMapping, groupKeyBMapping, groupKeyCMapping, groupStoreIndex, undoStoreIndex, collectorD,
                        tupleLifecycle, outputStoreSize));
        return buildQuadGroupBy(nodeConstructor);
    }

    @Override
    public <GroupKeyA_, GroupKeyB_, GroupKeyC_, GroupKeyD_> QuadConstraintStream<GroupKeyA_, GroupKeyB_, GroupKeyC_, GroupKeyD_>
            groupBy(Function<A, GroupKeyA_> groupKeyAMapping, Function<A, GroupKeyB_> groupKeyBMapping,
                    Function<A, GroupKeyC_> groupKeyCMapping, Function<A, GroupKeyD_> groupKeyDMapping) {
        GroupNodeConstructor<QuadTuple<GroupKeyA_, GroupKeyB_, GroupKeyC_, GroupKeyD_>> nodeConstructor =
                of((groupStoreIndex, tupleLifecycle, outputStoreSize) -> new Group4Mapping0CollectorUniNode<>(groupKeyAMapping,
                        groupKeyBMapping, groupKeyCMapping, groupKeyDMapping, groupStoreIndex, tupleLifecycle,
                        outputStoreSize));
        return buildQuadGroupBy(nodeConstructor);
    }

    // ************************************************************************
    // Operations with duplicate tuple possibility
    // ************************************************************************

    @Override
    public <ResultA_> UniConstraintStream<ResultA_> map(Function<A, ResultA_> mapping) {
        BavetMapBridgeUniConstraintStream<Solution_, A, ResultA_> bridge = shareAndAddChild(
                new BavetMapBridgeUniConstraintStream<>(constraintFactory, this, mapping));
        return constraintFactory.share(
                new BavetMapUniConstraintStream<>(constraintFactory, bridge),
                bridge::setMapStream);
    }

    @Override
    public <ResultA_> UniConstraintStream<ResultA_> flattenLast(Function<A, Iterable<ResultA_>> mapping) {
        BavetFlattenLastBridgeUniConstraintStream<Solution_, A, ResultA_> bridge = shareAndAddChild(
                new BavetFlattenLastBridgeUniConstraintStream<>(constraintFactory, this, mapping));
        return constraintFactory.share(
                new BavetFlattenLastUniConstraintStream<>(constraintFactory, bridge),
                bridge::setFlattenLastStream);
    }

    // ************************************************************************
    // Penalize/reward
    // ************************************************************************

    @Override
    public <Score_ extends Score<Score_>> UniConstraintBuilder<A, Score_> innerImpact(Score_ constraintWeight,
            ToIntFunction<A> matchWeigher, ScoreImpactType scoreImpactType) {
        var stream = shareAndAddChild(new BavetScoringUniConstraintStream<>(constraintFactory, this, matchWeigher));
        return newTerminator(stream, constraintWeight, scoreImpactType);
    }

    private <Score_ extends Score<Score_>> UniConstraintBuilderImpl<A, Score_> newTerminator(
            BavetScoringConstraintStream<Solution_> stream, Score_ constraintWeight, ScoreImpactType impactType) {
        return new UniConstraintBuilderImpl<>(
                (constraintPackage, constraintName, constraintWeight_, impactType_, justificationMapping,
                        indictedObjectsMapping) -> buildConstraint(
                                constraintPackage, constraintName, constraintWeight_, impactType_, justificationMapping,
                                indictedObjectsMapping, stream),
                impactType, constraintWeight);
    }

    @Override
    public <Score_ extends Score<Score_>> UniConstraintBuilder<A, Score_> innerImpact(Score_ constraintWeight,
            ToLongFunction<A> matchWeigher, ScoreImpactType scoreImpactType) {
        var stream = shareAndAddChild(new BavetScoringUniConstraintStream<>(constraintFactory, this, matchWeigher));
        return newTerminator(stream, constraintWeight, scoreImpactType);
    }

    @Override
    public <Score_ extends Score<Score_>> UniConstraintBuilder<A, Score_> innerImpact(Score_ constraintWeight,
            Function<A, BigDecimal> matchWeigher, ScoreImpactType scoreImpactType) {
        var stream = shareAndAddChild(new BavetScoringUniConstraintStream<>(constraintFactory, this, matchWeigher));
        return newTerminator(stream, constraintWeight, scoreImpactType);
    }

    @Override
    protected final BiFunction<A, Score<?>, DefaultConstraintJustification> getDefaultJustificationMapping() {
        return InnerUniConstraintStream.createDefaultJustificationMapping();
    }

    @Override
    protected final Function<A, Collection<?>> getDefaultIndictedObjectsMapping() {
        return InnerUniConstraintStream.createDefaultIndictedObjectsMapping();
    }

}
