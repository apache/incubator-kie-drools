package org.optaplanner.quarkus;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import org.optaplanner.core.api.domain.solution.cloner.SolutionCloner;
import org.optaplanner.core.config.solver.SolverConfig;
import org.optaplanner.core.config.solver.SolverManagerConfig;
import org.optaplanner.core.config.solver.termination.TerminationConfig;
import org.optaplanner.core.impl.domain.common.accessor.MemberAccessor;
import org.optaplanner.quarkus.config.OptaPlannerRuntimeConfig;

import io.quarkus.arc.Arc;
import io.quarkus.runtime.RuntimeValue;
import io.quarkus.runtime.annotations.Recorder;

@Recorder
public class OptaPlannerRecorder {

    public Supplier<SolverConfig> solverConfigSupplier(final SolverConfig solverConfig,
            Map<String, RuntimeValue<MemberAccessor>> generatedGizmoMemberAccessorMap,
            Map<String, RuntimeValue<SolutionCloner>> generatedGizmoSolutionClonerMap) {
        return () -> {
            OptaPlannerRuntimeConfig optaPlannerRuntimeConfig =
                    Arc.container().instance(OptaPlannerRuntimeConfig.class).get();
            updateSolverConfigWithRuntimeProperties(solverConfig, optaPlannerRuntimeConfig);
            Map<String, MemberAccessor> memberAccessorMap = new HashMap<>();
            Map<String, SolutionCloner> solutionClonerMap = new HashMap<>();
            generatedGizmoMemberAccessorMap
                    .forEach((className, runtimeValue) -> memberAccessorMap.put(className, runtimeValue.getValue()));
            generatedGizmoSolutionClonerMap
                    .forEach((className, runtimeValue) -> solutionClonerMap.put(className, runtimeValue.getValue()));

            solverConfig.setGizmoMemberAccessorMap(memberAccessorMap);
            solverConfig.setGizmoSolutionClonerMap(solutionClonerMap);
            return solverConfig;
        };
    }

    public Supplier<SolverManagerConfig> solverManagerConfig(final SolverManagerConfig solverManagerConfig) {
        return () -> {
            OptaPlannerRuntimeConfig optaPlannerRuntimeConfig =
                    Arc.container().instance(OptaPlannerRuntimeConfig.class).get();
            updateSolverManagerConfigWithRuntimeProperties(solverManagerConfig, optaPlannerRuntimeConfig);
            return solverManagerConfig;
        };
    }

    private void updateSolverConfigWithRuntimeProperties(SolverConfig solverConfig,
            OptaPlannerRuntimeConfig optaPlannerRunTimeConfig) {
        TerminationConfig terminationConfig = solverConfig.getTerminationConfig();
        if (terminationConfig == null) {
            terminationConfig = new TerminationConfig();
            solverConfig.setTerminationConfig(terminationConfig);
        }
        optaPlannerRunTimeConfig.solver.termination.spentLimit.ifPresent(terminationConfig::setSpentLimit);
        optaPlannerRunTimeConfig.solver.termination.unimprovedSpentLimit
                .ifPresent(terminationConfig::setUnimprovedSpentLimit);
        optaPlannerRunTimeConfig.solver.termination.bestScoreLimit.ifPresent(terminationConfig::setBestScoreLimit);
        optaPlannerRunTimeConfig.solver.moveThreadCount.ifPresent(solverConfig::setMoveThreadCount);
    }

    private void updateSolverManagerConfigWithRuntimeProperties(SolverManagerConfig solverManagerConfig,
            OptaPlannerRuntimeConfig optaPlannerRunTimeConfig) {
        optaPlannerRunTimeConfig.solverManager.parallelSolverCount.ifPresent(solverManagerConfig::setParallelSolverCount);
    }

}
