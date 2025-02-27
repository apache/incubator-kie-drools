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

public interface Function8<A, B, C, D, E, F, G, H, R> extends Serializable {

    R apply(A a, B b, C c, D d, E e, F f, G g, H h);

    class Impl<A, B, C, D, E, F, G, H, R> extends IntrospectableLambda implements Function8<A, B, C, D, E, F, G, H, R> {

        private final Function8<A, B, C, D, E, F, G, H, R> function;

        public Impl(Function8<A, B, C, D, E, F, G, H, R> function) {
            this.function = function;
        }

        @Override
        public R apply(A a, B b, C c, D d, E e, F f, G g, H h) {
            return function.apply(a, b, c, d, e, f, g, h);
        }

        @Override
        public Object getLambda() {
            return function;
        }
    }
}