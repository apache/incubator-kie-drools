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
package org.drools.mvel.expr;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Serializable;

import org.drools.base.base.ValueResolver;
import org.drools.base.definitions.rule.impl.RuleImpl;
import org.drools.base.reteoo.BaseTuple;
import org.drools.base.rule.Declaration;
import org.drools.base.rule.accessor.Accumulator;
import org.drools.mvel.MVELDialectRuntimeData;
import org.kie.api.runtime.rule.FactHandle;
import org.mvel2.integration.VariableResolverFactory;

import static org.drools.mvel.expr.MvelEvaluator.createMvelEvaluator;

/**
 * An MVEL accumulator function executor implementation
 */
public class MVELAccumulatorFunctionExecutor
    implements
    MVELCompileable,
    Externalizable,
    Accumulator {

    private static final long                          serialVersionUID = 510l;

    private MVELCompilationUnit                        unit;
    private org.kie.api.runtime.rule.AccumulateFunction function;

    private MvelEvaluator<Object> evaluator;

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

    public void compile( MVELDialectRuntimeData runtimeData) {
        evaluator = createMvelEvaluator(unit.getCompiledExpression( runtimeData ));
    }

    public void compile( MVELDialectRuntimeData runtimeData, RuleImpl rule) {
        evaluator = createMvelEvaluator(unit.getCompiledExpression( runtimeData, rule.toRuleNameAndPathString() ));
    }

    /* (non-Javadoc)
     * @see org.kie.spi.Accumulator#createContext()
     */
    public Object createContext() {
        return this.function.createContext();
    }

    /* (non-Javadoc)
     * @see org.kie.spi.Accumulator#init(java.lang.Object, org.kie.spi.Tuple, org.kie.rule.Declaration[], org.kie.WorkingMemory)
     */
    public Object init(Object workingMemoryContext,
                       Object context,
                       BaseTuple leftTuple,
                       Declaration[] declarations,
                       ValueResolver valueResolver) {
        return this.function.initContext( (Serializable) context );
    }

    /* (non-Javadoc)
     * @see org.kie.spi.Accumulator#accumulate(java.lang.Object, org.kie.spi.Tuple, org.kie.common.InternalFactHandle, org.kie.rule.Declaration[], org.kie.rule.Declaration[], org.kie.WorkingMemory)
     */
    public Object accumulate(Object workingMemoryContext,
                             Object context,
                             BaseTuple tuple,
                             FactHandle handle,
                             Declaration[] declarations,
                             Declaration[] innerDeclarations,
                             ValueResolver valueResolver) {
        
        VariableResolverFactory factory = unit.getFactory( null, null, null, handle, tuple, null, valueResolver, valueResolver.getGlobalResolver()  );
        
        final Object value = evaluator.evaluate( handle.getObject(), factory );
        return this.function.accumulateValue( (Serializable) context, value );
    }

    public boolean tryReverse(Object workingMemoryContext,
                              Object context,
                              BaseTuple leftTuple,
                              FactHandle handle,
                              Object value,
                              Declaration[] declarations,
                              Declaration[] innerDeclarations,
                              ValueResolver valueResolver) {
        return this.function.tryReverse( (Serializable) context, value );
    }

    /* (non-Javadoc)
     * @see org.kie.spi.Accumulator#getResult(java.lang.Object, org.kie.spi.Tuple, org.kie.rule.Declaration[], org.kie.WorkingMemory)
     */
    public Object getResult(Object workingMemoryContext,
                            Object context,
                            BaseTuple leftTuple,
                            Declaration[] declarations,
                            ValueResolver valueResolver) {
        try {
            return this.function.getResult( (Serializable) context);
        } catch (Exception e) {
            throw new RuntimeException( e );
        }
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

    @Override
    public void replaceDeclaration( Declaration declaration, Declaration resolved ) {
        unit.replaceDeclaration( declaration, resolved );
    }

}
