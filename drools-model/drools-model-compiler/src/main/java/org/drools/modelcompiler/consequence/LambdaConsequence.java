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
    private static final boolean ENABLE_LINEARIZED_ARGUMENTS_RETRIEVAL_OPTIMIZATION = true;

    private final org.drools.model.Consequence consequence;
    private final boolean        enabledTupleOptimization;
    private Declaration[]        requiredDeclarations;

    private TupleFactSupplier[] factSuppliers;
    private GlobalSupplier[]    globalSuppliers;
    private Object[]            facts;

    private FactHandleLookup    fhLookup;

    public LambdaConsequence( org.drools.model.Consequence consequence, boolean enabledTupleOptimization) {
        this.consequence = consequence;
        this.enabledTupleOptimization = ENABLE_LINEARIZED_ARGUMENTS_RETRIEVAL_OPTIMIZATION & enabledTupleOptimization;
    }

    @Override
    public String getName() {
        return RuleImpl.DEFAULT_CONSEQUENCE_NAME;
    }

    @Override
    public void evaluate( KnowledgeHelper knowledgeHelper, WorkingMemory workingMemory ) throws Exception {
        if ( this.requiredDeclarations == null ) {
            Declaration[] declarations = (( RuleTerminalNode ) knowledgeHelper.getMatch().getTuple().getTupleSink()).getRequiredDeclarations();
            if (enabledTupleOptimization) {
                this.requiredDeclarations = declarations;
            } else {
                Object[] facts = declarationsToFacts( knowledgeHelper, ( InternalWorkingMemory ) workingMemory, knowledgeHelper.getTuple(), declarations, consequence.getVariables(), consequence.isUsingDrools() );
                consequence.getBlock().execute( facts );
                return;
            }
        }

        // declarations is not null when first level rule is AND so it is possible to calculate them upfront
        consequence.getBlock().execute( fetchFacts( knowledgeHelper, ( InternalWorkingMemory ) workingMemory ) );
    }

    public static Object[] declarationsToFacts( WorkingMemory workingMemory, Tuple tuple, Declaration[] declarations, Variable[] vars ) {
        return declarationsToFacts( null, ( InternalWorkingMemory ) workingMemory, tuple, declarations, vars, false );
    }

    private static Object[] declarationsToFacts( KnowledgeHelper knowledgeHelper, InternalWorkingMemory workingMemory, Tuple tuple, Declaration[] declarations, Variable[] vars, boolean useDrools ) {
        Object[] objects;
        FactHandleLookup fhLookup = useDrools ? new FactHandleLookup.Multi() : null;

        int index = 0;
        if ( useDrools ) {
            index++;
            objects = new Object[vars.length + 1];
            objects[0] = new DroolsImpl( knowledgeHelper, workingMemory, fhLookup );
        } else {
            objects = new Object[vars.length];
        }

        int declrCounter = 0;
        for (Variable var : vars) {
            if ( var.isFact() ) {
                Declaration declaration = declarations[declrCounter++];
                InternalFactHandle fh = getOriginalFactHandle( tuple.get( declaration ) );
                if ( useDrools ) {
                    fhLookup.put( fh.getObject(), fh );
                }
                objects[index++] = declaration.getValue( workingMemory, fh );
            } else {
                objects[index++] = workingMemory.getGlobal( var.getName() );
            }
        }
        return objects;
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
            tuple = factSuppliers[j].resolveAndStore(facts, workingMemory, tuple, fhLookup);
        }

        if (globalSuppliers != null) {
            for (int j = 0; j < globalSuppliers.length; j++) {
                globalSuppliers[j].resolveAndStore(facts, workingMemory);
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
        int supplierIndex = 0;
        if ( consequence.isUsingDrools() ) {
            facts = new Object[vars.length + 1];
            supplierIndex++;
        } else {
            facts = new Object[vars.length];
            fhLookup = null;
        }

        int declrCounter = 0;
        for (Variable var : vars) {
            if ( var.isFact() ) {
                factSuppliers.add( new TupleFactSupplier( supplierIndex, requiredDeclarations[declrCounter++], consequence.isUsingDrools() ) );
            } else {
                facts[supplierIndex] = workingMemory.getGlobal( var.getName() );
                globalSuppliers.add( new GlobalSupplier( supplierIndex, var.getName() ) );
            }
            supplierIndex++;
        }

        FactHandleLookup fhLookup = null;
        if ( consequence.isUsingDrools() ) {
            fhLookup = FactHandleLookup.create( factSuppliers.size() );
            facts[0] = new DroolsImpl( knowledgeHelper, workingMemory, fhLookup );
        }

        Collections.sort( factSuppliers );
        Collections.sort( globalSuppliers );

        Tuple current = tuple;
        boolean first = true;
        for (TupleFactSupplier tupleFactSupplier : factSuppliers) {
            int targetTupleIndex = tupleFactSupplier.declarationTupleIndex;

            tupleFactSupplier.offsetFromPrior = 0;
            while (current.getIndex() != targetTupleIndex) {
                tupleFactSupplier.offsetFromPrior++;
                current = current.getParent();
            }

            tupleFactSupplier.setFirst( first );
            first = false;

            tupleFactSupplier.resolveAndStore(facts, workingMemory, current.getFactHandle(), fhLookup);
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
        private final int supplierIndex;
        private final String globalName;

        private GlobalSupplier( int supplierIndex, String globalName ) {
            this.supplierIndex = supplierIndex;
            this.globalName = globalName;
        }

        public void resolveAndStore(Object[] facts, InternalWorkingMemory workingMemory) {
            facts[supplierIndex] = workingMemory.getGlobal( globalName );
        }

        public int compareTo( GlobalSupplier o ) {
            return globalName.compareTo( o.globalName );
        }
    }

    private static class TupleFactSupplier implements Comparable<TupleFactSupplier> {
        private final int         supplierIndex;
        private final Declaration declaration;
        private final int         declarationTupleIndex;

        private boolean     useDrools;
        private int     offsetFromPrior;

        private TupleFactSupplier( int supplierIndex, Declaration declaration, boolean useDrools ) {
            this.supplierIndex = supplierIndex;
            this.declaration = declaration;
            this.declarationTupleIndex = declaration.getTupleIndex();
            this.useDrools = useDrools;
        }

        private void setFirst(boolean first) {
            if (!first) {
                // if this is not the first fact supplier and it's reading a value from the same fact handle of the former
                // supplier (formerSupplierOffset==0) it is not necessary to register the same fact handle twice on the drools object
                useDrools &= offsetFromPrior > 0;
            }
        }

        public Tuple resolveAndStore(Object[] facts, InternalWorkingMemory workingMemory, Tuple tuple, FactHandleLookup fhLookup) {
            // traverses the tuple of as many steps as distance between the former supplier and this one
            for (int i = 0; i < offsetFromPrior; i++) {
                tuple = tuple.getParent();
            }
            resolveAndStore(facts, workingMemory, tuple.getFactHandle(), fhLookup);
            return tuple;
        }

        public void resolveAndStore(Object[] facts, InternalWorkingMemory workingMemory, InternalFactHandle factHandle, FactHandleLookup fhLookup) {
            InternalFactHandle fh = getOriginalFactHandle( factHandle );
            if ( useDrools ) {
                fhLookup.put( fh.getObject(), fh );
            }
            facts[supplierIndex] = declaration.getValue(workingMemory, fh);
        }

        @Override
        public int compareTo( TupleFactSupplier o ) {
            // Sorted from the one extracting a fact from the bottom of the tuple to the one reading from its top
            // In this way the whole tuple can be traversed only once to retrieve all facts
            return o.declarationTupleIndex - declarationTupleIndex;
        }
    }
}