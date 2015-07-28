/*
 * Copyright 2014 JBoss Inc
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

package org.optaplanner.core.impl.domain.variable.custom;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.optaplanner.core.api.domain.variable.CustomShadowVariable;
import org.optaplanner.core.config.util.ConfigUtils;
import org.optaplanner.core.impl.domain.common.accessor.MemberAccessor;
import org.optaplanner.core.impl.domain.entity.descriptor.EntityDescriptor;
import org.optaplanner.core.impl.domain.policy.DescriptorPolicy;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.core.impl.domain.variable.descriptor.ShadowVariableDescriptor;
import org.optaplanner.core.impl.domain.variable.descriptor.VariableDescriptor;
import org.optaplanner.core.impl.domain.variable.listener.VariableListener;
import org.optaplanner.core.impl.domain.variable.supply.Demand;
import org.optaplanner.core.impl.score.director.InnerScoreDirector;

public class CustomShadowVariableDescriptor extends ShadowVariableDescriptor {

    protected Class<? extends VariableListener> variableListenerClass;
    protected List<VariableDescriptor> sourceVariableDescriptorList;

    public CustomShadowVariableDescriptor(EntityDescriptor entityDescriptor,
            MemberAccessor variableMemberAccessor) {
        super(entityDescriptor, variableMemberAccessor);
    }

    public void processAnnotations(DescriptorPolicy descriptorPolicy) {
        processPropertyAnnotations(descriptorPolicy);
    }

    private void processPropertyAnnotations(DescriptorPolicy descriptorPolicy) {
        CustomShadowVariable shadowVariableAnnotation = variableMemberAccessor
                .getAnnotation(CustomShadowVariable.class);
        variableListenerClass = shadowVariableAnnotation.variableListenerClass();
        CustomShadowVariable.Source[] sources = shadowVariableAnnotation.sources();
        if (sources.length < 1) {
            throw new IllegalArgumentException("The entityClass (" + entityDescriptor.getEntityClass()
                    + ") has a " + CustomShadowVariable.class.getSimpleName()
                    + " annotated property (" + variableMemberAccessor.getName()
                    + ") with sources (" + Arrays.toString(sources)
                    + ") which is empty.");
        }
    }

    public void linkShadowSources(DescriptorPolicy descriptorPolicy) {
        CustomShadowVariable shadowVariableAnnotation = variableMemberAccessor
                .getAnnotation(CustomShadowVariable.class);
        SolutionDescriptor solutionDescriptor = entityDescriptor.getSolutionDescriptor();
        CustomShadowVariable.Source[] sources = shadowVariableAnnotation.sources();
        sourceVariableDescriptorList = new ArrayList<VariableDescriptor>(sources.length);
        for (CustomShadowVariable.Source source : sources) {
            EntityDescriptor sourceEntityDescriptor;
            Class<?> sourceEntityClass = source.entityClass();
            if (sourceEntityClass.equals(CustomShadowVariable.Source.NullEntityClass.class)) {
                sourceEntityDescriptor = entityDescriptor;
            } else {
                sourceEntityDescriptor = solutionDescriptor.findEntityDescriptor(sourceEntityClass);
                if (sourceEntityDescriptor == null) {
                    throw new IllegalArgumentException("The entityClass (" + entityDescriptor.getEntityClass()
                            + ") has a " + CustomShadowVariable.class.getSimpleName()
                            + " annotated property (" + variableMemberAccessor.getName()
                            + ") with a sourceEntityClass (" + sourceEntityClass
                            + ") which is not a valid planning entity.");
                }
            }
            String sourceVariableName = source.variableName();
            VariableDescriptor sourceVariableDescriptor = sourceEntityDescriptor.getVariableDescriptor(
                    sourceVariableName);
            if (sourceVariableDescriptor == null) {
                throw new IllegalArgumentException("The entityClass (" + entityDescriptor.getEntityClass()
                        + ") has a " + CustomShadowVariable.class.getSimpleName()
                        + " annotated property (" + variableMemberAccessor.getName()
                        + ") with sourceVariableName (" + sourceVariableName
                        + ") which is not a valid planning variable on entityClass ("
                        + sourceEntityDescriptor.getEntityClass() + ").\n"
                        + entityDescriptor.buildInvalidVariableNameExceptionMessage(sourceVariableName));
            }
            sourceVariableDescriptor.registerShadowVariableDescriptor(this);
            sourceVariableDescriptorList.add(sourceVariableDescriptor);
        }
    }

    @Override
    public List<VariableDescriptor> getSourceVariableDescriptorList() {
        return sourceVariableDescriptorList;
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    @Override
    public Demand getProvidedDemand() {
        return new CustomShadowVariableDemand(this);
    }

    @Override
    public VariableListener buildVariableListener(InnerScoreDirector scoreDirector) {
        return ConfigUtils.newInstance(this, "variableListenerClass", variableListenerClass);
    }

}
