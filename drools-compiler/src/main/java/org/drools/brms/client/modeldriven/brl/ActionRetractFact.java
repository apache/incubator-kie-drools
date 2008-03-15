package org.drools.brms.client.modeldriven.brl;

import java.io.ObjectOutput;
import java.io.IOException;
import java.io.ObjectInput;

/**
 * This is used to specify that the bound fact should be retracted
 * when the rule fires.
 * @author Michael Neale
 *
 */
public class ActionRetractFact
    implements
    IAction {

    public ActionRetractFact() {
    }

    public ActionRetractFact(final String var) {
        this.variableName = var;
    }

    public String variableName;

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        variableName    = (String)in.readObject();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(variableName);
    }
}
