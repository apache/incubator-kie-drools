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
package org.drools.modelcompiler.constraints;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.List;
import java.util.Optional;

import org.drools.base.base.ObjectType;
import org.drools.base.base.ValueResolver;
import org.drools.base.base.ValueType;
import org.drools.base.base.field.ObjectFieldImpl;
import org.drools.base.reteoo.BaseTuple;
import org.drools.base.reteoo.PropertySpecificUtil;
import org.drools.base.rule.ContextEntry;
import org.drools.base.rule.Declaration;
import org.drools.base.rule.Pattern;
import org.drools.base.rule.accessor.FieldValue;
import org.drools.base.rule.accessor.ReadAccessor;
import org.drools.base.rule.accessor.TupleValueExtractor;
import org.drools.base.time.Interval;
import org.drools.base.util.IndexedValueReader;
import org.drools.base.util.index.ConstraintTypeOperator;
import org.drools.util.bitmask.BitMask;
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
import org.kie.api.KieBaseConfiguration;
import org.kie.api.runtime.rule.FactHandle;

import static org.drools.base.base.ValueType.determineValueType;
import static org.drools.base.reteoo.PropertySpecificUtil.getEmptyPropertyReactiveMask;
import static org.drools.modelcompiler.util.EvaluationUtil.adaptBitMask;

public class LambdaConstraint extends AbstractConstraint {

    private final ConstraintEvaluator evaluator;
    private final PredicateInformation predicateInformation;

    private FieldValue field;
    private ReadAccessor readAccessor;

    private TupleValueExtractor rightIndexExtractor;
    private AbstractIndexValueExtractor leftIndexExtractor;

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

    public static class LambdaRightTupleValueExtractor implements TupleValueExtractor {
        private ValueType valueType;

        private Function1 extractor;

        public LambdaRightTupleValueExtractor(ValueType valueType, Function1 extractor) {
            this.valueType  = valueType;
            this.extractor = extractor;
        }

        @Override
        public ValueType getValueType() {
            return valueType;
        }

        @Override
        public Object getValue(ValueResolver valueResolver, BaseTuple tuple) {
            return extractor.apply(tuple.getFactHandle().getObject());
        }

        @Override
        public TupleValueExtractor clone() {
            return new LambdaRightTupleValueExtractor(valueType, extractor);
        }
    }

