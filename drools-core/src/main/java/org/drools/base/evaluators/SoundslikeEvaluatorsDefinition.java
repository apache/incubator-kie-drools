/*
 * Copyright 2010 JBoss Inc
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

package org.drools.base.evaluators;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import org.drools.base.BaseEvaluator;
import org.drools.base.ValueType;
import org.drools.common.InternalFactHandle;
import org.drools.common.InternalWorkingMemory;
import org.drools.rule.VariableRestriction.ObjectVariableContextEntry;
import org.drools.rule.VariableRestriction.VariableContextEntry;
import org.drools.spi.Evaluator;
import org.drools.spi.FieldValue;
import org.drools.spi.InternalReadAccessor;
import org.mvel2.util.Soundex;

/**
 * This class defines the soundslike evaluator
 */
public class SoundslikeEvaluatorsDefinition implements EvaluatorDefinition {

    private static final String[] SUPPORTED_IDS = { StringSoundsLikeEvaluator.SOUNDSLIKE.getOperatorString() };
    private EvaluatorCache evaluators = new EvaluatorCache() {
        private static final long serialVersionUID = 510l;
        {
            addEvaluator( ValueType.STRING_TYPE,        StringSoundsLikeEvaluator.SOUNDSLIKE,         StringSoundsLikeEvaluator.INSTANCE );
            addEvaluator( ValueType.STRING_TYPE,        StringNotSoundsLikeEvaluator.NOT_SOUNDSLIKE,     StringNotSoundsLikeEvaluator.INSTANCE );
            addEvaluator( ValueType.OBJECT_TYPE,        StringSoundsLikeEvaluator.SOUNDSLIKE,         StringSoundsLikeEvaluator.INSTANCE );
            addEvaluator( ValueType.OBJECT_TYPE,        StringNotSoundsLikeEvaluator.NOT_SOUNDSLIKE,     StringNotSoundsLikeEvaluator.INSTANCE );
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
        return this.evaluators.getEvaluator( type,
                                             Operator.determineOperator( operatorId,
                                                                         isNegated ) );
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

    private static boolean soundslike(final String value1,
                                   final String value2) {

        final String soundex1;
        final String soundex2;

        if (value1 == null || value2 == null) {
            return false;
        }
        
        soundex1 = Soundex.soundex(value1);
        soundex2 = Soundex.soundex(value2);

        if (soundex1 == null) {
            return false;
        }

        return soundex1.equals(soundex2);
    }

    /*  *********************************************************
     *           Evaluator Implementations
     *  *********************************************************
     */
    public static class StringSoundsLikeEvaluator extends BaseEvaluator {

        public static final Operator  SOUNDSLIKE       = Operator.addOperatorToRegistry( "soundslike",
                false );
        
        private static final long     serialVersionUID = 510l;
        public final static Evaluator INSTANCE         = new StringSoundsLikeEvaluator();

        public StringSoundsLikeEvaluator() {
            super( ValueType.STRING_TYPE,
                   SOUNDSLIKE );
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final InternalReadAccessor extractor,
                                final InternalFactHandle handle1, final FieldValue handle2) {
            final String value1 = (String) extractor.getValue( workingMemory, handle1.getObject() );
            final String value2 = (String) handle2.getValue();

            return soundslike(value1,value2);
        }

        public boolean evaluateCachedRight(InternalWorkingMemory workingMemory,
                                           final VariableContextEntry context, final InternalFactHandle left) {
            final String value = (String) ((ObjectVariableContextEntry) context).right;

            return soundslike( value, (String) context.declaration.getExtractor().getValue( workingMemory, left.getObject() ) );
        }

        public boolean evaluateCachedLeft(InternalWorkingMemory workingMemory,
                                          final VariableContextEntry context, final InternalFactHandle rightHandle) {
            final String value = (String) context.extractor.getValue( workingMemory, rightHandle.getObject() );

            return soundslike(value, (String) ((ObjectVariableContextEntry) context).left );
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final InternalReadAccessor extractor1,
                                final InternalFactHandle handle1,
                                final InternalReadAccessor extractor2, final InternalFactHandle handle2) {
            final Object value1 = extractor1.getValue( workingMemory, handle1.getObject() );
            final Object value2 = extractor2.getValue( workingMemory, handle2.getObject() );

            return soundslike( (String) value1, (String) value2 );
        }

        public String toString() {
            return "Strings sound alike";
        }
    }

    public static class StringNotSoundsLikeEvaluator extends BaseEvaluator {

        public static final Operator  NOT_SOUNDSLIKE   = Operator.addOperatorToRegistry( "soundslike",
                true );
        private static final long     serialVersionUID = 510l;
        public final static Evaluator INSTANCE         = new StringNotSoundsLikeEvaluator();

        public StringNotSoundsLikeEvaluator() {
            super( ValueType.STRING_TYPE,
                   NOT_SOUNDSLIKE );
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final InternalReadAccessor extractor,
                                final InternalFactHandle handle1, final FieldValue object2) {
            final String value1 = (String) extractor.getValue( workingMemory, handle1.getObject() );
            final String value2 = (String) object2.getValue();

            return ! soundslike( value1,  value2 );
        }

        public boolean evaluateCachedRight(InternalWorkingMemory workingMemory,
                                           final VariableContextEntry context, final InternalFactHandle left) {
            final String value = (String) ((ObjectVariableContextEntry) context).right;

            return ! soundslike( value, (String) context.declaration.getExtractor().getValue( workingMemory, left.getObject() ) );
        }

        public boolean evaluateCachedLeft(InternalWorkingMemory workingMemory,
                                          final VariableContextEntry context, final InternalFactHandle right) {
            final String value = (String) context.extractor.getValue( workingMemory, right.getObject() );

            return ! soundslike( value, (String) ((ObjectVariableContextEntry) context).left );
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final InternalReadAccessor extractor1,
                                final InternalFactHandle handl1,
                                final InternalReadAccessor extractor2, final InternalFactHandle handl2) {
            final Object value1 = extractor1.getValue( workingMemory, handl1.getObject() );
            final Object value2 = extractor2.getValue( workingMemory, handl2.getObject() );

            return ! soundslike( (String) value1,  (String) value2 );
        }

        public String toString() {
            return "Strings not sound alike";
        }
    }


}
