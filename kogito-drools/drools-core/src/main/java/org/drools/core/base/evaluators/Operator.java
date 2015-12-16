/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
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

package org.drools.core.base.evaluators;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Operator
    implements
    Externalizable, org.kie.api.runtime.rule.Operator {

    private static final long                  serialVersionUID = 510l;

    // a static private cache so that pluggable operator can register their implementations
    // it is automatically initialized with common operator implementations
    private static final Map<String, Operator> CACHE            = Collections.synchronizedMap( new HashMap<String, Operator>() );

    // these static operator constants are kept here just to make it easier for the engine
    // to reference common used operators. The addition of new constants here is not
    // advisable though.
    public static final Operator               EQUAL            = addOperatorToRegistry( "==",
                                                                                         false );
    public static final Operator               NOT_EQUAL        = addOperatorToRegistry( "!=",
                                                                                         false );
    public static final Operator               LESS             = addOperatorToRegistry( "<",
                                                                                         false );
    public static final Operator               LESS_OR_EQUAL    = addOperatorToRegistry( "<=",
                                                                                         false );
    public static final Operator               GREATER          = addOperatorToRegistry( ">",
                                                                                         false );
    public static final Operator               GREATER_OR_EQUAL = addOperatorToRegistry( ">=",
                                                                                         false );

    // Some operators are supported by the runtime and no longer require Drools specific implementations,
    // so we are adding them directly to the cache:
    public static final Operator  CONTAINS      = Operator.addOperatorToRegistry( "contains",
                                                                                  false );
    public static final Operator  NOT_CONTAINS  = Operator.addOperatorToRegistry( "contains",
                                                                                  true );
    public static final Operator  EXCLUDES      = Operator.addOperatorToRegistry( "excludes",
                                                                                  false );
    public static final Operator  NOT_EXCLUDES  = Operator.addOperatorToRegistry( "excludes",
                                                                                  true );
    public static final Operator  MEMBEROF      = Operator.addOperatorToRegistry( "memberOf",
                                                                                  false );
    public static final Operator  NOT_MEMBEROF  = Operator.addOperatorToRegistry( "memberOf",
                                                                                  true );
    public static final Operator  MATCHES       = Operator.addOperatorToRegistry( "matches",
                                                                                  false );
    public static final Operator  NOT_MATCHES   = Operator.addOperatorToRegistry( "matches",
                                                                                  true );
    public static final Operator  SOUNDSLIKE       = Operator.addOperatorToRegistry( "soundslike",
                                                                                     false );
    public static final Operator  NOT_SOUNDSLIKE   = Operator.addOperatorToRegistry( "soundslike",
                                                                                     true );

    /**
     * Creates a new Operator instance for the given parameters,
     * adds it to the registry and return it
     *
     * @param operatorId the identification symbol of the operator
     * @param isNegated true if it is negated
     *
     * @return the newly created operator
     */
    public static Operator addOperatorToRegistry(final String operatorId,
                                                 final boolean isNegated) {
        Operator op = new Operator( operatorId,
                                    isNegated );
        CACHE.put( getKey( operatorId,
                           isNegated ),
                   op );
        return op;
    }

    public static Collection<Operator> getAllOperators() {
        return CACHE.values();
    }

    /**
     * Returns the operator instance for the given parameters
     *
     * @param operatorId the identification symbol of the operator
     * @param isNegated true if it is negated
     *
     * @return the operator in case it exists
     */
    public static Operator determineOperator(final String operatorId,
                                             final boolean isNegated) {
        Operator op = CACHE.get( getKey( operatorId,
                                         isNegated ) );
        return op;
    }

    private static String getKey(final String string,
                                 final boolean isNegated) {
        return isNegated + ":" + string;
    }

    // This class attributes
    private String  operator;
    private boolean isNegated;

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        operator    = (String)in.readObject();
        isNegated   = in.readBoolean();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(operator);
        out.writeBoolean(isNegated);
    }

    public Operator() {
        
    }

    private Operator(final String operator,
                     final boolean isNegated) {
        this.operator = operator;
        this.isNegated = isNegated;
    }

    private Object readResolve() throws java.io.ObjectStreamException {
        Operator op = determineOperator( this.operator,
                                         this.isNegated );
        return op != null ? op : this;
    }

    public String toString() {
        return "Operator = '" + this.operator + "'";
    }

    /* (non-Javadoc)
     * @see org.kie.base.evaluators.OperatorInterface#getOperatorString()
     */
    public String getOperatorString() {
        return this.operator;
    }

    /* (non-Javadoc)
     * @see org.kie.base.evaluators.OperatorInterface#isNegated()
     */
    public boolean isNegated() {
        return this.isNegated;
    }

    @Override
    public int hashCode() {
        final int PRIME = 31;
        int result = super.hashCode();
        result = PRIME * result + (isNegated ? 1231 : 1237);
        result = PRIME * result + ((operator == null) ? 0 : operator.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if ( this == obj ) return true;
        if ( obj == null ) return false;
        if ( getClass() != obj.getClass() ) return false;
        final Operator other = (Operator) obj;
        if ( isNegated != other.isNegated ) return false;
        if ( operator == null ) {
            if ( other.operator != null ) return false;
        } else if ( !operator.equals( other.operator ) ) return false;
        return true;
    }

}
