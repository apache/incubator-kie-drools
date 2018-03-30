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

package org.optaplanner.core.impl.domain.variable.inverserelation;

import org.optaplanner.core.impl.domain.variable.descriptor.VariableDescriptor;
import org.optaplanner.core.impl.domain.variable.supply.Demand;
import org.optaplanner.core.impl.score.director.InnerScoreDirector;

/**
 * To get an instance, demand a {@link CollectionInverseVariableDemand} from {@link InnerScoreDirector#getSupplyManager()}.
 */
public class CollectionInverseVariableDemand implements Demand<CollectionInverseVariableSupply> {

    private static final int CLASS_NAME_HASH_CODE = CollectionInverseVariableDemand.class.getName().hashCode() * 37;

    protected final VariableDescriptor sourceVariableDescriptor;

    public CollectionInverseVariableDemand(VariableDescriptor sourceVariableDescriptor) {
        this.sourceVariableDescriptor = sourceVariableDescriptor;
    }

    public VariableDescriptor getSourceVariableDescriptor() {
        return sourceVariableDescriptor;
    }

    // ************************************************************************
    // Creation method
    // ************************************************************************

    @Override
    public CollectionInverseVariableSupply createExternalizedSupply(InnerScoreDirector scoreDirector) {
        return new ExternalizedCollectionInverseVariableSupply(sourceVariableDescriptor);
    }

    // ************************************************************************
    // Equals/hashCode method
    // ************************************************************************

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CollectionInverseVariableDemand)) {
            return false;
        }
        CollectionInverseVariableDemand other = (CollectionInverseVariableDemand) o;
        if (!sourceVariableDescriptor.equals(other.sourceVariableDescriptor)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        return CLASS_NAME_HASH_CODE + sourceVariableDescriptor.hashCode();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + sourceVariableDescriptor.getSimpleEntityAndVariableName() + ")";
    }

}
