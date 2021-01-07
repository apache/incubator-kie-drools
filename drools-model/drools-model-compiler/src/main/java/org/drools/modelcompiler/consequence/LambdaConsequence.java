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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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

    // Enable the optimization to extract from the activation tuple the arguments to be passed to this
    // consequence in linear time by traversing the tuple only once.
    private static final boolean ENABLE_LINEARIZED_ARGUMENTS_RETRIVAL_OPTIMIZATION = true;

    private final org.drools.model.Consequence consequence;
    private final Declaration[] declarations;

    private FactSupplier[] factSuppliers;

    public LambdaConsequence( org.drools.model.Consequence consequence, Declaration[] declarations ) {
        this.consequence = consequence;
        this.declarations = ENABLE_LINEARIZED_ARGUMENTS_RETRIVAL_OPTIMIZATION ? declarations : null;
    }

    @Override
    public String getName() {
        return RuleImpl.DEFAULT_CONSEQUENCE_NAME;
    }

    @Override
    public void evaluate( KnowledgeHelper knowledgeHelper, WorkingMemory workingMemory ) throws Exception {
        Object[] facts;
        if ( this.declarations == null ) {
            Declaration[] declarations = (( RuleTerminalNode ) knowledgeHelper.getMatch().getTuple().getTupleSink()).getRequiredDeclarations();
            facts = declarationsToFacts( knowledgeHelper, ( InternalWorkingMemory ) workingMemory, knowledgeHelper.getTuple(), declarations, consequence.getVariables(), consequence.isUsingDrools() );
        } else {
            // declarations is not null when first level rule is AND so it is possible to calculate them upfront
            facts = fetchFacts( knowledgeHelper, ( InternalWorkingMemory ) workingMemory );
        }
        consequence.getBlock().execute( facts );
    }

    public static Object[] declarationsToFacts( WorkingMemory workingMemory, Tuple tuple, Declaration[] declarations, Variable[] vars ) {
        return declarationsToFacts( null, ( InternalWorkingMemory ) workingMemory, tuple, declarations, vars, false );
    }

    private static Object[] declarationsToFacts( KnowledgeHelper knowledgeHelper, InternalWorkingMemory workingMemory, Tuple tuple, Declaration[] declarations, Variable[] vars, boolean useDrools ) {
        Object[] facts;

        int factsOffset = 0;
        if ( useDrools ) {
            factsOffset++;
            facts = new Object[vars.length + 1];
            facts[0] = new DroolsImpl( knowledgeHelper, workingMemory );
        } else {
            facts = new Object[vars.length];
        }

        int declrCounter = 0;
        for (Variable var : vars) {
            if ( var.isFact() ) {
                Declaration declaration = declarations[declrCounter++];
                InternalFactHandle fh = getOriginalFactHandle( tuple.get( declaration ) );
                if ( useDrools ) {
                    (( DroolsImpl ) facts[0]).registerFactHandle( fh );
                }
                facts[factsOffset++] = declaration.getValue( workingMemory, fh );
            } else {
                facts[factsOffset++] = workingMemory.getGlobal( var.getName() );
            }
        }
        return facts;
    }

    private static InternalFactHandle getOriginalFactHandle( InternalFactHandle handle ) {
        if ( !handle.isEvent() ) {
            return handle;
        }
        InternalFactHandle linkedFH = (( EventFactHandle ) handle).getLinkedFactHandle();
        return linkedFH != null ? linkedFH : handle;
    }

    private Object[] fetchFacts( KnowledgeHelper knowledgeHelper, InternalWorkingMemory workingMemory ) {
        if (factSuppliers == null) {
            return initConsequence(knowledgeHelper, workingMemory);
        }

        Tuple tuple = knowledgeHelper.getTuple();
        Object[] facts = new Object[factSuppliers.length];
        for (int i = 0; i < facts.length; i++) {
            tuple = factSuppliers[i].get( facts, knowledgeHelper, workingMemory, tuple );
        }
        return facts;
    }

    private Object[] initConsequence( KnowledgeHelper knowledgeHelper, InternalWorkingMemory workingMemory) {
        Variable[] vars = consequence.getVariables();
        if (vars.length == 0) {
            return consequence.isUsingDrools() ? new Object[] { new DroolsImpl( knowledgeHelper, workingMemory ) } : new Object[0];
        }

        Tuple tuple = knowledgeHelper.getTuple();
        List<FactSupplier> factSuppliers = new ArrayList<>();

        Object[] facts;
        int factsOffset = 0;
        if ( consequence.isUsingDrools() ) {
            factsOffset++;
            factSuppliers.add( DroolsImplSupplier.INSTANCE );
            facts = new Object[vars.length + 1];
            facts[0] = new DroolsImpl( knowledgeHelper, workingMemory );
        } else {
            facts = new Object[vars.length];
        }

        int declrCounter = 0;
        for (Variable var : vars) {
            if ( var.isFact() ) {
                factSuppliers.add( new TupleFactSupplier(factsOffset, declarations[declrCounter++], consequence.isUsingDrools()) );
            } else {
                facts[factsOffset] = workingMemory.getGlobal( var.getName() );
                factSuppliers.add( new GlobalSupplier(factsOffset, var.getName()) );
            }
            factsOffset++;
        }

        // At this point the FactSuppliers (each of them supplying a single argument to be passed to the consequence)
        // are sorted as it follows:
        // - first (if necessary) the DroolsImplSupplier, used to eventually add the drools object to the list of arguments
        // - second all the TupleFactSuppliers, used to retrieve the facts from the activation tuple
        // - third the GlobalSupplier, used to retrieve the consequence's arguments from the globals
        // Internally the TupleFactSuppliers are sorted from the one extracting a fact from the bottom of the tuple to the
        // one reading from its top. In this way the whole tuple can be traversed only once to retrive all facts.
        Collections.sort( factSuppliers );

        int lastOffset = tuple.getIndex();
        Tuple current = tuple;
        boolean first = true;
        for (int i = consequence.isUsingDrools() ? 1 : 0; i < factSuppliers.size() && factSuppliers.get(i) instanceof TupleFactSupplier; i++) {
            TupleFactSupplier tupleFactSupplier = (( TupleFactSupplier ) factSuppliers.get( i ));
            tupleFactSupplier.formerSupplierOffset = lastOffset - tupleFactSupplier.declarationOffset;

            for (int j = 0; j < tupleFactSupplier.formerSupplierOffset; j++) {
                if (current.getFactHandle() == null) {
                    tupleFactSupplier.formerSupplierOffset++;
                }
                current = current.getParent();
            }

            while (current != null && current.getFactHandle() == null) {
                tupleFactSupplier.formerSupplierOffset++;
                current = current.getParent();
            }

            tupleFactSupplier.setFirst( first );
            first = false;
            lastOffset = tupleFactSupplier.declarationOffset;

            tupleFactSupplier.fetchFact( facts, workingMemory, current );
        }

        this.factSuppliers = factSuppliers.toArray( new FactSupplier[factSuppliers.size()] );
        return facts;
    }

    private interface FactSupplier extends Comparable<FactSupplier> {
        Tuple get( Object[] facts, KnowledgeHelper knowledgeHelper, InternalWorkingMemory workingMemory, Tuple tuple );
    }

    private static class DroolsImplSupplier implements FactSupplier {
        static final DroolsImplSupplier INSTANCE = new DroolsImplSupplier();

        @Override
        public Tuple get( Object[] facts, KnowledgeHelper knowledgeHelper, InternalWorkingMemory workingMemory, Tuple tuple ) {
            facts[0] = new DroolsImpl( knowledgeHelper, workingMemory );
            return tuple;
        }

        @Override
        public int compareTo( FactSupplier o ) {
            return -1;
        }
    }

    private static class GlobalSupplier implements FactSupplier {
        private final int offset;
        private final String globalName;

        private GlobalSupplier( int offset, String globalName ) {
            this.offset = offset;
            this.globalName = globalName;
        }

        @Override
        public Tuple get( Object[] facts, KnowledgeHelper knowledgeHelper, InternalWorkingMemory workingMemory, Tuple tuple ) {
            facts[offset] = workingMemory.getGlobal( globalName );
            return tuple;
        }

        @Override
        public int compareTo( FactSupplier o ) {
            return o instanceof GlobalSupplier ? globalName.compareTo( (( GlobalSupplier ) o).globalName ) : 1;

        }
    }

    private static class TupleFactSupplier implements FactSupplier {
        private final int factsOffset;
        private final Declaration declaration;
        private final int declarationOffset;

        private boolean useDrools;
        private int formerSupplierOffset;

        private TupleFactSupplier( int offset, Declaration declaration, boolean useDrools ) {
            this.factsOffset = offset;
            this.declaration = declaration;
            this.declarationOffset = declaration.getOffset();
            this.useDrools = useDrools;
        }

        private void setFirst(boolean first) {
            if (!first) {
                // if this is not the first fact supplier and it's reading a value from the same fact handle of the former
                // supplier (formerSupplierOffset==0) it is not necessary to register the same fact handle twice on the drools object
                useDrools &= formerSupplierOffset > 0;
            }
        }

        @Override
        public Tuple get( Object[] facts, KnowledgeHelper knowledgeHelper, InternalWorkingMemory workingMemory, Tuple tuple ) {
            // traverses the tuple of as many steps as distance between the former supplier and this one
            for (int i = 0; i < formerSupplierOffset; i++) {
                tuple = tuple.getParent();
            }
            fetchFact( facts, workingMemory, tuple );
            return tuple;
        }

        public void fetchFact( Object[] facts, InternalWorkingMemory workingMemory, Tuple tuple ) {
            InternalFactHandle fh = getOriginalFactHandle( tuple.getFactHandle() );
            if ( useDrools ) {
                (( DroolsImpl ) facts[0]).registerFactHandle( fh );
            }
            facts[factsOffset] = declaration.getValue( workingMemory, fh );
        }

        @Override
        public int compareTo( FactSupplier o ) {
            if (o instanceof DroolsImplSupplier) {
                return 1;
            }
            if (o instanceof GlobalSupplier) {
                return -1;
            }
            return (( TupleFactSupplier ) o).declarationOffset - declarationOffset;

        }
    }
}