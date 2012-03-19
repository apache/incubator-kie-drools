/*
 * Copyright 2005 JBoss Inc
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

package org.drools.rule;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import org.drools.base.evaluators.Operator;
import org.drools.common.InternalFactHandle;
import org.drools.common.InternalWorkingMemory;
import org.drools.core.util.AbstractHashTable.FieldIndex;
import org.drools.reteoo.LeftTuple;
import org.drools.spi.AcceptsReadAccessor;
import org.drools.spi.Evaluator;
import org.drools.spi.FieldValue;
import org.drools.spi.InternalReadAccessor;
import org.drools.spi.Restriction;
import org.drools.time.Interval;

public class VariableConstraint extends MutableTypeConstraint
    implements
    AcceptsReadAccessor,
    IndexableConstraint,
    IntervalProviderConstraint,
    Externalizable {

    private static final long    serialVersionUID = 510l;

    private InternalReadAccessor fieldExtractor;
    private Restriction          restriction;

    private transient IndexEvaluator indexEvaluator;

    public VariableConstraint() {
    }

    public VariableConstraint(final InternalReadAccessor fieldExtractor,
                              final Declaration declaration,
                              final Evaluator evaluator) {
        this.fieldExtractor = fieldExtractor;
        this.restriction = new VariableRestriction( fieldExtractor,
                                                    declaration,
                                                    evaluator );
    }

    public VariableConstraint(final InternalReadAccessor fieldExtractor,
                              final Restriction restriction) {
        this.fieldExtractor = fieldExtractor;
        this.restriction = restriction;
    }

    public void readExternal(ObjectInput in) throws IOException,
                                            ClassNotFoundException {
        super.readExternal( in );
        fieldExtractor = (InternalReadAccessor) in.readObject();
        restriction = (Restriction) in.readObject();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal( out );
        out.writeObject( fieldExtractor );
        out.writeObject( restriction );
    }
    
    public void setRestriction(Restriction restriction) {
        this.restriction = restriction;
    }
    
    public void setReadAccessor(InternalReadAccessor readAccessor) {
        this.fieldExtractor = readAccessor;
    }

    public Declaration[] getRequiredDeclarations() {
        return this.restriction.getRequiredDeclarations();
    }

    public FieldIndex getFieldIndex() {
        return new FieldIndex(getFieldExtractor(), getRequiredDeclarations()[0], getIndexEvaluator());
    }

    public void replaceDeclaration(Declaration oldDecl,
                                   Declaration newDecl) {
        this.restriction.replaceDeclaration( oldDecl,
                                             newDecl );
    }

    public InternalReadAccessor getFieldExtractor() {
        return this.fieldExtractor;
    }

    public Evaluator getEvaluator() {
        return this.restriction.getEvaluator();
    }
    
    private IndexEvaluator getIndexEvaluator() {
        if (indexEvaluator == null) {
            indexEvaluator = new WrapperIndexEvaluator(getEvaluator());
        }
        return indexEvaluator;
    }

    public Restriction getRestriction() {
        return this.restriction;
    }

    public boolean isAllowed(final InternalFactHandle handle,
                             final InternalWorkingMemory workingMemory,
                             final ContextEntry context) {
        return this.restriction.isAllowed( this.fieldExtractor,
                                           handle,
                                           workingMemory,
                                           context );
    }

    public boolean isAllowedCachedLeft(final ContextEntry context,
                                       final InternalFactHandle handle) {
        return this.restriction.isAllowedCachedLeft( context,
                                                     handle );
    }

    public boolean isAllowedCachedRight(final LeftTuple tuple,
                                        final ContextEntry context) {
        return this.restriction.isAllowedCachedRight( tuple,
                                                      context );
    }
    
    public boolean isTemporal() {
        return this.restriction.isTemporal();
    }
    
    public Interval getInterval() {
        if ( this.restriction instanceof UnificationRestriction ) {
            return ((UnificationRestriction) this.restriction).getInterval();
        } else {
            return ((VariableRestriction) this.restriction).getInterval();
        }
    }

    public String toString() {
        return this.fieldExtractor + " " + this.restriction;
    }

    public ContextEntry createContextEntry() {
        return this.restriction.createContextEntry();
    }

    public boolean isIndexable() {
        if (restriction instanceof VariableRestriction || restriction instanceof UnificationRestriction ){
            return (getEvaluator().getOperator() == Operator.EQUAL);
        }
        return false;
    }

    public FieldValue getField() {
        return null;
    }

    public boolean isUnification() {
        return restriction instanceof UnificationRestriction;
    }

    public int hashCode() {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result + this.fieldExtractor.hashCode();
        result = PRIME * result + this.restriction.hashCode();
        return result;
    }

    public boolean equals(final Object object) {
        if ( this == object ) {
            return true;
        }

        if ( object == null || getClass() != object.getClass() ) {
            return false;
        }

        final VariableConstraint other = (VariableConstraint) object;

        return this.fieldExtractor.equals( other.fieldExtractor ) && this.restriction.equals( other.restriction );
    }

    public VariableConstraint clone() {
        return new VariableConstraint( this.fieldExtractor,
                                       this.restriction.clone() );
    }

    public static class WrapperIndexEvaluator implements IndexEvaluator {

        private Evaluator evaluator;

        private WrapperIndexEvaluator(Evaluator evaluator) {
            this.evaluator = evaluator;
        }

        public boolean evaluate(InternalWorkingMemory workingMemory, InternalReadAccessor leftExtractor, Object left, InternalReadAccessor rightExtractor, Object right) {
            return evaluator.evaluate(workingMemory, leftExtractor, left, rightExtractor, right);
        }
    }

}
