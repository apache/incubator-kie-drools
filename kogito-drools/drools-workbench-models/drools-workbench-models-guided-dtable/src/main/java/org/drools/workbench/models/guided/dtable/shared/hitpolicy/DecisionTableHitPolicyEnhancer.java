/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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
package org.drools.workbench.models.guided.dtable.shared.hitpolicy;

import java.util.List;

import org.drools.workbench.models.guided.dtable.shared.model.AttributeCol52;
import org.drools.workbench.models.guided.dtable.shared.model.DTCellValue52;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.models.guided.dtable.shared.model.MetadataCol52;
import org.drools.workbench.models.guided.dtable.shared.validation.DecisionTableValidator;

/**
 * Validates and extends the model based on selected HitPolicy
 * @see org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52.HitPolicy
 */
public class DecisionTableHitPolicyEnhancer {

    private final GuidedDecisionTable52 dtable;

    private DecisionTableHitPolicyEnhancer( final GuidedDecisionTable52 originalDTable ) {
        this.dtable = originalDTable;
    }

    public static GuidedDecisionTable52 enhance( final GuidedDecisionTable52 originalDTable ) {
        return new DecisionTableHitPolicyEnhancer( originalDTable ).enhance();
    }

    private GuidedDecisionTable52 enhance() {
        new DecisionTableValidator( dtable ).validate();

        switch ( dtable.getHitPolicy() ) {

            case RESOLVED_HIT:
                addActivationGroup( "resolved-hit-policy-group" );
                addSalienceByMetadata();
                break;
            case UNIQUE_HIT:
                addActivationGroup( "unique-hit-policy-group" );
                break;
            case FIRST_HIT:
                addActivationGroup( "first-hit-policy-group" );
                break;
            case RULE_ORDER:
            case NONE:
            default:
                // We do nothing.
                break;

        }
        return dtable;
    }

    private void addSalienceByMetadata() {

        final int priorityColumnIndex = getResolvedHitMetadataColumnIndex();

        if ( priorityColumnIndex >= 0 ) {
            final RowPriorityResolver rowPriorityResolver = getRowPriorityResolver( priorityColumnIndex );
            final int indexOfSalienceColumn = makeSalienceColumn();
            final RowPriorities priorities = rowPriorityResolver.getPriorityRelations();

            for ( final RowNumber rowNumber : priorities.getRowNumbers() ) {
                dtable.getData()
                        .get( rowNumber.getRowIndex() )
                        .get( indexOfSalienceColumn )
                        .setNumericValue( priorities.getSalience( rowNumber )
                                                  .getSalience() );
            }
        }
    }

    private RowPriorityResolver getRowPriorityResolver( final int priorityColumnIndex ) {

        final int rowNumberIndex = getRowNumberColumnIndex();

        final RowPriorityResolver rowPriorityResolver = new RowPriorityResolver();

        for ( final List<DTCellValue52> row : dtable.getData() ) {

            final int rowNumber = row.get( rowNumberIndex )
                    .getNumericValue()
                    .intValue();

            final int priorityOver = getPriorityOver( priorityColumnIndex,
                                                      row );

            rowPriorityResolver.set( rowNumber,
                                     priorityOver );
        }

        return rowPriorityResolver;
    }

    private int makeSalienceColumn() {
        final AttributeCol52 attributeCol = new AttributeCol52();
        attributeCol.setAttribute( "salience" );
        dtable.getAttributeCols()
                .add( attributeCol );

        int columnIndex = dtable.getExpandedColumns()
                .indexOf( attributeCol );

        for ( final List<DTCellValue52> row : dtable.getData() ) {
            row.add( columnIndex,
                     new DTCellValue52( 0 ) );
        }

        return columnIndex;
    }

    private int getPriorityOver( final int priorityColumnIndex,
                                 final List<DTCellValue52> row ) {
        final String stringValue = row.get( priorityColumnIndex )
                .getStringValue();
        if ( stringValue == null || stringValue.trim()
                .equals( "" ) ) {
            return 0;
        } else {
            return Integer.parseInt( stringValue );
        }
    }

    private int getResolvedHitMetadataColumnIndex() {
        int columnIndex = -1;
        for ( final MetadataCol52 metadataCol52 : dtable.getMetadataCols() ) {
            if ( GuidedDecisionTable52.HitPolicy.RESOLVED_HIT_METADATA_NAME.equals( metadataCol52.getMetadata() ) ) {
                columnIndex = dtable.getExpandedColumns()
                        .indexOf( metadataCol52 );
            }
        }
        return columnIndex;
    }

    private int getRowNumberColumnIndex() {
        return dtable.getExpandedColumns()
                .indexOf( dtable.getRowNumberCol() );
    }

    private void addActivationGroup( final String activationGroupType ) {
        final AttributeCol52 attributeCol52 = new AttributeCol52();
        attributeCol52.setAttribute( GuidedDecisionTable52.ACTIVATION_GROUP_ATTR );
        dtable.getAttributeCols()
                .add( attributeCol52 );

        final int columnIndex = dtable.getExpandedColumns()
                .indexOf( attributeCol52 );

        for ( final List<DTCellValue52> row : dtable.getData() ) {
            row.add( columnIndex,
                     new DTCellValue52( activationGroupType + " " + dtable.getTableName() ) );
        }
    }
}
