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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kie.kogito.taskassigning.service.TaskAssigningServiceContext;
import org.kie.kogito.taskassigning.service.messaging.UserTaskEvent;

public class EventUtil {

    private EventUtil() {
    }

    /**
     * Given a list of events finds the newest event per each task (in case if any) that were never processed in current
     * context and adds it to the results list.
     * The returned events are automatically marked as processed in the context.
     *
     * @param context the context instance that holds the processed events information.
     * @param userTaskEvents a list of events to filter.
     * @return a list of events were each event is the newest one that could be found for the given task that was never
     *         processed in the current context before.
     */
    public static List<UserTaskEvent> filterNewestTaskEventsInContext(TaskAssigningServiceContext context, List<UserTaskEvent> userTaskEvents) {
        List<UserTaskEvent> result = new ArrayList<>();
        List<UserTaskEvent> newestTaskEvents = filterNewestTaskEvents(userTaskEvents);
        for (UserTaskEvent taskEvent : newestTaskEvents) {
            if (context.isNewTaskEventTime(taskEvent.getTaskId(), taskEvent.getLastUpdate())) {
                context.setTaskLastEventTime(taskEvent.getTaskId(), taskEvent.getLastUpdate());
                result.add(taskEvent);
            }
        }
        return result;
    }

    /**
     * Given a list of events finds the newest event per each task (in case if any) and it to the results list.
     * 
     * @param userTaskEvents a list of events to filter.
     * @return a list of events were each event is the newest one that could be found for the given task.
     */
    public static List<UserTaskEvent> filterNewestTaskEvents(List<UserTaskEvent> userTaskEvents) {
        Map<String, UserTaskEvent> lastEventPerTask = new HashMap<>();
        UserTaskEvent previousEventForTask;
        for (UserTaskEvent event : userTaskEvents) {
            previousEventForTask = lastEventPerTask.get(event.getTaskId());
            if (previousEventForTask == null || event.getLastUpdate().isAfter(previousEventForTask.getLastUpdate())) {
                lastEventPerTask.put(event.getTaskId(), event);
            }
        }
        return new ArrayList<>(lastEventPerTask.values());
    }
}
