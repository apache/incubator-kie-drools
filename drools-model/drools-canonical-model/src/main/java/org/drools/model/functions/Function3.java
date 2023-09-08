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

public interface Function3<A, B, C, R> extends Serializable {

    R apply(A a, B b, C c);

    class Impl<A, B, C, R> extends IntrospectableLambda implements Function3<A, B, C, R> {

        private final Function3<A, B, C, R> function;

        public Impl(Function3<A, B, C, R> function) {
            this.function = function;
        }

        @Override
        public R apply(A a, B b, C c) {
            return function.apply(a, b, c);
        }

        @Override
        public Object getLambda() {
            return function;
        }
    }
}
