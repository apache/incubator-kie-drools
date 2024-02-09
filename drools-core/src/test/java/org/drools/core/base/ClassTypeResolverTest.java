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
package org.drools.core.base;

import java.util.HashSet;

import org.drools.core.test.model.Cheese;
import org.drools.core.test.model.FirstClass;
import org.drools.core.test.model.SecondClass;
import org.drools.util.ClassTypeResolver;
import org.drools.util.TypeResolver;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

public class ClassTypeResolverTest {

    @Test
    public void testResolveObjectNotFromImport() throws Exception {
        final ClassTypeResolver resolver = new ClassTypeResolver(new HashSet(), Thread.currentThread().getContextClassLoader());
        assertThat(resolver.resolveType("String")).isEqualTo(String.class);
        assertThat(resolver.resolveType("java.lang.String")).isEqualTo(String.class);
        try {
            assertThat(resolver.resolveType("Cheese")).isEqualTo(Cheese.class);
            fail("Should raise a ClassNotFoundException");
        } catch (final ClassNotFoundException e) {
            // success
        }
        assertThat(resolver.resolveType("org.drools.core.test.model.Cheese")).isEqualTo(Cheese.class);
    }

    @Test
    public void testResolveObjectFromImport() throws Exception {
        final ClassTypeResolver resolver = new ClassTypeResolver(new HashSet(), Thread.currentThread().getContextClassLoader());
        resolver.addImport("org.drools.core.test.model.Cheese");
        resolver.addImport("org.drools.core.test.model.FirstClass");
        resolver.addImport("org.drools.core.test.model.FirstClass.AlternativeKey");
        resolver.addImport("org.drools.core.test.model.SecondClass");
        resolver.addImport("org.drools.core.test.model.SecondClass.AlternativeKey");

        assertThat(resolver.resolveType("String")).isEqualTo(String.class);
        assertThat(resolver.resolveType("java.lang.String")).isEqualTo(String.class);
        assertThat(resolver.resolveType("Cheese")).isEqualTo(Cheese.class);
        assertThat(resolver.resolveType("org.drools.core.test.model.Cheese")).isEqualTo(Cheese.class);
        assertThat(resolver.resolveType("org.drools.core.test.model.FirstClass")).isEqualTo(FirstClass.class);
        assertThat(resolver.resolveType("org.drools.core.test.model.FirstClass.AlternativeKey")).isEqualTo(FirstClass.AlternativeKey.class);

        assertThat(resolver.resolveType("org.drools.core.test.model.SecondClass")).isEqualTo(SecondClass.class);
        assertThat(resolver.resolveType("org.drools.core.test.model.SecondClass.AlternativeKey")).isEqualTo(SecondClass.AlternativeKey.class);
    }

    @Test
    public void testResolveObjectFromImportNested() throws Exception {
        final ClassTypeResolver resolver = new ClassTypeResolver(new HashSet(), Thread.currentThread().getContextClassLoader());
        resolver.addImport("org.drools.core.test.model.FirstClass");

        assertThat(resolver.resolveType("FirstClass.AlternativeKey")).isEqualTo(FirstClass.AlternativeKey.class);
    }

    @Test
    public void testResolveFullTypeName() throws Exception {

        final TypeResolver resolver = new ClassTypeResolver(new HashSet(), Thread.currentThread().getContextClassLoader());
        resolver.addImport("org.drools.core.test.model.Cheese");
        resolver.addImport("org.drools.core.test.model.FirstClass");

        assertThat(resolver.getFullTypeName("Cheese")).isEqualTo("org.drools.core.test.model.Cheese");
        assertThat(resolver.getFullTypeName("FirstClass")).isEqualTo("org.drools.core.test.model.FirstClass");
    }

    @Test
    public void testResolveObjectFromImportMultipleClassesDifferentPackages() throws Exception {
        final ClassTypeResolver resolver = new ClassTypeResolver(new HashSet(), Thread.currentThread().getContextClassLoader());
        resolver.addImport("org.drools.core.test.model.Cheese");
        assertThat(resolver.resolveType("String")).isEqualTo(String.class);
        assertThat(resolver.resolveType("java.lang.String")).isEqualTo(String.class);
        assertThat(resolver.resolveType("Cheese")).isEqualTo(Cheese.class);
        assertThat(resolver.resolveType("org.drools.core.test.model.Cheese")).isEqualTo(Cheese.class);
    }

    @Test
    public void testResolveArrayOfObjectsNotFromImport() throws Exception {
        final ClassTypeResolver resolver = new ClassTypeResolver(new HashSet(), Thread.currentThread().getContextClassLoader());
        assertThat(resolver.resolveType("String[]")).isEqualTo(String[].class);
        assertThat(resolver.resolveType("java.lang.String[]")).isEqualTo(String[].class);
        try {
            assertThat(resolver.resolveType("Cheese[]")).isEqualTo(Cheese[].class);
            fail("Should raise a ClassNotFoundException");
        } catch (final ClassNotFoundException e) {
            // success
        }
        assertThat(resolver.resolveType("org.drools.core.test.model.Cheese[]")).isEqualTo(Cheese[].class);
    }

