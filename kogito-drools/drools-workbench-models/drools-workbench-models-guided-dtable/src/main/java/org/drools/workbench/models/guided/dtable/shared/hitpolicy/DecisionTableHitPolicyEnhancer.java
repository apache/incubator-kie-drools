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

            case UNIQUE_HIT:
                addActivationGroup( "unique-hit-policy-group" );
                break;
            case FIRST_HIT:
                addSalienceBasedOnRowOrder();
                addActivationGroup( "first-hit-policy-group" );
                break;
            case RULE_ORDER:
                addSalienceBasedOnRowOrder();
                break;
            case NONE:
            default:
                // We do nothing.
                break;

        }

        return dtable;
    }

    private void addSalienceBasedOnRowOrder() {
        // Add Column
        final AttributeCol52 attributeCol52 = new AttributeCol52();
        attributeCol52.setAttribute( GuidedDecisionTable52.SALIENCE_ATTR );
        dtable.getAttributeCols()
                .add( attributeCol52 );

        final int columnIndex = dtable.getExpandedColumns()
                .indexOf( attributeCol52 );

        int salience = 0;

        final int size = dtable.getData()
                .size();

        // Add salience values
        for ( int i = ( size - 1 ); i >= 0; i-- ) {
            dtable.getData()
                    .get( i )
                    .add( columnIndex,
                          new DTCellValue52( salience++ ) );
        }
    }

    private void addActivationGroup( final String activationGroupType ) {
        // Add Column
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
