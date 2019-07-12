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

package org.optaplanner.core.config.localsearch;

import java.util.Arrays;

public enum LocalSearchType {
    HILL_CLIMBING,
    TABU_SEARCH,
    SIMULATED_ANNEALING,
    LATE_ACCEPTANCE,
    GREAT_DELUGE,
    VARIABLE_NEIGHBORHOOD_DESCENT;

    /**
     * @return {@link #values()} without duplicates (abstract types that end up behaving as one of the other types).
     */
    public static LocalSearchType[] getBluePrintTypes() {
        return Arrays.stream(values())
                // Workaround for https://issues.jboss.org/browse/PLANNER-1294
                .filter(localSearchType -> localSearchType != SIMULATED_ANNEALING)
                .toArray(LocalSearchType[]::new);
    }

}
