/*
 * Copyright 2014 JBoss Inc
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
import org.optaplanner.core.config.heuristic.selector.entity.EntitySorterManner;
import org.optaplanner.core.config.heuristic.selector.value.ValueSorterManner;

public enum ConstructionHeuristicType {
    FIRST_FIT,
    FIRST_FIT_DECREASING,
    BEST_FIT,
    BEST_FIT_DECREASING,
    CHEAPEST_INSERTION;

    public EntitySorterManner getDefaultEntitySorterManner() {
        switch (this) {
            case FIRST_FIT:
            case BEST_FIT:
                return EntitySorterManner.NONE;
            case FIRST_FIT_DECREASING:
            case BEST_FIT_DECREASING:
                return EntitySorterManner.DECREASING_DIFFICULTY;
            case CHEAPEST_INSERTION:
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
            case BEST_FIT:
            case BEST_FIT_DECREASING:
                return ValueSorterManner.INCREASING_STRENGTH;
            case CHEAPEST_INSERTION:
                return ValueSorterManner.INCREASING_STRENGTH_IF_AVAILABLE;
            default:
                throw new IllegalStateException("The constructionHeuristicType (" + this + ") is not implemented.");
        }
    }

    public EntityPlacerConfig newEntityPlacerConfig() {
        switch (this) {
            case FIRST_FIT:
            case FIRST_FIT_DECREASING:
            case BEST_FIT:
            case BEST_FIT_DECREASING:
                return new QueuedEntityPlacerConfig();
            case CHEAPEST_INSERTION:
                return new PooledEntityPlacerConfig();
            default:
                throw new IllegalStateException("The constructionHeuristicType (" + this + ") is not implemented.");
        }
    }
}
