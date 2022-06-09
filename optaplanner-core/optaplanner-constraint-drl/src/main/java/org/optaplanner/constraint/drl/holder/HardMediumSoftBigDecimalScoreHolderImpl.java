package org.optaplanner.constraint.drl.holder;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

import org.kie.api.definition.rule.Rule;
import org.kie.api.runtime.rule.RuleContext;
import org.optaplanner.core.api.domain.constraintweight.ConstraintConfiguration;
import org.optaplanner.core.api.domain.constraintweight.ConstraintWeight;
import org.optaplanner.core.api.score.buildin.hardmediumsoftbigdecimal.HardMediumSoftBigDecimalScore;
import org.optaplanner.core.api.score.buildin.hardmediumsoftbigdecimal.HardMediumSoftBigDecimalScoreHolder;

/**
 * @see HardMediumSoftBigDecimalScore
 */
public final class HardMediumSoftBigDecimalScoreHolderImpl extends AbstractScoreHolder<HardMediumSoftBigDecimalScore>
        implements HardMediumSoftBigDecimalScoreHolder {

    protected final Map<Rule, BigDecimalMatchExecutor> matchExecutorByNumberMap = new LinkedHashMap<>();
    /** Slower than {@link #matchExecutorByNumberMap} */
    protected final Map<Rule, ScoreMatchExecutor<HardMediumSoftBigDecimalScore>> matchExecutorByScoreMap =
            new LinkedHashMap<>();

    protected BigDecimal hardScore = BigDecimal.ZERO;
    protected BigDecimal mediumScore = BigDecimal.ZERO;
    protected BigDecimal softScore = BigDecimal.ZERO;

    public HardMediumSoftBigDecimalScoreHolderImpl(boolean constraintMatchEnabled) {
        super(constraintMatchEnabled);
    }

    public BigDecimal getHardScore() {
        return hardScore;
    }

    public BigDecimal getMediumScore() {
        return mediumScore;
    }

    public BigDecimal getSoftScore() {
        return softScore;
    }

    // ************************************************************************
    // Setup methods
    // ************************************************************************

    @Override
    public void configureConstraintWeight(Rule rule, HardMediumSoftBigDecimalScore constraintWeight) {
        super.configureConstraintWeight(rule, constraintWeight);
        BigDecimalMatchExecutor matchExecutor;
        if (constraintWeight.isZero()) {
            matchExecutor = (RuleContext kcontext, BigDecimal matchWeight) -> {
            };
        } else if (constraintWeight.getMediumScore().equals(BigDecimal.ZERO)
                && constraintWeight.getSoftScore().equals(BigDecimal.ZERO)) {
            matchExecutor =
                    (RuleContext kcontext, BigDecimal matchWeight) -> addHardConstraintMatch(kcontext,
                            constraintWeight.getHardScore().multiply(matchWeight));
        } else if (constraintWeight.getHardScore().equals(BigDecimal.ZERO)
                && constraintWeight.getSoftScore().equals(BigDecimal.ZERO)) {
            matchExecutor =
                    (RuleContext kcontext, BigDecimal matchWeight) -> addMediumConstraintMatch(
                            kcontext, constraintWeight.getMediumScore().multiply(matchWeight));
        } else if (constraintWeight.getHardScore().equals(BigDecimal.ZERO)
                && constraintWeight.getMediumScore().equals(BigDecimal.ZERO)) {
            matchExecutor =
                    (RuleContext kcontext, BigDecimal matchWeight) -> addSoftConstraintMatch(kcontext,
                            constraintWeight.getSoftScore().multiply(matchWeight));
        } else {
            matchExecutor = (RuleContext kcontext, BigDecimal matchWeight) -> addMultiConstraintMatch(
                    kcontext, constraintWeight.getHardScore().multiply(matchWeight),
                    constraintWeight.getMediumScore().multiply(matchWeight),
                    constraintWeight.getSoftScore().multiply(matchWeight));
        }
        matchExecutorByNumberMap.put(rule, matchExecutor);
        matchExecutorByScoreMap.put(rule, (RuleContext kcontext,
                HardMediumSoftBigDecimalScore weightMultiplier) -> addMultiConstraintMatch(kcontext,
                        constraintWeight.getHardScore().multiply(weightMultiplier.getHardScore()),
                        constraintWeight.getMediumScore().multiply(weightMultiplier.getMediumScore()),
                        constraintWeight.getSoftScore().multiply(weightMultiplier.getSoftScore())));
    }

    // ************************************************************************
    // Penalize and reward methods
    // ************************************************************************

    @Override
    public void penalize(RuleContext kcontext) {
        impactScore(kcontext, BigDecimal.ONE.negate());
    }

    @Override
    public void penalize(RuleContext kcontext, BigDecimal weightMultiplier) {
        impactScore(kcontext, weightMultiplier.negate());
    }

    @Override
    public void penalize(RuleContext kcontext, BigDecimal hardWeightMultiplier, BigDecimal mediumWeightMultiplier,
            BigDecimal softWeightMultiplier) {
        impactScore(kcontext, hardWeightMultiplier.negate(), mediumWeightMultiplier.negate(), softWeightMultiplier.negate());
    }

    @Override
    public void reward(RuleContext kcontext) {
        impactScore(kcontext, BigDecimal.ONE);
    }

    @Override
    public void reward(RuleContext kcontext, BigDecimal weightMultiplier) {
        impactScore(kcontext, weightMultiplier);
    }

    @Override
    public void reward(RuleContext kcontext, BigDecimal hardWeightMultiplier, BigDecimal mediumWeightMultiplier,
            BigDecimal softWeightMultiplier) {
        impactScore(kcontext, hardWeightMultiplier, mediumWeightMultiplier, softWeightMultiplier);
    }

    @Override
    public void impactScore(RuleContext kcontext) {
        impactScore(kcontext, BigDecimal.ONE);
    }

    @Override
    public void impactScore(RuleContext kcontext, int weightMultiplier) {
        impactScore(kcontext, BigDecimal.valueOf(weightMultiplier));
    }

    @Override
    public void impactScore(RuleContext kcontext, long weightMultiplier) {
        impactScore(kcontext, BigDecimal.valueOf(weightMultiplier));
    }

    @Override
    public void impactScore(RuleContext kcontext, BigDecimal weightMultiplier) {
        Rule rule = kcontext.getRule();
        BigDecimalMatchExecutor matchExecutor = matchExecutorByNumberMap.get(rule);
        if (matchExecutor == null) {
            throw new IllegalStateException("The DRL rule (" + rule.getPackageName() + ":" + rule.getName()
                    + ") does not match a @" + ConstraintWeight.class.getSimpleName() + " on the @"
                    + ConstraintConfiguration.class.getSimpleName() + " annotated class.");
        }
        matchExecutor.accept(kcontext, weightMultiplier);
    }

    private void impactScore(RuleContext kcontext, BigDecimal hardWeightMultiplier, BigDecimal mediumWeightMultiplier,
            BigDecimal softWeightMultiplier) {
        Rule rule = kcontext.getRule();
        ScoreMatchExecutor<HardMediumSoftBigDecimalScore> matchExecutor = matchExecutorByScoreMap.get(rule);
        if (matchExecutor == null) {
            throw new IllegalStateException("The DRL rule (" + rule.getPackageName() + ":" + rule.getName()
                    + ") does not match a @" + ConstraintWeight.class.getSimpleName() + " on the @"
                    + ConstraintConfiguration.class.getSimpleName() + " annotated class.");
        }
        matchExecutor.accept(kcontext,
                HardMediumSoftBigDecimalScore.of(hardWeightMultiplier, mediumWeightMultiplier, softWeightMultiplier));
    }

    // ************************************************************************
    // Other match methods
    // ************************************************************************

    @Override
    public void addHardConstraintMatch(RuleContext kcontext, BigDecimal hardWeight) {
        hardScore = hardScore.add(hardWeight);
        registerConstraintMatch(kcontext,
                () -> hardScore = hardScore.subtract(hardWeight),
                () -> HardMediumSoftBigDecimalScore.ofHard(hardWeight));
    }

    @Override
    public void addMediumConstraintMatch(RuleContext kcontext, BigDecimal mediumWeight) {
        mediumScore = mediumScore.add(mediumWeight);
        registerConstraintMatch(kcontext,
                () -> mediumScore = mediumScore.subtract(mediumWeight),
                () -> HardMediumSoftBigDecimalScore.ofMedium(mediumWeight));
    }

    @Override
    public void addSoftConstraintMatch(RuleContext kcontext, BigDecimal softWeight) {
        softScore = softScore.add(softWeight);
        registerConstraintMatch(kcontext,
                () -> softScore = softScore.subtract(softWeight),
                () -> HardMediumSoftBigDecimalScore.ofSoft(softWeight));
    }

    @Override
    public void addMultiConstraintMatch(RuleContext kcontext, BigDecimal hardWeight, BigDecimal mediumWeight,
            BigDecimal softWeight) {
        hardScore = hardScore.add(hardWeight);
        mediumScore = mediumScore.add(mediumWeight);
        softScore = softScore.add(softWeight);
        registerConstraintMatch(kcontext,
                () -> {
                    hardScore = hardScore.subtract(hardWeight);
                    mediumScore = mediumScore.subtract(mediumWeight);
                    softScore = softScore.subtract(softWeight);
                },
                () -> HardMediumSoftBigDecimalScore.of(hardWeight, mediumWeight, softWeight));
    }

    @Override
    public HardMediumSoftBigDecimalScore extractScore(int initScore) {
        return HardMediumSoftBigDecimalScore.ofUninitialized(initScore, hardScore, mediumScore, softScore);
    }

}
