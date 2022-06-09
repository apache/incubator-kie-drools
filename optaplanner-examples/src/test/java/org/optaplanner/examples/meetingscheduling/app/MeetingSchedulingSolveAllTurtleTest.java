package org.optaplanner.examples.meetingscheduling.app;

import org.optaplanner.examples.common.app.CommonApp;
import org.optaplanner.examples.common.app.UnsolvedDirSolveAllTurtleTest;
import org.optaplanner.examples.meetingscheduling.domain.MeetingSchedule;

class MeetingSchedulingSolveAllTurtleTest extends UnsolvedDirSolveAllTurtleTest<MeetingSchedule> {

    @Override
    protected CommonApp<MeetingSchedule> createCommonApp() {
        return new MeetingSchedulingApp();
    }
}
