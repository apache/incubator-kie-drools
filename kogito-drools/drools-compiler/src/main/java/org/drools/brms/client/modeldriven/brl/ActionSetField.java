package org.drools.brms.client.modeldriven.brl;

import java.io.ObjectOutput;
import java.io.IOException;
import java.io.ObjectInput;

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

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        variable    = (String)in.readObject();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(variable);
    }
}
