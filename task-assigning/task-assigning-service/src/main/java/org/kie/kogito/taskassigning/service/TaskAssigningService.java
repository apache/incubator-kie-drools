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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.eclipse.microprofile.context.ManagedExecutor;
import org.kie.kogito.taskassigning.ClientServices;
import org.kie.kogito.taskassigning.core.model.Task;
import org.kie.kogito.taskassigning.core.model.TaskAssigningSolution;
import org.kie.kogito.taskassigning.core.model.TaskAssignment;
import org.kie.kogito.taskassigning.core.model.User;
import org.kie.kogito.taskassigning.core.model.solver.realtime.AssignTaskProblemFactChange;
import org.kie.kogito.taskassigning.service.config.TaskAssigningConfig;
import org.kie.kogito.taskassigning.service.config.TaskAssigningConfigValidator;
import org.kie.kogito.taskassigning.service.messaging.BufferedUserTaskEventConsumer;
import org.kie.kogito.taskassigning.service.messaging.UserTaskEvent;
import org.kie.kogito.taskassigning.user.service.api.UserServiceConnector;
import org.optaplanner.core.api.solver.ProblemFactChange;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.api.solver.event.BestSolutionChangedEvent;
import org.optaplanner.core.api.solver.event.SolverEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.Startup;

import static org.kie.kogito.taskassigning.core.model.solver.TaskHelper.filterNonDummyAssignments;
import static org.kie.kogito.taskassigning.service.util.EventUtil.filterNewestTaskEvents;
import static org.kie.kogito.taskassigning.service.util.EventUtil.filterNewestTaskEventsInContext;
import static org.kie.kogito.taskassigning.service.util.TaskUtil.fromUserTaskEvents;

