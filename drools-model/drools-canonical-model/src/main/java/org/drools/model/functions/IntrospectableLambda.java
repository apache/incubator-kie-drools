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

import java.util.function.Supplier;

public abstract class IntrospectableLambda implements Supplier<Object> {
    private String lambdaFingerprint;

    public abstract Object getLambda();

    @Override
    public final Object get() {
        return getLambda();
    }

    @Override
    public String toString() {
        if(lambdaFingerprint == null) {
            lambdaFingerprint = LambdaPrinter.print(getLambda());
        }
        return lambdaFingerprint;
    }

    @Override
    public boolean equals( Object o ) {
        if ( this == o ) return true;
        if ( !(o instanceof IntrospectableLambda) ) return false;
        return toString().equals( o.toString() );
    }

    @Override
    public int hashCode() {
        return toString().hashCode();
    }
}
