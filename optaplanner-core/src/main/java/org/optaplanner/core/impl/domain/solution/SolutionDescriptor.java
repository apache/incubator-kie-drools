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

package org.optaplanner.core.impl.domain.solution;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.optaplanner.core.api.domain.solution.PlanningEntityCollectionProperty;
import org.optaplanner.core.api.domain.solution.PlanningEntityProperty;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.domain.solution.cloner.PlanningCloneable;
import org.optaplanner.core.api.domain.solution.cloner.SolutionCloner;
import org.optaplanner.core.config.util.ConfigUtils;
import org.optaplanner.core.impl.domain.common.PropertyAccessor;
import org.optaplanner.core.impl.domain.common.ReflectionPropertyAccessor;
import org.optaplanner.core.impl.domain.entity.PlanningEntityDescriptor;
import org.optaplanner.core.impl.domain.solution.cloner.FieldAccessingSolutionCloner;
import org.optaplanner.core.impl.domain.solution.cloner.PlanningCloneableSolutionCloner;
import org.optaplanner.core.impl.domain.variable.PlanningVariableDescriptor;
import org.optaplanner.core.impl.solution.Solution;

public class SolutionDescriptor {

    private final Class<? extends Solution> solutionClass;
    private final BeanInfo solutionBeanInfo;
    private SolutionCloner solutionCloner;
    
    private final Map<String, PropertyAccessor> propertyAccessorMap;
    private final Map<String, PropertyAccessor> entityPropertyAccessorMap;
    private final Map<String, PropertyAccessor> entityCollectionPropertyAccessorMap;

    private final Map<Class<?>, PlanningEntityDescriptor> planningEntityDescriptorMap;

    public SolutionDescriptor(Class<? extends Solution> solutionClass) {
        this.solutionClass = solutionClass;
        try {
            solutionBeanInfo = Introspector.getBeanInfo(solutionClass);
        } catch (IntrospectionException e) {
            throw new IllegalStateException("The solutionClass (" + solutionClass + ") is not a valid java bean.", e);
        }
        int mapSize = solutionBeanInfo.getPropertyDescriptors().length;
        propertyAccessorMap = new HashMap<String, PropertyAccessor>(mapSize);
        entityPropertyAccessorMap = new HashMap<String, PropertyAccessor>(mapSize);
        entityCollectionPropertyAccessorMap = new HashMap<String, PropertyAccessor>(mapSize);
        planningEntityDescriptorMap = new HashMap<Class<?>, PlanningEntityDescriptor>(mapSize);
    }

    public void processAnnotations() {
        processSolutionAnnotations();
        processPropertyAnnotations();
    }

    private void processSolutionAnnotations() {
        PlanningSolution solutionAnnotation = solutionClass.getAnnotation(PlanningSolution.class);
        if (solutionAnnotation == null) {
            throw new IllegalStateException("The solutionClass (" + solutionClass
                    + ") has been specified as a solution in the configuration," +
                    " but does not have a " + PlanningSolution.class.getSimpleName() + " annotation.");
        }
        processSolutionCloner(solutionAnnotation);
    }

    private void processSolutionCloner(PlanningSolution solutionAnnotation) {
        Class<? extends SolutionCloner> solutionClonerClass = solutionAnnotation.solutionCloner();
        if (solutionClonerClass == PlanningSolution.NullSolutionCloner.class) {
            solutionClonerClass = null;
        }
        if (solutionClonerClass != null) {
            solutionCloner = ConfigUtils.newInstance(this, "solutionClonerClass", solutionClonerClass);
        } else {
            if (PlanningCloneable.class.isAssignableFrom(solutionClass)) {
                solutionCloner = new PlanningCloneableSolutionCloner();
            } else {
                solutionCloner = new FieldAccessingSolutionCloner(this);
            }
        }
    }

