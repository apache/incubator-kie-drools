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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.drools.RuntimeDroolsException;
import org.drools.common.InternalFactHandle;
import org.drools.common.InternalWorkingMemory;
import org.drools.reteoo.LeftTuple;
import org.drools.spi.CompiledInvoker;
import org.drools.spi.Evaluator;
import org.drools.spi.InternalReadAccessor;
import org.drools.spi.PredicateExpression;
import org.drools.spi.Restriction;
import org.drools.spi.Wireable;

/**
 * A predicate can be written as a top level constraint or be nested
 * inside inside a field constraint (and as so, must implement the
 * Restriction interface).
 */
public class PredicateConstraint extends MutableTypeConstraint
    implements
    Restriction,
    Wireable,
    Externalizable {

    private static final long          serialVersionUID   = 510l;

    private PredicateExpression        expression;

    private Declaration[]              requiredDeclarations;

    private Declaration[]              previousDeclarations;

    private Declaration[]              localDeclarations;

    private String[]                   requiredGlobals;
    
    private String[]                   requiredOperators;

    private List<PredicateConstraint>  cloned             = Collections.<PredicateConstraint> emptyList();

    private static final Declaration[] EMPTY_DECLARATIONS = new Declaration[0];
    private static final String[]      EMPTY_STRINGS      = new String[0];

    public PredicateConstraint() {
        this( null );
    }

    public PredicateConstraint(final PredicateExpression evaluator) {
        this( evaluator,
              null,
              null,
              null,
              null );
    }

    public PredicateConstraint(final Declaration[] previousDeclarations,
                               final Declaration[] localDeclarations) {
        this( null,
              previousDeclarations,
              localDeclarations,
              null,
              null );
    }

    public PredicateConstraint(final PredicateExpression expression,
                               final Declaration[] previousDeclarations,
                               final Declaration[] localDeclarations,
                               final String[] requiredGlobals,
                               final String[] requiredOperators ) {

        this.expression = expression;

        if ( previousDeclarations == null ) {
            this.previousDeclarations = PredicateConstraint.EMPTY_DECLARATIONS;
        } else {
            this.previousDeclarations = previousDeclarations;
        }

        if ( localDeclarations == null ) {
            this.localDeclarations = PredicateConstraint.EMPTY_DECLARATIONS;
        } else {
            this.localDeclarations = localDeclarations;
        }

        if ( requiredGlobals == null ) {
            this.requiredGlobals = PredicateConstraint.EMPTY_STRINGS;
        } else {
            this.requiredGlobals = requiredGlobals;
        }

        if ( requiredOperators == null ) {
            this.requiredOperators = PredicateConstraint.EMPTY_STRINGS;
        } else {
            this.requiredOperators = requiredOperators;
        }

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
        super.readExternal( in );
        this.expression = (PredicateExpression) in.readObject();
        this.requiredDeclarations = (Declaration[]) in.readObject();
        this.previousDeclarations = (Declaration[]) in.readObject();
        this.localDeclarations = (Declaration[]) in.readObject();
        this.requiredGlobals = (String[]) in.readObject();
        this.requiredOperators = (String[]) in.readObject();
        this.cloned = (List<PredicateConstraint>) in.readObject();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal( out );
        if ( this.expression instanceof CompiledInvoker ) {
            out.writeObject( null );
        } else {
            out.writeObject( this.expression );
        }
        out.writeObject( this.requiredDeclarations );
        out.writeObject( this.previousDeclarations );
        out.writeObject( this.localDeclarations );
        out.writeObject( this.requiredGlobals );
        out.writeObject( this.requiredOperators );
        out.writeObject( this.cloned );
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
    }

    public void wire(Object object) {
        setPredicateExpression( (PredicateExpression) object );
        for ( PredicateConstraint clone : this.cloned ) {
            clone.wire( object );
        }
    }

    public void setPredicateExpression(final PredicateExpression expression) {
        this.expression = expression;
    }

    public PredicateExpression getPredicateExpression() {
        return this.expression;
    }
    
    public boolean isTemporal() {
        return false;
    }

    public String toString() {
        return "[PredicateConstraint previousDeclarations=" + this.previousDeclarations + " localDeclarations=" + this.localDeclarations + "]";
    }

    public int hashCode() {
        return this.expression.hashCode();
    }

    public boolean equals(final Object object) {
        if ( object == this ) {
            return true;
        }

        if ( object == null || object.getClass() != PredicateConstraint.class ) {
            return false;
        }

        final PredicateConstraint other = (PredicateConstraint) object;

        if ( this.previousDeclarations.length != other.previousDeclarations.length ) {
            return false;
        }

        if ( this.localDeclarations.length != other.localDeclarations.length ) {
            return false;
        }

        if ( this.requiredGlobals.length != other.requiredGlobals.length ) {
            return false;
        }

        if ( this.requiredOperators.length != other.requiredOperators.length ) {
            return false;
        }

        for ( int i = 0, length = this.previousDeclarations.length; i < length; i++ ) {
            if ( this.previousDeclarations[i].getPattern().getOffset() != other.previousDeclarations[i].getPattern().getOffset() ) {
                return false;
            }

            if ( !this.previousDeclarations[i].getExtractor().equals( other.previousDeclarations[i].getExtractor() ) ) {
                return false;
            }
        }

        for ( int i = 0, length = this.localDeclarations.length; i < length; i++ ) {
            if ( this.localDeclarations[i].getPattern().getOffset() != other.localDeclarations[i].getPattern().getOffset() ) {
                return false;
            }

            if ( !this.localDeclarations[i].getExtractor().equals( other.localDeclarations[i].getExtractor() ) ) {
                return false;
            }
        }

        if ( !Arrays.equals( this.requiredGlobals,
                             other.requiredGlobals ) ) {
            return false;
        }

        if ( !Arrays.equals( this.requiredOperators,
                             other.requiredOperators ) ) {
            return false;
        }

        return this.expression.equals( other.expression );
    }

    public ContextEntry createContextEntry() {
        PredicateContextEntry ctx = new PredicateContextEntry();
        ctx.dialectContext = this.expression.createContext();
        return ctx;
    }

    public boolean isAllowed(final InternalFactHandle handle,
                             final InternalWorkingMemory workingMemory,
                             final ContextEntry ctx) {
        try {
            return this.expression.evaluate( handle.getObject(),
                                             null,
                                             this.previousDeclarations,
                                             this.localDeclarations,
                                             workingMemory,
                                             ((PredicateContextEntry) ctx).dialectContext );
        } catch ( final Exception e ) {
            throw new RuntimeDroolsException( "Exception executing predicate " + this.expression,
                                              e );
        }
    }

    public boolean isAllowed(InternalReadAccessor extractor,
                             InternalFactHandle handle,
                             InternalWorkingMemory workingMemory,
                             ContextEntry context) {
        throw new UnsupportedOperationException( "Method not supported. Please contact development team." );
    }

    public boolean isAllowedCachedLeft(final ContextEntry context,
                                       final InternalFactHandle handle) {
        try {
            final PredicateContextEntry ctx = (PredicateContextEntry) context;
            return this.expression.evaluate( handle.getObject(),
                                             ctx.leftTuple,
                                             this.previousDeclarations,
                                             this.localDeclarations,
                                             ctx.workingMemory,
                                             ctx.dialectContext );
        } catch ( final Exception e ) {
            throw new RuntimeDroolsException( "Exception executing predicate " + this.expression,
                                              e );
        }
    }

    public boolean isAllowedCachedRight(final LeftTuple tuple,
                                        final ContextEntry context) {
        try {
            final PredicateContextEntry ctx = (PredicateContextEntry) context;
            return this.expression.evaluate( ctx.rightObject,
                                             tuple,
                                             this.previousDeclarations,
                                             this.localDeclarations,
                                             ctx.workingMemory,
                                             ctx.dialectContext );
        } catch ( final Exception e ) {
            throw new RuntimeDroolsException( "Exception executing predicate " + this.expression,
                                              e );
        }
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

        PredicateConstraint clone = new PredicateConstraint( this.expression,
                                                             previous,
                                                             local,
                                                             this.requiredGlobals,
                                                             this.requiredOperators );

        if ( this.cloned == Collections.EMPTY_LIST ) {
            this.cloned = new ArrayList<PredicateConstraint>( 1 );
        }

        this.cloned.add( clone );

        return clone;

    }

    public static class PredicateContextEntry
        implements
        ContextEntry {

        private static final long    serialVersionUID = 510l;

        public LeftTuple             leftTuple;
        public Object                rightObject;
        public InternalWorkingMemory workingMemory;

        public Object                dialectContext;

        private ContextEntry         entry;

        public PredicateContextEntry() {
        }

        public void readExternal(ObjectInput in) throws IOException,
                                                ClassNotFoundException {
            leftTuple = (LeftTuple) in.readObject();
            rightObject = in.readObject();
            workingMemory = (InternalWorkingMemory) in.readObject();
            dialectContext = in.readObject();
            entry = (ContextEntry) in.readObject();
        }

        public void writeExternal(ObjectOutput out) throws IOException {
            out.writeObject( leftTuple );
            out.writeObject( rightObject );
            out.writeObject( workingMemory );
            out.writeObject( dialectContext );
            out.writeObject( entry );
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
            this.rightObject = handle.getObject();
        }

        public void updateFromTuple(final InternalWorkingMemory workingMemory,
                                    final LeftTuple tuple) {
            this.workingMemory = workingMemory;
            this.leftTuple = tuple;
        }

        public void resetTuple() {
            this.leftTuple = null;
        }

        public void resetFactHandle() {
            this.rightObject = null;
        }
    }

    public Evaluator getEvaluator() {
        return null;
    }

}
