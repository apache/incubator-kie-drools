package org.drools.rule;

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

import org.drools.common.InternalFactHandle;
import org.drools.common.InternalWorkingMemory;
import org.drools.reteoo.ReteTuple;
import org.drools.spi.Evaluator;
import org.drools.spi.Extractor;
import org.drools.spi.FieldExtractor;
import org.drools.spi.FieldValue;
import org.drools.spi.Restriction;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectInput;

public class LiteralRestriction
    implements
    Restriction, Externalizable {

    private static final long          serialVersionUID     = 400L;

    private FieldValue           field;

    private Evaluator            evaluator;

    private FieldExtractor       extractor;

    private static final Declaration[] requiredDeclarations = new Declaration[0];

    public LiteralRestriction() {
        this(null, null, null);
    }

    public LiteralRestriction(final FieldValue field,
                              final Evaluator evaluator,
                              final FieldExtractor fieldExtractor) {
        this.field = field;
        this.evaluator = evaluator;
        this.extractor = fieldExtractor;
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        field   = (FieldValue)in.readObject();
        evaluator   = (Evaluator)in.readObject();
        extractor   = (FieldExtractor)in.readObject();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(field);
        out.writeObject(evaluator);
        out.writeObject(extractor);
    }
    public Evaluator getEvaluator() {
        return this.evaluator;
    }

    public FieldValue getField() {
        return this.field;
    }

    public boolean isAllowed(final Extractor extractor,
                             final InternalFactHandle handle,
                             final InternalWorkingMemory workingMemoiry,
                             final ContextEntry context ) {
        return this.evaluator.evaluate( null,
                                        extractor,
                                        handle.getObject(),
                                        this.field );
    }

    public boolean isAllowedCachedLeft(final ContextEntry context,
                                       final InternalFactHandle handle) {
        return this.evaluator.evaluate( null,
                                        ((LiteralContextEntry) context).getFieldExtractor(),
                                        handle.getObject(),
                                        this.field );
    }

    public boolean isAllowedCachedRight(final ReteTuple tuple,
                                        final ContextEntry context) {
        return this.evaluator.evaluate( null,
                                        ((LiteralContextEntry) context).getFieldExtractor(),
                                        ((LiteralContextEntry) context).getObject(),
                                        this.field );
    }

    /**
     * Literal constraints cannot have required declarations, so always return an empty array.
     * @return
     *      Return an empty <code>Declaration[]</code>
     */
    public Declaration[] getRequiredDeclarations() {
        return LiteralRestriction.requiredDeclarations;
    }

    public void replaceDeclaration(Declaration oldDecl,
                                   Declaration newDecl) {
    }

    public String toString() {
        return "[LiteralRestriction evaluator=" + this.evaluator + " value=" + this.field + "]";
    }

    public int hashCode() {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result + this.evaluator.hashCode();
        result = PRIME * result + ((this.field.getValue() != null) ? this.field.getValue().hashCode() : 0);
        return result;
    }

    public boolean equals(final Object object) {
        if ( this == object ) {
            return true;
        }
        if ( object == null || object.getClass() != LiteralRestriction.class ) {
            return false;
        }
        final LiteralRestriction other = (LiteralRestriction) object;

        return this.field.equals( other.field ) && this.evaluator.equals( other.evaluator );
    }

    public ContextEntry createContextEntry() {
        return new LiteralContextEntry( this.extractor );
    }

    public Object clone() {
        return new LiteralRestriction( this.field,
                                       this.evaluator,
                                       this.extractor );
    }

    private static class LiteralContextEntry
        implements
        ContextEntry {

        private static final long serialVersionUID = 2621864784428098347L;
        public FieldExtractor     extractor;
        public Object             object;
        public ContextEntry       next;

        public LiteralContextEntry() {
        }

        public LiteralContextEntry(final FieldExtractor extractor) {
            this.extractor = extractor;
        }

        public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
            extractor   = (FieldExtractor)in.readObject();
            object      = in.readObject();
            next        = (ContextEntry)in.readObject();
        }

        public void writeExternal(ObjectOutput out) throws IOException {
            out.writeObject(extractor);
            out.writeObject(object);
            out.writeObject(next);
        }

        public FieldExtractor getFieldExtractor() {
            return this.extractor;
        }

        public Object getObject() {
            return this.object;
        }

        public ContextEntry getNext() {
            return this.next;
        }

        public void setNext(final ContextEntry entry) {
            this.next = entry;
        }

        public void updateFromFactHandle(final InternalWorkingMemory workingMemory,
                                         final InternalFactHandle handle) {
            this.object = handle.getObject();
        }

        public void updateFromTuple(final InternalWorkingMemory workingMemory,
                                    final ReteTuple tuple) {
            // nothing to do
        }

        public void resetTuple() {
        }

        public void resetFactHandle() {
            this.object = null;
        }

    }

}