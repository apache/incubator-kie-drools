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
import org.drools.spi.BetaNodeFieldConstraint;
import org.drools.spi.PredicateExpression;

public class PredicateConstraint
    implements
    BetaNodeFieldConstraint {

    /**
     * 
     */
    private static final long          serialVersionUID   = -103424847725754568L;

    private PredicateExpression        expression;

    private final Declaration          declaration;

    private final Declaration[]        requiredDeclarations;

    private static final Declaration[] EMPTY_DECLARATIONS = new Declaration[0];

    public PredicateConstraint(final PredicateExpression evaluator,
                               final Declaration declaration) {
        this( evaluator,
              declaration,
              null );
    }

    public PredicateConstraint(final Declaration declaration,
                               final Declaration[] requiredDeclarations) {
        this( null,
              declaration,
              requiredDeclarations );
    }

    public PredicateConstraint(final PredicateExpression expression,
                               final Declaration declaration,
                               final Declaration[] requiredDeclarations) {

        this.expression = expression;

        this.declaration = declaration;

        if ( requiredDeclarations == null ) {
            this.requiredDeclarations = PredicateConstraint.EMPTY_DECLARATIONS;
        } else {
            this.requiredDeclarations = requiredDeclarations;
        }
    }

    public Declaration[] getRequiredDeclarations() {
        return this.requiredDeclarations;
    }

    public void setPredicateExpression(final PredicateExpression expression) {
        this.expression = expression;
    }

    public PredicateExpression getPredicateExpression() {
        return this.expression;
    }

    public String toString() {
        return "[PredicateConstraint declarations=" + this.requiredDeclarations + "]";
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

        if ( this.requiredDeclarations.length != other.requiredDeclarations.length ) {
            return false;
        }

        if ( this.declaration.getColumn().getFactIndex() != other.declaration.getColumn().getFactIndex() ) {
            return false;
        }

        if ( !this.declaration.getExtractor().equals( other.declaration.getExtractor() ) ) {
            return false;
        }

        for ( int i = 0, length = this.requiredDeclarations.length; i < length; i++ ) {
            if ( this.requiredDeclarations[i].getColumn().getFactIndex() != other.requiredDeclarations[i].getColumn().getFactIndex() ) {
                return false;
            }

            if ( !this.requiredDeclarations[i].getExtractor().equals( other.requiredDeclarations[i].getExtractor() ) ) {
                return false;
            }
        }

        return this.expression.equals( other.expression );
    }

    public ContextEntry getContextEntry() {
        return new PredicateContextEntry();
    }

    public boolean isAllowedCachedLeft(ContextEntry context,
                                       Object object) {
        try {
            PredicateContextEntry ctx = (PredicateContextEntry) context;
            return this.expression.evaluate( object,
                                      ctx.leftTuple,
                                      declaration,
                                      requiredDeclarations,
                                      ctx.workingMemory );
        } catch ( Exception e ) {
            throw new RuntimeDroolsException("Exception executing predicate "+this.expression, e);
        }
    }

    public boolean isAllowedCachedRight(ReteTuple tuple,
                                        ContextEntry context) {
        try {
            PredicateContextEntry ctx = (PredicateContextEntry) context;
            return this.expression.evaluate( ctx.rightObject,
                                      tuple,
                                      declaration,
                                      requiredDeclarations,
                                      ctx.workingMemory );
        } catch ( Exception e ) {
            throw new RuntimeDroolsException("Exception executing predicate "+this.expression, e);
        }
    }

    public static class PredicateContextEntry
        implements
        ContextEntry {
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