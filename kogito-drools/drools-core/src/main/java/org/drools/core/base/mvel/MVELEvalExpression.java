/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
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

package org.drools.core.base.mvel;

import org.drools.core.WorkingMemory;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.definitions.InternalKnowledgePackage;
import org.drools.core.definitions.rule.impl.RuleImpl;
import org.drools.core.reteoo.LeftTuple;
import org.drools.core.rule.Declaration;
import org.drools.core.rule.MVELDialectRuntimeData;
import org.drools.core.spi.EvalExpression;
import org.drools.core.spi.Tuple;
import org.mvel2.MVEL;
import org.mvel2.integration.VariableResolverFactory;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Serializable;

public class MVELEvalExpression
    implements
    EvalExpression,
    MVELCompileable,
    Externalizable {

    private static final long   serialVersionUID = 510l;

    private MVELCompilationUnit unit;
    private String              id;

    private Serializable        expr;

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

    public void compile(MVELDialectRuntimeData runtimeData) {
        expr = unit.getCompiledExpression( runtimeData );
    }

    public void compile( MVELDialectRuntimeData runtimeData, RuleImpl rule ) {
        expr = unit.getCompiledExpression( runtimeData, rule.toRuleNameAndPathString() );
    }

    public Object createContext() {
        return this.unit.createFactory();
    }    

    public boolean evaluate(final Tuple tuple,
                            final Declaration[] requiredDeclarations,
                            final WorkingMemory workingMemory,
                            final Object context) throws Exception {
        VariableResolverFactory factory = ( VariableResolverFactory ) context;
        
        unit.updateFactory( null,
                            null,
                            null,
                            (LeftTuple) tuple,
                            null,
                            (InternalWorkingMemory) workingMemory,
                            workingMemory.getGlobalResolver(),
                            factory );

        // do we have any functions for this namespace?
        InternalKnowledgePackage pkg = workingMemory.getKnowledgeBase().getPackage( "MAIN" );
        if ( pkg != null ) {
            MVELDialectRuntimeData data = (MVELDialectRuntimeData) pkg.getDialectRuntimeRegistry().getDialectData( this.id );
            factory.setNextFactory( data.getFunctionFactory() );
        }

        final Boolean result = (Boolean) MVEL.executeExpression( this.expr,
                                                                 null,
                                                                 factory );
        return result.booleanValue();
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
        MVELEvalExpression clone = new MVELEvalExpression( unit.clone(),
                                                           id );
        // expr should be stateless, so it should be fine to share the reference
        clone.expr = expr;
        
        return clone;
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        if ( expr == null ) {
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

        if ( expr == null ) {
            throw new RuntimeException( "this MVELReturnValueExpression must be compiled for equality" );
        }

        MVELEvalExpression other = (MVELEvalExpression) obj;
        if ( other.expr == null ) {
            throw new RuntimeException( "other MVELReturnValueExpression must be compiled for equality" );
        }
                
        return this.unit.getExpression().equals( other.unit.getExpression() );
    }
        

}
