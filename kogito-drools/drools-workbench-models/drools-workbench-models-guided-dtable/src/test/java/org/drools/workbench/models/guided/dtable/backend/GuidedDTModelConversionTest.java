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
package org.drools.workbench.models.guided.dtable.backend;

import org.drools.workbench.models.datamodel.oracle.DataType;
import org.drools.workbench.models.guided.dtable.backend.util.GuidedDecisionTableUpgradeHelper1;
import org.junit.Test;
import org.drools.workbench.models.datamodel.oracle.DataType;
import org.drools.workbench.models.guided.dtable.shared.model.ActionInsertFactCol52;
import org.drools.workbench.models.guided.dtable.shared.model.ActionRetractFactCol52;
import org.drools.workbench.models.guided.dtable.shared.model.ActionSetFieldCol52;
import org.drools.workbench.models.guided.dtable.shared.model.DTCellValue52;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.models.guided.dtable.shared.model.legacy.ActionInsertFactCol;
import org.drools.workbench.models.guided.dtable.shared.model.legacy.ActionRetractFactCol;
import org.drools.workbench.models.guided.dtable.shared.model.legacy.ActionSetFieldCol;
import org.drools.workbench.models.guided.dtable.shared.model.legacy.AttributeCol;
import org.drools.workbench.models.guided.dtable.shared.model.legacy.ConditionCol;
import org.drools.workbench.models.guided.dtable.shared.model.legacy.GuidedDecisionTable;
import org.drools.workbench.models.guided.dtable.shared.model.legacy.MetadataCol;
import org.drools.workbench.models.datamodel.rule.BaseSingleFieldConstraint;

import java.util.List;

import static org.junit.Assert.*;

@SuppressWarnings("deprecation")
public class GuidedDTModelConversionTest {

    private GuidedDecisionTableUpgradeHelper1 upgrader = new GuidedDecisionTableUpgradeHelper1();

