/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.ScoreManager;
import org.optaplanner.core.api.score.stream.ConstraintProvider;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.api.solver.SolverManager;
import org.optaplanner.core.config.score.director.ScoreDirectorFactoryConfig;
import org.optaplanner.core.config.solver.SolverConfig;
import org.optaplanner.core.config.solver.SolverManagerConfig;
import org.optaplanner.core.config.solver.termination.TerminationConfig;
import org.optaplanner.core.impl.score.director.easy.EasyScoreCalculator;
import org.optaplanner.core.impl.score.director.incremental.IncrementalScoreCalculator;
import org.optaplanner.persistence.jackson.api.OptaPlannerJacksonModule;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.boot.autoconfigure.AutoConfigurationPackages;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.domain.EntityScanner;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.type.filter.AssignableTypeFilter;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.util.ClassUtils;

import com.fasterxml.jackson.databind.Module;

@Configuration
@ConditionalOnClass({ SolverConfig.class, SolverFactory.class, ScoreManager.class, SolverManager.class })
@ConditionalOnMissingBean({ SolverConfig.class, SolverFactory.class, ScoreManager.class, SolverManager.class })
@EnableConfigurationProperties({ OptaPlannerProperties.class })
public class OptaPlannerAutoConfiguration implements BeanClassLoaderAware {

    private final ApplicationContext context;
    private final OptaPlannerProperties optaPlannerProperties;
    private ClassLoader beanClassLoader;

    protected OptaPlannerAutoConfiguration(ApplicationContext context,
            OptaPlannerProperties optaPlannerProperties) {
        this.context = context;
        this.optaPlannerProperties = optaPlannerProperties;
    }

    @Override
    public void setBeanClassLoader(ClassLoader beanClassLoader) {
        this.beanClassLoader = beanClassLoader;
    }

    @Bean
    @ConditionalOnMissingBean
    public <Solution_, ProblemId_> SolverManager<Solution_, ProblemId_> solverManager(SolverFactory solverFactory) {
        // TODO supply ThreadFactory
        SolverManagerConfig solverManagerConfig = new SolverManagerConfig();
        SolverManagerProperties solverManagerProperties = optaPlannerProperties.getSolverManager();
        if (solverManagerProperties != null) {
            if (solverManagerProperties.getParallelSolverCount() != null) {
                solverManagerConfig.setParallelSolverCount(solverManagerProperties.getParallelSolverCount());
            }
        }
        return SolverManager.create(solverFactory, solverManagerConfig);
    }

    @Bean
    @ConditionalOnMissingBean
    public <Solution_> ScoreManager<Solution_> scoreManager(SolverFactory solverFactory) {
        return ScoreManager.create(solverFactory);
    }

    @Bean
    @ConditionalOnMissingBean
    public <Solution_> SolverFactory<Solution_> solverFactory(SolverConfig solverConfig) {
        return SolverFactory.create(solverConfig);
    }

    @Bean
    @ConditionalOnMissingBean
    public SolverConfig solverConfig() {
        String solverConfigXml = optaPlannerProperties.getSolverConfigXml();
        SolverConfig solverConfig;
        if (solverConfigXml != null) {
            if (beanClassLoader.getResource(solverConfigXml) == null) {
                throw new IllegalStateException("Invalid optaplanner.solverConfigXml property (" + solverConfigXml
                        + "): that classpath resource does not exist.");
            }
            solverConfig = SolverConfig.createFromXmlResource(solverConfigXml, beanClassLoader);
        } else if (beanClassLoader.getResource(OptaPlannerProperties.DEFAULT_SOLVER_CONFIG_URL) != null) {
            solverConfig = SolverConfig.createFromXmlResource(
                    OptaPlannerProperties.DEFAULT_SOLVER_CONFIG_URL, beanClassLoader);
        } else {
            solverConfig = new SolverConfig(beanClassLoader);
        }

        applySolverProperties(solverConfig);
        return solverConfig;
    }

