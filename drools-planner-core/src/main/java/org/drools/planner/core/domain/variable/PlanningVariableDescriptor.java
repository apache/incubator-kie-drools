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

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

import org.drools.planner.api.domain.variable.PlanningValueStrengthWeightFactory;
import org.drools.planner.api.domain.variable.PlanningVariable;
import org.drools.planner.api.domain.variable.ValueRangeFromPlanningEntityProperty;
import org.drools.planner.api.domain.variable.ValueRangeFromSolutionProperty;
import org.drools.planner.api.domain.variable.ValueRangeUndefined;
import org.drools.planner.core.domain.common.DescriptorUtils;
import org.drools.planner.core.domain.entity.PlanningEntityDescriptor;
import org.drools.planner.core.solution.Solution;
import org.drools.planner.core.solution.director.SolutionDirector;

public class PlanningVariableDescriptor {

    private final PlanningEntityDescriptor planningEntityDescriptor;

    private final PropertyDescriptor variablePropertyDescriptor;

    private PlanningValueRangeDescriptor valueRangeDescriptor;
    private PlanningValueSorter valueSorter;

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
        valueSorter = new PlanningValueSorter();
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
            valueSorter.setStrengthComparator(strengthComparator);
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
            valueSorter.setStrengthWeightFactory(strengthWeightFactory);
        }

        Method propertyGetter = variablePropertyDescriptor.getReadMethod();
        int valueRangeAnnotationCount = 0;
        if (propertyGetter.isAnnotationPresent(ValueRangeFromSolutionProperty.class)) {
            valueRangeDescriptor = new SolutionPropertyPlanningValueRangeDescriptor(this,
                    propertyGetter.getAnnotation(ValueRangeFromSolutionProperty.class));
            valueRangeAnnotationCount++;
        }
        if (propertyGetter.isAnnotationPresent(ValueRangeFromPlanningEntityProperty.class)) {
            valueRangeDescriptor = new PlanningEntityPropertyPlanningValueRangeDescriptor(this,
                    propertyGetter.getAnnotation(ValueRangeFromPlanningEntityProperty.class));
            valueRangeAnnotationCount++;
        }
        if (propertyGetter.isAnnotationPresent(ValueRangeUndefined.class)) {
            valueRangeDescriptor = new UndefinedPlanningValueRangeDescriptor(this,
                    propertyGetter.getAnnotation(ValueRangeUndefined.class));
            valueRangeAnnotationCount++;
        }
        // TODO Support plugging in other ValueRange implementations
        if (valueRangeAnnotationCount <= 0) {
            throw new IllegalArgumentException("The planningEntityClass ("
                    + planningEntityDescriptor.getPlanningEntityClass()
                    + ") has a PlanningVariable annotated property (" + variablePropertyDescriptor.getName()
                    + ") that has no ValueRange* annotation, such as ValueRangeFromSolutionProperty.");
        }
        if (valueRangeAnnotationCount > 1) {
            throw new IllegalArgumentException("The planningEntityClass ("
                    + planningEntityDescriptor.getPlanningEntityClass()
                    + ") has a PlanningVariable annotated property (" + variablePropertyDescriptor.getName()
                    + ") that has multiple ValueRange* annotations.");
        }
    }

    public PlanningEntityDescriptor getPlanningEntityDescriptor() {
        return planningEntityDescriptor;
    }

    public String getVariablePropertyName() {
        return variablePropertyDescriptor.getName();
    }

    public PlanningValueSorter getValueSorter() {
        return valueSorter;
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

    public Object getValue(Object planningEntity) {
        return DescriptorUtils.executeGetter(variablePropertyDescriptor, planningEntity);
    }

    public void setValue(Object planningEntity, Object value) {
        DescriptorUtils.executeSetter(variablePropertyDescriptor, planningEntity, value);
    }

    public Collection<?> extractPlanningValues(SolutionDirector solutionDirector, Object planningEntity) {
        return valueRangeDescriptor.extractValues(solutionDirector, planningEntity);
    }

    public boolean isPlanningValuesCacheable() {
        return valueRangeDescriptor.isValuesCacheable();
    }

}
