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

import org.drools.WorkingMemory;
import org.drools.common.InternalFactHandle;
import org.drools.spi.Evaluator;
import org.drools.spi.FieldConstraint;
import org.drools.spi.FieldExtractor;
import org.drools.spi.Tuple;

public class VariableConstraint
    implements
    FieldConstraint {

    /**
     * 
     */
    private static final long         serialVersionUID = 320;

    private final FieldExtractor      fieldExtractor;
    private final VariableRestriction restriction;

    public VariableConstraint(final FieldExtractor fieldExtractor,
                              final Declaration declaration,
                              final Evaluator evaluator) {
        this.fieldExtractor = fieldExtractor;
        this.restriction = new VariableRestriction( declaration,
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

    public FieldExtractor getFieldExtractor() {
        return this.fieldExtractor;
    }

    public Evaluator getEvaluator() {
        return this.restriction.getEvaluator();
    }

    public boolean isAllowed(final InternalFactHandle handle,
                             final Tuple tuple,
                             final WorkingMemory workingMemory) {
        //can't do this as null indexing breaks.        
        //        Object left = workingMemory.getObject( tuple.get( this.declaration ) );
        //        Object right = workingMemory.getObject( handle );
        //        if ( left == right ) {
        //            return  false;
        //        } else {
        //            return evaluator.evaluate( this.fieldExtractor.getValue( right ),
        //                                       declaration.getValue( left ) );                
        //        }
        return this.restriction.isAllowed( this.fieldExtractor.getValue( handle.getObject() ),
                                           handle,
                                           tuple,
                                           workingMemory );
    }

    public String toString() {
        return "[VariableConstraint fieldExtractor=" + this.fieldExtractor + " declaration=" + getRequiredDeclarations() + "]";
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

}