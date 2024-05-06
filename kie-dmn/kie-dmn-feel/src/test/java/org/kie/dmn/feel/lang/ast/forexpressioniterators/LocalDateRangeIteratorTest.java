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
package org.kie.dmn.feel.lang.ast.forexpressioniterators;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LocalDateRangeIteratorTest {

    private static LocalDate before;
    private static LocalDate after;

    @BeforeAll
    static void setup() {
        before = LocalDate.of(2021, 1, 1);
        after = LocalDate.of(2021, 1, 3);
    }

    @Test
    void hasNextAscendantTest() {
        LocalDateRangeIterator iterator = new LocalDateRangeIterator(before, after);
        assertTrue(iterator.hasNext());
        LocalDate next = iterator.next();
        while (!next.equals(after)) {
            assertTrue(iterator.hasNext());
            next = iterator.next();
        }
        assertFalse(iterator.hasNext());
    }

    @Test
    void hasNextDescendantTest() {
        LocalDateRangeIterator iterator = new LocalDateRangeIterator(after, before);
        assertTrue(iterator.hasNext());
        LocalDate next = iterator.next();
        while (!next.equals(before)) {
            assertTrue(iterator.hasNext());
            next = iterator.next();
        }
        assertFalse(iterator.hasNext());
    }

    @Test
    void nextAscendantTest() {
        List<LocalDate> expected = Arrays.asList(LocalDate.of(2021, 1, 1), LocalDate.of(2021, 1, 2), LocalDate.of(2021, 1, 3));
        LocalDateRangeIterator iterator = new LocalDateRangeIterator(before, after);
        IntStream.range(0, 3).forEach(i -> assertEquals(expected.get(i), iterator.next()));
    }

    @Test
    void nextDescendantTest() {
        List<LocalDate> expected = Arrays.asList(LocalDate.of(2021, 1, 3), LocalDate.of(2021, 1, 2), LocalDate.of(2021, 1, 1));
        LocalDateRangeIterator iterator = new LocalDateRangeIterator(after, before);
        IntStream.range(0, 3).forEach(i -> assertEquals(expected.get(i), iterator.next()));
    }


}