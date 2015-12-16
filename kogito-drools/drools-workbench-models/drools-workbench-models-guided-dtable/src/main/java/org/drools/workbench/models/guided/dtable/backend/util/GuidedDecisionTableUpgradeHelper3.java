/*
 * Copyright 2011 Red Hat, Inc. and/or its affiliates.
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
package org.drools.workbench.models.guided.dtable.backend.util;

import org.drools.workbench.models.commons.backend.IUpgradeHelper;
import org.drools.workbench.models.guided.dtable.shared.model.ActionCol52;
import org.drools.workbench.models.guided.dtable.shared.model.AttributeCol52;
import org.drools.workbench.models.guided.dtable.shared.model.BRLVariableColumn;
import org.drools.workbench.models.guided.dtable.shared.model.BaseColumn;
import org.drools.workbench.models.guided.dtable.shared.model.ConditionCol52;
import org.drools.workbench.models.guided.dtable.shared.model.DTCellValue52;
import org.drools.workbench.models.guided.dtable.shared.model.DTColumnConfig52;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.models.guided.dtable.shared.model.LimitedEntryCol;
import org.drools.workbench.models.guided.dtable.shared.model.MetadataCol52;

/**
 * Helper class to upgrade Default Values to DTCellValue52 objects instead of
 * Strings. Support for this was added for Guvnor v5.4.
 */
public class GuidedDecisionTableUpgradeHelper3
        implements
        IUpgradeHelper<GuidedDecisionTable52, GuidedDecisionTable52> {

    /**
     * Convert the Default Values in the Decision Table model
     * @param source
     * @return The new DTModel
     */
    public GuidedDecisionTable52 upgrade( GuidedDecisionTable52 source ) {

        final GuidedDecisionTable52 destination = source;

        for ( BaseColumn column : source.getExpandedColumns() ) {
            DTColumnConfig52 dtColumn = null;
            if ( column instanceof MetadataCol52 ) {
                dtColumn = (DTColumnConfig52) column;
            } else if ( column instanceof AttributeCol52 ) {
                dtColumn = (DTColumnConfig52) column;
            } else if ( column instanceof ConditionCol52 ) {
                dtColumn = (DTColumnConfig52) column;
            } else if ( column instanceof ActionCol52 ) {
                dtColumn = (DTColumnConfig52) column;
            }
            if ( dtColumn instanceof LimitedEntryCol ) {
                dtColumn = null;
            }
            if ( dtColumn instanceof BRLVariableColumn ) {
                dtColumn = null;
            }
            if ( dtColumn != null ) {
                final String legacyDefaultValue = dtColumn.defaultValue;
                if ( legacyDefaultValue != null ) {
                    dtColumn.setDefaultValue( new DTCellValue52( legacyDefaultValue ) );
                    dtColumn.defaultValue = null;
                }
            }
        }

        return destination;
    }
}
