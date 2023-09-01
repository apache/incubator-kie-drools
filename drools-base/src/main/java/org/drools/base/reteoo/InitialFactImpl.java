package org.drools.base.reteoo;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import org.drools.base.InitialFact;

/**
 * We dont want users to be able to instantiate InitialFact so we expose it as
 * an interface and make the class and its constructor package protected
 */
public final class InitialFactImpl
    implements
    InitialFact,
    Externalizable {
    private static final InitialFact INSTANCE = new InitialFactImpl();

    private final int                hashCode = "InitialFactImpl".hashCode();

    public static InitialFact getInstance() {
        return InitialFactImpl.INSTANCE;
    }

    public InitialFactImpl() {
    }

    public void readExternal(ObjectInput in) throws IOException,
                                            ClassNotFoundException {
    }

    public void writeExternal(ObjectOutput out) throws IOException {

    }

    public int hashCode() {
        return this.hashCode;
    }

    public boolean equals(final Object object) {
        if ( this == object ) {
            return true;
        }

        if (!(object instanceof InitialFactImpl)) {
            return false;
        }

        return true;
    }
}
