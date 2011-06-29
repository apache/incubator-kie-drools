package org.drools.planner.core.domain.meta;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.drools.planner.core.domain.PlanningEntity;
import org.drools.planner.core.domain.PlanningVariable;

public class PlanningEntityDescriptor {

    private final SolutionDescriptor solutionDescriptor;

    private final Class<?> planningEntityClass;
    private final BeanInfo planningEntityBeanInfo;

    private final Map<String, PlanningVariableDescriptor> planningVariableDescriptorMap;

    public PlanningEntityDescriptor(SolutionDescriptor solutionDescriptor, Class<?> planningEntityClass) {
        this.solutionDescriptor = solutionDescriptor;
        this.planningEntityClass = planningEntityClass;
        PlanningEntity planningEntityAnnotation = planningEntityClass.getAnnotation(PlanningEntity.class);
        if (planningEntityAnnotation == null) {
            throw new IllegalStateException("The planningEntityClass (" + planningEntityClass
                    + ") has been specified as a planning entity in the configuration," +
                    " but does not have a PlanningEntity annotation.");
        }
        try {
            planningEntityBeanInfo = Introspector.getBeanInfo(planningEntityClass);
        } catch (IntrospectionException e) {
            throw new IllegalStateException("The planningEntityClass (" + planningEntityClass
                    + ") is not a valid java bean.", e);
        }
        int mapSize = planningEntityBeanInfo.getPropertyDescriptors().length;
        planningVariableDescriptorMap = new HashMap<String, PlanningVariableDescriptor>(mapSize);
        processPropertyAnnotations();
    }

    private void processPropertyAnnotations() {
        boolean noPlanningVariableAnnotation = true;
        for (PropertyDescriptor propertyDescriptor : planningEntityBeanInfo.getPropertyDescriptors()) {
            Method propertyGetter = propertyDescriptor.getReadMethod();
            if (propertyGetter.isAnnotationPresent(PlanningVariable.class)) {
                noPlanningVariableAnnotation = false;
                PlanningVariable planningVariableAnnotation = propertyGetter.getAnnotation(PlanningVariable.class);
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
