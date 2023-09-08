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
package org.kie.dmn.feel.lang.types.impl;

import java.time.Period;
import java.util.Comparator;

public class InterceptNotComparableComparator implements Comparator<Object> {

    @Override
    public int compare(Object o1, Object o2) {
        Comparable c1 = coerceComparablePeriod(o1);
        Comparable c2 = coerceComparablePeriod(o2);
        return c1.compareTo(c2);
    }

    private Comparable<?> coerceComparablePeriod(Object o1) {
        Comparable<?> c1;
        try {
            c1 = (Comparable<?>) o1;
        } catch (ClassCastException e) {
            if (o1 instanceof Period) {
                c1 = new ComparablePeriod((Period) o1);
            } else {
                throw e;
            }
        }
        return c1;
    }

}