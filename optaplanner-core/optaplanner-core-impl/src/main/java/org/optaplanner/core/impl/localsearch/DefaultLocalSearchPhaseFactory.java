package org.optaplanner.core.impl.localsearch;

import java.util.Collections;
import java.util.Objects;
import java.util.concurrent.ThreadFactory;

import org.optaplanner.core.config.heuristic.selector.common.SelectionCacheType;
import org.optaplanner.core.config.heuristic.selector.common.SelectionOrder;
import org.optaplanner.core.config.heuristic.selector.move.composite.UnionMoveSelectorConfig;
import org.optaplanner.core.config.heuristic.selector.move.generic.ChangeMoveSelectorConfig;
import org.optaplanner.core.config.heuristic.selector.move.generic.SwapMoveSelectorConfig;
import org.optaplanner.core.config.localsearch.LocalSearchPhaseConfig;
import org.optaplanner.core.config.localsearch.LocalSearchType;
import org.optaplanner.core.config.localsearch.decider.acceptor.AcceptorType;
import org.optaplanner.core.config.localsearch.decider.acceptor.LocalSearchAcceptorConfig;
import org.optaplanner.core.config.localsearch.decider.forager.LocalSearchForagerConfig;
import org.optaplanner.core.config.localsearch.decider.forager.LocalSearchPickEarlyType;
import org.optaplanner.core.config.solver.EnvironmentMode;
import org.optaplanner.core.impl.heuristic.HeuristicConfigPolicy;
import org.optaplanner.core.impl.heuristic.selector.move.MoveSelector;
import org.optaplanner.core.impl.heuristic.selector.move.MoveSelectorFactory;
import org.optaplanner.core.impl.heuristic.selector.move.composite.UnionMoveSelectorFactory;
import org.optaplanner.core.impl.localsearch.decider.LocalSearchDecider;
import org.optaplanner.core.impl.localsearch.decider.MultiThreadedLocalSearchDecider;
import org.optaplanner.core.impl.localsearch.decider.acceptor.Acceptor;
import org.optaplanner.core.impl.localsearch.decider.acceptor.AcceptorFactory;
import org.optaplanner.core.impl.localsearch.decider.forager.LocalSearchForager;
import org.optaplanner.core.impl.localsearch.decider.forager.LocalSearchForagerFactory;
import org.optaplanner.core.impl.phase.AbstractPhaseFactory;
import org.optaplanner.core.impl.solver.recaller.BestSolutionRecaller;
import org.optaplanner.core.impl.solver.termination.Termination;
import org.optaplanner.core.impl.solver.thread.ChildThreadType;

public class DefaultLocalSearchPhaseFactory<Solution_> extends AbstractPhaseFactory<Solution_, LocalSearchPhaseConfig> {

    public DefaultLocalSearchPhaseFactory(LocalSearchPhaseConfig phaseConfig) {
        super(phaseConfig);
    }