    private void applySolverProperties(SolverConfig solverConfig) {
        EntityScanner entityScanner = new EntityScanner(this.context);
        if (solverConfig.getSolutionClass() == null) {
            solverConfig.setSolutionClass(findSolutionClass(entityScanner));
        }
        if (solverConfig.getEntityClassList() == null) {
            solverConfig.setEntityClassList(findEntityClassList(entityScanner));
        }
        applyScoreDirectorFactoryProperties(solverConfig);
        SolverProperties solverProperties = optaPlannerProperties.getSolver();
        if (solverProperties != null) {
            if (solverProperties.getEnvironmentMode() != null) {
                solverConfig.setEnvironmentMode(solverProperties.getEnvironmentMode());
            }
            if (solverProperties.getDaemon() != null) {
                solverConfig.setDaemon(solverProperties.getDaemon());
            }
            if (solverProperties.getMoveThreadCount() != null) {
                solverConfig.setMoveThreadCount(solverProperties.getMoveThreadCount());
            }
            applyTerminationProperties(solverConfig, solverProperties);
        }
    }

    private Class<?> findSolutionClass(EntityScanner entityScanner) {
        Set<Class<?>> solutionClassSet;
        try {
            solutionClassSet = entityScanner.scan(PlanningSolution.class);
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("Scanning for @" + PlanningSolution.class.getSimpleName()
                    + " annotations failed.", e);
        }
        if (solutionClassSet.size() > 1) {
            throw new IllegalStateException("Multiple classes (" + solutionClassSet
                    + ") found with a @" + PlanningSolution.class.getSimpleName() + " annotation.");
        }
        if (solutionClassSet.isEmpty()) {
            throw new IllegalStateException("No classes (" + solutionClassSet
                    + ") found with a @" + PlanningSolution.class.getSimpleName() + " annotation.\n"
                    + "Maybe your @" + PlanningSolution.class.getSimpleName() + " annotated class "
                    + " is not in a subpackage of your @" + SpringBootApplication.class.getSimpleName()
                    + " annotated class's package.\n"
                    + "Maybe move your planning solution class to your application class's (sub)package"
                    + " (or use @" + EntityScan.class.getSimpleName() + ").");
        }
        return solutionClassSet.iterator().next();
    }

    private List<Class<?>> findEntityClassList(EntityScanner entityScanner) {
        Set<Class<?>> entityClassSet;
        try {
            entityClassSet = entityScanner.scan(PlanningEntity.class);
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("Scanning for @" + PlanningEntity.class.getSimpleName() + " failed.", e);
        }
        if (entityClassSet.isEmpty()) {
            throw new IllegalStateException("No classes (" + entityClassSet
                    + ") found with a @" + PlanningEntity.class.getSimpleName() + " annotation.\n"
                    + "Maybe your @" + PlanningEntity.class.getSimpleName() + " annotated class(es) "
                    + " are not in a subpackage of your @" + SpringBootApplication.class.getSimpleName()
                    + " annotated class's package.\n"
                    + "Maybe move your planning entity classes to your application class's (sub)package"
                    + " (or use @" + EntityScan.class.getSimpleName() + ").");
        }
        return new ArrayList<>(entityClassSet);
    }

