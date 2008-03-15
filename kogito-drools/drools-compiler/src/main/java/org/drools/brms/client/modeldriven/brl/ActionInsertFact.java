package org.drools.brms.client.modeldriven.brl;

import java.io.ObjectInput;
import java.io.IOException;
import java.io.ObjectOutput;

/**
 * This is used when asserting a new fact.
 * @author Michael Neale
 *
 */
public class ActionInsertFact extends ActionFieldList {


    public String factType;

    public ActionInsertFact(final String type) {
        this.factType = type;
    }

    public ActionInsertFact() {
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        factType    = (String)in.readObject();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(factType);
    }
}
