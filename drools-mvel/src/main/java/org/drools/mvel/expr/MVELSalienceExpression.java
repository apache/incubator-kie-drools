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
import org.drools.core.common.AgendaItem;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.definitions.InternalKnowledgePackage;
import org.drools.core.definitions.rule.impl.RuleImpl;
import org.drools.core.reteoo.LeftTuple;
import org.drools.core.reteoo.RuleTerminalNode;
import org.drools.core.rule.Declaration;
import org.drools.core.spi.KnowledgeHelper;
import org.drools.core.spi.Salience;
import org.drools.core.time.TimeUtils;
import org.drools.mvel.MVELDialectRuntimeData;
import org.kie.api.definition.rule.Rule;
import org.mvel2.integration.VariableResolverFactory;

import static org.drools.mvel.expr.MvelEvaluator.createMvelEvaluator;

public class MVELSalienceExpression
    implements
    Salience,
    MVELCompileable,
    Externalizable {

    private static final long   serialVersionUID = 510l;

    private MVELCompilationUnit unit;
    private String              id;

    private MvelEvaluator<Object> evaluator;

    public MVELSalienceExpression() {
    }

    public MVELSalienceExpression(final MVELCompilationUnit unit,
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

    public int getValue(final KnowledgeHelper khelper,
                        final Rule rule,
                        final WorkingMemory workingMemory) {
        VariableResolverFactory factory = unit.getFactory( khelper,
                                                           khelper != null ? (( AgendaItem ) khelper.getMatch()).getTerminalNode().getSalienceDeclarations() : null,
                                                           rule, null, 
                                                           khelper != null ? (LeftTuple) khelper.getMatch().getTuple() : null, 
                                                           null, (InternalWorkingMemory) workingMemory, workingMemory.getGlobalResolver() );
        
        // do we have any functions for this namespace?
        InternalKnowledgePackage pkg = workingMemory.getKnowledgeBase().getPackage( "MAIN" );
        if ( pkg != null ) {
            MVELDialectRuntimeData data = ( MVELDialectRuntimeData ) pkg.getDialectRuntimeRegistry().getDialectData( this.id );
            factory.setNextFactory( data.getFunctionFactory() );
        }

        Object value = evaluator.evaluate( factory );
        if (value instanceof String) {
            value = TimeUtils.parseTimeString( (String)value );
        }
        return ((Number)value).intValue();
    }

    @Override
    public Declaration[] findDeclarations( Map<String, Declaration> decls) {
        Declaration[] declrs = unit.getPreviousDeclarations();

        Declaration[] salienceDeclarations = new Declaration[declrs.length];
        int i = 0;
        for ( Declaration declr : declrs ) {
            salienceDeclarations[i++] = decls.get( declr.getIdentifier() );
        }
        Arrays.sort( salienceDeclarations, RuleTerminalNode.SortDeclarations.instance );
        return salienceDeclarations;
    }

    public boolean isDefault() {
        return false;
    }

    public int getValue() {
        throw new UnsupportedOperationException();
    }

    public boolean isDynamic() {
        return true;
    }

    public String toString() {
        return this.unit.getExpression();
    }

}
