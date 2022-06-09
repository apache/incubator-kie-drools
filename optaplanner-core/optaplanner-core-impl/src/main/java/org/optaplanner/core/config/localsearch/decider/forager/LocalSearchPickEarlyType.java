package org.optaplanner.core.config.localsearch.decider.forager;

import javax.xml.bind.annotation.XmlEnum;

@XmlEnum
public enum LocalSearchPickEarlyType {
    NEVER,
    FIRST_BEST_SCORE_IMPROVING,
    FIRST_LAST_STEP_SCORE_IMPROVING;
}
