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

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.drools.RuntimeDroolsException;
import org.drools.WorkingMemory;
import org.drools.common.InternalFactHandle;
import org.drools.common.InternalWorkingMemory;
import org.drools.reteoo.LeftTuple;
import org.drools.spi.AcceptsReadAccessor;
import org.drools.spi.CompiledInvoker;
import org.drools.spi.Evaluator;
import org.drools.spi.FieldValue;
import org.drools.spi.InternalReadAccessor;
import org.drools.spi.ReadAccessor;
import org.drools.spi.Restriction;
import org.drools.spi.ReturnValueExpression;
import org.drools.spi.Tuple;
import org.drools.spi.Wireable;

public class ReturnValueRestriction
    implements
    Restriction,
    AcceptsReadAccessor,
    Wireable {

    private static final long            serialVersionUID       = 510l;

    private ReturnValueExpression        expression;

    private String[]                     requiredGlobals;

    private Declaration[]                requiredDeclarations;

    private Declaration[]                previousDeclarations;

    private Declaration[]                localDeclarations;

    private Evaluator                    evaluator;

    private InternalReadAccessor         readAccessor;

    private static final Declaration[]   noRequiredDeclarations = new Declaration[]{};

    private static final String[]        noRequiredGlobals      = new String[]{};

    private List<ReturnValueRestriction> cloned                 = Collections.<ReturnValueRestriction> emptyList();

    public ReturnValueRestriction() {

    }

    public ReturnValueRestriction(final InternalReadAccessor fieldExtractor,
                                  final Declaration[] previousDeclarations,
                                  final Declaration[] localDeclarations,
                                  final String[] requiredGlobals,
                                  final Evaluator evaluator) {
        this( fieldExtractor,
              null,
              previousDeclarations,
              localDeclarations,
              requiredGlobals,
              evaluator );
    }

    public ReturnValueRestriction(final InternalReadAccessor fieldExtractor,
                                  final ReturnValueExpression returnValueExpression,
                                  final Declaration[] previousDeclarations,
                                  final Declaration[] localDeclarations,
                                  final String[] requiredGlobals,
                                  final Evaluator evaluator) {
        this.expression = returnValueExpression;
        this.readAccessor = fieldExtractor;

        if ( previousDeclarations != null ) {
            this.previousDeclarations = previousDeclarations;
        } else {
            this.previousDeclarations = ReturnValueRestriction.noRequiredDeclarations;
        }

        if ( localDeclarations != null ) {
            this.localDeclarations = localDeclarations;
        } else {
            this.localDeclarations = ReturnValueRestriction.noRequiredDeclarations;
        }

        if ( requiredGlobals != null ) {
            this.requiredGlobals = requiredGlobals;
        } else {
            this.requiredGlobals = ReturnValueRestriction.noRequiredGlobals;
        }

        this.evaluator = evaluator;

        this.requiredDeclarations = new Declaration[this.previousDeclarations.length + this.localDeclarations.length];
        System.arraycopy( this.previousDeclarations,
                          0,
                          this.requiredDeclarations,
                          0,
                          this.previousDeclarations.length );
        System.arraycopy( this.localDeclarations,
                          0,
                          this.requiredDeclarations,
                          this.previousDeclarations.length,
                          this.localDeclarations.length );
    }

    @SuppressWarnings("unchecked")
    public void readExternal(ObjectInput in) throws IOException,
                                            ClassNotFoundException {
        expression = (ReturnValueExpression) in.readObject();
        requiredGlobals = (String[]) in.readObject();
        requiredDeclarations = (Declaration[]) in.readObject();
        previousDeclarations = (Declaration[]) in.readObject();
        localDeclarations = (Declaration[]) in.readObject();
        evaluator = (Evaluator) in.readObject();
        readAccessor = (InternalReadAccessor) in.readObject();
        this.cloned = (List<ReturnValueRestriction>) in.readObject();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        if ( this.expression instanceof CompiledInvoker ) {
            out.writeObject( null );
        } else {
            out.writeObject( this.expression );
        }
        out.writeObject( requiredGlobals );
        out.writeObject( requiredDeclarations );
        out.writeObject( previousDeclarations );
        out.writeObject( localDeclarations );
        out.writeObject( evaluator );
        out.writeObject( readAccessor );
        out.writeObject( this.cloned );
    }

    public void setReadAccessor(InternalReadAccessor readAccessor) {
        this.readAccessor = readAccessor;
    }

    public Declaration[] getRequiredDeclarations() {
        return this.requiredDeclarations;
    }

    public Declaration[] getPreviousDeclarations() {
        return this.previousDeclarations;
    }

    public Declaration[] getLocalDeclarations() {
        return this.localDeclarations;
    }

    public String[] getRequiredGlobals() {
        return this.requiredGlobals;
    }

    public void replaceDeclaration(Declaration oldDecl,
                                   Declaration newDecl) {
        for ( int i = 0; i < this.requiredDeclarations.length; i++ ) {
            if ( this.requiredDeclarations[i].equals( oldDecl ) ) {
                this.requiredDeclarations[i] = newDecl;
            }
        }
        for ( int i = 0; i < this.previousDeclarations.length; i++ ) {
            if ( this.previousDeclarations[i].equals( oldDecl ) ) {
                this.previousDeclarations[i] = newDecl;
            }
        }
        for ( int i = 0; i < this.localDeclarations.length; i++ ) {
            if ( this.localDeclarations[i].equals( oldDecl ) ) {
                this.localDeclarations[i] = newDecl;
            }
        }
        this.expression.replaceDeclaration( oldDecl,
                                            newDecl );
    }

    public void wire(Object object) {
        setReturnValueExpression( (ReturnValueExpression) object );
        for ( ReturnValueRestriction clone : this.cloned ) {
            clone.wire( object );
        }
    }

    public void setReturnValueExpression(final ReturnValueExpression expression) {
        this.expression = expression;
    }

    public ReturnValueExpression getExpression() {
        return this.expression;
    }

    public Evaluator getEvaluator() {
        return this.evaluator;
    }

    public boolean isTemporal() {
        return this.evaluator.isTemporal();
    }

    public boolean isAllowed(final InternalReadAccessor readAccessor,
                             final InternalFactHandle handle,
                             final Tuple tuple,
                             final WorkingMemory workingMemory,
                             final ContextEntry context) {
        try {
            return this.evaluator.evaluate( (InternalWorkingMemory) workingMemory,
                                            this.readAccessor,
                                            handle.getObject(),
                                            this.expression.evaluate( handle.getObject(),
                                                                      tuple,
                                                                      this.previousDeclarations,
                                                                      this.localDeclarations,
                                                                      workingMemory,
                                                                      ((ReturnValueContextEntry) context).dialectContext ) );
        } catch ( final Exception e ) {
            throw new RuntimeDroolsException( e );
        }
    }

    public boolean isAllowed(final InternalReadAccessor extractor,
                             final InternalFactHandle handle,
                             final InternalWorkingMemory workingMemoiry,
                             final ContextEntry context) {
        throw new UnsupportedOperationException( "does not support method call isAllowed(Object object, InternalWorkingMemory workingMemoiry)" );
    }

    public boolean isAllowedCachedLeft(final ContextEntry context,
                                       final InternalFactHandle handle) {
        try {
            ReturnValueContextEntry ctx = (ReturnValueContextEntry) context;
            FieldValue value = this.expression.evaluate( handle.getObject(),
                                                         ctx.leftTuple,
                                                         this.previousDeclarations,
                                                         this.localDeclarations,
                                                         ctx.workingMemory,
                                                         ctx.dialectContext );
            return this.evaluator.evaluate( ctx.workingMemory,
                                            this.readAccessor,
                                            handle.getObject(),
                                            value );
        } catch ( final Exception e ) {
            throw new RuntimeDroolsException( e );
        }
    }

    public boolean isAllowedCachedRight(final LeftTuple tuple,
                                        final ContextEntry context) {
        try {
            ReturnValueContextEntry ctx = (ReturnValueContextEntry) context;
            FieldValue value = this.expression.evaluate( ctx.handle.getObject(),
                                                         tuple,
                                                         this.previousDeclarations,
                                                         this.localDeclarations,
                                                         ctx.workingMemory,
                                                         ctx.dialectContext );
            return this.evaluator.evaluate( ctx.workingMemory,
                                            this.readAccessor,
                                            ctx.handle.getObject(),
                                            value );
        } catch ( final Exception e ) {
            throw new RuntimeDroolsException( e );
        }
    }

    public int hashCode() {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result + this.evaluator.hashCode();
        result = PRIME * result + ((this.expression != null) ? this.expression.hashCode() : 0);
        result = PRIME * result + ReturnValueRestriction.hashCode( this.localDeclarations );
        result = PRIME * result + ReturnValueRestriction.hashCode( this.previousDeclarations );
        result = PRIME * result + ReturnValueRestriction.hashCode( this.requiredGlobals );
        return result;
    }

    public boolean equals(final Object object) {
        if ( object == this ) {
            return true;
        }

        if ( object == null || object.getClass() != ReturnValueRestriction.class ) {
            return false;
        }

        final ReturnValueRestriction other = (ReturnValueRestriction) object;

        if ( this.localDeclarations.length != other.localDeclarations.length ) {
            return false;
        }

        if ( this.previousDeclarations.length != other.previousDeclarations.length ) {
            return false;
        }

        if ( this.requiredGlobals.length != other.requiredGlobals.length ) {
            return false;
        }

        if ( !Arrays.equals( this.localDeclarations,
                             other.localDeclarations ) ) {
            return false;
        }

        if ( !Arrays.equals( this.previousDeclarations,
                             other.previousDeclarations ) ) {
            return false;
        }

        if ( !Arrays.equals( this.requiredGlobals,
                             other.requiredGlobals ) ) {
            return false;
        }

        return this.evaluator.equals( other.evaluator ) && this.expression.equals( other.expression );
    }

    private static int hashCode(final Object[] array) {
        final int PRIME = 31;
        if ( array == null ) {
            return 0;
        }
        int result = 1;
        for ( int index = 0; index < array.length; index++ ) {
            result = PRIME * result + (array[index] == null ? 0 : array[index].hashCode());
        }
        return result;
    }

    public ContextEntry createContextEntry() {
        ReturnValueContextEntry ctx = new ReturnValueContextEntry( this.readAccessor,
                                                                   this.previousDeclarations,
                                                                   this.localDeclarations );
        ctx.dialectContext = this.expression.createContext();
        return ctx;
    }

    public Object clone() {
        Declaration[] previous = new Declaration[this.previousDeclarations.length];
        for ( int i = 0; i < previous.length; i++ ) {
            previous[i] = (Declaration) this.previousDeclarations[i].clone();
        }

        Declaration[] local = new Declaration[this.localDeclarations.length];
        for ( int i = 0; i < local.length; i++ ) {
            local[i] = (Declaration) this.localDeclarations[i].clone();
        }

        ReturnValueRestriction clone = new ReturnValueRestriction( this.readAccessor,
                                                                   previous,
                                                                   local,
                                                                   this.requiredGlobals,
                                                                   this.evaluator );

        if ( this.cloned == Collections.EMPTY_LIST ) {
            this.cloned = new ArrayList<ReturnValueRestriction>( 1 );
        }

        this.cloned.add( clone );

        return clone;
    }

    public static class ReturnValueContextEntry
        implements
        ContextEntry {

        private static final long    serialVersionUID = 510l;

        public ReadAccessor          fieldExtractor;
        public InternalFactHandle    handle;
        public LeftTuple             leftTuple;
        public InternalWorkingMemory workingMemory;
        public Declaration[]         previousDeclarations;
        public Declaration[]         localDeclarations;

        private ContextEntry         entry;

        public Object                dialectContext;

        public ReturnValueContextEntry() {
        }

        public ReturnValueContextEntry(final ReadAccessor fieldExtractor,
                                       final Declaration[] previousDeclarations,
                                       final Declaration[] localDeclarations) {
            this.fieldExtractor = fieldExtractor;
            this.previousDeclarations = previousDeclarations;
            this.localDeclarations = localDeclarations;
        }

        public void readExternal(ObjectInput in) throws IOException,
                                                ClassNotFoundException {
            fieldExtractor = (ReadAccessor) in.readObject();
            handle = (InternalFactHandle) in.readObject();
            leftTuple = (LeftTuple) in.readObject();
            workingMemory = (InternalWorkingMemory) in.readObject();
            previousDeclarations = (Declaration[]) in.readObject();
            localDeclarations = (Declaration[]) in.readObject();
            entry = (ContextEntry) in.readObject();
            dialectContext = in.readObject();
        }

        public void writeExternal(ObjectOutput out) throws IOException {
            out.writeObject( fieldExtractor );
            out.writeObject( handle );
            out.writeObject( leftTuple );
            out.writeObject( workingMemory );
            out.writeObject( previousDeclarations );
            out.writeObject( localDeclarations );
            out.writeObject( entry );
            out.writeObject( dialectContext );
        }

        public ContextEntry getNext() {
            return this.entry;
        }

        public void setNext(final ContextEntry entry) {
            this.entry = entry;
        }

        public void updateFromFactHandle(final InternalWorkingMemory workingMemory,
                                         final InternalFactHandle handle) {
            this.workingMemory = workingMemory;
            this.handle = handle;
        }

        public void updateFromTuple(final InternalWorkingMemory workingMemory,
                                    final LeftTuple tuple) {
            this.workingMemory = workingMemory;
            this.leftTuple = tuple;
        }

        /* (non-Javadoc)
         * @see org.drools.rule.ReturnValueContextEntry#getFieldExtractor()
         */
        public ReadAccessor getFieldExtractor() {
            return this.fieldExtractor;
        }

        /* (non-Javadoc)
         * @see org.drools.rule.ReturnValueContextEntry#getTuple()
         */
        public LeftTuple getTuple() {
            return this.leftTuple;
        }

        /* (non-Javadoc)
         * @see org.drools.rule.ReturnValueContextEntry#getObject()
         */
        public InternalFactHandle getHandle() {
            return this.handle;
        }

        /* (non-Javadoc)
         * @see org.drools.rule.ReturnValueContextEntry#getRequiredDeclarations()
         */
        public Declaration[] getPreviousDeclarations() {
            return this.previousDeclarations;
        }

        public Declaration[] getLocalDeclarations() {
            return this.localDeclarations;
        }

        /* (non-Javadoc)
         * @see org.drools.rule.ReturnValueContextEntry#getWorkingMemory()
         */
        public InternalWorkingMemory getWorkingMemory() {
            return this.workingMemory;
        }

        public void resetTuple() {
            this.leftTuple = null;
        }

        public void resetFactHandle() {
            this.handle = null;
        }
    }

}
