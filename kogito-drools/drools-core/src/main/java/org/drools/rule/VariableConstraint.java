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

import org.drools.common.InternalWorkingMemory;
import org.drools.reteoo.ReteTuple;
import org.drools.spi.AlphaNodeFieldConstraint;
import org.drools.spi.BetaNodeFieldConstraint;
import org.drools.spi.Evaluator;
import org.drools.spi.FieldExtractor;

public class VariableConstraint
    implements
    AlphaNodeFieldConstraint,
    BetaNodeFieldConstraint {

    private static final long         serialVersionUID = 400L;

    private final FieldExtractor      fieldExtractor;
    private final VariableRestriction restriction;

    public VariableConstraint(final FieldExtractor fieldExtractor,
                              final Declaration declaration,
                              final Evaluator evaluator) {
        this.fieldExtractor = fieldExtractor;
        this.restriction = new VariableRestriction( fieldExtractor,
                                                    declaration,
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

    public void replaceDeclaration(Declaration oldDecl,
                                   Declaration newDecl) {
        this.restriction.replaceDeclaration( oldDecl,
                                             newDecl );
    }

    public FieldExtractor getFieldExtractor() {
        return this.fieldExtractor;
    }

    public Evaluator getEvaluator() {
        return this.restriction.getEvaluator();
    }

    public boolean isAllowed(final Object object,
                             final InternalWorkingMemory workingMemory) {
        return this.restriction.isAllowed( this.fieldExtractor,
                                           object,
                                           workingMemory );
    }

    public boolean isAllowedCachedLeft(final ContextEntry context,
                                       final Object object) {
        return this.restriction.isAllowedCachedLeft( context,
                                                     object );
    }

    public boolean isAllowedCachedRight(final ReteTuple tuple,
                                        final ContextEntry context) {
        return this.restriction.isAllowedCachedRight( tuple,
                                                      context );
    }

    public String toString() {
        return "[VariableConstraint fieldExtractor=" + this.fieldExtractor + " declaration=" + getRequiredDeclarations() + "]";
    }

    public ContextEntry getContextEntry() {
        return this.restriction.getContextEntry();
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

    public Object clone() {
        return new VariableConstraint( this.fieldExtractor,
                                       (VariableRestriction) this.restriction.clone() );
    }

}