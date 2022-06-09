package org.optaplanner.core.config.heuristic.selector.entity;

import javax.xml.bind.annotation.XmlEnum;

import org.optaplanner.core.api.domain.entity.PlanningEntity;

/**
 * The manner of sorting {@link PlanningEntity} instances.
 */

@XmlEnum
public enum EntitySorterManner {
    NONE,
    DECREASING_DIFFICULTY,
    DECREASING_DIFFICULTY_IF_AVAILABLE;
}
