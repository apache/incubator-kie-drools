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

package org.optaplanner.core.config.heuristic.selector.entity.pillar;

import java.util.Comparator;
import java.util.Objects;

import javax.xml.bind.annotation.XmlType;

@XmlType(propOrder = {
        "subPillarEnabled",
        "minimumSubPillarSize",
        "maximumSubPillarSize"
})
public final class SubPillarConfigPolicy {

    private final boolean subPillarEnabled;
    private final int minimumSubPillarSize;
    private final int maximumSubPillarSize;
    private final Comparator<?> entityComparator;

    private SubPillarConfigPolicy(int minimumSubPillarSize, int maximumSubPillarSize) {
        this.subPillarEnabled = true;
        this.minimumSubPillarSize = minimumSubPillarSize;
        this.maximumSubPillarSize = maximumSubPillarSize;
        validateSizes();
        this.entityComparator = null;
    }

    private SubPillarConfigPolicy(int minimumSubPillarSize, int maximumSubPillarSize, Comparator<?> entityComparator) {
        this.subPillarEnabled = true;
        this.minimumSubPillarSize = minimumSubPillarSize;
        this.maximumSubPillarSize = maximumSubPillarSize;
        validateSizes();
        if (entityComparator == null) {
            throw new IllegalStateException("The entityComparator must not be null.");
        }
        this.entityComparator = entityComparator;
    }

    private SubPillarConfigPolicy() {
        this.subPillarEnabled = false;
        this.minimumSubPillarSize = -1;
        this.maximumSubPillarSize = -1;
        this.entityComparator = null;
    }

    public static SubPillarConfigPolicy withoutSubpillars() {
        return new SubPillarConfigPolicy();
    }

    public static SubPillarConfigPolicy withSubpillars(int minSize, int maxSize) {
        return new SubPillarConfigPolicy(minSize, maxSize);
    }

    public static SubPillarConfigPolicy withSubpillarsUnlimited() {
        return withSubpillars(1, Integer.MAX_VALUE);
    }

    public static SubPillarConfigPolicy sequential(int minSize, int maxSize, Comparator<?> entityComparator) {
        return new SubPillarConfigPolicy(minSize, maxSize, entityComparator);
    }

    public static SubPillarConfigPolicy sequentialUnlimited(Comparator<?> entityComparator) {
        return sequential(1, Integer.MAX_VALUE, entityComparator);
    }

    private void validateSizes() {
        if (minimumSubPillarSize < 1) {
            throw new IllegalStateException("The sub pillar's minimumPillarSize (" + minimumSubPillarSize
                    + ") must be at least 1.");
        }
        if (minimumSubPillarSize > maximumSubPillarSize) {
            throw new IllegalStateException("The minimumPillarSize (" + minimumSubPillarSize
                    + ") must be at least maximumSubChainSize (" + maximumSubPillarSize + ").");
        }
    }

    public boolean isSubPillarEnabled() {
        return subPillarEnabled;
    }

    /**
     * @return Less than 1 when {@link #isSubPillarEnabled()} false.
     */
    public int getMinimumSubPillarSize() {
        return minimumSubPillarSize;
    }

    /**
     * @return Less than 1 when {@link #isSubPillarEnabled()} false.
     */
    public int getMaximumSubPillarSize() {
        return maximumSubPillarSize;
    }

    /**
     * @return Not null if the subpillars are to be treated as sequential. Always null if {@link #subPillarEnabled} is false.
     */
    public Comparator<?> getEntityComparator() {
        return entityComparator;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        SubPillarConfigPolicy that = (SubPillarConfigPolicy) o;
        return subPillarEnabled == that.subPillarEnabled
                && minimumSubPillarSize == that.minimumSubPillarSize
                && maximumSubPillarSize == that.maximumSubPillarSize
                && Objects.equals(entityComparator, that.entityComparator);
    }

    @Override
    public int hashCode() {
        return Objects.hash(subPillarEnabled, minimumSubPillarSize, maximumSubPillarSize, entityComparator);
    }
}
