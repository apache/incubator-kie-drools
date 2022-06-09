package org.optaplanner.examples.flightcrewscheduling.app;

import org.optaplanner.examples.common.app.CommonApp;
import org.optaplanner.examples.flightcrewscheduling.domain.FlightCrewSolution;
import org.optaplanner.examples.flightcrewscheduling.persistence.FlightCrewSchedulingXlsxFileIO;
import org.optaplanner.examples.flightcrewscheduling.swingui.FlightCrewSchedulingPanel;
import org.optaplanner.persistence.common.api.domain.solution.SolutionFileIO;

public class FlightCrewSchedulingApp extends CommonApp<FlightCrewSolution> {

    public static final String SOLVER_CONFIG =
            "org/optaplanner/examples/flightcrewscheduling/flightCrewSchedulingSolverConfig.xml";

    public static final String DATA_DIR_NAME = "flightcrewscheduling";

    public static void main(String[] args) {
        prepareSwingEnvironment();
        new FlightCrewSchedulingApp().init();
    }

    public FlightCrewSchedulingApp() {
        super("Flight crew scheduling",
                "Assign flights to pilots and flight attendants.",
                SOLVER_CONFIG, DATA_DIR_NAME,
                FlightCrewSchedulingPanel.LOGO_PATH);
    }

    @Override
    protected FlightCrewSchedulingPanel createSolutionPanel() {
        return new FlightCrewSchedulingPanel();
    }

    @Override
    public SolutionFileIO<FlightCrewSolution> createSolutionFileIO() {
        return new FlightCrewSchedulingXlsxFileIO();
    }

}
