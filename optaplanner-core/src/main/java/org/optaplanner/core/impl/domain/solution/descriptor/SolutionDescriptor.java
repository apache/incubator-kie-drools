/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.core.impl.domain.solution.descriptor;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Iterators;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.optaplanner.core.api.domain.solution.PlanningEntityCollectionProperty;
import org.optaplanner.core.api.domain.solution.PlanningEntityProperty;
import org.optaplanner.core.api.domain.solution.drools.ProblemFactCollectionProperty;
import org.optaplanner.core.api.domain.solution.drools.ProblemFactProperty;
import org.optaplanner.core.api.domain.solution.PlanningScore;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.domain.solution.Solution;
import org.optaplanner.core.api.domain.solution.cloner.PlanningCloneable;
import org.optaplanner.core.api.domain.solution.cloner.SolutionCloner;
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.config.util.ConfigUtils;
import org.optaplanner.core.impl.domain.common.AlphabeticMemberComparator;
import org.optaplanner.core.impl.domain.common.ReflectionHelper;
import org.optaplanner.core.impl.domain.common.accessor.BeanPropertyMemberAccessor;
import org.optaplanner.core.impl.domain.common.accessor.FieldMemberAccessor;
import org.optaplanner.core.impl.domain.common.accessor.MemberAccessor;
import org.optaplanner.core.impl.domain.common.accessor.MethodMemberAccessor;
import org.optaplanner.core.impl.domain.entity.descriptor.EntityDescriptor;
import org.optaplanner.core.impl.domain.policy.DescriptorPolicy;
import org.optaplanner.core.impl.domain.solution.cloner.FieldAccessingSolutionCloner;
import org.optaplanner.core.impl.domain.solution.cloner.PlanningCloneableSolutionCloner;
import org.optaplanner.core.impl.domain.variable.descriptor.GenuineVariableDescriptor;
import org.optaplanner.core.impl.domain.variable.descriptor.ShadowVariableDescriptor;
import org.optaplanner.core.impl.domain.variable.descriptor.VariableDescriptor;
import org.optaplanner.core.impl.score.director.ScoreDirector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @param <Solution_> the solution type, the class with the {@link PlanningSolution} annotation
 */
public class SolutionDescriptor<Solution_> {

    public static <Solution_> SolutionDescriptor<Solution_> buildSolutionDescriptor(Class<Solution_> solutionClass,
            Class<?> ... entityClasses) {
        return buildSolutionDescriptor(solutionClass, Arrays.asList(entityClasses));
    }

    public static <Solution_> SolutionDescriptor<Solution_> buildSolutionDescriptor(Class<Solution_> solutionClass,
            List<Class<?>> entityClassList) {
        DescriptorPolicy descriptorPolicy = new DescriptorPolicy();
        SolutionDescriptor<Solution_> solutionDescriptor = new SolutionDescriptor<>(solutionClass);
        solutionDescriptor.processAnnotations(descriptorPolicy);
        for (Class<?> entityClass : sortEntityClassList(entityClassList)) {
            EntityDescriptor<Solution_> entityDescriptor = new EntityDescriptor<>(solutionDescriptor, entityClass);
            solutionDescriptor.addEntityDescriptor(entityDescriptor);
            entityDescriptor.processAnnotations(descriptorPolicy);
        }
        solutionDescriptor.afterAnnotationsProcessed(descriptorPolicy);
        return solutionDescriptor;
    }

    private static List<Class<?>> sortEntityClassList(List<Class<?>> entityClassList) {
        List<Class<?>> sortedEntityClassList = new ArrayList<>(entityClassList.size());
        for (Class<?> entityClass : entityClassList) {
            boolean added = false;
            for (int i = 0; i < sortedEntityClassList.size(); i++) {
                Class<?> sortedEntityClass = sortedEntityClassList.get(i);
                if (entityClass.isAssignableFrom(sortedEntityClass)) {
                    sortedEntityClassList.add(i, entityClass);
                    added = true;
                    break;
                }
            }
            if (!added) {
                sortedEntityClassList.add(entityClass);
            }
        }
        return sortedEntityClassList;
    }

    // ************************************************************************
    // Non-static members
    // ************************************************************************

    protected final transient Logger logger = LoggerFactory.getLogger(getClass());

    private final Class<Solution_> solutionClass;
    private SolutionCloner<Solution_> solutionCloner;

    private final Map<String, MemberAccessor> entityPropertyAccessorMap;
    private final Map<String, MemberAccessor> entityCollectionPropertyAccessorMap;

