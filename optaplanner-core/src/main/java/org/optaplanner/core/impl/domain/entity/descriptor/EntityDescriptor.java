/*
 * Copyright 2011 Red Hat, Inc. and/or its affiliates.
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

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.solution.Solution;
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;
import org.optaplanner.core.api.domain.variable.AnchorShadowVariable;
import org.optaplanner.core.api.domain.variable.CustomShadowVariable;
import org.optaplanner.core.api.domain.variable.InverseRelationShadowVariable;
import org.optaplanner.core.api.domain.variable.PlanningVariable;
import org.optaplanner.core.config.heuristic.selector.common.decorator.SelectionSorterOrder;
import org.optaplanner.core.config.util.ConfigUtils;
import org.optaplanner.core.impl.domain.common.AlphabeticMemberComparator;
import org.optaplanner.core.impl.domain.common.accessor.FieldMemberAccessor;
import org.optaplanner.core.impl.domain.common.accessor.MemberAccessor;
import org.optaplanner.core.impl.domain.common.accessor.BeanPropertyMemberAccessor;
import org.optaplanner.core.impl.domain.common.ReflectionHelper;
import org.optaplanner.core.impl.domain.common.accessor.MethodMemberAccessor;
import org.optaplanner.core.impl.domain.policy.DescriptorPolicy;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.core.impl.domain.variable.anchor.AnchorShadowVariableDescriptor;
import org.optaplanner.core.impl.domain.variable.custom.CustomShadowVariableDescriptor;
import org.optaplanner.core.impl.domain.variable.descriptor.GenuineVariableDescriptor;
import org.optaplanner.core.impl.domain.variable.inverserelation.InverseRelationShadowVariableDescriptor;
import org.optaplanner.core.impl.domain.variable.descriptor.ShadowVariableDescriptor;
import org.optaplanner.core.impl.domain.variable.descriptor.VariableDescriptor;
import org.optaplanner.core.impl.heuristic.selector.common.decorator.ComparatorSelectionSorter;
import org.optaplanner.core.impl.heuristic.selector.common.decorator.SelectionFilter;
import org.optaplanner.core.impl.heuristic.selector.common.decorator.SelectionSorter;
import org.optaplanner.core.impl.heuristic.selector.common.decorator.SelectionSorterWeightFactory;
import org.optaplanner.core.impl.heuristic.selector.common.decorator.WeightFactorySelectionSorter;
import org.optaplanner.core.impl.score.director.ScoreDirector;

public class EntityDescriptor {

    public static final List<Class<? extends Annotation>> VARIABLE_ANNOTATION_CLASSES = Arrays.asList(
            PlanningVariable.class,
            InverseRelationShadowVariable.class, AnchorShadowVariable.class,
            CustomShadowVariable.class);

    private final SolutionDescriptor solutionDescriptor;

    private final Class<?> entityClass;
    private SelectionFilter movableEntitySelectionFilter;
    private SelectionSorter decreasingDifficultySorter;

    private List<EntityDescriptor> inheritedEntityDescriptorList;

    // Only declared variable descriptors, excludes inherited variable descriptors
    private Map<String, GenuineVariableDescriptor> declaredGenuineVariableDescriptorMap;
    private Map<String, ShadowVariableDescriptor> declaredShadowVariableDescriptorMap;

    // Caches the inherited and declared variable descriptors
    private Map<String, GenuineVariableDescriptor> effectiveGenuineVariableDescriptorMap;
    private Map<String, ShadowVariableDescriptor> effectiveShadowVariableDescriptorMap;
    private Map<String, VariableDescriptor> effectiveVariableDescriptorMap;

    public EntityDescriptor(SolutionDescriptor solutionDescriptor, Class<?> entityClass) {
        this.solutionDescriptor = solutionDescriptor;
        this.entityClass = entityClass;
    }

    public void processAnnotations(DescriptorPolicy descriptorPolicy) {
        processEntityAnnotations(descriptorPolicy);
        processValueRangeProviderAnnotations(descriptorPolicy);
        processPlanningVariableAnnotations(descriptorPolicy);
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

    private void processValueRangeProviderAnnotations(DescriptorPolicy descriptorPolicy) {
        // Only iterate declared fields and methods, not inherited members, to avoid registering the same ValueRangeProvider twice
        List<Field> fieldList = Arrays.asList(entityClass.getDeclaredFields());
        Collections.sort(fieldList, new AlphabeticMemberComparator());
        for (Field field : fieldList) {
            if (field.isAnnotationPresent(ValueRangeProvider.class)) {
                MemberAccessor memberAccessor = new FieldMemberAccessor(field);
                descriptorPolicy.addFromEntityValueRangeProvider(memberAccessor);
            }
        }
        List<Method> methodList = Arrays.asList(entityClass.getDeclaredMethods());
        Collections.sort(methodList, new AlphabeticMemberComparator());
        for (Method method : methodList) {
            if (method.isAnnotationPresent(ValueRangeProvider.class)) {
                ReflectionHelper.assertReadMethod(method, ValueRangeProvider.class);
                MemberAccessor memberAccessor = new MethodMemberAccessor(method);
                descriptorPolicy.addFromEntityValueRangeProvider(memberAccessor);
            }
        }
    }

    private void processPlanningVariableAnnotations(DescriptorPolicy descriptorPolicy) {
        declaredGenuineVariableDescriptorMap = new LinkedHashMap<String, GenuineVariableDescriptor>();
        declaredShadowVariableDescriptorMap = new LinkedHashMap<String, ShadowVariableDescriptor>();
        boolean noVariableAnnotation = true;
        List<Field> fieldList = Arrays.asList(entityClass.getDeclaredFields());
        Collections.sort(fieldList, new AlphabeticMemberComparator());
        for (Field field : fieldList) {
            Class<? extends Annotation> variableAnnotationClass = extractVariableAnnotationClass(field);
            if (variableAnnotationClass != null) {
                noVariableAnnotation = false;
                MemberAccessor memberAccessor = new FieldMemberAccessor(field);
                registerVariableAccessor(descriptorPolicy, variableAnnotationClass, memberAccessor);
            }
        }
        List<Method> methodList = Arrays.asList(entityClass.getDeclaredMethods());
        Collections.sort(methodList, new AlphabeticMemberComparator());
        for (Method method : methodList) {
            Class<? extends Annotation> variableAnnotationClass = extractVariableAnnotationClass(method);
            if (variableAnnotationClass != null) {
                noVariableAnnotation = false;
                ReflectionHelper.assertGetterMethod(method, variableAnnotationClass);
                MemberAccessor memberAccessor = new BeanPropertyMemberAccessor(method);
                if (!memberAccessor.supportSetter()) {
                    throw new IllegalStateException("The entityClass (" + entityClass
                            + ") has a " + variableAnnotationClass.getSimpleName()
                            + " annotated getter method (" + method
                            + "), but lacks a setter for that property (" + memberAccessor.getName() + ").");
                }
                registerVariableAccessor(descriptorPolicy, variableAnnotationClass, memberAccessor);
            }
        }
        if (noVariableAnnotation) {
            throw new IllegalStateException("The entityClass (" + entityClass
                    + ") should have at least 1 getter method or 1 field with a "
                    + PlanningVariable.class.getSimpleName() + " annotation or a shadow variable annotation.");
        }
    }

    private Class<? extends Annotation> extractVariableAnnotationClass(AnnotatedElement member) {
        Class<? extends Annotation> annotationClass = null;
        for (Class<? extends Annotation> detectedAnnotationClass : VARIABLE_ANNOTATION_CLASSES) {
            if (member.isAnnotationPresent(detectedAnnotationClass)) {
                if (annotationClass != null) {
                    throw new IllegalStateException("The entityClass (" + entityClass
                            + ") has a member (" + member + ") that has both a "
                            + annotationClass.getSimpleName() + " annotation and a "
                            + detectedAnnotationClass.getSimpleName() + " annotation.");
                }
                annotationClass = detectedAnnotationClass;
                // Do not break early: check other annotations too
            }
        }
        return annotationClass;
    }

    private void registerVariableAccessor(DescriptorPolicy descriptorPolicy,
            Class<? extends Annotation> variableAnnotationClass, MemberAccessor memberAccessor) {
        String memberName = memberAccessor.getName();
        if (declaredGenuineVariableDescriptorMap.containsKey(memberName)
                || declaredShadowVariableDescriptorMap.containsKey(memberName)) {
            VariableDescriptor duplicate = declaredGenuineVariableDescriptorMap.get(memberName);
            if (duplicate == null) {
                duplicate = declaredShadowVariableDescriptorMap.get(memberName);
            }
            throw new IllegalStateException("The entityClass (" + entityClass
                    + ") has a " + variableAnnotationClass.getSimpleName()
                    + " annotated member (" + memberAccessor
                    + ") that is duplicated by another member for variableDescriptor (" + duplicate + ").\n"
                    + "  Verify that the annotation is not defined on both the field and its getter.");
        }
        if (variableAnnotationClass.equals(PlanningVariable.class)) {
            GenuineVariableDescriptor variableDescriptor = new GenuineVariableDescriptor(
                    this, memberAccessor);
            declaredGenuineVariableDescriptorMap.put(memberName, variableDescriptor);
            variableDescriptor.processAnnotations(descriptorPolicy);
        } else if (variableAnnotationClass.equals(InverseRelationShadowVariable.class)) {
            ShadowVariableDescriptor variableDescriptor = new InverseRelationShadowVariableDescriptor(
                    this, memberAccessor);
            declaredShadowVariableDescriptorMap.put(memberName, variableDescriptor);
            variableDescriptor.processAnnotations(descriptorPolicy);
        } else if (variableAnnotationClass.equals(AnchorShadowVariable.class)) {
            ShadowVariableDescriptor variableDescriptor = new AnchorShadowVariableDescriptor(
                    this, memberAccessor);
            declaredShadowVariableDescriptorMap.put(memberName, variableDescriptor);
            variableDescriptor.processAnnotations(descriptorPolicy);
        } else if (variableAnnotationClass.equals(CustomShadowVariable.class)) {
            ShadowVariableDescriptor variableDescriptor = new CustomShadowVariableDescriptor(
                    this, memberAccessor);
            declaredShadowVariableDescriptorMap.put(memberName, variableDescriptor);
            variableDescriptor.processAnnotations(descriptorPolicy);
        } else {
            throw new IllegalStateException("The variableAnnotationClass ("
                    + variableAnnotationClass + ") is not implemented.");
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
        effectiveVariableDescriptorMap = new LinkedHashMap<String, VariableDescriptor>(
                effectiveGenuineVariableDescriptorMap.size() + effectiveShadowVariableDescriptorMap.size());
        effectiveVariableDescriptorMap.putAll(effectiveGenuineVariableDescriptorMap);
        effectiveVariableDescriptorMap.putAll(effectiveShadowVariableDescriptorMap);
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

    public boolean hasAnyDeclaredGenuineVariableDescriptor() {
        return !declaredGenuineVariableDescriptorMap.isEmpty();
    }

    public Collection<String> getGenuineVariableNameSet() {
        return effectiveGenuineVariableDescriptorMap.keySet();
    }

    public Map<String, GenuineVariableDescriptor> getGenuineVariableDescriptorMap() {
        return effectiveGenuineVariableDescriptorMap;
    }

    public Collection<GenuineVariableDescriptor> getGenuineVariableDescriptors() {
        return effectiveGenuineVariableDescriptorMap.values();
    }

    public List<GenuineVariableDescriptor> getGenuineVariableDescriptorList() {
        // TODO We might want to cache that list
        return new ArrayList<GenuineVariableDescriptor>(effectiveGenuineVariableDescriptorMap.values());
    }

    public boolean hasGenuineVariableDescriptor(String variableName) {
        return effectiveGenuineVariableDescriptorMap.containsKey(variableName);
    }

    public GenuineVariableDescriptor getGenuineVariableDescriptor(String variableName) {
        return effectiveGenuineVariableDescriptorMap.get(variableName);
    }

    public Map<String, ShadowVariableDescriptor> getShadowVariableDescriptorMap() {
        return effectiveShadowVariableDescriptorMap;
    }

    public Collection<ShadowVariableDescriptor> getShadowVariableDescriptors() {
        return effectiveShadowVariableDescriptorMap.values();
    }

    public boolean hasShadowVariableDescriptor(String variableName) {
        return effectiveShadowVariableDescriptorMap.containsKey(variableName);
    }

    public ShadowVariableDescriptor getShadowVariableDescriptor(String variableName) {
        return effectiveShadowVariableDescriptorMap.get(variableName);
    }

    public Map<String, VariableDescriptor> getVariableDescriptorMap() {
        return effectiveVariableDescriptorMap;
    }

    public Collection<VariableDescriptor> getVariableDescriptors() {
        return effectiveVariableDescriptorMap.values();
    }

    public boolean hasVariableDescriptor(String variableName) {
        return effectiveVariableDescriptorMap.containsKey(variableName);
    }

    public VariableDescriptor getVariableDescriptor(String variableName) {
        return effectiveVariableDescriptorMap.get(variableName);
    }

    public Collection<GenuineVariableDescriptor> getDeclaredGenuineVariableDescriptors() {
        return declaredGenuineVariableDescriptorMap.values();
    }

    public Collection<ShadowVariableDescriptor> getDeclaredShadowVariableDescriptors() {
        return declaredShadowVariableDescriptorMap.values();
    }

    public Collection<VariableDescriptor> getDeclaredVariableDescriptors() {
        Collection<VariableDescriptor> variableDescriptors = new ArrayList<VariableDescriptor>(
                declaredGenuineVariableDescriptorMap.size() + declaredShadowVariableDescriptorMap.size());
        variableDescriptors.addAll(declaredGenuineVariableDescriptorMap.values());
        variableDescriptors.addAll(declaredShadowVariableDescriptorMap.values());
        return variableDescriptors;
    }

    public String buildInvalidVariableNameExceptionMessage(String variableName) {
        if (!ReflectionHelper.hasGetterMethod(entityClass, variableName)) {
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

    public boolean hasAnyChainedGenuineVariables() {
        for (GenuineVariableDescriptor variableDescriptor : effectiveGenuineVariableDescriptorMap.values()) {
            if (!variableDescriptor.isChained()) {
                return true;
            }
        }
        return false;
    }

    // ************************************************************************
    // Extraction methods
    // ************************************************************************

    public List<Object> extractEntities(Solution solution) {
        return solutionDescriptor.getEntityListByEntityClass(solution, entityClass);
    }

    public long getGenuineVariableCount() {
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

    public boolean isMovable(ScoreDirector scoreDirector, Object entity) {
        return movableEntitySelectionFilter == null || movableEntitySelectionFilter.accept(scoreDirector, entity);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + entityClass.getName() + ")";
    }

}
