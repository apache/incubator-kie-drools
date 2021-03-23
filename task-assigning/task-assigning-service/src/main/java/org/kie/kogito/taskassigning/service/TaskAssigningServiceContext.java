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

import java.time.ZonedDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class TaskAssigningServiceContext {

    private static class TaskContext {
        private boolean published;
        private ZonedDateTime lastEventTime;

        public synchronized void setPublished(boolean published) {
            this.published = published;
        }

        public synchronized boolean isPublished() {
            return published;
        }

        public synchronized ZonedDateTime getLastEventTime() {
            return lastEventTime;
        }

        public synchronized void setLastEventTime(ZonedDateTime lastEventTime) {
            this.lastEventTime = lastEventTime;
        }
    }

    private AtomicLong changeSetIds = new AtomicLong();
    private AtomicLong currentChangeSetId = new AtomicLong();
    private AtomicLong lastProcessedChangeSetId = new AtomicLong(-1);
    private Map<String, TaskContext> taskContextMap = new ConcurrentHashMap<>();

    public long getCurrentChangeSetId() {
        return currentChangeSetId.get();
    }

    public void setCurrentChangeSetId(long currentChangeSetId) {
        this.currentChangeSetId.set(currentChangeSetId);
    }

    public long nextChangeSetId() {
        return changeSetIds.incrementAndGet();
    }

    public boolean isProcessedChangeSet(long changeSetId) {
        return changeSetId <= lastProcessedChangeSetId.get();
    }

    public boolean isCurrentChangeSetProcessed() {
        return isProcessedChangeSet(currentChangeSetId.get());
    }

    public void setProcessedChangeSet(long changeSetId) {
        lastProcessedChangeSetId.set(changeSetId);
    }

    public void setTaskPublished(String taskId, boolean published) {
        TaskContext taskContext = taskContextMap.computeIfAbsent(taskId, id -> new TaskContext());
        taskContext.setPublished(published);
    }

    public boolean isTaskPublished(String taskId) {
        TaskContext taskContext = taskContextMap.get(taskId);
        return taskContext != null && taskContext.isPublished();
    }

    public void setTaskLastEventTime(String taskId, ZonedDateTime lastEventTime) {
        TaskContext taskContext = taskContextMap.computeIfAbsent(taskId, id -> new TaskContext());
        taskContext.setLastEventTime(lastEventTime);
    }

    public ZonedDateTime getTaskLastEventTime(String taskId) {
        TaskContext taskContext = taskContextMap.get(taskId);
        return taskContext != null ? taskContext.getLastEventTime() : null;
    }

    public boolean isNewTaskEventTime(String taskId, ZonedDateTime taskEventTime) {
        ZonedDateTime lastTaskEventTime = getTaskLastEventTime(taskId);
        return lastTaskEventTime == null || taskEventTime.isAfter(lastTaskEventTime);
    }
}