    private final Map<String, MemberAccessor> factPropertyAccessorMap;
    private final Map<String, MemberAccessor> factCollectionPropertyAccessorMap;

    private MemberAccessor scoreAccessor;

    private final Map<Class<?>, EntityDescriptor<Solution_>> entityDescriptorMap;
    private final List<Class<?>> reversedEntityClassList;
    private final Map<Class<?>, EntityDescriptor<Solution_>> lowestEntityDescriptorCache;

    public SolutionDescriptor(Class<Solution_> solutionClass) {
        this.solutionClass = solutionClass;
        factPropertyAccessorMap = new LinkedHashMap<>();
        factCollectionPropertyAccessorMap = new LinkedHashMap<>();
        entityPropertyAccessorMap = new LinkedHashMap<>();
        entityCollectionPropertyAccessorMap = new LinkedHashMap<>();
        entityDescriptorMap = new LinkedHashMap<>();
        reversedEntityClassList = new ArrayList<>();
        lowestEntityDescriptorCache = new HashMap<>();
    }

    public void addEntityDescriptor(EntityDescriptor<Solution_> entityDescriptor) {
        Class<?> entityClass = entityDescriptor.getEntityClass();
        for (Class<?> otherEntityClass : entityDescriptorMap.keySet()) {
            if (entityClass.isAssignableFrom(otherEntityClass)) {
                throw new IllegalArgumentException("An earlier entityClass (" + otherEntityClass
                        + ") should not be a subclass of a later entityClass (" + entityClass
                        + "). Switch their declaration so superclasses are defined earlier.");
            }
        }
        entityDescriptorMap.put(entityClass, entityDescriptor);
        reversedEntityClassList.add(0, entityClass);
        lowestEntityDescriptorCache.put(entityClass, entityDescriptor);
    }

    private void processLegacySolution() {
        boolean hasFactAnnotation = !(this.factCollectionPropertyAccessorMap.isEmpty() && this.factPropertyAccessorMap.isEmpty());
        if (hasFactAnnotation) {
            throw new IllegalStateException("The solutionClass (" + solutionClass
                    + ") must not have any ProblemFactProperty or ProblemFactCollectionProperty annotations when " +
                    "implementing the legacy Solution interface.");
        } else {
            try {
                Method getProblemFactsMethod = solutionClass.getMethod("getProblemFacts");
                registerFactPropertyAccessor(ProblemFactCollectionProperty.class,
                        new MethodMemberAccessor(getProblemFactsMethod));
            } catch (NoSuchMethodException e) {
                throw new IllegalStateException("Impossible thing just happened. Implementation of Solution " +
                        "interface does not implement all of its methods.");
            }
        }
        boolean hasScoreAnnotation = this.scoreAccessor != null;
        if (hasScoreAnnotation) {
            throw new IllegalStateException("The solutionClass (" + solutionClass
                    + ") must not have any PlanningScore annotation when implementing the legacy Solution interface.");
        } else {
            try {
                Method getScoreMethod = solutionClass.getMethod("getScore");
                registerScoreAccessor(new BeanPropertyMemberAccessor(getScoreMethod));
            } catch (NoSuchMethodException e) {
                throw new IllegalStateException("Impossible thing just happened. Implementation of Solution " +
                        "interface does not implement all of its methods.");
            }
        }
    }

    public void processAnnotations(DescriptorPolicy descriptorPolicy) {
        processSolutionAnnotations(descriptorPolicy);
        ClassBrowser.getAllVisibleFields(solutionClass).stream().sorted(new AlphabeticMemberComparator())
                .forEach(field -> {
            processScoreAnnotation(descriptorPolicy, field);
            processValueRangeProviderAnnotation(descriptorPolicy, field);
            processEntityPropertyAnnotation(descriptorPolicy, field);
            processFactPropertyAnnotation(descriptorPolicy, field);
        });
        ClassBrowser.getAllVisibleMethods(solutionClass).stream().sorted(new AlphabeticMemberComparator())
                .forEach(method -> {
            processScoreAnnotation(descriptorPolicy, method);
            processValueRangeProviderAnnotation(descriptorPolicy, method);
            processEntityPropertyAnnotation(descriptorPolicy, method);
            processFactPropertyAnnotation(descriptorPolicy, method);
        });
        if (!hasEntityAnnotation()) {
            throw new IllegalStateException("The solutionClass (" + solutionClass
                    + ") should have at least 1 getter with a PlanningEntityCollectionProperty or PlanningEntityProperty"
                    + " annotation.");
        } else if (isLegacySolution()) {
            // TODO delete when where're getting rid of Solution
            processLegacySolution();
        } else {
            // the solution class does not implement Solution
            if (scoreAccessor == null) {
                throw new IllegalStateException("The solutionClass (" + solutionClass
                        + ") must have either a Score-returning getter method annotated with PlanningScore annotation with"
                        + " equivalent setter, or a field annotated the same.");
            }
        }
    }

