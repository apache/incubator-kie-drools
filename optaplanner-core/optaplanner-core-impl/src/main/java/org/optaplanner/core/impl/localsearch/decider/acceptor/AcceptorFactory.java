package org.optaplanner.core.impl.localsearch.decider.acceptor;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.optaplanner.core.config.localsearch.decider.acceptor.AcceptorType;
import org.optaplanner.core.config.localsearch.decider.acceptor.LocalSearchAcceptorConfig;
import org.optaplanner.core.config.localsearch.decider.acceptor.stepcountinghillclimbing.StepCountingHillClimbingType;
import org.optaplanner.core.impl.heuristic.HeuristicConfigPolicy;
import org.optaplanner.core.impl.localsearch.decider.acceptor.greatdeluge.GreatDelugeAcceptor;
import org.optaplanner.core.impl.localsearch.decider.acceptor.hillclimbing.HillClimbingAcceptor;
import org.optaplanner.core.impl.localsearch.decider.acceptor.lateacceptance.LateAcceptanceAcceptor;
import org.optaplanner.core.impl.localsearch.decider.acceptor.simulatedannealing.SimulatedAnnealingAcceptor;
import org.optaplanner.core.impl.localsearch.decider.acceptor.stepcountinghillclimbing.StepCountingHillClimbingAcceptor;
import org.optaplanner.core.impl.localsearch.decider.acceptor.tabu.EntityTabuAcceptor;
import org.optaplanner.core.impl.localsearch.decider.acceptor.tabu.MoveTabuAcceptor;
import org.optaplanner.core.impl.localsearch.decider.acceptor.tabu.ValueTabuAcceptor;
import org.optaplanner.core.impl.localsearch.decider.acceptor.tabu.size.EntityRatioTabuSizeStrategy;
import org.optaplanner.core.impl.localsearch.decider.acceptor.tabu.size.FixedTabuSizeStrategy;
import org.optaplanner.core.impl.localsearch.decider.acceptor.tabu.size.ValueRatioTabuSizeStrategy;

public class AcceptorFactory<Solution_> {

    // Based on Tomas Muller's work. TODO Confirm with benchmark across our examples/datasets
    private static final double DEFAULT_WATER_LEVEL_INCREMENT_RATIO = 0.00_000_005;

    public static <Solution_> AcceptorFactory<Solution_> create(LocalSearchAcceptorConfig acceptorConfig) {
        return new AcceptorFactory<>(acceptorConfig);
    }

    private final LocalSearchAcceptorConfig acceptorConfig;

    public AcceptorFactory(LocalSearchAcceptorConfig acceptorConfig) {
        this.acceptorConfig = acceptorConfig;
    }

    public Acceptor<Solution_> buildAcceptor(HeuristicConfigPolicy<Solution_> configPolicy) {
        List<Acceptor<Solution_>> acceptorList = Stream.of(
                buildHillClimbingAcceptor(),
                buildStepCountingHillClimbingAcceptor(),
                buildEntityTabuAcceptor(configPolicy),
                buildValueTabuAcceptor(configPolicy),
                buildMoveTabuAcceptor(configPolicy),
                buildUndoMoveTabuAcceptor(configPolicy),
                buildSimulatedAnnealingAcceptor(configPolicy),
                buildLateAcceptanceAcceptor(),
                buildGreatDelugeAcceptor(configPolicy))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());

