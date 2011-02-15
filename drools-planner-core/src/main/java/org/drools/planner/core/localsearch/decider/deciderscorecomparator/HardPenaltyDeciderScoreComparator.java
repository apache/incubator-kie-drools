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
import org.drools.planner.core.score.HardAndSoftScore;

/**
 * Compares 2 HardAndSoftScore based on the calculation the hard multiplied by a weight to the soft.
* @author Geoffrey De Smet
*/
public class HardPenaltyDeciderScoreComparator implements Comparator<Score> {

    private int hardWeight;

    public HardPenaltyDeciderScoreComparator(int hardWeight) {
        this.hardWeight = hardWeight;
    }

    public int getHardWeight() {
        return hardWeight;
    }

    public int compare(Score s1, Score s2) {
        HardAndSoftScore score1 = (HardAndSoftScore) s1;
        HardAndSoftScore score2 = (HardAndSoftScore) s2;
        int score1Side = score1.getHardScore() * hardWeight + score1.getSoftScore();
        int score2Side = score2.getHardScore() * hardWeight + score2.getSoftScore();
        return score1Side < score2Side ? -1 : (score1Side == score2Side ? 0 : 1);
    }

}