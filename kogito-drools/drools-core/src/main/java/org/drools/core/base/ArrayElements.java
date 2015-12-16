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

package org.drools.core.base;

import java.util.Arrays;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.drools.core.xml.jaxb.util.JaxbUnknownAdapter;

@XmlType(name="array-elements")
@XmlAccessorType(XmlAccessType.NONE)
public class ArrayElements {

    @XmlElement
    @XmlJavaTypeAdapter(JaxbUnknownAdapter.class)
    private Object[] elements;

    private static final Object[] EMPTY_ELEMENTS = new Object[0];

    public ArrayElements() {
        this(null);
    }

    public ArrayElements(final Object[] elements) {
        if ( elements != null ) {
            this.elements = elements;
        } else {
            this.elements = EMPTY_ELEMENTS;
        }
    }

    public Object[] getElements() {
        return this.elements;
    }

    public void setElements(Object[] elements) {
        this.elements = elements;
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
        if ( this == obj ) return true;
        if ( obj == null ) return false;
        if ( getClass() != obj.getClass() ) return false;
        ArrayElements other = (ArrayElements) obj;
        if ( !Arrays.equals( elements,
                             other.elements ) ) return false;
        return true;
    }


}
