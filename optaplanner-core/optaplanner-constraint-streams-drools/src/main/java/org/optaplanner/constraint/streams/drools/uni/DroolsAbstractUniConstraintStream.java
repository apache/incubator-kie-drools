package org.optaplanner.constraint.streams.drools.uni;

import static org.optaplanner.constraint.streams.common.RetrievalSemantics.STANDARD;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.ToIntFunction;
import java.util.function.ToLongFunction;

import org.optaplanner.constraint.streams.common.RetrievalSemantics;
import org.optaplanner.constraint.streams.common.ScoreImpactType;
import org.optaplanner.constraint.streams.common.bi.BiJoinerComber;
import org.optaplanner.constraint.streams.common.uni.InnerUniConstraintStream;
import org.optaplanner.constraint.streams.drools.DroolsConstraintFactory;
import org.optaplanner.constraint.streams.drools.bi.DroolsAbstractBiConstraintStream;
import org.optaplanner.constraint.streams.drools.bi.DroolsGroupingBiConstraintStream;
import org.optaplanner.constraint.streams.drools.bi.DroolsJoinBiConstraintStream;
import org.optaplanner.constraint.streams.drools.common.DroolsAbstractConstraintStream;
import org.optaplanner.constraint.streams.drools.common.RuleBuilder;
import org.optaplanner.constraint.streams.drools.common.UniLeftHandSide;
import org.optaplanner.constraint.streams.drools.quad.DroolsGroupingQuadConstraintStream;
import org.optaplanner.constraint.streams.drools.tri.DroolsGroupingTriConstraintStream;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.stream.Constraint;
import org.optaplanner.core.api.score.stream.bi.BiConstraintStream;
import org.optaplanner.core.api.score.stream.bi.BiJoiner;
import org.optaplanner.core.api.score.stream.quad.QuadConstraintStream;
import org.optaplanner.core.api.score.stream.tri.TriConstraintStream;
import org.optaplanner.core.api.score.stream.uni.UniConstraintCollector;
import org.optaplanner.core.api.score.stream.uni.UniConstraintStream;

