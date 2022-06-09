package org.optaplanner.core.impl.heuristic.selector.entity;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.config.heuristic.selector.common.SelectionCacheType;
import org.optaplanner.core.config.heuristic.selector.common.SelectionOrder;
import org.optaplanner.core.config.heuristic.selector.common.decorator.SelectionSorterOrder;
import org.optaplanner.core.config.heuristic.selector.common.nearby.NearbySelectionConfig;
import org.optaplanner.core.config.heuristic.selector.entity.EntitySelectorConfig;
import org.optaplanner.core.config.util.ConfigUtils;
import org.optaplanner.core.impl.domain.entity.descriptor.EntityDescriptor;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
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
import org.optaplanner.core.impl.heuristic.selector.entity.decorator.CachingEntitySelector;
import org.optaplanner.core.impl.heuristic.selector.entity.decorator.FilteringEntitySelector;
import org.optaplanner.core.impl.heuristic.selector.entity.decorator.ProbabilityEntitySelector;
import org.optaplanner.core.impl.heuristic.selector.entity.decorator.SelectedCountLimitEntitySelector;
import org.optaplanner.core.impl.heuristic.selector.entity.decorator.ShufflingEntitySelector;
import org.optaplanner.core.impl.heuristic.selector.entity.decorator.SortingEntitySelector;
import org.optaplanner.core.impl.heuristic.selector.entity.mimic.EntityMimicRecorder;
import org.optaplanner.core.impl.heuristic.selector.entity.mimic.MimicRecordingEntitySelector;
import org.optaplanner.core.impl.heuristic.selector.entity.mimic.MimicReplayingEntitySelector;
import org.optaplanner.core.impl.heuristic.selector.entity.nearby.NearEntityNearbyEntitySelector;

public class EntitySelectorFactory<Solution_> extends AbstractSelectorFactory<Solution_, EntitySelectorConfig> {

    public static <Solution_> EntitySelectorFactory<Solution_> create(
            EntitySelectorConfig entitySelectorConfig) {
        return new EntitySelectorFactory<>(entitySelectorConfig);
    }

    public EntitySelectorFactory(EntitySelectorConfig entitySelectorConfig) {
        super(entitySelectorConfig);
    }

    public EntityDescriptor<Solution_> extractEntityDescriptor(HeuristicConfigPolicy<Solution_> configPolicy) {
        if (config.getEntityClass() != null) {
            SolutionDescriptor<Solution_> solutionDescriptor = configPolicy.getSolutionDescriptor();
            EntityDescriptor<Solution_> entityDescriptor =
                    solutionDescriptor.getEntityDescriptorStrict(config.getEntityClass());
            if (entityDescriptor == null) {
                throw new IllegalArgumentException("The selectorConfig (" + config
                        + ") has an entityClass (" + config.getEntityClass() + ") that is not a known planning entity.\n"
                        + "Check your solver configuration. If that class (" + config.getEntityClass().getSimpleName()
                        + ") is not in the entityClassSet (" + solutionDescriptor.getEntityClassSet()
                        + "), check your @" + PlanningSolution.class.getSimpleName()
                        + " implementation's annotated methods too.");
            }
            return entityDescriptor;
        } else if (config.getMimicSelectorRef() != null) {
            return configPolicy.getEntityMimicRecorder(config.getMimicSelectorRef()).getEntityDescriptor();
        } else {
            return null;
        }
    }

