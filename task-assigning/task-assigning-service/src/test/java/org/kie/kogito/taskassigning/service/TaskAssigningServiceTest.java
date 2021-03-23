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

import java.net.URL;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.microprofile.context.ManagedExecutor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.extension.ExtendWith;
import org.kie.kogito.taskassigning.ClientServices;
import org.kie.kogito.taskassigning.core.model.Task;
import org.kie.kogito.taskassigning.core.model.TaskAssigningSolution;
import org.kie.kogito.taskassigning.core.model.TaskAssignment;
import org.kie.kogito.taskassigning.core.model.User;
import org.kie.kogito.taskassigning.core.model.solver.realtime.AddTaskProblemFactChange;
import org.kie.kogito.taskassigning.core.model.solver.realtime.AssignTaskProblemFactChange;
import org.kie.kogito.taskassigning.core.model.solver.realtime.RemoveTaskProblemFactChange;
import org.kie.kogito.taskassigning.service.config.TaskAssigningConfig;
import org.kie.kogito.taskassigning.service.messaging.BufferedUserTaskEventConsumer;
import org.kie.kogito.taskassigning.service.messaging.UserTaskEvent;
import org.kie.kogito.taskassigning.user.service.api.UserServiceConnector;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.optaplanner.core.api.score.buildin.bendablelong.BendableLongScore;
import org.optaplanner.core.api.solver.ProblemFactChange;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.api.solver.event.BestSolutionChangedEvent;
import org.optaplanner.core.api.solver.event.SolverEventListener;

