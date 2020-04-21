/*
 * Copyright 2011 Red Hat, Inc. and/or its affiliates.
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

package org.drools.compiler.factmodel.traits;

import org.drools.core.factmodel.traits.TraitProxy;
import org.drools.core.factmodel.traits.TraitTypeMap;
import org.drools.core.factmodel.traits.TraitableBean;
import org.drools.core.spi.InternalReadAccessor;
import org.drools.core.spi.WriteAccessor;
import org.drools.core.util.ExternalizableLinkedHashMap;

import java.util.HashMap;
import java.util.Map;

public class StudentProxy2 extends TraitProxy implements IStudent {

    public final Imp2 object;
    public final Map<String, Object> map;


    public static InternalReadAccessor name_reader;
    public static WriteAccessor name_writer;

    public static InternalReadAccessor bit_reader;
    public static WriteAccessor bit_writer;

    private static final String traitType = IStudent.class.getName();


    public StudentProxy2(Imp2 obj, Map<String, Object> m) {
        if ( m == null ) {
            m = new HashMap<String,Object>();
        }

        this.object = obj;
        this.map = m;

        fields = new StudentProxyWrapper2( obj, m );

        if ( obj._getDynamicProperties() == null ) {
            obj._setDynamicProperties( m );
        }

        if ( obj._getTraitMap() == null ) {
            obj._setTraitMap( new TraitTypeMap( new ExternalizableLinkedHashMap() ) );
        }
    }

    public Imp2 getCore() {
        return object;
    }

    public boolean isTop() {
        return false;
    }

    public String _getTraitName() {
        return traitType;
    }

    public TraitableBean getObject() {
        return object;
    }



    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;

        if (! this.getClass().equals(o.getClass())) {
            return false;
        }

        TraitProxy that = (TraitProxy) o;
        return getFields().equals( that.getFields() );
    }


    public int hashCode() {
        int result = object.hashCode();
        result = 31 * result + map.hashCode();
        result = "Student".hashCode() * result;
        return result;
    }

    public String toString() {
        return "(@Student) : " + getFields().entrySet().toString();
    }




    public double getD() {
        return bit_reader.getDoubleValue( object );
    }

    public void setD(double d) {
        bit_writer.setDoubleValue( object, d );
    }

    // get/set impl depends on bitmask
    public String getSchool() {
        return object.getSchool();
    }

    public void setSchool(String school) {
        object.setSchool( school );
    }

    public String getName() {
//        return object.getName();
        return (String) name_reader.getValue(object);
    }

    public void setName(String name) {
//        object.setName( name );
        name_writer.setValue(object, name);
    }

    public int getAge() {
        Object tmp = map.get( "age" );
        return  (Integer) (
                tmp != null ?
                tmp : 0 );
    }

    public void setAge(int age) {
        map.put( "age", Integer.valueOf( age ) );
    }








}
