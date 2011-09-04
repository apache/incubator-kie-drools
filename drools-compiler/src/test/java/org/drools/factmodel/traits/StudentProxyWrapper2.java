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

import org.drools.spi.InternalReadAccessor;
import org.drools.spi.WriteAccessor;

import java.util.*;

public class StudentProxyWrapper2 implements Map<String, Object>, MapWrapper {

        Imp2 object;
        Map<String, Object> map;

        public static InternalReadAccessor name_reader;
        public static WriteAccessor name_writer;

        public static InternalReadAccessor bit_reader;
        public static WriteAccessor bit_writer;


        public StudentProxyWrapper2( Imp2 object, Map<String,Object> map ) {
            this.object = object;
            this.map = map;

            map.put( "age", 0 );
            map.put( "xcsvf" , 0.0 );
            map.put( "name" , null );
            map.put( "csdfsd", 0L );
            map.put( "school" , null );

        }

        public int size() {
            return map.size()
//                   + ( object.getName() != null ? 1 : 0 )
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
            return map.containsKey( key );
        }

        public boolean containsValue(Object value) {
            if ( value == null ) {
                if ( object.getName() == null ) return true;
                if ( object.getSchool() == null ) return true;
                return map.containsValue( null );
            }
            return true;
        }

        public Object get( Object key ) {
            if ( "name".equals( key ) ) {
                return object.getName();
            }
            if ( "school".equals( key ) ) {
                return object.getSchool();
            }
            return map.get( key );
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

            return map.put(key, value);
        }

        public Object remove(Object key) {
            Object val;

            // any hard field must be 0-fied
            // any soft field must be 0-fied on the map

            // other fields will effectively be removed

            if ( "name".equals( key ) ) {
//                val = object.getName();
//                object.setName( null );
                val = name_reader.getValue( object );
                name_writer.setValue( object, null );
                return val;
            }
            if ( "bol".equals( key ) ) {
//                val = object.isBl();
//                object.setBl( true );
                val = bit_reader.getIntValue( object );
                bit_writer.setIntValue( object, 0 );
                return val;
            }
            if ( "age".equals( key ) ) {
                val = map.get( "age" );
                map.put( "age", 0 );
                return val;
            }

            val = map.remove( key );
            return val;
        }

        public void putAll(Map<? extends String, ? extends Object> m) {
            for ( String k : m.keySet() ) {
                put( k, m.get( k ) );
            }
        }

        public void clear() {
//            object.setName(null);
//            object.setD(0);
//            object.setBite( null );
//            object.setSchool( null );
            bit_writer.setIntValue( object, 0 );
            name_writer.setValue( object, null );
            map.clear();


            map.put( "age", 0 );
            map.put( "xcsvf" , 0.0 );
            map.put( "name" , null );
            map.put( "csdfsd", 0L );
            map.put( "school                     " , null );
        }

        public Set<String> keySet() {
            Set<String> set = new HashSet<String>();

            set.add("name");
            set.add("school");


            set.addAll( map.keySet() );
            return set;
        }

        public Collection<Object> values() {
            Collection<Object> values = new ArrayList<Object>();

            values.add( object.getName() );
            values.add( object.getSchool() );

            values.addAll( map.values() );
            return values;
        }

        public Set<Map.Entry<String, Object>> entrySet() {
            Set<Map.Entry<String, Object>> set = new HashSet<Map.Entry<String, Object>>();

            set.add( TraitProxy.buildEntry("name", object.getName()) );
            set.add( TraitProxy.buildEntry("school", object.getSchool()) );

            set.addAll( map.entrySet() );
            return set;
        }


    public boolean equals(Object o) {
        if (this == o) return true;
        MapWrapper that = (MapWrapper) o;
        return map.equals( that.getInnerMap() );
    }

    public int hashCode() {
        return map.hashCode();
    }

    public Map<String, Object> getInnerMap() {
        return map;
    }

    public String toString() {
        return "[["+entrySet()+"]]";
    }
}
