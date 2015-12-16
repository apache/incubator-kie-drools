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

package org.drools.core.util;

import org.junit.Test;
import static org.junit.Assert.*;

public class ClassUtilsTest {

    @Test
    public void testCanonicalNameSimpleClass() {
        String name = ClassUtils.canonicalName( ClassUtilsTest.class );
        assertEquals( "org.drools.core.util.ClassUtilsTest",
                      name );
    }

    @Test
    public void testCanonicalNameInnerClass() {
        String name = ClassUtils.canonicalName( A.class );
        assertEquals( "org.drools.core.util.ClassUtilsTest.A",
                      name );
    }
    
    @Test
    public void testCanonicalNameInnerInnerClass() {
        String name = ClassUtils.canonicalName( A.B.class );
        assertEquals( "org.drools.core.util.ClassUtilsTest.A.B",
                      name );
    }
    
    @Test
    public void testCanonicalNameArray() {
        String name = ClassUtils.canonicalName( Object[].class );
        assertEquals( "java.lang.Object[]",
                      name );
    }
    
    @Test
    public void testCanonicalNameMultiIndexArray() {
        String name = ClassUtils.canonicalName( Object[][][].class );
        assertEquals( "java.lang.Object[][][]",
                      name );
    }
    
    @Test
    public void testCanonicalNameMultiIndexArrayInnerClass() {
        String name = ClassUtils.canonicalName( A.B[][][].class );
        assertEquals( "org.drools.core.util.ClassUtilsTest.A.B[][][]",
                      name );
    }
    
    @Test
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
