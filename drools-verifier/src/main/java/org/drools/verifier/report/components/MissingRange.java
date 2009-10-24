package org.drools.verifier.report.components;

import org.drools.base.evaluators.Operator;
import org.drools.verifier.components.Field;

public abstract class MissingRange
    implements
    Comparable<MissingRange> {

    private static int       index = 0;
    protected final String   guid  = String.valueOf( index++ );

    protected final Field    field;
    protected final Operator operator;

    public MissingRange(Field field,
                        Operator operator) {
        this.field = field;
        this.operator = operator;
    }

    /**
     * Takes the given operator e, and returns a reversed version of it.
     * 
     * @return operator
     */
    public static Operator getReversedOperator(Operator e) {
        if ( e.equals( Operator.NOT_EQUAL ) ) {
            return Operator.EQUAL;
        } else if ( e.equals( Operator.EQUAL ) ) {
            return Operator.NOT_EQUAL;
        } else if ( e.equals( Operator.GREATER ) ) {
            return Operator.LESS_OR_EQUAL;
        } else if ( e.equals( Operator.LESS ) ) {
            return Operator.GREATER_OR_EQUAL;
        } else if ( e.equals( Operator.GREATER_OR_EQUAL ) ) {
            return Operator.LESS;
        } else if ( e.equals( Operator.LESS_OR_EQUAL ) ) {
            return Operator.GREATER;
        } else {
            return Operator.determineOperator( e.getOperatorString(),
                                               !e.isNegated() );
        }
    }

    public int compareTo(MissingRange another) {
        return this.guid.compareTo( another.getGuid() );
    }

    public String getGuid() {
        return guid;
    }

    public Field getField() {
        return field;
    }

    public Operator getOperator() {
        return operator;
    }
}
