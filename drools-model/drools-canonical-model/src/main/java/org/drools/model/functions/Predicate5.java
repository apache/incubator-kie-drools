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

public interface Predicate5<A, B, C, D, E> extends Serializable {

    boolean test( A a, B b, C c, D d, E e) throws Exception;

    default Predicate5<A, B, C, D, E> negate() {
        return (a, b, c, d, e) -> !test( a, b, c, d, e );
    }

    default PredicateInformation predicateInformation() { return PredicateInformation.EMPTY_PREDICATE_INFORMATION; }

    class Impl<A, B, C, D, E> extends IntrospectableLambda implements Predicate5<A, B, C, D, E> {

        private final Predicate5<A, B, C, D, E> predicate;

        public Impl(Predicate5<A, B, C, D, E> predicate) {
            this.predicate = predicate;
        }

        @Override
        public boolean test(A a, B b, C c, D d, E e) throws Exception {
            return predicate.test(a, b, c, d, e);
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
