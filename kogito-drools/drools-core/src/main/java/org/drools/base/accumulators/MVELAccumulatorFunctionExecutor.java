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

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.drools.FactHandle;
import org.drools.WorkingMemory;
import org.drools.base.mvel.DroolsMVELFactory;
import org.drools.common.InternalFactHandle;
import org.drools.rule.Declaration;
import org.drools.spi.Accumulator;
import org.drools.spi.Tuple;
import org.mvel.MVEL;

/**
 * An MVEL accumulator function executor implementation
 *
 * @author etirelli
 */
public class MVELAccumulatorFunctionExecutor
    implements
    Accumulator {

    private static final long        serialVersionUID = 400L;

    private final Object             dummy            = new Object();
    private DroolsMVELFactory  model;
    private Serializable       expression;
    private AccumulateFunction function;

    public MVELAccumulatorFunctionExecutor() {

    }

    public MVELAccumulatorFunctionExecutor(final DroolsMVELFactory factory,
                                           final Serializable expression,
                                           final AccumulateFunction function) {
        super();
        this.model = factory;
        this.expression = expression;
        this.function = function;
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        model       = (DroolsMVELFactory)in.readObject();
        expression  = (Serializable)in.readObject();
        function    = (AccumulateFunction)in.readObject();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(model);
        out.writeObject(expression);
        out.writeObject(function);
    }

    /* (non-Javadoc)
     * @see org.drools.spi.Accumulator#createContext()
     */
    public Object createContext() {
        MVELAccumulatorFunctionContext context = new MVELAccumulatorFunctionContext();
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
        this.function.init( ((MVELAccumulatorFunctionContext) context).context );
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
        DroolsMVELFactory factory = (DroolsMVELFactory) workingMemoryContext;
        factory.setContext( leftTuple,
                            null,
                            handle.getObject(),
                            workingMemory,
                            null );
        final Object value = MVEL.executeExpression( this.expression,
                                                     this.dummy,
                                                     factory );
        if( this.function.supportsReverse() ) {
            ((MVELAccumulatorFunctionContext) context).reverseSupport.put( new ReverseSupportKey(leftTuple, handle), value );
        }
        this.function.accumulate( ((MVELAccumulatorFunctionContext) context).context,
                                  value );
    }

    public void reverse(Object workingMemoryContext,
                        Object context,
                        Tuple leftTuple,
                        InternalFactHandle handle,
                        Declaration[] declarations,
                        Declaration[] innerDeclarations,
                        WorkingMemory workingMemory) throws Exception {
        final Object value = ((MVELAccumulatorFunctionContext) context).reverseSupport.remove( new ReverseSupportKey(leftTuple, handle) );
        this.function.reverse( ((MVELAccumulatorFunctionContext) context).context,
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
        return this.function.getResult( ((MVELAccumulatorFunctionContext) context).context );
    }

    public boolean supportsReverse() {
        return this.function.supportsReverse();
    }

    public Object createWorkingMemoryContext() {
        return this.model.clone();
    }
    
    private static class MVELAccumulatorFunctionContext {
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
