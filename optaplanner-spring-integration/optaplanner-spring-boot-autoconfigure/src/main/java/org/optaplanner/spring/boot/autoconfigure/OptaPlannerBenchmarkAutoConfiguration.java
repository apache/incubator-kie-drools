/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.optaplanner.spring.boot.autoconfigure;

import java.io.File;

import org.optaplanner.benchmark.api.PlannerBenchmarkFactory;
import org.optaplanner.benchmark.config.PlannerBenchmarkConfig;
import org.optaplanner.core.config.solver.SolverConfig;
import org.optaplanner.core.config.solver.termination.TerminationConfig;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@AutoConfigureAfter(OptaPlannerAutoConfiguration.class)
@ConditionalOnClass({ PlannerBenchmarkFactory.class })
@ConditionalOnMissingBean({ PlannerBenchmarkFactory.class })
@EnableConfigurationProperties({ OptaPlannerProperties.class })
public class OptaPlannerBenchmarkAutoConfiguration
        implements BeanClassLoaderAware {

    private final ApplicationContext context;
    private final OptaPlannerProperties optaPlannerProperties;
    private ClassLoader beanClassLoader;

    protected OptaPlannerBenchmarkAutoConfiguration(ApplicationContext context,
            OptaPlannerProperties optaPlannerProperties) {
        this.context = context;
        this.optaPlannerProperties = optaPlannerProperties;
    }

    @Bean
    public PlannerBenchmarkConfig plannerBenchmarkConfig(SolverConfig solverConfig) {
        PlannerBenchmarkConfig benchmarkConfig;
        if (optaPlannerProperties.getBenchmark() != null
                && optaPlannerProperties.getBenchmark().getSolverBenchmarkConfigXml() != null) {
            if (beanClassLoader.getResource(optaPlannerProperties.getBenchmark().getSolverBenchmarkConfigXml()) == null) {
                throw new IllegalStateException(
                        "Invalid optaplanner.benchmark.solverBenchmarkConfigXml property ("
                                + optaPlannerProperties.getBenchmark().getSolverBenchmarkConfigXml()
                                + "): that classpath resource does not exist.");
            }
            benchmarkConfig = PlannerBenchmarkConfig
                    .createFromXmlResource(optaPlannerProperties.getBenchmark().getSolverBenchmarkConfigXml(), beanClassLoader);
        } else if (beanClassLoader.getResource(OptaPlannerProperties.DEFAULT_SOLVER_BENCHMARK_CONFIG_URL) != null) {
            benchmarkConfig = PlannerBenchmarkConfig.createFromXmlResource(
                    OptaPlannerProperties.DEFAULT_SOLVER_CONFIG_URL, beanClassLoader);
        } else {
            benchmarkConfig = PlannerBenchmarkConfig.createFromSolverConfig(solverConfig);
            benchmarkConfig.setBenchmarkDirectory(new File(OptaPlannerProperties.DEFAULT_BENCHMARK_RESULT_DIRECTORY));
        }

        if (optaPlannerProperties.getBenchmark() != null && optaPlannerProperties.getBenchmark().getResultDirectory() != null) {
            benchmarkConfig.setBenchmarkDirectory(new File(optaPlannerProperties.getBenchmark().getResultDirectory()));
        }

        if (benchmarkConfig.getBenchmarkDirectory() == null) {
            benchmarkConfig.setBenchmarkDirectory(new File(OptaPlannerProperties.DEFAULT_BENCHMARK_RESULT_DIRECTORY));
        }

        if (optaPlannerProperties.getBenchmark() != null && optaPlannerProperties.getBenchmark().getSolver() != null) {
            OptaPlannerAutoConfiguration
                    .applyTerminationProperties(benchmarkConfig.getInheritedSolverBenchmarkConfig().getSolverConfig(),
                            optaPlannerProperties.getBenchmark().getSolver().getTermination());
        }

        if (!isTerminationConfigured(
                benchmarkConfig.getInheritedSolverBenchmarkConfig().getSolverConfig().getTerminationConfig())) {
            throw new IllegalStateException(
                    "Property optaplanner.benchmark.solver.termination.spentLimit is required if termination is not configured.");
        }
        return benchmarkConfig;
    }

    @Bean
    public PlannerBenchmarkFactory plannerBenchmarkFactory(PlannerBenchmarkConfig benchmarkConfig) {
        return PlannerBenchmarkFactory.create(benchmarkConfig);
    }

    @Override
    public void setBeanClassLoader(ClassLoader classLoader) {
        this.beanClassLoader = classLoader;
    }

    private boolean isTerminationConfigured(TerminationConfig terminationConfig) {
        return terminationConfig.getTerminationClass() != null ||
                terminationConfig.getSpentLimit() != null ||
                terminationConfig.getBestScoreLimit() != null ||
                terminationConfig.getUnimprovedSpentLimit() != null ||
                terminationConfig.getStepCountLimit() != null ||
                terminationConfig.getTerminationConfigList() != null;
    }
}
