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

public class ServiceMessage {

    public enum Type {
        INFO,
        WARN,
        ERROR
    }

    private Type type;
    private ZonedDateTime time;
    private String value;

    private ServiceMessage() {
        // marshalling constructor
    }

    private ServiceMessage(Type type, ZonedDateTime time, String value) {
        this.type = type;
        this.time = time;
        this.value = value;
    }

    public Type getType() {
        return type;
    }

    public ZonedDateTime getTime() {
        return time;
    }

    public String getValue() {
        return value;
    }

    public static ServiceMessage info(String value) {
        return new ServiceMessage(Type.INFO, ZonedDateTime.now(), value);
    }

    public static ServiceMessage warn(String value) {
        return new ServiceMessage(Type.WARN, ZonedDateTime.now(), value);
    }

    public static ServiceMessage error(String value) {
        return new ServiceMessage(Type.ERROR, ZonedDateTime.now(), value);
    }
}