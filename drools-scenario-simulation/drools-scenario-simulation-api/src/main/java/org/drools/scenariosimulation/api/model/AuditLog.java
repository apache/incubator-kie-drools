/**
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
package org.drools.scenariosimulation.api.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Java representation of a full <b>audit</b> report
 */
public class AuditLog {

    /**
     * The <code>List</code> of audit log lines
     */
    private List<AuditLogLine> auditLogLines = new ArrayList<>();

    /**
     * @return an <b>unmodifiable</b> version of {@link AuditLog#auditLogLines}
     */
    public List<AuditLogLine> getAuditLogLines() {
        return Collections.unmodifiableList(auditLogLines);
    }

    /**
     * Add an <code>AuditLogLine</code> to the end of {@link AuditLog#auditLogLines}
     * @param toAdd
     */
    public void addAuditLogLine(AuditLogLine toAdd) {
        auditLogLines.add(toAdd);
    }

    /**
     * Add a <code>List&lt;AuditLogLine&gt;</code> to the end of {@link AuditLog#auditLogLines}
     * @param toAdd
     */
    public void addAuditLogLines(List<AuditLogLine> toAdd) {
        auditLogLines.addAll(toAdd);
    }
}
