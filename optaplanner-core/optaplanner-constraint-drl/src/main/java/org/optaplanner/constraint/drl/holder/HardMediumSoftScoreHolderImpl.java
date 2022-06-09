package org.optaplanner.constraint.drl.holder;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

import org.kie.api.definition.rule.Rule;
import org.kie.api.runtime.rule.RuleContext;
import org.optaplanner.core.api.domain.constraintweight.ConstraintConfiguration;
import org.optaplanner.core.api.domain.constraintweight.ConstraintWeight;
import org.optaplanner.core.api.score.buildin.hardmediumsoft.HardMediumSoftScore;
import org.optaplanner.core.api.score.buildin.hardmediumsoft.HardMediumSoftScoreHolder;

/**
 * @see HardMediumSoftScore
 */
public final class HardMediumSoftScoreHolderImpl extends AbstractScoreHolder<HardMediumSoftScore>
        implements HardMediumSoftScoreHolder {

    protected final Map<Rule, IntMatchExecutor> matchExecutorByNumberMap = new LinkedHashMap<>();
    /** Slower than {@link #matchExecutorByNumberMap} */
    protected final Map<Rule, ScoreMatchExecutor<HardMediumSoftScore>> matchExecutorByScoreMap = new LinkedHashMap<>();

    protected int hardScore;
    protected int mediumScore;
    protected int softScore;

    public HardMediumSoftScoreHolderImpl(boolean constraintMatchEnabled) {
        super(constraintMatchEnabled);
    }

    public int getHardScore() {
        return hardScore;
    }

    public int getMediumScore() {
        return mediumScore;
    }

    public int getSoftScore() {
        return softScore;
    }

    // ************************************************************************
    // Setup methods
    // ************************************************************************

    @Override
    public void configureConstraintWeight(Rule rule, HardMediumSoftScore constraintWeight) {
        super.configureConstraintWeight(rule, constraintWeight);
        IntMatchExecutor matchExecutor;
        if (constraintWeight.isZero()) {
            matchExecutor = (RuleContext kcontext, int weightMultiplier) -> {
            };
        } else if (constraintWeight.getMediumScore() == 0 && constraintWeight.getSoftScore() == 0) {
            matchExecutor =
                    (RuleContext kcontext, int weightMultiplier) -> addHardConstraintMatch(kcontext,
                            constraintWeight.getHardScore() * weightMultiplier);
        } else if (constraintWeight.getHardScore() == 0 && constraintWeight.getSoftScore() == 0) {
            matchExecutor =
                    (RuleContext kcontext, int weightMultiplier) -> addMediumConstraintMatch(kcontext,
                            constraintWeight.getMediumScore() * weightMultiplier);
        } else if (constraintWeight.getHardScore() == 0 && constraintWeight.getMediumScore() == 0) {
            matchExecutor =
                    (RuleContext kcontext, int weightMultiplier) -> addSoftConstraintMatch(kcontext,
                            constraintWeight.getSoftScore() * weightMultiplier);
        } else {
            matchExecutor =
                    (RuleContext kcontext, int weightMultiplier) -> addMultiConstraintMatch(kcontext,
                            constraintWeight.getHardScore() * weightMultiplier,
                            constraintWeight.getMediumScore() * weightMultiplier,
                            constraintWeight.getSoftScore() * weightMultiplier);
        }
        matchExecutorByNumberMap.put(rule, matchExecutor);
        matchExecutorByScoreMap.put(rule, (RuleContext kcontext,
                HardMediumSoftScore weightMultiplier) -> addMultiConstraintMatch(kcontext,
                        constraintWeight.getHardScore() * weightMultiplier.getHardScore(),
                        constraintWeight.getMediumScore() * weightMultiplier.getMediumScore(),
                        constraintWeight.getSoftScore() * weightMultiplier.getSoftScore()));
    }

    // ************************************************************************
    // Penalize and reward methods
    // ************************************************************************

    @Override
    public void penalize(RuleContext kcontext) {
        impactScore(kcontext, -1);
    }

    @Override
    public void penalize(RuleContext kcontext, int weightMultiplier) {
        impactScore(kcontext, -weightMultiplier);
    }

    @Override
    public void penalize(RuleContext kcontext, int hardWeightMultiplier, int mediumWeightMultiplier, int softWeightMultiplier) {
        impactScore(kcontext, -hardWeightMultiplier, -mediumWeightMultiplier, -softWeightMultiplier);
    }

    @Override
    public void reward(RuleContext kcontext) {
        impactScore(kcontext, 1);
    }

    @Override
    public void reward(RuleContext kcontext, int weightMultiplier) {
        impactScore(kcontext, weightMultiplier);
    }

    @Override
    public void reward(RuleContext kcontext, int hardWeightMultiplier, int mediumWeightMultiplier, int softWeightMultiplier) {
        impactScore(kcontext, hardWeightMultiplier, mediumWeightMultiplier, softWeightMultiplier);
    }

    @Override
    public void impactScore(RuleContext kcontext) {
        impactScore(kcontext, 1);
    }

    @Override
    public void impactScore(RuleContext kcontext, int weightMultiplier) {
        Rule rule = kcontext.getRule();
        IntMatchExecutor matchExecutor = matchExecutorByNumberMap.get(rule);
        if (matchExecutor == null) {
            throw new IllegalStateException("The DRL rule (" + rule.getPackageName() + ":" + rule.getName()
                    + ") does not match a @" + ConstraintWeight.class.getSimpleName() + " on the @"
                    + ConstraintConfiguration.class.getSimpleName() + " annotated class.");
        }
        matchExecutor.accept(kcontext, weightMultiplier);
    }

    @Override
    public void impactScore(RuleContext kcontext, long weightMultiplier) {
        throw new UnsupportedOperationException("In the rule (" + kcontext.getRule().getName()
                + "), the scoreHolder class (" + getClass()
                + ") does not support a long weightMultiplier (" + weightMultiplier + ").\n"
                + "If you're using constraint streams, maybe switch from penalizeLong() to penalize().");
    }

    @Override
    public void impactScore(RuleContext kcontext, BigDecimal weightMultiplier) {
        throw new UnsupportedOperationException("In the rule (" + kcontext.getRule().getName()
                + "), the scoreHolder class (" + getClass()
                + ") does not support a BigDecimal weightMultiplier (" + weightMultiplier + ").\n"
                + "If you're using constraint streams, maybe switch from penalizeBigDecimal() to penalize().");
    }

    private void impactScore(RuleContext kcontext, int hardWeightMultiplier, int mediumWeightMultiplier,
            int softWeightMultiplier) {
        Rule rule = kcontext.getRule();
        ScoreMatchExecutor<HardMediumSoftScore> matchExecutor = matchExecutorByScoreMap.get(rule);
        if (matchExecutor == null) {
            throw new IllegalStateException("The DRL rule (" + rule.getPackageName() + ":" + rule.getName()
                    + ") does not match a @" + ConstraintWeight.class.getSimpleName() + " on the @"
                    + ConstraintConfiguration.class.getSimpleName() + " annotated class.");
        }
        matchExecutor.accept(kcontext,
                HardMediumSoftScore.of(hardWeightMultiplier, mediumWeightMultiplier, softWeightMultiplier));
    }

    // ************************************************************************
    // Other match methods
    // ************************************************************************

    @Override
    public void addHardConstraintMatch(RuleContext kcontext, int hardWeight) {
        hardScore += hardWeight;
        registerConstraintMatch(kcontext, () -> hardScore -= hardWeight, () -> HardMediumSoftScore.ofHard(hardWeight));
    }

    @Override
    public void addMediumConstraintMatch(RuleContext kcontext, int mediumWeight) {
        mediumScore += mediumWeight;
        registerConstraintMatch(kcontext, () -> mediumScore -= mediumWeight, () -> HardMediumSoftScore.ofMedium(mediumWeight));
    }

    @Override
    public void addSoftConstraintMatch(RuleContext kcontext, int softWeight) {
        softScore += softWeight;
        registerConstraintMatch(kcontext, () -> softScore -= softWeight, () -> HardMediumSoftScore.ofSoft(softWeight));
    }

    @Override
    public void addMultiConstraintMatch(RuleContext kcontext, int hardWeight, int mediumWeight, int softWeight) {
        hardScore += hardWeight;
        mediumScore += mediumWeight;
        softScore += softWeight;
        registerConstraintMatch(kcontext,
                () -> {
                    hardScore -= hardWeight;
                    mediumScore -= mediumWeight;
                    softScore -= softWeight;
                },
                () -> HardMediumSoftScore.of(hardWeight, mediumWeight, softWeight));
    }

    @Override
    public HardMediumSoftScore extractScore(int initScore) {
        return HardMediumSoftScore.ofUninitialized(initScore, hardScore, mediumScore, softScore);
    }

}
