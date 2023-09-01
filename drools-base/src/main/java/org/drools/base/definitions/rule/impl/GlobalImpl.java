package org.drools.base.definitions.rule.impl;

import org.kie.api.definition.rule.Global;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public class GlobalImpl implements Global, Externalizable {
    
    private String name;    
    private String type;

    public GlobalImpl() { }

    public GlobalImpl(String name, String type) {
        this.name = name;
        this.type = type;
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject( name );
        out.writeObject( type );
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        name = (String) in.readObject();
        type = (String) in.readObject();
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GlobalImpl global = (GlobalImpl) o;

        if (name != null ? !name.equals(global.name) : global.name != null) return false;
        if (type != null ? !type.equals(global.type) : global.type != null) return false;

        return true;
    }

    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (type != null ? type.hashCode() : 0);
        return result;
    }

    public String toString() {
        return "GlobalImpl{" +
                "name='" + name + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}
