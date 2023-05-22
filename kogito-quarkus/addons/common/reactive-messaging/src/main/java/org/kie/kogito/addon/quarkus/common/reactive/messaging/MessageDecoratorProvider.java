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
package org.kie.kogito.addon.quarkus.common.reactive.messaging;

import java.util.Collection;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.eclipse.microprofile.reactive.messaging.Message;

/**
 * Provides a {@link MessageDecorator} instance. Ideally should not be used outside the {@link MessageFactory}.
 */
@ApplicationScoped
public class MessageDecoratorProvider {

    @Inject
    Instance<MessageDecorator> messageDecorators;

    private Collection<MessageDecorator> sortedMessageDecorators;

    @PostConstruct
    void init() {
        sortedMessageDecorators = messageDecorators.stream().sorted().collect(Collectors.toList());
    }

    /**
     * Builds a new {@link MessageDecorator} depending on the implementation being presented in the classpath.
     *
     * @return an instance of {@link MessageDecorator}
     */
    public <T> Message<T> decorate(Message<T> message) {
        for (MessageDecorator messageDecorator : sortedMessageDecorators) {
            message = messageDecorator.decorate(message);
        }
        return message;
    }
}
