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
package org.kie.kogito.dmn.util;

import java.lang.reflect.InvocationTargetException;

import org.kie.dmn.api.core.DMNResult;
import org.kie.dmn.api.core.FEELPropertyAccessible;

public class StronglyTypedUtils {

    private StronglyTypedUtils() {
    }

    public static FEELPropertyAccessible convertToOutputSet(FEELPropertyAccessible inputSet,
            Class<? extends FEELPropertyAccessible> outputSetClass) {
        FEELPropertyAccessible outputSet;
        try {
            outputSet = outputSetClass.getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
                | NoSuchMethodException | SecurityException e) {
            throw new RuntimeException(e);
        }
        outputSet.fromMap(inputSet.allFEELProperties());
        return outputSet;
    }

    public static FEELPropertyAccessible extractOutputSet(DMNResult result,
            Class<? extends FEELPropertyAccessible> outputSetClass) {
        FEELPropertyAccessible outputSet;
        try {
            outputSet = outputSetClass.getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
                | NoSuchMethodException | SecurityException e) {
            throw new RuntimeException(e);
        }
        outputSet.fromMap(result.getContext().getAll());
        return outputSet;
    }
}
