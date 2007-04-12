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

public class LiteralRestriction
    implements
    Restriction {

    private static final long          serialVersionUID     = 320;

    private final FieldValue           field;

    private final Evaluator            evaluator;

    private static final Declaration[] requiredDeclarations = new Declaration[0];

    private final LiteralContextEntry  contextEntry;

    public LiteralRestriction(final FieldValue field,
                              final Evaluator evaluator,
                              final FieldExtractor fieldExtractor) {
        this.field = field;
        this.evaluator = evaluator;
        this.contextEntry = new LiteralContextEntry( fieldExtractor );
    }

    public Evaluator getEvaluator() {
        return this.evaluator;
    }

    public FieldValue getField() {
        return this.field;
    }

    public boolean isAllowed(final Extractor extractor,
                             final Object object,
                             final InternalWorkingMemory workingMemoiry) {
        return this.evaluator.evaluate( extractor,
                                        object,
                                        this.field );
    }

    public boolean isAllowedCachedLeft(final ContextEntry context,
                                       final Object object) {
        return this.evaluator.evaluate( ((LiteralContextEntry) context).getFieldExtractor(),
                                        object,
                                        this.field );
    }

    public boolean isAllowedCachedRight(final ReteTuple tuple,
                                        final ContextEntry context) {
        return this.evaluator.evaluate( ((LiteralContextEntry) context).getFieldExtractor(),
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

    public ContextEntry getContextEntry() {
        return this.contextEntry;
    }

    private static class LiteralContextEntry
        implements
        ContextEntry {
        public FieldExtractor extractor;
        public Object         object;
        public ContextEntry   next;

        public LiteralContextEntry(final FieldExtractor extractor) {
            this.extractor = extractor;
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

    }

}