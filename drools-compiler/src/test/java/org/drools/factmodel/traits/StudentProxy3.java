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
import org.drools.core.util.TripleFactory;
import org.drools.core.util.TripleStore;
import org.drools.runtime.rule.Variable;
import org.drools.spi.InternalReadAccessor;
import org.drools.spi.WriteAccessor;


public class StudentProxy3 extends TraitProxy implements IStudent {

    private static TripleFactory tripleFactory = TraitFactory.tripleFactory;

    public final Imp2 object;
    private TripleStore map;

    public static InternalReadAccessor name_reader;
    public static WriteAccessor name_writer;

    public static InternalReadAccessor bit_reader;
    public static WriteAccessor bit_writer;



    public StudentProxy3(Imp2 obj, final TripleStore m) {

        this.object = obj;
        this.map = m;

        fields = new StudentProxyWrapper3( obj, m );

        obj.setDynamicProperties( new TripleBasedBean(obj,m) );

        obj.setTraitMap( new TripleBasedTypes(obj,m) );

    }

    public Imp2 getCore() {
        return object;
    }

    public Object getObject() {
        return object;
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
        Triple t = map.get( propertyKey( "age" ) );
        if ( t == null || t.getValue() == null ) {
            return 0;
        }
        return (Integer) t.getValue();
    }

    public void setAge(int age) {
        map.put( property( "age", Integer.valueOf( age ) ) );
    }


    public Integer getAgeI() {
        Triple t = map.get( propertyKey( "age" ) );
        if ( t == null || t.getValue() == null ) {
            return null;
        }
        return (Integer) t.getValue();
    }

    public void setAgeI(Integer age) {
        map.put( property( "age", age ) );
    }



    protected Triple propertyKey( String property ) {
        return tripleFactory.newTriple( getObject(), property, Variable.v );
    }

    protected Triple property( String property, Object value ) {
        return tripleFactory.newTriple( getObject(), property, value );
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        TraitProxy that = (TraitProxy) o;

        if ( ! getFields().equals( that.getFields() ) ) return false;
        if ( ! getObject().equals( that.getObject() ) ) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + getObject().hashCode();
        result = 31 * result + this.getFields().hashCode();
        return result;
    }
}
