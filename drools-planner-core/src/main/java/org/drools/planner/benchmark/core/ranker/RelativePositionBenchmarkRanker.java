package org.drools.planner.benchmark.core.ranker;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.drools.planner.benchmark.api.BenchmarkRanker;
import org.drools.planner.benchmark.core.SolverBenchmark;
import org.drools.planner.core.score.Score;

public class RelativePositionBenchmarkRanker implements BenchmarkRanker {

    private final class BeatOthersBenchmarkComparator implements Comparator<SolverBenchmark> {

        private final Map<SolverBenchmark, Integer> numBeatOthers;
        private final Comparator<SolverBenchmark> whenBeatOthersEqualComparator;

        public BeatOthersBenchmarkComparator(Map<SolverBenchmark, Integer> numBeatOthers, Comparator<SolverBenchmark> comparator) {
            this.numBeatOthers = numBeatOthers;
            whenBeatOthersEqualComparator = comparator;
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
    private List<SolverBenchmark> rankedBenchmarks;

    public void rank(List<SolverBenchmark> benchmarks, Comparator<SolverBenchmark> comparator) {
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
        // and now sort the benchmarks based on that
        rankedBenchmarks = benchmarks;
        Collections.sort(rankedBenchmarks, new BeatOthersBenchmarkComparator(numBenchmarkBeatsOthers, comparator));
        Collections.reverse(rankedBenchmarks);
    }

    public int getRanking(SolverBenchmark benchmark) {
        return rankedBenchmarks.indexOf(benchmark);
    }

}
