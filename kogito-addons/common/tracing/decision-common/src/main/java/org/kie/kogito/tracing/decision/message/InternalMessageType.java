/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.tracing.decision.message;

import org.kie.kogito.tracing.event.message.MessageLevel;

public enum InternalMessageType {
    DMN_MODEL_NOT_FOUND(MessageLevel.ERROR, "DMN model not found"),
    NO_EXECUTION_STEP_HIERARCHY(MessageLevel.WARNING, "Can't build execution step hierarchy"),
    NOT_ENOUGH_DATA(MessageLevel.ERROR, "Not enough data to build a valid TraceEvent");

    private final MessageLevel level;
    private final String text;

    InternalMessageType(MessageLevel level, String text) {
        this.level = level;
        this.text = text;
    }

    public MessageLevel getLevel() {
        return level;
    }

    public String getText() {
        return text;
    }
}
