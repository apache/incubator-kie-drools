package org.drools.verifier.components;

import org.drools.verifier.report.components.Cause;

public interface Consequence
    extends
    Cause {

    public static class ConsequenceType {
        public static final ConsequenceType TEXT = new ConsequenceType( "TEXT" );

        public String                       type;

        public ConsequenceType(String t) {
            type = t;
        }

        public String toString() {
            return type;
        }
    }

    public String getGuid();

    public ConsequenceType getConsequenceType();

    public String getRuleGuid();
    
    public String getRuleName();
}
