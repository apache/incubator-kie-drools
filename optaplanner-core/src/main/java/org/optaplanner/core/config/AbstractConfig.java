/*
 * Copyright 2015 JBoss Inc
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

package org.optaplanner.core.config;

/**
 * A config class is a user friendly, validating configuration class that maps XML input.
 * It builds the runtime impl classes (which are optimized for scalability and performance instead).
 * <p>
 * A config class should adhere to "configuration by exception" in its XML/JSON input/output,
 * so all non-static fields should be null by default.
 * Using the config class to build a runtime class, must not alter the config class's XML/JSON output.
 * @param <C> the same class as the implementing subclass
 */
public abstract class AbstractConfig<C extends AbstractConfig> {

    /**
     * @return never null
     */
    public C newInstance() {
        Class<C> configClass = (Class<C>) getClass();
        try {
            return configClass.newInstance();
        } catch (InstantiationException e) {
            throw new IllegalStateException("The configClass (" + configClass
                    + ") does not have a public no-arg constructor.\n"
                    + "This is a bug, please report an issue with this stacktrace.", e);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException("The configClass (" + configClass
                    + ") does not have a public no-arg constructor.\n"
                    + "This is a bug, please report an issue with this stacktrace.", e);
        }
    }

    /**
     * Inherits each property of the {@code inheritedConfig} unless that property (or a semantic alternative)
     * is defined by this instance (which overwrites the inherited behaviour).
     * <p>
     * After the inheritance, if a property on this {@link AbstractConfig} composition is replaced,
     * it should not affect the inherited composition instance.
     * @param inheritedConfig never null
     */
    public abstract void inherit(C inheritedConfig);

}
