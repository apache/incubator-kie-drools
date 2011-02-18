/*
 * Copyright 2010 JBoss Inc
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

package org.drools.definition.type;

/**
 * A field from a declared fact type
 * 
 */
public interface FactField
    extends
    java.io.Externalizable {

    /**
     * Returns the type of this field.
     * 
     * @return
     */
    public Class< ? > getType();

    /**
     * Returns the name of this field.
     * @return
     */
    public String getName();

    /**
     * Returns true if this field is a key field. A key field
     * is included in hashcode() calculation and on the equals() 
     * method evaluation. Non-key fields are not checked in this 
     * methods.
     * 
     * @return
     */
    public boolean isKey();

    /**
     * Sets the value of this field in the given fact.
     * 
     * @param bean fact on which to set the field.
     * @param value the value to set on the field.
     * 
     */
    public void set(Object bean,
                    Object value);

    /**
     * Returns the value of this field in the given fact.
     * 
     * @param the fact from which the field will be read.
     * 
     * @return the value of the field on the given fact.
     */
    public Object get(Object bean);

}
