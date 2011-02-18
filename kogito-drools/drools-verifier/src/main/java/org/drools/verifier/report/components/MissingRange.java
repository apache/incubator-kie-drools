/*
 * Copyright 2010 JBoss Inc
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

package org.drools.verifier.report.components;

import org.drools.base.evaluators.Operator;
import org.drools.verifier.components.Field;

public abstract class MissingRange
    implements
    Comparable<MissingRange>,
    Reason,
    Cause {

    private static int       index = 0;
    protected final String   guid  = String.valueOf( index++ );

    protected final Field    field;
    protected final Operator operator;

    public MissingRange(Field field,
                        Operator operator) {
        this.field = field;
        this.operator = operator;
    }

    public ReasonType getReasonType() {
        return ReasonType.MISSING_VALUE;
    }

    /**
     * Takes the given operator e, and returns a reversed version of it.
     * 
     * @return operator
     */
    public static Operator getReversedOperator(Operator e) {
        if ( e.equals( Operator.NOT_EQUAL ) ) {
            return Operator.EQUAL;
        } else if ( e.equals( Operator.EQUAL ) ) {
            return Operator.NOT_EQUAL;
        } else if ( e.equals( Operator.GREATER ) ) {
            return Operator.LESS_OR_EQUAL;
        } else if ( e.equals( Operator.LESS ) ) {
            return Operator.GREATER_OR_EQUAL;
        } else if ( e.equals( Operator.GREATER_OR_EQUAL ) ) {
            return Operator.LESS;
        } else if ( e.equals( Operator.LESS_OR_EQUAL ) ) {
            return Operator.GREATER;
        } else {
            return Operator.determineOperator( e.getOperatorString(),
                                               !e.isNegated() );
        }
    }

    public int compareTo(MissingRange another) {
        return this.guid.compareTo( another.getGuid() );
    }

    public String getGuid() {
        return guid;
    }

    public Field getField() {
        return field;
    }

    public Operator getOperator() {
        return operator;
    }

    public abstract String getValueAsString();

}
