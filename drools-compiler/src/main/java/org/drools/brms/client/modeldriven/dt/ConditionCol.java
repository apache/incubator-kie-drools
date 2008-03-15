package org.drools.brms.client.modeldriven.dt;

import org.drools.brms.client.modeldriven.brl.PortableObject;

import java.io.ObjectOutput;
import java.io.IOException;
import java.io.ObjectInput;

public class ConditionCol implements PortableObject {

    public String header;

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        header  = (String)in.readObject();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(header);
    }
}
