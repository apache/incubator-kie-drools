/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.workbench.models.commons.backend.rule;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SplitterTest {

    @Test
    public void testSimple() {
        String[] split = Splitter.split("test.method()");
        assertEquals(2, split.length);
        assertEquals("test", split[0]);
        assertEquals("method()", split[1]);
    }

    @Test
    public void testLong() {
        String[] split = Splitter.split("test.method().Something().VALUE");
        assertEquals(4, split.length);
        assertEquals("test", split[0]);
        assertEquals("method()", split[1]);
        assertEquals("Something()", split[2]);
        assertEquals("VALUE", split[3]);
    }

    @Test
    public void testNested() {
        String[] split = Splitter.split("test( $var.get() )");
        assertEquals(1, split.length);
        assertEquals("test( $var.get() )", split[0]);
    }

    @Test
    public void testNestedComplex() {
        String[] split = Splitter.split("var.test( $var.get() ).more( 12, 14)");
        assertEquals(3, split.length);
        assertEquals("var", split[0]);
        assertEquals("test( $var.get() )", split[1]);
        assertEquals("more( 12, 14)", split[2]);
    }

}