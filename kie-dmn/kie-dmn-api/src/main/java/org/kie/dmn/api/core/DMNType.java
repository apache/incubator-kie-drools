/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.dmn.api.core;

import java.util.Map;

public interface DMNType
        extends Cloneable {

    String getNamespace();

    String getName();

    String getId();

    boolean isCollection();

    boolean isComposite();

    Map<String, DMNType> getFields();

    DMNType getBaseType();

    DMNType clone();

    /**
     * Definition of `instance of` accordingly to FEEL specifications Table 49.
     * @param o
     * @return if o is instance of the type represented by this type. If the parameter is null, returns false. 
     */
    boolean isInstanceOf(Object o);
    
    /**
     * Check if the value passed as parameter can be assigned to this type.
     * @param value
     * @return if value can be assigned to the type represented by this type. If the parameter is null, returns true. 
     */
    boolean isAssignableValue(Object value);
}
