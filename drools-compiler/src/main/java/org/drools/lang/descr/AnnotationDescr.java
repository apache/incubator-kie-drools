package org.drools.lang.descr;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

    // '' and 'a' are passed through as 
    public static String unquote( String s ){
        if( s.startsWith( "\"" ) && s.endsWith( "\"" ) ||
            s.startsWith( "'" ) && s.endsWith( "'" ) ) {
            return s.substring( 1, s.length() - 1 );
        } else {
            return s;
        }
    }
    
    public AnnotationDescr(final String name) {
        this.name = name;
        this.values = new HashMap<String, String>();
    }

    public AnnotationDescr(final String name,
                           final String value) {
        this.name = name;
        this.values = new HashMap<String, String>();
        this.values.put( VALUE, value );
    }

    public String getName() {
        return this.name;
    }
    
    public boolean hasValue() {
        return !this.values.isEmpty();
    }

    public void setValue( final String value ) {
        this.values.put( VALUE, value );
    }

    public void setKeyValue( final String key,
                             final String value ) {
        this.values.put( key, value );
    }

    public String getValue( final String key ) {
        return this.values.get( key );
    }
    
    public Map<String,String> getValues(){
        return this.values;
    }
    
    /**
     * Returns the metadata value as a single object or a Map
     * @return
     */
    public Object getValue() {
        Object single = getSingleValue();
        return single != null ? single : this.values;
    }

    public Object getValueStripped() {
        Object single = getSingleValueStripped();
        if( single != null ) return single;
        Map<String,String> sMap = new HashMap<String,String>();
        for( Map.Entry<String,String> entry: this.values.entrySet() ){
            sMap.put( entry.getKey(), unquote( entry.getValue() ) );
        }        
        return sMap;
    }

    public Map<String,String> getValueMap() {
        return this.values;
    }

    public String getSingleValue(){
        Set<String> keySet = this.values.keySet();
        if( keySet.size() == 1 &&
            "value".equals( keySet.iterator().next() ) ){
            return this.values.get( "value" );
        } else {
            return null;
        }
    }
    
    public String getSingleValueStripped(){
        Set<String> keySet = this.values.keySet();
        if( keySet.size() == 1 &&
            "value".equals( keySet.iterator().next() ) ){
            return unquote( this.values.get( "value" ) );
        } else {
            return null;
        }
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
