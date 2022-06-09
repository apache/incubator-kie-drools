package org.optaplanner.core.config.localsearch.decider.acceptor.stepcountinghillclimbing;

import javax.xml.bind.annotation.XmlEnum;

import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.config.localsearch.decider.forager.LocalSearchForagerConfig;

/**
 * Determines what increment the counter of Step Counting Hill Climbing.
 */
@XmlEnum
public enum StepCountingHillClimbingType {
    /**
     * Every selected move is counted.
     */
    SELECTED_MOVE,
    /**
     * Every accepted move is counted.
     * <p>
     * Note: If {@link LocalSearchForagerConfig#getAcceptedCountLimit()} = 1,
     * then this behaves exactly the same as {link #STEP}.
     */
    ACCEPTED_MOVE,
    /**
     * Every step is counted. Every step was always an accepted move. This is the default.
     */
    STEP,
    /**
     * Every step that equals or improves the {@link Score} of the last step is counted.
     */
    EQUAL_OR_IMPROVING_STEP,
    /**
     * Every step that improves the {@link Score} of the last step is counted.
     */
    IMPROVING_STEP;

}
