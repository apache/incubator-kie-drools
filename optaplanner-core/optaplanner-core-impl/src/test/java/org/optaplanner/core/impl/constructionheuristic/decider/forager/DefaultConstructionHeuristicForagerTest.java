package org.optaplanner.core.impl.constructionheuristic.decider.forager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.api.score.buildin.simple.SimpleScore;
import org.optaplanner.core.config.constructionheuristic.decider.forager.ConstructionHeuristicPickEarlyType;
import org.optaplanner.core.impl.constructionheuristic.scope.ConstructionHeuristicMoveScope;
import org.optaplanner.core.impl.constructionheuristic.scope.ConstructionHeuristicPhaseScope;
import org.optaplanner.core.impl.constructionheuristic.scope.ConstructionHeuristicStepScope;

class DefaultConstructionHeuristicForagerTest<Solution_> {

    @Test
    void checkPickEarlyNever() {
        DefaultConstructionHeuristicForager forager = new DefaultConstructionHeuristicForager(
                ConstructionHeuristicPickEarlyType.NEVER);
        ConstructionHeuristicStepScope<Solution_> stepScope = buildStepScope(SimpleScore.ofUninitialized(-8, -100));
        forager.checkPickEarly(buildMoveScope(stepScope, SimpleScore.ofUninitialized(-7, -110)));
        assertThat(forager.isQuitEarly()).isFalse();
        forager.checkPickEarly(buildMoveScope(stepScope, SimpleScore.ofUninitialized(-7, -100)));
        assertThat(forager.isQuitEarly()).isFalse();
        forager.checkPickEarly(buildMoveScope(stepScope, SimpleScore.ofUninitialized(-7, -90)));
        assertThat(forager.isQuitEarly()).isFalse();
    }

    @Test
    void checkPickEarlyFirstNonDeterioratingScore() {
        DefaultConstructionHeuristicForager forager = new DefaultConstructionHeuristicForager(
                ConstructionHeuristicPickEarlyType.FIRST_NON_DETERIORATING_SCORE);
        ConstructionHeuristicStepScope<Solution_> stepScope = buildStepScope(SimpleScore.ofUninitialized(-8, -100));
        forager.checkPickEarly(buildMoveScope(stepScope, SimpleScore.ofUninitialized(-7, -110)));
        assertThat(forager.isQuitEarly()).isFalse();
        forager.checkPickEarly(buildMoveScope(stepScope, SimpleScore.ofUninitialized(-7, -100)));
        assertThat(forager.isQuitEarly()).isTrue();
    }

    @Test
    void checkPickEarlyFirstFeasibleScore() {
        DefaultConstructionHeuristicForager forager = new DefaultConstructionHeuristicForager(
                ConstructionHeuristicPickEarlyType.FIRST_FEASIBLE_SCORE);
        ConstructionHeuristicStepScope<Solution_> stepScope = buildStepScope(HardSoftScore.ofUninitialized(-8, 0, -100));
        forager.checkPickEarly(buildMoveScope(stepScope, HardSoftScore.ofUninitialized(-7, -1, -110)));
        assertThat(forager.isQuitEarly()).isFalse();
        forager.checkPickEarly(buildMoveScope(stepScope, HardSoftScore.ofUninitialized(-7, -1, -90)));
        assertThat(forager.isQuitEarly()).isFalse();
        forager.checkPickEarly(buildMoveScope(stepScope, HardSoftScore.ofUninitialized(-7, 0, -110)));
        assertThat(forager.isQuitEarly()).isTrue();
    }

    @Test
    void checkPickEarlyFirstFeasibleScoreOrNonDeterioratingHard() {
        DefaultConstructionHeuristicForager forager = new DefaultConstructionHeuristicForager(
                ConstructionHeuristicPickEarlyType.FIRST_FEASIBLE_SCORE_OR_NON_DETERIORATING_HARD);
        ConstructionHeuristicStepScope<Solution_> stepScope = buildStepScope(HardSoftScore.ofUninitialized(-8, -10, -100));
        forager.checkPickEarly(buildMoveScope(stepScope, HardSoftScore.ofUninitialized(-7, -11, -110)));
        assertThat(forager.isQuitEarly()).isFalse();
        forager.checkPickEarly(buildMoveScope(stepScope, HardSoftScore.ofUninitialized(-7, -11, -90)));
        assertThat(forager.isQuitEarly()).isFalse();
        forager.checkPickEarly(buildMoveScope(stepScope, HardSoftScore.ofUninitialized(-7, -10, -110)));
        assertThat(forager.isQuitEarly()).isTrue();
    }

    protected ConstructionHeuristicStepScope<Solution_> buildStepScope(Score lastStepScore) {
        ConstructionHeuristicPhaseScope<Solution_> phaseScope = mock(ConstructionHeuristicPhaseScope.class);
        ConstructionHeuristicStepScope<Solution_> lastCompletedStepScope = mock(ConstructionHeuristicStepScope.class);
        when(lastCompletedStepScope.getPhaseScope()).thenReturn(phaseScope);
        when(lastCompletedStepScope.getScore()).thenReturn(lastStepScore);
        when(phaseScope.getLastCompletedStepScope()).thenReturn(lastCompletedStepScope);

        ConstructionHeuristicStepScope<Solution_> stepScope = mock(ConstructionHeuristicStepScope.class);
        when(stepScope.getPhaseScope()).thenReturn(phaseScope);
        return stepScope;
    }

    protected ConstructionHeuristicMoveScope<Solution_> buildMoveScope(ConstructionHeuristicStepScope<Solution_> stepScope,
            Score score) {
        ConstructionHeuristicMoveScope<Solution_> moveScope = mock(ConstructionHeuristicMoveScope.class);
        when(moveScope.getStepScope()).thenReturn(stepScope);
        when(moveScope.getScore()).thenReturn(score);
        return moveScope;
    }

}
