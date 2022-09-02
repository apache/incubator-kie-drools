package org.optaplanner.core.impl.exhaustivesearch;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import org.optaplanner.core.config.exhaustivesearch.ExhaustiveSearchPhaseConfig;
import org.optaplanner.core.config.exhaustivesearch.ExhaustiveSearchType;
import org.optaplanner.core.config.exhaustivesearch.NodeExplorationType;
import org.optaplanner.core.config.heuristic.selector.common.SelectionCacheType;
import org.optaplanner.core.config.heuristic.selector.common.SelectionOrder;
import org.optaplanner.core.config.heuristic.selector.entity.EntitySelectorConfig;
import org.optaplanner.core.config.heuristic.selector.entity.EntitySorterManner;
import org.optaplanner.core.config.heuristic.selector.move.MoveSelectorConfig;
import org.optaplanner.core.config.heuristic.selector.move.composite.CartesianProductMoveSelectorConfig;
import org.optaplanner.core.config.heuristic.selector.move.generic.ChangeMoveSelectorConfig;
import org.optaplanner.core.config.heuristic.selector.value.ValueSelectorConfig;
import org.optaplanner.core.config.heuristic.selector.value.ValueSorterManner;
import org.optaplanner.core.config.solver.EnvironmentMode;
import org.optaplanner.core.impl.domain.entity.descriptor.EntityDescriptor;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.core.impl.domain.variable.descriptor.GenuineVariableDescriptor;
import org.optaplanner.core.impl.exhaustivesearch.decider.ExhaustiveSearchDecider;
import org.optaplanner.core.impl.exhaustivesearch.node.bounder.ScoreBounder;
import org.optaplanner.core.impl.exhaustivesearch.node.bounder.TrendBasedScoreBounder;
import org.optaplanner.core.impl.heuristic.HeuristicConfigPolicy;
import org.optaplanner.core.impl.heuristic.selector.entity.EntitySelector;
import org.optaplanner.core.impl.heuristic.selector.entity.EntitySelectorFactory;
import org.optaplanner.core.impl.heuristic.selector.entity.mimic.ManualEntityMimicRecorder;
import org.optaplanner.core.impl.heuristic.selector.move.MoveSelector;
import org.optaplanner.core.impl.heuristic.selector.move.MoveSelectorFactory;
import org.optaplanner.core.impl.phase.AbstractPhaseFactory;
import org.optaplanner.core.impl.solver.recaller.BestSolutionRecaller;
import org.optaplanner.core.impl.solver.termination.Termination;

