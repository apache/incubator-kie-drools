package org.optaplanner.examples.rocktour.app;

import org.optaplanner.examples.common.app.CommonApp;
import org.optaplanner.examples.rocktour.domain.RockTourSolution;
import org.optaplanner.examples.rocktour.persistence.RockTourXlsxFileIO;
import org.optaplanner.examples.rocktour.swingui.RockTourPanel;
import org.optaplanner.persistence.common.api.domain.solution.SolutionFileIO;

public class RockTourApp extends CommonApp<RockTourSolution> {

    public static final String SOLVER_CONFIG = "org/optaplanner/examples/rocktour/rockTourSolverConfig.xml";

    public static final String DATA_DIR_NAME = "rocktour";

    public static void main(String[] args) {
        prepareSwingEnvironment();
        new RockTourApp().init();
    }

    public RockTourApp() {
        super("Rock tour",
                "Plan the most profitable and ecological rock tour.",
                SOLVER_CONFIG, DATA_DIR_NAME,
                RockTourPanel.LOGO_PATH);
    }

    @Override
    protected RockTourPanel createSolutionPanel() {
        return new RockTourPanel();
    }

    @Override
    public SolutionFileIO<RockTourSolution> createSolutionFileIO() {
        return new RockTourXlsxFileIO();
    }

}
