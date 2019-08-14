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
import java.lang.reflect.Member;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.entity.PlanningPin;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;
import org.optaplanner.core.api.domain.variable.AnchorShadowVariable;
import org.optaplanner.core.api.domain.variable.CustomShadowVariable;
import org.optaplanner.core.api.domain.variable.InverseRelationShadowVariable;
import org.optaplanner.core.api.domain.variable.PlanningVariable;
import org.optaplanner.core.config.heuristic.selector.common.decorator.SelectionSorterOrder;
import org.optaplanner.core.config.util.ConfigUtils;
import org.optaplanner.core.impl.domain.common.ReflectionHelper;
import org.optaplanner.core.impl.domain.common.accessor.MemberAccessor;
import org.optaplanner.core.impl.domain.common.accessor.MemberAccessorFactory;
import org.optaplanner.core.impl.domain.policy.DescriptorPolicy;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.core.impl.domain.variable.anchor.AnchorShadowVariableDescriptor;
import org.optaplanner.core.impl.domain.variable.custom.CustomShadowVariableDescriptor;
import org.optaplanner.core.impl.domain.variable.descriptor.GenuineVariableDescriptor;
import org.optaplanner.core.impl.domain.variable.descriptor.ShadowVariableDescriptor;
import org.optaplanner.core.impl.domain.variable.descriptor.VariableDescriptor;
import org.optaplanner.core.impl.domain.variable.inverserelation.InverseRelationShadowVariableDescriptor;
import org.optaplanner.core.impl.heuristic.selector.common.decorator.ComparatorSelectionSorter;
import org.optaplanner.core.impl.heuristic.selector.common.decorator.CompositeSelectionFilter;
import org.optaplanner.core.impl.heuristic.selector.common.decorator.SelectionFilter;
import org.optaplanner.core.impl.heuristic.selector.common.decorator.SelectionSorter;
import org.optaplanner.core.impl.heuristic.selector.common.decorator.SelectionSorterWeightFactory;
import org.optaplanner.core.impl.heuristic.selector.common.decorator.WeightFactorySelectionSorter;
import org.optaplanner.core.impl.heuristic.selector.entity.decorator.PinEntityFilter;
import org.optaplanner.core.impl.score.director.ScoreDirector;

import static org.optaplanner.core.impl.domain.common.accessor.MemberAccessorFactory.MemberAccessorType.*;

/**
 * @param <Solution_> the solution type, the class with the {@link PlanningSolution} annotation
 */
public class EntityDescriptor<Solution_> {

    public static final Class[] VARIABLE_ANNOTATION_CLASSES = {
            PlanningVariable.class,
            InverseRelationShadowVariable.class, AnchorShadowVariable.class,
            CustomShadowVariable.class};

    private final SolutionDescriptor<Solution_> solutionDescriptor;

    private final Class<?> entityClass;
    private final Predicate<Object> isInitializedPredicate;
    // Only declared movable filter, excludes inherited and descending movable filters
    private SelectionFilter declaredMovableEntitySelectionFilter;
    private SelectionSorter decreasingDifficultySorter;

    // Only declared variable descriptors, excludes inherited variable descriptors
    private Map<String, GenuineVariableDescriptor<Solution_>> declaredGenuineVariableDescriptorMap;
    private Map<String, ShadowVariableDescriptor<Solution_>> declaredShadowVariableDescriptorMap;

    private List<SelectionFilter> declaredPinEntityFilterList;

    private List<EntityDescriptor<Solution_>> inheritedEntityDescriptorList;

    // Caches the inherited, declared and descending movable filters (including @PlanningPin filters) as a composite filter
    private SelectionFilter effectiveMovableEntitySelectionFilter;

    // Caches the inherited and declared variable descriptors
    private Map<String, GenuineVariableDescriptor<Solution_>> effectiveGenuineVariableDescriptorMap;
    private Map<String, ShadowVariableDescriptor<Solution_>> effectiveShadowVariableDescriptorMap;
    private Map<String, VariableDescriptor<Solution_>> effectiveVariableDescriptorMap;

