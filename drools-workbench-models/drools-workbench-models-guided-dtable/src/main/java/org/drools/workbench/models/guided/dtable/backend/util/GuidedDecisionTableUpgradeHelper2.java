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
import org.drools.workbench.models.guided.dtable.shared.model.AttributeCol52;
import org.drools.workbench.models.guided.dtable.shared.model.BaseColumn;
import org.drools.workbench.models.guided.dtable.shared.model.DTCellValue52;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;

import java.util.List;

/**
 * Helper class to upgrade data-types for Guided Decision Table. This
 * implementation ensures the RowNumber, Salience and Duration columns have data
 * of the correct data-type. Support for this was added for Guvnor v5.4.
 */
public class GuidedDecisionTableUpgradeHelper2
        implements
        IUpgradeHelper<GuidedDecisionTable52, GuidedDecisionTable52> {

    /**
     * Convert the data-types in the Decision Table model
     * @param source
     * @return The new DTModel
     */
    public GuidedDecisionTable52 upgrade( GuidedDecisionTable52 source ) {

        final GuidedDecisionTable52 destination = source;

        //These are the columns we want to update
        final int iRowNumberColumnIndex = 0;
        Integer iSalienceColumnIndex = null;
        Integer iDurationColumnIndex = null;

        //Find the Salience and Duration column indexes
        List<BaseColumn> allColumns = destination.getExpandedColumns();
        for ( int iCol = 0; iCol < allColumns.size(); iCol++ ) {
            final BaseColumn column = allColumns.get( iCol );
            if ( column instanceof AttributeCol52 ) {
                AttributeCol52 attributeCol = (AttributeCol52) column;
                final String attributeName = attributeCol.getAttribute();
                if ( GuidedDecisionTable52.SALIENCE_ATTR.equals( attributeName ) ) {
                    iSalienceColumnIndex = iCol;
                } else if ( GuidedDecisionTable52.DURATION_ATTR.equals( attributeName ) ) {
                    iDurationColumnIndex = iCol;
                }
            }
        }

        //Update data-types
        for ( List<DTCellValue52> row : destination.getData() ) {

            //Row numbers are Integers
            final int rowNumberValue = row.get( iRowNumberColumnIndex ).getNumericValue().intValue();
            row.get( iRowNumberColumnIndex ).setNumericValue( rowNumberValue );

            //Salience should be an Integer
            if ( iSalienceColumnIndex != null ) {
                final Number salienceValue = row.get( iSalienceColumnIndex ).getNumericValue();
                if ( salienceValue == null ) {
                    row.get( iSalienceColumnIndex ).setNumericValue( (Integer) null );
                } else {
                    row.get( iSalienceColumnIndex ).setNumericValue( salienceValue.intValue() );
                }
            }

            //Duration should be a Long
            if ( iDurationColumnIndex != null ) {
                final Number durationValue = row.get( iDurationColumnIndex ).getNumericValue();
                if ( durationValue == null ) {
                    row.get( iDurationColumnIndex ).setNumericValue( (Long) null );
                } else {
                    row.get( iDurationColumnIndex ).setNumericValue( durationValue.longValue() );
                }
            }
        }

        return destination;
    }

}
