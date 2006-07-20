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

import org.drools.RuntimeDroolsException;
import org.drools.WorkingMemory;
import org.drools.common.InternalFactHandle;
import org.drools.spi.Evaluator;
import org.drools.spi.FieldConstraint;
import org.drools.spi.FieldExtractor;
import org.drools.spi.Restriction;
import org.drools.spi.ReturnValueExpression;
import org.drools.spi.Tuple;

public class ReturnValueRestriction
    implements
    Restriction {

    private static int hashCode(Object[] array) {
        final int PRIME = 31;
        if ( array == null ) return 0;
        int result = 1;
        for ( int index = 0; index < array.length; index++ ) {
            result = PRIME * result + (array[index] == null ? 0 : array[index].hashCode());
        }
        return result;
    }

    /**
     * 
     */
    private static final long          serialVersionUID       = 320;

    private ReturnValueExpression      expression;

    private final Declaration[]        requiredDeclarations;

    private final Evaluator            evaluator;

    private static final Declaration[] noRequiredDeclarations = new Declaration[]{};

    public ReturnValueRestriction(final Declaration[] declarations,
                                  final Evaluator evaluator) {
        this( null,
              declarations,
              evaluator );
    }

    public ReturnValueRestriction(final ReturnValueExpression returnValueExpression,
                                  final Declaration[] declarations,
                                  final Evaluator evaluator) {
        this.expression = returnValueExpression;

        if ( declarations != null ) {
            this.requiredDeclarations = declarations;
        } else {
            this.requiredDeclarations = ReturnValueRestriction.noRequiredDeclarations;
        }

        this.evaluator = evaluator;
    }

    public Declaration[] getRequiredDeclarations() {
        return this.requiredDeclarations;
    }

    public void setReturnValueExpression(final ReturnValueExpression expression) {
        this.expression = expression;
    }

    public ReturnValueExpression getExpression() {
        return this.expression;
    }

    public Evaluator getEvaluator() {
        return this.evaluator;
    }

    public boolean isAllowed(final Object object,
                             final InternalFactHandle handle,
                             final Tuple tuple,
                             final WorkingMemory workingMemory) {
        try {
            return this.evaluator.evaluate( object,
                                            this.expression.evaluate( tuple,
                                                                      this.requiredDeclarations,
                                                                      workingMemory ) );
        } catch ( final Exception e ) {
            throw new RuntimeDroolsException( e );
        }
    }

    public int hashCode() {
        final int PRIME = 31;
        int result = 1;
        result = this.evaluator.hashCode();
        result = this.expression.hashCode();
        result = PRIME * result + ReturnValueRestriction.hashCode( this.requiredDeclarations );
        return result;
    }

    public boolean equals(Object object) {
        if ( object == this ) {
            return true;
        }

        if ( object == null || object.getClass() != ReturnValueRestriction.class ) {
            return false;
        }

        final ReturnValueRestriction other = (ReturnValueRestriction) object;

        if ( this.requiredDeclarations.length != other.requiredDeclarations.length ) {
            return false;
        }

        if ( !Arrays.equals( this.requiredDeclarations,
                             other.requiredDeclarations ) ) {
            return false;
        }

        return this.expression.equals( other.expression );
    }
}