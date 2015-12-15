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

package org.optaplanner.core.impl.domain.valuerange.descriptor;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.optaplanner.core.api.domain.valuerange.CountableValueRange;
import org.optaplanner.core.api.domain.valuerange.ValueRange;
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;
import org.optaplanner.core.api.domain.variable.InverseRelationShadowVariable;
import org.optaplanner.core.api.domain.variable.PlanningVariable;
import org.optaplanner.core.impl.domain.common.accessor.MemberAccessor;
import org.optaplanner.core.impl.domain.entity.descriptor.EntityDescriptor;
import org.optaplanner.core.impl.domain.valuerange.buildin.collection.ListValueRange;
import org.optaplanner.core.impl.domain.variable.descriptor.GenuineVariableDescriptor;

public abstract class AbstractFromPropertyValueRangeDescriptor extends AbstractValueRangeDescriptor {

    protected final MemberAccessor memberAccessor;
    protected boolean collectionWrapping;
    protected boolean countable;

    public AbstractFromPropertyValueRangeDescriptor(
            GenuineVariableDescriptor variableDescriptor, boolean addNullInValueRange,
            MemberAccessor memberAccessor) {
        super(variableDescriptor, addNullInValueRange);
        this.memberAccessor = memberAccessor;
        ValueRangeProvider valueRangeProviderAnnotation = memberAccessor.getAnnotation(ValueRangeProvider.class);
        if (valueRangeProviderAnnotation == null) {
            throw new IllegalStateException("The member (" + memberAccessor
                    + ") must have a valueRangeProviderAnnotation (" + valueRangeProviderAnnotation + ").");
        }
        processValueRangeProviderAnnotation(valueRangeProviderAnnotation);
        if (addNullInValueRange && !countable) {
            throw new IllegalStateException("The valueRangeDescriptor (" + this
                    + ") is nullable, but not countable (" + countable + ").\n"
                    + "Maybe the member (" + memberAccessor + ") should return "
                    + CountableValueRange.class.getSimpleName() + ".");
        }
    }

