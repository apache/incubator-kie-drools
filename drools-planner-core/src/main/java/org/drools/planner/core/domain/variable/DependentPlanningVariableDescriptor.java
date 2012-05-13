/*
 * Copyright 2012 JBoss Inc
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

package org.drools.planner.core.domain.variable;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Comparator;

import org.drools.planner.api.domain.variable.DependentPlanningVariable;
import org.drools.planner.api.domain.variable.PlanningValueStrengthWeightFactory;
import org.drools.planner.api.domain.variable.PlanningVariable;
import org.drools.planner.core.domain.common.DescriptorUtils;
import org.drools.planner.core.domain.entity.PlanningEntityDescriptor;
import org.drools.planner.core.solution.Solution;

@Deprecated
public class DependentPlanningVariableDescriptor {

    private final PlanningEntityDescriptor planningEntityDescriptor;

    private final PropertyDescriptor variablePropertyDescriptor;

    private PlanningVariableDescriptor masterPlanningVariableDescriptor;

    public DependentPlanningVariableDescriptor(PlanningEntityDescriptor planningEntityDescriptor,
            PropertyDescriptor variablePropertyDescriptor) {
        this.planningEntityDescriptor = planningEntityDescriptor;
        this.variablePropertyDescriptor = variablePropertyDescriptor;
    }

    public void processAnnotations() {
        processPropertyAnnotations();
    }

    private void processPropertyAnnotations() {
        DependentPlanningVariable dependentPlanningVariableAnnotation = variablePropertyDescriptor.getReadMethod()
                .getAnnotation(DependentPlanningVariable.class);
        String masterPropertyName = dependentPlanningVariableAnnotation.master();
        masterPlanningVariableDescriptor = planningEntityDescriptor.getPlanningVariableDescriptor(masterPropertyName);
        if (masterPlanningVariableDescriptor == null) {
            throw new IllegalStateException("The planningEntityClass ("
                    + planningEntityDescriptor.getPlanningEntityClass()
                    + ") has a DependentPlanningVariable annotated property (" + variablePropertyDescriptor.getName()
                    + ") that refers to a masterPropertyName (" + masterPropertyName
                    + ") that does not exist on that class (" + planningEntityDescriptor.getPlanningEntityClass()
                    + ").");
        }
        masterPlanningVariableDescriptor.addDependentPlanningVariableDescriptor(this);
        String mappedByPropertyName = dependentPlanningVariableAnnotation.mappedBy();
        if (!mappedByPropertyName.equals("")) {
            Class<?> oppositeClass = variablePropertyDescriptor.getPropertyType();
            PlanningEntityDescriptor oppositePlanningEntityDescriptor
                    = planningEntityDescriptor.getSolutionDescriptor().getPlanningEntityDescriptor(oppositeClass);
            if (oppositePlanningEntityDescriptor == null) {
                throw new IllegalStateException("The planningEntityClass ("
                        + planningEntityDescriptor.getPlanningEntityClass()
                        + ") has a DependentPlanningVariable annotated property (" + variablePropertyDescriptor.getName()
                        + ") that refers to a mappedByPropertyName (" + mappedByPropertyName
                        + ") on the oppositeClass (" + oppositeClass
                        + ") that is not a PlanningEntity.");
            }
            PlanningVariableDescriptor mappedByPlanningVariableDescriptor
                    = oppositePlanningEntityDescriptor.getPlanningVariableDescriptor(mappedByPropertyName);
            if (mappedByPlanningVariableDescriptor == null) {
                throw new IllegalStateException("The planningEntityClass ("
                        + planningEntityDescriptor.getPlanningEntityClass()
                        + ") has a DependentPlanningVariable annotated property (" + variablePropertyDescriptor.getName()
                        + ") that refers to a mappedByPropertyName (" + mappedByPropertyName
                        + ") that is not a PlanningVariable on the oppositeClass (" + oppositeClass
                        + ").");
            }

            // TODO use mappedByPropertyName
        }

    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    public PlanningEntityDescriptor getPlanningEntityDescriptor() {
        return planningEntityDescriptor;
    }

    public String getVariablePropertyName() {
        return variablePropertyDescriptor.getName();
    }

}
