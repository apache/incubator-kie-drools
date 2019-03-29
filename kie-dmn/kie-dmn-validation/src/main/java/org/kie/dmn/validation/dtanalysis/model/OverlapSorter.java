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

import org.kie.dmn.feel.runtime.Range.RangeBoundary;

/**
 * @deprecated unified into Bounds sort.
 */
@Deprecated()
public class OverlapSorter implements Comparator<Bound<?>> {

    @Override
    public int compare(Bound<?> o1, Bound<?> o2) {
        int valueCompare = BoundValueComparator.compareValueDispatchingToInf(o1, o2);
        if (valueCompare != 0) {
            return valueCompare;
        }

        if (o1.getParent() != null && o2.getParent() != null) {
            if (o1.isUpperBound() && o2.isLowerBound()) {
                return 1;
            } else if (o1.isLowerBound() && o2.isUpperBound()) {
                return -1;
            } else if (o1.isLowerBound() && o2.isLowerBound()) {
                if (o1.getBoundaryType() == o2.getBoundaryType()) {
                    return 0;
                } else if (o1.getBoundaryType() == RangeBoundary.OPEN) {
                    return -1;
                } else {
                    return 1;
                }
            } else if (o1.isUpperBound() && o2.isUpperBound()) {
                if (o1.getBoundaryType() == o2.getBoundaryType()) {
                    return 0;
                } else if (o1.getBoundaryType() == RangeBoundary.OPEN) {
                    return 1;
                } else {
                    return +1;
                }
            }
        }

        if (o1.getBoundaryType() == o2.getBoundaryType()) {
            return 0;
        } else if (o1.getBoundaryType() == RangeBoundary.OPEN) {
            return -1;
        } else {
            return 1;
        }
    }
}
