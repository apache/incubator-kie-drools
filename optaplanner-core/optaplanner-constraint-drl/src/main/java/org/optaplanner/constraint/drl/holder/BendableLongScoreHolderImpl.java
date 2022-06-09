package org.optaplanner.constraint.drl.holder;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

import org.kie.api.definition.rule.Rule;
import org.kie.api.runtime.rule.RuleContext;
import org.optaplanner.core.api.domain.constraintweight.ConstraintConfiguration;
import org.optaplanner.core.api.domain.constraintweight.ConstraintWeight;
import org.optaplanner.core.api.domain.solution.PlanningScore;
import org.optaplanner.core.api.score.buildin.bendablelong.BendableLongScore;
import org.optaplanner.core.api.score.buildin.bendablelong.BendableLongScoreHolder;

/**
 * @see BendableLongScore
 */
public final class BendableLongScoreHolderImpl extends AbstractScoreHolder<BendableLongScore>
        implements BendableLongScoreHolder {

    protected final Map<Rule, LongMatchExecutor> matchExecutorByNumberMap = new LinkedHashMap<>();
    /** Slower than {@link #matchExecutorByNumberMap} */
    protected final Map<Rule, ScoreMatchExecutor<BendableLongScore>> matchExecutorByScoreMap = new LinkedHashMap<>();

    private long[] hardScores;
    private long[] softScores;

    public BendableLongScoreHolderImpl(boolean constraintMatchEnabled, int hardLevelsSize, int softLevelsSize) {
        super(constraintMatchEnabled);
        hardScores = new long[hardLevelsSize];
        softScores = new long[softLevelsSize];
    }

    @Override
    public int getHardLevelsSize() {
        return hardScores.length;
    }

    public long getHardScore(int hardLevel) {
        return hardScores[hardLevel];
    }

    @Override
    public int getSoftLevelsSize() {
        return softScores.length;
    }

    public long getSoftScore(int softLevel) {
        return softScores[softLevel];
    }

    // ************************************************************************
    // Setup methods
    // ************************************************************************

    @Override
    public void configureConstraintWeight(Rule rule, BendableLongScore constraintWeight) {
        super.configureConstraintWeight(rule, constraintWeight);
        LongMatchExecutor matchExecutor;
        if (constraintWeight.equals(BendableLongScore.zero(hardScores.length, softScores.length))) {
            matchExecutor = (RuleContext kcontext, long matchWeight) -> {
            };
        } else {
            Integer singleLevel = null;
            Long singleLevelWeight = null;
            for (int i = 0; i < constraintWeight.getLevelsSize(); i++) {
                long levelWeight = constraintWeight.getHardOrSoftScore(i);
                if (levelWeight != 0L) {
                    if (singleLevel != null) {
                        singleLevel = null;
                        singleLevelWeight = null;
                        break;
                    }
                    singleLevel = i;
                    singleLevelWeight = levelWeight;
                }
            }
            if (singleLevel != null) {
                long levelWeight = singleLevelWeight;
                if (singleLevel < constraintWeight.getHardLevelsSize()) {
                    int level = singleLevel;
                    matchExecutor =
                            (RuleContext kcontext, long matchWeight) -> addHardConstraintMatch(
                                    kcontext, level, levelWeight * matchWeight);
                } else {
                    int level = singleLevel - constraintWeight.getHardLevelsSize();
                    matchExecutor =
                            (RuleContext kcontext, long matchWeight) -> addSoftConstraintMatch(
                                    kcontext, level, levelWeight * matchWeight);
                }
            } else {
                matchExecutor = (RuleContext kcontext, long matchWeight) -> {
                    long[] hardWeights = new long[hardScores.length];
                    long[] softWeights = new long[softScores.length];
                    for (int i = 0; i < hardWeights.length; i++) {
                        hardWeights[i] = constraintWeight.getHardScore(i) * matchWeight;
                    }
                    for (int i = 0; i < softWeights.length; i++) {
                        softWeights[i] = constraintWeight.getSoftScore(i) * matchWeight;
                    }
                    addMultiConstraintMatch(kcontext, hardWeights, softWeights);
                };
            }
        }
        matchExecutorByNumberMap.put(rule, matchExecutor);
        matchExecutorByScoreMap.put(rule, (RuleContext kcontext, BendableLongScore weightMultiplier) -> {
            long[] hardWeights = new long[hardScores.length];
            long[] softWeights = new long[softScores.length];
            for (int i = 0; i < hardWeights.length; i++) {
                hardWeights[i] = constraintWeight.getHardScore(i) * weightMultiplier.getHardScore(i);
            }
            for (int i = 0; i < softWeights.length; i++) {
                softWeights[i] = constraintWeight.getSoftScore(i) * weightMultiplier.getSoftScore(i);
            }
            addMultiConstraintMatch(kcontext, hardWeights, softWeights);
        });
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
    public void penalize(RuleContext kcontext, long[] hardWeightsMultiplier, long[] softWeightsMultiplier) {
        long[] negatedHardWeightsMultiplier = new long[hardScores.length];
        long[] negatedSoftWeightsMultiplier = new long[softScores.length];
        for (int i = 0; i < negatedHardWeightsMultiplier.length; i++) {
            negatedHardWeightsMultiplier[i] = -hardWeightsMultiplier[i];
        }
        for (int i = 0; i < negatedSoftWeightsMultiplier.length; i++) {
            negatedSoftWeightsMultiplier[i] = -softWeightsMultiplier[i];
        }
        impactScore(kcontext, negatedHardWeightsMultiplier, negatedSoftWeightsMultiplier);
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
    public void reward(RuleContext kcontext, long[] hardWeightsMultiplier, long[] softWeightsMultiplier) {
        impactScore(kcontext, hardWeightsMultiplier, softWeightsMultiplier);
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

    private void impactScore(RuleContext kcontext, long[] hardWeightsMultiplier, long[] softWeightsMultiplier) {
        Rule rule = kcontext.getRule();
        ScoreMatchExecutor<BendableLongScore> matchExecutor = matchExecutorByScoreMap.get(rule);
        if (matchExecutor == null) {
            throw new IllegalStateException("The DRL rule (" + rule.getPackageName() + ":" + rule.getName()
                    + ") does not match a @" + ConstraintWeight.class.getSimpleName() + " on the @"
                    + ConstraintConfiguration.class.getSimpleName() + " annotated class.");
        }
        matchExecutor.accept(kcontext, BendableLongScore.of(hardWeightsMultiplier, softWeightsMultiplier));
    }

    // ************************************************************************
    // Other match methods
    // ************************************************************************

    @Override
    public void addHardConstraintMatch(RuleContext kcontext, int hardLevel, long weight) {
        if (hardLevel >= hardScores.length) {
            throw new IllegalArgumentException("The hardLevel (" + hardLevel
                    + ") isn't lower than the hardScores length (" + hardScores.length
                    + ") defined by the @" + PlanningScore.class.getSimpleName() + " on the planning solution class.");
        }
        hardScores[hardLevel] += weight;
        registerConstraintMatch(kcontext,
                () -> hardScores[hardLevel] -= weight,
                () -> {
                    long[] newHardScores = new long[hardScores.length];
                    long[] newSoftScores = new long[softScores.length];
                    newHardScores[hardLevel] = weight;
                    return BendableLongScore.of(newHardScores, newSoftScores);
                });
    }

    @Override
    public void addSoftConstraintMatch(RuleContext kcontext, int softLevel, long weight) {
        if (softLevel >= softScores.length) {
            throw new IllegalArgumentException("The softLevel (" + softLevel
                    + ") isn't lower than the softScores length (" + softScores.length
                    + ") defined by the @" + PlanningScore.class.getSimpleName() + " on the planning solution class.");
        }
        softScores[softLevel] += weight;
        registerConstraintMatch(kcontext,
                () -> softScores[softLevel] -= weight,
                () -> {
                    long[] newHardScores = new long[hardScores.length];
                    long[] newSoftScores = new long[softScores.length];
                    newSoftScores[softLevel] = weight;
                    return BendableLongScore.of(newHardScores, newSoftScores);
                });
    }

    @Override
    public void addMultiConstraintMatch(RuleContext kcontext, long[] hardWeights, long[] softWeights) {
        if (hardWeights.length != hardScores.length) {
            throw new IllegalArgumentException("The hardWeights length (" + hardWeights.length
                    + ") is different than the hardScores length (" + hardScores.length
                    + ") defined by the @" + PlanningScore.class.getSimpleName() + " on the planning solution class.");
        }
        for (int i = 0; i < hardScores.length; i++) {
            hardScores[i] += hardWeights[i];
        }
        if (softWeights.length != softScores.length) {
            throw new IllegalArgumentException("The softWeights length (" + softWeights.length
                    + ") is different than the softScores length (" + softScores.length
                    + ") defined by the @" + PlanningScore.class.getSimpleName() + " on the planning solution class.");
        }
        for (int i = 0; i < softScores.length; i++) {
            softScores[i] += softWeights[i];
        }
        registerConstraintMatch(kcontext,
                () -> {
                    for (int i = 0; i < hardScores.length; i++) {
                        hardScores[i] -= hardWeights[i];
                    }
                    for (int i = 0; i < softScores.length; i++) {
                        softScores[i] -= softWeights[i];
                    }
                },
                () -> BendableLongScore.of(hardWeights, softWeights));
    }

    @Override
    public BendableLongScore extractScore(int initScore) {
        return BendableLongScore.ofUninitialized(initScore,
                Arrays.copyOf(hardScores, hardScores.length),
                Arrays.copyOf(softScores, softScores.length));
    }

}
