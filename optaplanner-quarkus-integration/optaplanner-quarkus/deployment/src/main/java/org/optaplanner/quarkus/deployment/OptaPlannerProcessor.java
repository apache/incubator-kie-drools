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

package org.optaplanner.quarkus.deployment;

import static io.quarkus.deployment.annotations.ExecutionTime.STATIC_INIT;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Singleton;

import org.drools.core.base.ClassFieldAccessorFactory;
import org.jboss.jandex.AnnotationInstance;
import org.jboss.jandex.AnnotationTarget;
import org.jboss.jandex.ClassInfo;
import org.jboss.jandex.DotName;
import org.jboss.jandex.IndexView;
import org.jboss.jandex.Type;
import org.jboss.logging.Logger;
import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.score.stream.ConstraintProvider;
import org.optaplanner.core.config.score.director.ScoreDirectorFactoryConfig;
import org.optaplanner.core.config.solver.SolverConfig;
import org.optaplanner.core.config.solver.SolverManagerConfig;
import org.optaplanner.core.config.solver.termination.TerminationConfig;
import org.optaplanner.core.impl.score.director.easy.EasyScoreCalculator;
import org.optaplanner.core.impl.score.director.incremental.IncrementalScoreCalculator;
import org.optaplanner.quarkus.OptaPlannerBeanProvider;
import org.optaplanner.quarkus.OptaPlannerRecorder;

import io.quarkus.arc.deployment.AdditionalBeanBuildItem;
import io.quarkus.arc.deployment.SyntheticBeanBuildItem;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.annotations.Record;
import io.quarkus.deployment.builditem.CombinedIndexBuildItem;
import io.quarkus.deployment.builditem.FeatureBuildItem;
import io.quarkus.deployment.builditem.HotDeploymentWatchedFileBuildItem;
import io.quarkus.deployment.builditem.nativeimage.ReflectiveClassBuildItem;
import io.quarkus.deployment.builditem.nativeimage.ReflectiveHierarchyBuildItem;
import io.quarkus.deployment.builditem.nativeimage.RuntimeInitializedClassBuildItem;
import io.quarkus.deployment.recording.RecorderContext;
import io.quarkus.runtime.configuration.ConfigurationException;

class OptaPlannerProcessor {

    private static final Logger log = Logger.getLogger(OptaPlannerProcessor.class.getName());

    OptaPlannerBuildTimeConfig optaPlannerBuildTimeConfig;

    @BuildStep
    FeatureBuildItem feature() {
        return new FeatureBuildItem("optaplanner");
    }

    @BuildStep
    HotDeploymentWatchedFileBuildItem watchSolverConfigXml() {
        String solverConfigXML = optaPlannerBuildTimeConfig.solverConfigXml
                .orElse(OptaPlannerBuildTimeConfig.DEFAULT_SOLVER_CONFIG_URL);
        return new HotDeploymentWatchedFileBuildItem(solverConfigXML);
    }

    @BuildStep
    HotDeploymentWatchedFileBuildItem watchScoreDrl() {
        return new HotDeploymentWatchedFileBuildItem(SolverBuildTimeConfig.DEFAULT_SCORE_DRL_URL);
    }