    private void processPropertyAnnotations() {
        boolean noPlanningEntityPropertyAnnotation = true;
        for (PropertyDescriptor propertyDescriptor : solutionBeanInfo.getPropertyDescriptors()) {
            PropertyAccessor propertyAccessor = new ReflectionPropertyAccessor(propertyDescriptor);
            propertyAccessorMap.put(propertyAccessor.getName(), propertyAccessor);
            Method propertyGetter = propertyAccessor.getReadMethod();
            if (propertyGetter != null) {
                if (propertyGetter.isAnnotationPresent(PlanningEntityProperty.class)) {
                    noPlanningEntityPropertyAnnotation = false;
                    entityPropertyAccessorMap.put(propertyAccessor.getName(), propertyAccessor);
                } else if (propertyGetter.isAnnotationPresent(PlanningEntityCollectionProperty.class)) {
                    noPlanningEntityPropertyAnnotation = false;
                    if (!Collection.class.isAssignableFrom(propertyAccessor.getPropertyType())) {
                        throw new IllegalStateException("The solutionClass (" + solutionClass
                                + ") has a PlanningEntityCollection annotated property ("
                                + propertyAccessor.getName() + ") that does not return a Collection.");
                    }
                    entityCollectionPropertyAccessorMap.put(propertyAccessor.getName(), propertyAccessor);
                }
            }
        }
        if (noPlanningEntityPropertyAnnotation) {
            throw new IllegalStateException("The solutionClass (" + solutionClass
                    + ") should have at least 1 getter with a PlanningEntityCollection or PlanningEntityProperty"
                    + " annotation.");
        }
    }

    public void addPlanningEntityDescriptor(PlanningEntityDescriptor planningEntityDescriptor) {
        planningEntityDescriptorMap.put(planningEntityDescriptor.getPlanningEntityClass(), planningEntityDescriptor);
    }

    public Class<? extends Solution> getSolutionClass() {
        return solutionClass;
    }

    public SolutionCloner getSolutionCloner() {
        return solutionCloner;
    }

    public Map<String, PropertyAccessor> getEntityPropertyAccessorMap() {
        return entityPropertyAccessorMap;
    }

    public Map<String, PropertyAccessor> getEntityCollectionPropertyAccessorMap() {
        return entityCollectionPropertyAccessorMap;
    }

    // ************************************************************************
    // Model methods
    // ************************************************************************

    public PropertyAccessor getPropertyAccessor(String propertyName) {
        return propertyAccessorMap.get(propertyName);
    }

    public Set<Class<?>> getPlanningEntityClassSet() {
        return planningEntityDescriptorMap.keySet();
    }

    public Collection<PlanningEntityDescriptor> getPlanningEntityDescriptors() {
        return planningEntityDescriptorMap.values();
    }

    public boolean hasPlanningEntityDescriptorStrict(Class<?> planningEntityClass) {
        return planningEntityDescriptorMap.containsKey(planningEntityClass);
    }

    public PlanningEntityDescriptor getPlanningEntityDescriptorStrict(Class<?> planningEntityClass) {
        return planningEntityDescriptorMap.get(planningEntityClass);
    }

    public boolean hasPlanningEntityDescriptor(Class<?> planningEntitySubclass) {
        PlanningEntityDescriptor entityDescriptor = null;
        Class<?> planningEntityClass = planningEntitySubclass;
        while (planningEntityClass != null) {
            entityDescriptor = planningEntityDescriptorMap.get(planningEntityClass);
            if (entityDescriptor != null) {
                return true;
            }
            planningEntityClass = planningEntityClass.getSuperclass();
        }
        return false;
    }

    public PlanningEntityDescriptor getPlanningEntityDescriptor(Class<?> planningEntitySubclass) {
        PlanningEntityDescriptor entityDescriptor = null;
        Class<?> planningEntityClass = planningEntitySubclass;
        while (planningEntityClass != null) {
            entityDescriptor = planningEntityDescriptorMap.get(planningEntityClass);
            if (entityDescriptor != null) {
                return entityDescriptor;
            }
            planningEntityClass = planningEntityClass.getSuperclass();
        }
        // TODO move this into the client methods
        throw new IllegalArgumentException("A planningEntity is an instance of a planningEntitySubclass ("
                + planningEntitySubclass + ") that is not configured as a planningEntity.\n" +
                "If that class (" + planningEntitySubclass.getSimpleName() + ") (or superclass thereof) is not a " +
                "planningEntityClass (" + getPlanningEntityClassSet()
                + "), check your Solution implementation's annotated methods.\n" +
                "If it is, check your solver configuration.");
    }
    
