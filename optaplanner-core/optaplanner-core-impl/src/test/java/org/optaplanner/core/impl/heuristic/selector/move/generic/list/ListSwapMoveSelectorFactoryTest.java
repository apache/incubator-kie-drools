package org.optaplanner.core.impl.heuristic.selector.move.generic.list;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.optaplanner.core.impl.heuristic.HeuristicConfigPolicyTestUtils.buildHeuristicConfigPolicy;

import org.junit.jupiter.api.Test;
import org.optaplanner.core.config.heuristic.selector.common.SelectionCacheType;
import org.optaplanner.core.config.heuristic.selector.common.SelectionOrder;
import org.optaplanner.core.config.heuristic.selector.move.generic.list.ListSwapMoveSelectorConfig;
import org.optaplanner.core.config.heuristic.selector.value.ValueSelectorConfig;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.core.impl.heuristic.HeuristicConfigPolicy;
import org.optaplanner.core.impl.heuristic.selector.move.MoveSelector;
import org.optaplanner.core.impl.heuristic.selector.move.MoveSelectorFactory;
import org.optaplanner.core.impl.testdata.domain.TestdataSolution;
import org.optaplanner.core.impl.testdata.domain.list.TestdataListSolution;
import org.optaplanner.core.impl.testdata.domain.list.mixed.TestdataMixedVariablesSolution;

class ListSwapMoveSelectorFactoryTest {

    @Test
    void noUnfolding() {
        SolutionDescriptor<TestdataListSolution> solutionDescriptor = TestdataListSolution.buildSolutionDescriptor();
        ListSwapMoveSelectorConfig moveSelectorConfig = new ListSwapMoveSelectorConfig()
                .withValueSelectorConfig(new ValueSelectorConfig("valueList"));
        MoveSelector<TestdataListSolution> moveSelector =
                MoveSelectorFactory.<TestdataListSolution> create(moveSelectorConfig).buildMoveSelector(
                        buildHeuristicConfigPolicy(solutionDescriptor), SelectionCacheType.JUST_IN_TIME, SelectionOrder.RANDOM);
        assertThat(moveSelector).isInstanceOf(ListSwapMoveSelector.class);
    }

    @Test
    void unfoldedSingleListVariable() {
        SolutionDescriptor<TestdataListSolution> solutionDescriptor = TestdataListSolution.buildSolutionDescriptor();
        ListSwapMoveSelectorConfig moveSelectorConfig = new ListSwapMoveSelectorConfig();
        MoveSelector<TestdataListSolution> moveSelector =
                MoveSelectorFactory.<TestdataListSolution> create(moveSelectorConfig).buildMoveSelector(
                        buildHeuristicConfigPolicy(solutionDescriptor), SelectionCacheType.JUST_IN_TIME, SelectionOrder.RANDOM);
        assertThat(moveSelector).isInstanceOf(ListSwapMoveSelector.class);
    }

    @Test
    void unfoldingSkipsBasicVariablesGracefully() {
        SolutionDescriptor<TestdataMixedVariablesSolution> solutionDescriptor =
                TestdataMixedVariablesSolution.buildSolutionDescriptor();
        ListSwapMoveSelectorConfig moveSelectorConfig = new ListSwapMoveSelectorConfig();
        MoveSelector<TestdataMixedVariablesSolution> moveSelector =
                MoveSelectorFactory.<TestdataMixedVariablesSolution> create(moveSelectorConfig).buildMoveSelector(
                        buildHeuristicConfigPolicy(solutionDescriptor), SelectionCacheType.JUST_IN_TIME, SelectionOrder.RANDOM);
        assertThat(moveSelector).isInstanceOf(ListSwapMoveSelector.class);
    }

    @Test
    void unfoldingFailsIfThereIsNoListVariable() {
        ListSwapMoveSelectorConfig config = new ListSwapMoveSelectorConfig();
        ListSwapMoveSelectorFactory<TestdataSolution> moveSelectorFactory = new ListSwapMoveSelectorFactory<>(config);

        HeuristicConfigPolicy<TestdataSolution> heuristicConfigPolicy =
                buildHeuristicConfigPolicy(TestdataSolution.buildSolutionDescriptor());

        assertThatIllegalArgumentException()
                .isThrownBy(() -> moveSelectorFactory.buildMoveSelector(heuristicConfigPolicy,
                        SelectionCacheType.JUST_IN_TIME, SelectionOrder.RANDOM))
                .withMessageContaining("cannot unfold");
    }

    @Test
    void explicitConfigMustUseListVariable() {
        ListSwapMoveSelectorConfig config = new ListSwapMoveSelectorConfig()
                .withValueSelectorConfig(new ValueSelectorConfig("value"));

        ListSwapMoveSelectorFactory<TestdataMixedVariablesSolution> moveSelectorFactory =
                new ListSwapMoveSelectorFactory<>(config);

        HeuristicConfigPolicy<TestdataMixedVariablesSolution> heuristicConfigPolicy =
                buildHeuristicConfigPolicy(TestdataMixedVariablesSolution.buildSolutionDescriptor());

        assertThatIllegalArgumentException()
                .isThrownBy(() -> moveSelectorFactory.buildMoveSelector(heuristicConfigPolicy,
                        SelectionCacheType.JUST_IN_TIME, SelectionOrder.RANDOM))
                .withMessageContaining("not a planning list variable");
    }

    // TODO test or remove secondary value selector config
}
