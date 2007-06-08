package org.drools.brms.client.modeldriven.brxml;

/**
 * For setting a field on a bound LHS variable or a global.
 * If setting a field on a fact bound variable, this will 
 * NOT notify the engine of any changes (unless done outside of the engine).
 * 
 * @author Michael Neale
 */
public class ActionSetField extends ActionFieldList {

    public ActionSetField(final String var) {
        this.variable = var;
    }


    public ActionSetField() {
    }

    public String variable;

}
