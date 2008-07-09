/**
 * 
 */
package org.drools.guvnor.client.modeldriven.brl;

/**
 * For modifying a field on a bound LHS variable or a global. Modify here
 * means that users can call methods that modify the object.
 * If setting a field on a fact bound variable, this will 
 * NOT notify the engine of any changes (unless done outside of the engine).
 * 
 * @author isabel
 *
 */
public class ActionCallMethod extends ActionSetField {
 
    public ActionCallMethod(final String itemText) {
        super( itemText );
    }

    public ActionCallMethod() {
        super();
    }

    public ActionFieldFunction getFieldValue(int i) {
        return (ActionFieldFunction) this.fieldValues[i];
    }

    public void addFieldValue(final ActionFieldValue val) {
        if (val instanceof ActionFieldFunction) {
            super.addFieldValue(val);
        } else {
            throw new IllegalArgumentException("Cannot assign field values of types other than ActionFieldFunction.");
        }
    }
}
