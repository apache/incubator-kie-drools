/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
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

package org.drools.core.base.evaluators;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import org.drools.core.base.BaseEvaluator;
import org.drools.core.base.ValueType;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.rule.VariableRestriction.ObjectVariableContextEntry;
import org.drools.core.rule.VariableRestriction.VariableContextEntry;
import org.drools.core.spi.Evaluator;
import org.drools.core.spi.FieldValue;
import org.drools.core.spi.InternalReadAccessor;

/**
 * This class defines the matches evaluator
 */
public class MatchesEvaluatorsDefinition implements EvaluatorDefinition {

    protected static final String   matchesOp = "matches";

    public static Operator          MATCHES;
    public static Operator          NOT_MATCHES;

    private static String[]         SUPPORTED_IDS;

    { init(); }

    static void init() {
        if ( SUPPORTED_IDS == null ) {
            MATCHES = Operator.addOperatorToRegistry( matchesOp, false );
            NOT_MATCHES = Operator.addOperatorToRegistry( matchesOp, true );
            SUPPORTED_IDS = new String[] { matchesOp };
        }
    }

    private EvaluatorCache evaluators = new EvaluatorCache() {
        private static final long serialVersionUID = 510l;
        {
            addEvaluator( ValueType.STRING_TYPE,        MATCHES,         StringMatchesEvaluator.INSTANCE );
            addEvaluator( ValueType.OBJECT_TYPE,        MATCHES,         StringMatchesEvaluator.INSTANCE );
            addEvaluator( ValueType.STRING_TYPE,        NOT_MATCHES,     StringNotMatchesEvaluator.INSTANCE );
            addEvaluator( ValueType.OBJECT_TYPE,        NOT_MATCHES,     StringNotMatchesEvaluator.INSTANCE );
        }
    };

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        evaluators  = (EvaluatorCache)in.readObject();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(evaluators);
    }

    /**
     * @inheridDoc
     */
    public Evaluator getEvaluator(ValueType type,
                                  Operator operator) {
        return this.evaluators.getEvaluator( type,
                                             operator );
    }

    /**
     * @inheridDoc
     */
    public Evaluator getEvaluator(ValueType type,
                                  Operator operator,
                                  String parameterText) {
        return this.evaluators.getEvaluator( type,
                                             operator );
    }


    public Evaluator getEvaluator(final ValueType type,
                                  final String operatorId,
                                  final boolean isNegated,
                                  final String parameterText) {
        return this.getEvaluator( type,
                                  operatorId,
                                  isNegated,
                                  parameterText,
                                  Target.FACT,
                                  Target.FACT );
        
    }
    
    /**
     * @inheritDoc
     */
    public Evaluator getEvaluator(final ValueType type,
                                  final String operatorId,
                                  final boolean isNegated,
                                  final String parameterText,
                                  final Target left,
                                  final Target right ) {
        return this.evaluators.getEvaluator( type, Operator.determineOperator( operatorId, isNegated ) );
    }

    public String[] getEvaluatorIds() {
        return SUPPORTED_IDS;
    }

    public boolean isNegatable() {
        return true;
    }

    public Target getTarget() {
        return Target.FACT;
    }

    public boolean supportsType(ValueType type) {
        return this.evaluators.supportsType( type );
    }

    /*  *********************************************************
     *           Evaluator Implementations
     *  *********************************************************
     */
    public static class StringMatchesEvaluator extends BaseEvaluator {
        private static final long     serialVersionUID = 400L;
        public final static Evaluator INSTANCE         = new StringMatchesEvaluator();

        {
            MatchesEvaluatorsDefinition.init();
        }

        public StringMatchesEvaluator() {
            super( ValueType.STRING_TYPE,
                   MATCHES );
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final InternalReadAccessor extractor,
                                final InternalFactHandle handle1, final FieldValue fieldValue) {
            final String value1 = (String) extractor.getValue( workingMemory, handle1.getObject() );
            final String value2 = (String) fieldValue.getValue();
            if ( value1 == null ) {
                return false;
            }
            return value1.matches( value2 );
        }

        public boolean evaluateCachedRight(InternalWorkingMemory workingMemory,
                                           final VariableContextEntry context, final InternalFactHandle left) {
            final String value = (String) ((ObjectVariableContextEntry) context).right;
            if ( value == null ) {
                return false;
            }
            return value.matches( (String) context.declaration.getExtractor().getValue( workingMemory, left.getObject() ) );
        }

        public boolean evaluateCachedLeft(InternalWorkingMemory workingMemory,
                                          final VariableContextEntry context, final InternalFactHandle right) {
            final String value = (String) context.extractor.getValue( workingMemory, right.getObject() );
            if ( value == null ) {
                return false;
            }
            return value.matches( (String) ((ObjectVariableContextEntry) context).left );
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final InternalReadAccessor extractor1,
                                final InternalFactHandle handle1,
                                final InternalReadAccessor extractor2, final InternalFactHandle handle2) {
            final Object value1 = extractor1.getValue( workingMemory, handle1.getObject() );
            final Object value2 = extractor2.getValue( workingMemory, handle2.getObject() );
            if ( value1 == null ) {
                return false;
            }
            return ((String) value1).matches( (String) value2 );
        }

        public String toString() {
            return "String matches";
        }
    }

    public static class StringNotMatchesEvaluator extends BaseEvaluator {
        private static final long     serialVersionUID = 400L;
        public final static Evaluator INSTANCE         = new StringNotMatchesEvaluator();

        {
            MatchesEvaluatorsDefinition.init();
        }

        public StringNotMatchesEvaluator() {
            super( ValueType.STRING_TYPE,
                   NOT_MATCHES );
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final InternalReadAccessor extractor,
                                final InternalFactHandle handle1, final FieldValue fieldValue) {
            final String value1 = (String) extractor.getValue( workingMemory, handle1.getObject() );
            final String value2 = (String) fieldValue.getValue();
            if ( value1 == null ) {
                return false;
            }
            return ! value1.matches( value2 );
        }

        public boolean evaluateCachedRight(InternalWorkingMemory workingMemory,
                                           final VariableContextEntry context, final InternalFactHandle left) {
            final String value = (String) ((ObjectVariableContextEntry) context).right;
            if ( value == null ) {
                return false;
            }
            return ! value.matches( (String) context.declaration.getExtractor().getValue( workingMemory, left.getObject() ) );
        }

        public boolean evaluateCachedLeft(InternalWorkingMemory workingMemory,
                                          final VariableContextEntry context, final InternalFactHandle right) {
            final String value = (String) context.extractor.getValue( workingMemory, right.getObject() );
            if ( value == null ) {
                return false;
            }
            return ! value.matches( (String) ((ObjectVariableContextEntry) context).left );
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final InternalReadAccessor extractor1,
                                final InternalFactHandle handle1,
                                final InternalReadAccessor extractor2, final InternalFactHandle handle2) {
            final Object value1 = extractor1.getValue( workingMemory, handle1.getObject() );
            final Object value2 = extractor2.getValue( workingMemory, handle2.getObject() );
            if ( value1 == null ) {
                return false;
            }
            return ! ((String) value1).matches( (String) value2 );
        }

        public String toString() {
            return "String not matches";
        }
    }

}