    // ************************************************************************
    // Constructors and simple getters/setters
    // ************************************************************************

    public EntityDescriptor(SolutionDescriptor<Solution_> solutionDescriptor, Class<?> entityClass) {
        this.solutionDescriptor = solutionDescriptor;
        this.entityClass = entityClass;
        isInitializedPredicate = this::isInitialized;
    }

    /**
     * Using entityDescriptor::isInitialized directly breaks node sharing
     * because it creates multiple instances of this {@link Predicate}.
     * @return never null, always the same {@link Predicate} instance to {@link #isInitialized(Object)}
     */
    public Predicate<Object> getIsInitializedPredicate() {
        return isInitializedPredicate;
    }

    // ************************************************************************
    // Lifecycle methods
    // ************************************************************************

    public void processAnnotations(DescriptorPolicy descriptorPolicy) {
        processEntityAnnotations(descriptorPolicy);
        declaredGenuineVariableDescriptorMap = new LinkedHashMap<>();
        declaredShadowVariableDescriptorMap = new LinkedHashMap<>();
        declaredPinEntityFilterList = new ArrayList<>(2);
        // Only iterate declared fields and methods, not inherited members, to avoid registering the same one twice
        List<Member> memberList = ConfigUtils.getDeclaredMembers(entityClass);
        for (Member member : memberList) {
            processValueRangeProviderAnnotation(descriptorPolicy, member);
            processPlanningVariableAnnotation(descriptorPolicy, member);
            processPlanningPinAnnotation(descriptorPolicy, member);
        }
        if (declaredGenuineVariableDescriptorMap.isEmpty() && declaredShadowVariableDescriptorMap.isEmpty()) {
            throw new IllegalStateException("The entityClass (" + entityClass
                    + ") should have at least 1 getter method or 1 field with a "
                    + PlanningVariable.class.getSimpleName() + " annotation or a shadow variable annotation.");
        }
        processVariableAnnotations(descriptorPolicy);
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
            declaredMovableEntitySelectionFilter = ConfigUtils.newInstance(this,
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
            decreasingDifficultySorter = new ComparatorSelectionSorter<Solution_, Object>(
                    difficultyComparator, SelectionSorterOrder.DESCENDING);
        }
        if (difficultyWeightFactoryClass != null) {
            SelectionSorterWeightFactory<Solution_, Object> difficultyWeightFactory = ConfigUtils.newInstance(this,
                    "difficultyWeightFactoryClass", difficultyWeightFactoryClass);
            decreasingDifficultySorter = new WeightFactorySelectionSorter<>(
                    difficultyWeightFactory, SelectionSorterOrder.DESCENDING);
        }
    }

    private void processValueRangeProviderAnnotation(DescriptorPolicy descriptorPolicy, Member member) {
        if (((AnnotatedElement) member).isAnnotationPresent(ValueRangeProvider.class)) {
            MemberAccessor memberAccessor = MemberAccessorFactory.buildMemberAccessor(
                    member, FIELD_OR_READ_METHOD, ValueRangeProvider.class);
            descriptorPolicy.addFromEntityValueRangeProvider(
                    memberAccessor);
        }
    }

    private void processPlanningVariableAnnotation(DescriptorPolicy descriptorPolicy, Member member) {
        Class<? extends Annotation> variableAnnotationClass = ConfigUtils.extractAnnotationClass(
                member, VARIABLE_ANNOTATION_CLASSES);
        if (variableAnnotationClass != null) {
            MemberAccessorFactory.MemberAccessorType memberAccessorType;
            if (variableAnnotationClass.equals(CustomShadowVariable.class)) {
                memberAccessorType = FIELD_OR_GETTER_METHOD;
            } else {
                memberAccessorType = FIELD_OR_GETTER_METHOD_WITH_SETTER;
            }
            MemberAccessor memberAccessor = MemberAccessorFactory.buildMemberAccessor(
                    member, memberAccessorType, variableAnnotationClass);
            registerVariableAccessor(descriptorPolicy, variableAnnotationClass, memberAccessor);
        }
    }

