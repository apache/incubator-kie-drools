/*
 * Copyright 2011 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.planner.core.domain.entity;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.drools.planner.api.domain.entity.PlanningEntity;
import org.drools.planner.api.domain.entity.PlanningEntityDifficultyWeightFactory;
import org.drools.planner.api.domain.variable.DependentPlanningVariable;
import org.drools.planner.api.domain.variable.PlanningVariable;
import org.drools.planner.core.domain.solution.SolutionDescriptor;
import org.drools.planner.core.domain.variable.DependentPlanningVariableDescriptor;
import org.drools.planner.core.domain.variable.PlanningVariableDescriptor;
import org.drools.planner.core.solution.Solution;

public class PlanningEntityDescriptor {

    private final SolutionDescriptor solutionDescriptor;

    private final Class<?> planningEntityClass;
    private final BeanInfo planningEntityBeanInfo;
    private PlanningEntitySorter planningEntitySorter;

    private Map<String, PlanningVariableDescriptor> planningVariableDescriptorMap;
    private Map<String, DependentPlanningVariableDescriptor> dependentPlanningVariableDescriptorMap;

    public PlanningEntityDescriptor(SolutionDescriptor solutionDescriptor, Class<?> planningEntityClass) {
        this.solutionDescriptor = solutionDescriptor;
        this.planningEntityClass = planningEntityClass;
        try {
            planningEntityBeanInfo = Introspector.getBeanInfo(planningEntityClass);
        } catch (IntrospectionException e) {
            throw new IllegalStateException("The planningEntityClass (" + planningEntityClass
                    + ") is not a valid java bean.", e);
        }
    }

    public void processAnnotations() {
        processEntityAnnotations();
        processPropertyAnnotations();
    }

    private void processEntityAnnotations() {
        PlanningEntity planningEntityAnnotation = planningEntityClass.getAnnotation(PlanningEntity.class);
        if (planningEntityAnnotation == null) {
            throw new IllegalStateException("The planningEntityClass (" + planningEntityClass
                    + ") has been specified as a planning entity in the configuration," +
                    " but does not have a PlanningEntity annotation.");
        }
        planningEntitySorter = new PlanningEntitySorter();
        processDifficulty(planningEntityAnnotation);
    }

    private void processDifficulty(PlanningEntity planningEntityAnnotation) {
        Class<? extends Comparator> difficultyComparatorClass = planningEntityAnnotation.difficultyComparatorClass();
        if (difficultyComparatorClass == PlanningEntity.NullDifficultyComparator.class) {
            difficultyComparatorClass = null;
        }
        Class<? extends PlanningEntityDifficultyWeightFactory> difficultyWeightFactoryClass
                = planningEntityAnnotation.difficultyWeightFactoryClass();
        if (difficultyWeightFactoryClass == PlanningEntity.NullDifficultyWeightFactory.class) {
            difficultyWeightFactoryClass = null;
        }
        if (difficultyComparatorClass != null && difficultyWeightFactoryClass != null) {
            throw new IllegalStateException("The planningEntityClass (" + planningEntityClass
                    + ") cannot have a difficultyComparatorClass (" + difficultyComparatorClass.getName()
                    + ") and a difficultyWeightFactoryClass (" + difficultyWeightFactoryClass.getName()
                    + ") at the same time.");
        }
        if (difficultyComparatorClass != null) {
            Comparator<Object> difficultyComparator;
            try {
                difficultyComparator = difficultyComparatorClass.newInstance();
            } catch (InstantiationException e) {
                throw new IllegalArgumentException("The difficultyComparatorClass ("
                        + difficultyComparatorClass.getName()
                        + ") does not have a public no-arg constructor", e);
            } catch (IllegalAccessException e) {
                throw new IllegalArgumentException("The difficultyComparatorClass ("
                        + difficultyComparatorClass.getName()
                        + ") does not have a public no-arg constructor", e);
            }
            planningEntitySorter.setDifficultyComparator(difficultyComparator);
        }
        if (difficultyWeightFactoryClass != null) {
            PlanningEntityDifficultyWeightFactory difficultyWeightFactory;
            try {
                difficultyWeightFactory = difficultyWeightFactoryClass.newInstance();
            } catch (InstantiationException e) {
                throw new IllegalArgumentException("The difficultyWeightFactoryClass ("
                        + difficultyWeightFactoryClass.getName()
                        + ") does not have a public no-arg constructor", e);
            } catch (IllegalAccessException e) {
                throw new IllegalArgumentException("The difficultyWeightFactoryClass ("
                        + difficultyWeightFactoryClass.getName()
                        + ") does not have a public no-arg constructor", e);
            }
            planningEntitySorter.setDifficultyWeightFactory(difficultyWeightFactory);
        }
    }

    private void processPropertyAnnotations() {
        PropertyDescriptor[] propertyDescriptors = planningEntityBeanInfo.getPropertyDescriptors();
        int mapSize = propertyDescriptors.length;
        planningVariableDescriptorMap = new LinkedHashMap<String, PlanningVariableDescriptor>(mapSize);
        boolean noPlanningVariableAnnotation = true;
        for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
            Method propertyGetter = propertyDescriptor.getReadMethod();
            if (propertyGetter != null && propertyGetter.isAnnotationPresent(PlanningVariable.class)) {
                if (propertyGetter.isAnnotationPresent(DependentPlanningVariable.class)) {
                    throw new IllegalStateException("The planningEntityClass (" + planningEntityClass
                            + ") has a PlanningVariable annotated property (" + propertyDescriptor.getName()
                            + ") that is also annotated with DependentPlanningVariable.");
                }
                noPlanningVariableAnnotation = false;
                if (propertyDescriptor.getWriteMethod() == null) {
                    throw new IllegalStateException("The planningEntityClass (" + planningEntityClass
                            + ") has a PlanningVariable annotated property (" + propertyDescriptor.getName()
                            + ") that should have a setter.");
                }
                PlanningVariableDescriptor planningVariableDescriptor = new PlanningVariableDescriptor(
                        this, propertyDescriptor);
                planningVariableDescriptorMap.put(propertyDescriptor.getName(), planningVariableDescriptor);
                planningVariableDescriptor.processAnnotations();
            }
        }
        dependentPlanningVariableDescriptorMap = new LinkedHashMap<String, DependentPlanningVariableDescriptor>(
                mapSize);
        for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
            Method propertyGetter = propertyDescriptor.getReadMethod();
            if (propertyGetter != null && propertyGetter.isAnnotationPresent(DependentPlanningVariable.class)) {
                if (propertyDescriptor.getWriteMethod() == null) {
                    throw new IllegalStateException("The planningEntityClass (" + planningEntityClass
                            + ") has a DependentPlanningVariable annotated property (" + propertyDescriptor.getName()
                            + ") that should have a setter.");
                }
                DependentPlanningVariableDescriptor dependentPlanningVariableDescriptor
                        = new DependentPlanningVariableDescriptor(this, propertyDescriptor);
                dependentPlanningVariableDescriptorMap.put(propertyDescriptor.getName(),
                        dependentPlanningVariableDescriptor);
                dependentPlanningVariableDescriptor.processAnnotations();
            }
        }
        if (noPlanningVariableAnnotation) {
            throw new IllegalStateException("The planningEntityClass (" + planningEntityClass
                    + ") should have at least 1 getter with a PlanningVariable annotation.");
        }
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    public SolutionDescriptor getSolutionDescriptor() {
        return solutionDescriptor;
    }

    public Class<?> getPlanningEntityClass() {
        return planningEntityClass;
    }
    
    public boolean appliesToPlanningEntity(Object entity) {
        return entity.getClass().isAssignableFrom(planningEntityClass);
    }

    public PlanningEntitySorter getPlanningEntitySorter() {
        return planningEntitySorter;
    }

    public PropertyDescriptor getPropertyDescriptor(String propertyName) {
        for (PropertyDescriptor propertyDescriptor : planningEntityBeanInfo.getPropertyDescriptors()) {
            if (propertyDescriptor.getName().equals(propertyName)) {
                return propertyDescriptor;
            }
        }
        return null;
    }

    public Collection<PlanningVariableDescriptor> getPlanningVariableDescriptors() {
        return planningVariableDescriptorMap.values();
    }
    
    public PlanningVariableDescriptor getPlanningVariableDescriptor(String propertyName) {
        return planningVariableDescriptorMap.get(propertyName);
    }

    public List<Object> extractEntities(Solution solution) {
        return solutionDescriptor.getPlanningEntityListByPlanningEntityClass(solution, planningEntityClass);
    }

    public long getProblemScale(Solution solution, Object planningEntity) {
        long problemScale = 1L;
        for (PlanningVariableDescriptor planningVariableDescriptor : planningVariableDescriptorMap.values()) {
            problemScale *= planningVariableDescriptor.getProblemScale(solution, planningEntity);
        }
        return problemScale;
    }

    public boolean isInitialized(Object planningEntity) {
        for (PlanningVariableDescriptor planningVariableDescriptor : planningVariableDescriptorMap.values()) {
            if (!planningVariableDescriptor.isInitialized(planningEntity)) {
                return false;
            }
        }
        return true;
    }

    public void uninitialize(Object planningEntity) {
        for (PlanningVariableDescriptor planningVariableDescriptor : planningVariableDescriptorMap.values()) {
            planningVariableDescriptor.uninitialize(planningEntity);
        }
    }

}
