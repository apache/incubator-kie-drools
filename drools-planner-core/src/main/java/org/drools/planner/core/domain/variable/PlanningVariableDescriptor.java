package org.drools.planner.core.domain.variable;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

import org.drools.planner.api.domain.variable.PlanningValueStrengthWeightFactory;
import org.drools.planner.api.domain.variable.PlanningVariable;
import org.drools.planner.api.domain.variable.ValueRangeFromSolutionProperty;
import org.drools.planner.api.domain.variable.ValueRangeUndefined;
import org.drools.planner.core.domain.common.DescriptorUtils;
import org.drools.planner.core.domain.entity.PlanningEntityDescriptor;
import org.drools.planner.core.solution.Solution;

public class PlanningVariableDescriptor {

    private final PlanningEntityDescriptor planningEntityDescriptor;

    private final PropertyDescriptor variablePropertyDescriptor;

    private PropertyDescriptor rangePropertyDescriptor; // TODO extract to RangeValue interface
    private PlanningValueSorter planningValueSorter;

    public PlanningVariableDescriptor(PlanningEntityDescriptor planningEntityDescriptor,
            PropertyDescriptor variablePropertyDescriptor) {
        this.planningEntityDescriptor = planningEntityDescriptor;
        this.variablePropertyDescriptor = variablePropertyDescriptor;
        processPropertyAnnotations();
    }

    private void processPropertyAnnotations() {
        PlanningVariable planningVariableAnnotation = variablePropertyDescriptor.getReadMethod()
                .getAnnotation(PlanningVariable.class);
        Class<? extends Comparator> strengthComparatorClass = planningVariableAnnotation.strengthComparatorClass();
        if (strengthComparatorClass == PlanningVariable.NullStrengthComparator.class) {
            strengthComparatorClass = null;
        }
        Class<? extends PlanningValueStrengthWeightFactory> strengthWeightFactoryClass
                = planningVariableAnnotation.strengthWeightFactoryClass();
        if (strengthWeightFactoryClass == PlanningVariable.NullStrengthWeightFactory.class) {
            strengthWeightFactoryClass = null;
        }
        if (strengthComparatorClass != null && strengthWeightFactoryClass != null) {
            throw new IllegalStateException("The planningEntityClass ("
                    + planningEntityDescriptor.getPlanningEntityClass()
                    + ") " + variablePropertyDescriptor.getName() + ") cannot have a strengthComparatorClass (" + strengthComparatorClass.getName()
                    + ") and a strengthWeightFactoryClass (" + strengthWeightFactoryClass.getName()
                    + ") at the same time.");
        }
        planningValueSorter = new PlanningValueSorter();
        if (strengthComparatorClass != null) {
            Comparator<Object> strengthComparator;
            try {
                strengthComparator = strengthComparatorClass.newInstance();
            } catch (InstantiationException e) {
                throw new IllegalArgumentException("The strengthComparatorClass ("
                        + strengthComparatorClass.getName()
                        + ") does not have a public no-arg constructor", e);
            } catch (IllegalAccessException e) {
                throw new IllegalArgumentException("The strengthComparatorClass ("
                        + strengthComparatorClass.getName()
                        + ") does not have a public no-arg constructor", e);
            }
            planningValueSorter.setStrengthComparator(strengthComparator);
        }
        if (strengthWeightFactoryClass != null) {
            PlanningValueStrengthWeightFactory strengthWeightFactory;
            try {
                strengthWeightFactory = strengthWeightFactoryClass.newInstance();
            } catch (InstantiationException e) {
                throw new IllegalArgumentException("The strengthWeightFactoryClass ("
                        + strengthWeightFactoryClass.getName()
                        + ") does not have a public no-arg constructor", e);
            } catch (IllegalAccessException e) {
                throw new IllegalArgumentException("The strengthWeightFactoryClass ("
                        + strengthWeightFactoryClass.getName()
                        + ") does not have a public no-arg constructor", e);
            }
            planningValueSorter.setStrengthWeightFactory(strengthWeightFactory);
        }

        Method propertyGetter = variablePropertyDescriptor.getReadMethod();
        if (propertyGetter.isAnnotationPresent(ValueRangeFromSolutionProperty.class)) {
            processValueRangeSolutionPropertyAnnotation(propertyGetter.getAnnotation(ValueRangeFromSolutionProperty.class));
        } else if (propertyGetter.isAnnotationPresent(ValueRangeUndefined.class)) {
            processValueRangeUndefinedAnnotation(propertyGetter.getAnnotation(ValueRangeUndefined.class));
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
            String exceptionMessage = "The planningEntityClass ("
                    + planningEntityDescriptor.getPlanningEntityClass()
                    + ") has a PlanningVariable annotated property (" + variablePropertyDescriptor.getName()
                    + ") that refers to a solutionClass ("
                    + planningEntityDescriptor.getSolutionDescriptor().getSolutionClass()
                    + ") solutionProperty (" + solutionPropertyName
                    + ") that does not exist.";
            if (solutionPropertyName.length() >= 2 && Character.isUpperCase(solutionPropertyName.charAt(1))) {
                String correctedSolutionPropertyName = solutionPropertyName.substring(0, 1).toUpperCase()
                        + solutionPropertyName.substring(1);
                exceptionMessage += " But it probably needs to be correctedSolutionPropertyName ("
                        + correctedSolutionPropertyName + ") instead because the JavaBeans spec states" +
                        " the first letter should be a upper case if the second is upper case.";
            }
            throw new IllegalArgumentException(exceptionMessage);
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

    private void processValueRangeUndefinedAnnotation(ValueRangeUndefined valueRangeUndefined) {
        // TODO extract to RangeValue interface
        rangePropertyDescriptor = null;
    }

    public PlanningEntityDescriptor getPlanningEntityDescriptor() {
        return planningEntityDescriptor;
    }

    public String getVariablePropertyName() {
        return variablePropertyDescriptor.getName();
    }

    public PlanningValueSorter getPlanningValueSorter() {
        return planningValueSorter;
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

    private Collection<?> extractPlanningValueCollection(Solution solution) {
        // TODO extract to RangeValue interface
        if (rangePropertyDescriptor == null) {
            throw new IllegalStateException("The planningEntityClass ("
                    + planningEntityDescriptor.getPlanningEntityClass()
                    + ") has a PlanningVariable annotated property (" + variablePropertyDescriptor.getName()
                    + ") which uses a @ValueRangeUndefined instead of a @ValueRangeFromSolutionProperty.");
        }
        return (Collection<?>) DescriptorUtils.executeGetter(rangePropertyDescriptor, solution);
    }

    public List<Object> getPlanningValueList(Solution solution) {
        return new ArrayList<Object>(extractPlanningValueCollection(solution));
    }

}
