/*
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

import org.drools.WorkingMemory;
import org.drools.common.InternalRuleBase;
import org.drools.common.InternalWorkingMemory;
import org.drools.reteoo.LeftTuple;
import org.drools.rule.MVELDialectRuntimeData;
import org.drools.rule.Package;
import org.drools.spi.Consequence;
import org.drools.spi.KnowledgeHelper;
import org.mvel2.MVEL;
import org.mvel2.compiler.CompiledExpression;
import org.mvel2.debug.DebugTools;
import org.mvel2.integration.VariableResolverFactory;
import org.mvel2.integration.impl.CachingMapVariableResolverFactory;
import org.mvel2.integration.impl.IndexedVariableResolverFactory;

public class MVELConsequence
    implements
    Consequence,
    MVELCompileable,
        Externalizable {
    private static final long   serialVersionUID = 510l;

    private MVELCompilationUnit unit;
    private String              id;

    private Serializable        expr;

    public MVELConsequence() {
    }

    public MVELConsequence(final MVELCompilationUnit unit,
                           final String id) {
        this.unit = unit;
        this.id = id;
    }

    public void readExternal(ObjectInput in) throws IOException,
                                            ClassNotFoundException {
        unit = (MVELCompilationUnit) in.readObject();
        id = in.readUTF();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject( unit );
        out.writeUTF( id );
    }

    public void compile(ClassLoader classLoader) {
        expr = unit.getCompiledExpression( classLoader );
    }

    public void evaluate(final KnowledgeHelper knowledgeHelper,
                         final WorkingMemory workingMemory) throws Exception {
        VariableResolverFactory factory = unit.getFactory( knowledgeHelper, knowledgeHelper.getRule(), knowledgeHelper, (LeftTuple) knowledgeHelper.getTuple(), null, (InternalWorkingMemory) workingMemory );
        
        // do we have any functions for this namespace?
        Package pkg = workingMemory.getRuleBase().getPackage( "MAIN" );
        if ( pkg != null ) {
            MVELDialectRuntimeData data = (MVELDialectRuntimeData) pkg.getDialectRuntimeRegistry().getDialectData( this.id );
            factory.setNextFactory( data.getFunctionFactory() );
        }

        CompiledExpression compexpr = (CompiledExpression) this.expr;

        pkg = knowledgeHelper.getWorkingMemory().getRuleBase().getPackage(knowledgeHelper.getRule().getPackage() );

        ClassLoader tempClassLoader = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader( ((InternalRuleBase) workingMemory.getRuleBase()).getRootClassLoader() );

        try {
            if ( MVELDebugHandler.isDebugMode() ) {
                if ( MVELDebugHandler.verbose ) {
                    System.out.println( "Executing expression " + compexpr.getSourceName() );
                    System.out.println( DebugTools.decompile( compexpr ) );
                }
                MVEL.executeDebugger( compexpr,
                                      knowledgeHelper,
                                      factory );
            } else {
                MVEL.executeExpression( compexpr,
                                        knowledgeHelper,
                                        factory );
            }
        } finally {
            Thread.currentThread().setContextClassLoader( tempClassLoader );
        }
    }

    public Serializable getCompExpr() {
        return expr;
    }

    public String getName() {
        return "default";
    }

}
