/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.core.impl.score.stream.common;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.optaplanner.core.impl.score.stream.common.JoinerType.CONTAINING;
import static org.optaplanner.core.impl.score.stream.common.JoinerType.DISJOINT;
import static org.optaplanner.core.impl.score.stream.common.JoinerType.EQUAL;
import static org.optaplanner.core.impl.score.stream.common.JoinerType.GREATER_THAN;
import static org.optaplanner.core.impl.score.stream.common.JoinerType.GREATER_THAN_OR_EQUAL;
import static org.optaplanner.core.impl.score.stream.common.JoinerType.INTERSECTING;
import static org.optaplanner.core.impl.score.stream.common.JoinerType.LESS_THAN;
import static org.optaplanner.core.impl.score.stream.common.JoinerType.LESS_THAN_OR_EQUAL;

public class JoinerTypeTest {

    @Test
    public void equals() {
        assertTrue(EQUAL.matches(1, 1));
        assertFalse(EQUAL.matches(1, 2));
        assertFalse(EQUAL.matches(1, null));
        assertFalse(EQUAL.matches(null, 1));
    }

    @Test
    public void lessThan() {
        assertFalse(LESS_THAN.matches(1, 1));
        assertTrue(LESS_THAN.matches(1, 2));
        assertFalse(LESS_THAN.matches(2, 1));
    }

    @Test
    public void lessThanOrEquals() {
        assertTrue(LESS_THAN_OR_EQUAL.matches(1, 1));
        assertTrue(LESS_THAN_OR_EQUAL.matches(1, 2));
        assertFalse(LESS_THAN_OR_EQUAL.matches(2, 1));
    }

    @Test
    public void greaterThan() {
        assertFalse(GREATER_THAN.matches(1, 1));
        assertTrue(GREATER_THAN.matches(2, 1));
        assertFalse(GREATER_THAN.matches(1, 2));
    }

    @Test
    public void greaterThanOrEquals() {
        assertTrue(GREATER_THAN_OR_EQUAL.matches(1, 1));
        assertTrue(GREATER_THAN_OR_EQUAL.matches(2, 1));
        assertFalse(GREATER_THAN_OR_EQUAL.matches(1, 2));
    }

    @Test
    public void containing() {
        Collection<Integer> collection = Arrays.asList(1);
        assertTrue(CONTAINING.matches(collection, 1));
        assertFalse(CONTAINING.matches(collection, 2));
    }

    @Test
    public void intersecting() {
        Collection<Integer> left = Arrays.asList(1, 2, 3);
        Collection<Integer> right = Arrays.asList(3, 4, 5);
        assertTrue(INTERSECTING.matches(left, right));
        assertTrue(INTERSECTING.matches(right, left));
        assertFalse(INTERSECTING.matches(left, Collections.emptySet()));
    }

    @Test
    public void disjoint() {
        Collection<Integer> first = Arrays.asList(1, 2, 3);
        Collection<Integer> second = Arrays.asList(3, 4, 5);
        assertFalse(DISJOINT.matches(first, second));
        assertFalse(DISJOINT.matches(second, first));
        Collection<Integer> third = Arrays.asList(4, 5);
        assertTrue(DISJOINT.matches(first, third));
        assertTrue(DISJOINT.matches(third, first));
        // empty sets are disjoint
        assertTrue(DISJOINT.matches(Collections.emptyList(), Collections.emptySet()));
        assertTrue(DISJOINT.matches(first, Collections.emptySet()));
    }
}
