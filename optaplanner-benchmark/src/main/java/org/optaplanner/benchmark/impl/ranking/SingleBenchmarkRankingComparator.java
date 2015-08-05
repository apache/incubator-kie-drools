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

import java.io.Serializable;
import java.util.Comparator;

import org.apache.commons.lang3.builder.CompareToBuilder;
import org.optaplanner.benchmark.impl.result.SingleBenchmarkResult;
import org.optaplanner.core.api.score.Score;

public class SingleBenchmarkRankingComparator implements Comparator<SingleBenchmarkResult>, Serializable {

    private final Comparator<Score> resilientScoreComparator = new ResilientScoreComparator();

    @Override
    public int compare(SingleBenchmarkResult a, SingleBenchmarkResult b) {
        return new CompareToBuilder()
                .append(b.isFailure(), a.isFailure()) // Reverse, less is better (redundant: failed benchmarks don't get ranked at all)
                .append(b.getUninitializedVariableCount(), a.getUninitializedVariableCount()) // Reverse, less is better
                .append(a.getScore(), b.getScore(), resilientScoreComparator)
                .toComparison();
    }

}
