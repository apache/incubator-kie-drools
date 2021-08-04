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
package org.kie.kogito.addon.cloudevents.quarkus.message;

import org.eclipse.microprofile.reactive.messaging.Message;
import org.kie.kogito.addon.cloudevents.message.MessagePayloadDecorator;
import org.kie.kogito.addon.cloudevents.message.MessagePayloadDecoratorProvider;

/**
 * Microprofile Message factory for CloudEvents Addon. All messages produced by the addon is created by this factory.
 * Others can extend the behavior of this factory by implementing {@link MessageDecorator}s and {@link MessagePayloadDecorator}.
 */
public final class MessageFactory {

    private final MessageDecorator messageDecorator;
    private final MessagePayloadDecoratorProvider payloadDecoratorProvider;

    public MessageFactory(boolean useCloudEvents) {
        this.messageDecorator = MessageDecoratorFactory.newInstance(useCloudEvents);
        this.payloadDecoratorProvider = MessagePayloadDecoratorProvider.getInstance();
    }

    public Message<String> build(final String payload) {
        String decoratedPayload = this.payloadDecoratorProvider.decorate(payload);
        return this.messageDecorator.decorate(decoratedPayload);
    }

    public MessageDecorator getMessageDecorator() {
        return messageDecorator;
    }

    public MessagePayloadDecoratorProvider getPayloadDecoratorProvider() {
        return payloadDecoratorProvider;
    }
}
