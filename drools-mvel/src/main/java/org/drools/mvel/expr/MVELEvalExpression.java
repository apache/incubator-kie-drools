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

import org.drools.base.base.ValueResolver;
import org.drools.base.definitions.InternalKnowledgePackage;
import org.drools.base.definitions.rule.impl.RuleImpl;
import org.drools.base.reteoo.BaseTuple;
import org.drools.base.rule.Declaration;
import org.drools.base.rule.accessor.EvalExpression;
import org.drools.kiesession.rulebase.InternalKnowledgeBase;
import org.drools.mvel.MVELDialectRuntimeData;
import org.mvel2.integration.VariableResolverFactory;

import static org.drools.mvel.expr.MvelEvaluator.createMvelEvaluator;

public class MVELEvalExpression
    implements
    EvalExpression,
    MVELCompileable,
    Externalizable {

    private static final long   serialVersionUID = 510l;

    private MVELCompilationUnit unit;
    private String              id;

    private MvelEvaluator<Boolean> evaluator;

    public MVELEvalExpression() {
    }

    public MVELEvalExpression(final MVELCompilationUnit unit,
                              final String id) {
        this.unit = unit;
        this.id = id;
    }

    public void readExternal(ObjectInput in) throws IOException,
                                            ClassNotFoundException {
        id = in.readUTF();
        unit = (MVELCompilationUnit) in.readObject();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeUTF( id );
        out.writeObject( unit );
    }

    public void compile( MVELDialectRuntimeData runtimeData) {
        evaluator = createMvelEvaluator( unit.getCompiledExpression( runtimeData ) );
    }

    public void compile( MVELDialectRuntimeData runtimeData, RuleImpl rule ) {
        evaluator = createMvelEvaluator( unit.getCompiledExpression( runtimeData, rule.toRuleNameAndPathString() ) );
    }

    public Object createContext() {
        return this.unit.createFactory();
    }    

    public boolean evaluate(final BaseTuple tuple,
                            final Declaration[] requiredDeclarations,
                            final ValueResolver valueResolver,
                            final Object context) throws Exception {
        VariableResolverFactory factory = ( VariableResolverFactory ) context;
        
        unit.updateFactory( null,
                            tuple,
                            null,
                            valueResolver,
                            valueResolver.getGlobalResolver(),
                            factory );

        // do we have any functions for this namespace?
        InternalKnowledgePackage pkg = ((InternalKnowledgeBase)valueResolver.getRuleBase()).getPackage("MAIN");
        if ( pkg != null ) {
            MVELDialectRuntimeData data = ( MVELDialectRuntimeData ) pkg.getDialectRuntimeRegistry().getDialectData( this.id );
            factory.setNextFactory( data.getFunctionFactory() );
        }

        return evaluator.evaluate( factory );
    }

    public String toString() {
        return this.unit.getExpression();
    }

    public void replaceDeclaration(Declaration declaration,
                                   Declaration resolved) {
        this.unit.replaceDeclaration( declaration,
                                      resolved );
    }

    public MVELEvalExpression clone() {
        MVELEvalExpression clone = new MVELEvalExpression( unit.clone(), id );
        // expr should be stateless, so it should be fine to share the reference
        clone.evaluator = evaluator;
        
        return clone;
    }

    @Override
    public MVELEvalExpression clonePreservingDeclarations(EvalExpression original) {
        MVELCompilationUnit cloneUnit = unit.clone();
        cloneUnit.setPreviousDeclarations( ((MVELEvalExpression)original).unit.getPreviousDeclarations() );
        MVELEvalExpression clone = new MVELEvalExpression( cloneUnit, id );
        // expr should be stateless, so it should be fine to share the reference
        clone.evaluator = evaluator;

        return clone;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        if ( evaluator == null ) {
            throw new RuntimeException( "this MVELPredicateExpression must be compiled for hashCode" );
        }
        result = prime * result + unit.getExpression().hashCode();
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if ( this == obj ) return true;
        if ( obj == null ) return false;
        if ( getClass() != obj.getClass() ) return false;

        if ( evaluator == null ) {
            throw new RuntimeException( "this MVELReturnValueExpression must be compiled for equality" );
        }

        MVELEvalExpression other = (MVELEvalExpression) obj;
        if ( other.evaluator == null ) {
            throw new RuntimeException( "other MVELReturnValueExpression must be compiled for equality" );
        }
                
        return this.unit.getExpression().equals( other.unit.getExpression() );
    }
        

}
