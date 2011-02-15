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
import org.drools.planner.core.localsearch.LocalSearchSolverLifecycleListener;

/**
 * A DeciderScoreComparatorFactory creates a new DeciderScoreComparator each step,
 * which compares 2 scores to decide the next step.
 * That Score Comparator can consider shifting penalty, aging penalty, ...
 * in which case it differs from the natural ordering of scores.
 * @author Geoffrey De Smet
 */
public interface DeciderScoreComparatorFactory extends LocalSearchSolverLifecycleListener {

    /**
     * @return never null
     */
    Comparator<Score> createDeciderScoreComparator();

}
