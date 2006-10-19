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

import java.util.Arrays;

import org.drools.common.InternalWorkingMemory;
import org.drools.reteoo.ReteTuple;
import org.drools.rule.VariableConstraint.VariableContextEntry;
import org.drools.spi.Evaluator;
import org.drools.spi.Extractor;
import org.drools.spi.Restriction;

public class VariableRestriction
    implements
    Restriction {

    /**
     * 
     */
    private static final long   serialVersionUID = 320;

    private final Declaration   declaration;

    private final Declaration[] requiredDeclarations;

    private final Evaluator     evaluator;

    public VariableRestriction(final Declaration declaration,
                               final Evaluator evaluator) {
        this.declaration = declaration;
        this.requiredDeclarations = new Declaration[]{declaration};
        this.evaluator = evaluator;
    }

    public Declaration[] getRequiredDeclarations() {
        return this.requiredDeclarations;
    }

    public Evaluator getEvaluator() {
        return this.evaluator;
    }

    public boolean isAllowedCachedLeft(final ContextEntry context,
                                       final Object object) {
        //        return this.evaluator.evaluate( ((VariableContextEntry) context).left, 
        //                                        ((VariableContextEntry) context).extractor,
        //                                        object );        
        return this.evaluator.evaluateCachedLeft( (VariableContextEntry) context,
                                                  object );
    }

    public boolean isAllowedCachedRight(final ReteTuple tuple,
                                        final ContextEntry context) {
        //        return this.evaluator.evaluate( this.declaration.getExtractor(),
        //                                        tuple.get( this.declaration ).getObject(), 
        //                                        ((VariableContextEntry) context).right );        
        return this.evaluator.evaluateCachedRight( (VariableContextEntry) context,
                                                   tuple.get( this.declaration ).getObject() );
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

    public boolean isAllowed(final Extractor extractor,
                             final Object object,
                             final InternalWorkingMemory workingMemoiry) {
        throw new UnsupportedOperationException( "does not support method  call  isAllowed(Object object, InternalWorkingMemory workingMemoiry)" );
    }
}