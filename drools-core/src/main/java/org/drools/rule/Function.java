package org.drools.rule;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public class Function implements Dialectable, Externalizable {
    private String name;
    private String dialect;

    public Function() {

    }

    public Function(String name,
                    String dialect) {
        this.name = name;
        this.dialect = dialect;
    }

    public String getName() {
        return this.name;
    }

    public String getDialect() {
        return this.dialect;
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        name    = (String)in.readObject();
        dialect = (String)in.readObject();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(name);
        out.writeObject(dialect);
    }
}
