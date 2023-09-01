package org.drools.traits.core.factmodel;

import java.io.Serializable;

public abstract class Key<T> implements LatticeElement<T>, Serializable {

    private T value;
    private int id;

    public Key( int id, T value ) {
        this.value = value;
        this.id = id;
    }

    public T getValue() {
        return value;
    }

    public void setValue( T value ) {
        this.value = value;
    }

    public int getId() {
        return id;
    }

    public void setId( int id ) {
        this.id = id;
    }

    @Override
    public boolean equals( Object o ) {
        if ( this == o ) return true;
        if ( o == null || getClass() != o.getClass() ) return false;

        Key key = (Key) o;

        if ( id != key.id ) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id;
    }

    @Override
    public String toString() {
        return id + " :: " + value;
    }
}