    public Collection<PlanningVariableDescriptor> getChainedVariableDescriptors() {
        Collection<PlanningVariableDescriptor> chainedVariableDescriptors
                = new ArrayList<PlanningVariableDescriptor>();
        for (PlanningEntityDescriptor entityDescriptor : planningEntityDescriptorMap.values()) {
            for (PlanningVariableDescriptor variableDescriptor : entityDescriptor.getPlanningVariableDescriptors()) {
                if (variableDescriptor.isChained()) {
                    chainedVariableDescriptors.add(variableDescriptor);
                }
            }
        }
        return chainedVariableDescriptors;
    }

    // ************************************************************************
    // Extraction methods
    // ************************************************************************

    public Collection<Object> getAllFacts(Solution solution) {
        Collection<Object> facts = new ArrayList<Object>();
        Collection<?> problemFacts = solution.getProblemFacts();
        if (problemFacts == null) {
            throw new IllegalStateException("The solution (" + solution
                    + ")'s method getProblemFacts() should never return null.");
        }
        facts.addAll(problemFacts);
        for (PropertyAccessor entityPropertyAccessor : entityPropertyAccessorMap.values()) {
            Object entity = extractPlanningEntity(entityPropertyAccessor, solution);
            if (entity != null) {
                facts.add(entity);
            }
        }
        for (PropertyAccessor entityCollectionPropertyAccessor : entityCollectionPropertyAccessorMap.values()) {
            Collection<?> entityCollection = extractPlanningEntityCollection(
                    entityCollectionPropertyAccessor, solution);
            for (Object entity : entityCollection) {
                facts.add(entity);
            }
        }
        return facts;
    }

    public List<Object> getPlanningEntityList(Solution solution) {
        List<Object> planningEntityList = new ArrayList<Object>();
        for (PropertyAccessor entityPropertyAccessor : entityPropertyAccessorMap.values()) {
            Object entity = extractPlanningEntity(entityPropertyAccessor, solution);
            if (entity != null) {
                planningEntityList.add(entity);
            }
        }
        for (PropertyAccessor entityCollectionPropertyAccessor : entityCollectionPropertyAccessorMap.values()) {
            Collection<?> entityCollection = extractPlanningEntityCollection(
                    entityCollectionPropertyAccessor, solution);
            planningEntityList.addAll(entityCollection);
        }
        return planningEntityList;
    }

    public List<Object> getPlanningEntityListByPlanningEntityClass(Solution solution, Class<?> planningEntityClass) {
        List<Object> planningEntityList = new ArrayList<Object>();
        for (PropertyAccessor entityPropertyAccessor : entityPropertyAccessorMap.values()) {
            if (entityPropertyAccessor.getPropertyType().isAssignableFrom(planningEntityClass)) {
                Object entity = extractPlanningEntity(entityPropertyAccessor, solution);
                if (entity != null && planningEntityClass.isInstance(entity)) {
                    planningEntityList.add(entity);
                }
            }
        }
        for (PropertyAccessor entityCollectionPropertyAccessor : entityCollectionPropertyAccessorMap.values()) {
            // TODO if (entityCollectionPropertyAccessor.getPropertyType().getElementType().isAssignableFrom(planningEntityClass)) {
            Collection<?> entityCollection = extractPlanningEntityCollection(
                    entityCollectionPropertyAccessor, solution);
            for (Object entity : entityCollection) {
                if (planningEntityClass.isInstance(entity)) {
                    planningEntityList.add(entity);
                }
            }
        }
        return planningEntityList;
    }

    /**
     * @param solution never null
     * @return >= 0
     */
    public int getPlanningEntityCount(Solution solution) {
        return getPlanningEntityList(solution).size();
    }

