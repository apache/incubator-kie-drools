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
import java.util.Optional;

import org.kie.kogito.event.Converter;

import io.cloudevents.CloudEventData;
import io.cloudevents.core.data.PojoCloudEventData;

public abstract class AbstractCloudEventDataConverter<O> implements Converter<CloudEventData, O> {

    protected final Class<O> targetClass;

    protected AbstractCloudEventDataConverter(Class<O> targetClass) {
        this.targetClass = targetClass;
    }

    @Override
    public O convert(CloudEventData value) throws IOException {
        if (value == null) {
            return null;
        }
        return isTargetInstanceAlready(value).orElse(toValue(value));
    }

    protected Optional<O> isTargetInstanceAlready(CloudEventData value) {
        if (value instanceof PojoCloudEventData) {
            Object pojo = ((PojoCloudEventData<?>) value).getValue();
            if (targetClass.isAssignableFrom(pojo.getClass())) {
                return Optional.of(targetClass.cast(pojo));
            }
        }
        return Optional.empty();
    }

    protected abstract O toValue(CloudEventData value) throws IOException;
}
