/*
 * Copyright 2012 JBoss Inc
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

package org.optaplanner.core.config.heuristic.selector;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.optaplanner.core.config.heuristic.selector.common.SelectionOrder;
import org.optaplanner.core.config.heuristic.selector.entity.EntitySelectorConfig;
import org.optaplanner.core.config.heuristic.selector.move.MoveSelectorConfig;
import org.optaplanner.core.config.heuristic.selector.value.ValueSelectorConfig;
import org.optaplanner.core.impl.domain.entity.PlanningEntityDescriptor;
import org.optaplanner.core.impl.domain.solution.SolutionDescriptor;
import org.optaplanner.core.impl.domain.variable.PlanningVariableDescriptor;
import org.optaplanner.core.impl.heuristic.selector.common.SelectionCacheType;

/**
 * General superclass for {@link MoveSelectorConfig}, {@link EntitySelectorConfig} and {@link ValueSelectorConfig}.
 */
public abstract class SelectorConfig {

    // ************************************************************************
    // Helper methods
    // ************************************************************************

    protected void validateCacheTypeVersusSelectionOrder(
            SelectionCacheType resolvedCacheType, SelectionOrder resolvedSelectionOrder) {
        switch (resolvedSelectionOrder) {
            case INHERIT:
                throw new IllegalArgumentException("The moveSelectorConfig (" + this
                        + ") has a resolvedSelectionOrder (" + resolvedSelectionOrder
                        + ") which should have been resolved by now.");
            case ORIGINAL:
            case RANDOM:
                break;
            case SORTED:
            case SHUFFLED:
            case PROBABILISTIC:
                if (resolvedCacheType.isNotCached()) {
                    throw new IllegalArgumentException("The moveSelectorConfig (" + this
                            + ") has a resolvedSelectionOrder (" + resolvedSelectionOrder
                            + ") which does not support the resolvedCacheType (" + resolvedCacheType + ").");
                }
                break;
            default:
                throw new IllegalStateException("The resolvedSelectionOrder (" + resolvedSelectionOrder
                        + ") is not implemented.");
        }
    }

    protected PlanningEntityDescriptor deduceEntityDescriptor(SolutionDescriptor solutionDescriptor,
            Class<?> entityClass) {
        PlanningEntityDescriptor entityDescriptor;
        if (entityClass != null) {
            entityDescriptor = solutionDescriptor.getPlanningEntityDescriptorStrict(entityClass);
            if (entityDescriptor == null) {
                throw new IllegalArgumentException("The selectorConfig (" + this
                        + ") has an entityClass (" + entityClass + ") that is not a known planning entity.\n"
                        + "Check your solver configuration. If that class (" + entityClass.getSimpleName()
                        + ") is not in the planningEntityClassSet (" + solutionDescriptor.getPlanningEntityClassSet()
                        + "), check your Solution implementation's annotated methods too.");
            }
        } else {
            Collection<PlanningEntityDescriptor> entityDescriptors = solutionDescriptor.getPlanningEntityDescriptors();
            if (entityDescriptors.size() != 1) {
                throw new IllegalArgumentException("The selectorConfig (" + this
                        + ") has no entityClass (" + entityClass
                        + ") configured and because there are multiple in the planningEntityClassSet ("
                        + solutionDescriptor.getPlanningEntityClassSet()
                        + "), it can not be deducted automatically.");
            }
            entityDescriptor = entityDescriptors.iterator().next();
        }
        return entityDescriptor;
    }

    protected PlanningVariableDescriptor deduceVariableDescriptor(
            PlanningEntityDescriptor entityDescriptor, String variableName) {
        PlanningVariableDescriptor variableDescriptor;
        if (variableName != null) {
            variableDescriptor = entityDescriptor.getPlanningVariableDescriptor(variableName);
            if (variableDescriptor == null) {
                throw new IllegalArgumentException("The selectorConfig (" + this
                        + ") has a variableName (" + variableName
                        + ") for planningEntityClass (" + entityDescriptor.getPlanningEntityClass()
                        + ") that is not annotated as a planning variable.\n" +
                        "Check your planning entity implementation's annotated methods.");
            }
        } else {
            Collection<PlanningVariableDescriptor> planningVariableDescriptors = entityDescriptor
                    .getPlanningVariableDescriptors();
            if (planningVariableDescriptors.size() != 1) {
                throw new IllegalArgumentException("The selectorConfig (" + this
                        + ") has no configured variableName (" + variableName
                        + ") for planningEntityClass (" + entityDescriptor.getPlanningEntityClass()
                        + ") and because there are multiple in the variableNameSet ("
                        + entityDescriptor.getPlanningVariableNameSet()
                        + "), it can not be deducted automatically.");
            }
            variableDescriptor = planningVariableDescriptors.iterator().next();
        }
        return variableDescriptor;
    }

    protected Collection<PlanningVariableDescriptor> deduceVariableDescriptors(
            PlanningEntityDescriptor entityDescriptor, List<String> variableNameIncludeList) {
        Collection<PlanningVariableDescriptor> variableDescriptors = entityDescriptor.getPlanningVariableDescriptors();
        if (variableNameIncludeList == null) {
            return variableDescriptors;
        }
        List<PlanningVariableDescriptor> resolvedVariableDescriptors
                = new ArrayList<PlanningVariableDescriptor>(variableDescriptors.size());
        for (String variableNameInclude : variableNameIncludeList) {
            boolean found = false;
            for (PlanningVariableDescriptor variableDescriptor : variableDescriptors) {
                if (variableDescriptor.getVariableName().equals(variableNameInclude)) {
                    resolvedVariableDescriptors.add(variableDescriptor);
                    found = true;
                    break;
                }
            }
            if (!found) {
                throw new IllegalStateException("The selectorConfig (" + this
                        + ") has a variableNameInclude (" + variableNameInclude
                        + ") which does not exist in the entity (" + entityDescriptor.getPlanningEntityClass()
                        + ")'s variableDescriptors (" + variableDescriptors + ").");
            }
        }
        return resolvedVariableDescriptors;
    }

    // ************************************************************************
    // Builder methods
    // ************************************************************************

    protected void inherit(SelectorConfig inheritedConfig) {
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "()";
    }

}
