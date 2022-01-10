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
package org.kie.kogito.addon.cloudevents.message;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.ServiceLoader;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provider for {@link MessagePayloadDecorator} instances.
 */
public final class MessagePayloadDecoratorProvider implements MessagePayloadDecorator {

    private static final Logger LOGGER = LoggerFactory.getLogger(MessagePayloadDecoratorProvider.class);

    private final Set<MessagePayloadDecorator> payloadDecorators;

    private MessagePayloadDecoratorProvider() {
        this.payloadDecorators = new LinkedHashSet<>();
        final ServiceLoader<MessagePayloadDecorator> loader = ServiceLoader.load(MessagePayloadDecorator.class);
        loader.iterator().forEachRemaining(this.payloadDecorators::add);
        LOGGER.debug("Payload decorators loaded {}", this.payloadDecorators);
    }

    public static MessagePayloadDecoratorProvider getInstance() {
        return LazyHolder.INSTANCE;
    }

    public Set<MessagePayloadDecorator> getPayloadDecorators() {
        return Collections.unmodifiableSet(payloadDecorators);
    }

    /**
     * Decorates the message payload using all {@link MessagePayloadDecorator}s found in the classpath.
     * 
     * @param jsonPayload the CloudEvent in JSON format
     * @return the CloudEvent JSON decorated
     */
    @Override
    public String decorate(final String jsonPayload) {
        String decoratedPayload = jsonPayload;
        for (final MessagePayloadDecorator d : this.payloadDecorators) {
            decoratedPayload = d.decorate(decoratedPayload);
        }
        LOGGER.debug("Message payload decorated {}", decoratedPayload);
        return jsonPayload;
    }

    private static class LazyHolder {
        private static final MessagePayloadDecoratorProvider INSTANCE = new MessagePayloadDecoratorProvider();
    }
}
