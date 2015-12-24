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

package org.optaplanner.core.impl.domain.solution.descriptor;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;

import com.google.common.collect.Iterators;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.optaplanner.core.api.domain.solution.PlanningEntityCollectionProperty;
import org.optaplanner.core.api.domain.solution.PlanningEntityProperty;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.domain.solution.Solution;
import org.optaplanner.core.api.domain.solution.cloner.PlanningCloneable;
import org.optaplanner.core.api.domain.solution.cloner.SolutionCloner;
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.config.util.ConfigUtils;
import org.optaplanner.core.impl.domain.common.AlphabeticMemberComparator;
import org.optaplanner.core.impl.domain.common.accessor.FieldMemberAccessor;
import org.optaplanner.core.impl.domain.common.accessor.MemberAccessor;
import org.optaplanner.core.impl.domain.common.accessor.BeanPropertyMemberAccessor;
import org.optaplanner.core.impl.domain.common.ReflectionHelper;
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

public class SolutionDescriptor {

    public static SolutionDescriptor buildSolutionDescriptor(Class<? extends Solution> solutionClass,
            Class<?> ... entityClasses) {
        return buildSolutionDescriptor(solutionClass, Arrays.asList(entityClasses));
    }

    public static SolutionDescriptor buildSolutionDescriptor(Class<? extends Solution> solutionClass,
            List<Class<?>> entityClassList) {
        DescriptorPolicy descriptorPolicy = new DescriptorPolicy();
        SolutionDescriptor solutionDescriptor = new SolutionDescriptor(solutionClass);
        solutionDescriptor.processAnnotations(descriptorPolicy);
        for (Class<?> entityClass : sortEntityClassList(entityClassList)) {
            EntityDescriptor entityDescriptor = new EntityDescriptor(solutionDescriptor, entityClass);
            solutionDescriptor.addEntityDescriptor(entityDescriptor);
            entityDescriptor.processAnnotations(descriptorPolicy);
        }
        solutionDescriptor.afterAnnotationsProcessed(descriptorPolicy);
        return solutionDescriptor;
    }

    private static List<Class<?>> sortEntityClassList(List<Class<?>> entityClassList) {
        List<Class<?>> sortedEntityClassList = new ArrayList<Class<?>>(entityClassList.size());
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

    private final Class<? extends Solution> solutionClass;
    private SolutionCloner solutionCloner;

    private final Map<String, MemberAccessor> entityPropertyAccessorMap;
    private final Map<String, MemberAccessor> entityCollectionPropertyAccessorMap;

    private final Map<Class<?>, EntityDescriptor> entityDescriptorMap;
    private final List<Class<?>> reversedEntityClassList;
    private final Map<Class<?>, EntityDescriptor> lowestEntityDescriptorCache;

    public SolutionDescriptor(Class<? extends Solution> solutionClass) {
        this.solutionClass = solutionClass;
        entityPropertyAccessorMap = new LinkedHashMap<String, MemberAccessor>();
        entityCollectionPropertyAccessorMap = new LinkedHashMap<String, MemberAccessor>();
        entityDescriptorMap = new LinkedHashMap<Class<?>, EntityDescriptor>();
        reversedEntityClassList = new ArrayList<Class<?>>();
        lowestEntityDescriptorCache = new HashMap<Class<?>, EntityDescriptor>();
    }

    public void addEntityDescriptor(EntityDescriptor entityDescriptor) {
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

    public void processAnnotations(DescriptorPolicy descriptorPolicy) {
        processSolutionAnnotations(descriptorPolicy);
        processValueRangeProviderAnnotations(descriptorPolicy);
        processEntityPropertyAnnotations(descriptorPolicy);
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
                solutionCloner = new PlanningCloneableSolutionCloner();
            } else {
                solutionCloner = new FieldAccessingSolutionCloner(this);
            }
        }
    }

    private void processValueRangeProviderAnnotations(DescriptorPolicy descriptorPolicy) {
        // TODO This does not support annotations on inherited fields
        List<Field> fieldList = Arrays.asList(solutionClass.getDeclaredFields());
        Collections.sort(fieldList, new AlphabeticMemberComparator());
        for (Field field : fieldList) {
            if (field.isAnnotationPresent(ValueRangeProvider.class)) {
                MemberAccessor memberAccessor = new FieldMemberAccessor(field);
                descriptorPolicy.addFromSolutionValueRangeProvider(memberAccessor);
            }
        }
        // TODO This does not support annotations on inherited members
        List<Method> methodList = Arrays.asList(solutionClass.getDeclaredMethods());
        Collections.sort(methodList, new AlphabeticMemberComparator());
        for (Method method : methodList) {
            if (method.isAnnotationPresent(ValueRangeProvider.class)) {
                ReflectionHelper.assertReadMethod(method, ValueRangeProvider.class);
                MemberAccessor memberAccessor = new MethodMemberAccessor(method);
                descriptorPolicy.addFromSolutionValueRangeProvider(memberAccessor);
            }
        }
    }

