package org.drools.base;

import org.drools.WorkingMemory;
import org.drools.spi.Salience;
import org.drools.spi.Tuple;

import java.io.Externalizable;
import java.io.ObjectInput;
import java.io.IOException;
import java.io.ObjectOutput;

public class SalienceInteger
    implements
    Salience, Externalizable {

    /**
     *
     */
    private static final long serialVersionUID = 400L;

    public static final Salience DEFAULT_SALIENCE = new SalienceInteger( 0 );

    private int value;

    public SalienceInteger() {
    }

    public SalienceInteger(int value) {
        this.value = value;
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        value   = in.readInt();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeInt(value);
    }
    public int getValue(final Tuple tuple,
                        final WorkingMemory workingMemory) {
        return this.value;
    }

    public String toString() {
        return String.valueOf( this.value );
    }

}
