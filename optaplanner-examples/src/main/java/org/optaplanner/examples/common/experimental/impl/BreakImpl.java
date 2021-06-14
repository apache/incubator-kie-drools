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

import org.optaplanner.examples.common.experimental.api.Break;
import org.optaplanner.examples.common.experimental.api.Sequence;

class BreakImpl<ValueType_, DifferenceType_ extends Comparable<DifferenceType_>>
        implements Break<ValueType_, DifferenceType_> {
    private Sequence<ValueType_, DifferenceType_> previousSequence;
    private Sequence<ValueType_, DifferenceType_> nextSequence;
    private DifferenceType_ length;

    public BreakImpl(Sequence<ValueType_, DifferenceType_> previousSequence, Sequence<ValueType_, DifferenceType_> nextSequence,
            DifferenceType_ length) {
        this.previousSequence = previousSequence;
        this.nextSequence = nextSequence;
        this.length = length;
    }

    @Override
    public Sequence<ValueType_, DifferenceType_> getPreviousSequence() {
        return previousSequence;
    }

    @Override
    public Sequence<ValueType_, DifferenceType_> getNextSequence() {
        return nextSequence;
    }

    @Override
    public DifferenceType_ getLength() {
        return length;
    }

    public void setPreviousSequence(Sequence<ValueType_, DifferenceType_> previousSequence) {
        this.previousSequence = previousSequence;
    }

    public void setNextSequence(Sequence<ValueType_, DifferenceType_> nextSequence) {
        this.nextSequence = nextSequence;
    }

    public void setLength(DifferenceType_ length) {
        this.length = length;
    }

    @Override
    public String toString() {
        return "Break{" +
                "previousSequence=" + previousSequence +
                ", nextSequence=" + nextSequence +
                ", length=" + length +
                '}';
    }
}
