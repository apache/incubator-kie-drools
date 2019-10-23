/*
 * Copyright 2005 JBoss Inc
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

package org.drools.modelcompiler.consequence;

import org.drools.core.WorkingMemory;
import org.drools.core.common.EventFactHandle;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.definitions.rule.impl.RuleImpl;
import org.drools.core.reteoo.RuleTerminalNode;
import org.drools.core.rule.Declaration;
import org.drools.core.spi.Consequence;
import org.drools.core.spi.KnowledgeHelper;
import org.drools.core.spi.Tuple;
import org.drools.model.Variable;

public class LambdaConsequence implements Consequence {

    private final org.drools.model.Consequence consequence;

    public LambdaConsequence( org.drools.model.Consequence consequence ) {
        this.consequence = consequence;
    }

    @Override
    public String getName() {
        return RuleImpl.DEFAULT_CONSEQUENCE_NAME;
    }

    @Override
    public void evaluate( KnowledgeHelper knowledgeHelper, WorkingMemory workingMemory ) throws Exception {
        Declaration[] declarations = ((RuleTerminalNode)knowledgeHelper.getMatch().getTuple().getTupleSink()).getRequiredDeclarations();
        Object[] facts = declarationsToFacts( knowledgeHelper, workingMemory, knowledgeHelper.getTuple(), declarations, consequence.getVariables(), consequence.isUsingDrools() );
        consequence.getBlock().execute( facts );
    }

    public static Object[] declarationsToFacts( WorkingMemory workingMemory, Tuple tuple, Declaration[] declarations, Variable[] vars ) {
        return declarationsToFacts( null, workingMemory, tuple, declarations, vars, false );
    }

    private static Object[] declarationsToFacts( KnowledgeHelper knowledgeHelper, WorkingMemory workingMemory, Tuple tuple, Declaration[] declarations, Variable[] vars, boolean useDrools ) {
        Object[] facts;

        int factsOffset = 0;
        if (useDrools) {
            factsOffset++;
            facts = new Object[vars.length + 1];
            facts[0] = new DroolsImpl(knowledgeHelper, workingMemory);
        } else {
            facts = new Object[vars.length];
        }

        int declrCounter = 0;
        for (Variable var : vars) {
            if ( var.isFact() ) {
                Declaration declaration = declarations[declrCounter++];
                InternalFactHandle fh = getOriginalFactHandle( tuple.get( declaration ) );
                if (useDrools) {
                    ( (DroolsImpl) facts[0] ).registerFactHandle( fh );
                }
                facts[factsOffset++] = declaration.getValue( (InternalWorkingMemory ) workingMemory, fh.getObject() );
            } else {
                facts[factsOffset++] = workingMemory.getGlobal( var.getName() );
            }
        }
        return facts;
    }

    private static InternalFactHandle getOriginalFactHandle(InternalFactHandle handle) {
        InternalFactHandle linkedFH = handle.isEvent() ? ((EventFactHandle )handle).getLinkedFactHandle() : null;
        return linkedFH != null ? linkedFH : handle;
    }
}
