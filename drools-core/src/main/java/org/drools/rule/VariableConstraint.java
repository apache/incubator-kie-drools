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
import org.drools.reteoo.ReteTuple;
import org.drools.spi.BetaNodeFieldConstraint;
import org.drools.spi.Evaluator;
import org.drools.spi.FieldExtractor;

public class VariableConstraint
    implements
    BetaNodeFieldConstraint {

    /**
     * 
     */
    private static final long         serialVersionUID = 320L;

    private final FieldExtractor      fieldExtractor;
    private final VariableRestriction restriction;

    private static final byte         TYPE_BOOLEAN     = 0;
    private static final byte         TYPE_FLOAT       = 1;
    private static final byte         TYPE_INTEGER     = 2;
    private static final byte         TYPE_OBJECT      = 3;

    public VariableConstraint(final FieldExtractor fieldExtractor,
                              final Declaration declaration,
                              final Evaluator evaluator) {
        this.fieldExtractor = fieldExtractor;
        this.restriction = new VariableRestriction( declaration,
                                                    evaluator );
    }

    public VariableConstraint(final FieldExtractor fieldExtractor,
                              final VariableRestriction restriction) {
        this.fieldExtractor = fieldExtractor;
        this.restriction = restriction;
    }

    public Declaration[] getRequiredDeclarations() {
        return this.restriction.getRequiredDeclarations();
    }

    public FieldExtractor getFieldExtractor() {
        return this.fieldExtractor;
    }

    public Evaluator getEvaluator() {
        return this.restriction.getEvaluator();
    }

    public boolean isAllowedCachedLeft(ContextEntry context,
                                       Object object) {
        return this.restriction.isAllowedCachedLeft( context,
                                                     object );
    }

    public boolean isAllowedCachedRight(ReteTuple tuple,
                                        ContextEntry context) {
        return this.restriction.isAllowedCachedRight( tuple,
                                                      context );
    }

    public String toString() {
        return "[VariableConstraint fieldExtractor=" + this.fieldExtractor + " declaration=" + getRequiredDeclarations() + "]";
    }

    public ContextEntry getContextEntry() {
        Class classType = this.fieldExtractor.getValueType().getClassType();
        if ( classType.isPrimitive() ) {
            if ( classType == Boolean.TYPE ) {
                return new BooleanVariableContextEntry( this.fieldExtractor,
                                                        this.restriction.getRequiredDeclarations()[0] );
            } else if ( (classType == Double.TYPE) || (classType == Float.TYPE) ) {
                return new DoubleVariableContextEntry( this.fieldExtractor,
                                                       this.restriction.getRequiredDeclarations()[0] );
            } else {
                return new LongVariableContextEntry( this.fieldExtractor,
                                                     this.restriction.getRequiredDeclarations()[0] );
            }
        } else {
            return new ObjectVariableContextEntry( this.fieldExtractor,
                                                   this.restriction.getRequiredDeclarations()[0] );
        }
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

    public static abstract class VariableContextEntry
        implements
        ContextEntry {
        public FieldExtractor extractor;
        public Declaration    declaration;
        private ContextEntry  entry;

        public VariableContextEntry(FieldExtractor extractor,
                                    Declaration declaration) {
            this.extractor = extractor;
            this.declaration = declaration;
        }

        public ContextEntry getNext() {
            return this.entry;
        }

        public void setNext(ContextEntry entry) {
            this.entry = entry;
        }
    }

    public static class ObjectVariableContextEntry extends VariableContextEntry {
        public Object left;
        public Object right;

        public ObjectVariableContextEntry(FieldExtractor extractor,
                                          Declaration declaration) {
            super( extractor,
                   declaration );
        }

        public void updateFromTuple(ReteTuple tuple) {
            this.left = this.declaration.getExtractor().getValue( tuple.get( this.declaration ).getObject() );
        }

        public void updateFromFactHandle(InternalFactHandle handle) {
            this.right = this.extractor.getValue( handle.getObject() );
        }
    }

    public static class LongVariableContextEntry extends VariableContextEntry {
        public long left;
        public long right;

        public LongVariableContextEntry(FieldExtractor extractor,
                                        Declaration declaration) {
            super( extractor,
                   declaration );
        }

        public void updateFromTuple(ReteTuple tuple) {
            this.left = this.declaration.getExtractor().getLongValue( tuple.get( this.declaration ).getObject() );
        }

        public void updateFromFactHandle(InternalFactHandle handle) {
            this.right = this.extractor.getLongValue( handle.getObject() );
        }
    }

    public static class DoubleVariableContextEntry extends VariableContextEntry {
        public double left;
        public double right;

        public DoubleVariableContextEntry(FieldExtractor extractor,
                                          Declaration declaration) {
            super( extractor,
                   declaration );
        }

        public void updateFromTuple(ReteTuple tuple) {
            this.left = this.declaration.getExtractor().getDoubleValue( tuple.get( this.declaration ).getObject() );
        }

        public void updateFromFactHandle(InternalFactHandle handle) {
            this.right = this.extractor.getDoubleValue( handle.getObject() );
        }
    }

    public static class BooleanVariableContextEntry extends VariableContextEntry {
        public boolean left;
        public boolean right;

        public BooleanVariableContextEntry(FieldExtractor extractor,
                                           Declaration declaration) {
            super( extractor,
                   declaration );
        }

        public void updateFromTuple(ReteTuple tuple) {
            this.left = this.declaration.getExtractor().getBooleanValue( tuple.get( this.declaration ).getObject() );
        }

        public void updateFromFactHandle(InternalFactHandle handle) {
            this.right = this.extractor.getBooleanValue( handle.getObject() );
        }
    }

}