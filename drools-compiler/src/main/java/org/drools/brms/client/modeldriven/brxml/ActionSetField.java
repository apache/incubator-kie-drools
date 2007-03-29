package org.drools.brms.client.modeldriven.brxml;

/**
 * For setting a field on a bound LHS variable or a global.
 * If setting a field on a fact bound variable, this will 
 * NOT notify the engine of any changes (unless done outside of the engine).
 * 
 * @author Michael Neale
 */
public class ActionSetField extends ActionFieldList {

    
    public ActionSetField(String var) {
        this.variable = var;
    }
    
    /**
     * This is used mainly for display purposes. 
     */    
    public String getType() {
        return "set";
    }
    
    public ActionSetField() {}
    public String variable;

     
 
    
}
