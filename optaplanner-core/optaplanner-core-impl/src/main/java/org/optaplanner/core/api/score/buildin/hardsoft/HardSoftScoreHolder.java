package org.optaplanner.core.api.score.buildin.hardsoft;

import org.kie.api.runtime.rule.RuleContext;
import org.optaplanner.core.api.domain.constraintweight.ConstraintWeight;
import org.optaplanner.core.api.score.holder.ScoreHolder;

/**
 * @see HardSoftScore
 * @deprecated Score DRL is deprecated and will be removed in a future major version of OptaPlanner.
 *             See <a href="https://www.optaplanner.org/learn/drl-to-constraint-streams-migration.html">DRL to Constraint
 *             Streams migration recipe</a>.
 */
@Deprecated(forRemoval = true)
public interface HardSoftScoreHolder extends ScoreHolder<HardSoftScore> {

    /**
     * Penalize a match by the {@link ConstraintWeight} negated and multiplied with the weightMultiplier for all score levels.
     *
     * @param kcontext never null, the magic variable in DRL
     * @param weightMultiplier at least 0
     */
    void penalize(RuleContext kcontext, int weightMultiplier);

    /**
     * Penalize a match by the {@link ConstraintWeight} negated and multiplied with the specific weightMultiplier per score
     * level.
     * Slower than {@link #penalize(RuleContext, int)}.
     *
     * @param kcontext never null, the magic variable in DRL
     * @param hardWeightMultiplier at least 0
     * @param softWeightMultiplier at least 0
     */
    void penalize(RuleContext kcontext, int hardWeightMultiplier, int softWeightMultiplier);

    /**
     * Reward a match by the {@link ConstraintWeight} multiplied with the weightMultiplier for all score levels.
     *
     * @param kcontext never null, the magic variable in DRL
     * @param weightMultiplier at least 0
     */
    void reward(RuleContext kcontext, int weightMultiplier);

    /**
     * Reward a match by the {@link ConstraintWeight} multiplied with the specific weightMultiplier per score level.
     * Slower than {@link #reward(RuleContext, int)}.
     *
     * @param kcontext never null, the magic variable in DRL
     * @param hardWeightMultiplier at least 0
     * @param softWeightMultiplier at least 0
     */
    void reward(RuleContext kcontext, int hardWeightMultiplier, int softWeightMultiplier);

    void impactScore(RuleContext kcontext, int weightMultiplier);

    /**
     * @param kcontext never null, the magic variable in DRL
     * @param hardWeight higher is better, negative for a penalty, positive for a reward
     */
    void addHardConstraintMatch(RuleContext kcontext, int hardWeight);

    /**
     * @param kcontext never null, the magic variable in DRL
     * @param softWeight higher is better, negative for a penalty, positive for a reward
     */
    void addSoftConstraintMatch(RuleContext kcontext, int softWeight);

    /**
     * @param kcontext never null, the magic variable in DRL
     * @param hardWeight higher is better, negative for a penalty, positive for a reward
     * @param softWeight higher is better, negative for a penalty, positive for a reward
     */
    void addMultiConstraintMatch(RuleContext kcontext, int hardWeight, int softWeight);
}
