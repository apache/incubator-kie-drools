/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.core.config.localsearch.decider.forager;

import javax.xml.bind.annotation.XmlEnum;

import org.optaplanner.core.impl.localsearch.decider.forager.finalist.FinalistPodium;
import org.optaplanner.core.impl.localsearch.decider.forager.finalist.HighestScoreFinalistPodium;
import org.optaplanner.core.impl.localsearch.decider.forager.finalist.StrategicOscillationByLevelFinalistPodium;

@XmlEnum
public enum FinalistPodiumType {
    HIGHEST_SCORE,
    STRATEGIC_OSCILLATION,
    STRATEGIC_OSCILLATION_BY_LEVEL,
    STRATEGIC_OSCILLATION_BY_LEVEL_ON_BEST_SCORE;

    public FinalistPodium buildFinalistPodium() {
        switch (this) {
            case HIGHEST_SCORE:
                return new HighestScoreFinalistPodium();
            case STRATEGIC_OSCILLATION:
            case STRATEGIC_OSCILLATION_BY_LEVEL:
                return new StrategicOscillationByLevelFinalistPodium(false);
            case STRATEGIC_OSCILLATION_BY_LEVEL_ON_BEST_SCORE:
                return new StrategicOscillationByLevelFinalistPodium(true);
            default:
                throw new IllegalStateException("The finalistPodiumType (" + this + ") is not implemented.");
        }
    }

}
