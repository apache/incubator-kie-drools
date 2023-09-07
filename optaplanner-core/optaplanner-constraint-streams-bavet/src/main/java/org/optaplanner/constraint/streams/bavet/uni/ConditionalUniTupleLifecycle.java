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

package org.optaplanner.constraint.streams.bavet.uni;

import java.util.function.Predicate;

import org.optaplanner.constraint.streams.bavet.common.AbstractConditionalTupleLifecycle;
import org.optaplanner.constraint.streams.bavet.common.TupleLifecycle;

final class ConditionalUniTupleLifecycle<A> extends AbstractConditionalTupleLifecycle<UniTuple<A>> {
    private final Predicate<A> predicate;

    public ConditionalUniTupleLifecycle(Predicate<A> predicate, TupleLifecycle<UniTuple<A>> tupleLifecycle) {
        super(tupleLifecycle);
        this.predicate = predicate;
    }

    @Override
    protected boolean test(UniTuple<A> tuple) {
        return predicate.test(tuple.getFactA());
    }
}
