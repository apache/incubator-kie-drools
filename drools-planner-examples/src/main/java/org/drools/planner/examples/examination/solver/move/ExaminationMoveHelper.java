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

import org.drools.WorkingMemory;
import org.drools.runtime.rule.FactHandle;
import org.drools.planner.examples.examination.domain.Exam;
import org.drools.planner.examples.examination.domain.Period;
import org.drools.planner.examples.examination.domain.Room;

public class ExaminationMoveHelper {

    public static void movePeriod(WorkingMemory workingMemory, Exam exam, Period period) {
        FactHandle factHandle = workingMemory.getFactHandle(exam);
        exam.setPeriod(period);
        workingMemory.update(factHandle, exam);
        movePeriodCoincidene(workingMemory, exam, period);
    }

    public static void moveRoom(WorkingMemory workingMemory, Exam exam, Room room) {
        FactHandle factHandle = workingMemory.getFactHandle(exam);
        exam.setRoom(room);
        workingMemory.update(factHandle, exam);
    }

    public static void moveExam(WorkingMemory workingMemory, Exam exam, Period period, Room room) {
        FactHandle factHandle = workingMemory.getFactHandle(exam);
        exam.setPeriod(period);
        exam.setRoom(room);
        workingMemory.update(factHandle, exam);
        movePeriodCoincidene(workingMemory, exam, period);
    }

    public static void movePeriodCoincidene(WorkingMemory workingMemory, Exam exam, Period period) {
        if (exam.getExamCoincidence() != null) {
            for (Exam coincidenceExam : exam.getExamCoincidence().getCoincidenceExamSet()) {
                if (!exam.equals(coincidenceExam)) {
                    FactHandle factHandle = workingMemory.getFactHandle(coincidenceExam);
                    coincidenceExam.setPeriod(period);
                    workingMemory.update(factHandle, coincidenceExam);
                }
            }
        }
    }

    private ExaminationMoveHelper() {
    }

}