    /**
     * Calculates an indication on how big this problem instance is.
     * This is intentionally very loosely defined for now.
     * @param solution never null
     * @return >= 0
     */
    public long getProblemScale(Solution solution) {
        long problemScale = 0L;
        for (PropertyAccessor entityPropertyAccessor : entityPropertyAccessorMap.values()) {
            Object entity = extractPlanningEntity(entityPropertyAccessor, solution);
            if (entity != null) {
                PlanningEntityDescriptor entityDescriptor = getPlanningEntityDescriptor(entity.getClass());
                problemScale += entityDescriptor.getProblemScale(solution, entity);
            }
        }
        for (PropertyAccessor entityCollectionPropertyAccessor : entityCollectionPropertyAccessorMap.values()) {
            Collection<?> entityCollection = extractPlanningEntityCollection(
                    entityCollectionPropertyAccessor, solution);
            for (Object entity : entityCollection) {
                PlanningEntityDescriptor entityDescriptor = getPlanningEntityDescriptor(entity.getClass());
                problemScale += entityDescriptor.getProblemScale(solution, entity);
            }
        }
        return problemScale;
    }

    public int countUninitializedVariables(Solution solution) {
        int uninitializedVariableCount = 0;
        for (PropertyAccessor entityPropertyAccessor : entityPropertyAccessorMap.values()) {
            Object entity = extractPlanningEntity(entityPropertyAccessor, solution);
            if (entity != null) {
                PlanningEntityDescriptor entityDescriptor = getPlanningEntityDescriptor(entity.getClass());
                uninitializedVariableCount += entityDescriptor.countUninitializedVariables(entity);
            }
        }
        for (PropertyAccessor entityCollectionPropertyAccessor : entityCollectionPropertyAccessorMap.values()) {
            Collection<?> entityCollection = extractPlanningEntityCollection(
                    entityCollectionPropertyAccessor, solution);
            for (Object entity : entityCollection) {
                PlanningEntityDescriptor entityDescriptor = getPlanningEntityDescriptor(entity.getClass());
                uninitializedVariableCount += entityDescriptor.countUninitializedVariables(entity);
            }
        }
        return uninitializedVariableCount;
    }

    /**
     * @param solution never null
     * @return true if all the planning entities are initialized
     */
    public boolean isInitialized(Solution solution) {
        for (PropertyAccessor entityPropertyAccessor : entityPropertyAccessorMap.values()) {
            Object entity = extractPlanningEntity(entityPropertyAccessor, solution);
            if (entity != null) {
                PlanningEntityDescriptor entityDescriptor = getPlanningEntityDescriptor(entity.getClass());
                if (!entityDescriptor.isInitialized(entity)) {
                    return false;
                }
            }
        }
        for (PropertyAccessor entityCollectionPropertyAccessor : entityCollectionPropertyAccessorMap.values()) {
            Collection<?> entityCollection = extractPlanningEntityCollection(
                    entityCollectionPropertyAccessor, solution);
            for (Object entity : entityCollection) {
                PlanningEntityDescriptor entityDescriptor = getPlanningEntityDescriptor(entity.getClass());
                if (!entityDescriptor.isInitialized(entity)) {
                    return false;
                }
            }
        }
        return true;
    }

    private Object extractPlanningEntity(PropertyAccessor entityPropertyAccessor, Solution solution) {
        return entityPropertyAccessor.executeGetter(solution);
    }

    private Collection<?> extractPlanningEntityCollection(
            PropertyAccessor entityCollectionPropertyAccessor, Solution solution) {
        Collection<?> entityCollection = (Collection<?>) entityCollectionPropertyAccessor.executeGetter(solution);
        if (entityCollection == null) {
            throw new IllegalArgumentException("The solutionClass (" + solutionClass
                    + ")'s entityCollectionProperty ("
                    + entityCollectionPropertyAccessor.getName() + ") should never return null.");
        }
        return entityCollection;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + solutionClass.getName() + ")";
    }

}
