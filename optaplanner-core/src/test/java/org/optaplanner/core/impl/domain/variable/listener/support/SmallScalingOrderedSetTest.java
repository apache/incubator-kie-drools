/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.core.impl.domain.variable.listener.support;

import java.util.Arrays;

import org.junit.Test;

import static org.junit.Assert.*;

public class SmallScalingOrderedSetTest {

    @Test
    public void addRemoveAroundThreshold() {
        SmallScalingOrderedSet<String> set = new SmallScalingOrderedSet<>();
        assertTrue(set.add("s1"));
        assertFalse(set.add("s1"));
        assertTrue(set.add("s2"));
        assertFalse(set.add("s1"));
        assertFalse(set.add("s2"));
        assertTrue(set.remove("s2"));
        assertFalse(set.remove("s2"));
        assertTrue(set.add("s2"));
        assertEquals(2, set.size());
        assertTrue(set.contains("s1"));
        assertTrue(set.contains("s2"));

        for (int i = 0; i < SmallScalingOrderedSet.LIST_SIZE_THRESHOLD - 3; i++) {
            set.add("filler " + i);
        }
        assertFalse(set.add("s2"));
        assertTrue(set.add("s3"));
        assertFalse(set.add("s2"));
        assertEquals(SmallScalingOrderedSet.LIST_SIZE_THRESHOLD, set.size());
        assertTrue(set.add("s4"));
        assertFalse(set.add("s2"));
        assertFalse(set.add("s3"));
        assertFalse(set.add("s4"));
        assertEquals(SmallScalingOrderedSet.LIST_SIZE_THRESHOLD + 1, set.size());
        assertTrue(set.remove("s4"));
        assertFalse(set.add("s2"));
        assertFalse(set.add("s3"));
        assertEquals(SmallScalingOrderedSet.LIST_SIZE_THRESHOLD, set.size());
        assertTrue(set.add("s5"));
        assertFalse(set.add("s2"));
        assertFalse(set.add("s3"));
        assertEquals(SmallScalingOrderedSet.LIST_SIZE_THRESHOLD + 1, set.size());
        assertTrue(set.add("s6"));
        assertFalse(set.add("s2"));
        assertFalse(set.add("s3"));
        assertEquals(SmallScalingOrderedSet.LIST_SIZE_THRESHOLD + 2, set.size());
        assertTrue(set.contains("s1"));
        assertTrue(set.contains("s2"));
        assertTrue(set.contains("s3"));
        assertFalse(set.contains("s4"));
        assertTrue(set.contains("s5"));
        assertTrue(set.contains("s6"));
    }

    @Test
    public void addAllAroundThreshold() {
        SmallScalingOrderedSet<String> set = new SmallScalingOrderedSet<>();
        assertTrue(set.addAll(Arrays.asList("s1", "s2", "s3")));
        assertEquals(3, set.size());
        assertTrue(set.addAll(Arrays.asList("s1", "s3", "s4", "s5")));
        assertFalse(set.addAll(Arrays.asList("s1", "s2", "s4")));
        assertEquals(5, set.size());
        assertTrue(set.contains("s1"));
        assertTrue(set.contains("s2"));
        assertTrue(set.contains("s3"));
        assertTrue(set.contains("s4"));
        assertTrue(set.contains("s5"));

        for (int i = 0; i < SmallScalingOrderedSet.LIST_SIZE_THRESHOLD - 7; i++) {
            set.add("filler " + i);
        }
        assertEquals(SmallScalingOrderedSet.LIST_SIZE_THRESHOLD - 2, set.size());
        assertTrue(set.addAll(Arrays.asList("s6", "s7", "s2", "s3", "s8", "s9")));
        assertEquals(SmallScalingOrderedSet.LIST_SIZE_THRESHOLD + 2, set.size());
        assertTrue(set.remove("s1"));
        assertTrue(set.remove("s5"));
        assertEquals(SmallScalingOrderedSet.LIST_SIZE_THRESHOLD, set.size());
        assertTrue(set.addAll(Arrays.asList("s1", "s2", "s10")));
        assertEquals(SmallScalingOrderedSet.LIST_SIZE_THRESHOLD + 2, set.size());
        assertTrue(set.contains("s1"));
        assertTrue(set.contains("s2"));
        assertTrue(set.contains("s3"));
        assertTrue(set.contains("s4"));
        assertFalse(set.contains("s5"));
        assertTrue(set.contains("s6"));
        assertTrue(set.contains("s7"));
        assertTrue(set.contains("s8"));
        assertTrue(set.contains("s9"));
        assertTrue(set.contains("s10"));
    }

}
