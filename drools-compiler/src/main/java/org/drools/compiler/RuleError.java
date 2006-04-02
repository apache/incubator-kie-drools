package org.drools.compiler;

import org.apache.commons.jci.problems.CompilationProblem;
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
    
    /** 
     * This will return the line number of the error, if possible
     * Otherwise it will be -1
     */
    public int getLine() {
        if (this.descr != null) {
            return this.descr.getLine();
        } else {
            return -1;
        }
    }

    public String getMessage() {
        String summary = message;
        if (object instanceof CompilationProblem[]) {
            CompilationProblem[] problem = (CompilationProblem[]) object;
            for ( int i = 0; i < problem.length; i++ ) {
                summary = summary + " " + problem[i].getMessage();
            }
            
        }
        return summary;
    }
            
}
