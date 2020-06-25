/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.core.impl.score.comparator;

import java.util.Comparator;

import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;

/**
 * Compares 2 {@link HardSoftScore}s based on the calculation of the hard multiplied by a weight, summed with the soft.
 */
public class FlatteningHardSoftScoreComparator implements Comparator<Score> {

    private int hardWeight;

    public FlatteningHardSoftScoreComparator(int hardWeight) {
        this.hardWeight = hardWeight;
    }

    public int getHardWeight() {
        return hardWeight;
    }

    @Override
    public int compare(Score s1, Score s2) {
        HardSoftScore score1 = (HardSoftScore) s1;
        HardSoftScore score2 = (HardSoftScore) s2;
        long score1Side = (long) score1.getHardScore() * (long) hardWeight + (long) score1.getSoftScore();
        long score2Side = (long) score2.getHardScore() * (long) hardWeight + (long) score2.getSoftScore();
        return score1Side < score2Side ? -1 : (score1Side == score2Side ? 0 : 1);
    }

}
