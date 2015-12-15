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

package org.optaplanner.core.config.heuristic.selector.value;

import org.optaplanner.core.api.domain.variable.PlanningVariable;
import org.optaplanner.core.impl.domain.variable.descriptor.GenuineVariableDescriptor;
import org.optaplanner.core.impl.heuristic.selector.common.decorator.SelectionSorter;

/**
 * The manner of sorting a values for a {@link PlanningVariable}.
 */
public enum ValueSorterManner {
    NONE,
    INCREASING_STRENGTH,
    INCREASING_STRENGTH_IF_AVAILABLE,
    DECREASING_STRENGTH,
    DECREASING_STRENGTH_IF_AVAILABLE;

    public boolean hasSorter(GenuineVariableDescriptor variableDescriptor) {
        switch (this) {
            case NONE:
                return false;
            case INCREASING_STRENGTH:
            case DECREASING_STRENGTH:
                return true;
            case INCREASING_STRENGTH_IF_AVAILABLE:
                return variableDescriptor.getIncreasingStrengthSorter() != null;
            case DECREASING_STRENGTH_IF_AVAILABLE:
                return variableDescriptor.getDecreasingStrengthSorter() != null;
            default:
                throw new IllegalStateException("The sorterManner ("
                        + this + ") is not implemented.");
        }
    }

    public SelectionSorter determineSorter(GenuineVariableDescriptor variableDescriptor) {
        SelectionSorter sorter;
        switch (this) {
            case NONE:
                throw new IllegalStateException("Impossible state: hasSorter() should have returned null.");
            case INCREASING_STRENGTH:
            case INCREASING_STRENGTH_IF_AVAILABLE:
                sorter = variableDescriptor.getIncreasingStrengthSorter();
                break;
            case DECREASING_STRENGTH:
            case DECREASING_STRENGTH_IF_AVAILABLE:
                sorter = variableDescriptor.getDecreasingStrengthSorter();
                break;
            default:
                throw new IllegalStateException("The sorterManner ("
                        + this + ") is not implemented.");
        }
        if (sorter == null) {
            throw new IllegalArgumentException("The sorterManner (" + this
                    + ") on entity class (" + variableDescriptor.getEntityDescriptor().getEntityClass()
                    + ")'s variable (" + variableDescriptor.getVariableName()
                    + ") fails because that variable getter's " + PlanningVariable.class.getSimpleName()
                    + " annotation does not declare any strength comparison.");
        }
        return sorter;
    }

}
