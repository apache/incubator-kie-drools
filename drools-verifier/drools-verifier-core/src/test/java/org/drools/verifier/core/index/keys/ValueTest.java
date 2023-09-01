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
package org.drools.verifier.core.index.keys;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ValueTest {


    @Test
    void testIntegerVSInteger() throws Exception {
        final Value nroZero = new Value(0);
        final Value nroOne = new Value(1);

        assertThat(nroZero).isLessThan(nroOne);
        assertThat(nroOne).isGreaterThan(nroZero);
    }

    @Test
    void testStringVSInteger() throws Exception {
        final Value hello = new Value("hello");
        final Value nroOne = new Value(1);

        assertThat(hello).isGreaterThan(nroOne);
        assertThat(nroOne).isLessThan(hello);
    }

    @Test
    void testStringVSIntegerString() throws Exception {
        final Value hello = new Value("hello");
        final Value nroOne = new Value("1");

        assertThat(hello).isGreaterThan(nroOne);
        assertThat(nroOne).isLessThan(hello);
    }

    @Test
    void testStringVSString() throws Exception {
        final Value a = new Value("a");
        final Value b = new Value("b");

        assertThat(a).isLessThan(b);
        assertThat(b).isGreaterThan(a);
    }
}