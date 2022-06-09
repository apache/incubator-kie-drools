package org.optaplanner.core.config.heuristic.selector;

import org.optaplanner.core.config.AbstractConfig;
import org.optaplanner.core.config.heuristic.selector.entity.EntitySelectorConfig;
import org.optaplanner.core.config.heuristic.selector.move.MoveSelectorConfig;
import org.optaplanner.core.config.heuristic.selector.value.ValueSelectorConfig;

/**
 * General superclass for {@link MoveSelectorConfig}, {@link EntitySelectorConfig} and {@link ValueSelectorConfig}.
 */
public abstract class SelectorConfig<Config_ extends SelectorConfig<Config_>> extends AbstractConfig<Config_> {

}