public class DefaultExhaustiveSearchPhaseFactory<Solution_>
        extends AbstractPhaseFactory<Solution_, ExhaustiveSearchPhaseConfig> {

    public DefaultExhaustiveSearchPhaseFactory(ExhaustiveSearchPhaseConfig phaseConfig) {
        super(phaseConfig);
    }

    @Override
    public ExhaustiveSearchPhase<Solution_> buildPhase(int phaseIndex,
            HeuristicConfigPolicy<Solution_> solverConfigPolicy, BestSolutionRecaller<Solution_> bestSolutionRecaller,
            Termination<Solution_> solverTermination) {
        ExhaustiveSearchType exhaustiveSearchType_ = Objects.requireNonNullElse(
                phaseConfig.getExhaustiveSearchType(),
                ExhaustiveSearchType.BRANCH_AND_BOUND);
        EntitySorterManner entitySorterManner = Objects.requireNonNullElse(
                phaseConfig.getEntitySorterManner(),
                exhaustiveSearchType_.getDefaultEntitySorterManner());
        ValueSorterManner valueSorterManner = Objects.requireNonNullElse(
                phaseConfig.getValueSorterManner(),
                exhaustiveSearchType_.getDefaultValueSorterManner());
        HeuristicConfigPolicy<Solution_> phaseConfigPolicy = solverConfigPolicy.cloneBuilder()
                .withReinitializeVariableFilterEnabled(true)
                .withInitializedChainedValueFilterEnabled(true)
                .withEntitySorterManner(entitySorterManner)
                .withValueSorterManner(valueSorterManner)
                .build();
        Termination<Solution_> phaseTermination = buildPhaseTermination(phaseConfigPolicy, solverTermination);
        boolean scoreBounderEnabled = exhaustiveSearchType_.isScoreBounderEnabled();
        NodeExplorationType nodeExplorationType_;
        if (exhaustiveSearchType_ == ExhaustiveSearchType.BRUTE_FORCE) {
            nodeExplorationType_ = Objects.requireNonNullElse(phaseConfig.getNodeExplorationType(),
                    NodeExplorationType.ORIGINAL_ORDER);
            if (nodeExplorationType_ != NodeExplorationType.ORIGINAL_ORDER) {
                throw new IllegalArgumentException("The phaseConfig (" + phaseConfig
                        + ") has an nodeExplorationType (" + phaseConfig.getNodeExplorationType()
                        + ") which is not compatible with its exhaustiveSearchType (" + phaseConfig.getExhaustiveSearchType()
                        + ").");
            }
        } else {
            nodeExplorationType_ = Objects.requireNonNullElse(phaseConfig.getNodeExplorationType(),
                    NodeExplorationType.DEPTH_FIRST);
        }
        EntitySelectorConfig entitySelectorConfig_ = buildEntitySelectorConfig(phaseConfigPolicy);
        EntitySelector<Solution_> entitySelector = EntitySelectorFactory.<Solution_> create(entitySelectorConfig_)
                .buildEntitySelector(phaseConfigPolicy, SelectionCacheType.PHASE, SelectionOrder.ORIGINAL);

        DefaultExhaustiveSearchPhase.Builder<Solution_> builder = new DefaultExhaustiveSearchPhase.Builder<>(
                phaseIndex,
                solverConfigPolicy.getLogIndentation(),
                phaseTermination,
                nodeExplorationType_.buildNodeComparator(scoreBounderEnabled),
                entitySelector,
                buildDecider(phaseConfigPolicy, entitySelector, bestSolutionRecaller, phaseTermination, scoreBounderEnabled));

        EnvironmentMode environmentMode = phaseConfigPolicy.getEnvironmentMode();
        if (environmentMode.isNonIntrusiveFullAsserted()) {
            builder.setAssertWorkingSolutionScoreFromScratch(true);
            builder.setAssertStepScoreFromScratch(true); // Does nothing because ES doesn't use predictStepScore()
        }
        if (environmentMode.isIntrusiveFastAsserted()) {
            builder.setAssertExpectedWorkingSolutionScore(true);
            builder.setAssertExpectedStepScore(true); // Does nothing because ES doesn't use predictStepScore()
            builder.setAssertShadowVariablesAreNotStaleAfterStep(true); // Does nothing because ES doesn't use predictStepScore()
        }
        return builder.build();
    }

    private EntitySelectorConfig buildEntitySelectorConfig(HeuristicConfigPolicy<Solution_> configPolicy) {
        EntitySelectorConfig entitySelectorConfig_;
        if (phaseConfig.getEntitySelectorConfig() == null) {
            entitySelectorConfig_ = new EntitySelectorConfig();
            EntityDescriptor<Solution_> entityDescriptor = deduceEntityDescriptor(configPolicy.getSolutionDescriptor());
            entitySelectorConfig_.setEntityClass(entityDescriptor.getEntityClass());
            if (EntitySelectorConfig.hasSorter(configPolicy.getEntitySorterManner(), entityDescriptor)) {
                entitySelectorConfig_.setCacheType(SelectionCacheType.PHASE);
                entitySelectorConfig_.setSelectionOrder(SelectionOrder.SORTED);
                entitySelectorConfig_.setSorterManner(configPolicy.getEntitySorterManner());
            }
        } else {
            entitySelectorConfig_ = phaseConfig.getEntitySelectorConfig();
        }
        if (entitySelectorConfig_.getCacheType() != null
                && entitySelectorConfig_.getCacheType().compareTo(SelectionCacheType.PHASE) < 0) {
            throw new IllegalArgumentException("The phaseConfig (" + phaseConfig
                    + ") cannot have an entitySelectorConfig (" + entitySelectorConfig_
                    + ") with a cacheType (" + entitySelectorConfig_.getCacheType()
                    + ") lower than " + SelectionCacheType.PHASE + ".");
        }
        return entitySelectorConfig_;
    }

    protected EntityDescriptor<Solution_> deduceEntityDescriptor(SolutionDescriptor<Solution_> solutionDescriptor) {
        Collection<EntityDescriptor<Solution_>> entityDescriptors = solutionDescriptor.getGenuineEntityDescriptors();
        if (entityDescriptors.size() != 1) {
            throw new IllegalArgumentException("The phaseConfig (" + phaseConfig
                    + ") has no entitySelector configured"
                    + " and because there are multiple in the entityClassSet (" + solutionDescriptor.getEntityClassSet()
                    + "), it cannot be deduced automatically.");
        }
        return entityDescriptors.iterator().next();
    }

    private ExhaustiveSearchDecider<Solution_> buildDecider(HeuristicConfigPolicy<Solution_> configPolicy,
            EntitySelector<Solution_> sourceEntitySelector, BestSolutionRecaller<Solution_> bestSolutionRecaller,
            Termination<Solution_> termination, boolean scoreBounderEnabled) {
        ManualEntityMimicRecorder<Solution_> manualEntityMimicRecorder =
                new ManualEntityMimicRecorder<>(sourceEntitySelector);
        String mimicSelectorId = sourceEntitySelector.getEntityDescriptor().getEntityClass().getName(); // TODO mimicSelectorId must be a field
        configPolicy.addEntityMimicRecorder(mimicSelectorId, manualEntityMimicRecorder);
        MoveSelectorConfig<?> moveSelectorConfig_ = buildMoveSelectorConfig(configPolicy,
                sourceEntitySelector, mimicSelectorId);
        MoveSelector<Solution_> moveSelector = MoveSelectorFactory.<Solution_> create(moveSelectorConfig_)
                .buildMoveSelector(configPolicy, SelectionCacheType.JUST_IN_TIME, SelectionOrder.ORIGINAL);
        ScoreBounder scoreBounder = scoreBounderEnabled
                ? new TrendBasedScoreBounder(configPolicy.getScoreDefinition(), configPolicy.getInitializingScoreTrend())
                : null;
        ExhaustiveSearchDecider<Solution_> decider = new ExhaustiveSearchDecider<>(configPolicy.getLogIndentation(),
                bestSolutionRecaller, termination,
                manualEntityMimicRecorder, moveSelector, scoreBounderEnabled, scoreBounder);
        EnvironmentMode environmentMode = configPolicy.getEnvironmentMode();
        if (environmentMode.isNonIntrusiveFullAsserted()) {
            decider.setAssertMoveScoreFromScratch(true);
        }
        if (environmentMode.isIntrusiveFastAsserted()) {
            decider.setAssertExpectedUndoMoveScore(true);
        }
        return decider;
    }

    private MoveSelectorConfig<?> buildMoveSelectorConfig(HeuristicConfigPolicy<Solution_> configPolicy,
            EntitySelector<Solution_> entitySelector, String mimicSelectorId) {
        MoveSelectorConfig<?> moveSelectorConfig_;
        if (phaseConfig.getMoveSelectorConfig() == null) {
            EntityDescriptor<Solution_> entityDescriptor = entitySelector.getEntityDescriptor();
            // Keep in sync with DefaultExhaustiveSearchPhase.fillLayerList()
            // which includes all genuineVariableDescriptors
            List<GenuineVariableDescriptor<Solution_>> variableDescriptorList =
                    entityDescriptor.getGenuineVariableDescriptorList();
            if (entityDescriptor.hasAnyGenuineListVariables()) {
                throw new IllegalArgumentException(
                        "Exhaustive Search does not support list variables (" + variableDescriptorList + ").");
            }
            List<MoveSelectorConfig> subMoveSelectorConfigList = new ArrayList<>(variableDescriptorList.size());
            for (GenuineVariableDescriptor<Solution_> variableDescriptor : variableDescriptorList) {
                ChangeMoveSelectorConfig changeMoveSelectorConfig = new ChangeMoveSelectorConfig();
                changeMoveSelectorConfig.setEntitySelectorConfig(
                        EntitySelectorConfig.newMimicSelectorConfig(mimicSelectorId));
                ValueSelectorConfig changeValueSelectorConfig = new ValueSelectorConfig();
                changeValueSelectorConfig.setVariableName(variableDescriptor.getVariableName());
                if (ValueSelectorConfig.hasSorter(configPolicy.getValueSorterManner(), variableDescriptor)) {
                    if (variableDescriptor.isValueRangeEntityIndependent()) {
                        changeValueSelectorConfig.setCacheType(SelectionCacheType.PHASE);
                    } else {
                        changeValueSelectorConfig.setCacheType(SelectionCacheType.STEP);
                    }
                    changeValueSelectorConfig.setSelectionOrder(SelectionOrder.SORTED);
                    changeValueSelectorConfig.setSorterManner(configPolicy.getValueSorterManner());
                }
                changeMoveSelectorConfig.setValueSelectorConfig(changeValueSelectorConfig);
                subMoveSelectorConfigList.add(changeMoveSelectorConfig);
            }
            if (subMoveSelectorConfigList.size() > 1) {
                moveSelectorConfig_ = new CartesianProductMoveSelectorConfig(subMoveSelectorConfigList);
            } else {
                moveSelectorConfig_ = subMoveSelectorConfigList.get(0);
            }
        } else {
            moveSelectorConfig_ = phaseConfig.getMoveSelectorConfig();
            // TODO Fail fast if it does not include all genuineVariableDescriptors as expected by DefaultExhaustiveSearchPhase.fillLayerList()
        }
        return moveSelectorConfig_;
    }
}
