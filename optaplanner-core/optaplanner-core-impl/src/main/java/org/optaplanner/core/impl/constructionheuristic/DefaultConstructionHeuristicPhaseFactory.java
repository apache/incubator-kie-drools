package org.optaplanner.core.impl.constructionheuristic;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ThreadFactory;

import org.optaplanner.core.config.constructionheuristic.ConstructionHeuristicPhaseConfig;
import org.optaplanner.core.config.constructionheuristic.ConstructionHeuristicType;
import org.optaplanner.core.config.constructionheuristic.decider.forager.ConstructionHeuristicForagerConfig;
import org.optaplanner.core.config.constructionheuristic.placer.EntityPlacerConfig;
import org.optaplanner.core.config.constructionheuristic.placer.PooledEntityPlacerConfig;
import org.optaplanner.core.config.constructionheuristic.placer.QueuedEntityPlacerConfig;
import org.optaplanner.core.config.constructionheuristic.placer.QueuedValuePlacerConfig;
import org.optaplanner.core.config.heuristic.selector.common.SelectionCacheType;
import org.optaplanner.core.config.heuristic.selector.common.SelectionOrder;
import org.optaplanner.core.config.heuristic.selector.entity.EntitySorterManner;
import org.optaplanner.core.config.heuristic.selector.move.MoveSelectorConfig;
import org.optaplanner.core.config.heuristic.selector.move.composite.CartesianProductMoveSelectorConfig;
import org.optaplanner.core.config.heuristic.selector.move.composite.UnionMoveSelectorConfig;
import org.optaplanner.core.config.heuristic.selector.move.generic.ChangeMoveSelectorConfig;
import org.optaplanner.core.config.heuristic.selector.value.ValueSelectorConfig;
import org.optaplanner.core.config.heuristic.selector.value.ValueSorterManner;
import org.optaplanner.core.config.solver.EnvironmentMode;
import org.optaplanner.core.config.util.ConfigUtils;
import org.optaplanner.core.impl.constructionheuristic.decider.ConstructionHeuristicDecider;
import org.optaplanner.core.impl.constructionheuristic.decider.MultiThreadedConstructionHeuristicDecider;
import org.optaplanner.core.impl.constructionheuristic.decider.forager.ConstructionHeuristicForager;
import org.optaplanner.core.impl.constructionheuristic.decider.forager.ConstructionHeuristicForagerFactory;
import org.optaplanner.core.impl.constructionheuristic.placer.EntityPlacer;
import org.optaplanner.core.impl.constructionheuristic.placer.EntityPlacerFactory;
import org.optaplanner.core.impl.constructionheuristic.placer.PooledEntityPlacerFactory;
import org.optaplanner.core.impl.constructionheuristic.placer.QueuedEntityPlacerFactory;
import org.optaplanner.core.impl.constructionheuristic.placer.QueuedValuePlacerFactory;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.core.impl.domain.variable.descriptor.ListVariableDescriptor;
import org.optaplanner.core.impl.heuristic.HeuristicConfigPolicy;
import org.optaplanner.core.impl.phase.AbstractPhaseFactory;
import org.optaplanner.core.impl.solver.recaller.BestSolutionRecaller;
import org.optaplanner.core.impl.solver.termination.Termination;
import org.optaplanner.core.impl.solver.thread.ChildThreadType;

