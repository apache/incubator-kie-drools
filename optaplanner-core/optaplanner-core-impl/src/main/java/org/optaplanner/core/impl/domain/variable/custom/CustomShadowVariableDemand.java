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

package org.optaplanner.core.impl.domain.variable.custom;

import java.util.Objects;

import org.optaplanner.core.impl.domain.variable.supply.Demand;
import org.optaplanner.core.impl.domain.variable.supply.Supply;
import org.optaplanner.core.impl.domain.variable.supply.SupplyManager;

/**
 * Unlike other {@link Demand}s, a custom demand isn't equalized based on its sources, but based on its target.
 * Therefore a custom shadow variable cannot be reused by built-in systems.
 */
public class CustomShadowVariableDemand<Solution_> implements Demand<CustomShadowVariableDemand.NoSupply> {

    private final CustomShadowVariableDescriptor<Solution_> targetShadowVariableDescriptor;

    public CustomShadowVariableDemand(CustomShadowVariableDescriptor<Solution_> targetShadowVariableDescriptor) {
        this.targetShadowVariableDescriptor = targetShadowVariableDescriptor;
    }

    // ************************************************************************
    // Creation method
    // ************************************************************************

    @Override
    public NoSupply createExternalizedSupply(SupplyManager supplyManager) {
        throw new IllegalArgumentException("A custom shadow variable cannot be externalized.");
    }

    // ************************************************************************
    // Equals/hashCode method
    // ************************************************************************

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CustomShadowVariableDemand)) {
            return false;
        }
        CustomShadowVariableDemand<Solution_> other = (CustomShadowVariableDemand<Solution_>) o;
        return targetShadowVariableDescriptor == other.targetShadowVariableDescriptor;
    }

    @Override
    public int hashCode() {
        return Objects.hash(CustomShadowVariableDemand.class.getName(), targetShadowVariableDescriptor);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }

    public interface NoSupply extends Supply {
    }
}
