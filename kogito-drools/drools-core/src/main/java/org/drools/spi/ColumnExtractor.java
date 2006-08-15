package org.drools.spi;

import org.drools.base.ClassObjectType;
import org.drools.base.ValueType;
import org.drools.facttemplates.Fact;

/*
 * Copyright 2005 JBoss Inc
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

public class ColumnExtractor
    implements
    Extractor {

    /**
     * 
     */
    private static final long serialVersionUID = 9055898682913511836L;
    private ObjectType        objectType;

    public ColumnExtractor(final ObjectType objectType) {
        this.objectType = objectType;
    }

    public Object getValue(final Object object) {
        return object;
    }

    public ObjectType getObjectType() {
        return this.objectType;
    }

    public Class getExtractToClass() {
        // @todo : this is a bit nasty, but does the trick
        if ( objectType.getClass() == ClassObjectType.class ) {
            return ( ( ClassObjectType ) objectType ).getClassType();
        } else {
            return Fact.class;
        } 
    }

    public ValueType getValueType() {
        return objectType.getValueType();
    }

}