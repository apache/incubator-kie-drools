/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.modelcompiler.consequence;

import org.drools.base.base.ValueResolver;
import org.drools.base.definitions.rule.impl.RuleImpl;
import org.drools.base.reteoo.BaseTuple;
import org.drools.base.rule.Declaration;
import org.drools.base.rule.consequence.Consequence;
import org.drools.core.common.DefaultEventHandle;
import org.drools.core.common.ReteEvaluator;
import org.drools.core.reteoo.RuleTerminalNode;
import org.drools.core.rule.consequence.KnowledgeHelper;
import org.drools.model.Variable;
import org.kie.api.runtime.rule.FactHandle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LambdaConsequence implements Consequence<KnowledgeHelper> {

    // Enable the optimization to extract from the activation tuple the arguments to be passed to this
    // consequence in linear time by traversing the tuple only once.
    private static final boolean ENABLE_LINEARIZED_ARGUMENTS_RETRIEVAL_OPTIMIZATION = true;

    private final org.drools.model.Consequence consequence;
    private final int factsNr;
    private final boolean enabledTupleOptimization;
    private Declaration[] requiredDeclarations;

    private TupleFactSupplier[] factSuppliers;
    private GlobalSupplier[] globalSuppliers;
    private Object[] facts;

    private FactHandleLookup fhLookup;

    public LambdaConsequence( org.drools.model.Consequence consequence, boolean enabledTupleOptimization) {
        this.consequence = consequence;
        this.enabledTupleOptimization = ENABLE_LINEARIZED_ARGUMENTS_RETRIEVAL_OPTIMIZATION & enabledTupleOptimization;
        this.factsNr = consequence.getVariables().length + ( consequence.isUsingDrools() ? 1 : 0 );
    }

    @Override
    public String getName() {
        return RuleImpl.DEFAULT_CONSEQUENCE_NAME;
    }

    @Override
    public void evaluate(KnowledgeHelper knowledgeHelper, ValueResolver valueResolver) throws Exception {
        if ( this.requiredDeclarations == null ) {
            Declaration[] declarations = (( RuleTerminalNode ) knowledgeHelper.getMatch().getTuple().getSink()).getRequiredDeclarations();
            if (enabledTupleOptimization) {
                this.requiredDeclarations = declarations;
            } else {
                Object[] facts = declarationsToFacts( knowledgeHelper, valueResolver, knowledgeHelper.getTuple(), declarations, consequence.getVariables(), consequence.isUsingDrools() );
                consequence.getBlock().execute( facts );
                return;
            }
        }

        // declarations is not null when first level rule is AND so it is possible to calculate them upfront
        consequence.getBlock().execute( fetchFacts( knowledgeHelper, valueResolver ) );
    }

    public static Object[] declarationsToFacts(ValueResolver reteEvaluator, BaseTuple tuple, Declaration[] declarations, Variable[] vars ) {
        return declarationsToFacts( null, reteEvaluator, tuple, declarations, vars, false );
    }

    private static Object[] declarationsToFacts( KnowledgeHelper knowledgeHelper, ValueResolver valueResolver, BaseTuple tuple, Declaration[] declarations, Variable[] vars, boolean useDrools ) {
        Object[] objects;
        FactHandleLookup fhLookup = useDrools ? new FactHandleLookup.Multi() : null;

        int index = 0;
        if ( useDrools ) {
            index++;
            objects = new Object[vars.length + 1];
            objects[0] = new DroolsImpl( knowledgeHelper, valueResolver, fhLookup );
        } else {
            objects = new Object[vars.length];
        }

        int declrCounter = 0;
        for (Variable var : vars) {
            if ( var.isFact() ) {
                Declaration declaration = declarations[declrCounter++];
                FactHandle fh = getOriginalFactHandle(tuple.get(declaration));
                if ( useDrools ) {
                    fhLookup.put( fh.getObject(), fh );
                }
                objects[index++] = declaration.getValue( valueResolver, fh );
            } else {
                objects[index++] = valueResolver.getGlobal( var.getName() );
            }
        }
        return objects;
    }

    private static FactHandle getOriginalFactHandle( FactHandle handle ) {
        if ( !handle.isEvent() ) {
            return handle;
        }
        FactHandle linkedFH = ((DefaultEventHandle) handle).getLinkedFactHandle();
        return linkedFH != null ? linkedFH : handle;
    }

    private Object[] fetchFacts( KnowledgeHelper knowledgeHelper, ValueResolver valueResolver ) {
        ReteEvaluator reteEvaluator = (ReteEvaluator) valueResolver;
        if (factSuppliers == null) {
            return initConsequence(knowledgeHelper, reteEvaluator);
        }

        Object[] facts;
        FactHandleLookup fhLookup = null;
        if (reteEvaluator.getRuleSessionConfiguration().isThreadSafe()) {
            facts = new Object[factsNr];
            if ( consequence.isUsingDrools() ) {
                fhLookup = FactHandleLookup.create( factSuppliers.length );
                facts[0] = new DroolsImpl( knowledgeHelper, reteEvaluator, fhLookup );
            }
        } else {
            facts = this.facts;
            if ( consequence.isUsingDrools() ) {
                fhLookup = this.fhLookup;
                fhLookup.clear();
                facts[0] = new DroolsImpl( knowledgeHelper, reteEvaluator, fhLookup );
            }
        }

        BaseTuple tuple = knowledgeHelper.getTuple();
        for (int j = 0; j < factSuppliers.length; j++) {
            tuple = factSuppliers[j].resolveAndStore(facts, reteEvaluator, tuple, fhLookup);
        }

        if (globalSuppliers != null) {
            for (int j = 0; j < globalSuppliers.length; j++) {
                globalSuppliers[j].resolveAndStore(facts, reteEvaluator);
            }
        }

        return facts;
    }

    private Object[] initConsequence( KnowledgeHelper knowledgeHelper, ReteEvaluator reteEvaluator) {
        Variable[] vars = consequence.getVariables();
        if (vars.length == 0) {
            return consequence.isUsingDrools() ? new Object[] { new DroolsImpl( knowledgeHelper, reteEvaluator, null ) } : new Object[0];
        }

        BaseTuple tuple = knowledgeHelper.getTuple();
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
                facts[supplierIndex] = reteEvaluator.getGlobal( var.getName() );
                globalSuppliers.add( new GlobalSupplier( supplierIndex, var.getName() ) );
            }
            supplierIndex++;
        }

        FactHandleLookup fhLookup = null;
        if ( consequence.isUsingDrools() ) {
            fhLookup = FactHandleLookup.create( factSuppliers.size() );
            facts[0] = new DroolsImpl( knowledgeHelper, reteEvaluator, fhLookup );
        }

        Collections.sort( factSuppliers );
        Collections.sort( globalSuppliers );

        BaseTuple current = tuple;
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

            tupleFactSupplier.resolveAndStore(facts, reteEvaluator, current.getFactHandle(), fhLookup);
        }

        // factSuppliers has to be last because factSuppliers is used as an initialization flag in fetchFacts(). See DROOLS-6961
        this.globalSuppliers = globalSuppliers.isEmpty() ? null : globalSuppliers.toArray( new GlobalSupplier[globalSuppliers.size()] );
        this.factSuppliers = factSuppliers.toArray( new TupleFactSupplier[factSuppliers.size()] );

        if (!reteEvaluator.getRuleSessionConfiguration().isThreadSafe()) {
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

        public void resolveAndStore(Object[] facts, ReteEvaluator reteEvaluator) {
            facts[supplierIndex] = reteEvaluator.getGlobal( globalName );
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

        public BaseTuple resolveAndStore(Object[] facts, ValueResolver reteEvaluator, BaseTuple tuple, FactHandleLookup fhLookup) {
            // traverses the tuple of as many steps as distance between the former supplier and this one
            for (int i = 0; i < offsetFromPrior; i++) {
                tuple = tuple.getParent();
            }
            resolveAndStore(facts, reteEvaluator, tuple.getFactHandle(), fhLookup);
            return tuple;
        }

        public void resolveAndStore(Object[] facts, ValueResolver reteEvaluator, FactHandle factHandle, FactHandleLookup fhLookup) {
            FactHandle fh = getOriginalFactHandle( factHandle );
            if ( useDrools ) {
                fhLookup.put( fh.getObject(), fh );
            }
            facts[supplierIndex] = declaration.getValue(reteEvaluator, fh);
        }

        @Override
        public int compareTo( TupleFactSupplier o ) {
            // Sorted from the one extracting a fact from the bottom of the tuple to the one reading from its top
            // In this way the whole tuple can be traversed only once to retrieve all facts
            return o.declarationTupleIndex - declarationTupleIndex;
        }
    }
}