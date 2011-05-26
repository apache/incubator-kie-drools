package org.drools.planner.core.domain.meta;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.drools.planner.core.domain.PlanningEntityCollectionProperty;
import org.drools.planner.core.domain.PlanningFactCollectionProperty;
import org.drools.planner.core.domain.PlanningFactProperty;
import org.drools.planner.core.solution.Solution;

public class SolutionDescriptor implements Serializable {

    private final Class<? extends Solution> solutionClass;
    private final BeanInfo solutionBeanInfo;
    
    private final Map<String, PropertyDescriptor> propertyDescriptorMap;
    private final Map<String, PropertyDescriptor> factPropertyDescriptorMap;
    private final Map<String, PropertyDescriptor> factCollectionPropertyDescriptorMap;
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
        factPropertyDescriptorMap = new HashMap<String, PropertyDescriptor>(mapSize);
        factCollectionPropertyDescriptorMap = new HashMap<String, PropertyDescriptor>(mapSize);
        entityCollectionPropertyDescriptorMap = new HashMap<String, PropertyDescriptor>(mapSize);
        planningEntityDescriptorMap = new HashMap<Class<?>, PlanningEntityDescriptor>(mapSize);
        processPropertyAnnotations();
    }

    private void processPropertyAnnotations() {
        boolean noPlanningEntityCollectionAnnotation = true;
        for (PropertyDescriptor propertyDescriptor : solutionBeanInfo.getPropertyDescriptors()) {
            propertyDescriptorMap.put(propertyDescriptor.getName(), propertyDescriptor);
            Method propertyGetter = propertyDescriptor.getReadMethod();
            if (propertyGetter.isAnnotationPresent(PlanningFactProperty.class)) {
                factPropertyDescriptorMap.put(propertyDescriptor.getName(), propertyDescriptor);
            } else if (propertyGetter.isAnnotationPresent(PlanningFactCollectionProperty.class)) {
                if (!Collection.class.isAssignableFrom(propertyDescriptor.getPropertyType())) {
                    throw new IllegalStateException("The solutionClass (" + solutionClass
                            + ") has a PlanningFactCollectionProperty annotated property ("
                            + propertyDescriptor.getName() + ") that does not return a Collection.");
                }
                factCollectionPropertyDescriptorMap.put(propertyDescriptor.getName(), propertyDescriptor);
            } else if (propertyGetter.isAnnotationPresent(PlanningEntityCollectionProperty.class)) {
                PlanningEntityCollectionProperty planningEntityCollectionPropertyAnnotation = propertyGetter
                        .getAnnotation(PlanningEntityCollectionProperty.class);
                noPlanningEntityCollectionAnnotation = false;
                if (!Collection.class.isAssignableFrom(propertyDescriptor.getPropertyType())) {
                    throw new IllegalStateException("The solutionClass (" + solutionClass
                            + ") has a PlanningEntityCollection annotated property ("
                            + propertyDescriptor.getName() + ") that does not return a Collection.");
                }
                entityCollectionPropertyDescriptorMap.put(propertyDescriptor.getName(), propertyDescriptor);
            }
        }
        if (noPlanningEntityCollectionAnnotation) {
            throw new IllegalStateException("The solutionClass (" + solutionClass
                    + ") should have at least 1 getter with a PlanningEntityCollection annotation.");
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

    public PlanningEntityDescriptor getPlanningEntityDescriptor(Class<?> planningEntityClass) {
        return planningEntityDescriptorMap.get(planningEntityClass);
    }

    public Collection<? extends Object> getFacts(Solution solution) {
        Collection<Object> planningFacts = new ArrayList<Object>();
        for (PropertyDescriptor factPropertyDescriptor : factPropertyDescriptorMap.values()) {
            Object fact = DescriptorUtils.executeGetter(factPropertyDescriptor, solution);
            planningFacts.add(fact);
        }
        for (PropertyDescriptor factCollectionPropertyDescriptor : factCollectionPropertyDescriptorMap.values()) {
            Collection<? extends Object> factCollection = (Collection<? extends Object>)
                    DescriptorUtils.executeGetter(factCollectionPropertyDescriptor, solution);
            planningFacts.addAll(factCollection);
        }
        for (PropertyDescriptor entityCollectionPropertyDescriptor : entityCollectionPropertyDescriptorMap.values()) {
            Collection<? extends Object> entityCollection = (Collection<? extends Object>)
                    DescriptorUtils.executeGetter(entityCollectionPropertyDescriptor, solution);
            for (Object entity : entityCollection) {
                PlanningEntityDescriptor planningEntityDescriptor = planningEntityDescriptorMap.get(entity.getClass());
                if (planningEntityDescriptor.isInitialized(entity)) {
                    planningFacts.add(entity);
                }
            }
        }
        return planningFacts;
    }

    public Collection<? extends Object> getPlanningEntities(Solution solution) {
        Collection<Object> planningEntities = new ArrayList<Object>();
        for (PropertyDescriptor entityCollectionPropertyDescriptor : entityCollectionPropertyDescriptorMap.values()) {
            Collection<? extends Object> entityCollection = (Collection<? extends Object>)
                    DescriptorUtils.executeGetter(entityCollectionPropertyDescriptor, solution);
            for (Object entity : entityCollection) {
                planningEntities.add(entity);
            }
        }
        return planningEntities;
    }

}
