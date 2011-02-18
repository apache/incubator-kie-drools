/**
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

import org.drools.common.InternalFactHandle;
import org.drools.common.InternalWorkingMemory;
import org.drools.spi.AcceptsReadAccessor;
import org.drools.spi.AlphaNodeFieldConstraint;
import org.drools.spi.Constraint;
import org.drools.spi.Evaluator;
import org.drools.spi.FieldValue;
import org.drools.spi.InternalReadAccessor;

public class LiteralConstraint
    implements
    AlphaNodeFieldConstraint,
    AcceptsReadAccessor,
    Externalizable {

    private static final long    serialVersionUID = 510l;

    private InternalReadAccessor readAccesor;
    private LiteralRestriction   restriction;

    public LiteralConstraint() {
        this( null,
              null );
    }

    public LiteralConstraint(final InternalReadAccessor extractor,
                             final Evaluator evaluator,
                             final FieldValue field) {
        this.readAccesor = extractor;
        this.restriction = new LiteralRestriction( field,
                                                   evaluator,
                                                   extractor );
    }

    public LiteralConstraint(final InternalReadAccessor extractor,
                             final LiteralRestriction restriction) {
        this.readAccesor = extractor;
        this.restriction = restriction;
    }

    public void readExternal(ObjectInput in) throws IOException,
                                            ClassNotFoundException {
        readAccesor = (InternalReadAccessor) in.readObject();
        restriction = (LiteralRestriction) in.readObject();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject( readAccesor );
        out.writeObject( restriction );
    }

    public void setReadAccessor(InternalReadAccessor readAccessor) {
        this.readAccesor = readAccessor;
    }

    public Evaluator getEvaluator() {
        return this.restriction.getEvaluator();
    }

    public FieldValue getField() {
        return this.restriction.getField();
    }

    public InternalReadAccessor getFieldExtractor() {
        return this.readAccesor;
    }

    /**
     * Literal constraints cannot have required declarations, so always return an empty array.
     * @return
     *      Return an empty <code>Declaration[]</code>
     */
    public Declaration[] getRequiredDeclarations() {
        return this.restriction.getRequiredDeclarations();
    }

    public void replaceDeclaration(Declaration oldDecl,
                                   Declaration newDecl) {
        this.restriction.replaceDeclaration( oldDecl,
                                             newDecl );
    }

    public boolean isAllowed(final InternalFactHandle handle,
                             final InternalWorkingMemory workingMemory,
                             final ContextEntry ctx) {
        return this.restriction.isAllowed( this.readAccesor,
                                           handle,
                                           workingMemory,
                                           ctx );
    }

    public String toString() {
        return "[LiteralConstraint fieldExtractor=" + this.readAccesor + " evaluator=" + getEvaluator() + " value=" + getField() + "]";
    }

    public int hashCode() {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result + this.readAccesor.hashCode();
        result = PRIME * result + this.restriction.hashCode();
        return result;
    }

    public boolean equals(final Object object) {
        if ( this == object ) {
            return true;
        }
        if ( object == null || object.getClass() != LiteralConstraint.class ) {
            return false;
        }
        final LiteralConstraint other = (LiteralConstraint) object;

        return this.readAccesor.equals( other.readAccesor ) && this.restriction.equals( other.restriction );
    }

    public Object clone() {
        return new LiteralConstraint( this.readAccesor,
                                      this.getEvaluator(),
                                      this.getField() );
    }

    public ContextEntry createContextEntry() {
        // no need for context info
        return null;
    }

    public ConstraintType getType() {
        return Constraint.ConstraintType.ALPHA;
    }

    public boolean isTemporal() {
        return this.restriction.isTemporal();
    }
}
