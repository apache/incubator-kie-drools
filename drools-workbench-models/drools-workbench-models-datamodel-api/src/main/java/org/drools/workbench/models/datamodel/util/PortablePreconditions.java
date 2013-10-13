/*
 * Copyright 2012 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.workbench.models.datamodel.util;

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
}
