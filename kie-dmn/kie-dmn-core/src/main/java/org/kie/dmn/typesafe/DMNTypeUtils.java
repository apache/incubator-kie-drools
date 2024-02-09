/**
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
package org.kie.dmn.typesafe;

import org.kie.dmn.api.core.DMNType;
import org.kie.dmn.core.impl.BaseDMNTypeImpl;
import org.kie.dmn.feel.lang.Type;
import org.kie.dmn.feel.lang.types.BuiltInType;

/**
 * Internal utility class
 */
public class DMNTypeUtils {

    private DMNTypeUtils() {
        // only static method in this class.
    }

    public static boolean isFEELBuiltInType(DMNType dmnType) {
        return dmnType.getNamespace().equals(org.kie.dmn.model.v1_1.KieDMNModelInstrumentedBase.URI_FEEL) ||
               dmnType.getNamespace().equals(org.kie.dmn.model.v1_2.KieDMNModelInstrumentedBase.URI_FEEL) ||
               dmnType.getNamespace().equals(org.kie.dmn.model.v1_3.KieDMNModelInstrumentedBase.URI_FEEL) ||
               dmnType.getNamespace().equals(org.kie.dmn.model.v1_4.KieDMNModelInstrumentedBase.URI_FEEL) ||
               dmnType.getNamespace().equals(org.kie.dmn.model.v1_5.KieDMNModelInstrumentedBase.URI_FEEL)
               ;
    }

    public static boolean isFEELAny(DMNType dmnType) {
        return isFEELBuiltInType(dmnType) && getFEELType(dmnType) == BuiltInType.UNKNOWN;
    }

    private static Type getFEELType(DMNType dmnType) {
        return ((BaseDMNTypeImpl) dmnType).getFeelType();
    }

    public static BuiltInType getFEELBuiltInType(DMNType dmnType) {
        Type feelType = DMNTypeUtils.getFEELType(dmnType);
        BuiltInType builtin;
        try {
            builtin = (BuiltInType) feelType;
        } catch (ClassCastException e) {
            throw new IllegalArgumentException();
        }
        return builtin;
    }

    public static boolean isInnerComposite(DMNType dmnType) {
        DMNType belonging = getBelongingType(dmnType);
        return belonging != null && dmnType.isComposite();
    }

    public static DMNType genericOfCollection(DMNType dmnType) {
        if (!dmnType.isCollection()) {
            throw new IllegalArgumentException();
        }
        return dmnType.getBaseType() != null ? dmnType.getBaseType() : dmnType; // handling of anonymous inner composite collection
    }

    public static DMNType getRootBaseTypeOfCollection(DMNType dmnType) {
        if (dmnType.isCollection()) {
            if (dmnType.getBaseType() != null) {
                return getRootBaseTypeOfCollection(dmnType.getBaseType());
            } else {
                return dmnType;
            }
        } else {
            return dmnType.getBaseType() != null ? dmnType.getBaseType() : dmnType;
        }
    }

    public static DMNType getBelongingType(DMNType dmnType) {
        return ((BaseDMNTypeImpl) dmnType).getBelongingType();
    }
}
