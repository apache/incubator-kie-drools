/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.domain.variable.AbstractVariableListener;
import org.optaplanner.core.api.domain.variable.InverseRelationShadowVariable;
import org.optaplanner.core.api.domain.variable.PlanningListVariable;
import org.optaplanner.core.api.domain.variable.PlanningVariable;
import org.optaplanner.core.api.domain.variable.PlanningVariableGraphType;
import org.optaplanner.core.config.util.ConfigUtils;
import org.optaplanner.core.impl.domain.common.accessor.MemberAccessor;
import org.optaplanner.core.impl.domain.entity.descriptor.EntityDescriptor;
import org.optaplanner.core.impl.domain.policy.DescriptorPolicy;
import org.optaplanner.core.impl.domain.variable.descriptor.GenuineVariableDescriptor;
import org.optaplanner.core.impl.domain.variable.descriptor.ListVariableDescriptor;
import org.optaplanner.core.impl.domain.variable.descriptor.ShadowVariableDescriptor;
import org.optaplanner.core.impl.domain.variable.descriptor.VariableDescriptor;
import org.optaplanner.core.impl.domain.variable.supply.Demand;
import org.optaplanner.core.impl.score.director.InnerScoreDirector;

/**
 * @param <Solution_> the solution type, the class with the {@link PlanningSolution} annotation
 */
public class InverseRelationShadowVariableDescriptor<Solution_> extends ShadowVariableDescriptor<Solution_> {

    protected VariableDescriptor<Solution_> sourceVariableDescriptor;
    protected boolean singleton;
    protected boolean chained;

    public InverseRelationShadowVariableDescriptor(EntityDescriptor<Solution_> entityDescriptor,
            MemberAccessor variableMemberAccessor) {
        super(entityDescriptor, variableMemberAccessor);
    }

    @Override
    public void processAnnotations(DescriptorPolicy descriptorPolicy) {
        // Do nothing
    }

    @Override
    public void linkVariableDescriptors(DescriptorPolicy descriptorPolicy) {
        linkShadowSources(descriptorPolicy);
    }