    private void processValueRangeProviderAnnotation(ValueRangeProvider valueRangeProviderAnnotation) {
        EntityDescriptor entityDescriptor = variableDescriptor.getEntityDescriptor();
        Class<?> type = memberAccessor.getType();
        collectionWrapping = Collection.class.isAssignableFrom(type);
        if (!collectionWrapping && !ValueRange.class.isAssignableFrom(type)) {
            throw new IllegalArgumentException("The entityClass (" + entityDescriptor.getEntityClass()
                    + ") has a " + PlanningVariable.class.getSimpleName()
                    + " annotated property (" + variableDescriptor.getVariableName()
                    + ") that refers to a " + ValueRangeProvider.class.getSimpleName()
                    + " annotated member (" + memberAccessor
                    + ") that does not return a " + Collection.class.getSimpleName()
                    + " or a " + ValueRange.class.getSimpleName() + ".");
        }
        if (collectionWrapping) {
            Type genericType = memberAccessor.getGenericType();
            // TODO PLANNER-383 As of 7.0, it should fail fast if a ValueRangeProvider's Collection is unparameterized
//            if (!(genericType instanceof ParameterizedType)) {
//                throw new IllegalArgumentException("The entityClass (" + entityDescriptor.getEntityClass()
//                        + ") has a " + PlanningVariable.class.getSimpleName()
//                        + " annotated property (" + variableDescriptor.getVariableName()
//                        + ") that refers to a " + ValueRangeProvider.class.getSimpleName()
//                        + " annotated member (" + memberAccessor
//                        + ") that returns a " + Collection.class.getSimpleName()
//                        + " which has no generic parameters.");
//            }
            if (genericType instanceof ParameterizedType) { // TODO PLANNER-383 remove this if statement
                ParameterizedType parameterizedType = (ParameterizedType) genericType;
                Type[] typeArguments = parameterizedType.getActualTypeArguments();
                if (typeArguments.length != 1) {
                    throw new IllegalArgumentException("The entityClass (" + entityDescriptor.getEntityClass()
                            + ") has a " + PlanningVariable.class.getSimpleName()
                            + " annotated property (" + variableDescriptor.getVariableName()
                            + ") that refers to a " + ValueRangeProvider.class.getSimpleName()
                            + " annotated member (" + memberAccessor
                            + ") that returns a parameterized " + Collection.class.getSimpleName()
                            + ") with an unsupported number of generic parameters (" + typeArguments.length + ").");
                }
                Type typeArgument = typeArguments[0];
                if (!(typeArgument instanceof Class)) {
                    throw new IllegalArgumentException("The entityClass (" + entityDescriptor.getEntityClass()
                            + ") has a " + PlanningVariable.class.getSimpleName()
                            + " annotated property (" + variableDescriptor.getVariableName()
                            + ") that refers to a " + ValueRangeProvider.class.getSimpleName()
                            + " annotated member (" + memberAccessor
                            + ") that returns a parameterized " + Collection.class.getSimpleName()
                            + " with an unsupported type arguments (" + typeArgument + ").");
                }
                Class collectionElementClass = ((Class) typeArgument);
                Class<?> variablePropertyType = variableDescriptor.getVariablePropertyType();
                if (!variablePropertyType.isAssignableFrom(collectionElementClass)) {
                    throw new IllegalArgumentException("The entityClass (" + entityDescriptor.getEntityClass()
                            + ") has a " + PlanningVariable.class.getSimpleName()
                            + " annotated property (" + variableDescriptor.getVariableName()
                            + ") that refers to a " + ValueRangeProvider.class.getSimpleName()
                            + " annotated member (" + memberAccessor
                            + ") that returns a " + Collection.class.getSimpleName()
                            + " with elements of type (" + collectionElementClass
                            + ") which cannot be assigned to the " + PlanningVariable.class.getSimpleName()
                            + "'s type (" + variablePropertyType + ").");
                }
            }
        }
        countable = collectionWrapping || CountableValueRange.class.isAssignableFrom(type);
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    @Override
    public boolean isCountable() {
        return countable;
    }

    protected ValueRange<?> readValueRange(Object bean) {
        Object valueRangeObject = memberAccessor.executeGetter(bean);
        if (valueRangeObject == null) {
            throw new IllegalStateException("The @" + ValueRangeProvider.class.getSimpleName()
                    + " annotated member (" + memberAccessor
                    + ") called on bean (" + bean
                    + ") must not return a null valueRangeObject (" + valueRangeObject + ").");
        }
        ValueRange<Object> valueRange;
        if (collectionWrapping) {
            List<Object> list = transformToList((Collection<Object>) valueRangeObject);
            valueRange = new ListValueRange<Object>(list);
        } else {
            valueRange = (ValueRange<Object>) valueRangeObject;
        }
        valueRange = doNullInValueRangeWrapping(valueRange);
        if (valueRange.isEmpty()) {
            throw new IllegalStateException("The @" + ValueRangeProvider.class.getSimpleName()
                    + " annotated member (" + memberAccessor
                    + ") called on bean (" + bean
                    + ") must not return an empty valueRange (" + valueRangeObject + ").\n"
                    + "  If this a valid dataset, apply overconstrained planning as described in the documentation.");
        }
        return valueRange;
    }

    private <T> List<T> transformToList(Collection<T> collection) {
        // TODO The user might not be aware of these performance pitfalls with Set and LinkedList:
        // - If only ValueRange.createOriginalIterator() is used, cloning a Set to a List is a waste of time.
        // - If the List is a LinkedList, ValueRange.createRandomIterator(Random)
        //   and ValueRange.get(int) are not efficient.
        return (collection instanceof List ? (List<T>) collection : new ArrayList<T>(collection));
    }

}
