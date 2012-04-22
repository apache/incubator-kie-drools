/*
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

package org.drools.planner.benchmark.core.comparator;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.drools.planner.benchmark.api.SolverBenchmarkComparatorFactory;
import org.drools.planner.benchmark.core.SolverBenchmark;
import org.drools.planner.core.score.Score;

/**
 * This benchmark comparator factory will look into each benchmark's scores and 
 * for each of these scores, it will determine how many other benchmark's scores
 * this benchmark beats. The best-ranking benchmark will have beaten the most 
 * scores of the other benchmarks.
 * 
 * It should be more fair than those that rank benchmarks based on their total 
 * score. When the scores for different problem benchmarks differ greatly 
 * (e.g. 10 v. 10000), comparing in absolute terms is misleading. 
 * 
 * For that reason, using this factory only makes sense when there are more 
 * problem benchmarks inside one solver benchmark - otherwise, simple 
 * comparator-based factory will do exactly the same.
 */
public class RelativePositionSolverBenchmarkComparatorFactory implements SolverBenchmarkComparatorFactory {

    /**
     * Compares benchmarks by how many times their scores beat other benchmarks'
     * scores. 
     */
    private final class BeatOthersBenchmarkComparator implements Comparator<SolverBenchmark> {

        private final Map<SolverBenchmark, Integer> numBeatOthers;
        private final Comparator<SolverBenchmark> whenBeatOthersEqualComparator;

        public BeatOthersBenchmarkComparator(Map<SolverBenchmark, Integer> numBeatOthers) {
            this.numBeatOthers = numBeatOthers;
            whenBeatOthersEqualComparator = new TotalScoreSolverBenchmarkComparator();
        }

        public int compare(SolverBenchmark o1, SolverBenchmark o2) {
            int compare = numBeatOthers.get(o1).compareTo(numBeatOthers.get(o2));
            if (compare == 0) {
                return whenBeatOthersEqualComparator.compare(o1, o2);
            } else {
                return compare;
            }
        }

    }

    private final Map<SolverBenchmark, Integer> numBenchmarkBeatsOthers = new HashMap<SolverBenchmark, Integer>();

    public Comparator<SolverBenchmark> createSolverBenchmarkComparator(List<SolverBenchmark> benchmarks) {
        numBenchmarkBeatsOthers.clear();
        // find out how many times a particular benchmark beat other benchmarks
        for (SolverBenchmark benchmark : benchmarks) {
            List<Score> originalScoreList = benchmark.getScoreList();
            int numBeatOthers = 0;
            for (SolverBenchmark benchmark2 : benchmarks) {
                if (benchmark == benchmark2) {
                    // don't compare benchmark with itself
                    continue;
                }
                // compare each score; the more the original scores are better, the better the benchmark as a whole
                List<Score> comparedScoreList = benchmark.getScoreList();
                for (int i = 0; i < originalScoreList.size(); i++) {
                    if (originalScoreList.get(i).compareTo(comparedScoreList.get(i)) > 0) {
                        numBeatOthers++;
                    }
                }

            }
            numBenchmarkBeatsOthers.put(benchmark, numBeatOthers);
        }
        return new BeatOthersBenchmarkComparator(numBenchmarkBeatsOthers);
    }

}