    private void registerVariableAccessor(DescriptorPolicy descriptorPolicy,
            Class<? extends Annotation> variableAnnotationClass, MemberAccessor memberAccessor) {
        String memberName = memberAccessor.getName();
        if (declaredGenuineVariableDescriptorMap.containsKey(memberName)
                || declaredShadowVariableDescriptorMap.containsKey(memberName)) {
            VariableDescriptor<Solution_> duplicate = declaredGenuineVariableDescriptorMap.get(memberName);
            if (duplicate == null) {
                duplicate = declaredShadowVariableDescriptorMap.get(memberName);
            }
            throw new IllegalStateException("The entityClass (" + entityClass
                    + ") has a " + variableAnnotationClass.getSimpleName()
                    + " annotated member (" + memberAccessor
                    + ") that is duplicated by another member for variableDescriptor (" + duplicate + ").\n"
                    + "Maybe the annotation is defined on both the field and its getter.");
        }
        if (variableAnnotationClass.equals(PlanningVariable.class)) {
            GenuineVariableDescriptor<Solution_> variableDescriptor = new GenuineVariableDescriptor<>(this,
                    memberAccessor);
            declaredGenuineVariableDescriptorMap.put(memberName, variableDescriptor);
        } else if (variableAnnotationClass.equals(InverseRelationShadowVariable.class)) {
            ShadowVariableDescriptor<Solution_> variableDescriptor = new InverseRelationShadowVariableDescriptor<>(
                    this, memberAccessor);
            declaredShadowVariableDescriptorMap.put(memberName, variableDescriptor);
        } else if (variableAnnotationClass.equals(AnchorShadowVariable.class)) {
            ShadowVariableDescriptor<Solution_> variableDescriptor = new AnchorShadowVariableDescriptor<>(
                    this, memberAccessor);
            declaredShadowVariableDescriptorMap.put(memberName, variableDescriptor);
        } else if (variableAnnotationClass.equals(CustomShadowVariable.class)) {
            ShadowVariableDescriptor<Solution_> variableDescriptor = new CustomShadowVariableDescriptor<>(
                    this, memberAccessor);
            declaredShadowVariableDescriptorMap.put(memberName, variableDescriptor);
        } else {
            throw new IllegalStateException("The variableAnnotationClass ("
                    + variableAnnotationClass + ") is not implemented.");
        }
    }

    private void processPlanningPinAnnotation(DescriptorPolicy descriptorPolicy, Member member) {
        if (((AnnotatedElement) member).isAnnotationPresent(PlanningPin.class)) {
            MemberAccessor memberAccessor = MemberAccessorFactory.buildMemberAccessor(
                    member, FIELD_OR_READ_METHOD, PlanningPin.class);
            Class<?> type = memberAccessor.getType();
            if (!Boolean.TYPE.isAssignableFrom(type) && !Boolean.class.isAssignableFrom(type)) {
                throw new IllegalStateException("The entityClass (" + entityClass
                        + ") has a " + PlanningPin.class.getSimpleName()
                        + " annotated member (" + memberAccessor
                        + ") that is not a boolean or Boolean.");
            }
            declaredPinEntityFilterList.add(new PinEntityFilter(memberAccessor));
        }
    }

    private void processVariableAnnotations(DescriptorPolicy descriptorPolicy) {
        for (GenuineVariableDescriptor<Solution_> variableDescriptor : declaredGenuineVariableDescriptorMap.values()) {
            variableDescriptor.processAnnotations(descriptorPolicy);
        }
        for (ShadowVariableDescriptor<Solution_> variableDescriptor : declaredShadowVariableDescriptorMap.values()) {
            variableDescriptor.processAnnotations(descriptorPolicy);
        }
    }

    public void linkEntityDescriptors(DescriptorPolicy descriptorPolicy) {
        investigateParentsToLinkInherited(entityClass);
        createEffectiveVariableDescriptorMaps();
        createEffectiveMovableEntitySelectionFilter();
        // linkVariableDescriptors() is in a separate loop
    }

