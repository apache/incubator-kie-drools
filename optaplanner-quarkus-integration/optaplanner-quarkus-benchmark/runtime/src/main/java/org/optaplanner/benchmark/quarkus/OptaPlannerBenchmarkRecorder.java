package org.optaplanner.benchmark.quarkus;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.optaplanner.benchmark.config.PlannerBenchmarkConfig;
import org.optaplanner.benchmark.config.SolverBenchmarkConfig;
import org.optaplanner.benchmark.quarkus.config.OptaPlannerBenchmarkRuntimeConfig;
import org.optaplanner.core.config.localsearch.LocalSearchPhaseConfig;
import org.optaplanner.core.config.phase.PhaseConfig;
import org.optaplanner.core.config.score.director.ScoreDirectorFactoryConfig;
import org.optaplanner.core.config.solver.SolverConfig;
import org.optaplanner.core.config.solver.termination.TerminationConfig;

import io.quarkus.arc.Arc;
import io.quarkus.runtime.annotations.Recorder;

@Recorder
public class OptaPlannerBenchmarkRecorder {
    public Supplier<PlannerBenchmarkConfig> benchmarkConfigSupplier(PlannerBenchmarkConfig benchmarkConfig) {
        return () -> {
            OptaPlannerBenchmarkRuntimeConfig optaPlannerRuntimeConfig =
                    Arc.container().instance(OptaPlannerBenchmarkRuntimeConfig.class).get();
            SolverConfig solverConfig =
                    Arc.container().instance(SolverConfig.class).get();
            return updateBenchmarkConfigWithRuntimeProperties(benchmarkConfig, optaPlannerRuntimeConfig, solverConfig);
        };
    }

