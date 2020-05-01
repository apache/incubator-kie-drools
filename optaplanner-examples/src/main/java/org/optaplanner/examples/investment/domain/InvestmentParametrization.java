/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.examples.investment.domain;

import org.optaplanner.examples.common.domain.AbstractPersistable;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * Institutional weightings.
 */
@XStreamAlias("InvestmentParametrization")
public class InvestmentParametrization extends AbstractPersistable {

    private long standardDeviationMillisMaximum; // In millis (so multiplied by 1000)

    public long getStandardDeviationMillisMaximum() {
        return standardDeviationMillisMaximum;
    }

    public void setStandardDeviationMillisMaximum(long standardDeviationMillisMaximum) {
        this.standardDeviationMillisMaximum = standardDeviationMillisMaximum;
    }

    // ************************************************************************
    // Complex methods
    // ************************************************************************

    public long calculateSquaredStandardDeviationFemtosMaximum() {
        return standardDeviationMillisMaximum * standardDeviationMillisMaximum
                * 1000L * 1000L * 1000L;
    }

}
