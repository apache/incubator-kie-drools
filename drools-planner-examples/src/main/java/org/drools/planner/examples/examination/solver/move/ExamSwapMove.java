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

package org.drools.planner.examples.examination.solver.move;

import java.util.Arrays;
import java.util.Collection;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.drools.planner.core.move.Move;
import org.drools.planner.core.score.director.ScoreDirector;
import org.drools.planner.examples.examination.domain.Exam;
import org.drools.planner.examples.examination.domain.Period;
import org.drools.planner.examples.examination.domain.Room;

public class ExamSwapMove implements Move {

    private Exam leftExam;
    private Exam rightExam;

    public ExamSwapMove(Exam leftExam, Exam rightExam) {
        this.leftExam = leftExam;
        this.rightExam = rightExam;
    }

    public boolean isMoveDoable(ScoreDirector scoreDirector) {
        return !(ObjectUtils.equals(leftExam.getPeriod(), rightExam.getPeriod())
                && ObjectUtils.equals(leftExam.getRoom(), rightExam.getRoom()));
    }

    public Move createUndoMove(ScoreDirector scoreDirector) {
        return new ExamSwapMove(rightExam, leftExam);
    }

    public void doMove(ScoreDirector scoreDirector) {
        Period oldLeftPeriod = leftExam.getPeriod();
        Period oldRightPeriod = rightExam.getPeriod();
        Room oldLeftRoom = leftExam.getRoom();
        Room oldRightRoom = rightExam.getRoom();
        if (oldLeftPeriod.equals(oldRightPeriod)) {
            ExaminationMoveHelper.moveRoom(scoreDirector, leftExam, oldRightRoom);
            ExaminationMoveHelper.moveRoom(scoreDirector, rightExam, oldLeftRoom);
        } else if (oldLeftRoom.equals(oldRightRoom)) {
            ExaminationMoveHelper.movePeriod(scoreDirector, leftExam, oldRightPeriod);
            ExaminationMoveHelper.movePeriod(scoreDirector, rightExam, oldLeftPeriod);
        } else {
            ExaminationMoveHelper.moveExam(scoreDirector, leftExam, oldRightPeriod, oldRightRoom);
            ExaminationMoveHelper.moveExam(scoreDirector, rightExam, oldLeftPeriod, oldLeftRoom);
        }
    }

    public Collection<? extends Object> getPlanningEntities() {
        return Arrays.asList(leftExam, rightExam);
    }

    public Collection<? extends Object> getPlanningValues() {
        return Arrays.<Object>asList(leftExam.getPeriod(), leftExam.getRoom(), rightExam.getPeriod(), rightExam.getRoom());
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o instanceof ExamSwapMove) {
            ExamSwapMove other = (ExamSwapMove) o;
            return new EqualsBuilder()
                    .append(leftExam, other.leftExam)
                    .append(rightExam, other.rightExam)
                    .isEquals();
        } else {
            return false;
        }
    }

    public int hashCode() {
        return new HashCodeBuilder()
                .append(leftExam)
                .append(rightExam)
                .toHashCode();
    }

    public String toString() {
        return leftExam + " <=> " + rightExam;
    }

}
