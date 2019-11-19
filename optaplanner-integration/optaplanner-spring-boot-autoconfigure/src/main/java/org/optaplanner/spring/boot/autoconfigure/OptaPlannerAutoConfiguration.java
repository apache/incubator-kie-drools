/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.score.stream.ConstraintProvider;
import org.optaplanner.core.api.solver.SolverManager;
import org.optaplanner.core.config.score.director.ScoreDirectorFactoryConfig;
import org.optaplanner.core.config.solver.SolverConfig;
import org.optaplanner.core.config.solver.termination.TerminationConfig;
import org.springframework.boot.autoconfigure.AutoConfigurationPackages;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.domain.EntityScanner;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.type.filter.AssignableTypeFilter;
import org.springframework.util.ClassUtils;

@Configuration
@ConditionalOnClass(SolverManager.class)
@ConditionalOnMissingBean(SolverManager.class)
@EnableConfigurationProperties({OptaPlannerProperties.class})
public class OptaPlannerAutoConfiguration {

    private final ApplicationContext context;
    private final OptaPlannerProperties optaPlannerProperties;

    protected OptaPlannerAutoConfiguration(ApplicationContext context,
            OptaPlannerProperties optaPlannerProperties) {
        this.context = context;
        this.optaPlannerProperties = optaPlannerProperties;
    }

    @Bean
    public SolverManager<?> solverManager() {
        SolverConfig solverConfig = solverConfig();
        return SolverManager.create(solverConfig);
    }

    private SolverConfig solverConfig() {
        ClassLoader classLoader = getClass().getClassLoader();
        String solverConfigXML = optaPlannerProperties.getSolverConfigXML();
        SolverConfig solverConfig;
        if (solverConfigXML != null) {
            if (classLoader.getResource(solverConfigXML) == null) {
                throw new IllegalStateException("Invalid optaplanner.solverConfigXML property (" + solverConfigXML
                        + "): that classpath resource does not exist.");
            }
            solverConfig = SolverConfig.createFromXmlResource(solverConfigXML, classLoader);
        } else if (classLoader.getResource(OptaPlannerProperties.DEFAULT_SOLVER_CONFIG_URL) != null) {
            solverConfig = SolverConfig.createFromXmlResource(
                    OptaPlannerProperties.DEFAULT_SOLVER_CONFIG_URL, classLoader);
        } else {
            solverConfig = new SolverConfig(classLoader);
        }

        applySolverProperties(solverConfig);
        return solverConfig;
    }

    private void applySolverProperties(SolverConfig solverConfig) {
        SolverProperties solverProperties = optaPlannerProperties.getSolver();
        if (solverProperties.getEnvironmentMode() != null) {
            solverConfig.setEnvironmentMode(solverProperties.getEnvironmentMode());
        }
        EntityScanner entityScanner = new EntityScanner(this.context);
        if (solverConfig.getSolutionClass() == null) {
            solverConfig.setSolutionClass(findSolutionClass(entityScanner));
        }
        if (solverConfig.getEntityClassList() == null) {
            solverConfig.setEntityClassList(findEntityClassList(entityScanner));
        }
        if (solverConfig.getScoreDirectorFactoryConfig() == null) {
            ScoreDirectorFactoryConfig scoreDirectorFactoryConfig = new ScoreDirectorFactoryConfig();
            scoreDirectorFactoryConfig.setConstraintProviderClass(findConstraintProviderClass());
            solverConfig.setScoreDirectorFactoryConfig(scoreDirectorFactoryConfig);
        }
        applyTerminationProperties(solverConfig);
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
                    + ") found with a @" + PlanningSolution.class.getSimpleName() + " annotation.");
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
                    + ") found with a @" + PlanningEntity.class.getSimpleName() + " annotation.");
        }
        return new ArrayList<>(entityClassSet);
    }

    private Class<? extends ConstraintProvider> findConstraintProviderClass() {
        // Does not use EntityScanner because ConstraintProvider shouldn't be found through @EntityScan
        if (!AutoConfigurationPackages.has(context)) {
            return null;
        }
        ClassPathScanningCandidateComponentProvider scanner
                = new ClassPathScanningCandidateComponentProvider(false);
        scanner.setEnvironment(context.getEnvironment());
        scanner.setResourceLoader(context);
        scanner.addIncludeFilter(new AssignableTypeFilter(ConstraintProvider.class));

        List<Class<? extends ConstraintProvider>> constraintProviderClassList = AutoConfigurationPackages.get(context).stream()
                .flatMap(basePackage -> scanner.findCandidateComponents(basePackage).stream())
                .map(candidate -> {
                    try {
                        return (Class<? extends ConstraintProvider>) ClassUtils.forName(candidate.getBeanClassName(), context.getClassLoader());
                    } catch (ClassNotFoundException e) {
                        throw new IllegalStateException("The " + ConstraintProvider.class.getSimpleName() + " class ("
                                + candidate.getBeanClassName() + ") cannot be found.", e);
                    }
                })
                .collect(Collectors.toList());

        if (constraintProviderClassList.size() > 1) {
            throw new IllegalStateException("Multiple classes (" + constraintProviderClassList
                    + ") found that implement " + ConstraintProvider.class.getSimpleName() + ".");
        }
        if (constraintProviderClassList.isEmpty()) {
            return null;
        }
        return constraintProviderClassList.get(0);
    }

    private void applyTerminationProperties(SolverConfig solverConfig) {
        TerminationProperties terminationProperties = optaPlannerProperties.getSolver().getTermination();
        TerminationConfig terminationConfig = solverConfig.getTerminationConfig();
        if (terminationConfig == null) {
            terminationConfig = new TerminationConfig();
            solverConfig.setTerminationConfig(terminationConfig);
        }
        if (terminationProperties.getSpentLimit() != null) {
            terminationConfig.setSpentLimit(terminationProperties.getSpentLimit());
        }
        if (terminationProperties.getUnimprovedSpentLimit() != null) {
            terminationConfig.setUnimprovedSpentLimit(terminationProperties.getUnimprovedSpentLimit());
        }
        if (terminationProperties.getBestScoreLimit() != null) {
            terminationConfig.setBestScoreLimit(terminationProperties.getBestScoreLimit());
        }
    }

}
