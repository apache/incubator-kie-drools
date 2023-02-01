package org.optaplanner.core.impl.constructionheuristic.placer;

import static org.optaplanner.core.config.heuristic.selector.common.SelectionCacheType.PHASE;
import static org.optaplanner.core.config.heuristic.selector.common.SelectionCacheType.STEP;

import org.optaplanner.core.config.constructionheuristic.placer.EntityPlacerConfig;
import org.optaplanner.core.config.heuristic.selector.common.SelectionOrder;
import org.optaplanner.core.config.heuristic.selector.entity.EntitySelectorConfig;
import org.optaplanner.core.config.heuristic.selector.move.generic.ChangeMoveSelectorConfig;
import org.optaplanner.core.config.heuristic.selector.value.ValueSelectorConfig;
import org.optaplanner.core.impl.AbstractFromConfigFactory;
import org.optaplanner.core.impl.domain.variable.descriptor.GenuineVariableDescriptor;
import org.optaplanner.core.impl.heuristic.HeuristicConfigPolicy;

abstract class AbstractEntityPlacerFactory<Solution_, EntityPlacerConfig_ extends EntityPlacerConfig<EntityPlacerConfig_>>
        extends AbstractFromConfigFactory<Solution_, EntityPlacerConfig_> implements EntityPlacerFactory<Solution_> {

    protected AbstractEntityPlacerFactory(EntityPlacerConfig_ placerConfig) {
        super(placerConfig);
    }

    protected ChangeMoveSelectorConfig buildChangeMoveSelectorConfig(HeuristicConfigPolicy<Solution_> configPolicy,
            String entitySelectorConfigId, GenuineVariableDescriptor<Solution_> variableDescriptor) {
        ChangeMoveSelectorConfig changeMoveSelectorConfig = new ChangeMoveSelectorConfig();
        changeMoveSelectorConfig.setEntitySelectorConfig(
                EntitySelectorConfig.newMimicSelectorConfig(entitySelectorConfigId));
        ValueSelectorConfig changeValueSelectorConfig = new ValueSelectorConfig()
                .withVariableName(variableDescriptor.getVariableName());
        if (ValueSelectorConfig.hasSorter(configPolicy.getValueSorterManner(), variableDescriptor)) {
            changeValueSelectorConfig = changeValueSelectorConfig
                    .withCacheType(variableDescriptor.isValueRangeEntityIndependent() ? PHASE : STEP)
                    .withSelectionOrder(SelectionOrder.SORTED)
                    .withSorterManner(configPolicy.getValueSorterManner());
        }
        return changeMoveSelectorConfig.withValueSelectorConfig(changeValueSelectorConfig);
    }
}
