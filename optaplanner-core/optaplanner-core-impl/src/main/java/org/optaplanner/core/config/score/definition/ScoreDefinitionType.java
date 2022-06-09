package org.optaplanner.core.config.score.definition;

import javax.xml.bind.annotation.XmlEnum;

@XmlEnum
public enum ScoreDefinitionType {
    SIMPLE,
    SIMPLE_LONG,
    /**
     * WARNING: NOT RECOMMENDED TO USE DUE TO ROUNDING ERRORS THAT CAUSE SCORE CORRUPTION.
     * Use {@link #SIMPLE_BIG_DECIMAL} instead.
     */
    @Deprecated(forRemoval = true)
    SIMPLE_DOUBLE,
    SIMPLE_BIG_DECIMAL,
    HARD_SOFT,
    HARD_SOFT_LONG,
    /**
     * WARNING: NOT RECOMMENDED TO USE DUE TO ROUNDING ERRORS THAT CAUSE SCORE CORRUPTION.
     * Use {@link #HARD_SOFT_BIG_DECIMAL} instead.
     */
    @Deprecated(forRemoval = true)
    HARD_SOFT_DOUBLE,
    HARD_SOFT_BIG_DECIMAL,
    HARD_MEDIUM_SOFT,
    HARD_MEDIUM_SOFT_LONG,
    BENDABLE,
    BENDABLE_LONG,
    BENDABLE_BIG_DECIMAL;
}
