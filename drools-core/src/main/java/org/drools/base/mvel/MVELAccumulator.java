/**
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

package org.drools.base.mvel;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.drools.common.InternalFactHandle;
import org.drools.common.InternalWorkingMemory;
import org.drools.reteoo.LeftTuple;
import org.drools.rule.Declaration;
import org.drools.rule.MVELDialectRuntimeData;
import org.drools.rule.Package;
import org.drools.WorkingMemory;
import org.drools.spi.Accumulator;
import org.drools.spi.Tuple;
import org.mvel2.MVEL;
import org.mvel2.compiler.CompiledExpression;
import org.mvel2.integration.VariableResolverFactory;

/**
 * An MVEL accumulator implementation
 *
 * @author etirelli
 */
public class MVELAccumulator
    implements
    MVELCompileable,
    Accumulator,
    Externalizable {

    private static final long serialVersionUID = 510l;

    MVELCompilationUnit       initUnit;
    MVELCompilationUnit       actionUnit;
    MVELCompilationUnit       reverseUnit;
    MVELCompilationUnit       resultUnit;
    
    private Serializable      init;
    private Serializable      action;
    private Serializable      reverse;
    private Serializable      result;

    public MVELAccumulator() {
    }

    public MVELAccumulator(final MVELCompilationUnit initUnit,
                           final MVELCompilationUnit actionUnit,
                           final MVELCompilationUnit reverseUnit,
                           final MVELCompilationUnit resultUnit) {
        super();
        this.initUnit = initUnit;
        this.actionUnit = actionUnit;
        this.reverseUnit = reverseUnit;
        this.resultUnit = resultUnit;
    }

    public void readExternal(ObjectInput in) throws IOException,
                                            ClassNotFoundException {
        initUnit = (MVELCompilationUnit) in.readObject();
        actionUnit = (MVELCompilationUnit) in.readObject();
        reverseUnit = (MVELCompilationUnit) in.readObject();
        resultUnit = (MVELCompilationUnit) in.readObject();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject( initUnit );
        out.writeObject( actionUnit );
        out.writeObject( reverseUnit );
        out.writeObject( resultUnit );
    }

    public void compile(ClassLoader classLoader) {
        init = initUnit.getCompiledExpression( classLoader );
        action = actionUnit.getCompiledExpression( classLoader );
        result = resultUnit.getCompiledExpression( classLoader );
                
        if ( reverseUnit != null ) {
            reverse = reverseUnit.getCompiledExpression( classLoader );
        }        
    }

    /* (non-Javadoc)
     * @see org.drools.spi.Accumulator#createContext()
     */
    public Serializable createContext() {
        Map<Integer, Object[]> shadow = null;
        if ( this.reverse != null ) {
            shadow = new HashMap<Integer, Object[]>();
        }
        return new MVELAccumulatorContext( shadow );
    }

    /* (non-Javadoc)
     * @see org.drools.spi.Accumulator#init(java.lang.Object, org.drools.spi.Tuple, org.drools.rule.Declaration[], org.drools.WorkingMemory)
     */
    public void init(Object workingMemoryContext,
                     Object context,
                     Tuple leftTuple,
                     Declaration[] declarations,
                     WorkingMemory workingMemory) throws Exception {
        Object[] localVars = new Object[initUnit.getOtherIdentifiers().length];        
        VariableResolverFactory factory = initUnit.getFactory( null, null, (LeftTuple) leftTuple, localVars, null, (InternalWorkingMemory) workingMemory );
        
        Package pkg = workingMemory.getRuleBase().getPackage( "MAIN" );
        if ( pkg != null ) {
            MVELDialectRuntimeData data = (MVELDialectRuntimeData) pkg.getDialectRuntimeRegistry().getDialectData( "mvel" );
            factory.setNextFactory( data.getFunctionFactory() );
        }

        MVEL.executeExpression( this.init,
                                null,
                                factory );
        
        ((MVELAccumulatorContext) context).setVariables( localVars );
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
        Object[]  localVars = ((MVELAccumulatorContext) context).getVariables();        
        VariableResolverFactory factory = actionUnit.getFactory( null, null, (LeftTuple) leftTuple, localVars, null, (InternalWorkingMemory) workingMemory );

        if ( reverse != null ) {
            // SNAPSHOT variable values
            takeSnapshot(  (MVELAccumulatorContext) context,
                          handle );
        }
        MVEL.executeExpression( this.action,
                                null,
                                factory );
    }

    private void takeSnapshot(MVELAccumulatorContext context,
                              InternalFactHandle handle) {
        Object[] vars = context.getVariables();
        Object[] shad = new Object[vars.length];
        
        for ( int i = 0, length = vars.length; i < length; i++ ) {
            shad[i] = vars[i];
        }        
        context.getShadow().put( handle.getId(), shad );
    }

    public void reverse(Object workingMemoryContext,
                        Object context,
                        Tuple leftTuple,
                        InternalFactHandle handle,
                        Declaration[] declarations,
                        Declaration[] innerDeclarations,
                        WorkingMemory workingMemory) throws Exception {
        Object[] localVars = ((MVELAccumulatorContext) context).shadow.remove( handle.getId() );        
        VariableResolverFactory factory = actionUnit.getFactory( null, null, (LeftTuple) leftTuple, localVars, null, (InternalWorkingMemory) workingMemory );        
        

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
        Object[]  localVars = ((MVELAccumulatorContext) context).getVariables();        
        VariableResolverFactory factory = actionUnit.getFactory( null, null, (LeftTuple) leftTuple, localVars, null, (InternalWorkingMemory) workingMemory );

        final Object result = MVEL.executeExpression( this.result,
                                                      null,
                                                      factory );
        return result;
    }

    public boolean supportsReverse() {
        return this.reverse != null;
    }

    public Object createWorkingMemoryContext() {
        return null;
    }

    private static class MVELAccumulatorContext
        implements
        Serializable {

        private static final long                      serialVersionUID = 510l;

        private Object[]               variables;
        private Map<Integer, Object[]> shadow;

        public MVELAccumulatorContext(Map<Integer,  Object[]> shadow) {
            this.shadow = shadow;
        }

        public Object[] getVariables() {
            return variables;
        }

        public void setVariables(Object[] variables) {
            this.variables = variables;
        }

        public Map<Integer, Object[]> getShadow() {
            return shadow;
        }

        public void setShadow(Map<Integer, Object[]> shadow) {
            this.shadow = shadow;
        }
        
        
    }

}
