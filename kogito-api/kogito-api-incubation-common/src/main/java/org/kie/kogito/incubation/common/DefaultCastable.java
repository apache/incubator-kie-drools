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

package org.kie.kogito.incubation.common;

import org.kie.kogito.incubation.common.objectmapper.InternalObjectMapper;

/**
 * Utility interface, useful to mix-in to get a default `as` implementation.
 * <p>
 * Provides a default implementation for the {@link #as(Class)} method,
 * delegating to {@link org.kie.kogito.incubation.common.objectmapper.InternalObjectMapper#convertValue(Object, Class)}
 */
public interface DefaultCastable extends Castable {

    default <T extends DataContext> T as(Class<T> type) {
        if (type.isInstance(this)) {
            return type.cast(this);
        }
        return InternalObjectMapper.objectMapper().convertValue(this, type);
    }
}
