package org.drools.brms.client.modeldriven.brxml;

/**
 * Basically the same as setting fields, EXCEPT that
 * it will notify the engine of the changes.
 * This only applies to bound fact variables from the LHS.
 * 
 * @author Michael Neale
 */
public class ActionModifyField extends ActionSetField {

    public ActionModifyField(final String itemText) {
        super( itemText );
    }

    public ActionModifyField() {
        super();
    }

    /**
     * This is used mainly for display purposes. 
     */
    public String getType() {
        return "modify";
    }

}
