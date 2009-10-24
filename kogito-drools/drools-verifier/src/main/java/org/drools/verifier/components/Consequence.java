package org.drools.verifier.components;

import org.drools.verifier.report.components.Cause;

public interface Consequence
    extends
    Cause {

    public static class ConsequenceType {
        public static final ConsequenceType TEXT = new ConsequenceType( "TEXT" );

        protected String                    type;

        public ConsequenceType(String t) {
            type = t;
        }
    }

    public ConsequenceType getConsequenceType();

}
