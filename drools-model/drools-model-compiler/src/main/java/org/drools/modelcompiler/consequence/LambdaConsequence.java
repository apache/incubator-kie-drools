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

    private TupleFactSupplier[] factSuppliers;
    private GlobalSupplier[] globalSuppliers;
    private Object[] facts;

    private FactHandleLookup fhLookup;

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
        FactHandleLookup fhLookup = useDrools ? new FactHandleLookup.Multi() : null;

        int factsOffset = 0;
        if ( useDrools ) {
            factsOffset++;
            facts = new Object[vars.length + 1];
            facts[0] = new DroolsImpl( knowledgeHelper, workingMemory, fhLookup );
        } else {
            facts = new Object[vars.length];
        }

        int declrCounter = 0;
        for (Variable var : vars) {
            if ( var.isFact() ) {
                Declaration declaration = declarations[declrCounter++];
                InternalFactHandle fh = getOriginalFactHandle( tuple.get( declaration ) );
                if ( useDrools ) {
                    fhLookup.put( fh.getObject(), fh );
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

        Object[] facts;
        FactHandleLookup fhLookup = null;
        if (workingMemory.getSessionConfiguration().isThreadSafe()) {
            if ( consequence.isUsingDrools() ) {
                facts = new Object[consequence.getVariables().length + 1];
                fhLookup = FactHandleLookup.create( factSuppliers.length );
                facts[0] = new DroolsImpl( knowledgeHelper, workingMemory, fhLookup );
            } else {
                facts = new Object[consequence.getVariables().length];
            }
        } else {
            facts = this.facts;
            if ( consequence.isUsingDrools() ) {
                fhLookup = this.fhLookup;
                fhLookup.clear();
                facts[0] = new DroolsImpl( knowledgeHelper, workingMemory, fhLookup );
            }
        }

        Tuple tuple = knowledgeHelper.getTuple();
        for (int j = 0; j < factSuppliers.length; j++) {
            tuple = factSuppliers[j].get( facts, workingMemory, tuple, fhLookup );
        }

        if (globalSuppliers != null) {
            for (int j = 0; j < globalSuppliers.length; j++) {
                globalSuppliers[j].get( facts, workingMemory );
            }
        }

        return facts;
    }

    private Object[] initConsequence( KnowledgeHelper knowledgeHelper, InternalWorkingMemory workingMemory) {
        Variable[] vars = consequence.getVariables();
        if (vars.length == 0) {
            return consequence.isUsingDrools() ? new Object[] { new DroolsImpl( knowledgeHelper, workingMemory, null ) } : new Object[0];
        }

        Tuple tuple = knowledgeHelper.getTuple();
        List<TupleFactSupplier> factSuppliers = new ArrayList<>();
        List<GlobalSupplier> globalSuppliers = new ArrayList<>();

        Object[] facts;
        int factsOffset = 0;
        if ( consequence.isUsingDrools() ) {
            facts = new Object[vars.length + 1];
            factsOffset++;
        } else {
            facts = new Object[vars.length];
            fhLookup = null;
        }

        int declrCounter = 0;
        for (Variable var : vars) {
            if ( var.isFact() ) {
                factSuppliers.add( new TupleFactSupplier( factsOffset, declarations[declrCounter++], consequence.isUsingDrools() ) );
            } else {
                facts[factsOffset] = workingMemory.getGlobal( var.getName() );
                globalSuppliers.add( new GlobalSupplier( factsOffset, var.getName() ) );
            }
            factsOffset++;
        }

        FactHandleLookup fhLookup = null;
        if ( consequence.isUsingDrools() ) {
            fhLookup = FactHandleLookup.create( factSuppliers.size() );
            facts[0] = new DroolsImpl( knowledgeHelper, workingMemory, fhLookup );
        }

        Collections.sort( factSuppliers );
        Collections.sort( globalSuppliers );

        int lastOffset = tuple.getIndex();
        Tuple current = tuple;
        boolean first = true;
        for (TupleFactSupplier tupleFactSupplier : factSuppliers) {
            tupleFactSupplier.formerSupplierOffset = lastOffset - tupleFactSupplier.declarationOffset;

            for (int j = 0; j < tupleFactSupplier.formerSupplierOffset; j++) {
                if ( current.getFactHandle() == null ) {
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

            tupleFactSupplier.fetchFact( facts, workingMemory, current, fhLookup );
        }

        this.factSuppliers = factSuppliers.toArray( new TupleFactSupplier[factSuppliers.size()] );
        this.globalSuppliers = globalSuppliers.isEmpty() ? null : globalSuppliers.toArray( new GlobalSupplier[globalSuppliers.size()] );

        if (!workingMemory.getSessionConfiguration().isThreadSafe()) {
            this.facts = facts;
            this.fhLookup = fhLookup;
        }
        return facts;
    }

    private static class GlobalSupplier implements Comparable<GlobalSupplier> {
        private final int offset;
        private final String globalName;

        private GlobalSupplier( int offset, String globalName ) {
            this.offset = offset;
            this.globalName = globalName;
        }

        public void get( Object[] facts, InternalWorkingMemory workingMemory ) {
            facts[offset] = workingMemory.getGlobal( globalName );
        }

        public int compareTo( GlobalSupplier o ) {
            return globalName.compareTo( o.globalName );

        }
    }

    private static class TupleFactSupplier implements Comparable<TupleFactSupplier> {
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

        public Tuple get( Object[] facts, InternalWorkingMemory workingMemory, Tuple tuple,FactHandleLookup fhLookup ) {
            // traverses the tuple of as many steps as distance between the former supplier and this one
            for (int i = 0; i < formerSupplierOffset; i++) {
                tuple = tuple.getParent();
            }
            fetchFact( facts, workingMemory, tuple, fhLookup );
            return tuple;
        }

        public void fetchFact( Object[] facts, InternalWorkingMemory workingMemory, Tuple tuple, FactHandleLookup fhLookup ) {
            InternalFactHandle fh = getOriginalFactHandle( tuple.getFactHandle() );
            if ( useDrools ) {
                fhLookup.put( fh.getObject(), fh );
            }
            facts[factsOffset] = declaration.getValue( workingMemory, fh );
        }

        @Override
        public int compareTo( TupleFactSupplier o ) {
            // Sorted from the one extracting a fact from the bottom of the tuple to the one reading from its top
            // In this way the whole tuple can be traversed only once to retrive all facts
            return o.declarationOffset - declarationOffset;

        }
    }
}