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
import org.kie.kogito.taskassigning.service.event.BufferedTaskAssigningServiceEventConsumer;
import org.kie.kogito.taskassigning.service.event.DataEvent;
import org.kie.kogito.taskassigning.service.event.TaskDataEvent;
import org.kie.kogito.taskassigning.service.event.UserDataEvent;
import org.kie.kogito.taskassigning.service.messaging.ReactiveMessagingEventConsumer;
import org.kie.kogito.taskassigning.service.processing.AttributesProcessorRegistry;
import org.kie.kogito.taskassigning.user.service.UserServiceConnector;
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
import static org.kie.kogito.taskassigning.service.util.EventUtil.filterNewestUserEvent;
import static org.kie.kogito.taskassigning.service.util.TaskUtil.fromTaskDataEvents;
import static org.kie.kogito.taskassigning.service.util.TraceUtil.tracePlanning;
import static org.kie.kogito.taskassigning.service.util.TraceUtil.traceSolution;

@ApplicationScoped
@Startup
public class TaskAssigningService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TaskAssigningService.class);

    private static final Predicate<TaskDataEvent> IS_ACTIVE_TASK_EVENT = taskDataEvent -> !TaskState.isTerminal(taskDataEvent.getData().getState());

    private static final String SERVICE_INOPERATIVE_MESSAGE = "Service has become inoperative or is executing the" +
            " shutdown procedure, service status: {}";

    @Inject
    SolverFactory<TaskAssigningSolution> solverFactory;

    @Inject
    TaskAssigningConfig config;

    @Inject
    ManagedExecutor managedExecutor;

    @Inject
    BufferedTaskAssigningServiceEventConsumer serviceEventConsumer;

    @Inject
    ReactiveMessagingEventConsumer serviceMessageConsumer;

    @Inject
    ClientServices clientServices;

    @Inject
    UserServiceConnector userServiceConnector;

    @Inject
    UserServiceConnectorDelegate userServiceConnectorDelegate;

    @Inject
    UserServiceAdapter userServiceAdapter;

    @Inject
    SolutionDataLoader solutionDataLoader;

    @Inject
    AttributesProcessorRegistry processorRegistry;

    private SolverExecutor solverExecutor;

    private PlanningExecutor planningExecutor;

    private TaskAssigningServiceContext context;

    private final AtomicReference<TaskAssigningSolution> currentSolution = new AtomicReference<>(null);

    private final AtomicBoolean applyingPlanningExecutionResult = new AtomicBoolean();

    private final AtomicBoolean startingFromEvents = new AtomicBoolean();

    private List<TaskDataEvent> startingEvents;

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
        serviceEventConsumer.setConsumer(this::onDataEvents);
        solverExecutor = createSolverExecutor(solverFactory, this::onBestSolutionChange);
        managedExecutor.execute(solverExecutor);
        planningExecutor = createPlanningExecutor(clientServices, config);
        managedExecutor.execute(planningExecutor);
        loadSolutionData(true, true, config.getDataLoaderPageSize());
    }

    /**
     * Invoked by the SolutionDataLoader when the data for initializing the solution has been loaded successfully.
     * Two main scenarios might happen:
     * a) The service is starting and thus the initial solution load is attempted. If there are available tasks, the
     * solver will be started, first solution will arrive, etc.
     *
     * b) No tasks where available at the time of service initialization and thus no solution to start the solver.
     * At a later point in time events arrived and the solution can be started with the information coming for them plus
     * the user information loaded by the solution data loader.
     *
     * @param result contains the requested data for creating the initial solution.
     */
    void onSolutionDataLoad(SolutionDataLoader.Result result) {
        lock.lock();
        try {
            LOGGER.debug("Solution data loading has finished, startingFromEvents: {}, includeTasks: {}"
                    + ", includeUsers: {}, tasks: {}, users: {}", startingFromEvents,
                    !startingFromEvents.get(), true, result.getTasks().size(), result.getUsers().size());
            context.setStatus(ServiceStatus.READY);
            TaskAssigningSolution solution;
            List<TaskAssignment> taskAssignments;
            if (startingFromEvents.get()) {
                // b) the initialization procedure has started after some startingEvents arrival, and the solution
                // data loader has responded with the users.
                if (hasQueuedEvents()) {
                    // incorporate the events that could have been arrived in the middle while the users were being loaded.
                    List<TaskDataEvent> newEvents = filterNewestTaskEventsInContext(context, pollEvents());
                    startingEvents = combineAndFilerNewestActiveTaskEvents(startingEvents, newEvents);
                }
                solution = SolutionBuilder.newBuilder()
                        .withTasks(fromTaskDataEvents(startingEvents))
                        .withUsers(result.getUsers())
                        .withProcessors(processorRegistry)
                        .build();
                startingFromEvents.set(false);
                startingEvents = null;
            } else {
                // a) normal initialization procedure after getting the tasks and users from the solution data loader
                solution = SolutionBuilder.newBuilder()
                        .withTasks(result.getTasks())
                        .withUsers(result.getUsers())
                        .withProcessors(processorRegistry)
                        .build();
            }
            // if the solution has non dummy tasks the solver can be started.
            taskAssignments = filterNonDummyAssignments(solution.getTaskAssignmentList());
            if (!taskAssignments.isEmpty()) {
                taskAssignments.forEach(taskAssignment -> {
                    context.setTaskPublished(taskAssignment.getId(), taskAssignment.isPinned());
                    context.setTaskLastEventTime(taskAssignment.getId(), taskAssignment.getTask().getLastUpdate());
                });
                solverExecutor.start(solution);
                userServiceAdapter.start();
            } else {
                resumeEvents();
            }
        } catch (Exception e) {
            failFast(e);
        } finally {
            lock.unlock();
        }
    }

    private void onSolutionDataLoadFailure(Throwable throwable) {
        failFast(throwable);
    }

    private List<TaskDataEvent> combineAndFilerNewestActiveTaskEvents(List<TaskDataEvent> previousStartingEvents,
            List<TaskDataEvent> newEvents) {
        List<TaskDataEvent> combinedEvents = new ArrayList<>(previousStartingEvents);
        combinedEvents.addAll(newEvents);
        return filterNewestTaskEvents(combinedEvents)
                .stream()
                .filter(IS_ACTIVE_TASK_EVENT)
                .collect(Collectors.toList());
    }

    private void onDataEvents(List<DataEvent<?>> events) {
        lock.lock();
        try {
            pauseEvents();
            managedExecutor.runAsync(() -> processDataEvents(events));
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
    void processDataEvents(List<DataEvent<?>> events) {
        if (isNotOperative()) {
            LOGGER.warn(SERVICE_INOPERATIVE_MESSAGE, context.getStatus());
            return;
        }
        lock.lock();
        try {
            List<TaskDataEvent> newTaskDataEvents = filterNewestTaskEventsInContext(context, events);
            if (currentSolution.get() == null) {
                List<TaskDataEvent> activeTaskEvents = newTaskDataEvents.stream()
                        .filter(IS_ACTIVE_TASK_EVENT)
                        .collect(Collectors.toList());
                if (!activeTaskEvents.isEmpty()) {
                    // b) no solution exists, store the events and get the users from the external user service.
                    startingEvents = activeTaskEvents;
                    startingFromEvents.set(true);
                    loadSolutionData(false, true, config.getDataLoaderPageSize());
                } else {
                    resumeEvents();
                }
            } else {
                // a) a solution exists, calculate and apply the potential changes if any.
                UserDataEvent userDataEvent = filterNewestUserEvent(events);
                List<ProblemFactChange<TaskAssigningSolution>> changes = SolutionChangesBuilder.create()
                        .forSolution(currentSolution.get())
                        .withContext(context)
                        .withUserServiceConnector(userServiceConnectorDelegate)
                        .withProcessors(processorRegistry)
                        .fromTasksData(fromTaskDataEvents(newTaskDataEvents))
                        .fromUserDataEvent(userDataEvent)
                        .build();
                if (!changes.isEmpty()) {
                    solverExecutor.addProblemFactChanges(changes);
                } else {
                    executePlanOrResumeEvents(currentSolution.get());
                }
            }
        } catch (Exception e) {
            failFast(e);
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
            managedExecutor.runAsync(() -> executeSolutionChange(newBestSolution));
        }
    }

    private void executeSolutionChange(TaskAssigningSolution solution) {
        if (isNotOperative()) {
            LOGGER.warn(SERVICE_INOPERATIVE_MESSAGE, context.getStatus());
            return;
        }
        lock.lock();
        try {
            LOGGER.debug("process the next generated solution, applyingPlanningExecutionResult: {}", applyingPlanningExecutionResult.get());
            if (LOGGER.isTraceEnabled()) {
                traceSolution(LOGGER, solution);
            }
            currentSolution.set(solution);
            context.setProcessedChangeSet(context.getCurrentChangeSetId());
            List<ProblemFactChange<TaskAssigningSolution>> pendingEventsChanges = null;
            if (applyingPlanningExecutionResult.get()) {
                // solution is the result of applying the pinning changes corresponding to the last executed plan,
                // prioritize events that could have arrived in the middle if any.
                applyingPlanningExecutionResult.set(false);
                List<DataEvent<?>> pendingEvents = pollEvents();
                List<TaskDataEvent> pendingTaskDataEvents = filterNewestTaskEventsInContext(context, pendingEvents);
                UserDataEvent pendingUserDataEvent = filterNewestUserEvent(pendingEvents);
                if (!pendingTaskDataEvents.isEmpty() || pendingUserDataEvent != null) {
                    pendingEventsChanges = SolutionChangesBuilder.create()
                            .forSolution(solution)
                            .withContext(context)
                            .withUserServiceConnector(userServiceConnectorDelegate)
                            .withProcessors(processorRegistry)
                            .fromTasksData(fromTaskDataEvents(pendingTaskDataEvents))
                            .fromUserDataEvent(pendingUserDataEvent)
                            .build();
                }
            }
            if (pendingEventsChanges != null && !pendingEventsChanges.isEmpty()) {
                solverExecutor.addProblemFactChanges(pendingEventsChanges);
            } else {
                executePlanOrResumeEvents(solution);
            }
        } catch (Exception e) {
            failFast(e);
        } finally {
            lock.unlock();
        }
    }

    private void executePlanOrResumeEvents(TaskAssigningSolution solution) {
        List<PlanningItem> planningItems = PlanningBuilder.create()
                .forSolution(solution)
                .withContext(context)
                .withPublishWindowSize(config.getPublishWindowSize())
                .build();
        if (LOGGER.isTraceEnabled()) {
            tracePlanning(LOGGER, planningItems);
        }
        if (!planningItems.isEmpty()) {
            planningExecutor.start(planningItems, this::onPlanningExecuted);
        } else {
            resumeEvents();
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
        if (isNotOperative()) {
            LOGGER.warn(SERVICE_INOPERATIVE_MESSAGE, context.getStatus());
            return;
        }
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
        } catch (Exception e) {
            failFast(e);
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
     * Handles the TaskAssigningService finalization procedure.
     */
    void destroy() {
        try {
            context.setStatus(ServiceStatus.SHUTDOWN);
            LOGGER.info("Service is going down and will be destroyed.");
            userServiceAdapter.destroy();
            solverExecutor.destroy();
            planningExecutor.destroy();
            LOGGER.info("Service destroy sequence was executed successfully.");
        } catch (Exception e) {
            LOGGER.error("An error was produced during service destroy, but it'll go down anyway.", e);
        }
    }

    private void loadSolutionData(boolean includeTasks, boolean includeUsers, int pageSize) {
        solutionDataLoader.loadSolutionData(includeTasks, includeUsers, pageSize)
                .thenAccept(this::onSolutionDataLoad)
                .exceptionally(throwable -> {
                    onSolutionDataLoadFailure(throwable);
                    return null;
                });
    }

    void failFast(Throwable cause) {
        String msg = String.format("An unrecoverable error was produced: %s", cause.getMessage());
        LOGGER.error(msg, cause);
        context.setStatus(ServiceStatus.ERROR, ServiceMessage.error(msg));
        solverExecutor.destroy();
        planningExecutor.destroy();
        userServiceAdapter.destroy();
        serviceMessageConsumer.failFast();
    }

    private boolean isNotOperative() {
        return context.getStatus() == ServiceStatus.ERROR || context.getStatus() == ServiceStatus.SHUTDOWN;
    }

    private void startUpValidation() {
        validateConfig();
        validateUserService();
        validateSolver();
    }

    private void validateConfig() {
        TaskAssigningConfigValidator.of(config).validate();
    }

    private void validateUserService() {
        userServiceConnector.start();
    }

    private void validateSolver() {
        solverFactory.buildSolver();
    }

    private void pauseEvents() {
        serviceEventConsumer.pause();
    }

    private void resumeEvents() {
        serviceEventConsumer.resume();
    }

    private List<DataEvent<?>> pollEvents() {
        return serviceEventConsumer.pollEvents();
    }

    private boolean hasQueuedEvents() {
        return serviceEventConsumer.queuedEvents() > 0;
    }

    public TaskAssigningServiceContext getContext() {
        return context;
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
}
