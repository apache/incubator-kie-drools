/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.verifier.core.index.model;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class ActivationTimeTest {

    private final ActivationTime start;
    private final ActivationTime end;
    private final boolean expected;

    public ActivationTimeTest(final ActivationTime start,
                              final ActivationTime end,
                              final boolean expected) {
        this.start = start;
        this.end = end;
        this.expected = expected;
    }

    @Parameterized.Parameters
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

    @Test
    public void testOverlaps() {
        assertEquals(expected, start.overlaps(end));
    }
}