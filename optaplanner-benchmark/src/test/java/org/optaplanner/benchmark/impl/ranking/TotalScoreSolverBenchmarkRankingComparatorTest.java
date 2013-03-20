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

public class TotalScoreSolverBenchmarkRankingComparatorTest extends AbstractRankingComparatorTest {

    @Test
    public void normal() {
        TotalScoreSolverBenchmarkRankingComparator comparator = new TotalScoreSolverBenchmarkRankingComparator();
        SolverBenchmark a = new SolverBenchmark(null);
        List<SingleBenchmark> aSingleBenchmarkList = new ArrayList<SingleBenchmark>();
        addSingleBenchmark(aSingleBenchmarkList, -1000, -30, -1000);
        addSingleBenchmark(aSingleBenchmarkList, -400, -30, -1000);
        addSingleBenchmark(aSingleBenchmarkList, -30, -30, -1000);
        a.setSingleBenchmarkList(aSingleBenchmarkList);
        a.benchmarkingEnded();
        SolverBenchmark b = new SolverBenchmark(null);
        List<SingleBenchmark> bSingleBenchmarkList = new ArrayList<SingleBenchmark>();
        addSingleBenchmark(bSingleBenchmarkList, -1000, -50, -1000);
        addSingleBenchmark(bSingleBenchmarkList, -200, -50, -1000);
        addSingleBenchmark(bSingleBenchmarkList, -50, -50, -1000);
        b.setSingleBenchmarkList(bSingleBenchmarkList);
        b.benchmarkingEnded();
        assertEquals(-1, comparator.compare(a, b));
        assertEquals(1, comparator.compare(b, a));
    }

    @Test
    public void totalIsEqual() {
        TotalScoreSolverBenchmarkRankingComparator comparator = new TotalScoreSolverBenchmarkRankingComparator();
        SolverBenchmark a = new SolverBenchmark(null);
        List<SingleBenchmark> aSingleBenchmarkList = new ArrayList<SingleBenchmark>();
        addSingleBenchmark(aSingleBenchmarkList, -1005, -30, -1005);
        addSingleBenchmark(aSingleBenchmarkList, -200, -30, -1005);
        addSingleBenchmark(aSingleBenchmarkList, -30, -30, -1005);
        a.setSingleBenchmarkList(aSingleBenchmarkList);
        a.benchmarkingEnded();
        SolverBenchmark b = new SolverBenchmark(null);
        List<SingleBenchmark> bSingleBenchmarkList = new ArrayList<SingleBenchmark>();
        addSingleBenchmark(bSingleBenchmarkList, -1000, -35, -1000);
        addSingleBenchmark(bSingleBenchmarkList, -200, -35, -1000);
        addSingleBenchmark(bSingleBenchmarkList, -35, -35, -1000);
        b.setSingleBenchmarkList(bSingleBenchmarkList);
        b.benchmarkingEnded();
        assertEquals(-1, comparator.compare(a, b));
        assertEquals(1, comparator.compare(b, a));
    }

}
