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
import java.util.Collection;
import java.util.Optional;

import org.kie.dmn.api.core.DMNType;
import org.kie.dmn.feel.lang.types.BuiltInType;

/**
 *  Class used to centralize all coercion-related behavior
 */
public class CoerceUtil {

    private CoerceUtil() {
        // singleton class
    }

    public static Optional<Object[]> coerceParams(Class<?> currentIdxActualParameterType, Class<?> expectedParameterType, Object[] actualParams, int i) {
        Object actualObject = actualParams[i];
        // singleton list spec defines that "a=[a]", i.e., singleton collections should be treated as the single element
        // and vice-versa
        if ( Collection.class.isAssignableFrom( currentIdxActualParameterType ) ) {
            Collection<?> valueCollection = (Collection<?>) actualObject;
            if ( valueCollection.size() == 1 ) {
                Object singletonValue = valueCollection.iterator().next();
                // re-perform the assignable-from check, this time using the element itself the singleton value from the original parameter list
                if ( singletonValue != null && expectedParameterType.isAssignableFrom( singletonValue.getClass() ) ) {
                    return actualCoerceParams(actualParams, singletonValue, i);
                }
            }
        }
        if (actualObject instanceof LocalDate localDate &&
                ZonedDateTime.class.isAssignableFrom(expectedParameterType)) {
            Object coercedObject = EvalHelper.coerceDateTime(localDate);
            return actualCoerceParams(actualParams, coercedObject, i);
        }
        return Optional.empty();
    }

    static Optional<Object[]> actualCoerceParams(Object[] actualParams, Object coercedObject, int i) {
        Object[] newParams = new Object[actualParams.length];
        System.arraycopy( actualParams, 0, newParams, 0, actualParams.length ); // can't rely on adjustForVariableParameters() have actually copied
        newParams[i] = coercedObject;
        return Optional.of(newParams);
    }


}