    private void applyScoreDirectorFactoryProperties(SolverConfig solverConfig) {
        if (solverConfig.getScoreDirectorFactoryConfig() == null) {
            ScoreDirectorFactoryConfig scoreDirectorFactoryConfig = new ScoreDirectorFactoryConfig();
            scoreDirectorFactoryConfig.setEasyScoreCalculatorClass(findImplementingClass(EasyScoreCalculator.class));
            scoreDirectorFactoryConfig.setConstraintProviderClass(findImplementingClass(ConstraintProvider.class));
            scoreDirectorFactoryConfig
                    .setIncrementalScoreCalculatorClass(findImplementingClass(IncrementalScoreCalculator.class));
            if (beanClassLoader.getResource(SolverProperties.DEFAULT_SCORE_DRL_URL) != null) {
                scoreDirectorFactoryConfig.setScoreDrlList(Collections.singletonList(
                        SolverProperties.DEFAULT_SCORE_DRL_URL));
            }
            if (scoreDirectorFactoryConfig.getEasyScoreCalculatorClass() == null
                    && scoreDirectorFactoryConfig.getConstraintProviderClass() == null
                    && scoreDirectorFactoryConfig.getIncrementalScoreCalculatorClass() == null
                    && scoreDirectorFactoryConfig.getScoreDrlList() == null) {
                throw new IllegalStateException("No classes found that implement "
                        + EasyScoreCalculator.class.getSimpleName() + ", "
                        + ConstraintProvider.class.getSimpleName() + " or "
                        + IncrementalScoreCalculator.class.getSimpleName() + ", nor a "
                        + SolverProperties.DEFAULT_SCORE_DRL_URL + " resource.\n"
                        + "Maybe your @" + ConstraintProvider.class.getSimpleName() + " annotated class "
                        + " is not in a subpackage of your @" + SpringBootApplication.class.getSimpleName()
                        + " annotated class's package.\n"
                        + "Maybe move your constraint provider class to your application class's (sub)package.");
            }
            solverConfig.setScoreDirectorFactoryConfig(scoreDirectorFactoryConfig);
        }
    }

    private <T> Class<? extends T> findImplementingClass(Class<T> targetClass) {
        // Does not use EntityScanner because these classes shouldn't be found through @EntityScan
        if (!AutoConfigurationPackages.has(context)) {
            return null;
        }
        ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);
        scanner.setEnvironment(context.getEnvironment());
        scanner.setResourceLoader(context);
        scanner.addIncludeFilter(new AssignableTypeFilter(targetClass));

        List<String> packages = AutoConfigurationPackages.get(context);
        List<Class<? extends T>> classList = packages.stream()
                .flatMap(basePackage -> scanner.findCandidateComponents(basePackage).stream())
                .map(candidate -> {
                    try {
                        Class<? extends T> clazz = ClassUtils.forName(candidate.getBeanClassName(), context.getClassLoader())
                                .asSubclass(targetClass);
                        return clazz;
                    } catch (ClassNotFoundException e) {
                        throw new IllegalStateException("The " + targetClass.getSimpleName() + " class ("
                                + candidate.getBeanClassName() + ") cannot be found.", e);
                    }
                })
                .collect(Collectors.toList());
        if (classList.size() > 1) {
            throw new IllegalStateException("Multiple classes (" + classList
                    + ") found that implement the interface " + targetClass.getSimpleName() + ".");
        }
        if (classList.isEmpty()) {
            return null;
        }
        return classList.get(0);
    }

    private void applyTerminationProperties(SolverConfig solverConfig, SolverProperties solverProperties) {
        TerminationConfig terminationConfig = solverConfig.getTerminationConfig();
        if (terminationConfig == null) {
            terminationConfig = new TerminationConfig();
            solverConfig.setTerminationConfig(terminationConfig);
        }
        TerminationProperties terminationProperties = solverProperties.getTermination();
        if (terminationProperties != null) {
            if (terminationProperties.getSpentLimit() != null) {
                terminationConfig.overwriteSpentLimit(terminationProperties.getSpentLimit());
            }
            if (terminationProperties.getUnimprovedSpentLimit() != null) {
                terminationConfig.overwriteUnimprovedSpentLimit(terminationProperties.getUnimprovedSpentLimit());
            }
            if (terminationProperties.getBestScoreLimit() != null) {
                terminationConfig.setBestScoreLimit(terminationProperties.getBestScoreLimit());
            }
        }
    }

    // @Bean wrapped by static class to avoid classloading issues if dependencies are absent
    @Configuration(proxyBeanMethods = false)
    @ConditionalOnClass({ Jackson2ObjectMapperBuilder.class, Score.class })
    static class OptaPlannerJacksonConfiguration {

        @Bean
        Module jacksonModule() {
            return OptaPlannerJacksonModule.createModule();
        }

    }

}
