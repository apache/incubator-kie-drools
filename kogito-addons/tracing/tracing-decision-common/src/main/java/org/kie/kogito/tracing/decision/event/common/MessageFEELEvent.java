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

import com.fasterxml.jackson.annotation.JsonInclude;
import org.kie.dmn.api.feel.runtime.events.FEELEvent;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

public class MessageFEELEvent {

    private final FEELEvent.Severity severity;
    private final String message;
    @JsonInclude(NON_NULL)
    private final Integer line;
    @JsonInclude(NON_NULL)
    private final Integer column;
    @JsonInclude(NON_NULL)
    private final MessageExceptionField sourceException;

    public MessageFEELEvent(FEELEvent.Severity severity, String message, Integer line, Integer column, MessageExceptionField sourceException) {
        this.severity = severity;
        this.message = message;
        this.line = line == null || line < 0 ? null : line;
        this.column = column == null || column < 0 ? null : column;
        this.sourceException = sourceException;
    }

    public FEELEvent.Severity getSeverity() {
        return severity;
    }

    public String getMessage() {
        return message;
    }

    public Integer getLine() {
        return line;
    }

    public Integer getColumn() {
        return column;
    }

    public MessageExceptionField getSourceException() {
        return sourceException;
    }

    public static MessageFEELEvent from(FEELEvent feelEvent) {
        if (feelEvent == null) {
            return null;
        }
        return new MessageFEELEvent(
                feelEvent.getSeverity(),
                feelEvent.getMessage(),
                feelEvent.getLine(),
                feelEvent.getColumn(),
                MessageExceptionField.from(feelEvent.getSourceException())
        );
    }
}
