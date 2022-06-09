package org.optaplanner.examples.taskassigning.app;

import java.util.stream.Stream;

import org.optaplanner.core.api.score.buildin.bendable.BendableScore;
import org.optaplanner.core.config.solver.EnvironmentMode;
import org.optaplanner.examples.common.app.SolverPerformanceTest;
import org.optaplanner.examples.taskassigning.domain.TaskAssigningSolution;

class TaskAssigningPerformanceTest extends SolverPerformanceTest<TaskAssigningSolution, BendableScore> {

    private static final String UNSOLVED_DATA_FILE = "data/taskassigning/unsolved/50tasks-5employees.xml";

    @Override
    protected TaskAssigningApp createCommonApp() {
        return new TaskAssigningApp();
    }

    @Override
    protected Stream<TestData<BendableScore>> testData() {
        return Stream.of(
                testData(UNSOLVED_DATA_FILE, BendableScore.of(new int[] { 0 },
                        new int[] { -3925, -6293940, -7784, -20600 }), EnvironmentMode.REPRODUCIBLE),
                testData(UNSOLVED_DATA_FILE, BendableScore.of(new int[] { 0 },
                        new int[] { -3925, -6293940, -7883, -20476 }), EnvironmentMode.FAST_ASSERT));
    }
}
