package org.optaplanner.core.impl.heuristic.selector.move.generic;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.optaplanner.core.config.heuristic.selector.common.SelectionCacheType;
import org.optaplanner.core.config.heuristic.selector.common.SelectionOrder;
import org.optaplanner.core.config.heuristic.selector.entity.EntitySelectorConfig;
import org.optaplanner.core.config.heuristic.selector.move.generic.SwapMoveSelectorConfig;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.core.impl.heuristic.selector.AbstractSelectorFactoryTest;
import org.optaplanner.core.impl.heuristic.selector.move.MoveSelector;
import org.optaplanner.core.impl.heuristic.selector.move.MoveSelectorFactory;
import org.optaplanner.core.impl.heuristic.selector.move.composite.UnionMoveSelector;
import org.optaplanner.core.impl.testdata.domain.TestdataEntity;
import org.optaplanner.core.impl.testdata.domain.list.mixed.TestdataMixedVariablesSolution;
import org.optaplanner.core.impl.testdata.domain.multientity.TestdataHerdEntity;
import org.optaplanner.core.impl.testdata.domain.multientity.TestdataLeadEntity;
import org.optaplanner.core.impl.testdata.domain.multientity.TestdataMultiEntitySolution;
import org.optaplanner.core.impl.testdata.domain.multivar.TestdataMultiVarSolution;

class SwapMoveSelectorFactoryTest extends AbstractSelectorFactoryTest {

    @Test
    void deducibleMultiVar() {
        SolutionDescriptor solutionDescriptor = TestdataMultiVarSolution.buildSolutionDescriptor();
        SwapMoveSelectorConfig moveSelectorConfig = new SwapMoveSelectorConfig();
        moveSelectorConfig.setVariableNameIncludeList(Arrays.asList("secondaryValue"));
        MoveSelector moveSelector = MoveSelectorFactory.create(moveSelectorConfig).buildMoveSelector(
                buildHeuristicConfigPolicy(solutionDescriptor), SelectionCacheType.JUST_IN_TIME, SelectionOrder.RANDOM);
        assertThat(moveSelector)
                .isInstanceOf(SwapMoveSelector.class);
    }

    @Test
    void undeducibleMultiVar() {
        SolutionDescriptor solutionDescriptor = TestdataMultiVarSolution.buildSolutionDescriptor();
        SwapMoveSelectorConfig moveSelectorConfig = new SwapMoveSelectorConfig();
        moveSelectorConfig.setVariableNameIncludeList(Arrays.asList("nonExistingValue"));
        assertThatIllegalArgumentException().isThrownBy(() -> MoveSelectorFactory.create(moveSelectorConfig).buildMoveSelector(
                buildHeuristicConfigPolicy(solutionDescriptor),
                SelectionCacheType.JUST_IN_TIME,
                SelectionOrder.RANDOM));
    }

    @Test
    void unfoldedMultiVar() {
        SolutionDescriptor solutionDescriptor = TestdataMultiVarSolution.buildSolutionDescriptor();
        SwapMoveSelectorConfig moveSelectorConfig = new SwapMoveSelectorConfig();
        MoveSelector moveSelector = MoveSelectorFactory.create(moveSelectorConfig).buildMoveSelector(
                buildHeuristicConfigPolicy(solutionDescriptor), SelectionCacheType.JUST_IN_TIME, SelectionOrder.RANDOM);
        assertThat(moveSelector)
                .isInstanceOf(SwapMoveSelector.class);
    }

    @Test
    void deducibleMultiEntity() {
        SolutionDescriptor solutionDescriptor = TestdataMultiEntitySolution.buildSolutionDescriptor();
        SwapMoveSelectorConfig moveSelectorConfig = new SwapMoveSelectorConfig();
        moveSelectorConfig.setEntitySelectorConfig(new EntitySelectorConfig(TestdataHerdEntity.class));
        MoveSelector moveSelector = MoveSelectorFactory.create(moveSelectorConfig).buildMoveSelector(
                buildHeuristicConfigPolicy(solutionDescriptor), SelectionCacheType.JUST_IN_TIME, SelectionOrder.RANDOM);
        assertThat(moveSelector)
                .isInstanceOf(SwapMoveSelector.class);
    }

    @Test
    void undeducibleMultiEntity() {
        SolutionDescriptor solutionDescriptor = TestdataMultiEntitySolution.buildSolutionDescriptor();
        SwapMoveSelectorConfig moveSelectorConfig = new SwapMoveSelectorConfig();
        moveSelectorConfig.setEntitySelectorConfig(new EntitySelectorConfig(TestdataEntity.class));
        assertThatIllegalArgumentException().isThrownBy(() -> MoveSelectorFactory.create(moveSelectorConfig).buildMoveSelector(
                buildHeuristicConfigPolicy(solutionDescriptor),
                SelectionCacheType.JUST_IN_TIME,
                SelectionOrder.RANDOM));
    }

