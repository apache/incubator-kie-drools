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
package org.kie.kogito.codegen.core.events;

import java.util.Set;

import org.kie.kogito.event.cloudevents.CloudEventMeta;

/**
 * {@link CloudEventMeta} builder
 *
 * @param <C> the {@link CloudEventMeta} DTO for the given context
 * @param <S> the source to extract the {@link CloudEventMeta} information
 */
public interface CloudEventMetaBuilder<C extends CloudEventMeta, S> {

    /**
     * Generates a {@link Set} of {@link CloudEventMeta} objects based on the given engine model
     *
     * @param sourceModel the given executor model
     */
    Set<C> build(S sourceModel);
}
