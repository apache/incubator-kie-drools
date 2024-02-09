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
package org.kie.dmn.core.jsr223;

import java.math.BigDecimal;

public class JSR223Utils {
    
    private JSR223Utils() {
        // only static utils method.
    }
    
    public static double doubleValueExact(BigDecimal original) {
        double result = original.doubleValue();
        if (!(Double.isNaN(result) || Double.isInfinite(result))) {
            if (new BigDecimal(String.valueOf(result)).compareTo(original) == 0) {
                return result;
            }
        }
        throw new ArithmeticException(String.format("Conversion of %s incurred in loss of precision from BigDecimal", original));
    }

    /**
     * TODO PROVISIONAL, as this does not support non-latin characters, and without accents.
     */
    public static String escapeIdentifierForBinding(String original) {
        StringBuilder sb = new StringBuilder(original.length());
        Iterable<Integer> iterable = original.codePoints()::iterator;
        int i = 0;
        for (Integer cp : iterable) {
            if (i == 0) {
                if (cp >= '0' && cp <= '9') {
                    sb.append("_");
                }
            }
            if (cp >= '0' && cp <= '9') {
                sb.append((char) (int) cp);
            } else if (cp >= 'a' && cp <= 'z') {
                sb.append((char) (int) cp);
            } else if (cp >= 'A' && cp <= 'Z') {
                sb.append((char) (int) cp);
            } else {
                sb.append("_");
            }
            i++;
        }
        return sb.toString();
    }
}
