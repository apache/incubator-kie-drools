/*
 * Copyright 2011 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.base.evaluators;

import org.drools.base.BaseEvaluator;
import org.drools.base.ValueType;
import org.drools.common.InternalWorkingMemory;
import org.drools.factmodel.traits.*;
import org.drools.rule.VariableRestriction.VariableContextEntry;
import org.drools.spi.Evaluator;
import org.drools.spi.FieldValue;
import org.drools.spi.InternalReadAccessor;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Collection;
import java.util.Set;

/**
 * <p>The implementation of the 'str' evaluator definition.</p>
 * 
 * <p>The <b><code>str</code></b> compares two string values.</p> 
 * 
 * <p>Lets look at some examples:</p>
 * 
 * <pre>$m : Message( routingValue str[startsWith] "R1" )</pre>
 * <pre>$m : Message( routingValue str[endsWith] "R2" )</pre>
 * <pre>$m : Message( routingValue str[length] 17 )</pre>
 */
public class IsAEvaluatorDefinition implements EvaluatorDefinition {
    public static final Operator ISA = Operator.addOperatorToRegistry(
            "isA", false);
    public static final Operator NOT_ISA = Operator
            .addOperatorToRegistry("isA", true);
    private static final String[] SUPPORTED_IDS = { ISA
            .getOperatorString() };

    private Evaluator[] evaluator;

    /**
     * @inheridDoc
     */
    public Evaluator getEvaluator(ValueType type, Operator operator) {
        return this.getEvaluator(type, operator.getOperatorString(), operator
                .isNegated(), null);
    }

    /**
     * @inheridDoc
     */
    public Evaluator getEvaluator(ValueType type, Operator operator,
            String parameterText) {
        return this.getEvaluator(type, operator.getOperatorString(), operator
                .isNegated(), parameterText);
    }

    /**
     * @inheridDoc
     */
    public Evaluator getEvaluator(ValueType type, String operatorId,
            boolean isNegated, String parameterText) {
        return getEvaluator(type, operatorId, isNegated, parameterText,
                Target.FACT, Target.FACT);
    }

    /**
     * @inheridDoc
     */
    public Evaluator getEvaluator(ValueType type, String operatorId,
            boolean isNegated, String parameterText, Target leftTarget,
            Target rightTarget) {
        IsAEvaluator evaluator = new IsAEvaluator(type, isNegated);
        evaluator.setParameterText(parameterText);
        return evaluator;
    }

    /**
     * @inheridDoc
     */
    public String[] getEvaluatorIds() {
        return SUPPORTED_IDS;
    }

    /**
     * @inheridDoc
     */
    public Target getTarget() {
        return Target.FACT;
    }

    /**
     * @inheridDoc
     */
    public boolean isNegatable() {
        return true;
    }

    /**
     * @inheridDoc
     */
    public boolean supportsType(ValueType type) {
        return (type.equals( ValueType.TRAIT_TYPE ) );
    }

    /**
     * @inheridDoc
     */
    public void readExternal(ObjectInput in) throws IOException,
            ClassNotFoundException {
        evaluator = (Evaluator[]) in.readObject();
    }

    /**
     * @inheridDoc
     */
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(evaluator);
    }

    public static class IsAEvaluator extends BaseEvaluator {

        public void setParameterText(String parameterText) {

        }

        public IsAEvaluator(final ValueType type, final boolean isNegated) {
            super(type, isNegated ? NOT_ISA : ISA );
        }

        /**
         * @inheridDoc
         */
        public boolean evaluate(InternalWorkingMemory workingMemory,
                InternalReadAccessor extractor, Object object, FieldValue value) {
            final Object objectValue = extractor
                    .getValue(workingMemory, object);

            Object typeName = value.getValue();
            if ( typeName instanceof Class ) {
                typeName = ((Class) typeName).getName();
            }

            TraitableBean core = null;
            if ( objectValue instanceof Thing) {
                Thing thing = (Thing) objectValue;
                core = (TraitableBean) thing.getCore();
                return this.getOperator().isNegated() ^ core.hasTrait(typeName.toString());
            } else if ( objectValue.getClass().getAnnotation( Traitable.class ) != null ) {
                core = (TraitableBean) objectValue;
                return this.getOperator().isNegated() ^ core.hasTrait( typeName.toString() );
            }


            return false;
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                InternalReadAccessor leftExtractor, Object left,
                InternalReadAccessor rightExtractor, Object right) {
            final Object value1 = leftExtractor.getValue(workingMemory, left);
            final Object value2 = rightExtractor.getValue(workingMemory, right);

            Object target = value1;
            Object source = value2;

            return compare( source, target );
        }


        public boolean evaluateCachedLeft(InternalWorkingMemory workingMemory,
                VariableContextEntry context, Object right) {

            Object target = right;
            Object source = context.getObject();

            return compare( source, target );
        }

        public boolean evaluateCachedRight(InternalWorkingMemory workingMemory,
                VariableContextEntry context, Object left) {

            Object target = left;
            Object source = context.getObject();

            return compare( source, target );
        }



        private boolean compare(Object source, Object target) {
            Collection sourceTraits = null;
            Collection targetTraits = null;
            if ( source instanceof Thing) {
                sourceTraits = ((TraitableBean) ((Thing) source).getCore()).getTraits();
            } else if ( source.getClass().getAnnotation( Traitable.class ) != null ) {
                sourceTraits = ((TraitableBean) source).getTraits();
            }

            if ( target instanceof Thing) {
                targetTraits = ((TraitableBean) ((Thing) target).getCore()).getTraits();
            } else if ( source.getClass().getAnnotation( Traitable.class ) != null ) {
                targetTraits = ((TraitableBean) target).getTraits();
            }

            return ( targetTraits != null &&
                    ( this.getOperator().isNegated() ^ sourceTraits.containsAll( targetTraits ) ) );
        }

        @Override
        public String toString() {
            return "IsAEvaluatorDefinition isA";

        }

    }

}
