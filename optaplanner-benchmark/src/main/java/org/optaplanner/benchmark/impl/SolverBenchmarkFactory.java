package org.optaplanner.benchmark.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.optaplanner.benchmark.config.ProblemBenchmarksConfig;
import org.optaplanner.benchmark.config.SolverBenchmarkConfig;
import org.optaplanner.benchmark.config.statistic.ProblemStatisticType;
import org.optaplanner.benchmark.config.statistic.SingleStatisticType;
import org.optaplanner.benchmark.impl.result.PlannerBenchmarkResult;
import org.optaplanner.benchmark.impl.result.SolverBenchmarkResult;
import org.optaplanner.core.config.solver.SolverConfig;
import org.optaplanner.core.config.solver.monitoring.MonitoringConfig;
import org.optaplanner.core.config.solver.monitoring.SolverMetric;
import org.optaplanner.core.config.util.ConfigUtils;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.core.impl.solver.DefaultSolverFactory;

public class SolverBenchmarkFactory {
    private final SolverBenchmarkConfig config;

    public SolverBenchmarkFactory(SolverBenchmarkConfig config) {
        this.config = config;
    }

    public <Solution_> void buildSolverBenchmark(ClassLoader classLoader, PlannerBenchmarkResult plannerBenchmark,
            Solution_[] extraProblems) {
        validate();
        SolverBenchmarkResult solverBenchmarkResult = new SolverBenchmarkResult(plannerBenchmark);
        solverBenchmarkResult.setName(config.getName());
        solverBenchmarkResult.setSubSingleCount(ConfigUtils.inheritOverwritableProperty(config.getSubSingleCount(), 1));
        if (config.getSolverConfig().getClassLoader() == null) {
            config.getSolverConfig().setClassLoader(classLoader);
        }
        if (config.getSolverConfig().getMonitoringConfig() != null &&
                config.getSolverConfig().getMonitoringConfig().getSolverMetricList() != null &&
                !config.getSolverConfig().getMonitoringConfig().getSolverMetricList().isEmpty()) {
            throw new IllegalArgumentException(
                    "The solverBenchmarkConfig (" + config + ") has a " + SolverConfig.class.getSimpleName() +
                            " (" + config.getSolverConfig() + " ) with a non-empty " + MonitoringConfig.class.getSimpleName() +
                            " (" + config.getSolverConfig().getMonitoringConfig() + ").");
        }
        List<SolverMetric> solverMetricList = getSolverMetrics(config.getProblemBenchmarksConfig());
        solverBenchmarkResult.setSolverConfig(config.getSolverConfig()
                .copyConfig().withMonitoringConfig(
                        new MonitoringConfig()
                                .withSolverMetricList(solverMetricList)));
        DefaultSolverFactory<Solution_> defaultSolverFactory = new DefaultSolverFactory<>(config.getSolverConfig());
        SolutionDescriptor<Solution_> solutionDescriptor = defaultSolverFactory.getSolutionDescriptor();
        for (Solution_ extraProblem : extraProblems) {
            if (!solutionDescriptor.getSolutionClass().isInstance(extraProblem)) {
                throw new IllegalArgumentException("The solverBenchmark name (" + config.getName()
                        + ") for solution class (" + solutionDescriptor.getSolutionClass()
                        + ") cannot solve a problem (" + extraProblem
                        + ") of class (" + (extraProblem == null ? null : extraProblem.getClass()) + ").");
            }
        }
        solverBenchmarkResult.setScoreDefinition(solutionDescriptor.getScoreDefinition());
        solverBenchmarkResult.setSingleBenchmarkResultList(new ArrayList<>());
        ProblemBenchmarksConfig problemBenchmarksConfig_ =
                config.getProblemBenchmarksConfig() == null ? new ProblemBenchmarksConfig()
                        : config.getProblemBenchmarksConfig();
        plannerBenchmark.getSolverBenchmarkResultList().add(solverBenchmarkResult);
        ProblemBenchmarksFactory problemBenchmarksFactory = new ProblemBenchmarksFactory(problemBenchmarksConfig_);
        problemBenchmarksFactory.buildProblemBenchmarkList(solverBenchmarkResult, extraProblems);
    }

    protected void validate() {
        if (!DefaultPlannerBenchmarkFactory.VALID_NAME_PATTERN.matcher(config.getName()).matches()) {
            throw new IllegalStateException("The solverBenchmark name (" + config.getName()
                    + ") is invalid because it does not follow the nameRegex ("
                    + DefaultPlannerBenchmarkFactory.VALID_NAME_PATTERN.pattern() + ")" +
                    " which might cause an illegal filename.");
        }
        if (!config.getName().trim().equals(config.getName())) {
            throw new IllegalStateException("The solverBenchmark name (" + config.getName()
                    + ") is invalid because it starts or ends with whitespace.");
        }
        if (config.getSubSingleCount() != null && config.getSubSingleCount() < 1) {
            throw new IllegalStateException("The solverBenchmark name (" + config.getName()
                    + ") is invalid because the subSingleCount (" + config.getSubSingleCount() + ") must be greater than 1.");
        }
    }

    protected List<SolverMetric> getSolverMetrics(ProblemBenchmarksConfig config) {
        List<SolverMetric> out = new ArrayList<>();
        for (ProblemStatisticType problemStatisticType : Optional.ofNullable(config)
                .map(ProblemBenchmarksConfig::determineProblemStatisticTypeList)
                .orElseGet(ProblemStatisticType::defaultList)) {
            if (problemStatisticType == ProblemStatisticType.SCORE_CALCULATION_SPEED) {
                out.add(SolverMetric.SCORE_CALCULATION_COUNT);
            } else {
                out.add(SolverMetric.valueOf(problemStatisticType.name()));
            }
        }
        for (SingleStatisticType singleStatisticType : Optional.ofNullable(config)
                .map(ProblemBenchmarksConfig::determineSingleStatisticTypeList)
                .orElseGet(Collections::emptyList)) {
            out.add(SolverMetric.valueOf(singleStatisticType.name()));
        }
        return out;
    }
}
