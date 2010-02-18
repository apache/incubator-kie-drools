package org.drools.util;

import org.drools.core.util.ClassUtils;

import junit.framework.TestCase;

public class ClassUtilsTest extends TestCase {

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testCanonicalNameSimpleClass() {
        String name = ClassUtils.canonicalName( ClassUtilsTest.class );
        assertEquals( "org.drools.util.ClassUtilsTest",
                      name );
    }

    public void testCanonicalNameInnerClass() {
        String name = ClassUtils.canonicalName( A.class );
        assertEquals( "org.drools.util.ClassUtilsTest.A",
                      name );
    }
    
    public void testCanonicalNameInnerInnerClass() {
        String name = ClassUtils.canonicalName( A.B.class );
        assertEquals( "org.drools.util.ClassUtilsTest.A.B",
                      name );
    }
    
    public void testCanonicalNameArray() {
        String name = ClassUtils.canonicalName( Object[].class );
        assertEquals( "java.lang.Object[]",
                      name );
    }
    
    public void testCanonicalNameMultiIndexArray() {
        String name = ClassUtils.canonicalName( Object[][][].class );
        assertEquals( "java.lang.Object[][][]",
                      name );
    }
    
    public void testCanonicalNameMultiIndexArrayInnerClass() {
        String name = ClassUtils.canonicalName( A.B[][][].class );
        assertEquals( "org.drools.util.ClassUtilsTest.A.B[][][]",
                      name );
    }
    
    public void testCanonicalNameMultiIndexArrayPrimitives() {
        String name = ClassUtils.canonicalName( long[][][].class );
        assertEquals( "long[][][]",
                      name );
    }
    
    public static class A {
        public static class B {
        }
    }
}
