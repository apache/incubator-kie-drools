package org.optaplanner.constraint.drl.holder;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

import org.kie.api.definition.rule.Rule;
import org.kie.api.runtime.rule.RuleContext;
import org.optaplanner.core.api.domain.constraintweight.ConstraintConfiguration;
import org.optaplanner.core.api.domain.constraintweight.ConstraintWeight;
import org.optaplanner.core.api.score.buildin.hardmediumsoft.HardMediumSoftScore;
import org.optaplanner.core.api.score.buildin.hardmediumsoftlong.HardMediumSoftLongScore;
import org.optaplanner.core.api.score.buildin.hardmediumsoftlong.HardMediumSoftLongScoreHolder;

/**
 * @see HardMediumSoftScore
 */
public final class HardMediumSoftLongScoreHolderImpl extends AbstractScoreHolder<HardMediumSoftLongScore>
        implements HardMediumSoftLongScoreHolder {

    protected final Map<Rule, LongMatchExecutor> matchExecutorByNumberMap = new LinkedHashMap<>();
    /** Slower than {@link #matchExecutorByNumberMap} */
    protected final Map<Rule, ScoreMatchExecutor<HardMediumSoftLongScore>> matchExecutorByScoreMap = new LinkedHashMap<>();

    protected long hardScore;
    protected long mediumScore;
    protected long softScore;

    public HardMediumSoftLongScoreHolderImpl(boolean constraintMatchEnabled) {
        super(constraintMatchEnabled);
    }

    public long getHardScore() {
        return hardScore;
    }

    public long getMediumScore() {
        return mediumScore;
    }

    public long getSoftScore() {
        return softScore;
    }

    // ************************************************************************
    // Setup methods
    // ************************************************************************

    @Override
    public void configureConstraintWeight(Rule rule, HardMediumSoftLongScore constraintWeight) {
        super.configureConstraintWeight(rule, constraintWeight);
        LongMatchExecutor matchExecutor;
        if (constraintWeight.isZero()) {
            matchExecutor = (RuleContext kcontext, long matchWeight) -> {
            };
        } else if (constraintWeight.getMediumScore() == 0 && constraintWeight.getSoftScore() == 0) {
            matchExecutor =
                    (RuleContext kcontext, long matchWeight) -> addHardConstraintMatch(kcontext,
                            constraintWeight.getHardScore() * matchWeight);
        } else if (constraintWeight.getHardScore() == 0 && constraintWeight.getSoftScore() == 0) {
            matchExecutor =
                    (RuleContext kcontext, long matchWeight) -> addMediumConstraintMatch(kcontext,
                            constraintWeight.getMediumScore() * matchWeight);
        } else if (constraintWeight.getHardScore() == 0 && constraintWeight.getMediumScore() == 0) {
            matchExecutor =
                    (RuleContext kcontext, long matchWeight) -> addSoftConstraintMatch(kcontext,
                            constraintWeight.getSoftScore() * matchWeight);
        } else {
            matchExecutor =
                    (RuleContext kcontext, long matchWeight) -> addMultiConstraintMatch(kcontext,
                            constraintWeight.getHardScore() * matchWeight, constraintWeight.getMediumScore() * matchWeight,
                            constraintWeight.getSoftScore() * matchWeight);
        }
        matchExecutorByNumberMap.put(rule, matchExecutor);
        matchExecutorByScoreMap.put(rule, (RuleContext kcontext,
                HardMediumSoftLongScore weightMultiplier) -> addMultiConstraintMatch(kcontext,
                        constraintWeight.getHardScore() * weightMultiplier.getHardScore(),
                        constraintWeight.getMediumScore() * weightMultiplier.getMediumScore(),
                        constraintWeight.getSoftScore() * weightMultiplier.getSoftScore()));
    }

    // ************************************************************************
    // Penalize and reward methods
    // ************************************************************************

    @Override
    public void penalize(RuleContext kcontext) {
        impactScore(kcontext, -1L);
    }

    @Override
    public void penalize(RuleContext kcontext, long weightMultiplier) {
        impactScore(kcontext, -weightMultiplier);
    }

    @Override
    public void penalize(RuleContext kcontext, long hardWeightMultiplier, long mediumWeightMultiplier,
            long softWeightMultiplier) {
        impactScore(kcontext, -hardWeightMultiplier, -mediumWeightMultiplier, -softWeightMultiplier);
    }

    @Override
    public void reward(RuleContext kcontext) {
        impactScore(kcontext, 1L);
    }

    @Override
    public void reward(RuleContext kcontext, long weightMultiplier) {
        impactScore(kcontext, weightMultiplier);
    }

    @Override
    public void reward(RuleContext kcontext, long hardWeightMultiplier, long mediumWeightMultiplier,
            long softWeightMultiplier) {
        impactScore(kcontext, hardWeightMultiplier, mediumWeightMultiplier, softWeightMultiplier);
    }

    @Override
    public void impactScore(RuleContext kcontext) {
        impactScore(kcontext, 1L);
    }

    @Override
    public void impactScore(RuleContext kcontext, int weightMultiplier) {
        impactScore(kcontext, (long) weightMultiplier);
    }

    @Override
    public void impactScore(RuleContext kcontext, long weightMultiplier) {
        Rule rule = kcontext.getRule();
        LongMatchExecutor matchExecutor = matchExecutorByNumberMap.get(rule);
        if (matchExecutor == null) {
            throw new IllegalStateException("The DRL rule (" + rule.getPackageName() + ":" + rule.getName()
                    + ") does not match a @" + ConstraintWeight.class.getSimpleName() + " on the @"
                    + ConstraintConfiguration.class.getSimpleName() + " annotated class.");
        }
        matchExecutor.accept(kcontext, weightMultiplier);
    }

    @Override
    public void impactScore(RuleContext kcontext, BigDecimal weightMultiplier) {
        throw new UnsupportedOperationException("In the rule (" + kcontext.getRule().getName()
                + "), the scoreHolder class (" + getClass()
                + ") does not support a BigDecimal weightMultiplier (" + weightMultiplier + ").\n"
                + "If you're using constraint streams, maybe switch from penalizeBigDecimal() to penalizeLong().");
    }

    private void impactScore(RuleContext kcontext, long hardWeightMultiplier, long mediumWeightMultiplier,
            long softWeightMultiplier) {
        Rule rule = kcontext.getRule();
        ScoreMatchExecutor<HardMediumSoftLongScore> matchExecutor = matchExecutorByScoreMap.get(rule);
        if (matchExecutor == null) {
            throw new IllegalStateException("The DRL rule (" + rule.getPackageName() + ":" + rule.getName()
                    + ") does not match a @" + ConstraintWeight.class.getSimpleName() + " on the @"
                    + ConstraintConfiguration.class.getSimpleName() + " annotated class.");
        }
        matchExecutor.accept(kcontext,
                HardMediumSoftLongScore.of(hardWeightMultiplier, mediumWeightMultiplier, softWeightMultiplier));
    }

    // ************************************************************************
    // Other match methods
    // ************************************************************************

    @Override
    public void addHardConstraintMatch(RuleContext kcontext, long hardWeight) {
        hardScore += hardWeight;
        registerConstraintMatch(kcontext, () -> hardScore -= hardWeight, () -> HardMediumSoftLongScore.ofHard(hardWeight));
    }

    @Override
    public void addMediumConstraintMatch(RuleContext kcontext, long mediumWeight) {
        mediumScore += mediumWeight;
        registerConstraintMatch(kcontext, () -> mediumScore -= mediumWeight,
                () -> HardMediumSoftLongScore.ofMedium(mediumWeight));
    }

    @Override
    public void addSoftConstraintMatch(RuleContext kcontext, long softWeight) {
        softScore += softWeight;
        registerConstraintMatch(kcontext, () -> softScore -= softWeight, () -> HardMediumSoftLongScore.ofSoft(softWeight));
    }

    @Override
    public void addMultiConstraintMatch(RuleContext kcontext, long hardWeight, long mediumWeight, long softWeight) {
        hardScore += hardWeight;
        mediumScore += mediumWeight;
        softScore += softWeight;
        registerConstraintMatch(kcontext,
                () -> {
                    hardScore -= hardWeight;
                    mediumScore -= mediumWeight;
                    softScore -= softWeight;
                },
                () -> HardMediumSoftLongScore.of(hardWeight, mediumWeight, softWeight));
    }

    @Override
    public HardMediumSoftLongScore extractScore(int initScore) {
        return HardMediumSoftLongScore.ofUninitialized(initScore, hardScore, mediumScore, softScore);
    }

}
