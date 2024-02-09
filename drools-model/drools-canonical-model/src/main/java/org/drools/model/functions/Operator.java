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
package org.drools.model.functions;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.drools.model.operators.ContainsOperator;
import org.drools.model.operators.ExcludesOperator;
import org.drools.model.operators.InOperator;
import org.drools.model.operators.MatchesOperator;
import org.drools.model.operators.MemberOfOperator;
import org.drools.model.operators.SoundsLikeOperator;
import org.drools.model.operators.StringEndsWithOperator;
import org.drools.model.operators.StringLengthWithOperator;
import org.drools.model.operators.StringStartsWithOperator;

public interface Operator<A, B> extends Predicate2<A, B[]> {

    default boolean requiresCoercion() {
        return false;
    }

    default boolean isCompatibleWithType(Class<?> type) {
        return true;
    }

    interface SingleValue<A, B> extends Operator<A, B> {
        default boolean test( A o1, B[] o2 ) {
            return eval( o1, o2[0] );
        }

        boolean eval(A o1, B o2);
    }

    interface MultipleValue<A, B> extends Operator<A, B> {
        default boolean test( A o1, B[] o2 ) {
            return eval( o1, o2 );
        }

        boolean eval(A o1, B[] o2);
    }

    String getOperatorName();

    class Register {
        private static final Map<String, Operator> opMap = new HashMap<>();

        static {
            register( InOperator.INSTANCE );
            register( MatchesOperator.INSTANCE );
            register( ContainsOperator.INSTANCE );
            register( ExcludesOperator.INSTANCE );
            register( MemberOfOperator.INSTANCE );
            register( SoundsLikeOperator.INSTANCE );
            register( StringStartsWithOperator.INSTANCE );
            register( StringEndsWithOperator.INSTANCE );
            register( StringLengthWithOperator.INSTANCE );
        }

        public static void register(Operator operator) {
            opMap.put( operator.getOperatorName(), operator);
        }

        public static boolean hasOperator(String opName) {
            return opMap.containsKey( opName );
        }

        public static Operator getOperator(String opName) {
            return opMap.get( opName );
        }

        public static Collection<String> getOperators() {
            return opMap.keySet();
        }
    }
}
