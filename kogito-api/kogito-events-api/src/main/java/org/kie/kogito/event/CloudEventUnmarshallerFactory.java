/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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

/**
 * This interface is one of the extension point for customers to incorporate more event formats when using cloud events.
 * It is responsible for creating CloudEventUnmarshaller instance for an specific business target class.
 *
 * @param <S> Kogito business object type
 * @param <T> External service object type
 */
public interface CloudEventUnmarshallerFactory<T> {
    /**
     * Creates a Cloud event unmarshaller object
     * 
     * @param <S> external service input type
     * @param targetClass target business object type
     * @return Unmarshaller object that will be used to create CloudEvents for that type
     */
    <S> CloudEventUnmarshaller<T, S> unmarshaller(Class<S> targetClass);
}