    private boolean isLegacySolution() {
        return Solution.class.isAssignableFrom(solutionClass);
    }

    private void processSolutionAnnotations(DescriptorPolicy descriptorPolicy) {
        PlanningSolution solutionAnnotation = solutionClass.getAnnotation(PlanningSolution.class);
        if (solutionAnnotation == null) {
            throw new IllegalStateException("The solutionClass (" + solutionClass
                    + ") has been specified as a solution in the configuration," +
                    " but does not have a " + PlanningSolution.class.getSimpleName() + " annotation.");
        }
        processSolutionCloner(descriptorPolicy, solutionAnnotation);
    }

    private void processSolutionCloner(DescriptorPolicy descriptorPolicy, PlanningSolution solutionAnnotation) {
        Class<? extends SolutionCloner> solutionClonerClass = solutionAnnotation.solutionCloner();
        if (solutionClonerClass == PlanningSolution.NullSolutionCloner.class) {
            solutionClonerClass = null;
        }
        if (solutionClonerClass != null) {
            solutionCloner = ConfigUtils.newInstance(this, "solutionClonerClass", solutionClonerClass);
        } else {
            if (PlanningCloneable.class.isAssignableFrom(solutionClass)) {
                solutionCloner = new PlanningCloneableSolutionCloner<>();
            } else {
                solutionCloner = new FieldAccessingSolutionCloner<>(this);
            }
        }
    }

    private void processValueRangeProviderAnnotation(DescriptorPolicy descriptorPolicy, Field field) {
        if (field.isAnnotationPresent(ValueRangeProvider.class)) {
            MemberAccessor memberAccessor = new FieldMemberAccessor(field);
            descriptorPolicy.addFromSolutionValueRangeProvider(memberAccessor);
        }
    }

    private void processValueRangeProviderAnnotation(DescriptorPolicy descriptorPolicy, Method method) {
        if (method.isAnnotationPresent(ValueRangeProvider.class)) {
            ReflectionHelper.assertReadMethod(method, ValueRangeProvider.class);
            MemberAccessor memberAccessor = new MethodMemberAccessor(method);
            descriptorPolicy.addFromSolutionValueRangeProvider(memberAccessor);
        }
    }

    private void processEntityPropertyAnnotation(DescriptorPolicy descriptorPolicy, Field field) {
        Class<? extends Annotation> entityPropertyAnnotationClass = extractEntityPropertyAnnotationClass(field);
        if (entityPropertyAnnotationClass != null) {
            MemberAccessor memberAccessor = new FieldMemberAccessor(field);
            registerPropertyAccessor(entityPropertyAnnotationClass, memberAccessor);
        }
    }

    private void processEntityPropertyAnnotation(DescriptorPolicy descriptorPolicy, Method method) {
        Class<? extends Annotation> entityPropertyAnnotationClass = extractEntityPropertyAnnotationClass(method);
        if (entityPropertyAnnotationClass != null) {
            ReflectionHelper.assertGetterMethod(method, entityPropertyAnnotationClass);
            MemberAccessor memberAccessor = new BeanPropertyMemberAccessor(method);
            registerPropertyAccessor(entityPropertyAnnotationClass, memberAccessor);
        }
    }

    private void processFactPropertyAnnotation(DescriptorPolicy descriptorPolicy, Field field) {
        Class<? extends Annotation> factPropertyAnnotationClass = extractFactPropertyAnnotationClass(field);
        if (factPropertyAnnotationClass != null) {
            MemberAccessor memberAccessor = new FieldMemberAccessor(field);
            registerFactPropertyAccessor(factPropertyAnnotationClass, memberAccessor);
        }
    }

    private void processFactPropertyAnnotation(DescriptorPolicy descriptorPolicy, Method method) {
        Class<? extends Annotation> factPropertyAnnotationClass = extractFactPropertyAnnotationClass(method);
        if (factPropertyAnnotationClass != null) {
            ReflectionHelper.assertGetterMethod(method, factPropertyAnnotationClass);
            MemberAccessor memberAccessor = new BeanPropertyMemberAccessor(method);
            registerFactPropertyAccessor(factPropertyAnnotationClass, memberAccessor);
        }
    }

