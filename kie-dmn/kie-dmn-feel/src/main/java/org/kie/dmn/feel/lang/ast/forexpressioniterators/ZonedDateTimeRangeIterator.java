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

import java.time.ZonedDateTime;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class ZonedDateTimeRangeIterator implements Iterator<ZonedDateTime> {

    private final ZonedDateTime start;
    private final ZonedDateTime end;

    private ZonedDateTime cursor;
    private final Direction direction;
    private final int increment;

    public ZonedDateTimeRangeIterator(ZonedDateTime start, ZonedDateTime end) {
        // datetime
        this.start = start;
        this.end = end;
        this.direction = (!start.isAfter(end)) ? Direction.ASCENDANT : Direction.DESCENDANT;
        this.increment = (direction == Direction.ASCENDANT) ? 1 : -1;
    }

    @Override
    public boolean hasNext() {
        if (cursor == null) {
            return true;
        } else {
            ZonedDateTime lookAhead = cursor.plusDays(increment);
            if (direction == Direction.ASCENDANT) {
                return !lookAhead.isAfter(end);
            } else {
                return !lookAhead.isBefore(end);
            }
        }
    }

    @Override
    public ZonedDateTime next() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }
        if (cursor == null) {
            cursor = start;
        } else {
            cursor = cursor.plusDays(increment);
        }
        return cursor;
    }

}