/*
 *  Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.kie.kogito.tracing.decision.event.common;

import org.kie.api.builder.Message;

public enum InternalMessageType {
    DMN_MODEL_NOT_FOUND(Message.Level.ERROR, "DMN model not found"),
    NO_EXECUTION_STEP_HIERARCHY(Message.Level.WARNING, "Can't build execution step hierarchy"),
    NOT_ENOUGH_DATA(Message.Level.ERROR, "Not enough data to build a valid TraceEvent");

    private final Message.Level level;
    private final String text;

    InternalMessageType(Message.Level level, String text) {
        this.level = level;
        this.text = text;
    }

    public Message.Level getLevel() {
        return level;
    }

    public String getText() {
        return text;
    }
}
