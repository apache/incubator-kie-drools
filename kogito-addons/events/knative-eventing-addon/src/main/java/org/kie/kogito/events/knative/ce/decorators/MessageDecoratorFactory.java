/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.kogito.events.knative.ce.decorators;

import java.util.Optional;

/**
 * Decorator Factory
 */
public final class MessageDecoratorFactory {

    private static final String SMALLRYE_HTTP_METADATA_CLASS = "io.smallrye.reactive.messaging.http.HttpResponseMetadata";

    private MessageDecoratorFactory() {
    }

    /**
     * Builds a new {@link MessageDecorator} depending on the implementation being presented in the classpath.
     *
     * @return an {@link Optional} instance of {@link MessageDecorator}
     */
    public static Optional<MessageDecorator> newInstance() {
        try {
            Class.forName(SMALLRYE_HTTP_METADATA_CLASS, false, MessageDecoratorFactory.class.getClassLoader());
            return Optional.of(new CloudEventHttpOutgoingDecorator());
        } catch (ClassNotFoundException e) {
            return Optional.empty();
        }
    }

}
