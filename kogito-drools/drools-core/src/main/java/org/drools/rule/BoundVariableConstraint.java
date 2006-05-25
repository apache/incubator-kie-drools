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
import org.drools.common.InstanceEqualsConstraint;
import org.drools.common.InternalFactHandle;
import org.drools.spi.Evaluator;
import org.drools.spi.FieldConstraint;
import org.drools.spi.FieldExtractor;
import org.drools.spi.Tuple;

public class BoundVariableConstraint
    implements
    FieldConstraint {

    private final FieldExtractor fieldExtractor;

    private final Declaration    declaration;
    
    private final int            column;

    private final Declaration[]  requiredDeclarations;

    private final Evaluator      evaluator;

    public BoundVariableConstraint(FieldExtractor fieldExtractor,
                                   Declaration declaration,
                                   Evaluator evaluator) {
        this.fieldExtractor = fieldExtractor;
        this.declaration = declaration;
        this.column = declaration.getColumn();
        this.requiredDeclarations = new Declaration[]{declaration};
        this.evaluator = evaluator;
    }

    public Declaration[] getRequiredDeclarations() {
        return this.requiredDeclarations;
    }

    public FieldExtractor getFieldExtractor() {
        return this.fieldExtractor;
    }

    public Evaluator getEvaluator() {
        return this.evaluator;
    }

    public boolean isAllowed(InternalFactHandle handle,
                             Tuple tuple,
                             WorkingMemory workingMemory) {
        //can't do this as null indexing breaks.        
        //        Object left = workingMemory.getObject( tuple.get( this.declaration ) );
        //        Object right = workingMemory.getObject( handle );
        //        if ( left == right ) {
        //            return  false;
        //        } else {
        //            return evaluator.evaluate( this.fieldExtractor.getValue( right ),
        //                                       declaration.getValue( left ) );                
        //        }
        return evaluator.evaluate( this.fieldExtractor.getValue( handle.getObject() ),
                                   declaration.getValue( tuple.get( this.column ).getObject() ) );
    }
    
    public String toString() {
        return "[BoundVariableConstraint fieldExtractor=" + this.fieldExtractor + " declaration=" + this.declaration + "]";
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result + this.column;
        result = PRIME * result + ((this.declaration == null) ? 0 : this.declaration.hashCode());
        result = PRIME * result + ((this.evaluator == null) ? 0 : this.evaluator.hashCode());
        result = PRIME * result + ((this.fieldExtractor == null) ? 0 : this.fieldExtractor.hashCode());
        result = PRIME * result + Arrays.hashCode( this.requiredDeclarations );
        return result;
    }    
    
    public boolean equals(Object object) {
        if ( this == object ) {
            return true;
        }

        if ( object == null || getClass() != object.getClass() ) {
            return false;
        }
        
        BoundVariableConstraint other = ( BoundVariableConstraint ) object;
         
        return (this.column == other.column) && this.fieldExtractor.equals( other.fieldExtractor ) && this.declaration.equals( other.declaration ) && this.evaluator.equals( other.evaluator ) && Arrays.equals( this.requiredDeclarations,
                                                                                                                                                                                                                 other.requiredDeclarations );       
    }
    
    
}