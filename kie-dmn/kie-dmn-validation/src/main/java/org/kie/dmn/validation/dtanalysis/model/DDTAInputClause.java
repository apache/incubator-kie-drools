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

import java.util.Collections;
import java.util.List;

public class DDTAInputClause implements Domain {

    private final Interval domainMinMax;
    private final List discreteValues;
    private final List<Comparable<?>> discreteDMNOrder;

    public DDTAInputClause(Interval domainMinMax) {
        this.domainMinMax = domainMinMax;
        this.discreteValues = Collections.emptyList();
        this.discreteDMNOrder = Collections.emptyList();
    }

    public DDTAInputClause(Interval domainMinMax, List discreteValues, List<Comparable<?>> discreteDMNOrder) {
        this.domainMinMax = domainMinMax;
        this.discreteValues = discreteValues;
        this.discreteDMNOrder = discreteDMNOrder;
    }

    @Override
    public Bound<?> getMin() {
        return domainMinMax.getLowerBound();
    }

    @Override
    public Bound<?> getMax() {
        return domainMinMax.getUpperBound();
    }

    @Override
    public Interval getDomainMinMax() {
        return domainMinMax;
    }

    @Override
    public List getDiscreteValues() {
        return Collections.unmodifiableList(discreteValues);
    }

    @Override
    public boolean isDiscreteDomain() {
        return discreteValues != null && !discreteValues.isEmpty();
    }

    /**
     * Used by MC/DC
     * NOT to be used by Gap analysis, as domain ordering is not necessarily respected while modeling.
     */
    public List<Comparable<?>> getDiscreteDMNOrder() {
        return discreteDMNOrder;
    }

}
