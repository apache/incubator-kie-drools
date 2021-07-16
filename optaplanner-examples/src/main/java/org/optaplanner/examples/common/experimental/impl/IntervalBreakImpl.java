/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.examples.common.experimental.impl;

import java.util.Objects;

import org.optaplanner.examples.common.experimental.api.IntervalBreak;
import org.optaplanner.examples.common.experimental.api.IntervalCluster;

class IntervalBreakImpl<Interval_, Point_ extends Comparable<Point_>, Difference_ extends Comparable<Difference_>>
        implements IntervalBreak<Interval_, Point_, Difference_> {
    private IntervalCluster<Interval_, Point_, Difference_> previousCluster;
    private IntervalCluster<Interval_, Point_, Difference_> nextCluster;
    private Difference_ length;

    public IntervalBreakImpl(IntervalCluster<Interval_, Point_, Difference_> previousCluster,
            IntervalCluster<Interval_, Point_, Difference_> nextCluster,
            Difference_ length) {
        this.previousCluster = previousCluster;
        this.nextCluster = nextCluster;
        this.length = length;
    }

    @Override
    public IntervalCluster<Interval_, Point_, Difference_> getPreviousIntervalCluster() {
        return previousCluster;
    }

    @Override
    public IntervalCluster<Interval_, Point_, Difference_> getNextIntervalCluster() {
        return nextCluster;
    }

    @Override
    public Difference_ getLength() {
        return length;
    }

    public void setPreviousCluster(
            IntervalCluster<Interval_, Point_, Difference_> previousCluster) {
        this.previousCluster = previousCluster;
    }

    public void setNextCluster(
            IntervalCluster<Interval_, Point_, Difference_> nextCluster) {
        this.nextCluster = nextCluster;
    }

    public void setLength(Difference_ length) {
        this.length = length;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        IntervalBreakImpl<?, ?, ?> that = (IntervalBreakImpl<?, ?, ?>) o;
        return Objects.equals(previousCluster, that.previousCluster) && Objects
                .equals(nextCluster, that.nextCluster) && Objects.equals(length, that.length);
    }

    @Override
    public int hashCode() {
        return Objects.hash(previousCluster, nextCluster, length);
    }

    @Override
    public String toString() {
        return "IntervalBreak{" +
                "previousCluster=" + previousCluster +
                ", nextCluster=" + nextCluster +
                ", length=" + length +
                '}';
    }
}
