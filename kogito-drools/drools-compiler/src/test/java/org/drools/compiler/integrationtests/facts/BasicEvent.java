/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.drools.compiler.integrationtests.facts;

import java.io.Serializable;
import java.util.Date;

public class BasicEvent implements Serializable {

    private static final long serialVersionUID = 2172618811749631685L;

    private Date eventTimestamp;
    private Long eventDuration;
    private String name;

    public BasicEvent(final Date eventTimestamp, final Long eventDuration, final String name) {
        this.eventTimestamp = eventTimestamp;
        this.eventDuration = eventDuration;
        this.name = name;
    }

    public BasicEvent() {
    }

    public Date getEventTimestamp() {
        return eventTimestamp;
    }

    public void setEventTimestamp(final Date eventTimestamp) {
        this.eventTimestamp = eventTimestamp;
    }

    public Long getEventDuration() {
        return eventDuration;
    }

    public void setEventDuration(final Long eventDuration) {
        this.eventDuration = eventDuration;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }
}
