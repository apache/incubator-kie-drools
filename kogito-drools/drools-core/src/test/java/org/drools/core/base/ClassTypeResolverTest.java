/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
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

package org.drools.core.base;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.HashSet;

import org.drools.core.test.model.Cheese;
import org.drools.core.test.model.FirstClass;
import org.drools.core.test.model.SecondClass;
import org.junit.Test;

public class ClassTypeResolverTest {

    @Test
    public void testResolvePrimtiveTypes() throws Exception {
        final ClassTypeResolver resolver = new ClassTypeResolver(new HashSet(), Thread.currentThread().getContextClassLoader());
        assertEquals( boolean.class,
                      resolver.resolveType( "boolean" ) );
        assertEquals( double.class,
                      resolver.resolveType( "double" ) );
        assertEquals( float.class,
                      resolver.resolveType( "float" ) );
        assertEquals( int.class,
                      resolver.resolveType( "int" ) );
        assertEquals( char.class,
                      resolver.resolveType( "char" ) );
        assertEquals( long.class,
                      resolver.resolveType( "long" ) );
        assertEquals( byte.class,
                      resolver.resolveType( "byte" ) );
        assertEquals( short.class,
                      resolver.resolveType( "short" ) );
    }

    @Test
    public void testResolveArrayOfPrimitiveTypes() throws Exception {
        final ClassTypeResolver resolver =  new ClassTypeResolver(new HashSet(), Thread.currentThread().getContextClassLoader());
        assertEquals( boolean[].class,
                      resolver.resolveType( "boolean[]" ) );
        assertEquals( double[].class,
                      resolver.resolveType( "double[]" ) );
        assertEquals( float[].class,
                      resolver.resolveType( "float[]" ) );
        assertEquals( int[].class,
                      resolver.resolveType( "int[]" ) );
        assertEquals( char[].class,
                      resolver.resolveType( "char[]" ) );
        assertEquals( long[].class,
                      resolver.resolveType( "long[]" ) );
        assertEquals( byte[].class,
                      resolver.resolveType( "byte[]" ) );
        assertEquals( short[].class,
                      resolver.resolveType( "short[]" ) );
    }

    @Test
    public void testResolveMultidimensionnalArrayOfPrimitiveTypes() throws Exception {
        final ClassTypeResolver resolver =  new ClassTypeResolver(new HashSet(), Thread.currentThread().getContextClassLoader());
        assertEquals( int[][].class,
                      resolver.resolveType( "int[][]" ) );
        assertEquals( int[][][].class,
                      resolver.resolveType( "int[][][]" ) );
        assertEquals( int[][][][].class,
                      resolver.resolveType( "int[][][][]" ) );
    }

    @Test
    public void testResolveObjectNotFromImport() throws Exception {
        final ClassTypeResolver resolver =  new ClassTypeResolver(new HashSet(), Thread.currentThread().getContextClassLoader());
        assertEquals( String.class,
                      resolver.resolveType( "String" ) );
        assertEquals( String.class,
                      resolver.resolveType( "java.lang.String" ) );
        try {
            assertEquals( Cheese.class,
                          resolver.resolveType( "Cheese" ) );
            fail( "Should raise a ClassNotFoundException" );
        } catch ( final ClassNotFoundException e ) {
            // success
        }
        assertEquals( Cheese.class,
                      resolver.resolveType( "org.drools.core.test.model.Cheese" ) );
    }

    @Test
    public void testResolveObjectFromImport() throws Exception {
        final ClassTypeResolver resolver = new ClassTypeResolver( new HashSet(), Thread.currentThread().getContextClassLoader() );
        resolver.addImport( "org.drools.core.test.model.Cheese" );
        resolver.addImport( "org.drools.core.test.model.FirstClass" );
        resolver.addImport( "org.drools.core.test.model.FirstClass.AlternativeKey" );
        resolver.addImport( "org.drools.core.test.model.SecondClass" );
        resolver.addImport( "org.drools.core.test.model.SecondClass.AlternativeKey" );

        assertEquals( String.class,
                      resolver.resolveType( "String" ) );
        assertEquals( String.class,
                      resolver.resolveType( "java.lang.String" ) );
        assertEquals( Cheese.class,
                      resolver.resolveType( "Cheese" ) );
        assertEquals( Cheese.class,
                      resolver.resolveType( "org.drools.core.test.model.Cheese" ) );
        assertEquals( FirstClass.class,
                      resolver.resolveType( "org.drools.core.test.model.FirstClass" ) );
        assertEquals( FirstClass.AlternativeKey.class,
                      resolver.resolveType( "org.drools.core.test.model.FirstClass.AlternativeKey" ) );

        assertEquals( SecondClass.class,
                      resolver.resolveType( "org.drools.core.test.model.SecondClass" ) );
        assertEquals( SecondClass.AlternativeKey.class,
                      resolver.resolveType( "org.drools.core.test.model.SecondClass.AlternativeKey" ) );
    }

    @Test
    public void testResolveObjectFromImportNested() throws Exception {
        final ClassTypeResolver resolver = new ClassTypeResolver( new HashSet(), Thread.currentThread().getContextClassLoader() );
        resolver.addImport( "org.drools.core.test.model.FirstClass" );

        assertEquals( FirstClass.AlternativeKey.class,
                      resolver.resolveType( "FirstClass.AlternativeKey" ) );
    }

