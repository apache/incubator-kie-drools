/**
 * 
 */
package org.codehaus.jfdi.interpreter;

import java.util.ArrayList;

import junit.framework.TestCase;

import org.drools.Cheese;

/**
 * @author fburlet
 *
 */
public class ClassTypeResolverTest extends TestCase {

    public void testResolvePrimtiveTypes() throws Exception {
        final ClassTypeResolver resolver = new ClassTypeResolver();
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

    public void testResolveArrayOfPrimitiveTypes() throws Exception {
        final ClassTypeResolver resolver = new ClassTypeResolver();
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

    public void testResolveMultidimensionnalArrayOfPrimitiveTypes() throws Exception {
        final ClassTypeResolver resolver = new ClassTypeResolver();
        assertEquals( int[][].class,
                      resolver.resolveType( "int[][]" ) );
        assertEquals( int[][][].class,
                      resolver.resolveType( "int[][][]" ) );
        assertEquals( int[][][][].class,
                      resolver.resolveType( "int[][][][]" ) );
    }

    public void testResolveObjectNotFromImport() throws Exception {
        final ClassTypeResolver resolver = new ClassTypeResolver();
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
                      resolver.resolveType( "org.drools.Cheese" ) );
    }

    public void testResolveObjectFromImport() throws Exception {
        final ClassTypeResolver resolver = new ClassTypeResolver( new ArrayList() );
        resolver.addImport( "org.drools.Cheese" );
        assertEquals( String.class,
                      resolver.resolveType( "String" ) );
        assertEquals( String.class,
                      resolver.resolveType( "java.lang.String" ) );
        assertEquals( Cheese.class,
                      resolver.resolveType( "Cheese" ) );
        assertEquals( Cheese.class,
                      resolver.resolveType( "org.drools.Cheese" ) );
    }

    public void testResolveArrayOfObjectsNotFromImport() throws Exception {
        final ClassTypeResolver resolver = new ClassTypeResolver();
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
                      resolver.resolveType( "org.drools.Cheese[]" ) );
    }

    public void testResolveArrayOfObjectsFromImport() throws Exception {
        final ClassTypeResolver resolver = new ClassTypeResolver( new ArrayList() );
        resolver.addImport( "org.drools.Cheese" );
        assertEquals( String[].class,
                      resolver.resolveType( "String[]" ) );
        assertEquals( String[].class,
                      resolver.resolveType( "java.lang.String[]" ) );
        assertEquals( Cheese[].class,
                      resolver.resolveType( "Cheese[]" ) );
        assertEquals( Cheese[].class,
                      resolver.resolveType( "org.drools.Cheese[]" ) );
    }

    public void testResolveMultidimensionnalArrayOfObjectsNotFromImport() throws Exception {
        final ClassTypeResolver resolver = new ClassTypeResolver();
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
                      resolver.resolveType( "org.drools.Cheese[][]" ) );
    }

    public void testResolveMultidimensionnalArrayOfObjectsFromImport() throws Exception {
        final ClassTypeResolver resolver = new ClassTypeResolver( new ArrayList() );
        resolver.addImport( "org.drools.Cheese" );
        assertEquals( String[][].class,
                      resolver.resolveType( "String[][]" ) );
        assertEquals( String[][].class,
                      resolver.resolveType( "java.lang.String[][]" ) );
        assertEquals( Cheese[][].class,
                      resolver.resolveType( "Cheese[][]" ) );
        assertEquals( Cheese[][].class,
                      resolver.resolveType( "org.drools.Cheese[][]" ) );
    }
}
