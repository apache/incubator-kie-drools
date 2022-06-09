package org.optaplanner.examples.curriculumcourse.domain.solver;

import static java.util.Comparator.comparingInt;

import java.util.Comparator;

import org.optaplanner.core.impl.heuristic.selector.common.decorator.SelectionSorterWeightFactory;
import org.optaplanner.examples.curriculumcourse.domain.CourseSchedule;
import org.optaplanner.examples.curriculumcourse.domain.Room;

public class RoomStrengthWeightFactory implements SelectionSorterWeightFactory<CourseSchedule, Room> {

    @Override
    public RoomStrengthWeight createSorterWeight(CourseSchedule schedule, Room room) {
        return new RoomStrengthWeight(room);
    }

    public static class RoomStrengthWeight implements Comparable<RoomStrengthWeight> {

        private static final Comparator<Room> COMPARATOR = comparingInt(Room::getCapacity).thenComparingLong(Room::getId);

        private final Room room;

        public RoomStrengthWeight(Room room) {
            this.room = room;
        }

        @Override
        public int compareTo(RoomStrengthWeight other) {
            return COMPARATOR.compare(room, other.room);
        }
    }
}
