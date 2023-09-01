package org.drools.core.util;

import org.drools.util.ClassUtils;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ClassUtilsTest {

    @Test
    public void testCanonicalNameSimpleClass() {
        String name = ClassUtils.canonicalName( ClassUtilsTest.class );
        assertThat(name).isEqualTo("org.drools.core.util.ClassUtilsTest");
    }

    @Test
    public void testCanonicalNameInnerClass() {
        String name = ClassUtils.canonicalName( A.class );
        assertThat(name).isEqualTo("org.drools.core.util.ClassUtilsTest.A");
    }
    
    @Test
    public void testCanonicalNameInnerInnerClass() {
        String name = ClassUtils.canonicalName( A.B.class );
        assertThat(name).isEqualTo("org.drools.core.util.ClassUtilsTest.A.B");
    }
    
    @Test
    public void testCanonicalNameArray() {
        String name = ClassUtils.canonicalName( Object[].class );
        assertThat(name).isEqualTo("java.lang.Object[]");
    }
    
    @Test
    public void testCanonicalNameMultiIndexArray() {
        String name = ClassUtils.canonicalName( Object[][][].class );
        assertThat(name).isEqualTo("java.lang.Object[][][]");
    }
    
    @Test
    public void testCanonicalNameMultiIndexArrayInnerClass() {
        String name = ClassUtils.canonicalName( A.B[][][].class );
        assertThat(name).isEqualTo("org.drools.core.util.ClassUtilsTest.A.B[][][]");
    }
    
    @Test
    public void testCanonicalNameMultiIndexArrayPrimitives() {
        String name = ClassUtils.canonicalName( long[][][].class );
        assertThat(name).isEqualTo("long[][][]");
    }
    
    public static class A {
        public static class B {
        }
    }
}
