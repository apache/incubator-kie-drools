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
import org.drools.base.mvel.DroolsMVELFactory;
import org.drools.base.mvel.MVELCompilationUnit;
import org.drools.base.mvel.MVELCompileable;
import org.drools.common.InternalFactHandle;
import org.drools.common.InternalWorkingMemory;
import org.drools.reteoo.LeftTuple;
import org.drools.rule.Declaration;
import org.drools.spi.Accumulator;
import org.drools.spi.Tuple;
import org.mvel2.MVEL;
import org.mvel2.integration.VariableResolverFactory;

/**
 * An MVEL accumulator function executor implementation
 *
 * @author etirelli
 */
public class MVELAccumulatorFunctionExecutor
    implements
    MVELCompileable,
    Externalizable,
    Accumulator {

    private static final long                          serialVersionUID = 510l;

    private final Object                               dummy            = new Object();

    private MVELCompilationUnit                        unit;
    private org.drools.runtime.rule.AccumulateFunction function;

    private Serializable                               expression;

    public MVELAccumulatorFunctionExecutor() {

    }

    public MVELAccumulatorFunctionExecutor(MVELCompilationUnit unit,
                                           final org.drools.runtime.rule.AccumulateFunction function) {
        super();
        this.unit = unit;
        this.function = function;
    }

    public void readExternal(ObjectInput in) throws IOException,
                                            ClassNotFoundException {
        unit = (MVELCompilationUnit) in.readObject();
        function = (org.drools.runtime.rule.AccumulateFunction) in.readObject();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject( unit );
        out.writeObject( function );
    }

    public void compile(ClassLoader classLoader) {
        expression = unit.getCompiledExpression( classLoader );
    }

    /* (non-Javadoc)
     * @see org.drools.spi.Accumulator#createContext()
     */
    public Serializable createContext() {
        MVELAccumulatorFunctionContext context = new MVELAccumulatorFunctionContext();
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
        
        VariableResolverFactory factory = unit.getFactory( null, null, (LeftTuple) leftTuple, null, handle.getObject(), (InternalWorkingMemory) workingMemory );
        
        final Object value = MVEL.executeExpression( this.expression,
                                                     this.dummy,
                                                     factory );
        if ( this.function.supportsReverse() ) {
            ((MVELAccumulatorFunctionContext) context).reverseSupport.put( Integer.valueOf( handle.getId() ),
                                                                           value );
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
        final Object value = ((MVELAccumulatorFunctionContext) context).reverseSupport.remove( Integer.valueOf( handle.getId() ) );
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
        return null; //this.model.clone();
    }

    private static class MVELAccumulatorFunctionContext
        implements
        Externalizable {
        public Serializable               context;
        public Map<Integer, Object> reverseSupport;

        public MVELAccumulatorFunctionContext() {
        }

        @SuppressWarnings("unchecked")
        public void readExternal(ObjectInput in) throws IOException,
                                                ClassNotFoundException {
            context = (Serializable) in.readObject();
            reverseSupport = (Map<Integer, Object>) in.readObject();
        }

        public void writeExternal(ObjectOutput out) throws IOException {
            out.writeObject( context );
            out.writeObject( reverseSupport );
        }
    }

}
