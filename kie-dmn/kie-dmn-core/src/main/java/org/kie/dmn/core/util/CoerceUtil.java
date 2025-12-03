/*
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
package org.kie.dmn.core.util;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Collections;

import org.kie.dmn.api.core.DMNType;
import org.kie.dmn.core.impl.SimpleTypeImpl;
import org.kie.dmn.feel.lang.types.BuiltInType;
import org.kie.dmn.feel.util.DateTimeEvalHelper;

/**
 *  Class used to centralize all coercion-related behavior
 */
public class CoerceUtil {

    private CoerceUtil() {
        // singleton class
    }

    public static Object coerceValue(DMNType requiredType, Object valueToCoerce) {
        return (requiredType != null && valueToCoerce != null) ? actualCoerceValue(requiredType, valueToCoerce) :
                valueToCoerce;
    }

    static Object actualCoerceValue(DMNType requiredType, Object valueToCoerce) {
        Object toReturn = valueToCoerce;
        if (!requiredType.isCollection() && valueToCoerce instanceof Collection &&
                ((Collection) valueToCoerce).size() == 1) {
            // spec defines that "a=[a]", i.e., singleton collections should be treated as the single element
            // and vice-versa
            return ((Collection) valueToCoerce).toArray()[0];
        }
        if (requiredType.isCollection() && !(valueToCoerce instanceof Collection) &&
                (!(requiredType instanceof SimpleTypeImpl simpleType)
                || simpleType.getFeelType() != BuiltInType.UNKNOWN)) {
            return Collections.singletonList(valueToCoerce);
        }
        if (valueToCoerce instanceof LocalDate localDate &&
                requiredType instanceof SimpleTypeImpl simpleType &&
                simpleType.getFeelType() == BuiltInType.DATE_TIME) {
            return DateTimeEvalHelper.coerceDateTime(localDate);
        }
        return toReturn;
    }
}