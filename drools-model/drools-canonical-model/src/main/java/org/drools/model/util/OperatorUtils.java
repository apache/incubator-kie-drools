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
package org.drools.model.util;

import java.math.BigDecimal;
import java.util.Objects;

public class OperatorUtils {

    private OperatorUtils() {
    }

    public static boolean areEqual(Object o1, Object o2) {
        return o1 instanceof Number && o2 instanceof Number ? areNumericEqual((Number) o1, (Number) o2) : Objects.equals(o1, o2);
    }

    public static boolean areNumericEqual(Number n1, Number n2) {
        return n1.getClass() != n2.getClass() || n1 instanceof Comparable ?
                asBigDecimal(n1).compareTo(asBigDecimal(n2)) == 0 : // BigDecimal.equals() returns false for different scales
                Objects.equals(n1, n2);
    }

    public static int compare(Object o1, Object o2) {
        return o1.getClass() != o2.getClass() && o1 instanceof Number && o2 instanceof Number ?
                asBigDecimal((Number)o1).compareTo(asBigDecimal((Number)o2)) :
                ((Comparable) o1).compareTo(o2);
    }

    public static BigDecimal asBigDecimal(Number num) {
        return num instanceof BigDecimal ? (BigDecimal) num : BigDecimal.valueOf(num.doubleValue());
    }
}