    /**
     * Sourced on a basic genuine planning variable, the shadow type is a Collection (such as List or Set).
     * Sourced on a list or chained planning variable, the shadow variable type is a single instance.
     *
     * @param descriptorPolicy descriptor policy
     */
    private void linkShadowSources(DescriptorPolicy descriptorPolicy) {
        InverseRelationShadowVariable shadowVariableAnnotation = variableMemberAccessor
                .getAnnotation(InverseRelationShadowVariable.class);
        Class<?> variablePropertyType = getVariablePropertyType();
        Class<?> sourceClass;
        if (Collection.class.isAssignableFrom(variablePropertyType)) {
            Type genericType = variableMemberAccessor.getGenericType();
            sourceClass = ConfigUtils.extractCollectionGenericTypeParameterLeniently(
                    "entityClass", entityDescriptor.getEntityClass(),
                    variablePropertyType, genericType,
                    InverseRelationShadowVariable.class, variableMemberAccessor.getName());
            singleton = false;
        } else {
            sourceClass = variablePropertyType;
            singleton = true;
        }
        EntityDescriptor<Solution_> sourceEntityDescriptor = getEntityDescriptor().getSolutionDescriptor()
                .findEntityDescriptor(sourceClass);
        if (sourceEntityDescriptor == null) {
            throw new IllegalArgumentException("The entityClass (" + entityDescriptor.getEntityClass()
                    + ") has an @" + InverseRelationShadowVariable.class.getSimpleName()
                    + " annotated property (" + variableMemberAccessor.getName()
                    + ") with a sourceClass (" + sourceClass
                    + ") which is not a valid planning entity.");
        }
        String sourceVariableName = shadowVariableAnnotation.sourceVariableName();
        // TODO can we getGenuineVariableDescriptor()?
        sourceVariableDescriptor = sourceEntityDescriptor.getVariableDescriptor(sourceVariableName);
        if (sourceVariableDescriptor == null) {
            throw new IllegalArgumentException("The entityClass (" + entityDescriptor.getEntityClass()
                    + ") has an @" + InverseRelationShadowVariable.class.getSimpleName()
                    + " annotated property (" + variableMemberAccessor.getName()
                    + ") with sourceVariableName (" + sourceVariableName
                    + ") which is not a valid planning variable on entityClass ("
                    + sourceEntityDescriptor.getEntityClass() + ").\n"
                    + sourceEntityDescriptor.buildInvalidVariableNameExceptionMessage(sourceVariableName));
        }
        chained = (sourceVariableDescriptor instanceof GenuineVariableDescriptor) &&
                ((GenuineVariableDescriptor<Solution_>) sourceVariableDescriptor).isChained();
        boolean list = (sourceVariableDescriptor instanceof GenuineVariableDescriptor) &&
                ((GenuineVariableDescriptor<Solution_>) sourceVariableDescriptor).isListVariable();
        if (singleton) {
            if (!chained && !list) {
                throw new IllegalArgumentException("The entityClass (" + entityDescriptor.getEntityClass()
                        + ") has an @" + InverseRelationShadowVariable.class.getSimpleName()
                        + " annotated property (" + variableMemberAccessor.getName()
                        + ") which does not return a " + Collection.class.getSimpleName()
                        + " with sourceVariableName (" + sourceVariableName
                        + ") which is neither a list variable @" + PlanningListVariable.class.getSimpleName()
                        + " nor a chained variable @" + PlanningVariable.class.getSimpleName()
                        + "(graphType=" + PlanningVariableGraphType.CHAINED + ")."
                        + " Only list and chained variables support a singleton inverse.");
            }
        } else {
            if (chained || list) {
                throw new IllegalArgumentException("The entityClass (" + entityDescriptor.getEntityClass()
                        + ") has an @" + InverseRelationShadowVariable.class.getSimpleName()
                        + " annotated property (" + variableMemberAccessor.getName()
                        + ") which returns a " + Collection.class.getSimpleName()
                        + " (" + variablePropertyType
                        + ") with sourceVariableName (" + sourceVariableName
                        + ") which is a" + (chained
                                ? " chained variable @" + PlanningVariable.class.getSimpleName()
                                        + "(graphType=" + PlanningVariableGraphType.CHAINED
                                        + "). A chained variable supports only a singleton inverse."
                                : " list variable @" + PlanningListVariable.class.getSimpleName()
                                        + ". A list variable supports only a singleton inverse."));
            }
        }
        sourceVariableDescriptor.registerSinkVariableDescriptor(this);
    }

    @Override
    public List<VariableDescriptor<Solution_>> getSourceVariableDescriptorList() {
        return Collections.singletonList(sourceVariableDescriptor);
    }

    @Override
    public Class<? extends AbstractVariableListener> getVariableListenerClass() {
        if (singleton) {
            if (chained) {
                return SingletonInverseVariableListener.class;
            } else {
                return SingletonListInverseVariableListener.class;
            }
        } else {
            return CollectionInverseVariableListener.class;
        }
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    @Override
    public Demand<?> getProvidedDemand() {
        if (singleton) {
            if (chained) {
                return new SingletonInverseVariableDemand<>(sourceVariableDescriptor);
            } else {
                return new SingletonListInverseVariableDemand<>(
                        (ListVariableDescriptor<Solution_>) sourceVariableDescriptor);
            }
        } else {
            return new CollectionInverseVariableDemand<>(sourceVariableDescriptor);
        }
    }

    @Override
    public AbstractVariableListener<Solution_, Object> buildVariableListener(InnerScoreDirector<Solution_, ?> scoreDirector) {
        if (singleton) {
            if (chained) {
                return new SingletonInverseVariableListener<>(this, sourceVariableDescriptor);
            } else {
                return new SingletonListInverseVariableListener<>(
                        this, (ListVariableDescriptor<Solution_>) sourceVariableDescriptor);
            }
        } else {
            return new CollectionInverseVariableListener<>(this, sourceVariableDescriptor);
        }
    }

}
