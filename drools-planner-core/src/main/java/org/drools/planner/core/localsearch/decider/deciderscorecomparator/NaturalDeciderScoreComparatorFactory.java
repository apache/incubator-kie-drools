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

package org.drools.planner.core.localsearch.decider.deciderscorecomparator;

import java.util.Comparator;

import org.drools.planner.core.score.Score;
import org.drools.planner.core.score.comparator.NaturalScoreComparator;

/**
 * Implementation of {@link DeciderScoreComparatorFactory}.
 * Compares by the natural order of scores.
 * @see DeciderScoreComparatorFactory
 * @author Geoffrey De Smet
 */
public class NaturalDeciderScoreComparatorFactory extends AbstractDeciderScoreComparatorFactory {

    private Comparator<Score> naturalDeciderScoreComparator = new NaturalScoreComparator();

    public Comparator<Score> createDeciderScoreComparator() {
        return naturalDeciderScoreComparator;
    }

}