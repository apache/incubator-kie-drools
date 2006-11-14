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

import org.drools.RuntimeDroolsException;
import org.drools.common.InternalFactHandle;
import org.drools.common.InternalWorkingMemory;
import org.drools.reteoo.ReteTuple;
import org.drools.spi.AlphaNodeFieldConstraint;
import org.drools.spi.BetaNodeFieldConstraint;
import org.drools.spi.PredicateExpression;

public class PredicateConstraint
    implements
    BetaNodeFieldConstraint,
    AlphaNodeFieldConstraint {

    /**
     * 
     */
    private static final long          serialVersionUID   = -103424847725754568L;

    private PredicateExpression        expression;

    private final Declaration          declaration;

    private final Declaration[]        requiredDeclarations;

    private final Declaration[]        previousDeclarations;

    private final Declaration[]        localDeclarations;

    private static final Declaration[] EMPTY_DECLARATIONS = new Declaration[0];

    public PredicateConstraint(final PredicateExpression evaluator,
                               final Declaration declaration) {
        this( evaluator,
              declaration,
              null,
              null );
    }

    public PredicateConstraint(final Declaration declaration,
                               final Declaration[] previousDeclarations,
                               final Declaration[] localDeclarations) {
        this( null,
              declaration,
              previousDeclarations,
              localDeclarations );
    }

    public PredicateConstraint(final PredicateExpression expression,
                               final Declaration declaration,
                               final Declaration[] previousDeclarations,
                               final Declaration[] localDeclarations) {

        this.expression = expression;

        this.declaration = declaration;

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
        
        this.requiredDeclarations = new Declaration[ this.previousDeclarations.length + this.localDeclarations.length ];
        System.arraycopy( this.previousDeclarations, 0, this.requiredDeclarations, 0, this.previousDeclarations.length );
        System.arraycopy( this.localDeclarations, 0, this.requiredDeclarations, this.previousDeclarations.length, this.localDeclarations.length );
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

    public void setPredicateExpression(final PredicateExpression expression) {
        this.expression = expression;
    }

    public PredicateExpression getPredicateExpression() {
        return this.expression;
    }

    public String toString() {
        return "[PredicateConstraint previousDeclarations=" + this.previousDeclarations + " localDeclarations=" + this.localDeclarations+ "]";
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

        if ( this.declaration.getColumn().getFactIndex() != other.declaration.getColumn().getFactIndex() ) {
            return false;
        }

        if ( !this.declaration.getExtractor().equals( other.declaration.getExtractor() ) ) {
            return false;
        }

        for ( int i = 0, length = this.previousDeclarations.length; i < length; i++ ) {
            if ( this.previousDeclarations[i].getColumn().getFactIndex() != other.previousDeclarations[i].getColumn().getFactIndex() ) {
                return false;
            }

            if ( !this.previousDeclarations[i].getExtractor().equals( other.previousDeclarations[i].getExtractor() ) ) {
                return false;
            }
        }

        for ( int i = 0, length = this.localDeclarations.length; i < length; i++ ) {
            if ( this.localDeclarations[i].getColumn().getFactIndex() != other.localDeclarations[i].getColumn().getFactIndex() ) {
                return false;
            }

            if ( !this.localDeclarations[i].getExtractor().equals( other.localDeclarations[i].getExtractor() ) ) {
                return false;
            }
        }

        return this.expression.equals( other.expression );
    }

    public ContextEntry getContextEntry() {
        return new PredicateContextEntry();
    }

    public boolean isAllowed(Object object,
                             InternalWorkingMemory workingMemory) {
        try {
            return this.expression.evaluate( object,
                                             null,
                                             declaration,
                                             previousDeclarations,
                                             localDeclarations,
                                             workingMemory );
        } catch ( Exception e ) {
            throw new RuntimeDroolsException( "Exception executing predicate " + this.expression,
                                              e );
        }
    }

    public boolean isAllowedCachedLeft(ContextEntry context,
                                       Object object) {
        try {
            PredicateContextEntry ctx = (PredicateContextEntry) context;
            return this.expression.evaluate( object,
                                             ctx.leftTuple,
                                             declaration,
                                             previousDeclarations,
                                             localDeclarations,
                                             ctx.workingMemory );
        } catch ( Exception e ) {
            throw new RuntimeDroolsException( "Exception executing predicate " + this.expression,
                                              e );
        }
    }

    public boolean isAllowedCachedRight(ReteTuple tuple,
                                        ContextEntry context) {
        try {
            PredicateContextEntry ctx = (PredicateContextEntry) context;
            return this.expression.evaluate( ctx.rightObject,
                                             tuple,
                                             declaration,
                                             previousDeclarations,
                                             localDeclarations,
                                             ctx.workingMemory );
        } catch ( Exception e ) {
            throw new RuntimeDroolsException( "Exception executing predicate " + this.expression,
                                              e );
        }
    }

    public static class PredicateContextEntry
        implements
        ContextEntry {

        private static final long serialVersionUID = 4217315252579887635L;
        
        public ReteTuple             leftTuple;
        public Object                rightObject;
        public InternalWorkingMemory workingMemory;

        private ContextEntry         entry;

        public PredicateContextEntry() {
        }

        public ContextEntry getNext() {
            return this.entry;
        }

        public void setNext(final ContextEntry entry) {
            this.entry = entry;
        }

        public void updateFromFactHandle(InternalWorkingMemory workingMemory,
                                         InternalFactHandle handle) {
            this.workingMemory = workingMemory;
            this.rightObject = handle.getObject();
        }

        public void updateFromTuple(InternalWorkingMemory workingMemory,
                                    ReteTuple tuple) {
            this.workingMemory = workingMemory;
            this.leftTuple = tuple;
        }
    }

}