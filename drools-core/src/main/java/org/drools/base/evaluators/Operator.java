package org.drools.base.evaluators;

import java.io.Serializable;

import org.drools.RuntimeDroolsException;

public class Operator
    implements
    Serializable {

    private static final long    serialVersionUID = 400L;

    public static final Operator EQUAL            = new Operator( "==" );
    public static final Operator NOT_EQUAL        = new Operator( "!=" );
    public static final Operator LESS             = new Operator( "<" );
    public static final Operator LESS_OR_EQUAL    = new Operator( "<=" );
    public static final Operator GREATER          = new Operator( ">" );
    public static final Operator GREATER_OR_EQUAL = new Operator( ">=" );
    public static final Operator CONTAINS         = new Operator( "contains" );
    public static final Operator EXCLUDES         = new Operator( "excludes" );
    public static final Operator NOT_CONTAINS     = new Operator( "not contains" );
    public static final Operator MATCHES          = new Operator( "matches" );
    public static final Operator NOT_MATCHES      = new Operator( "not matches" );
    public static final Operator MEMBEROF         = new Operator( "memberOf" );
    public static final Operator NOTMEMBEROF      = new Operator( "not memberOf" );

    private String               operator;

    private Operator(final String operator) {
        this.operator = operator;
    }

    private Object readResolve() throws java.io.ObjectStreamException {
        return determineOperator( this.operator );
    }

    public static Operator determineOperator(final String string) {
        if ( string.equals( "==" ) ) {
            return Operator.EQUAL;
        } else if ( string.equals( "!=" ) ) {
            return Operator.NOT_EQUAL;
        } else if ( string.equals( "<" ) ) {
            return Operator.LESS;
        } else if ( string.equals( "<=" ) ) {
            return Operator.LESS_OR_EQUAL;
        } else if ( string.equals( ">" ) ) {
            return Operator.GREATER;
        } else if ( string.equals( ">=" ) ) {
            return Operator.GREATER_OR_EQUAL;
        } else if ( string.equals( "contains" ) ) {
            return Operator.CONTAINS;
        } else if ( string.equals( "not contains" ) ) {
            return Operator.NOT_CONTAINS;
        } else if ( string.equals( "matches" ) ) {
            return Operator.MATCHES;
        } else if ( string.equals( "not matches" ) ) {
            return Operator.NOT_MATCHES;
        } else if ( string.equals( "excludes" ) ) {
            return Operator.EXCLUDES;
        } else if ( string.equals( "memberOf" ) ) {
            return Operator.MEMBEROF;
        } else if ( string.equals( "not memberOf" ) ) {
            return Operator.NOTMEMBEROF;
        }
        throw new RuntimeDroolsException( "unable to determine operator for String [" + string + "]" );
    }

    public String toString() {
        return "Operator = '" + this.operator + "'";
    }

    public int hashCode() {
        return this.operator.hashCode();
    }

    public boolean equals(final Object object) {
        if ( object == this ) {
            return true;
        }

        return false;
    }
}
