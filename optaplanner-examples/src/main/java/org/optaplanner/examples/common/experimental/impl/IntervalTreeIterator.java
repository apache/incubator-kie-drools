/*
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

package org.optaplanner.examples.common.experimental.impl;

import java.util.Iterator;

final class IntervalTreeIterator<Interval_, Point_ extends Comparable<Point_>> implements Iterator<Interval_> {

    private final Iterator<IntervalSplitPoint<Interval_, Point_>> splitPointSetIterator;
    private Iterator<Interval_> splitPointValueIterator;

    IntervalTreeIterator(Iterable<IntervalSplitPoint<Interval_, Point_>> splitPointSet) {
        this.splitPointSetIterator = splitPointSet.iterator();
        if (splitPointSetIterator.hasNext()) {
            splitPointValueIterator = splitPointSetIterator.next().getValuesStartingFromSplitPointIterator();
        }
    }

    @Override
    public boolean hasNext() {
        return splitPointValueIterator != null && splitPointValueIterator.hasNext();
    }

    @Override
    public Interval_ next() {
        Interval_ next = splitPointValueIterator.next();

        while (!splitPointValueIterator.hasNext() && splitPointSetIterator.hasNext()) {
            splitPointValueIterator = splitPointSetIterator.next().getValuesStartingFromSplitPointIterator();
        }

        if (!splitPointValueIterator.hasNext()) {
            splitPointValueIterator = null;
        }

        return next;
    }
}
