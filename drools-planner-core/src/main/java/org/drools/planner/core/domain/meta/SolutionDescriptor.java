package org.drools.planner.core.domain.meta;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.drools.planner.core.domain.PlanningEntityCollectionProperty;
import org.drools.planner.core.domain.PlanningEntityProperty;
import org.drools.planner.core.solution.Solution;

public class SolutionDescriptor implements Serializable {

    private final Class<? extends Solution> solutionClass;
    private final BeanInfo solutionBeanInfo;
    
    private final Map<String, PropertyDescriptor> propertyDescriptorMap;
    private final Map<String, PropertyDescriptor> entityPropertyDescriptorMap;
    private final Map<String, PropertyDescriptor> entityCollectionPropertyDescriptorMap;

    private final Map<Class<?>, PlanningEntityDescriptor> planningEntityDescriptorMap;

    public SolutionDescriptor(Class<? extends Solution> solutionClass) {
        this.solutionClass = solutionClass;
        try {
            solutionBeanInfo = Introspector.getBeanInfo(solutionClass);
        } catch (IntrospectionException e) {
            throw new IllegalStateException("The solutionClass (" + solutionClass + ") is not a valid java bean.", e);
        }
        int mapSize = solutionBeanInfo.getPropertyDescriptors().length;
        propertyDescriptorMap = new HashMap<String, PropertyDescriptor>(mapSize);
        entityPropertyDescriptorMap = new HashMap<String, PropertyDescriptor>(mapSize);
        entityCollectionPropertyDescriptorMap = new HashMap<String, PropertyDescriptor>(mapSize);
        planningEntityDescriptorMap = new HashMap<Class<?>, PlanningEntityDescriptor>(mapSize);
        processPropertyAnnotations();
    }

    private void processPropertyAnnotations() {
        boolean noPlanningEntityPropertyAnnotation = true;
        for (PropertyDescriptor propertyDescriptor : solutionBeanInfo.getPropertyDescriptors()) {
            propertyDescriptorMap.put(propertyDescriptor.getName(), propertyDescriptor);
            Method propertyGetter = propertyDescriptor.getReadMethod();
            if (propertyGetter.isAnnotationPresent(PlanningEntityProperty.class)) {
                noPlanningEntityPropertyAnnotation = false;
                entityPropertyDescriptorMap.put(propertyDescriptor.getName(), propertyDescriptor);
            } else if (propertyGetter.isAnnotationPresent(PlanningEntityCollectionProperty.class)) {
                noPlanningEntityPropertyAnnotation = false;
                if (!Collection.class.isAssignableFrom(propertyDescriptor.getPropertyType())) {
                    throw new IllegalStateException("The solutionClass (" + solutionClass
                            + ") has a PlanningEntityCollection annotated property ("
                            + propertyDescriptor.getName() + ") that does not return a Collection.");
                }
                entityCollectionPropertyDescriptorMap.put(propertyDescriptor.getName(), propertyDescriptor);
            }
        }
        if (noPlanningEntityPropertyAnnotation) {
            throw new IllegalStateException("The solutionClass (" + solutionClass
                    + ") should have at least 1 getter with a PlanningEntityCollection or PlanningEntityProperty"
                    + " annotation.");
        }
    }

    public Class<? extends Solution> getSolutionClass() {
        return solutionClass;
    }

    public PropertyDescriptor getPropertyDescriptor(String propertyName) {
        return propertyDescriptorMap.get(propertyName);
    }

    public void addPlanningEntityDescriptor(PlanningEntityDescriptor planningEntityDescriptor) {
        planningEntityDescriptorMap.put(planningEntityDescriptor.getPlanningEntityClass(), planningEntityDescriptor);
    }

