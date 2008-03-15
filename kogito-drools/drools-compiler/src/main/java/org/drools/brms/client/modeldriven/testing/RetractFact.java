package org.drools.brms.client.modeldriven.testing;

import java.io.ObjectInput;
import java.io.IOException;
import java.io.ObjectOutput;

/**
 * Retract a named fact.
 * @author Michael Neale
 *
 */
public class RetractFact implements Fixture {

    public RetractFact() {}
    public RetractFact(String s) {
        this.name = s;
    }

    public String name;

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        name    = (String)in.readObject();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(name);
    }
}
