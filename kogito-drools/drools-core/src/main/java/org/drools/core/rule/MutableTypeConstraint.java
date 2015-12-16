/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.core.rule;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.concurrent.atomic.AtomicBoolean;

import org.drools.core.spi.AlphaNodeFieldConstraint;
import org.drools.core.spi.BetaNodeFieldConstraint;
import org.drools.core.spi.Constraint;

/**
 * A base class for constraints
 */
public abstract class MutableTypeConstraint
    implements
    AlphaNodeFieldConstraint,
    BetaNodeFieldConstraint,
    Externalizable {

    private Constraint.ConstraintType type = Constraint.ConstraintType.UNKNOWN;

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