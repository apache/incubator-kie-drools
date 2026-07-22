/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.kie.kogito.event;

import java.net.URI;
import java.time.OffsetDateTime;

import io.cloudevents.SpecVersion;

public record DataEventState<E> (
        E data,
        String dataContentType,
        URI dataSchema,
        String id,
        String kogitoAddons,
        String kogitoBusinessKey,
        String kogitoIdentity,
        String kogitoParentProcessInstanceId,
        String kogitoProcessId,
        String kogitoProcessInstanceId,
        String kogitoProcessInstanceState,
        String kogitoProcessType,
        String kogitoProcessVersion,
        String kogitoReferenceId,
        String kogitoRootProcessId,
        String kogitoRootProcessInstanceId,
        String kogitoRootProcessVersion,
        String kogitoStartFromNode,
        URI source,
        SpecVersion specVersion,
        String subject,
        OffsetDateTime time,
        String type) {
}
