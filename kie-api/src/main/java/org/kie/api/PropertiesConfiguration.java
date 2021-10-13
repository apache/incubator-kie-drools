/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
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

package org.kie.api;

/**
 * Base class for other Configuration classes.
 */
public interface PropertiesConfiguration {

    /**
     * Sets a property value
     *
     * @param name name
     * @param value value
     */
    public void setProperty(String name,
                            String value);

    /**
     * Gets a property value
     *
     * @param name name
     * @return property
     */
    public String getProperty(String name);
}
