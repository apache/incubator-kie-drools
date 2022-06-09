package org.optaplanner.examples.taskassigning.optional.score;

import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.config.score.director.ScoreDirectorFactoryConfig;
import org.optaplanner.core.config.solver.SolverConfig;
import org.optaplanner.examples.taskassigning.app.TaskAssigningApp;
import org.optaplanner.examples.taskassigning.domain.Customer;
import org.optaplanner.examples.taskassigning.domain.Employee;
import org.optaplanner.examples.taskassigning.domain.Priority;
import org.optaplanner.examples.taskassigning.domain.Skill;
import org.optaplanner.examples.taskassigning.domain.Task;
import org.optaplanner.examples.taskassigning.domain.TaskAssigningSolution;
import org.optaplanner.examples.taskassigning.domain.TaskType;
import org.optaplanner.test.impl.score.buildin.bendable.BendableScoreVerifier;

class TaskAssigningScoreConstraintTest {

    private static final ScoreDirectorFactoryConfig SCORE_DIRECTOR_FACTORY_CONFIG = new ScoreDirectorFactoryConfig()
            .withScoreDrls("org/optaplanner/examples/taskassigning/optional/score/taskAssigningConstraints.drl");
    private static final SolverConfig SOLVER_CONFIG = SolverConfig.createFromXmlResource(TaskAssigningApp.SOLVER_CONFIG)
            .withScoreDirectorFactory(SCORE_DIRECTOR_FACTORY_CONFIG);
    private final BendableScoreVerifier<TaskAssigningSolution> scoreVerifier =
            new BendableScoreVerifier<>(SolverFactory.create(SOLVER_CONFIG));

    @Test
    void skillRequirements() {
        Skill s1 = new Skill(1L, "Law degree");
        TaskType tt1 = new TaskType(1L, "TT1", "Task type 1", 100);
        tt1.getRequiredSkillList().add(s1);
        TaskType tt2 = new TaskType(2L, "TT2", "Task type 2", 2000);
        Customer c1 = new Customer(1L, "Steel Inc");
        Employee e1 = new Employee(1L, "Ann");
        Employee e2 = new Employee(2L, "Beth");
        Employee e3 = new Employee(3L, "Carl");
        Task t1 = new Task(1L, tt1, 0, c1, 0, Priority.CRITICAL);
        Task t2 = new Task(2L, tt1, 0, c1, 0, Priority.MAJOR);
        Task t3 = new Task(3L, tt2, 0, c1, 0, Priority.MINOR);
        TaskAssigningSolution solution = new TaskAssigningSolution(0L,
                Arrays.asList(s1),
                Arrays.asList(tt1, tt2),
                Arrays.asList(c1),
                Arrays.asList(e1, e2, e3),
                Arrays.asList(t1, t2, t3));
        scoreVerifier.assertHardWeight("Skill requirements", 0, 0, solution);
        // E1: [T1]
        addTaskAndUpdateShadows(e1, 0, t1);
        scoreVerifier.assertHardWeight("Skill requirements", 0, -1, solution);
        // E1: [T1,T2]
        addTaskAndUpdateShadows(e1, 1, t2);
        scoreVerifier.assertHardWeight("Skill requirements", 0, -2, solution);
        // E1: [T3,T1,T2]
        addTaskAndUpdateShadows(e1, 0, t3);
        scoreVerifier.assertHardWeight("Skill requirements", 0, -2, solution);
    }

    private static void addTaskAndUpdateShadows(Employee employee, int index, Task task) {
        employee.getTasks().add(index, task);
        task.setEmployee(employee);
        for (int i = index; i < employee.getTasks().size(); i++) {
            Task t = employee.getTasks().get(i);
            t.setIndex(i);
            t.setStartTime(i == 0 ? 0 : employee.getTasks().get(i - 1).getEndTime());
        }
    }

}
