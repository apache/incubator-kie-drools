/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 *
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.modelcompiler.constraints;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.List;

import org.drools.core.RuleBaseConfiguration;
import org.drools.core.base.field.ObjectFieldImpl;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.reteoo.PropertySpecificUtil;
import org.drools.core.rule.ContextEntry;
import org.drools.core.rule.Declaration;
import org.drools.core.spi.FieldValue;
import org.drools.core.spi.InternalReadAccessor;
import org.drools.core.spi.PatternExtractor;
import org.drools.core.spi.Tuple;
import org.drools.core.time.Interval;
import org.drools.core.util.AbstractHashTable.FieldIndex;
import org.drools.core.util.bitmask.BitMask;
import org.drools.core.util.index.IndexUtil;
import org.drools.model.AlphaIndex;
import org.drools.model.BetaIndex;
import org.drools.model.Index;
import org.drools.model.functions.PredicateInformation;

import static org.drools.core.reteoo.PropertySpecificUtil.getEmptyPropertyReactiveMask;
import static org.drools.modelcompiler.util.EvaluationUtil.adaptBitMask;

public class LambdaConstraint extends AbstractConstraint {

    private final ConstraintEvaluator evaluator;
    private PredicateInformation predicateInformation;

    private FieldValue field;
    private InternalReadAccessor readAccessor;
    private Declaration indexingDeclaration;

    public LambdaConstraint(ConstraintEvaluator evaluator,
                            PredicateInformation predicateInformation) {
        this.evaluator = evaluator;
        this.predicateInformation = predicateInformation;
        initIndexes();
    }

    public LambdaConstraint(ConstraintEvaluator evaluator) {
        this(evaluator, PredicateInformation.EMPTY_PREDICATE_INFORMATION);
    }

    private void initIndexes() {
        Index index = evaluator.getIndex();
        if (index != null) {
            readAccessor = new LambdaReadAccessor( index.getIndexId(), index.getIndexedClass(), index.getLeftOperandExtractor() );
            if (index instanceof AlphaIndex) {
                field = new ObjectFieldImpl( ( ( AlphaIndex ) index).getRightValue() );
            } else if (index instanceof BetaIndex) {
                indexingDeclaration = evaluator.getRequiredDeclarations()[0];
                if ( indexingDeclaration.getExtractor() instanceof PatternExtractor ) {
                    indexingDeclaration = indexingDeclaration.clone();
                    org.drools.model.Index.ConstraintType constraintType = index.getConstraintType();
                    Class<?> accessorFieldType;
                    if (constraintType.isComparison() && ((BetaIndex) index).getRightReturnType() != null) {
                        accessorFieldType = ((BetaIndex) index).getRightReturnType();
                    } else {
                        accessorFieldType = index.getIndexedClass();
                    }
                    indexingDeclaration.setReadAccessor( new LambdaReadAccessor( index.getIndexId(), accessorFieldType, (( BetaIndex ) index).getRightOperandExtractor() ) );
                }
            }
        }
    }

    @Override
    public String toString() {
        return evaluator.toString();
    }

    @Override
    public Declaration[] getRequiredDeclarations() {
        return evaluator.getRequiredDeclarations();
    }

    @Override
    public void replaceDeclaration(Declaration oldDecl, Declaration newDecl) {
        evaluator.replaceDeclaration( oldDecl, newDecl );
        if (indexingDeclaration == oldDecl) {
            this.indexingDeclaration = newDecl;
        } else if (indexingDeclaration != null && indexingDeclaration.getIdentifier().equals(oldDecl.getIdentifier()) && indexingDeclaration.getPattern() == oldDecl.getPattern()) {
            // indexingDeclaration was cloned from oldDecl
            Declaration newIndexingDeclaration = newDecl.clone();
            newIndexingDeclaration.setReadAccessor(indexingDeclaration.getExtractor());
            indexingDeclaration = newIndexingDeclaration;
        }
    }

    @Override
    public BitMask getListenedPropertyMask( Class modifiedClass, List<String> settableProperties ) {
        BitMask mask = adaptBitMask( evaluator.getReactivityBitMask() );
        if (mask != null) {
            return mask;
        }

        if (evaluator.getReactiveProps().length == 0) {
            return super.getListenedPropertyMask( modifiedClass, settableProperties );
        }

        mask = getEmptyPropertyReactiveMask(settableProperties.size());
        for (String prop : evaluator.getReactiveProps()) {
            int pos = settableProperties.indexOf(prop);
            if (pos >= 0) { // Ignore not settable properties
                mask = mask.set( pos + PropertySpecificUtil.CUSTOM_BITS_OFFSET );
            }
        }
        return mask;
    }

