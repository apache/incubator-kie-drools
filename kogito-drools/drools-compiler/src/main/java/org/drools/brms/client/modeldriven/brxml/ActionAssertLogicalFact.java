package org.drools.brms.client.modeldriven.brxml;

/**
 * Logical assertions are used as part of "truth maintenance". 
 * 
 * @author Michael Neale
 */
public class ActionAssertLogicalFact extends ActionAssertFact {

    public ActionAssertLogicalFact(String fact) {
        super(fact);
    }
    
    public ActionAssertLogicalFact() {
        super();
    }

    /**
     * This is used mainly for display purposes. 
     */    
    public String getType() {
        return "assertLogical";
    }
    
}
