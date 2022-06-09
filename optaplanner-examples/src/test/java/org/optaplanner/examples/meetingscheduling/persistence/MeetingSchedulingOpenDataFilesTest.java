package org.optaplanner.examples.meetingscheduling.persistence;

import org.optaplanner.examples.common.app.CommonApp;
import org.optaplanner.examples.common.persistence.OpenDataFilesTest;
import org.optaplanner.examples.meetingscheduling.app.MeetingSchedulingApp;
import org.optaplanner.examples.meetingscheduling.domain.MeetingSchedule;

class MeetingSchedulingOpenDataFilesTest extends OpenDataFilesTest<MeetingSchedule> {

    @Override
    protected CommonApp<MeetingSchedule> createCommonApp() {
        return new MeetingSchedulingApp();
    }
}
