/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.drools.workbench.models.guided.dtable.backend;

import java.util.List;

import org.drools.workbench.models.guided.dtable.backend.util.DataUtilities;
import org.drools.workbench.models.guided.dtable.shared.model.AttributeCol52;
import org.drools.workbench.models.guided.dtable.shared.model.DTCellValue52;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.models.guided.dtable.shared.model.MetadataCol52;
import org.junit.Before;
import org.junit.Test;

import static org.drools.workbench.models.guided.dtable.backend.TestUtil.*;

public class GuidedDTDRLPersistenceResolvedHitPolicyTest {


    private GuidedDecisionTable52 dtable;

    @Before
    public void setUp() throws
                        Exception {
        dtable = new GuidedDecisionTable52();
        dtable.setTableName( "Resolved hit policy table" );

        dtable.setHitPolicy( GuidedDecisionTable52.HitPolicy.RESOLVED_HIT );

        dtable.setData( DataUtilities.makeDataLists(
                new Object[][]{
                        new Object[]{1, "desc-row1"},
                        new Object[]{2, "desc-row2"},
                        new Object[]{3, "desc-row3"}
                } ) );

    }

    @Test(expected = IllegalArgumentException.class)
    public void blockUseOfSalience() {

        final AttributeCol52 attributeCol52 = new AttributeCol52();
        attributeCol52.setAttribute( "salience" );
        attributeCol52.setDefaultValue( new DTCellValue52( "123" ) );

        dtable.getAttributeCols()
                .add( attributeCol52 );

        GuidedDTDRLPersistence.getInstance()
                .marshal( dtable );
    }

    @Test(expected = IllegalArgumentException.class)
    public void blockUseOfActivationGroup() {

        final AttributeCol52 attributeCol52 = new AttributeCol52();
        attributeCol52.setAttribute( "activation-group" );
        attributeCol52.setDefaultValue( new DTCellValue52( "test" ) );

        dtable.getAttributeCols()
                .add( attributeCol52 );

        GuidedDTDRLPersistence.getInstance()
                .marshal( dtable );
    }

    @Test
    public void noPrioritiesSet() throws
                                  Exception {

        final String drl = GuidedDTDRLPersistence.getInstance()
                .marshal( dtable );

        assertContainsLinesInOrder( drl,
                                    "rule \"Row 1 Resolved hit policy table\"",
                                    "activation-group \"resolved-hit-policy-group Resolved hit policy table\"",
                                    "rule \"Row 2 Resolved hit policy table\"",
                                    "activation-group \"resolved-hit-policy-group Resolved hit policy table\"",
                                    "rule \"Row 3 Resolved hit policy table\"",
                                    "activation-group \"resolved-hit-policy-group Resolved hit policy table\"" );
    }

    @Test
    public void prioritiesSet() throws
                                Exception {

        final MetadataCol52 metadataCol = new MetadataCol52();
        metadataCol.setMetadata( GuidedDecisionTable52.HitPolicy.RESOLVED_HIT_METADATA_NAME );
        dtable.getMetadataCols()
                .add( metadataCol );

        int indexOf = dtable.getExpandedColumns()
                .indexOf( metadataCol );

        int index = 0;
        for ( final List<DTCellValue52> row : dtable.getData() ) {
            final DTCellValue52 element = new DTCellValue52();
            element.setStringValue( Integer.toString( index++ ) );
            row.add( indexOf,
                     element );
        }

        final String drl = GuidedDTDRLPersistence.getInstance()
                .marshal( dtable );

        assertContainsLinesInOrder( drl,
                                    "rule \"Row 1 Resolved hit policy table\"",
                                    "activation-group \"resolved-hit-policy-group Resolved hit policy table\"",
                                    "salience 0",
                                    "rule \"Row 2 Resolved hit policy table\"",
                                    "activation-group \"resolved-hit-policy-group Resolved hit policy table\"",
                                    "salience 1",
                                    "rule \"Row 3 Resolved hit policy table\"",
                                    "activation-group \"resolved-hit-policy-group Resolved hit policy table\"",
                                    "salience 2" );
    }
}
