/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.kie.dmn.feel.util;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.kie.dmn.feel.lang.Type;
import org.kie.dmn.feel.lang.types.BuiltInType;

/**
 *  Class used to centralize all coercion-related behavior
 */
public class CoerceUtil {

    private CoerceUtil() {
        // singleton class
    }

    public static Object coerceParameter(Type requiredType, Object valueToCoerce) {
        return (requiredType != null && valueToCoerce != null) ? actualCoerceParameter(requiredType, valueToCoerce) :
                valueToCoerce;
    }

    public static Optional<Object[]> coerceParams(Class<?> currentIdxActualParameterType, Class<?> expectedParameterType, Object[] actualParams, int i) {
        Object actualObject = actualParams[i];
        Optional<Object> coercedObject = coerceParam(currentIdxActualParameterType, expectedParameterType,
                                                     actualObject);
        return coercedObject.map(o -> actualCoerceParams(actualParams, o, i));
    }

    static Optional<Object> coerceParam(Class<?> currentIdxActualParameterType, Class<?> expectedParameterType,
                                        Object actualObject) {
        /* 10.3.2.9.4 Type conversions
           from singleton list:
           When the type of the expression is List<T>, the value of the expression is a singleton list and the target
           type is T, the expression is converted by unwrapping the first element. */
        if (Collection.class.isAssignableFrom(currentIdxActualParameterType)) {
            Collection<?> valueCollection = (Collection<?>) actualObject;
            if (valueCollection.size() == 1) {
                Object singletonValue = valueCollection.iterator().next();
                // re-perform the assignable-from check, this time using the element itself the singleton value from
                // the original parameter list
                if (singletonValue != null) {
                    return expectedParameterType.isAssignableFrom(singletonValue.getClass()) ?
                            Optional.of(singletonValue) :
                            coerceParam(singletonValue.getClass(), expectedParameterType, singletonValue);
                }
            }
        }
        /* to singleton list:
           When the type of the expression is T and the target type is List<T> the expression is converted to a
           singleton list. */
        if (!Collection.class.isAssignableFrom(currentIdxActualParameterType) &&
                Collection.class.isAssignableFrom(expectedParameterType)) {
            return Optional.of(new ArrayList<>(List.of(actualObject)));
        }
        if (actualObject instanceof LocalDate localDate &&
                ZonedDateTime.class.isAssignableFrom(expectedParameterType)) {
            Object coercedObject = DateTimeEvalHelper.coerceDateTime(localDate);
            return Optional.of(coercedObject);
        }
        return Optional.empty();
    }

    static Object actualCoerceParameter(Type requiredType, Object valueToCoerce) {
        Object toReturn = valueToCoerce;
        if (valueToCoerce instanceof LocalDate localDate &&
                requiredType == BuiltInType.DATE_TIME) {
            return DateTimeEvalHelper.coerceDateTime(localDate);
        }
        return toReturn;
    }

    static Object[] actualCoerceParams(Object[] actualParams, Object coercedObject, int i) {
        Object[] toReturn = new Object[actualParams.length];
        System.arraycopy( actualParams, 0, toReturn, 0, actualParams.length );
        toReturn[i] = coercedObject;
        return toReturn;
    }


}