    private void initIndexes() {
        Index index = evaluator.getIndex();
        if (index != null) {
            switch (index.getIndexType()) {
                case ALPHA:
                    this.field = new ObjectFieldImpl( ( ( AlphaIndex ) index).getRightValue() );
                    this.readAccessor = new LambdaReadAccessor(index.getIndexId(), index.getIndexedClass(), index.getLeftOperandExtractor());
                    break;
                case BETA:
                    this.leftIndexExtractor = initBetaIndex( ( BetaIndexN ) index );
                    this.rightIndexExtractor = new LambdaRightTupleValueExtractor(ValueType.determineValueType(index.getIndexedClass()), index.getLeftOperandExtractor());
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
        if ( leftIndexExtractor != null ) {
            leftIndexExtractor.replaceDeclaration( oldDecl, newDecl );
        }
    }

    /*
     * pattern is not used in this method because reactOnProperties are already filtered by ExpressionTyper.addReactOnPropertyForArgument
     */
    @Override
    public BitMask getListenedPropertyMask( Optional<Pattern> pattern, ObjectType objectType, List<String> settableProperties ) {
        BitMask mask = adaptBitMask( evaluator.getReactivityBitMask() );
        if (mask != null) {
            return mask;
        }

        if (evaluator.getReactiveProps().length == 0) {
            return super.getListenedPropertyMask( pattern, objectType, settableProperties );
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
    public boolean isAllowed(FactHandle handle, ValueResolver valueResolver) {
        try {
            return evaluator.evaluate(handle, valueResolver);
        } catch (RuntimeException e) {
            throw new ConstraintEvaluationException(predicateInformation, e);
        }
    }

    @Override
    public boolean isAllowedCachedLeft(ContextEntry context, FactHandle handle) {
        LambdaContextEntry lambdaContext = ((LambdaContextEntry) context);
        try {
            return evaluator.evaluate(handle, lambdaContext.getTuple(), lambdaContext.getReteEvaluator());
        } catch (RuntimeException e) {
            throw new ConstraintEvaluationException(predicateInformation, e);
        }
    }

    @Override
    public boolean isAllowedCachedRight(BaseTuple tuple, ContextEntry context) {
        LambdaContextEntry lambdaContext = ((LambdaContextEntry) context);
        try {
            return evaluator.evaluate(lambdaContext.getHandle(), tuple, lambdaContext.getReteEvaluator());
        } catch (RuntimeException e) {
            throw new ConstraintEvaluationException(predicateInformation, e);
        }
    }

    @Override
    public ContextEntry createContext() {
        return new LambdaContextEntry();
    }

    @Override
    public boolean isUnification() {
        return false;
    }

    @Override
    public boolean isIndexable(int nodeType, KieBaseConfiguration config) {
        return getConstraintType().isIndexableForNode(nodeType, this, config);
    }

    @Override
    public ConstraintTypeOperator getConstraintType() {
        Index index = evaluator.getIndex();
        if (index != null) {
            switch (index.getConstraintType()) {
                case EQUAL:
                    return ConstraintTypeOperator.EQUAL;
                case NOT_EQUAL:
                    return ConstraintTypeOperator.NOT_EQUAL;
                case GREATER_THAN:
                    return ConstraintTypeOperator.GREATER_THAN;
                case GREATER_OR_EQUAL:
                    return ConstraintTypeOperator.GREATER_OR_EQUAL;
                case LESS_THAN:
                    return ConstraintTypeOperator.LESS_THAN;
                case LESS_OR_EQUAL:
                    return ConstraintTypeOperator.LESS_OR_EQUAL;
                case RANGE:
                    return ConstraintTypeOperator.RANGE;
            }
        }
        return ConstraintTypeOperator.UNKNOWN;
    }

    @Override
    public FieldValue getField() {
        return field;
    }

    @Override
    public IndexedValueReader getFieldIndex() {
        return new IndexedValueReader(leftIndexExtractor, rightIndexExtractor);
    }

    @Override
    public ReadAccessor getFieldExtractor() {
        return readAccessor;
    }

    @Override
    public TupleValueExtractor getRightIndexExtractor() {
        return rightIndexExtractor;
    }

    @Override
    public TupleValueExtractor getLeftIndexExtractor() {
        return leftIndexExtractor;
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

        private BaseTuple tuple;
        private FactHandle handle;

        private transient ValueResolver valueResolver;

        public void updateFromTuple(ValueResolver valueResolver, BaseTuple tuple) {
            this.tuple = tuple;
            this.valueResolver = valueResolver;
        }

        public void updateFromFactHandle(ValueResolver valueResolver, FactHandle handle) {
            this.valueResolver = valueResolver;
            this.handle = handle;
        }

        public void resetTuple() {
            tuple = null;
        }

        public void resetFactHandle() {
            valueResolver = null;
            handle = null;
        }

        public void writeExternal(ObjectOutput out ) throws IOException {
            out.writeObject(tuple);
            out.writeObject( handle );
        }

        public void readExternal(ObjectInput in ) throws IOException, ClassNotFoundException {
            tuple = (BaseTuple) in.readObject();
            handle = (FactHandle) in.readObject();
        }

        public BaseTuple getTuple() {
            return tuple;
        }

        public FactHandle getHandle() {
            return handle;
        }

        public ValueResolver getReteEvaluator() {
            return valueResolver;
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
        public Object getValue( ValueResolver valueResolver, BaseTuple tuple ) {
            return extractor.apply( d1.getValue( valueResolver, tuple ) );
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
        public Object getValue( ValueResolver valueResolver, BaseTuple tuple ) {
            return extractor.apply( d1.getValue( valueResolver, tuple ), d2.getValue( valueResolver, tuple ) );
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
        public Object getValue( ValueResolver valueResolver, BaseTuple tuple ) {
            return extractor.apply( d1.getValue( valueResolver, tuple ), d2.getValue( valueResolver, tuple ), d3.getValue( valueResolver, tuple ) );
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
        public Object getValue( ValueResolver valueResolver, BaseTuple tuple ) {
            return extractor.apply( d1.getValue( valueResolver, tuple ), d2.getValue( valueResolver, tuple ), d3.getValue( valueResolver, tuple ), d4.getValue( valueResolver, tuple ) );
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
