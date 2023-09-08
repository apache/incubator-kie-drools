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
package org.drools.traits.core.factmodel;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.util.Date;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;

import org.drools.core.base.ClassFieldAccessorCache;
import org.drools.mvel.accessors.ClassFieldAccessorStore;
import org.drools.compiler.builder.impl.classbuilder.ClassBuilder;
import org.drools.base.factmodel.ClassDefinition;
import org.drools.base.factmodel.FieldDefinition;
import org.drools.core.rule.JavaDialectRuntimeData;
import org.drools.wiring.dynamic.PackageClassLoader;
import org.drools.wiring.api.classloader.ProjectClassLoader;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.drools.util.ClassUtils.convertClassToResourcePath;

public class ClassBuilderTest {

    ClassFieldAccessorStore store = new ClassFieldAccessorStore();
    ClassLoader classLoader;
    JavaDialectRuntimeData data;
    
    @Before
    public void setUp() throws Exception {
        data = new JavaDialectRuntimeData();
    }

    private Class build(ClassBuilder builder, ClassDefinition classDef) throws Exception {
        classLoader = new PackageClassLoader(data.getStore(), ProjectClassLoader.createProjectClassLoader());
        byte[] d = builder.buildClass( classDef, classLoader);
                     
        data.write( convertClassToResourcePath(classDef.getClassName()), d );

        
        this.store = new ClassFieldAccessorStore();
        store.setClassFieldAccessorCache( new ClassFieldAccessorCache( classLoader ) );
        store.setEagerWire( true );
        
        Class clazz = classLoader.loadClass( classDef.getClassName() );
        classDef.setDefinedClass( clazz );
        
        return clazz;
        
    }
    
    /*
     * Test method for 'org.drools.core.common.asm.ClassBuilder.buildClass(ClassDefinition)'
     */
    @Test
    public void testBuildClass() {
        try {
            ClassBuilder builder = new TraitClassBuilderFactory().getBeanClassBuilder();

            ClassDefinition classDef = new ClassDefinition( "org.drools.TestClass1",
                                                            null,
                                                            new String[]{"java.io.Serializable"} );
            FieldDefinition intDef = new FieldDefinition("intAttr",
                                                         "int" );

            FieldDefinition stringDef = new FieldDefinition( "stringAttr",
                                                             "java.lang.String" );//"java.lang.String" );
            classDef.addField( intDef );
            classDef.addField( stringDef );
            
            Class clazz = build(builder, classDef);

            
            
            intDef.setReadWriteAccessor( store.getAccessor( clazz,
                                                            intDef.getName() ) );
            stringDef.setReadWriteAccessor( store.getAccessor( clazz,
                                                               stringDef.getName() ) );

            byte[] d = builder.buildClass( classDef, classLoader );

            assertThat(clazz).as("Returned class should be the same").isSameAs(classDef.getDefinedClass());
            assertThat(clazz.getName()).as("Class name should be equal").isEqualTo(classDef.getClassName());

            Serializable instance = (Serializable) clazz.newInstance();

            String stringValue = "Atributo String ok";
            stringDef.setValue( instance,
                                stringValue );
            assertThat(stringDef.getValue(instance)).as("Attribute should have been correctly set").isEqualTo(stringValue);

            int intValue = 50;
            intDef.setValue( instance,
                             new Integer( intValue ) );
            assertThat(((Integer) intDef.getValue(instance)).intValue()).as("Attribute should have been correctly set").isEqualTo(intValue);

            // testing class rebuilding
            clazz = build(builder, classDef);

        } catch ( Exception e ) {
            e.printStackTrace();
            fail( "Error creating class" );
        }
    }

    /**
     * how to write to a jar.
     * @param data
     * @throws FileNotFoundException
     * @throws IOException
     */
    private void writeJar(byte[] data) throws FileNotFoundException,
                                      IOException {
        FileOutputStream out = new FileOutputStream("/Users/michaelneale/edson.jar");
        JarOutputStream jout = new JarOutputStream( out );
        JarEntry je = new JarEntry( "br/com/auster/TestClass1.class" );
        jout.putNextEntry( je );
        jout.write( data );
        jout.closeEntry();
        jout.close();
    }