    @Override
    public LocalSearchPhase<Solution_> buildPhase(int phaseIndex, HeuristicConfigPolicy<Solution_> solverConfigPolicy,
            BestSolutionRecaller<Solution_> bestSolutionRecaller, Termination<Solution_> solverTermination) {
        HeuristicConfigPolicy<Solution_> phaseConfigPolicy = solverConfigPolicy.createPhaseConfigPolicy();
        Termination<Solution_> phaseTermination = buildPhaseTermination(phaseConfigPolicy, solverTermination);
        DefaultLocalSearchPhase.Builder<Solution_> builder = new DefaultLocalSearchPhase.Builder<>(
                phaseIndex,
                solverConfigPolicy.getLogIndentation(),
                phaseTermination,
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

    private LocalSearchDecider<Solution_> buildDecider(HeuristicConfigPolicy<Solution_> configPolicy,
            Termination<Solution_> termination) {
        MoveSelector<Solution_> moveSelector = buildMoveSelector(configPolicy);
        Acceptor<Solution_> acceptor = buildAcceptor(configPolicy);
        LocalSearchForager<Solution_> forager = buildForager(configPolicy);
        if (moveSelector.isNeverEnding() && !forager.supportsNeverEndingMoveSelector()) {
            throw new IllegalStateException("The moveSelector (" + moveSelector
                    + ") has neverEnding (" + moveSelector.isNeverEnding()
                    + "), but the forager (" + forager
                    + ") does not support it.\n"
                    + "Maybe configure the <forager> with an <acceptedCountLimit>.");
        }
        Integer moveThreadCount = configPolicy.getMoveThreadCount();
        EnvironmentMode environmentMode = configPolicy.getEnvironmentMode();
        LocalSearchDecider<Solution_> decider;
        if (moveThreadCount == null) {
            decider = new LocalSearchDecider<>(configPolicy.getLogIndentation(), termination, moveSelector, acceptor, forager);
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
            MultiThreadedLocalSearchDecider<Solution_> multiThreadedDecider = new MultiThreadedLocalSearchDecider<>(
                    configPolicy.getLogIndentation(), termination, moveSelector, acceptor, forager,
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

    protected Acceptor<Solution_> buildAcceptor(HeuristicConfigPolicy<Solution_> configPolicy) {
        LocalSearchAcceptorConfig acceptorConfig_;
        if (phaseConfig.getAcceptorConfig() != null) {
            if (phaseConfig.getLocalSearchType() != null) {
                throw new IllegalArgumentException("The localSearchType (" + phaseConfig.getLocalSearchType()
                        + ") must not be configured if the acceptorConfig (" + phaseConfig.getAcceptorConfig()
                        + ") is explicitly configured.");
            }
            acceptorConfig_ = phaseConfig.getAcceptorConfig();
        } else {
            LocalSearchType localSearchType_ =
                    Objects.requireNonNullElse(phaseConfig.getLocalSearchType(), LocalSearchType.LATE_ACCEPTANCE);
            acceptorConfig_ = new LocalSearchAcceptorConfig();
            switch (localSearchType_) {
                case HILL_CLIMBING:
                case VARIABLE_NEIGHBORHOOD_DESCENT:
                    acceptorConfig_.setAcceptorTypeList(Collections.singletonList(AcceptorType.HILL_CLIMBING));
                    break;
                case TABU_SEARCH:
                    acceptorConfig_.setAcceptorTypeList(Collections.singletonList(AcceptorType.ENTITY_TABU));
                    break;
                case SIMULATED_ANNEALING:
                    acceptorConfig_.setAcceptorTypeList(Collections.singletonList(AcceptorType.SIMULATED_ANNEALING));
                    break;
                case LATE_ACCEPTANCE:
                    acceptorConfig_.setAcceptorTypeList(Collections.singletonList(AcceptorType.LATE_ACCEPTANCE));
                    break;
                case GREAT_DELUGE:
                    acceptorConfig_.setAcceptorTypeList(Collections.singletonList(AcceptorType.GREAT_DELUGE));
                    break;
                default:
                    throw new IllegalStateException("The localSearchType (" + localSearchType_
                            + ") is not implemented.");
            }
        }
        return AcceptorFactory.<Solution_> create(acceptorConfig_)
                .buildAcceptor(configPolicy);
    }

    protected LocalSearchForager<Solution_> buildForager(HeuristicConfigPolicy<Solution_> configPolicy) {
        LocalSearchForagerConfig foragerConfig_;
        if (phaseConfig.getForagerConfig() != null) {
            if (phaseConfig.getLocalSearchType() != null) {
                throw new IllegalArgumentException("The localSearchType (" + phaseConfig.getLocalSearchType()
                        + ") must not be configured if the foragerConfig (" + phaseConfig.getForagerConfig()
                        + ") is explicitly configured.");
            }
            foragerConfig_ = phaseConfig.getForagerConfig();
        } else {
            LocalSearchType localSearchType_ =
                    Objects.requireNonNullElse(phaseConfig.getLocalSearchType(), LocalSearchType.LATE_ACCEPTANCE);
            foragerConfig_ = new LocalSearchForagerConfig();
            switch (localSearchType_) {
                case HILL_CLIMBING:
                    foragerConfig_.setAcceptedCountLimit(1);
                    break;
                case TABU_SEARCH:
                    // Slow stepping algorithm
                    foragerConfig_.setAcceptedCountLimit(1000);
                    break;
                case SIMULATED_ANNEALING:
                case LATE_ACCEPTANCE:
                case GREAT_DELUGE:
                    // Fast stepping algorithm
                    foragerConfig_.setAcceptedCountLimit(1);
                    break;
                case VARIABLE_NEIGHBORHOOD_DESCENT:
                    foragerConfig_.setPickEarlyType(LocalSearchPickEarlyType.FIRST_LAST_STEP_SCORE_IMPROVING);
                    break;
                default:
                    throw new IllegalStateException("The localSearchType (" + localSearchType_
                            + ") is not implemented.");
            }
        }
        return LocalSearchForagerFactory.<Solution_> create(foragerConfig_).buildForager();
    }

    protected MoveSelector<Solution_> buildMoveSelector(HeuristicConfigPolicy<Solution_> configPolicy) {
        MoveSelector<Solution_> moveSelector;
        SelectionCacheType defaultCacheType = SelectionCacheType.JUST_IN_TIME;
        SelectionOrder defaultSelectionOrder;
        if (phaseConfig.getLocalSearchType() == LocalSearchType.VARIABLE_NEIGHBORHOOD_DESCENT) {
            defaultSelectionOrder = SelectionOrder.ORIGINAL;
        } else {
            defaultSelectionOrder = SelectionOrder.RANDOM;
        }
        if (phaseConfig.getMoveSelectorConfig() == null) {
            // Default to changeMoveSelector and swapMoveSelector
            UnionMoveSelectorConfig unionMoveSelectorConfig = new UnionMoveSelectorConfig().withMoveSelectors(
                    new ChangeMoveSelectorConfig(),
                    new SwapMoveSelectorConfig());
            moveSelector = new UnionMoveSelectorFactory<Solution_>(unionMoveSelectorConfig)
                    .buildMoveSelector(configPolicy, defaultCacheType, defaultSelectionOrder);
        } else {
            moveSelector = MoveSelectorFactory.<Solution_> create(phaseConfig.getMoveSelectorConfig())
                    .buildMoveSelector(configPolicy, defaultCacheType, defaultSelectionOrder);
        }
        return moveSelector;
    }
}
