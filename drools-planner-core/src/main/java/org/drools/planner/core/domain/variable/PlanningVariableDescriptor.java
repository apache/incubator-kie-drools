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

package org.drools.planner.core.domain.variable;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

import org.drools.planner.api.domain.value.ValueRange;
import org.drools.planner.api.domain.value.ValueRanges;
import org.drools.planner.api.domain.variable.PlanningVariable;
import org.drools.planner.config.util.ConfigUtils;
import org.drools.planner.core.domain.common.PropertyAccessor;
import org.drools.planner.core.domain.entity.PlanningEntityDescriptor;
import org.drools.planner.core.domain.value.CompositePlanningValueRangeDescriptor;
import org.drools.planner.core.domain.value.PlanningEntityPropertyPlanningValueRangeDescriptor;
import org.drools.planner.core.domain.value.PlanningValueRangeDescriptor;
import org.drools.planner.core.domain.value.SolutionPropertyPlanningValueRangeDescriptor;
import org.drools.planner.core.domain.value.UndefinedPlanningValueRangeDescriptor;
import org.drools.planner.core.heuristic.selector.common.decorator.SelectionFilter;
import org.drools.planner.core.heuristic.selector.common.decorator.SelectionSorterWeightFactory;
import org.drools.planner.core.heuristic.selector.entity.decorator.NullValueReinitializeVariableEntityFilter;
import org.drools.planner.core.solution.Solution;

public class PlanningVariableDescriptor {

    private final PlanningEntityDescriptor planningEntityDescriptor;

    private final PropertyAccessor variablePropertyAccessor;
    private boolean chained;

    private PlanningValueRangeDescriptor valueRangeDescriptor;
    private boolean nullable;
    private SelectionFilter reinitializeVariableEntityFilter;
    private PlanningValueSorter valueSorter;

    public PlanningVariableDescriptor(PlanningEntityDescriptor planningEntityDescriptor,
            PropertyAccessor variablePropertyAccessor) {
        this.planningEntityDescriptor = planningEntityDescriptor;
        this.variablePropertyAccessor = variablePropertyAccessor;
    }

    public void processAnnotations() {
        processPropertyAnnotations();
    }

    private void processPropertyAnnotations() {
        PlanningVariable planningVariableAnnotation = variablePropertyAccessor.getReadMethod()
                .getAnnotation(PlanningVariable.class);
        valueSorter = new PlanningValueSorter();
        processNullable(planningVariableAnnotation);
        processStrength(planningVariableAnnotation);
        processChained(planningVariableAnnotation);
        processValueRangeAnnotation();
    }

    private void processNullable(PlanningVariable planningVariableAnnotation) {
        nullable = planningVariableAnnotation.nullable();
        if (nullable && variablePropertyAccessor.getPropertyType().isPrimitive()) {
            throw new IllegalArgumentException("The planningEntityClass ("
                    + planningEntityDescriptor.getPlanningEntityClass()
                    + ") has a PlanningVariable annotated property (" + variablePropertyAccessor.getName()
                    + ") with nullable (" + nullable + "), which is not compatible with the primitive propertyType ("
                    + variablePropertyAccessor.getPropertyType() + ").");
        }
        Class<? extends SelectionFilter> reinitializeVariableEntityFilterClass
                = planningVariableAnnotation.reinitializeVariableEntityFilter();
        if (reinitializeVariableEntityFilterClass == PlanningVariable.NullReinitializeVariableEntityFilter.class) {
            reinitializeVariableEntityFilterClass = null;
        }
        if (reinitializeVariableEntityFilterClass != null) {
            reinitializeVariableEntityFilter = ConfigUtils.newInstance(this,
                    "reinitializeVariableEntityFilterClass", reinitializeVariableEntityFilterClass);
        } else {
            reinitializeVariableEntityFilter = new NullValueReinitializeVariableEntityFilter(this);
        }
    }

    private void processStrength(PlanningVariable planningVariableAnnotation) {
        Class<? extends Comparator> strengthComparatorClass = planningVariableAnnotation.strengthComparatorClass();
        if (strengthComparatorClass == PlanningVariable.NullStrengthComparator.class) {
            strengthComparatorClass = null;
        }
        Class<? extends SelectionSorterWeightFactory> strengthWeightFactoryClass
                = planningVariableAnnotation.strengthWeightFactoryClass();
        if (strengthWeightFactoryClass == PlanningVariable.NullStrengthWeightFactory.class) {
            strengthWeightFactoryClass = null;
        }
        if (strengthComparatorClass != null && strengthWeightFactoryClass != null) {
            throw new IllegalStateException("The planningEntityClass ("
                    + planningEntityDescriptor.getPlanningEntityClass()  + ") property ("
                    + variablePropertyAccessor.getName()
                    + ") cannot have a strengthComparatorClass (" + strengthComparatorClass.getName()
                    + ") and a strengthWeightFactoryClass (" + strengthWeightFactoryClass.getName()
                    + ") at the same time.");
        }
        if (strengthComparatorClass != null) {
            Comparator<Object> strengthComparator = ConfigUtils.newInstance(this,
                    "strengthComparatorClass", strengthComparatorClass);
            valueSorter.setStrengthComparator(strengthComparator);
        }
        if (strengthWeightFactoryClass != null) {
            SelectionSorterWeightFactory strengthWeightFactory = ConfigUtils.newInstance(this,
                    "strengthWeightFactoryClass", strengthWeightFactoryClass);
            valueSorter.setStrengthWeightFactory(strengthWeightFactory);
        }
    }

