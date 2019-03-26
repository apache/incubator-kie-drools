/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.drools.workbench.models.guided.dtable.shared.auditlog;

import org.drools.workbench.models.datamodel.auditlog.AuditLogEntry;

/**
 * An Audit Event when a row is deleted
 */
public class DeleteRowAuditLogEntry extends AuditLogEntry {

    private static final long serialVersionUID = 8049692773593046770L;

    private static final String TYPE = DecisionTableAuditEvents.DELETE_ROW.name();

    public int rowIndex;

    public DeleteRowAuditLogEntry() {
    }

    public DeleteRowAuditLogEntry( final String userName ) {
        super( userName );
    }

    public DeleteRowAuditLogEntry( final String userName,
                                   final int rowIndex ) {
        super( userName );
        this.rowIndex = rowIndex;
    }

    @Override
    public String getGenericType() {
        return TYPE;
    }

    public int getRowIndex() {
        return this.rowIndex;
    }

}