    @Test
    public void testConversion() {

        GuidedDecisionTable dt = new GuidedDecisionTable();
        dt.tableName = "michael";

        MetadataCol md = new MetadataCol();
        md.attr = "legacy";
        md.defaultValue = "yes";
        dt.metadataCols.add( md );

        AttributeCol attr = new AttributeCol();
        attr.attr = "salience";
        attr.defaultValue = "66";
        dt.attributeCols.add( attr );

        ConditionCol con = new ConditionCol();
        con.boundName = "f1";
        con.constraintValueType = BaseSingleFieldConstraint.TYPE_LITERAL;
        con.factField = "age";
        con.factType = "Driver";
        con.header = "Driver f1 age";
        con.operator = "==";
        dt.conditionCols.add( con );

        ConditionCol con2 = new ConditionCol();
        con2.boundName = "f1";
        con2.constraintValueType = BaseSingleFieldConstraint.TYPE_LITERAL;
        con2.factField = "name";
        con2.factType = "Driver";
        con2.header = "Driver f1 name";
        con2.operator = "==";
        dt.conditionCols.add( con2 );

        ConditionCol con3 = new ConditionCol();
        con3.boundName = "f1";
        con3.constraintValueType = BaseSingleFieldConstraint.TYPE_RET_VALUE;
        con3.factField = "rating";
        con3.factType = "Driver";
        con3.header = "Driver rating";
        con3.operator = "==";
        dt.conditionCols.add( con3 );

        ConditionCol con4 = new ConditionCol();
        con4.boundName = "f2";
        con4.constraintValueType = BaseSingleFieldConstraint.TYPE_PREDICATE;
        con4.factType = "Driver";
        con4.header = "Driver 2 pimp";
        con4.factField = "(not needed)";
        dt.conditionCols.add( con4 );

        ActionInsertFactCol ins = new ActionInsertFactCol();
        ins.boundName = "ins";
        ins.factType = "Cheese";
        ins.factField = "price";
        ins.type = DataType.TYPE_NUMERIC_INTEGER;
        dt.actionCols.add( ins );

        ActionRetractFactCol ret = new ActionRetractFactCol();
        ret.boundName = "ret1";
        dt.actionCols.add( ret );

        ActionSetFieldCol set = new ActionSetFieldCol();
        set.boundName = "f1";
        set.factField = "goo1";
        set.type = DataType.TYPE_STRING;
        dt.actionCols.add( set );

        ActionSetFieldCol set2 = new ActionSetFieldCol();
        set2.boundName = "f1";
        set2.factField = "goo2";
        set2.defaultValue = "whee";
        set2.type = DataType.TYPE_STRING;
        dt.actionCols.add( set2 );

        dt.data = new String[][]{
                new String[]{ "1", "desc", "metar1", "saliencer1", "c1r1", "c2r1", "c3r1", "c4r1", "a1r1", "a2r1", "a3r1", "a4r1" },
                new String[]{ "2", "desc", "metar2", "saliencer2", "c1r2", "c2r2", "c3r2", "c4r2", "a1r2", "a2r2", "a3r2", "a4r2" }
        };

        String[][] expected = new String[][]{
                new String[]{ "1", "desc", "metar1", "saliencer1", "c1r1", "c2r1", "c3r1", "c4r1", "a1r1", "ret1", "a3r1", "a4r1" },
                new String[]{ "2", "desc", "metar2", "saliencer2", "c1r2", "c2r2", "c3r2", "c4r2", "a1r2", "ret1", "a3r2", "a4r2" }
        };

        GuidedDecisionTable52 tsdt = upgrader.upgrade( dt );

        assertEquals( "michael",
                      tsdt.getTableName() );

        assertEquals( GuidedDecisionTable52.TableFormat.EXTENDED_ENTRY,
                      tsdt.getTableFormat() );

        assertEquals( 1,
                      tsdt.getMetadataCols().size() );
        assertEquals( "legacy",
                      tsdt.getMetadataCols().get( 0 ).getMetadata() );
        assertEquals( "yes",
                      tsdt.getMetadataCols().get( 0 ).getDefaultValue().getStringValue() );

        assertEquals( 1,
                      tsdt.getAttributeCols().size() );
        assertEquals( "salience",
                      tsdt.getAttributeCols().get( 0 ).getAttribute() );
        assertEquals( "66",
                      tsdt.getAttributeCols().get( 0 ).getDefaultValue().getStringValue() );

        assertEquals( 2,
                      tsdt.getConditions().size() );

        assertEquals( "f1",
                      tsdt.getConditionPattern( "f1" ).getBoundName() );
        assertEquals( "Driver",
                      tsdt.getConditionPattern( "f1" ).getFactType() );

        assertEquals( "f2",
                      tsdt.getConditionPattern( "f2" ).getBoundName() );
        assertEquals( "Driver",
                      tsdt.getConditionPattern( "f2" ).getFactType() );

        assertEquals( 3,
                      tsdt.getConditionPattern( "f1" ).getChildColumns().size() );

        assertEquals( 1,
                      tsdt.getConditionPattern( "f2" ).getChildColumns().size() );

        assertEquals( BaseSingleFieldConstraint.TYPE_LITERAL,
                      tsdt.getConditionPattern( "f1" ).getChildColumns().get( 0 ).getConstraintValueType() );
        assertEquals( "age",
                      tsdt.getConditionPattern( "f1" ).getChildColumns().get( 0 ).getFactField() );
        assertEquals( "Driver",
                      tsdt.getPattern( tsdt.getConditionPattern( "f1" ).getChildColumns().get( 0 ) ).getFactType() );
        assertEquals( "Driver f1 age",
                      tsdt.getConditionPattern( "f1" ).getChildColumns().get( 0 ).getHeader() );
        assertEquals( "==",
                      tsdt.getConditionPattern( "f1" ).getChildColumns().get( 0 ).getOperator() );

        assertEquals( BaseSingleFieldConstraint.TYPE_LITERAL,
                      tsdt.getConditionPattern( "f1" ).getChildColumns().get( 1 ).getConstraintValueType() );
        assertEquals( "name",
                      tsdt.getConditionPattern( "f1" ).getChildColumns().get( 1 ).getFactField() );
        assertEquals( "Driver",
                      tsdt.getPattern( tsdt.getConditionPattern( "f1" ).getChildColumns().get( 1 ) ).getFactType() );
        assertEquals( "Driver f1 name",
                      tsdt.getConditionPattern( "f1" ).getChildColumns().get( 1 ).getHeader() );
        assertEquals( "==",
                      tsdt.getConditionPattern( "f1" ).getChildColumns().get( 1 ).getOperator() );

        assertEquals( BaseSingleFieldConstraint.TYPE_RET_VALUE,
                      tsdt.getConditionPattern( "f1" ).getChildColumns().get( 2 ).getConstraintValueType() );
        assertEquals( "rating",
                      tsdt.getConditionPattern( "f1" ).getChildColumns().get( 2 ).getFactField() );
        assertEquals( "Driver",
                      tsdt.getPattern( tsdt.getConditionPattern( "f1" ).getChildColumns().get( 2 ) ).getFactType() );
        assertEquals( "Driver rating",
                      tsdt.getConditionPattern( "f1" ).getChildColumns().get( 2 ).getHeader() );
        assertEquals( "==",
                      tsdt.getConditionPattern( "f1" ).getChildColumns().get( 2 ).getOperator() );

        assertEquals( BaseSingleFieldConstraint.TYPE_PREDICATE,
                      tsdt.getConditionPattern( "f2" ).getChildColumns().get( 0 ).getConstraintValueType() );
        assertEquals( "(not needed)",
                      tsdt.getConditionPattern( "f2" ).getChildColumns().get( 0 ).getFactField() );
        assertEquals( "Driver",
                      tsdt.getPattern( tsdt.getConditionPattern( "f2" ).getChildColumns().get( 0 ) ).getFactType() );
        assertEquals( "Driver 2 pimp",
                      tsdt.getConditionPattern( "f2" ).getChildColumns().get( 0 ).getHeader() );

        assertEquals( 4,
                      tsdt.getActionCols().size() );

        ActionInsertFactCol52 a1 = (ActionInsertFactCol52) tsdt.getActionCols().get( 0 );
        assertEquals( "ins",
                      a1.getBoundName() );
        assertEquals( "Cheese",
                      a1.getFactType() );
        assertEquals( "price",
                      a1.getFactField() );
        assertEquals( DataType.TYPE_NUMERIC_INTEGER,
                      a1.getType() );

        ActionRetractFactCol52 a2 = (ActionRetractFactCol52) tsdt.getActionCols().get( 1 );
        assertNotNull( a2 );

        ActionSetFieldCol52 a3 = (ActionSetFieldCol52) tsdt.getActionCols().get( 2 );
        assertEquals( "f1",
                      a3.getBoundName() );
        assertEquals( "goo1",
                      a3.getFactField() );
        assertEquals( DataType.TYPE_STRING,
                      a3.getType() );

        ActionSetFieldCol52 a4 = (ActionSetFieldCol52) tsdt.getActionCols().get( 3 );
        assertEquals( "f1",
                      a4.getBoundName() );
        assertEquals( "goo2",
                      a4.getFactField() );
        assertEquals( "whee",
                      a4.getDefaultValue().getStringValue() );
        assertEquals( DataType.TYPE_STRING,
                      a4.getType() );

        assertEquals( 2,
                      tsdt.getData().size() );
        isRowEquivalent( tsdt.getData().get( 0 ),
                         expected[ 0 ] );
        isRowEquivalent( tsdt.getData().get( 1 ),
                         expected[ 1 ] );

    }

