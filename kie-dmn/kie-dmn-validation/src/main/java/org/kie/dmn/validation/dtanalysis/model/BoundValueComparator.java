/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.dmn.validation.dtanalysis.model;

import java.util.Comparator;

public class BoundValueComparator implements Comparator<Bound<?>> {

    @Override
    public int compare(Bound<?> o1, Bound<?> o2) {
        return compareValueDispatchingToInf(o1, o2);
    }

    public static int compareValueDispatchingToInf(Bound<?> o1, Bound<?> o2) {
        if (o1.getValue() != Interval.NEG_INF && o1.getValue() != Interval.POS_INF && (o2.getValue() == Interval.NEG_INF || o2.getValue() == Interval.POS_INF)) {
            return 0 - ((Comparable) o2.getValue()).compareTo(o1.getValue());
        }
        return ((Comparable) o1.getValue()).compareTo(o2.getValue());
    }

}
