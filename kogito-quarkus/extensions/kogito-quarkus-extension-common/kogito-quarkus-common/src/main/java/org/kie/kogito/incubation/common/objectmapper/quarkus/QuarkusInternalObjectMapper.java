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

package org.kie.kogito.incubation.common.objectmapper.quarkus;

import java.util.Map;

import javax.enterprise.inject.spi.CDI;

import org.kie.kogito.incubation.common.DataContext;
import org.kie.kogito.incubation.common.ExtendedDataContext;
import org.kie.kogito.incubation.common.MapDataContext;
import org.kie.kogito.incubation.common.MapLikeDataContext;
import org.kie.kogito.incubation.common.objectmapper.InternalObjectMapper;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * This class bridges CDI to non-CDI classes by using <code>CDI.current().select()</code>
 */
public class QuarkusInternalObjectMapper implements InternalObjectMapper {

    @Override
    public <T> T convertValue(Object self, Class<T> type) {
        if (type.isInstance(self)) {
            return type.cast(self);
        }

        ObjectMapper objectMapper =
                CDI.current().select(ObjectMapper.class).get();

        if (MapLikeDataContext.class == type || MapDataContext.class == type) {
            return (T) MapDataContext.of(objectMapper.convertValue(self, Map.class));
        }

        if (ExtendedDataContext.class == type) {
            if (self instanceof DataContext) {
                return (T) ExtendedDataContext.ofData((DataContext) self);
            } else {
                return (T) ExtendedDataContext.ofData(convertValue(self, MapDataContext.class));
            }
        }

        return objectMapper.convertValue(self, type);
    }
}
