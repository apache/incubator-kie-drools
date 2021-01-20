/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.kie.kogito.taskassigning.core;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Set;

import org.kie.kogito.taskassigning.core.model.ChainElement;
import org.kie.kogito.taskassigning.core.model.OrganizationalEntity;
import org.kie.kogito.taskassigning.core.model.Task;
import org.kie.kogito.taskassigning.core.model.TaskAssigningSolution;
import org.kie.kogito.taskassigning.core.model.TaskAssignment;
import org.kie.kogito.taskassigning.core.model.User;
import org.kie.kogito.taskassigning.core.model.solver.DefaultTaskAssigningConstraintProvider;
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.config.constructionheuristic.ConstructionHeuristicPhaseConfig;
import org.optaplanner.core.config.constructionheuristic.ConstructionHeuristicType;
import org.optaplanner.core.config.localsearch.LocalSearchPhaseConfig;
import org.optaplanner.core.config.score.director.ScoreDirectorFactoryConfig;
import org.optaplanner.core.config.solver.SolverConfig;
import org.optaplanner.core.config.solver.termination.TerminationConfig;
import org.optaplanner.persistence.xstream.impl.domain.solution.XStreamSolutionFileIO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractTaskAssigningCoreTest {

    protected final Logger LOGGER = LoggerFactory.getLogger(getClass());

    protected boolean writeTestFiles() {
        return Boolean.parseBoolean(System.getProperty("org.kie.kogito.taskassigning.test.writeFiles", "false"));
    }

    protected SolverConfig createBaseConfig() {
        SolverConfig config = new SolverConfig();
        config.setSolutionClass(TaskAssigningSolution.class);
        config.setEntityClassList(Arrays.asList(ChainElement.class, TaskAssignment.class));
        config.setScoreDirectorFactoryConfig(new ScoreDirectorFactoryConfig().withConstraintProviderClass(DefaultTaskAssigningConstraintProvider.class));
        return config;
    }

    protected Solver<TaskAssigningSolution> createDaemonSolver() {
        SolverConfig config = createBaseConfig();
        config.setDaemon(true);
        SolverFactory<TaskAssigningSolution> solverFactory = SolverFactory.create(config);
        return solverFactory.buildSolver();
    }

    protected Solver<TaskAssigningSolution> createNonDaemonSolver(int stepCountLimit) {
        SolverConfig config = createBaseConfig();
        ConstructionHeuristicPhaseConfig constructionHeuristicPhaseConfig = new ConstructionHeuristicPhaseConfig();
        constructionHeuristicPhaseConfig.setConstructionHeuristicType(ConstructionHeuristicType.FIRST_FIT);
        LocalSearchPhaseConfig phaseConfig = new LocalSearchPhaseConfig();
        phaseConfig.setTerminationConfig(new TerminationConfig().withStepCountLimit(stepCountLimit));
        config.setPhaseConfigList(Arrays.asList(constructionHeuristicPhaseConfig, phaseConfig));
        SolverFactory<TaskAssigningSolution> solverFactory = SolverFactory.create(config);
        return solverFactory.buildSolver();
    }

    protected TaskAssigningSolution readTaskAssigningSolution(String resource) {
        File resourceFile = Paths.get(getClass().getResource(resource).getPath()).toFile();
        XStreamSolutionFileIO<TaskAssigningSolution> solutionFileIO = new XStreamSolutionFileIO<>(TaskAssigningSolution.class);
        return solutionFileIO.read(resourceFile);
    }

    private static void appendln(StringBuilder builder) {
        builder.append('\n');
    }

    private static void appendln(StringBuilder builder, String text) {
        builder.append(text);
        appendln(builder);
    }

    public static void printSolution(TaskAssigningSolution solution, StringBuilder builder) {
        solution.getUserList().forEach(user -> {
            appendln(builder, "------------------------------------------");
            appendln(builder, printUser(user));
            appendln(builder, "------------------------------------------");
            appendln(builder);
            TaskAssignment taskAssignment = user.getNextElement();
            while (taskAssignment != null) {
                builder.append(" -> ");
                appendln(builder, printTask(taskAssignment));
                taskAssignment = taskAssignment.getNextElement();
                if (taskAssignment != null) {
                    appendln(builder);
                }
            }
            appendln(builder);
        });
    }

    public static String printSolution(TaskAssigningSolution solution) {
        StringBuilder builder = new StringBuilder();
        printSolution(solution, builder);
        return builder.toString();
    }

    public static String printUser(User user) {
        return "User{" +
                "id='" + user.getId() + '\'' +
                ", groups=" + printOrganizationalEntities(user.getGroups()) +
                '}';
    }

    public static String printTask(TaskAssignment taskAssignment) {
        StringBuilder builder = new StringBuilder();
        Task task = taskAssignment.getTask();
        builder.append(task.getName() +
                               ", pinned: " + taskAssignment.isPinned() +
                               ", priority: " + task.getPriority() +
                               ", startTimeInMinutes: " + taskAssignment.getStartTimeInMinutes() +
                               ", durationInMinutes:" + taskAssignment.getDurationInMinutes() +
                               ", endTimeInMinutes: " + taskAssignment.getEndTimeInMinutes() +
                               ", user: " + taskAssignment.getUser().getId() +
                               ", potentialOwners: " + task.getPotentialUsers());
        return builder.toString();
    }

    public static String printOrganizationalEntities(Set<? extends OrganizationalEntity> potentialOwners) {
        StringBuilder builder = new StringBuilder();
        builder.append("{");
        if (potentialOwners != null) {
            potentialOwners.forEach(organizationalEntity -> {
                if (builder.length() > 1) {
                    builder.append(", ");
                }
                if (organizationalEntity.isUser()) {
                    builder.append("user = " + organizationalEntity.getId());
                } else {
                    builder.append("group = " + organizationalEntity.getId());
                }
            });
        }
        builder.append("}");
        return builder.toString();
    }

    public static void writeToTempFile(String fileName, String content) throws IOException {
        File tmpFile = File.createTempFile(fileName, null);
        Files.write(tmpFile.toPath(), content.getBytes());
    }
}