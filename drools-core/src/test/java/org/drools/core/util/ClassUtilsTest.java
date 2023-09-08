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
