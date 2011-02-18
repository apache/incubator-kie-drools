/*
 * Copyright 2010 JBoss Inc
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

package org.drools.base.accumulators;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.drools.WorkingMemory;
import org.drools.common.InternalFactHandle;
import org.drools.rule.Declaration;
import org.drools.spi.Accumulator;
import org.drools.spi.CompiledInvoker;
import org.drools.spi.ReturnValueExpression;
import org.drools.spi.Tuple;
import org.drools.spi.Wireable;

/**
 * A Java accumulator function executor implementation
 *
 * @author etirelli
 */
public class JavaAccumulatorFunctionExecutor
    implements
    Accumulator,
    Externalizable,
    Wireable {

    private static final long     serialVersionUID = 510l;

    private ReturnValueExpression expression;
    private org.drools.runtime.rule.AccumulateFunction    function;

    public JavaAccumulatorFunctionExecutor() {

    }

    public JavaAccumulatorFunctionExecutor(final org.drools.runtime.rule.AccumulateFunction function) {
        super();
        this.function = function;
    }

    public void readExternal(ObjectInput in) throws IOException,
                                            ClassNotFoundException {
        expression = (ReturnValueExpression) in.readObject();
        function = (org.drools.runtime.rule.AccumulateFunction) in.readObject();
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
     * @see org.drools.spi.Accumulator#createContext()
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
     * @see org.drools.spi.Accumulator#init(java.lang.Object, org.drools.spi.Tuple, org.drools.rule.Declaration[], org.drools.WorkingMemory)
     */
    public void init(Object workingMemoryContext,
                     Object context,
                     Tuple leftTuple,
                     Declaration[] declarations,
                     WorkingMemory workingMemory) throws Exception {
        this.function.init( ((JavaAccumulatorFunctionContext) context).context );
    }

    /* (non-Javadoc)
     * @see org.drools.spi.Accumulator#accumulate(java.lang.Object, org.drools.spi.Tuple, org.drools.common.InternalFactHandle, org.drools.rule.Declaration[], org.drools.rule.Declaration[], org.drools.WorkingMemory)
     */
    public void accumulate(Object workingMemoryContext,
                           Object context,
                           Tuple leftTuple,
                           InternalFactHandle handle,
                           Declaration[] declarations,
                           Declaration[] innerDeclarations,
                           WorkingMemory workingMemory) throws Exception {
        final Object value = this.expression.evaluate( handle.getObject(),
                                                                            leftTuple,
                                                                            declarations,
                                                                            innerDeclarations,
                                                                            workingMemory,
                                                                            workingMemoryContext ).getValue();
        if ( this.function.supportsReverse() ) {
            ((JavaAccumulatorFunctionContext) context).reverseSupport.put( Integer.valueOf( handle.getId() ),
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

        final Object value = ((JavaAccumulatorFunctionContext) context).reverseSupport.remove( Integer.valueOf( handle.getId() ) );
        this.function.reverse( ((JavaAccumulatorFunctionContext) context).context,
                               value );
    }

    /* (non-Javadoc)
     * @see org.drools.spi.Accumulator#getResult(java.lang.Object, org.drools.spi.Tuple, org.drools.rule.Declaration[], org.drools.WorkingMemory)
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
        setExpression( (ReturnValueExpression) object );
    }

    public void setExpression(ReturnValueExpression expression) {
        this.expression = expression;
    }

    public Object createWorkingMemoryContext() {
        // no working memory context needed
        return null;
    }

    private static class JavaAccumulatorFunctionContext
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
    }

}
