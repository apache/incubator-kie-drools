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
import org.drools.spi.Evaluator;
import org.drools.spi.FieldExtractor;
import org.drools.spi.ReturnValueExpression;

public class ReturnValueConstraint
    implements
    BetaNodeFieldConstraint {

    /**
     * 
     */
    private static final long            serialVersionUID = -3888281054472597050L;

    private final FieldExtractor         fieldExtractor;
    private final ReturnValueRestriction restriction;

    public ReturnValueConstraint(final FieldExtractor fieldExtractor,
                                 final Declaration[] declarations,
                                 final Evaluator evaluator) {
        this.fieldExtractor = fieldExtractor;
        this.restriction = new ReturnValueRestriction( declarations,
                                                       evaluator );
    }

    public ReturnValueConstraint(final FieldExtractor fieldExtractor,
                                 final ReturnValueExpression expression,
                                 final Declaration[] declarations,
                                 final Evaluator evaluator) {
        this.fieldExtractor = fieldExtractor;
        this.restriction = new ReturnValueRestriction( expression,
                                                       declarations,
                                                       evaluator );
    }

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
        return new ReturnValueContextEntry();
    }

    public boolean isAllowedCachedLeft(ContextEntry context,
                                       Object object) {
        try {
            ReturnValueContextEntry ctx = (ReturnValueContextEntry) context;
            return this.restriction.isAllowed( this.fieldExtractor,
                                               object,
                                               ctx.leftTuple,
                                               ctx.workingMemory );
        } catch ( Exception e ) {
            throw new RuntimeDroolsException( "Exception executing ReturnValue constraint " + this.restriction,
                                              e );
        }
    }

    public boolean isAllowedCachedRight(ReteTuple tuple,
                                        ContextEntry context) {
        try {
            ReturnValueContextEntry ctx = (ReturnValueContextEntry) context;
            return this.restriction.isAllowed( this.fieldExtractor,
                                               ctx.rightObject,
                                               tuple,
                                               ctx.workingMemory );
        } catch ( Exception e ) {
            throw new RuntimeDroolsException( "Exception executing ReturnValue constraint " + this.restriction,
                                              e );
        }
    }

    public static class ReturnValueContextEntry
        implements
        ContextEntry {
        public ReteTuple             leftTuple;
        public Object                rightObject;
        public InternalWorkingMemory workingMemory;

        private ContextEntry         entry;

        public ReturnValueContextEntry() {
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