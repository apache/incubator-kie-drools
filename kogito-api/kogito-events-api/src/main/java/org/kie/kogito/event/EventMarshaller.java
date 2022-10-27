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

import java.io.IOException;

/**
 * This interface is one of the extension point for customers to incorporate more event formats.
 * It is responsible for converting Kogito business objects into the format expected by the external service when not using cloud events.
 * 
 * @param <R> The expected output type that will be consumed by the external service
 */
public interface EventMarshaller<R> {
    /**
     * Converts Kogito business object into external service one
     * 
     * @param <T> Kogito business object type
     * @param event Kogito business object.
     * @return
     */
    <T> R marshall(T event) throws IOException;
}
