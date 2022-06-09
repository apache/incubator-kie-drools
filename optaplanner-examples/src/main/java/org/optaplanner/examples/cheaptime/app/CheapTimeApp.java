package org.optaplanner.examples.cheaptime.app;

import java.util.Collections;
import java.util.Set;

import org.optaplanner.examples.cheaptime.domain.CheapTimeSolution;
import org.optaplanner.examples.cheaptime.persistence.CheapTimeExporter;
import org.optaplanner.examples.cheaptime.persistence.CheapTimeImporter;
import org.optaplanner.examples.cheaptime.persistence.CheapTimeXmlSolutionFileIO;
import org.optaplanner.examples.cheaptime.swingui.CheapTimePanel;
import org.optaplanner.examples.common.app.CommonApp;
import org.optaplanner.examples.common.persistence.AbstractSolutionExporter;
import org.optaplanner.examples.common.persistence.AbstractSolutionImporter;
import org.optaplanner.persistence.common.api.domain.solution.SolutionFileIO;

public class CheapTimeApp extends CommonApp<CheapTimeSolution> {

    public static final String SOLVER_CONFIG = "org/optaplanner/examples/cheaptime/cheapTimeSolverConfig.xml";

    public static final String DATA_DIR_NAME = "cheaptime";

    public static void main(String[] args) {
        prepareSwingEnvironment();
        new CheapTimeApp().init();
    }

    public CheapTimeApp() {
        super("Cheap time scheduling",
                "Official competition name: ICON Challenge on Forecasting and Scheduling\n\n" +
                        "Assign tasks to machines and time.\n\n" +
                        "Each machine must have enough hardware to run all of its tasks.\n" +
                        "Each task and machine consumes power. The power price differs over time.\n" +
                        "Minimize the power cost.",
                SOLVER_CONFIG, DATA_DIR_NAME,
                CheapTimePanel.LOGO_PATH);
    }

    @Override
    protected CheapTimePanel createSolutionPanel() {
        return new CheapTimePanel();
    }

    @Override
    public SolutionFileIO<CheapTimeSolution> createSolutionFileIO() {
        return new CheapTimeXmlSolutionFileIO();
    }

    @Override
    protected Set<AbstractSolutionImporter<CheapTimeSolution>> createSolutionImporters() {
        return Collections.singleton(new CheapTimeImporter());
    }

    @Override
    protected Set<AbstractSolutionExporter<CheapTimeSolution>> createSolutionExporters() {
        return Collections.singleton(new CheapTimeExporter());
    }

}