    @Test
    public void testResolveFullTypeName() throws Exception {

        final TypeResolver resolver = new ClassTypeResolver( new HashSet(), Thread.currentThread().getContextClassLoader() );
        resolver.addImport( "org.drools.core.test.model.Cheese" );
        resolver.addImport( "org.drools.core.test.model.FirstClass" );

        assertEquals("org.drools.core.test.model.Cheese", resolver.getFullTypeName("Cheese"));
        assertEquals("org.drools.core.test.model.FirstClass", resolver.getFullTypeName("FirstClass"));



    }

    @Test
    public void testResolveObjectFromImportMultipleClassesDifferentPackages() throws Exception {
        final ClassTypeResolver resolver = new ClassTypeResolver( new HashSet(), Thread.currentThread().getContextClassLoader() );
        resolver.addImport( "org.drools.core.test.model.Cheese" );
        assertEquals( String.class,
                      resolver.resolveType( "String" ) );
        assertEquals( String.class,
                      resolver.resolveType( "java.lang.String" ) );
        assertEquals( Cheese.class,
                      resolver.resolveType( "Cheese" ) );
        assertEquals( Cheese.class,
                      resolver.resolveType( "org.drools.core.test.model.Cheese" ) );
    }

    @Test
    public void testResolveArrayOfObjectsNotFromImport() throws Exception {
        final ClassTypeResolver resolver =  new ClassTypeResolver(new HashSet(), Thread.currentThread().getContextClassLoader());
        assertEquals( String[].class,
                      resolver.resolveType( "String[]" ) );
        assertEquals( String[].class,
                      resolver.resolveType( "java.lang.String[]" ) );
        try {
            assertEquals( Cheese[].class,
                          resolver.resolveType( "Cheese[]" ) );
            fail( "Should raise a ClassNotFoundException" );
        } catch ( final ClassNotFoundException e ) {
            // success
        }
        assertEquals( Cheese[].class,
                      resolver.resolveType( "org.drools.core.test.model.Cheese[]" ) );
    }

    @Test
    public void testResolveArrayOfObjectsFromImport() throws Exception {
        final ClassTypeResolver resolver = new ClassTypeResolver( new HashSet(), Thread.currentThread().getContextClassLoader() );
        resolver.addImport( "org.drools.core.test.model.Cheese" );
        assertEquals( String[].class,
                      resolver.resolveType( "String[]" ) );
        assertEquals( String[].class,
                      resolver.resolveType( "java.lang.String[]" ) );
        assertEquals( Cheese[].class,
                      resolver.resolveType( "Cheese[]" ) );
        assertEquals( Cheese[].class,
                      resolver.resolveType( "org.drools.core.test.model.Cheese[]" ) );
    }

    @Test
    public void testResolveMultidimensionnalArrayOfObjectsNotFromImport() throws Exception {
        final ClassTypeResolver resolver =  new ClassTypeResolver(new HashSet(), Thread.currentThread().getContextClassLoader());
        assertEquals( String[][].class,
                      resolver.resolveType( "String[][]" ) );
        assertEquals( String[][].class,
                      resolver.resolveType( "java.lang.String[][]" ) );
        try {
            assertEquals( Cheese[][].class,
                          resolver.resolveType( "Cheese[][]" ) );
            fail( "Should raise a ClassNotFoundException" );
        } catch ( final ClassNotFoundException e ) {
            // success
        }
        assertEquals( Cheese[][].class,
                      resolver.resolveType( "org.drools.core.test.model.Cheese[][]" ) );
    }

    @Test
    public void testResolveMultidimensionnalArrayOfObjectsFromImport() throws Exception {
        final ClassTypeResolver resolver = new ClassTypeResolver( new HashSet(), Thread.currentThread().getContextClassLoader() );
        resolver.addImport( "org.drools.core.test.model.Cheese" );
        assertEquals( String[][].class,
                      resolver.resolveType( "String[][]" ) );
        assertEquals( String[][].class,
                      resolver.resolveType( "java.lang.String[][]" ) );
        assertEquals( Cheese[][].class,
                      resolver.resolveType( "Cheese[][]" ) );
        assertEquals( Cheese[][].class,
                      resolver.resolveType( "org.drools.core.test.model.Cheese[][]" ) );
    }
    
    @Test
    public void testDefaultPackageImport() throws Exception {
        final ClassTypeResolver resolver = new ClassTypeResolver( new HashSet(), Thread.currentThread().getContextClassLoader() );
        resolver.addImport( "Goo" );
        try {
            resolver.resolveType( "Goo" );
            fail( "Can't import default namespace classes");
        } catch ( ClassNotFoundException e) {
            // swallow as this should be thrown
        }
    }
    
    @Test
    public void testNestedClassResolving() throws Exception {
        final ClassTypeResolver resolver = new ClassTypeResolver( new HashSet(), Thread.currentThread().getContextClassLoader() );
        
        // single nesting
        resolver.addImport( "org.drools.core.test.model.Person.Nested1" );
        assertEquals( org.drools.core.test.model.Person.Nested1.class,
                      resolver.resolveType( "Nested1" ) );
        
        // double nesting
        resolver.addImport( "org.drools.core.test.model.Person.Nested1.Nested2" );
        assertEquals( org.drools.core.test.model.Person.Nested1.Nested2.class,
                      resolver.resolveType( "Nested2" ) );
        
        // triple nesting
        resolver.addImport( "org.drools.core.test.model.Person.Nested1.Nested2.Nested3" );
        assertEquals( org.drools.core.test.model.Person.Nested1.Nested2.Nested3.class,
                      resolver.resolveType( "Nested3" ) );
        
    }
    
}
