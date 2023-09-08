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
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.drools.base.base.ValueResolver;
import org.drools.base.definitions.InternalKnowledgePackage;
import org.drools.base.definitions.rule.impl.RuleImpl;
import org.drools.base.reteoo.BaseTuple;
import org.drools.base.rule.Declaration;
import org.drools.base.rule.accessor.Accumulator;
import org.drools.kiesession.rulebase.InternalKnowledgeBase;
import org.drools.mvel.MVELDialectRuntimeData;
import org.drools.mvel.expr.MVELCompilationUnit.DroolsVarFactory;
import org.kie.api.runtime.rule.FactHandle;
import org.mvel2.integration.VariableResolverFactory;

import static org.drools.mvel.expr.MvelEvaluator.createMvelEvaluator;

/**
 * An MVEL accumulator implementation
 */
public class MVELAccumulator
    implements MVELCompileable, Accumulator, Externalizable {

    private static final long serialVersionUID = 510l;

    MVELCompilationUnit       initUnit;
    MVELCompilationUnit       actionUnit;
    MVELCompilationUnit       reverseUnit;
    MVELCompilationUnit       resultUnit;
    
    private MvelEvaluator<Void> init;
    private MvelEvaluator<Void> action;
    private MvelEvaluator<Void> reverse;
    private MvelEvaluator<Void> result;

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

    public void compile( MVELDialectRuntimeData runtimeData) {
        compile(runtimeData, null);
    }

    public void compile( MVELDialectRuntimeData runtimeData, RuleImpl rule) {
        init = createMvelEvaluator( initUnit.getCompiledExpression( runtimeData ) );
        action = createMvelEvaluator( actionUnit.getCompiledExpression( runtimeData ) );
        result = createMvelEvaluator( resultUnit.getCompiledExpression( runtimeData ) );
                
        if ( reverseUnit != null ) {
            reverse = createMvelEvaluator( reverseUnit.getCompiledExpression( runtimeData, rule != null ? rule.toRuleNameAndPathString() : null ) );
        }
    }

    /* (non-Javadoc)
     * @see org.kie.spi.Accumulator#createContext()
     */
    public Object createContext() {
        return new MVELAccumulatorContext( );
    }

    /* (non-Javadoc)
     * @see org.kie.spi.Accumulator#init(java.lang.Object, org.kie.spi.Tuple, org.kie.rule.Declaration[], org.kie.WorkingMemory)
     */
    public Object init(Object workingMemoryContext,
                       Object context,
                       BaseTuple tuple,
                       Declaration[] declarations,
                       ValueResolver valueResolver) {
        Object[] localVars = new Object[initUnit.getOtherIdentifiers().length];
        
        MVELAccumulatorFactoryContext factoryContext = (MVELAccumulatorFactoryContext)workingMemoryContext;
        VariableResolverFactory factory = factoryContext.getInitFactory();
        initUnit.updateFactory( null, tuple, localVars, valueResolver, valueResolver.getGlobalResolver(), factory );

        InternalKnowledgePackage pkg = ((InternalKnowledgeBase)valueResolver.getRuleBase()).getPackage("MAIN");
        if ( pkg != null ) {
            MVELDialectRuntimeData data = ( MVELDialectRuntimeData ) pkg.getDialectRuntimeRegistry().getDialectData( "mvel" );
            factory.setNextFactory( data.getFunctionFactory() );
        }

        this.init.evaluate( factory );
        
        
        DroolsVarFactory df = ( DroolsVarFactory ) factory.getNextFactory();
        if ( localVars.length > 0 ) {
            for ( int i = 0; i < df.getOtherVarsLength(); i++ ) {
                localVars[i] = factory.getIndexedVariableResolver( df.getOtherVarsPos() + i ).getValue();
            }
        }
        
        ((MVELAccumulatorContext) context).setVariables( localVars );
        return context;
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
        Object[]  localVars = ((MVELAccumulatorContext) context).getVariables();
        MVELAccumulatorFactoryContext factoryContext = (MVELAccumulatorFactoryContext)workingMemoryContext;
        VariableResolverFactory factory = factoryContext.getActionFactory();
        actionUnit.updateFactory( handle, tuple, localVars, valueResolver, valueResolver.getGlobalResolver(), factory );

        DroolsVarFactory df = ( DroolsVarFactory ) factory.getNextFactory();

        Object[] shadow = null;
        if ( reverse != null ) {
            shadow = new Object [df.getOtherVarsPos()];
            for ( int i = 0; i < df.getOtherVarsPos(); i++ ) {
                shadow[i] = factory.getIndexedVariableResolver( i ).getValue();
            }
        }

        this.action.evaluate( factory );

        if ( localVars.length > 0 ) {
            for ( int i = 0; i < df.getOtherVarsLength(); i++ ) {
                localVars[i] = factory.getIndexedVariableResolver( df.getOtherVarsPos() + i ).getValue();
            }
        }
        
        ((MVELAccumulatorContext) context).setVariables( localVars );

        return shadow;
    }

    public boolean tryReverse(Object workingMemoryContext,
                              Object context,
                              BaseTuple leftTuple,
                              FactHandle handle,
                              Object value,
                              Declaration[] declarations,
                              Declaration[] innerDeclarations,
                              ValueResolver valueResolver) {

        if (!supportsReverse()) {
            return false;
        }

        Object[]  localVars = ((MVELAccumulatorContext) context).getVariables();
        MVELAccumulatorFactoryContext factoryContext = (MVELAccumulatorFactoryContext)workingMemoryContext;
        
        VariableResolverFactory factory = factoryContext.getActionFactory();
        DroolsVarFactory df = ( DroolsVarFactory ) factory.getNextFactory();
        
        Object[] vars = (Object[]) value;
        for ( int i = 0; i < df.getOtherVarsPos(); i++ ) {
            factory.getIndexedVariableResolver( i ).setValue(vars[i]);  
        }

        if ( localVars.length > 0 ) {
            for ( int i = 0; i < df.getOtherVarsLength(); i++ ) {
                factory.getIndexedVariableResolver( df.getOtherVarsPos() + i ).setValue( localVars[i] );
            }
        }

        this.reverse.evaluate( factory );

        if ( localVars.length > 0 ) {
            for ( int i = 0; i < df.getOtherVarsLength(); i++ ) {
                localVars[i] = factory.getIndexedVariableResolver( df.getOtherVarsPos() + i ).getValue();
            }
        }
        
        ((MVELAccumulatorContext) context).setVariables( localVars );

        return true;
    }

    /* (non-Javadoc)
     * @see org.kie.spi.Accumulator#getResult(java.lang.Object, org.kie.spi.Tuple, org.kie.rule.Declaration[], org.kie.WorkingMemory)
     */
    public Object getResult(Object workingMemoryContext,
                            Object context,
                            BaseTuple tuple,
                            Declaration[] declarations,
                            ValueResolver valueResolver) {
        Object[]  localVars = ((MVELAccumulatorContext) context).getVariables();
        
        MVELAccumulatorFactoryContext factoryContext = (MVELAccumulatorFactoryContext)workingMemoryContext;
        VariableResolverFactory factory = factoryContext.getResultFactory();
        resultUnit.updateFactory( null, tuple, localVars, valueResolver, valueResolver.getGlobalResolver(), factory );

        return this.result.evaluate( factory );
    }

    public boolean supportsReverse() {
        return this.reverse != null;
    }

    public Object createWorkingMemoryContext() {        
        return new MVELAccumulatorFactoryContext(initUnit.createFactory(), 
                                                 actionUnit.createFactory(),
                                                 resultUnit.createFactory() );
    }

    @Override
    public Declaration[] getRequiredDeclarations() {
        Set<Declaration> declarationSet = new HashSet<>();
        if ( initUnit != null ) {
            declarationSet.addAll( Arrays.asList( initUnit.getPreviousDeclarations() ) );
        }
        if ( actionUnit != null ) {
            declarationSet.addAll( Arrays.asList( actionUnit.getPreviousDeclarations() ) );
        }
        if ( resultUnit != null ) {
            declarationSet.addAll( Arrays.asList( resultUnit.getPreviousDeclarations() ) );
        }
        if ( reverseUnit != null ) {
            declarationSet.addAll( Arrays.asList( reverseUnit.getPreviousDeclarations() ) );
        }
        if ( ! declarationSet.isEmpty() ) {
            return declarationSet.toArray( new Declaration[ declarationSet.size() ] );
        } else {
            return new Declaration[ 0 ];
        }
    }

    private static class MVELAccumulatorFactoryContext {
        VariableResolverFactory initFactory;
        VariableResolverFactory actionFactory;
        VariableResolverFactory resultFactory;
        public MVELAccumulatorFactoryContext(VariableResolverFactory initFactory,
                                             VariableResolverFactory actionFactory,
                                             VariableResolverFactory resultFactory) {
            this.initFactory = initFactory;
            this.actionFactory = actionFactory;
            this.resultFactory = resultFactory;
        }
        public VariableResolverFactory getInitFactory() {
            return initFactory;
        }
        public VariableResolverFactory getActionFactory() {
            return actionFactory;
        }

        public VariableResolverFactory getResultFactory() {
            return resultFactory;
        }
    }

    private static class MVELAccumulatorContext
        implements
        Serializable {

        private static final long serialVersionUID = 510l;

        private Object[]          variables;

        public MVELAccumulatorContext() {
        }

        public Object[] getVariables() {
            return variables;
        }

        public void setVariables(Object[] variables) {
            this.variables = variables;
        }
    }

}
