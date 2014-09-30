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

package org.optaplanner.core.impl.domain.entity.descriptor;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.solution.Solution;
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;
import org.optaplanner.core.api.domain.variable.CustomShadowVariable;
import org.optaplanner.core.api.domain.variable.InverseRelationShadowVariable;
import org.optaplanner.core.api.domain.variable.PlanningVariable;
import org.optaplanner.core.config.heuristic.selector.common.decorator.SelectionSorterOrder;
import org.optaplanner.core.config.util.ConfigUtils;
import org.optaplanner.core.impl.domain.policy.DescriptorPolicy;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.core.impl.domain.variable.descriptor.CustomShadowVariableDescriptor;
import org.optaplanner.core.impl.domain.variable.descriptor.GenuineVariableDescriptor;
import org.optaplanner.core.impl.domain.variable.descriptor.InverseRelationShadowVariableDescriptor;
import org.optaplanner.core.impl.domain.variable.descriptor.ShadowVariableDescriptor;
import org.optaplanner.core.impl.domain.variable.descriptor.VariableDescriptor;
import org.optaplanner.core.impl.domain.variable.listener.VariableListener;
import org.optaplanner.core.impl.heuristic.selector.common.decorator.ComparatorSelectionSorter;
import org.optaplanner.core.impl.heuristic.selector.common.decorator.SelectionFilter;
import org.optaplanner.core.impl.heuristic.selector.common.decorator.SelectionSorter;
import org.optaplanner.core.impl.heuristic.selector.common.decorator.SelectionSorterWeightFactory;
import org.optaplanner.core.impl.heuristic.selector.common.decorator.WeightFactorySelectionSorter;
import org.optaplanner.core.impl.score.director.ScoreDirector;

public class EntityDescriptor {

    private final SolutionDescriptor solutionDescriptor;

    private final Class<?> entityClass;
    private final BeanInfo entityBeanInfo;
    private SelectionFilter movableEntitySelectionFilter;
    private SelectionSorter decreasingDifficultySorter;

    private List<EntityDescriptor> inheritedEntityDescriptorList;

    // Only declared variable descriptors, excludes inherited variable descriptors
    private Map<String, GenuineVariableDescriptor> declaredGenuineVariableDescriptorMap;
    private Map<String, ShadowVariableDescriptor> declaredShadowVariableDescriptorMap;

    // Caches the inherited and declared variable descriptors
    private Map<String, GenuineVariableDescriptor> effectiveGenuineVariableDescriptorMap;
    private Map<String, ShadowVariableDescriptor> effectiveShadowVariableDescriptorMap;

    public EntityDescriptor(SolutionDescriptor solutionDescriptor, Class<?> entityClass) {
        this.solutionDescriptor = solutionDescriptor;
        this.entityClass = entityClass;
        try {
            entityBeanInfo = Introspector.getBeanInfo(entityClass);
        } catch (IntrospectionException e) {
            throw new IllegalStateException("The entityClass (" + entityClass
                    + ") is not a valid java bean.", e);
        }
    }

    public void processAnnotations(DescriptorPolicy descriptorPolicy) {
        processEntityAnnotations(descriptorPolicy);
        processMethodAnnotations(descriptorPolicy);
        processPropertyAnnotations(descriptorPolicy);
    }

    private void processEntityAnnotations(DescriptorPolicy descriptorPolicy) {
        PlanningEntity entityAnnotation = entityClass.getAnnotation(PlanningEntity.class);
        if (entityAnnotation == null) {
            throw new IllegalStateException("The entityClass (" + entityClass
                    + ") has been specified as a planning entity in the configuration," +
                    " but does not have a " + PlanningEntity.class.getSimpleName() + " annotation.");
        }
        processMovable(descriptorPolicy, entityAnnotation);
        processDifficulty(descriptorPolicy, entityAnnotation);
    }

    private void processMovable(DescriptorPolicy descriptorPolicy, PlanningEntity entityAnnotation) {
        Class<? extends SelectionFilter> movableEntitySelectionFilterClass = entityAnnotation.movableEntitySelectionFilter();
        if (movableEntitySelectionFilterClass == PlanningEntity.NullMovableEntitySelectionFilter.class) {
            movableEntitySelectionFilterClass = null;
        }
        if (movableEntitySelectionFilterClass != null) {
            movableEntitySelectionFilter = ConfigUtils.newInstance(this,
                    "movableEntitySelectionFilterClass", movableEntitySelectionFilterClass);
        }
    }

