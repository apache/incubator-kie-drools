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

package org.drools.planner.examples.examination.solver.move.factory;

import java.util.ArrayList;
import java.util.List;

import org.drools.planner.core.move.Move;
import org.drools.planner.core.move.factory.CachedMoveFactory;
import org.drools.planner.core.solution.Solution;
import org.drools.planner.examples.examination.domain.Exam;
import org.drools.planner.examples.examination.domain.Examination;
import org.drools.planner.examples.examination.domain.Period;
import org.drools.planner.examples.examination.solver.move.PeriodChangeMove;

public class PeriodChangeMoveFactory extends CachedMoveFactory {

    public List<Move> createCachedMoveList(Solution solution) {
        Examination examination = (Examination) solution;
        List<Period> periodList = examination.getPeriodList();
        List<Move> moveList = new ArrayList<Move>();
        for (Exam exam : examination.getExamList()) {
            if (exam.isCoincidenceLeader()) {
                for (Period period : periodList) {
                    moveList.add(new PeriodChangeMove(exam, period));
                }
            }
        }
        return moveList;
    }

}