    @Test
    public void testEquals() {
        try {
            ClassBuilder builder = new TraitClassBuilderFactory().getBeanClassBuilder();

            ClassDefinition classDef = new ClassDefinition( "org.drools.TestClass2",
                                                            null,
                                                            new String[]{} );
            FieldDefinition long1Def = new FieldDefinition( "longAttr1",
                                                            "long",
                                                            true );
            FieldDefinition long2Def = new FieldDefinition( "longAttr2",
                                                            "long",
                                                            true );
            FieldDefinition doubleDef = new FieldDefinition( "doubleAttr",
                                                             "double",
                                                             true );
            FieldDefinition intDef = new FieldDefinition( "intAttr",
                                                          "int",
                                                          true );
            FieldDefinition strDef = new FieldDefinition( "stringAttr",
                                                          "java.lang.String",
                                                          true );
            FieldDefinition dateDef = new FieldDefinition( "dateAttr",
                                                           "java.util.Date",
                                                           true );
            FieldDefinition str2Def = new FieldDefinition( "stringAttr2",
                                                           "java.lang.String" );
            classDef.addField( long1Def );
            classDef.addField( long2Def );
            classDef.addField( doubleDef );
            classDef.addField( intDef );
            classDef.addField( strDef );
            classDef.addField( dateDef );
            classDef.addField( str2Def );

            Class clazz = build(builder, classDef);
            long1Def.setReadWriteAccessor( store.getAccessor( clazz,
                                                              long1Def.getName() ) );
            long2Def.setReadWriteAccessor( store.getAccessor( clazz,
                                                              long2Def.getName() ) );
            doubleDef.setReadWriteAccessor( store.getAccessor( clazz,
                                                               doubleDef.getName() ) );
            intDef.setReadWriteAccessor( store.getAccessor( clazz,
                                                            intDef.getName() ) );
            strDef.setReadWriteAccessor( store.getAccessor( clazz,
                                                            strDef.getName( ) ) );
            dateDef.setReadWriteAccessor( store.getAccessor( clazz,
                                                             dateDef.getName() ) );
            str2Def.setReadWriteAccessor( store.getAccessor( clazz,
                                                             str2Def.getName() ) );

            Object x = clazz.newInstance();
            Object y = clazz.newInstance();

            long1Def.setValue( x,
                               Long.valueOf( 20 ) );
            long2Def.setValue( x,
                               Long.valueOf( 30 ) );
            doubleDef.setValue( x,
                                Double.valueOf( 50.0 ) );
            intDef.setValue( x,
                             Integer.valueOf( 10 ) );
            strDef.setValue( x,
                             "abc" );
            dateDef.setValue( x,
                              new Date( 1000 ) );
            str2Def.setValue( x,
                              "instance1" );

            long1Def.setValue( y,
                               Long.valueOf( 20 ) );
            long2Def.setValue( y,
                               Long.valueOf( 30 ) );
            doubleDef.setValue( y,
                                Double.valueOf( 50.0 ) );
            intDef.setValue( y,
                             Integer.valueOf( 10 ) );
            strDef.setValue( y,
                             "abc" );
            dateDef.setValue( y,
                              new Date( 1000 ) );
            str2Def.setValue( y,
                              "instance2" );

            Object o = new Object();

            assertThat(x.equals(x)).isTrue();

            assertThat(x.equals(o)).isFalse();

            assertThat(x.equals(y)).isTrue();

            intDef.setValue( y,
                             Integer.valueOf( 1 ) );
            assertThat(x.equals(y)).isFalse();

            intDef.setValue( y,
                             Integer.valueOf( 10 ) );
            strDef.setValue( y,
                             "xyz" );
            assertThat(x.equals(y)).isFalse();

            strDef.setValue( y,
                             null );
            assertThat(x.equals(y)).isFalse();

            strDef.setValue( y,
                             "abc" );
            dateDef.setValue( y,
                              new Date( 1 ) );
            assertThat(x.equals(y)).isFalse();

            dateDef.setValue( y,
                              null );
            assertThat(x.equals(y)).isFalse();

        } catch ( Exception e ) {
            e.printStackTrace();
            fail( "Exception not expected" );
        }
    }