    private void processDifficulty(DescriptorPolicy descriptorPolicy, PlanningEntity entityAnnotation) {
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
            throw new IllegalStateException("The entityClass (" + entityClass
                    + ") cannot have a difficultyComparatorClass (" + difficultyComparatorClass.getName()
                    + ") and a difficultyWeightFactoryClass (" + difficultyWeightFactoryClass.getName()
                    + ") at the same time.");
        }
        if (difficultyComparatorClass != null) {
            Comparator<Object> difficultyComparator = ConfigUtils.newInstance(this,
                    "difficultyComparatorClass", difficultyComparatorClass);
            decreasingDifficultySorter = new ComparatorSelectionSorter(
                    difficultyComparator, SelectionSorterOrder.DESCENDING);
        }
        if (difficultyWeightFactoryClass != null) {
            SelectionSorterWeightFactory difficultyWeightFactory = ConfigUtils.newInstance(this,
                    "difficultyWeightFactoryClass", difficultyWeightFactoryClass);
            decreasingDifficultySorter = new WeightFactorySelectionSorter(
                    difficultyWeightFactory, SelectionSorterOrder.DESCENDING);
        }
    }

    private void processMethodAnnotations(DescriptorPolicy descriptorPolicy) {
        // Only iterate declared methods, not inherited methods, to avoid registering the same ValueRangeProvide twice
        for (Method method : entityClass.getDeclaredMethods()) {
            if (method.isAnnotationPresent(ValueRangeProvider.class)) {
                descriptorPolicy.addFromEntityValueRangeProvider(method);
            }
        }
    }

    private void processPropertyAnnotations(DescriptorPolicy descriptorPolicy) {
        PropertyDescriptor[] propertyDescriptors = entityBeanInfo.getPropertyDescriptors();
        declaredGenuineVariableDescriptorMap = new LinkedHashMap<String, GenuineVariableDescriptor>(propertyDescriptors.length);
        declaredShadowVariableDescriptorMap = new LinkedHashMap<String, ShadowVariableDescriptor>(propertyDescriptors.length);
        boolean noPlanningVariableAnnotation = true;
        for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
            Method propertyGetter = propertyDescriptor.getReadMethod();
            // Only process declared methods, not inherited methods, to avoid registering the same variable twice
            if (propertyGetter != null && propertyGetter.getDeclaringClass() == entityClass) {
                Class<? extends Annotation> variableAnnotationClass = null;
                for (Class<? extends Annotation> detectedAnnotationClass : Arrays.asList(
                        PlanningVariable.class, InverseRelationShadowVariable.class, CustomShadowVariable.class)) {
                    if (propertyGetter.isAnnotationPresent(detectedAnnotationClass)) {
                        if (variableAnnotationClass != null) {
                            throw new IllegalStateException("The entityClass (" + entityClass
                                    + ") has a property (" + propertyDescriptor.getName() + ") that has both a "
                                    + variableAnnotationClass.getSimpleName() + " annotation and a "
                                    + detectedAnnotationClass.getSimpleName() + " annotation.");
                        }
                        variableAnnotationClass = detectedAnnotationClass;
                    }
                }
                if (variableAnnotationClass != null) {
                    noPlanningVariableAnnotation = false;
                    if (propertyDescriptor.getWriteMethod() == null) {
                        throw new IllegalStateException("The entityClass (" + entityClass
                                + ") has a " + variableAnnotationClass.getSimpleName()
                                + " annotated property (" + propertyDescriptor.getName()
                                + ") that should have a setter.");
                    }
                    if (variableAnnotationClass.equals(PlanningVariable.class)) {
                        GenuineVariableDescriptor variableDescriptor = new GenuineVariableDescriptor(
                                this, propertyDescriptor);
                        declaredGenuineVariableDescriptorMap.put(propertyDescriptor.getName(), variableDescriptor);
                        variableDescriptor.processAnnotations(descriptorPolicy);
                    } else if (variableAnnotationClass.equals(InverseRelationShadowVariable.class)) {
                        ShadowVariableDescriptor variableDescriptor = new InverseRelationShadowVariableDescriptor(
                                this, propertyDescriptor);
                        declaredShadowVariableDescriptorMap.put(propertyDescriptor.getName(), variableDescriptor);
                        variableDescriptor.processAnnotations(descriptorPolicy);
                    } else if (variableAnnotationClass.equals(CustomShadowVariable.class)) {
                        ShadowVariableDescriptor variableDescriptor = new CustomShadowVariableDescriptor(
                                this, propertyDescriptor);
                        declaredShadowVariableDescriptorMap.put(propertyDescriptor.getName(), variableDescriptor);
                        variableDescriptor.processAnnotations(descriptorPolicy);
                    } else {
                        throw new IllegalStateException("The variableAnnotationClass ("
                                + variableAnnotationClass + ") is not implemented.");
                    }
                }
            }
        }
        if (noPlanningVariableAnnotation) {
            throw new IllegalStateException("The entityClass (" + entityClass
                    + ") should have at least 1 getter with a " + PlanningVariable.class.getSimpleName()
                    + " annotation or a shadow variable annotation.");
        }
    }

    public void linkInheritedEntityDescriptors(DescriptorPolicy descriptorPolicy) {
        inheritedEntityDescriptorList = new ArrayList<EntityDescriptor>(4);
        investigateParentsToLinkInherited(entityClass);
        createEffectiveVariableDescriptorMaps();
    }

    private void investigateParentsToLinkInherited(Class<?> investigateClass) {
        if (investigateClass == null || investigateClass.isArray()) {
            return;
        }
        linkInherited(investigateClass.getSuperclass());
        for (Class<?> superInterface : investigateClass.getInterfaces()) {
            linkInherited(superInterface);
        }
    }

    private void linkInherited(Class<?> investigateClass) {
        EntityDescriptor superEntityDescriptor = solutionDescriptor.getEntityDescriptorStrict(investigateClass);
        if (superEntityDescriptor != null) {
            inheritedEntityDescriptorList.add(superEntityDescriptor);
        } else {
            investigateParentsToLinkInherited(investigateClass);
        }
    }

    public void linkShadowSources(DescriptorPolicy descriptorPolicy) {
        for (ShadowVariableDescriptor shadowVariableDescriptor : declaredShadowVariableDescriptorMap.values()) {
            shadowVariableDescriptor.linkShadowSources(descriptorPolicy);
        }
    }

    private void createEffectiveVariableDescriptorMaps() {
        effectiveGenuineVariableDescriptorMap = new LinkedHashMap<String, GenuineVariableDescriptor>(
                declaredGenuineVariableDescriptorMap.size());
        effectiveShadowVariableDescriptorMap = new LinkedHashMap<String, ShadowVariableDescriptor>(
                declaredShadowVariableDescriptorMap.size());
        for (EntityDescriptor inheritedEntityDescriptor : inheritedEntityDescriptorList) {
            effectiveGenuineVariableDescriptorMap.putAll(inheritedEntityDescriptor.getGenuineVariableDescriptorMap());
            effectiveShadowVariableDescriptorMap.putAll(inheritedEntityDescriptor.getShadowVariableDescriptorMap());
        }
        effectiveGenuineVariableDescriptorMap.putAll(declaredGenuineVariableDescriptorMap);
        effectiveShadowVariableDescriptorMap.putAll(declaredShadowVariableDescriptorMap);
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    public SolutionDescriptor getSolutionDescriptor() {
        return solutionDescriptor;
    }

    public Class<?> getEntityClass() {
        return entityClass;
    }
    
    public boolean matchesEntity(Object entity) {
        return entityClass.isAssignableFrom(entity.getClass());
    }

    public boolean hasMovableEntitySelectionFilter() {
        return movableEntitySelectionFilter != null;
    }

    public SelectionFilter getMovableEntitySelectionFilter() {
        return movableEntitySelectionFilter;
    }

    public SelectionSorter getDecreasingDifficultySorter() {
        return decreasingDifficultySorter;
    }

    public boolean hasProperty(String propertyName) {
        for (PropertyDescriptor propertyDescriptor : entityBeanInfo.getPropertyDescriptors()) {
            if (propertyDescriptor.getName().equals(propertyName)) {
                return true;
            }
        }
        return false;
    }

    public boolean hasAnyDeclaredGenuineVariableDescriptor() {
        return !declaredGenuineVariableDescriptorMap.isEmpty();
    }

    public Collection<String> getGenuineVariableNameSet() {
        return effectiveGenuineVariableDescriptorMap.keySet();
    }

    public Map<String, GenuineVariableDescriptor> getGenuineVariableDescriptorMap() {
        return effectiveGenuineVariableDescriptorMap;
    }

    public Map<String, ShadowVariableDescriptor> getShadowVariableDescriptorMap() {
        return effectiveShadowVariableDescriptorMap;
    }

    public Collection<GenuineVariableDescriptor> getGenuineVariableDescriptors() {
        return effectiveGenuineVariableDescriptorMap.values();
    }

    public boolean hasGenuineVariableDescriptor(String variableName) {
        return effectiveGenuineVariableDescriptorMap.containsKey(variableName);
    }
    
    public GenuineVariableDescriptor getGenuineVariableDescriptor(String variableName) {
        return effectiveGenuineVariableDescriptorMap.get(variableName);
    }

    public boolean hasShadowVariableDescriptor(String variableName) {
        return effectiveShadowVariableDescriptorMap.containsKey(variableName);
    }

    public ShadowVariableDescriptor getShadowVariableDescriptor(String variableName) {
        return effectiveShadowVariableDescriptorMap.get(variableName);
    }

    public void addDeclaredVariableListenersToMap(
            Map<VariableDescriptor, List<VariableListener>> variableListenerMap) {
        for (GenuineVariableDescriptor variableDescriptor : declaredGenuineVariableDescriptorMap.values()) {
            variableListenerMap.put(variableDescriptor, variableDescriptor.buildVariableListenerList());
        }
        for (ShadowVariableDescriptor variableDescriptor : declaredShadowVariableDescriptorMap.values()) {
            variableListenerMap.put(variableDescriptor, variableDescriptor.buildVariableListenerList());
        }
    }

    public Collection<VariableDescriptor> getDeclaredVariableDescriptors() {
        Collection<VariableDescriptor> variableDescriptors = new ArrayList<VariableDescriptor>(
                declaredGenuineVariableDescriptorMap.size() + declaredShadowVariableDescriptorMap.size());
        variableDescriptors.addAll(declaredGenuineVariableDescriptorMap.values());
        variableDescriptors.addAll(declaredShadowVariableDescriptorMap.values());
        return variableDescriptors;
    }

    public VariableDescriptor getVariableDescriptor(String variableName) {
        VariableDescriptor variableDescriptor = effectiveGenuineVariableDescriptorMap.get(variableName);
        if (variableDescriptor == null) {
            variableDescriptor = effectiveShadowVariableDescriptorMap.get(variableName);
        }
        return variableDescriptor;
    }

    public String buildInvalidVariableNameExceptionMessage(String variableName) {
        if (!hasProperty(variableName)) {
            String exceptionMessage = "The variableName (" + variableName
                    + ") for entityClass (" + entityClass
                    + ") does not exists as a property (getter/setter) on that class.\n"
                    + "Check the spelling of the variableName (" + variableName + ").";
            if (variableName.length() >= 2
                    && !Character.isUpperCase(variableName.charAt(0))
                    && Character.isUpperCase(variableName.charAt(1))) {
                String correctedVariableName = variableName.substring(0, 1).toUpperCase()
                        + variableName.substring(1);
                exceptionMessage += " It probably needs to be correctedVariableName ("
                        + correctedVariableName + ") instead because the JavaBeans spec states" +
                        " the first letter should be a upper case if the second is upper case.";
            }
            return exceptionMessage;
        }
        return "The variableName (" + variableName
                + ") for entityClass (" + entityClass
                + ") exists as a property (getter/setter) on that class,"
                + " but not as an annotated as a planning variable.\n"
                + "Check if your planning entity's getter has the annotation "
                + PlanningVariable.class.getSimpleName() + " (or a shadow variable annotation).";

    }

    // ************************************************************************
    // Extraction methods
    // ************************************************************************

    public List<Object> extractEntities(Solution solution) {
        return solutionDescriptor.getEntityListByEntityClass(solution, entityClass);
    }

    public long getVariableCount() {
        return effectiveGenuineVariableDescriptorMap.size();
    }

    public long getProblemScale(Solution solution, Object entity) {
        long problemScale = 1L;
        for (GenuineVariableDescriptor variableDescriptor : effectiveGenuineVariableDescriptorMap.values()) {
            problemScale *= variableDescriptor.getValueCount(solution, entity);
        }
        return problemScale;
    }

    public int countUninitializedVariables(Object entity) {
        int count = 0;
        for (GenuineVariableDescriptor variableDescriptor : effectiveGenuineVariableDescriptorMap.values()) {
            if (!variableDescriptor.isInitialized(entity)) {
                count++;
            }
        }
        return count;
    }

    public boolean isInitialized(Object entity) {
        for (GenuineVariableDescriptor variableDescriptor : effectiveGenuineVariableDescriptorMap.values()) {
            if (!variableDescriptor.isInitialized(entity)) {
                return false;
            }
        }
        return true;
    }

    public int countReinitializableVariables(ScoreDirector scoreDirector, Object entity) {
        int count = 0;
        for (GenuineVariableDescriptor variableDescriptor : effectiveGenuineVariableDescriptorMap.values()) {
            if (variableDescriptor.isReinitializable(scoreDirector, entity)) {
                count++;
            }
        }
        return count;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + entityClass.getName() + ")";
    }

}
