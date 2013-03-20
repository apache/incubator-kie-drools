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

package org.optaplanner.core.impl.heuristic.selector.value;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.optaplanner.core.impl.domain.value.FromEntityPropertyPlanningValueRangeDescriptor;
import org.optaplanner.core.impl.domain.variable.PlanningVariableDescriptor;
import org.optaplanner.core.impl.heuristic.selector.common.SelectionCacheType;
import org.optaplanner.core.impl.heuristic.selector.common.iterator.CachedListRandomIterator;

/**
 * This is the common {@link ValueSelector} implementation.
 */
public class FromEntityPropertyValueSelector extends AbstractValueSelector {

    protected final PlanningVariableDescriptor variableDescriptor;
    protected final FromEntityPropertyPlanningValueRangeDescriptor valueRangeDescriptor;
    protected final boolean randomSelection;

    public FromEntityPropertyValueSelector(FromEntityPropertyPlanningValueRangeDescriptor valueRangeDescriptor,
            SelectionCacheType cacheType, boolean randomSelection) {
        this.variableDescriptor = valueRangeDescriptor.getVariableDescriptor();
        this.valueRangeDescriptor = valueRangeDescriptor;
        this.randomSelection = randomSelection;
        if (cacheType.isCached()) {
            throw new IllegalArgumentException("The selector (" + this
                    + ") does not support the cacheType (" + cacheType + ").");
        }
    }

    public PlanningVariableDescriptor getVariableDescriptor() {
        return variableDescriptor;
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    public boolean isContinuous() {
        return variableDescriptor.isContinuous();
    }

    public boolean isNeverEnding() {
        return randomSelection || isContinuous();
    }

    public long getSize(Object entity) {
        Collection<?> values = valueRangeDescriptor.extractValues(entity);
        long size = (long) values.size();
        if (variableDescriptor.isNullable()) {
            size++;
        }
        return size;
    }

    public Iterator<Object> iterator(Object entity) {
        Collection<Object> values = valueRangeDescriptor.extractValues(entity);
        List<Object> valueList = new ArrayList<Object>(values.size() + 1);
        valueList.addAll(values);
        if (variableDescriptor.isNullable()) {
            valueList.add(null);
        }
        if (!randomSelection) {
            return valueList.iterator();
        } else {
            return new CachedListRandomIterator<Object>(valueList, workingRandom);
        }
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + variableDescriptor.getVariableName() + ")";
    }

}
