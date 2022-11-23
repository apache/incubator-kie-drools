package org.optaplanner.examples.taskassigning.app;

import org.optaplanner.examples.common.app.CommonApp;
import org.optaplanner.examples.taskassigning.domain.TaskAssigningSolution;
import org.optaplanner.examples.taskassigning.persistence.TaskAssigningSolutionFileIO;
import org.optaplanner.examples.taskassigning.swingui.TaskAssigningPanel;
import org.optaplanner.persistence.common.api.domain.solution.SolutionFileIO;

public class TaskAssigningApp extends CommonApp<TaskAssigningSolution> {

    public static final String SOLVER_CONFIG =
            "org/optaplanner/examples/taskassigning/taskAssigningSolverConfig.xml";

    public static final String DATA_DIR_NAME = "taskassigning";

    public static void main(String[] args) {
        prepareSwingEnvironment();
        new TaskAssigningApp().init();
    }

    public TaskAssigningApp() {
        super("Task assigning",
                "Assign tasks to employees in a sequence.\n\n"
                        + "Match skills and affinity.\n"
                        + "Prioritize critical tasks.\n"
                        + "Minimize the makespan.",
                SOLVER_CONFIG, DATA_DIR_NAME,
                TaskAssigningPanel.LOGO_PATH);
    }

    @Override
    protected TaskAssigningPanel createSolutionPanel() {
        return new TaskAssigningPanel();
    }

    @Override
    public SolutionFileIO<TaskAssigningSolution> createSolutionFileIO() {
        return new TaskAssigningSolutionFileIO();
    }

}
