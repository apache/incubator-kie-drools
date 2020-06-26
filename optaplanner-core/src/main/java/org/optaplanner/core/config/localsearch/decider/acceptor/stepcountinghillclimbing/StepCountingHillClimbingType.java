/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
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
