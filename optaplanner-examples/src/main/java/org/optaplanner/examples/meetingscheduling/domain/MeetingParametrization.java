/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.optaplanner.examples.meetingscheduling.domain;

import org.optaplanner.examples.common.domain.AbstractPersistable;

public class MeetingParametrization extends AbstractPersistable {

    public static final String ROOM_CONFLICT = "Room conflict";
    public static final String DONT_GO_IN_OVERTIME = "Don't go in overtime";
    public static final String REQUIRED_ATTENDANCE_CONFLICT = "Required attendance conflict";
    public static final String REQUIRED_ROOM_CAPACITY = "Required room capacity";
    public static final String START_AND_END_ON_SAME_DAY = "Start and end on same day";

    public static final String REQUIRED_AND_PREFERRED_ATTENDANCE_CONFLICT = "Required and preferred attendance conflict";
    public static final String PREFERRED_ATTENDANCE_CONFLICT = "Preferred attendance conflict";

    public static final String DO_ALL_MEETINGS_AS_SOON_AS_POSSIBLE = "Do all meetings as soon as possible";
    public static final String ONE_TIME_GRAIN_BREAK_BETWEEN_TWO_CONSECUTIVE_MEETINGS = "One TimeGrain break between two consecutive meetings";
    public static final String OVERLAPPING_MEETINGS = "Overlapping meetings";
    public static final String ASSIGN_LARGER_ROOMS_FIRST = "Assign larger rooms first";
    public static final String ROOM_STABILITY = "Room stability";

    private int roomConflict = 1;
    private int dontGoInOvertime = 1;
    private int requiredAttendanceConflict = 1;
    private int requiredRoomCapacity = 1;
    private int startAndEndOnSameDay = 1;

    private int requiredAndPreferredAttendanceConflict = 1;
    private int preferredAttendanceConflict = 1;

    private int doAllMeetingsAsSoonAsPossible = 1;
    private int oneTimeGrainBreakBetweenTwoConsecutiveMeetings = 100;
    private int overlappingMeetings = 10;
    private int assignLargerRoomsFirst = 1;
    private int roomStability = 1;

    public MeetingParametrization() {
    }

    public MeetingParametrization(long id) {
        super(id);
    }

    // ************************************************************************
    // Simple getters and setters
    // ************************************************************************

    public int getRoomConflict() {
        return roomConflict;
    }

    public void setRoomConflict(int roomConflict) {
        this.roomConflict = roomConflict;
    }

    public int getDontGoInOvertime() {
        return dontGoInOvertime;
    }

    public void setDontGoInOvertime(int dontGoInOvertime) {
        this.dontGoInOvertime = dontGoInOvertime;
    }

    public int getRequiredAttendanceConflict() {
        return requiredAttendanceConflict;
    }

    public void setRequiredAttendanceConflict(int requiredAttendanceConflict) {
        this.requiredAttendanceConflict = requiredAttendanceConflict;
    }

    public int getRequiredRoomCapacity() {
        return requiredRoomCapacity;
    }

    public void setRequiredRoomCapacity(int requiredRoomCapacity) {
        this.requiredRoomCapacity = requiredRoomCapacity;
    }

    public int getStartAndEndOnSameDay() {
        return startAndEndOnSameDay;
    }

    public void setStartAndEndOnSameDay(int startAndEndOnSameDay) {
        this.startAndEndOnSameDay = startAndEndOnSameDay;
    }

    public int getRequiredAndPreferredAttendanceConflict() {
        return requiredAndPreferredAttendanceConflict;
    }

    public void setRequiredAndPreferredAttendanceConflict(int requiredAndPreferredAttendanceConflict) {
        this.requiredAndPreferredAttendanceConflict = requiredAndPreferredAttendanceConflict;
    }

    public int getPreferredAttendanceConflict() {
        return preferredAttendanceConflict;
    }

    public void setPreferredAttendanceConflict(int preferredAttendanceConflict) {
        this.preferredAttendanceConflict = preferredAttendanceConflict;
    }

    public int getDoAllMeetingsAsSoonAsPossible() {
        return doAllMeetingsAsSoonAsPossible;
    }

    public void setDoAllMeetingsAsSoonAsPossible(int doAllMeetingsAsSoonAsPossible) {
        this.doAllMeetingsAsSoonAsPossible = doAllMeetingsAsSoonAsPossible;
    }

    public int getOneTimeGrainBreakBetweenTwoConsecutiveMeetings() {
        return oneTimeGrainBreakBetweenTwoConsecutiveMeetings;
    }

    public void setOneTimeGrainBreakBetweenTwoConsecutiveMeetings(int oneTimeGrainBreakBetweenTwoConsecutiveMeetings) {
        this.oneTimeGrainBreakBetweenTwoConsecutiveMeetings = oneTimeGrainBreakBetweenTwoConsecutiveMeetings;
    }

    public int getOverlappingMeetings() {
        return overlappingMeetings;
    }

    public void setOverlappingMeetings(int overlappingMeetings) {
        this.overlappingMeetings = overlappingMeetings;
    }

    public int getAssignLargerRoomsFirst() {
        return assignLargerRoomsFirst;
    }

    public void setAssignLargerRoomsFirst(int assignLargerRoomsFirst) {
        this.assignLargerRoomsFirst = assignLargerRoomsFirst;
    }

    public int getRoomStability() {
        return roomStability;
    }

    public void setRoomStability(int roomStability) {
        this.roomStability = roomStability;
    }
}
