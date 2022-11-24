package org.optaplanner.examples.nqueens.app;

import org.optaplanner.examples.common.app.CommonApp;
import org.optaplanner.examples.nqueens.domain.NQueens;
import org.optaplanner.examples.nqueens.persistence.NQueensSolutionFileIO;
import org.optaplanner.examples.nqueens.swingui.NQueensPanel;
import org.optaplanner.persistence.common.api.domain.solution.SolutionFileIO;

/**
 * For an easy example, look at {@link NQueensHelloWorld} instead.
 */
public class NQueensApp extends CommonApp<NQueens> {

    public static final String SOLVER_CONFIG = "org/optaplanner/examples/nqueens/nqueensSolverConfig.xml";

    public static final String DATA_DIR_NAME = "nqueens";

    public static void main(String[] args) {
        prepareSwingEnvironment();
        new NQueensApp().init();
    }

    public NQueensApp() {
        super("N queens",
                "Place queens on a chessboard.\n\n" +
                        "No 2 queens must be able to attack each other.",
                SOLVER_CONFIG, DATA_DIR_NAME,
                NQueensPanel.LOGO_PATH);
    }

    @Override
    protected NQueensPanel createSolutionPanel() {
        return new NQueensPanel();
    }

    @Override
    public SolutionFileIO<NQueens> createSolutionFileIO() {
        return new NQueensSolutionFileIO();
    }

}
