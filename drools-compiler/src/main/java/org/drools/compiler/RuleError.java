package org.drools.compiler;

import org.drools.lang.descr.PatternDescr;
import org.drools.rule.Rule;

public class RuleError extends DroolsError {
    private Rule         rule;
    private PatternDescr descr;
    private Object       object;
    private String       message;
    
    public RuleError(Rule rule,
                     PatternDescr descr,
                     Object object,
                     String message) {
        super();
        this.rule = rule;
        this.descr = descr;
        this.object = object;
        this.message = message;
    }
    
    public Rule getRule() {
        return rule;
    }

    public PatternDescr getDescr() {
        return descr;
    }
       
    public Object getObject() {
        return object;
    }

    public String getMessage() {
        return message;
    }
            
}
