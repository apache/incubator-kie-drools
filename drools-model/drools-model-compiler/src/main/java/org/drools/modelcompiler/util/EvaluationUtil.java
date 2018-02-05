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

package org.drools.modelcompiler.util;

import java.math.BigDecimal;

public class EvaluationUtil {

    public static boolean areNullSafeEquals(Object obj1, Object obj2) {
        return obj1 != null ? obj1.equals( obj2 ) : obj2 == null;
    }

    public static boolean compareStringsAsNumbers(String s1, String s2, String op) {
        int result = new BigDecimal(s1).compareTo( new BigDecimal( s2 ) );
        switch (op) {
            case "<" : return result < 0;
            case "<=" : return result <= 0;
            case ">" : return result > 0;
            case ">=" : return result >= 0;
        }
        throw new RuntimeException( "unknown operator: " + op );
    }
}
