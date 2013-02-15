/*
 * Copyright 2013 JBoss Inc
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

package org.drools.planner.core.score.buildin.bendable;

import java.util.Arrays;

import org.drools.planner.core.score.Score;
import org.drools.planner.core.score.holder.AbstractScoreHolder;

public class BendableScoreHolder extends AbstractScoreHolder {

    private int[] hardScores;
    private int[] softScores;

    public BendableScoreHolder(int hardScoresSize, int softScoresSize) {
        hardScores = new int[hardScoresSize];
        softScores = new int[softScoresSize];
    }

    public int getHardScoresSize() {
        return hardScores.length;
    }

    public int getHardScore(int index) {
        return hardScores[index];
    }

    public void setHardScore(int index, int hardScore) {
        hardScores[index] = hardScore;
    }

    public int getSoftScoresSize() {
        return softScores.length;
    }

    public int getSoftScore(int index) {
        return softScores[index];
    }

    public void setSoftScore(int index, int softScore) {
        softScores[index] = softScore;
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    public Score extractScore() {
        return new BendableScore(Arrays.copyOf(hardScores, hardScores.length),
                Arrays.copyOf(softScores, softScores.length));
    }

}
