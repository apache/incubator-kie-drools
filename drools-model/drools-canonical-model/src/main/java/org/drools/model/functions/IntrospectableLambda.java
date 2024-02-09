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

import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class IntrospectableLambda implements Supplier<Object> {
    private static final Logger logger = LoggerFactory.getLogger(IntrospectableLambda.class);

    private String lambdaFingerprint;

    // Duplicated from CoreComponentsBuilder to avoid dependency on drools-core in drools-canonical-model
    static boolean IS_NATIVE_IMAGE = System.getProperty("org.graalvm.nativeimage.imagecode") != null;

    public abstract Object getLambda();

    protected IntrospectableLambda() { }

    protected IntrospectableLambda(String lambdaFingerprint) {
        this.lambdaFingerprint = lambdaFingerprint;
    }

    @Override
    public final Object get() {
        return getLambda();
    }

    @Override
    public String toString() {
        if (lambdaFingerprint == null) {
            lambdaFingerprint = generateFingerprint();
        }

        return lambdaFingerprint;
    }

    private String generateFingerprint() {
        if(this.getLambda() instanceof HashedExpression) {
            logger.debug("The constraint supports org.drools.model.functions.HashedExpression, node sharing is enabled and compile-time fingerprint is used");
            return ((HashedExpression) this.getLambda()).getExpressionHash();
        } else if(!IS_NATIVE_IMAGE) {
            // LambdaIntrospector is not supported on native image (it uses MVEL and reflection)
            logger.debug("No HashedExpression provided, generating fingerprint using reflection via org.drools.mvel.asm.LambdaIntrospector, node sharing enabled");
            return LambdaPrinter.print(getLambda());
        } else {
            logger.warn("No HashedExpression provided with lambda, using System.identityHashCode as the lambda fingerprint, this will impact performances as node sharing won't work correctly");
            return "HASHCODE-FINGEPRINT" + System.identityHashCode(this);
        }
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
