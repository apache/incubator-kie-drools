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

import org.optaplanner.core.api.domain.variable.InverseRelationShadowVariable;
import org.optaplanner.core.impl.domain.entity.descriptor.EntityDescriptor;
import org.optaplanner.core.impl.domain.policy.DescriptorPolicy;
import org.optaplanner.core.impl.domain.variable.listener.InverseRelationVariableListener;
import org.optaplanner.core.impl.domain.variable.listener.VariableListener;

public class InverseRelationShadowVariableDescriptor extends ShadowVariableDescriptor {

    protected VariableDescriptor sourceVariableDescriptor;

    public InverseRelationShadowVariableDescriptor(EntityDescriptor entityDescriptor,
            PropertyDescriptor propertyDescriptor) {
        super(entityDescriptor, propertyDescriptor);
    }

    public void processAnnotations(DescriptorPolicy descriptorPolicy) {
        processPropertyAnnotations(descriptorPolicy);
    }

    private void processPropertyAnnotations(DescriptorPolicy descriptorPolicy) {

    }

    public void linkShadowSources(DescriptorPolicy descriptorPolicy) {
        InverseRelationShadowVariable shadowVariableAnnotation = variablePropertyAccessor.getReadMethod()
                .getAnnotation(InverseRelationShadowVariable.class);
        Class<?> masterClass = getVariablePropertyType();
        EntityDescriptor sourceEntityDescriptor = getEntityDescriptor().getSolutionDescriptor()
                .findEntityDescriptor(masterClass);
        if (sourceEntityDescriptor == null) {
            throw new IllegalArgumentException("The entityClass (" + entityDescriptor.getEntityClass()
                    + ") has a " + InverseRelationShadowVariable.class.getSimpleName()
                    + " annotated property (" + variablePropertyAccessor.getName()
                    + ") with a masterClass (" + masterClass
                    + ") which is not a valid planning entity.");
        }
        String sourceVariableName = shadowVariableAnnotation.sourceVariableName();
        sourceVariableDescriptor = sourceEntityDescriptor.getVariableDescriptor(sourceVariableName);
        if (sourceVariableDescriptor == null) {
            throw new IllegalArgumentException("The entityClass (" + entityDescriptor.getEntityClass()
                    + ") has a " + InverseRelationShadowVariable.class.getSimpleName()
                    + " annotated property (" + variablePropertyAccessor.getName()
                    + ") with sourceVariableName (" + sourceVariableName
                    + ") which is not a valid planning variable on entityClass ("
                    + sourceEntityDescriptor.getEntityClass() + ").\n"
                    + entityDescriptor.buildInvalidVariableNameExceptionMessage(sourceVariableName));
        }
        if (!(sourceVariableDescriptor instanceof GenuineVariableDescriptor) ||
                !((GenuineVariableDescriptor) sourceVariableDescriptor).isChained()) {
            // TODO support for non-chained variables too, including shadow variables
            throw new IllegalArgumentException("The entityClass (" + entityDescriptor.getEntityClass()
                    + ") has a " + InverseRelationShadowVariable.class.getSimpleName()
                    + " annotated property (" + variablePropertyAccessor.getName()
                    + ") with sourceVariableName (" + sourceVariableName
                    + ") which is not chained.");
        }
        sourceVariableDescriptor.registerShadowVariableDescriptor(this);
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    @Override
    public VariableListener buildVariableListener() {
        return new InverseRelationVariableListener(this, sourceVariableDescriptor);
    }

}
