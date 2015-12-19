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

package org.optaplanner.core.impl.domain.variable.custom;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.optaplanner.core.api.domain.variable.CustomShadowVariable;
import org.optaplanner.core.api.domain.variable.PlanningVariableReference;
import org.optaplanner.core.config.util.ConfigUtils;
import org.optaplanner.core.impl.domain.common.accessor.MemberAccessor;
import org.optaplanner.core.impl.domain.entity.descriptor.EntityDescriptor;
import org.optaplanner.core.impl.domain.policy.DescriptorPolicy;
import org.optaplanner.core.impl.domain.variable.descriptor.ShadowVariableDescriptor;
import org.optaplanner.core.impl.domain.variable.descriptor.VariableDescriptor;
import org.optaplanner.core.impl.domain.variable.listener.VariableListener;
import org.optaplanner.core.impl.domain.variable.supply.Demand;
import org.optaplanner.core.impl.score.director.InnerScoreDirector;

public class CustomShadowVariableDescriptor extends ShadowVariableDescriptor {

    protected CustomShadowVariableDescriptor refVariableDescriptor;

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
        PlanningVariableReference variableListenerRef = shadowVariableAnnotation.variableListenerRef();
        if (variableListenerRef.variableName().equals("")) {
            variableListenerRef = null;
        }
        variableListenerClass = shadowVariableAnnotation.variableListenerClass();
        if (variableListenerClass == CustomShadowVariable.NullVariableListener.class) {
            variableListenerClass = null;
        }
        CustomShadowVariable.Source[] sources = shadowVariableAnnotation.sources();
        if (variableListenerRef != null) {
            if (variableListenerClass != null || sources.length > 0) {
                throw new IllegalArgumentException("The entityClass (" + entityDescriptor.getEntityClass()
                        + ") has a " + CustomShadowVariable.class.getSimpleName()
                        + " annotated property (" + variableMemberAccessor.getName()
                        + ") with a non-null variableListenerRef (" + variableListenerRef
                        + "), so it can not have a variableListenerClass (" + variableListenerClass
                        + ") nor any sources (" + Arrays.toString(sources) + ").");
            }
        } else {
            if (variableListenerClass == null) {
                throw new IllegalArgumentException("The entityClass (" + entityDescriptor.getEntityClass()
                        + ") has a " + CustomShadowVariable.class.getSimpleName()
                        + " annotated property (" + variableMemberAccessor.getName()
                        + ") which lacks a variableListenerClass (" + variableListenerClass + ").");
            }
            if (sources.length < 1) {
                throw new IllegalArgumentException("The entityClass (" + entityDescriptor.getEntityClass()
                        + ") has a " + CustomShadowVariable.class.getSimpleName()
                        + " annotated property (" + variableMemberAccessor.getName()
                        + ") with sources (" + Arrays.toString(sources)
                        + ") which is empty.");
            }
        }
    }

    public boolean isRef() {
        // refVariableDescriptor might not be initialized yet, but variableListenerClass will
        return variableListenerClass == null;
    }

    @Override
    public void linkShadowSources(DescriptorPolicy descriptorPolicy) {
        CustomShadowVariable shadowVariableAnnotation = variableMemberAccessor
                .getAnnotation(CustomShadowVariable.class);
        PlanningVariableReference variableListenerRef = shadowVariableAnnotation.variableListenerRef();
        if (variableListenerRef.variableName().equals("")) {
            variableListenerRef = null;
        }
        if (variableListenerRef != null) {
            EntityDescriptor refEntityDescriptor;
            Class<?> refEntityClass = variableListenerRef.entityClass();
            if (refEntityClass.equals(PlanningVariableReference.NullEntityClass.class)) {
                refEntityDescriptor = entityDescriptor;
            } else {
                refEntityDescriptor = entityDescriptor.getSolutionDescriptor().findEntityDescriptor(refEntityClass);
                if (refEntityDescriptor == null) {
                    throw new IllegalArgumentException("The entityClass (" + entityDescriptor.getEntityClass()
                            + ") has a " + CustomShadowVariable.class.getSimpleName()
                            + " annotated property (" + variableMemberAccessor.getName()
                            + ") with a refEntityClass (" + refEntityClass
                            + ") which is not a valid planning entity.");
                }
            }
            String refVariableName = variableListenerRef.variableName();
            VariableDescriptor uncastRefVariableDescriptor = refEntityDescriptor.getVariableDescriptor(refVariableName);
            if (uncastRefVariableDescriptor == null) {
                throw new IllegalArgumentException("The entityClass (" + entityDescriptor.getEntityClass()
                        + ") has a " + CustomShadowVariable.class.getSimpleName()
                        + " annotated property (" + variableMemberAccessor.getName()
                        + ") with refVariableName (" + refVariableName
                        + ") which is not a valid planning variable on entityClass ("
                        + refEntityDescriptor.getEntityClass() + ").\n"
                        + entityDescriptor.buildInvalidVariableNameExceptionMessage(refVariableName));
            }
            if (!(uncastRefVariableDescriptor instanceof CustomShadowVariableDescriptor)) {
                throw new IllegalArgumentException("The entityClass (" + entityDescriptor.getEntityClass()
                        + ") has a " + CustomShadowVariable.class.getSimpleName()
                        + " annotated property (" + variableMemberAccessor.getName()
                        + ") with refVariable (" + uncastRefVariableDescriptor.getSimpleEntityAndVariableName()
                        + ") that lacks a " + CustomShadowVariable.class.getSimpleName() + " annotation.");
            }
            refVariableDescriptor = (CustomShadowVariableDescriptor) uncastRefVariableDescriptor;
            if (refVariableDescriptor.isRef()) {
                throw new IllegalArgumentException("The entityClass (" + entityDescriptor.getEntityClass()
                        + ") has a " + CustomShadowVariable.class.getSimpleName()
                        + " annotated property (" + variableMemberAccessor.getName()
                        + ") with refVariable (" + refVariableDescriptor + ") that must not be a reference too.");
            }
            refVariableDescriptor.registerSinkVariableDescriptor(this);
        } else {
            CustomShadowVariable.Source[] sources = shadowVariableAnnotation.sources();
            sourceVariableDescriptorList = new ArrayList<VariableDescriptor>(sources.length);
            for (CustomShadowVariable.Source source : sources) {
                EntityDescriptor sourceEntityDescriptor;
                Class<?> sourceEntityClass = source.entityClass();
                if (sourceEntityClass.equals(CustomShadowVariable.Source.NullEntityClass.class)) {
                    sourceEntityDescriptor = entityDescriptor;
                } else {
                    sourceEntityDescriptor = entityDescriptor.getSolutionDescriptor()
                            .findEntityDescriptor(sourceEntityClass);
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
                sourceVariableDescriptor.registerSinkVariableDescriptor(this);
                sourceVariableDescriptorList.add(sourceVariableDescriptor);
            }
        }
    }

    @Override
    public List<VariableDescriptor> getSourceVariableDescriptorList() {
        if (refVariableDescriptor != null) {
            return Collections.<VariableDescriptor>singletonList(refVariableDescriptor);
        }
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
    public boolean hasVariableListener(InnerScoreDirector scoreDirector) {
        return refVariableDescriptor == null;
    }

    @Override
    public VariableListener buildVariableListener(InnerScoreDirector scoreDirector) {
        if (refVariableDescriptor != null) {
            throw new IllegalStateException("The shadowVariableDescriptor (" + this
                    + ") references another shadowVariableDescriptor (" + refVariableDescriptor
                    + ") so it cannot build a " + VariableListener.class.getSimpleName() + ".");
        }
        return ConfigUtils.newInstance(this, "variableListenerClass", variableListenerClass);
    }

}
