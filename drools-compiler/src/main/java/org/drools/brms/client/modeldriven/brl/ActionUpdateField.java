package org.drools.brms.client.modeldriven.brl;

/**
 * Basically the same as setting fields, EXCEPT that
 * it will notify the engine of the changes.
 * This only applies to bound fact variables from the LHS.
 * 
 * @author Michael Neale
 */
public class ActionUpdateField extends ActionSetField {

    public ActionUpdateField(final String itemText) {
        super( itemText );
    }

    public ActionUpdateField() {
        super();
    }


}
