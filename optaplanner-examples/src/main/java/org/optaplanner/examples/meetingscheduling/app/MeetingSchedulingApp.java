package org.optaplanner.examples.meetingscheduling.app;

import org.optaplanner.examples.common.app.CommonApp;
import org.optaplanner.examples.meetingscheduling.domain.MeetingSchedule;
import org.optaplanner.examples.meetingscheduling.persistence.MeetingSchedulingXlsxFileIO;
import org.optaplanner.examples.meetingscheduling.swingui.MeetingSchedulingPanel;
import org.optaplanner.persistence.common.api.domain.solution.SolutionFileIO;

public class MeetingSchedulingApp extends CommonApp<MeetingSchedule> {

    public static final String SOLVER_CONFIG =
            "org/optaplanner/examples/meetingscheduling/meetingSchedulingSolverConfig.xml";

    public static final String DATA_DIR_NAME = "meetingscheduling";

    public static void main(String[] args) {
        prepareSwingEnvironment();
        new MeetingSchedulingApp().init();
    }

    public MeetingSchedulingApp() {
        super("Meeting scheduling",
                "Assign meetings a starting time and a room.",
                SOLVER_CONFIG, DATA_DIR_NAME,
                MeetingSchedulingPanel.LOGO_PATH);
    }

    @Override
    protected MeetingSchedulingPanel createSolutionPanel() {
        return new MeetingSchedulingPanel();
    }

    @Override
    public SolutionFileIO<MeetingSchedule> createSolutionFileIO() {
        return new MeetingSchedulingXlsxFileIO();
    }

}
