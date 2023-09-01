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
package org.drools.verifier.core.index.model;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

public class ActivationTimeTest {


    public static Collection<Object[]> testData() {
        return Arrays.asList(new Object[][]{
                {new ActivationTime(new Date(0), new Date(10)), new ActivationTime(new Date(0), new Date(10)), true},
                {new ActivationTime(new Date(0), new Date(10)), new ActivationTime(new Date(10), new Date(100)), true},
                {new ActivationTime(new Date(10), new Date(20)), new ActivationTime(new Date(0), new Date(100)), true},
                {new ActivationTime(new Date(0), new Date(100)), new ActivationTime(new Date(10), new Date(20)), true},
                {new ActivationTime(new Date(0), new Date(100)), new ActivationTime(new Date(0), new Date(20)), true},
                {new ActivationTime(new Date(0), new Date(100)), new ActivationTime(new Date(0), new Date(20)), true},
                {new ActivationTime(new Date(0), new Date(100)), new ActivationTime(new Date(10), new Date(100)), true},
                {new ActivationTime(new Date(10), new Date(100)), new ActivationTime(new Date(0), new Date(100)), true},
                {new ActivationTime(new Date(10), new Date(100)), new ActivationTime(new Date(0), new Date(10)), true},
                {new ActivationTime(new Date(0), new Date(10)), new ActivationTime(new Date(100), new Date(110)), false},
                {new ActivationTime(new Date(100), new Date(110)), new ActivationTime(new Date(0), new Date(10)), false},
                {new ActivationTime(null, new Date(100)), new ActivationTime(new Date(10), null), true},
                {new ActivationTime(null, new Date(10)), new ActivationTime(new Date(100), null), false},
                {new ActivationTime(new Date(10), null), new ActivationTime(null, new Date(100)), true},
                {new ActivationTime(new Date(100), null), new ActivationTime(null, new Date(10)), false},
                {new ActivationTime(null, null), new ActivationTime(null, null), true},
                {new ActivationTime(null, null), new ActivationTime(new Date(100), new Date(110)), true},
                {new ActivationTime(new Date(100), new Date(110)), new ActivationTime(null, null), true},
                {new ActivationTime(null, null), new ActivationTime(null, new Date(110)), true},
                {new ActivationTime(null, new Date(110)), new ActivationTime(null, null), true},
                {new ActivationTime(null, null), new ActivationTime(new Date(100), null), true},
                {new ActivationTime(new Date(100), null), new ActivationTime(null, null), true},
        });
    }

    @MethodSource("testData")
    @ParameterizedTest
    void testOverlaps(final ActivationTime start, final ActivationTime end, final boolean expected) {
        assertThat(start.overlaps(end)).isEqualTo(expected);
    }
}