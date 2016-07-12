/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
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
}
