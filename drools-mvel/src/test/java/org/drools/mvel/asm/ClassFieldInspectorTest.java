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
package org.drools.mvel.asm;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.Map;

import org.drools.core.util.asm.BeanInherit;
import org.drools.core.util.asm.InterfaceChild;
import org.drools.core.util.asm.InterfaceChildImpl;
import org.drools.core.util.asm.TestAbstract;
import org.drools.core.util.asm.TestInterface;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

public class ClassFieldInspectorTest {

    @Test
    public void testIt() throws Exception {
        final ClassFieldInspectorImpl ext = new ClassFieldInspectorImpl( Person.class );
        assertThat(ext.getFieldNames().size()).isEqualTo(8);
        assertThat(ext.getGetterMethods().get("age").getName()).isEqualTo("getAge");
        assertThat(ext.getGetterMethods().get("happy").getName()).isEqualTo("isHappy");
        assertThat(ext.getGetterMethods().get("name").getName()).isEqualTo("getName");

        final Map<String, Integer> names = ext.getFieldNames();
        assertThat(names).isNotNull();
        assertThat(names.size()).isEqualTo(8);
        assertThat(names.get("nAme")).isNull();

    }

    @Test
    public void testInterface() throws Exception {
        final ClassFieldInspectorImpl ext = new ClassFieldInspectorImpl( TestInterface.class );
        assertThat(ext.getFieldNames().size()).isEqualTo(3);
        assertThat(ext.getGetterMethods().get("something").getName()).isEqualTo("getSomething");
        assertThat(ext.getGetterMethods().get("another").getName()).isEqualTo("getAnother");

        final Map<String, Integer> names = ext.getFieldNames();
        assertThat(names).isNotNull();
        assertThat(names.size()).isEqualTo(3);

    }

    @Test
    public void testAbstract() throws Exception {
        final ClassFieldInspectorImpl ext = new ClassFieldInspectorImpl( TestAbstract.class );
        assertThat(ext.getFieldNames().size()).isEqualTo(6);
        assertThat(ext.getGetterMethods().get("something").getName()).isEqualTo("getSomething");
        assertThat(ext.getGetterMethods().get("another").getName()).isEqualTo("getAnother");

        final Map<String, Integer> names = ext.getFieldNames();
        assertThat(names).isNotNull();
        assertThat(names.size()).isEqualTo(6);

    }

    @Test
    public void testInheritedFields() throws Exception {
        ClassFieldInspectorImpl ext = new ClassFieldInspectorImpl( BeanInherit.class );
        assertThat(ext.getFieldNames().size()).isEqualTo(6);
        assertThat( ext.getFieldTypesField().get( "text" ) ).isNotNull();
        assertThat( ext.getFieldTypesField().get( "number" ) ).isNotNull();

        ext = new ClassFieldInspectorImpl( InterfaceChildImpl.class );
        assertThat(ext.getFieldNames().size()).isEqualTo(9);

        // test inheritence from abstract class
        assertThat( ext.getFieldNames().get( "HTML" ) ).isNotNull();
        assertThat( ext.getFieldTypesField().get( "HTML" ) ).isNotNull();

        // check normal field on child class
        assertThat( ext.getFieldNames().get( "baz" ) ).isNotNull();
        assertThat( ext.getFieldTypesField().get( "baz" ) ).isNotNull();

        // test inheritence from an interface
        assertThat( ext.getFieldNames().get( "URI" ) ).isNotNull();
        assertThat( ext.getFieldTypesField().get( "URI" ) ).isNotNull();
    }

    @Test
    public void testIntefaceInheritance() throws Exception {
        final ClassFieldInspectorImpl ext = new ClassFieldInspectorImpl( InterfaceChild.class );
        final Map fields = ext.getFieldNames();
        assertThat(fields.containsKey("foo")).isTrue();
        assertThat(fields.containsKey("bar")).isTrue();
        assertThat(fields.containsKey("baz")).isTrue();
        assertThat(fields.containsKey("URI")).isTrue();
    }

    @Test
    public void testFieldIndexCalculation() {
        try {
            final ClassFieldInspectorImpl ext = new ClassFieldInspectorImpl( SubPerson.class );
            final Map map = ext.getFieldNames();
            final String[] fields = new String[map.size()];
            for ( final Iterator i = map.entrySet().iterator(); i.hasNext(); ) {
                final Map.Entry entry = (Map.Entry) i.next();
                final String fieldName = (String) entry.getKey();
                final int fieldIndex = (Integer) entry.getValue();
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
        final ClassFieldInspectorImpl ext = new ClassFieldInspectorImpl( Person.class );
        final Map types = ext.getFieldTypes();
        assertThat(types).isNotNull();
        assertThat(types.get("happy")).isEqualTo(boolean.class);
        assertThat(types.get("age")).isEqualTo(int.class);
        assertThat(types.get("name")).isEqualTo(String.class);
    }

    @Test
    public void testGetMethodForField() throws Exception {
        final ClassFieldInspectorImpl ext = new ClassFieldInspectorImpl( Person.class );
        final Map methods = ext.getGetterMethods();
        assertThat(methods).isNotNull();
        assertThat(((Method) methods.get("happy")).getName()).isEqualTo("isHappy");
        assertThat(((Method) methods.get("name")).getName()).isEqualTo("getName");
        // test case sensitive
        assertThat(methods.get("nAme")).isNull();
        assertThat(((Method) methods.get("age")).getName()).isEqualTo("getAge");

    }

    @Test
    public void testNonGetter() throws Exception {
        final ClassFieldInspectorImpl ext = new ClassFieldInspectorImpl( NonGetter.class );
        final Map methods = ext.getGetterMethods();
        assertThat(((Method) methods.get("foo")).getName()).isEqualTo("getFoo");
        assertThat(methods.size()).isEqualTo(5);
        assertThat(ext.getFieldNames().containsKey("foo")).isTrue();
        assertThat(ext.getFieldNames().containsKey("baz")).isTrue();
        assertThat(ext.getFieldTypes().get("foo")).isEqualTo(String.class);

    }

    @Test
    public void testWierdCapsForField() throws Exception {
        final ClassFieldInspectorImpl ext = new ClassFieldInspectorImpl( Person.class );
        final Map methods = ext.getGetterMethods();
        assertThat(((Method) methods.get("URI")).getName()).isEqualTo("getURI");
        assertThat(methods.size()).isEqualTo(7);
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
        final ClassFieldInspectorImpl ext = new ClassFieldInspectorImpl( SuperCar.class );
        final Class<?> engine = ext.getFieldTypes().get("engine");
        assertThat(engine).isEqualTo(SuperEngine.class);
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
