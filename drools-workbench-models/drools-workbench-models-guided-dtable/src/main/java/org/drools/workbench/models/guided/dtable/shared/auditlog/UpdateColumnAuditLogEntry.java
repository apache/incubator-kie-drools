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

import java.util.List;

import org.drools.workbench.models.guided.dtable.shared.model.BaseColumn;
import org.drools.workbench.models.guided.dtable.shared.model.BaseColumnFieldDiff;

/**
 * An Audit Event when a column is updated
 */
public class UpdateColumnAuditLogEntry extends InsertColumnAuditLogEntry {

    private static final long serialVersionUID = -6953659333450748813L;

    private static final String TYPE = DecisionTableAuditEvents.UPDATE_COLUMN.name();

    private ColumnDetails originalDetails;

    private List<BaseColumnFieldDiff> diffs;

    public UpdateColumnAuditLogEntry() {
    }

    public UpdateColumnAuditLogEntry( final String userName ) {
        super( userName );
    }

    public UpdateColumnAuditLogEntry( final String userName,
                                      final BaseColumn originalColumn,
                                      final BaseColumn newColumn ) {
        super( userName,
               newColumn );
        this.originalDetails = getDetails( originalColumn );
        this.diffs = null;
    }

    public UpdateColumnAuditLogEntry( final String userName,
                                      final BaseColumn originalColumn,
                                      final BaseColumn newColumn,
                                      final List<BaseColumnFieldDiff> diffs ) {
        super( userName,
               newColumn );
        this.originalDetails = getDetails( originalColumn );
        this.diffs = diffs;
    }

    @Override
    public String getGenericType() {
        return TYPE;
    }

    public ColumnDetails getOriginalDetails() {
        return originalDetails;
    }

    public List<BaseColumnFieldDiff> getDiffs() {
        return diffs;
    }
}