    private void processEntityPropertyAnnotations(DescriptorPolicy descriptorPolicy) {
        boolean noEntityPropertyAnnotation = true;
        // TODO This does not support annotations on inherited fields
        List<Field> fieldList = Arrays.asList(solutionClass.getDeclaredFields());
        Collections.sort(fieldList, new AlphabeticMemberComparator());
        for (Field field : fieldList) {
            Class<? extends Annotation> entityPropertyAnnotationClass = extractEntityPropertyAnnotationClass(field);
            if (entityPropertyAnnotationClass != null) {
                noEntityPropertyAnnotation = false;
                MemberAccessor memberAccessor = new FieldMemberAccessor(field);
                registerEntityPropertyAccessor(entityPropertyAnnotationClass, memberAccessor);
            }
        }
        // TODO This does not support annotations on inherited methods
        List<Method> methodList = Arrays.asList(solutionClass.getDeclaredMethods());
        Collections.sort(methodList, new AlphabeticMemberComparator());
        for (Method method : methodList) {
            Class<? extends Annotation> entityPropertyAnnotationClass = extractEntityPropertyAnnotationClass(method);
            if (entityPropertyAnnotationClass != null) {
                noEntityPropertyAnnotation = false;
                ReflectionHelper.assertGetterMethod(method, entityPropertyAnnotationClass);
                MemberAccessor memberAccessor = new BeanPropertyMemberAccessor(method);
                registerEntityPropertyAccessor(entityPropertyAnnotationClass, memberAccessor);
            }
        }
        if (noEntityPropertyAnnotation) {
            throw new IllegalStateException("The solutionClass (" + solutionClass
                    + ") should have at least 1 getter with a PlanningEntityCollectionProperty or PlanningEntityProperty"
                    + " annotation.");
        }
    }

