package org.drools.planner.benchmark.core.ranker;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.drools.planner.benchmark.api.BenchmarkRanker;
import org.drools.planner.benchmark.core.SolverBenchmark;
import org.drools.planner.core.score.Score;

/**
 * This benchmark ranker will look into each benchmark's scores and for each of 
 * these scores, it will determine how many other benchmark's scores this 
 * benchmark beats. The best-ranking benchmark will have beaten the most scores 
 * of the other benchmarks.
 * 
 * It should be more fair than rankers who rank benchmarks based on their total 
 * score. When the scores for different problem benchmarks differ greatly 
 * (e.g. 10 v. 10000), comparing in absolute terms is misleading. 
 * 
 * For that reason, using this ranker only makes sense when there are more 
 * problem benchmarks inside one solver benchmark - otherwise, simple 
 * comparator-based ranker will do exactly the same.
 */
public class RelativePositionBenchmarkRanker implements BenchmarkRanker {

    /**
     * Compares benchmarks by how many times their scores beats other benchmarks' scores. 
     */
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

    /**
     * The comparator will only be used to decide the ranking of two benchmarks 
     * in case that their scores beat exactly the same amount of other 
     * benchmarks' scores.
     */
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

    /**
     * The benchmark with the lowest ranking will be the benchmark whose scores
     * beat most of the other benchmarks' scores.
     */
    public int getRanking(SolverBenchmark benchmark) {
        return rankedBenchmarks.indexOf(benchmark);
    }

}