    @BuildStep
    @Record(STATIC_INIT)
    void recordAndRegisterBeans(OptaPlannerRecorder recorder, RecorderContext recorderContext,
            CombinedIndexBuildItem combinedIndex,
            BuildProducer<ReflectiveHierarchyBuildItem> reflectiveHierarchyClass,
            BuildProducer<ReflectiveClassBuildItem> reflectiveClass,
            BuildProducer<SyntheticBeanBuildItem> syntheticBeanBuildItemBuildProducer,
            BuildProducer<AdditionalBeanBuildItem> additionalBeans) {
        IndexView indexView = combinedIndex.getIndex();

        // Only skip this extension if everything is missing. Otherwise, if some parts are missing, fail fast later.
        if (indexView.getAnnotations(DotNames.PLANNING_SOLUTION).isEmpty()
                && indexView.getAnnotations(DotNames.PLANNING_ENTITY).isEmpty()) {
            log.warn("Skipping OptaPlanner extension because there are no " + PlanningSolution.class.getSimpleName()
                    + " or " + PlanningEntity.class.getSimpleName() + " annotated classes.");
            return;
        }

        // Quarkus extensions must always use getContextClassLoader()
        // Internally, OptaPlanner defaults the ClassLoader to getContextClassLoader() too
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        SolverConfig solverConfig;
        if (optaPlannerBuildTimeConfig.solverConfigXml.isPresent()) {
            String solverConfigXML = optaPlannerBuildTimeConfig.solverConfigXml.get();
            if (classLoader.getResource(solverConfigXML) == null) {
                throw new ConfigurationException("Invalid quarkus.optaplanner.solverConfigXML property ("
                        + solverConfigXML + "): that classpath resource does not exist.");
            }
            solverConfig = SolverConfig.createFromXmlResource(solverConfigXML);
        } else if (classLoader.getResource(OptaPlannerBuildTimeConfig.DEFAULT_SOLVER_CONFIG_URL) != null) {
            solverConfig = SolverConfig.createFromXmlResource(
                    OptaPlannerBuildTimeConfig.DEFAULT_SOLVER_CONFIG_URL);
        } else {
            solverConfig = new SolverConfig();
        }
        // The deployment classLoader must not escape to runtime
        solverConfig.setClassLoader(null);

        applySolverProperties(recorderContext, indexView, solverConfig);

        if (solverConfig.getSolutionClass() != null) {
            Type jandexType = Type.create(DotName.createSimple(solverConfig.getSolutionClass().getName()), Type.Kind.CLASS);
            reflectiveHierarchyClass.produce(new ReflectiveHierarchyBuildItem(jandexType));
        }
        List<Class<?>> reflectiveClassList = new ArrayList<>(5);
        ScoreDirectorFactoryConfig scoreDirectorFactoryConfig = solverConfig.getScoreDirectorFactoryConfig();
        if (scoreDirectorFactoryConfig != null) {
            if (scoreDirectorFactoryConfig.getEasyScoreCalculatorClass() != null) {
                reflectiveClassList.add(scoreDirectorFactoryConfig.getEasyScoreCalculatorClass());
            }
            if (scoreDirectorFactoryConfig.getConstraintProviderClass() != null) {
                reflectiveClassList.add(scoreDirectorFactoryConfig.getConstraintProviderClass());
            }
            if (scoreDirectorFactoryConfig.getIncrementalScoreCalculatorClass() != null) {
                reflectiveClassList.add(scoreDirectorFactoryConfig.getIncrementalScoreCalculatorClass());
            }
        }
        reflectiveClass.produce(
                new ReflectiveClassBuildItem(true, false, false,
                        reflectiveClassList.stream().map(Class::getName).toArray(String[]::new)));

        SolverManagerConfig solverManagerConfig = new SolverManagerConfig();
        optaPlannerBuildTimeConfig.solverManager.parallelSolverCount.ifPresent(solverManagerConfig::setParallelSolverCount);

        syntheticBeanBuildItemBuildProducer.produce(SyntheticBeanBuildItem.configure(SolverConfig.class)
                .scope(Singleton.class)
                .defaultBean()
                .supplier(recorder.solverConfigSupplier(solverConfig)).done());

        syntheticBeanBuildItemBuildProducer.produce(SyntheticBeanBuildItem.configure(SolverManagerConfig.class)
                .scope(Singleton.class)
                .defaultBean()
                .supplier(recorder.solverManagerConfig(solverManagerConfig)).done());
        additionalBeans.produce(new AdditionalBeanBuildItem(OptaPlannerBeanProvider.class));
    }

    private void applySolverProperties(RecorderContext recorderContext,
            IndexView indexView, SolverConfig solverConfig) {
        if (solverConfig.getSolutionClass() == null) {
            solverConfig.setSolutionClass(findSolutionClass(recorderContext, indexView));
        }
        if (solverConfig.getEntityClassList() == null) {
            solverConfig.setEntityClassList(findEntityClassList(recorderContext, indexView));
        }
        applyScoreDirectorFactoryProperties(indexView, solverConfig);
        optaPlannerBuildTimeConfig.solver.environmentMode.ifPresent(solverConfig::setEnvironmentMode);
        optaPlannerBuildTimeConfig.solver.moveThreadCount.ifPresent(solverConfig::setMoveThreadCount);
        applyTerminationProperties(solverConfig);
    }

    private Class<?> findSolutionClass(RecorderContext recorderContext, IndexView indexView) {
        Collection<AnnotationInstance> annotationInstances = indexView.getAnnotations(DotNames.PLANNING_SOLUTION);
        if (annotationInstances.size() > 1) {
            throw new IllegalStateException("Multiple classes (" + convertAnnotationInstancesToString(annotationInstances)
                    + ") found with a @" + PlanningSolution.class.getSimpleName() + " annotation.");
        }
        if (annotationInstances.isEmpty()) {
            throw new IllegalStateException("No classes (" + convertAnnotationInstancesToString(annotationInstances)
                    + ") found with a @" + PlanningSolution.class.getSimpleName() + " annotation.");
        }
        AnnotationTarget solutionTarget = annotationInstances.iterator().next().target();
        if (solutionTarget.kind() != AnnotationTarget.Kind.CLASS) {
            throw new IllegalStateException("A target (" + solutionTarget
                    + ") with a @" + PlanningSolution.class.getSimpleName() + " must be a class.");
        }
        return convertClassInfoToClass(solutionTarget.asClass());
    }

