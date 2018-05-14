/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.examples.rocktour.domain;

import org.optaplanner.examples.common.domain.AbstractPersistable;

public class RockTourParametrization extends AbstractPersistable {

    public static final String REVENUE_OPPORTUNITY = "Revenue opportunity";

    private int revenueOpportunity = 1;

    public RockTourParametrization() {
    }

    public RockTourParametrization(long id) {
        super(id);
    }

    // ************************************************************************
    // Simple getters and setters
    // ************************************************************************

    public int getRevenueOpportunity() {
        return revenueOpportunity;
    }

    public void setRevenueOpportunity(int revenueOpportunity) {
        this.revenueOpportunity = revenueOpportunity;
    }

}
