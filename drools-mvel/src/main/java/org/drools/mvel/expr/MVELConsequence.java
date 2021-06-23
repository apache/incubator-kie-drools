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
import java.io.Serializable;

import org.drools.core.WorkingMemory;
import org.drools.core.common.AgendaItem;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.definitions.InternalKnowledgePackage;
import org.drools.core.definitions.rule.impl.RuleImpl;
import org.drools.core.spi.Consequence;
import org.drools.core.spi.KnowledgeHelper;
import org.drools.mvel.MVELDialectRuntimeData;
import org.mvel2.integration.VariableResolverFactory;

import static org.drools.mvel.expr.MvelEvaluator.createMvelEvaluator;

public class MVELConsequence
    implements
    Consequence,
    MVELCompileable,
        Externalizable {
    private static final long   serialVersionUID = 510l;

    private MVELCompilationUnit unit;
    private String              id;

    private String              consequenceName;

    private MvelEvaluator<Void> evaluator;

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

    public void compile( MVELDialectRuntimeData runtimeData) {
        evaluator = createMvelEvaluator( unit.getCompiledExpression( runtimeData ) );
    }

    public void compile( MVELDialectRuntimeData runtimeData, RuleImpl rule) {
        evaluator = createMvelEvaluator( unit.getCompiledExpression( runtimeData, rule.toRuleNameAndPathString() ) );
    }

    public void evaluate(final KnowledgeHelper knowledgeHelper,
                         final WorkingMemory workingMemory) throws Exception {

        VariableResolverFactory factory = unit.getFactory(knowledgeHelper, ((AgendaItem) knowledgeHelper.getMatch()).getTerminalNode().getRequiredDeclarations(),
                knowledgeHelper.getRule(), knowledgeHelper.getTuple(), null, (InternalWorkingMemory) workingMemory, workingMemory.getGlobalResolver());

        // do we have any functions for this namespace?
        InternalKnowledgePackage pkg = workingMemory.getKnowledgeBase().getPackage("MAIN");
        if (pkg != null) {
            MVELDialectRuntimeData data = (MVELDialectRuntimeData) pkg.getDialectRuntimeRegistry().getDialectData(this.id);
            factory.setNextFactory(data.getFunctionFactory());
        }

        evaluator.evaluate(knowledgeHelper, factory);
    }

    public Serializable getCompExpr() {
        return evaluator != null ? evaluator.getExpr() : null;
    }

    public String getName() {
        return consequenceName;
    }

}
