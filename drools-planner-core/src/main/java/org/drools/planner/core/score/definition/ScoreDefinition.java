/**
 * Copyright 2010 JBoss Inc
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

package org.drools.planner.core.score.definition;

import org.drools.planner.core.score.Score;

/**
 * A ScoreDefinition knows how to compare scores and what the perfect maximum/minimum Score is.
 * @see AbstractScoreDefinition
 * @see HardAndSoftScoreDefinition
 * @author Geoffrey De Smet
 */
public interface ScoreDefinition<S extends Score> {

    /**
     * The perfect maximum score is the score of which there is no better in any problem instance.
     * This doesn't mean that the current problem instance, or any problem instance for that matter,
     * could ever attain that score.
     * </p>
     * For example, most cases have a perfect maximum score of zero, as most use cases only have negative scores.
     * @return null if not supported
     */
    S getPerfectMaximumScore();

    /**
     * The perfect minimum score is the score of which there is no worser in any problem instance.
     * This doesn't mean that the current problem instance, or any problem instance for that matter,
     * could ever attain such a bad score.
     * </p>
     * For example, most cases have a perfect minimum score of negative infinity.
     * @return null if not supported
     */
    S getPerfectMinimumScore();

    /**
     * Parses the String and returns a Score.
     * @param scoreString never null
     * @return never null
     */
    Score parseScore(String scoreString);

    /**
     * @param startScore never null
     * @param endScore never null
     * @param score never null
     * @return between 0.0 and 1.0
     */
    double calculateTimeGradient(S startScore, S endScore, S score);

    /**
     * 
     * @param score never null
     * @return null if should not be shown on the graph
     */
    Double translateScoreToGraphValue(S score);

}
