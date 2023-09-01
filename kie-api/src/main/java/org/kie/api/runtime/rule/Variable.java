package org.kie.api.runtime.rule;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public class Variable implements Externalizable {
    public static final Variable v = new Variable();

    public Variable() {
        // for serialization
    }
    public void writeExternal(ObjectOutput out) throws IOException {
    }

    public void readExternal(ObjectInput in) throws IOException,
                                            ClassNotFoundException {
    }



}