        if (acceptorList.size() == 1) {
            return acceptorList.get(0);
        } else if (acceptorList.size() > 1) {
            return new CompositeAcceptor<>(acceptorList);
        } else {
            throw new IllegalArgumentException(
                    "The acceptor does not specify any acceptorType (" + acceptorConfig.getAcceptorTypeList()
                            + ") or other acceptor property.\n"
                            + "For a good starting values,"
                            + " see the docs section \"Which optimization algorithms should I use?\".");
        }
    }

    private Optional<HillClimbingAcceptor<Solution_>> buildHillClimbingAcceptor() {
        if (acceptorConfig.getAcceptorTypeList() != null
                && acceptorConfig.getAcceptorTypeList().contains(AcceptorType.HILL_CLIMBING)) {
            HillClimbingAcceptor<Solution_> acceptor = new HillClimbingAcceptor<>();
            return Optional.of(acceptor);
        }
        return Optional.empty();
    }

    private Optional<StepCountingHillClimbingAcceptor<Solution_>> buildStepCountingHillClimbingAcceptor() {
        if ((acceptorConfig.getAcceptorTypeList() != null
                && acceptorConfig.getAcceptorTypeList().contains(AcceptorType.STEP_COUNTING_HILL_CLIMBING))
                || acceptorConfig.getStepCountingHillClimbingSize() != null) {
            int stepCountingHillClimbingSize_ =
                    Objects.requireNonNullElse(acceptorConfig.getStepCountingHillClimbingSize(), 400);
            StepCountingHillClimbingType stepCountingHillClimbingType_ =
                    Objects.requireNonNullElse(acceptorConfig.getStepCountingHillClimbingType(),
                            StepCountingHillClimbingType.STEP);
            StepCountingHillClimbingAcceptor<Solution_> acceptor = new StepCountingHillClimbingAcceptor<>(
                    stepCountingHillClimbingSize_, stepCountingHillClimbingType_);
            return Optional.of(acceptor);
        }
        return Optional.empty();
    }

    private Optional<EntityTabuAcceptor<Solution_>> buildEntityTabuAcceptor(HeuristicConfigPolicy<Solution_> configPolicy) {
        if ((acceptorConfig.getAcceptorTypeList() != null
                && acceptorConfig.getAcceptorTypeList().contains(AcceptorType.ENTITY_TABU))
                || acceptorConfig.getEntityTabuSize() != null || acceptorConfig.getEntityTabuRatio() != null
                || acceptorConfig.getFadingEntityTabuSize() != null || acceptorConfig.getFadingEntityTabuRatio() != null) {
            EntityTabuAcceptor<Solution_> acceptor = new EntityTabuAcceptor<>(configPolicy.getLogIndentation());
            if (acceptorConfig.getEntityTabuSize() != null) {
                if (acceptorConfig.getEntityTabuRatio() != null) {
                    throw new IllegalArgumentException("The acceptor cannot have both acceptorConfig.getEntityTabuSize() ("
                            + acceptorConfig.getEntityTabuSize() + ") and acceptorConfig.getEntityTabuRatio() ("
                            + acceptorConfig.getEntityTabuRatio() + ").");
                }
                acceptor.setTabuSizeStrategy(new FixedTabuSizeStrategy<>(acceptorConfig.getEntityTabuSize()));
            } else if (acceptorConfig.getEntityTabuRatio() != null) {
                acceptor.setTabuSizeStrategy(new EntityRatioTabuSizeStrategy<>(acceptorConfig.getEntityTabuRatio()));
            } else if (acceptorConfig.getFadingEntityTabuSize() == null && acceptorConfig.getFadingEntityTabuRatio() == null) {
                acceptor.setTabuSizeStrategy(new EntityRatioTabuSizeStrategy<>(0.1));
            }
            if (acceptorConfig.getFadingEntityTabuSize() != null) {
                if (acceptorConfig.getFadingEntityTabuRatio() != null) {
                    throw new IllegalArgumentException(
                            "The acceptor cannot have both acceptorConfig.getFadingEntityTabuSize() ("
                                    + acceptorConfig.getFadingEntityTabuSize()
                                    + ") and acceptorConfig.getFadingEntityTabuRatio() ("
                                    + acceptorConfig.getFadingEntityTabuRatio() + ").");
                }
                acceptor.setFadingTabuSizeStrategy(new FixedTabuSizeStrategy<>(acceptorConfig.getFadingEntityTabuSize()));
            } else if (acceptorConfig.getFadingEntityTabuRatio() != null) {
                acceptor.setFadingTabuSizeStrategy(
                        new EntityRatioTabuSizeStrategy<>(acceptorConfig.getFadingEntityTabuRatio()));
            }
            if (configPolicy.getEnvironmentMode().isNonIntrusiveFullAsserted()) {
                acceptor.setAssertTabuHashCodeCorrectness(true);
            }
            return Optional.of(acceptor);
        }
        return Optional.empty();
    }

    private Optional<ValueTabuAcceptor<Solution_>> buildValueTabuAcceptor(HeuristicConfigPolicy<Solution_> configPolicy) {
        if ((acceptorConfig.getAcceptorTypeList() != null
                && acceptorConfig.getAcceptorTypeList().contains(AcceptorType.VALUE_TABU))
                || acceptorConfig.getValueTabuSize() != null || acceptorConfig.getValueTabuRatio() != null
                || acceptorConfig.getFadingValueTabuSize() != null || acceptorConfig.getFadingValueTabuRatio() != null) {
            ValueTabuAcceptor<Solution_> acceptor = new ValueTabuAcceptor<>(configPolicy.getLogIndentation());
            if (acceptorConfig.getValueTabuSize() != null) {
                if (acceptorConfig.getValueTabuRatio() != null) {
                    throw new IllegalArgumentException("The acceptor cannot have both acceptorConfig.getValueTabuSize() ("
                            + acceptorConfig.getValueTabuSize() + ") and acceptorConfig.getValueTabuRatio() ("
                            + acceptorConfig.getValueTabuRatio() + ").");
                }
                acceptor.setTabuSizeStrategy(new FixedTabuSizeStrategy<>(acceptorConfig.getValueTabuSize()));
            } else if (acceptorConfig.getValueTabuRatio() != null) {
                acceptor.setTabuSizeStrategy(new ValueRatioTabuSizeStrategy<>(acceptorConfig.getValueTabuRatio()));
            }
            if (acceptorConfig.getFadingValueTabuSize() != null) {
                if (acceptorConfig.getFadingValueTabuRatio() != null) {
                    throw new IllegalArgumentException("The acceptor cannot have both acceptorConfig.getFadingValueTabuSize() ("
                            + acceptorConfig.getFadingValueTabuSize() + ") and acceptorConfig.getFadingValueTabuRatio() ("
                            + acceptorConfig.getFadingValueTabuRatio() + ").");
                }
                acceptor.setFadingTabuSizeStrategy(new FixedTabuSizeStrategy<>(acceptorConfig.getFadingValueTabuSize()));
            } else if (acceptorConfig.getFadingValueTabuRatio() != null) {
                acceptor.setFadingTabuSizeStrategy(new ValueRatioTabuSizeStrategy<>(acceptorConfig.getFadingValueTabuRatio()));
            }

            if (acceptorConfig.getValueTabuSize() != null) {
                acceptor.setTabuSizeStrategy(new FixedTabuSizeStrategy<>(acceptorConfig.getValueTabuSize()));
            }
            if (acceptorConfig.getFadingValueTabuSize() != null) {
                acceptor.setFadingTabuSizeStrategy(new FixedTabuSizeStrategy<>(acceptorConfig.getFadingValueTabuSize()));
            }
            if (configPolicy.getEnvironmentMode().isNonIntrusiveFullAsserted()) {
                acceptor.setAssertTabuHashCodeCorrectness(true);
            }
            return Optional.of(acceptor);
        }
        return Optional.empty();
    }

    private Optional<MoveTabuAcceptor<Solution_>> buildMoveTabuAcceptor(HeuristicConfigPolicy<Solution_> configPolicy) {
        if ((acceptorConfig.getAcceptorTypeList() != null
                && acceptorConfig.getAcceptorTypeList().contains(AcceptorType.MOVE_TABU))
                || acceptorConfig.getMoveTabuSize() != null || acceptorConfig.getFadingMoveTabuSize() != null) {
            MoveTabuAcceptor<Solution_> acceptor = new MoveTabuAcceptor<>(configPolicy.getLogIndentation());
            acceptor.setUseUndoMoveAsTabuMove(false);
            if (acceptorConfig.getMoveTabuSize() != null) {
                acceptor.setTabuSizeStrategy(new FixedTabuSizeStrategy<>(acceptorConfig.getMoveTabuSize()));
            }
            if (acceptorConfig.getFadingMoveTabuSize() != null) {
                acceptor.setFadingTabuSizeStrategy(new FixedTabuSizeStrategy<>(acceptorConfig.getFadingMoveTabuSize()));
            }
            if (configPolicy.getEnvironmentMode().isNonIntrusiveFullAsserted()) {
                acceptor.setAssertTabuHashCodeCorrectness(true);
            }
            return Optional.of(acceptor);
        }
        return Optional.empty();
    }

    private Optional<MoveTabuAcceptor<Solution_>> buildUndoMoveTabuAcceptor(HeuristicConfigPolicy<Solution_> configPolicy) {
        if ((acceptorConfig.getAcceptorTypeList() != null
                && acceptorConfig.getAcceptorTypeList().contains(AcceptorType.UNDO_MOVE_TABU))
                || acceptorConfig.getUndoMoveTabuSize() != null || acceptorConfig.getFadingUndoMoveTabuSize() != null) {
            MoveTabuAcceptor<Solution_> acceptor = new MoveTabuAcceptor<>(configPolicy.getLogIndentation());
            acceptor.setUseUndoMoveAsTabuMove(true);
            if (acceptorConfig.getUndoMoveTabuSize() != null) {
                acceptor.setTabuSizeStrategy(new FixedTabuSizeStrategy<>(acceptorConfig.getUndoMoveTabuSize()));
            }
            if (acceptorConfig.getFadingUndoMoveTabuSize() != null) {
                acceptor.setFadingTabuSizeStrategy(new FixedTabuSizeStrategy<>(acceptorConfig.getFadingUndoMoveTabuSize()));
            }
            if (configPolicy.getEnvironmentMode().isNonIntrusiveFullAsserted()) {
                acceptor.setAssertTabuHashCodeCorrectness(true);
            }
            return Optional.of(acceptor);
        }
        return Optional.empty();
    }

    private Optional<SimulatedAnnealingAcceptor<Solution_>>
            buildSimulatedAnnealingAcceptor(HeuristicConfigPolicy<Solution_> configPolicy) {
        if ((acceptorConfig.getAcceptorTypeList() != null
                && acceptorConfig.getAcceptorTypeList().contains(AcceptorType.SIMULATED_ANNEALING))
                || acceptorConfig.getSimulatedAnnealingStartingTemperature() != null) {
            SimulatedAnnealingAcceptor<Solution_> acceptor = new SimulatedAnnealingAcceptor<>();
            if (acceptorConfig.getSimulatedAnnealingStartingTemperature() == null) {
                // TODO Support SA without a parameter
                throw new IllegalArgumentException("The acceptorType (" + AcceptorType.SIMULATED_ANNEALING
                        + ") currently requires a acceptorConfig.getSimulatedAnnealingStartingTemperature() ("
                        + acceptorConfig.getSimulatedAnnealingStartingTemperature() + ").");
            }
            acceptor.setStartingTemperature(
                    configPolicy.getScoreDefinition().parseScore(acceptorConfig.getSimulatedAnnealingStartingTemperature()));
            return Optional.of(acceptor);
        }
        return Optional.empty();
    }

    private Optional<LateAcceptanceAcceptor<Solution_>> buildLateAcceptanceAcceptor() {
        if ((acceptorConfig.getAcceptorTypeList() != null
                && acceptorConfig.getAcceptorTypeList().contains(AcceptorType.LATE_ACCEPTANCE))
                || acceptorConfig.getLateAcceptanceSize() != null) {
            LateAcceptanceAcceptor<Solution_> acceptor = new LateAcceptanceAcceptor<>();
            acceptor.setLateAcceptanceSize(Objects.requireNonNullElse(acceptorConfig.getLateAcceptanceSize(), 400));
            return Optional.of(acceptor);
        }
        return Optional.empty();
    }

    private Optional<GreatDelugeAcceptor<Solution_>> buildGreatDelugeAcceptor(HeuristicConfigPolicy<Solution_> configPolicy) {
        if ((acceptorConfig.getAcceptorTypeList() != null
                && acceptorConfig.getAcceptorTypeList().contains(AcceptorType.GREAT_DELUGE))
                || acceptorConfig.getGreatDelugeWaterLevelIncrementScore() != null
                || acceptorConfig.getGreatDelugeWaterLevelIncrementRatio() != null) {
            GreatDelugeAcceptor<Solution_> acceptor = new GreatDelugeAcceptor<>();
            if (acceptorConfig.getGreatDelugeWaterLevelIncrementScore() != null) {
                if (acceptorConfig.getGreatDelugeWaterLevelIncrementRatio() != null) {
                    throw new IllegalArgumentException("The acceptor cannot have both a "
                            + "acceptorConfig.getGreatDelugeWaterLevelIncrementScore() ("
                            + acceptorConfig.getGreatDelugeWaterLevelIncrementScore()
                            + ") and a acceptorConfig.getGreatDelugeWaterLevelIncrementRatio() ("
                            + acceptorConfig.getGreatDelugeWaterLevelIncrementRatio() + ").");
                }
                acceptor.setWaterLevelIncrementScore(
                        configPolicy.getScoreDefinition().parseScore(acceptorConfig.getGreatDelugeWaterLevelIncrementScore()));
            } else if (acceptorConfig.getGreatDelugeWaterLevelIncrementRatio() != null) {
                if (acceptorConfig.getGreatDelugeWaterLevelIncrementRatio() <= 0.0) {
                    throw new IllegalArgumentException("The acceptorConfig.getGreatDelugeWaterLevelIncrementRatio() ("
                            + acceptorConfig.getGreatDelugeWaterLevelIncrementRatio()
                            + ") must be positive because the water level should increase.");
                }
                acceptor.setWaterLevelIncrementRatio(acceptorConfig.getGreatDelugeWaterLevelIncrementRatio());
            } else {
                acceptor.setWaterLevelIncrementRatio(DEFAULT_WATER_LEVEL_INCREMENT_RATIO);
            }
            return Optional.of(acceptor);
        }
        return Optional.empty();
    }
}
