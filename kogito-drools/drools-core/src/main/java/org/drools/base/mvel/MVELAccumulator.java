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

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.drools.WorkingMemory;
import org.drools.common.InternalFactHandle;
import org.drools.rule.Declaration;
import org.drools.rule.MVELDialectRuntimeData;
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

    private static final long serialVersionUID = 400L;

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

    public void readExternal(ObjectInput in) throws IOException,
                                            ClassNotFoundException {
        prototype = (DroolsMVELFactory) in.readObject();
        init = (Serializable) in.readObject();
        action = (Serializable) in.readObject();
        reverse = (Serializable) in.readObject();
        result = (Serializable) in.readObject();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject( prototype );
        out.writeObject( init );
        out.writeObject( action );
        out.writeObject( reverse );
        out.writeObject( result );
    }

    /* (non-Javadoc)
     * @see org.drools.spi.Accumulator#createContext()
     */
    public Object createContext() {
        Map<InternalFactHandle, Map<String, Object>> shadow = null;
        if ( this.reverse != null ) {
            shadow = new HashMap<InternalFactHandle, Map<String, Object>>();
        }
        return new MVELAccumulatorContext( new HashMap(),
                                           shadow );
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
        Package pkg = workingMemory.getRuleBase().getPackage( "MAIN" );
        if ( pkg != null ) {
            MVELDialectRuntimeData data = (MVELDialectRuntimeData) pkg.getDialectRuntimeRegistry().getDialectData( "mvel" );
            factory.setNextFactory( data.getFunctionFactory() );
        }

        factory.setContext( leftTuple,
                            null,
                            null,
                            workingMemory,
                            ((MVELAccumulatorContext) context).variables );
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
                            ((MVELAccumulatorContext) context).variables );

        if ( reverse != null ) {
            // SNAPSHOT variable values
            takeSnapshot( context,
                          factory,
                          handle );
        }
        MVEL.executeExpression( this.action,
                                null,
                                factory );
    }

    private void takeSnapshot(Object context,
                              DroolsMVELFactory factory,
                              InternalFactHandle handle) {
        DroolsMVELShadowFactory shad = (DroolsMVELShadowFactory) factory;
        Map<String, Object> varsMap = ((MVELAccumulatorContext) context).shadow.get( handle );
        if ( varsMap == null ) {
            varsMap = new HashMap<String, Object>();
            ((MVELAccumulatorContext) context).shadow.put( handle,
                                                           varsMap );
        }
        for ( String var : shad.getShadowVariables() ) {
            varsMap.put( var,
                      shad.getVariableResolver( var ).getValue() );
        }
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
                            ((MVELAccumulatorContext) context).variables );

        // set shadow values overriding actual values
        // ALSO, since reverse() is called, we know the factory is a shadow factory
        ((DroolsMVELShadowFactory) factory).setShadowValues( ((MVELAccumulatorContext) context).shadow.get( handle ) );

        MVEL.executeExpression( this.reverse,
                                null,
                                factory );

        // cleaning up shadow values map
        ((DroolsMVELShadowFactory) factory).setShadowValues( null );
        ((MVELAccumulatorContext) context).shadow.remove( handle );
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
                            ((MVELAccumulatorContext) context).variables );

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

    private static class MVELAccumulatorContext
        implements
        Serializable {

        private static final long                                 serialVersionUID = -308602705153011537L;

        public final Map                                          variables;
        public final Map<InternalFactHandle, Map<String, Object>> shadow;

        public MVELAccumulatorContext(final Map variables,
                                      final Map<InternalFactHandle, Map<String, Object>> shadow) {
            this.variables = variables;
            this.shadow = shadow;
        }
    }

}
