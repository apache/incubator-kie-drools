package org.optaplanner.examples.coachshuttlegathering.app;

import java.util.Collections;
import java.util.Set;

import org.optaplanner.examples.coachshuttlegathering.domain.CoachShuttleGatheringSolution;
import org.optaplanner.examples.coachshuttlegathering.persistence.CoachShuttleGatheringExporter;
import org.optaplanner.examples.coachshuttlegathering.persistence.CoachShuttleGatheringImporter;
import org.optaplanner.examples.coachshuttlegathering.persistence.CoachShuttleGatheringXmlSolutionFileIO;
import org.optaplanner.examples.coachshuttlegathering.swingui.CoachShuttleGatheringPanel;
import org.optaplanner.examples.common.app.CommonApp;
import org.optaplanner.examples.common.persistence.AbstractSolutionExporter;
import org.optaplanner.examples.common.persistence.AbstractSolutionImporter;
import org.optaplanner.persistence.common.api.domain.solution.SolutionFileIO;

public class CoachShuttleGatheringApp extends CommonApp<CoachShuttleGatheringSolution> {

    public static final String SOLVER_CONFIG =
            "org/optaplanner/examples/coachshuttlegathering/coachShuttleGatheringSolverConfig.xml";

    public static final String DATA_DIR_NAME = "coachshuttlegathering";

    public static void main(String[] args) {
        prepareSwingEnvironment();
        new CoachShuttleGatheringApp().init();
    }

    public CoachShuttleGatheringApp() {
        super("Coach shuttle gathering",
                "Transport passengers to a hub by using coaches and shuttles.",
                SOLVER_CONFIG, DATA_DIR_NAME,
                CoachShuttleGatheringPanel.LOGO_PATH);
    }

    @Override
    protected CoachShuttleGatheringPanel createSolutionPanel() {
        return new CoachShuttleGatheringPanel();
    }

    @Override
    public SolutionFileIO<CoachShuttleGatheringSolution> createSolutionFileIO() {
        return new CoachShuttleGatheringXmlSolutionFileIO();
    }

    @Override
    protected Set<AbstractSolutionImporter<CoachShuttleGatheringSolution>> createSolutionImporters() {
        return Collections.singleton(new CoachShuttleGatheringImporter());
    }

    @Override
    protected Set<AbstractSolutionExporter<CoachShuttleGatheringSolution>> createSolutionExporters() {
        return Collections.singleton(new CoachShuttleGatheringExporter());
    }

}