    @Test
    public void testConversionPatternGrouping() {

        GuidedDecisionTable dt = new GuidedDecisionTable();
        dt.tableName = "michael";

        MetadataCol md = new MetadataCol();
        md.attr = "legacy";
        md.defaultValue = "yes";
        dt.metadataCols.add( md );

        AttributeCol attr = new AttributeCol();
        attr.attr = "salience";
        attr.defaultValue = "66";
        dt.attributeCols.add( attr );

        ConditionCol con = new ConditionCol();
        con.boundName = "f1";
        con.constraintValueType = BaseSingleFieldConstraint.TYPE_LITERAL;
        con.factField = "age";
        con.factType = "Driver";
        con.header = "Driver f1 age";
        con.operator = "==";
        dt.conditionCols.add( con );

        ConditionCol con2 = new ConditionCol();
        con2.boundName = "f2";
        con2.constraintValueType = BaseSingleFieldConstraint.TYPE_LITERAL;
        con2.factField = "name";
        con2.factType = "Person";
        con2.header = "Person f2 name";
        con2.operator = "==";
        dt.conditionCols.add( con2 );

        ConditionCol con3 = new ConditionCol();
        con3.boundName = "f1";
        con3.constraintValueType = BaseSingleFieldConstraint.TYPE_RET_VALUE;
        con3.factField = "rating";
        con3.factType = "Driver";
        con3.header = "Driver rating";
        con3.operator = "==";
        dt.conditionCols.add( con3 );

        ConditionCol con4 = new ConditionCol();
        con4.boundName = "f2";
        con4.constraintValueType = BaseSingleFieldConstraint.TYPE_PREDICATE;
        con4.factType = "Person";
        con4.header = "Person f2 not needed";
        con4.factField = "(not needed)";
        dt.conditionCols.add( con4 );

        ActionInsertFactCol ins = new ActionInsertFactCol();
        ins.boundName = "ins";
        ins.factType = "Cheese";
        ins.factField = "price";
        ins.type = DataType.TYPE_NUMERIC_INTEGER;
        dt.actionCols.add( ins );

        ActionRetractFactCol ret = new ActionRetractFactCol();
        ret.boundName = "ret1";
        dt.actionCols.add( ret );

        ActionSetFieldCol set = new ActionSetFieldCol();
        set.boundName = "f1";
        set.factField = "goo1";
        set.type = DataType.TYPE_STRING;
        dt.actionCols.add( set );

        ActionSetFieldCol set2 = new ActionSetFieldCol();
        set2.boundName = "f1";
        set2.factField = "goo2";
        set2.defaultValue = "whee";
        set2.type = DataType.TYPE_STRING;
        dt.actionCols.add( set2 );

        dt.data = new String[][]{
                new String[]{ "1", "desc", "metar1", "saliencer1", "f1c1r1", "f2c1r1", "f1c2r1", "f2c2r1", "a1r1", "a2r1", "a3r1", "a4r1" },
                new String[]{ "2", "desc", "metar2", "saliencer2", "f1c1r2", "f2c1r2", "f1c2r2", "f2c2r2", "a1r2", "a2r2", "a3r2", "a4r2" }
        };

        String[][] expected = new String[][]{
                new String[]{ "1", "desc", "metar1", "saliencer1", "f1c1r1", "f1c2r1", "f2c1r1", "f2c2r1", "a1r1", "ret1", "a3r1", "a4r1" },
                new String[]{ "2", "desc", "metar2", "saliencer2", "f1c1r2", "f1c2r2", "f2c1r2", "f2c2r2", "a1r2", "ret1", "a3r2", "a4r2" }
        };

        GuidedDecisionTable52 tsdt = upgrader.upgrade( dt );

        assertEquals( "michael",
                      tsdt.getTableName() );

        assertEquals( 1,
                      tsdt.getMetadataCols().size() );
        assertEquals( "legacy",
                      tsdt.getMetadataCols().get( 0 ).getMetadata() );
        assertEquals( "yes",
                      tsdt.getMetadataCols().get( 0 ).getDefaultValue().getStringValue() );

        assertEquals( 1,
                      tsdt.getAttributeCols().size() );
        assertEquals( "salience",
                      tsdt.getAttributeCols().get( 0 ).getAttribute() );
        assertEquals( "66",
                      tsdt.getAttributeCols().get( 0 ).getDefaultValue().getStringValue() );

        assertEquals( 2,
                      tsdt.getConditions().size() );

        assertEquals( "f1",
                      tsdt.getConditionPattern( "f1" ).getBoundName() );
        assertEquals( "Driver",
                      tsdt.getConditionPattern( "f1" ).getFactType() );

        assertEquals( "f2",
                      tsdt.getConditionPattern( "f2" ).getBoundName() );
        assertEquals( "Person",
                      tsdt.getConditionPattern( "f2" ).getFactType() );

        assertEquals( 2,
                      tsdt.getConditionPattern( "f1" ).getChildColumns().size() );

        assertEquals( 2,
                      tsdt.getConditionPattern( "f2" ).getChildColumns().size() );

        assertEquals( BaseSingleFieldConstraint.TYPE_LITERAL,
                      tsdt.getConditionPattern( "f1" ).getChildColumns().get( 0 ).getConstraintValueType() );
        assertEquals( "age",
                      tsdt.getConditionPattern( "f1" ).getChildColumns().get( 0 ).getFactField() );
        assertEquals( "Driver",
                      tsdt.getPattern( tsdt.getConditionPattern( "f1" ).getChildColumns().get( 0 ) ).getFactType() );
        assertEquals( "Driver f1 age",
                      tsdt.getConditionPattern( "f1" ).getChildColumns().get( 0 ).getHeader() );
        assertEquals( "==",
                      tsdt.getConditionPattern( "f1" ).getChildColumns().get( 0 ).getOperator() );

        assertEquals( BaseSingleFieldConstraint.TYPE_RET_VALUE,
                      tsdt.getConditionPattern( "f1" ).getChildColumns().get( 1 ).getConstraintValueType() );
        assertEquals( "rating",
                      tsdt.getConditionPattern( "f1" ).getChildColumns().get( 1 ).getFactField() );
        assertEquals( "Driver",
                      tsdt.getPattern( tsdt.getConditionPattern( "f1" ).getChildColumns().get( 1 ) ).getFactType() );
        assertEquals( "Driver rating",
                      tsdt.getConditionPattern( "f1" ).getChildColumns().get( 1 ).getHeader() );
        assertEquals( "==",
                      tsdt.getConditionPattern( "f1" ).getChildColumns().get( 1 ).getOperator() );

        assertEquals( BaseSingleFieldConstraint.TYPE_LITERAL,
                      tsdt.getConditionPattern( "f2" ).getChildColumns().get( 0 ).getConstraintValueType() );
        assertEquals( "name",
                      tsdt.getConditionPattern( "f2" ).getChildColumns().get( 0 ).getFactField() );
        assertEquals( "Person",
                      tsdt.getPattern( tsdt.getConditionPattern( "f2" ).getChildColumns().get( 0 ) ).getFactType() );
        assertEquals( "Person f2 name",
                      tsdt.getConditionPattern( "f2" ).getChildColumns().get( 0 ).getHeader() );
        assertEquals( "==",
                      tsdt.getConditionPattern( "f2" ).getChildColumns().get( 0 ).getOperator() );

        assertEquals( BaseSingleFieldConstraint.TYPE_PREDICATE,
                      tsdt.getConditionPattern( "f2" ).getChildColumns().get( 1 ).getConstraintValueType() );
        assertEquals( "(not needed)",
                      tsdt.getConditionPattern( "f2" ).getChildColumns().get( 1 ).getFactField() );
        assertEquals( "Person",
                      tsdt.getPattern( tsdt.getConditionPattern( "f2" ).getChildColumns().get( 1 ) ).getFactType() );
        assertEquals( "Person f2 not needed",
                      tsdt.getConditionPattern( "f2" ).getChildColumns().get( 1 ).getHeader() );
        assertEquals( null,
                      tsdt.getConditionPattern( "f2" ).getChildColumns().get( 1 ).getOperator() );

        assertEquals( 4,
                      tsdt.getActionCols().size() );

        ActionInsertFactCol52 a1 = (ActionInsertFactCol52) tsdt.getActionCols().get( 0 );
        assertEquals( "ins",
                      a1.getBoundName() );
        assertEquals( "Cheese",
                      a1.getFactType() );
        assertEquals( "price",
                      a1.getFactField() );
        assertEquals( DataType.TYPE_NUMERIC_INTEGER,
                      a1.getType() );

        ActionRetractFactCol52 a2 = (ActionRetractFactCol52) tsdt.getActionCols().get( 1 );
        assertNotNull( a2 );

        ActionSetFieldCol52 a3 = (ActionSetFieldCol52) tsdt.getActionCols().get( 2 );
        assertEquals( "f1",
                      a3.getBoundName() );
        assertEquals( "goo1",
                      a3.getFactField() );
        assertEquals( DataType.TYPE_STRING,
                      a3.getType() );

        ActionSetFieldCol52 a4 = (ActionSetFieldCol52) tsdt.getActionCols().get( 3 );
        assertEquals( "f1",
                      a4.getBoundName() );
        assertEquals( "goo2",
                      a4.getFactField() );
        assertEquals( "whee",
                      a4.getDefaultValue().getStringValue() );
        assertEquals( DataType.TYPE_STRING,
                      a4.getType() );

        assertEquals( 2,
                      tsdt.getData().size() );

        for ( int i = 0; i < 2; i++ ) {
            System.out.println( "Row-" + i );
            StringBuilder sb = new StringBuilder();
            for ( DTCellValue52 c : tsdt.getData().get( i ) ) {
                sb.append( c.getStringValue() + ", " );
            }
            sb.delete( sb.lastIndexOf( "," ),
                       sb.length() );
            System.out.println( sb.toString() );
        }

        assertEquals( new Integer( 1 ),
                      (Integer) tsdt.getData().get( 0 ).get( 0 ).getNumericValue() );
        assertEquals( "desc",
                      tsdt.getData().get( 0 ).get( 1 ).getStringValue() );
        assertEquals( "metar1",
                      tsdt.getData().get( 0 ).get( 2 ).getStringValue() );
        assertEquals( "saliencer1",
                      tsdt.getData().get( 0 ).get( 3 ).getStringValue() );
        assertEquals( "f1c1r1",
                      tsdt.getData().get( 0 ).get( 4 ).getStringValue() );
        assertEquals( "f1c2r1",
                      tsdt.getData().get( 0 ).get( 5 ).getStringValue() );
        assertEquals( "f2c1r1",
                      tsdt.getData().get( 0 ).get( 6 ).getStringValue() );
        assertEquals( "f2c2r1",
                      tsdt.getData().get( 0 ).get( 7 ).getStringValue() );
        assertEquals( "a1r1",
                      tsdt.getData().get( 0 ).get( 8 ).getStringValue() );
        assertEquals( "ret1",
                      tsdt.getData().get( 0 ).get( 9 ).getStringValue() );
        assertEquals( "a3r1",
                      tsdt.getData().get( 0 ).get( 10 ).getStringValue() );
        assertEquals( "a4r1",
                      tsdt.getData().get( 0 ).get( 11 ).getStringValue() );

        assertEquals( new Integer( 2 ),
                      (Integer) tsdt.getData().get( 1 ).get( 0 ).getNumericValue() );
        assertEquals( "desc",
                      tsdt.getData().get( 1 ).get( 1 ).getStringValue() );
        assertEquals( "metar2",
                      tsdt.getData().get( 1 ).get( 2 ).getStringValue() );
        assertEquals( "saliencer2",
                      tsdt.getData().get( 1 ).get( 3 ).getStringValue() );
        assertEquals( "f1c1r2",
                      tsdt.getData().get( 1 ).get( 4 ).getStringValue() );
        assertEquals( "f1c2r2",
                      tsdt.getData().get( 1 ).get( 5 ).getStringValue() );
        assertEquals( "f2c1r2",
                      tsdt.getData().get( 1 ).get( 6 ).getStringValue() );
        assertEquals( "f2c2r2",
                      tsdt.getData().get( 1 ).get( 7 ).getStringValue() );
        assertEquals( "a1r2",
                      tsdt.getData().get( 1 ).get( 8 ).getStringValue() );
        assertEquals( "ret1",
                      tsdt.getData().get( 1 ).get( 9 ).getStringValue() );
        assertEquals( "a3r2",
                      tsdt.getData().get( 1 ).get( 10 ).getStringValue() );
        assertEquals( "a4r2",
                      tsdt.getData().get( 1 ).get( 11 ).getStringValue() );

        isRowEquivalent( tsdt.getData().get( 0 ),
                         expected[ 0 ] );
        isRowEquivalent( tsdt.getData().get( 1 ),
                         expected[ 1 ] );

    }

