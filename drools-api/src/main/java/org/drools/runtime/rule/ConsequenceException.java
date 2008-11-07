package org.drools.runtime.rule;

import org.drools.definition.rule.Rule;

public class ConsequenceException extends RuntimeException {
    private Rule              rule;
    
    public ConsequenceException(final Throwable rootCause,
                                final Rule rule) {
        super( rootCause );
        this.rule = rule;
    }
    
    public Rule getRule() {
        return this.rule;
    }
    
}
