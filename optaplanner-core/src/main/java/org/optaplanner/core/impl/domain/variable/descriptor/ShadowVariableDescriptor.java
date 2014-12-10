/*
 * Copyright 2013 JBoss Inc
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

import org.optaplanner.core.impl.domain.entity.descriptor.EntityDescriptor;
import org.optaplanner.core.impl.domain.policy.DescriptorPolicy;
import org.optaplanner.core.impl.domain.variable.listener.VariableListener;
import org.optaplanner.core.impl.domain.variable.supply.Demand;
import org.optaplanner.core.impl.domain.variable.supply.Supply;
import org.optaplanner.core.impl.score.director.InnerScoreDirector;

public abstract class ShadowVariableDescriptor extends VariableDescriptor {

    public ShadowVariableDescriptor(EntityDescriptor entityDescriptor,
            PropertyDescriptor propertyDescriptor) {
        super(entityDescriptor, propertyDescriptor);
    }

    public abstract void processAnnotations(DescriptorPolicy descriptorPolicy);

    public abstract void linkShadowSources(DescriptorPolicy descriptorPolicy);

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    /**
     * @return never null
     */
    public abstract Demand getProvidedDemand();

    /**
     * @param scoreDirector never null
     * @return never null
     */
    public abstract VariableListener buildVariableListener(InnerScoreDirector scoreDirector);

}
