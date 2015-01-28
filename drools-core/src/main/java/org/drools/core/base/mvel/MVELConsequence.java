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

package org.drools.core.base.mvel;

import org.drools.core.WorkingMemory;
import org.drools.core.common.AgendaItem;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.definitions.InternalKnowledgePackage;
import org.drools.core.definitions.rule.impl.RuleImpl;
import org.drools.core.reteoo.LeftTuple;
import org.drools.core.rule.MVELDialectRuntimeData;
import org.drools.core.spi.Consequence;
import org.drools.core.spi.KnowledgeHelper;
import org.mvel2.MVEL;
import org.mvel2.compiler.CompiledExpression;
import org.mvel2.debug.DebugTools;
import org.mvel2.integration.VariableResolverFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Serializable;

public class MVELConsequence
    implements
    Consequence,
    MVELCompileable,
        Externalizable {
    private static final long   serialVersionUID = 510l;
    protected static transient Logger logger = LoggerFactory.getLogger(MVELConsequence.class);

    private MVELCompilationUnit unit;
    private String              id;

    private Serializable        expr;

    private String              consequenceName;

    public MVELConsequence() {
    }

    public MVELConsequence(final MVELCompilationUnit unit,
                           final String id,
                           String consequenceName) {
        this.unit = unit;
        this.id = id;
        this.consequenceName = consequenceName;
    }

    public void readExternal(ObjectInput in) throws IOException,
                                            ClassNotFoundException {
        unit = (MVELCompilationUnit) in.readObject();
        id = in.readUTF();
        consequenceName = in.readUTF();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject( unit );
        out.writeUTF( id );
        out.writeUTF(consequenceName);
    }

    public void compile(MVELDialectRuntimeData runtimeData) {
        expr = unit.getCompiledExpression( runtimeData );
    }

    public void compile(MVELDialectRuntimeData runtimeData, RuleImpl rule) {
        expr = unit.getCompiledExpression( runtimeData, rule.toRuleNameAndPathString() );
    }

    public void evaluate(final KnowledgeHelper knowledgeHelper,
                         final WorkingMemory workingMemory) throws Exception {
        
        VariableResolverFactory factory = unit.getFactory( knowledgeHelper,  ((AgendaItem)knowledgeHelper.getMatch()).getTerminalNode().getDeclarations(),
                                                           knowledgeHelper.getRule(), (LeftTuple) knowledgeHelper.getTuple(), null, (InternalWorkingMemory) workingMemory, workingMemory.getGlobalResolver()  );
        
        // do we have any functions for this namespace?
        InternalKnowledgePackage pkg = workingMemory.getKnowledgeBase().getPackage( "MAIN" );
        if ( pkg != null ) {
            MVELDialectRuntimeData data = (MVELDialectRuntimeData) pkg.getDialectRuntimeRegistry().getDialectData( this.id );
            factory.setNextFactory( data.getFunctionFactory() );
        }

        CompiledExpression compexpr = (CompiledExpression) this.expr;

        if ( MVELDebugHandler.isDebugMode() ) {
            if ( MVELDebugHandler.verbose ) {
                logger.info(DebugTools.decompile(compexpr));
            }
            MVEL.executeDebugger( compexpr,
                                  knowledgeHelper,
                                  factory );
        } else {
            MVEL.executeExpression( compexpr,
                                    knowledgeHelper,
                                    factory );
        }
    }

    public Serializable getCompExpr() {
        return expr;
    }

    public String getName() {
        return consequenceName;
    }

}
