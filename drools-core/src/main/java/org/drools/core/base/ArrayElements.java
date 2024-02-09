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
package org.drools.core.base;

import java.util.Arrays;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;

@XmlType(name="array-elements")
@XmlAccessorType(XmlAccessType.NONE)
public class ArrayElements {

    @XmlElement
    private Object[] elements;

    private static final Object[] EMPTY_ELEMENTS = new Object[0];

    public ArrayElements() {
        this(null);
    }

    public ArrayElements(final Object[] elements) {
        setElements( elements );
    }

    public Object[] getElements() {
        return this.elements;
    }

    public void setElements(Object[] elements) {
        this.elements = elements != null ? elements : EMPTY_ELEMENTS;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Arrays.hashCode( elements );
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if ( this == obj ) {
            return true;
        }
        if ( obj == null ) {
            return false;
        }
        if ( getClass() != obj.getClass() ) {
            return false;
        }
        ArrayElements other = (ArrayElements) obj;
        if ( !Arrays.equals( elements,
                             other.elements ) ) {
            return false;
        }
        return true;
    }


}