    /**
     * @param configPolicy never null
     * @param minimumCacheType never null, If caching is used (different from {@link SelectionCacheType#JUST_IN_TIME}),
     *        then it should be at least this {@link SelectionCacheType} because an ancestor already uses such caching
     *        and less would be pointless.
     * @param inheritedSelectionOrder never null
     * @return never null
     */
    public EntitySelector<Solution_> buildEntitySelector(HeuristicConfigPolicy<Solution_> configPolicy,
            SelectionCacheType minimumCacheType, SelectionOrder inheritedSelectionOrder) {
        if (config.getMimicSelectorRef() != null) {
            return buildMimicReplaying(configPolicy);
        }
        EntityDescriptor<Solution_> entityDescriptor =
                config.getEntityClass() == null ? deduceEntityDescriptor(configPolicy.getSolutionDescriptor())
                        : deduceEntityDescriptor(configPolicy.getSolutionDescriptor(), config.getEntityClass());
        SelectionCacheType resolvedCacheType = SelectionCacheType.resolve(config.getCacheType(), minimumCacheType);
        SelectionOrder resolvedSelectionOrder = SelectionOrder.resolve(config.getSelectionOrder(), inheritedSelectionOrder);

        if (config.getNearbySelectionConfig() != null) {
            config.getNearbySelectionConfig().validateNearby(resolvedCacheType, resolvedSelectionOrder);
        }
        validateCacheTypeVersusSelectionOrder(resolvedCacheType, resolvedSelectionOrder);
        validateSorting(resolvedSelectionOrder);
        validateProbability(resolvedSelectionOrder);
        validateSelectedLimit(minimumCacheType);

        // baseEntitySelector and lower should be SelectionOrder.ORIGINAL if they are going to get cached completely
        boolean baseRandomSelection = determineBaseRandomSelection(entityDescriptor, resolvedCacheType, resolvedSelectionOrder);
        SelectionCacheType baseSelectionCacheType = SelectionCacheType.max(minimumCacheType, resolvedCacheType);
        EntitySelector<Solution_> entitySelector = buildBaseEntitySelector(entityDescriptor, baseSelectionCacheType,
                baseRandomSelection);
        if (config.getNearbySelectionConfig() != null) {
            // TODO Static filtering (such as movableEntitySelectionFilter) should affect nearbySelection
            entitySelector = applyNearbySelection(configPolicy, config.getNearbySelectionConfig(), minimumCacheType,
                    resolvedSelectionOrder, entitySelector);
        }
        entitySelector = applyFiltering(entitySelector);
        entitySelector = applySorting(resolvedCacheType, resolvedSelectionOrder, entitySelector);
        entitySelector = applyProbability(resolvedCacheType, resolvedSelectionOrder, entitySelector);
        entitySelector = applyShuffling(resolvedCacheType, resolvedSelectionOrder, entitySelector);
        entitySelector = applyCaching(resolvedCacheType, resolvedSelectionOrder, entitySelector);
        entitySelector = applySelectedLimit(resolvedSelectionOrder, entitySelector);
        entitySelector = applyMimicRecording(configPolicy, entitySelector);
        return entitySelector;
    }

    protected EntitySelector<Solution_> buildMimicReplaying(HeuristicConfigPolicy<Solution_> configPolicy) {
        final boolean anyConfigurationParameterDefined = Stream
                .of(config.getId(), config.getEntityClass(), config.getCacheType(), config.getSelectionOrder(),
                        config.getNearbySelectionConfig(), config.getFilterClass(), config.getSorterManner(),
                        config.getSorterComparatorClass(), config.getSorterWeightFactoryClass(), config.getSorterOrder(),
                        config.getSorterClass(), config.getProbabilityWeightFactoryClass(), config.getSelectedCountLimit())
                .filter(Objects::nonNull).findFirst().isPresent();
        if (anyConfigurationParameterDefined) {
            throw new IllegalArgumentException("The entitySelectorConfig (" + config
                    + ") with mimicSelectorRef (" + config.getMimicSelectorRef()
                    + ") has another property that is not null.");
        }
        EntityMimicRecorder<Solution_> entityMimicRecorder =
                configPolicy.getEntityMimicRecorder(config.getMimicSelectorRef());
        if (entityMimicRecorder == null) {
            throw new IllegalArgumentException("The entitySelectorConfig (" + config
                    + ") has a mimicSelectorRef (" + config.getMimicSelectorRef()
                    + ") for which no entitySelector with that id exists (in its solver phase).");
        }
        return new MimicReplayingEntitySelector<>(entityMimicRecorder);
    }