    private void investigateParentsToLinkInherited(Class<?> investigateClass) {
        inheritedEntityDescriptorList = new ArrayList<>(4);
        if (investigateClass == null || investigateClass.isArray()) {
            return;
        }
        linkInherited(investigateClass.getSuperclass());
        for (Class<?> superInterface : investigateClass.getInterfaces()) {
            linkInherited(superInterface);
        }
    }

    private void linkInherited(Class<?> potentialEntityClass) {
        EntityDescriptor<Solution_> entityDescriptor = solutionDescriptor.getEntityDescriptorStrict(
                potentialEntityClass);
        if (entityDescriptor != null) {
            inheritedEntityDescriptorList.add(entityDescriptor);
        } else {
            investigateParentsToLinkInherited(potentialEntityClass);
        }
    }

    private void createEffectiveVariableDescriptorMaps() {
        effectiveGenuineVariableDescriptorMap = new LinkedHashMap<>(declaredGenuineVariableDescriptorMap.size());
        effectiveShadowVariableDescriptorMap = new LinkedHashMap<>(declaredShadowVariableDescriptorMap.size());
        for (EntityDescriptor<Solution_> inheritedEntityDescriptor : inheritedEntityDescriptorList) {
            effectiveGenuineVariableDescriptorMap.putAll(inheritedEntityDescriptor.getGenuineVariableDescriptorMap());
            effectiveShadowVariableDescriptorMap.putAll(inheritedEntityDescriptor.getShadowVariableDescriptorMap());
        }
        effectiveGenuineVariableDescriptorMap.putAll(declaredGenuineVariableDescriptorMap);
        effectiveShadowVariableDescriptorMap.putAll(declaredShadowVariableDescriptorMap);
        effectiveVariableDescriptorMap = new LinkedHashMap<>(
                effectiveGenuineVariableDescriptorMap.size() + effectiveShadowVariableDescriptorMap.size());
        effectiveVariableDescriptorMap.putAll(effectiveGenuineVariableDescriptorMap);
        effectiveVariableDescriptorMap.putAll(effectiveShadowVariableDescriptorMap);
    }

    private void createEffectiveMovableEntitySelectionFilter() {
        if (declaredMovableEntitySelectionFilter != null && !hasAnyDeclaredGenuineVariableDescriptor()) {
            throw new IllegalStateException("The entityClass (" + entityClass
                    + ") has a movableEntitySelectionFilterClass (" + declaredMovableEntitySelectionFilter.getClass()
                    + "), but it has no declared genuine variables, only shadow variables.");
        }
        List<SelectionFilter> selectionFilterList = new ArrayList<>();
        // TODO Also add in child entity selectors
        for (EntityDescriptor<Solution_> inheritedEntityDescriptor : inheritedEntityDescriptorList) {
            if (inheritedEntityDescriptor.hasEffectiveMovableEntitySelectionFilter()) {
                // Includes movable and pinned
                selectionFilterList.add(inheritedEntityDescriptor.getEffectiveMovableEntitySelectionFilter());
            }
        }
        if (declaredMovableEntitySelectionFilter != null) {
            selectionFilterList.add(declaredMovableEntitySelectionFilter);
        }
        selectionFilterList.addAll(declaredPinEntityFilterList);
        if (selectionFilterList.isEmpty()) {
            effectiveMovableEntitySelectionFilter = null;
        } else if (selectionFilterList.size() == 1) {
            effectiveMovableEntitySelectionFilter = selectionFilterList.get(0);
        } else {
            effectiveMovableEntitySelectionFilter = new CompositeSelectionFilter(selectionFilterList);
        }
    }

