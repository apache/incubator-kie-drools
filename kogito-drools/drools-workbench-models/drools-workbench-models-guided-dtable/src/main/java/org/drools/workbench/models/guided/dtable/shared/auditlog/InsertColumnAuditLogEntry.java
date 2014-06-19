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
package org.drools.workbench.models.guided.dtable.shared.auditlog;

import org.drools.workbench.models.datamodel.auditlog.AuditLogEntry;
import org.drools.workbench.models.guided.dtable.shared.model.ActionInsertFactCol52;
import org.drools.workbench.models.guided.dtable.shared.model.ActionSetFieldCol52;
import org.drools.workbench.models.guided.dtable.shared.model.ActionWorkItemCol52;
import org.drools.workbench.models.guided.dtable.shared.model.ActionWorkItemInsertFactCol52;
import org.drools.workbench.models.guided.dtable.shared.model.ActionWorkItemSetFieldCol52;
import org.drools.workbench.models.guided.dtable.shared.model.AttributeCol52;
import org.drools.workbench.models.guided.dtable.shared.model.BRLActionColumn;
import org.drools.workbench.models.guided.dtable.shared.model.BRLConditionColumn;
import org.drools.workbench.models.guided.dtable.shared.model.BaseColumn;
import org.drools.workbench.models.guided.dtable.shared.model.ConditionCol52;
import org.drools.workbench.models.guided.dtable.shared.model.LimitedEntryActionInsertFactCol52;
import org.drools.workbench.models.guided.dtable.shared.model.LimitedEntryActionSetFieldCol52;
import org.drools.workbench.models.guided.dtable.shared.model.LimitedEntryConditionCol52;
import org.drools.workbench.models.guided.dtable.shared.model.MetadataCol52;

/**
 * An Audit Event for when a column is inserted
 */
public class InsertColumnAuditLogEntry extends AuditLogEntry {

    private static final long serialVersionUID = -7525789306354393393L;

    private static final String TYPE = DecisionTableAuditEvents.INSERT_COLUMN.name();

    private ColumnDetails details;

    public InsertColumnAuditLogEntry() {
    }

    public InsertColumnAuditLogEntry( final String userName ) {
        super( userName );
    }

    public InsertColumnAuditLogEntry( final String userName,
                                      final BaseColumn column ) {
        super( userName );
        this.details = getDetails( column );
    }

    protected ColumnDetails getDetails( final BaseColumn column ) {
        //Order of columns is important to account for sub-types and super-types
        if ( column instanceof MetadataCol52 ) {
            return new MetadataColumnDetails( (MetadataCol52) column );
        } else if ( column instanceof AttributeCol52 ) {
            return new AttributeColumnDetails( (AttributeCol52) column );
        } else if ( column instanceof BRLConditionColumn ) {
            return new ColumnDetails( column );
        } else if ( column instanceof ConditionCol52 ) {
            return new ConditionColumnDetails( (ConditionCol52) column );
        } else if ( column instanceof LimitedEntryConditionCol52 ) {
            return new LimitedEntryConditionColumnDetails( (LimitedEntryConditionCol52) column );
        } else if ( column instanceof BRLActionColumn ) {
            return new ColumnDetails( column );
        } else if ( column instanceof ActionWorkItemInsertFactCol52 ) {
            return new ActionWorkItemInsertFactColumnDetails( (ActionWorkItemInsertFactCol52) column );
        } else if ( column instanceof ActionInsertFactCol52 ) {
            return new ActionInsertFactColumnDetails( (ActionInsertFactCol52) column );
        } else if ( column instanceof LimitedEntryActionInsertFactCol52 ) {
            return new LimitedEntryActionInsertFactColumnDetails( (LimitedEntryActionInsertFactCol52) column );
        } else if ( column instanceof ActionWorkItemSetFieldCol52 ) {
            return new ActionWorkItemSetFieldColumnDetails( (ActionWorkItemSetFieldCol52) column );
        } else if ( column instanceof ActionSetFieldCol52 ) {
            return new ActionSetFieldColumnDetails( (ActionSetFieldCol52) column );
        } else if ( column instanceof LimitedEntryActionSetFieldCol52 ) {
            return new LimitedEntryActionSetFieldColumnDetails( (LimitedEntryActionSetFieldCol52) column );
        } else if ( column instanceof ActionWorkItemCol52 ) {
            return new ActionWorkItemColumnDetails( (ActionWorkItemCol52) column );
        } else {
            return new ColumnDetails( column );
        }
    }

    @Override
    public String getGenericType() {
        return TYPE;
    }

    public ColumnDetails getDetails() {
        return details;
    }

}
