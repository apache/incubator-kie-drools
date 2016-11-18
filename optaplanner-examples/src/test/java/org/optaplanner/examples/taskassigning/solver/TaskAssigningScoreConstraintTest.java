/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.optaplanner.examples.taskassigning.solver;

import java.util.Arrays;

import org.junit.Test;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.examples.taskassigning.app.TaskAssigningApp;
import org.optaplanner.examples.taskassigning.domain.Customer;
import org.optaplanner.examples.taskassigning.domain.Employee;
import org.optaplanner.examples.taskassigning.domain.Priority;
import org.optaplanner.examples.taskassigning.domain.Skill;
import org.optaplanner.examples.taskassigning.domain.Task;
import org.optaplanner.examples.taskassigning.domain.TaskAssigningSolution;
import org.optaplanner.examples.taskassigning.domain.TaskOrEmployee;
import org.optaplanner.examples.taskassigning.domain.TaskType;
import org.optaplanner.test.impl.score.buildin.bendable.BendableScoreVerifier;

public class TaskAssigningScoreConstraintTest {

    private BendableScoreVerifier<TaskAssigningSolution> scoreVerifier = new BendableScoreVerifier<>(
            SolverFactory.createFromXmlResource(TaskAssigningApp.SOLVER_CONFIG));

    @Test
    public void skillRequirements() {
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
        setPreviousAndShadows(t1, e1);
        scoreVerifier.assertHardWeight("Skill requirements", 0, -1, solution);
        setPreviousAndShadows(t2, t1);
        scoreVerifier.assertHardWeight("Skill requirements", 0, -2, solution);
        setPreviousAndShadows(t3, e1);
        scoreVerifier.assertHardWeight("Skill requirements", 0, -2, solution);
    }

    private static void setPreviousAndShadows(Task task, TaskOrEmployee previous) {
        task.setPreviousTaskOrEmployee(previous);
        task.setEmployee(previous.getEmployee());
        previous.setNextTask(task);
        task.setStartTime(previous.getEndTime());
    }

}
