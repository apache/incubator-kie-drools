/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.process.instance.impl;

import org.drools.core.base.mvel.MVELCompilationUnit;
import org.drools.core.base.mvel.MVELCompileable;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.definitions.impl.KnowledgePackageImpl;
import org.drools.core.definitions.rule.impl.RuleImpl;
import org.drools.core.impl.StatefulKnowledgeSessionImpl;
import org.drools.core.impl.StatelessKnowledgeSessionImpl;
import org.drools.core.rule.MVELDialectRuntimeData;
import org.drools.core.spi.GlobalResolver;
import org.drools.core.util.MVELSafeHelper;
import org.kie.api.definition.KiePackage;
import org.kie.api.runtime.process.ProcessContext;
import org.kie.internal.runtime.StatefulKnowledgeSession;
import org.kie.internal.runtime.StatelessKnowledgeSession;
import org.mvel2.integration.VariableResolverFactory;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Serializable;

public class MVELReturnValueEvaluator
    implements
    ReturnValueEvaluator,
    MVELCompileable,
    Externalizable {
    private static final long   serialVersionUID = 510l;

    private MVELCompilationUnit unit;
    private String              id;

    private Serializable        expr;

    public MVELReturnValueEvaluator() {
    }

    public MVELReturnValueEvaluator(final MVELCompilationUnit unit,
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

    public void compile(MVELDialectRuntimeData data) {
        expr = unit.getCompiledExpression( data );
    }

    public void compile(MVELDialectRuntimeData data, RuleImpl rule) {
        expr = unit.getCompiledExpression( data );
    }

    public String getDialect() {
        return this.id;
    }

    public Object evaluate(ProcessContext context) throws Exception {
        int length = unit.getOtherIdentifiers().length;
        Object[] vars = new Object[ length ];
        if (unit.getOtherIdentifiers() != null) {
            for (int i = 0; i < length; i++ ) {
                vars[i] = context.getVariable( unit.getOtherIdentifiers()[i] );
            }
        }

        InternalWorkingMemory internalWorkingMemory = null;
        if( context.getKieRuntime() instanceof StatefulKnowledgeSessionImpl ) {
            internalWorkingMemory = ((StatefulKnowledgeSessionImpl) context.getKieRuntime()).getInternalWorkingMemory();
        } else if( context.getKieRuntime() instanceof StatelessKnowledgeSession) {
            StatefulKnowledgeSession statefulKnowledgeSession = ((StatelessKnowledgeSessionImpl) context.getKieRuntime()).newWorkingMemory();
            internalWorkingMemory = ((StatefulKnowledgeSessionImpl) statefulKnowledgeSession).getInternalWorkingMemory();
        }
        
        VariableResolverFactory factory 
            = unit.getFactory( context, 
                               null, // No previous declarations
                               null, // No rule
                               null, // No "right object" 
                               null, // No (left) tuples
                               vars, 
                               internalWorkingMemory,
                               (GlobalResolver) context.getKieRuntime().getGlobals() );

        // do we have any functions for this namespace?
        KiePackage pkg = context.getKieRuntime().getKieBase().getKiePackage("MAIN");
        if ( pkg instanceof KnowledgePackageImpl) {
            MVELDialectRuntimeData data = ( MVELDialectRuntimeData ) ((KnowledgePackageImpl) pkg).getDialectRuntimeRegistry().getDialectData( id );
            factory.setNextFactory( data.getFunctionFactory() );
        }

        Object value = MVELSafeHelper.getEvaluator().executeExpression( this.expr,
	                                           null,
	                                           factory );

        if ( !(value instanceof Boolean) ) {
            throw new RuntimeException( "Constraints must return boolean values: " + 
        		unit.getExpression() + " returns " + value + 
        		(value == null? "" : " (type=" + value.getClass()));
        }
        return ((Boolean) value).booleanValue();

    }

    public Serializable getCompExpr() {
        return expr;
    }
    
    public String toString() {
        return this.unit.getExpression();
    }    

}