    private void isRowEquivalent( List<DTCellValue52> row,
                                  String[] array ) {
        assertEquals( row.size(),
                      array.length );

        int newRowNum = (Integer) row.get( 0 ).getNumericValue();
        int oldRowNum = Integer.valueOf( array[ 0 ] );
        assertEquals( newRowNum,
                      oldRowNum );

        for ( int iCol = 1; iCol < row.size(); iCol++ ) {
            DTCellValue52 cell = row.get( iCol );
            String v1 = cell.getStringValue();
            String v2 = array[ iCol ];
            assertTrue( isEqualOrNull( v1,
                                       v2 ) );
            assertEquals( v1,
                          v2 );
        }
    }

    @Test
    public void testConversionPatternGrouping2() {

        GuidedDecisionTable dt = new GuidedDecisionTable();
        dt.tableName = "michael";

        ConditionCol con = new ConditionCol();
        con.boundName = "z1";
        con.constraintValueType = BaseSingleFieldConstraint.TYPE_LITERAL;
        con.factField = "age";
        con.factType = "Driver";
        con.header = "Driver z1 age";
        con.operator = "==";
        dt.conditionCols.add( con );

        ConditionCol con2 = new ConditionCol();
        con2.boundName = "f1";
        con2.constraintValueType = BaseSingleFieldConstraint.TYPE_LITERAL;
        con2.factField = "name";
        con2.factType = "Person";
        con2.header = "Person f1 name";
        con2.operator = "==";
        dt.conditionCols.add( con2 );

        ConditionCol con3 = new ConditionCol();
        con3.boundName = "z1";
        con3.constraintValueType = BaseSingleFieldConstraint.TYPE_RET_VALUE;
        con3.factField = "rating";
        con3.factType = "Driver";
        con3.header = "Driver rating";
        con3.operator = "==";
        dt.conditionCols.add( con3 );

        ConditionCol con4 = new ConditionCol();
        con4.boundName = "f2";
        con4.constraintValueType = BaseSingleFieldConstraint.TYPE_PREDICATE;
        con4.factType = "Person2";
        con4.header = "Person2 f2 not needed";
        con4.factField = "(not needed)";
        dt.conditionCols.add( con4 );

        dt.data = new String[][]{
                new String[]{ "1", "desc", "z1c1r1", "f1c1r1", "z1c2r1", "f2c1r1" },
                new String[]{ "2", "desc", "z1c1r2", "f1c1r2", "z1c2r2", "f2c1r2" }
        };

        String[][] expected = new String[][]{
                new String[]{ "1", "desc", "z1c1r1", "z1c2r1", "f1c1r1", "f2c1r1" },
                new String[]{ "2", "desc", "z1c1r2", "z1c2r2", "f1c1r2", "f2c1r2" }
        };

        GuidedDecisionTable52 tsdt = upgrader.upgrade( dt );

        assertEquals( "michael",
                      tsdt.getTableName() );

        assertEquals( 0,
                      tsdt.getMetadataCols().size() );

        assertEquals( 0,
                      tsdt.getAttributeCols().size() );

        assertEquals( 3,
                      tsdt.getConditions().size() );

        assertEquals( "z1",
                      tsdt.getConditionPattern( "z1" ).getBoundName() );
        assertEquals( "Driver",
                      tsdt.getConditionPattern( "z1" ).getFactType() );

        assertEquals( "f1",
                      tsdt.getConditionPattern( "f1" ).getBoundName() );
        assertEquals( "Person",
                      tsdt.getConditionPattern( "f1" ).getFactType() );

        assertEquals( "f2",
                      tsdt.getConditionPattern( "f2" ).getBoundName() );
        assertEquals( "Person2",
                      tsdt.getConditionPattern( "f2" ).getFactType() );

        assertEquals( 2,
                      tsdt.getConditionPattern( "z1" ).getChildColumns().size() );

        assertEquals( 1,
                      tsdt.getConditionPattern( "f1" ).getChildColumns().size() );

        assertEquals( 1,
                      tsdt.getConditionPattern( "f2" ).getChildColumns().size() );

        assertEquals( BaseSingleFieldConstraint.TYPE_LITERAL,
                      tsdt.getConditionPattern( "z1" ).getChildColumns().get( 0 ).getConstraintValueType() );
        assertEquals( "age",
                      tsdt.getConditionPattern( "z1" ).getChildColumns().get( 0 ).getFactField() );
        assertEquals( "Driver",
                      tsdt.getPattern( tsdt.getConditionPattern( "z1" ).getChildColumns().get( 0 ) ).getFactType() );
        assertEquals( "Driver z1 age",
                      tsdt.getConditionPattern( "z1" ).getChildColumns().get( 0 ).getHeader() );
        assertEquals( "==",
                      tsdt.getConditionPattern( "z1" ).getChildColumns().get( 0 ).getOperator() );

        assertEquals( BaseSingleFieldConstraint.TYPE_RET_VALUE,
                      tsdt.getConditionPattern( "z1" ).getChildColumns().get( 1 ).getConstraintValueType() );
        assertEquals( "rating",
                      tsdt.getConditionPattern( "z1" ).getChildColumns().get( 1 ).getFactField() );
        assertEquals( "Driver",
                      tsdt.getPattern( tsdt.getConditionPattern( "z1" ).getChildColumns().get( 1 ) ).getFactType() );
        assertEquals( "Driver rating",
                      tsdt.getConditionPattern( "z1" ).getChildColumns().get( 1 ).getHeader() );
        assertEquals( "==",
                      tsdt.getConditionPattern( "z1" ).getChildColumns().get( 1 ).getOperator() );

        assertEquals( BaseSingleFieldConstraint.TYPE_LITERAL,
                      tsdt.getConditionPattern( "f1" ).getChildColumns().get( 0 ).getConstraintValueType() );
        assertEquals( "name",
                      tsdt.getConditionPattern( "f1" ).getChildColumns().get( 0 ).getFactField() );
        assertEquals( "Person",
                      tsdt.getPattern( tsdt.getConditionPattern( "f1" ).getChildColumns().get( 0 ) ).getFactType() );
        assertEquals( "Person f1 name",
                      tsdt.getConditionPattern( "f1" ).getChildColumns().get( 0 ).getHeader() );
        assertEquals( "==",
                      tsdt.getConditionPattern( "f1" ).getChildColumns().get( 0 ).getOperator() );

        assertEquals( BaseSingleFieldConstraint.TYPE_PREDICATE,
                      tsdt.getConditionPattern( "f2" ).getChildColumns().get( 0 ).getConstraintValueType() );
        assertEquals( "(not needed)",
                      tsdt.getConditionPattern( "f2" ).getChildColumns().get( 0 ).getFactField() );
        assertEquals( "Person2",
                      tsdt.getPattern( tsdt.getConditionPattern( "f2" ).getChildColumns().get( 0 ) ).getFactType() );
        assertEquals( "Person2 f2 not needed",
                      tsdt.getConditionPattern( "f2" ).getChildColumns().get( 0 ).getHeader() );
        assertEquals( null,
                      tsdt.getConditionPattern( "f2" ).getChildColumns().get( 0 ).getOperator() );

        assertEquals( 2,
                      tsdt.getData().size() );

        for ( int i = 0; i < 2; i++ ) {
            System.out.println( "Row-" + i );
            StringBuilder sb = new StringBuilder();
            for ( DTCellValue52 c : tsdt.getData().get( i ) ) {
                sb.append( c.getStringValue() + ", " );
            }
            sb.delete( sb.lastIndexOf( "," ),
                       sb.length() );
            System.out.println( sb.toString() );
        }

        assertEquals( new Integer( 1 ),
                      (Integer) tsdt.getData().get( 0 ).get( 0 ).getNumericValue() );
        assertEquals( "desc",
                      tsdt.getData().get( 0 ).get( 1 ).getStringValue() );
        assertEquals( "z1c1r1",
                      tsdt.getData().get( 0 ).get( 2 ).getStringValue() );
        assertEquals( "z1c2r1",
                      tsdt.getData().get( 0 ).get( 3 ).getStringValue() );
        assertEquals( "f1c1r1",
                      tsdt.getData().get( 0 ).get( 4 ).getStringValue() );
        assertEquals( "f2c1r1",
                      tsdt.getData().get( 0 ).get( 5 ).getStringValue() );

        assertEquals( new Integer( 2 ),
                      (Integer) tsdt.getData().get( 1 ).get( 0 ).getNumericValue() );
        assertEquals( "desc",
                      tsdt.getData().get( 1 ).get( 1 ).getStringValue() );
        assertEquals( "z1c1r2",
                      tsdt.getData().get( 1 ).get( 2 ).getStringValue() );
        assertEquals( "z1c2r2",
                      tsdt.getData().get( 1 ).get( 3 ).getStringValue() );
        assertEquals( "f1c1r2",
                      tsdt.getData().get( 1 ).get( 4 ).getStringValue() );
        assertEquals( "f2c1r2",
                      tsdt.getData().get( 1 ).get( 5 ).getStringValue() );

        isRowEquivalent( tsdt.getData().get( 0 ),
                         expected[ 0 ] );
        isRowEquivalent( tsdt.getData().get( 1 ),
                         expected[ 1 ] );

    }

    private boolean isEqualOrNull( Object v1,
                                   Object v2 ) {
        if ( v1 == null && v2 == null ) {
            return true;
        }
        if ( v1 != null && v2 == null ) {
            return false;
        }
        if ( v1 == null && v2 != null ) {
            return false;
        }
        return v1.equals( v2 );
    }

}
