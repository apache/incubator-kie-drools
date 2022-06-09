package org.optaplanner.core.config.exhaustivesearch;

import javax.xml.bind.annotation.XmlEnum;

import org.optaplanner.core.config.heuristic.selector.entity.EntitySorterManner;
import org.optaplanner.core.config.heuristic.selector.value.ValueSorterManner;

@XmlEnum
public enum ExhaustiveSearchType {
    BRUTE_FORCE,
    BRANCH_AND_BOUND;

    public EntitySorterManner getDefaultEntitySorterManner() {
        switch (this) {
            case BRUTE_FORCE:
                return EntitySorterManner.NONE;
            case BRANCH_AND_BOUND:
                return EntitySorterManner.DECREASING_DIFFICULTY_IF_AVAILABLE;
            default:
                throw new IllegalStateException("The exhaustiveSearchType ("
                        + this + ") is not implemented.");
        }
    }

    public ValueSorterManner getDefaultValueSorterManner() {
        switch (this) {
            case BRUTE_FORCE:
                return ValueSorterManner.NONE;
            case BRANCH_AND_BOUND:
                return ValueSorterManner.INCREASING_STRENGTH_IF_AVAILABLE;
            default:
                throw new IllegalStateException("The exhaustiveSearchType ("
                        + this + ") is not implemented.");
        }
    }

    public boolean isScoreBounderEnabled() {
        switch (this) {
            case BRUTE_FORCE:
                return false;
            case BRANCH_AND_BOUND:
                return true;
            default:
                throw new IllegalStateException("The exhaustiveSearchType ("
                        + this + ") is not implemented.");
        }
    }

}
