package org.optaplanner.core.impl.heuristic.selector.value;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;
import org.optaplanner.core.config.heuristic.selector.common.SelectionCacheType;
import org.optaplanner.core.config.heuristic.selector.common.SelectionOrder;
import org.optaplanner.core.config.heuristic.selector.common.decorator.SelectionSorterOrder;
import org.optaplanner.core.config.heuristic.selector.common.nearby.NearbySelectionConfig;
import org.optaplanner.core.config.heuristic.selector.value.ValueSelectorConfig;
import org.optaplanner.core.config.util.ConfigUtils;
import org.optaplanner.core.impl.domain.entity.descriptor.EntityDescriptor;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.core.impl.domain.valuerange.descriptor.EntityIndependentValueRangeDescriptor;
import org.optaplanner.core.impl.domain.valuerange.descriptor.ValueRangeDescriptor;
import org.optaplanner.core.impl.domain.variable.descriptor.GenuineVariableDescriptor;
import org.optaplanner.core.impl.heuristic.HeuristicConfigPolicy;
import org.optaplanner.core.impl.heuristic.selector.AbstractSelectorFactory;
import org.optaplanner.core.impl.heuristic.selector.common.decorator.ComparatorSelectionSorter;
import org.optaplanner.core.impl.heuristic.selector.common.decorator.SelectionFilter;
import org.optaplanner.core.impl.heuristic.selector.common.decorator.SelectionProbabilityWeightFactory;
import org.optaplanner.core.impl.heuristic.selector.common.decorator.SelectionSorter;
import org.optaplanner.core.impl.heuristic.selector.common.decorator.SelectionSorterWeightFactory;
import org.optaplanner.core.impl.heuristic.selector.common.decorator.WeightFactorySelectionSorter;
import org.optaplanner.core.impl.heuristic.selector.common.nearby.NearbyDistanceMeter;
import org.optaplanner.core.impl.heuristic.selector.common.nearby.NearbyRandom;
import org.optaplanner.core.impl.heuristic.selector.common.nearby.NearbyRandomFactory;
import org.optaplanner.core.impl.heuristic.selector.entity.EntitySelector;
import org.optaplanner.core.impl.heuristic.selector.entity.EntitySelectorFactory;
import org.optaplanner.core.impl.heuristic.selector.value.decorator.CachingValueSelector;
import org.optaplanner.core.impl.heuristic.selector.value.decorator.DowncastingValueSelector;
import org.optaplanner.core.impl.heuristic.selector.value.decorator.EntityDependentSortingValueSelector;
import org.optaplanner.core.impl.heuristic.selector.value.decorator.FilteringValueSelector;
import org.optaplanner.core.impl.heuristic.selector.value.decorator.InitializedValueSelector;
import org.optaplanner.core.impl.heuristic.selector.value.decorator.ProbabilityValueSelector;
import org.optaplanner.core.impl.heuristic.selector.value.decorator.ReinitializeVariableValueSelector;
import org.optaplanner.core.impl.heuristic.selector.value.decorator.SelectedCountLimitValueSelector;
import org.optaplanner.core.impl.heuristic.selector.value.decorator.ShufflingValueSelector;
import org.optaplanner.core.impl.heuristic.selector.value.decorator.SortingValueSelector;
import org.optaplanner.core.impl.heuristic.selector.value.decorator.UnassignedValueSelector;
import org.optaplanner.core.impl.heuristic.selector.value.mimic.MimicRecordingValueSelector;
import org.optaplanner.core.impl.heuristic.selector.value.mimic.MimicReplayingValueSelector;
import org.optaplanner.core.impl.heuristic.selector.value.mimic.ValueMimicRecorder;
import org.optaplanner.core.impl.heuristic.selector.value.nearby.NearEntityNearbyValueSelector;