    @Test
    public void testHashCode() {
        try {
            ClassBuilder builder = new TraitClassBuilderFactory().getBeanClassBuilder();

            ClassDefinition classDef = new ClassDefinition( "org.drools.TestClass3",
                                                            null,
                                                            new String[]{} );
            FieldDefinition intDef = new FieldDefinition( "intAttr",
                                                          "int",
                                                          true );
            FieldDefinition strDef = new FieldDefinition( "stringAttr",
                                                          "java.lang.String",
                                                          false );
            classDef.addField( intDef );
            classDef.addField( strDef );

            Class clazz = build(builder, classDef);
            intDef.setReadWriteAccessor( store.getAccessor( clazz,
                                                            intDef.getName() ) );
            strDef.setReadWriteAccessor( store.getAccessor( clazz,
                                                            strDef.getName() ) );

            Object x = clazz.newInstance();

            intDef.setValue( x,
                             new Integer( 10 ) );
            strDef.setValue( x,
                             "abc" );

            assertThat(x.hashCode()).as("Wrong hashcode calculation").isEqualTo(31 + 10);
            assertThat(x.hashCode()).as("Wrong hashcode calculation").isEqualTo(x.hashCode());

        } catch ( Exception e ) {
            e.printStackTrace();
            fail( "Exception not expected" );
        }
    }

    @Test
    public void testToString() {
        try {
            ClassBuilder builder = new TraitClassBuilderFactory().getBeanClassBuilder();

            ClassDefinition classDef = new ClassDefinition( "org.drools.TestClass4",
                                                            null,
                                                            new String[]{} );
            FieldDefinition long1Def = new FieldDefinition( "longAttr1",
                                                            "long",
                                                            true );
            FieldDefinition long2Def = new FieldDefinition( "longAttr2",
                                                            "long",
                                                            true );
            FieldDefinition doubleDef = new FieldDefinition( "doubleAttr",
                                                             "double",
                                                             true );
            FieldDefinition intDef = new FieldDefinition( "intAttr",
                                                          "int",
                                                          true );
            FieldDefinition strDef = new FieldDefinition( "stringAttr",
                                                          "java.lang.String",
                                                          true );
            FieldDefinition dateDef = new FieldDefinition( "dateAttr",
                                                           "java.util.Date",
                                                           true );
            FieldDefinition str2Def = new FieldDefinition( "stringAttr2",
                                                           "java.lang.String" );
            classDef.addField( long1Def );
            classDef.addField( long2Def );
            classDef.addField( doubleDef );
            classDef.addField( intDef );
            classDef.addField( strDef );
            classDef.addField( dateDef );
            classDef.addField( str2Def );

            Class clazz = build(builder, classDef);
            long1Def.setReadWriteAccessor( store.getAccessor( clazz,
                                                              long1Def.getName() ) );
            long2Def.setReadWriteAccessor( store.getAccessor( clazz,
                                                              long2Def.getName() ) );
            doubleDef.setReadWriteAccessor( store.getAccessor( clazz,
                                                               doubleDef.getName() ) );
            intDef.setReadWriteAccessor( store.getAccessor( clazz,
                                                            intDef.getName() ) );
            strDef.setReadWriteAccessor( store.getAccessor( clazz,
                                                            strDef.getName() ) );
            dateDef.setReadWriteAccessor( store.getAccessor( clazz,
                                                             dateDef.getName() ) );
            str2Def.setReadWriteAccessor( store.getAccessor( clazz,
                                                             str2Def.getName() ) );

            Object x = clazz.newInstance();

            long1Def.setValue( x,
                               new Long( 20 ) );
            long2Def.setValue( x,
                               new Long( 30 ) );
            doubleDef.setValue( x,
                                new Double( 50.0 ) );
            intDef.setValue( x,
                             new Integer( 10 ) );
            strDef.setValue( x,
                             "abc" );
            dateDef.setValue( x,
                              new Date( 1000 ) );
            str2Def.setValue( x,
                              "instance1" );

            String result = x.toString();

            assertThat(result.contains(long1Def.getName())).isTrue();
            assertThat(result.contains(long2Def.getName())).isTrue();
            assertThat(result.contains(doubleDef.getName())).isTrue();
            assertThat(result.contains(intDef.getName())).isTrue();
            assertThat(result.contains(strDef.getName())).isTrue();
            assertThat(result.contains(dateDef.getName())).isTrue();
            assertThat(result.contains(str2Def.getName())).isTrue();

        } catch ( Exception e ) {
            e.printStackTrace();
            fail( "Exception not expected" );
        }

    }

