package org.optaplanner.core.impl.constructionheuristic.placer;

import org.optaplanner.core.config.constructionheuristic.placer.EntityPlacerConfig;
import org.optaplanner.core.config.heuristic.selector.common.SelectionCacheType;
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
        ValueSelectorConfig changeValueSelectorConfig = new ValueSelectorConfig();
        changeValueSelectorConfig.setVariableName(variableDescriptor.getVariableName());
        if (ValueSelectorConfig.hasSorter(configPolicy.getValueSorterManner(), variableDescriptor)) {
            if (variableDescriptor.isValueRangeEntityIndependent()) {
                changeValueSelectorConfig.setCacheType(SelectionCacheType.PHASE);
            } else {
                changeValueSelectorConfig.setCacheType(SelectionCacheType.STEP);
            }
            changeValueSelectorConfig.setSelectionOrder(SelectionOrder.SORTED);
            changeValueSelectorConfig.setSorterManner(configPolicy.getValueSorterManner());
        }
        changeMoveSelectorConfig.setValueSelectorConfig(changeValueSelectorConfig);
        return changeMoveSelectorConfig;
    }
}
