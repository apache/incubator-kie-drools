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

package org.optaplanner.core.impl.domain.variable.anchor;

import java.util.Collections;
import java.util.List;

import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.domain.variable.AnchorShadowVariable;
import org.optaplanner.core.api.domain.variable.VariableListener;
import org.optaplanner.core.impl.domain.common.accessor.MemberAccessor;
import org.optaplanner.core.impl.domain.entity.descriptor.EntityDescriptor;
import org.optaplanner.core.impl.domain.policy.DescriptorPolicy;
import org.optaplanner.core.impl.domain.variable.descriptor.GenuineVariableDescriptor;
import org.optaplanner.core.impl.domain.variable.descriptor.ShadowVariableDescriptor;
import org.optaplanner.core.impl.domain.variable.descriptor.VariableDescriptor;
import org.optaplanner.core.impl.domain.variable.inverserelation.SingletonInverseVariableDemand;
import org.optaplanner.core.impl.domain.variable.inverserelation.SingletonInverseVariableSupply;
import org.optaplanner.core.impl.score.director.InnerScoreDirector;

/**
 * @param <Solution_> the solution type, the class with the {@link PlanningSolution} annotation
 */
public class AnchorShadowVariableDescriptor<Solution_> extends ShadowVariableDescriptor<Solution_> {

    protected VariableDescriptor<Solution_> sourceVariableDescriptor;

    public AnchorShadowVariableDescriptor(EntityDescriptor<Solution_> entityDescriptor,
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

    private void linkShadowSources(DescriptorPolicy descriptorPolicy) {
        AnchorShadowVariable shadowVariableAnnotation = variableMemberAccessor.getAnnotation(AnchorShadowVariable.class);
        String sourceVariableName = shadowVariableAnnotation.sourceVariableName();
        sourceVariableDescriptor = entityDescriptor.getVariableDescriptor(sourceVariableName);
        if (sourceVariableDescriptor == null) {
            throw new IllegalArgumentException("The entityClass (" + entityDescriptor.getEntityClass()
                    + ") has an @" + AnchorShadowVariable.class.getSimpleName()
                    + " annotated property (" + variableMemberAccessor.getName()
                    + ") with sourceVariableName (" + sourceVariableName
                    + ") which is not a valid planning variable on entityClass ("
                    + entityDescriptor.getEntityClass() + ").\n"
                    + entityDescriptor.buildInvalidVariableNameExceptionMessage(sourceVariableName));
        }
        if (!(sourceVariableDescriptor instanceof GenuineVariableDescriptor) ||
                !((GenuineVariableDescriptor<Solution_>) sourceVariableDescriptor).isChained()) {
            throw new IllegalArgumentException("The entityClass (" + entityDescriptor.getEntityClass()
                    + ") has an @" + AnchorShadowVariable.class.getSimpleName()
                    + " annotated property (" + variableMemberAccessor.getName()
                    + ") with sourceVariableName (" + sourceVariableName
                    + ") which is not chained.");
        }
        sourceVariableDescriptor.registerSinkVariableDescriptor(this);
    }

    @Override
    public List<VariableDescriptor<Solution_>> getSourceVariableDescriptorList() {
        return Collections.singletonList(sourceVariableDescriptor);
    }

    @Override
    public Class<? extends VariableListener> getVariableListenerClass() {
        return AnchorVariableListener.class;
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    @Override
    public AnchorVariableDemand<Solution_> getProvidedDemand() {
        return new AnchorVariableDemand<>(sourceVariableDescriptor);
    }

    @Override
    public AnchorVariableListener<Solution_> buildVariableListener(InnerScoreDirector<Solution_, ?> scoreDirector) {
        SingletonInverseVariableSupply inverseVariableSupply = scoreDirector.getSupplyManager()
                .demand(new SingletonInverseVariableDemand<>(sourceVariableDescriptor));
        return new AnchorVariableListener<>(this, sourceVariableDescriptor, inverseVariableSupply);
    }

}