    public PlanningEntityDescriptor getPlanningEntityDescriptor(Class<?> planningEntityImplementationClass) {
        PlanningEntityDescriptor planningEntityDescriptor = null;
        Class<?> planningEntityClass = planningEntityImplementationClass;
        while (planningEntityClass != null) {
            planningEntityDescriptor = planningEntityDescriptorMap.get(planningEntityClass);
            if (planningEntityDescriptor != null) {
                return planningEntityDescriptor;
            }
            planningEntityClass = planningEntityClass.getSuperclass();
        }
        return null;
    }

    public Collection<Object> getAllFacts(Solution solution) {
        Collection<Object> planningFacts = new ArrayList<Object>();
        planningFacts.addAll(solution.getProblemFacts());
        for (PropertyDescriptor entityPropertyDescriptor : entityPropertyDescriptorMap.values()) {
            Object entity = DescriptorUtils.executeGetter(entityPropertyDescriptor, solution);
            PlanningEntityDescriptor planningEntityDescriptor = getPlanningEntityDescriptor(entity.getClass());
            if (entity != null && planningEntityDescriptor.isInitialized(entity)) {
                planningFacts.add(entity);
            }
        }
        for (PropertyDescriptor entityCollectionPropertyDescriptor : entityCollectionPropertyDescriptorMap.values()) {
            Collection<? extends Object> entityCollection = (Collection<? extends Object>)
                    DescriptorUtils.executeGetter(entityCollectionPropertyDescriptor, solution);
            if (entityCollection == null) {
                throw new IllegalArgumentException("The entity collection property ("
                        + entityCollectionPropertyDescriptor.getName() + ") should never return null.");
            }
            for (Object entity : entityCollection) {
                PlanningEntityDescriptor planningEntityDescriptor = getPlanningEntityDescriptor(entity.getClass());
                if (planningEntityDescriptor.isInitialized(entity)) {
                    planningFacts.add(entity);
                }
            }
        }
        return planningFacts;
    }

    public Collection<Object> getPlanningEntities(Solution solution) {
        Collection<Object> planningEntities = new ArrayList<Object>();
        for (PropertyDescriptor entityPropertyDescriptor : entityPropertyDescriptorMap.values()) {
            Object entity = DescriptorUtils.executeGetter(entityPropertyDescriptor, solution);
            if (entity != null) {
                planningEntities.add(entity);
            }
        }
        for (PropertyDescriptor entityCollectionPropertyDescriptor : entityCollectionPropertyDescriptorMap.values()) {
            Collection<? extends Object> entityCollection = (Collection<? extends Object>)
                    DescriptorUtils.executeGetter(entityCollectionPropertyDescriptor, solution);
            if (entityCollection == null) {
                throw new IllegalArgumentException("The entity collection property ("
                        + entityCollectionPropertyDescriptor.getName() + ") should never return null.");
            }
            for (Object entity : entityCollection) {
                planningEntities.add(entity);
            }
        }
        return planningEntities;
    }

    /**
     * @param solution never null
     * @return true if all the planning entities are initialized
     */
    public boolean isInitialized(Solution solution) {
        for (PropertyDescriptor entityPropertyDescriptor : entityPropertyDescriptorMap.values()) {
            Object entity = DescriptorUtils.executeGetter(entityPropertyDescriptor, solution);
            PlanningEntityDescriptor planningEntityDescriptor = getPlanningEntityDescriptor(entity.getClass());
            if (entity == null || !planningEntityDescriptor.isInitialized(entity)) {
                return false;
            }
        }
        for (PropertyDescriptor entityCollectionPropertyDescriptor : entityCollectionPropertyDescriptorMap.values()) {
            Collection<? extends Object> entityCollection = (Collection<? extends Object>)
                    DescriptorUtils.executeGetter(entityCollectionPropertyDescriptor, solution);
            for (Object entity : entityCollection) {
                PlanningEntityDescriptor planningEntityDescriptor = getPlanningEntityDescriptor(entity.getClass());
                if (!planningEntityDescriptor.isInitialized(entity)) {
                    return false;
                }
            }
        }
        return true;
    }

}
