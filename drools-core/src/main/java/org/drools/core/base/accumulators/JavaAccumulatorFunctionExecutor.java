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
package org.drools.core.base.accumulators;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Serializable;

import org.drools.base.base.ValueResolver;
import org.drools.base.reteoo.BaseTuple;
import org.drools.base.rule.Declaration;
import org.drools.base.rule.accessor.Accumulator;
import org.drools.base.rule.accessor.CompiledInvoker;
import org.drools.base.rule.accessor.ReturnValueExpression;
import org.drools.base.rule.accessor.Wireable;
import org.kie.api.runtime.rule.FactHandle;

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
        if ( CompiledInvoker.isCompiledInvoker(this.expression) ) {
            out.writeObject( null );
        } else {
            out.writeObject( this.expression );
        }
        out.writeObject( function );
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
                           BaseTuple leftTuple,
                           FactHandle handle,
                           Declaration[] declarations,
                           Declaration[] innerDeclarations,
                           ValueResolver valueResolver) {
        try {
            Object value = this.expression.evaluate( handle,
                                                     leftTuple,
                                                     declarations,
                                                     innerDeclarations,
                                                     valueResolver,
                                                     workingMemoryContext ).getValue();
            return this.function.accumulateValue( (Serializable) context, value );
        } catch (Exception e) {
            throw new RuntimeException( e );
        }
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
            return this.function.getResult( (Serializable) context );
        } catch (Exception e) {
            throw new RuntimeException( e );
        }
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

    @Override
    public boolean equals( Object o ) {
        if ( this == o ) {
            return true;
        }
        if ( o == null || getClass() != o.getClass() ) {
            return false;
        }

        JavaAccumulatorFunctionExecutor that = (JavaAccumulatorFunctionExecutor) o;

        return expression.equals( that.expression ) && function.equals( that.function );
    }

    @Override
    public int hashCode() {
        int result = expression.hashCode();
        result = 31 * result + function.hashCode();
        return result;
    }
}
