/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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

    // TODO
    public static boolean isFEELBuiltInType(DMNType dmnType) {
        return dmnType.getNamespace().contains("FEEL");
    }

    public static boolean isFEELAny(DMNType dmnType) {
        return isFEELBuiltInType(dmnType) && getFEELType(dmnType) == BuiltInType.UNKNOWN;
    }

    private static Type getFEELType(DMNType dmnType) {
        return ((BaseDMNTypeImpl) dmnType).getFeelType();
    }

    public static boolean isInnerComposite(DMNType dmnType) {
        DMNType belonging = getBelongingType(dmnType);
        return belonging != null && dmnType.isComposite();
    }

    public static DMNType getBelongingType(DMNType dmnType) {
        return ((BaseDMNTypeImpl) dmnType).getBelongingType();
    }

}
