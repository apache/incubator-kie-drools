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

import org.junit.Test;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class BigDecimalRangeIteratorTest {

    @Test
    public void hasNextAscendantTest() {
        BigDecimal start = BigDecimal.valueOf(1);
        BigDecimal end = BigDecimal.valueOf(3);
        BigDecimalRangeIterator iterator = new BigDecimalRangeIterator(start, end);
        assertTrue(iterator.hasNext());
        BigDecimal next = iterator.next();
        while (!next.equals(end)) {
            assertTrue(iterator.hasNext());
            next = iterator.next();
        }
        assertFalse(iterator.hasNext());
    }

    @Test
    public void hasNextDescendantTest() {
        BigDecimal start = BigDecimal.valueOf(3);
        BigDecimal end = BigDecimal.valueOf(1);
        BigDecimalRangeIterator iterator = new BigDecimalRangeIterator(start, end);
        assertTrue(iterator.hasNext());
        BigDecimal next = iterator.next();
        while (!next.equals(end)) {
            assertTrue(iterator.hasNext());
            next = iterator.next();
        }
        assertFalse(iterator.hasNext());
    }

    @Test
    public void nextAscendantTest() {
        BigDecimal start = BigDecimal.valueOf(1);
        BigDecimal end = BigDecimal.valueOf(3);
        List<BigDecimal> expected = Arrays.asList(BigDecimal.valueOf(1), BigDecimal.valueOf(2), BigDecimal.valueOf(3));
        BigDecimalRangeIterator iterator = new BigDecimalRangeIterator(start, end);
        IntStream.range(0, 3).forEach(i -> assertEquals(expected.get(i), iterator.next()));
    }

    @Test
    public void nextDescendantTest() {
        BigDecimal start = BigDecimal.valueOf(3);
        BigDecimal end = BigDecimal.valueOf(1);
        List<BigDecimal> expected = Arrays.asList(BigDecimal.valueOf(3), BigDecimal.valueOf(2), BigDecimal.valueOf(1));
        BigDecimalRangeIterator iterator = new BigDecimalRangeIterator(start, end);
        IntStream.range(0, 3).forEach(i -> assertEquals(expected.get(i), iterator.next()));
    }


}