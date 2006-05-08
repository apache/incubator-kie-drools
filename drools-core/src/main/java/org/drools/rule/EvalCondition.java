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
import org.drools.spi.EvalExpression;
import org.drools.spi.Tuple;

public class EvalCondition extends ConditionalElement {
    private EvalExpression             expression;

    private final Declaration[]        requiredDeclarations;

    private static final Declaration[] EMPTY_DECLARATIONS = new Declaration[0];

    public EvalCondition(Declaration[] requiredDeclarations) {
        this( null,
              requiredDeclarations );
    }

    public EvalCondition(EvalExpression eval,
                         Declaration[] requiredDeclarations) {

        this.expression = eval;

        if ( requiredDeclarations == null ) {
            this.requiredDeclarations = EvalCondition.EMPTY_DECLARATIONS;
        } else {
            this.requiredDeclarations = requiredDeclarations;
        }
    }

    public EvalExpression getEvalExpression() {
        return this.expression;
    }

    public void setEvalExpression(EvalExpression expression) {
        this.expression = expression;
    }

    public Declaration[] getRequiredDeclarations() {
        return this.requiredDeclarations;
    }

    public boolean isAllowed(Tuple tuple,
                             WorkingMemory workingMemory) {
        try {
            return expression.evaluate( tuple,
                                        this.requiredDeclarations,
                                        workingMemory );
        } catch ( Exception e ) {
            throw new RuntimeDroolsException( e );
        }
    }

    public Object clone() {
        EvalCondition eval = new EvalCondition( this.expression,
                                                this.requiredDeclarations );
        return eval;
    }    
    
    public int hashCode() {
        return this.expression.hashCode();
    }
    
    public boolean equals(Object object) {
        if ( object == this ){
            return true;
        }
        
        if ( object == null || object.getClass() != EvalCondition.class ) {
            return false;
        }
        
        EvalCondition other = ( EvalCondition ) object;
        
        return this.expression.equals( other.expression );
    }
};