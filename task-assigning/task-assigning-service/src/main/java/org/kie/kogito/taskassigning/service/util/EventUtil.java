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

package org.kie.kogito.taskassigning.service.util;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import org.kie.kogito.taskassigning.service.TaskAssigningServiceContext;
import org.kie.kogito.taskassigning.service.event.DataEvent;
import org.kie.kogito.taskassigning.service.event.SolutionUpdatedOnBackgroundDataEvent;
import org.kie.kogito.taskassigning.service.event.TaskDataEvent;
import org.kie.kogito.taskassigning.service.event.UserDataEvent;

public class EventUtil {

    private EventUtil() {
    }

    /**
     * Given a list of events finds the newest event per each task (in case if any) that were never processed in current
     * context and adds it to the results list.
     * The returned events are automatically marked as processed in the context.
     *
     * @param context the context instance that holds the processed events information.
     * @param dataEvents a list of events to filter.
     * @return a list of events were each event is the newest one that could be found for the given task that was never
     *         processed in the current context before.
     */
    public static List<TaskDataEvent> filterNewestTaskEventsInContext(TaskAssigningServiceContext context, List<DataEvent<?>> dataEvents) {
        List<TaskDataEvent> result = new ArrayList<>();
        List<TaskDataEvent> newestTaskEvents = filterNewestTaskEvents(dataEvents);
        for (TaskDataEvent taskEvent : newestTaskEvents) {
            if (context.isNewTaskEventTime(taskEvent.getTaskId(), taskEvent.getEventTime())) {
                context.setTaskLastEventTime(taskEvent.getTaskId(), taskEvent.getEventTime());
                result.add(taskEvent);
            }
        }
        return result;
    }

    /**
     * Given a list of events finds the newest event per each task (in case if any) and it to the results list.
     * 
     * @param dataEvents a list of events to filter.
     * @return a list of events were each event is the newest one that could be found for the given task.
     */
    public static <T extends DataEvent<?>> List<TaskDataEvent> filterNewestTaskEvents(List<T> dataEvents) {
        Map<String, TaskDataEvent> lastEventPerTask = new HashMap<>();
        AtomicReference<TaskDataEvent> previousEventForTask = new AtomicReference<>();
        dataEvents.stream()
                .filter(dataEvent -> dataEvent.getDataEventType() == DataEvent.DataEventType.TASK_DATA_EVENT)
                .map(TaskDataEvent.class::cast)
                .forEach(taskDataEvent -> {
                    previousEventForTask.set(lastEventPerTask.get(taskDataEvent.getTaskId()));
                    if (previousEventForTask.get() == null || taskDataEvent.getEventTime().isAfter(previousEventForTask.get().getEventTime())) {
                        lastEventPerTask.put(taskDataEvent.getData().getId(), taskDataEvent);
                    }
                });
        return new ArrayList<>(lastEventPerTask.values());
    }

    /**
     * Given a list of events finds the newest user data event if any.
     * 
     * @param dataEvents a list of events to filter.
     * @return the newest user data event found if any.
     */
    public static UserDataEvent filterNewestUserEvent(List<DataEvent<?>> dataEvents) {
        return dataEvents.stream()
                .filter(event -> event.getDataEventType() == DataEvent.DataEventType.USER_DATA_EVENT)
                .map(UserDataEvent.class::cast)
                .max(Comparator.comparing(DataEvent::getEventTime))
                .orElse(null);
    }

    public static SolutionUpdatedOnBackgroundDataEvent filterNewestSolutionUpdatedOnBackgroundEvent(List<DataEvent<?>> dataEvents) {
        return dataEvents.stream()
                .filter(event -> event.getDataEventType() == DataEvent.DataEventType.SOLUTION_UPDATED_ON_BACKGROUND_DATA_EVENT)
                .map(SolutionUpdatedOnBackgroundDataEvent.class::cast)
                .max(Comparator.comparing(DataEvent::getEventTime))
                .orElse(null);
    }
}
