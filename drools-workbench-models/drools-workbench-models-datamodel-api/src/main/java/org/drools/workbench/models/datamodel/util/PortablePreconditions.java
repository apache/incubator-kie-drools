/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.drools.workbench.models.datamodel.util;

import java.util.Collection;
import java.util.Map;

/**
 * Helper class for parameters validation, such as not null arguments.
 */
public class PortablePreconditions {

    /**
     * Should not be instantiated
     */
    protected PortablePreconditions() {
        throw new IllegalStateException("This class should be not instantiated!");
    }

    /**
     * Assert that this parameter is marked as valid by the condition passed as parameter.
     * @param name of parameter
     * @param condition itself
     */
    public static void checkCondition(final String name, final boolean condition) {
        if (!condition) {
            throw new IllegalStateException("Condition '" + name + "' is invalid!");
        }
    }

    /**
     * Assert that this parameter is not null, as also each item of the array is not null.
     * @param <T> parameter type
     * @param name of parameter
     * @param parameters itself
     */
    public static <T> void checkEachParameterNotNull(final String name, final T... parameters) {
        if (parameters == null) {
            throw new IllegalArgumentException("Parameter named '" + name + "' should be not null!");
        }
        for (final Object parameter : parameters) {
            if (parameter == null) {
                throw new IllegalArgumentException("Parameter named '" + name + "' should be not null!");

            }
        }

    }

    /**
     * Assert that this parameter is not empty. It will test for null and also the size of this array.
     * @param name of parameter
     * @param parameter itself
     */
    public static <T extends Collection<?>> T checkNotEmpty(final String name, final T parameter) {
        if (parameter == null || parameter.size() == 0) {
            throw new IllegalArgumentException("Parameter named '" + name + "' should be filled!");
        }
        return parameter;
    }

    /**
     * Assert that this parameter is not empty. It will test for null and also the size of this array.
     * @param name of parameter
     * @param parameter itself
     */
    public static void checkNotEmpty(final String name, final Map<?, ?> parameter) {
        if (parameter == null || parameter.size() == 0) {
            throw new IllegalArgumentException("Parameter named '" + name + "' should be filled!");
        }
    }

    /**
     * Assert that this parameter is not empty. It trims the parameter to see if have any valid data on that.
     * @param name of parameter
     * @param parameter itself
     */
    public static String checkNotEmpty(final String name, final String parameter) {
        if (parameter == null || parameter.trim().length() == 0) {
            throw new IllegalArgumentException("Parameter named '" + name + "' should be filled!");
        }
        return parameter;
    }

    /**
     * Assert that this parameter is not empty. It will test for null and also the size of this array.
     * @param <T> type of the array
     * @param name of parameter
     * @param parameter itself
     */
    public static <T> T[] checkNotEmpty(final String name, final T[] parameter) {
        if (parameter == null || parameter.length == 0) {
            throw new IllegalArgumentException("Parameter named '" + name + "' should be filled!");
        }
        return parameter;
    }

    /**
     * Assert that this parameter is not null.
     * @param name of parameter
     * @param parameter itself
     */
    public static <T> T checkNotNull(final String name, final T parameter) {
        if (parameter == null) {
            throw new IllegalArgumentException("Parameter named '" + name + "' should be not null!");
        }
        return parameter;
    }

    /**
     * Assert that this parameter is null.
     * @param name of parameter
     * @param parameter itself
     */
    public static void checkNullMandatory(final String name, final Object parameter) {
        if (parameter != null) {
            throw new IllegalArgumentException("Parameter named '" + name + "' should be null!");
        }
    }

}
