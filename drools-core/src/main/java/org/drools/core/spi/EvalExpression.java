/*
 * Copyright 2005 Red Hat, Inc. and/or its affiliates.
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

package org.drools.core.spi;

import java.io.Serializable;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedExceptionAction;

import org.drools.core.WorkingMemory;
import org.drools.core.rule.Declaration;
import org.kie.internal.security.KiePolicyHelper;

public interface EvalExpression
    extends
    Invoker,
    Cloneable {
    
    Object createContext();
    
    boolean evaluate(Tuple tuple,
                     Declaration[] requiredDeclarations,
                     WorkingMemory workingMemory,
                     Object context ) throws Exception;

    void replaceDeclaration(Declaration declaration,
                            Declaration resolved);

    EvalExpression clone();

    public static boolean isCompiledInvoker(final EvalExpression expression) {
        return (expression instanceof CompiledInvoker)
                || (expression instanceof SafeEvalExpression && ((SafeEvalExpression) expression).wrapsCompiledInvoker());
    }

    public class SafeEvalExpression implements EvalExpression, Serializable {
        private static final long serialVersionUID = -5682290553015978731L;
        private EvalExpression delegate;
        public SafeEvalExpression(EvalExpression delegate) {
            this.delegate = delegate;
        }

        @Override
        public Object createContext() {
            return AccessController.doPrivileged(
                    (PrivilegedAction<Object>) () -> delegate.createContext(), KiePolicyHelper.getAccessContext());
        }

        @Override
        public boolean evaluate(final Tuple tuple,
                final Declaration[] requiredDeclarations,
                final WorkingMemory workingMemory,
                final Object context) throws Exception {
            return AccessController.doPrivileged(
                    (PrivilegedExceptionAction<Boolean>) () -> delegate.evaluate(tuple, requiredDeclarations, workingMemory, context), KiePolicyHelper.getAccessContext());
        }

        @Override
        public void replaceDeclaration(Declaration declaration, Declaration resolved) {
            delegate.replaceDeclaration(declaration, resolved);
        }

        @Override
        public SafeEvalExpression clone() {
            return new SafeEvalExpression( this.delegate.clone() );
        }

        public boolean wrapsCompiledInvoker() {
            return delegate instanceof CompiledInvoker;
        }
    }
}
