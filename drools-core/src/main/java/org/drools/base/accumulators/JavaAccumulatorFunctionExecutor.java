/*
 * Copyright 2007 JBoss Inc
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
 *
 * Created on Jun 20, 2007
 */
package org.drools.base.accumulators;

import org.drools.FactHandle;
import org.drools.WorkingMemory;
import org.drools.common.InternalFactHandle;
import org.drools.rule.Declaration;
import org.drools.spi.Accumulator;
import org.drools.spi.ReturnValueExpression;
import org.drools.spi.Tuple;

import java.io.ObjectOutput;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * An MVEL accumulator function executor implementation
 *
 * @author etirelli
 */
public class JavaAccumulatorFunctionExecutor
    implements
    Accumulator {

    private static final long           serialVersionUID = 400L;

    private ReturnValueExpression expression;
    private AccumulateFunction   function;

    public JavaAccumulatorFunctionExecutor() {

    }

    public JavaAccumulatorFunctionExecutor(final AccumulateFunction function) {
        super();
        this.function = function;
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        expression  = (ReturnValueExpression)in.readObject();
        function    = (AccumulateFunction)in.readObject();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(expression);
        out.writeObject(function);
    }

    /* (non-Javadoc)
     * @see org.drools.spi.Accumulator#createContext()
     */
    public Object createContext() {
        JavaAccumulatorFunctionContext context = new JavaAccumulatorFunctionContext();
        context.context = this.function.createContext(); 
        if( this.function.supportsReverse() ) {
            context.reverseSupport = new HashMap<ReverseSupportKey, Object>();
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
        if( this.function.supportsReverse() ) {
            ((JavaAccumulatorFunctionContext) context).reverseSupport.put( new ReverseSupportKey(leftTuple, handle), value );
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
        
        final Object value = ((JavaAccumulatorFunctionContext) context).reverseSupport.remove( new ReverseSupportKey(leftTuple, handle) );
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

    public void setExpression(ReturnValueExpression expression) {
        this.expression = expression;
    }

    public Object createWorkingMemoryContext() {
        // no working memory context needed
        return null;
    }
    
    private static class JavaAccumulatorFunctionContext {
        public Object context;
        public Map<ReverseSupportKey, Object> reverseSupport;
    }
    
    private static class ReverseSupportKey implements Serializable {
        private static final long serialVersionUID = 1473894397336046369L;
        public final Tuple leftTuple;
        public final FactHandle factHandle;
        public ReverseSupportKey( final Tuple leftTuple, final FactHandle handle ) {
            this.leftTuple = leftTuple;
            this.factHandle = handle;
        }
        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((factHandle == null) ? 0 : factHandle.hashCode());
            result = prime * result + ((leftTuple == null) ? 0 : leftTuple.hashCode());
            return result;
        }
        @Override
        public boolean equals(Object obj) {
            if ( this == obj ) return true;
            if ( obj == null ) return false;
            if ( getClass() != obj.getClass() ) return false;
            final ReverseSupportKey other = (ReverseSupportKey) obj;
            if ( factHandle == null ) {
                if ( other.factHandle != null ) return false;
            } else if ( !factHandle.equals( other.factHandle ) ) return false;
            if ( leftTuple == null ) {
                if ( other.leftTuple != null ) return false;
            } else if ( !leftTuple.equals( other.leftTuple ) ) return false;
            return true;
        }
        
    }
}