    private void processChained(PlanningVariable planningVariableAnnotation) {
        chained = planningVariableAnnotation.chained();
        if (chained && !variablePropertyAccessor.getPropertyType().isAssignableFrom(
                planningEntityDescriptor.getPlanningEntityClass())) {
            throw new IllegalArgumentException("The planningEntityClass ("
                    + planningEntityDescriptor.getPlanningEntityClass()
                    + ") has a PlanningVariable annotated property (" + variablePropertyAccessor.getName()
                    + ") with chained (" + chained + ") and propertyType (" + variablePropertyAccessor.getPropertyType()
                    + ") which is not a superclass/interface of or the same as the planningEntityClass ("
                    + planningEntityDescriptor.getPlanningEntityClass() + ").");
        }
        if (chained && nullable) {
            throw new IllegalArgumentException("The planningEntityClass ("
                    + planningEntityDescriptor.getPlanningEntityClass()
                    + ") has a PlanningVariable annotated property (" + variablePropertyAccessor.getName()
                    + ") with chained (" + chained + "), which is not compatible with nullable (" + nullable + ").");
        }
    }

    private void processValueRangeAnnotation() {
        Method propertyGetter = variablePropertyAccessor.getReadMethod();
        ValueRange valueRangeAnnotation = propertyGetter.getAnnotation(ValueRange.class);
        ValueRanges valueRangesAnnotation = propertyGetter.getAnnotation(ValueRanges.class);
        if (valueRangeAnnotation != null) {
            if (valueRangesAnnotation != null) {
                throw new IllegalArgumentException("The planningEntityClass ("
                        + planningEntityDescriptor.getPlanningEntityClass()
                        + ") has a PlanningVariable annotated property (" + variablePropertyAccessor.getName()
                        + ") that has a @ValueRange and @ValueRanges annotation: fold them into 1 @ValueRanges.");
            }
            valueRangeDescriptor = buildValueRangeDescriptor(valueRangeAnnotation);
        } else {
            if (valueRangesAnnotation == null) {
                throw new IllegalArgumentException("The planningEntityClass ("
                        + planningEntityDescriptor.getPlanningEntityClass()
                        + ") has a PlanningVariable annotated property (" + variablePropertyAccessor.getName()
                        + ") that has no @ValueRange or @ValueRanges annotation.");
            }
            List<PlanningValueRangeDescriptor> valueRangeDescriptorList
                    = new ArrayList<PlanningValueRangeDescriptor>(valueRangesAnnotation.value().length);
            for (ValueRange partialValueRangeAnnotation : valueRangesAnnotation.value()) {
                valueRangeDescriptorList.add(buildValueRangeDescriptor(partialValueRangeAnnotation));
            }
            valueRangeDescriptor = new CompositePlanningValueRangeDescriptor(this, valueRangeDescriptorList);
        }
    }

    private PlanningValueRangeDescriptor buildValueRangeDescriptor(ValueRange valueRangeAnnotation) {
        switch (valueRangeAnnotation.type()) {
            case FROM_SOLUTION_PROPERTY:
                return new SolutionPropertyPlanningValueRangeDescriptor(this, valueRangeAnnotation);
            case FROM_PLANNING_ENTITY_PROPERTY:
                return new PlanningEntityPropertyPlanningValueRangeDescriptor(this, valueRangeAnnotation);
            case UNDEFINED:
                return new UndefinedPlanningValueRangeDescriptor(this, valueRangeAnnotation);
            default:
                throw new IllegalStateException("The valueRangeType ("
                        + valueRangeAnnotation.type() + ") is not implemented.");
        }
        // TODO Support plugging in other ValueRange implementations
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    public PlanningEntityDescriptor getPlanningEntityDescriptor() {
        return planningEntityDescriptor;
    }

    public String getVariableName() {
        return variablePropertyAccessor.getName();
    }

    public Class<?> getVariablePropertyType() {
        return variablePropertyAccessor.getPropertyType();
    }

    /**
     * @return true if the value range is continuous (as in for example every double value between 1.2 and 1.4)
     */
    public boolean isContinuous() {
        // TODO not yet supported
        return false;
    }

    public boolean isChained() {
        return chained;
    }

    public boolean isNullable() {
        return nullable;
    }

    /**
     * A {@link PlanningVariable#nullable()} value is always considered initialized, but it can still be reinitialized
     * with {@link PlanningVariable#reinitializeVariableEntityFilter()}.
     * @param planningEntity never null
     * @return true if the variable on that planningEntity is initialized
     */
    public boolean isInitialized(Object planningEntity) {
        if (nullable) {
            return true;
        }
        Object variable = getValue(planningEntity);
        return variable != null;
    }

    public Object getValue(Object planningEntity) {
        return variablePropertyAccessor.executeGetter(planningEntity);
    }

    public void setValue(Object planningEntity, Object value) {
        variablePropertyAccessor.executeSetter(planningEntity, value);
    }

    public Collection<?> extractAllPlanningValues(Solution solution) {
        // TODO this does not include null if nullable, currently FromSolutionPropertyValueSelector does that
        return valueRangeDescriptor.extractAllValues(solution);
    }

    public Collection<?> extractPlanningValues(Solution solution, Object planningEntity) {
        // TODO this does not include null if nullable, currently FromSolutionPropertyValueSelector does that
        return valueRangeDescriptor.extractValues(solution, planningEntity);
    }

    @Deprecated
    public boolean isPlanningValuesCacheable() {
        return valueRangeDescriptor.isValuesCacheable();
    }

    @Deprecated
    public PlanningValueSorter getValueSorter() {
        return valueSorter;
    }

    public long getProblemScale(Solution solution, Object planningEntity) {
        return valueRangeDescriptor.getProblemScale(solution, planningEntity);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + variablePropertyAccessor.getName()
                + " of " + planningEntityDescriptor.getPlanningEntityClass().getName() + ")";
    }

}
