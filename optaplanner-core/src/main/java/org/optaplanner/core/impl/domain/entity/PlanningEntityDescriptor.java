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

package org.optaplanner.core.impl.domain.entity;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.PlanningVariable;
import org.optaplanner.core.config.util.ConfigUtils;
import org.optaplanner.core.impl.domain.solution.SolutionDescriptor;
import org.optaplanner.core.impl.domain.variable.PlanningVariableDescriptor;
import org.optaplanner.core.impl.heuristic.selector.common.decorator.SelectionFilter;
import org.optaplanner.core.impl.heuristic.selector.common.decorator.SelectionSorterWeightFactory;
import org.optaplanner.core.impl.solution.Solution;

public class PlanningEntityDescriptor {

    private final SolutionDescriptor solutionDescriptor;

    private final Class<?> planningEntityClass;
    private final BeanInfo planningEntityBeanInfo;
    private SelectionFilter movableEntitySelectionFilter;
    private PlanningEntitySorter planningEntitySorter;

    private Map<String, PlanningVariableDescriptor> planningVariableDescriptorMap;

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
        PlanningEntity entityAnnotation = planningEntityClass.getAnnotation(PlanningEntity.class);
        if (entityAnnotation == null) {
            throw new IllegalStateException("The planningEntityClass (" + planningEntityClass
                    + ") has been specified as a planning entity in the configuration," +
                    " but does not have a " + PlanningEntity.class.getSimpleName() + " annotation.");
        }
        processMovable(entityAnnotation);
        planningEntitySorter = new PlanningEntitySorter();
        processDifficulty(entityAnnotation);
    }

    private void processMovable(PlanningEntity entityAnnotation) {
        Class<? extends SelectionFilter> movableEntitySelectionFilterClass = entityAnnotation.movableEntitySelectionFilter();
        if (movableEntitySelectionFilterClass == PlanningEntity.NullMovableEntitySelectionFilter.class) {
            movableEntitySelectionFilterClass = null;
        }
        if (movableEntitySelectionFilterClass != null) {
            movableEntitySelectionFilter = ConfigUtils.newInstance(this,
                    "movableEntitySelectionFilterClass", movableEntitySelectionFilterClass);
        }
    }

    private void processDifficulty(PlanningEntity entityAnnotation) {
        Class<? extends Comparator> difficultyComparatorClass = entityAnnotation.difficultyComparatorClass();
        if (difficultyComparatorClass == PlanningEntity.NullDifficultyComparator.class) {
            difficultyComparatorClass = null;
        }
        Class<? extends SelectionSorterWeightFactory> difficultyWeightFactoryClass
                = entityAnnotation.difficultyWeightFactoryClass();
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
            Comparator<Object> difficultyComparator = ConfigUtils.newInstance(this,
                    "difficultyComparatorClass", difficultyComparatorClass);
            planningEntitySorter.setDifficultyComparator(difficultyComparator);
        }
        if (difficultyWeightFactoryClass != null) {
            SelectionSorterWeightFactory difficultyWeightFactory = ConfigUtils.newInstance(this,
                    "difficultyWeightFactoryClass", difficultyWeightFactoryClass);
            planningEntitySorter.setDifficultyWeightFactory(difficultyWeightFactory);
        }
    }

    private void processPropertyAnnotations() {
        PropertyDescriptor[] propertyDescriptors = planningEntityBeanInfo.getPropertyDescriptors();
        planningVariableDescriptorMap = new LinkedHashMap<String, PlanningVariableDescriptor>(propertyDescriptors.length);
        boolean noPlanningVariableAnnotation = true;
        for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
            Method propertyGetter = propertyDescriptor.getReadMethod();
            if (propertyGetter != null && propertyGetter.isAnnotationPresent(PlanningVariable.class)) {
                noPlanningVariableAnnotation = false;
                if (propertyDescriptor.getWriteMethod() == null) {
                    throw new IllegalStateException("The planningEntityClass (" + planningEntityClass
                            + ") has a PlanningVariable annotated property (" + propertyDescriptor.getName()
                            + ") that should have a setter.");
                }
                PlanningVariableDescriptor variableDescriptor = new PlanningVariableDescriptor(
                        this, propertyDescriptor);
                planningVariableDescriptorMap.put(propertyDescriptor.getName(), variableDescriptor);
                variableDescriptor.processAnnotations();
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

    public boolean hasMovableEntitySelectionFilter() {
        return movableEntitySelectionFilter != null;
    }

    public SelectionFilter getMovableEntitySelectionFilter() {
        return movableEntitySelectionFilter;
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

    public Collection<String> getPlanningVariableNameSet() {
        return planningVariableDescriptorMap.keySet();
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
        for (PlanningVariableDescriptor variableDescriptor : planningVariableDescriptorMap.values()) {
            problemScale *= variableDescriptor.getProblemScale(solution, planningEntity);
        }
        return problemScale;
    }

    public int countUninitializedVariables(Object planningEntity) {
        int uninitializedVariableCount = 0;
        for (PlanningVariableDescriptor variableDescriptor : planningVariableDescriptorMap.values()) {
            if (!variableDescriptor.isInitialized(planningEntity)) {
                uninitializedVariableCount++;
            }
        }
        return uninitializedVariableCount;
    }

    public boolean isInitialized(Object planningEntity) {
        for (PlanningVariableDescriptor variableDescriptor : planningVariableDescriptorMap.values()) {
            if (!variableDescriptor.isInitialized(planningEntity)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + planningEntityClass.getName() + ")";
    }

}
