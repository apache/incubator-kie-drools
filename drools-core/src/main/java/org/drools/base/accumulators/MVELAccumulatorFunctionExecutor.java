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

import java.io.Serializable;

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

    private static final long         serialVersionUID = 1081306132058101176L;

    private final Object              dummy            = new Object();
    private final DroolsMVELFactory   factory;
    private final Serializable        expression;
    private final AccumulateFunction function;

    public MVELAccumulatorFunctionExecutor(final DroolsMVELFactory factory,
                                           final Serializable expression,
                                           final AccumulateFunction function) {
        super();
        this.factory = factory;
        this.expression = expression;
        this.function = function;
    }

    /* (non-Javadoc)
     * @see org.drools.spi.Accumulator#createContext()
     */
    public Object createContext() {
        return this.function.createContext();
    }

    /* (non-Javadoc)
     * @see org.drools.spi.Accumulator#init(java.lang.Object, org.drools.spi.Tuple, org.drools.rule.Declaration[], org.drools.WorkingMemory)
     */
    public void init(Object context,
                     Tuple leftTuple,
                     Declaration[] declarations,
                     WorkingMemory workingMemory) throws Exception {
        this.function.init( context );
    }

    /* (non-Javadoc)
     * @see org.drools.spi.Accumulator#accumulate(java.lang.Object, org.drools.spi.Tuple, org.drools.common.InternalFactHandle, org.drools.rule.Declaration[], org.drools.rule.Declaration[], org.drools.WorkingMemory)
     */
    public void accumulate(Object context,
                           Tuple leftTuple,
                           InternalFactHandle handle,
                           Declaration[] declarations,
                           Declaration[] innerDeclarations,
                           WorkingMemory workingMemory) throws Exception {
        this.factory.setContext( leftTuple,
                                 null,
                                 handle.getObject(),
                                 workingMemory );
        final Object value = MVEL.executeExpression( this.expression,
                                                     this.dummy,
                                                     this.factory );
        this.function.accumulate( context,
                                  value );
    }

    public void reverse(Object context,
                        Tuple leftTuple,
                        InternalFactHandle handle,
                        Declaration[] declarations,
                        Declaration[] innerDeclarations,
                        WorkingMemory workingMemory) throws Exception {
        this.factory.setContext( leftTuple,
                                 null,
                                 handle.getObject(),
                                 workingMemory );
        final Object value = MVEL.executeExpression( this.expression,
                                                     this.dummy,
                                                     this.factory );
        this.function.reverse( context, value );
    }

    /* (non-Javadoc)
     * @see org.drools.spi.Accumulator#getResult(java.lang.Object, org.drools.spi.Tuple, org.drools.rule.Declaration[], org.drools.WorkingMemory)
     */
    public Object getResult(Object context,
                            Tuple leftTuple,
                            Declaration[] declarations,
                            WorkingMemory workingMemory) throws Exception {
        return this.function.getResult( context );
    }

    public boolean supportsReverse() {
        return this.function.supportsReverse();
    }

}
