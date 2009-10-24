package org.drools.verifier.components;

import java.io.Serializable;

/**
 * 
 * @author Toni Rikkola
 */
public class OperatorDescr extends PatternComponent
    implements
    Serializable {
    private static final long serialVersionUID = 8393994152436331910L;

    public static class Type {
        public static final Type AND = new Type( "AND" );
        public static final Type OR  = new Type( "OR" );

        protected final String   type;

        private Type(String t) {
            type = t;
        }

    };

    private Type type;

    public OperatorDescr() {
    }

    public OperatorDescr(Type operatorType) {
        this.type = operatorType;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public VerifierComponentType getVerifierComponentType() {
        return VerifierComponentType.OPERATOR;
    }

}
