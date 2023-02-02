package org.optaplanner.core.config.heuristic.selector.move.generic.list;

/**
 * Defines subList properties implemented by various subList move selector configs.
 */
public interface SubListSelectorConfig {

    Integer getMinimumSubListSize();

    Integer getMaximumSubListSize();

}
