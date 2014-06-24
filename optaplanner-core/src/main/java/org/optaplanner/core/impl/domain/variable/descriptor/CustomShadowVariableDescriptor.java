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

package org.optaplanner.core.impl.domain.variable.descriptor;

import java.beans.PropertyDescriptor;
import java.util.Arrays;

import org.optaplanner.core.api.domain.variable.CustomShadowVariable;
import org.optaplanner.core.config.util.ConfigUtils;
import org.optaplanner.core.impl.domain.entity.descriptor.EntityDescriptor;
import org.optaplanner.core.impl.domain.policy.DescriptorPolicy;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.core.impl.domain.variable.listener.VariableListener;

public class CustomShadowVariableDescriptor extends ShadowVariableDescriptor {

    protected Class<? extends VariableListener> variableListenerClass;

    public CustomShadowVariableDescriptor(EntityDescriptor entityDescriptor,
            PropertyDescriptor propertyDescriptor) {
        super(entityDescriptor, propertyDescriptor);
    }

    public void processAnnotations(DescriptorPolicy descriptorPolicy) {
        processPropertyAnnotations(descriptorPolicy);
    }

    private void processPropertyAnnotations(DescriptorPolicy descriptorPolicy) {
        CustomShadowVariable shadowVariableAnnotation = variablePropertyAccessor.getReadMethod()
                .getAnnotation(CustomShadowVariable.class);
        variableListenerClass = shadowVariableAnnotation.variableListenerClass();
        CustomShadowVariable.Source[] sources = shadowVariableAnnotation.sources();
        if (sources.length < 1) {
            throw new IllegalArgumentException("The entityClass (" + entityDescriptor.getEntityClass()
                    + ") has a " + CustomShadowVariable.class.getSimpleName()
                    + " annotated property (" + variablePropertyAccessor.getName()
                    + ") with sources (" + Arrays.toString(sources)
                    + ") which is empty.");
        }
    }

    public void linkShadowSources(DescriptorPolicy descriptorPolicy) {
        CustomShadowVariable shadowVariableAnnotation = variablePropertyAccessor.getReadMethod()
                .getAnnotation(CustomShadowVariable.class);
        SolutionDescriptor solutionDescriptor = entityDescriptor.getSolutionDescriptor();
        CustomShadowVariable.Source[] sources = shadowVariableAnnotation.sources();
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
                            + " annotated property (" + variablePropertyAccessor.getName()
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
                        + " annotated property (" + variablePropertyAccessor.getName()
                        + ") with sourceVariableName (" + sourceVariableName
                        + ") which is not a valid planning variable on entityClass ("
                        + sourceEntityDescriptor.getEntityClass() + ").\n"
                        + entityDescriptor.buildInvalidVariableNameExceptionMessage(sourceVariableName));
            }
            sourceVariableDescriptor.registerShadowVariableDescriptor(this);
        }
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    @Override
    public VariableListener buildVariableListener() {
        return ConfigUtils.newInstance(this, "variableListenerClass", variableListenerClass);
    }

}
