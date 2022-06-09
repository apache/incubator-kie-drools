package org.optaplanner.core.config.constructionheuristic;

import javax.xml.bind.annotation.XmlEnum;

import org.optaplanner.core.config.heuristic.selector.entity.EntitySorterManner;
import org.optaplanner.core.config.heuristic.selector.value.ValueSorterManner;

@XmlEnum
public enum ConstructionHeuristicType {
    /**
     * A specific form of {@link #ALLOCATE_ENTITY_FROM_QUEUE}.
     */
    FIRST_FIT,
    /**
     * A specific form of {@link #ALLOCATE_ENTITY_FROM_QUEUE}.
     */
    FIRST_FIT_DECREASING,
    /**
     * A specific form of {@link #ALLOCATE_ENTITY_FROM_QUEUE}.
     */
    WEAKEST_FIT,
    /**
     * A specific form of {@link #ALLOCATE_ENTITY_FROM_QUEUE}.
     */
    WEAKEST_FIT_DECREASING,
    /**
     * A specific form of {@link #ALLOCATE_ENTITY_FROM_QUEUE}.
     */
    STRONGEST_FIT,
    /**
     * A specific form of {@link #ALLOCATE_ENTITY_FROM_QUEUE}.
     */
    STRONGEST_FIT_DECREASING,
    /**
     * Put all entities in a queue.
     * Assign the first entity (from that queue) to the best value.
     * Repeat until all entities are assigned.
     */
    ALLOCATE_ENTITY_FROM_QUEUE,
    /**
     * Put all values in a round-robin queue.
     * Assign the best entity to the first value (from that queue).
     * Repeat until all entities are assigned.
     */
    ALLOCATE_TO_VALUE_FROM_QUEUE,
    /**
     * A specific form of {@link #ALLOCATE_FROM_POOL}.
     */
    CHEAPEST_INSERTION,
    /**
     * Put all entity-value combinations in a pool.
     * Assign the best entity to best value.
     * Repeat until all entities are assigned.
     */
    ALLOCATE_FROM_POOL;

    public EntitySorterManner getDefaultEntitySorterManner() {
        switch (this) {
            case FIRST_FIT:
            case WEAKEST_FIT:
            case STRONGEST_FIT:
                return EntitySorterManner.NONE;
            case FIRST_FIT_DECREASING:
            case WEAKEST_FIT_DECREASING:
            case STRONGEST_FIT_DECREASING:
                return EntitySorterManner.DECREASING_DIFFICULTY;
            case ALLOCATE_ENTITY_FROM_QUEUE:
            case ALLOCATE_TO_VALUE_FROM_QUEUE:
            case CHEAPEST_INSERTION:
            case ALLOCATE_FROM_POOL:
                return EntitySorterManner.DECREASING_DIFFICULTY_IF_AVAILABLE;
            default:
                throw new IllegalStateException("The constructionHeuristicType (" + this + ") is not implemented.");
        }
    }

    public ValueSorterManner getDefaultValueSorterManner() {
        switch (this) {
            case FIRST_FIT:
            case FIRST_FIT_DECREASING:
                return ValueSorterManner.NONE;
            case WEAKEST_FIT:
            case WEAKEST_FIT_DECREASING:
                return ValueSorterManner.INCREASING_STRENGTH;
            case STRONGEST_FIT:
            case STRONGEST_FIT_DECREASING:
                return ValueSorterManner.DECREASING_STRENGTH;
            case ALLOCATE_ENTITY_FROM_QUEUE:
            case ALLOCATE_TO_VALUE_FROM_QUEUE:
            case CHEAPEST_INSERTION:
            case ALLOCATE_FROM_POOL:
                return ValueSorterManner.INCREASING_STRENGTH_IF_AVAILABLE;
            default:
                throw new IllegalStateException("The constructionHeuristicType (" + this + ") is not implemented.");
        }
    }

    /**
     * @return {@link ConstructionHeuristicType#values()} without duplicates (abstract types that end up behaving as one of the
     *         other types).
     */
    public static ConstructionHeuristicType[] getBluePrintTypes() {
        return new ConstructionHeuristicType[] {
                FIRST_FIT,
                FIRST_FIT_DECREASING,
                WEAKEST_FIT,
                WEAKEST_FIT_DECREASING,
                STRONGEST_FIT,
                STRONGEST_FIT_DECREASING,
                CHEAPEST_INSERTION
        };
    }

}
