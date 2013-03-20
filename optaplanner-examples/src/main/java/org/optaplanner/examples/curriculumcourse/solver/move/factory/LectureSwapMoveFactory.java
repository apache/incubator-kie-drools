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

package org.optaplanner.examples.curriculumcourse.solver.move.factory;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import org.optaplanner.core.impl.heuristic.selector.move.factory.MoveListFactory;
import org.optaplanner.core.impl.move.Move;
import org.optaplanner.core.impl.solution.Solution;
import org.optaplanner.examples.curriculumcourse.domain.CourseSchedule;
import org.optaplanner.examples.curriculumcourse.domain.Lecture;
import org.optaplanner.examples.curriculumcourse.solver.move.LectureSwapMove;

public class LectureSwapMoveFactory implements MoveListFactory {

    public List<Move> createMoveList(Solution solution) {
        CourseSchedule schedule = (CourseSchedule) solution;
        List<Lecture> lectureList = schedule.getLectureList();
        List<Move> moveList = new ArrayList<Move>();
        for (ListIterator<Lecture> leftIt = lectureList.listIterator(); leftIt.hasNext();) {
            Lecture leftLecture = leftIt.next();
            for (ListIterator<Lecture> rightIt = lectureList.listIterator(leftIt.nextIndex()); rightIt.hasNext();) {
                Lecture rightLecture = rightIt.next();
                if (!leftLecture.getCourse().equals(rightLecture.getCourse())) {
                    moveList.add(new LectureSwapMove(leftLecture, rightLecture));
                }
            }
        }
        return moveList;
    }

}
