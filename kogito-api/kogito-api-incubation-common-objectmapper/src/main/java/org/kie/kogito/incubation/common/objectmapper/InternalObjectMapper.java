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

package org.kie.kogito.incubation.common.objectmapper;

import java.util.ServiceLoader;

/**
 * For internal use only.
 * Provides a method to convert an object into a given type.
 * This is an implementation detail. We may move this to a separate module in the future.
 */
public interface InternalObjectMapper {
    <T> T convertValue(Object self, Class<T> type);

    public static InternalObjectMapper objectMapper() {
        return ServiceLoader.load(InternalObjectMapper.class).findFirst()
                .orElseThrow(MissingInternalObjectMapper::new);
    }
}
