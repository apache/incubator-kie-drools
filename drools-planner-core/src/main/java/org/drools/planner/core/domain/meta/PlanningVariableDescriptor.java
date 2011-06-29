package org.drools.planner.core.domain.meta;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.Collection;

import org.drools.planner.core.domain.ValueRangeFromSolutionProperty;
import org.drools.planner.core.solution.Solution;

public class PlanningVariableDescriptor {

    private final PlanningEntityDescriptor planningEntityDescriptor;

    private final PropertyDescriptor variablePropertyDescriptor;

    private PropertyDescriptor rangePropertyDescriptor; // TODO extract to RangeValue interface

    public PlanningVariableDescriptor(PlanningEntityDescriptor planningEntityDescriptor,
            PropertyDescriptor variablePropertyDescriptor) {
        this.planningEntityDescriptor = planningEntityDescriptor;
        this.variablePropertyDescriptor = variablePropertyDescriptor;
        processPropertyAnnotations();
    }

    private void processPropertyAnnotations() {
        Method propertyGetter = variablePropertyDescriptor.getReadMethod();
        if (propertyGetter.isAnnotationPresent(ValueRangeFromSolutionProperty.class)) {
            processValueRangeSolutionPropertyAnnotation(propertyGetter.getAnnotation(ValueRangeFromSolutionProperty.class));
        } else {
            // TODO Support plugging in other ValueRange implementations
            throw new IllegalArgumentException("The planningEntityClass ("
                    + planningEntityDescriptor.getPlanningEntityClass()
                    + ") has a PlanningVariable annotated property (" + variablePropertyDescriptor.getName()
                    + ") that has no ValueRange* annotation, such as ValueRangeFromSolutionProperty.");
        }
    }

    private void processValueRangeSolutionPropertyAnnotation(
            ValueRangeFromSolutionProperty valueRangeFromSolutionPropertyAnnotation) {
        // TODO extract to RangeValue interface
        String solutionPropertyName = valueRangeFromSolutionPropertyAnnotation.propertyName();
        rangePropertyDescriptor = planningEntityDescriptor.getSolutionDescriptor()
                .getPropertyDescriptor(solutionPropertyName);
        if (rangePropertyDescriptor == null) {
            throw new IllegalArgumentException("The planningEntityClass ("
                    + planningEntityDescriptor.getPlanningEntityClass()
                    + ") has a PlanningVariable annotated property (" + variablePropertyDescriptor.getName()
                    + ") that refers to a solutionClass ("
                    + planningEntityDescriptor.getSolutionDescriptor().getSolutionClass()
                    + ") solutionProperty (" + solutionPropertyName
                    + ") that does not exist.");
        }
        if (!Collection.class.isAssignableFrom(rangePropertyDescriptor.getPropertyType())) {
            throw new IllegalArgumentException("The planningEntityClass ("
                    + planningEntityDescriptor.getPlanningEntityClass()
                    + ") has a PlanningVariable annotated property (" + variablePropertyDescriptor.getName()
                    + ") that refers to a solutionClass ("
                    + planningEntityDescriptor.getSolutionDescriptor().getSolutionClass()
                    + ") solutionProperty (" + solutionPropertyName
                    + ") that does not return a Collection.");
        }
    }

    public boolean isInitialized(Object planningEntity) {
        // TODO extract to VariableInitialized interface
        Object variable = DescriptorUtils.executeGetter(variablePropertyDescriptor, planningEntity);
        return variable != null;
    }

    public void uninitialize(Object planningEntity) {
        // TODO extract to VariableInitialized interface
        DescriptorUtils.executeSetter(variablePropertyDescriptor, planningEntity, null);
    }

    public Object getValue(Object bean) {
        return DescriptorUtils.executeGetter(variablePropertyDescriptor, bean);
    }

    public void setValue(Object bean, Object value) {
        DescriptorUtils.executeSetter(variablePropertyDescriptor, bean, value);
    }

    // TODO extract to RangeValue interface
    public Collection<?> getRangeValues(Solution solution) {
        return (Collection<?>) DescriptorUtils.executeGetter(rangePropertyDescriptor, solution);
    }

}
