package org.optaplanner.constraint.streams.drools.bi;

import static org.optaplanner.constraint.streams.common.RetrievalSemantics.STANDARD;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.ToIntBiFunction;
import java.util.function.ToLongBiFunction;

import org.optaplanner.constraint.streams.common.RetrievalSemantics;
import org.optaplanner.constraint.streams.common.ScoreImpactType;
import org.optaplanner.constraint.streams.common.bi.BiConstraintBuilderImpl;
import org.optaplanner.constraint.streams.common.bi.InnerBiConstraintStream;
import org.optaplanner.constraint.streams.common.tri.TriJoinerComber;
import org.optaplanner.constraint.streams.drools.DroolsConstraintFactory;
import org.optaplanner.constraint.streams.drools.common.BiLeftHandSide;
import org.optaplanner.constraint.streams.drools.common.DroolsAbstractConstraintStream;
import org.optaplanner.constraint.streams.drools.common.RuleBuilder;
import org.optaplanner.constraint.streams.drools.quad.DroolsGroupingQuadConstraintStream;
import org.optaplanner.constraint.streams.drools.tri.DroolsAbstractTriConstraintStream;
import org.optaplanner.constraint.streams.drools.tri.DroolsGroupingTriConstraintStream;
import org.optaplanner.constraint.streams.drools.tri.DroolsJoinTriConstraintStream;
import org.optaplanner.constraint.streams.drools.uni.DroolsAbstractUniConstraintStream;
import org.optaplanner.constraint.streams.drools.uni.DroolsGroupingUniConstraintStream;
import org.optaplanner.constraint.streams.drools.uni.DroolsMappingUniConstraintStream;
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

