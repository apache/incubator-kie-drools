package org.drools.base.base;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import org.drools.base.rule.accessor.Salience;
import org.kie.api.definition.rule.Rule;
import org.kie.api.runtime.rule.Match;

public class SalienceInteger
    implements
        Salience, Externalizable {

    private static final long serialVersionUID = 510l;

    public static final Salience DEFAULT_SALIENCE = new SalienceInteger( DEFAULT_SALIENCE_VALUE );

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

    public int getValue(final Match activation,
                        final Rule rule,
                        final ValueResolver valueResolver) {
        return this.value;
    }

    public int getValue() {
        return value;
    }

    @Override
    public boolean isDefault() {
        return getValue() == DEFAULT_SALIENCE.getValue();
    }

    public String toString() {
        return String.valueOf( this.value );
    }

    public boolean isDynamic() {
        return false;
    }

}
