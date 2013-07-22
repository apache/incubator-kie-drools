/*
 * Copyright 2013 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.optaplanner.core.impl.domain.variable.shadow;

import java.beans.PropertyDescriptor;
import java.util.Arrays;
import java.util.Comparator;

import org.apache.commons.lang.ArrayUtils;
import org.optaplanner.core.api.domain.variable.PlanningVariable;
import org.optaplanner.core.impl.domain.common.PropertyAccessor;
import org.optaplanner.core.impl.domain.common.ReflectionPropertyAccessor;
import org.optaplanner.core.impl.domain.entity.PlanningEntityDescriptor;
import org.optaplanner.core.impl.domain.policy.DescriptorPolicy;
import org.optaplanner.core.impl.domain.variable.PlanningVariableDescriptor;
import org.optaplanner.core.impl.domain.variable.listener.PlanningVariableListener;
import org.optaplanner.core.impl.heuristic.selector.common.decorator.SelectionFilter;
import org.optaplanner.core.impl.heuristic.selector.common.decorator.SelectionSorterWeightFactory;

public class ShadowVariableDescriptor {

    private final PlanningEntityDescriptor entityDescriptor;

    private final PropertyAccessor variablePropertyAccessor;
    private String mappedBy;
    private PlanningVariableDescriptor mappedByVariableDescriptor;

    public ShadowVariableDescriptor(PlanningEntityDescriptor entityDescriptor,
            PropertyDescriptor propertyDescriptor) {
        this.entityDescriptor = entityDescriptor;
        variablePropertyAccessor = new ReflectionPropertyAccessor(propertyDescriptor);
    }

    public void processAnnotations(DescriptorPolicy descriptorPolicy) {
        processPropertyAnnotations(descriptorPolicy);
    }

    private void processPropertyAnnotations(DescriptorPolicy descriptorPolicy) {
        PlanningVariable planningVariableAnnotation = variablePropertyAccessor.getReadMethod()
                .getAnnotation(PlanningVariable.class);
        // Keep in sync with PlanningVariableDescriptor.processPropertyAnnotations()
        processMappedBy(descriptorPolicy, planningVariableAnnotation);
        processNullable(descriptorPolicy, planningVariableAnnotation);
        processChained(descriptorPolicy, planningVariableAnnotation);
        processValueRangeRefs(descriptorPolicy, planningVariableAnnotation);
        processStrength(descriptorPolicy, planningVariableAnnotation);
        processVariableListeners(descriptorPolicy, planningVariableAnnotation);
    }

    private void processMappedBy(DescriptorPolicy descriptorPolicy, PlanningVariable planningVariableAnnotation) {
        mappedBy = planningVariableAnnotation.mappedBy();
        if (mappedBy.equals("")) {
            throw new IllegalStateException("Impossible state: the " + PlanningEntityDescriptor.class
                    + " would never try to build a " + ShadowVariableDescriptor.class
                    + " for a non-shadow variable with mappedBy (" + mappedBy + ").");
        }
    }

    private void processNullable(DescriptorPolicy descriptorPolicy, PlanningVariable planningVariableAnnotation) {
        boolean nullable = planningVariableAnnotation.nullable();
        if (nullable) {
            throw new IllegalArgumentException("The planningEntityClass ("
                    + entityDescriptor.getPlanningEntityClass()
                    + ") has shadow PlanningVariable annotated property (" + variablePropertyAccessor.getName()
                    + ") with mappedBy (" + planningVariableAnnotation.mappedBy()
                    + ") which also has nullable (" + nullable + ").");
        }
        Class<? extends SelectionFilter> reinitializeVariableEntityFilterClass
                = planningVariableAnnotation.reinitializeVariableEntityFilter();
        if (reinitializeVariableEntityFilterClass != PlanningVariable.NullReinitializeVariableEntityFilter.class) {
            throw new IllegalArgumentException("The planningEntityClass ("
                    + entityDescriptor.getPlanningEntityClass()
                    + ") has shadow PlanningVariable annotated property (" + variablePropertyAccessor.getName()
                    + ") with mappedBy (" + planningVariableAnnotation.mappedBy()
                    + ") which also has reinitializeVariableEntityFilterClass ("
                    + reinitializeVariableEntityFilterClass + ").");
        }
    }

    private void processChained(DescriptorPolicy descriptorPolicy, PlanningVariable planningVariableAnnotation) {
        boolean chained = planningVariableAnnotation.chained();
        if (chained) {
            throw new IllegalArgumentException("The planningEntityClass ("
                    + entityDescriptor.getPlanningEntityClass()
                    + ") has shadow PlanningVariable annotated property (" + variablePropertyAccessor.getName()
                    + ") with mappedBy (" + planningVariableAnnotation.mappedBy()
                    + ") which also has chained (" + chained + ").");
        }
    }

    private void processValueRangeRefs(DescriptorPolicy descriptorPolicy, PlanningVariable planningVariableAnnotation) {
        String[] valueRangeRefs = planningVariableAnnotation.valueRangeProviderRefs();
        if (!ArrayUtils.isEmpty(valueRangeRefs)) {
            throw new IllegalArgumentException("The planningEntityClass ("
                    + entityDescriptor.getPlanningEntityClass()
                    + ") has shadow PlanningVariable annotated property (" + variablePropertyAccessor.getName()
                    + ") with mappedBy (" + planningVariableAnnotation.mappedBy()
                    + ") which has a non-empty valueRangeProviderRefs (" + Arrays.toString(valueRangeRefs) + ").");
        }
    }

    private void processStrength(DescriptorPolicy descriptorPolicy, PlanningVariable planningVariableAnnotation) {
        Class<? extends Comparator> strengthComparatorClass = planningVariableAnnotation.strengthComparatorClass();
        if (strengthComparatorClass != PlanningVariable.NullStrengthComparator.class) {
            throw new IllegalArgumentException("The planningEntityClass ("
                    + entityDescriptor.getPlanningEntityClass()
                    + ") has shadow PlanningVariable annotated property (" + variablePropertyAccessor.getName()
                    + ") with mappedBy (" + planningVariableAnnotation.mappedBy()
                    + ") which also has strengthComparatorClass (" + strengthComparatorClass + ").");
        }
        Class<? extends SelectionSorterWeightFactory> strengthWeightFactoryClass
                = planningVariableAnnotation.strengthWeightFactoryClass();
        if (strengthWeightFactoryClass != PlanningVariable.NullStrengthWeightFactory.class) {
            throw new IllegalArgumentException("The planningEntityClass ("
                    + entityDescriptor.getPlanningEntityClass()
                    + ") has shadow PlanningVariable annotated property (" + variablePropertyAccessor.getName()
                    + ") with mappedBy (" + planningVariableAnnotation.mappedBy()
                    + ") which also has strengthWeightFactoryClass (" + strengthWeightFactoryClass + ").");
        }
    }

    private void processVariableListeners(DescriptorPolicy descriptorPolicy, PlanningVariable planningVariableAnnotation) {
        Class<? extends PlanningVariableListener>[] variableListenerClasses
                = planningVariableAnnotation.variableListenerClasses();
        if (variableListenerClasses.length != 0) {
            throw new IllegalArgumentException("The planningEntityClass ("
                    + entityDescriptor.getPlanningEntityClass()
                    + ") has shadow PlanningVariable annotated property (" + variablePropertyAccessor.getName()
                    + ") with mappedBy (" + planningVariableAnnotation.mappedBy()
                    + ") which also has variableListenerClasses (" + variableListenerClasses + ").");
        }
    }

    public void afterAnnotationsProcessed(DescriptorPolicy descriptorPolicy) {
        Class<?> masterClass = getVariablePropertyType();
        PlanningEntityDescriptor mappedByEntityDescriptor = getEntityDescriptor().getSolutionDescriptor()
                .getEntityDescriptor(masterClass);
        if (mappedByEntityDescriptor == null) {
            throw new IllegalArgumentException("The planningEntityClass ("
                    + entityDescriptor.getPlanningEntityClass()
                    + ") has shadow PlanningVariable annotated property (" + variablePropertyAccessor.getName()
                    + ") with a masterClass (" + masterClass
                    + ") which is not a valid planning entity.");
        }
        mappedByVariableDescriptor = mappedByEntityDescriptor.getVariableDescriptor(mappedBy);
        if (mappedByVariableDescriptor == null) {
            throw new IllegalArgumentException("The planningEntityClass ("
                    + entityDescriptor.getPlanningEntityClass()
                    + ") has shadow PlanningVariable annotated property (" + variablePropertyAccessor.getName()
                    + ") with mappedBy (" + mappedBy
                    + ") which is not a valid planning variable on ("
                    + mappedByEntityDescriptor.getPlanningEntityClass() + ").");
        }
        if (!mappedByVariableDescriptor.isChained()) {
            throw new IllegalArgumentException("The planningEntityClass ("
                    + entityDescriptor.getPlanningEntityClass()
                    + ") has shadow PlanningVariable annotated property (" + variablePropertyAccessor.getName()
                    + ") with mappedBy (" + mappedBy
                    + ") which is not a valid planning variable on ("
                    + mappedByEntityDescriptor.getPlanningEntityClass() + ").");
        }
        mappedByVariableDescriptor.registerShadowVariableDescriptor(this);
    }

    public PlanningVariableDescriptor getMappedByVariableDescriptor() {
        return mappedByVariableDescriptor;
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    public PlanningEntityDescriptor getEntityDescriptor() {
        return entityDescriptor;
    }

    public String getVariableName() {
        return variablePropertyAccessor.getName();
    }

    public Class<?> getVariablePropertyType() {
        return variablePropertyAccessor.getPropertyType();
    }

    public PlanningVariableListener buildPlanningVariableListener() {
        return new ChainedMappedByVariableListener(this);
    }

    // ************************************************************************
    // Extraction methods
    // ************************************************************************

    public Object getValue(Object entity) {
        return variablePropertyAccessor.executeGetter(entity);
    }

    public void setValue(Object entity, Object value) {
        variablePropertyAccessor.executeSetter(entity, value);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + variablePropertyAccessor.getName()
                + " of " + entityDescriptor.getPlanningEntityClass().getName() + ")";
    }

}
