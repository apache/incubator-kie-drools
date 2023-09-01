package org.drools.verifier.api.reporting;

import java.util.EnumSet;
import java.util.Set;

public enum CheckType {

    CONFLICTING_ROWS,
    DEFICIENT_ROW,
    IMPOSSIBLE_MATCH,
    MISSING_ACTION,
    MISSING_RESTRICTION,
    MULTIPLE_VALUES_FOR_ONE_ACTION,
    VALUE_FOR_FACT_FIELD_IS_SET_TWICE,
    VALUE_FOR_ACTION_IS_SET_TWICE,
    REDUNDANT_CONDITIONS_TITLE,
    REDUNDANT_ROWS,
    SUBSUMPTANT_ROWS,
    MISSING_RANGE,
    SINGLE_HIT_LOST,
    EMPTY_RULE,
    ILLEGAL_VERIFIER_STATE;

    public static Set<CheckType> getRowLevelCheckTypes() {
        return EnumSet.of(
                CONFLICTING_ROWS,
                DEFICIENT_ROW,
                REDUNDANT_ROWS,
                SUBSUMPTANT_ROWS,
                SINGLE_HIT_LOST);
    }
}
