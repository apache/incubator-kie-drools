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
import org.drools.common.InternalWorkingMemory;
import org.drools.reteoo.ReteTuple;
import org.drools.rule.ReturnValueRestriction.ReturnValueContextEntry;
import org.drools.spi.AlphaNodeFieldConstraint;
import org.drools.spi.BetaNodeFieldConstraint;
import org.drools.spi.Evaluator;
import org.drools.spi.FieldExtractor;
import org.drools.spi.ReturnValueExpression;

public class ReturnValueConstraint
    implements
    BetaNodeFieldConstraint,
    AlphaNodeFieldConstraint {

    /**
     * 
     */
    private static final long            serialVersionUID = 320L;

    private final FieldExtractor         fieldExtractor;
    private final ReturnValueRestriction restriction;

    public ReturnValueConstraint(final FieldExtractor fieldExtractor,
                                 final ReturnValueRestriction restriction) {
        this.fieldExtractor = fieldExtractor;
        this.restriction = restriction;
    }

    public Declaration[] getRequiredDeclarations() {
        return this.restriction.getRequiredDeclarations();
    }

    public void setReturnValueExpression(final ReturnValueExpression expression) {
        this.restriction.setReturnValueExpression( expression );
    }

    public ReturnValueExpression getExpression() {
        return this.restriction.getExpression();
    }

    public Evaluator getEvaluator() {
        return this.restriction.getEvaluator();
    }

    public String toString() {
        return "[ReturnValueConstraint fieldExtractor=" + this.fieldExtractor + " evaluator=" + getEvaluator() + " declarations=" + getRequiredDeclarations() + "]";
    }

    public int hashCode() {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result + this.fieldExtractor.hashCode();
        result = PRIME * result + this.restriction.hashCode();
        return result;
    }

    public boolean equals(final Object object) {
        if ( object == this ) {
            return true;
        }

        if ( object == null || object.getClass() != ReturnValueConstraint.class ) {
            return false;
        }

        final ReturnValueConstraint other = (ReturnValueConstraint) object;

        return this.fieldExtractor.equals( other.fieldExtractor ) && this.restriction.equals( other.restriction );
    }

    public ContextEntry getContextEntry() {
        return this.restriction.getContextEntry();
    }

    public boolean isAllowed(final Object object,
                             final InternalWorkingMemory workingMemory) {
        try {
            return this.restriction.isAllowed( this.fieldExtractor,
                                               object,
                                               null,
                                               workingMemory );
        } catch ( final Exception e ) {
            throw new RuntimeDroolsException( "Exception executing ReturnValue constraint " + this.restriction + " : " + e.getMessage(),
                                              e );
        }
    }

    public boolean isAllowedCachedLeft(final ContextEntry context,
                                       final Object object) {
        try {
            final ReturnValueContextEntry ctx = (ReturnValueContextEntry) context;
            return this.restriction.isAllowed( this.fieldExtractor,
                                               object,
                                               ctx.getTuple(),
                                               ctx.getWorkingMemory() );
        } catch ( final Exception e ) {
            throw new RuntimeDroolsException( "Exception executing ReturnValue constraint " + this.restriction + " : " + e.getMessage(),
                                              e );
        }
    }

    public boolean isAllowedCachedRight(final ReteTuple tuple,
                                        final ContextEntry context) {
        try {
            final ReturnValueContextEntry ctx = (ReturnValueContextEntry) context;
            return this.restriction.isAllowed( this.fieldExtractor,
                                               ctx.getObject(),
                                               tuple,
                                               ctx.getWorkingMemory() );
        } catch ( final Exception e ) {
            throw new RuntimeDroolsException( "Exception executing ReturnValue constraint " + this.restriction + " : " + e.getMessage(),
                                              e );
        }
    }

}