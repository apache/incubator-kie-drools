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

class BreakImpl<Value_, Difference_ extends Comparable<Difference_>>
        implements Break<Value_, Difference_> {
    private Sequence<Value_, Difference_> previousSequence;
    private Sequence<Value_, Difference_> nextSequence;
    private Difference_ length;

    public BreakImpl(Sequence<Value_, Difference_> previousSequence, Sequence<Value_, Difference_> nextSequence,
            Difference_ length) {
        this.previousSequence = previousSequence;
        this.nextSequence = nextSequence;
        this.length = length;
    }

    @Override
    public Sequence<Value_, Difference_> getPreviousSequence() {
        return previousSequence;
    }

    @Override
    public Sequence<Value_, Difference_> getNextSequence() {
        return nextSequence;
    }

    @Override
    public Difference_ getLength() {
        return length;
    }

    public void setPreviousSequence(Sequence<Value_, Difference_> previousSequence) {
        this.previousSequence = previousSequence;
    }

    public void setNextSequence(Sequence<Value_, Difference_> nextSequence) {
        this.nextSequence = nextSequence;
    }

    public void setLength(Difference_ length) {
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