public class ValueSelectorFactory<Solution_>
        extends AbstractSelectorFactory<Solution_, ValueSelectorConfig> {

    public static <Solution_> ValueSelectorFactory<Solution_> create(ValueSelectorConfig valueSelectorConfig) {
        return new ValueSelectorFactory<>(valueSelectorConfig);
    }

    public ValueSelectorFactory(ValueSelectorConfig valueSelectorConfig) {
        super(valueSelectorConfig);
    }

    public GenuineVariableDescriptor<Solution_> extractVariableDescriptor(HeuristicConfigPolicy<Solution_> configPolicy,
            EntityDescriptor<Solution_> entityDescriptor) {
        String variableName = config.getVariableName();
        if (variableName != null) {
            return getVariableDescriptorForName(downcastEntityDescriptor(configPolicy, entityDescriptor), variableName);
        } else if (config.getMimicSelectorRef() != null) {
            return configPolicy.getValueMimicRecorder(config.getMimicSelectorRef()).getVariableDescriptor();
        } else {
            return null;
        }
    }

    /**
     *
     * @param configPolicy never null
     * @param entityDescriptor never null
     * @param minimumCacheType never null, If caching is used (different from {@link SelectionCacheType#JUST_IN_TIME}),
     *        then it should be at least this {@link SelectionCacheType} because an ancestor already uses such caching
     *        and less would be pointless.
     * @param inheritedSelectionOrder never null
     * @return never null
     */
    public ValueSelector<Solution_> buildValueSelector(HeuristicConfigPolicy<Solution_> configPolicy,
            EntityDescriptor<Solution_> entityDescriptor, SelectionCacheType minimumCacheType,
            SelectionOrder inheritedSelectionOrder) {
        return buildValueSelector(configPolicy, entityDescriptor, minimumCacheType, inheritedSelectionOrder,
                configPolicy.isReinitializeVariableFilterEnabled(), false);
    }

    public ValueSelector<Solution_> buildValueSelector(HeuristicConfigPolicy<Solution_> configPolicy,
            EntityDescriptor<Solution_> entityDescriptor, SelectionCacheType minimumCacheType,
            SelectionOrder inheritedSelectionOrder, boolean applyReinitializeVariableFiltering,
            boolean applyUnassignedValueFiltering) {
        GenuineVariableDescriptor<Solution_> variableDescriptor = deduceGenuineVariableDescriptor(
                downcastEntityDescriptor(configPolicy, entityDescriptor), config.getVariableName());
        if (config.getMimicSelectorRef() != null) {
            ValueSelector<Solution_> valueSelector = buildMimicReplaying(configPolicy);
            valueSelector =
                    applyReinitializeVariableFiltering(applyReinitializeVariableFiltering, variableDescriptor, valueSelector);
            valueSelector = applyDowncasting(valueSelector);
            return valueSelector;
        }
        SelectionCacheType resolvedCacheType = SelectionCacheType.resolve(config.getCacheType(), minimumCacheType);
        SelectionOrder resolvedSelectionOrder = SelectionOrder.resolve(config.getSelectionOrder(), inheritedSelectionOrder);

        if (config.getNearbySelectionConfig() != null) {
            config.getNearbySelectionConfig().validateNearby(resolvedCacheType, resolvedSelectionOrder);
        }
        validateCacheTypeVersusSelectionOrder(resolvedCacheType, resolvedSelectionOrder);
        validateSorting(resolvedSelectionOrder);
        validateProbability(resolvedSelectionOrder);
        validateSelectedLimit(minimumCacheType);

        // baseValueSelector and lower should be SelectionOrder.ORIGINAL if they are going to get cached completely
        ValueSelector<Solution_> valueSelector =
                buildBaseValueSelector(variableDescriptor, SelectionCacheType.max(minimumCacheType, resolvedCacheType),
                        determineBaseRandomSelection(variableDescriptor, resolvedCacheType, resolvedSelectionOrder));

        if (config.getNearbySelectionConfig() != null) {
            // TODO Static filtering (such as movableEntitySelectionFilter) should affect nearbySelection too
            valueSelector = applyNearbySelection(configPolicy, config.getNearbySelectionConfig(), minimumCacheType,
                    resolvedSelectionOrder, valueSelector);
        }
        valueSelector = applyFiltering(valueSelector);
        valueSelector = applyInitializedChainedValueFilter(configPolicy, variableDescriptor, valueSelector);
        valueSelector = applySorting(resolvedCacheType, resolvedSelectionOrder, valueSelector);
        valueSelector = applyProbability(resolvedCacheType, resolvedSelectionOrder, valueSelector);
        valueSelector = applyShuffling(resolvedCacheType, resolvedSelectionOrder, valueSelector);
        valueSelector = applyCaching(resolvedCacheType, resolvedSelectionOrder, valueSelector);
        valueSelector = applySelectedLimit(valueSelector);
        valueSelector = applyUnassignedValueFiltering(applyUnassignedValueFiltering, variableDescriptor, valueSelector);
        valueSelector = applyMimicRecording(configPolicy, valueSelector);
        valueSelector =
                applyReinitializeVariableFiltering(applyReinitializeVariableFiltering, variableDescriptor, valueSelector);
        valueSelector = applyDowncasting(valueSelector);
        return valueSelector;
    }

    protected ValueSelector<Solution_> buildMimicReplaying(HeuristicConfigPolicy<Solution_> configPolicy) {
        if (config.getId() != null
                || config.getVariableName() != null
                || config.getCacheType() != null
                || config.getSelectionOrder() != null
                || config.getNearbySelectionConfig() != null
                || config.getFilterClass() != null
                || config.getSorterManner() != null
                || config.getSorterComparatorClass() != null
                || config.getSorterWeightFactoryClass() != null
                || config.getSorterOrder() != null
                || config.getSorterClass() != null
                || config.getProbabilityWeightFactoryClass() != null
                || config.getSelectedCountLimit() != null) {
            throw new IllegalArgumentException("The valueSelectorConfig (" + config
                    + ") with mimicSelectorRef (" + config.getMimicSelectorRef()
                    + ") has another property that is not null.");
        }
        ValueMimicRecorder<Solution_> valueMimicRecorder = configPolicy.getValueMimicRecorder(config.getMimicSelectorRef());
        if (valueMimicRecorder == null) {
            throw new IllegalArgumentException("The valueSelectorConfig (" + config
                    + ") has a mimicSelectorRef (" + config.getMimicSelectorRef()
                    + ") for which no valueSelector with that id exists (in its solver phase).");
        }
        return new MimicReplayingValueSelector<>(valueMimicRecorder);
    }

    protected EntityDescriptor<Solution_> downcastEntityDescriptor(HeuristicConfigPolicy<Solution_> configPolicy,
            EntityDescriptor<Solution_> entityDescriptor) {
        if (config.getDowncastEntityClass() != null) {
            Class<?> parentEntityClass = entityDescriptor.getEntityClass();
            if (!parentEntityClass.isAssignableFrom(config.getDowncastEntityClass())) {
                throw new IllegalStateException("The downcastEntityClass (" + config.getDowncastEntityClass()
                        + ") is not a subclass of the parentEntityClass (" + parentEntityClass
                        + ") configured by the " + EntitySelector.class.getSimpleName() + ".");
            }
            SolutionDescriptor<Solution_> solutionDescriptor = configPolicy.getSolutionDescriptor();
            entityDescriptor = solutionDescriptor.getEntityDescriptorStrict(config.getDowncastEntityClass());
            if (entityDescriptor == null) {
                throw new IllegalArgumentException("The selectorConfig (" + config
                        + ") has an downcastEntityClass (" + config.getDowncastEntityClass()
                        + ") that is not a known planning entity.\n"
                        + "Check your solver configuration. If that class (" + config.getDowncastEntityClass().getSimpleName()
                        + ") is not in the entityClassSet (" + solutionDescriptor.getEntityClassSet()
                        + "), check your @" + PlanningSolution.class.getSimpleName()
                        + " implementation's annotated methods too.");
            }
        }
        return entityDescriptor;
    }

    protected boolean determineBaseRandomSelection(GenuineVariableDescriptor<Solution_> variableDescriptor,
            SelectionCacheType resolvedCacheType, SelectionOrder resolvedSelectionOrder) {
        switch (resolvedSelectionOrder) {
            case ORIGINAL:
                return false;
            case SORTED:
            case SHUFFLED:
            case PROBABILISTIC:
                // baseValueSelector and lower should be ORIGINAL if they are going to get cached completely
                return false;
            case RANDOM:
                // Predict if caching will occur
                return resolvedCacheType.isNotCached()
                        || (isBaseInherentlyCached(variableDescriptor) && !hasFiltering(variableDescriptor));
            default:
                throw new IllegalStateException("The selectionOrder (" + resolvedSelectionOrder
                        + ") is not implemented.");
        }
    }

    protected boolean isBaseInherentlyCached(GenuineVariableDescriptor<Solution_> variableDescriptor) {
        return variableDescriptor.isValueRangeEntityIndependent();
    }

    private ValueSelector<Solution_> buildBaseValueSelector(GenuineVariableDescriptor<Solution_> variableDescriptor,
            SelectionCacheType minimumCacheType, boolean randomSelection) {
        ValueRangeDescriptor<Solution_> valueRangeDescriptor = variableDescriptor.getValueRangeDescriptor();
        // TODO minimumCacheType SOLVER is only a problem if the valueRange includes entities or custom weird cloning
        if (minimumCacheType == SelectionCacheType.SOLVER) {
            // TODO Solver cached entities are not compatible with DroolsScoreCalculator and IncrementalScoreDirector
            // because between phases the entities get cloned and the KieSession/Maps contains those clones afterwards
            // https://issues.redhat.com/browse/PLANNER-54
            throw new IllegalArgumentException("The minimumCacheType (" + minimumCacheType
                    + ") is not yet supported. Please use " + SelectionCacheType.PHASE + " instead.");
        }
        if (valueRangeDescriptor.isEntityIndependent()) {
            return new FromSolutionPropertyValueSelector<>(
                    (EntityIndependentValueRangeDescriptor<Solution_>) valueRangeDescriptor, minimumCacheType,
                    randomSelection);
        } else {
            // TODO Do not allow PHASE cache on FromEntityPropertyValueSelector, except if the moveSelector is PHASE cached too.
            return new FromEntityPropertyValueSelector<>(valueRangeDescriptor, randomSelection);
        }
    }

    private boolean hasFiltering(GenuineVariableDescriptor<Solution_> variableDescriptor) {
        return config.getFilterClass() != null || variableDescriptor.hasMovableChainedTrailingValueFilter();
    }

    protected ValueSelector<Solution_> applyFiltering(ValueSelector<Solution_> valueSelector) {
        GenuineVariableDescriptor<Solution_> variableDescriptor = valueSelector.getVariableDescriptor();
        if (hasFiltering(variableDescriptor)) {
            List<SelectionFilter<Solution_, Object>> filterList = new ArrayList<>(config.getFilterClass() == null ? 1 : 2);
            if (config.getFilterClass() != null) {
                filterList.add(ConfigUtils.newInstance(config, "filterClass", config.getFilterClass()));
            }
            // Filter out pinned entities
            if (variableDescriptor.hasMovableChainedTrailingValueFilter()) {
                filterList.add(variableDescriptor.getMovableChainedTrailingValueFilter());
            }
            valueSelector = FilteringValueSelector.create(valueSelector, filterList);
        }
        return valueSelector;
    }

    protected ValueSelector<Solution_> applyInitializedChainedValueFilter(HeuristicConfigPolicy<Solution_> configPolicy,
            GenuineVariableDescriptor<Solution_> variableDescriptor, ValueSelector<Solution_> valueSelector) {
        if (configPolicy.isInitializedChainedValueFilterEnabled() && variableDescriptor.isChained()) {
            valueSelector = InitializedValueSelector.create(valueSelector);
        }
        return valueSelector;
    }

    protected void validateSorting(SelectionOrder resolvedSelectionOrder) {
        if ((config.getSorterManner() != null || config.getSorterComparatorClass() != null
                || config.getSorterWeightFactoryClass() != null
                || config.getSorterOrder() != null || config.getSorterClass() != null)
                && resolvedSelectionOrder != SelectionOrder.SORTED) {
            throw new IllegalArgumentException("The valueSelectorConfig (" + config
                    + ") with sorterManner (" + config.getSorterManner()
                    + ") and sorterComparatorClass (" + config.getSorterComparatorClass()
                    + ") and sorterWeightFactoryClass (" + config.getSorterWeightFactoryClass()
                    + ") and sorterOrder (" + config.getSorterOrder()
                    + ") and sorterClass (" + config.getSorterClass()
                    + ") has a resolvedSelectionOrder (" + resolvedSelectionOrder
                    + ") that is not " + SelectionOrder.SORTED + ".");
        }
        if (config.getSorterManner() != null && config.getSorterComparatorClass() != null) {
            throw new IllegalArgumentException("The valueSelectorConfig (" + config
                    + ") has both a sorterManner (" + config.getSorterManner()
                    + ") and a sorterComparatorClass (" + config.getSorterComparatorClass() + ").");
        }
        if (config.getSorterManner() != null && config.getSorterWeightFactoryClass() != null) {
            throw new IllegalArgumentException("The valueSelectorConfig (" + config
                    + ") has both a sorterManner (" + config.getSorterManner()
                    + ") and a sorterWeightFactoryClass (" + config.getSorterWeightFactoryClass() + ").");
        }
        if (config.getSorterManner() != null && config.getSorterClass() != null) {
            throw new IllegalArgumentException("The valueSelectorConfig (" + config
                    + ") has both a sorterManner (" + config.getSorterManner()
                    + ") and a sorterClass (" + config.getSorterClass() + ").");
        }
        if (config.getSorterManner() != null && config.getSorterOrder() != null) {
            throw new IllegalArgumentException("The valueSelectorConfig (" + config
                    + ") with sorterManner (" + config.getSorterManner()
                    + ") has a non-null sorterOrder (" + config.getSorterOrder() + ").");
        }
        if (config.getSorterComparatorClass() != null && config.getSorterWeightFactoryClass() != null) {
            throw new IllegalArgumentException("The valueSelectorConfig (" + config
                    + ") has both a sorterComparatorClass (" + config.getSorterComparatorClass()
                    + ") and a sorterWeightFactoryClass (" + config.getSorterWeightFactoryClass() + ").");
        }
        if (config.getSorterComparatorClass() != null && config.getSorterClass() != null) {
            throw new IllegalArgumentException("The valueSelectorConfig (" + config
                    + ") has both a sorterComparatorClass (" + config.getSorterComparatorClass()
                    + ") and a sorterClass (" + config.getSorterClass() + ").");
        }
        if (config.getSorterWeightFactoryClass() != null && config.getSorterClass() != null) {
            throw new IllegalArgumentException("The valueSelectorConfig (" + config
                    + ") has both a sorterWeightFactoryClass (" + config.getSorterWeightFactoryClass()
                    + ") and a sorterClass (" + config.getSorterClass() + ").");
        }
        if (config.getSorterClass() != null && config.getSorterOrder() != null) {
            throw new IllegalArgumentException("The valueSelectorConfig (" + config
                    + ") with sorterClass (" + config.getSorterClass()
                    + ") has a non-null sorterOrder (" + config.getSorterOrder() + ").");
        }
    }

    protected ValueSelector<Solution_> applySorting(SelectionCacheType resolvedCacheType,
            SelectionOrder resolvedSelectionOrder, ValueSelector<Solution_> valueSelector) {
        if (resolvedSelectionOrder == SelectionOrder.SORTED) {
            SelectionSorter<Solution_, Object> sorter;
            if (config.getSorterManner() != null) {
                GenuineVariableDescriptor<Solution_> variableDescriptor = valueSelector.getVariableDescriptor();
                if (!ValueSelectorConfig.hasSorter(config.getSorterManner(), variableDescriptor)) {
                    return valueSelector;
                }
                sorter = ValueSelectorConfig.determineSorter(config.getSorterManner(), variableDescriptor);
            } else if (config.getSorterComparatorClass() != null) {
                Comparator<Object> sorterComparator =
                        ConfigUtils.newInstance(config, "sorterComparatorClass", config.getSorterComparatorClass());
                sorter = new ComparatorSelectionSorter<>(sorterComparator,
                        SelectionSorterOrder.resolve(config.getSorterOrder()));
            } else if (config.getSorterWeightFactoryClass() != null) {
                SelectionSorterWeightFactory<Solution_, Object> sorterWeightFactory =
                        ConfigUtils.newInstance(config, "sorterWeightFactoryClass", config.getSorterWeightFactoryClass());
                sorter = new WeightFactorySelectionSorter<>(sorterWeightFactory,
                        SelectionSorterOrder.resolve(config.getSorterOrder()));
            } else if (config.getSorterClass() != null) {
                sorter = ConfigUtils.newInstance(config, "sorterClass", config.getSorterClass());
            } else {
                throw new IllegalArgumentException("The valueSelectorConfig (" + config
                        + ") with resolvedSelectionOrder (" + resolvedSelectionOrder
                        + ") needs a sorterManner (" + config.getSorterManner()
                        + ") or a sorterComparatorClass (" + config.getSorterComparatorClass()
                        + ") or a sorterWeightFactoryClass (" + config.getSorterWeightFactoryClass()
                        + ") or a sorterClass (" + config.getSorterClass() + ").");
            }
            if (!valueSelector.getVariableDescriptor().isValueRangeEntityIndependent()
                    && resolvedCacheType == SelectionCacheType.STEP) {
                valueSelector = new EntityDependentSortingValueSelector<>(valueSelector, resolvedCacheType, sorter);
            } else {
                if (!(valueSelector instanceof EntityIndependentValueSelector)) {
                    throw new IllegalArgumentException("The valueSelectorConfig (" + config
                            + ") with resolvedCacheType (" + resolvedCacheType
                            + ") and resolvedSelectionOrder (" + resolvedSelectionOrder
                            + ") needs to be based on an "
                            + EntityIndependentValueSelector.class.getSimpleName() + " (" + valueSelector + ")."
                            + " Check your @" + ValueRangeProvider.class.getSimpleName() + " annotations.");
                }
                valueSelector = new SortingValueSelector<>((EntityIndependentValueSelector<Solution_>) valueSelector,
                        resolvedCacheType, sorter);
            }
        }
        return valueSelector;
    }

    protected void validateProbability(SelectionOrder resolvedSelectionOrder) {
        if (config.getProbabilityWeightFactoryClass() != null
                && resolvedSelectionOrder != SelectionOrder.PROBABILISTIC) {
            throw new IllegalArgumentException("The valueSelectorConfig (" + config
                    + ") with probabilityWeightFactoryClass (" + config.getProbabilityWeightFactoryClass()
                    + ") has a resolvedSelectionOrder (" + resolvedSelectionOrder
                    + ") that is not " + SelectionOrder.PROBABILISTIC + ".");
        }
    }

    protected ValueSelector<Solution_> applyProbability(SelectionCacheType resolvedCacheType,
            SelectionOrder resolvedSelectionOrder, ValueSelector<Solution_> valueSelector) {
        if (resolvedSelectionOrder == SelectionOrder.PROBABILISTIC) {
            if (config.getProbabilityWeightFactoryClass() == null) {
                throw new IllegalArgumentException("The valueSelectorConfig (" + config
                        + ") with resolvedSelectionOrder (" + resolvedSelectionOrder
                        + ") needs a probabilityWeightFactoryClass ("
                        + config.getProbabilityWeightFactoryClass() + ").");
            }
            SelectionProbabilityWeightFactory<Solution_, Object> probabilityWeightFactory =
                    ConfigUtils.newInstance(config, "probabilityWeightFactoryClass",
                            config.getProbabilityWeightFactoryClass());
            if (!(valueSelector instanceof EntityIndependentValueSelector)) {
                throw new IllegalArgumentException("The valueSelectorConfig (" + config
                        + ") with resolvedCacheType (" + resolvedCacheType
                        + ") and resolvedSelectionOrder (" + resolvedSelectionOrder
                        + ") needs to be based on an "
                        + EntityIndependentValueSelector.class.getSimpleName() + " (" + valueSelector + ")."
                        + " Check your @" + ValueRangeProvider.class.getSimpleName() + " annotations.");
            }
            valueSelector = new ProbabilityValueSelector<>((EntityIndependentValueSelector<Solution_>) valueSelector,
                    resolvedCacheType, probabilityWeightFactory);
        }
        return valueSelector;
    }

    private ValueSelector<Solution_> applyShuffling(SelectionCacheType resolvedCacheType,
            SelectionOrder resolvedSelectionOrder, ValueSelector<Solution_> valueSelector) {
        if (resolvedSelectionOrder == SelectionOrder.SHUFFLED) {
            if (!(valueSelector instanceof EntityIndependentValueSelector)) {
                throw new IllegalArgumentException("The valueSelectorConfig (" + config
                        + ") with resolvedCacheType (" + resolvedCacheType
                        + ") and resolvedSelectionOrder (" + resolvedSelectionOrder
                        + ") needs to be based on an "
                        + EntityIndependentValueSelector.class.getSimpleName() + " (" + valueSelector + ")."
                        + " Check your @" + ValueRangeProvider.class.getSimpleName() + " annotations.");
            }
            valueSelector = new ShufflingValueSelector<>((EntityIndependentValueSelector<Solution_>) valueSelector,
                    resolvedCacheType);
        }
        return valueSelector;
    }

    private ValueSelector<Solution_> applyCaching(SelectionCacheType resolvedCacheType,
            SelectionOrder resolvedSelectionOrder, ValueSelector<Solution_> valueSelector) {
        if (resolvedCacheType.isCached() && resolvedCacheType.compareTo(valueSelector.getCacheType()) > 0) {
            if (!(valueSelector instanceof EntityIndependentValueSelector)) {
                throw new IllegalArgumentException("The valueSelectorConfig (" + config
                        + ") with resolvedCacheType (" + resolvedCacheType
                        + ") and resolvedSelectionOrder (" + resolvedSelectionOrder
                        + ") needs to be based on an "
                        + EntityIndependentValueSelector.class.getSimpleName() + " (" + valueSelector + ")."
                        + " Check your @" + ValueRangeProvider.class.getSimpleName() + " annotations.");
            }
            valueSelector = new CachingValueSelector<>((EntityIndependentValueSelector<Solution_>) valueSelector,
                    resolvedCacheType, resolvedSelectionOrder.toRandomSelectionBoolean());
        }
        return valueSelector;
    }

    private void validateSelectedLimit(SelectionCacheType minimumCacheType) {
        if (config.getSelectedCountLimit() != null && minimumCacheType.compareTo(SelectionCacheType.JUST_IN_TIME) > 0) {
            throw new IllegalArgumentException("The valueSelectorConfig (" + config
                    + ") with selectedCountLimit (" + config.getSelectedCountLimit()
                    + ") has a minimumCacheType (" + minimumCacheType
                    + ") that is higher than " + SelectionCacheType.JUST_IN_TIME + ".");
        }
    }

    private ValueSelector<Solution_> applySelectedLimit(ValueSelector<Solution_> valueSelector) {
        if (config.getSelectedCountLimit() != null) {
            valueSelector = new SelectedCountLimitValueSelector<>(valueSelector, config.getSelectedCountLimit());
        }
        return valueSelector;
    }

    private ValueSelector<Solution_> applyNearbySelection(HeuristicConfigPolicy<Solution_> configPolicy,
            NearbySelectionConfig nearbySelectionConfig, SelectionCacheType minimumCacheType,
            SelectionOrder resolvedSelectionOrder, ValueSelector<Solution_> valueSelector) {
        boolean randomSelection = resolvedSelectionOrder.toRandomSelectionBoolean();
        EntitySelectorFactory<Solution_> entitySelectorFactory =
                EntitySelectorFactory.create(nearbySelectionConfig.getOriginEntitySelectorConfig());
        EntitySelector<Solution_> originEntitySelector =
                entitySelectorFactory.buildEntitySelector(configPolicy, minimumCacheType, resolvedSelectionOrder);
        NearbyDistanceMeter<?, ?> nearbyDistanceMeter =
                (NearbyDistanceMeter<?, ?>) ConfigUtils.newInstance(nearbySelectionConfig, "nearbyDistanceMeterClass",
                        nearbySelectionConfig.getNearbyDistanceMeterClass());
        // TODO Check nearbyDistanceMeterClass.getGenericInterfaces() to confirm generic type S is an entityClass
        NearbyRandom nearbyRandom =
                NearbyRandomFactory.create(config.getNearbySelectionConfig()).buildNearbyRandom(randomSelection);
        return new NearEntityNearbyValueSelector<>(valueSelector, originEntitySelector, nearbyDistanceMeter,
                nearbyRandom, randomSelection);
    }

    private ValueSelector<Solution_> applyMimicRecording(HeuristicConfigPolicy<Solution_> configPolicy,
            ValueSelector<Solution_> valueSelector) {
        if (config.getId() != null) {
            if (config.getId().isEmpty()) {
                throw new IllegalArgumentException("The valueSelectorConfig (" + config
                        + ") has an empty id (" + config.getId() + ").");
            }
            if (!(valueSelector instanceof EntityIndependentValueSelector)) {
                throw new IllegalArgumentException("The valueSelectorConfig (" + config
                        + ") with id (" + config.getId()
                        + ") needs to be based on an "
                        + EntityIndependentValueSelector.class.getSimpleName() + " (" + valueSelector + ")."
                        + " Check your @" + ValueRangeProvider.class.getSimpleName() + " annotations.");
            }
            MimicRecordingValueSelector<Solution_> mimicRecordingValueSelector = new MimicRecordingValueSelector<>(
                    (EntityIndependentValueSelector<Solution_>) valueSelector);
            configPolicy.addValueMimicRecorder(config.getId(), mimicRecordingValueSelector);
            valueSelector = mimicRecordingValueSelector;
        }
        return valueSelector;
    }

    private ValueSelector<Solution_> applyUnassignedValueFiltering(boolean applyUnassignedValueFiltering,
            GenuineVariableDescriptor<Solution_> variableDescriptor, ValueSelector<Solution_> valueSelector) {
        if (applyUnassignedValueFiltering && variableDescriptor.isListVariable()) {
            if (!(valueSelector instanceof EntityIndependentValueSelector)) {
                throw new IllegalArgumentException("The valueSelectorConfig (" + config
                        + ") with id (" + config.getId()
                        + ") needs to be based on an "
                        + EntityIndependentValueSelector.class.getSimpleName() + " (" + valueSelector + ")."
                        + " Check your @" + ValueRangeProvider.class.getSimpleName() + " annotations.");
            }
            valueSelector = new UnassignedValueSelector<>(((EntityIndependentValueSelector<Solution_>) valueSelector));
        }
        return valueSelector;
    }

    private ValueSelector<Solution_> applyReinitializeVariableFiltering(boolean applyReinitializeVariableFiltering,
            GenuineVariableDescriptor<Solution_> variableDescriptor, ValueSelector<Solution_> valueSelector) {
        if (applyReinitializeVariableFiltering && !variableDescriptor.isListVariable()) {
            valueSelector = new ReinitializeVariableValueSelector<>(valueSelector);
        }
        return valueSelector;
    }

    private ValueSelector<Solution_> applyDowncasting(ValueSelector<Solution_> valueSelector) {
        if (config.getDowncastEntityClass() != null) {
            valueSelector = new DowncastingValueSelector<>(valueSelector, config.getDowncastEntityClass());
        }
        return valueSelector;
    }
}