    private PlannerBenchmarkConfig updateBenchmarkConfigWithRuntimeProperties(PlannerBenchmarkConfig plannerBenchmarkConfig,
            OptaPlannerBenchmarkRuntimeConfig benchmarkRuntimeConfig,
            SolverConfig solverConfig) {
        if (plannerBenchmarkConfig == null) { // no benchmarkConfig.xml provided
            // Can't do this in processor; SolverConfig is not completed yet (has some runtime properties)
            plannerBenchmarkConfig = PlannerBenchmarkConfig.createFromSolverConfig(solverConfig);
        }

        plannerBenchmarkConfig.setBenchmarkDirectory(new File(benchmarkRuntimeConfig.resultDirectory));
        SolverBenchmarkConfig inheritedBenchmarkConfig = plannerBenchmarkConfig.getInheritedSolverBenchmarkConfig();

        if (plannerBenchmarkConfig.getSolverBenchmarkBluePrintConfigList() != null) {
            if (inheritedBenchmarkConfig == null) {
                inheritedBenchmarkConfig = new SolverBenchmarkConfig();
                plannerBenchmarkConfig.setInheritedSolverBenchmarkConfig(inheritedBenchmarkConfig);
                inheritedBenchmarkConfig.setSolverConfig(solverConfig.copyConfig());
            }
            TerminationConfig inheritedTerminationConfig;
            if (inheritedBenchmarkConfig.getSolverConfig().getTerminationConfig() != null) {
                inheritedTerminationConfig = inheritedBenchmarkConfig.getSolverConfig().getTerminationConfig();
            } else {
                inheritedTerminationConfig = new TerminationConfig();
                inheritedBenchmarkConfig.getSolverConfig().setTerminationConfig(inheritedTerminationConfig);
            }
            benchmarkRuntimeConfig.termination.spentLimit.ifPresent(inheritedTerminationConfig::setSpentLimit);
            benchmarkRuntimeConfig.termination.unimprovedSpentLimit
                    .ifPresent(inheritedTerminationConfig::setUnimprovedSpentLimit);
            benchmarkRuntimeConfig.termination.bestScoreLimit.ifPresent(inheritedTerminationConfig::setBestScoreLimit);
        }

        TerminationConfig inheritedTerminationConfig = null;
        if (inheritedBenchmarkConfig != null && inheritedBenchmarkConfig.getSolverConfig() != null &&
                inheritedBenchmarkConfig.getSolverConfig().getTerminationConfig() != null) {
            inheritedTerminationConfig = inheritedBenchmarkConfig.getSolverConfig().getTerminationConfig();
        }

        if (inheritedTerminationConfig == null || !inheritedTerminationConfig.isConfigured()) {
            List<SolverBenchmarkConfig> solverBenchmarkConfigList = plannerBenchmarkConfig.getSolverBenchmarkConfigList();
            List<String> unconfiguredTerminationSolverBenchmarkList = new ArrayList<>();
            if (solverBenchmarkConfigList == null) {
                throw new IllegalStateException("At least one of the properties " +
                        "quarkus.optaplanner.benchmark.solver.termination.spent-limit, " +
                        "quarkus.optaplanner.benchmark.solver.termination.best-score-limit, " +
                        "quarkus.optaplanner.benchmark.solver.termination.unimproved-spent-limit " +
                        "is required if termination is not configured in the " +
                        "inherited solver benchmark config and solverBenchmarkBluePrint is used.");
            }
            for (int i = 0; i < solverBenchmarkConfigList.size(); i++) {
                SolverBenchmarkConfig solverBenchmarkConfig = solverBenchmarkConfigList.get(i);
                if (solverBenchmarkConfig.getSolverConfig() == null) {
                    solverBenchmarkConfig.setSolverConfig(new SolverConfig());
                }
                TerminationConfig terminationConfig = solverBenchmarkConfig.getSolverConfig().getTerminationConfig();
                if (terminationConfig == null) {
                    terminationConfig = new TerminationConfig();
                    solverBenchmarkConfig.getSolverConfig().setTerminationConfig(terminationConfig);
                } else if (terminationConfig.isConfigured()) {
                    continue;
                }

                benchmarkRuntimeConfig.termination.spentLimit.ifPresent(terminationConfig::setSpentLimit);
                benchmarkRuntimeConfig.termination.unimprovedSpentLimit
                        .ifPresent(terminationConfig::setUnimprovedSpentLimit);
                benchmarkRuntimeConfig.termination.bestScoreLimit.ifPresent(terminationConfig::setBestScoreLimit);

                if (!terminationConfig.isConfigured()) {
                    List<PhaseConfig> phaseConfigList = solverBenchmarkConfig.getSolverConfig().getPhaseConfigList();
                    boolean isTerminationConfiguredForAllLocalSearchPhases =
                            phaseConfigList != null && !phaseConfigList.isEmpty();

                    if (isTerminationConfiguredForAllLocalSearchPhases) {
                        for (PhaseConfig<?> phaseConfig : phaseConfigList) {
                            if (phaseConfig instanceof LocalSearchPhaseConfig) {
                                if (phaseConfig.getTerminationConfig() == null
                                        || !phaseConfig.getTerminationConfig().isConfigured()) {
                                    isTerminationConfiguredForAllLocalSearchPhases = false;
                                    break;
                                }
                            }
                        }
                    }
                    if (!isTerminationConfiguredForAllLocalSearchPhases) {
                        String benchmarkConfigName = solverBenchmarkConfig.getName();
                        if (benchmarkConfigName == null) {
                            benchmarkConfigName = "SolverBenchmarkConfig " + i;
                        }
                        unconfiguredTerminationSolverBenchmarkList.add(benchmarkConfigName);
                    }
                }
            }
            if (!unconfiguredTerminationSolverBenchmarkList.isEmpty()) {
                throw new IllegalStateException("The following " + SolverBenchmarkConfig.class.getSimpleName() + " do not " +
                        "have termination configured: " +
                        unconfiguredTerminationSolverBenchmarkList.stream()
                                .collect(Collectors.joining(", ", "[", "]"))
                        + ". " +
                        "At least one of the properties " +
                        "quarkus.optaplanner.benchmark.solver.termination.spent-limit, " +
                        "quarkus.optaplanner.benchmark.solver.termination.best-score-limit, " +
                        "quarkus.optaplanner.benchmark.solver.termination.unimproved-spent-limit " +
                        "is required if termination is not configured in a solver benchmark and the " +
                        "inherited solver benchmark config.");
            }
        }

        if (plannerBenchmarkConfig.getSolverBenchmarkConfigList() != null) {
            for (SolverBenchmarkConfig childBenchmarkConfig : plannerBenchmarkConfig.getSolverBenchmarkConfigList()) {
                if (childBenchmarkConfig.getSolverConfig() == null) {
                    childBenchmarkConfig.setSolverConfig(new SolverConfig());
                }
                inheritPropertiesFromSolverConfig(childBenchmarkConfig, inheritedBenchmarkConfig, solverConfig);
            }
        }

        if (plannerBenchmarkConfig.getSolverBenchmarkConfigList() == null
                && plannerBenchmarkConfig.getSolverBenchmarkBluePrintConfigList() == null) {
            plannerBenchmarkConfig.setSolverBenchmarkConfigList(Collections.singletonList(new SolverBenchmarkConfig()));
        }
        return plannerBenchmarkConfig;
    }

