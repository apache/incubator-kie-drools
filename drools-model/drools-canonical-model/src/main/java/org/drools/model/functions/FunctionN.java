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

public interface FunctionN<R> extends Serializable {
    R apply(Object... objs);

    default Function1 asFunction1() {
        return (Function1) ((Impl)this).f;
    }

    class Impl<A, R> extends IntrospectableLambda implements FunctionN<R> {

        private final Object f;
        private final FunctionN<R> function;

        public Impl(Object f, FunctionN<R> function) {
            super(getLambdaFingerprint( f ));
            this.f = f;
            this.function = function;
        }

        @Override
        public R apply(Object... objs) {
            return function.apply(objs);
        }

        @Override
        public Object getLambda() {
            throw new UnsupportedOperationException();
        }

        private static <A, R> String getLambdaFingerprint( Object f ) {
            return f instanceof HashedExpression ? (( HashedExpression ) f).getExpressionHash() : f.toString();
        }
    }
}
