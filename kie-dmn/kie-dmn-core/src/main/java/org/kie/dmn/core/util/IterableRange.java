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
package org.kie.dmn.core.util;

import java.math.BigDecimal;
import java.util.Iterator;
import java.util.NoSuchElementException;

import org.kie.dmn.feel.runtime.Range;
import org.kie.dmn.feel.runtime.Range.RangeBoundary;

public class IterableRange implements Iterable<BigDecimal> {

    private final static BigDecimal STEP = new BigDecimal(1);

    private final Range range;

    public IterableRange(Range range) {
        this.range = range;
    }

    @Override
    public Iterator<BigDecimal> iterator() {
        return new IterableRangeIterator();
    }

    private class IterableRangeIterator implements Iterator<BigDecimal> {

        private BigDecimal current = null;
        private final BigDecimal from = (BigDecimal) range.getLowEndPoint();
        private final BigDecimal to = (BigDecimal) range.getHighEndPoint();

        @Override
        public boolean hasNext() {
            if (current == null) {
                return true;
            }
            if (range.getHighBoundary() == RangeBoundary.CLOSED) {
                return current.add(STEP).compareTo(to) <= 0;
            } else {
                return current.add(STEP).compareTo(to) < 0;
            }
        }

        @Override
        public BigDecimal next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }

            if (current == null) {
                if (range.getLowBoundary() == RangeBoundary.CLOSED) {
                    current = from;
                } else {
                    current = from.add(STEP);
                }
            } else {
                current = current.add(STEP);
            }
            return current;
        }

    }

}
