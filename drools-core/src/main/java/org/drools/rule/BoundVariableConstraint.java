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



import org.drools.FactHandle;
import org.drools.WorkingMemory;
import org.drools.spi.Evaluator;
import org.drools.spi.FieldConstraint;
import org.drools.spi.FieldExtractor;
import org.drools.spi.Tuple;

public class BoundVariableConstraint
    implements
    FieldConstraint {

    private final FieldExtractor fieldExtractor;

    private final Declaration    declaration;

    private final Declaration[]  requiredDeclarations;

    private final Evaluator      evaluator;

    public BoundVariableConstraint(FieldExtractor fieldExtractor,
                                   Declaration declaration,
                                   Evaluator evaluator) {
        this.fieldExtractor = fieldExtractor;
        this.declaration = declaration;
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

    public boolean isAllowed(FactHandle handle,
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
        return evaluator.evaluate( this.fieldExtractor.getValue( workingMemory.getObject( handle ) ),
                                   declaration.getValue( workingMemory.getObject( tuple.get( this.declaration ) ) ) );

    }
}