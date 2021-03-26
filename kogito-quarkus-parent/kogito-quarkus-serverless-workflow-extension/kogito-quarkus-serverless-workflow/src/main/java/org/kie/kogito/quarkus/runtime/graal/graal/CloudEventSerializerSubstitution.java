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
package org.kie.kogito.quarkus.runtime.graal.graal;

import com.oracle.svm.core.annotate.Substitute;
import com.oracle.svm.core.annotate.TargetClass;

import io.smallrye.mutiny.Uni;
import io.smallrye.reactive.messaging.cloudevents.CloudEventMessage;
import io.smallrye.reactive.messaging.http.converters.CloudEventSerializer;
import io.vertx.mutiny.core.buffer.Buffer;

/**
 * Avoid Smallrye CloudEvent Serializers to be initialized on build time. It will try to initialize CloudEvents Serializer that's not been used.
 * We use CloudEvents SDK directly instead.
 */
@TargetClass(CloudEventSerializer.class)
final class CloudEventSerializerSubstitution {

    @Substitute
    public Uni<Buffer> convert(CloudEventMessage payload) {
        throw new UnsupportedOperationException("This CloudEvent serializer shouldn't be used");
    }
}
