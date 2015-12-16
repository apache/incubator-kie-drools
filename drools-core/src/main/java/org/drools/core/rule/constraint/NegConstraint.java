/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.drools.core.rule.constraint;

import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.rule.Declaration;
import org.drools.core.spi.AlphaNodeFieldConstraint;
import org.drools.core.spi.Constraint;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.concurrent.atomic.AtomicBoolean;

public class NegConstraint implements AlphaNodeFieldConstraint {

    private Constraint.ConstraintType type = ConstraintType.ALPHA;

    private transient AtomicBoolean inUse = new AtomicBoolean(false);

    private boolean operator;

    public NegConstraint() {
        this.operator = true;
    }

    public NegConstraint(boolean operator) {
        this.operator = operator;
    }

    @Override
    public boolean isAllowed(InternalFactHandle handle, InternalWorkingMemory workingMemory) {
        return ( !operator && !handle.isNegated() ) || ( operator && handle.isNegated() );
    }


    public AlphaNodeFieldConstraint cloneIfInUse() {
        if (inUse.compareAndSet(false, true)) {
            return this;
        }
        NegConstraint clone = clone();
        clone.inUse.set(true);
        return clone;
    }

    @Override
    public Declaration[] getRequiredDeclarations() {
        return new Declaration[0];
    }

    @Override
    public void replaceDeclaration(Declaration oldDecl, Declaration newDecl) {
        throw new UnsupportedOperationException();
    }

    @Override
    public NegConstraint clone() {
        NegConstraint negConstraint = new NegConstraint();
        negConstraint.operator = this.operator;
        return negConstraint;
    }

    @Override
    public ConstraintType getType() {
        return type;
    }

    @Override
    public boolean isTemporal() {
        return false;
    }

    public boolean setInUse() {
        return inUse.getAndSet(true);
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeBoolean(operator);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        operator = in.readBoolean();
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }

        NegConstraint that = (NegConstraint) o;

        if (operator != that.operator) { return false; }
        if (type != that.type) { return false; }

        return true;
    }

    @Override
    public int hashCode() {
        int result = type.hashCode();
        result = 31 * result + (operator ? 1 : 0);
        return result;
    }
}
