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
package org.kie.kogito.event;

import java.io.IOException;

/**
 * This interface is one of the extension point for customers to incorporate more event formats when NOT using cloud events.
 * It is responsible for converting objects received in the external service format into business objects consumed by Kogito.
 * Default implementation of Kogito uses Jackson and JSON.
 *
 * @param <S> the external service object type
 */
public interface EventUnmarshaller<S> {

    /**
     * Convert object received from the external service, denoted by generic type S, into a business object
     * 
     * @param <T> business object type
     * @param input object received by the external system
     * @param outputClass target business class
     * @param parametrizedClasses business class might use generics
     * @return kogito business object
     * @throws IOException if there is any problem with the format. This method should not willingly throw any runtime
     */
    <T> T unmarshall(S input, Class<T> outputClass, Class<?>... parametrizedClasses) throws IOException;
}
