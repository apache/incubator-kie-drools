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

package org.optaplanner.core.impl.domain.solution.descriptor;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.optaplanner.core.api.domain.solution.PlanningEntityCollectionProperty;
import org.optaplanner.core.api.domain.solution.PlanningEntityProperty;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.domain.solution.Solution;
import org.optaplanner.core.api.domain.solution.cloner.PlanningCloneable;
import org.optaplanner.core.api.domain.solution.cloner.SolutionCloner;
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;
import org.optaplanner.core.config.util.ConfigUtils;
import org.optaplanner.core.impl.domain.common.PropertyAccessor;
import org.optaplanner.core.impl.domain.common.ReflectionPropertyAccessor;
import org.optaplanner.core.impl.domain.entity.descriptor.EntityDescriptor;
import org.optaplanner.core.impl.domain.policy.DescriptorPolicy;
import org.optaplanner.core.impl.domain.solution.cloner.FieldAccessingSolutionCloner;
import org.optaplanner.core.impl.domain.solution.cloner.PlanningCloneableSolutionCloner;
import org.optaplanner.core.impl.domain.variable.descriptor.GenuineVariableDescriptor;
import org.optaplanner.core.impl.domain.variable.descriptor.VariableDescriptor;
import org.optaplanner.core.impl.domain.variable.listener.VariableListener;
import org.optaplanner.core.impl.domain.variable.listener.VariableListenerSupport;
import org.optaplanner.core.impl.score.director.ScoreDirector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SolutionDescriptor {

    protected final transient Logger logger = LoggerFactory.getLogger(getClass());

    private final Class<? extends Solution> solutionClass;
    private final BeanInfo solutionBeanInfo;
    private SolutionCloner solutionCloner;
    
    private final Map<String, PropertyAccessor> propertyAccessorMap;
    private final Map<String, PropertyAccessor> entityPropertyAccessorMap;
    private final Map<String, PropertyAccessor> entityCollectionPropertyAccessorMap;

    private final Map<Class<?>, EntityDescriptor> entityDescriptorMap;
    private final List<Class<?>> reversedEntityClassList;
    private final Map<Class<?>, EntityDescriptor> lowestEntityDescriptorCache;

    public SolutionDescriptor(Class<? extends Solution> solutionClass) {
        this.solutionClass = solutionClass;
        try {
            solutionBeanInfo = Introspector.getBeanInfo(solutionClass);
        } catch (IntrospectionException e) {
            throw new IllegalStateException("The solutionClass (" + solutionClass + ") is not a valid java bean.", e);
        }
        int mapSize = solutionBeanInfo.getPropertyDescriptors().length;
        propertyAccessorMap = new LinkedHashMap<String, PropertyAccessor>(mapSize);
        entityPropertyAccessorMap = new LinkedHashMap<String, PropertyAccessor>(mapSize);
        entityCollectionPropertyAccessorMap = new LinkedHashMap<String, PropertyAccessor>(mapSize);
        entityDescriptorMap = new LinkedHashMap<Class<?>, EntityDescriptor>(mapSize);
        reversedEntityClassList = new ArrayList<Class<?>>(mapSize);
        lowestEntityDescriptorCache = new HashMap<Class<?>, EntityDescriptor>(mapSize);
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
        processMethodAnnotations(descriptorPolicy);
        processPropertyAnnotations(descriptorPolicy);
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

    private void processMethodAnnotations(DescriptorPolicy descriptorPolicy) {
        // This only iterates public methods
        for (Method method : solutionClass.getMethods()) {
            if (method.isAnnotationPresent(ValueRangeProvider.class)) {
                descriptorPolicy.addFromSolutionValueRangeProvider(method);
            }
        }
    }

    private void processPropertyAnnotations(DescriptorPolicy descriptorPolicy) {
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

    public void afterAnnotationsProcessed(DescriptorPolicy descriptorPolicy) {
        for (EntityDescriptor entityDescriptor : entityDescriptorMap.values()) {
            entityDescriptor.linkInheritedEntityDescriptors(descriptorPolicy);
        }
        for (EntityDescriptor entityDescriptor : entityDescriptorMap.values()) {
            entityDescriptor.linkShadowSources(descriptorPolicy);
        }
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
            throw new IllegalArgumentException("A planning entity is an instance of a entitySubclass ("
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
    
    public Collection<GenuineVariableDescriptor> getChainedVariableDescriptors() {
        Collection<GenuineVariableDescriptor> chainedVariableDescriptors
                = new ArrayList<GenuineVariableDescriptor>();
        for (EntityDescriptor entityDescriptor : entityDescriptorMap.values()) {
            for (GenuineVariableDescriptor variableDescriptor : entityDescriptor.getGenuineVariableDescriptors()) {
                if (variableDescriptor.isChained()) {
                    chainedVariableDescriptors.add(variableDescriptor);
                }
            }
        }
        return chainedVariableDescriptors;
    }

    public VariableListenerSupport buildVariableListenerSupport() {
        // Order is important, hence LinkedHashMap
        Map<VariableDescriptor, List<VariableListener>> variableListenerMap
                = new LinkedHashMap<VariableDescriptor, List<VariableListener>>();
        for (EntityDescriptor entityDescriptor : entityDescriptorMap.values()) {
            entityDescriptor.addDeclaredVariableListenersToMap(variableListenerMap);
        }
        return new VariableListenerSupport(variableListenerMap);
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
        for (PropertyAccessor entityPropertyAccessor : entityPropertyAccessorMap.values()) {
            Object entity = extractEntity(entityPropertyAccessor, solution);
            if (entity != null) {
                facts.add(entity);
            }
        }
        for (PropertyAccessor entityCollectionPropertyAccessor : entityCollectionPropertyAccessorMap.values()) {
            Collection<?> entityCollection = extractEntityCollection(
                    entityCollectionPropertyAccessor, solution);
            for (Object entity : entityCollection) {
                facts.add(entity);
            }
        }
        return facts;
    }

    /**
     * @param solution never null
     * @return >= 0
     */
    public int getEntityCount(Solution solution) {
        int entityCount = 0;
        for (PropertyAccessor entityPropertyAccessor : entityPropertyAccessorMap.values()) {
            Object entity = extractEntity(entityPropertyAccessor, solution);
            if (entity != null) {
                entityCount++;
            }
        }
        for (PropertyAccessor entityCollectionPropertyAccessor : entityCollectionPropertyAccessorMap.values()) {
            Collection<?> entityCollection = extractEntityCollection(
                    entityCollectionPropertyAccessor, solution);
            entityCount += entityCollection.size();
        }
        return entityCount;
    }

    public List<Object> getEntityList(Solution solution) {
        List<Object> entityList = new ArrayList<Object>();
        for (PropertyAccessor entityPropertyAccessor : entityPropertyAccessorMap.values()) {
            Object entity = extractEntity(entityPropertyAccessor, solution);
            if (entity != null) {
                entityList.add(entity);
            }
        }
        for (PropertyAccessor entityCollectionPropertyAccessor : entityCollectionPropertyAccessorMap.values()) {
            Collection<?> entityCollection = extractEntityCollection(
                    entityCollectionPropertyAccessor, solution);
            entityList.addAll(entityCollection);
        }
        return entityList;
    }

    public List<Object> getEntityListByEntityClass(Solution solution, Class<?> entityClass) {
        List<Object> entityList = new ArrayList<Object>();
        for (PropertyAccessor entityPropertyAccessor : entityPropertyAccessorMap.values()) {
            if (entityPropertyAccessor.getPropertyType().isAssignableFrom(entityClass)) {
                Object entity = extractEntity(entityPropertyAccessor, solution);
                if (entity != null && entityClass.isInstance(entity)) {
                    entityList.add(entity);
                }
            }
        }
        for (PropertyAccessor entityCollectionPropertyAccessor : entityCollectionPropertyAccessorMap.values()) {
            // TODO if (entityCollectionPropertyAccessor.getPropertyType().getElementType().isAssignableFrom(entityClass)) {
            Collection<?> entityCollection = extractEntityCollection(
                    entityCollectionPropertyAccessor, solution);
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
     * @return >= 0
     */
    public long getVariableCount(Solution solution) {
        long variableCount = 0L;
        for (PropertyAccessor entityPropertyAccessor : entityPropertyAccessorMap.values()) {
            Object entity = extractEntity(entityPropertyAccessor, solution);
            if (entity != null) {
                EntityDescriptor entityDescriptor = findEntityDescriptorOrFail(entity.getClass());
                variableCount += entityDescriptor.getVariableCount();
            }
        }
        for (PropertyAccessor entityCollectionPropertyAccessor : entityCollectionPropertyAccessorMap.values()) {
            Collection<?> entityCollection = extractEntityCollection(
                    entityCollectionPropertyAccessor, solution);
            for (Object entity : entityCollection) {
                EntityDescriptor entityDescriptor = findEntityDescriptorOrFail(entity.getClass());
                variableCount += entityDescriptor.getVariableCount();
            }
        }
        return variableCount;
    }

    /**
     * @param solution never null
     * @return >= 0
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
     * @return >= 0
     */
    public long getProblemScale(Solution solution) {
        long problemScale = 0L;
        for (PropertyAccessor entityPropertyAccessor : entityPropertyAccessorMap.values()) {
            Object entity = extractEntity(entityPropertyAccessor, solution);
            if (entity != null) {
                EntityDescriptor entityDescriptor = findEntityDescriptorOrFail(entity.getClass());
                problemScale += entityDescriptor.getProblemScale(solution, entity);
            }
        }
        for (PropertyAccessor entityCollectionPropertyAccessor : entityCollectionPropertyAccessorMap.values()) {
            Collection<?> entityCollection = extractEntityCollection(
                    entityCollectionPropertyAccessor, solution);
            for (Object entity : entityCollection) {
                EntityDescriptor entityDescriptor = findEntityDescriptorOrFail(entity.getClass());
                problemScale += entityDescriptor.getProblemScale(solution, entity);
            }
        }
        return problemScale;
    }

    public int countUninitializedVariables(Solution solution) {
        int count = 0;
        for (PropertyAccessor entityPropertyAccessor : entityPropertyAccessorMap.values()) {
            Object entity = extractEntity(entityPropertyAccessor, solution);
            if (entity != null) {
                EntityDescriptor entityDescriptor = findEntityDescriptorOrFail(entity.getClass());
                count += entityDescriptor.countUninitializedVariables(entity);
            }
        }
        for (PropertyAccessor entityCollectionPropertyAccessor : entityCollectionPropertyAccessorMap.values()) {
            Collection<?> entityCollection = extractEntityCollection(
                    entityCollectionPropertyAccessor, solution);
            for (Object entity : entityCollection) {
                EntityDescriptor entityDescriptor = findEntityDescriptorOrFail(entity.getClass());
                count += entityDescriptor.countUninitializedVariables(entity);
            }
        }
        return count;
    }

    /**
     * @param scoreDirector never null
     * @param solution never null
     * @return true if all the movable planning entities are initialized
     */
    public boolean isInitialized(ScoreDirector scoreDirector, Solution solution) {
        for (PropertyAccessor entityPropertyAccessor : entityPropertyAccessorMap.values()) {
            Object entity = extractEntity(entityPropertyAccessor, solution);
            if (entity != null) {
                EntityDescriptor entityDescriptor = findEntityDescriptorOrFail(entity.getClass());
                if (!entityDescriptor.isInitialized(entity)) {
                    if (!entityDescriptor.hasMovableEntitySelectionFilter()
                            || entityDescriptor.getMovableEntitySelectionFilter().accept(scoreDirector, entity)) {
                        return false;
                    }
                }
            }
        }
        for (PropertyAccessor entityCollectionPropertyAccessor : entityCollectionPropertyAccessorMap.values()) {
            Collection<?> entityCollection = extractEntityCollection(
                    entityCollectionPropertyAccessor, solution);
            for (Object entity : entityCollection) {
                EntityDescriptor entityDescriptor = findEntityDescriptorOrFail(entity.getClass());
                if (!entityDescriptor.isInitialized(entity)) {
                    if (!entityDescriptor.hasMovableEntitySelectionFilter()
                            || entityDescriptor.getMovableEntitySelectionFilter().accept(scoreDirector, entity)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public int countReinitializableVariables(ScoreDirector scoreDirector, Solution solution) {
        int count = 0;
        for (PropertyAccessor entityPropertyAccessor : entityPropertyAccessorMap.values()) {
            Object entity = extractEntity(entityPropertyAccessor, solution);
            if (entity != null) {
                EntityDescriptor entityDescriptor = findEntityDescriptorOrFail(entity.getClass());
                count += entityDescriptor.countReinitializableVariables(scoreDirector, entity);
            }
        }
        for (PropertyAccessor entityCollectionPropertyAccessor : entityCollectionPropertyAccessorMap.values()) {
            Collection<?> entityCollection = extractEntityCollection(
                    entityCollectionPropertyAccessor, solution);
            for (Object entity : entityCollection) {
                EntityDescriptor entityDescriptor = findEntityDescriptorOrFail(entity.getClass());
                count += entityDescriptor.countReinitializableVariables(scoreDirector, entity);
            }
        }
        return count;
    }

    private Object extractEntity(PropertyAccessor entityPropertyAccessor, Solution solution) {
        return entityPropertyAccessor.executeGetter(solution);
    }

    private Collection<?> extractEntityCollection(
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
