package org.optaplanner.core.config.constructionheuristic.decider.forager;

import javax.xml.bind.annotation.XmlEnum;

@XmlEnum
public enum ConstructionHeuristicPickEarlyType {
    NEVER,
    FIRST_NON_DETERIORATING_SCORE,
    FIRST_FEASIBLE_SCORE,
    FIRST_FEASIBLE_SCORE_OR_NON_DETERIORATING_HARD;
}
