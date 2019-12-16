/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 *
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.model.functions;

import java.io.Serializable;

public interface Predicate2<A, B> extends Serializable {
    boolean test(A a, B b) throws Exception;

    default Predicate2<A, B> negate() {
        return (a, b) -> !test( a, b );
    }

    class Impl<A, B> extends IntrospectableLambda implements Predicate2<A, B> {

        private final Predicate2<A, B> predicate;

        public Impl(Predicate2<A, B> predicate) {
            this.predicate = predicate;
        }

        @Override
        public boolean test(A a, B b) throws Exception {
            return predicate.test(a, b);
        }

        @Override
        public Object getLambda() {
            return predicate;
        }
    }
}
