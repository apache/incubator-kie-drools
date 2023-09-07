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

package org.optaplanner.constraint.streams.bavet.tri;

import org.optaplanner.constraint.streams.bavet.common.AbstractConditionalTupleLifecycle;
import org.optaplanner.constraint.streams.bavet.common.TupleLifecycle;
import org.optaplanner.core.api.function.TriPredicate;

final class ConditionalTriTupleLifecycle<A, B, C> extends AbstractConditionalTupleLifecycle<TriTuple<A, B, C>> {
    private final TriPredicate<A, B, C> predicate;

    public ConditionalTriTupleLifecycle(TriPredicate<A, B, C> predicate, TupleLifecycle<TriTuple<A, B, C>> tupleLifecycle) {
        super(tupleLifecycle);
        this.predicate = predicate;
    }

    @Override
    protected boolean test(TriTuple<A, B, C> tuple) {
        return predicate.test(tuple.getFactA(), tuple.getFactB(), tuple.getFactC());
    }

}
