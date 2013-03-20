/*
 * Copyright 2012 JBoss Inc
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

package org.optaplanner.benchmark.impl.ranking;

import java.util.ArrayList;
import java.util.List;

import org.optaplanner.benchmark.impl.SingleBenchmark;
import org.optaplanner.benchmark.impl.SolverBenchmark;
import org.junit.Test;

import static org.junit.Assert.*;

public class WorstScoreSolverBenchmarkRankingComparatorTest extends AbstractRankingComparatorTest {

    @Test
    public void normal() {
        WorstScoreSolverBenchmarkRankingComparator comparator = new WorstScoreSolverBenchmarkRankingComparator();
        SolverBenchmark a = new SolverBenchmark(null);
        List<SingleBenchmark> aSingleBenchmarkList = new ArrayList<SingleBenchmark>();
        addSingleBenchmark(aSingleBenchmarkList, -100, -30, -2001);
        addSingleBenchmark(aSingleBenchmarkList, -2001, -30, -2001);
        addSingleBenchmark(aSingleBenchmarkList, -30, -30, -2001);
        a.setSingleBenchmarkList(aSingleBenchmarkList);
        a.benchmarkingEnded();
        SolverBenchmark b = new SolverBenchmark(null);
        List<SingleBenchmark> bSingleBenchmarkList = new ArrayList<SingleBenchmark>();
        addSingleBenchmark(bSingleBenchmarkList, -900, -30, -2000);
        addSingleBenchmark(bSingleBenchmarkList, -2000, -30, -2000);
        addSingleBenchmark(bSingleBenchmarkList, -30, -30, -2000);
        b.setSingleBenchmarkList(bSingleBenchmarkList);
        b.benchmarkingEnded();
        assertEquals(-1, comparator.compare(a, b));
        assertEquals(1, comparator.compare(b, a));
    }

    @Test
    public void worstIsEqual() {
        WorstScoreSolverBenchmarkRankingComparator comparator = new WorstScoreSolverBenchmarkRankingComparator();
        SolverBenchmark a = new SolverBenchmark(null);
        List<SingleBenchmark> aSingleBenchmarkList = new ArrayList<SingleBenchmark>();
        addSingleBenchmark(aSingleBenchmarkList, -101, -30, -2000);
        addSingleBenchmark(aSingleBenchmarkList, -2000, -30, -2000);
        addSingleBenchmark(aSingleBenchmarkList, -30, -30, -2000);
        a.setSingleBenchmarkList(aSingleBenchmarkList);
        a.benchmarkingEnded();
        SolverBenchmark b = new SolverBenchmark(null);
        List<SingleBenchmark> bSingleBenchmarkList = new ArrayList<SingleBenchmark>();
        addSingleBenchmark(bSingleBenchmarkList, -100, -40, -2000);
        addSingleBenchmark(bSingleBenchmarkList, -2000, -40, -2000);
        addSingleBenchmark(bSingleBenchmarkList, -40, -40, -2000);
        b.setSingleBenchmarkList(bSingleBenchmarkList);
        b.benchmarkingEnded();
        assertEquals(-1, comparator.compare(a, b));
        assertEquals(1, comparator.compare(b, a));
    }

}
