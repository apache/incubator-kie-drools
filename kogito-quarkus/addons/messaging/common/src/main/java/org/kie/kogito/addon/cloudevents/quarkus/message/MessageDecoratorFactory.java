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

/**
 * Provides a {@link MessageDecorator} instance. Ideally should not be used outside the {@link MessageFactory}.
 */
public final class MessageDecoratorFactory {

    private static final String QUARKUS_HTTP_METADATA_CLASS = "io.quarkus.reactivemessaging.http.runtime.OutgoingHttpMetadata";

    private MessageDecoratorFactory() {
    }

    public static MessageDecorator newInstance() {
        return newInstance(true);
    }

    /**
     * Builds a new {@link MessageDecorator} depending on the implementation being presented in the classpath.
     *
     * @return an instance of {@link MessageDecorator}
     */
    public static MessageDecorator newInstance(boolean useCloudEvent) {
        if (useCloudEvent) {
            try {
                Class.forName(QUARKUS_HTTP_METADATA_CLASS, false, MessageDecoratorFactory.class.getClassLoader());
                return new CloudEventHttpOutgoingDecorator();
            } catch (ClassNotFoundException e) {
                // returning NoOpMessageDecorator (complementary comment forced by sonar) 
            }
        }
        return new NoOpMessageDecorator();
    }
}
