package org.drools.factmodel.traits;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public class TypeWrapper implements Externalizable {

    private transient Class klass;
    private String name;

    public TypeWrapper() {
    }

    public TypeWrapper( Class klass ) {
        this.klass = klass;
        this.name = klass.getName();
    }

    public Class getKlass() {
        return klass;
    }

    public void setKlass( Class klass ) {
        this.klass = klass;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals( Object o ) {
        if ( this == o ) return true;
        if ( o == null || getClass() != o.getClass() ) return false;

        TypeWrapper that = (TypeWrapper) o;

        if ( !name.equals( that.name ) ) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    public void writeExternal( ObjectOutput out ) throws IOException {
        out.writeObject( name );
    }

    public void readExternal( ObjectInput in ) throws IOException, ClassNotFoundException {
        name = (String) in.readObject();
    }

    @Override
    public String toString() {
        return "Wrapper{" + name + "}";
    }
}
