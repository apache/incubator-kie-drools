/*
 * Copyright 2012 JBoss Inc
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
package org.drools.guvnor.models.guided.dtable.shared.auditlog;

import org.drools.guvnor.models.commons.shared.auditlog.DefaultAuditLogEntry;

/**
 * An Audit Event when a row is inserted
 */
public class InsertRowAuditLogEntry extends DefaultAuditLogEntry {

    private static final long serialVersionUID = 8049692773593046770L;

    private static final String TYPE = DecisionTableAuditEvents.INSERT_ROW.name();

    public int rowIndex;

    public InsertRowAuditLogEntry() {
    }

    public InsertRowAuditLogEntry( final String userName ) {
        super( userName );
    }

    public InsertRowAuditLogEntry( final String userName,
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