    private List<Class<?>> findEntityClassList(RecorderContext recorderContext, IndexView indexView) {
        Collection<AnnotationInstance> annotationInstances = indexView.getAnnotations(DotNames.PLANNING_ENTITY);
        if (annotationInstances.isEmpty()) {
            throw new IllegalStateException("No classes (" + convertAnnotationInstancesToString(annotationInstances)
                    + ") found with a @" + PlanningEntity.class.getSimpleName() + " annotation.");
        }
        List<AnnotationTarget> targetList = annotationInstances.stream()
                .map(AnnotationInstance::target)
                .collect(Collectors.toList());
        if (targetList.stream().anyMatch(target -> target.kind() != AnnotationTarget.Kind.CLASS)) {
            throw new IllegalStateException("All targets (" + targetList
                    + ") with a @" + PlanningEntity.class.getSimpleName() + " must be a class.");
        }
        return targetList.stream()
                .map(target -> (Class<?>) convertClassInfoToClass(target.asClass()))
                .collect(Collectors.toList());
    }

    private void applyScoreDirectorFactoryProperties(IndexView indexView, SolverConfig solverConfig) {
        if (solverConfig.getScoreDirectorFactoryConfig() == null) {
            ScoreDirectorFactoryConfig scoreDirectorFactoryConfig = new ScoreDirectorFactoryConfig();
            scoreDirectorFactoryConfig.setEasyScoreCalculatorClass(
                    findImplementingClass(DotNames.EASY_SCORE_CALCULATOR, indexView));
            scoreDirectorFactoryConfig.setConstraintProviderClass(
                    findImplementingClass(DotNames.CONSTRAINT_PROVIDER, indexView));
            scoreDirectorFactoryConfig.setIncrementalScoreCalculatorClass(
                    findImplementingClass(DotNames.INCREMENTAL_SCORE_CALCULATOR, indexView));
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            if (classLoader.getResource(SolverBuildTimeConfig.DEFAULT_SCORE_DRL_URL) != null) {
                scoreDirectorFactoryConfig.setScoreDrlList(Collections.singletonList(
                        SolverBuildTimeConfig.DEFAULT_SCORE_DRL_URL));
            }
            if (scoreDirectorFactoryConfig.getEasyScoreCalculatorClass() == null
                    && scoreDirectorFactoryConfig.getConstraintProviderClass() == null
                    && scoreDirectorFactoryConfig.getIncrementalScoreCalculatorClass() == null
                    && scoreDirectorFactoryConfig.getScoreDrlList() == null) {
                throw new IllegalStateException("No classes found that implement "
                        + EasyScoreCalculator.class.getSimpleName() + ", "
                        + ConstraintProvider.class.getSimpleName() + " or "
                        + IncrementalScoreCalculator.class.getSimpleName() + ", nor a "
                        + SolverBuildTimeConfig.DEFAULT_SCORE_DRL_URL + " resource.");
            }
            solverConfig.setScoreDirectorFactoryConfig(scoreDirectorFactoryConfig);
        }
    }

    private <T> Class<? extends T> findImplementingClass(DotName targetDotName, IndexView indexView) {
        Collection<ClassInfo> classInfos = indexView.getAllKnownImplementors(targetDotName);
        if (classInfos.size() > 1) {
            throw new IllegalStateException("Multiple classes (" + convertClassInfosToString(classInfos)
                    + ") found that implement the interface " + targetDotName + ".");
        }
        if (classInfos.isEmpty()) {
            return null;
        }
        ClassInfo classInfo = classInfos.iterator().next();
        return convertClassInfoToClass(classInfo);
    }

    private void applyTerminationProperties(SolverConfig solverConfig) {
        TerminationConfig terminationConfig = solverConfig.getTerminationConfig();
        if (terminationConfig == null) {
            terminationConfig = new TerminationConfig();
            solverConfig.setTerminationConfig(terminationConfig);
        }
        optaPlannerBuildTimeConfig.solver.termination.spentLimit.ifPresent(terminationConfig::setSpentLimit);
        optaPlannerBuildTimeConfig.solver.termination.unimprovedSpentLimit
                .ifPresent(terminationConfig::setUnimprovedSpentLimit);
        optaPlannerBuildTimeConfig.solver.termination.bestScoreLimit.ifPresent(terminationConfig::setBestScoreLimit);
    }

    private String convertAnnotationInstancesToString(Collection<AnnotationInstance> annotationInstances) {
        return "[" + annotationInstances.stream().map(instance -> instance.target().toString())
                .collect(Collectors.joining(", ")) + "]";
    }

    private String convertClassInfosToString(Collection<ClassInfo> classInfos) {
        return "[" + classInfos.stream().map(instance -> instance.name().toString())
                .collect(Collectors.joining(", ")) + "]";
    }

    private <T> Class<? extends T> convertClassInfoToClass(ClassInfo classInfo) {
        String className = classInfo.name().toString();
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        try {
            return (Class<? extends T>) classLoader.loadClass(className);
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("The class (" + className
                    + ") cannot be created during deployment.", e);
        }
    }

    @BuildStep
    public RuntimeInitializedClassBuildItem nativeImageDroolsTricks() {
        return new RuntimeInitializedClassBuildItem(ClassFieldAccessorFactory.class.getName());
    }

}
