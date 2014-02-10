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

package org.drools.core.spi;

import java.io.Serializable;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedExceptionAction;

import org.drools.core.WorkingMemory;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.rule.Declaration;
import org.kie.internal.security.KiePolicyHelper;

public interface ReturnValueExpression
    extends
    Invoker {
    
    public Object createContext();
    
    public FieldValue evaluate(InternalFactHandle handle,
                               Tuple tuple,
                               Declaration[] previousDeclarations,
                               Declaration[] localDeclarations,
                               WorkingMemory workingMemory,
                               Object context ) throws Exception;
    
    public void replaceDeclaration(Declaration declaration,
                                   Declaration resolved);

    public static class SafeReturnValueExpression implements ReturnValueExpression, Serializable {
        private static final long serialVersionUID = -5616989474935380093L;
        private ReturnValueExpression delegate;
        public SafeReturnValueExpression(ReturnValueExpression delegate) {
            this.delegate = delegate;
        }

        public Object createContext() {
            return AccessController.doPrivileged(new PrivilegedAction<Object>() {
                @Override
                public Object run() {
                    return delegate.createContext();
                }
            }, KiePolicyHelper.getAccessContext());
        }

        public FieldValue evaluate(final InternalFactHandle handle,
                final Tuple tuple, 
                final Declaration[] previousDeclarations, 
                final Declaration[] localDeclarations, 
                final WorkingMemory workingMemory, 
                final Object context) throws Exception {
            return AccessController.doPrivileged(new PrivilegedExceptionAction<FieldValue>() {
                @Override
                public FieldValue run() throws Exception {
                    return delegate.evaluate(handle, tuple, previousDeclarations, localDeclarations, workingMemory, context);
                }
            }, KiePolicyHelper.getAccessContext());
        }

        public void replaceDeclaration(Declaration declaration, Declaration resolved) {
            delegate.replaceDeclaration(declaration, resolved);
        }

    }

    
}
