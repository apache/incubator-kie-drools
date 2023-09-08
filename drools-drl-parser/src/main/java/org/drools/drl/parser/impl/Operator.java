/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.drl.parser.impl;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Operator implements Externalizable, org.kie.api.runtime.rule.Operator {

    // a static private cache so that pluggable operator can register their implementations
    // it is automatically initialized with common operator implementations
    private static final Map<String, Operator> CACHE = new ConcurrentHashMap<>();

    static {
        // forces the initialization of the enum and then the registration of all operators
        BuiltInOperator builtInOperator = BuiltInOperator.EQUAL;
    }

    public enum BuiltInOperator {
        EQUAL("==", false),
        NOT_EQUAL("!=", false),
        LESS("<", false),
        LESS_OR_EQUAL("<=", false),
        GREATER(">", false),
        GREATER_OR_EQUAL(">=", false),
        CONTAINS("contains"),
        EXCLUDES("excludes"),
        MATCHES("matches"),
        MEMBEROF("memberOf"),
        SOUNDSLIKE("soundslike"),
        AFTER("after"),
        BEFORE("before"),
        COINCIDES("coincides"),
        DURING("during"),
        FINISHED_BY("finishedby"),
        FINISHES("finishes"),
        INCLUDES("includes"),
        MEETS("meets"),
        MET_BY("metby"),
        OVERLAPPED_BY("overlappedby"),
        OVERLAPS("overlaps"),
        STARTED_BY("startedby"),
        STARTS("starts"),
        STR("str");

        private final String symbol;
        private final Operator operator;

        BuiltInOperator(String symbol) {
            this(symbol, true);
        }

        BuiltInOperator(String symbol, boolean supportNegation) {
            this.symbol = symbol;
            this.operator = Operator.addOperatorToRegistry(symbol, false);
            if (supportNegation) {
                Operator.addOperatorToRegistry(symbol, true);
            }
        }

        public String getSymbol() {
            return symbol;
        }

        public Operator getOperator() {
            return operator;
        }
    }

    private static final long                  serialVersionUID = 510l;

    /**
     * Creates a new Operator instance for the given parameters,
     * adds it to the registry and return it
     *
     * @param operatorId the identification symbol of the operator
     * @param isNegated true if it is negated
     *
     * @return the newly created operator
     */
    public static Operator addOperatorToRegistry(final String operatorId, final boolean isNegated) {
        Operator op = new Operator( operatorId, isNegated );
        CACHE.put( getKey( operatorId, isNegated ), op );
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
    public static Operator determineOperator(final String operatorId, final boolean isNegated) {
        return CACHE.get( getKey( operatorId, isNegated ) );
    }

    private static String getKey(final String string, final boolean isNegated) {
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

    private Operator(final String operator, final boolean isNegated) {
        this.operator = operator;
        this.isNegated = isNegated;
    }

    private Object readResolve() throws java.io.ObjectStreamException {
        Operator op = determineOperator( this.operator, this.isNegated );
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
        }
        return operator.equals(other.operator);
    }

}
