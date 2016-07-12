/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.optaplanner.core.config.constructionheuristic;

import org.optaplanner.core.config.constructionheuristic.placer.EntityPlacerConfig;
import org.optaplanner.core.config.constructionheuristic.placer.PooledEntityPlacerConfig;
import org.optaplanner.core.config.constructionheuristic.placer.QueuedEntityPlacerConfig;
import org.optaplanner.core.config.constructionheuristic.placer.QueuedValuePlacerConfig;
import org.optaplanner.core.config.heuristic.selector.entity.EntitySorterManner;
import org.optaplanner.core.config.heuristic.selector.value.ValueSorterManner;

import static org.optaplanner.core.config.constructionheuristic.ConstructionHeuristicType.*;

public final class ConstructionHeuristicTypeHelper {

    private ConstructionHeuristicTypeHelper() {
    }

    public static EntitySorterManner getDefaultEntitySorterManner(ConstructionHeuristicType constructionHeuristicType) {
        switch (constructionHeuristicType) {
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
                throw new IllegalStateException("The constructionHeuristicType (" + constructionHeuristicType + ") is not implemented.");
        }
    }

    public static ValueSorterManner getDefaultValueSorterManner(ConstructionHeuristicType constructionHeuristicType) {
        switch (constructionHeuristicType) {
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
                throw new IllegalStateException("The constructionHeuristicType (" + constructionHeuristicType + ") is not implemented.");
        }
    }

    public static EntityPlacerConfig newEntityPlacerConfig(ConstructionHeuristicType constructionHeuristicType) {
        switch (constructionHeuristicType) {
            case FIRST_FIT:
            case FIRST_FIT_DECREASING:
            case WEAKEST_FIT:
            case WEAKEST_FIT_DECREASING:
            case STRONGEST_FIT:
            case STRONGEST_FIT_DECREASING:
            case ALLOCATE_ENTITY_FROM_QUEUE:
                return new QueuedEntityPlacerConfig();
            case ALLOCATE_TO_VALUE_FROM_QUEUE:
                return new QueuedValuePlacerConfig();
            case CHEAPEST_INSERTION:
            case ALLOCATE_FROM_POOL:
                return new PooledEntityPlacerConfig();
            default:
                throw new IllegalStateException("The constructionHeuristicType (" + constructionHeuristicType + ") is not implemented.");
        }
    }

    /**
     * @return {@link ConstructionHeuristicType#values()} without duplicates (abstract types that end up behaving as one of the other types).
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