    private void processScoreAnnotation(DescriptorPolicy descriptorPolicy, Field field) {
        Class<? extends Annotation> scoreAnnotationClass = extractScoreAnnotationClass(field);
        if (scoreAnnotationClass != null) {
            MemberAccessor memberAccessor = new FieldMemberAccessor(field);
            registerScoreAccessor(memberAccessor);
        }
    }

    private void processScoreAnnotation(DescriptorPolicy descriptorPolicy, Method method) {
        Class<? extends Annotation> scoreAnnotationClass = extractScoreAnnotationClass(method);
        if (scoreAnnotationClass != null) {
            ReflectionHelper.assertGetterMethod(method, scoreAnnotationClass);
            MemberAccessor memberAccessor = new BeanPropertyMemberAccessor(method);
            registerScoreAccessor(memberAccessor);
        }
    }

    private Class<? extends Annotation> extractEntityPropertyAnnotationClass(AnnotatedElement member) {
        return extractAnnotationClass(member, PlanningEntityProperty.class, PlanningEntityCollectionProperty.class);
    }

    private Class<? extends Annotation> extractFactPropertyAnnotationClass(AnnotatedElement member) {
        return extractAnnotationClass(member, ProblemFactProperty.class, ProblemFactCollectionProperty.class);
    }

    private Class<? extends Annotation> extractScoreAnnotationClass(AnnotatedElement member) {
        return extractAnnotationClass(member, PlanningScore.class);
    }

