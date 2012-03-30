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

import org.drools.planner.core.score.director.ScoreDirector;
import org.drools.planner.examples.examination.domain.Exam;
import org.drools.planner.examples.examination.domain.Period;
import org.drools.planner.examples.examination.domain.Room;

public class ExaminationMoveHelper {

    public static void movePeriod(ScoreDirector scoreDirector, Exam exam, Period period) {
        scoreDirector.beforeVariableChanged(exam, "period");
        exam.setPeriod(period);
        scoreDirector.afterVariableChanged(exam, "period");

        movePeriodCoincidence(scoreDirector, exam, period);
    }

    public static void moveRoom(ScoreDirector scoreDirector, Exam exam, Room room) {
        scoreDirector.beforeVariableChanged(exam, "room");
        exam.setRoom(room);
        scoreDirector.afterVariableChanged(exam, "room");
    }

    public static void moveExam(ScoreDirector scoreDirector, Exam exam, Period period, Room room) {
        scoreDirector.beforeAllVariablesChanged(exam);
        exam.setPeriod(period);
        exam.setRoom(room);
        scoreDirector.afterAllVariablesChanged(exam);

        movePeriodCoincidence(scoreDirector, exam, period);
    }

    public static void movePeriodCoincidence(ScoreDirector scoreDirector, Exam exam, Period period) {
        if (exam.getExamCoincidence() != null) {
            for (Exam coincidenceExam : exam.getExamCoincidence().getCoincidenceExamSet()) {
                if (!exam.equals(coincidenceExam)) {
                    scoreDirector.beforeVariableChanged(coincidenceExam, "period");
                    coincidenceExam.setPeriod(period);
                    scoreDirector.afterVariableChanged(coincidenceExam, "period");
                }
            }
        }
    }

    private ExaminationMoveHelper() {
    }

}
