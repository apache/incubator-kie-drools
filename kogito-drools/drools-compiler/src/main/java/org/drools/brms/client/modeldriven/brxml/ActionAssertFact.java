package org.drools.brms.client.modeldriven.brxml;

/**
 * This is used when asserting a new fact.
 * @author Michael Neale
 *
 */
public class ActionAssertFact
    extends
    ActionFieldList {

    /**
     * This is used mainly for display purposes. 
     */
    public String getType() {
        return "assert";
    }
    
    public String factType;
    
    public ActionAssertFact(String type) {
        this.factType = type;
    }
    
    public ActionAssertFact() {}
    

}
