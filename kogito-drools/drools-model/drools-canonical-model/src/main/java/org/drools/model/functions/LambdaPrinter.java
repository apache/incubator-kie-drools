/*
 * Copyright 2005 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.model.functions;

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
                    return (LambdaPrinter) Class.forName( "org.drools.modelcompiler.util.LambdaIntrospector" ).newInstance();
                } catch (Exception e) {
                    System.err.println( "Unable to find LambdaIntrospector, caused by: " + e.getMessage() );
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
            return lambda.toString();
        }
    }

}
