/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
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

package org.drools.core.base.accumulators;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.drools.core.WorkingMemory;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.rule.Declaration;
import org.drools.core.spi.Accumulator;
import org.drools.core.spi.CompiledInvoker;
import org.drools.core.spi.ReturnValueExpression;
import org.drools.core.spi.ReturnValueExpression.SafeReturnValueExpression;
import org.drools.core.spi.Tuple;
import org.drools.core.spi.Wireable;
import org.kie.internal.security.KiePolicyHelper;

/**
 * A Java accumulator function executor implementation
 */
public class JavaAccumulatorFunctionExecutor
    implements
    Accumulator,
    Externalizable,
    Wireable {

    private static final long     serialVersionUID = 510l;

    private ReturnValueExpression expression;
    private org.kie.api.runtime.rule.AccumulateFunction    function;

    public JavaAccumulatorFunctionExecutor() {

    }

    public JavaAccumulatorFunctionExecutor(final org.kie.api.runtime.rule.AccumulateFunction function) {
        super();
        this.function = function;
    }

    public void readExternal(ObjectInput in) throws IOException,
                                            ClassNotFoundException {
        expression = (ReturnValueExpression) in.readObject();
        function = (org.kie.api.runtime.rule.AccumulateFunction) in.readObject();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        if ( this.expression instanceof CompiledInvoker ) {
            out.writeObject( null );
        } else {
            out.writeObject( this.expression );
        }
        out.writeObject( function );
    }

    /* (non-Javadoc)
     * @see org.kie.spi.Accumulator#createContext()
     */
    public Serializable createContext() {
        JavaAccumulatorFunctionContext context = new JavaAccumulatorFunctionContext();
        context.context = this.function.createContext();
        if ( this.function.supportsReverse() ) {
            context.reverseSupport = new HashMap<Integer, Object>();
        }
        return context;
    }

    /* (non-Javadoc)
     * @see org.kie.spi.Accumulator#init(java.lang.Object, org.kie.spi.Tuple, org.kie.rule.Declaration[], org.kie.WorkingMemory)
     */
    public void init(Object workingMemoryContext,
                     Object context,
                     Tuple leftTuple,
                     Declaration[] declarations,
                     WorkingMemory workingMemory) throws Exception {
        this.function.init( ((JavaAccumulatorFunctionContext) context).context );
    }

    /* (non-Javadoc)
     * @see org.kie.spi.Accumulator#accumulate(java.lang.Object, org.kie.spi.Tuple, org.kie.common.InternalFactHandle, org.kie.rule.Declaration[], org.kie.rule.Declaration[], org.kie.WorkingMemory)
     */
    public void accumulate(Object workingMemoryContext,
                           Object context,
                           Tuple leftTuple,
                           InternalFactHandle handle,
                           Declaration[] declarations,
                           Declaration[] innerDeclarations,
                           WorkingMemory workingMemory) throws Exception {
        final Object value = this.expression.evaluate( handle,
                                                       leftTuple,
                                                       declarations,
                                                       innerDeclarations,
                                                       workingMemory,
                                                       workingMemoryContext ).getValue();
        if ( this.function.supportsReverse() ) {
            ((JavaAccumulatorFunctionContext) context).reverseSupport.put( handle.getId(),
                                                                           value );
        }
        this.function.accumulate( ((JavaAccumulatorFunctionContext) context).context,
                                  value );
    }

    public void reverse(Object workingMemoryContext,
                        Object context,
                        Tuple leftTuple,
                        InternalFactHandle handle,
                        Declaration[] declarations,
                        Declaration[] innerDeclarations,
                        WorkingMemory workingMemory) throws Exception {

        final Object value = ((JavaAccumulatorFunctionContext) context).reverseSupport.remove(handle.getId());
        this.function.reverse( ((JavaAccumulatorFunctionContext) context).context,
                               value );
    }

    /* (non-Javadoc)
     * @see org.kie.spi.Accumulator#getResult(java.lang.Object, org.kie.spi.Tuple, org.kie.rule.Declaration[], org.kie.WorkingMemory)
     */
    public Object getResult(Object workingMemoryContext,
                            Object context,
                            Tuple leftTuple,
                            Declaration[] declarations,
                            WorkingMemory workingMemory) throws Exception {
        return this.function.getResult( ((JavaAccumulatorFunctionContext) context).context );
    }

    public boolean supportsReverse() {
        return this.function.supportsReverse();
    }

    public ReturnValueExpression getExpression() {
        return expression;
    }

    public void wire(Object object) {
        setExpression( KiePolicyHelper.isPolicyEnabled() ? new SafeReturnValueExpression((ReturnValueExpression) object ) : (ReturnValueExpression) object );
    }

    public void setExpression(ReturnValueExpression expression) {
        this.expression = expression;
    }

    public Object createWorkingMemoryContext() {
        // no working memory context needed
        return null;
    }

    @Override
    public boolean equals( Object o ) {
        if ( this == o ) return true;
        if ( o == null || getClass() != o.getClass() ) return false;

        JavaAccumulatorFunctionExecutor that = (JavaAccumulatorFunctionExecutor) o;

        return expression.equals( that.expression ) && function.equals( that.function );
    }

    @Override
    public int hashCode() {
        int result = expression.hashCode();
        result = 31 * result + function.hashCode();
        return result;
    }

    public static class JavaAccumulatorFunctionContext
        implements
        Externalizable {
        public Serializable               context;
        public Map<Integer, Object>       reverseSupport;

        public JavaAccumulatorFunctionContext() {
        }

        @SuppressWarnings("unchecked")
        public void readExternal(ObjectInput in) throws IOException,
                                                ClassNotFoundException {
            context = (Externalizable) in.readObject();
            reverseSupport = (Map<Integer, Object>) in.readObject();
        }

        public void writeExternal(ObjectOutput out) throws IOException {
            out.writeObject( context );
            out.writeObject( reverseSupport );
        }

        public Collection<Object> getAccumulatedObjects() {
            return reverseSupport.values();
        }
    }
    
}