import io.quarkus.runtime.ShutdownEvent;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.kie.kogito.taskassigning.service.TestUtil.mockTaskAssignment;
import static org.kie.kogito.taskassigning.service.TestUtil.mockTaskData;
import static org.kie.kogito.taskassigning.service.TestUtil.mockUser;
import static org.kie.kogito.taskassigning.service.TestUtil.parseZonedDateTime;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class TaskAssigningServiceTest {

    private static final String DATA_INDEX_SERVER_URL = "http://localhost:8180/graphql";

    private static final String USER_1_ID = "USER_1_ID";
    private static final String USER_2_ID = "USER_2_ID";

    private static final String TASK_1_ID = "TASK_1_ID";
    private static final ZonedDateTime TASK_1_LAST_UPDATE = parseZonedDateTime("2021-03-11T10:00:00.001Z");
    private static final String TASK_2_ID = "TASK_2_ID";
    private static final ZonedDateTime TASK_2_LAST_UPDATE = parseZonedDateTime("2021-03-11T11:00:00.001Z");
    private static final String TASK_3_ID = "TASK_3_ID";
    private static final ZonedDateTime TASK_3_LAST_UPDATE = parseZonedDateTime("2021-03-11T12:00:00.001Z");
    private static final String TASK_4_ID = "TASK_4_ID";
    private static final ZonedDateTime TASK_4_LAST_UPDATE = parseZonedDateTime("2021-03-11T13:00:00.001Z");

    private static final Duration DATA_LOADER_RETRY_INTERVAL = Duration.ofMillis(5000);
    private static final int DATA_LOADER_RETRIES = 5;
    private static final int DATA_LOADER_PAGE_SIZE = 10;
    private static final int PUBLISH_WINDOW_SIZE = 5;

    @Mock
    private SolverFactory<TaskAssigningSolution> solverFactory;

    @Mock
    private TaskAssigningConfig config;

    @Mock
    private ManagedExecutor managedExecutor;

    @Mock
    private TaskServiceConnector taskServiceConnector;

    @Mock
    private UserServiceConnector userServiceConnector;

    @Mock
    private BufferedUserTaskEventConsumer userTaskEventConsumer;

    @Mock
    private ClientServices clientServices;

    @Mock
    private SolverExecutor solverExecutor;

    @Mock
    private PlanningExecutor planningExecutor;

    @Mock
    private SolutionDataLoader solutionDataLoader;

    private TaskAssigningServiceContext context;

    @Captor
    private ArgumentCaptor<SolverEventListener<TaskAssigningSolution>> solverListenerCaptor;

    @Captor
    private ArgumentCaptor<Consumer<SolutionDataLoader.Result>> solutionDataLoaderConsumerCaptor;

    @Captor
    private ArgumentCaptor<Consumer<List<UserTaskEvent>>> userTaskEventsConsumerCaptor;

    @Captor
    private ArgumentCaptor<List<PlanningItem>> planningCaptor;

    @Captor
    private ArgumentCaptor<TaskAssigningSolution> solutionCaptor;

    @Captor
    private ArgumentCaptor<List<ProblemFactChange<TaskAssigningSolution>>> problemFactChangesCaptor;

    @Mock
    SolverEventListener<TaskAssigningSolution> solverEventListener;

    private TaskAssigningService taskAssigningService;

    private CountDownLatch eventsProcessed;

    @BeforeEach
    void setUp() {
        context = spy(new TaskAssigningServiceContext());
        taskAssigningService = spy(new TaskAssigningServiceMock());
        taskAssigningService.solverFactory = solverFactory;
        taskAssigningService.config = config;
        taskAssigningService.managedExecutor = managedExecutor;
        taskAssigningService.taskServiceConnector = taskServiceConnector;
        taskAssigningService.userServiceConnector = userServiceConnector;
        taskAssigningService.userTaskEventConsumer = userTaskEventConsumer;
        taskAssigningService.clientServices = clientServices;
    }

    @Test
    void start() throws Exception {
        prepareStart();
    }

    @Test
    void startWithSolverValidationFailure() throws Exception {
        doReturn(new URL(DATA_INDEX_SERVER_URL)).when(config).getDataIndexServerUrl();
        String errorMessage = "Solver factory error";
        doThrow(new RuntimeException(errorMessage)).when(solverFactory).buildSolver();
        assertThatThrownBy(() -> taskAssigningService.start()).hasMessage(errorMessage);
    }

    @Test
    void startWithConfigValidationFailure() {
        doReturn(null).when(config).getDataIndexServerUrl();
        assertThatThrownBy(() -> taskAssigningService.start())
                .hasMessage("A config value must be set for the property: kogito.task-assigning.data-index.server-url");
    }

    @Test
    void startWithDataLoadErrors() throws Exception {
        prepareStart();
        SolutionDataLoader.Result result = new SolutionDataLoader.Result(Collections.singletonList(new Exception("Data loading error")));
        solutionDataLoaderConsumerCaptor.getValue().accept(result);
        verify(solutionDataLoader, times(2))
                .start(solutionDataLoaderConsumerCaptor.capture(), eq(true), eq(true),
                        eq(DATA_LOADER_RETRY_INTERVAL), eq(DATA_LOADER_RETRIES), eq(DATA_LOADER_PAGE_SIZE));
    }

    @Test
    void startWithSolutionDataLoadAndNonEmptySolution() throws Exception {
        SolutionDataLoader.Result result = new SolutionDataLoader.Result(
                Arrays.asList(
                        mockTaskData(TASK_1_ID, TaskState.READY.value(), TASK_1_LAST_UPDATE),
                        mockTaskData(TASK_2_ID, TaskState.RESERVED.value(), TASK_2_LAST_UPDATE)),
                Collections.singletonList(mockExternalUser(USER_1_ID)));
        prepareStart();
        solutionDataLoaderConsumerCaptor.getValue().accept(result);
        verify(solverExecutor).start(any());
        verify(userTaskEventConsumer, never()).resume();
        assertThat(context.isTaskPublished(TASK_1_ID)).isFalse();
        assertThat(context.getTaskLastEventTime(TASK_1_ID)).isEqualTo(TASK_1_LAST_UPDATE);
        assertThat(context.isTaskPublished(TASK_2_ID)).isTrue();
        assertThat(context.getTaskLastEventTime(TASK_2_ID)).isEqualTo(TASK_2_LAST_UPDATE);
    }

    @Test
    void startWithSolutionDataLoadAndEmptySolution() throws Exception {
        SolutionDataLoader.Result result = new SolutionDataLoader.Result(Collections.emptyList(),
                Collections.emptyList());
        prepareStart();
        solutionDataLoaderConsumerCaptor.getValue().accept(result);
        verify(solverExecutor, never()).start(any());
        verify(context, never()).setTaskPublished(anyString(), anyBoolean());
        verify(context, never()).setTaskPublished(anyString(), anyBoolean());
        verify(userTaskEventConsumer).resume();
        verify(solverExecutor, never()).start(any());
    }

    @Test
    @Timeout(5)
    void startSolutionFromEventsAndEmptySolution() throws Exception {
        eventsProcessed = new CountDownLatch(1);
        prepareStart();
        SolutionDataLoader.Result initialEmptyData = new SolutionDataLoader.Result(Collections.emptyList(),
                Collections.emptyList());
        solutionDataLoaderConsumerCaptor.getValue().accept(initialEmptyData);

        List<UserTaskEvent> eventList = Arrays.asList(
                mockUserTaskEvent(TASK_1_ID, TaskState.READY.value(), TASK_1_LAST_UPDATE),
                mockUserTaskEvent(TASK_2_ID, TaskState.ABORTED.value(), TASK_2_LAST_UPDATE));

        userTaskEventsConsumerCaptor.getValue().accept(eventList);
        eventsProcessed.await();

        List<UserTaskEvent> queuedEventList = Arrays.asList(
                mockUserTaskEvent(TASK_1_ID, TaskState.COMPLETED.value(), TASK_1_LAST_UPDATE.plusSeconds(1)),
                mockUserTaskEvent(TASK_3_ID, TaskState.SKIPPED.value(), TASK_3_LAST_UPDATE));

        doReturn(queuedEventList.size()).when(userTaskEventConsumer).queuedEvents();
        doReturn(queuedEventList).when(userTaskEventConsumer).pollEvents();

        SolutionDataLoader.Result userData = new SolutionDataLoader.Result(Collections.emptyList(),
                Collections.singletonList(mockExternalUser(USER_1_ID)));

        solutionDataLoaderConsumerCaptor.getValue().accept(userData);

        verify(solverExecutor, never()).start(any());
        verify(userTaskEventConsumer, times(2)).resume();
        verify(context, never()).setTaskPublished(eq(TASK_1_ID), anyBoolean());
        assertThat(context.getTaskLastEventTime(TASK_1_ID)).isEqualTo(TASK_1_LAST_UPDATE.plusSeconds(1));
        verify(context, never()).setTaskPublished(eq(TASK_2_ID), anyBoolean());
        assertThat(context.getTaskLastEventTime(TASK_2_ID)).isEqualTo(TASK_2_LAST_UPDATE);
        verify(context, never()).setTaskPublished(eq(TASK_3_ID), anyBoolean());
        assertThat(context.getTaskLastEventTime(TASK_3_ID)).isEqualTo(TASK_3_LAST_UPDATE);
    }

    @Test
    @Timeout(5)
    void startSolutionFromEventsAndNonEmptySolution() throws Exception {
        eventsProcessed = new CountDownLatch(1);
        prepareStart();

        SolutionDataLoader.Result initialEmptyData = new SolutionDataLoader.Result(Collections.emptyList(),
                Collections.emptyList());
        solutionDataLoaderConsumerCaptor.getValue().accept(initialEmptyData);

        List<UserTaskEvent> eventList = Arrays.asList(
                mockUserTaskEvent(TASK_1_ID, TaskState.READY.value(), TASK_1_LAST_UPDATE),
                mockUserTaskEvent(TASK_2_ID, TaskState.RESERVED.value(), TASK_2_LAST_UPDATE));

        userTaskEventsConsumerCaptor.getValue().accept(eventList);
        eventsProcessed.await();

        List<UserTaskEvent> queuedEventList = Arrays.asList(
                mockUserTaskEvent(TASK_3_ID, TaskState.READY.value(), TASK_3_LAST_UPDATE),
                mockUserTaskEvent(TASK_4_ID, TaskState.COMPLETED.value(), TASK_4_LAST_UPDATE));

        doReturn(queuedEventList.size()).when(userTaskEventConsumer).queuedEvents();
        doReturn(queuedEventList).when(userTaskEventConsumer).pollEvents();

        SolutionDataLoader.Result userData = new SolutionDataLoader.Result(Collections.emptyList(),
                Collections.singletonList(mockExternalUser(USER_1_ID)));

        solutionDataLoaderConsumerCaptor.getValue().accept(userData);

        verify(solverExecutor).start(any());
        assertThat(context.isTaskPublished(TASK_1_ID)).isFalse();
        assertThat(context.getTaskLastEventTime(TASK_1_ID)).isEqualTo(TASK_1_LAST_UPDATE);
        assertThat(context.isTaskPublished(TASK_2_ID)).isTrue();
        assertThat(context.getTaskLastEventTime(TASK_2_ID)).isEqualTo(TASK_2_LAST_UPDATE);
        assertThat(context.isTaskPublished(TASK_3_ID)).isFalse();
        assertThat(context.getTaskLastEventTime(TASK_3_ID)).isEqualTo(TASK_3_LAST_UPDATE);
        verify(context, never()).setTaskPublished(eq(TASK_4_ID), anyBoolean());
        assertThat(context.getTaskLastEventTime(TASK_4_ID)).isEqualTo(TASK_4_LAST_UPDATE);
    }

    @Test
    void onTaskEventsWithExistingSolutionAndThereAreChanges() throws Exception {
        SolutionDataLoader.Result result = new SolutionDataLoader.Result(
                Collections.singletonList(mockTaskData(TASK_1_ID, TaskState.RESERVED.value(), USER_1_ID, TASK_1_LAST_UPDATE)),
                Collections.singletonList(mockExternalUser(USER_1_ID)));

        prepareStartAndSetInitialSolution(result);

        List<UserTaskEvent> eventList = Arrays.asList(
                mockUserTaskEvent(TASK_2_ID, TaskState.READY.value(), TASK_2_LAST_UPDATE),
                mockUserTaskEvent(TASK_3_ID, TaskState.READY.value(), TASK_3_LAST_UPDATE));

        eventsProcessed = new CountDownLatch(1);
        userTaskEventsConsumerCaptor.getValue().accept(eventList);
        eventsProcessed.await();

        verify(solverExecutor).addProblemFactChanges(problemFactChangesCaptor.capture());
        assertThat(problemFactChangesCaptor.getValue())
                .isNotNull()
                .hasSize(3);

        assertHasAddTaskChangeForTask(problemFactChangesCaptor.getValue(), TASK_2_ID);
        assertHasAddTaskChangeForTask(problemFactChangesCaptor.getValue(), TASK_3_ID);
    }

    @Test
    void onTaskEventsWithExistingSolutionAndThereAreNoChanges() throws Exception {
        SolutionDataLoader.Result result = new SolutionDataLoader.Result(
                Collections.singletonList(mockTaskData(TASK_1_ID, TaskState.RESERVED.value(), USER_1_ID, TASK_1_LAST_UPDATE)),
                Collections.singletonList(mockExternalUser(USER_1_ID)));

        prepareStartAndSetInitialSolution(result);

        List<UserTaskEvent> eventList = Arrays.asList(
                mockUserTaskEvent(TASK_2_ID, TaskState.ABORTED.value(), TASK_2_LAST_UPDATE),
                mockUserTaskEvent(TASK_3_ID, TaskState.ABORTED.value(), TASK_3_LAST_UPDATE));

        eventsProcessed = new CountDownLatch(1);
        userTaskEventsConsumerCaptor.getValue().accept(eventList);
        eventsProcessed.await();

        verify(solverExecutor, never()).addProblemFactChanges(any());
        verify(userTaskEventConsumer, times(2)).resume();
    }

    @Test
    void onSolutionChangeWithPlanningItems() throws Exception {
        prepareStart();
        TaskAssigningSolution solution = buildSolution();

        context.setTaskPublished(TASK_1_ID, false);
        context.setTaskPublished(TASK_2_ID, true);
        context.setTaskPublished(TASK_3_ID, false);
        context.setTaskPublished(TASK_4_ID, true);

        taskAssigningService.onBestSolutionChange(mockEvent(solution, true, true));
        verify(planningExecutor).start(planningCaptor.capture(), any());
        List<PlanningItem> planningItems = planningCaptor.getValue();
        assertThat(planningItems)
                .isNotNull()
                .hasSize(2);
    }

    @Test
    void onSolutionChangeWithNoPlanningItems() throws Exception {
        prepareStart();
        TaskAssigningSolution solution = buildSolution();

        context.setTaskPublished(TASK_1_ID, true);
        context.setTaskPublished(TASK_2_ID, true);
        context.setTaskPublished(TASK_3_ID, true);
        context.setTaskPublished(TASK_4_ID, true);

        taskAssigningService.onBestSolutionChange(mockEvent(solution, true, true));
        verify(planningExecutor, never()).start(any(), any());
        verify(userTaskEventConsumer).resume();
    }

    @Test
    void onSolutionChangeWhenApplyingPlanningExecutionResult() throws Exception {
        prepareStart();
        TaskAssigningSolution initialSolution = buildSolution();

        context.setTaskPublished(TASK_1_ID, false);
        context.setTaskPublished(TASK_2_ID, true);
        context.setTaskPublished(TASK_3_ID, true);
        context.setTaskPublished(TASK_4_ID, true);

        taskAssigningService.onBestSolutionChange(mockEvent(initialSolution, true, true));
        verify(planningExecutor).start(planningCaptor.capture(), any());
        assertThat(planningCaptor.getValue())
                .isNotNull()
                .hasSize(1);

        PlanningItem planningItem = new PlanningItem(Task.newBuilder().id(TASK_1_ID).build(), USER_1_ID);
        PlanningExecutionResult executionResult = new PlanningExecutionResult(Collections.singletonList(new PlanningExecutionResultItem(planningItem)));
        taskAssigningService.onPlanningExecuted(executionResult);

        List<UserTaskEvent> eventList = Arrays.asList(
                mockUserTaskEvent(TASK_1_ID, TaskState.COMPLETED.value(), TASK_1_LAST_UPDATE.plusSeconds(1)),
                mockUserTaskEvent(TASK_2_ID, TaskState.ABORTED.value(), TASK_2_LAST_UPDATE.plusSeconds(1)));
        doReturn(eventList).when(userTaskEventConsumer).pollEvents();

        TaskAssigningSolution newSolution = buildSolution();
        context.setCurrentChangeSetId(context.nextChangeSetId());
        taskAssigningService.onBestSolutionChange(mockEvent(newSolution, true, true));
        verify(solverExecutor, times(2)).addProblemFactChanges(problemFactChangesCaptor.capture());
        List<ProblemFactChange<TaskAssigningSolution>> changes = problemFactChangesCaptor.getAllValues().get(1);
        assertHasRemoveTaskChangeForTask(changes, TASK_1_ID);
        assertHasRemoveTaskChangeForTask(changes, TASK_2_ID);
    }

    @Test
    void onPlanningExecutedWithPinningChanges() throws Exception {
        prepareStart();
        TaskAssigningSolution solution = new TaskAssigningSolution("1",
                Arrays.asList(mockUser(USER_1_ID, Collections.emptyList()),
                        mockUser(USER_2_ID, Collections.emptyList())),
                Collections.emptyList());
        taskAssigningService.onBestSolutionChange(mockEvent(solution, true, true));
        context.setTaskPublished(TASK_1_ID, false);
        context.setTaskPublished(TASK_2_ID, false);
        PlanningItem planningItem1 = new PlanningItem(Task.newBuilder().id(TASK_1_ID).build(), USER_1_ID);
        PlanningItem planningItem2 = new PlanningItem(Task.newBuilder().id(TASK_2_ID).build(), USER_2_ID);
        PlanningExecutionResult executionResult = new PlanningExecutionResult(
                Arrays.asList(new PlanningExecutionResultItem(planningItem1),
                        new PlanningExecutionResultItem(planningItem2, new RuntimeException("planningItem2 failed"))));

        taskAssigningService.onPlanningExecuted(executionResult);
        assertThat(context.isTaskPublished(TASK_1_ID)).isTrue();
        assertThat(context.isTaskPublished(TASK_2_ID)).isFalse();
        verify(solverExecutor).addProblemFactChanges(problemFactChangesCaptor.capture());
        List<ProblemFactChange<TaskAssigningSolution>> changes = problemFactChangesCaptor.getValue();
        assertThat(changes)
                .isNotNull()
                .hasSize(2);
        assertHasAssignTaskChangeForTask(changes, TASK_1_ID, USER_1_ID);
    }

    @Test
    void onPlanningExecutedWithNoPinningChangesAndNoQueuedEvents() throws Exception {
        preparePlanningExecutionWithNoPinningChanges(0);
        assertThat(context.isTaskPublished(TASK_1_ID)).isFalse();
        assertThat(context.isTaskPublished(TASK_2_ID)).isFalse();
        verify(planningExecutor).start(planningCaptor.capture(), any());
        verify(userTaskEventConsumer, times(1)).resume();
        List<PlanningItem> planningItems = planningCaptor.getValue();
        assertThat(planningItems)
                .isNotNull()
                .hasSize(2);
        assertThat(planningItems.get(0).getTask().getId())
                .isEqualTo(TASK_1_ID);
        assertThat(planningItems.get(1).getTask().getId())
                .isEqualTo(TASK_2_ID);
    }

    @Test
    void onPlanningExecutedWithNoPinningChangesAndQueuedEvents() throws Exception {
        preparePlanningExecutionWithNoPinningChanges(1);
        verify(planningExecutor, never()).start(any(), any());
        verify(userTaskEventConsumer, times(2)).resume();
    }

    private void preparePlanningExecutionWithNoPinningChanges(int queuedEvents) throws Exception {
        prepareStart();
        TaskAssigningSolution solution = new TaskAssigningSolution("1",
                Arrays.asList(mockUser(USER_1_ID, Collections.emptyList()),
                        mockUser(USER_2_ID, Collections.emptyList())),
                Collections.emptyList());
        taskAssigningService.onBestSolutionChange(mockEvent(solution, true, true));
        context.setTaskPublished(TASK_1_ID, false);
        context.setTaskPublished(TASK_2_ID, false);
        PlanningItem planningItem1 = new PlanningItem(Task.newBuilder().id(TASK_1_ID).build(), USER_1_ID);
        PlanningItem planningItem2 = new PlanningItem(Task.newBuilder().id(TASK_2_ID).build(), USER_2_ID);
        PlanningExecutionResult executionResult = new PlanningExecutionResult(
                Arrays.asList(new PlanningExecutionResultItem(planningItem1, new RuntimeException("planningItem1 failed")),
                        new PlanningExecutionResultItem(planningItem2, new RuntimeException("planningItem2 failed"))));
        doReturn(queuedEvents).when(userTaskEventConsumer).queuedEvents();
        taskAssigningService.onPlanningExecuted(executionResult);
    }

    @Test
    void onShutDownEvent() throws Exception {
        prepareStart();
        taskAssigningService.onShutDownEvent(new ShutdownEvent());
    }

    @Test
    void destroy() throws Exception {
        prepareStart();
        taskAssigningService.destroy();
    }

    @Test
    void createContext() {
        assertThat(taskAssigningService.createContext()).isNotNull();
    }

    @Test
    void createSolverExecutor() {
        SolverExecutor solverExecutor = taskAssigningService.createSolverExecutor(solverFactory, solverEventListener);
        assertThat(solverExecutor).isNotNull();
    }

    @Test
    void createPlanningExecutor() {
        PlanningExecutor planningExecutor = taskAssigningService.createPlanningExecutor(clientServices, config);
        assertThat(planningExecutor).isNotNull();
    }

    @Test
    void createSolutionDataLoader() {
        SolutionDataLoader solutionDataLoader = taskAssigningService.createSolutionDataLoader(taskServiceConnector, userServiceConnector);
        assertThat(solutionDataLoader).isNotNull();
    }

    private void verifyDestroy() {
        verify(solverExecutor).destroy();
        verify(solutionDataLoader).destroy();
        verify(planningExecutor).destroy();
    }

    private TaskAssigningSolution buildSolution() {
        List<TaskAssignment> user1Assignments = Arrays.asList(mockTaskAssignment(TASK_1_ID),
                mockTaskAssignment(TASK_2_ID));
        User user1 = mockUser(USER_1_ID, user1Assignments);

        List<TaskAssignment> user2Assignments = Arrays.asList(mockTaskAssignment(TASK_3_ID),
                mockTaskAssignment(TASK_4_ID));

        User user2 = mockUser(USER_2_ID, user2Assignments);
        List<TaskAssignment> assignments = new ArrayList<>();
        assignments.addAll(user1Assignments);
        assignments.addAll(user2Assignments);
        return new TaskAssigningSolution("1", Arrays.asList(user1, user2), assignments);
    }

    private void prepareStart() throws Exception {
        doReturn(context).when(taskAssigningService).createContext();
        doReturn(new URL(DATA_INDEX_SERVER_URL)).when(config).getDataIndexServerUrl();
        doReturn(DATA_LOADER_RETRY_INTERVAL).when(config).getDataLoaderRetryInterval();
        doReturn(DATA_LOADER_RETRIES).when(config).getDataLoaderRetries();
        doReturn(DATA_LOADER_PAGE_SIZE).when(config).getDataLoaderPageSize();
        lenient().doReturn(PUBLISH_WINDOW_SIZE).when(config).getPublishWindowSize();
        doReturn(solverExecutor).when(taskAssigningService).createSolverExecutor(eq(solverFactory), solverListenerCaptor.capture());
        doReturn(planningExecutor).when(taskAssigningService).createPlanningExecutor(clientServices, config);
        doReturn(solutionDataLoader).when(taskAssigningService).createSolutionDataLoader(taskServiceConnector, userServiceConnector);

        assertDoesNotThrow(() -> taskAssigningService.start());

        verify(taskAssigningService).createContext();
        verify(userTaskEventConsumer).setConsumer(userTaskEventsConsumerCaptor.capture());
        verify(managedExecutor).execute(solverExecutor);
        verify(managedExecutor).execute(planningExecutor);
        verify(managedExecutor).execute(solutionDataLoader);
        verify(solutionDataLoader).start(solutionDataLoaderConsumerCaptor.capture(),
                eq(true), eq(true), eq(DATA_LOADER_RETRY_INTERVAL), eq(DATA_LOADER_RETRIES), eq(DATA_LOADER_PAGE_SIZE));
    }

    private void prepareStartAndSetInitialSolution(SolutionDataLoader.Result result) throws Exception {
        prepareStart();
        solutionDataLoaderConsumerCaptor.getValue().accept(result);
        verify(solverExecutor).start(solutionCaptor.capture());
        TaskAssigningSolution initialSolution = solutionCaptor.getValue();
        BestSolutionChangedEvent<TaskAssigningSolution> solutionChangedEvent = mockEvent(initialSolution, true, true);
        solverListenerCaptor.getValue().bestSolutionChanged(solutionChangedEvent);
        verify(userTaskEventConsumer).resume();
    }

    private static org.kie.kogito.taskassigning.user.service.api.User mockExternalUser(String id) {
        org.kie.kogito.taskassigning.user.service.api.User user = mock(org.kie.kogito.taskassigning.user.service.api.User.class);
        doReturn(id).when(user).getId();
        doReturn(Collections.emptyMap()).when(user).getAttributes();
        doReturn(Collections.emptySet()).when(user).getGroups();
        return user;
    }

    private static UserTaskEvent mockUserTaskEvent(String taskId, String state, ZonedDateTime lastUpdate) {
        UserTaskEvent userTaskEvent = new UserTaskEvent();
        userTaskEvent.setTaskId(taskId);
        userTaskEvent.setState(state);
        userTaskEvent.setLastUpdate(lastUpdate);
        userTaskEvent.setPotentialUsers(Collections.emptyList());
        userTaskEvent.setPotentialGroups(Collections.emptyList());
        userTaskEvent.setExcludedUsers(Collections.emptyList());
        userTaskEvent.setAdminUsers(Collections.emptyList());
        userTaskEvent.setAdminGroups(Collections.emptyList());
        return userTaskEvent;
    }

    @SuppressWarnings("unchecked")
    private static BestSolutionChangedEvent<TaskAssigningSolution> mockEvent(TaskAssigningSolution solution,
            boolean allChangesProcessed,
            boolean solutionInitialized) {
        BestSolutionChangedEvent<TaskAssigningSolution> event = mock(BestSolutionChangedEvent.class);
        doReturn(allChangesProcessed).when(event).isEveryProblemFactChangeProcessed();
        BendableLongScore score = BendableLongScore.zero(1, 1).withInitScore(solutionInitialized ? 1 : -1);
        TaskAssigningSolution spySolution = spy(solution);
        doReturn(score).when(spySolution).getScore();
        doReturn(spySolution).when(event).getNewBestSolution();
        return event;
    }

    private static void assertHasAddTaskChangeForTask(List<ProblemFactChange<TaskAssigningSolution>> problemFactChanges, String expectedTaskId) {
        List<AddTaskProblemFactChange> addChanges = filterByType(problemFactChanges, AddTaskProblemFactChange.class)
                .filter(addTaskChange -> expectedTaskId.equals(addTaskChange.getTaskAssignment().getId()))
                .collect(Collectors.toList());
        assertThat(addChanges)
                .withFailMessage("One AddTaskProblemFactChange for task: %s is expected, but there are %s.",
                        expectedTaskId, addChanges.size())
                .hasSize(1);
    }

    private static void assertHasRemoveTaskChangeForTask(List<ProblemFactChange<TaskAssigningSolution>> problemFactChanges, String expectedTaskId) {
        List<RemoveTaskProblemFactChange> removeChanges = filterByType(problemFactChanges, RemoveTaskProblemFactChange.class)
                .filter(removeTaskChange -> expectedTaskId.equals(removeTaskChange.getTaskAssignment().getId()))
                .collect(Collectors.toList());
        assertThat(removeChanges)
                .withFailMessage("One RemoveTaskProblemFactChange for task: %s is expected, but there are %s.",
                        expectedTaskId, removeChanges.size())
                .hasSize(1);
    }

    private static void assertHasAssignTaskChangeForTask(List<ProblemFactChange<TaskAssigningSolution>> problemFactChanges,
            String expectedTaskId, String expectedUserId) {
        List<AssignTaskProblemFactChange> assignChanges = filterByType(problemFactChanges, AssignTaskProblemFactChange.class)
                .filter(assignChange -> expectedTaskId.equals(assignChange.getTaskAssignment().getId()))
                .filter(assignChange -> expectedUserId.equals(assignChange.getUser().getId()))
                .collect(Collectors.toList());
        assertThat(assignChanges)
                .withFailMessage("One AssignTaskProblemFactChange for task: %s and user: %s is expected, but there are %s.",
                        expectedTaskId, expectedUserId, assignChanges.size())
                .hasSize(1);
    }

    @SuppressWarnings("unchecked")
    private static <T extends ProblemFactChange<TaskAssigningSolution>> Stream<T> filterByType(List<? extends ProblemFactChange<TaskAssigningSolution>> changes, Class<T> clazz) {
        return changes.stream()
                .filter(Objects::nonNull)
                .filter(clazz::isInstance)
                .map(change -> (T) change);
    }

    private class TaskAssigningServiceMock extends TaskAssigningService {
        @Override
        void processTaskEvents(List<UserTaskEvent> events) {
            super.processTaskEvents(events);
            eventsProcessed.countDown();
        }
    }
}
