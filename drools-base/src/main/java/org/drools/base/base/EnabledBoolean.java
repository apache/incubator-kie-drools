package org.drools.base.base;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import org.drools.base.definitions.rule.impl.RuleImpl;
import org.drools.base.reteoo.BaseTuple;
import org.drools.base.rule.accessor.Enabled;
import org.drools.base.rule.Declaration;

public class EnabledBoolean
    implements
        Enabled,
    Externalizable {

    private static final long   serialVersionUID = 510l;

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

    public boolean getValue(final BaseTuple tuple,
                            final Declaration[] declarations,
                            final RuleImpl rule,
                            final ValueResolver valueResolver) {
        return this.value;
    }

    public String toString() {
        return String.valueOf( this.value );
    }

}
