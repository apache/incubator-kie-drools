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

import org.drools.planner.core.move.Move;
import org.drools.planner.core.move.factory.CachedMoveFactory;
import org.drools.planner.core.solution.Solution;
import org.drools.planner.examples.curriculumcourse.domain.CurriculumCourseSchedule;
import org.drools.planner.examples.curriculumcourse.domain.Lecture;
import org.drools.planner.examples.curriculumcourse.domain.Room;
import org.drools.planner.examples.curriculumcourse.solver.move.RoomChangeMove;

public class RoomChangeMoveFactory extends CachedMoveFactory {

    public List<Move> createCachedMoveList(Solution solution) {
        CurriculumCourseSchedule schedule = (CurriculumCourseSchedule) solution;
        List<Room> roomList = schedule.getRoomList();
        List<Move> moveList = new ArrayList<Move>();
        for (Lecture lecture : schedule.getLectureList()) {
            for (Room room : roomList) {
                moveList.add(new RoomChangeMove(lecture, room));
            }
        }
        return moveList;
    }

}
