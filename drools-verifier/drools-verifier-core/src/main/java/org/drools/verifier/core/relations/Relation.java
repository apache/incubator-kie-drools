package org.drools.verifier.core.relations;

import org.drools.verifier.core.index.keys.UUIDKey;

public abstract class Relation<T extends Relation> {

    protected final T origin;
    protected T parent = null;

    public Relation(final T origin) {
        this.origin = origin;
        if (origin != null) {
            origin.setParent(this);
        }
    }

    public T getOrigin() {
        if (origin == null) {
            return (T) this;
        } else {
            return origin;
        }
    }

    public abstract boolean foundIssue();

    public abstract UUIDKey otherUUID();

    public abstract boolean doesRelationStillExist();

    protected void setParent(final T parent) {
        this.parent = parent;
    }
}
