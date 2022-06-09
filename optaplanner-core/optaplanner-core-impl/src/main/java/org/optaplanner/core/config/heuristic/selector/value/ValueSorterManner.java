package org.optaplanner.core.config.heuristic.selector.value;

import javax.xml.bind.annotation.XmlEnum;

import org.optaplanner.core.api.domain.variable.PlanningVariable;

/**
 * The manner of sorting a values for a {@link PlanningVariable}.
 */

@XmlEnum
public enum ValueSorterManner {
    NONE,
    INCREASING_STRENGTH,
    INCREASING_STRENGTH_IF_AVAILABLE,
    DECREASING_STRENGTH,
    DECREASING_STRENGTH_IF_AVAILABLE;
}
