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
package org.kie.dmn.validation.dtanalysis.model;

import java.util.Collections;
import java.util.List;

public class DDTAOutputClause {

    private final Interval domainMinMax;
    private final List discreteValues;
    private final List outputOrder;

    public DDTAOutputClause(Interval domainMinMax) {
        this.domainMinMax = domainMinMax;
        this.discreteValues = Collections.emptyList();
        this.outputOrder = Collections.emptyList();
    }

    public DDTAOutputClause(Interval domainMinMax, List discreteValues, List outputOrder) {
        this.domainMinMax = domainMinMax;
        this.discreteValues = discreteValues;
        this.outputOrder = outputOrder;
    }

    public Bound<?> getMin() {
        return domainMinMax.getLowerBound();
    }

    public Bound<?> getMax() {
        return domainMinMax.getUpperBound();
    }

    public Interval getDomainMinMax() {
        return domainMinMax;
    }

    public List getDiscreteValues() {
        return Collections.unmodifiableList(discreteValues);
    }

    public boolean isDiscreteDomain() {
        return discreteValues != null && !discreteValues.isEmpty();
    }

    public List getOutputOrder() {
        return Collections.unmodifiableList(outputOrder);
    }
}
