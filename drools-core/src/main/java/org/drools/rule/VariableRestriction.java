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

import org.drools.base.ValueType;
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

    private static final long    serialVersionUID = 400L;

    private Declaration          declaration;

    private final Declaration[]  requiredDeclarations;

    private final Evaluator      evaluator;

    private final FieldExtractor extractor;

    public VariableRestriction(final FieldExtractor fieldExtractor,
                               final Declaration declaration,
                               final Evaluator evaluator) {
        this.declaration = declaration;
        this.requiredDeclarations = new Declaration[]{declaration};
        this.evaluator = evaluator;
        this.extractor = fieldExtractor;
    }

    public Declaration[] getRequiredDeclarations() {
        return this.requiredDeclarations;
    }

    public void replaceDeclaration(Declaration oldDecl,
                                   Declaration newDecl) {
        if ( this.declaration.equals( oldDecl ) ) {
            this.declaration = newDecl;
            this.requiredDeclarations[0] = newDecl;
        }
    }

    public Evaluator getEvaluator() {
        return this.evaluator;
    }

    public boolean isAllowed(final Extractor extractor,
                             final InternalFactHandle handle,
                             final InternalWorkingMemory workingMemory) {
        return this.evaluator.evaluate( workingMemory,
                                        this.extractor,
                                        this.evaluator.prepareObject( handle ),
                                        this.declaration.getExtractor(),
                                        this.evaluator.prepareObject( handle ) );
    }

    public boolean isAllowedCachedLeft(final ContextEntry context,
                                       final InternalFactHandle handle) {
        return this.evaluator.evaluateCachedLeft( ((VariableContextEntry) context).workingMemory,
                                                  (VariableContextEntry) context,
                                                  this.evaluator.prepareObject( handle ) );
    }

    public boolean isAllowedCachedRight(final ReteTuple tuple,
                                        final ContextEntry context) {
        return this.evaluator.evaluateCachedRight( ((VariableContextEntry) context).workingMemory,
                                                   (VariableContextEntry) context,
                                                   this.evaluator.prepareObject( tuple.get( this.declaration ) ) );
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

    private final VariableContextEntry createContextEntry(final Evaluator eval,
                                                          final FieldExtractor fieldExtractor) {
        ValueType coerced = eval.getCoercedValueType();

        if ( coerced.isBoolean() ) {
            return new BooleanVariableContextEntry( fieldExtractor,
                                                    this.declaration,
                                                    this.evaluator );
        } else if ( coerced.isFloatNumber() ) {
            return new DoubleVariableContextEntry( fieldExtractor,
                                                   this.declaration,
                                                   this.evaluator );
        } else if ( coerced.isIntegerNumber() ) {
            return new LongVariableContextEntry( fieldExtractor,
                                                 this.declaration,
                                                 this.evaluator );
        } else if ( coerced.isChar() ) {
            return new CharVariableContextEntry( fieldExtractor,
                                                 this.declaration,
                                                 this.evaluator );
        } else {
            return new ObjectVariableContextEntry( fieldExtractor,
                                                   this.declaration,
                                                   this.evaluator );
        }
    }

    public ContextEntry createContextEntry() {
        return this.createContextEntry( this.evaluator,
                                        this.extractor );
    }

    public Object clone() {
        return new VariableRestriction( this.extractor,
                                        (Declaration) this.declaration.clone(),
                                        this.evaluator );
    }

    public static abstract class VariableContextEntry
        implements
        ContextEntry {
        public FieldExtractor        extractor;
        public Evaluator             evaluator;
        public Object                object;
        public Declaration           declaration;
        public ReteTuple             reteTuple;
        public ContextEntry          entry;
        public boolean               leftNull;
        public boolean               rightNull;
        public InternalWorkingMemory workingMemory;

        public VariableContextEntry(final FieldExtractor extractor,
                                    final Declaration declaration,
                                    final Evaluator evaluator) {
            this.extractor = extractor;
            this.declaration = declaration;
            this.evaluator = evaluator;
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

        public boolean isLeftNull() {
            return this.leftNull;
        }

        public boolean isRightNull() {
            return this.rightNull;
        }

        public void resetTuple() {
            this.reteTuple = null;
        }

        public void resetFactHandle() {
            this.object = null;
        }
    }

    public static class ObjectVariableContextEntry extends VariableContextEntry {

        private static final long serialVersionUID = 400L;
        public Object             left;
        public Object             right;

        public ObjectVariableContextEntry(final FieldExtractor extractor,
                                          final Declaration declaration,
                                          final Evaluator evaluator) {
            super( extractor,
                   declaration,
                   evaluator );
        }

        public void updateFromTuple(final InternalWorkingMemory workingMemory,
                                    final ReteTuple tuple) {
            this.reteTuple = tuple;
            this.workingMemory = workingMemory;
            this.leftNull = this.declaration.getExtractor().isNullValue( workingMemory,
                                                                         evaluator.prepareObject( tuple.get( this.declaration ) ) );
            this.left = this.declaration.getExtractor().getValue( workingMemory,
                                                                  evaluator.prepareObject( tuple.get( this.declaration ) ) );
        }

        public void updateFromFactHandle(final InternalWorkingMemory workingMemory,
                                         final InternalFactHandle handle) {
            this.object = evaluator.prepareObject( handle );
            this.workingMemory = workingMemory;
            this.rightNull = this.extractor.isNullValue( workingMemory,
                                                         evaluator.prepareObject( handle ) );
            this.right = this.extractor.getValue( workingMemory,
                                                  evaluator.prepareObject( handle ) );
        }

        public void resetTuple() {
            this.left = null;
            this.reteTuple = null;
        }

        public void resetFactHandle() {
            this.right = null;
            this.object = null;
        }
    }

    public static class LongVariableContextEntry extends VariableContextEntry {

        private static final long serialVersionUID = 400L;

        public long               left;
        public long               right;

        public LongVariableContextEntry(final FieldExtractor extractor,
                                        final Declaration declaration,
                                        final Evaluator evaluator) {
            super( extractor,
                   declaration,
                   evaluator );
        }

        public void updateFromTuple(final InternalWorkingMemory workingMemory,
                                    final ReteTuple tuple) {
            this.reteTuple = tuple;
            this.workingMemory = workingMemory;
            this.leftNull = this.declaration.getExtractor().isNullValue( workingMemory,
                                                                         evaluator.prepareObject( tuple.get( this.declaration ) ) );

            if ( !leftNull ) {
                this.left = this.declaration.getExtractor().getLongValue( workingMemory,
                                                                          evaluator.prepareObject( tuple.get( this.declaration ) ) );
            } else {
                this.left = 0;
            }
        }

        public void updateFromFactHandle(final InternalWorkingMemory workingMemory,
                                         final InternalFactHandle handle) {
            this.object = evaluator.prepareObject( handle );
            this.workingMemory = workingMemory;
            this.rightNull = this.extractor.isNullValue( workingMemory,
                                                         evaluator.prepareObject( handle ) );

            if ( !rightNull ) { // avoid a NullPointerException
                this.right = this.extractor.getLongValue( workingMemory,
                                                          evaluator.prepareObject( handle ) );
            } else {
                this.right = 0;
            }
        }
    }

    public static class CharVariableContextEntry extends VariableContextEntry {

        private static final long serialVersionUID = 400L;

        public char               left;
        public char               right;

        public CharVariableContextEntry(final FieldExtractor extractor,
                                        final Declaration declaration,
                                        final Evaluator evaluator) {
            super( extractor,
                   declaration,
                   evaluator );
        }

        public void updateFromTuple(final InternalWorkingMemory workingMemory,
                                    final ReteTuple tuple) {
            this.reteTuple = tuple;
            this.workingMemory = workingMemory;
            this.leftNull = this.declaration.getExtractor().isNullValue( workingMemory,
                                                                         evaluator.prepareObject( tuple.get( this.declaration ) ) );

            if ( !leftNull ) {
                this.left = this.declaration.getExtractor().getCharValue( workingMemory,
                                                                          evaluator.prepareObject( tuple.get( this.declaration ) ) );
            } else {
                this.left = 0;
            }
        }

        public void updateFromFactHandle(final InternalWorkingMemory workingMemory,
                                         final InternalFactHandle handle) {
            this.object = evaluator.prepareObject( handle );
            this.workingMemory = workingMemory;
            this.rightNull = this.extractor.isNullValue( workingMemory,
                                                         evaluator.prepareObject( handle ) );

            if ( !rightNull ) { // avoid a NullPointerException
                this.right = this.extractor.getCharValue( workingMemory,
                                                          evaluator.prepareObject( handle ) );
            } else {
                this.right = 0;
            }
        }
    }

    public static class DoubleVariableContextEntry extends VariableContextEntry {

        private static final long serialVersionUID = 400L;

        public double             left;
        public double             right;

        public DoubleVariableContextEntry(final FieldExtractor extractor,
                                          final Declaration declaration,
                                          final Evaluator evaluator) {
            super( extractor,
                   declaration,
                   evaluator );
        }

        public void updateFromTuple(final InternalWorkingMemory workingMemory,
                                    final ReteTuple tuple) {
            this.reteTuple = tuple;
            this.workingMemory = workingMemory;
            this.leftNull = this.declaration.getExtractor().isNullValue( workingMemory,
                                                                         evaluator.prepareObject( tuple.get( this.declaration ) ) );

            if ( !leftNull ) {
                this.left = this.declaration.getExtractor().getDoubleValue( workingMemory,
                                                                            evaluator.prepareObject( tuple.get( this.declaration ) ) );
            } else {
                this.left = 0;
            }
        }

        public void updateFromFactHandle(final InternalWorkingMemory workingMemory,
                                         final InternalFactHandle handle) {
            this.object = evaluator.prepareObject( handle );
            this.workingMemory = workingMemory;
            this.rightNull = this.extractor.isNullValue( workingMemory,
                                                         evaluator.prepareObject( handle ) );

            if ( !rightNull ) { // avoid a NullPointerException
                this.right = this.extractor.getDoubleValue( workingMemory,
                                                            evaluator.prepareObject( handle ) );
            } else {
                this.right = 0;
            }
        }
    }

    public static class BooleanVariableContextEntry extends VariableContextEntry {

        private static final long serialVersionUID = 400L;
        public boolean            left;
        public boolean            right;

        public BooleanVariableContextEntry(final FieldExtractor extractor,
                                           final Declaration declaration,
                                           final Evaluator evaluator) {
            super( extractor,
                   declaration,
                   evaluator );
        }

        public void updateFromTuple(final InternalWorkingMemory workingMemory,
                                    final ReteTuple tuple) {
            this.reteTuple = tuple;
            this.workingMemory = workingMemory;
            this.leftNull = this.declaration.getExtractor().isNullValue( workingMemory,
                                                                         evaluator.prepareObject( tuple.get( this.declaration ) ) );

            if ( !leftNull ) {
                this.left = this.declaration.getExtractor().getBooleanValue( workingMemory,
                                                                             evaluator.prepareObject( tuple.get( this.declaration ) ) );
            } else {
                this.left = false;
            }
        }

        public void updateFromFactHandle(final InternalWorkingMemory workingMemory,
                                         final InternalFactHandle handle) {
            this.object = evaluator.prepareObject( handle );
            this.workingMemory = workingMemory;
            this.rightNull = this.extractor.isNullValue( workingMemory,
                                                         evaluator.prepareObject( handle ) );

            if ( !rightNull ) { // avoid a NullPointerException
                this.right = this.extractor.getBooleanValue( workingMemory,
                                                             evaluator.prepareObject( handle ) );
            } else {
                this.right = false;
            }
        }
    }
}