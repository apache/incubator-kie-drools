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

package org.kie.kogito.taskassigning.service.event;

import java.time.ZonedDateTime;

public abstract class DataEvent<T> {

    public enum DataEventType {
        TASK_DATA_EVENT,
        USER_DATA_EVENT;
    }

    protected DataEventType dataEventType;
    protected T data;
    protected ZonedDateTime eventTime;

    protected DataEvent(DataEventType dataEventType, T data, ZonedDateTime eventTime) {
        this.dataEventType = dataEventType;
        this.data = data;
        this.eventTime = eventTime;
    }

    public DataEventType getDataEventType() {
        return dataEventType;
    }

    public T getData() {
        return data;
    }

    public void setEventTime(ZonedDateTime eventTime) {
        this.eventTime = eventTime;
    }

    public ZonedDateTime getEventTime() {
        return eventTime;
    }
}
