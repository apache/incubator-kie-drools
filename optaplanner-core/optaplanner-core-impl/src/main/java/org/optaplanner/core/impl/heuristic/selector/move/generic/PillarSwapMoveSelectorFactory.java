package org.optaplanner.core.impl.heuristic.selector.move.generic;

import java.util.List;
import java.util.Objects;

import org.optaplanner.core.config.heuristic.selector.common.SelectionCacheType;
import org.optaplanner.core.config.heuristic.selector.common.SelectionOrder;
import org.optaplanner.core.config.heuristic.selector.entity.pillar.PillarSelectorConfig;
import org.optaplanner.core.config.heuristic.selector.move.generic.PillarSwapMoveSelectorConfig;
import org.optaplanner.core.impl.domain.variable.descriptor.GenuineVariableDescriptor;
import org.optaplanner.core.impl.heuristic.HeuristicConfigPolicy;
import org.optaplanner.core.impl.heuristic.selector.entity.pillar.PillarSelector;
import org.optaplanner.core.impl.heuristic.selector.entity.pillar.PillarSelectorFactory;
import org.optaplanner.core.impl.heuristic.selector.move.AbstractMoveSelectorFactory;
import org.optaplanner.core.impl.heuristic.selector.move.MoveSelector;

public class PillarSwapMoveSelectorFactory<Solution_>
        extends AbstractMoveSelectorFactory<Solution_, PillarSwapMoveSelectorConfig> {

    public PillarSwapMoveSelectorFactory(PillarSwapMoveSelectorConfig moveSelectorConfig) {
        super(moveSelectorConfig);
    }

    @Override
    protected MoveSelector<Solution_> buildBaseMoveSelector(HeuristicConfigPolicy<Solution_> configPolicy,
            SelectionCacheType minimumCacheType, boolean randomSelection) {
        PillarSelectorConfig leftPillarSelectorConfig =
                Objects.requireNonNullElseGet(config.getPillarSelectorConfig(), PillarSelectorConfig::new);
        PillarSelector<Solution_> leftPillarSelector =
                buildPillarSelector(leftPillarSelectorConfig, configPolicy, minimumCacheType, randomSelection);
        PillarSelectorConfig rightPillarSelectorConfig =
                Objects.requireNonNullElse(config.getSecondaryPillarSelectorConfig(), leftPillarSelectorConfig);
        PillarSelector<Solution_> rightPillarSelector =
                buildPillarSelector(rightPillarSelectorConfig, configPolicy, minimumCacheType, randomSelection);

        List<GenuineVariableDescriptor<Solution_>> variableDescriptorList =
                deduceVariableDescriptorList(leftPillarSelector.getEntityDescriptor(), config.getVariableNameIncludeList());
        return new PillarSwapMoveSelector<>(leftPillarSelector, rightPillarSelector, variableDescriptorList,
                randomSelection);
    }

    private PillarSelector<Solution_> buildPillarSelector(PillarSelectorConfig pillarSelectorConfig,
            HeuristicConfigPolicy<Solution_> configPolicy, SelectionCacheType minimumCacheType,
            boolean randomSelection) {
        return PillarSelectorFactory.<Solution_> create(pillarSelectorConfig)
                .buildPillarSelector(configPolicy, config.getSubPillarType(),
                        config.getSubPillarSequenceComparatorClass(), minimumCacheType,
                        SelectionOrder.fromRandomSelectionBoolean(randomSelection),
                        config.getVariableNameIncludeList());
    }

}
