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
package org.kie.kogito.event.impl;

import java.io.IOException;

import org.kie.kogito.event.Converter;
import org.kie.kogito.event.DataEvent;
import org.kie.kogito.event.DataEventFactory;
import org.kie.kogito.event.EventUnmarshaller;

public class DataEventConverter<T, S> implements Converter<T, DataEvent<S>> {

    private final Class<S> objectClass;
    private final EventUnmarshaller<T> unmarshaller;

    public DataEventConverter(Class<S> objectClass, EventUnmarshaller<T> unmarshaller) {
        this.objectClass = objectClass;
        this.unmarshaller = unmarshaller;
    }

    @Override
    public DataEvent<S> convert(T value) throws IOException {
        return DataEventFactory.from(unmarshaller.unmarshall(value, objectClass));
    }
}
