package org.drools.base.evaluators;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Collections;
import java.util.Map;

import org.drools.base.BaseEvaluator;
import org.drools.base.ValueType;
import org.drools.base.evaluators.EvaluatorDefinition;
import org.drools.base.evaluators.Operator;
import org.drools.common.InternalFactHandle;
import org.drools.common.InternalWorkingMemory;
import org.drools.rule.VariableRestriction.ObjectVariableContextEntry;
import org.drools.rule.VariableRestriction.VariableContextEntry;
import org.drools.spi.Evaluator;
import org.drools.spi.FieldValue;
import org.drools.spi.InternalReadAccessor;

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
public class StrEvaluatorDefinition implements EvaluatorDefinition {


    protected static final String   strOp = "str";

    public static Operator          STR_COMPARE;
    public static Operator          NOT_STR_COMPARE;

    private static String[]         SUPPORTED_IDS;

    { init(); }

    static void init() {
        if ( Operator.determineOperator( strOp, false ) == null ) {
            STR_COMPARE = Operator.addOperatorToRegistry( strOp, false );
            NOT_STR_COMPARE = Operator.addOperatorToRegistry( strOp, true );
            SUPPORTED_IDS = new String[] { strOp };
        }
    }

    public enum Operations {
        startsWith, endsWith, length;
    }

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
        StrEvaluator evaluator = new StrEvaluator(type, isNegated);
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
        return true;
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

    public static class StrEvaluator extends BaseEvaluator {
        private Operations parameter;

        {
            StrEvaluatorDefinition.init();
        }

        public void setParameterText(String parameterText) {
            this.parameter = Operations.valueOf(parameterText);
        }

        public Operations getParameter() {
            return parameter;
        }

        public StrEvaluator(final ValueType type, final boolean isNegated) {
            super(type, isNegated ? NOT_STR_COMPARE : STR_COMPARE);
        }

        /**
         * @inheridDoc
         */
        public boolean evaluate(InternalWorkingMemory workingMemory,
                                InternalReadAccessor extractor, InternalFactHandle factHandle, FieldValue value) {
            final Object objectValue = extractor.getValue(workingMemory, factHandle.getObject());
            switch (parameter) {
                case startsWith:
                    return this.getOperator().isNegated() ^ (((String)objectValue).startsWith( (String)value.getValue() ));
                case endsWith:
                    return this.getOperator().isNegated() ^ (((String)objectValue).endsWith( (String)value.getValue() ));
                case length:
                    return this.getOperator().isNegated() ^ (((String)objectValue).length() == value.getLongValue() );
                default:
                    throw new IllegalAccessError("Illegal str comparison parameter");
            }
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                InternalReadAccessor leftExtractor, InternalFactHandle left,
                                InternalReadAccessor rightExtractor, InternalFactHandle right) {
            final Object value1 = leftExtractor.getValue(workingMemory, left.getObject());
            final Object value2 = rightExtractor.getValue(workingMemory, right.getObject());

            switch (parameter) {
                case startsWith:
                    return this.getOperator().isNegated() ^ (((String)value1).startsWith( (String) value2 ));
                case endsWith:
                    return this.getOperator().isNegated() ^ (((String)value1).endsWith( (String) value2 ));
                case length:
                    return this.getOperator().isNegated() ^ (((String)value1).length() == ((Number) value2).longValue() );
                default:
                    throw new IllegalAccessError("Illegal str comparison parameter");
            }
        }

        public boolean evaluateCachedLeft(InternalWorkingMemory workingMemory,
                VariableContextEntry context, InternalFactHandle right) {

                switch (parameter) {
                case startsWith:
                    return this.getOperator().isNegated() ^ (((String)right.getObject()).startsWith( (String)((ObjectVariableContextEntry)
                            context).left) );
                case endsWith:
                    return this.getOperator().isNegated() ^ (((String)right.getObject()).endsWith( (String)((ObjectVariableContextEntry)
                            context).left));
                case length:
                    return this.getOperator().isNegated() ^ (((String)right.getObject()).length() ==  ((Number)((ObjectVariableContextEntry)
                            context).left).longValue());
                default:
                    throw new IllegalAccessError("Illegal str comparison parameter");
                }

        }

        public boolean evaluateCachedRight(InternalWorkingMemory workingMemory,
                VariableContextEntry context, InternalFactHandle left) {
                switch (parameter) {
                case startsWith:
                    return this.getOperator().isNegated() ^ (((String)left.getObject()).startsWith((String)((ObjectVariableContextEntry)
                            context).right));
                case endsWith:
                    return this.getOperator().isNegated() ^ (((String)left.getObject()).endsWith((String)((ObjectVariableContextEntry)
                            context).right));
                case length:
                    return this.getOperator().isNegated() ^ (((String)left.getObject()).length() == ((Number)((ObjectVariableContextEntry)
                            context).right).longValue());
                default:
                    throw new IllegalAccessError("Illegal str comparison parameter");
                }

        }

        @Override
        public String toString() {
            return "StrEvaluatorDefinition str";

        }

    }

}
