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

package org.optaplanner.examples.flightcrewscheduling.domain;

import org.optaplanner.examples.common.domain.AbstractPersistable;

public class FlightCrewParametrization extends AbstractPersistable {

    public static final String REQUIRED_SKILL = "Required skill";
    public static final String FLIGHT_CONFLICT = "Flight conflict";

    public static final String NIGHTS_AWAY_FROM_BASE_FAIRNESS = "Nights away from base fairness";

    private int nightsAwayFromBaseFairness = 10;

    public FlightCrewParametrization() {
    }

    // ************************************************************************
    // Simple getters and setters
    // ************************************************************************

    public int getNightsAwayFromBaseFairness() {
        return nightsAwayFromBaseFairness;
    }

    public void setNightsAwayFromBaseFairness(int nightsAwayFromBaseFairness) {
        this.nightsAwayFromBaseFairness = nightsAwayFromBaseFairness;
    }

}
