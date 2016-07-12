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

package org.optaplanner.core.config.heuristic.selector.entity;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.impl.domain.entity.descriptor.EntityDescriptor;
import org.optaplanner.core.impl.heuristic.selector.common.decorator.SelectionSorter;

public final class EntitySorterMannerHelper {

    private EntitySorterMannerHelper() {
    }

    public static boolean hasSorter(EntitySorterManner entitySorterManner, EntityDescriptor entityDescriptor) {
        switch (entitySorterManner) {
            case NONE:
                return false;
            case DECREASING_DIFFICULTY:
                return true;
            case DECREASING_DIFFICULTY_IF_AVAILABLE:
                return entityDescriptor.getDecreasingDifficultySorter() != null;
            default:
                throw new IllegalStateException("The sorterManner ("
                        + entitySorterManner + ") is not implemented.");
        }
    }

    public static SelectionSorter determineSorter(EntitySorterManner entitySorterManner, EntityDescriptor entityDescriptor) {
        SelectionSorter sorter;
        switch (entitySorterManner) {
            case NONE:
                throw new IllegalStateException("Impossible state: hasSorter() should have returned null.");
            case DECREASING_DIFFICULTY:
            case DECREASING_DIFFICULTY_IF_AVAILABLE:
                sorter = entityDescriptor.getDecreasingDifficultySorter();
                if (sorter == null) {
                    throw new IllegalArgumentException("The sorterManner (" + entitySorterManner
                            + ") on entity class (" + entityDescriptor.getEntityClass()
                            + ") fails because that entity class's @" + PlanningEntity.class.getSimpleName()
                            + " annotation does not declare any difficulty comparison.");
                }
                return sorter;
            default:
                throw new IllegalStateException("The sorterManner ("
                        + entitySorterManner + ") is not implemented.");
        }
    }

}
