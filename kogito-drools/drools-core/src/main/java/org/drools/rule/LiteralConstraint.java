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

import org.drools.common.InternalFactHandle;
import org.drools.common.InternalWorkingMemory;
import org.drools.spi.AlphaNodeFieldConstraint;
import org.drools.spi.Evaluator;
import org.drools.spi.FieldExtractor;
import org.drools.spi.FieldValue;

public class LiteralConstraint
    implements
    AlphaNodeFieldConstraint {

    /**
     * 
     */
    private static final long        serialVersionUID = 400L;

    private final FieldExtractor     extractor;

    private final LiteralRestriction restriction;

    public LiteralConstraint(final FieldExtractor extractor,
                             final Evaluator evaluator,
                             final FieldValue field) {
        this.extractor = extractor;
        this.restriction = new LiteralRestriction( field,
                                                   evaluator,
                                                   extractor );
    }

    public LiteralConstraint(final FieldExtractor extractor,
                             final LiteralRestriction restriction) {
        this.extractor = extractor;
        this.restriction = restriction;
    }

    public Evaluator getEvaluator() {
        return this.restriction.getEvaluator();
    }

    public FieldValue getField() {
        return this.restriction.getField();
    }

    public FieldExtractor getFieldExtractor() {
        return this.extractor;
    }

    /**
     * Literal constraints cannot have required declarations, so always return an empty array.
     * @return
     *      Return an empty <code>Declaration[]</code>
     */
    public Declaration[] getRequiredDeclarations() {
        return this.restriction.getRequiredDeclarations();
    }

    public void replaceDeclaration(Declaration oldDecl,
                                   Declaration newDecl) {
        this.restriction.replaceDeclaration( oldDecl,
                                             newDecl );
    }

    public boolean isAllowed(final InternalFactHandle handle,
                             final InternalWorkingMemory workingMemory) {
        return this.restriction.isAllowed( this.extractor,
                                           handle,
                                           workingMemory );
    }

    public String toString() {
        return "[LiteralConstraint fieldExtractor=" + this.extractor + " evaluator=" + getEvaluator() + " value=" + getField() + "]";
    }

    public int hashCode() {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result + this.extractor.hashCode();
        result = PRIME * result + this.restriction.hashCode();
        return result;
    }

    public boolean equals(final Object object) {
        if ( this == object ) {
            return true;
        }
        if ( object == null || object.getClass() != LiteralConstraint.class ) {
            return false;
        }
        final LiteralConstraint other = (LiteralConstraint) object;

        return this.extractor.equals( other.extractor ) && this.restriction.equals( other.restriction );
    }

    public Object clone() {
        return new LiteralConstraint( this.extractor,
                                      this.getEvaluator(),
                                      this.getField() );
    }
}