    protected boolean determineBaseRandomSelection(EntityDescriptor<Solution_> entityDescriptor,
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
                        || (isBaseInherentlyCached() && !hasFiltering(entityDescriptor));
            default:
                throw new IllegalStateException("The selectionOrder (" + resolvedSelectionOrder
                        + ") is not implemented.");
        }
    }

    protected boolean isBaseInherentlyCached() {
        return true;
    }

    private EntitySelector<Solution_> buildBaseEntitySelector(EntityDescriptor<Solution_> entityDescriptor,
            SelectionCacheType minimumCacheType, boolean randomSelection) {
        if (minimumCacheType == SelectionCacheType.SOLVER) {
            // TODO Solver cached entities are not compatible with DroolsScoreCalculator and IncrementalScoreDirector
            // because between phases the entities get cloned and the KieSession/Maps contains those clones afterwards
            // https://issues.redhat.com/browse/PLANNER-54
            throw new IllegalArgumentException("The minimumCacheType (" + minimumCacheType
                    + ") is not yet supported. Please use " + SelectionCacheType.PHASE + " instead.");
        }
        // FromSolutionEntitySelector has an intrinsicCacheType STEP
        return new FromSolutionEntitySelector<>(entityDescriptor, minimumCacheType, randomSelection);
    }

    private boolean hasFiltering(EntityDescriptor<Solution_> entityDescriptor) {
        return config.getFilterClass() != null || entityDescriptor.hasEffectiveMovableEntitySelectionFilter();
    }

    private EntitySelector<Solution_> applyNearbySelection(HeuristicConfigPolicy<Solution_> configPolicy,
            NearbySelectionConfig nearbySelectionConfig, SelectionCacheType minimumCacheType,
            SelectionOrder resolvedSelectionOrder, EntitySelector<Solution_> entitySelector) {
        boolean randomSelection = resolvedSelectionOrder.toRandomSelectionBoolean();
        EntitySelectorFactory<Solution_> entitySelectorFactory =
                EntitySelectorFactory.create(nearbySelectionConfig.getOriginEntitySelectorConfig());
        EntitySelector<Solution_> originEntitySelector =
                entitySelectorFactory.buildEntitySelector(configPolicy, minimumCacheType, resolvedSelectionOrder);
        NearbyDistanceMeter nearbyDistanceMeter = ConfigUtils.newInstance(nearbySelectionConfig, "nearbyDistanceMeterClass",
                nearbySelectionConfig.getNearbyDistanceMeterClass());
        // TODO Check nearbyDistanceMeterClass.getGenericInterfaces() to confirm generic type S is an entityClass
        NearbyRandom nearbyRandom = NearbyRandomFactory.create(nearbySelectionConfig).buildNearbyRandom(randomSelection);
        return new NearEntityNearbyEntitySelector<>(entitySelector, originEntitySelector, nearbyDistanceMeter,
                nearbyRandom, randomSelection);
    }

    private EntitySelector<Solution_> applyFiltering(EntitySelector<Solution_> entitySelector) {
        EntityDescriptor<Solution_> entityDescriptor = entitySelector.getEntityDescriptor();
        if (hasFiltering(entityDescriptor)) {
            List<SelectionFilter<Solution_, Object>> filterList = new ArrayList<>(config.getFilterClass() == null ? 1 : 2);
            if (config.getFilterClass() != null) {
                filterList.add(ConfigUtils.newInstance(config, "filterClass", config.getFilterClass()));
            }
            // Filter out pinned entities
            if (entityDescriptor.hasEffectiveMovableEntitySelectionFilter()) {
                filterList.add(entityDescriptor.getEffectiveMovableEntitySelectionFilter());
            }
            // Do not filter out initialized entities here for CH and ES, because they can be partially initialized
            // Instead, ValueSelectorConfig.applyReinitializeVariableFiltering() does that.
            entitySelector = new FilteringEntitySelector<Solution_>(entitySelector, filterList);
        }
        return entitySelector;
    }

    protected void validateSorting(SelectionOrder resolvedSelectionOrder) {
        if ((config.getSorterManner() != null || config.getSorterComparatorClass() != null
                || config.getSorterWeightFactoryClass() != null
                || config.getSorterOrder() != null || config.getSorterClass() != null)
                && resolvedSelectionOrder != SelectionOrder.SORTED) {
            throw new IllegalArgumentException("The entitySelectorConfig (" + config
                    + ") with sorterManner (" + config.getSorterManner()
                    + ") and sorterComparatorClass (" + config.getSorterComparatorClass()
                    + ") and sorterWeightFactoryClass (" + config.getSorterWeightFactoryClass()
                    + ") and sorterOrder (" + config.getSorterOrder()
                    + ") and sorterClass (" + config.getSorterClass()
                    + ") has a resolvedSelectionOrder (" + resolvedSelectionOrder
                    + ") that is not " + SelectionOrder.SORTED + ".");
        }
        if (config.getSorterManner() != null && config.getSorterComparatorClass() != null) {
            throw new IllegalArgumentException("The entitySelectorConfig (" + config
                    + ") has both a sorterManner (" + config.getSorterManner()
                    + ") and a sorterComparatorClass (" + config.getSorterComparatorClass() + ").");
        }
        if (config.getSorterManner() != null && config.getSorterWeightFactoryClass() != null) {
            throw new IllegalArgumentException("The entitySelectorConfig (" + config
                    + ") has both a sorterManner (" + config.getSorterManner()
                    + ") and a sorterWeightFactoryClass (" + config.getSorterWeightFactoryClass() + ").");
        }
        if (config.getSorterManner() != null && config.getSorterClass() != null) {
            throw new IllegalArgumentException("The entitySelectorConfig (" + config
                    + ") has both a sorterManner (" + config.getSorterManner()
                    + ") and a sorterClass (" + config.getSorterClass() + ").");
        }
        if (config.getSorterManner() != null && config.getSorterOrder() != null) {
            throw new IllegalArgumentException("The entitySelectorConfig (" + config
                    + ") with sorterManner (" + config.getSorterManner()
                    + ") has a non-null sorterOrder (" + config.getSorterOrder() + ").");
        }
        if (config.getSorterComparatorClass() != null && config.getSorterWeightFactoryClass() != null) {
            throw new IllegalArgumentException("The entitySelectorConfig (" + config
                    + ") has both a sorterComparatorClass (" + config.getSorterComparatorClass()
                    + ") and a sorterWeightFactoryClass (" + config.getSorterWeightFactoryClass() + ").");
        }
        if (config.getSorterComparatorClass() != null && config.getSorterClass() != null) {
            throw new IllegalArgumentException("The entitySelectorConfig (" + config
                    + ") has both a sorterComparatorClass (" + config.getSorterComparatorClass()
                    + ") and a sorterClass (" + config.getSorterClass() + ").");
        }
        if (config.getSorterWeightFactoryClass() != null && config.getSorterClass() != null) {
            throw new IllegalArgumentException("The entitySelectorConfig (" + config
                    + ") has both a sorterWeightFactoryClass (" + config.getSorterWeightFactoryClass()
                    + ") and a sorterClass (" + config.getSorterClass() + ").");
        }
        if (config.getSorterClass() != null && config.getSorterOrder() != null) {
            throw new IllegalArgumentException("The entitySelectorConfig (" + config
                    + ") with sorterClass (" + config.getSorterClass()
                    + ") has a non-null sorterOrder (" + config.getSorterOrder() + ").");
        }
    }

    protected EntitySelector<Solution_> applySorting(SelectionCacheType resolvedCacheType,
            SelectionOrder resolvedSelectionOrder, EntitySelector<Solution_> entitySelector) {
        if (resolvedSelectionOrder == SelectionOrder.SORTED) {
            SelectionSorter<Solution_, Object> sorter;
            if (config.getSorterManner() != null) {
                EntityDescriptor<Solution_> entityDescriptor = entitySelector.getEntityDescriptor();
                if (!EntitySelectorConfig.hasSorter(config.getSorterManner(), entityDescriptor)) {
                    return entitySelector;
                }
                sorter = EntitySelectorConfig.determineSorter(config.getSorterManner(), entityDescriptor);
            } else if (config.getSorterComparatorClass() != null) {
                Comparator<Object> sorterComparator = ConfigUtils.newInstance(config,
                        "sorterComparatorClass", config.getSorterComparatorClass());
                sorter = new ComparatorSelectionSorter<>(sorterComparator,
                        SelectionSorterOrder.resolve(config.getSorterOrder()));
            } else if (config.getSorterWeightFactoryClass() != null) {
                SelectionSorterWeightFactory<Solution_, Object> sorterWeightFactory = ConfigUtils.newInstance(config,
                        "sorterWeightFactoryClass", config.getSorterWeightFactoryClass());
                sorter = new WeightFactorySelectionSorter<>(sorterWeightFactory,
                        SelectionSorterOrder.resolve(config.getSorterOrder()));
            } else if (config.getSorterClass() != null) {
                sorter = ConfigUtils.newInstance(config, "sorterClass", config.getSorterClass());
            } else {
                throw new IllegalArgumentException("The entitySelectorConfig (" + config
                        + ") with resolvedSelectionOrder (" + resolvedSelectionOrder
                        + ") needs a sorterManner (" + config.getSorterManner()
                        + ") or a sorterComparatorClass (" + config.getSorterComparatorClass()
                        + ") or a sorterWeightFactoryClass (" + config.getSorterWeightFactoryClass()
                        + ") or a sorterClass (" + config.getSorterClass() + ").");
            }
            entitySelector = new SortingEntitySelector<>(entitySelector, resolvedCacheType, sorter);
        }
        return entitySelector;
    }

    protected void validateProbability(SelectionOrder resolvedSelectionOrder) {
        if (config.getProbabilityWeightFactoryClass() != null
                && resolvedSelectionOrder != SelectionOrder.PROBABILISTIC) {
            throw new IllegalArgumentException("The entitySelectorConfig (" + config
                    + ") with probabilityWeightFactoryClass (" + config.getProbabilityWeightFactoryClass()
                    + ") has a resolvedSelectionOrder (" + resolvedSelectionOrder
                    + ") that is not " + SelectionOrder.PROBABILISTIC + ".");
        }
    }

    protected EntitySelector<Solution_> applyProbability(SelectionCacheType resolvedCacheType,
            SelectionOrder resolvedSelectionOrder, EntitySelector<Solution_> entitySelector) {
        if (resolvedSelectionOrder == SelectionOrder.PROBABILISTIC) {
            if (config.getProbabilityWeightFactoryClass() == null) {
                throw new IllegalArgumentException("The entitySelectorConfig (" + config
                        + ") with resolvedSelectionOrder (" + resolvedSelectionOrder
                        + ") needs a probabilityWeightFactoryClass ("
                        + config.getProbabilityWeightFactoryClass() + ").");
            }
            SelectionProbabilityWeightFactory<Solution_, Object> probabilityWeightFactory =
                    ConfigUtils.newInstance(config, "probabilityWeightFactoryClass",
                            config.getProbabilityWeightFactoryClass());
            entitySelector = new ProbabilityEntitySelector<>(entitySelector, resolvedCacheType,
                    probabilityWeightFactory);
        }
        return entitySelector;
    }

    private EntitySelector<Solution_> applyShuffling(SelectionCacheType resolvedCacheType,
            SelectionOrder resolvedSelectionOrder, EntitySelector<Solution_> entitySelector) {
        if (resolvedSelectionOrder == SelectionOrder.SHUFFLED) {
            entitySelector = new ShufflingEntitySelector<>(entitySelector, resolvedCacheType);
        }
        return entitySelector;
    }

    private EntitySelector<Solution_> applyCaching(SelectionCacheType resolvedCacheType,
            SelectionOrder resolvedSelectionOrder, EntitySelector<Solution_> entitySelector) {
        if (resolvedCacheType.isCached() && resolvedCacheType.compareTo(entitySelector.getCacheType()) > 0) {
            entitySelector = new CachingEntitySelector<>(entitySelector, resolvedCacheType,
                    resolvedSelectionOrder.toRandomSelectionBoolean());
        }
        return entitySelector;
    }

    private void validateSelectedLimit(SelectionCacheType minimumCacheType) {
        if (config.getSelectedCountLimit() != null
                && minimumCacheType.compareTo(SelectionCacheType.JUST_IN_TIME) > 0) {
            throw new IllegalArgumentException("The entitySelectorConfig (" + config
                    + ") with selectedCountLimit (" + config.getSelectedCountLimit()
                    + ") has a minimumCacheType (" + minimumCacheType
                    + ") that is higher than " + SelectionCacheType.JUST_IN_TIME + ".");
        }
    }

    private EntitySelector<Solution_> applySelectedLimit(SelectionOrder resolvedSelectionOrder,
            EntitySelector<Solution_> entitySelector) {
        if (config.getSelectedCountLimit() != null) {
            entitySelector = new SelectedCountLimitEntitySelector<>(entitySelector,
                    resolvedSelectionOrder.toRandomSelectionBoolean(), config.getSelectedCountLimit());
        }
        return entitySelector;
    }

    private EntitySelector<Solution_> applyMimicRecording(HeuristicConfigPolicy<Solution_> configPolicy,
            EntitySelector<Solution_> entitySelector) {
        if (config.getId() != null) {
            if (config.getId().isEmpty()) {
                throw new IllegalArgumentException("The entitySelectorConfig (" + config
                        + ") has an empty id (" + config.getId() + ").");
            }
            MimicRecordingEntitySelector<Solution_> mimicRecordingEntitySelector =
                    new MimicRecordingEntitySelector<>(entitySelector);
            configPolicy.addEntityMimicRecorder(config.getId(), mimicRecordingEntitySelector);
            entitySelector = mimicRecordingEntitySelector;
        }
        return entitySelector;
    }
}
