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
package org.kie.kogito.event;

import java.util.function.Consumer;

/**
 * Generic receiver for cloud events.
 *
 * Implementations provide their specific (usually injectable) behavior.
 *
 */
public interface EventReceiver {

    /**
     * Helper method to subscribe to the events.
     * 
     * @param consumer the consumer that will receive the events.
     * @param clazz the type of object the event contains
     */
    <T> void subscribe(Consumer<T> consumer, SubscriptionInfo<T> subscription);
}
