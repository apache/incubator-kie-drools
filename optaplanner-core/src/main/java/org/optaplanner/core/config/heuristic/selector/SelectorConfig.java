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

import org.optaplanner.core.api.domain.variable.PlanningVariable;
import org.optaplanner.core.config.heuristic.selector.common.SelectionOrder;
import org.optaplanner.core.config.heuristic.selector.entity.EntitySelectorConfig;
import org.optaplanner.core.config.heuristic.selector.move.MoveSelectorConfig;
import org.optaplanner.core.config.heuristic.selector.value.ValueSelectorConfig;
import org.optaplanner.core.impl.domain.entity.descriptor.EntityDescriptor;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.core.impl.domain.variable.descriptor.GenuineVariableDescriptor;
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

    protected EntityDescriptor deduceEntityDescriptor(SolutionDescriptor solutionDescriptor,
            Class<?> entityClass) {
        EntityDescriptor entityDescriptor;
        if (entityClass != null) {
            entityDescriptor = solutionDescriptor.getEntityDescriptorStrict(entityClass);
            if (entityDescriptor == null) {
                throw new IllegalArgumentException("The selectorConfig (" + this
                        + ") has an entityClass (" + entityClass + ") that is not a known planning entity.\n"
                        + "Check your solver configuration. If that class (" + entityClass.getSimpleName()
                        + ") is not in the planningEntityClassSet (" + solutionDescriptor.getEntityClassSet()
                        + "), check your Solution implementation's annotated methods too.");
            }
        } else {
            Collection<EntityDescriptor> entityDescriptors = solutionDescriptor.getGenuineEntityDescriptors();
            if (entityDescriptors.size() != 1) {
                throw new IllegalArgumentException("The selectorConfig (" + this
                        + ") has no entityClass (" + entityClass
                        + ") configured and because there are multiple in the planningEntityClassSet ("
                        + solutionDescriptor.getEntityClassSet()
                        + "), it can not be deducted automatically.");
            }
            entityDescriptor = entityDescriptors.iterator().next();
        }
        return entityDescriptor;
    }

    protected GenuineVariableDescriptor deduceVariableDescriptor(
            EntityDescriptor entityDescriptor, String variableName) {
        GenuineVariableDescriptor variableDescriptor;
        if (variableName != null) {
            variableDescriptor = entityDescriptor.getVariableDescriptor(variableName);
            if (variableDescriptor == null) {
                if (entityDescriptor.getPropertyDescriptor(variableName) == null) {
                    throw new IllegalArgumentException("The selectorConfig (" + this
                            + ") has a variableName (" + variableName
                            + ") for planningEntityClass (" + entityDescriptor.getEntityClass()
                            + ") that does not have that as a getter.\n"
                            + "Check the spelling of the variableName (" + variableName + ").");
                } else {
                    throw new IllegalArgumentException("The selectorConfig (" + this
                            + ") has a variableName (" + variableName
                            + ") for planningEntityClass (" + entityDescriptor.getEntityClass()
                            + ") that is not annotated as a planning variable.\n"
                            + "Check if your planning entity's getter has the annotation "
                            + PlanningVariable.class.getSimpleName() + ".");
                }
            }
        } else {
            Collection<GenuineVariableDescriptor> variableDescriptors = entityDescriptor
                    .getVariableDescriptors();
            if (variableDescriptors.size() != 1) {
                throw new IllegalArgumentException("The selectorConfig (" + this
                        + ") has no configured variableName (" + variableName
                        + ") for planningEntityClass (" + entityDescriptor.getEntityClass()
                        + ") and because there are multiple in the variableNameSet ("
                        + entityDescriptor.getPlanningVariableNameSet()
                        + "), it can not be deducted automatically.");
            }
            variableDescriptor = variableDescriptors.iterator().next();
        }
        return variableDescriptor;
    }

    protected Collection<GenuineVariableDescriptor> deduceVariableDescriptors(
            EntityDescriptor entityDescriptor, List<String> variableNameIncludeList) {
        Collection<GenuineVariableDescriptor> variableDescriptors = entityDescriptor.getVariableDescriptors();
        if (variableNameIncludeList == null) {
            return variableDescriptors;
        }
        List<GenuineVariableDescriptor> resolvedVariableDescriptors
                = new ArrayList<GenuineVariableDescriptor>(variableDescriptors.size());
        for (String variableNameInclude : variableNameIncludeList) {
            boolean found = false;
            for (GenuineVariableDescriptor variableDescriptor : variableDescriptors) {
                if (variableDescriptor.getVariableName().equals(variableNameInclude)) {
                    resolvedVariableDescriptors.add(variableDescriptor);
                    found = true;
                    break;
                }
            }
            if (!found) {
                throw new IllegalStateException("The selectorConfig (" + this
                        + ") has a variableNameInclude (" + variableNameInclude
                        + ") which does not exist in the entity (" + entityDescriptor.getEntityClass()
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
