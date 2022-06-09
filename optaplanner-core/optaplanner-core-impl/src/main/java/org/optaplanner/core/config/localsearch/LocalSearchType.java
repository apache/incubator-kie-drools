package org.optaplanner.core.config.localsearch;

import java.util.Arrays;

import javax.xml.bind.annotation.XmlEnum;

@XmlEnum
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
                // Workaround for https://issues.redhat.com/browse/PLANNER-1294
                .filter(localSearchType -> localSearchType != SIMULATED_ANNEALING)
                .toArray(LocalSearchType[]::new);
    }

}
