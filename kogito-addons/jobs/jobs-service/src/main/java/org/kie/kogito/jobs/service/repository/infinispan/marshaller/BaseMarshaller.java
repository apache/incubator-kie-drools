/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.kogito.jobs.service.repository.infinispan.marshaller;

import java.io.IOException;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.Optional;

import org.infinispan.protostream.MessageMarshaller;
import org.kie.kogito.jobs.service.utils.DateUtil;

public abstract class BaseMarshaller<T> implements MessageMarshaller<T> {

    public String getPackage() {
        return "job.service";
    }

    protected Instant zonedDateTimeToInstant(ZonedDateTime dateTime) {
        return Optional.ofNullable(dateTime).map(ZonedDateTime::toInstant).orElse(null);
    }

    protected ZonedDateTime instantToZonedDateTime(Instant instant) throws IOException {
        return Optional.ofNullable(instant)
                .map(i -> ZonedDateTime.ofInstant(i, DateUtil.DEFAULT_ZONE))
                .orElse(null);
    }
}
