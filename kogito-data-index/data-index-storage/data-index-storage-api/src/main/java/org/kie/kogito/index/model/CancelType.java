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
package org.kie.kogito.index.model;

import static org.kie.kogito.event.process.ProcessInstanceNodeEventBody.EVENT_TYPE_ABORTED;
import static org.kie.kogito.event.process.ProcessInstanceNodeEventBody.EVENT_TYPE_ERROR;
import static org.kie.kogito.event.process.ProcessInstanceNodeEventBody.EVENT_TYPE_OBSOLETE;
import static org.kie.kogito.event.process.ProcessInstanceNodeEventBody.EVENT_TYPE_SKIPPED;

public enum CancelType {
    ABORTED,
    SKIPPED,
    OBSOLETE,
    ERROR;

    public static CancelType fromEventType(Integer eventType) {
        return switch (eventType) {
            case EVENT_TYPE_ABORTED -> ABORTED;
            case EVENT_TYPE_SKIPPED -> SKIPPED;
            case EVENT_TYPE_OBSOLETE -> OBSOLETE;
            case EVENT_TYPE_ERROR -> ERROR;
            default -> throw new IllegalStateException("Unexpected eventType: " + eventType);
        };
    }
}
