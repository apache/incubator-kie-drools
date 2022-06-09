package org.optaplanner.examples.batchscheduling.app;

import org.optaplanner.examples.batchscheduling.domain.BatchSchedule;
import org.optaplanner.examples.batchscheduling.swingui.BatchSchedulingPanel;
import org.optaplanner.examples.common.app.CommonApp;
import org.optaplanner.examples.common.swingui.SolutionPanel;
import org.optaplanner.persistence.common.api.domain.solution.SolutionFileIO;
import org.optaplanner.persistence.xstream.impl.domain.solution.XStreamSolutionFileIO;

public class BatchSchedulingApp extends CommonApp<BatchSchedule> {

    public static final String SOLVER_CONFIG =
            "org/optaplanner/examples/batchscheduling/batchSchedulingSolverConfig.xml";
    public static final String DATA_DIR_NAME = "batchscheduling";

    // Determines rounding logic. If fractional value is more than specified number
    // (e.g. 20), then the value is rounded off to next higher value.
    // Minimum value is 0 and maximum 100
    public static final Integer FRACTIONAL_VOLUME_PERCENTAGE = 20;

    // Scaling parameter. Lower value indicates more processing time but accurate
    // result.
    // Minimum value 1. No Maximum defined but it depends on the volume present in
    // the batches.
    // Note: This is an example of process manufacturing (not discrete
    // manufacturing). As such, variables (i.e. batch volume) needs to be split into
    // quantifiable unit.
    public static final Integer PERIODINTERVAL_IN_MINUTES = 5;

    // CURRENT_SEGMENT_NON_ALLOCATION_PENALTY and OTHER_SEGMENT_ALLOCATION_PENALTY are used to compute
    // hard0Score (and not hard1Score)
    // SELECTED_ROUTEPATH_NON_ALLOCATION_PENALTY indicates penalty applied per Batch if delay is not set for any segment in the selected RoutePath 
    // NON_SELECTED_ROUTEPATH_ALLOCATION_PENALTY indicates penalty applied per Batch if delay is set for any segment that is not the selected RoutePath 
    public static final int SELECTED_ROUTEPATH_NON_ALLOCATION_PENALTY = 1;
    public static final int NON_SELECTED_ROUTEPATH_ALLOCATION_PENALTY = 1;

    public static final String ROUTE_PATH_SEPERATOR = "---";

    public static void main(String[] args) {
        prepareSwingEnvironment();
        new BatchSchedulingApp().init();
    }

    @Override
    public SolutionFileIO<BatchSchedule> createSolutionFileIO() {
        return new XStreamSolutionFileIO<>(BatchSchedulingApp.class);
    }

    public BatchSchedulingApp() {
        super("Batch Scheduling",
                "Official competition name:" +
                        " commodity scheduling problem in a pipeline grid MRCMPSP)\n\n" +
                        "Schedule all batches.\n\n" +
                        "Minimize delivery time and maximize segments utilization.",
                SOLVER_CONFIG, DATA_DIR_NAME,
                BatchSchedulingPanel.LOGO_PATH);
    }

    @Override
    protected SolutionPanel<BatchSchedule> createSolutionPanel() {
        return new BatchSchedulingPanel();
    }
}
