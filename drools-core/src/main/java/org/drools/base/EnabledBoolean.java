package org.drools.base;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import org.drools.WorkingMemory;
import org.drools.rule.Rule;
import org.drools.spi.Enabled;
import org.drools.spi.Tuple;

public class EnabledBoolean
    implements
    Enabled,
    Externalizable {

    /**
     *
     */
    private static final long   serialVersionUID = 400L;

    public static final Enabled ENABLED_TRUE  = new EnabledBoolean( true );
    public static final Enabled ENABLED_FALSE  = new EnabledBoolean( false );

    private boolean             value;

    public EnabledBoolean() {
    }

    public EnabledBoolean(boolean value) {
        this.value = value;
    }

    public void readExternal(ObjectInput in) throws IOException,
                                            ClassNotFoundException {
        value = in.readBoolean();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeBoolean( value );
    }

    public boolean getValue(final Tuple tuple,
                            final Rule rule,
                            final WorkingMemory workingMemory) {
        return this.value;
    }

    public String toString() {
        return String.valueOf( this.value );
    }

}