public abstract class DroolsAbstractUniConstraintStream<Solution_, A> extends DroolsAbstractConstraintStream<Solution_>
        implements InnerUniConstraintStream<A> {

    public DroolsAbstractUniConstraintStream(DroolsConstraintFactory<Solution_> constraintFactory,
            RetrievalSemantics retrievalSemantics) {
        super(constraintFactory, retrievalSemantics);
    }

    @Override
    public UniConstraintStream<A> filter(Predicate<A> predicate) {
        DroolsFilterUniConstraintStream<Solution_, A> stream = new DroolsFilterUniConstraintStream<>(constraintFactory,
                this, predicate);
        addChildStream(stream);
        return stream;
    }

    @Override
    @SafeVarargs
    public final <B> BiConstraintStream<A, B> join(UniConstraintStream<B> otherStream,
            BiJoiner<A, B>... joiners) {
        BiJoinerComber<A, B> joinerComber = BiJoinerComber.comb(joiners);
        return join(otherStream, joinerComber);
    }

    @Override
    public final <B> BiConstraintStream<A, B> join(UniConstraintStream<B> otherStream,
            BiJoinerComber<A, B> joinerComber) {
        DroolsAbstractUniConstraintStream<Solution_, B> castOtherStream =
                (DroolsAbstractUniConstraintStream<Solution_, B>) otherStream;
        DroolsAbstractBiConstraintStream<Solution_, A, B> stream = new DroolsJoinBiConstraintStream<>(constraintFactory,
                this, castOtherStream, joinerComber.getMergedJoiner());
        addChildStream(stream);
        castOtherStream.addChildStream(stream);
        if (joinerComber.getMergedFiltering() == null) {
            return stream;
        } else {
            return stream.filter(joinerComber.getMergedFiltering());
        }
    }

    @SafeVarargs
    @Override
    public final <B> UniConstraintStream<A> ifExists(Class<B> otherClass, BiJoiner<A, B>... joiners) {
        return ifExistsOrNot(true, getRetrievalSemantics() != STANDARD, otherClass, joiners);
    }

    @SafeVarargs
    @Override
    public final <B> UniConstraintStream<A> ifExistsIncludingNullVars(Class<B> otherClass, BiJoiner<A, B>... joiners) {
        return ifExistsOrNot(true, true, otherClass, joiners);
    }

    @SafeVarargs
    @Override
    public final <B> UniConstraintStream<A> ifNotExists(Class<B> otherClass, BiJoiner<A, B>... joiners) {
        return ifExistsOrNot(false, getRetrievalSemantics() != STANDARD,
                otherClass, joiners);
    }

    @SafeVarargs
    @Override
    public final <B> UniConstraintStream<A> ifNotExistsIncludingNullVars(Class<B> otherClass, BiJoiner<A, B>... joiners) {
        return ifExistsOrNot(false, true, otherClass, joiners);
    }

    @SafeVarargs
    private <B> UniConstraintStream<A> ifExistsOrNot(boolean shouldExist, boolean shouldIncludeNullVars,
            Class<B> otherClass, BiJoiner<A, B>... joiners) {
        getConstraintFactory().assertValidFromType(otherClass);
        DroolsExistsUniConstraintStream<Solution_, A> stream = new DroolsExistsUniConstraintStream<>(constraintFactory, this,
                shouldExist, shouldIncludeNullVars, otherClass, joiners);
        addChildStream(stream);
        return stream;
    }

    @Override
    public <ResultContainer_, Result_> UniConstraintStream<Result_> groupBy(
            UniConstraintCollector<A, ResultContainer_, Result_> collector) {
        DroolsGroupingUniConstraintStream<Solution_, Result_> stream = new DroolsGroupingUniConstraintStream<>(
                constraintFactory, this, collector);
        addChildStream(stream);
        return stream;
    }

    @Override
    public <ResultContainerA_, ResultA_, ResultContainerB_, ResultB_> BiConstraintStream<ResultA_, ResultB_> groupBy(
            UniConstraintCollector<A, ResultContainerA_, ResultA_> collectorA,
            UniConstraintCollector<A, ResultContainerB_, ResultB_> collectorB) {
        DroolsGroupingBiConstraintStream<Solution_, ResultA_, ResultB_> stream =
                new DroolsGroupingBiConstraintStream<>(constraintFactory, this, collectorA, collectorB);
        addChildStream(stream);
        return stream;
    }

    @Override
    public <ResultContainerA_, ResultA_, ResultContainerB_, ResultB_, ResultContainerC_, ResultC_>
            TriConstraintStream<ResultA_, ResultB_, ResultC_>
            groupBy(UniConstraintCollector<A, ResultContainerA_, ResultA_> collectorA,
                    UniConstraintCollector<A, ResultContainerB_, ResultB_> collectorB,
                    UniConstraintCollector<A, ResultContainerC_, ResultC_> collectorC) {
        DroolsGroupingTriConstraintStream<Solution_, ResultA_, ResultB_, ResultC_> stream =
                new DroolsGroupingTriConstraintStream<>(constraintFactory, this, collectorA, collectorB, collectorC);
        addChildStream(stream);
        return stream;
    }

    @Override
    public <ResultContainerA_, ResultA_, ResultContainerB_, ResultB_, ResultContainerC_, ResultC_, ResultContainerD_, ResultD_>
            QuadConstraintStream<ResultA_, ResultB_, ResultC_, ResultD_>
            groupBy(UniConstraintCollector<A, ResultContainerA_, ResultA_> collectorA,
                    UniConstraintCollector<A, ResultContainerB_, ResultB_> collectorB,
                    UniConstraintCollector<A, ResultContainerC_, ResultC_> collectorC,
                    UniConstraintCollector<A, ResultContainerD_, ResultD_> collectorD) {
        DroolsGroupingQuadConstraintStream<Solution_, ResultA_, ResultB_, ResultC_, ResultD_> stream =
                new DroolsGroupingQuadConstraintStream<>(constraintFactory, this, collectorA, collectorB, collectorC,
                        collectorD);
        addChildStream(stream);
        return stream;
    }

    @Override
    public <GroupKey_> UniConstraintStream<GroupKey_> groupBy(Function<A, GroupKey_> groupKeyMapping) {
        DroolsGroupingUniConstraintStream<Solution_, GroupKey_> stream = new DroolsGroupingUniConstraintStream<>(
                constraintFactory, this, groupKeyMapping);
        addChildStream(stream);
        return stream;
    }

    @Override
    public <GroupKey_, ResultContainer_, Result_> BiConstraintStream<GroupKey_, Result_> groupBy(
            Function<A, GroupKey_> groupKeyMapping, UniConstraintCollector<A, ResultContainer_, Result_> collector) {
        DroolsGroupingBiConstraintStream<Solution_, GroupKey_, Result_> stream = new DroolsGroupingBiConstraintStream<>(
                constraintFactory, this, groupKeyMapping, collector);
        addChildStream(stream);
        return stream;
    }

    @Override
    public <GroupKey_, ResultContainerB_, ResultB_, ResultContainerC_, ResultC_>
            TriConstraintStream<GroupKey_, ResultB_, ResultC_> groupBy(Function<A, GroupKey_> groupKeyMapping,
                    UniConstraintCollector<A, ResultContainerB_, ResultB_> collectorB,
                    UniConstraintCollector<A, ResultContainerC_, ResultC_> collectorC) {
        DroolsGroupingTriConstraintStream<Solution_, GroupKey_, ResultB_, ResultC_> stream =
                new DroolsGroupingTriConstraintStream<>(constraintFactory, this, groupKeyMapping, collectorB, collectorC);
        addChildStream(stream);
        return stream;
    }

    @Override
    public <GroupKey_, ResultContainerB_, ResultB_, ResultContainerC_, ResultC_, ResultContainerD_, ResultD_>
            QuadConstraintStream<GroupKey_, ResultB_, ResultC_, ResultD_>
            groupBy(Function<A, GroupKey_> groupKeyMapping, UniConstraintCollector<A, ResultContainerB_, ResultB_> collectorB,
                    UniConstraintCollector<A, ResultContainerC_, ResultC_> collectorC,
                    UniConstraintCollector<A, ResultContainerD_, ResultD_> collectorD) {
        DroolsGroupingQuadConstraintStream<Solution_, GroupKey_, ResultB_, ResultC_, ResultD_> stream =
                new DroolsGroupingQuadConstraintStream<>(constraintFactory, this, groupKeyMapping, collectorB, collectorC,
                        collectorD);
        addChildStream(stream);
        return stream;
    }

    @Override
    public <GroupKeyA_, GroupKeyB_> BiConstraintStream<GroupKeyA_, GroupKeyB_> groupBy(
            Function<A, GroupKeyA_> groupKeyAMapping, Function<A, GroupKeyB_> groupKeyBMapping) {
        DroolsGroupingBiConstraintStream<Solution_, GroupKeyA_, GroupKeyB_> stream = new DroolsGroupingBiConstraintStream<>(
                constraintFactory, this, groupKeyAMapping,
                groupKeyBMapping);
        addChildStream(stream);
        return stream;
    }

    @Override
    public <GroupKeyA_, GroupKeyB_, ResultContainer_, Result_> TriConstraintStream<GroupKeyA_, GroupKeyB_, Result_> groupBy(
            Function<A, GroupKeyA_> groupKeyAMapping,
            Function<A, GroupKeyB_> groupKeyBMapping, UniConstraintCollector<A, ResultContainer_, Result_> collector) {
        DroolsGroupingTriConstraintStream<Solution_, GroupKeyA_, GroupKeyB_, Result_> stream =
                new DroolsGroupingTriConstraintStream<>(constraintFactory, this, groupKeyAMapping, groupKeyBMapping, collector);
        addChildStream(stream);
        return stream;
    }

    @Override
    public <GroupKeyA_, GroupKeyB_, ResultContainerC_, ResultC_, ResultContainerD_, ResultD_>
            QuadConstraintStream<GroupKeyA_, GroupKeyB_, ResultC_, ResultD_> groupBy(
                    Function<A, GroupKeyA_> groupKeyAMapping,
                    Function<A, GroupKeyB_> groupKeyBMapping, UniConstraintCollector<A, ResultContainerC_, ResultC_> collectorC,
                    UniConstraintCollector<A, ResultContainerD_, ResultD_> collectorD) {
        DroolsGroupingQuadConstraintStream<Solution_, GroupKeyA_, GroupKeyB_, ResultC_, ResultD_> stream =
                new DroolsGroupingQuadConstraintStream<>(constraintFactory, this, groupKeyAMapping, groupKeyBMapping,
                        collectorC, collectorD);
        addChildStream(stream);
        return stream;
    }

    @Override
    public <GroupKeyA_, GroupKeyB_, GroupKeyC_> TriConstraintStream<GroupKeyA_, GroupKeyB_, GroupKeyC_> groupBy(
            Function<A, GroupKeyA_> groupKeyAMapping, Function<A, GroupKeyB_> groupKeyBMapping,
            Function<A, GroupKeyC_> groupKeyCMapping) {
        DroolsGroupingTriConstraintStream<Solution_, GroupKeyA_, GroupKeyB_, GroupKeyC_> stream =
                new DroolsGroupingTriConstraintStream<>(constraintFactory, this, groupKeyAMapping, groupKeyBMapping,
                        groupKeyCMapping);
        addChildStream(stream);
        return stream;
    }

    @Override
    public <GroupKeyA_, GroupKeyB_, GroupKeyC_, ResultContainerD_, ResultD_>
            QuadConstraintStream<GroupKeyA_, GroupKeyB_, GroupKeyC_, ResultD_> groupBy(Function<A, GroupKeyA_> groupKeyAMapping,
                    Function<A, GroupKeyB_> groupKeyBMapping, Function<A, GroupKeyC_> groupKeyCMapping,
                    UniConstraintCollector<A, ResultContainerD_, ResultD_> collectorD) {
        DroolsGroupingQuadConstraintStream<Solution_, GroupKeyA_, GroupKeyB_, GroupKeyC_, ResultD_> stream =
                new DroolsGroupingQuadConstraintStream<>(constraintFactory, this, groupKeyAMapping, groupKeyBMapping,
                        groupKeyCMapping, collectorD);
        addChildStream(stream);
        return stream;
    }

    @Override
    public <GroupKeyA_, GroupKeyB_, GroupKeyC_, GroupKeyD_> QuadConstraintStream<GroupKeyA_, GroupKeyB_, GroupKeyC_, GroupKeyD_>
            groupBy(Function<A, GroupKeyA_> groupKeyAMapping, Function<A, GroupKeyB_> groupKeyBMapping,
                    Function<A, GroupKeyC_> groupKeyCMapping, Function<A, GroupKeyD_> groupKeyDMapping) {
        DroolsGroupingQuadConstraintStream<Solution_, GroupKeyA_, GroupKeyB_, GroupKeyC_, GroupKeyD_> stream =
                new DroolsGroupingQuadConstraintStream<>(constraintFactory, this, groupKeyAMapping, groupKeyBMapping,
                        groupKeyCMapping, groupKeyDMapping);
        addChildStream(stream);
        return stream;
    }

    @Override
    public <ResultA_> UniConstraintStream<ResultA_> map(Function<A, ResultA_> mapping) {
        DroolsMappingUniConstraintStream<Solution_, ResultA_> stream =
                new DroolsMappingUniConstraintStream<>(constraintFactory, this, Objects.requireNonNull(mapping));
        addChildStream(stream);
        return stream;
    }

    @Override
    public <ResultA_> UniConstraintStream<ResultA_> flattenLast(Function<A, Iterable<ResultA_>> mapping) {
        DroolsFlatteningUniConstraintStream<Solution_, ResultA_> stream =
                new DroolsFlatteningUniConstraintStream<>(constraintFactory, this, Objects.requireNonNull(mapping));
        addChildStream(stream);
        return stream;
    }

    @Override
    public final Constraint impactScore(String constraintPackage, String constraintName, Score<?> constraintWeight,
            ScoreImpactType impactType) {
        RuleBuilder<Solution_> ruleBuilder = getLeftHandSide().andTerminate();
        return buildConstraint(constraintPackage, constraintName, constraintWeight, impactType, ruleBuilder);
    }

    @Override
    public final Constraint impactScore(String constraintPackage, String constraintName, Score<?> constraintWeight,
            ToIntFunction<A> matchWeigher, ScoreImpactType impactType) {
        RuleBuilder<Solution_> ruleBuilder = getLeftHandSide().andTerminate(matchWeigher);
        return buildConstraint(constraintPackage, constraintName, constraintWeight, impactType, ruleBuilder);
    }

    @Override
    public final Constraint impactScoreLong(String constraintPackage, String constraintName,
            Score<?> constraintWeight, ToLongFunction<A> matchWeigher, ScoreImpactType impactType) {
        RuleBuilder<Solution_> ruleBuilder = getLeftHandSide().andTerminate(matchWeigher);
        return buildConstraint(constraintPackage, constraintName, constraintWeight, impactType, ruleBuilder);
    }

    @Override
    public final Constraint impactScoreBigDecimal(String constraintPackage, String constraintName,
            Score<?> constraintWeight, Function<A, BigDecimal> matchWeigher, ScoreImpactType impactType) {
        RuleBuilder<Solution_> ruleBuilder = getLeftHandSide().andTerminate(matchWeigher);
        return buildConstraint(constraintPackage, constraintName, constraintWeight, impactType, ruleBuilder);
    }

    @Override
    public final Constraint impactScoreConfigurable(String constraintPackage, String constraintName,
            ScoreImpactType impactType) {
        RuleBuilder<Solution_> ruleBuilder = getLeftHandSide().andTerminate();
        return buildConstraintConfigurable(constraintPackage, constraintName, impactType, ruleBuilder);
    }

    @Override
    public final Constraint impactScoreConfigurable(String constraintPackage, String constraintName,
            ToIntFunction<A> matchWeigher, ScoreImpactType impactType) {
        RuleBuilder<Solution_> ruleBuilder = getLeftHandSide().andTerminate(matchWeigher);
        return buildConstraintConfigurable(constraintPackage, constraintName, impactType, ruleBuilder);
    }

    @Override
    public final Constraint impactScoreConfigurableLong(String constraintPackage, String constraintName,
            ToLongFunction<A> matchWeigher, ScoreImpactType impactType) {
        RuleBuilder<Solution_> ruleBuilder = getLeftHandSide().andTerminate(matchWeigher);
        return buildConstraintConfigurable(constraintPackage, constraintName, impactType, ruleBuilder);
    }

    @Override
    public final Constraint impactScoreConfigurableBigDecimal(String constraintPackage, String constraintName,
            Function<A, BigDecimal> matchWeigher, ScoreImpactType impactType) {
        RuleBuilder<Solution_> ruleBuilder = getLeftHandSide().andTerminate(matchWeigher);
        return buildConstraintConfigurable(constraintPackage, constraintName, impactType, ruleBuilder);
    }

    public abstract UniLeftHandSide<A> getLeftHandSide();

}
