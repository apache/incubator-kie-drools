/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.kogito.tracing.decision.event;

import org.kie.dmn.api.core.DMNMessage;
import org.kie.dmn.api.core.DMNMessageType;

public class EvaluateEventMessage {

    private final DMNMessageType type;
    private final org.kie.api.builder.Message.Level level;
    private final String text;
    private final String sourceId;

    public EvaluateEventMessage(DMNMessageType type, org.kie.api.builder.Message.Level level, String text, String sourceId) {
        this.type = type;
        this.level = level;
        this.text = text;
        this.sourceId = sourceId;
    }

    public DMNMessageType getType() {
        return type;
    }

    public org.kie.api.builder.Message.Level getLevel() {
        return level;
    }

    public String getText() {
        return text;
    }

    public String getSourceId() {
        return sourceId;
    }

    static EvaluateEventMessage from(DMNMessage msg) {
        return new EvaluateEventMessage(
                msg.getMessageType(),
                msg.getLevel(),
                msg.getText(),
                msg.getSourceId()
        );
    }

}
