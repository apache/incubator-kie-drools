/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.core.impl.domain.variable.inverserelation;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.optaplanner.core.api.domain.variable.InverseRelationShadowVariable;
import org.optaplanner.core.impl.domain.common.accessor.MemberAccessor;
import org.optaplanner.core.impl.domain.entity.descriptor.EntityDescriptor;
import org.optaplanner.core.impl.domain.policy.DescriptorPolicy;
import org.optaplanner.core.impl.domain.variable.descriptor.GenuineVariableDescriptor;
import org.optaplanner.core.impl.domain.variable.descriptor.ShadowVariableDescriptor;
import org.optaplanner.core.impl.domain.variable.descriptor.VariableDescriptor;
import org.optaplanner.core.impl.domain.variable.listener.VariableListener;
import org.optaplanner.core.impl.domain.variable.supply.Demand;
import org.optaplanner.core.impl.score.director.InnerScoreDirector;

public class InverseRelationShadowVariableDescriptor extends ShadowVariableDescriptor {

    protected VariableDescriptor sourceVariableDescriptor;
    protected boolean singleton;

    public InverseRelationShadowVariableDescriptor(EntityDescriptor entityDescriptor,
            MemberAccessor variableMemberAccessor) {
        super(entityDescriptor, variableMemberAccessor);
    }

    public void processAnnotations(DescriptorPolicy descriptorPolicy) {
        processPropertyAnnotations(descriptorPolicy);
    }

    private void processPropertyAnnotations(DescriptorPolicy descriptorPolicy) {

    }

    public void linkShadowSources(DescriptorPolicy descriptorPolicy) {
        InverseRelationShadowVariable shadowVariableAnnotation = variableMemberAccessor
                .getAnnotation(InverseRelationShadowVariable.class);
        Class<?> variablePropertyType = getVariablePropertyType();
        Class<?> masterClass;
        if (Collection.class.isAssignableFrom(variablePropertyType)) {
            Type genericType = variableMemberAccessor.getGenericType();
            if (!(genericType instanceof ParameterizedType)) {
                throw new IllegalArgumentException("The entityClass (" + entityDescriptor.getEntityClass()
                        + ") has a " + InverseRelationShadowVariable.class.getSimpleName()
                        + " annotated property (" + variableMemberAccessor.getName()
                        + ") with a property type (" + variablePropertyType
                        + ") which is non parameterized collection.");
            }
            ParameterizedType parameterizedType = (ParameterizedType) genericType;
            Type[] typeArguments = parameterizedType.getActualTypeArguments();
            if (typeArguments.length != 1) {
                throw new IllegalArgumentException("The entityClass (" + entityDescriptor.getEntityClass()
                        + ") has a " + InverseRelationShadowVariable.class.getSimpleName()
                        + " annotated property (" + variableMemberAccessor.getName()
                        + ") with a property type (" + variablePropertyType
                        + ") which is parameterized collection with an unsupported number of type arguments ("
                        + typeArguments.length + ").");
            }
            Type typeArgument = typeArguments[0];
            if (!(typeArgument instanceof Class)) {
                throw new IllegalArgumentException("The entityClass (" + entityDescriptor.getEntityClass()
                        + ") has a " + InverseRelationShadowVariable.class.getSimpleName()
                        + " annotated property (" + variableMemberAccessor.getName()
                        + ") with a property type (" + variablePropertyType
                        + ") which is parameterized collection with an unsupported type arguments ("
                        + typeArgument + ").");
            }
            masterClass = ((Class) typeArgument);
            singleton = false;
        } else {
            masterClass = variablePropertyType;
            singleton = true;
        }
        EntityDescriptor sourceEntityDescriptor = getEntityDescriptor().getSolutionDescriptor()
                .findEntityDescriptor(masterClass);
        if (sourceEntityDescriptor == null) {
            throw new IllegalArgumentException("The entityClass (" + entityDescriptor.getEntityClass()
                    + ") has a " + InverseRelationShadowVariable.class.getSimpleName()
                    + " annotated property (" + variableMemberAccessor.getName()
                    + ") with a masterClass (" + masterClass
                    + ") which is not a valid planning entity.");
        }
        String sourceVariableName = shadowVariableAnnotation.sourceVariableName();
        sourceVariableDescriptor = sourceEntityDescriptor.getVariableDescriptor(sourceVariableName);
        if (sourceVariableDescriptor == null) {
            throw new IllegalArgumentException("The entityClass (" + entityDescriptor.getEntityClass()
                    + ") has a " + InverseRelationShadowVariable.class.getSimpleName()
                    + " annotated property (" + variableMemberAccessor.getName()
                    + ") with sourceVariableName (" + sourceVariableName
                    + ") which is not a valid planning variable on entityClass ("
                    + sourceEntityDescriptor.getEntityClass() + ").\n"
                    + entityDescriptor.buildInvalidVariableNameExceptionMessage(sourceVariableName));
        }
        boolean chained = (sourceVariableDescriptor instanceof GenuineVariableDescriptor) &&
                ((GenuineVariableDescriptor) sourceVariableDescriptor).isChained();
        if (singleton) {
            if (!chained) {
                throw new IllegalArgumentException("The entityClass (" + entityDescriptor.getEntityClass()
                        + ") has a " + InverseRelationShadowVariable.class.getSimpleName()
                        + " annotated property (" + variableMemberAccessor.getName()
                        + ") which does not return a " + Collection.class.getSimpleName()
                        + " with sourceVariableName (" + sourceVariableName
                        + ") which is not chained. Only a chained variable supports a singleton inverse.");
            }
        } else {
            if (chained) {
                throw new IllegalArgumentException("The entityClass (" + entityDescriptor.getEntityClass()
                        + ") has a " + InverseRelationShadowVariable.class.getSimpleName()
                        + " annotated property (" + variableMemberAccessor.getName()
                        + ") which does returns a " + Collection.class.getSimpleName()
                        + " with sourceVariableName (" + sourceVariableName
                        + ") which is chained. A chained variable supports only a singleton inverse.");
            }
        }
        sourceVariableDescriptor.registerSinkVariableDescriptor(this);
    }

    @Override
    public List<VariableDescriptor> getSourceVariableDescriptorList() {
        return Collections.singletonList(sourceVariableDescriptor);
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    @Override
    public Demand getProvidedDemand() {
        if (singleton) {
            return new SingletonInverseVariableDemand(sourceVariableDescriptor);
        } else {
            return new CollectionInverseVariableDemand(sourceVariableDescriptor);
        }
    }

    @Override
    public VariableListener buildVariableListener(InnerScoreDirector scoreDirector) {
        if (singleton) {
            return new SingletonInverseVariableListener(this, sourceVariableDescriptor);
        } else {
            return new CollectionInverseVariableListener(this, sourceVariableDescriptor);
        }
    }

}
