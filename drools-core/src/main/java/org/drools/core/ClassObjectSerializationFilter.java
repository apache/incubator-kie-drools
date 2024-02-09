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
package org.drools.core;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlRootElement;

import org.kie.api.runtime.ClassObjectFilter;
import org.kie.api.runtime.ObjectFilter;

@XmlRootElement(name="class-object-filter")
@XmlAccessorType(XmlAccessType.FIELD)
public class ClassObjectSerializationFilter implements ObjectFilter {

    @XmlAttribute(name="string",required=true)
    private String className = null;

    private transient Class filteredClass;

    private transient boolean skipLoadClass;

    public ClassObjectSerializationFilter() {
        // JAXB constructor
    }

    public ClassObjectSerializationFilter(Class clazz) {
        // use canonical name to avoid problems with anonymous classes
        this.className = clazz.getCanonicalName();
        this.filteredClass = clazz;
    }

    public ClassObjectSerializationFilter(ClassObjectFilter objectFilter) {
        this(objectFilter.getFilteredClass());
    }

    public Class getFilteredClass() {
        return filteredClass;
    }

    public void setFilteredClass(Class filteredClass) {
        this.filteredClass = filteredClass;
    }

    /**
     * @param object The object to be filtered
     * @return whether or not the Iterator accepts the given object according to its class.
     */
    @Override
    public boolean accept(Object object) {
        if ( !skipLoadClass && filteredClass == null ) {
            try {
                filteredClass = Class.forName(this.className);
            } catch( ClassNotFoundException e ) {
                skipLoadClass = true;
            }
        }
        return skipLoadClass ?
                className.equals( object.getClass().getCanonicalName() ) :
                filteredClass.isAssignableFrom( object.getClass() );
    }
}
