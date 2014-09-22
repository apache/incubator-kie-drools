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

package org.optaplanner.core.impl.domain.valuerange.descriptor;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.optaplanner.core.api.domain.valuerange.CountableValueRange;
import org.optaplanner.core.api.domain.valuerange.ValueRange;
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;
import org.optaplanner.core.api.domain.variable.PlanningVariable;
import org.optaplanner.core.impl.domain.common.DefaultReadMethodAccessor;
import org.optaplanner.core.impl.domain.common.ReadMethodAccessor;
import org.optaplanner.core.impl.domain.entity.descriptor.EntityDescriptor;
import org.optaplanner.core.impl.domain.valuerange.buildin.collection.ListValueRange;
import org.optaplanner.core.impl.domain.variable.descriptor.GenuineVariableDescriptor;

public abstract class AbstractFromPropertyValueRangeDescriptor extends AbstractValueRangeDescriptor {

    protected final ReadMethodAccessor readMethodAccessor;
    protected boolean collectionWrapping;
    protected boolean countable;

    public AbstractFromPropertyValueRangeDescriptor(
            GenuineVariableDescriptor variableDescriptor, boolean addNullInValueRange,
            Method readMethod) {
        super(variableDescriptor, addNullInValueRange);
        readMethodAccessor = new DefaultReadMethodAccessor(readMethod);
        ValueRangeProvider valueRangeProviderAnnotation = readMethod.getAnnotation(ValueRangeProvider.class);
        if (valueRangeProviderAnnotation == null) {
            throw new IllegalStateException("The readMethod (" + readMethod
                    + ") must have a valueRangeProviderAnnotation (" + valueRangeProviderAnnotation + ").");
        }
        processValueRangeProviderAnnotation(valueRangeProviderAnnotation);
        if (addNullInValueRange && !countable) {
            throw new IllegalStateException("The valueRangeDescriptor (" + this
                    + ") is nullable, but not countable (" + countable + ").\n"
                    + "Maybe the readMethod (" + readMethod + ") should return "
                    + CountableValueRange.class.getSimpleName() + ".");
        }
    }

    private void processValueRangeProviderAnnotation(ValueRangeProvider valueRangeProviderAnnotation) {
        EntityDescriptor entityDescriptor = variableDescriptor.getEntityDescriptor();
        Class<?> returnType = readMethodAccessor.getReturnType();
        collectionWrapping = Collection.class.isAssignableFrom(returnType);
        if (!collectionWrapping && !ValueRange.class.isAssignableFrom(returnType)) {
            throw new IllegalArgumentException("The entityClass (" + entityDescriptor.getEntityClass()
                    + ") has a " + PlanningVariable.class.getSimpleName()
                    + " annotated property (" + variableDescriptor.getVariableName()
                    + ") that refers to a " + ValueRangeProvider.class.getSimpleName()
                    + " annotated method (" + readMethodAccessor.getReadMethod()
                    + ") that does not return a " + Collection.class.getSimpleName()
                    + " or a " + ValueRange.class.getSimpleName() + ".");
        }
        countable = collectionWrapping || CountableValueRange.class.isAssignableFrom(returnType);
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    @Override
    public boolean isCountable() {
        return countable;
    }

    protected ValueRange<?> readValueRange(Object bean) {
        Object valueRangeObject = readMethodAccessor.read(bean);
        ValueRange<Object> valueRange;
        if (collectionWrapping) {
            List<Object> list = transformToList((Collection<Object>) valueRangeObject);
            valueRange = new ListValueRange<Object>(list);
        } else {
            valueRange = (ValueRange<Object>) valueRangeObject;
        }
        return doNullInValueRangeWrapping(valueRange);
    }

    private <T> List<T> transformToList(Collection<T> collection) {
        // TODO The user might not be aware of these performance pitfalls with Set and LinkedList:
        // - If only ValueRange.createOriginalIterator() is used, cloning a Set to a List is a waste of time.
        // - If the List is a LinkedList, ValueRange.createRandomIterator(Random)
        //   and ValueRange.get(int) are not efficient.
        return (collection instanceof List ? (List<T>) collection : new ArrayList<T>(collection));
    }

}