    @Override
    public LambdaConstraint clone() {
        LambdaConstraint clone = new LambdaConstraint( evaluator.clone(),
                                                       this.predicateInformation );
        clone.field = this.field;
        clone.readAccessor = this.readAccessor;
        return clone;
    }

    @Override
    public boolean isTemporal() {
        return evaluator.isTemporal();
    }

    @Override
    public Interval getInterval() {
        return evaluator.getInterval();
    }

    @Override
    public boolean isAllowed(InternalFactHandle handle, InternalWorkingMemory workingMemory) {
        try {
            return evaluator.evaluate(handle, workingMemory);
        } catch (RuntimeException e) {
            throw predicateInformation.betterErrorMessage(e);
        }
    }

    @Override
    public boolean isAllowedCachedLeft(ContextEntry context, InternalFactHandle handle) {
        LambdaContextEntry lambdaContext = ((LambdaContextEntry) context);
        return evaluator.evaluate(handle, lambdaContext.getTuple(), lambdaContext.getWorkingMemory());
    }

    @Override
    public boolean isAllowedCachedRight(Tuple tuple, ContextEntry context) {
        LambdaContextEntry lambdaContext = ((LambdaContextEntry) context);
        return evaluator.evaluate(lambdaContext.getHandle(), tuple, lambdaContext.getWorkingMemory());
    }

    @Override
    public ContextEntry createContextEntry() {
        return new LambdaContextEntry();
    }

    @Override
    public boolean isUnification() {
        return false;
    }

    @Override
    public boolean isIndexable( short nodeType, RuleBaseConfiguration config ) {
        return getConstraintType().isIndexableForNode(nodeType, this, config);
    }

    @Override
    public IndexUtil.ConstraintType getConstraintType() {
        Index index = evaluator.getIndex();
        if (index != null) {
            switch (index.getConstraintType()) {
                case EQUAL:
                    return IndexUtil.ConstraintType.EQUAL;
                case NOT_EQUAL:
                    return IndexUtil.ConstraintType.NOT_EQUAL;
                case GREATER_THAN:
                    return IndexUtil.ConstraintType.GREATER_THAN;
                case GREATER_OR_EQUAL:
                    return IndexUtil.ConstraintType.GREATER_OR_EQUAL;
                case LESS_THAN:
                    return IndexUtil.ConstraintType.LESS_THAN;
                case LESS_OR_EQUAL:
                    return IndexUtil.ConstraintType.LESS_OR_EQUAL;
                case RANGE:
                    return IndexUtil.ConstraintType.RANGE;
            }
        }
        return IndexUtil.ConstraintType.UNKNOWN;
    }

    @Override
    public FieldValue getField() {
        return field;
    }

    @Override
    public FieldIndex getFieldIndex() {
        return new FieldIndex(readAccessor, indexingDeclaration);
    }

    @Override
    public InternalReadAccessor getFieldExtractor() {
        return readAccessor;
    }

    @Override
    public Declaration getIndexingDeclaration() {
        return indexingDeclaration;
    }

    public static class LambdaContextEntry implements ContextEntry {

        private Tuple tuple;
        private InternalFactHandle handle;

        private transient InternalWorkingMemory workingMemory;

        public void updateFromTuple(InternalWorkingMemory workingMemory, Tuple tuple) {
            this.tuple = tuple;
            this.workingMemory = workingMemory;
        }

        public void updateFromFactHandle(InternalWorkingMemory workingMemory, InternalFactHandle handle) {
            this.workingMemory = workingMemory;
            this.handle = handle;
        }

        public void resetTuple() {
            tuple = null;
        }

        public void resetFactHandle() {
            workingMemory = null;
            handle = null;
        }

        public void writeExternal(ObjectOutput out ) throws IOException {
            out.writeObject(tuple);
            out.writeObject( handle );
        }

        public void readExternal(ObjectInput in ) throws IOException, ClassNotFoundException {
            tuple = (Tuple)in.readObject();
            handle = (InternalFactHandle) in.readObject();
        }

        public Tuple getTuple() {
            return tuple;
        }

        public InternalFactHandle getHandle() {
            return handle;
        }

        public InternalWorkingMemory getWorkingMemory() {
            return workingMemory;
        }

        public ContextEntry getNext() {
            throw new UnsupportedOperationException();
        }

        public void setNext(final ContextEntry entry) {
            throw new UnsupportedOperationException();
        }
    }

    @Override
    public boolean equals(Object other) {
        return this == other || other != null && getClass() == other.getClass() && evaluator.equals( (( LambdaConstraint ) other).evaluator );
    }

    @Override
    public int hashCode() {
        return evaluator.hashCode();
    }
}
