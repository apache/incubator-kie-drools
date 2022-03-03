/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.core.impl;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.config.AbstractConfig;
import org.optaplanner.core.config.heuristic.selector.common.SelectionCacheType;
import org.optaplanner.core.config.heuristic.selector.common.SelectionOrder;
import org.optaplanner.core.config.heuristic.selector.entity.EntitySelectorConfig;
import org.optaplanner.core.impl.domain.entity.descriptor.EntityDescriptor;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.core.impl.domain.variable.descriptor.GenuineVariableDescriptor;
import org.optaplanner.core.impl.heuristic.HeuristicConfigPolicy;

public abstract class AbstractFromConfigFactory<Solution_, Config_ extends AbstractConfig<Config_>> {

    protected final Config_ config;

    public AbstractFromConfigFactory(Config_ config) {
        this.config = config;
    }

    public static <Solution_> EntitySelectorConfig getDefaultEntitySelectorConfigForEntity(
            HeuristicConfigPolicy<Solution_> configPolicy, EntityDescriptor<Solution_> entityDescriptor) {
        EntitySelectorConfig entitySelectorConfig = new EntitySelectorConfig();
        Class<?> entityClass = entityDescriptor.getEntityClass();
        entitySelectorConfig.setId(entityClass.getName());
        entitySelectorConfig.setEntityClass(entityClass);
        if (EntitySelectorConfig.hasSorter(configPolicy.getEntitySorterManner(), entityDescriptor)) {
            entitySelectorConfig.setCacheType(SelectionCacheType.PHASE);
            entitySelectorConfig.setSelectionOrder(SelectionOrder.SORTED);
            entitySelectorConfig.setSorterManner(configPolicy.getEntitySorterManner());
        }
        return entitySelectorConfig;
    }

    protected EntityDescriptor<Solution_> deduceEntityDescriptor(SolutionDescriptor<Solution_> solutionDescriptor,
            Class<?> entityClass) {
        EntityDescriptor<Solution_> entityDescriptor =
                solutionDescriptor.getEntityDescriptorStrict(Objects.requireNonNull(entityClass));
        if (entityDescriptor == null) {
            throw new IllegalArgumentException("The config (" + config
                    + ") has an entityClass (" + entityClass + ") that is not a known planning entity.\n"
                    + "Check your solver configuration. If that class (" + entityClass.getSimpleName()
                    + ") is not in the entityClassSet (" + solutionDescriptor.getEntityClassSet()
                    + "), check your @" + PlanningSolution.class.getSimpleName()
                    + " implementation's annotated methods too.");
        }
        return entityDescriptor;
    }

    protected EntityDescriptor<Solution_> deduceEntityDescriptor(SolutionDescriptor<Solution_> solutionDescriptor) {
        Collection<EntityDescriptor<Solution_>> entityDescriptors = solutionDescriptor.getGenuineEntityDescriptors();
        if (entityDescriptors.size() != 1) {
            throw new IllegalArgumentException("The config (" + config
                    + ") has no entityClass configured and because there are multiple in the entityClassSet ("
                    + solutionDescriptor.getEntityClassSet()
                    + "), it cannot be deduced automatically.");
        }
        return entityDescriptors.iterator().next();
    }

    protected GenuineVariableDescriptor<Solution_> deduceVariableDescriptor(
            EntityDescriptor<Solution_> entityDescriptor, String variableName) {
        GenuineVariableDescriptor<Solution_> variableDescriptor =
                entityDescriptor.getGenuineVariableDescriptor(Objects.requireNonNull(variableName));
        if (variableDescriptor == null) {
            throw new IllegalArgumentException("The config (" + config
                    + ") has a variableName (" + variableName
                    + ") which is not a valid planning variable on entityClass ("
                    + entityDescriptor.getEntityClass() + ").\n"
                    + entityDescriptor.buildInvalidVariableNameExceptionMessage(variableName));
        }
        return variableDescriptor;
    }

    protected GenuineVariableDescriptor<Solution_> deduceVariableDescriptor(
            EntityDescriptor<Solution_> entityDescriptor) {
        List<GenuineVariableDescriptor<Solution_>> variableDescriptorList =
                entityDescriptor.getGenuineVariableDescriptorList();
        if (variableDescriptorList.size() != 1) {
            throw new IllegalArgumentException("The config (" + config
                    + ") has no configured variableName for entityClass (" + entityDescriptor.getEntityClass()
                    + ") and because there are multiple variableNames ("
                    + entityDescriptor.getGenuineVariableNameSet()
                    + "), it cannot be deduced automatically.");
        }
        return variableDescriptorList.iterator().next();
    }

    protected List<GenuineVariableDescriptor<Solution_>> deduceVariableDescriptorList(
            EntityDescriptor<Solution_> entityDescriptor, List<String> variableNameIncludeList) {
        Objects.requireNonNull(entityDescriptor);
        List<GenuineVariableDescriptor<Solution_>> variableDescriptorList =
                entityDescriptor.getGenuineVariableDescriptorList();
        if (variableNameIncludeList == null) {
            return variableDescriptorList;
        }

        return variableNameIncludeList.stream()
                .map(variableNameInclude -> variableDescriptorList.stream()
                        .filter(variableDescriptor -> variableDescriptor.getVariableName().equals(variableNameInclude))
                        .findFirst()
                        .orElseThrow(() -> new IllegalArgumentException("The config (" + config
                                + ") has a variableNameInclude (" + variableNameInclude
                                + ") which does not exist in the entity (" + entityDescriptor.getEntityClass()
                                + ")'s variableDescriptorList (" + variableDescriptorList + ").")))
                .collect(Collectors.toList());
    }
}
