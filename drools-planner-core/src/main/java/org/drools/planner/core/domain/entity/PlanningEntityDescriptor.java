package org.drools.planner.core.domain.entity;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import org.drools.planner.api.domain.entity.PlanningEntity;
import org.drools.planner.api.domain.entity.PlanningEntityDifficultyWeightFactory;
import org.drools.planner.api.domain.variable.PlanningVariable;
import org.drools.planner.core.domain.solution.SolutionDescriptor;
import org.drools.planner.core.domain.variable.PlanningVariableDescriptor;

public class PlanningEntityDescriptor {

    private final SolutionDescriptor solutionDescriptor;

    private final Class<?> planningEntityClass;
    private final BeanInfo planningEntityBeanInfo;
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
        processEntityAnnotations();
        processPropertyAnnotations();
    }

    private void processEntityAnnotations() {
        PlanningEntity planningEntityAnnotation = planningEntityClass.getAnnotation(PlanningEntity.class);
        if (planningEntityAnnotation == null) {
            throw new IllegalStateException("The planningEntityClass (" + planningEntityClass
                    + ") has been specified as a planning entity in the configuration," +
                    " but does not have a PlanningEntity annotation.");
        }
        Class<? extends Comparator> difficultyComparatorClass = planningEntityAnnotation.difficultyComparatorClass();
        if (difficultyComparatorClass == PlanningEntity.NullDifficultyComparator.class) {
            difficultyComparatorClass = null;
        }
        Class<? extends PlanningEntityDifficultyWeightFactory> difficultyWeightFactoryClass
                = planningEntityAnnotation.difficultyWeightFactoryClass();
        if (difficultyWeightFactoryClass == PlanningEntity.NullDifficultyWeightFactory.class) {
            difficultyWeightFactoryClass = null;
        }
        if (difficultyComparatorClass != null && difficultyWeightFactoryClass != null) {
            throw new IllegalStateException("The planningEntityClass (" + planningEntityClass
                    + ") cannot have a difficultyComparatorClass (" + difficultyComparatorClass.getName()
                    + ") and a difficultyWeightFactoryClass (" + difficultyWeightFactoryClass.getName()
                    + ") at the same time.");
        }
        planningEntitySorter = new PlanningEntitySorter();
        if (difficultyComparatorClass != null) {
            Comparator<Object> difficultyComparator;
            try {
                difficultyComparator = difficultyComparatorClass.newInstance();
            } catch (InstantiationException e) {
                throw new IllegalArgumentException("The difficultyComparatorClass ("
                        + difficultyComparatorClass.getName()
                        + ") does not have a public no-arg constructor", e);
            } catch (IllegalAccessException e) {
                throw new IllegalArgumentException("The difficultyComparatorClass ("
                        + difficultyComparatorClass.getName()
                        + ") does not have a public no-arg constructor", e);
            }
            planningEntitySorter.setDifficultyComparator(difficultyComparator);
        }
        if (difficultyWeightFactoryClass != null) {
            PlanningEntityDifficultyWeightFactory difficultyWeightFactory;
            try {
                difficultyWeightFactory = difficultyWeightFactoryClass.newInstance();
            } catch (InstantiationException e) {
                throw new IllegalArgumentException("The difficultyWeightFactoryClass ("
                        + difficultyWeightFactoryClass.getName()
                        + ") does not have a public no-arg constructor", e);
            } catch (IllegalAccessException e) {
                throw new IllegalArgumentException("The difficultyWeightFactoryClass ("
                        + difficultyWeightFactoryClass.getName()
                        + ") does not have a public no-arg constructor", e);
            }
            planningEntitySorter.setDifficultyWeightFactory(difficultyWeightFactory);
        }
    }

    private void processPropertyAnnotations() {
        int mapSize = planningEntityBeanInfo.getPropertyDescriptors().length;
        planningVariableDescriptorMap = new HashMap<String, PlanningVariableDescriptor>(mapSize);
        boolean noPlanningVariableAnnotation = true;
        for (PropertyDescriptor propertyDescriptor : planningEntityBeanInfo.getPropertyDescriptors()) {
            Method propertyGetter = propertyDescriptor.getReadMethod();
            if (propertyGetter.isAnnotationPresent(PlanningVariable.class)) {
                noPlanningVariableAnnotation = false;
                if (propertyDescriptor.getWriteMethod() == null) {
                    throw new IllegalStateException("The planningEntityClass (" + planningEntityClass
                            + ") has a PlanningVariable annotated property (" + propertyDescriptor.getName()
                            + ") that should have a setter.");
                }
                PlanningVariableDescriptor planningVariableDescriptor = new PlanningVariableDescriptor(
                        this, propertyDescriptor);
                planningVariableDescriptorMap.put(propertyDescriptor.getName(), planningVariableDescriptor);
            }
        }
        if (noPlanningVariableAnnotation) {
            throw new IllegalStateException("The planningEntityClass (" + planningEntityClass
                    + ") should have at least 1 getter with a PlanningVariable annotation.");
        }
    }

    public SolutionDescriptor getSolutionDescriptor() {
        return solutionDescriptor;
    }

    public Class<?> getPlanningEntityClass() {
        return planningEntityClass;
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

    public Collection<PlanningVariableDescriptor> getPlanningVariableDescriptors() {
        return planningVariableDescriptorMap.values();
    }

    public boolean isInitialized(Object planningEntity) {
        for (PlanningVariableDescriptor planningVariableDescriptor : planningVariableDescriptorMap.values()) {
            if (!planningVariableDescriptor.isInitialized(planningEntity)) {
                return false;
            }
        }
        return true;
    }

    public void uninitialize(Object planningEntity) {
        for (PlanningVariableDescriptor planningVariableDescriptor : planningVariableDescriptorMap.values()) {
            planningVariableDescriptor.uninitialize(planningEntity);
        }
    }

}
