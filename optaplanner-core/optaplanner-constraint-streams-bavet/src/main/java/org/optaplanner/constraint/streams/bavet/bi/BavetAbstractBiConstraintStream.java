package org.optaplanner.constraint.streams.bavet.bi;

import static org.optaplanner.constraint.streams.bavet.common.GroupNodeConstructor.of;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.ToIntBiFunction;
import java.util.function.ToLongBiFunction;

import org.optaplanner.constraint.streams.bavet.BavetConstraintFactory;
import org.optaplanner.constraint.streams.bavet.common.BavetAbstractConstraintStream;
import org.optaplanner.constraint.streams.bavet.common.BavetScoringConstraintStream;
import org.optaplanner.constraint.streams.bavet.common.GroupNodeConstructor;
import org.optaplanner.constraint.streams.bavet.quad.BavetGroupQuadConstraintStream;
import org.optaplanner.constraint.streams.bavet.quad.QuadTuple;
import org.optaplanner.constraint.streams.bavet.tri.BavetGroupTriConstraintStream;
import org.optaplanner.constraint.streams.bavet.tri.BavetJoinTriConstraintStream;
import org.optaplanner.constraint.streams.bavet.tri.TriTuple;
import org.optaplanner.constraint.streams.bavet.uni.BavetAbstractUniConstraintStream;
import org.optaplanner.constraint.streams.bavet.uni.BavetGroupUniConstraintStream;
import org.optaplanner.constraint.streams.bavet.uni.BavetIfExistsBridgeUniConstraintStream;
import org.optaplanner.constraint.streams.bavet.uni.BavetJoinBridgeUniConstraintStream;
import org.optaplanner.constraint.streams.bavet.uni.BavetMapUniConstraintStream;
import org.optaplanner.constraint.streams.bavet.uni.UniTuple;
import org.optaplanner.constraint.streams.common.RetrievalSemantics;
import org.optaplanner.constraint.streams.common.ScoreImpactType;
import org.optaplanner.constraint.streams.common.bi.BiConstraintBuilderImpl;
import org.optaplanner.constraint.streams.common.bi.InnerBiConstraintStream;
import org.optaplanner.constraint.streams.common.tri.TriJoinerComber;
import org.optaplanner.core.api.function.TriFunction;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.stream.DefaultConstraintJustification;
import org.optaplanner.core.api.score.stream.bi.BiConstraintBuilder;
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
        GroupNodeConstructor<UniTuple<Result_>> nodeConstructor = of((groupStoreIndex, undoStoreIndex, tupleLifecycle,
                outputStoreSize, environmentMode) -> new Group0Mapping1CollectorBiNode<>(groupStoreIndex, undoStoreIndex,
                        collector, tupleLifecycle, outputStoreSize, environmentMode));
        return buildUniGroupBy(nodeConstructor);
    }

    private <NewA> UniConstraintStream<NewA> buildUniGroupBy(GroupNodeConstructor<UniTuple<NewA>> nodeConstructor) {
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
        GroupNodeConstructor<BiTuple<ResultA_, ResultB_>> nodeConstructor = of((groupStoreIndex, undoStoreIndex, tupleLifecycle,
                outputStoreSize, environmentMode) -> new Group0Mapping2CollectorBiNode<>(groupStoreIndex, undoStoreIndex,
                        collectorA, collectorB, tupleLifecycle, outputStoreSize, environmentMode));
        return buildBiGroupBy(nodeConstructor);
    }

    private <NewA, NewB> BiConstraintStream<NewA, NewB>
            buildBiGroupBy(GroupNodeConstructor<BiTuple<NewA, NewB>> nodeConstructor) {
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
        GroupNodeConstructor<TriTuple<ResultA_, ResultB_, ResultC_>> nodeConstructor = of((groupStoreIndex, undoStoreIndex,
                tupleLifecycle, outputStoreSize, environmentMode) -> new Group0Mapping3CollectorBiNode<>(groupStoreIndex,
                        undoStoreIndex, collectorA, collectorB, collectorC, tupleLifecycle, outputStoreSize, environmentMode));
        return buildTriGroupBy(nodeConstructor);
    }

    private <NewA, NewB, NewC> TriConstraintStream<NewA, NewB, NewC>
            buildTriGroupBy(GroupNodeConstructor<TriTuple<NewA, NewB, NewC>> nodeConstructor) {
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
        GroupNodeConstructor<QuadTuple<ResultA_, ResultB_, ResultC_, ResultD_>> nodeConstructor =
                of((groupStoreIndex, undoStoreIndex, tupleLifecycle, outputStoreSize,
                        environmentMode) -> new Group0Mapping4CollectorBiNode<>(groupStoreIndex, undoStoreIndex, collectorA,
                                collectorB, collectorC, collectorD, tupleLifecycle, outputStoreSize, environmentMode));
        return buildQuadGroupBy(nodeConstructor);
    }

    private <NewA, NewB, NewC, NewD> QuadConstraintStream<NewA, NewB, NewC, NewD>
            buildQuadGroupBy(GroupNodeConstructor<QuadTuple<NewA, NewB, NewC, NewD>> nodeConstructor) {
        BavetQuadGroupBridgeBiConstraintStream<Solution_, A, B, NewA, NewB, NewC, NewD> bridge = shareAndAddChild(
                new BavetQuadGroupBridgeBiConstraintStream<>(constraintFactory, this, nodeConstructor));
        return constraintFactory.share(
                new BavetGroupQuadConstraintStream<>(constraintFactory, bridge),
                bridge::setGroupStream);
    }

    @Override
    public <GroupKey_> UniConstraintStream<GroupKey_> groupBy(BiFunction<A, B, GroupKey_> groupKeyMapping) {
        GroupNodeConstructor<UniTuple<GroupKey_>> nodeConstructor =
                of((groupStoreIndex, tupleLifecycle, outputStoreSize, environmentMode) -> new Group1Mapping0CollectorBiNode<>(
                        groupKeyMapping, groupStoreIndex, tupleLifecycle, outputStoreSize, environmentMode));
        return buildUniGroupBy(nodeConstructor);
    }

    @Override
    public <GroupKey_, ResultContainerB_, ResultB_, ResultContainerC_, ResultC_>
            TriConstraintStream<GroupKey_, ResultB_, ResultC_> groupBy(BiFunction<A, B, GroupKey_> groupKeyMapping,
                    BiConstraintCollector<A, B, ResultContainerB_, ResultB_> collectorB,
                    BiConstraintCollector<A, B, ResultContainerC_, ResultC_> collectorC) {
        GroupNodeConstructor<TriTuple<GroupKey_, ResultB_, ResultC_>> nodeConstructor =
                of((groupStoreIndex, undoStoreIndex, tupleLifecycle, outputStoreSize,
                        environmentMode) -> new Group1Mapping2CollectorBiNode<>(groupKeyMapping, groupStoreIndex,
                                undoStoreIndex, collectorB, collectorC, tupleLifecycle, outputStoreSize, environmentMode));
        return buildTriGroupBy(nodeConstructor);
    }

    @Override
    public <GroupKey_, ResultContainerB_, ResultB_, ResultContainerC_, ResultC_, ResultContainerD_, ResultD_>
            QuadConstraintStream<GroupKey_, ResultB_, ResultC_, ResultD_>
            groupBy(BiFunction<A, B, GroupKey_> groupKeyMapping,
                    BiConstraintCollector<A, B, ResultContainerB_, ResultB_> collectorB,
                    BiConstraintCollector<A, B, ResultContainerC_, ResultC_> collectorC,
                    BiConstraintCollector<A, B, ResultContainerD_, ResultD_> collectorD) {
        GroupNodeConstructor<QuadTuple<GroupKey_, ResultB_, ResultC_, ResultD_>> nodeConstructor =
                of((groupStoreIndex, undoStoreIndex, tupleLifecycle, outputStoreSize,
                        environmentMode) -> new Group1Mapping3CollectorBiNode<>(groupKeyMapping, groupStoreIndex,
                                undoStoreIndex, collectorB, collectorC, collectorD, tupleLifecycle, outputStoreSize,
                                environmentMode));
        return buildQuadGroupBy(nodeConstructor);
    }

    @Override
    public <GroupKey_, ResultContainer_, Result_> BiConstraintStream<GroupKey_, Result_> groupBy(
            BiFunction<A, B, GroupKey_> groupKeyMapping,
            BiConstraintCollector<A, B, ResultContainer_, Result_> collector) {
        GroupNodeConstructor<BiTuple<GroupKey_, Result_>> nodeConstructor = of((groupStoreIndex, undoStoreIndex, tupleLifecycle,
                outputStoreSize, environmentMode) -> new Group1Mapping1CollectorBiNode<>(groupKeyMapping, groupStoreIndex,
                        undoStoreIndex, collector, tupleLifecycle, outputStoreSize, environmentMode));
        return buildBiGroupBy(nodeConstructor);
    }

    @Override
    public <GroupKeyA_, GroupKeyB_> BiConstraintStream<GroupKeyA_, GroupKeyB_> groupBy(
            BiFunction<A, B, GroupKeyA_> groupKeyAMapping, BiFunction<A, B, GroupKeyB_> groupKeyBMapping) {
        GroupNodeConstructor<BiTuple<GroupKeyA_, GroupKeyB_>> nodeConstructor =
                of((groupStoreIndex, tupleLifecycle, outputStoreSize, environmentMode) -> new Group2Mapping0CollectorBiNode<>(
                        groupKeyAMapping, groupKeyBMapping, groupStoreIndex, tupleLifecycle, outputStoreSize, environmentMode));
        return buildBiGroupBy(nodeConstructor);
    }

    @Override
    public <GroupKeyA_, GroupKeyB_, ResultContainer_, Result_> TriConstraintStream<GroupKeyA_, GroupKeyB_, Result_> groupBy(
            BiFunction<A, B, GroupKeyA_> groupKeyAMapping, BiFunction<A, B, GroupKeyB_> groupKeyBMapping,
            BiConstraintCollector<A, B, ResultContainer_, Result_> collector) {
        GroupNodeConstructor<TriTuple<GroupKeyA_, GroupKeyB_, Result_>> nodeConstructor =
                of((groupStoreIndex, undoStoreIndex, tupleLifecycle, outputStoreSize,
                        environmentMode) -> new Group2Mapping1CollectorBiNode<>(groupKeyAMapping, groupKeyBMapping,
                                groupStoreIndex, undoStoreIndex, collector, tupleLifecycle, outputStoreSize, environmentMode));
        return buildTriGroupBy(nodeConstructor);
    }

    @Override
    public <GroupKeyA_, GroupKeyB_, ResultContainerC_, ResultC_, ResultContainerD_, ResultD_>
            QuadConstraintStream<GroupKeyA_, GroupKeyB_, ResultC_, ResultD_> groupBy(
                    BiFunction<A, B, GroupKeyA_> groupKeyAMapping, BiFunction<A, B, GroupKeyB_> groupKeyBMapping,
                    BiConstraintCollector<A, B, ResultContainerC_, ResultC_> collectorC,
                    BiConstraintCollector<A, B, ResultContainerD_, ResultD_> collectorD) {
        GroupNodeConstructor<QuadTuple<GroupKeyA_, GroupKeyB_, ResultC_, ResultD_>> nodeConstructor = of((groupStoreIndex,
                undoStoreIndex, tupleLifecycle, outputStoreSize,
                environmentMode) -> new Group2Mapping2CollectorBiNode<>(groupKeyAMapping, groupKeyBMapping, groupStoreIndex,
                        undoStoreIndex, collectorC, collectorD, tupleLifecycle, outputStoreSize, environmentMode));
        return buildQuadGroupBy(nodeConstructor);
    }

    @Override
    public <GroupKeyA_, GroupKeyB_, GroupKeyC_> TriConstraintStream<GroupKeyA_, GroupKeyB_, GroupKeyC_> groupBy(
            BiFunction<A, B, GroupKeyA_> groupKeyAMapping, BiFunction<A, B, GroupKeyB_> groupKeyBMapping,
            BiFunction<A, B, GroupKeyC_> groupKeyCMapping) {
        GroupNodeConstructor<TriTuple<GroupKeyA_, GroupKeyB_, GroupKeyC_>> nodeConstructor = of((groupStoreIndex,
                tupleLifecycle, outputStoreSize, environmentMode) -> new Group3Mapping0CollectorBiNode<>(groupKeyAMapping,
                        groupKeyBMapping, groupKeyCMapping, groupStoreIndex, tupleLifecycle, outputStoreSize, environmentMode));
        return buildTriGroupBy(nodeConstructor);
    }

    @Override
    public <GroupKeyA_, GroupKeyB_, GroupKeyC_, ResultContainerD_, ResultD_>
            QuadConstraintStream<GroupKeyA_, GroupKeyB_, GroupKeyC_, ResultD_>
            groupBy(BiFunction<A, B, GroupKeyA_> groupKeyAMapping, BiFunction<A, B, GroupKeyB_> groupKeyBMapping,
                    BiFunction<A, B, GroupKeyC_> groupKeyCMapping,
                    BiConstraintCollector<A, B, ResultContainerD_, ResultD_> collectorD) {
        GroupNodeConstructor<QuadTuple<GroupKeyA_, GroupKeyB_, GroupKeyC_, ResultD_>> nodeConstructor = of((groupStoreIndex,
                undoStoreIndex, tupleLifecycle, outputStoreSize,
                environmentMode) -> new Group3Mapping1CollectorBiNode<>(groupKeyAMapping, groupKeyBMapping, groupKeyCMapping,
                        groupStoreIndex, undoStoreIndex, collectorD, tupleLifecycle, outputStoreSize, environmentMode));
        return buildQuadGroupBy(nodeConstructor);
    }

    @Override
    public <GroupKeyA_, GroupKeyB_, GroupKeyC_, GroupKeyD_> QuadConstraintStream<GroupKeyA_, GroupKeyB_, GroupKeyC_, GroupKeyD_>
            groupBy(BiFunction<A, B, GroupKeyA_> groupKeyAMapping, BiFunction<A, B, GroupKeyB_> groupKeyBMapping,
                    BiFunction<A, B, GroupKeyC_> groupKeyCMapping, BiFunction<A, B, GroupKeyD_> groupKeyDMapping) {
        GroupNodeConstructor<QuadTuple<GroupKeyA_, GroupKeyB_, GroupKeyC_, GroupKeyD_>> nodeConstructor =
                of((groupStoreIndex, tupleLifecycle, outputStoreSize, environmentMode) -> new Group4Mapping0CollectorBiNode<>(
                        groupKeyAMapping, groupKeyBMapping, groupKeyCMapping, groupKeyDMapping, groupStoreIndex, tupleLifecycle,
                        outputStoreSize, environmentMode));
        return buildQuadGroupBy(nodeConstructor);
    }

    // ************************************************************************
    // Operations with duplicate tuple possibility
    // ************************************************************************

    @Override
    public <ResultA_> UniConstraintStream<ResultA_> map(BiFunction<A, B, ResultA_> mapping) {
        BavetMapBridgeBiConstraintStream<Solution_, A, B, ResultA_> bridge = shareAndAddChild(
                new BavetMapBridgeBiConstraintStream<>(constraintFactory, this, mapping));
        return constraintFactory.share(
                new BavetMapUniConstraintStream<>(constraintFactory, bridge),
                bridge::setMapStream);
    }

    @Override
    public <ResultB_> BiConstraintStream<A, ResultB_> flattenLast(Function<B, Iterable<ResultB_>> mapping) {
        BavetFlattenLastBridgeBiConstraintStream<Solution_, A, B, ResultB_> bridge = shareAndAddChild(
                new BavetFlattenLastBridgeBiConstraintStream<>(constraintFactory, this, mapping));
        return constraintFactory.share(
                new BavetFlattenLastBiConstraintStream<>(constraintFactory, bridge),
                bridge::setFlattenLastStream);
    }

    // ************************************************************************
    // Penalize/reward
    // ************************************************************************

    @Override
    public <Score_ extends Score<Score_>> BiConstraintBuilder<A, B, Score_> innerImpact(Score_ constraintWeight,
            ToIntBiFunction<A, B> matchWeigher, ScoreImpactType scoreImpactType) {
        var stream = shareAndAddChild(new BavetScoringBiConstraintStream<>(constraintFactory, this, matchWeigher));
        return newTerminator(stream, scoreImpactType, constraintWeight);
    }

    private <Score_ extends Score<Score_>> BiConstraintBuilderImpl<A, B, Score_> newTerminator(
            BavetScoringConstraintStream<Solution_> stream, ScoreImpactType impactType, Score_ constraintWeight) {
        return new BiConstraintBuilderImpl<>(
                (constraintPackage, constraintName, constraintWeight_, impactType_, justificationMapping,
                        indictedObjectsMapping) -> buildConstraint(constraintPackage, constraintName, constraintWeight_,
                                impactType_, justificationMapping, indictedObjectsMapping, stream),
                impactType, constraintWeight);
    }

    @Override
    public <Score_ extends Score<Score_>> BiConstraintBuilder<A, B, Score_> innerImpact(Score_ constraintWeight,
            ToLongBiFunction<A, B> matchWeigher, ScoreImpactType scoreImpactType) {
        var stream = shareAndAddChild(new BavetScoringBiConstraintStream<>(constraintFactory, this, matchWeigher));
        return newTerminator(stream, scoreImpactType, constraintWeight);
    }

    @Override
    public <Score_ extends Score<Score_>> BiConstraintBuilder<A, B, Score_> innerImpact(Score_ constraintWeight,
            BiFunction<A, B, BigDecimal> matchWeigher, ScoreImpactType scoreImpactType) {
        var stream = shareAndAddChild(new BavetScoringBiConstraintStream<>(constraintFactory, this, matchWeigher));
        return newTerminator(stream, scoreImpactType, constraintWeight);
    }

    @Override
    protected final TriFunction<A, B, Score<?>, DefaultConstraintJustification> getDefaultJustificationMapping() {
        return InnerBiConstraintStream.createDefaultJustificationMapping();
    }

    @Override
    protected final BiFunction<A, B, Collection<?>> getDefaultIndictedObjectsMapping() {
        return InnerBiConstraintStream.createDefaultIndictedObjectsMapping();
    }

}
