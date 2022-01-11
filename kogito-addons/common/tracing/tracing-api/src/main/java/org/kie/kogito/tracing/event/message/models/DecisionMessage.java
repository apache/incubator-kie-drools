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
package org.kie.kogito.tracing.event.message.models;

import org.kie.kogito.ModelDomain;
import org.kie.kogito.tracing.event.message.Message;
import org.kie.kogito.tracing.event.message.MessageCategory;
import org.kie.kogito.tracing.event.message.MessageExceptionField;
import org.kie.kogito.tracing.event.message.MessageFEELEvent;
import org.kie.kogito.tracing.event.message.MessageLevel;

import com.fasterxml.jackson.annotation.JsonInclude;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@JsonInclude(NON_NULL)
public final class DecisionMessage extends Message {

    private MessageFEELEvent feelEvent;

    private DecisionMessage() {
        // needed for serialization
    }

    public DecisionMessage(MessageLevel level, MessageCategory category, String type, String sourceId, String text, MessageFEELEvent feelEvent, MessageExceptionField exception) {
        super(level, category, type, sourceId, text, exception, ModelDomain.DECISION);
        this.feelEvent = feelEvent;
    }

    public MessageFEELEvent getFeelEvent() {
        return feelEvent;
    }

}