public class DefaultConstructionHeuristicPhaseFactory<Solution_>
        extends AbstractPhaseFactory<Solution_, ConstructionHeuristicPhaseConfig> {

    public DefaultConstructionHeuristicPhaseFactory(ConstructionHeuristicPhaseConfig phaseConfig) {
        super(phaseConfig);
    }

    @Override
    public ConstructionHeuristicPhase<Solution_> buildPhase(int phaseIndex,
            HeuristicConfigPolicy<Solution_> solverConfigPolicy, BestSolutionRecaller<Solution_> bestSolutionRecaller,
            Termination<Solution_> solverTermination) {
        ConstructionHeuristicType constructionHeuristicType_ = Objects.requireNonNullElse(
                phaseConfig.getConstructionHeuristicType(),
                ConstructionHeuristicType.ALLOCATE_ENTITY_FROM_QUEUE);
        EntitySorterManner entitySorterManner = Objects.requireNonNullElse(
                phaseConfig.getEntitySorterManner(),
                constructionHeuristicType_.getDefaultEntitySorterManner());
        ValueSorterManner valueSorterManner = Objects.requireNonNullElse(
                phaseConfig.getValueSorterManner(),
                constructionHeuristicType_.getDefaultValueSorterManner());
        HeuristicConfigPolicy<Solution_> phaseConfigPolicy = solverConfigPolicy.cloneBuilder()
                .withReinitializeVariableFilterEnabled(true)
                .withInitializedChainedValueFilterEnabled(true)
                .withEntitySorterManner(entitySorterManner)
                .withValueSorterManner(valueSorterManner)
                .build();
        Termination<Solution_> phaseTermination = buildPhaseTermination(phaseConfigPolicy, solverTermination);
        EntityPlacerConfig entityPlacerConfig_ = getValidEntityPlacerConfig()
                .orElseGet(() -> buildDefaultEntityPlacerConfig(phaseConfigPolicy, constructionHeuristicType_));
        EntityPlacer<Solution_> entityPlacer = EntityPlacerFactory.<Solution_> create(entityPlacerConfig_)
                .buildEntityPlacer(phaseConfigPolicy);

        DefaultConstructionHeuristicPhase.Builder<Solution_> builder = new DefaultConstructionHeuristicPhase.Builder<>(
                phaseIndex,
                solverConfigPolicy.getLogIndentation(),
                phaseTermination,
                entityPlacer,
                buildDecider(phaseConfigPolicy, phaseTermination));

        EnvironmentMode environmentMode = phaseConfigPolicy.getEnvironmentMode();
        if (environmentMode.isNonIntrusiveFullAsserted()) {
            builder.setAssertStepScoreFromScratch(true);
        }
        if (environmentMode.isIntrusiveFastAsserted()) {
            builder.setAssertExpectedStepScore(true);
            builder.setAssertShadowVariablesAreNotStaleAfterStep(true);
        }
        return builder.build();
    }

    private Optional<EntityPlacerConfig> getValidEntityPlacerConfig() {
        EntityPlacerConfig entityPlacerConfig = phaseConfig.getEntityPlacerConfig();
        if (entityPlacerConfig == null) {
            return Optional.empty();
        }
        if (phaseConfig.getConstructionHeuristicType() != null) {
            throw new IllegalArgumentException(
                    "The constructionHeuristicType (" + phaseConfig.getConstructionHeuristicType()
                            + ") must not be configured if the entityPlacerConfig (" + entityPlacerConfig
                            + ") is explicitly configured.");
        }
        if (phaseConfig.getMoveSelectorConfigList() != null) {
            throw new IllegalArgumentException("The moveSelectorConfigList (" + phaseConfig.getMoveSelectorConfigList()
                    + ") cannot be configured if the entityPlacerConfig (" + entityPlacerConfig
                    + ") is explicitly configured.");
        }
        return Optional.of(entityPlacerConfig);
    }

    private EntityPlacerConfig buildDefaultEntityPlacerConfig(HeuristicConfigPolicy<Solution_> configPolicy,
            ConstructionHeuristicType constructionHeuristicType) {
        return findValidListVariableDescriptor(configPolicy.getSolutionDescriptor())
                .map(listVariableDescriptor -> buildListVariableQueuedValuePlacerConfig(configPolicy, listVariableDescriptor))
                .orElseGet(() -> buildUnfoldedEntityPlacerConfig(configPolicy, constructionHeuristicType));
    }

    private Optional<ListVariableDescriptor<?>> findValidListVariableDescriptor(
            SolutionDescriptor<Solution_> solutionDescriptor) {
        List<ListVariableDescriptor<Solution_>> listVariableDescriptors = solutionDescriptor.getListVariableDescriptors();
        if (listVariableDescriptors.isEmpty()) {
            return Optional.empty();
        }
        if (listVariableDescriptors.size() > 1) {
            throw new IllegalArgumentException("Construction Heuristic phase does not support multiple list variables ("
                    + listVariableDescriptors + ").");
        }

        failIfConfigured(phaseConfig.getConstructionHeuristicType(), "constructionHeuristicType");
        failIfConfigured(phaseConfig.getEntityPlacerConfig(), "entityPlacerConfig");
        failIfConfigured(phaseConfig.getMoveSelectorConfigList(), "moveSelectorConfigList");

        return Optional.of(listVariableDescriptors.get(0));
    }

    private static void failIfConfigured(Object configValue, String configName) {
        if (configValue != null) {
            throw new IllegalArgumentException("Construction Heuristic phase with a list variable does not support "
                    + configName + " configuration. Remove the " + configName + " (" + configValue + ") from the config.");
        }
    }

    public static EntityPlacerConfig buildListVariableQueuedValuePlacerConfig(
            HeuristicConfigPolicy<?> configPolicy,
            ListVariableDescriptor<?> variableDescriptor) {
        String mimicSelectorId = variableDescriptor.getVariableName();

        // Prepare recording ValueSelector config.
        ValueSelectorConfig mimicRecordingValueSelectorConfig = new ValueSelectorConfig(variableDescriptor.getVariableName());
        mimicRecordingValueSelectorConfig.setId(mimicSelectorId);
        if (ValueSelectorConfig.hasSorter(configPolicy.getValueSorterManner(), variableDescriptor)) {
            mimicRecordingValueSelectorConfig.setCacheType(SelectionCacheType.PHASE);
            mimicRecordingValueSelectorConfig.setSelectionOrder(SelectionOrder.SORTED);
            mimicRecordingValueSelectorConfig.setSorterManner(configPolicy.getValueSorterManner());
        }
        // Prepare replaying ValueSelector config.
        ValueSelectorConfig mimicReplayingValueSelectorConfig = new ValueSelectorConfig();
        mimicReplayingValueSelectorConfig.setMimicSelectorRef(mimicSelectorId);

        // ChangeMoveSelector uses the replaying ValueSelector.
        ChangeMoveSelectorConfig changeMoveSelectorConfig = new ChangeMoveSelectorConfig();
        changeMoveSelectorConfig.setValueSelectorConfig(mimicReplayingValueSelectorConfig);

        // Finally, QueuedValuePlacer uses the recording ValueSelector and a ChangeMoveSelector.
        // The ChangeMoveSelector's replaying ValueSelector mimics the QueuedValuePlacer's recording ValueSelector.
        QueuedValuePlacerConfig queuedValuePlacerConfig = new QueuedValuePlacerConfig();
        queuedValuePlacerConfig.setValueSelectorConfig(mimicRecordingValueSelectorConfig);
        queuedValuePlacerConfig.setMoveSelectorConfig(changeMoveSelectorConfig);

        return queuedValuePlacerConfig;
    }

    private ConstructionHeuristicDecider<Solution_> buildDecider(HeuristicConfigPolicy<Solution_> configPolicy,
            Termination<Solution_> termination) {
        ConstructionHeuristicForagerConfig foragerConfig_ =
                Objects.requireNonNullElseGet(phaseConfig.getForagerConfig(), ConstructionHeuristicForagerConfig::new);
        ConstructionHeuristicForager<Solution_> forager =
                ConstructionHeuristicForagerFactory.<Solution_> create(foragerConfig_).buildForager(configPolicy);
        EnvironmentMode environmentMode = configPolicy.getEnvironmentMode();
        ConstructionHeuristicDecider<Solution_> decider;
        Integer moveThreadCount = configPolicy.getMoveThreadCount();
        if (moveThreadCount == null) {
            decider = new ConstructionHeuristicDecider<>(configPolicy.getLogIndentation(), termination, forager);
        } else {
            Integer moveThreadBufferSize = configPolicy.getMoveThreadBufferSize();
            if (moveThreadBufferSize == null) {
                // TODO Verify this is a good default by more meticulous benchmarking on multiple machines and JDK's
                // If it's too low, move threads will need to wait on the buffer, which hurts performance
                // If it's too high, more moves are selected that aren't foraged
                moveThreadBufferSize = 10;
            }
            ThreadFactory threadFactory = configPolicy.buildThreadFactory(ChildThreadType.MOVE_THREAD);
            int selectedMoveBufferSize = moveThreadCount * moveThreadBufferSize;
            MultiThreadedConstructionHeuristicDecider<Solution_> multiThreadedDecider =
                    new MultiThreadedConstructionHeuristicDecider<>(configPolicy.getLogIndentation(), termination, forager,
                            threadFactory, moveThreadCount, selectedMoveBufferSize);
            if (environmentMode.isNonIntrusiveFullAsserted()) {
                multiThreadedDecider.setAssertStepScoreFromScratch(true);
            }
            if (environmentMode.isIntrusiveFastAsserted()) {
                multiThreadedDecider.setAssertExpectedStepScore(true);
                multiThreadedDecider.setAssertShadowVariablesAreNotStaleAfterStep(true);
            }
            decider = multiThreadedDecider;
        }
        if (environmentMode.isNonIntrusiveFullAsserted()) {
            decider.setAssertMoveScoreFromScratch(true);
        }
        if (environmentMode.isIntrusiveFastAsserted()) {
            decider.setAssertExpectedUndoMoveScore(true);
        }
        return decider;
    }

    private EntityPlacerConfig buildUnfoldedEntityPlacerConfig(HeuristicConfigPolicy<Solution_> phaseConfigPolicy,
            ConstructionHeuristicType constructionHeuristicType) {
        switch (constructionHeuristicType) {
            case FIRST_FIT:
            case FIRST_FIT_DECREASING:
            case WEAKEST_FIT:
            case WEAKEST_FIT_DECREASING:
            case STRONGEST_FIT:
            case STRONGEST_FIT_DECREASING:
            case ALLOCATE_ENTITY_FROM_QUEUE:
                if (!ConfigUtils.isEmptyCollection(phaseConfig.getMoveSelectorConfigList())) {
                    return QueuedEntityPlacerFactory.unfoldNew(phaseConfigPolicy, phaseConfig.getMoveSelectorConfigList());
                }
                return new QueuedEntityPlacerConfig();
            case ALLOCATE_TO_VALUE_FROM_QUEUE:
                if (!ConfigUtils.isEmptyCollection(phaseConfig.getMoveSelectorConfigList())) {
                    return QueuedValuePlacerFactory.unfoldNew(checkSingleMoveSelectorConfig());
                }
                return new QueuedValuePlacerConfig();
            case CHEAPEST_INSERTION:
            case ALLOCATE_FROM_POOL:
                if (!ConfigUtils.isEmptyCollection(phaseConfig.getMoveSelectorConfigList())) {
                    return PooledEntityPlacerFactory.unfoldNew(phaseConfigPolicy, checkSingleMoveSelectorConfig());
                }
                return new PooledEntityPlacerConfig();
            default:
                throw new IllegalStateException(
                        "The constructionHeuristicType (" + constructionHeuristicType + ") is not implemented.");
        }
    }

    private MoveSelectorConfig<?> checkSingleMoveSelectorConfig() {
        if (phaseConfig.getMoveSelectorConfigList().size() != 1) {
            throw new IllegalArgumentException("For the constructionHeuristicType ("
                    + phaseConfig.getConstructionHeuristicType() + "), the moveSelectorConfigList ("
                    + phaseConfig.getMoveSelectorConfigList()
                    + ") must be a singleton. Use a single " + UnionMoveSelectorConfig.class.getSimpleName()
                    + " or " + CartesianProductMoveSelectorConfig.class.getSimpleName()
                    + " element to nest multiple MoveSelectors.");
        }

        return phaseConfig.getMoveSelectorConfigList().get(0);
    }
}
