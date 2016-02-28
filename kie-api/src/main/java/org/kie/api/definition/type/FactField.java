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

package org.kie.api.definition.type;

import java.util.List;
import java.util.Map;

/**
 * A field from a declared fact type
 */
public interface FactField
    extends
    java.io.Externalizable {

    /**
     * @return type of this field.
     */
    Class< ? > getType();

    /**
     * @return name of this field.
     */
    String getName();

    /**
     * @return true if this field is a key field. A key field
     * is included in hashcode() calculation and on the equals() 
     * method evaluation. Non-key fields are not checked in this 
     * method.
     */
    boolean isKey();

    /**
     * Sets the value of this field in the given fact.
     * 
     * @param bean fact on which to set the field.
     * @param value the value to set on the field.
     */
    void set(Object bean,
             Object value);

    /**
     * @param bean the fact from which the field will be read.
     * 
     * @return the value of this field on the given fact.
     */
    Object get(Object bean);

    /**
     * Returns the index of this field in the field list for
     * the defining fact type. The list (and thus the index)
     * takes into account the fields inherited from the parent
     * class, if any.
     *
     * @return  the index of this field in the defining type
     */
    int getIndex();

    /**
     * Returns the list of field-level annotations
     * used in this field definition
     *
     * @return  the list of field-level annotations
     */
    List<Annotation> getFieldAnnotations();

    /**
     * Returns the annotations of this field definition as
     * key-value pairs.
     *
     * @return a key-value map of the field-level annotations
     */
    Map<String,Object> getMetaData();

}
