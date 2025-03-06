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

package org.drools.model.functions;

import java.io.Serializable;

public interface Predicate14<A, B, C, D, E, F, G, H, I, J, K, L, M, N> extends Serializable {

    boolean test(A a, B b, C c, D d, E e, F f, G g, H h, I i, J j, K k, L l, M m, N n) throws Exception;

    default Predicate14<A, B, C, D, E, F, G, H, I, J, K, L, M, N> negate() {
        return (a, b, c, d, e, f, g, h, i, j, k, l, m, n) -> !test(a, b, c, d, e, f, g, h, i, j, k, l, m, n);
    }

    default PredicateInformation predicateInformation() { return PredicateInformation.EMPTY_PREDICATE_INFORMATION; }

    class Impl<A, B, C, D, E, F, G, H, I, J, K, L, M, N> extends IntrospectableLambda implements Predicate14<A, B, C, D, E, F, G, H, I, J, K, L, M, N> {

        private final Predicate14<A, B, C, D, E, F, G, H, I, J, K, L, M, N> predicate;

        public Impl(Predicate14<A, B, C, D, E, F, G, H, I, J, K, L, M, N> predicate) {
            this.predicate = predicate;
        }

        @Override
        public boolean test(A a, B b, C c, D d, E e, F f, G g, H h, I i, J j, K k, L l, M m, N n) throws Exception {
            return predicate.test(a, b, c, d, e, f, g, h, i, j, k, l, m, n);
        }

        @Override
        public Object getLambda() {
            return predicate;
        }

        @Override
        public PredicateInformation predicateInformation() {
            return predicate.predicateInformation();
        }
    }
}
