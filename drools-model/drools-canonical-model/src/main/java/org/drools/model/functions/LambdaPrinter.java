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

import java.lang.reflect.Field;
import java.util.function.Function;

public interface LambdaPrinter {

    String getLambdaFingerprint(Object lambda);

    static String print(Object lambda) {
        return Factory.get().getLambdaFingerprint(lambda);
    }

    class Factory {
        private static class LazyHolder {
            private static LambdaPrinter INSTANCE = buildPrinter();

            private static LambdaPrinter buildPrinter() {
                try {
                    return new LambdaVisitor( (Function<Object, String>) Class.forName( "org.drools.mvel.asm.LambdaIntrospector" ).newInstance() );
                } catch (Exception e) {
                    return new DummyLambdaPrinter();
                }
            }
        }

        public static LambdaPrinter get() {
            return LazyHolder.INSTANCE;
        }
    }

    class DummyLambdaPrinter implements LambdaPrinter {

        @Override
        public String getLambdaFingerprint( Object lambda ) {
            if (lambda.toString().equals("INSTANCE")) { // Materialized lambda
                return getExpressionHash(lambda);
            }
            return lambda.toString();
        }

        private static String getExpressionHash(Object lambda) {
            Field expressionHash;
            try {
                expressionHash = lambda.getClass().getDeclaredField("EXPRESSION_HASH");
                return (String) expressionHash.get(lambda);
            } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
                throw new RuntimeException( e );
            }
        }
    }

    class LambdaVisitor implements LambdaPrinter {

        private final Function<Object, String> introspector;

        public LambdaVisitor( Function<Object, String> introspector ) {
            this.introspector = introspector;
        }

        @Override
        public String getLambdaFingerprint( Object lambda ) {
            return introspector.apply( lambda );
        }
    }

}
