package org.optaplanner.benchmark.quarkus.deployment;

import org.jboss.logging.Logger;
import org.optaplanner.benchmark.config.PlannerBenchmarkConfig;
import org.optaplanner.benchmark.quarkus.OptaPlannerBenchmarkBeanProvider;
import org.optaplanner.benchmark.quarkus.OptaPlannerBenchmarkRecorder;
import org.optaplanner.benchmark.quarkus.UnavailableOptaPlannerBenchmarkBeanProvider;
import org.optaplanner.benchmark.quarkus.config.OptaPlannerBenchmarkRuntimeConfig;
import org.optaplanner.core.config.solver.SolverConfig;
import org.optaplanner.quarkus.deployment.SolverConfigBuildItem;

import io.quarkus.arc.deployment.AdditionalBeanBuildItem;
import io.quarkus.arc.deployment.SyntheticBeanBuildItem;
import io.quarkus.arc.deployment.UnremovableBeanBuildItem;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.annotations.ExecutionTime;
import io.quarkus.deployment.annotations.Record;
import io.quarkus.deployment.builditem.FeatureBuildItem;
import io.quarkus.deployment.builditem.HotDeploymentWatchedFileBuildItem;
import io.quarkus.runtime.configuration.ConfigurationException;

class OptaPlannerBenchmarkProcessor {

    private static final Logger log = Logger.getLogger(OptaPlannerBenchmarkProcessor.class.getName());

    OptaPlannerBenchmarkBuildTimeConfig optaPlannerBenchmarkBuildTimeConfig;

    @BuildStep
    FeatureBuildItem feature() {
        return new FeatureBuildItem("optaplanner-benchmark");
    }

    @BuildStep
    HotDeploymentWatchedFileBuildItem watchSolverBenchmarkConfigXml() {
        String solverBenchmarkConfigXML = optaPlannerBenchmarkBuildTimeConfig.solverBenchmarkConfigXml
                .orElse(OptaPlannerBenchmarkBuildTimeConfig.DEFAULT_SOLVER_BENCHMARK_CONFIG_URL);
        return new HotDeploymentWatchedFileBuildItem(solverBenchmarkConfigXML);
    }

    @BuildStep
    @Record(ExecutionTime.STATIC_INIT)
    void registerAdditionalBeans(BuildProducer<AdditionalBeanBuildItem> additionalBeans,
            BuildProducer<SyntheticBeanBuildItem> syntheticBeans,
            BuildProducer<UnremovableBeanBuildItem> unremovableBeans,
            SolverConfigBuildItem solverConfigBuildItem,
            OptaPlannerBenchmarkRecorder recorder) {
        if (solverConfigBuildItem.getSolverConfig() == null) {
            log.warn("Skipping OptaPlanner Benchmark extension because the OptaPlanner extension was skipped.");
            additionalBeans.produce(new AdditionalBeanBuildItem(UnavailableOptaPlannerBenchmarkBeanProvider.class));
            return;
        }
        PlannerBenchmarkConfig benchmarkConfig;
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        if (optaPlannerBenchmarkBuildTimeConfig.solverBenchmarkConfigXml.isPresent()) {
            String solverBenchmarkConfigXML = optaPlannerBenchmarkBuildTimeConfig.solverBenchmarkConfigXml.get();
            if (classLoader.getResource(solverBenchmarkConfigXML) == null) {
                throw new ConfigurationException("Invalid quarkus.optaplanner.benchmark.solver-benchmark-config-xml property ("
                        + solverBenchmarkConfigXML + "): that classpath resource does not exist.");
            }
            benchmarkConfig = PlannerBenchmarkConfig.createFromXmlResource(solverBenchmarkConfigXML);
        } else if (classLoader.getResource(OptaPlannerBenchmarkBuildTimeConfig.DEFAULT_SOLVER_BENCHMARK_CONFIG_URL) != null) {
            benchmarkConfig = PlannerBenchmarkConfig.createFromXmlResource(
                    OptaPlannerBenchmarkBuildTimeConfig.DEFAULT_SOLVER_BENCHMARK_CONFIG_URL);
        } else {
            benchmarkConfig = null;
        }
        syntheticBeans.produce(SyntheticBeanBuildItem.configure(PlannerBenchmarkConfig.class)
                .supplier(recorder.benchmarkConfigSupplier(benchmarkConfig))
                .done());
        additionalBeans.produce(new AdditionalBeanBuildItem(OptaPlannerBenchmarkBeanProvider.class));
        unremovableBeans.produce(UnremovableBeanBuildItem.beanTypes(OptaPlannerBenchmarkRuntimeConfig.class));
        unremovableBeans.produce(UnremovableBeanBuildItem.beanTypes(SolverConfig.class));
    }
}
