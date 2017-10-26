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

import java.util.IdentityHashMap;
import java.util.Map;

import org.drools.core.WorkingMemory;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.definitions.rule.impl.RuleImpl;
import org.drools.core.reteoo.RuleTerminalNode;
import org.drools.core.rule.Declaration;
import org.drools.core.rule.TypeDeclaration;
import org.drools.core.spi.Consequence;
import org.drools.core.spi.KnowledgeHelper;
import org.drools.core.spi.Tuple;
import org.drools.model.BitMask;
import org.drools.model.Drools;
import org.drools.model.Variable;
import org.drools.model.bitmask.AllSetBitMask;
import org.drools.model.bitmask.AllSetButLastBitMask;
import org.drools.model.bitmask.EmptyBitMask;
import org.drools.model.bitmask.EmptyButLastBitMask;
import org.drools.model.bitmask.LongBitMask;
import org.drools.model.bitmask.OpenBitSet;
import org.drools.model.functions.FunctionN;
import org.drools.modelcompiler.RuleContext;

import static java.util.Arrays.asList;
import static org.drools.core.reteoo.PropertySpecificUtil.calculatePositiveMask;

public class LambdaConsequence implements Consequence {

    private final org.drools.model.Consequence consequence;
    private final RuleContext context;

    public LambdaConsequence( org.drools.model.Consequence consequence, RuleContext context ) {
        this.consequence = consequence;
        this.context = context;
    }

    @Override
    public String getName() {
        return RuleImpl.DEFAULT_CONSEQUENCE_NAME;
    }

    @Override
    public void evaluate( KnowledgeHelper knowledgeHelper, WorkingMemory workingMemory ) throws Exception {
        Tuple tuple = knowledgeHelper.getTuple();
        Declaration[] declarations = ((RuleTerminalNode)knowledgeHelper.getMatch().getTuple().getTupleSink()).getRequiredDeclarations();

        Variable[] vars = consequence.getVariables();
        Object[] facts;

        int factsOffset = 0;
        if (consequence.isUsingDrools()) {
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
                InternalFactHandle fh = tuple.get( declaration );
                if (consequence.isUsingDrools()) {
                    ( (DroolsImpl) facts[0] ).registerFactHandle( fh );
                }
                facts[factsOffset++] = declaration.getValue( (InternalWorkingMemory) workingMemory, fh.getObject() );
            } else {
                facts[factsOffset++] = workingMemory.getGlobal( var.getName() );
            }
        }

        consequence.getBlock().execute( facts );

        Object[] objs = knowledgeHelper.getTuple().toObjects();

        for ( org.drools.model.Consequence.Update update : consequence.getUpdates() ) {
            Object updatedFact = context.getBoundFact( update.getUpdatedVariable(), objs );
            // TODO the Update specs has the changed fields so use update(FactHandle newObject, long mask, Class<?> modifiedClass) instead
            knowledgeHelper.update( updatedFact );
        }

        for ( FunctionN insert : consequence.getInserts() ) {
            Object insertedFact = insert.apply( facts );
            knowledgeHelper.insert( insertedFact );
        }

        for ( Variable delete : consequence.getDeletes() ) {
            Object deletedFact = context.getBoundFact( delete, objs );
            knowledgeHelper.delete( deletedFact );
        }
    }

    public static class DroolsImpl implements Drools {
        private final KnowledgeHelper knowledgeHelper;
        private final WorkingMemory workingMemory;

        private final Map<Object, InternalFactHandle> fhLookup = new IdentityHashMap<>();

        DroolsImpl(KnowledgeHelper knowledgeHelper, WorkingMemory workingMemory) {
            this.workingMemory = workingMemory;
            this.knowledgeHelper = knowledgeHelper;
        }

        @Override
        public void insert(Object object) {
            workingMemory.insert(object);
        }

        @Override
        public void insertLogical(Object object) {
            knowledgeHelper.insertLogical(object);
        }

        @Override
        public void update(Object object, String... modifiedProperties) {
            Class modifiedClass = object.getClass();
            TypeDeclaration typeDeclaration = workingMemory.getKnowledgeBase().getOrCreateExactTypeDeclaration( modifiedClass );
            org.drools.core.util.bitmask.BitMask mask = typeDeclaration.isPropertyReactive() ?
                    calculatePositiveMask(asList(modifiedProperties), typeDeclaration.getAccessibleProperties() ) :
                    org.drools.core.util.bitmask.AllSetBitMask.get();

            knowledgeHelper.update( fhLookup.get(object), mask, modifiedClass);
        }

        @Override
        public void update(Object object, BitMask modifiedProperties ) {
            Class<?> modifiedClass = modifiedProperties.getPatternClass();
            knowledgeHelper.update( fhLookup.get(object), adaptBitMask(modifiedProperties), modifiedClass);
        }

        @Override
        public void delete(Object object) {
            workingMemory.delete( fhLookup.get(object) );
        }

        void registerFactHandle(InternalFactHandle fh) {
            fhLookup.put( fh.getObject(), fh );
        }

        @Override
        public <T> T getRuntime(Class<T> runtimeClass) {
            return (T)knowledgeHelper.getKieRuntime();
        }
    }

    private static org.drools.core.util.bitmask.BitMask adaptBitMask(BitMask mask) {
        if (mask instanceof LongBitMask) {
            return new org.drools.core.util.bitmask.LongBitMask( ( (LongBitMask) mask ).asLong() );
        }
        if (mask instanceof EmptyBitMask) {
            return org.drools.core.util.bitmask.EmptyBitMask.get();
        }
        if (mask instanceof AllSetBitMask) {
            return org.drools.core.util.bitmask.AllSetBitMask.get();
        }
        if (mask instanceof AllSetButLastBitMask) {
            return org.drools.core.util.bitmask.AllSetButLastBitMask.get();
        }
        if (mask instanceof EmptyButLastBitMask) {
            return org.drools.core.util.bitmask.EmptyButLastBitMask.get();
        }
        if (mask instanceof OpenBitSet) {
            return new org.drools.core.util.bitmask.OpenBitSet( ( (OpenBitSet) mask ).getBits(), ( (OpenBitSet) mask ).getNumWords() );
        }
        throw new IllegalArgumentException( "Unknown bitmask: " + mask );
    }
}