    @Test
    public void testConstructorWithFields() {
        try {
            ClassBuilder builder = new TraitClassBuilderFactory().getBeanClassBuilder();

            ClassDefinition classDef = new ClassDefinition( "org.drools.TestClass5",
                                                            null,
                                                            new String[]{} );

            String[] types = new String[]{"byte", "short", "int", "long", "float", "double", "char", "java.lang.String", "boolean"};
            FieldDefinition[] fields = new FieldDefinition[types.length];
            for ( int i = 0; i < types.length; i++ ) {
                String attrName = types[i].substring( types[i].lastIndexOf( '.' ) + 1 );
                attrName = attrName.substring( 0,
                                               1 ).toLowerCase() + attrName.substring( 1 ) + "Attr";
                fields[i] = new FieldDefinition( attrName, // attr name
                                                 types[i], // attr type
                                                 i % 2 == 0 ); // half of them are key
                classDef.addField( fields[i] );
            }

            Class clazz = build(builder, classDef);

            for ( FieldDefinition field : fields ) {
                field.setReadWriteAccessor( store.getAccessor( clazz,
                                                               field.getName() ) );
            }

            Constructor< ? >[] cons = clazz.getConstructors();

            assertThat(cons.length).isEqualTo(3);
            for ( Constructor< ? > c : cons ) {
                Class< ? >[] ptypes = c.getParameterTypes();
                if ( ptypes.length == 0 ) {
                    // default constructor
                } else if ( ptypes.length == fields.length ) {
                    // constructor with fields
                    for ( int i = 0; i < ptypes.length; i++ ) {
                        if ( !ptypes[i].equals( fields[i].getType() ) ) {
                            fail( "Wrong parameter in constructor. index=" + i + " expected=" + fields[i].getType() + " found=" + ptypes[i] );
                        }
                    }

                    // test actual invocation
                    Object instance = c.newInstance( (byte) 1,
                                                     (short) 2,
                                                     3,
                                                     4l,
                                                     5.0f,
                                                     6.0d,
                                                     'a',
                                                     "xyz",
                                                     true );

                    assertThat(fields[0].getValue(instance)).isEqualTo((byte) 1);
                    assertThat(fields[1].getValue(instance)).isEqualTo((short) 2);
                    assertThat(fields[2].getValue(instance)).isEqualTo(3);
                    assertThat(fields[3].getValue(instance)).isEqualTo(4l);
                    assertThat(fields[4].getValue(instance)).isEqualTo(5.0f);
                    assertThat(fields[5].getValue(instance)).isEqualTo(6.0d);
                    assertThat(fields[6].getValue(instance)).isEqualTo('a');
                    assertThat(fields[7].getValue(instance)).isEqualTo("xyz");
                    assertThat(fields[8].getValue(instance)).isEqualTo(true);
                } else if ( ptypes.length == ( fields.length / 2 +1 ) ) { // as defined in the beginning of the test
                    // constructor with key fields
                    int i = 0;
                    for ( FieldDefinition field : fields ) {
                        if ( field.isKey() && !ptypes[i++].equals( field.getType() ) ) {
                            fail( "Wrong parameter in constructor. index=" + i + " expected=" + field.getType() + " found=" + ptypes[i] );
                        }
                    }
                    // test actual invocation
                    Object instance = c.newInstance( (byte) 1,
                                                     3,
                                                     5.0f,
                                                     'a',
                                                     true );

                    assertThat(fields[0].getValue(instance)).isEqualTo((byte) 1);
                    assertThat(fields[2].getValue(instance)).isEqualTo(3);
                    assertThat(fields[4].getValue(instance)).isEqualTo(5.0f);
                    assertThat(fields[6].getValue(instance)).isEqualTo('a');
                    assertThat(fields[8].getValue(instance)).isEqualTo(true);
                    
                } else {
                    fail( "Unexpected constructor: " + c.toString() );
                }
            }

        } catch ( Exception e ) {
            e.printStackTrace();
            fail( "Unexpected Exception: " + e.getMessage() );
        }

    }
    
    @Test
    public void testGetResourcesJBRULES3122() {
        try {
            ClassBuilder builder = new TraitClassBuilderFactory().getBeanClassBuilder();

            ClassDefinition classDef = new ClassDefinition("org.drools.TestClass4", null, new String[] {});
            Class clazz = build(builder, classDef);
            ClassLoader cl = clazz.getClassLoader();

            // We expect normal classloader stuff to work
            assertThat(cl.getResources("not-there.txt").hasMoreElements()).isFalse();
        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception not expected: " + e.getMessage());
        }
    }
}
