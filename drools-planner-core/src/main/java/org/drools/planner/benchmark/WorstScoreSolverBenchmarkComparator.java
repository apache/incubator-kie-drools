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

package org.drools.planner.benchmark;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.lang.builder.CompareToBuilder;
import org.drools.planner.core.score.Score;

/**
 * @author Geoffrey De Smet
 */
public class WorstScoreSolverBenchmarkComparator implements Comparator<SolverBenchmark> {

    public int compare(SolverBenchmark a, SolverBenchmark b) {
        List<Score> aScoreList = a.getScoreList();
        Collections.sort(aScoreList); // Worst scores become first in the list
        List<Score> bScoreList = b.getScoreList();
        Collections.sort(bScoreList); // Worst scores become first in the list
        return new CompareToBuilder()
                .append(aScoreList.toArray(), bScoreList.toArray())
                .toComparison();
    }

}
