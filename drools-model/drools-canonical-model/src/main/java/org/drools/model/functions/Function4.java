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

public interface Function4<A, B, C, D, R> extends Serializable {

    R apply(A a, B b, C c, D d);

    class Impl<A, B, C, D, R> extends IntrospectableLambda implements Function4<A, B, C, D, R> {

        private final Function4<A, B, C, D, R> function;

        public Impl(Function4<A, B, C, D, R> function) {
            this.function = function;
        }

        @Override
        public R apply(A a, B b, C c, D d) {
            return function.apply(a, b, c, d);
        }

        @Override
        public Object getLambda() {
            return function;
        }
    }
}
