/*
 * Copyright 2011 JBoss Inc
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

package org.drools.planner.examples.nqueens.domain.solution;

import org.apache.commons.lang.builder.CompareToBuilder;
import org.drools.planner.api.domain.entity.PlanningEntityDifficultyWeightFactory;
import org.drools.planner.core.solution.Solution;
import org.drools.planner.examples.curriculumcourse.domain.Course;
import org.drools.planner.examples.nqueens.domain.NQueens;
import org.drools.planner.examples.nqueens.domain.Queen;

public class QueenDifficultyWeightFactory implements PlanningEntityDifficultyWeightFactory {

    public Comparable createDifficultyWeight(Solution solution, Object planningEntity) {
        NQueens nQueens = (NQueens) solution;
        int n = nQueens.getN();
        Queen queen = (Queen) planningEntity;
        int x = queen.getX();
        int distanceFromMiddle = calculateDistanceFromMiddle(n, x);
        return new QueenDifficultyWeight(queen, distanceFromMiddle);
    }

    private static int calculateDistanceFromMiddle(int n, int x) {
        int middle = n / 2;
        int distanceFromMiddle = Math.abs(x - middle);
        if ((n % 2 == 0) && (x < middle)) {
            distanceFromMiddle--;
        }
        return distanceFromMiddle;
    }

    public static class QueenDifficultyWeight implements Comparable<QueenDifficultyWeight> {

        private final Queen queen;
        private final int distanceFromMiddle;

        public QueenDifficultyWeight(Queen queen, int distanceFromMiddle) {
            this.queen = queen;
            this.distanceFromMiddle = distanceFromMiddle;
        }

        public int compareTo(QueenDifficultyWeight other) {
            return new CompareToBuilder()
                    .append(distanceFromMiddle, other.distanceFromMiddle)
                    .append(queen.getX(), other.queen.getX())
                    .append(queen.getId(), other.queen.getId())
                    .toComparison();
        }

    }

}
