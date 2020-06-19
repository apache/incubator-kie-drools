/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.core.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.impl.domain.entity.descriptor.EntityDescriptor;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.core.impl.domain.variable.descriptor.GenuineVariableDescriptor;

/**
 * A config class is a user friendly, validating configuration class that maps XML input.
 * It builds the runtime impl classes (which are optimized for scalability and performance instead).
 * <p>
 * A config class should adhere to "configuration by exception" in its XML/JSON input/output,
 * so all non-static fields should be null by default.
 * Using the config class to build a runtime class, must not alter the config class's XML/JSON output.
 *
 * @param <C> the same class as the implementing subclass
 */
@XmlAccessorType(XmlAccessType.FIELD)
public abstract class AbstractConfig<C extends AbstractConfig> {

    // ************************************************************************
    // Helper methods
    // ************************************************************************

    protected EntityDescriptor deduceEntityDescriptor(SolutionDescriptor solutionDescriptor,
            Class<?> entityClass) {
        EntityDescriptor entityDescriptor;
        if (entityClass != null) {
            entityDescriptor = solutionDescriptor.getEntityDescriptorStrict(entityClass);
            if (entityDescriptor == null) {
                throw new IllegalArgumentException("The config (" + this
                        + ") has an entityClass (" + entityClass + ") that is not a known planning entity.\n"
                        + "Check your solver configuration. If that class (" + entityClass.getSimpleName()
                        + ") is not in the entityClassSet (" + solutionDescriptor.getEntityClassSet()
                        + "), check your " + PlanningSolution.class.getSimpleName()
                        + " implementation's annotated methods too.");
            }
        } else {
            Collection<EntityDescriptor> entityDescriptors = solutionDescriptor.getGenuineEntityDescriptors();
            if (entityDescriptors.size() != 1) {
                throw new IllegalArgumentException("The config (" + this
                        + ") has no entityClass (" + entityClass
                        + ") configured and because there are multiple in the entityClassSet ("
                        + solutionDescriptor.getEntityClassSet()
                        + "), it can not be deduced automatically.");
            }
            entityDescriptor = entityDescriptors.iterator().next();
        }
        return entityDescriptor;
    }

    protected GenuineVariableDescriptor deduceVariableDescriptor(
            EntityDescriptor entityDescriptor, String variableName) {
        GenuineVariableDescriptor variableDescriptor;
        if (variableName != null) {
            variableDescriptor = entityDescriptor.getGenuineVariableDescriptor(variableName);
            if (variableDescriptor == null) {
                throw new IllegalArgumentException("The config (" + this
                        + ") has a variableName (" + variableName
                        + ") which is not a valid planning variable on entityClass ("
                        + entityDescriptor.getEntityClass() + ").\n"
                        + entityDescriptor.buildInvalidVariableNameExceptionMessage(variableName));
            }
        } else {
            Collection<GenuineVariableDescriptor> variableDescriptors = entityDescriptor
                    .getGenuineVariableDescriptors();
            if (variableDescriptors.size() != 1) {
                throw new IllegalArgumentException("The config (" + this
                        + ") has no configured variableName (" + variableName
                        + ") for entityClass (" + entityDescriptor.getEntityClass()
                        + ") and because there are multiple variableNames ("
                        + entityDescriptor.getGenuineVariableNameSet()
                        + "), it can not be deduced automatically.");
            }
            variableDescriptor = variableDescriptors.iterator().next();
        }
        return variableDescriptor;
    }

    protected List<GenuineVariableDescriptor> deduceVariableDescriptorList(
            EntityDescriptor entityDescriptor, List<String> variableNameIncludeList) {
        List<GenuineVariableDescriptor> variableDescriptorList = entityDescriptor.getGenuineVariableDescriptorList();
        if (variableNameIncludeList == null) {
            return variableDescriptorList;
        }
        List<GenuineVariableDescriptor> resolvedVariableDescriptorList = new ArrayList<>(variableDescriptorList.size());
        for (String variableNameInclude : variableNameIncludeList) {
            boolean found = false;
            for (GenuineVariableDescriptor variableDescriptor : variableDescriptorList) {
                if (variableDescriptor.getVariableName().equals(variableNameInclude)) {
                    resolvedVariableDescriptorList.add(variableDescriptor);
                    found = true;
                    break;
                }
            }
            if (!found) {
                throw new IllegalArgumentException("The config (" + this
                        + ") has a variableNameInclude (" + variableNameInclude
                        + ") which does not exist in the entity (" + entityDescriptor.getEntityClass()
                        + ")'s variableDescriptorList (" + variableDescriptorList + ").");
            }
        }
        return resolvedVariableDescriptorList;
    }

    // ************************************************************************
    // Other methods
    // ************************************************************************

    /**
     * Inherits each property of the {@code inheritedConfig} unless that property (or a semantic alternative)
     * is defined by this instance (which overwrites the inherited behaviour).
     * <p>
     * After the inheritance, if a property on this {@link AbstractConfig} composition is replaced,
     * it should not affect the inherited composition instance.
     *
     * @param inheritedConfig never null
     * @return this
     */
    public abstract C inherit(C inheritedConfig);

    /**
     * Typically implemented by constructing a new instance and calling {@link #inherit(AbstractConfig)} on it
     *
     * @return new instance
     */
    public abstract C copyConfig();

    @Override
    public String toString() {
        return getClass().getSimpleName() + "()";
    }

}
