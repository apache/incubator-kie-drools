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

package org.drools.planner.examples.curriculumcourse.solver.move.factory;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import org.drools.planner.core.move.Move;
import org.drools.planner.core.move.factory.CachedMoveFactory;
import org.drools.planner.core.solution.Solution;
import org.drools.planner.examples.curriculumcourse.domain.CurriculumCourseSchedule;
import org.drools.planner.examples.curriculumcourse.domain.Lecture;
import org.drools.planner.examples.curriculumcourse.solver.move.LectureSwitchMove;

public class LectureSwitchMoveFactory extends CachedMoveFactory {

    public List<Move> createCachedMoveList(Solution solution) {
        CurriculumCourseSchedule schedule = (CurriculumCourseSchedule) solution;
        List<Lecture> lectureList = schedule.getLectureList();
        List<Move> moveList = new ArrayList<Move>();
        for (ListIterator<Lecture> leftIt = lectureList.listIterator(); leftIt.hasNext();) {
            Lecture leftLecture = leftIt.next();
            for (ListIterator<Lecture> rightIt = lectureList.listIterator(leftIt.nextIndex()); rightIt.hasNext();) {
                Lecture rightLecture = rightIt.next();
                if (!leftLecture.getCourse().equals(rightLecture.getCourse())) {
                    moveList.add(new LectureSwitchMove(leftLecture, rightLecture));
                }
            }
        }
        return moveList;
    }

}
