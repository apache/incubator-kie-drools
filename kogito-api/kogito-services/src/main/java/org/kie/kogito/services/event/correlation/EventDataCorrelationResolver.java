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
package org.kie.kogito.services.event.correlation;

import java.util.Objects;
import java.util.Optional;

import org.kie.kogito.correlation.Correlation;

public class EventDataCorrelationResolver extends SimpleAttributeCorrelationResolver {

    public static final String DATA_REFERENCE_KEY = "data";

    public EventDataCorrelationResolver() {
        super(DATA_REFERENCE_KEY);
    }

    @Override
    public Correlation resolve(Object data) {
        return Optional.of(super.resolve(data)).filter(c -> Objects.nonNull(c.getValue())).orElse(new Correlation(DATA_REFERENCE_KEY, data));
    }
}