public abstract class DroolsAbstractBiConstraintStream<Solution_, A, B>
        extends DroolsAbstractConstraintStream<Solution_, BiLeftHandSide<A, B>>
        implements InnerBiConstraintStream<A, B> {

    public DroolsAbstractBiConstraintStream(DroolsConstraintFactory<Solution_> constraintFactory,
            RetrievalSemantics retrievalSemantics) {
        super(constraintFactory, retrievalSemantics);
    }

    @Override
    public BiConstraintStream<A, B> filter(BiPredicate<A, B> predicate) {
        DroolsAbstractBiConstraintStream<Solution_, A, B> stream =
                new DroolsFilterBiConstraintStream<>(constraintFactory, this, predicate);
        addChildStream(stream);
        return stream;
    }

    @Override
    @SafeVarargs
    public final <C> TriConstraintStream<A, B, C> join(UniConstraintStream<C> otherStream,
            TriJoiner<A, B, C>... joiners) {
        TriJoinerComber<A, B, C> joinerComber = TriJoinerComber.comb(joiners);
        DroolsAbstractTriConstraintStream<Solution_, A, B, C> stream =
                new DroolsJoinTriConstraintStream<>(constraintFactory, this,
                        (DroolsAbstractUniConstraintStream<Solution_, C>) otherStream,
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
    public final <C> BiConstraintStream<A, B> ifExists(Class<C> otherClass, TriJoiner<A, B, C>... joiners) {
        return ifExistsOrNot(true, getRetrievalSemantics() != STANDARD, otherClass, joiners);
    }

    @SafeVarargs
    @Override
    public final <C> BiConstraintStream<A, B> ifExistsIncludingNullVars(Class<C> otherClass, TriJoiner<A, B, C>... joiners) {
        return ifExistsOrNot(true, true, otherClass, joiners);
    }

    @SafeVarargs
    @Override
    public final <C> BiConstraintStream<A, B> ifNotExists(Class<C> otherClass, TriJoiner<A, B, C>... joiners) {
        return ifExistsOrNot(false, getRetrievalSemantics() != STANDARD, otherClass, joiners);
    }

    @SafeVarargs
    @Override
    public final <C> BiConstraintStream<A, B> ifNotExistsIncludingNullVars(Class<C> otherClass, TriJoiner<A, B, C>... joiners) {
        return ifExistsOrNot(false, true, otherClass, joiners);
    }

    @SafeVarargs
    private <C> BiConstraintStream<A, B> ifExistsOrNot(boolean shouldExist, boolean shouldIncludeNullVars,
            Class<C> otherClass, TriJoiner<A, B, C>... joiners) {
        getConstraintFactory().assertValidFromType(otherClass);
        DroolsExistsBiConstraintStream<Solution_, A, B> stream = new DroolsExistsBiConstraintStream<>(constraintFactory, this,
                shouldExist, shouldIncludeNullVars, otherClass, joiners);
        addChildStream(stream);
        return stream;
    }

    @Override
    public <ResultContainer_, Result_> UniConstraintStream<Result_> groupBy(
            BiConstraintCollector<A, B, ResultContainer_, Result_> collector) {
        DroolsGroupingUniConstraintStream<Solution_, Result_> stream = new DroolsGroupingUniConstraintStream<>(
                constraintFactory, this, collector);
        addChildStream(stream);
        return stream;
    }

    @Override
    public <ResultContainerA_, ResultA_, ResultContainerB_, ResultB_> BiConstraintStream<ResultA_, ResultB_> groupBy(
            BiConstraintCollector<A, B, ResultContainerA_, ResultA_> collectorA,
            BiConstraintCollector<A, B, ResultContainerB_, ResultB_> collectorB) {
        DroolsGroupingBiConstraintStream<Solution_, ResultA_, ResultB_> stream =
                new DroolsGroupingBiConstraintStream<>(constraintFactory, this, collectorA, collectorB);
        addChildStream(stream);
        return stream;
    }

    @Override
    public <ResultContainerA_, ResultA_, ResultContainerB_, ResultB_, ResultContainerC_, ResultC_>
            TriConstraintStream<ResultA_, ResultB_, ResultC_>
            groupBy(BiConstraintCollector<A, B, ResultContainerA_, ResultA_> collectorA,
                    BiConstraintCollector<A, B, ResultContainerB_, ResultB_> collectorB,
                    BiConstraintCollector<A, B, ResultContainerC_, ResultC_> collectorC) {
        DroolsGroupingTriConstraintStream<Solution_, ResultA_, ResultB_, ResultC_> stream =
                new DroolsGroupingTriConstraintStream<>(constraintFactory, this, collectorA, collectorB, collectorC);
        addChildStream(stream);
        return stream;
    }

    @Override
    public <ResultContainerA_, ResultA_, ResultContainerB_, ResultB_, ResultContainerC_, ResultC_, ResultContainerD_, ResultD_>
            QuadConstraintStream<ResultA_, ResultB_, ResultC_, ResultD_>
            groupBy(BiConstraintCollector<A, B, ResultContainerA_, ResultA_> collectorA,
                    BiConstraintCollector<A, B, ResultContainerB_, ResultB_> collectorB,
                    BiConstraintCollector<A, B, ResultContainerC_, ResultC_> collectorC,
                    BiConstraintCollector<A, B, ResultContainerD_, ResultD_> collectorD) {
        DroolsGroupingQuadConstraintStream<Solution_, ResultA_, ResultB_, ResultC_, ResultD_> stream =
                new DroolsGroupingQuadConstraintStream<>(constraintFactory, this, collectorA, collectorB, collectorC,
                        collectorD);
        addChildStream(stream);
        return stream;
    }

    @Override
    public <GroupKey_> UniConstraintStream<GroupKey_> groupBy(BiFunction<A, B, GroupKey_> groupKeyMapping) {
        DroolsGroupingUniConstraintStream<Solution_, GroupKey_> stream = new DroolsGroupingUniConstraintStream<>(
                constraintFactory, this, groupKeyMapping);
        addChildStream(stream);
        return stream;
    }

    @Override
    public <GroupKey_, __, Result_> BiConstraintStream<GroupKey_, Result_> groupBy(
            BiFunction<A, B, GroupKey_> groupKeyMapping,
            BiConstraintCollector<A, B, __, Result_> collector) {
        DroolsGroupingBiConstraintStream<Solution_, GroupKey_, Result_> stream = new DroolsGroupingBiConstraintStream<>(
                constraintFactory, this, groupKeyMapping, collector);
        addChildStream(stream);
        return stream;
    }

    @Override
    public <GroupKey_, ResultContainerB_, ResultB_, ResultContainerC_, ResultC_>
            TriConstraintStream<GroupKey_, ResultB_, ResultC_> groupBy(BiFunction<A, B, GroupKey_> groupKeyMapping,
                    BiConstraintCollector<A, B, ResultContainerB_, ResultB_> collectorB,
                    BiConstraintCollector<A, B, ResultContainerC_, ResultC_> collectorC) {
        DroolsGroupingTriConstraintStream<Solution_, GroupKey_, ResultB_, ResultC_> stream =
                new DroolsGroupingTriConstraintStream<>(constraintFactory, this, groupKeyMapping, collectorB, collectorC);
        addChildStream(stream);
        return stream;
    }

    @Override
    public <GroupKey_, ResultContainerB_, ResultB_, ResultContainerC_, ResultC_, ResultContainerD_, ResultD_>
            QuadConstraintStream<GroupKey_, ResultB_, ResultC_, ResultD_> groupBy(BiFunction<A, B, GroupKey_> groupKeyMapping,
                    BiConstraintCollector<A, B, ResultContainerB_, ResultB_> collectorB,
                    BiConstraintCollector<A, B, ResultContainerC_, ResultC_> collectorC,
                    BiConstraintCollector<A, B, ResultContainerD_, ResultD_> collectorD) {
        DroolsGroupingQuadConstraintStream<Solution_, GroupKey_, ResultB_, ResultC_, ResultD_> stream =
                new DroolsGroupingQuadConstraintStream<>(constraintFactory, this, groupKeyMapping, collectorB, collectorC,
                        collectorD);
        addChildStream(stream);
        return stream;
    }

    @Override
    public <GroupKeyA_, GroupKeyB_> BiConstraintStream<GroupKeyA_, GroupKeyB_> groupBy(
            BiFunction<A, B, GroupKeyA_> groupKeyAMapping, BiFunction<A, B, GroupKeyB_> groupKeyBMapping) {
        DroolsGroupingBiConstraintStream<Solution_, GroupKeyA_, GroupKeyB_> stream = new DroolsGroupingBiConstraintStream<>(
                constraintFactory, this, groupKeyAMapping, groupKeyBMapping);
        addChildStream(stream);
        return stream;
    }

    @Override
    public <GroupKeyA_, GroupKeyB_, ResultContainer_, Result_> TriConstraintStream<GroupKeyA_, GroupKeyB_, Result_> groupBy(
            BiFunction<A, B, GroupKeyA_> groupKeyAMapping, BiFunction<A, B, GroupKeyB_> groupKeyBMapping,
            BiConstraintCollector<A, B, ResultContainer_, Result_> collector) {
        DroolsGroupingTriConstraintStream<Solution_, GroupKeyA_, GroupKeyB_, Result_> stream =
                new DroolsGroupingTriConstraintStream<>(constraintFactory, this, groupKeyAMapping, groupKeyBMapping, collector);
        addChildStream(stream);
        return stream;
    }

    @Override
    public <GroupKeyA_, GroupKeyB_, ResultContainerC_, ResultC_, ResultContainerD_, ResultD_>
            QuadConstraintStream<GroupKeyA_, GroupKeyB_, ResultC_, ResultD_> groupBy(
                    BiFunction<A, B, GroupKeyA_> groupKeyAMapping, BiFunction<A, B, GroupKeyB_> groupKeyBMapping,
                    BiConstraintCollector<A, B, ResultContainerC_, ResultC_> collectorC,
                    BiConstraintCollector<A, B, ResultContainerD_, ResultD_> collectorD) {
        DroolsGroupingQuadConstraintStream<Solution_, GroupKeyA_, GroupKeyB_, ResultC_, ResultD_> stream =
                new DroolsGroupingQuadConstraintStream<>(constraintFactory, this, groupKeyAMapping, groupKeyBMapping,
                        collectorC, collectorD);
        addChildStream(stream);
        return stream;
    }

    @Override
    public <GroupKeyA_, GroupKeyB_, GroupKeyC_> TriConstraintStream<GroupKeyA_, GroupKeyB_, GroupKeyC_> groupBy(
            BiFunction<A, B, GroupKeyA_> groupKeyAMapping, BiFunction<A, B, GroupKeyB_> groupKeyBMapping,
            BiFunction<A, B, GroupKeyC_> groupKeyCMapping) {
        DroolsGroupingTriConstraintStream<Solution_, GroupKeyA_, GroupKeyB_, GroupKeyC_> stream =
                new DroolsGroupingTriConstraintStream<>(constraintFactory, this, groupKeyAMapping, groupKeyBMapping,
                        groupKeyCMapping);
        addChildStream(stream);
        return stream;
    }

    @Override
    public <GroupKeyA_, GroupKeyB_, GroupKeyC_, ResultContainerD_, ResultD_>
            QuadConstraintStream<GroupKeyA_, GroupKeyB_, GroupKeyC_, ResultD_>
            groupBy(BiFunction<A, B, GroupKeyA_> groupKeyAMapping, BiFunction<A, B, GroupKeyB_> groupKeyBMapping,
                    BiFunction<A, B, GroupKeyC_> groupKeyCMapping,
                    BiConstraintCollector<A, B, ResultContainerD_, ResultD_> collectorD) {
        DroolsGroupingQuadConstraintStream<Solution_, GroupKeyA_, GroupKeyB_, GroupKeyC_, ResultD_> stream =
                new DroolsGroupingQuadConstraintStream<>(constraintFactory, this, groupKeyAMapping, groupKeyBMapping,
                        groupKeyCMapping, collectorD);
        addChildStream(stream);
        return stream;
    }

    @Override
    public <GroupKeyA_, GroupKeyB_, GroupKeyC_, GroupKeyD_> QuadConstraintStream<GroupKeyA_, GroupKeyB_, GroupKeyC_, GroupKeyD_>
            groupBy(BiFunction<A, B, GroupKeyA_> groupKeyAMapping, BiFunction<A, B, GroupKeyB_> groupKeyBMapping,
                    BiFunction<A, B, GroupKeyC_> groupKeyCMapping, BiFunction<A, B, GroupKeyD_> groupKeyDMapping) {
        DroolsGroupingQuadConstraintStream<Solution_, GroupKeyA_, GroupKeyB_, GroupKeyC_, GroupKeyD_> stream =
                new DroolsGroupingQuadConstraintStream<>(constraintFactory, this, groupKeyAMapping, groupKeyBMapping,
                        groupKeyCMapping, groupKeyDMapping);
        addChildStream(stream);
        return stream;
    }

    @Override
    public <ResultA_> UniConstraintStream<ResultA_> map(BiFunction<A, B, ResultA_> mapping) {
        DroolsMappingUniConstraintStream<Solution_, ResultA_> stream =
                new DroolsMappingUniConstraintStream<>(constraintFactory, this, Objects.requireNonNull(mapping));
        addChildStream(stream);
        return stream;
    }

    @Override
    public <ResultB_> BiConstraintStream<A, ResultB_> flattenLast(Function<B, Iterable<ResultB_>> mapping) {
        DroolsFlatteningBiConstraintStream<Solution_, A, ResultB_> stream =
                new DroolsFlatteningBiConstraintStream<>(constraintFactory, this, Objects.requireNonNull(mapping));
        addChildStream(stream);
        return stream;
    }

    @Override
    public <Score_ extends Score<Score_>> BiConstraintBuilder<A, B, Score_> innerImpact(Score_ constraintWeight,
            ToIntBiFunction<A, B> matchWeigher, ScoreImpactType scoreImpactType) {
        RuleBuilder<Solution_> ruleBuilder = createLeftHandSide().andTerminate(matchWeigher);
        return newTerminator(ruleBuilder, constraintWeight, scoreImpactType);
    }

    private <Score_ extends Score<Score_>> BiConstraintBuilderImpl<A, B, Score_> newTerminator(
            RuleBuilder<Solution_> ruleBuilder, Score_ constraintWeight, ScoreImpactType impactType) {
        return new BiConstraintBuilderImpl<>(
                (constraintPackage, constraintName, constraintWeight_, impactType_, justificationMapping,
                        indictedObjectsMapping) -> buildConstraint(constraintPackage, constraintName, constraintWeight_,
                                impactType_, justificationMapping, indictedObjectsMapping, ruleBuilder),
                impactType, constraintWeight);
    }

    @Override
    public <Score_ extends Score<Score_>> BiConstraintBuilder<A, B, Score_> innerImpact(Score_ constraintWeight,
            ToLongBiFunction<A, B> matchWeigher, ScoreImpactType scoreImpactType) {
        RuleBuilder<Solution_> ruleBuilder = createLeftHandSide().andTerminate(matchWeigher);
        return newTerminator(ruleBuilder, constraintWeight, scoreImpactType);
    }

    @Override
    public <Score_ extends Score<Score_>> BiConstraintBuilder<A, B, Score_> innerImpact(Score_ constraintWeight,
            BiFunction<A, B, BigDecimal> matchWeigher, ScoreImpactType scoreImpactType) {
        RuleBuilder<Solution_> ruleBuilder = createLeftHandSide().andTerminate(matchWeigher);
        return newTerminator(ruleBuilder, constraintWeight, scoreImpactType);
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
