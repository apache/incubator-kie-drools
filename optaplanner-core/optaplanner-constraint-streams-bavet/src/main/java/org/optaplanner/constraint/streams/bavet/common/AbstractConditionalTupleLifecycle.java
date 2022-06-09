/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.constraint.streams.bavet.common;

import java.util.Objects;

public abstract class AbstractConditionalTupleLifecycle<Tuple_ extends Tuple>
        implements TupleLifecycle<Tuple_> {

    private final TupleLifecycle<Tuple_> tupleLifecycle;

    protected AbstractConditionalTupleLifecycle(TupleLifecycle<Tuple_> tupleLifecycle) {
        this.tupleLifecycle = Objects.requireNonNull(tupleLifecycle);
    }

    @Override
    public final void insert(Tuple_ tuple) {
        if (test(tuple)) {
            tupleLifecycle.insert(tuple);
        }
    }

    @Override
    public final void update(Tuple_ tuple) {
        if (test(tuple)) {
            tupleLifecycle.update(tuple);
        } else {
            tupleLifecycle.retract(tuple);
        }
    }

    @Override
    public final void retract(Tuple_ tuple) {
        tupleLifecycle.retract(tuple);
    }

    abstract protected boolean test(Tuple_ tuple);

}
