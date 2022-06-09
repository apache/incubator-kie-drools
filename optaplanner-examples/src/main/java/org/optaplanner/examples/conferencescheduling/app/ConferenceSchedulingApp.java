
package org.optaplanner.examples.conferencescheduling.app;

import org.optaplanner.examples.common.app.CommonApp;
import org.optaplanner.examples.conferencescheduling.domain.ConferenceSolution;
import org.optaplanner.examples.conferencescheduling.persistence.ConferenceSchedulingXlsxFileIO;
import org.optaplanner.examples.conferencescheduling.swingui.ConferenceCFPImportAction;
import org.optaplanner.examples.conferencescheduling.swingui.ConferenceSchedulingPanel;
import org.optaplanner.persistence.common.api.domain.solution.SolutionFileIO;

public class ConferenceSchedulingApp extends CommonApp<ConferenceSolution> {

    public static final String SOLVER_CONFIG =
            "org/optaplanner/examples/conferencescheduling/conferenceSchedulingSolverConfig.xml";

    public static final String DATA_DIR_NAME = "conferencescheduling";

    public static void main(String[] args) {
        prepareSwingEnvironment();
        new ConferenceSchedulingApp().init();
    }

    public ConferenceSchedulingApp() {
        super("Conference scheduling",
                "Assign conference talks to a timeslot and a room.",
                SOLVER_CONFIG, DATA_DIR_NAME,
                ConferenceSchedulingPanel.LOGO_PATH);
    }

    @Override
    protected ConferenceSchedulingPanel createSolutionPanel() {
        return new ConferenceSchedulingPanel();
    }

    @Override
    public SolutionFileIO<ConferenceSolution> createSolutionFileIO() {
        return new ConferenceSchedulingXlsxFileIO();
    }

    @Override
    protected ExtraAction<ConferenceSolution>[] createExtraActions() {
        return new ExtraAction[] { new ConferenceCFPImportAction() };
    }
}