    @Test
    public void testResolveArrayOfObjectsFromImport() throws Exception {
        final ClassTypeResolver resolver = new ClassTypeResolver(new HashSet(), Thread.currentThread().getContextClassLoader());
        resolver.addImport("org.drools.core.test.model.Cheese");
        assertThat(resolver.resolveType("String[]")).isEqualTo(String[].class);
        assertThat(resolver.resolveType("java.lang.String[]")).isEqualTo(String[].class);
        assertThat(resolver.resolveType("Cheese[]")).isEqualTo(Cheese[].class);
        assertThat(resolver.resolveType("org.drools.core.test.model.Cheese[]")).isEqualTo(Cheese[].class);
    }

    @Test
    public void testResolveMultidimensionnalArrayOfObjectsNotFromImport() throws Exception {
        final ClassTypeResolver resolver = new ClassTypeResolver(new HashSet(), Thread.currentThread().getContextClassLoader());
        assertThat(resolver.resolveType("String[][]")).isEqualTo(String[][].class);
        assertThat(resolver.resolveType("java.lang.String[][]")).isEqualTo(String[][].class);
        try {
            assertThat(resolver.resolveType("Cheese[][]")).isEqualTo(Cheese[][].class);
            fail("Should raise a ClassNotFoundException");
        } catch (final ClassNotFoundException e) {
            // success
        }
        assertThat(resolver.resolveType("org.drools.core.test.model.Cheese[][]")).isEqualTo(Cheese[][].class);
    }

    @Test
    public void testResolveMultidimensionnalArrayOfObjectsFromImport() throws Exception {
        final ClassTypeResolver resolver = new ClassTypeResolver(new HashSet(), Thread.currentThread().getContextClassLoader());
        resolver.addImport("org.drools.core.test.model.Cheese");
        assertThat(resolver.resolveType("String[][]")).isEqualTo(String[][].class);
        assertThat(resolver.resolveType("java.lang.String[][]")).isEqualTo(String[][].class);
        assertThat(resolver.resolveType("Cheese[][]")).isEqualTo(Cheese[][].class);
        assertThat(resolver.resolveType("org.drools.core.test.model.Cheese[][]")).isEqualTo(Cheese[][].class);
    }

    @Test
    public void testDefaultPackageImport() throws Exception {
        final ClassTypeResolver resolver = new ClassTypeResolver(new HashSet(), Thread.currentThread().getContextClassLoader());
        resolver.addImport("Goo");
        try {
            resolver.resolveType("Goo");
            fail("Can't import default namespace classes");
        } catch (ClassNotFoundException e) {
            // swallow as this should be thrown
        }
    }

    @Test
    public void testNestedClassResolving() throws Exception {
        final ClassTypeResolver resolver = new ClassTypeResolver(new HashSet(), Thread.currentThread().getContextClassLoader());

        // single nesting
        resolver.addImport("org.drools.core.test.model.Person.Nested1");
        assertThat(resolver.resolveType("Nested1")).isEqualTo(org.drools.core.test.model.Person.Nested1.class);

        // double nesting
        resolver.addImport("org.drools.core.test.model.Person.Nested1.Nested2");
        assertThat(resolver.resolveType("Nested2")).isEqualTo(org.drools.core.test.model.Person.Nested1.Nested2.class);

        // triple nesting
        resolver.addImport("org.drools.core.test.model.Person.Nested1.Nested2.Nested3");
        assertThat(resolver.resolveType("Nested3")).isEqualTo(org.drools.core.test.model.Person.Nested1.Nested2.Nested3.class);
    }

    @Test
    public void testMacOSXClassLoaderBehavior() throws Exception {
        SimulateMacOSXClassLoader simulatedMacOSXClassLoader = new SimulateMacOSXClassLoader(Thread.currentThread().getContextClassLoader(), new HashSet());
        simulatedMacOSXClassLoader.addClassInScope(org.drools.core.test.model.Cheese.class);

        final ClassTypeResolver resolver = new ClassTypeResolver(new HashSet(), simulatedMacOSXClassLoader);
        resolver.addImport("org.drools.core.test.model.*");

        assertThat(resolver.resolveType("Cheese")).isEqualTo(org.drools.core.test.model.Cheese.class);
        try {
            resolver.resolveType("cheese");    // <<- on Mac/OSX throws NoClassDefFoundError which escapes the try/catch and fail the test.
            //     while on say Linux, it passes the test (catched as ClassNotFoundException)
            fail("the type cheese (lower-case c) should not exists at all");
        } catch (ClassNotFoundException e) {
            // swallow as this should be thrown
        }
    }

    @Test
    public void testMacOSXClassLoaderBehaviorNested() throws Exception {
        SimulateMacOSXClassLoader simulatedMacOSXClassLoader = new SimulateMacOSXClassLoader(Thread.currentThread().getContextClassLoader(), new HashSet());
        simulatedMacOSXClassLoader.addClassInScope(org.drools.core.test.model.Person.Nested1.Nested2.class);

        final ClassTypeResolver resolver = new ClassTypeResolver(new HashSet(), simulatedMacOSXClassLoader);
        resolver.addImport("org.drools.core.test.model.*");

        assertThat(resolver.resolveType("Person.Nested1.Nested2")).isEqualTo(org.drools.core.test.model.Person.Nested1.Nested2.class);
        try {
            resolver.resolveType("Person.nested1.nested2");    // <<- on Mac/OSX throws NoClassDefFoundError which escapes the try/catch and fail the test.
            //     while on say Linux, it passes the test (catched as ClassNotFoundException)
            fail("should have resolved nothing.");
        } catch (ClassNotFoundException e) {
            // swallow as this should be thrown
        }
    }
}
