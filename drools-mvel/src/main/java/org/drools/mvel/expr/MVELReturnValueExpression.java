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
import java.util.Arrays;

import org.drools.base.base.ValueResolver;
import org.drools.base.definitions.InternalKnowledgePackage;
import org.drools.base.definitions.rule.impl.RuleImpl;
import org.drools.core.impl.InternalRuleBase;
import org.drools.core.impl.KnowledgeBaseImpl;
import org.drools.base.reteoo.BaseTuple;
import org.drools.base.rule.Declaration;
import org.drools.base.rule.accessor.FieldValue;
import org.drools.base.rule.accessor.ReturnValueExpression;
import org.drools.mvel.MVELDialectRuntimeData;
import org.drools.mvel.field.FieldFactory;
import org.kie.api.runtime.rule.FactHandle;
import org.mvel2.integration.VariableResolverFactory;

import static org.drools.mvel.expr.MvelEvaluator.createMvelEvaluator;

public class MVELReturnValueExpression
    implements
    ReturnValueExpression,
    MVELCompileable,
    Externalizable {
    private static final long   serialVersionUID = 510l;

    private MVELCompilationUnit unit;
    private String              id;

    private MvelEvaluator<Object> evaluator;

    public MVELReturnValueExpression() {
    }

    public MVELReturnValueExpression(final MVELCompilationUnit unit,
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

    public FieldValue evaluate(final FactHandle handle,
                               final BaseTuple tuple,
                               final Declaration[] previousDeclarations,
                               final Declaration[] requiredDeclarations,
                               final ValueResolver valueResolver,
                               final Object ctx) throws Exception {
        VariableResolverFactory factory = ( VariableResolverFactory )ctx;
        
        unit.updateFactory( handle, tuple, null, valueResolver, valueResolver.getGlobalResolver(), factory );

        
        // do we have any functions for this namespace?
        InternalKnowledgePackage pkg = ((InternalRuleBase)valueResolver.getRuleBase()).getPackage("MAIN");
        if ( pkg != null ) {
            MVELDialectRuntimeData data = ( MVELDialectRuntimeData ) pkg.getDialectRuntimeRegistry().getDialectData( this.id );
            factory.setNextFactory( data.getFunctionFactory() );
        }

        Object value = evaluator.evaluate( handle, factory );
        return FieldFactory.getInstance().getFieldValue( value );
    }


    public String toString() {
        return this.unit.getExpression();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        if ( evaluator == null ) {
            throw new RuntimeException( "this MVELReturnValueExpression must be compiled for hashCode" );
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

        MVELReturnValueExpression other = (MVELReturnValueExpression) obj;
        if ( other.evaluator == null ) {
            throw new RuntimeException( "other MVELReturnValueExpression must be compiled for equality" );
        }
                
        return this.unit.getExpression().equals( other.unit.getExpression() );
    }

    public void replaceDeclaration(Declaration declaration,
                                   Declaration resolved) {
        this.unit.replaceDeclaration( declaration,
                                      resolved );
    }

}
