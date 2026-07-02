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

package org.optaplanner.constraint.streams.bavet.common;

final class AggregatedTupleLifecycle<Tuple_ extends Tuple> implements TupleLifecycle<Tuple_> {
    private final TupleLifecycle<Tuple_>[] lifecycles;

    public AggregatedTupleLifecycle(TupleLifecycle<Tuple_>[] lifecycles) {
        this.lifecycles = lifecycles;
    }

    @Override
    public void insert(Tuple_ tuple) {
        for (TupleLifecycle<Tuple_> lifecycle : lifecycles) {
            lifecycle.insert(tuple);
        }
    }

    @Override
    public void update(Tuple_ tuple) {
        for (TupleLifecycle<Tuple_> lifecycle : lifecycles) {
            lifecycle.update(tuple);
        }
    }

    @Override
    public void retract(Tuple_ tuple) {
        for (TupleLifecycle<Tuple_> lifecycle : lifecycles) {
            lifecycle.retract(tuple);
        }
    }

    @Override
    public String toString() {
        return "size = " + lifecycles.length;
    }

}
