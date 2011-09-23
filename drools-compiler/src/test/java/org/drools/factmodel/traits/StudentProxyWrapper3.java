/*
 * Copyright 2011 JBoss Inc
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

package org.drools.factmodel.traits;

import org.drools.core.util.Triple;
import org.drools.core.util.TripleImpl;
import org.drools.core.util.TripleStore;
import org.drools.runtime.rule.Variable;
import org.drools.spi.InternalReadAccessor;
import org.drools.spi.WriteAccessor;

import java.util.*;

public class StudentProxyWrapper3 extends TripleBasedStruct {


    Imp2 object;


    public static InternalReadAccessor name_reader;
    public static WriteAccessor name_writer;

    public static InternalReadAccessor bit_reader;
    public static WriteAccessor bit_writer;


    public StudentProxyWrapper3( Imp2 object, TripleStore store ) {
        this.object = object;
        this.store = store;

        this.store.put( property( "age", 0) );
        this.store.put( property( "xcsvf" , 0.0 ) );
        this.store.put( property( "name" , null ) );
        this.store.put( property( "csdfsd", 0L ) );
        this.store.put( property( "school" , null ) );

    }

    public int size() {
        return super.size() +
                + ( object.getSchool() != null ? 1 : 0 )
                +  1
                + ( object.getName() != null ? 1 : 0 )
                ;
    }

    public boolean isEmpty() {
        return false;
    }

    public boolean containsKey(Object key) {
        if ( "name".equals( key ) ) return true;
        if ( "school".equals( key ) ) return true;
        return super.containsKey( key );
    }

    public boolean containsValue(Object value) {
        if ( value == null ) {
            if ( object.getName() == null ) return true;
            if ( object.getSchool() == null ) return true;
            if ( object.getSchool() == null ) return true;
        }
        return super.containsValue( value );
    }

    public Object get( Object key ) {
        if ( "name".equals( key ) ) {
            return object.getName();
        }
        if ( "school".equals( key ) ) {
            return object.getSchool();
        }
        return super.get( key );
    }

    public Object put(String key, Object value) {
        if ( "name".equals( key ) ) {
//                object.setName( (String) value );
            name_writer.setValue( object, value );
            return value;
        }
        if ( "school".equals( key ) ) {
            object.setSchool((String) value);
            return value;
        }
        if ( "num".equals( key ) ) {
            double d = (Double) value;
            bit_writer.setDoubleValue( object, d );
            return value;
        }

        return super.put( key, value );
    }




    public Object remove(Object key) {
        Object val;

        // any hard field must be 0-fied
        // any soft field must be 0-fied on the map

        // other fields will effectively be removed

        if ( "name".equals( key ) ) {
            val = name_reader.getValue( object );
            name_writer.setValue( object, null );
            return val;
        }
        if ( "bol".equals( key ) ) {
            val = bit_reader.getIntValue( object );
            bit_writer.setIntValue( object, 0 );
            return val;
        }
        if ( "age".equals( key ) ) {
            val = this.store.get( key( "age" ) );
            super.put( "age", 0 );
            return val;
        }

        val = super.remove( key );
        return val;
    }



    public void clear() {
        bit_writer.setIntValue( object, 0 );
        name_writer.setValue( object, null );
        super.clear();


        this.store.put( property( "age", 0) );
        this.store.put( property( "xcsvf" , 0.0 ) );
        this.store.put( property( "name" , null ) );
        this.store.put( property( "csdfsd", 0L ) );
        this.store.put( property( "school" , null ) );

    }

    public Set<String> keySet() {
        Set<String> set = new HashSet<String>();

        set.add("name");
        set.add("school");

        set.addAll( super.keySet() );
        return set;
    }

    public Collection<Object> values() {
        Collection<Object> values = new ArrayList<Object>();

        values.add( object.getName() );
        values.add( object.getSchool() );

        values.addAll( super.values() );
        return values;
    }

    public Set<Entry<String, Object>> entrySet() {
        Set<Entry<String, Object>> set = new HashSet<Entry<String, Object>>();

        set.add( TraitProxy.buildEntry("name", object.getName()) );
        set.add( TraitProxy.buildEntry("school", object.getSchool()) );

        set.addAll( super.entrySet() );
        return set;
    }

    @Override
    protected Object getObject() {
        return object;
    }


    public boolean equals(Object o) {
        if (this == o) return true;

        return getTriplesForSubject(object).equals(
                getTriplesForSubject(((TripleBasedStruct) o).getObject()) );
    }

    public int hashCode() {
        return getTriplesForSubject(object).hashCode();
    }



    public String toString() {
        return "[["+entrySet()+"]]";
    }
}
