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
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.ToIntFunction;
import java.util.function.ToLongFunction;

import org.optaplanner.constraint.streams.bavet.BavetConstraintFactory;
import org.optaplanner.constraint.streams.bavet.bi.BavetGroupBiConstraintStream;
import org.optaplanner.constraint.streams.bavet.bi.BavetJoinBiConstraintStream;
import org.optaplanner.constraint.streams.bavet.common.BavetAbstractConstraintStream;
import org.optaplanner.constraint.streams.bavet.common.JoinerUtils;
import org.optaplanner.constraint.streams.bavet.common.index.IndexerFactory;
import org.optaplanner.constraint.streams.bi.DefaultBiJoiner;
import org.optaplanner.constraint.streams.common.RetrievalSemantics;
import org.optaplanner.constraint.streams.common.ScoreImpactType;
import org.optaplanner.constraint.streams.uni.InnerUniConstraintStream;
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
    public <B> BiConstraintStream<A, B> actuallyJoin(UniConstraintStream<B> otherStream,
            DefaultBiJoiner<A, B>... joiners) {
        BavetAbstractUniConstraintStream<Solution_, B> other = assertBavetUniConstraintStream(otherStream);
        DefaultBiJoiner<A, B> mergedJoiner = DefaultBiJoiner.merge(joiners);
        IndexerFactory indexerFactory = new IndexerFactory(mergedJoiner);
        Function<A, Object[]> leftMapping = JoinerUtils.combineLeftMappings(mergedJoiner);
        BavetJoinBridgeUniConstraintStream<Solution_, A> leftBridge = shareAndAddChild(
                new BavetJoinBridgeUniConstraintStream<>(constraintFactory, this, true));
        Function<B, Object[]> rightMapping = JoinerUtils.combineRightMappings(mergedJoiner);
        BavetJoinBridgeUniConstraintStream<Solution_, B> rightBridge = other.shareAndAddChild(
                new BavetJoinBridgeUniConstraintStream<>(constraintFactory, other, false));
        return constraintFactory.share(
                new BavetJoinBiConstraintStream<>(constraintFactory, leftBridge, rightBridge,
                        leftMapping, rightMapping, indexerFactory),
                joinStream_ -> {
                    leftBridge.setJoinStream(joinStream_);
                    rightBridge.setJoinStream(joinStream_);
                });
    }

    @Override
    public <B> BiConstraintStream<A, B> join(Class<B> otherClass, BiJoiner<A, B>... joiners) {
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
    public final <B> UniConstraintStream<A> ifExists(Class<B> otherClass, BiJoiner<A, B>... joiners) {
        if (getRetrievalSemantics() == RetrievalSemantics.STANDARD) {
            return ifExists(constraintFactory.forEach(otherClass), joiners);
        } else {
            return ifExists(constraintFactory.from(otherClass), joiners);
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
            return ifNotExists(constraintFactory.from(otherClass), joiners);
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

    private final <B> UniConstraintStream<A> ifExistsOrNot(boolean shouldExist, UniConstraintStream<B> otherStream,
            BiJoiner<A, B>[] joiners) {
        // TODO support FilteringBiJoiner like join() which probably should do it either?
        BavetAbstractUniConstraintStream<Solution_, B> other = assertBavetUniConstraintStream(otherStream);

        if (joiners.length != 1) {
            throw new UnsupportedOperationException();
        }
        BiJoiner<A, B> joiner = joiners[0];
        if (!(joiner instanceof DefaultBiJoiner)) {
            throw new IllegalArgumentException("The joiner class (" + joiner.getClass() + ") is not supported.");
        }
        DefaultBiJoiner<A, B> castedJoiner = (DefaultBiJoiner<A, B>) joiner;
        IndexerFactory indexerFactory = new IndexerFactory(castedJoiner);
        Function<A, Object[]> leftMapping = JoinerUtils.combineLeftMappings(castedJoiner);
        Function<B, Object[]> rightMapping = JoinerUtils.combineRightMappings(castedJoiner);
        BavetIfExistsBridgeUniConstraintStream<Solution_, A, B> parentBridgeB = other.shareAndAddChild(
                new BavetIfExistsBridgeUniConstraintStream<>(constraintFactory, other));
        return constraintFactory.share(
                new BavetIfExistsUniConstraintStream<>(constraintFactory, this, parentBridgeB,
                        shouldExist,
                        leftMapping, rightMapping, indexerFactory),
                ifExistsStream_ -> {
                    childStreamList.add(ifExistsStream_);
                    parentBridgeB.setIfExistsStream(ifExistsStream_);
                });
    }

    // ************************************************************************
    // Group by
    // ************************************************************************

    @Override
    public <ResultContainer_, Result_> UniConstraintStream<Result_> groupBy(
            UniConstraintCollector<A, ResultContainer_, Result_> collector) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <ResultContainerA_, ResultA_, ResultContainerB_, ResultB_> BiConstraintStream<ResultA_, ResultB_> groupBy(
            UniConstraintCollector<A, ResultContainerA_, ResultA_> collectorA,
            UniConstraintCollector<A, ResultContainerB_, ResultB_> collectorB) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <ResultContainerA_, ResultA_, ResultContainerB_, ResultB_, ResultContainerC_, ResultC_>
            TriConstraintStream<ResultA_, ResultB_, ResultC_>
            groupBy(UniConstraintCollector<A, ResultContainerA_, ResultA_> collectorA,
                    UniConstraintCollector<A, ResultContainerB_, ResultB_> collectorB,
                    UniConstraintCollector<A, ResultContainerC_, ResultC_> collectorC) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <ResultContainerA_, ResultA_, ResultContainerB_, ResultB_, ResultContainerC_, ResultC_, ResultContainerD_, ResultD_>
            QuadConstraintStream<ResultA_, ResultB_, ResultC_, ResultD_>
            groupBy(UniConstraintCollector<A, ResultContainerA_, ResultA_> collectorA,
                    UniConstraintCollector<A, ResultContainerB_, ResultB_> collectorB,
                    UniConstraintCollector<A, ResultContainerC_, ResultC_> collectorC,
                    UniConstraintCollector<A, ResultContainerD_, ResultD_> collectorD) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <GroupKey_> UniConstraintStream<GroupKey_> groupBy(Function<A, GroupKey_> groupKeyMapping) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <GroupKey_, ResultContainerB_, ResultB_, ResultContainerC_, ResultC_>
            TriConstraintStream<GroupKey_, ResultB_, ResultC_> groupBy(Function<A, GroupKey_> groupKeyMapping,
                    UniConstraintCollector<A, ResultContainerB_, ResultB_> collectorB,
                    UniConstraintCollector<A, ResultContainerC_, ResultC_> collectorC) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <GroupKey_, ResultContainerB_, ResultB_, ResultContainerC_, ResultC_, ResultContainerD_, ResultD_>
            QuadConstraintStream<GroupKey_, ResultB_, ResultC_, ResultD_>
            groupBy(Function<A, GroupKey_> groupKeyMapping, UniConstraintCollector<A, ResultContainerB_, ResultB_> collectorB,
                    UniConstraintCollector<A, ResultContainerC_, ResultC_> collectorC,
                    UniConstraintCollector<A, ResultContainerD_, ResultD_> collectorD) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <GroupKeyA_, GroupKeyB_> BiConstraintStream<GroupKeyA_, GroupKeyB_> groupBy(
            Function<A, GroupKeyA_> groupKeyAMapping, Function<A, GroupKeyB_> groupKeyBMapping) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <GroupKey_, ResultContainer_, Result_> BiConstraintStream<GroupKey_, Result_> groupBy(
            Function<A, GroupKey_> groupKeyMapping,
            UniConstraintCollector<A, ResultContainer_, Result_> collector) {
        BavetGroupBridgeUniConstraintStream<Solution_, A, GroupKey_, ResultContainer_, Result_> bridge =
                shareAndAddChild(
                        new BavetGroupBridgeUniConstraintStream<>(constraintFactory, this, groupKeyMapping, collector));
        return constraintFactory.share(
                new BavetGroupBiConstraintStream<>(constraintFactory, bridge),
                bridge::setGroupStream);
    }

    @Override
    public <GroupKeyA_, GroupKeyB_, ResultContainer_, Result_> TriConstraintStream<GroupKeyA_, GroupKeyB_, Result_> groupBy(
            Function<A, GroupKeyA_> groupKeyAMapping, Function<A, GroupKeyB_> groupKeyBMapping,
            UniConstraintCollector<A, ResultContainer_, Result_> collector) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <GroupKeyA_, GroupKeyB_, ResultContainerC_, ResultC_, ResultContainerD_, ResultD_>
            QuadConstraintStream<GroupKeyA_, GroupKeyB_, ResultC_, ResultD_> groupBy(
                    Function<A, GroupKeyA_> groupKeyAMapping, Function<A, GroupKeyB_> groupKeyBMapping,
                    UniConstraintCollector<A, ResultContainerC_, ResultC_> collectorC,
                    UniConstraintCollector<A, ResultContainerD_, ResultD_> collectorD) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <GroupKeyA_, GroupKeyB_, GroupKeyC_> TriConstraintStream<GroupKeyA_, GroupKeyB_, GroupKeyC_> groupBy(
            Function<A, GroupKeyA_> groupKeyAMapping, Function<A, GroupKeyB_> groupKeyBMapping,
            Function<A, GroupKeyC_> groupKeyCMapping) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <GroupKeyA_, GroupKeyB_, GroupKeyC_, ResultContainerD_, ResultD_>
            QuadConstraintStream<GroupKeyA_, GroupKeyB_, GroupKeyC_, ResultD_> groupBy(Function<A, GroupKeyA_> groupKeyAMapping,
                    Function<A, GroupKeyB_> groupKeyBMapping, Function<A, GroupKeyC_> groupKeyCMapping,
                    UniConstraintCollector<A, ResultContainerD_, ResultD_> collectorD) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <GroupKeyA_, GroupKeyB_, GroupKeyC_, GroupKeyD_> QuadConstraintStream<GroupKeyA_, GroupKeyB_, GroupKeyC_, GroupKeyD_>
            groupBy(Function<A, GroupKeyA_> groupKeyAMapping, Function<A, GroupKeyB_> groupKeyBMapping,
                    Function<A, GroupKeyC_> groupKeyCMapping, Function<A, GroupKeyD_> groupKeyDMapping) {
        throw new UnsupportedOperationException();
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
