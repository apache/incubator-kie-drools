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

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class BigDecimalRangeIterator implements Iterator<BigDecimal> {

    private final BigDecimal start;
    private final BigDecimal end;

    private BigDecimal cursor;
    private final Direction direction;
    private final BigDecimal increment;

    public BigDecimalRangeIterator(BigDecimal start, BigDecimal end) {
        this.start = start;
        this.end = end;
        this.direction = (start.compareTo(end) <= 0) ? Direction.ASCENDANT : Direction.DESCENDANT;
        this.increment = (direction == Direction.ASCENDANT) ? new BigDecimal(1, MathContext.DECIMAL128) : new BigDecimal(-1, MathContext.DECIMAL128);
    }

    @Override
    public boolean hasNext() {
        if (cursor == null) {
            return true;
        } else {
            BigDecimal lookAhead = cursor.add(increment);
            if (direction == Direction.ASCENDANT) {
                return lookAhead.compareTo(end) <= 0;
            } else {
                return lookAhead.compareTo(end) >= 0;
            }
        }
    }

    @Override
    public BigDecimal next() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }
        if (cursor == null) {
            cursor = start;
        } else {
            cursor = cursor.add(increment);
        }
        return cursor;
    }

}