@ApplicationScoped
@Startup
public class TaskAssigningService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TaskAssigningService.class);

    private static final Predicate<UserTaskEvent> IS_ACTIVE_TASK_EVENT = userTaskEvent -> !TaskState.isTerminal(userTaskEvent.getState());

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

    @Inject
    BufferedUserTaskEventConsumer userTaskEventConsumer;

    @Inject
    ClientServices clientServices;

    private SolverExecutor solverExecutor;

    private SolutionDataLoader solutionDataLoader;

    private PlanningExecutor planningExecutor;

    private TaskAssigningServiceContext context;

    private AtomicReference<TaskAssigningSolution> currentSolution = new AtomicReference<>(null);

    private AtomicBoolean applyingPlanningExecutionResult = new AtomicBoolean();

    private AtomicBoolean startingFromEvents = new AtomicBoolean();

    private List<UserTaskEvent> startingEvents;

    /**
     * Synchronizes potential concurrent accesses between the different components that invoke callbacks on the service.
     */
    private final ReentrantLock lock = new ReentrantLock();

    /**
     * Handles the TaskAssigningService initialization procedure and instructs the SolutionDataLoader for getting the
     * information for the initial solution.
     */
    @PostConstruct
    void start() {
        startUpValidation();
        context = createContext();
        userTaskEventConsumer.setConsumer(this::onTaskEvents);
        solverExecutor = createSolverExecutor(solverFactory, this::onBestSolutionChange);
        managedExecutor.execute(solverExecutor);
        planningExecutor = createPlanningExecutor(clientServices, config);
        managedExecutor.execute(planningExecutor);
        solutionDataLoader = createSolutionDataLoader(taskServiceConnector, userServiceConnector);
        managedExecutor.execute(solutionDataLoader);
        solutionDataLoader.start(this::onSolutionDataLoad,
                true, true,
                config.getDataLoaderRetryInterval(), config.getDataLoaderRetries(), config.getDataLoaderPageSize());
    }

    /**
     * Invoked by the SolutionDataLoader when the data for initializing the solution has been loaded or the number of
     * unsuccessful retries has been reached.
     * Three main scenarios might happen:
     * a) The service is starting and thus the initial solution load is attempted. If there are available tasks, the
     * solver will be started, first solution will arrive, etc.
     *
     * b) The number of unsuccessful retries has been reached, please continue retrying.
     *
     * c) No tasks where available at the time of service initialization and thus no solution to start the solver.
     * At a later point in time events arrived and the solution can be started with the information coming for them plus
     * the user information loaded by the solution data loader.
     *
     * @param result contains the requested data for creating the initial solution.
     */
    void onSolutionDataLoad(SolutionDataLoader.Result result) {
        lock.lock();
        try {
            LOGGER.debug("Solution data loading has finished, startingFromEvents: {}, hasErrors: {}, includeTasks: {}"
                    + ", includeUsers: {}, tasks: {}, users: {}", startingFromEvents, result.hasErrors(),
                    !startingFromEvents.get(), true, result.getTasks().size(), result.getUsers().size());
            if (result.hasErrors()) {
                solutionDataLoader.start(this::onSolutionDataLoad,
                        !startingFromEvents.get(), true,
                        config.getDataLoaderRetryInterval(), config.getDataLoaderRetries(), config.getDataLoaderPageSize());
            } else {
                TaskAssigningSolution solution;
                List<TaskAssignment> taskAssignments;
                if (startingFromEvents.get()) {
                    if (hasQueuedEvents()) {
                        List<UserTaskEvent> newEvents = filterNewestTaskEventsInContext(context, pollEvents());
                        startingEvents = combineAndFilerNewestActiveTaskEvents(startingEvents, newEvents);
                    }
                    solution = SolutionBuilder.newBuilder()
                            .withTasks(fromUserTaskEvents(startingEvents))
                            .withUsers(result.getUsers())
                            .build();
                    startingFromEvents.set(false);
                    startingEvents = null;
                } else {
                    solution = SolutionBuilder.newBuilder()
                            .withTasks(result.getTasks())
                            .withUsers(result.getUsers())
                            .build();
                }
                taskAssignments = filterNonDummyAssignments(solution.getTaskAssignmentList());
                if (!taskAssignments.isEmpty()) {
                    taskAssignments.forEach(taskAssignment -> {
                        context.setTaskPublished(taskAssignment.getId(), taskAssignment.isPinned());
                        context.setTaskLastEventTime(taskAssignment.getId(), taskAssignment.getTask().getLastUpdate());
                    });
                    solverExecutor.start(solution);
                } else {
                    resumeEvents();
                }
            }
        } finally {
            lock.unlock();
        }
    }

    private List<UserTaskEvent> combineAndFilerNewestActiveTaskEvents(List<UserTaskEvent> previousStartingEvents,
            List<UserTaskEvent> newEvents) {
        List<UserTaskEvent> combinedEvents = new ArrayList<>(previousStartingEvents);
        combinedEvents.addAll(newEvents);
        return filterNewestTaskEvents(combinedEvents)
                .stream()
                .filter(IS_ACTIVE_TASK_EVENT)
                .collect(Collectors.toList());
    }

    private void onTaskEvents(List<UserTaskEvent> events) {
        lock.lock();
        try {
            pauseEvents();
            CompletableFuture.runAsync(() -> processTaskEvents(events));
        } finally {
            lock.unlock();
        }
    }

    /**
     * Invoked when a set of events are received for processing.
     * Two main scenarios might happen:
     * a) A solution already exists and thus the proper problem fact changes are calculated and passed to the solver for
     * execution. If there are no changes to apply, wait for more events.
     *
     * b) No solution exists. Instruct the solution data loader to read the users information and the solver will be
     * started when this information is returned plus the information collected from the events.
     * 
     * @param events a list of events to process.
     */
    void processTaskEvents(List<UserTaskEvent> events) {
        lock.lock();
        try {
            List<UserTaskEvent> newEvents = filterNewestTaskEventsInContext(context, events);
            if (currentSolution.get() == null) {
                // b) no solution exists, start if from the events information.
                List<UserTaskEvent> activeTaskEvents = newEvents.stream()
                        .filter(IS_ACTIVE_TASK_EVENT)
                        .collect(Collectors.toList());
                if (!activeTaskEvents.isEmpty()) {
                    startingEvents = activeTaskEvents;
                    startingFromEvents.set(true);
                    solutionDataLoader.start(this::onSolutionDataLoad,
                            false, true,
                            config.getDataLoaderRetryInterval(), config.getDataLoaderRetries(), config.getDataLoaderPageSize());
                } else {
                    resumeEvents();
                }
            } else {
                // a) a solution exists, calculate and apply the potential changes to apply.
                List<ProblemFactChange<TaskAssigningSolution>> changes = SolutionChangesBuilder.create()
                        .forSolution(currentSolution.get())
                        .withContext(context)
                        .withUserServiceConnector(userServiceConnector)
                        .fromTasksData(fromUserTaskEvents(newEvents))
                        .build();
                if (!changes.isEmpty()) {
                    solverExecutor.addProblemFactChanges(changes);
                } else {
                    resumeEvents();
                }
            }
        } finally {
            lock.unlock();
        }
    }

    /**
     * Invoked when a new solution has been produced.
     * Three main scenarios might happen:
     * a) The produced solution is the result of set problem fact changes derived from new incoming events.
     * A new solution was created, the corresponding plan is created and delivered to the PlanningExecutor for execution.
     *
     * b) Same as a) but the corresponding plan has no items, nothing to execute. Wait for more events.
     *
     * c) The produced solution is the result of executing a set of "pinning changes" derived from the result of a
     * planning execution. Prioritize the execution of events that might have been arrived in the middle if any.
     *
     * @param event a BestSolutionChangeEvent with the new solution.
     */
    void onBestSolutionChange(BestSolutionChangedEvent<TaskAssigningSolution> event) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("onBestSolutionChange: isEveryProblemFactChangeProcessed: {}, currentChangeSetId: {}," +
                    " isCurrentChangeSetProcessed: {}, newBestSolution: {}",
                    event.isEveryProblemFactChangeProcessed(), context.getCurrentChangeSetId(),
                    context.isCurrentChangeSetProcessed(), event.getNewBestSolution());
        }
        TaskAssigningSolution newBestSolution = event.getNewBestSolution();
        if (event.isEveryProblemFactChangeProcessed() && newBestSolution.getScore().isSolutionInitialized()) {
            onBestSolutionChange(newBestSolution);
        }
    }

    private void onBestSolutionChange(TaskAssigningSolution newBestSolution) {
        if (!context.isCurrentChangeSetProcessed()) {
            executeSolutionChange(newBestSolution);
        }
    }

    private void executeSolutionChange(TaskAssigningSolution solution) {
        lock.lock();
        try {
            LOGGER.debug("process the next generated solution, applyingPlanningExecutionResult: {}", applyingPlanningExecutionResult.get());
            currentSolution.set(solution);
            context.setProcessedChangeSet(context.getCurrentChangeSetId());
            List<ProblemFactChange<TaskAssigningSolution>> pendingEventsChanges = null;
            if (Boolean.TRUE.equals(applyingPlanningExecutionResult.get())) {
                applyingPlanningExecutionResult.set(false);
                List<UserTaskEvent> pendingEvents = filterNewestTaskEventsInContext(context, pollEvents());
                if (!pendingEvents.isEmpty()) {
                    pendingEventsChanges = SolutionChangesBuilder.create()
                            .forSolution(solution)
                            .withContext(context)
                            .withUserServiceConnector(userServiceConnector)
                            .fromTasksData(fromUserTaskEvents(pendingEvents))
                            .build();
                }
            }

            if (pendingEventsChanges != null && !pendingEventsChanges.isEmpty()) {
                solverExecutor.addProblemFactChanges(pendingEventsChanges);
            } else {
                List<PlanningItem> planningItems = PlanningBuilder.create()
                        .forSolution(solution)
                        .withContext(context)
                        .withPublishWindowSize(config.getPublishWindowSize())
                        .build();
                if (!planningItems.isEmpty()) {
                    planningExecutor.start(planningItems, this::onPlanningExecuted);
                } else {
                    resumeEvents();
                }
            }
        } finally {
            lock.unlock();
        }
    }

    /**
     * Invoked when the PlanningExecutor finalized the execution of a plan.
     * Three main scenarios might happen:
     * a) There are successful invocations and thus tasks were assigned, the corresponding "pinning changes" must be produced.
     * Create and add them to the Solver.
     * 
     * b) No "pinning changes" to execute there and no available events, retry with the planning items that failed.
     * 
     * c) No "pinning changes" but there are available events, execute them.
     *
     * @param result a PlanningExecutionResult with results of the planning execution.
     */
    void onPlanningExecuted(PlanningExecutionResult result) {
        lock.lock();
        try {
            LOGGER.debug("Planning was executed");
            applyingPlanningExecutionResult.set(false);
            TaskAssigningSolution solution = currentSolution.get();
            Map<String, User> usersById = solution.getUserList()
                    .stream()
                    .collect(Collectors.toMap(User::getId, Function.identity()));
            List<ProblemFactChange<TaskAssigningSolution>> pinningChanges = new ArrayList<>();
            Task task;
            User user;
            boolean published;
            for (PlanningExecutionResultItem resultItem : result.getItems()) {
                task = resultItem.getItem().getTask();
                published = !resultItem.hasError();
                if (published) {
                    user = usersById.get(resultItem.getItem().getTargetUser());
                    pinningChanges.add(new AssignTaskProblemFactChange(new TaskAssignment(task), user));
                }
                context.setTaskPublished(task.getId(), published);
            }
            if (!pinningChanges.isEmpty()) {
                LOGGER.debug("Pinning changes must be executed for the successful invocations: {}", pinningChanges.size());
                pinningChanges.add(0, scoreDirector -> context.setCurrentChangeSetId(context.nextChangeSetId()));
                applyingPlanningExecutionResult.set(true);
                solverExecutor.addProblemFactChanges(pinningChanges);
            } else if (!hasQueuedEvents()) {
                List<PlanningItem> failingItems = result.getItems().stream()
                        .filter(PlanningExecutionResultItem::hasError)
                        .map(PlanningExecutionResultItem::getItem)
                        .collect(Collectors.toList());
                LOGGER.debug("No new events to process, but some items failed: {}, we must retry", failingItems.size());
                planningExecutor.start(failingItems, this::onPlanningExecuted);
            } else {
                LOGGER.debug("Some items failed but there are events to process, try to adjust the solution accordingly.");
                resumeEvents();
            }
        } finally {
            lock.unlock();
        }
    }

    // use the observer instead of the @PreDestroy alternative.
    // https://github.com/quarkusio/quarkus/issues/15026
    void onShutDownEvent(@Observes ShutdownEvent ev) {
        destroy();
    }

    /**
     * Handles the TaskAssigningService finalization prodecure.
     */
    void destroy() {
        try {
            LOGGER.info("Service is going down and will be destroyed.");
            solverExecutor.destroy();
            solutionDataLoader.destroy();
            planningExecutor.destroy();
            LOGGER.info("Service destroy sequence was executed successfully.");
        } catch (Exception e) {
            LOGGER.error("An error was produced during service destroy, but it'll go down anyway.", e);
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

    private void pauseEvents() {
        userTaskEventConsumer.pause();
    }

    private void resumeEvents() {
        userTaskEventConsumer.resume();
    }

    private List<UserTaskEvent> pollEvents() {
        return userTaskEventConsumer.pollEvents();
    }

    private boolean hasQueuedEvents() {
        return userTaskEventConsumer.queuedEvents() > 0;
    }

    TaskAssigningServiceContext createContext() {
        return new TaskAssigningServiceContext();
    }

    SolverExecutor createSolverExecutor(SolverFactory<TaskAssigningSolution> solverFactory, SolverEventListener<TaskAssigningSolution> eventListener) {
        return new SolverExecutor(solverFactory, eventListener);
    }

    PlanningExecutor createPlanningExecutor(ClientServices clientServices, TaskAssigningConfig config) {
        return new PlanningExecutor(clientServices, config);
    }

    SolutionDataLoader createSolutionDataLoader(TaskServiceConnector taskServiceConnector, UserServiceConnector userServiceConnector) {
        return new SolutionDataLoader(taskServiceConnector, userServiceConnector);
    }
}
