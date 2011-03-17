package org.drools.lang.descr;

import java.util.HashMap;
import java.util.Map;

/*
 * Copyright 2011 JBoss Inc
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
public class AnnotationDescr extends BaseDescr {
    private static final String VALUE            = "value";
    private static final long   serialVersionUID = 520l;

    private final String        name;
    private Map<String, String> values;

    public AnnotationDescr(final String name) {
        this.name = name;
        this.values = new HashMap<String, String>();
    }

    public AnnotationDescr(final String name,
                           final String value) {
        this.name = name;
        this.values = new HashMap<String, String>();
        this.values.put( VALUE,
                         value );
    }

    public String getName() {
        return this.name;
    }
    
    public boolean hasValue() {
        return !this.values.isEmpty();
    }

    public String getValue() {
        return this.values.get( VALUE );
    }

    public void setValue( final String value ) {
        this.values.put( VALUE,
                         value );
    }

    public void setKeyValue( final String key,
                             final String value ) {
        this.values.put( key,
                         value );
    }

    public String getValue( final String key ) {
        return this.values.get( key );
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }

    @Override
    public boolean equals( Object obj ) {
        if ( this == obj ) return true;
        if ( obj == null ) return false;
        if ( getClass() != obj.getClass() ) return false;
        AnnotationDescr other = (AnnotationDescr) obj;
        if ( name == null ) {
            if ( other.name != null ) return false;
        } else if ( !name.equals( other.name ) ) return false;
        return true;
    }
}
