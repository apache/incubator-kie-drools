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

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Arrays;

import org.drools.base.ValueType;
import org.drools.common.InternalFactHandle;
import org.drools.common.InternalWorkingMemory;
import org.drools.reteoo.LeftTuple;
import org.drools.spi.AcceptsReadAccessor;
import org.drools.spi.Evaluator;
import org.drools.spi.InternalReadAccessor;
import org.drools.spi.ReadAccessor;
import org.drools.spi.Restriction;
import org.drools.time.Interval;

public class VariableRestriction
    implements
    AcceptsReadAccessor,
    Restriction {

    private static final long    serialVersionUID = 400L;

    private Declaration          declaration;

    private Declaration[]  requiredDeclarations;

    private Evaluator      evaluator;

    private InternalReadAccessor readAccessor;

    public VariableRestriction() {
    }

    public VariableRestriction(final InternalReadAccessor fieldExtractor,
                               final Declaration declaration,
                               final Evaluator evaluator) {
        this.declaration = declaration;
        this.requiredDeclarations = new Declaration[]{declaration};
        this.evaluator = evaluator;
        this.readAccessor = fieldExtractor;
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(declaration);
        out.writeObject(requiredDeclarations);
        out.writeObject(evaluator);
        out.writeObject(readAccessor);
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        declaration = (Declaration) in.readObject();
        requiredDeclarations = (Declaration[]) in.readObject();
        evaluator = (Evaluator) in.readObject();
        readAccessor = (InternalReadAccessor) in.readObject();
    }

    public void setReadAccessor(InternalReadAccessor readAccessor) {
        this.readAccessor = readAccessor;
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

    public boolean isAllowed(final InternalReadAccessor extractor,
                             final InternalFactHandle handle,
                             final InternalWorkingMemory workingMemory,
                             final ContextEntry context ) {
        return this.evaluator.evaluate( workingMemory,
                                        this.readAccessor,
                                        this.evaluator.prepareLeftObject( handle ),
                                        this.declaration.getExtractor(),
                                        this.evaluator.prepareLeftObject( handle ) );
    }

    public boolean isAllowedCachedLeft(final ContextEntry context,
                                       final InternalFactHandle handle) {
        return this.evaluator.evaluateCachedLeft( ((VariableContextEntry) context).workingMemory,
                                                  (VariableContextEntry) context,
                                                  this.evaluator.prepareLeftObject( handle ) );
    }

    public boolean isAllowedCachedRight(final LeftTuple tuple,
                                        final ContextEntry context) {
        return this.evaluator.evaluateCachedRight( ((VariableContextEntry) context).workingMemory,
                                                   (VariableContextEntry) context,
                                                   this.evaluator.prepareLeftObject( tuple.get( this.declaration ) ) );
    }
    
    public boolean isTemporal() {
        return this.evaluator.isTemporal();
    }
    
    public Interval getInterval() {
        return this.evaluator.getInterval();
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
                                                          final InternalReadAccessor fieldExtractor) {
        ValueType coerced = eval.getCoercedValueType();

        if ( coerced.isBoolean() ) {
            return new BooleanVariableContextEntry( fieldExtractor,
                                                    this.declaration,
                                                    this.evaluator );
        } else if ( coerced.isFloatNumber() ) {
            return new DoubleVariableContextEntry( fieldExtractor,
                                                   this.declaration,
                                                   this.evaluator );
        } else if ( coerced.isIntegerNumber() || coerced.isEvent() ) {
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
                                        this.readAccessor );
    }

    public Object clone() {
        return new VariableRestriction( this.readAccessor,
                                        (Declaration) this.declaration.clone(),
                                        this.evaluator );
    }

    public static abstract class VariableContextEntry
        implements
        ContextEntry {
        public InternalReadAccessor  extractor;
        public Evaluator             evaluator;
        public Object                object;
        public Declaration           declaration;
        public LeftTuple             reteTuple;
        public ContextEntry          entry;
        public boolean               leftNull;
        public boolean               rightNull;
        public InternalWorkingMemory workingMemory;

        public VariableContextEntry() {
        }

        public VariableContextEntry(final InternalReadAccessor extractor,
                                    final Declaration declaration,
                                    final Evaluator evaluator) {
            this.extractor = extractor;
            this.declaration = declaration;
            this.evaluator = evaluator;
        }

        public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
            workingMemory   = (InternalWorkingMemory)in.readObject();
            extractor       = (InternalReadAccessor)in.readObject();
            evaluator       = (Evaluator)in.readObject();
            object          = in.readObject();
            declaration     = (Declaration)in.readObject();
            reteTuple       = (LeftTuple)in.readObject();
            entry           = (ContextEntry)in.readObject();
            leftNull        = in.readBoolean();
            rightNull       = in.readBoolean();
        }

        public void writeExternal(ObjectOutput out) throws IOException {
            out.writeObject(workingMemory);
            out.writeObject(extractor);
            out.writeObject(evaluator);
            out.writeObject(object);
            out.writeObject(declaration);
            out.writeObject(reteTuple);
            out.writeObject(entry);
            out.writeBoolean(leftNull);
            out.writeBoolean(rightNull);
        }

        public ContextEntry getNext() {
            return this.entry;
        }

        public void setNext(final ContextEntry entry) {
            this.entry = entry;
        }

        public ReadAccessor getFieldExtractor() {
            return this.extractor;
        }

        public Object getObject() {
            return this.object;
        }

        public LeftTuple getTuple() {
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

        public ObjectVariableContextEntry() {
        }

        public ObjectVariableContextEntry(final InternalReadAccessor extractor,
                                          final Declaration declaration,
                                          final Evaluator evaluator) {
            super( extractor,
                   declaration,
                   evaluator );
        }

        public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
            super.readExternal(in);
            left    = in.readObject();
            right   = in.readObject();
        }

        public void writeExternal(ObjectOutput out) throws IOException {
            super.writeExternal(out);
            out.writeObject(left);
            out.writeObject(right);
        }

        public void updateFromTuple(final InternalWorkingMemory workingMemory,
                                    final LeftTuple tuple) {
            this.reteTuple = tuple;
            this.workingMemory = workingMemory;
            this.leftNull = this.declaration.getExtractor().isNullValue( workingMemory,
                                                                         evaluator.prepareLeftObject( tuple.get( this.declaration ) ) );
            this.left = this.declaration.getExtractor().getValue( workingMemory,
                                                                  evaluator.prepareLeftObject( tuple.get( this.declaration ) ) );
        }

        public void updateFromFactHandle(final InternalWorkingMemory workingMemory,
                                         final InternalFactHandle handle) {
            this.object = evaluator.prepareLeftObject( handle );
            this.workingMemory = workingMemory;
            this.rightNull = this.extractor.isNullValue( workingMemory,
                                                         evaluator.prepareRightObject( handle ) );
            this.right = this.extractor.getValue( workingMemory,
                                                  evaluator.prepareRightObject( handle ) );
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
    
    public static class PrimitiveArrayVariableContextEntry extends VariableContextEntry {

        private static final long serialVersionUID = 400L;
        public Object             left;
        public Object             right;

        public PrimitiveArrayVariableContextEntry() {
        }

        public PrimitiveArrayVariableContextEntry(final InternalReadAccessor extractor,
                                          final Declaration declaration,
                                          final Evaluator evaluator) {
            super( extractor,
                   declaration,
                   evaluator );
        }

        public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
            super.readExternal(in);
            left    = in.readObject();
            right   = in.readObject();
        }

        public void writeExternal(ObjectOutput out) throws IOException {
            super.writeExternal(out);
            out.writeObject(left);
            out.writeObject(right);
        }

        public void updateFromTuple(final InternalWorkingMemory workingMemory,
                                    final LeftTuple tuple) {
            this.reteTuple = tuple;
            this.workingMemory = workingMemory;
            this.leftNull = this.declaration.getExtractor().isNullValue( workingMemory,
                                                                         evaluator.prepareLeftObject( tuple.get( this.declaration ) ) );
            this.left = this.declaration.getExtractor().getValue( workingMemory,
                                                                  evaluator.prepareLeftObject( tuple.get( this.declaration ) ) );
        }

        public void updateFromFactHandle(final InternalWorkingMemory workingMemory,
                                         final InternalFactHandle handle) {
            this.object = evaluator.prepareLeftObject( handle );
            this.workingMemory = workingMemory;
            this.rightNull = this.extractor.isNullValue( workingMemory,
                                                         evaluator.prepareRightObject( handle ) );
            this.right = this.extractor.getValue( workingMemory,
                                                  evaluator.prepareRightObject( handle ) );
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

        public LongVariableContextEntry() {
        }

        public LongVariableContextEntry(final InternalReadAccessor extractor,
                                        final Declaration declaration,
                                        final Evaluator evaluator) {
            super( extractor,
                   declaration,
                   evaluator );
        }

        public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
            super.readExternal(in);
            left    = in.readLong();
            right   = in.readLong();
        }

        public void writeExternal(ObjectOutput out) throws IOException {
            super.writeExternal(out);
            out.writeLong(left);
            out.writeLong(right);
        }

        public void updateFromTuple(final InternalWorkingMemory workingMemory,
                                    final LeftTuple tuple) {
            this.reteTuple = tuple;
            this.workingMemory = workingMemory;
            this.leftNull = this.declaration.getExtractor().isNullValue( workingMemory,
                                                                         evaluator.prepareLeftObject( tuple.get( this.declaration ) ) );

            if ( !leftNull ) {
                this.left = this.declaration.getExtractor().getLongValue( workingMemory,
                                                                          evaluator.prepareLeftObject( tuple.get( this.declaration ) ) );
            } else {
                this.left = 0;
            }
        }

        public void updateFromFactHandle(final InternalWorkingMemory workingMemory,
                                         final InternalFactHandle handle) {
            this.object = evaluator.prepareLeftObject( handle );
            this.workingMemory = workingMemory;
            this.rightNull = this.extractor.isNullValue( workingMemory,
                                                         evaluator.prepareRightObject( handle ) );

            if ( !rightNull ) { // avoid a NullPointerException
                this.right = this.extractor.getLongValue( workingMemory,
                                                          evaluator.prepareRightObject( handle ) );
            } else {
                this.right = 0;
            }
        }
    }

    public static class CharVariableContextEntry extends VariableContextEntry {

        private static final long serialVersionUID = 400L;

        public char               left;
        public char               right;

        public CharVariableContextEntry() {
        }

        public CharVariableContextEntry(final InternalReadAccessor extractor,
                                        final Declaration declaration,
                                        final Evaluator evaluator) {
            super( extractor,
                   declaration,
                   evaluator );
        }

        public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
            super.readExternal(in);
            left    = in.readChar();
            right   = in.readChar();
        }

        public void writeExternal(ObjectOutput out) throws IOException {
            super.writeExternal(out);
            out.writeChar(left);
            out.writeChar(right);
        }

        public void updateFromTuple(final InternalWorkingMemory workingMemory,
                                    final LeftTuple tuple) {
            this.reteTuple = tuple;
            this.workingMemory = workingMemory;
            this.leftNull = this.declaration.getExtractor().isNullValue( workingMemory,
                                                                         evaluator.prepareLeftObject( tuple.get( this.declaration ) ) );

            if ( !leftNull ) {
                this.left = this.declaration.getExtractor().getCharValue( workingMemory,
                                                                          evaluator.prepareLeftObject( tuple.get( this.declaration ) ) );
            } else {
                this.left = 0;
            }
        }

        public void updateFromFactHandle(final InternalWorkingMemory workingMemory,
                                         final InternalFactHandle handle) {
            this.object = evaluator.prepareLeftObject( handle );
            this.workingMemory = workingMemory;
            this.rightNull = this.extractor.isNullValue( workingMemory,
                                                         evaluator.prepareRightObject( handle ) );

            if ( !rightNull ) { // avoid a NullPointerException
                this.right = this.extractor.getCharValue( workingMemory,
                                                          evaluator.prepareRightObject( handle ) );
            } else {
                this.right = 0;
            }
        }
    }

    public static class DoubleVariableContextEntry extends VariableContextEntry {

        private static final long serialVersionUID = 400L;

        public double             left;
        public double             right;

        public DoubleVariableContextEntry() {
        }

        public DoubleVariableContextEntry(final InternalReadAccessor extractor,
                                          final Declaration declaration,
                                          final Evaluator evaluator) {
            super( extractor,
                   declaration,
                   evaluator );
        }

        public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
            super.readExternal(in);
            left    = in.readDouble();
            right   = in.readDouble();
        }

        public void writeExternal(ObjectOutput out) throws IOException {
            super.writeExternal(out);
            out.writeDouble(left);
            out.writeDouble(right);
        }

        public void updateFromTuple(final InternalWorkingMemory workingMemory,
                                    final LeftTuple tuple) {
            this.reteTuple = tuple;
            this.workingMemory = workingMemory;
            this.leftNull = this.declaration.getExtractor().isNullValue( workingMemory,
                                                                         evaluator.prepareLeftObject( tuple.get( this.declaration ) ) );

            if ( !leftNull ) {
                this.left = this.declaration.getExtractor().getDoubleValue( workingMemory,
                                                                            evaluator.prepareLeftObject( tuple.get( this.declaration ) ) );
            } else {
                this.left = 0;
            }
        }

        public void updateFromFactHandle(final InternalWorkingMemory workingMemory,
                                         final InternalFactHandle handle) {
            this.object = evaluator.prepareLeftObject( handle );
            this.workingMemory = workingMemory;
            this.rightNull = this.extractor.isNullValue( workingMemory,
                                                         evaluator.prepareRightObject( handle ) );

            if ( !rightNull ) { // avoid a NullPointerException
                this.right = this.extractor.getDoubleValue( workingMemory,
                                                            evaluator.prepareRightObject( handle ) );
            } else {
                this.right = 0;
            }
        }
    }

    public static class BooleanVariableContextEntry extends VariableContextEntry {

        private static final long serialVersionUID = 400L;
        public boolean            left;
        public boolean            right;

        public BooleanVariableContextEntry() {
        }

        public BooleanVariableContextEntry(final InternalReadAccessor extractor,
                                           final Declaration declaration,
                                           final Evaluator evaluator) {
            super( extractor,
                   declaration,
                   evaluator );
        }

        public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
            super.readExternal(in);
            left    = in.readBoolean();
            right   = in.readBoolean();
        }

        public void writeExternal(ObjectOutput out) throws IOException {
            super.writeExternal(out);
            out.writeBoolean(left);
            out.writeBoolean(right);
        }

        public void updateFromTuple(final InternalWorkingMemory workingMemory,
                                    final LeftTuple tuple) {
            this.reteTuple = tuple;
            this.workingMemory = workingMemory;
            this.leftNull = this.declaration.getExtractor().isNullValue( workingMemory,
                                                                         evaluator.prepareLeftObject( tuple.get( this.declaration ) ) );

            if ( !leftNull ) {
                this.left = this.declaration.getExtractor().getBooleanValue( workingMemory,
                                                                             evaluator.prepareLeftObject( tuple.get( this.declaration ) ) );
            } else {
                this.left = false;
            }
        }

        public void updateFromFactHandle(final InternalWorkingMemory workingMemory,
                                         final InternalFactHandle handle) {
            this.object = evaluator.prepareLeftObject( handle );
            this.workingMemory = workingMemory;
            this.rightNull = this.extractor.isNullValue( workingMemory,
                                                         evaluator.prepareRightObject( handle ) );

            if ( !rightNull ) { // avoid a NullPointerException
                this.right = this.extractor.getBooleanValue( workingMemory,
                                                             evaluator.prepareRightObject( handle ) );
            } else {
                this.right = false;
            }
        }
    }
}