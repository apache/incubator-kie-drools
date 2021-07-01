/*
 * Copyright (c) 2020. Red Hat, Inc. and/or its affiliates.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.mvel.expr;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Arrays;
import java.util.Map;

import org.drools.core.WorkingMemory;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.definitions.InternalKnowledgePackage;
import org.drools.core.definitions.rule.impl.RuleImpl;
import org.drools.core.reteoo.LeftTuple;
import org.drools.core.reteoo.RuleTerminalNode;
import org.drools.core.rule.Declaration;
import org.drools.core.spi.Enabled;
import org.drools.core.spi.Tuple;
import org.drools.mvel.MVELDialectRuntimeData;
import org.mvel2.integration.VariableResolverFactory;

import static org.drools.mvel.expr.MvelEvaluator.createMvelEvaluator;

public class MVELEnabledExpression
    implements
    Enabled,
    MVELCompileable,
    Externalizable {

    private static final long   serialVersionUID = 510l;

    private MVELCompilationUnit unit;
    private String              id;

    private MvelEvaluator<Boolean> evaluator;

    public MVELEnabledExpression() {
    }

    public MVELEnabledExpression(final MVELCompilationUnit unit,
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
    
    public MVELCompilationUnit getMVELCompilationUnit() {
        return this.unit;
    }    

    public void compile( MVELDialectRuntimeData runtimeData) {
        evaluator = createMvelEvaluator( unit.getCompiledExpression( runtimeData ) );
    }

    public void compile( MVELDialectRuntimeData runtimeData, RuleImpl rule ) {
        evaluator = createMvelEvaluator( unit.getCompiledExpression( runtimeData, rule.toRuleNameAndPathString() ) );
    }

    public boolean getValue(final Tuple tuple,
                            final Declaration[] declarations,
                            final RuleImpl rule,
                            final WorkingMemory workingMemory) {
        VariableResolverFactory factory = unit.getFactory( null, declarations,
                                                           rule, null, (LeftTuple) tuple, null, (InternalWorkingMemory) workingMemory, workingMemory.getGlobalResolver()  );

        // do we have any functions for this namespace?
        InternalKnowledgePackage pkg = workingMemory.getKnowledgeBase().getPackage( "MAIN" );
        if ( pkg != null ) {
            MVELDialectRuntimeData data = ( MVELDialectRuntimeData ) pkg.getDialectRuntimeRegistry().getDialectData( this.id );
            factory.setNextFactory( data.getFunctionFactory() );
        }

        return evaluator.evaluate( factory );
    }
    
    public String toString() {
        return this.unit.getExpression();
    }

    @Override
    public Declaration[] findDeclarations( Map<String, Declaration> decls) {
        Declaration[] declrs = unit.getPreviousDeclarations();

        Declaration[] enabledDeclarations = new Declaration[declrs.length];
        int i = 0;
        for ( Declaration declr : declrs ) {
            enabledDeclarations[i++] = decls.get( declr.getIdentifier() );
        }
        Arrays.sort( enabledDeclarations, RuleTerminalNode.SortDeclarations.instance );
        return enabledDeclarations;
    }
}
