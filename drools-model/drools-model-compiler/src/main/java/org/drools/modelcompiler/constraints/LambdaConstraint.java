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
import org.drools.core.base.ValueType;
import org.drools.core.base.field.ObjectFieldImpl;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.reteoo.PropertySpecificUtil;
import org.drools.core.rule.ContextEntry;
import org.drools.core.rule.Declaration;
import org.drools.core.spi.FieldValue;
import org.drools.core.spi.InternalReadAccessor;
import org.drools.core.spi.Tuple;
import org.drools.core.spi.TupleValueExtractor;
import org.drools.core.time.Interval;
import org.drools.core.util.AbstractHashTable.FieldIndex;
import org.drools.core.util.bitmask.BitMask;
import org.drools.core.util.index.IndexUtil;
import org.drools.model.AlphaIndex;
import org.drools.model.BetaIndex;
import org.drools.model.BetaIndex2;
import org.drools.model.BetaIndex3;
import org.drools.model.BetaIndex4;
import org.drools.model.BetaIndexN;
import org.drools.model.Index;
import org.drools.model.functions.Function1;
import org.drools.model.functions.Function2;
import org.drools.model.functions.Function3;
import org.drools.model.functions.Function4;
import org.drools.model.functions.PredicateInformation;

import static org.drools.core.base.ValueType.determineValueType;
import static org.drools.core.reteoo.PropertySpecificUtil.getEmptyPropertyReactiveMask;
import static org.drools.modelcompiler.util.EvaluationUtil.adaptBitMask;

public class LambdaConstraint extends AbstractConstraint {

    private final ConstraintEvaluator evaluator;
    private final PredicateInformation predicateInformation;

    private FieldValue field;
    private InternalReadAccessor readAccessor;
    private AbstractIndexValueExtractor indexExtractor;

    public LambdaConstraint(ConstraintEvaluator evaluator,
                            PredicateInformation predicateInformation) {
        this.evaluator = evaluator;
        this.predicateInformation = predicateInformation;
        initIndexes();
    }

    public LambdaConstraint(ConstraintEvaluator evaluator) {
        this(evaluator, PredicateInformation.EMPTY_PREDICATE_INFORMATION);
    }

    public ConstraintEvaluator getEvaluator() {
        return evaluator;
    }

    private void initIndexes() {
        Index index = evaluator.getIndex();
        if (index != null) {
            this.readAccessor = new LambdaReadAccessor( index.getIndexId(), index.getIndexedClass(), index.getLeftOperandExtractor() );
            switch (index.getIndexType()) {
                case ALPHA:
                    this.field = new ObjectFieldImpl( ( ( AlphaIndex ) index).getRightValue() );
                    break;
                case BETA:
                    this.indexExtractor = initBetaIndex( ( BetaIndexN ) index );
                    break;
            }
        }
    }

    private AbstractIndexValueExtractor initBetaIndex( BetaIndexN index) {
        switch (index.getArity()) {
            case 1:
                BetaIndex index1 = (( BetaIndex ) index);
                return new IndexValueExtractor1(evaluator.getRequiredDeclarations()[0], index1.getRightOperandExtractor(), index1.getRightReturnType());
            case 2:
                BetaIndex2 index2 = (( BetaIndex2 ) index);
                return new IndexValueExtractor2(evaluator.getRequiredDeclarations()[0], evaluator.getRequiredDeclarations()[1], index2.getRightOperandExtractor(), index2.getRightReturnType());
            case 3:
                BetaIndex3 index3 = (( BetaIndex3 ) index);
                return new IndexValueExtractor3(evaluator.getRequiredDeclarations()[0], evaluator.getRequiredDeclarations()[1], evaluator.getRequiredDeclarations()[2], index3.getRightOperandExtractor(), index3.getRightReturnType());
            case 4:
                BetaIndex4 index4 = (( BetaIndex4 ) index);
                return new IndexValueExtractor4(evaluator.getRequiredDeclarations()[0], evaluator.getRequiredDeclarations()[1], evaluator.getRequiredDeclarations()[2], evaluator.getRequiredDeclarations()[3], index4.getRightOperandExtractor(), index4.getRightReturnType());
        }
        throw new UnsupportedOperationException( "Unsupported arity " + index.getArity() + " for beta index" );
    }

