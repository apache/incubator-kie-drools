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
import org.drools.WorkingMemory;
import org.drools.common.InternalFactHandle;
import org.drools.spi.FieldConstraint;
import org.drools.spi.PredicateExpression;
import org.drools.spi.Tuple;

public class PredicateConstraint
    implements
    FieldConstraint {

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

    public boolean isAllowed(final InternalFactHandle handle,
                             final Tuple tuple,
                             final WorkingMemory workingMemory) {
        try {
            return this.expression.evaluate( tuple,
                                             handle,
                                             this.declaration,
                                             this.requiredDeclarations,
                                             workingMemory );
        } catch ( final Exception e ) {
            throw new RuntimeDroolsException( e );
        }

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
        
    	if ( this.declaration.getColumn() != other.declaration.getColumn() ) {
    		return false;
    	}

    	if ( !this.declaration.getExtractor().equals( other.declaration.getExtractor() ) ) {
    		return false;
    	}        
        
        for ( int i = 0, length = this.requiredDeclarations.length; i < length; i++ ) {
        	if ( this.requiredDeclarations[i].getColumn() != other.requiredDeclarations[i].getColumn() ) {
        		return false;
        	}

        	if ( !this.requiredDeclarations[i].getExtractor().equals( other.requiredDeclarations[i].getExtractor() ) ) {
        		return false;
        	}        	
        }        

        return this.expression.equals( other.expression );
    }
};