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

package org.drools.core.base.accumulators;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.drools.core.WorkingMemory;
import org.drools.core.base.mvel.MVELCompilationUnit;
import org.drools.core.base.mvel.MVELCompileable;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.reteoo.LeftTuple;
import org.drools.core.rule.Declaration;
import org.drools.core.rule.MVELDialectRuntimeData;
import org.drools.core.spi.MvelAccumulator;
import org.drools.core.spi.Tuple;
import org.mvel2.MVEL;
import org.mvel2.integration.VariableResolverFactory;

/**
 * An MVEL accumulator function executor implementation
 */
public class MVELAccumulatorFunctionExecutor
    implements
    MVELCompileable,
    Externalizable,
    MvelAccumulator {

    private static final long                          serialVersionUID = 510l;

    private MVELCompilationUnit                        unit;
    private org.kie.api.runtime.rule.AccumulateFunction function;

    private Serializable                               expression;

    public MVELAccumulatorFunctionExecutor() {

    }

    public MVELAccumulatorFunctionExecutor(MVELCompilationUnit unit,
                                           final org.kie.api.runtime.rule.AccumulateFunction function) {
        super();
        this.unit = unit;
        this.function = function;
    }

    public void readExternal(ObjectInput in) throws IOException,
                                            ClassNotFoundException {
        unit = (MVELCompilationUnit) in.readObject();
        function = (org.kie.api.runtime.rule.AccumulateFunction) in.readObject();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject( unit );
        out.writeObject( function );
    }

    public void compile(MVELDialectRuntimeData runtimeData) {
        expression = unit.getCompiledExpression( runtimeData );
    }

    /* (non-Javadoc)
     * @see org.kie.spi.Accumulator#createContext()
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
     * @see org.kie.spi.Accumulator#init(java.lang.Object, org.kie.spi.Tuple, org.kie.rule.Declaration[], org.kie.WorkingMemory)
     */
    public void init(Object workingMemoryContext,
                     Object context,
                     Tuple leftTuple,
                     Declaration[] declarations,
                     WorkingMemory workingMemory) throws Exception {
        this.function.init( ((MVELAccumulatorFunctionContext) context).context );
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
        
        VariableResolverFactory factory = unit.getFactory( null, null, null, handle, (LeftTuple) leftTuple, null, (InternalWorkingMemory) workingMemory, workingMemory.getGlobalResolver()  );
        
        final Object value = MVEL.executeExpression( this.expression,
                                                     handle.getObject(),
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
     * @see org.kie.spi.Accumulator#getResult(java.lang.Object, org.kie.spi.Tuple, org.kie.rule.Declaration[], org.kie.WorkingMemory)
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

    @Override
    public Declaration[] getRequiredDeclarations() {
        return unit.getPreviousDeclarations();
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
