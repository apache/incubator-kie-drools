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

package org.optaplanner.core.impl.domain.variable.anchor;

import java.util.Objects;

import org.optaplanner.core.impl.domain.variable.descriptor.VariableDescriptor;
import org.optaplanner.core.impl.domain.variable.inverserelation.SingletonInverseVariableDemand;
import org.optaplanner.core.impl.domain.variable.inverserelation.SingletonInverseVariableSupply;
import org.optaplanner.core.impl.domain.variable.supply.Demand;
import org.optaplanner.core.impl.score.director.InnerScoreDirector;

public class AnchorVariableDemand implements Demand<AnchorVariableSupply> {

    protected final VariableDescriptor sourceVariableDescriptor;

    public AnchorVariableDemand(VariableDescriptor sourceVariableDescriptor) {
        this.sourceVariableDescriptor = sourceVariableDescriptor;
    }

    public VariableDescriptor getSourceVariableDescriptor() {
        return sourceVariableDescriptor;
    }

    // ************************************************************************
    // Creation method
    // ************************************************************************

    @Override
    public AnchorVariableSupply createExternalizedSupply(InnerScoreDirector scoreDirector) {
        SingletonInverseVariableSupply inverseVariableSupply = scoreDirector.getSupplyManager()
                .demand(new SingletonInverseVariableDemand(sourceVariableDescriptor));
        return new ExternalizedAnchorVariableSupply(sourceVariableDescriptor, inverseVariableSupply);
    }

    // ************************************************************************
    // Equals/hashCode method
    // ************************************************************************

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AnchorVariableDemand)) {
            return false;
        }
        AnchorVariableDemand other = (AnchorVariableDemand) o;
        if (!sourceVariableDescriptor.equals(other.sourceVariableDescriptor)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hash(AnchorVariableDemand.class.getName(), sourceVariableDescriptor);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + sourceVariableDescriptor.getSimpleEntityAndVariableName() + ")";
    }

}
