/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
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

import java.util.List;

import org.optaplanner.core.impl.domain.common.accessor.MemberAccessor;
import org.optaplanner.core.impl.domain.entity.descriptor.EntityDescriptor;
import org.optaplanner.core.impl.domain.policy.DescriptorPolicy;
import org.optaplanner.core.impl.domain.variable.listener.VariableListener;
import org.optaplanner.core.impl.domain.variable.supply.Demand;
import org.optaplanner.core.impl.score.director.InnerScoreDirector;

public abstract class ShadowVariableDescriptor extends VariableDescriptor {

    private int globalShadowOrder = Integer.MAX_VALUE;

    public ShadowVariableDescriptor(EntityDescriptor entityDescriptor,
            MemberAccessor variableMemberAccessor) {
        super(entityDescriptor, variableMemberAccessor);
    }

    public abstract void processAnnotations(DescriptorPolicy descriptorPolicy);

    public abstract void linkShadowSources(DescriptorPolicy descriptorPolicy);

    /**
     * Inverse of {@link #getSinkVariableDescriptorList()}.
     * @return never null, only variables affect this shadow variable directly
     */
    public abstract List<VariableDescriptor> getSourceVariableDescriptorList();

    public int getGlobalShadowOrder() {
        return globalShadowOrder;
    }

    public void setGlobalShadowOrder(int globalShadowOrder) {
        this.globalShadowOrder = globalShadowOrder;
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    /**
     * @return never null
     */
    public abstract Demand getProvidedDemand();

    public boolean hasVariableListener(InnerScoreDirector scoreDirector) {
        return true;
    }

    /**
     * @param scoreDirector never null
     * @return never null
     */
    public abstract VariableListener buildVariableListener(InnerScoreDirector scoreDirector);

    @Override
    public String toString() {
        return getSimpleEntityAndVariableName() + " shadow";
    }

}