    private Class<? extends Annotation> extractEntityPropertyAnnotationClass(AnnotatedElement member) {
        Class<? extends Annotation> annotationClass = null;
        for (Class<? extends Annotation> detectedAnnotationClass : Arrays.asList(PlanningEntityProperty.class, PlanningEntityCollectionProperty.class)) {
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

    private void registerEntityPropertyAccessor(Class<? extends Annotation> entityPropertyAnnotationClass,
            MemberAccessor memberAccessor) {
        String memberName = memberAccessor.getName();
        if (entityPropertyAccessorMap.containsKey(memberName)
                || entityCollectionPropertyAccessorMap.containsKey(memberName)) {
            MemberAccessor duplicate = entityPropertyAccessorMap.get(memberName);
            if (duplicate == null) {
                duplicate = entityCollectionPropertyAccessorMap.get(memberName);
            }
            throw new IllegalStateException("The solutionClass (" + solutionClass
                    + ") has a " + entityPropertyAnnotationClass.getSimpleName()
                    + " annotated member (" + memberAccessor
                    + ") that is duplicated by another member (" + duplicate + ").\n"
                    + "  Verify that the annotation is not defined on both the field and its getter.");
        }
        if (entityPropertyAnnotationClass.equals(PlanningEntityProperty.class)) {
            entityPropertyAccessorMap.put(memberName, memberAccessor);
        } else if (entityPropertyAnnotationClass.equals(PlanningEntityCollectionProperty.class)) {
            if (!Collection.class.isAssignableFrom(memberAccessor.getType())) {
                throw new IllegalStateException("The solutionClass (" + solutionClass
                        + ") has a " + PlanningEntityCollectionProperty.class.getSimpleName()
                        + " annotated member (" + memberName + ") that does not return a "
                        + Collection.class.getSimpleName() + ".");
            }
            entityCollectionPropertyAccessorMap.put(memberName, memberAccessor);
        }
    }

    public void afterAnnotationsProcessed(DescriptorPolicy descriptorPolicy) {
        for (EntityDescriptor entityDescriptor : entityDescriptorMap.values()) {
            entityDescriptor.linkInheritedEntityDescriptors(descriptorPolicy);
        }
        for (EntityDescriptor entityDescriptor : entityDescriptorMap.values()) {
            entityDescriptor.linkShadowSources(descriptorPolicy);
        }
        determineGlobalShadowOrder();
        if (logger.isTraceEnabled()) {
            logger.trace("    Model annotations parsed for Solution {}:", solutionClass.getSimpleName());
            for (Map.Entry<Class<?>, EntityDescriptor> entry : entityDescriptorMap.entrySet()) {
                EntityDescriptor entityDescriptor = entry.getValue();
                logger.trace("        Entity {}:", entityDescriptor.getEntityClass().getSimpleName());
                for (VariableDescriptor variableDescriptor : entityDescriptor.getDeclaredVariableDescriptors()) {
                    logger.trace("            Variable {} ({})", variableDescriptor.getVariableName(),
                            variableDescriptor instanceof GenuineVariableDescriptor ? "genuine" : "shadow");
                }
            }
        }
    }

    private void determineGlobalShadowOrder() {
        // Topological sorting with Kahn's algorithm
        Comparator<Pair<ShadowVariableDescriptor, Integer>> comparator = new Comparator<Pair<ShadowVariableDescriptor, Integer>>() {
            @Override
            public int compare(Pair<ShadowVariableDescriptor, Integer> a, Pair<ShadowVariableDescriptor, Integer> b) {
                int aSourceSize = a.getValue();
                int bSourceSize = b.getValue();
                // TODO replace by Integer.compare() when Java 7 is minimum
                if (aSourceSize > bSourceSize) {
                    return 1;
                } else if (aSourceSize < bSourceSize) {
                    return -1;
                } else {
                    return 0;
                }
            }
        };
        List<Pair<ShadowVariableDescriptor, Integer>> pairList = new ArrayList<Pair<ShadowVariableDescriptor, Integer>>();
        Map<ShadowVariableDescriptor, Pair<ShadowVariableDescriptor, Integer>> shadowToPairMap
                = new HashMap<ShadowVariableDescriptor, Pair<ShadowVariableDescriptor, Integer>>();
        for (EntityDescriptor entityDescriptor : entityDescriptorMap.values()) {
            for (ShadowVariableDescriptor shadow : entityDescriptor.getDeclaredShadowVariableDescriptors()) {
                int sourceSize = shadow.getSourceVariableDescriptorList().size();
                Pair<ShadowVariableDescriptor, Integer> pair = MutablePair.of(shadow, sourceSize);
                pairList.add(pair);
                shadowToPairMap.put(shadow, pair);
            }
        }
        for (EntityDescriptor entityDescriptor : entityDescriptorMap.values()) {
            for (GenuineVariableDescriptor genuine : entityDescriptor.getDeclaredGenuineVariableDescriptors()) {
                for (ShadowVariableDescriptor sink : genuine.getSinkVariableDescriptorList()) {
                    Pair<ShadowVariableDescriptor, Integer> sinkPair = shadowToPairMap.get(sink);
                    sinkPair.setValue(sinkPair.getValue() - 1);
                }
            }
        }
        int globalShadowOrder = 0;
        while (!pairList.isEmpty()) {
            Collections.sort(pairList, comparator);
            Pair<ShadowVariableDescriptor, Integer> pair = pairList.remove(0);
            ShadowVariableDescriptor shadow = pair.getKey();
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
            for (ShadowVariableDescriptor sink : shadow.getSinkVariableDescriptorList()) {
                Pair<ShadowVariableDescriptor, Integer> sinkPair = shadowToPairMap.get(sink);
                sinkPair.setValue(sinkPair.getValue() - 1);
            }
            shadow.setGlobalShadowOrder(globalShadowOrder);
            globalShadowOrder++;
        }
    }

    public Class<? extends Solution> getSolutionClass() {
        return solutionClass;
    }

    /**
     * @return the {@link Class} of {@link Solution#getScore()}
     */
    public Class<? extends Score> extractScoreClass() {
        try {
            return (Class<? extends Score>) solutionClass.getMethod("getScore").getReturnType();
        } catch (NoSuchMethodException e) {
            throw new IllegalStateException("Impossible situation: a solutionClass (" + solutionClass
                    + ") which implements the interface Solution, lacks its getScore() method.", e);
        }
    }

    public SolutionCloner getSolutionCloner() {
        return solutionCloner;
    }

    public Map<String, MemberAccessor> getEntityPropertyAccessorMap() {
        return entityPropertyAccessorMap;
    }

    public Map<String, MemberAccessor> getEntityCollectionPropertyAccessorMap() {
        return entityCollectionPropertyAccessorMap;
    }

    // ************************************************************************
    // Model methods
    // ************************************************************************

    public Set<Class<?>> getEntityClassSet() {
        return entityDescriptorMap.keySet();
    }

    public Collection<EntityDescriptor> getEntityDescriptors() {
        return entityDescriptorMap.values();
    }

    public Collection<EntityDescriptor> getGenuineEntityDescriptors() {
        List<EntityDescriptor> genuineEntityDescriptorList = new ArrayList<EntityDescriptor>(
                entityDescriptorMap.size());
        for (EntityDescriptor entityDescriptor : entityDescriptorMap.values()) {
            if (entityDescriptor.hasAnyDeclaredGenuineVariableDescriptor()) {
                genuineEntityDescriptorList.add(entityDescriptor);
            }
        }
        return genuineEntityDescriptorList;
    }

    public boolean hasEntityDescriptorStrict(Class<?> entityClass) {
        return entityDescriptorMap.containsKey(entityClass);
    }

    public EntityDescriptor getEntityDescriptorStrict(Class<?> entityClass) {
        return entityDescriptorMap.get(entityClass);
    }

    public boolean hasEntityDescriptor(Class<?> entitySubclass) {
        EntityDescriptor entityDescriptor = findEntityDescriptor(entitySubclass);
        return entityDescriptor != null;
    }

    public EntityDescriptor findEntityDescriptorOrFail(Class<?> entitySubclass) {
        EntityDescriptor entityDescriptor = findEntityDescriptor(entitySubclass);
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

    public EntityDescriptor findEntityDescriptor(Class<?> entitySubclass) {
        EntityDescriptor entityDescriptor = lowestEntityDescriptorCache.get(entitySubclass);
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

    public GenuineVariableDescriptor findGenuineVariableDescriptor(Object entity, String variableName) {
        EntityDescriptor entityDescriptor = findEntityDescriptorOrFail(entity.getClass());
        return entityDescriptor.getGenuineVariableDescriptor(variableName);
    }

    public GenuineVariableDescriptor findGenuineVariableDescriptorOrFail(Object entity, String variableName) {
        EntityDescriptor entityDescriptor = findEntityDescriptorOrFail(entity.getClass());
        GenuineVariableDescriptor variableDescriptor = entityDescriptor.getGenuineVariableDescriptor(variableName);
        if (variableDescriptor == null) {
            throw new IllegalArgumentException(entityDescriptor.buildInvalidVariableNameExceptionMessage(variableName));
        }
        return variableDescriptor;
    }

    public VariableDescriptor findVariableDescriptor(Object entity, String variableName) {
        EntityDescriptor entityDescriptor = findEntityDescriptorOrFail(entity.getClass());
        return entityDescriptor.getVariableDescriptor(variableName);
    }

    public VariableDescriptor findVariableDescriptorOrFail(Object entity, String variableName) {
        EntityDescriptor entityDescriptor = findEntityDescriptorOrFail(entity.getClass());
        VariableDescriptor variableDescriptor = entityDescriptor.getVariableDescriptor(variableName);
        if (variableDescriptor == null) {
            throw new IllegalArgumentException(entityDescriptor.buildInvalidVariableNameExceptionMessage(variableName));
        }
        return variableDescriptor;
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
        for (MemberAccessor entityMemberAccessor : entityPropertyAccessorMap.values()) {
            Object entity = extractEntity(entityMemberAccessor, solution);
            if (entity != null) {
                facts.add(entity);
            }
        }
        for (MemberAccessor entityCollectionMemberAccessor : entityCollectionPropertyAccessorMap.values()) {
            Collection<Object> entityCollection = extractEntityCollection(entityCollectionMemberAccessor, solution);
            facts.addAll(entityCollection);
        }
        return facts;
    }

    /**
     * @param solution never null
     * @return {@code >= 0}
     */
    public int getEntityCount(Solution solution) {
        int entityCount = 0;
        for (MemberAccessor entityMemberAccessor : entityPropertyAccessorMap.values()) {
            Object entity = extractEntity(entityMemberAccessor, solution);
            if (entity != null) {
                entityCount++;
            }
        }
        for (MemberAccessor entityCollectionMemberAccessor : entityCollectionPropertyAccessorMap.values()) {
            Collection<Object> entityCollection = extractEntityCollection(entityCollectionMemberAccessor, solution);
            entityCount += entityCollection.size();
        }
        return entityCount;
    }

    public List<Object> getEntityList(Solution solution) {
        List<Object> entityList = new ArrayList<Object>();
        for (MemberAccessor entityMemberAccessor : entityPropertyAccessorMap.values()) {
            Object entity = extractEntity(entityMemberAccessor, solution);
            if (entity != null) {
                entityList.add(entity);
            }
        }
        for (MemberAccessor entityCollectionMemberAccessor : entityCollectionPropertyAccessorMap.values()) {
            Collection<Object> entityCollection = extractEntityCollection(entityCollectionMemberAccessor, solution);
            entityList.addAll(entityCollection);
        }
        return entityList;
    }

    public List<Object> getEntityListByEntityClass(Solution solution, Class<?> entityClass) {
        List<Object> entityList = new ArrayList<Object>();
        for (MemberAccessor entityMemberAccessor : entityPropertyAccessorMap.values()) {
            if (entityMemberAccessor.getType().isAssignableFrom(entityClass)) {
                Object entity = extractEntity(entityMemberAccessor, solution);
                if (entity != null && entityClass.isInstance(entity)) {
                    entityList.add(entity);
                }
            }
        }
        for (MemberAccessor entityCollectionMemberAccessor : entityCollectionPropertyAccessorMap.values()) {
            // TODO if (entityCollectionPropertyAccessor.getPropertyType().getElementType().isAssignableFrom(entityClass)) {
            Collection<Object> entityCollection = extractEntityCollection(entityCollectionMemberAccessor, solution);
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
    public long getGenuineVariableCount(Solution solution) {
        long variableCount = 0L;
        for (Iterator<Object> it = extractAllEntitiesIterator(solution); it.hasNext();) {
            Object entity = it.next();
            EntityDescriptor entityDescriptor = findEntityDescriptorOrFail(entity.getClass());
            variableCount += entityDescriptor.getGenuineVariableCount();
        }
        return variableCount;
    }

    /**
     * @param solution never null
     * @return {@code >= 0}
     */
    public int getValueCount(Solution solution) {
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
    public long getProblemScale(Solution solution) {
        long problemScale = 0L;
        for (Iterator<Object> it = extractAllEntitiesIterator(solution); it.hasNext();) {
            Object entity = it.next();
            EntityDescriptor entityDescriptor = findEntityDescriptorOrFail(entity.getClass());
            problemScale += entityDescriptor.getProblemScale(solution, entity);
        }
        return problemScale;
    }

    public int countUninitializedVariables(Solution solution) {
        int count = 0;
        for (Iterator<Object> it = extractAllEntitiesIterator(solution); it.hasNext();) {
            Object entity = it.next();
            EntityDescriptor entityDescriptor = findEntityDescriptorOrFail(entity.getClass());
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
        EntityDescriptor entityDescriptor = findEntityDescriptorOrFail(entity.getClass());
        return entityDescriptor.isInitialized(entity) || !entityDescriptor.isMovable(scoreDirector, entity);
    }

    public int countReinitializableVariables(ScoreDirector scoreDirector, Solution solution) {
        int count = 0;
        for (Iterator<Object> it = extractAllEntitiesIterator(solution); it.hasNext();) {
            Object entity = it.next();
            EntityDescriptor entityDescriptor = findEntityDescriptorOrFail(entity.getClass());
            count += entityDescriptor.countReinitializableVariables(scoreDirector, entity);
        }
        return count;
    }

    public Iterator<Object> extractAllEntitiesIterator(Solution solution) {
        List<Iterator<Object>> iteratorList = new ArrayList<Iterator<Object>>(
                entityPropertyAccessorMap.size() + entityCollectionPropertyAccessorMap.size());
        for (MemberAccessor entityMemberAccessor : entityPropertyAccessorMap.values()) {
            Object entity = extractEntity(entityMemberAccessor, solution);
            if (entity != null) {
                iteratorList.add(Collections.singletonList(entity).iterator());
            }
        }
        for (MemberAccessor entityCollectionMemberAccessor : entityCollectionPropertyAccessorMap.values()) {
            Collection<Object> entityCollection = extractEntityCollection(entityCollectionMemberAccessor, solution);
            iteratorList.add(entityCollection.iterator());
        }
        return Iterators.concat(iteratorList.iterator());
    }

    private Object extractEntity(MemberAccessor entityMemberAccessor, Solution solution) {
        return entityMemberAccessor.executeGetter(solution);
    }

    private Collection<Object> extractEntityCollection(
            MemberAccessor entityCollectionMemberAccessor, Solution solution) {
        Collection<Object> entityCollection = (Collection<Object>) entityCollectionMemberAccessor.executeGetter(solution);
        if (entityCollection == null) {
            throw new IllegalArgumentException("The solutionClass (" + solutionClass
                    + ")'s entityCollectionProperty ("
                    + entityCollectionMemberAccessor.getName() + ") should never return null.");
        }
        return entityCollection;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + solutionClass.getName() + ")";
    }

}
