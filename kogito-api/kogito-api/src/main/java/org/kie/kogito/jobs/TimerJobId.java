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
package org.kie.kogito.jobs;

import java.text.MessageFormat;
import java.text.ParseException;
import java.util.UUID;

import org.kie.kogito.timer.TimerInstance;

public class TimerJobId implements JobId<Long, TimerInstance> {

    public static final String SIGNAL = "timerTriggered";
    private static MessageFormat format = new MessageFormat("{0}:{1}:{2}");
    public static final String TYPE = "TIMER";
    private Long timerId;
    private String uuid;

    public TimerJobId() {
    }

    public TimerJobId(Long timerId) {
        this.timerId = timerId;
        this.uuid = UUID.randomUUID().toString();
    }

    public TimerJobId(String uuid) {
        this.uuid = uuid;
        this.timerId = 0L;
    }

    @Override
    public String encode() {
        return format.format(new Object[] { TYPE, timerId, uuid });
    }

    @Override
    public String signal() {
        return SIGNAL;
    }

    @Override
    public Long correlationId() {
        return timerId;
    }

    @Override
    public TimerJobId decode(String value) {
        try {
            Object[] values = format.parse(value);
            this.timerId = Long.parseLong((String) values[1]);
            this.uuid = (String) values[2];
            return this;
        } catch (ParseException e) {
            throw new IllegalArgumentException(e);
        }
    }

    @Override
    public TimerInstance payload(Object... parameters) {
        Integer limit = (Integer) parameters[0];
        return TimerInstance.with(timerId, encode(), limit);
    }
}