    @Test
    void unfoldedMultiEntity() {
        SolutionDescriptor solutionDescriptor = TestdataMultiEntitySolution.buildSolutionDescriptor();
        SwapMoveSelectorConfig moveSelectorConfig = new SwapMoveSelectorConfig();
        MoveSelector moveSelector = MoveSelectorFactory.create(moveSelectorConfig).buildMoveSelector(
                buildHeuristicConfigPolicy(solutionDescriptor), SelectionCacheType.JUST_IN_TIME, SelectionOrder.RANDOM);
        assertThat(moveSelector)
                .isInstanceOf(UnionMoveSelector.class);
        assertThat(((UnionMoveSelector) moveSelector).getChildMoveSelectorList().size()).isEqualTo(2);
    }

    @Test
    void deducibleMultiEntityWithSecondaryEntitySelector() {
        SolutionDescriptor solutionDescriptor = TestdataMultiEntitySolution.buildSolutionDescriptor();
        SwapMoveSelectorConfig moveSelectorConfig = new SwapMoveSelectorConfig();
        moveSelectorConfig.setEntitySelectorConfig(new EntitySelectorConfig(TestdataHerdEntity.class));
        moveSelectorConfig.setSecondaryEntitySelectorConfig(new EntitySelectorConfig(TestdataHerdEntity.class));
        MoveSelector moveSelector = MoveSelectorFactory.create(moveSelectorConfig).buildMoveSelector(
                buildHeuristicConfigPolicy(solutionDescriptor), SelectionCacheType.JUST_IN_TIME, SelectionOrder.RANDOM);
        assertThat(moveSelector)
                .isInstanceOf(SwapMoveSelector.class);
    }

    @Test
    void unswappableMultiEntityWithSecondaryEntitySelector() {
        SolutionDescriptor solutionDescriptor = TestdataMultiEntitySolution.buildSolutionDescriptor();
        SwapMoveSelectorConfig moveSelectorConfig = new SwapMoveSelectorConfig();
        moveSelectorConfig.setEntitySelectorConfig(new EntitySelectorConfig(TestdataLeadEntity.class));
        moveSelectorConfig.setSecondaryEntitySelectorConfig(new EntitySelectorConfig(TestdataHerdEntity.class));
        assertThatIllegalArgumentException().isThrownBy(() -> MoveSelectorFactory.create(moveSelectorConfig).buildMoveSelector(
                buildHeuristicConfigPolicy(solutionDescriptor),
                SelectionCacheType.JUST_IN_TIME,
                SelectionOrder.RANDOM));
    }

    @Test
    void unfoldedMultiEntityWithSecondaryEntitySelector() {
        SolutionDescriptor solutionDescriptor = TestdataMultiEntitySolution.buildSolutionDescriptor();
        SwapMoveSelectorConfig moveSelectorConfig = new SwapMoveSelectorConfig();
        moveSelectorConfig.setEntitySelectorConfig(new EntitySelectorConfig());
        moveSelectorConfig.setSecondaryEntitySelectorConfig(new EntitySelectorConfig());
        MoveSelector moveSelector = MoveSelectorFactory.create(moveSelectorConfig).buildMoveSelector(
                buildHeuristicConfigPolicy(solutionDescriptor), SelectionCacheType.JUST_IN_TIME, SelectionOrder.RANDOM);
        assertThat(moveSelector)
                .isInstanceOf(UnionMoveSelector.class);
        assertThat(((UnionMoveSelector) moveSelector).getChildMoveSelectorList().size()).isEqualTo(2);
    }

    @Test
    void mixingBasicAndListVariablesUnsupported() {
        SolutionDescriptor<TestdataMixedVariablesSolution> solutionDescriptor =
                TestdataMixedVariablesSolution.buildSolutionDescriptor();
        SwapMoveSelectorConfig moveSelectorConfig = new SwapMoveSelectorConfig();
        assertThatIllegalArgumentException().isThrownBy(
                () -> MoveSelectorFactory.<TestdataMixedVariablesSolution> create(moveSelectorConfig).buildMoveSelector(
                        buildHeuristicConfigPolicy(solutionDescriptor),
                        SelectionCacheType.JUST_IN_TIME,
                        SelectionOrder.RANDOM))
                .withMessageContaining("variableDescriptorList");
    }

}
