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
import org.optaplanner.core.api.score.buildin.bendable.BendableScore;
import org.optaplanner.core.api.score.buildin.bendable.BendableScoreHolder;

/**
 * @see BendableScore
 */
public final class BendableScoreHolderImpl extends AbstractScoreHolder<BendableScore> implements BendableScoreHolder {

    protected final Map<Rule, IntMatchExecutor> matchExecutorByNumberMap = new LinkedHashMap<>();
    /** Slower than {@link #matchExecutorByNumberMap} */
    protected final Map<Rule, ScoreMatchExecutor<BendableScore>> matchExecutorByScoreMap = new LinkedHashMap<>();

    private int[] hardScores;
    private int[] softScores;

    public BendableScoreHolderImpl(boolean constraintMatchEnabled, int hardLevelsSize, int softLevelsSize) {
        super(constraintMatchEnabled);
        hardScores = new int[hardLevelsSize];
        softScores = new int[softLevelsSize];
    }

    @Override
    public int getHardLevelsSize() {
        return hardScores.length;
    }

    public int getHardScore(int hardLevel) {
        return hardScores[hardLevel];
    }

    @Override
    public int getSoftLevelsSize() {
        return softScores.length;
    }

    public int getSoftScore(int softLevel) {
        return softScores[softLevel];
    }

    // ************************************************************************
    // Setup methods
    // ************************************************************************

    @Override
    public void configureConstraintWeight(Rule rule, BendableScore constraintWeight) {
        super.configureConstraintWeight(rule, constraintWeight);
        IntMatchExecutor matchExecutor;
        if (constraintWeight.equals(BendableScore.zero(hardScores.length, softScores.length))) {
            matchExecutor = (RuleContext kcontext, int matchWeight) -> {
            };
        } else {
            Integer singleLevel = null;
            Integer singleLevelWeight = null;
            for (int i = 0; i < constraintWeight.getLevelsSize(); i++) {
                int levelWeight = constraintWeight.getHardOrSoftScore(i);
                if (levelWeight != 0) {
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
                int levelWeight = singleLevelWeight;
                if (singleLevel < constraintWeight.getHardLevelsSize()) {
                    int level = singleLevel;
                    matchExecutor = (RuleContext kcontext, int matchWeight) -> addHardConstraintMatch(
                            kcontext, level, levelWeight * matchWeight);
                } else {
                    int level = singleLevel - constraintWeight.getHardLevelsSize();
                    matchExecutor = (RuleContext kcontext, int matchWeight) -> addSoftConstraintMatch(
                            kcontext, level, levelWeight * matchWeight);
                }
            } else {
                matchExecutor = (RuleContext kcontext, int matchWeight) -> {
                    int[] hardWeights = new int[hardScores.length];
                    int[] softWeights = new int[softScores.length];
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
        matchExecutorByScoreMap.put(rule, (RuleContext kcontext, BendableScore weightMultiplier) -> {
            int[] hardWeights = new int[hardScores.length];
            int[] softWeights = new int[softScores.length];
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
        impactScore(kcontext, -1);
    }

    @Override
    public void penalize(RuleContext kcontext, int weightMultiplier) {
        impactScore(kcontext, -weightMultiplier);
    }

    @Override
    public void penalize(RuleContext kcontext, int[] hardWeightsMultiplier, int[] softWeightsMultiplier) {
        int[] negatedHardWeightsMultiplier = new int[hardScores.length];
        int[] negatedSoftWeightsMultiplier = new int[softScores.length];
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
        impactScore(kcontext, 1);
    }

    @Override
    public void reward(RuleContext kcontext, int weightMultiplier) {
        impactScore(kcontext, weightMultiplier);
    }

    @Override
    public void reward(RuleContext kcontext, int[] hardWeightsMultiplier, int[] softWeightsMultiplier) {
        impactScore(kcontext, hardWeightsMultiplier, softWeightsMultiplier);
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

    private void impactScore(RuleContext kcontext, int[] hardWeightsMultiplier, int[] softWeightsMultiplier) {
        Rule rule = kcontext.getRule();
        ScoreMatchExecutor<BendableScore> matchExecutor = matchExecutorByScoreMap.get(rule);
        if (matchExecutor == null) {
            throw new IllegalStateException("The DRL rule (" + rule.getPackageName() + ":" + rule.getName()
                    + ") does not match a @" + ConstraintWeight.class.getSimpleName() + " on the @"
                    + ConstraintConfiguration.class.getSimpleName() + " annotated class.");
        }
        matchExecutor.accept(kcontext, BendableScore.of(hardWeightsMultiplier, softWeightsMultiplier));
    }

    // ************************************************************************
    // Other match methods
    // ************************************************************************

    @Override
    public void addHardConstraintMatch(RuleContext kcontext, int hardLevel, int weight) {
        if (hardLevel >= hardScores.length) {
            throw new IllegalArgumentException("The hardLevel (" + hardLevel
                    + ") isn't lower than the hardScores length (" + hardScores.length
                    + ") defined by the @" + PlanningScore.class.getSimpleName() + " on the planning solution class.");
        }
        hardScores[hardLevel] += weight;
        registerConstraintMatch(kcontext,
                () -> hardScores[hardLevel] -= weight,
                () -> {
                    int[] newHardScores = new int[hardScores.length];
                    int[] newSoftScores = new int[softScores.length];
                    newHardScores[hardLevel] = weight;
                    return BendableScore.of(newHardScores, newSoftScores);
                });
    }

    @Override
    public void addSoftConstraintMatch(RuleContext kcontext, int softLevel, int weight) {
        if (softLevel >= softScores.length) {
            throw new IllegalArgumentException("The softLevel (" + softLevel
                    + ") isn't lower than the softScores length (" + softScores.length
                    + ") defined by the @" + PlanningScore.class.getSimpleName() + " on the planning solution class.");
        }
        softScores[softLevel] += weight;
        registerConstraintMatch(kcontext,
                () -> softScores[softLevel] -= weight,
                () -> {
                    int[] newHardScores = new int[hardScores.length];
                    int[] newSoftScores = new int[softScores.length];
                    newSoftScores[softLevel] = weight;
                    return BendableScore.of(newHardScores, newSoftScores);
                });
    }

    @Override
    public void addMultiConstraintMatch(RuleContext kcontext, int[] hardWeights, int[] softWeights) {
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
                () -> BendableScore.of(hardWeights, softWeights));
    }

    @Override
    public BendableScore extractScore(int initScore) {
        return BendableScore.ofUninitialized(initScore,
                Arrays.copyOf(hardScores, hardScores.length),
                Arrays.copyOf(softScores, softScores.length));
    }

}
