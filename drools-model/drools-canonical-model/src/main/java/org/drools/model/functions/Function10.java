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

public interface Function10<A, B, C, D, E, F, G, H, I, J, R> extends Serializable {

    R apply(A a, B b, C c, D d, E e, F f, G g, H h, I i, J j);

    class Impl<A, B, C, D, E, F, G, H, I, J, R> extends IntrospectableLambda implements Function10<A, B, C, D, E, F, G, H, I, J, R> {

        private final Function10<A, B, C, D, E, F, G, H, I, J, R> function;

        public Impl(Function10<A, B, C, D, E, F, G, H, I, J, R> function) {
            this.function = function;
        }

        @Override
        public R apply(A a, B b, C c, D d, E e, F f, G g, H h, I i, J j) {
            return function.apply(a, b, c, d, e, f, g, h, i, j);
        }

        @Override
        public Object getLambda() {
            return function;
        }
    }
}