    public void linkVariableDescriptors(DescriptorPolicy descriptorPolicy) {
        for (GenuineVariableDescriptor<Solution_> variableDescriptor : declaredGenuineVariableDescriptorMap.values()) {
            variableDescriptor.linkVariableDescriptors(descriptorPolicy);
        }
        for (ShadowVariableDescriptor<Solution_> shadowVariableDescriptor : declaredShadowVariableDescriptorMap.values()) {
            shadowVariableDescriptor.linkVariableDescriptors(descriptorPolicy);
        }
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    public SolutionDescriptor<Solution_> getSolutionDescriptor() {
        return solutionDescriptor;
    }

    public Class<?> getEntityClass() {
        return entityClass;
    }

    public boolean matchesEntity(Object entity) {
        return entityClass.isAssignableFrom(entity.getClass());
    }

    public boolean hasEffectiveMovableEntitySelectionFilter() {
        return effectiveMovableEntitySelectionFilter != null;
    }

    public SelectionFilter getEffectiveMovableEntitySelectionFilter() {
        return effectiveMovableEntitySelectionFilter;
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

    public Map<String, GenuineVariableDescriptor<Solution_>> getGenuineVariableDescriptorMap() {
        return effectiveGenuineVariableDescriptorMap;
    }

    public Collection<GenuineVariableDescriptor<Solution_>> getGenuineVariableDescriptors() {
        return effectiveGenuineVariableDescriptorMap.values();
    }

    public List<GenuineVariableDescriptor<Solution_>> getGenuineVariableDescriptorList() {
        // TODO We might want to cache that list
        return new ArrayList<>(effectiveGenuineVariableDescriptorMap.values());
    }

    public boolean hasGenuineVariableDescriptor(String variableName) {
        return effectiveGenuineVariableDescriptorMap.containsKey(variableName);
    }

    public GenuineVariableDescriptor<Solution_> getGenuineVariableDescriptor(String variableName) {
        return effectiveGenuineVariableDescriptorMap.get(variableName);
    }

    public Map<String, ShadowVariableDescriptor<Solution_>> getShadowVariableDescriptorMap() {
        return effectiveShadowVariableDescriptorMap;
    }

    public Collection<ShadowVariableDescriptor<Solution_>> getShadowVariableDescriptors() {
        return effectiveShadowVariableDescriptorMap.values();
    }

    public boolean hasShadowVariableDescriptor(String variableName) {
        return effectiveShadowVariableDescriptorMap.containsKey(variableName);
    }

    public ShadowVariableDescriptor<Solution_> getShadowVariableDescriptor(String variableName) {
        return effectiveShadowVariableDescriptorMap.get(variableName);
    }

    public Map<String, VariableDescriptor<Solution_>> getVariableDescriptorMap() {
        return effectiveVariableDescriptorMap;
    }

    public Collection<VariableDescriptor<Solution_>> getVariableDescriptors() {
        return effectiveVariableDescriptorMap.values();
    }

    public boolean hasVariableDescriptor(String variableName) {
        return effectiveVariableDescriptorMap.containsKey(variableName);
    }

    public VariableDescriptor<Solution_> getVariableDescriptor(String variableName) {
        return effectiveVariableDescriptorMap.get(variableName);
    }

    public Collection<GenuineVariableDescriptor<Solution_>> getDeclaredGenuineVariableDescriptors() {
        return declaredGenuineVariableDescriptorMap.values();
    }

    public Collection<ShadowVariableDescriptor<Solution_>> getDeclaredShadowVariableDescriptors() {
        return declaredShadowVariableDescriptorMap.values();
    }

    public Collection<VariableDescriptor<Solution_>> getDeclaredVariableDescriptors() {
        Collection<VariableDescriptor<Solution_>> variableDescriptors = new ArrayList<>(
                declaredGenuineVariableDescriptorMap.size() + declaredShadowVariableDescriptorMap.size());
        variableDescriptors.addAll(declaredGenuineVariableDescriptorMap.values());
        variableDescriptors.addAll(declaredShadowVariableDescriptorMap.values());
        return variableDescriptors;
    }

    public String buildInvalidVariableNameExceptionMessage(String variableName) {
        if (!ReflectionHelper.hasGetterMethod(entityClass, variableName)
                && !ReflectionHelper.hasField(entityClass, variableName)) {
            String exceptionMessage = "The variableName (" + variableName
                    + ") for entityClass (" + entityClass
                    + ") does not exists as a getter or field on that class.\n"
                    + "Check the spelling of the variableName (" + variableName + ").";
            if (variableName.length() >= 2
                    && !Character.isUpperCase(variableName.charAt(0))
                    && Character.isUpperCase(variableName.charAt(1))) {
                String correctedVariableName = variableName.substring(0, 1).toUpperCase() + variableName.substring(1);
                exceptionMessage += "Maybe it needs to be correctedVariableName (" + correctedVariableName
                        + ") instead, if it's a getter, because the JavaBeans spec states that "
                        + "the first letter should be a upper case if the second is upper case.";
            }
            return exceptionMessage;
        }
        return "The variableName (" + variableName
                + ") for entityClass (" + entityClass
                + ") exists as a getter or field on that class,"
                + " but isn't in the planning variables (" + effectiveVariableDescriptorMap.keySet() + ").\n"
                + (Character.isUpperCase(variableName.charAt(0)) ? "Maybe the variableName (" + variableName + ") should start with a lowercase.\n" : "")
                + "Maybe your planning entity's getter or field lacks a " + PlanningVariable.class.getSimpleName()
                + " annotation or a shadow variable annotation.";
    }

    public boolean hasAnyGenuineVariables() {
        return !effectiveGenuineVariableDescriptorMap.isEmpty();
    }

    public boolean hasAnyChainedGenuineVariables() {
        for (GenuineVariableDescriptor<Solution_> variableDescriptor : effectiveGenuineVariableDescriptorMap.values()) {
            if (!variableDescriptor.isChained()) {
                return true;
            }
        }
        return false;
    }

    // ************************************************************************
    // Extraction methods
    // ************************************************************************

    public List<Object> extractEntities(Solution_ solution) {
        return solutionDescriptor.getEntityListByEntityClass(solution, entityClass);
    }

    public long getGenuineVariableCount() {
        return effectiveGenuineVariableDescriptorMap.size();
    }

    public long getMaximumValueCount(Solution_ solution, Object entity) {
        long maximumValueCount = 0L;
        for (GenuineVariableDescriptor<Solution_> variableDescriptor : effectiveGenuineVariableDescriptorMap.values()) {
            maximumValueCount = Math.max(maximumValueCount, variableDescriptor.getValueCount(solution, entity));
        }
        return maximumValueCount;

    }

    public long getProblemScale(Solution_ solution, Object entity) {
        long problemScale = 1L;
        for (GenuineVariableDescriptor<Solution_> variableDescriptor : effectiveGenuineVariableDescriptorMap.values()) {
            problemScale *= variableDescriptor.getValueCount(solution, entity);
        }
        return problemScale;
    }

    public int countUninitializedVariables(Object entity) {
        int count = 0;
        for (GenuineVariableDescriptor<Solution_> variableDescriptor : effectiveGenuineVariableDescriptorMap.values()) {
            if (!variableDescriptor.isInitialized(entity)) {
                count++;
            }
        }
        return count;
    }

    public boolean isInitialized(Object entity) {
        for (GenuineVariableDescriptor<Solution_> variableDescriptor : effectiveGenuineVariableDescriptorMap.values()) {
            if (!variableDescriptor.isInitialized(entity)) {
                return false;
            }
        }
        return true;
    }

    public int countReinitializableVariables(ScoreDirector<Solution_> scoreDirector, Object entity) {
        int count = 0;
        for (GenuineVariableDescriptor<Solution_> variableDescriptor : effectiveGenuineVariableDescriptorMap.values()) {
            if (variableDescriptor.isReinitializable(scoreDirector, entity)) {
                count++;
            }
        }
        return count;
    }

    public boolean isMovable(ScoreDirector<Solution_> scoreDirector, Object entity) {
        return effectiveMovableEntitySelectionFilter == null || effectiveMovableEntitySelectionFilter.accept(scoreDirector, entity);
    }

    /**
     * @param scoreDirector never null
     * @param entity never null
     * @return true if the entity is initialized or immovable
     */
    public boolean isEntityInitializedOrImmovable(ScoreDirector<Solution_> scoreDirector, Object entity) {
        return isInitialized(entity) || !isMovable(scoreDirector, entity);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + entityClass.getName() + ")";
    }

}