    @Override
    public String toString() {
        return "[" + evaluator.toString() + ", " + predicateInformation.getStringConstraint() + "]";
    }

    @Override
    public Declaration[] getRequiredDeclarations() {
        return evaluator.getRequiredDeclarations();
    }

    @Override
    public void replaceDeclaration(Declaration oldDecl, Declaration newDecl) {
        evaluator.replaceDeclaration( oldDecl, newDecl );
        if ( indexExtractor != null ) {
            indexExtractor.replaceDeclaration( oldDecl, newDecl );
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
        return new FieldIndex(readAccessor, indexExtractor );
    }

    @Override
    public InternalReadAccessor getFieldExtractor() {
        return readAccessor;
    }

    @Override
    public TupleValueExtractor getIndexExtractor() {
        return indexExtractor;
    }

    @Override
    public boolean equals(Object other) {
        return this == other || other != null && getClass() == other.getClass() && evaluator.equals( (( LambdaConstraint ) other).evaluator );
    }

    @Override
    public int hashCode() {
        return evaluator.hashCode();
    }

    public PredicateInformation getPredicateInformation() {
        return predicateInformation;
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

    public static abstract class AbstractIndexValueExtractor implements TupleValueExtractor {
        protected Declaration d1;
        protected final ValueType valueType;

        protected AbstractIndexValueExtractor( Declaration d1, Class<?> clazz ) {
            this(d1, determineValueType(clazz));
        }

        protected AbstractIndexValueExtractor( Declaration d1, ValueType valueType ) {
            this.d1 = d1;
            this.valueType = valueType;
        }

        @Override
        public ValueType getValueType() {
            return valueType;
        }

        public abstract TupleValueExtractor clone();

        public abstract void replaceDeclaration(Declaration oldDecl, Declaration newDecl);

        protected Declaration replaceDeclaration(Declaration oldDecl, Declaration newDecl, Declaration indexingDeclaration) {
            if (indexingDeclaration.getIdentifier().equals(oldDecl.getIdentifier()) && indexingDeclaration.getPattern() == oldDecl.getPattern()) {
                // indexingDeclaration was cloned from oldDecl
                Declaration newIndexingDeclaration = newDecl.clone();
                newIndexingDeclaration.setReadAccessor( indexingDeclaration.getExtractor() );
                return newIndexingDeclaration;
            }
            return indexingDeclaration;
        }
    }

    public static class IndexValueExtractor1 extends AbstractIndexValueExtractor {

        private final Function1 extractor;

        public IndexValueExtractor1( Declaration d1, Function1 extractor, Class<?> clazz ) {
            super(d1, clazz);
            this.extractor = extractor;
        }

        public IndexValueExtractor1( Declaration d1, Function1 extractor, ValueType valueType ) {
            super(d1, valueType);
            this.extractor = extractor;
        }

        @Override
        public Object getValue( InternalWorkingMemory workingMemory, Tuple tuple ) {
            return extractor.apply( d1.getValue( workingMemory, tuple ) );
        }

        @Override
        public TupleValueExtractor clone() {
            return new IndexValueExtractor1( d1.clone(), extractor, valueType );
        }

        @Override
        public void replaceDeclaration(Declaration oldDecl, Declaration newDecl) {
            d1 = replaceDeclaration( oldDecl, newDecl, d1 );
        }
    }

    public static class IndexValueExtractor2 extends AbstractIndexValueExtractor {

        private Declaration d2;
        private final Function2 extractor;

        public IndexValueExtractor2( Declaration d1, Declaration d2, Function2 extractor, Class<?> clazz ) {
            super(d1, clazz);
            this.d2 = d2;
            this.extractor = extractor;
        }

        public IndexValueExtractor2( Declaration d1, Declaration d2, Function2 extractor, ValueType valueType ) {
            super(d1, valueType);
            this.d2 = d2;
            this.extractor = extractor;
        }

        @Override
        public ValueType getValueType() {
            return valueType;
        }

        @Override
        public Object getValue( InternalWorkingMemory workingMemory, Tuple tuple ) {
            return extractor.apply( d1.getValue( workingMemory, tuple ), d2.getValue( workingMemory, tuple ) );
        }

        @Override
        public TupleValueExtractor clone() {
            return new IndexValueExtractor2( d1.clone(), d2.clone(), extractor, valueType );
        }

        @Override
        public void replaceDeclaration(Declaration oldDecl, Declaration newDecl) {
            d1 = replaceDeclaration( oldDecl, newDecl, d1 );
            d2 = replaceDeclaration( oldDecl, newDecl, d2 );
        }
    }

    public static class IndexValueExtractor3 extends AbstractIndexValueExtractor {

        private Declaration d2;
        private Declaration d3;
        private final Function3 extractor;

        public IndexValueExtractor3( Declaration d1, Declaration d2, Declaration d3, Function3 extractor, Class<?> clazz ) {
            super(d1, clazz);
            this.d2 = d2;
            this.d3 = d3;
            this.extractor = extractor;
        }

        public IndexValueExtractor3( Declaration d1, Declaration d2, Declaration d3, Function3 extractor, ValueType valueType ) {
            super(d1, valueType);
            this.d2 = d2;
            this.d3 = d3;
            this.extractor = extractor;
        }

        @Override
        public ValueType getValueType() {
            return valueType;
        }

        @Override
        public Object getValue( InternalWorkingMemory workingMemory, Tuple tuple ) {
            return extractor.apply( d1.getValue( workingMemory, tuple ), d2.getValue( workingMemory, tuple ), d3.getValue( workingMemory, tuple ) );
        }

        @Override
        public TupleValueExtractor clone() {
            return new IndexValueExtractor3( d1.clone(), d2.clone(), d3.clone(), extractor, valueType );
        }

        @Override
        public void replaceDeclaration(Declaration oldDecl, Declaration newDecl) {
            d1 = replaceDeclaration( oldDecl, newDecl, d1 );
            d2 = replaceDeclaration( oldDecl, newDecl, d2 );
            d3 = replaceDeclaration( oldDecl, newDecl, d3 );
        }
    }

    public static class IndexValueExtractor4 extends AbstractIndexValueExtractor {

        private Declaration d2;
        private Declaration d3;
        private Declaration d4;
        private final Function4 extractor;

        public IndexValueExtractor4( Declaration d1, Declaration d2, Declaration d3, Declaration d4, Function4 extractor, Class<?> clazz ) {
            super(d1, clazz);
            this.d2 = d2;
            this.d3 = d3;
            this.d4 = d4;
            this.extractor = extractor;
        }

        public IndexValueExtractor4( Declaration d1, Declaration d2, Declaration d3, Declaration d4, Function4 extractor, ValueType valueType ) {
            super(d1, valueType);
            this.d2 = d2;
            this.d3 = d3;
            this.d4 = d4;
            this.extractor = extractor;
        }

        @Override
        public ValueType getValueType() {
            return valueType;
        }

        @Override
        public Object getValue( InternalWorkingMemory workingMemory, Tuple tuple ) {
            return extractor.apply( d1.getValue( workingMemory, tuple ), d2.getValue( workingMemory, tuple ), d3.getValue( workingMemory, tuple ), d4.getValue( workingMemory, tuple ) );
        }

        @Override
        public TupleValueExtractor clone() {
            return new IndexValueExtractor4( d1.clone(), d2.clone(), d3.clone(), d4.clone(), extractor, valueType );
        }

        @Override
        public void replaceDeclaration(Declaration oldDecl, Declaration newDecl) {
            d1 = replaceDeclaration( oldDecl, newDecl, d1 );
            d2 = replaceDeclaration( oldDecl, newDecl, d2 );
            d3 = replaceDeclaration( oldDecl, newDecl, d3 );
            d4 = replaceDeclaration( oldDecl, newDecl, d4 );
        }
    }
}