    private void inheritPropertiesFromSolverConfig(SolverBenchmarkConfig childBenchmarkConfig,
            SolverBenchmarkConfig inheritedBenchmarkConfig,
            SolverConfig solverConfig) {
        inheritProperty(childBenchmarkConfig, inheritedBenchmarkConfig, solverConfig,
                SolverConfig::getSolutionClass, SolverConfig::setSolutionClass);
        inheritProperty(childBenchmarkConfig, inheritedBenchmarkConfig, solverConfig,
                SolverConfig::getEntityClassList, SolverConfig::setEntityClassList);
        inheritScoreCalculation(childBenchmarkConfig, inheritedBenchmarkConfig, solverConfig);
    }

    private <T> void inheritProperty(SolverBenchmarkConfig childBenchmarkConfig,
            SolverBenchmarkConfig inheritedBenchmarkConfig,
            SolverConfig solverConfig,
            Function<SolverConfig, T> getter,
            BiConsumer<SolverConfig, T> setter) {
        if (getter.apply(childBenchmarkConfig.getSolverConfig()) != null) {
            return;
        }
        if (inheritedBenchmarkConfig != null && inheritedBenchmarkConfig.getSolverConfig() != null &&
                getter.apply(inheritedBenchmarkConfig.getSolverConfig()) != null) {
            return;
        }
        setter.accept(childBenchmarkConfig.getSolverConfig(), getter.apply(solverConfig));
    }

    private void inheritScoreCalculation(SolverBenchmarkConfig childBenchmarkConfig,
            SolverBenchmarkConfig inheritedBenchmarkConfig,
            SolverConfig solverConfig) {

        if (isScoreCalculationDefined(childBenchmarkConfig.getSolverConfig())) {
            return;
        }
        if (inheritedBenchmarkConfig != null && inheritedBenchmarkConfig.getSolverConfig() != null &&
                isScoreCalculationDefined(inheritedBenchmarkConfig.getSolverConfig())) {
            return;
        }
        ScoreDirectorFactoryConfig childScoreDirectorFactoryConfig = childBenchmarkConfig.getSolverConfig()
                .getScoreDirectorFactoryConfig();
        ScoreDirectorFactoryConfig inheritedScoreDirectorFactoryConfig = solverConfig.getScoreDirectorFactoryConfig();
        if (childScoreDirectorFactoryConfig == null) {
            childScoreDirectorFactoryConfig = new ScoreDirectorFactoryConfig();
            childBenchmarkConfig.getSolverConfig().setScoreDirectorFactoryConfig(childScoreDirectorFactoryConfig);
        }
        childScoreDirectorFactoryConfig.inherit(inheritedScoreDirectorFactoryConfig);
    }

    private boolean isScoreCalculationDefined(SolverConfig solverConfig) {
        if (solverConfig == null) {
            return false;
        }
        ScoreDirectorFactoryConfig scoreDirectorFactoryConfig = solverConfig.getScoreDirectorFactoryConfig();
        if (scoreDirectorFactoryConfig == null) {
            return false;
        }
        return scoreDirectorFactoryConfig.getEasyScoreCalculatorClass() != null ||
                scoreDirectorFactoryConfig.getIncrementalScoreCalculatorClass() != null ||
                scoreDirectorFactoryConfig.getScoreDrlList() != null ||
                scoreDirectorFactoryConfig.getScoreDrlFileList() != null ||
                scoreDirectorFactoryConfig.getConstraintProviderClass() != null;
    }
}
