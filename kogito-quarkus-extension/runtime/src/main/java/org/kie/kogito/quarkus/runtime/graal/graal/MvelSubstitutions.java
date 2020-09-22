/*
 *  Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package org.kie.kogito.quarkus.runtime.graal.graal;

import java.io.Serializable;
import java.util.Map;

import com.oracle.svm.core.annotate.Substitute;
import com.oracle.svm.core.annotate.TargetClass;
import org.mvel2.MVEL;
import org.mvel2.ParserContext;
import org.mvel2.compiler.Accessor;
import org.mvel2.integration.VariableResolverFactory;
import org.mvel2.optimizers.impl.asm.ASMAccessorOptimizer;

final class MvelSubstitutions {

}

@TargetClass(MVEL.class)
final class MVEL_Target {

    @Substitute
    public static Class analyze(char[] expression, ParserContext ctx) {
        throw new UnsupportedOperationException("Cannot run MVEL#analyze in native mode");
    }

    @Substitute
    public static Class analyze(String expression, ParserContext ctx) {
        throw new UnsupportedOperationException("Cannot run MVEL#analyze in native mode");
    }

    @Substitute
    public static Serializable compileExpression(String expression, ParserContext ctx) {
        throw new UnsupportedOperationException("Cannot run MVEL#analyze in native mode");
    }

    @Substitute
    public static Object executeExpression(final Object compiledExpression, final Object ctx) {
        throw new UnsupportedOperationException("Cannot run MVEL#analyze in native mode");
    }

    @Substitute
    public static Object eval(String expression, Map<String, Object> vars) {
        throw new UnsupportedOperationException("Cannot run MVEL#analyze in native mode");
    }

    @Substitute
    public static Object eval(String expression) {
        throw new UnsupportedOperationException("Cannot run MVEL#analyze in native mode");
    }
}

@TargetClass(ASMAccessorOptimizer.class)
final class ASMAccessorOptimizer_Target {

    @Substitute
    public Accessor optimizeAccessor( ParserContext pCtx, char[] property, int start, int offset, Object ctx, Object thisRef,
                                      VariableResolverFactory factory, boolean rootThisRef, Class ingressType) {
        throw new UnsupportedOperationException();
    }

    @Substitute
    public Accessor optimizeSetAccessor(ParserContext pCtx, char[] property, int start, int offset, Object ctx, Object thisRef,
                                        VariableResolverFactory factory, boolean rootThisRef, Object value, Class ingressType) {
        throw new UnsupportedOperationException();
    }

    @Substitute
    public Accessor optimizeCollection(ParserContext pCtx, Object collectionGraph, Class type, char[] property, int start, int offset, Object ctx, Object thisRef, VariableResolverFactory factory){
        throw new UnsupportedOperationException();
    }

    @Substitute
    public Accessor optimizeObjectCreation(ParserContext pCtx, char[] property, int start, int offset, Object ctx, Object thisRef, VariableResolverFactory factory) {
        throw new UnsupportedOperationException();
    }
}
