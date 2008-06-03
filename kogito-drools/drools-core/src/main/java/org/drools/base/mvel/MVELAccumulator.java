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
package org.drools.base.mvel;

import java.io.Serializable;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectInput;
import java.util.HashMap;
import java.util.Map;

import org.drools.WorkingMemory;
import org.drools.common.InternalFactHandle;
import org.drools.rule.Declaration;
import org.drools.rule.MVELDialectData;
import org.drools.rule.Package;
import org.drools.spi.Accumulator;
import org.drools.spi.Tuple;
import org.mvel.MVEL;

/**
 * An MVEL accumulator implementation
 *
 * @author etirelli
 */
public class MVELAccumulator
    implements
    Accumulator {

    private static final long       serialVersionUID = 400L;

    private DroolsMVELFactory prototype;
    private Serializable      init;
    private Serializable      action;
    private Serializable      reverse;
    private Serializable      result;

    public MVELAccumulator() {
    }

    public MVELAccumulator(final DroolsMVELFactory factory,
                           final Serializable init,
                           final Serializable action,
                           final Serializable reverse,
                           final Serializable result) {
        super();
        this.prototype = factory;
        this.init = init;
        this.action = action;
        this.reverse = reverse;
        this.result = result;
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        prototype   = (DroolsMVELFactory)in.readObject();
        init   = (Serializable)in.readObject();
        action   = (Serializable)in.readObject();
        reverse   = (Serializable)in.readObject();
        result   = (Serializable)in.readObject();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(prototype);
        out.writeObject(init);
        out.writeObject(action);
        out.writeObject(reverse);
        out.writeObject(result);
    }

    /* (non-Javadoc)
     * @see org.drools.spi.Accumulator#createContext()
     */
    public Object createContext() {
        return new HashMap();
    }

    /* (non-Javadoc)
     * @see org.drools.spi.Accumulator#init(java.lang.Object, org.drools.spi.Tuple, org.drools.rule.Declaration[], org.drools.WorkingMemory)
     */
    public void init(Object workingMemoryContext,
                     Object context,
                     Tuple leftTuple,
                     Declaration[] declarations,
                     WorkingMemory workingMemory) throws Exception {
        DroolsMVELFactory factory = (DroolsMVELFactory) workingMemoryContext;
        factory.setContext( leftTuple,
                            null,
                            null,
                            workingMemory,
                            (Map) context );
        MVEL.executeExpression( this.init,
                                null,
                                factory );
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
                            (Map) context );

        Package pkg = workingMemory.getRuleBase().getPackage( "MAIN" );
        if ( pkg != null ) {
            MVELDialectData data = ( MVELDialectData ) pkg.getDialectDatas().getDialectData( "mvel" );
            factory.setNextFactory( data.getFunctionFactory() );
        }

        MVEL.executeExpression( this.action,
                                null,
                                factory );
    }

    public void reverse(Object workingMemoryContext,
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
                            (Map) context );
        
        Package pkg = workingMemory.getRuleBase().getPackage( "MAIN" );
        if ( pkg != null ) {
            MVELDialectData data = ( MVELDialectData ) pkg.getDialectDatas().getDialectData( "mvel" );
            factory.setNextFactory( data.getFunctionFactory() );
        }

        MVEL.executeExpression( this.reverse,
                                null,
                                factory );
    }

    /* (non-Javadoc)
     * @see org.drools.spi.Accumulator#getResult(java.lang.Object, org.drools.spi.Tuple, org.drools.rule.Declaration[], org.drools.WorkingMemory)
     */
    public Object getResult(Object workingMemoryContext,
                            Object context,
                            Tuple leftTuple,
                            Declaration[] declarations,
                            WorkingMemory workingMemory) throws Exception {
        DroolsMVELFactory factory = (DroolsMVELFactory) workingMemoryContext;
        factory.setContext( leftTuple,
                            null,
                            null,
                            workingMemory,
                            (Map) context );

        Package pkg = workingMemory.getRuleBase().getPackage( "MAIN" );
        if ( pkg != null ) {
            MVELDialectData data = ( MVELDialectData ) pkg.getDialectDatas().getDialectData( "mvel" );
            factory.setNextFactory( data.getFunctionFactory() );
        }

        final Object result = MVEL.executeExpression( this.result,
                                                      null,
                                                      factory );
        return result;
    }

    public boolean supportsReverse() {
        return this.reverse != null;
    }

    public Object createWorkingMemoryContext() {
        return this.prototype.clone();
    }

}
