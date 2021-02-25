/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.kogito.taskassigning.service;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicReference;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.eclipse.microprofile.context.ManagedExecutor;
import org.kie.kogito.taskassigning.core.model.TaskAssigningSolution;
import org.kie.kogito.taskassigning.service.config.TaskAssigningConfig;
import org.kie.kogito.taskassigning.service.config.TaskAssigningConfigValidator;
import org.kie.kogito.taskassigning.user.service.api.UserServiceConnector;
import org.optaplanner.core.api.solver.SolverFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.Startup;

/**
 * Class in experimental status! don't waste time here!
 * The only objective by now is to be sure the tasks can be consumed and the solver started.
 */
@ApplicationScoped
@Startup
public class TaskAssigningService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TaskAssigningService.class);

    @Inject
    SolverFactory<TaskAssigningSolution> solverFactory;

    @Inject
    TaskAssigningConfig config;

    @Inject
    ManagedExecutor managedExecutor;

    @Inject
    TaskServiceConnector taskServiceConnector;

    @Inject
    UserServiceConnector userServiceConnector;

    SolverExecutor solverExecutor;

    SolutionDataLoader solutionDataLoader;

    AtomicReference<String> serviceStatus = new AtomicReference<>("Stopped");

    int totalChances = 3;

    @PostConstruct
    void start() {
        serviceStatus.set("Starting");
        startUpValidation();
        solverExecutor = new SolverExecutor(solverFactory, solution -> {
            serviceStatus.set("Solution produced!");
            LOGGER.debug("A new solution has been produced {}", solution);
        });

        managedExecutor.execute(solverExecutor);
        solutionDataLoader = new SolutionDataLoader(taskServiceConnector,
                userServiceConnector,
                Duration.ofMillis(5000));
        managedExecutor.execute(solutionDataLoader);
        solutionDataLoader.start(this::processTaskLoadResult, 1);
    }

    // use the observer instead of the @PreDestroy alternative.
    // https://github.com/quarkusio/quarkus/issues/15026
    void onShutDownEvent(@Observes ShutdownEvent ev) {
        destroy();
    }

    void destroy() {
        try {
            serviceStatus.set("Destroying");
            LOGGER.info("Service is going down and will be destroyed.");
            solverExecutor.destroy();
            solutionDataLoader.destroy();
            LOGGER.info("Service destroy sequence was executed successfully.");
        } catch (Throwable e) {
            LOGGER.error("An error was produced during service destroy, but it'll go down anyway.", e);
        }
    }

    public String getStatus() {
        return serviceStatus.get();
    }

    private void processTaskLoadResult(SolutionDataLoader.Result result) {
        if (result.hasErrors()) {
            LOGGER.error("The following error was produced during initial solution loading", result.getErrors().get(0));

            if (totalChances-- > 0) {
                LOGGER.debug("Initial solution load failed but we have totalChances {} to retry", totalChances);
                solutionDataLoader.start(this::processTaskLoadResult, 1);
            } else {
                LOGGER.debug("There are no more chances left for starting the solution, service won't be able to start");
                solutionDataLoader.destroy();
                solverExecutor.destroy();
            }

        } else {
            LOGGER.debug("Data loading successful: tasks: {}, users: {}", result.getTasks().size(), result.getUsers().size());
            TaskAssigningSolution solution = SolutionBuilder.newBuilder()
                    .withTasks(result.getTasks())
                    .withUsers(result.getUsers())
                    .build();
            serviceStatus.set("Starting Solver");
            solverExecutor.start(solution);
        }

    }

    private void startUpValidation() {
        validateConfig();
        validateSolver();
    }

    private void validateConfig() {
        TaskAssigningConfigValidator.of(config).validate();
    }

    private void validateSolver() {
        solverFactory.buildSolver();
    }
}
