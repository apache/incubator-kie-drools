/*
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
package org.kie.dmn.feel.util;

import java.time.ZoneId;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalQueries;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;

import org.kie.dmn.feel.lang.FEELDialect;
import org.kie.dmn.feel.lang.ast.dialectHandlers.DefaultDialectHandler;
import org.kie.dmn.feel.runtime.Range;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BooleanEvalHelper {
    public static final Logger LOG = LoggerFactory.getLogger(BooleanEvalHelper.class);

    /**
     * DMNv1.2 Table 48: Specific semantics of equality
     * DMNv1.3 Table 71: Semantic of date and time functions
     */
    public static Boolean isEqualDateTimeInSemanticD(TemporalAccessor left, TemporalAccessor right) {
        boolean result = true;
        Optional<Integer> lY = Optional.ofNullable(left.isSupported(ChronoField.YEAR) ? left.get(ChronoField.YEAR) : null);
        Optional<Integer> rY = Optional.ofNullable(right.isSupported(ChronoField.YEAR) ? right.get(ChronoField.YEAR) : null);
        result &= lY.equals(rY);
        Optional<Integer> lM = Optional.ofNullable(left.isSupported(ChronoField.MONTH_OF_YEAR) ? left.get(ChronoField.MONTH_OF_YEAR) : null);
        Optional<Integer> rM = Optional.ofNullable(right.isSupported(ChronoField.MONTH_OF_YEAR) ? right.get(ChronoField.MONTH_OF_YEAR) : null);
        result &= lM.equals(rM);
        Optional<Integer> lD = Optional.ofNullable(left.isSupported(ChronoField.DAY_OF_MONTH) ? left.get(ChronoField.DAY_OF_MONTH) : null);
        Optional<Integer> rD = Optional.ofNullable(right.isSupported(ChronoField.DAY_OF_MONTH) ? right.get(ChronoField.DAY_OF_MONTH) : null);
        result &= lD.equals(rD);
        result &= isEqualTimeInSemanticD(left, right);
        return result;
    }

    /**
     * DMNv1.2 Table 48: Specific semantics of equality
     * DMNv1.3 Table 71: Semantic of date and time functions
     */
    public static Boolean isEqualTimeInSemanticD(TemporalAccessor left, TemporalAccessor right) {
        boolean result = true;
        Optional<Integer> lH = Optional.ofNullable(left.isSupported(ChronoField.HOUR_OF_DAY) ? left.get(ChronoField.HOUR_OF_DAY) : null);
        Optional<Integer> rH = Optional.ofNullable(right.isSupported(ChronoField.HOUR_OF_DAY) ? right.get(ChronoField.HOUR_OF_DAY) : null);
        result &= lH.equals(rH);
        Optional<Integer> lM = Optional.ofNullable(left.isSupported(ChronoField.MINUTE_OF_HOUR) ? left.get(ChronoField.MINUTE_OF_HOUR) : null);
        Optional<Integer> rM = Optional.ofNullable(right.isSupported(ChronoField.MINUTE_OF_HOUR) ? right.get(ChronoField.MINUTE_OF_HOUR) : null);
        result &= lM.equals(rM);
        Optional<Integer> lS = Optional.ofNullable(left.isSupported(ChronoField.SECOND_OF_MINUTE) ? left.get(ChronoField.SECOND_OF_MINUTE) : null);
        Optional<Integer> rS = Optional.ofNullable(right.isSupported(ChronoField.SECOND_OF_MINUTE) ? right.get(ChronoField.SECOND_OF_MINUTE) : null);
        result &= lS.equals(rS);
        Optional<ZoneId> lTZ = Optional.ofNullable(left.query(TemporalQueries.zone()));
        Optional<ZoneId> rTZ = Optional.ofNullable(right.query(TemporalQueries.zone()));
        result &= lTZ.equals(rTZ);
        return result;
    }

    /**
     * This method consider if the <code>value</code> object is a <code>String</code>
     * In that case, return the {@link String#equals(Object)} result
     * Otherwise, default to the isEqual method
     *
     * @param value
     * @param itemFromList
     * @return the boolean result based on the conditions
     */
    public static boolean isEqualsStringCompare(Object value, Object itemFromList) {
        if (value == null && itemFromList == null) {
            return true; // both null → equal
        }
        if (value instanceof String) {
            return value.equals(itemFromList);
        } else {
            // Defaulting FEELDialect to FEEL
            Boolean dmnEqual = DefaultDialectHandler.isEqual(value, itemFromList, () -> null, () -> null);
            return dmnEqual != null && dmnEqual;
        }
    }

    /**
     * Return the original object or, depending on the FEELDialect, a default value
     *
     * @param rawReturn
     * @param feelDialect
     * @return
     */
    public static Boolean getBooleanOrDialectDefault(Object rawReturn, FEELDialect feelDialect) {
        Boolean toReturn = null;
        if (rawReturn instanceof Boolean bool) {
            toReturn = bool;
        } else if (feelDialect.equals(FEELDialect.BFEEL)) {
            toReturn = false;
        }
        return toReturn;
    }

    public static Boolean isEqual(Range left, Range right) {
        return left.equals(right);
    }

    public static Boolean isEqual(Iterable left, Iterable right) {
        Iterator li = left.iterator();
        Iterator ri = right.iterator();
        while (li.hasNext() && ri.hasNext()) {
            Object l = li.next();
            Object r = ri.next();
            if (!isEqualObject(l, r))
                return false;
        }
        return li.hasNext() == ri.hasNext();
    }

    public static Boolean isEqual(Map<?, ?> left, Map<?, ?> right) {
        if (left.size() != right.size()) {
            return false;
        }
        for (Map.Entry le : left.entrySet()) {
            Object l = le.getValue();
            Object r = right.get(le.getKey());
            if (!isEqualObject(l, r))
                return false;
        }
        return true;
    }

    static Boolean isEqualObject(Object l, Object r) {
        if (l instanceof Iterable && r instanceof Iterable && !isEqual((Iterable) l, (Iterable) r)) {
            return false;
        } else if (l instanceof Map && r instanceof Map && !isEqual((Map) l, (Map) r)) {
            return false;
        } else if (l != null && r != null && !l.equals(r)) {
            return false;
        } else if ((l == null || r == null) && l != r) {
            return false;
        }
        return true;
    }

}
