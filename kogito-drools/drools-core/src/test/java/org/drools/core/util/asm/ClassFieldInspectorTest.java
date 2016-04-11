/*
 * Copyright 2005 Red Hat, Inc. and/or its affiliates.
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

package org.drools.core.util.asm;

import org.junit.Test;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.Map;

import static org.junit.Assert.*;

public class ClassFieldInspectorTest {

    @Test
    public void testIt() throws Exception {
        final ClassFieldInspector ext = new ClassFieldInspector( Person.class );
        assertEquals( 7,
                      ext.getFieldNames().size() );
        assertEquals( "getAge" ,
                      ext.getGetterMethods().get( "age" ).getName() );
        assertEquals( "isHappy" ,
                      ext.getGetterMethods().get( "happy" ).getName() );
        assertEquals( "getName" ,
                      ext.getGetterMethods().get( "name" ).getName() );

        final Map<String, Integer> names = ext.getFieldNames();
        assertNotNull( names );
        assertEquals( 7,
                      names.size() );
        assertNull( names.get( "nAme" ) );

    }

    @Test
    public void testInterface() throws Exception {
        final ClassFieldInspector ext = new ClassFieldInspector( TestInterface.class );
        assertEquals( 2,
                      ext.getFieldNames().size() );
        assertEquals( "getSomething" ,
                      ext.getGetterMethods().get( "something" ).getName() );
        assertEquals( "getAnother" ,
                      ext.getGetterMethods().get( "another" ).getName() );

        final Map<String, Integer> names = ext.getFieldNames();
        assertNotNull( names );
        assertEquals( 2,
                      names.size() );

    }

    @Test
    public void testAbstract() throws Exception {
        final ClassFieldInspector ext = new ClassFieldInspector( TestAbstract.class );
        assertEquals( 5,
                      ext.getFieldNames().size() );
        assertEquals( "getSomething" ,
                      ext.getGetterMethods().get( "something" ).getName() );
        assertEquals( "getAnother" ,
                      ext.getGetterMethods().get( "another" ).getName() );

        final Map<String, Integer> names = ext.getFieldNames();
        assertNotNull( names );
        assertEquals( 5,
                      names.size() );

    }

    @Test
    public void testInheritedFields() throws Exception {
        ClassFieldInspector ext = new ClassFieldInspector( BeanInherit.class );
        assertEquals( 5,
                      ext.getFieldNames().size() );
        assertNotNull( ext.getFieldTypesField().get( "text" ) );
        assertNotNull( ext.getFieldTypesField().get( "number" ) );

        ext = new ClassFieldInspector( InterfaceChildImpl.class );
        assertEquals( 8,
                      ext.getFieldNames().size() );

        // test inheritence from abstract class
        assertNotNull( ext.getFieldNames().get( "HTML" ) );
        assertNotNull( ext.getFieldTypesField().get( "HTML" ) );

        // check normal field on child class
        assertNotNull( ext.getFieldNames().get( "baz" ) );
        assertNotNull( ext.getFieldTypesField().get( "baz" ) );

        // test inheritence from an interface
        assertNotNull( ext.getFieldNames().get( "URI" ) );
        assertNotNull( ext.getFieldTypesField().get( "URI" ) );
    }

    @Test
    public void testIntefaceInheritance() throws Exception {
        final ClassFieldInspector ext = new ClassFieldInspector( InterfaceChild.class );
        final Map fields = ext.getFieldNames();
        assertTrue( fields.containsKey( "foo" ) );
        assertTrue( fields.containsKey( "bar" ) );
        assertTrue( fields.containsKey( "baz" ) );
        assertTrue( fields.containsKey( "URI" ) );
    }

    @Test
    public void testFieldIndexCalculation() {
        try {
            final ClassFieldInspector ext = new ClassFieldInspector( SubPerson.class );
            final Map map = ext.getFieldNames();
            final String[] fields = new String[map.size()];
            for ( final Iterator i = map.entrySet().iterator(); i.hasNext(); ) {
                final Map.Entry entry = (Map.Entry) i.next();
                final String fieldName = (String) entry.getKey();
                final int fieldIndex = ((Integer) entry.getValue()).intValue();
                if ( fields[fieldIndex] == null ) {
                    fields[fieldIndex] = fieldName;
                } else {
                    fail( "Duplicate index found for 2 fields: index[" + fieldIndex + "] = [" + fields[fieldIndex] + "] and [" + fieldName + "]" );
                }
            }
        } catch ( final IOException e ) {
            e.printStackTrace();
            fail( "Unexpected exception thrown" );
        }
    }

    @Test
    public void testGetReturnTypes() throws Exception {
        final ClassFieldInspector ext = new ClassFieldInspector( Person.class );
        final Map types = ext.getFieldTypes();
        assertNotNull( types );
        assertEquals( boolean.class,
                      types.get( "happy" ) );
        assertEquals( int.class,
                      types.get( "age" ) );
        assertEquals( String.class,
                      types.get( "name" ) );
    }

    @Test
    public void testGetMethodForField() throws Exception {
        final ClassFieldInspector ext = new ClassFieldInspector( Person.class );
        final Map methods = ext.getGetterMethods();
        assertNotNull( methods );
        assertEquals( "isHappy",
                      ((Method) methods.get( "happy" )).getName() );
        assertEquals( "getName",
                      ((Method) methods.get( "name" )).getName() );
        // test case sensitive
        assertNull( methods.get( "nAme" ) );
        assertEquals( "getAge",
                      ((Method) methods.get( "age" )).getName() );

    }

    @Test
    public void testNonGetter() throws Exception {
        final ClassFieldInspector ext = new ClassFieldInspector( NonGetter.class );
        final Map methods = ext.getGetterMethods();
        assertEquals( "getFoo",
                      ((Method) methods.get( "foo" )).getName() );
        assertEquals( 5,
                      methods.size() );
        assertTrue( ext.getFieldNames().containsKey( "foo" ) );
        assertTrue( ext.getFieldNames().containsKey( "baz" ) );
        assertEquals( String.class,
                      ext.getFieldTypes().get( "foo" ) );

    }

    @Test
    public void testWierdCapsForField() throws Exception {
        final ClassFieldInspector ext = new ClassFieldInspector( Person.class );
        final Map methods = ext.getGetterMethods();
        assertEquals( "getURI",
                      ((Method) methods.get( "URI" )).getName() );
        assertEquals( 7,
                      methods.size() );
    }

    static class NonGetter {

        public int foo() {
            return 42;
        }

        public String getFoo() {
            return "foo";
        }

        public String baz() {
            return "";
        }

        public void bas() {

        }
    }

    static class Person {
        public static String aStaticString;
        private boolean      happy;
        private String       name;
        private int          age;
        private String       URI;

        static {
            aStaticString = "A static String";
        }

        public int getAge() {
            return this.age;
        }

        public void setAge(final int age) {
            this.age = age;
        }

        public boolean isHappy() {
            return this.happy;
        }

        public void setHappy(final boolean happy) {
            this.happy = happy;
        }

        public String getName() {
            return this.name;
        }

        public void setName(final String name) {
            this.name = name;
        }

        //ignore this as it returns void type
        public void getNotAGetter() {
            return;
        }

        //ignore this as private
        private boolean isBogus() {
            return false;
        }

        //this will not show up as it is a getter that takes an argument
        public String getAlsoBad(final String s) {
            return "ignored";
        }

        //this should show up, as its a getter, but all CAPS
        public String getURI() {
            return this.URI;
        }

        public void setURI(final String URI) {
            this.URI = URI;
        }
    }

    static class SubPerson {
        private int childField;

        /**
         * @return the childField
         */
        public int getChildField() {
            return this.childField;
        }

        /**
         * @param childField the childField to set
         */
        public void setChildField(final int childField) {
            this.childField = childField;
        }

    }

    @Test
    public void testOverridingMethodWithCovariantReturnType() throws Exception{
        final ClassFieldInspector ext = new ClassFieldInspector( SuperCar.class );
        final Class<?> engine = ext.getFieldTypes().get("engine");
        assertEquals(SuperEngine.class, engine);
    }

    static class Vehicle<T>{
        private T engine;
        public T getEngine(){
            return engine;
        }
    }

    static class Car extends Vehicle<NormalEngine>{

        @Override
        public NormalEngine getEngine() {
            return new NormalEngine();
        }
    }

    static class SuperCar extends Car {
        @Override
        public SuperEngine getEngine() {
            return new SuperEngine();
        }
    }

    static class NormalEngine { }
    static class SuperEngine extends NormalEngine { }
}
