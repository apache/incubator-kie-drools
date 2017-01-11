/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.workbench.models.guided.dtable.backend;

import org.assertj.core.api.Assertions;
import org.drools.workbench.models.datamodel.oracle.DataType;
import org.drools.workbench.models.guided.dtable.backend.util.DataUtilities;
import org.drools.workbench.models.guided.dtable.shared.model.DTCellValue52;
import org.junit.Before;
import org.junit.Test;
import org.drools.workbench.models.guided.dtable.shared.model.ActionInsertFactCol52;
import org.drools.workbench.models.guided.dtable.shared.model.ActionSetFieldCol52;
import org.drools.workbench.models.guided.dtable.shared.model.AttributeCol52;
import org.drools.workbench.models.guided.dtable.shared.model.ConditionCol52;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.models.guided.dtable.shared.model.MetadataCol52;
import org.drools.workbench.models.guided.dtable.shared.model.Pattern52;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;

import static org.drools.workbench.models.guided.dtable.backend.TestUtil.loadResource;
import static org.junit.Assert.*;

public class GuidedDTXMLPersistenceTest {

    private DataUtilities upgrader = new DataUtilities();

    @Before
    public void setUp() throws Exception {
        GuidedDTXMLPersistence.getInstance();
    }

    @Test
    public void testRoundTrip() {

        GuidedDecisionTable52 dt = new GuidedDecisionTable52();

        dt.getActionCols().add( new ActionInsertFactCol52() );
        ActionSetFieldCol52 set = new ActionSetFieldCol52();
        set.setFactField( "foo" );
        dt.getActionCols().add( set );

        dt.getMetadataCols().add( new MetadataCol52() );

        dt.getAttributeCols().add( new AttributeCol52() );

        Pattern52 p = new Pattern52();
        ConditionCol52 c = new ConditionCol52();
        p.getChildColumns().add( c );
        dt.getConditions().add( p );

        dt.setData( upgrader.makeDataLists( new String[][]{ new String[]{ "1", "hola" } } ) );
        dt.setTableName( "blah" );

        String xml = GuidedDTXMLPersistence.getInstance().marshal( dt );
        System.out.println( xml );
        assertNotNull( xml );
        assertEquals( -1,
                      xml.indexOf( "ActionSetField" ) );
        assertEquals( -1,
                      xml.indexOf( "ConditionCol" ) );
        assertEquals( -1,
                      xml.indexOf( "GuidedDecisionTable" ) );

        GuidedDecisionTable52 dt_ = GuidedDTXMLPersistence.getInstance().unmarshal( xml );
        assertNotNull( dt_ );
        assertEquals( "blah",
                      dt_.getTableName() );
        assertEquals( 1,
                      dt_.getMetadataCols().size() );
        assertEquals( 1,
                      dt_.getAttributeCols().size() );
        assertEquals( 2,
                      dt_.getActionCols().size() );
        assertEquals( 1,
                      dt_.getConditions().size() );
        assertEquals( 1,
                      dt_.getConditions().get( 0 ).getChildColumns().size() );

    }

    @Test
    public void testBackwardsCompatibility() throws Exception {
        String xml = loadResource( "ExistingDecisionTable.xml" );
        GuidedDecisionTable52 dt_ = GuidedDTXMLPersistence.getInstance().unmarshal( xml );
        assertNotNull( dt_ );
        assertEquals( "blah",
                      dt_.getTableName() );
        assertEquals( 1,
                      dt_.getMetadataCols().size() );
        assertEquals( 1,
                      dt_.getAttributeCols().size() );
        assertEquals( 2,
                      dt_.getActionCols().size() );
        assertEquals( 1,
                      dt_.getConditions().size() );
        assertEquals( 1,
                      dt_.getConditions().get( 0 ).getChildColumns().size() );

        assertTrue( dt_.getActionCols().get( 1 ) instanceof ActionSetFieldCol52 );
        ActionSetFieldCol52 asf = (ActionSetFieldCol52) dt_.getActionCols().get( 1 );
        assertEquals( "foo",
                      asf.getFactField() );
        assertEquals( false,
                      asf.isUpdate() );
    }

    @Test
    public void testUnmarshallLegacyNumericType() {
        // NUMERIC type is used in version 5.x and it still needs to be supported (e.g. for jcr2vfs migration)
        String guidedDTableXml = "<decision-table52>\n" +
                "  <tableName>Some rules</tableName>\n" +
                "  <rowNumberCol>\n" +
                "    <hideColumn>false</hideColumn>\n" +
                "    <width>24</width>\n" +
                "  </rowNumberCol>\n" +
                "  <metadataCols/>\n" +
                "  <attributeCols/>\n" +
                "  <conditionPatterns/>\n" +
                "  <actionCols/>\n" +
                "  <data>\n" +
                "    <list>\n" +
                "      <value>\n" +
                "        <valueNumeric>1</valueNumeric>\n" +
                "        <dataType>NUMERIC</dataType>\n" + // this is the legacy type
                "        <isOtherwise>false</isOtherwise>\n" +
                "      </value>\n" +
                "    </list>\n" +
                "  </data>\n" +
                "</decision-table52>";
        GuidedDecisionTable52 guidedDTable = GuidedDTXMLPersistence.getInstance().unmarshal(guidedDTableXml);
        List<List<DTCellValue52>> dataList = guidedDTable.getData();
        Assertions.assertThat(dataList).hasSize(1);
        List<DTCellValue52> cellValueList = dataList.get(0);
        Assertions.assertThat(cellValueList).hasSize(1);
        // NUMERIC gets upgraded/migrated to NUMERIC_INTEGER
        Assertions.assertThat(cellValueList.get(0).getDataType()).isEqualTo(DataType.DataTypes.NUMERIC_INTEGER);
        Assertions.assertThat(cellValueList.get(0).getNumericValue().intValue()).isEqualTo(1);
    }
}
