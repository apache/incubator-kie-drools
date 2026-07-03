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
package org.kie.kogito.app.jobs.jpa;

import org.kie.kogito.Model;
import org.kie.kogito.process.Process;

/**
 * Represents a data isolation key for filtering jobs by process identity.
 * Currently contains only processId, but designed to support future expansion
 * to include additional fields for composite key filtering.
 *
 * @param processId The process ID used for data isolation filtering
 */

public record DataIsolationKeyDescriptor(String processId, String processVersion) {

    // Compact constructor for validation
    public DataIsolationKeyDescriptor {
        if (processId == null || processId.isBlank()) {
            throw new IllegalArgumentException("processId is required and cannot be blank");
        }
    }

    public static DataIsolationKeyDescriptor fromProcess(Process<? extends Model> process) {
        return new DataIsolationKeyDescriptor(process.id(), process.version());
    }
}
