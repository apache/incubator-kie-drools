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

    protected EntityDescriptor<Solution_> deduceEntityDescriptor(HeuristicConfigPolicy<Solution_> configPolicy,
            Class<?> entityClass) {
        SolutionDescriptor<Solution_> solutionDescriptor = configPolicy.getSolutionDescriptor();
        return entityClass == null
                ? getTheOnlyEntityDescriptor(solutionDescriptor)
                : getEntityDescriptorForClass(solutionDescriptor, entityClass);
    }

    private EntityDescriptor<Solution_> getEntityDescriptorForClass(SolutionDescriptor<Solution_> solutionDescriptor,
            Class<?> entityClass) {
        EntityDescriptor<Solution_> entityDescriptor = solutionDescriptor.getEntityDescriptorStrict(entityClass);
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

    protected EntityDescriptor<Solution_> getTheOnlyEntityDescriptor(SolutionDescriptor<Solution_> solutionDescriptor) {
        Collection<EntityDescriptor<Solution_>> entityDescriptors = solutionDescriptor.getGenuineEntityDescriptors();
        if (entityDescriptors.size() != 1) {
            throw new IllegalArgumentException("The config (" + config
                    + ") has no entityClass configured and because there are multiple in the entityClassSet ("
                    + solutionDescriptor.getEntityClassSet()
                    + "), it cannot be deduced automatically.");
        }
        return entityDescriptors.iterator().next();
    }

    protected GenuineVariableDescriptor<Solution_> deduceGenuineVariableDescriptor(EntityDescriptor<Solution_> entityDescriptor,
            String variableName) {
        return variableName == null
                ? getTheOnlyVariableDescriptor(entityDescriptor)
                : getVariableDescriptorForName(entityDescriptor, variableName);
    }

    protected GenuineVariableDescriptor<Solution_> getVariableDescriptorForName(EntityDescriptor<Solution_> entityDescriptor,
            String variableName) {
        GenuineVariableDescriptor<Solution_> variableDescriptor = entityDescriptor.getGenuineVariableDescriptor(variableName);
        if (variableDescriptor == null) {
            throw new IllegalArgumentException("The config (" + config
                    + ") has a variableName (" + variableName
                    + ") which is not a valid planning variable on entityClass ("
                    + entityDescriptor.getEntityClass() + ").\n"
                    + entityDescriptor.buildInvalidVariableNameExceptionMessage(variableName));
        }
        return variableDescriptor;
    }

    protected GenuineVariableDescriptor<Solution_> getTheOnlyVariableDescriptor(EntityDescriptor<Solution_> entityDescriptor) {
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
