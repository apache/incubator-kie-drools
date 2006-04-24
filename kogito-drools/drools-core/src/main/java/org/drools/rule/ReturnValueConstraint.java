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
import org.drools.spi.Evaluator;
import org.drools.spi.FieldConstraint;
import org.drools.spi.FieldExtractor;
import org.drools.spi.ReturnValueExpression;
import org.drools.spi.Tuple;

public class ReturnValueConstraint
    implements
    FieldConstraint {

    private final FieldExtractor       fieldExtractor;

    private ReturnValueExpression      returnValueExpression;

    private final Declaration[]        requiredDeclarations;

    private final Evaluator            evaluator;

    private static final Declaration[] noRequiredDeclarations = new Declaration[]{};

    public ReturnValueConstraint(FieldExtractor fieldExtractor,
                                 Declaration[] declarations,
                                 Evaluator evaluator) {
        this( fieldExtractor,
              null,
              declarations,
              evaluator );
    }

    public ReturnValueConstraint(FieldExtractor fieldExtractor,
                                 ReturnValueExpression returnValueExpression,
                                 Declaration[] declarations,
                                 Evaluator evaluator) {
        this.fieldExtractor = fieldExtractor;

        this.returnValueExpression = returnValueExpression;

        if ( declarations != null ) {
            this.requiredDeclarations = declarations;
        } else {
            this.requiredDeclarations = ReturnValueConstraint.noRequiredDeclarations;
        }

        this.evaluator = evaluator;
    }

    public Declaration[] getRequiredDeclarations() {
        return this.requiredDeclarations;
    }

    public void setReturnValueExpression(ReturnValueExpression expression) {
        this.returnValueExpression = expression;
    }

    public ReturnValueExpression getReturnValueExpression() {
        return this.returnValueExpression;
    }

    public boolean isAllowed(InternalFactHandle handle,
                             Tuple tuple,
                             WorkingMemory workingMemory) {
        try {
            return evaluator.evaluate( this.fieldExtractor.getValue( handle.getObject() ),
                                       this.returnValueExpression.evaluate( tuple,
                                                                            this.requiredDeclarations,
                                                                            workingMemory ) );
        } catch ( Exception e ) {
            throw new RuntimeDroolsException( e );
        }
    }
}