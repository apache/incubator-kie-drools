/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.optaplanner.core.impl.heuristic.selector.value.decorator;

import java.util.Collections;
import java.util.Iterator;
import java.util.Objects;

import org.optaplanner.core.impl.domain.variable.descriptor.GenuineVariableDescriptor;
import org.optaplanner.core.impl.heuristic.selector.AbstractDemandEnabledSelector;
import org.optaplanner.core.impl.heuristic.selector.value.ValueSelector;

public final class DowncastingValueSelector<Solution_>
        extends AbstractDemandEnabledSelector<Solution_>
        implements ValueSelector<Solution_> {

    private final ValueSelector<Solution_> childValueSelector;
    private final Class<?> downcastEntityClass;

    public DowncastingValueSelector(ValueSelector<Solution_> childValueSelector, Class<?> downcastEntityClass) {
        this.childValueSelector = childValueSelector;
        this.downcastEntityClass = downcastEntityClass;
        phaseLifecycleSupport.addEventListener(childValueSelector);
    }

    public ValueSelector<Solution_> getChildValueSelector() {
        return childValueSelector;
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    @Override
    public GenuineVariableDescriptor<Solution_> getVariableDescriptor() {
        return childValueSelector.getVariableDescriptor();
    }

    @Override
    public boolean isCountable() {
        return childValueSelector.isCountable();
    }

    @Override
    public boolean isNeverEnding() {
        return childValueSelector.isNeverEnding();
    }

    @Override
    public long getSize(Object entity) {
        if (!downcastEntityClass.isInstance(entity)) {
            return 0L;
        }
        return childValueSelector.getSize(entity);
    }

    @Override
    public Iterator<Object> iterator(Object entity) {
        if (!downcastEntityClass.isInstance(entity)) {
            return Collections.emptyIterator();
        }
        return childValueSelector.iterator(entity);
    }

    @Override
    public Iterator<Object> endingIterator(Object entity) {
        if (!downcastEntityClass.isInstance(entity)) {
            return Collections.emptyIterator();
        }
        return childValueSelector.endingIterator(entity);
    }

    @Override
    public boolean equals(Object other) {
        if (this == other)
            return true;
        if (other == null || getClass() != other.getClass())
            return false;
        DowncastingValueSelector<?> that = (DowncastingValueSelector<?>) other;
        return Objects.equals(childValueSelector, that.childValueSelector)
                && Objects.equals(downcastEntityClass, that.downcastEntityClass);
    }

    @Override
    public int hashCode() {
        return Objects.hash(childValueSelector, downcastEntityClass);
    }

    @Override
    public String toString() {
        return "Downcasting(" + childValueSelector + ")";
    }

}
