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

import java.util.Arrays;

import org.drools.common.InternalFactHandle;
import org.drools.common.InternalWorkingMemory;
import org.drools.reteoo.ReteTuple;
import org.drools.spi.Evaluator;
import org.drools.spi.Extractor;
import org.drools.spi.FieldExtractor;
import org.drools.spi.Restriction;

public class VariableRestriction
    implements
    Restriction {

    private static final long   serialVersionUID = 320;

    private final Declaration   declaration;

    private final Declaration[] requiredDeclarations;

    private final Evaluator     evaluator;
    
    private final VariableContextEntry contextEntry;

    public VariableRestriction(final FieldExtractor fieldExtractor,
                               final Declaration declaration,
                               final Evaluator evaluator) {
        this.declaration = declaration;
        this.requiredDeclarations = new Declaration[]{declaration};
        this.evaluator = evaluator;
        this.contextEntry = this.createContextEntry( fieldExtractor );
    }

    public Declaration[] getRequiredDeclarations() {
        return this.requiredDeclarations;
    }

    public Evaluator getEvaluator() {
        return this.evaluator;
    }

    public boolean isAllowed(final Extractor extractor,
                             final Object object,
                             final InternalWorkingMemory workingMemoiry) {
        throw new UnsupportedOperationException( "does not support method call isAllowed(Extractor extractor, Object object, InternalWorkingMemory workingMemoiry)" );
    }

    public boolean isAllowedCachedLeft(final ContextEntry context,
                                       final Object object) {
        return this.evaluator.evaluateCachedLeft( (VariableContextEntry) context,
                                                  object );
    }

    public boolean isAllowedCachedRight(final ReteTuple tuple,
                                        final ContextEntry context) {
        return this.evaluator.evaluateCachedRight( (VariableContextEntry) context,
                                                   tuple.get( this.declaration ).getObject() );
    }

    public String toString() {
        return "[VariableRestriction declaration=" + this.declaration + "]";
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result + ((this.declaration == null) ? 0 : this.declaration.hashCode());
        result = PRIME * result + ((this.evaluator == null) ? 0 : this.evaluator.hashCode());
        result = PRIME * result + this.requiredDeclarations[0].hashCode();
        return result;
    }

    public boolean equals(final Object object) {
        if ( this == object ) {
            return true;
        }

        if ( object == null || getClass() != object.getClass() ) {
            return false;
        }

        final VariableRestriction other = (VariableRestriction) object;

        return this.declaration.equals( other.declaration ) && this.evaluator.equals( other.evaluator ) && Arrays.equals( this.requiredDeclarations,
                                                                                                                          other.requiredDeclarations );
    }
    
    private final VariableContextEntry createContextEntry(FieldExtractor fieldExtractor) {
        final Class classType = fieldExtractor.getValueType().getClassType();
        if ( classType.isPrimitive() ) {
            if ( classType == Boolean.TYPE ) {
                return new BooleanVariableContextEntry( fieldExtractor,
                                                        this.declaration );
            } else if ( (classType == Double.TYPE) || (classType == Float.TYPE) ) {
                return new DoubleVariableContextEntry( fieldExtractor,
                                                       this.declaration );
            } else {
                return new LongVariableContextEntry( fieldExtractor,
                                                     this.declaration );
            }
        } else {
            return new ObjectVariableContextEntry( fieldExtractor,
                                                   this.declaration );
        }
    }

    public ContextEntry getContextEntry() {
        return this.contextEntry;
    }

    public static abstract class VariableContextEntry
        implements
        ContextEntry {
        public FieldExtractor extractor;
        public Object         object;
        public Declaration    declaration;
        public ReteTuple      reteTuple;
        public ContextEntry   entry;

        public VariableContextEntry(final FieldExtractor extractor,
                                        final Declaration declaration) {
            this.extractor = extractor;
            this.declaration = declaration;
        }

        public ContextEntry getNext() {
            return this.entry;
        }

        public void setNext(final ContextEntry entry) {
            this.entry = entry;
        }

        public FieldExtractor getFieldExtractor() {
            return this.extractor;
        }

        public Object getObject() {
            return this.object;
        }

        public ReteTuple getTuple() {
            return this.reteTuple;
        }

        public Declaration getVariableDeclaration() {
            return this.declaration;
        }

    }

    public static class ObjectVariableContextEntry extends VariableContextEntry {
        public Object left;
        public Object right;

        public ObjectVariableContextEntry(final FieldExtractor extractor,
                                          final Declaration declaration) {
            super( extractor,
                   declaration );
        }

        public void updateFromTuple(final InternalWorkingMemory workingMemory,
                                    final ReteTuple tuple) {
            this.reteTuple = tuple;
            this.left = this.declaration.getExtractor().getValue( tuple.get( this.declaration ).getObject() );
        }

        public void updateFromFactHandle(final InternalWorkingMemory workingMemory,
                                         final InternalFactHandle handle) {
            this.object = handle.getObject();
            this.right = this.extractor.getValue( handle.getObject() );
        }
    }

    public static class LongVariableContextEntry extends VariableContextEntry {
        public long left;
        public long right;

        public LongVariableContextEntry(final FieldExtractor extractor,
                                        final Declaration declaration) {
            super( extractor,
                   declaration );
        }

        public void updateFromTuple(final InternalWorkingMemory workingMemory,
                                    final ReteTuple tuple) {
            this.reteTuple = tuple;
            this.left = this.declaration.getExtractor().getLongValue( tuple.get( this.declaration ).getObject() );
        }

        public void updateFromFactHandle(final InternalWorkingMemory workingMemory,
                                         final InternalFactHandle handle) {
            this.object = handle.getObject();
            this.right = this.extractor.getLongValue( handle.getObject() );
        }
    }

    public static class DoubleVariableContextEntry extends VariableContextEntry {
        public double left;
        public double right;

        public DoubleVariableContextEntry(final FieldExtractor extractor,
                                          final Declaration declaration) {
            super( extractor,
                   declaration );
        }

        public void updateFromTuple(final InternalWorkingMemory workingMemory,
                                    final ReteTuple tuple) {
            this.reteTuple = tuple;
            this.left = this.declaration.getExtractor().getDoubleValue( tuple.get( this.declaration ).getObject() );
        }

        public void updateFromFactHandle(final InternalWorkingMemory workingMemory,
                                         final InternalFactHandle handle) {
            this.object = handle.getObject();
            this.right = this.extractor.getDoubleValue( handle.getObject() );
        }
    }

    public static class BooleanVariableContextEntry extends VariableContextEntry {
        public boolean left;
        public boolean right;

        public BooleanVariableContextEntry(final FieldExtractor extractor,
                                           final Declaration declaration) {
            super( extractor,
                   declaration );
        }

        public void updateFromTuple(final InternalWorkingMemory workingMemory,
                                    final ReteTuple tuple) {
            this.reteTuple = tuple;
            this.left = this.declaration.getExtractor().getBooleanValue( tuple.get( this.declaration ).getObject() );
        }

        public void updateFromFactHandle(final InternalWorkingMemory workingMemory,
                                         final InternalFactHandle handle) {
            this.object = handle.getObject();
            this.right = this.extractor.getBooleanValue( handle.getObject() );
        }
    }

}