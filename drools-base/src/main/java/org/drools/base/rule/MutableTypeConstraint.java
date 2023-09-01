package org.drools.base.rule;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.concurrent.atomic.AtomicBoolean;

import org.drools.base.rule.constraint.AlphaNodeFieldConstraint;
import org.drools.base.rule.constraint.BetaNodeFieldConstraint;
import org.drools.base.rule.constraint.Constraint;

/**
 * A base class for constraints
 */
public abstract class MutableTypeConstraint
    implements
        AlphaNodeFieldConstraint,
        BetaNodeFieldConstraint,
    Externalizable {

    private ConstraintType type = Constraint.ConstraintType.UNKNOWN;

    private transient AtomicBoolean inUse = new AtomicBoolean(false);

    public void setType( ConstraintType type ) {
        this.type = type;
    }

    public ConstraintType getType() {
        return this.type;
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        type    =  (Constraint.ConstraintType)in.readObject();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(type);
    }

    public abstract MutableTypeConstraint clone();

    public MutableTypeConstraint cloneIfInUse() {
        if (inUse.compareAndSet(false, true)) {
            return this;
        }
        MutableTypeConstraint clone = clone();
        clone.inUse.set(true);
        return clone;
    }

    public boolean setInUse() {
        return inUse.getAndSet(true);
    }
}