    private Class<? extends Annotation> extractAnnotationClass(AnnotatedElement member, Class<? extends Annotation>... annotations) {
        Class<? extends Annotation> annotationClass = null;
        for (Class<? extends Annotation> detectedAnnotationClass : Arrays.asList(annotations)) {
            if (member.isAnnotationPresent(detectedAnnotationClass)) {
                if (annotationClass != null) {
                    throw new IllegalStateException("The solutionClass (" + solutionClass
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

    private void registerPropertyAccessor(Class<? extends Annotation> entityPropertyAnnotationClass,
                                          MemberAccessor memberAccessor) {
        registerPropertyAccessor(entityPropertyAnnotationClass, memberAccessor, entityPropertyAccessorMap,
                entityCollectionPropertyAccessorMap, PlanningEntityProperty.class,
                PlanningEntityCollectionProperty.class);
    }

    private void registerFactPropertyAccessor(Class<? extends Annotation> factPropertyAnnotationClass,
                                                MemberAccessor memberAccessor) {
        registerPropertyAccessor(factPropertyAnnotationClass, memberAccessor, factPropertyAccessorMap,
                factCollectionPropertyAccessorMap, ProblemFactProperty.class, ProblemFactCollectionProperty.class);
    }

    private void registerScoreAccessor(MemberAccessor memberAccessor) {
        if (scoreAccessor != null) {
            throw new IllegalStateException("The solutionClass (" + solutionClass
                    + ") has a " + PlanningScore.class.getSimpleName()
                    + " annotated member (" + memberAccessor
                    + ") that is duplicated by another member (" + scoreAccessor + ").\n"
                    + "  Verify that the annotation is not defined on both the field and its getter.");
        } else if (!Score.class.isAssignableFrom(memberAccessor.getType())) {
            throw new IllegalStateException("The solutionClass (" + solutionClass
                    + ") has a " + PlanningScore.class.getSimpleName()
                    + " annotated member (" + memberAccessor + ") that does not return a subtype of Score.");
        } else if (!memberAccessor.supportSetter()) {
            throw new IllegalStateException("The solutionClass (" + solutionClass
                    + ") has a " + PlanningScore.class.getSimpleName()
                    + " annotated member (" + memberAccessor + ") that has no equivalent setter.");
        }
        scoreAccessor = memberAccessor;
    }

    private void registerPropertyAccessor(Class<? extends Annotation> annotationClass,
                                          MemberAccessor memberAccessor,
                                          Map<String, MemberAccessor> propertyAccessorMap,
                                          Map<String, MemberAccessor> collectionPropertyAccessorMap,
                                          Class<? extends Annotation> propertyAnnotationClass,
                                          Class<? extends Annotation> collectionPropertyAnnotationClass) {
        String memberName = memberAccessor.getName();
        if (propertyAccessorMap.containsKey(memberName)
                || collectionPropertyAccessorMap.containsKey(memberName)) {
            MemberAccessor duplicate = propertyAccessorMap.get(memberName);
            if (duplicate == null) {
                duplicate = collectionPropertyAccessorMap.get(memberName);
            }
            throw new IllegalStateException("The solutionClass (" + solutionClass
                    + ") has a " + propertyAnnotationClass.getSimpleName()
                    + " annotated member (" + memberAccessor
                    + ") that is duplicated by another member (" + duplicate + ").\n"
                    + "  Verify that the annotation is not defined on both the field and its getter.");
        }
        if (annotationClass.equals(propertyAnnotationClass)) {
            propertyAccessorMap.put(memberName, memberAccessor);
        } else if (annotationClass.equals(collectionPropertyAnnotationClass)) {
            if (!Collection.class.isAssignableFrom(memberAccessor.getType())) {
                throw new IllegalStateException("The solutionClass (" + solutionClass
                        + ") has a " + collectionPropertyAnnotationClass.getSimpleName()
                        + " annotated member (" + memberName + ") that does not return a "
                        + Collection.class.getSimpleName() + ".");
            }
            collectionPropertyAccessorMap.put(memberName, memberAccessor);
        }
    }

    public void afterAnnotationsProcessed(DescriptorPolicy descriptorPolicy) {
        for (EntityDescriptor<Solution_> entityDescriptor : entityDescriptorMap.values()) {
            entityDescriptor.linkInheritedEntityDescriptors(descriptorPolicy);
        }
        for (EntityDescriptor<Solution_> entityDescriptor : entityDescriptorMap.values()) {
            entityDescriptor.linkShadowSources(descriptorPolicy);
        }
        determineGlobalShadowOrder();
        if (logger.isTraceEnabled()) {
            logger.trace("    Model annotations parsed for Solution {}:", solutionClass.getSimpleName());
            for (Map.Entry<Class<?>, EntityDescriptor<Solution_>> entry : entityDescriptorMap.entrySet()) {
                EntityDescriptor<Solution_> entityDescriptor = entry.getValue();
                logger.trace("        Entity {}:", entityDescriptor.getEntityClass().getSimpleName());
                for (VariableDescriptor<Solution_> variableDescriptor : entityDescriptor.getDeclaredVariableDescriptors()) {
                    logger.trace("            Variable {} ({})", variableDescriptor.getVariableName(),
                            variableDescriptor instanceof GenuineVariableDescriptor ? "genuine" : "shadow");
                }
            }
        }
    }

    private void determineGlobalShadowOrder() {
        // Topological sorting with Kahn's algorithm
        List<Pair<ShadowVariableDescriptor, Integer>> pairList = new ArrayList<>();
        Map<ShadowVariableDescriptor, Pair<ShadowVariableDescriptor, Integer>> shadowToPairMap
                = new HashMap<>();
        for (EntityDescriptor<Solution_> entityDescriptor : entityDescriptorMap.values()) {
            for (ShadowVariableDescriptor<Solution_> shadow : entityDescriptor.getDeclaredShadowVariableDescriptors()) {
                int sourceSize = shadow.getSourceVariableDescriptorList().size();
                Pair<ShadowVariableDescriptor, Integer> pair = MutablePair.of(shadow, sourceSize);
                pairList.add(pair);
                shadowToPairMap.put(shadow, pair);
            }
        }
        for (EntityDescriptor<Solution_> entityDescriptor : entityDescriptorMap.values()) {
            for (GenuineVariableDescriptor<Solution_> genuine : entityDescriptor.getDeclaredGenuineVariableDescriptors()) {
                for (ShadowVariableDescriptor<Solution_> sink : genuine.getSinkVariableDescriptorList()) {
                    Pair<ShadowVariableDescriptor, Integer> sinkPair = shadowToPairMap.get(sink);
                    sinkPair.setValue(sinkPair.getValue() - 1);
                }
            }
        }
        int globalShadowOrder = 0;
        while (!pairList.isEmpty()) {
            Collections.sort(pairList, (a, b) -> Integer.compare(a.getValue(), b.getValue()));
            Pair<ShadowVariableDescriptor, Integer> pair = pairList.remove(0);
            ShadowVariableDescriptor<Solution_> shadow = pair.getKey();
            if (pair.getValue() != 0) {
                if (pair.getValue() < 0) {
                    throw new IllegalStateException("Impossible state because the shadowVariable ("
                            + shadow.getSimpleEntityAndVariableName()
                            + ") can not be used more as a sink than it has sources.");
                }
                throw new IllegalStateException("There is a cyclic shadow variable path"
                        + " that involves the shadowVariable (" + shadow.getSimpleEntityAndVariableName()
                        + ") because it must be later than its sources (" + shadow.getSourceVariableDescriptorList()
                        + ") and also earlier than its sinks (" + shadow.getSinkVariableDescriptorList() + ").");
            }
            for (ShadowVariableDescriptor<Solution_> sink : shadow.getSinkVariableDescriptorList()) {
                Pair<ShadowVariableDescriptor, Integer> sinkPair = shadowToPairMap.get(sink);
                sinkPair.setValue(sinkPair.getValue() - 1);
            }
            shadow.setGlobalShadowOrder(globalShadowOrder);
            globalShadowOrder++;
        }
    }

    public Class<Solution_> getSolutionClass() {
        return solutionClass;
    }

    /**
     * @return the {@link Class} of {@link PlanningScore}
     */
    public Class<? extends Score> extractScoreClass() {
        return (Class<? extends Score>) scoreAccessor.getType();
    }

    public SolutionCloner<Solution_> getSolutionCloner() {
        return solutionCloner;
    }

    public Map<String, MemberAccessor> getEntityPropertyAccessorMap() {
        return entityPropertyAccessorMap;
    }

    public Map<String, MemberAccessor> getEntityCollectionPropertyAccessorMap() {
        return entityCollectionPropertyAccessorMap;
    }

    public Map<String, MemberAccessor> getFactPropertyAccessorMap() {
        return factPropertyAccessorMap;
    }

    public Map<String, MemberAccessor> getFactCollectionPropertyAccessorMap() {
        return factCollectionPropertyAccessorMap;
    }

    // ************************************************************************
    // Model methods
    // ************************************************************************

    public Set<Class<?>> getEntityClassSet() {
        return entityDescriptorMap.keySet();
    }

    public Collection<EntityDescriptor<Solution_>> getEntityDescriptors() {
        return entityDescriptorMap.values();
    }

    public Collection<EntityDescriptor<Solution_>> getGenuineEntityDescriptors() {
        List<EntityDescriptor<Solution_>> genuineEntityDescriptorList = new ArrayList<>(
                entityDescriptorMap.size());
        for (EntityDescriptor<Solution_> entityDescriptor : entityDescriptorMap.values()) {
            if (entityDescriptor.hasAnyDeclaredGenuineVariableDescriptor()) {
                genuineEntityDescriptorList.add(entityDescriptor);
            }
        }
        return genuineEntityDescriptorList;
    }

    public boolean hasEntityDescriptorStrict(Class<?> entityClass) {
        return entityDescriptorMap.containsKey(entityClass);
    }

    public EntityDescriptor<Solution_> getEntityDescriptorStrict(Class<?> entityClass) {
        return entityDescriptorMap.get(entityClass);
    }

    private boolean hasEntityAnnotation() {
        return !(this.entityCollectionPropertyAccessorMap.isEmpty() && this.entityPropertyAccessorMap.isEmpty());
    }

    public boolean hasEntityDescriptor(Class<?> entitySubclass) {
        EntityDescriptor<Solution_> entityDescriptor = findEntityDescriptor(entitySubclass);
        return entityDescriptor != null;
    }

    public EntityDescriptor<Solution_> findEntityDescriptorOrFail(Class<?> entitySubclass) {
        EntityDescriptor<Solution_> entityDescriptor = findEntityDescriptor(entitySubclass);
        if (entityDescriptor == null) {
            throw new IllegalArgumentException("A planning entity is an instance of an entitySubclass ("
                    + entitySubclass + ") that is not configured as a planning entity.\n" +
                    "If that class (" + entitySubclass.getSimpleName()
                    + ") (or superclass thereof) is not a entityClass (" + getEntityClassSet()
                    + "), check your Solution implementation's annotated methods.\n" +
                    "If it is, check your solver configuration.");
        }
        return entityDescriptor;
    }

    public EntityDescriptor<Solution_> findEntityDescriptor(Class<?> entitySubclass) {
        EntityDescriptor<Solution_> entityDescriptor = lowestEntityDescriptorCache.get(entitySubclass);
        if (entityDescriptor == null) {
            // Reverse order to find the nearest ancestor
            for (Class<?> entityClass : reversedEntityClassList) {
                if (entityClass.isAssignableFrom(entitySubclass)) {
                    entityDescriptor = entityDescriptorMap.get(entityClass);
                    lowestEntityDescriptorCache.put(entitySubclass, entityDescriptor);
                    break;
                }
            }
        }
        return entityDescriptor;
    }

    public GenuineVariableDescriptor<Solution_> findGenuineVariableDescriptor(Object entity, String variableName) {
        EntityDescriptor<Solution_> entityDescriptor = findEntityDescriptorOrFail(entity.getClass());
        return entityDescriptor.getGenuineVariableDescriptor(variableName);
    }

    public GenuineVariableDescriptor<Solution_> findGenuineVariableDescriptorOrFail(Object entity, String variableName) {
        EntityDescriptor<Solution_> entityDescriptor = findEntityDescriptorOrFail(entity.getClass());
        GenuineVariableDescriptor<Solution_> variableDescriptor = entityDescriptor.getGenuineVariableDescriptor(variableName);
        if (variableDescriptor == null) {
            throw new IllegalArgumentException(entityDescriptor.buildInvalidVariableNameExceptionMessage(variableName));
        }
        return variableDescriptor;
    }

    public VariableDescriptor<Solution_> findVariableDescriptor(Object entity, String variableName) {
        EntityDescriptor<Solution_> entityDescriptor = findEntityDescriptorOrFail(entity.getClass());
        return entityDescriptor.getVariableDescriptor(variableName);
    }

    public VariableDescriptor<Solution_> findVariableDescriptorOrFail(Object entity, String variableName) {
        EntityDescriptor<Solution_> entityDescriptor = findEntityDescriptorOrFail(entity.getClass());
        VariableDescriptor<Solution_> variableDescriptor = entityDescriptor.getVariableDescriptor(variableName);
        if (variableDescriptor == null) {
            throw new IllegalArgumentException(entityDescriptor.buildInvalidVariableNameExceptionMessage(variableName));
        }
        return variableDescriptor;
    }

    // ************************************************************************
    // Extraction methods
    // ************************************************************************

    public Collection<Object> getAllFacts(Solution_ solution) {
        Collection<Object> facts = new ArrayList<>();
        // will add both entities and facts
        Arrays.asList(entityPropertyAccessorMap, factPropertyAccessorMap).forEach(map -> map.forEach((key, value) -> {
            Object object = extract(value, solution);
            if (object != null) {
                facts.add(object);
            }
        }));
        Arrays.asList(entityCollectionPropertyAccessorMap, factCollectionPropertyAccessorMap).forEach(map ->
                map.forEach((key, value) -> facts.addAll(extractCollection(value, solution))));
        return facts;
    }

    /**
     * @param solution never null
     * @return {@code >= 0}
     */
    public int getEntityCount(Solution_ solution) {
        int entityCount = 0;
        for (MemberAccessor entityMemberAccessor : entityPropertyAccessorMap.values()) {
            Object entity = extract(entityMemberAccessor, solution);
            if (entity != null) {
                entityCount++;
            }
        }
        for (MemberAccessor entityCollectionMemberAccessor : entityCollectionPropertyAccessorMap.values()) {
            Collection<Object> entityCollection = extractCollection(entityCollectionMemberAccessor, solution);
            entityCount += entityCollection.size();
        }
        return entityCount;
    }

    public List<Object> getEntityList(Solution_ solution) {
        List<Object> entityList = new ArrayList<>();
        for (MemberAccessor entityMemberAccessor : entityPropertyAccessorMap.values()) {
            Object entity = extract(entityMemberAccessor, solution);
            if (entity != null) {
                entityList.add(entity);
            }
        }
        for (MemberAccessor entityCollectionMemberAccessor : entityCollectionPropertyAccessorMap.values()) {
            Collection<Object> entityCollection = extractCollection(entityCollectionMemberAccessor, solution);
            entityList.addAll(entityCollection);
        }
        return entityList;
    }

    public List<Object> getEntityListByEntityClass(Solution_ solution, Class<?> entityClass) {
        List<Object> entityList = new ArrayList<>();
        for (MemberAccessor entityMemberAccessor : entityPropertyAccessorMap.values()) {
            if (entityMemberAccessor.getType().isAssignableFrom(entityClass)) {
                Object entity = extract(entityMemberAccessor, solution);
                if (entity != null && entityClass.isInstance(entity)) {
                    entityList.add(entity);
                }
            }
        }
        for (MemberAccessor entityCollectionMemberAccessor : entityCollectionPropertyAccessorMap.values()) {
            // TODO if (entityCollectionPropertyAccessor.getPropertyType().getElementType().isAssignableFrom(entityClass)) {
            Collection<Object> entityCollection = extractCollection(entityCollectionMemberAccessor, solution);
            for (Object entity : entityCollection) {
                if (entityClass.isInstance(entity)) {
                    entityList.add(entity);
                }
            }
        }
        return entityList;
    }

    /**
     * @param solution never null
     * @return {@code >= 0}
     */
    public long getGenuineVariableCount(Solution_ solution) {
        long variableCount = 0L;
        for (Iterator<Object> it = extractAllEntitiesIterator(solution); it.hasNext();) {
            Object entity = it.next();
            EntityDescriptor<Solution_> entityDescriptor = findEntityDescriptorOrFail(entity.getClass());
            variableCount += entityDescriptor.getGenuineVariableCount();
        }
        return variableCount;
    }

    /**
     * @param solution never null
     * @return Score of the given solution.
     */
    public Score getScore(Solution_ solution) {
        return (Score)scoreAccessor.executeGetter(solution);
    }

    /**
     * @param solution never null
     * @param score
     */
    public void setScore(Solution_ solution, Score score) {
        scoreAccessor.executeSetter(solution, score);
    }

    /**
     * @param solution never null
     * @return {@code >= 0}
     */
    public int getValueCount(Solution_ solution) {
        int valueCount = 0;
        // TODO FIXME for ValueRatioTabuSizeStrategy
        throw new UnsupportedOperationException(
                "getValueCount is not yet supported - this blocks ValueRatioTabuSizeStrategy");
        // return valueCount;
    }

    /**
     * Calculates an indication on how big this problem instance is.
     * This is intentionally very loosely defined for now.
     * @param solution never null
     * @return {@code >= 0}
     */
    public long getProblemScale(Solution_ solution) {
        long problemScale = 0L;
        for (Iterator<Object> it = extractAllEntitiesIterator(solution); it.hasNext();) {
            Object entity = it.next();
            EntityDescriptor<Solution_> entityDescriptor = findEntityDescriptorOrFail(entity.getClass());
            problemScale += entityDescriptor.getProblemScale(solution, entity);
        }
        return problemScale;
    }

    public int countUninitializedVariables(Solution_ solution) {
        int count = 0;
        for (Iterator<Object> it = extractAllEntitiesIterator(solution); it.hasNext();) {
            Object entity = it.next();
            EntityDescriptor<Solution_> entityDescriptor = findEntityDescriptorOrFail(entity.getClass());
            count += entityDescriptor.countUninitializedVariables(entity);
        }
        return count;
    }

    /**
     * @param scoreDirector never null
     * @param entity never null
     * @return true if the entity is initialized or immovable
     */
    public boolean isEntityInitializedOrImmovable(ScoreDirector scoreDirector, Object entity) {
        EntityDescriptor<Solution_> entityDescriptor = findEntityDescriptorOrFail(entity.getClass());
        return entityDescriptor.isInitialized(entity) || !entityDescriptor.isMovable(scoreDirector, entity);
    }

    public int countReinitializableVariables(ScoreDirector scoreDirector, Solution_ solution) {
        int count = 0;
        for (Iterator<Object> it = extractAllEntitiesIterator(solution); it.hasNext();) {
            Object entity = it.next();
            EntityDescriptor<Solution_> entityDescriptor = findEntityDescriptorOrFail(entity.getClass());
            count += entityDescriptor.countReinitializableVariables(scoreDirector, entity);
        }
        return count;
    }

    public Iterator<Object> extractAllEntitiesIterator(Solution_ solution) {
        List<Iterator<Object>> iteratorList = new ArrayList<>(
                entityPropertyAccessorMap.size() + entityCollectionPropertyAccessorMap.size());
        for (MemberAccessor entityMemberAccessor : entityPropertyAccessorMap.values()) {
            Object entity = extract(entityMemberAccessor, solution);
            if (entity != null) {
                iteratorList.add(Collections.singletonList(entity).iterator());
            }
        }
        for (MemberAccessor entityCollectionMemberAccessor : entityCollectionPropertyAccessorMap.values()) {
            Collection<Object> entityCollection = extractCollection(entityCollectionMemberAccessor, solution);
            iteratorList.add(entityCollection.iterator());
        }
        return Iterators.concat(iteratorList.iterator());
    }

    private Object extract(MemberAccessor memberAccessor, Solution_ solution) {
        return memberAccessor.executeGetter(solution);
    }

    private Collection<Object> extractCollection(MemberAccessor collectionMemberAccessor, Solution_ solution,
                                                 boolean isFact) {
        Collection<Object> entityCollection = (Collection<Object>) collectionMemberAccessor.executeGetter(solution);
        if (entityCollection == null) {
            String descr = isFact ? "factCollectionProperty" : "entityCollectionProperty";
            throw new IllegalArgumentException("The solutionClass (" + solutionClass
                    + ")'s " + descr + " ("
                    + collectionMemberAccessor.getName() + ") should never return null.");
        }
        return entityCollection;
    }

    private Collection<Object> extractCollection(MemberAccessor collectionMemberAccessor, Solution_ solution) {
        return extractCollection(collectionMemberAccessor, solution, false);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + solutionClass.getName() + ")";
    }

}
