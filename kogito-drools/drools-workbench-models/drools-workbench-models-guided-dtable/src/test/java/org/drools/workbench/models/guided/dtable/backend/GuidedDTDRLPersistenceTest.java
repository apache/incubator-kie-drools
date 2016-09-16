/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
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

import java.util.ArrayList;
import java.util.List;

import org.drools.workbench.models.commons.backend.rule.RuleModelDRLPersistenceImpl;
import org.drools.workbench.models.datamodel.imports.Import;
import org.drools.workbench.models.datamodel.oracle.DataType;
import org.drools.workbench.models.datamodel.rule.ActionExecuteWorkItem;
import org.drools.workbench.models.datamodel.rule.ActionFieldValue;
import org.drools.workbench.models.datamodel.rule.ActionInsertFact;
import org.drools.workbench.models.datamodel.rule.ActionRetractFact;
import org.drools.workbench.models.datamodel.rule.ActionSetField;
import org.drools.workbench.models.datamodel.rule.ActionWorkItemFieldValue;
import org.drools.workbench.models.datamodel.rule.BaseSingleFieldConstraint;
import org.drools.workbench.models.datamodel.rule.FactPattern;
import org.drools.workbench.models.datamodel.rule.FreeFormLine;
import org.drools.workbench.models.datamodel.rule.IAction;
import org.drools.workbench.models.datamodel.rule.IPattern;
import org.drools.workbench.models.datamodel.rule.RuleAttribute;
import org.drools.workbench.models.datamodel.rule.RuleMetadata;
import org.drools.workbench.models.datamodel.rule.RuleModel;
import org.drools.workbench.models.datamodel.rule.SingleFieldConstraint;
import org.drools.workbench.models.datamodel.workitems.PortableBooleanParameterDefinition;
import org.drools.workbench.models.datamodel.workitems.PortableFloatParameterDefinition;
import org.drools.workbench.models.datamodel.workitems.PortableIntegerParameterDefinition;
import org.drools.workbench.models.datamodel.workitems.PortableStringParameterDefinition;
import org.drools.workbench.models.datamodel.workitems.PortableWorkDefinition;
import org.drools.workbench.models.guided.dtable.backend.util.DataUtilities;
import org.drools.workbench.models.guided.dtable.backend.util.GuidedDTTemplateDataProvider;
import org.drools.workbench.models.guided.dtable.backend.util.TemplateDataProvider;
import org.drools.workbench.models.guided.dtable.shared.model.ActionCol52;
import org.drools.workbench.models.guided.dtable.shared.model.ActionInsertFactCol52;
import org.drools.workbench.models.guided.dtable.shared.model.ActionRetractFactCol52;
import org.drools.workbench.models.guided.dtable.shared.model.ActionSetFieldCol52;
import org.drools.workbench.models.guided.dtable.shared.model.ActionWorkItemCol52;
import org.drools.workbench.models.guided.dtable.shared.model.ActionWorkItemInsertFactCol52;
import org.drools.workbench.models.guided.dtable.shared.model.ActionWorkItemSetFieldCol52;
import org.drools.workbench.models.guided.dtable.shared.model.AttributeCol52;
import org.drools.workbench.models.guided.dtable.shared.model.BRLActionColumn;
import org.drools.workbench.models.guided.dtable.shared.model.BRLActionVariableColumn;
import org.drools.workbench.models.guided.dtable.shared.model.BRLConditionColumn;
import org.drools.workbench.models.guided.dtable.shared.model.BRLConditionVariableColumn;
import org.drools.workbench.models.guided.dtable.shared.model.BaseColumn;
import org.drools.workbench.models.guided.dtable.shared.model.CompositeColumn;
import org.drools.workbench.models.guided.dtable.shared.model.ConditionCol52;
import org.drools.workbench.models.guided.dtable.shared.model.DTCellValue52;
import org.drools.workbench.models.guided.dtable.shared.model.DescriptionCol52;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.models.guided.dtable.shared.model.LimitedEntryActionInsertFactCol52;
import org.drools.workbench.models.guided.dtable.shared.model.LimitedEntryActionSetFieldCol52;
import org.drools.workbench.models.guided.dtable.shared.model.LimitedEntryConditionCol52;
import org.drools.workbench.models.guided.dtable.shared.model.MetadataCol52;
import org.drools.workbench.models.guided.dtable.shared.model.Pattern52;
import org.drools.workbench.models.guided.dtable.shared.model.RowNumberCol52;
import org.junit.Test;

import static org.junit.Assert.*;

public class GuidedDTDRLPersistenceTest {

    @Test
    public void testInWithSimpleSingleLiteralValue() {
        GuidedDecisionTable52 dt = new GuidedDecisionTable52();
        dt.setTableName( "in_operator" );

        Pattern52 p1 = new Pattern52();
        p1.setFactType( "Person" );

        ConditionCol52 con = new ConditionCol52();
        con.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        con.setFieldType( DataType.TYPE_STRING );
        con.setFactField( "field1" );
        con.setHeader( "Person field1" );
        con.setOperator( "in" );
        p1.getChildColumns().add( con );

        dt.getConditions().add( p1 );

        dt.setData( DataUtilities.makeDataLists( new String[][]{
                new String[]{ "1", "desc1", "ak1,mk1" },
                new String[]{ "2", "desc2", "(ak2,mk2)" },
                new String[]{ "3", "desc3", "( ak3, mk3 )" },
                new String[]{ "4", "desc4", "( \"ak4\", \"mk4\" )" },
                new String[]{ "5", "desc5", "( \"ak5 \", \" mk5\" )" },
        } ) );

        GuidedDTDRLPersistence p = GuidedDTDRLPersistence.getInstance();
        String drl = p.marshal( dt );

        String expected = "//from row number: 1\n" +
                "//desc1\n" +
                "rule \"Row 1 in_operator\"\n" +
                "dialect \"mvel\"\n" +
                "when\n" +
                "  Person( field1 in ( \"ak1\", \"mk1\" ) )\n" +
                "then\n" +
                "end\n" +
                "//from row number: 2\n" +
                "//desc2\n" +
                "rule \"Row 2 in_operator\"\n" +
                "dialect \"mvel\"\n" +
                "when\n" +
                "  Person( field1 in ( \"ak2\", \"mk2\" ) )\n" +
                "then\n" +
                "end\n" +
                "//from row number: 3\n" +
                "//desc3\n" +
                "rule \"Row 3 in_operator\"\n" +
                "dialect \"mvel\"\n" +
                "when\n" +
                "  Person( field1 in ( \"ak3\", \"mk3\" ) )\n" +
                "then\n" +
                "end\n" +
                "//from row number: 4\n" +
                "//desc4\n" +
                "rule \"Row 4 in_operator\"\n" +
                "dialect \"mvel\"\n" +
                "when\n" +
                "  Person( field1 in ( \"ak4\", \"mk4\" ) )\n" +
                "then\n" +
                "end\n" +
                "//from row number: 5\n" +
                "//desc5\n" +
                "rule \"Row 5 in_operator\"\n" +
                "dialect \"mvel\"\n" +
                "when\n" +
                "  Person( field1 in ( \"ak5 \", \" mk5\" ) )\n" +
                "then\n" +
                "end";

        assertEqualsIgnoreWhitespace( expected,
                                      drl );
    }

    @Test
    public void test2Rules() throws Exception {
        GuidedDecisionTable52 dt = new GuidedDecisionTable52();

        dt.setTableName( "michael" );

        AttributeCol52 attr = new AttributeCol52();
        attr.setAttribute( "salience" );
        attr.setDefaultValue( new DTCellValue52( "66" ) );
        dt.getAttributeCols().add( attr );

        Pattern52 p1 = new Pattern52();
        p1.setBoundName( "f1" );
        p1.setFactType( "Driver" );

        ConditionCol52 con = new ConditionCol52();
        con.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        con.setFactField( "age" );
        con.setHeader( "Driver f1 age" );
        con.setOperator( "==" );
        p1.getChildColumns().add( con );

        ConditionCol52 con2 = new ConditionCol52();
        con2.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        con2.setFactField( "name" );
        con2.setHeader( "Driver f1 name" );
        con2.setOperator( "==" );
        p1.getChildColumns().add( con2 );

        ConditionCol52 con3 = new ConditionCol52();
        con3.setConstraintValueType( BaseSingleFieldConstraint.TYPE_RET_VALUE );
        con3.setFactField( "rating" );
        con3.setHeader( "Driver rating" );
        con3.setOperator( "==" );
        p1.getChildColumns().add( con3 );

        dt.getConditions().add( p1 );

        Pattern52 p2 = new Pattern52();
        p2.setBoundName( "f2" );
        p2.setFactType( "Driver" );

        ConditionCol52 con4 = new ConditionCol52();
        con4.setConstraintValueType( BaseSingleFieldConstraint.TYPE_PREDICATE );
        con4.setHeader( "Driver 2 pimp" );
        con4.setFactField( "(not needed)" );
        p2.getChildColumns().add( con4 );

        dt.getConditions().add( p2 );

        ActionInsertFactCol52 ins = new ActionInsertFactCol52();
        ins.setBoundName( "ins" );
        ins.setFactType( "Cheese" );
        ins.setFactField( "price" );
        ins.setType( DataType.TYPE_NUMERIC_INTEGER );
        dt.getActionCols().add( ins );

        ActionRetractFactCol52 ret = new ActionRetractFactCol52();
        dt.getActionCols().add( ret );

        ActionSetFieldCol52 set = new ActionSetFieldCol52();
        set.setBoundName( "f1" );
        set.setFactField( "goo1" );
        set.setType( DataType.TYPE_STRING );
        dt.getActionCols().add( set );

        ActionSetFieldCol52 set2 = new ActionSetFieldCol52();
        set2.setBoundName( "f1" );
        set2.setFactField( "goo2" );
        set2.setDefaultValue( new DTCellValue52( "whee" ) );
        set2.setType( DataType.TYPE_STRING );
        dt.getActionCols().add( set2 );

        dt.setData( DataUtilities.makeDataLists( new String[][]{
                new String[]{ "1", "desc", "42", "33", "michael", "age * 0.2", "age > 7", "6.60", "true", "gooVal1", "f2" },
                new String[]{ "2", "desc", "66", "39", "bob", "age * 0.3", "age > 7", "6.60", "", "gooVal1", "whee" }
        } ) );

        GuidedDTDRLPersistence p = GuidedDTDRLPersistence.getInstance();
        String drl = p.marshal( dt );

        assertTrue( drl.indexOf( "from row number" ) > -1 );
        assertTrue( drl.indexOf( "rating == ( age * 0.2 )" ) > 0 );
        assertTrue( drl.indexOf( "f2 : Driver( eval( age > 7 ))" ) > 0 );
        assertTrue( drl.indexOf( "rating == ( age * 0.3 )" ) > drl.indexOf( "rating == ( age * 0.2 )" ) );
        assertTrue( drl.indexOf( "f1.setGoo2( \"whee\" )" ) > 0 );
        assertTrue( drl.indexOf( "salience 66" ) > 0 );
    }

    @Test
    public void testAttribs() {
        GuidedDTDRLPersistence p = new GuidedDTDRLPersistence();
        Object[] row = new Object[]{ "1", "desc", "a", null };

        List<BaseColumn> allColumns = new ArrayList<BaseColumn>();
        allColumns.add( new RowNumberCol52() );
        allColumns.add( new DescriptionCol52() );
        List<AttributeCol52> attributeCols = new ArrayList<AttributeCol52>();

        RuleModel rm = new RuleModel();
        RuleAttribute[] orig = rm.attributes;
        p.doAttribs( allColumns,
                     attributeCols,
                     DataUtilities.makeDataRowList( row ),
                     rm );

        assertSame( orig,
                    rm.attributes );

        AttributeCol52 col1 = new AttributeCol52();
        col1.setAttribute( "salience" );
        AttributeCol52 col2 = new AttributeCol52();
        col2.setAttribute( "agenda-group" );
        attributeCols.add( col1 );
        attributeCols.add( col2 );
        allColumns.addAll( attributeCols );

        p.doAttribs( allColumns,
                     attributeCols,
                     DataUtilities.makeDataRowList( row ),
                     rm );

        assertEquals( 1,
                      rm.attributes.length );
        assertEquals( "salience",
                      rm.attributes[ 0 ].getAttributeName() );
        assertEquals( "a",
                      rm.attributes[ 0 ].getValue() );

        row = new Object[]{ "1", "desc", 1l, "b" };
        p.doAttribs( allColumns,
                     attributeCols,
                     DataUtilities.makeDataRowList( row ),
                     rm );
        assertEquals( 2,
                      rm.attributes.length );
        assertEquals( "salience",
                      rm.attributes[ 0 ].getAttributeName() );
        assertEquals( "1",
                      rm.attributes[ 0 ].getValue() );
        assertEquals( "agenda-group",
                      rm.attributes[ 1 ].getAttributeName() );
        assertEquals( "b",
                      rm.attributes[ 1 ].getValue() );

    }

    @Test
    public void testCellCSV() {
        GuidedDTDRLPersistence p = new GuidedDTDRLPersistence();
        assertEquals( "(\"Michael\", \"Mark\", \"Peter\")",
                      p.makeInList( "Michael, Mark, Peter" ) );
        assertEquals( "(\"Michael\")",
                      p.makeInList( "Michael" ) );
        assertEquals( "(\"Michael\")",
                      p.makeInList( "\"Michael\"" ) );
        assertEquals( "(\"Michael\", \"Ma rk\", \"Peter\")",
                      p.makeInList( "Michael, \"Ma rk\", Peter" ) );
        assertEquals( "(WEE WAAH)",
                      p.makeInList( "(WEE WAAH)" ) );
    }

    @Test
    public void testConditionAndActionCellValue() {
        GuidedDTDRLPersistence p = new GuidedDTDRLPersistence();
        assertFalse( p.validCell( null,
                                  DataType.DataTypes.NUMERIC ) );
        assertFalse( p.validCell( "",
                                  DataType.DataTypes.NUMERIC ) );
        assertFalse( p.validCell( "  ",
                                  DataType.DataTypes.NUMERIC ) );

        assertFalse( p.validCell( null,
                                  DataType.DataTypes.STRING ) );
        assertFalse( p.validCell( "",
                                  DataType.DataTypes.STRING ) );
        assertFalse( p.validCell( "  ",
                                  DataType.DataTypes.STRING ) );
    }

    @Test
    public void testAttributeCellValue() {
        GuidedDTDRLPersistence p = new GuidedDTDRLPersistence();
        assertFalse( p.validateAttributeCell( null ) );
        assertFalse( p.validateAttributeCell( "" ) );
        assertFalse( p.validateAttributeCell( "  " ) );
    }

    @Test
    public void testMetadataCellValue() {
        GuidedDTDRLPersistence p = new GuidedDTDRLPersistence();
        assertFalse( p.validateMetadataCell( null ) );
        assertFalse( p.validateMetadataCell( ( "" ) ) );
        assertFalse( p.validateMetadataCell( ( "  " ) ) );
    }

    @Test
    public void testInOperator() {
        GuidedDecisionTable52 dt = new GuidedDecisionTable52();
        dt.setTableName( "michael" );

        AttributeCol52 attr = new AttributeCol52();
        attr.setAttribute( "salience" );
        attr.setDefaultValue( new DTCellValue52( "66" ) );
        dt.getAttributeCols().add( attr );

        Pattern52 p1 = new Pattern52();
        p1.setBoundName( "f1" );
        p1.setFactType( "Driver" );

        ConditionCol52 con = new ConditionCol52();
        con.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        con.setFactField( "age" );
        con.setHeader( "Driver f1 age" );
        con.setOperator( "==" );
        p1.getChildColumns().add( con );

        ConditionCol52 con2 = new ConditionCol52();
        con2.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        con2.setFactField( "name" );
        con2.setHeader( "Driver f1 name" );
        con2.setOperator( "in" );
        p1.getChildColumns().add( con2 );

        ConditionCol52 con3 = new ConditionCol52();
        con3.setConstraintValueType( BaseSingleFieldConstraint.TYPE_RET_VALUE );
        con3.setFactField( "rating" );
        con3.setHeader( "Driver rating" );
        con3.setOperator( "==" );
        p1.getChildColumns().add( con3 );

        ConditionCol52 con4 = new ConditionCol52();
        con4.setConstraintValueType( BaseSingleFieldConstraint.TYPE_PREDICATE );
        con4.setHeader( "Driver 2 pimp" );
        con4.setFactField( "(not needed)" );
        p1.getChildColumns().add( con4 );

        dt.getConditions().add( p1 );

        ActionInsertFactCol52 ins = new ActionInsertFactCol52();
        ins.setBoundName( "ins" );
        ins.setFactType( "Cheese" );
        ins.setFactField( "price" );
        ins.setType( DataType.TYPE_NUMERIC_INTEGER );
        dt.getActionCols().add( ins );

        ActionRetractFactCol52 ret = new ActionRetractFactCol52();
        dt.getActionCols().add( ret );

        ActionSetFieldCol52 set = new ActionSetFieldCol52();
        set.setBoundName( "f1" );
        set.setFactField( "goo1" );
        set.setType( DataType.TYPE_STRING );
        dt.getActionCols().add( set );

        ActionSetFieldCol52 set2 = new ActionSetFieldCol52();
        set2.setBoundName( "f1" );
        set2.setFactField( "goo2" );
        set2.setDefaultValue( new DTCellValue52( "whee" ) );
        set2.setType( DataType.TYPE_STRING );
        dt.getActionCols().add( set2 );

        dt.setData( DataUtilities.makeDataLists( new String[][]{
                new String[]{ "1", "desc", "42", "33", "michael, manik", "age * 0.2", "age > 7", "6.60", "true", "gooVal1", "f2" },
                new String[]{ "2", "desc", "", "39", "bob, frank", "age * 0.3", "age > 7", "6.60", "", "gooVal1", null }
        } ) );

        GuidedDTDRLPersistence p = GuidedDTDRLPersistence.getInstance();
        String drl = p.marshal( dt );

        assertTrue( drl.indexOf( "name in ( \"michael\"," ) > 0 );

    }

    @Test
    public void testInterpolate() {
        GuidedDecisionTable52 dt = new GuidedDecisionTable52();
        dt.setTableName( "michael" );

        AttributeCol52 attr = new AttributeCol52();
        attr.setAttribute( "salience" );
        attr.setDefaultValue( new DTCellValue52( "66" ) );
        dt.getAttributeCols().add( attr );

        Pattern52 p1 = new Pattern52();
        p1.setBoundName( "f1" );
        p1.setFactType( "Driver" );

        ConditionCol52 con = new ConditionCol52();
        con.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        con.setFactField( "age" );
        con.setHeader( "Driver f1 age" );
        con.setOperator( "==" );
        p1.getChildColumns().add( con );

        ConditionCol52 con2 = new ConditionCol52();
        con2.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        con2.setFactField( "name" );
        con2.setHeader( "Driver f1 name" );
        con2.setOperator( "==" );
        p1.getChildColumns().add( con2 );

        ConditionCol52 con3 = new ConditionCol52();
        con3.setConstraintValueType( BaseSingleFieldConstraint.TYPE_RET_VALUE );
        con3.setFactField( "rating" );
        con3.setHeader( "Driver rating" );
        con3.setOperator( "==" );
        p1.getChildColumns().add( con3 );

        dt.getConditions().add( p1 );

        Pattern52 p2 = new Pattern52();
        p2.setBoundName( "f2" );
        p2.setFactType( "Driver" );

        ConditionCol52 con4 = new ConditionCol52();
        con4.setConstraintValueType( BaseSingleFieldConstraint.TYPE_PREDICATE );
        con4.setHeader( "Driver 2 pimp" );
        con4.setFactField( "this.hasSomething($param)" );
        p2.getChildColumns().add( con4 );

        dt.getConditions().add( p2 );

        ActionInsertFactCol52 ins = new ActionInsertFactCol52();
        ins.setBoundName( "ins" );
        ins.setFactType( "Cheese" );
        ins.setFactField( "price" );
        ins.setType( DataType.TYPE_NUMERIC_INTEGER );
        dt.getActionCols().add( ins );

        ActionRetractFactCol52 ret = new ActionRetractFactCol52();
        dt.getActionCols().add( ret );

        ActionSetFieldCol52 set = new ActionSetFieldCol52();
        set.setBoundName( "f1" );
        set.setFactField( "goo1" );
        set.setType( DataType.TYPE_STRING );
        dt.getActionCols().add( set );

        ActionSetFieldCol52 set2 = new ActionSetFieldCol52();
        set2.setBoundName( "f1" );
        set2.setFactField( "goo2" );
        set2.setDefaultValue( new DTCellValue52( "whee" ) );
        set2.setType( DataType.TYPE_STRING );
        dt.getActionCols().add( set2 );

        dt.setData( DataUtilities.makeDataLists( new String[][]{
                new String[]{ "1", "desc", "42", "33", "michael", "age * 0.2", "BAM", "6.60", "true", "gooVal1", "f2" },
                new String[]{ "2", "desc", "66", "39", "bob", "age * 0.3", "BAM", "6.60", "", "gooVal1", "whee" }
        } ) );

        GuidedDTDRLPersistence p = GuidedDTDRLPersistence.getInstance();
        String drl = p.marshal( dt );

        assertTrue( drl.indexOf( "from row number" ) > -1 );
        assertTrue( drl.indexOf( "rating == ( age * 0.2 )" ) > 0 );
        assertTrue( drl.indexOf( "f2 : Driver( eval( this.hasSomething(BAM) ))" ) > 0 );
        assertTrue( drl.indexOf( "rating == ( age * 0.3 )" ) > drl.indexOf( "rating == ( age * 0.2 )" ) );
        assertTrue( drl.indexOf( "f1.setGoo2( \"whee\" )" ) > 0 );
        assertTrue( drl.indexOf( "salience 66" ) > 0 );
    }

    @Test
    public void testLHS() {
        GuidedDTDRLPersistence p = new GuidedDTDRLPersistence();
        String[] row = new String[]{ "1", "desc", "a", "mike", "33 + 1", "age > 6", "stilton" };
        String[][] data = new String[ 1 ][];
        data[ 0 ] = row;

        List<BaseColumn> allColumns = new ArrayList<BaseColumn>();
        List<CompositeColumn<? extends BaseColumn>> allPatterns = new ArrayList<CompositeColumn<? extends BaseColumn>>();
        allColumns.add( new RowNumberCol52() );
        allColumns.add( new DescriptionCol52() );
        allColumns.add( new MetadataCol52() );

        Pattern52 p1 = new Pattern52();
        p1.setBoundName( "p1" );
        p1.setFactType( "Person" );
        allPatterns.add( p1 );

        ConditionCol52 col = new ConditionCol52();
        col.setFactField( "name" );
        col.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        col.setOperator( "==" );
        p1.getChildColumns().add( col );
        allColumns.add( col );

        ConditionCol52 col2 = new ConditionCol52();
        col2.setFactField( "age" );
        col2.setConstraintValueType( BaseSingleFieldConstraint.TYPE_RET_VALUE );
        col2.setOperator( "<" );
        p1.getChildColumns().add( col2 );
        allColumns.add( col2 );

        ConditionCol52 col3 = new ConditionCol52();
        col3.setConstraintValueType( BaseSingleFieldConstraint.TYPE_PREDICATE );
        p1.getChildColumns().add( col3 );
        allColumns.add( col3 );

        Pattern52 p2 = new Pattern52();
        p2.setBoundName( "c" );
        p2.setFactType( "Cheese" );
        allPatterns.add( p2 );

        ConditionCol52 col4 = new ConditionCol52();
        col4.setFactField( "type" );
        col4.setOperator( "==" );
        col4.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        p2.getChildColumns().add( col4 );
        allColumns.add( col4 );

        RuleModel rm = new RuleModel();

        List<DTCellValue52> rowData = DataUtilities.makeDataRowList( row );
        TemplateDataProvider rowDataProvider = new GuidedDTTemplateDataProvider( allColumns,
                                                                                 rowData );

        p.doConditions( allColumns,
                        allPatterns,
                        rowDataProvider,
                        rowData,
                        DataUtilities.makeDataLists( data ),
                        rm );
        assertEquals( 2,
                      rm.lhs.length );

        assertEquals( "Person",
                      ( (FactPattern) rm.lhs[ 0 ] ).getFactType() );
        assertEquals( "p1",
                      ( (FactPattern) rm.lhs[ 0 ] ).getBoundName() );

        assertEquals( "Cheese",
                      ( (FactPattern) rm.lhs[ 1 ] ).getFactType() );
        assertEquals( "c",
                      ( (FactPattern) rm.lhs[ 1 ] ).getBoundName() );

        // examine the first pattern
        FactPattern person = (FactPattern) rm.lhs[ 0 ];
        assertEquals( 3,
                      person.getConstraintList().getConstraints().length );
        SingleFieldConstraint cons = (SingleFieldConstraint) person.getConstraint( 0 );
        assertEquals( BaseSingleFieldConstraint.TYPE_LITERAL,
                      cons.getConstraintValueType() );
        assertEquals( "name",
                      cons.getFieldName() );
        assertEquals( "==",
                      cons.getOperator() );
        assertEquals( "mike",
                      cons.getValue() );

        cons = (SingleFieldConstraint) person.getConstraint( 1 );
        assertEquals( BaseSingleFieldConstraint.TYPE_RET_VALUE,
                      cons.getConstraintValueType() );
        assertEquals( "age",
                      cons.getFieldName() );
        assertEquals( "<",
                      cons.getOperator() );
        assertEquals( "33 + 1",
                      cons.getValue() );

        cons = (SingleFieldConstraint) person.getConstraint( 2 );
        assertEquals( BaseSingleFieldConstraint.TYPE_PREDICATE,
                      cons.getConstraintValueType() );
        assertEquals( "age > 6",
                      cons.getValue() );

        // examine the second pattern
        FactPattern cheese = (FactPattern) rm.lhs[ 1 ];
        assertEquals( 1,
                      cheese.getConstraintList().getConstraints().length );
        cons = (SingleFieldConstraint) cheese.getConstraint( 0 );
        assertEquals( "type",
                      cons.getFieldName() );
        assertEquals( "==",
                      cons.getOperator() );
        assertEquals( "stilton",
                      cons.getValue() );
        assertEquals( BaseSingleFieldConstraint.TYPE_LITERAL,
                      cons.getConstraintValueType() );
    }

    @Test
    public void testLHSBindings() {
        GuidedDTDRLPersistence p = new GuidedDTDRLPersistence();
        String[] row = new String[]{ "1", "desc", "mike", "33 + 1", "age > 6" };
        String[][] data = new String[ 1 ][];
        data[ 0 ] = row;

        List<BaseColumn> allColumns = new ArrayList<BaseColumn>();
        List<CompositeColumn<? extends BaseColumn>> allPatterns = new ArrayList<CompositeColumn<? extends BaseColumn>>();
        allColumns.add( new RowNumberCol52() );
        allColumns.add( new DescriptionCol52() );

        Pattern52 p1 = new Pattern52();
        p1.setBoundName( "p1" );
        p1.setFactType( "Person" );
        allPatterns.add( p1 );

        ConditionCol52 col = new ConditionCol52();
        col.setFactField( "name" );
        col.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        col.setOperator( "==" );
        col.setBinding( "$name" );
        p1.getChildColumns().add( col );
        allColumns.add( col );

        ConditionCol52 col2 = new ConditionCol52();
        col2.setFactField( "age" );
        col2.setConstraintValueType( BaseSingleFieldConstraint.TYPE_RET_VALUE );
        col2.setOperator( "<" );
        col2.setBinding( "$name" );
        p1.getChildColumns().add( col2 );
        allColumns.add( col2 );

        ConditionCol52 col3 = new ConditionCol52();
        col3.setConstraintValueType( BaseSingleFieldConstraint.TYPE_PREDICATE );
        col3.setBinding( "$name" );
        p1.getChildColumns().add( col3 );
        allColumns.add( col3 );

        List<DTCellValue52> rowData = DataUtilities.makeDataRowList( row );
        TemplateDataProvider rowDataProvider = new GuidedDTTemplateDataProvider( allColumns,
                                                                                 rowData );

        RuleModel rm = new RuleModel();

        p.doConditions( allColumns,
                        allPatterns,
                        rowDataProvider,
                        rowData,
                        DataUtilities.makeDataLists( data ),
                        rm );
        assertEquals( 1,
                      rm.lhs.length );

        assertEquals( "Person",
                      ( (FactPattern) rm.lhs[ 0 ] ).getFactType() );
        assertEquals( "p1",
                      ( (FactPattern) rm.lhs[ 0 ] ).getBoundName() );

        // examine the first pattern
        FactPattern person = (FactPattern) rm.lhs[ 0 ];
        assertEquals( 3,
                      person.getConstraintList().getConstraints().length );

        SingleFieldConstraint cons = (SingleFieldConstraint) person.getConstraint( 0 );
        assertEquals( BaseSingleFieldConstraint.TYPE_LITERAL,
                      cons.getConstraintValueType() );
        assertEquals( "name",
                      cons.getFieldName() );
        assertEquals( "==",
                      cons.getOperator() );
        assertEquals( "mike",
                      cons.getValue() );
        assertEquals( "$name",
                      cons.getFieldBinding() );

        cons = (SingleFieldConstraint) person.getConstraint( 1 );
        assertEquals( BaseSingleFieldConstraint.TYPE_RET_VALUE,
                      cons.getConstraintValueType() );
        assertEquals( "age",
                      cons.getFieldName() );
        assertEquals( "<",
                      cons.getOperator() );
        assertEquals( "33 + 1",
                      cons.getValue() );
        assertNull( cons.getFieldBinding() );

        cons = (SingleFieldConstraint) person.getConstraint( 2 );
        assertEquals( BaseSingleFieldConstraint.TYPE_PREDICATE,
                      cons.getConstraintValueType() );
        assertEquals( "age > 6",
                      cons.getValue() );
        assertNull( cons.getFieldBinding() );

    }

    @Test
    public void testLHSNotPattern() {
        GuidedDTDRLPersistence p = new GuidedDTDRLPersistence();
        String[] row = new String[]{ "1", "desc", "a", "mike", "33 + 1", "age > 6", "stilton" };
        String[][] data = new String[ 1 ][];
        data[ 0 ] = row;

        List<BaseColumn> allColumns = new ArrayList<BaseColumn>();
        List<CompositeColumn<? extends BaseColumn>> allPatterns = new ArrayList<CompositeColumn<? extends BaseColumn>>();
        allColumns.add( new RowNumberCol52() );
        allColumns.add( new DescriptionCol52() );
        allColumns.add( new MetadataCol52() );

        Pattern52 p1 = new Pattern52();
        p1.setNegated( true );
        p1.setBoundName( "p1" );
        p1.setFactType( "Person" );
        allPatterns.add( p1 );

        ConditionCol52 col = new ConditionCol52();
        col.setFactField( "name" );
        col.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        col.setOperator( "==" );
        p1.getChildColumns().add( col );
        allColumns.add( col );

        ConditionCol52 col2 = new ConditionCol52();
        col2.setFactField( "age" );
        col2.setConstraintValueType( BaseSingleFieldConstraint.TYPE_RET_VALUE );
        col2.setOperator( "<" );
        p1.getChildColumns().add( col2 );
        allColumns.add( col2 );

        ConditionCol52 col3 = new ConditionCol52();
        col3.setConstraintValueType( BaseSingleFieldConstraint.TYPE_PREDICATE );
        p1.getChildColumns().add( col3 );
        allColumns.add( col3 );

        Pattern52 p2 = new Pattern52();
        p2.setBoundName( "c" );
        p2.setFactType( "Cheese" );
        allPatterns.add( p2 );

        ConditionCol52 col4 = new ConditionCol52();
        col4.setFactField( "type" );
        col4.setOperator( "==" );
        col4.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        p2.getChildColumns().add( col4 );
        allColumns.add( col4 );

        List<DTCellValue52> rowData = DataUtilities.makeDataRowList( row );
        TemplateDataProvider rowDataProvider = new GuidedDTTemplateDataProvider( allColumns,
                                                                                 rowData );

        RuleModel rm = new RuleModel();

        p.doConditions( allColumns,
                        allPatterns,
                        rowDataProvider,
                        rowData,
                        DataUtilities.makeDataLists( data ),
                        rm );

        String drl = RuleModelDRLPersistenceImpl.getInstance().marshal( rm );

        assertEquals( 2,
                      rm.lhs.length );

        assertEquals( "Person",
                      ( (FactPattern) rm.lhs[ 0 ] ).getFactType() );
        assertEquals( "p1",
                      ( (FactPattern) rm.lhs[ 0 ] ).getBoundName() );

        assertEquals( "Cheese",
                      ( (FactPattern) rm.lhs[ 1 ] ).getFactType() );
        assertEquals( "c",
                      ( (FactPattern) rm.lhs[ 1 ] ).getBoundName() );

        // examine the first pattern
        FactPattern person = (FactPattern) rm.lhs[ 0 ];
        assertEquals( 3,
                      person.getConstraintList().getConstraints().length );
        SingleFieldConstraint cons = (SingleFieldConstraint) person.getConstraint( 0 );
        assertEquals( BaseSingleFieldConstraint.TYPE_LITERAL,
                      cons.getConstraintValueType() );
        assertEquals( "name",
                      cons.getFieldName() );
        assertEquals( "==",
                      cons.getOperator() );
        assertEquals( "mike",
                      cons.getValue() );

        cons = (SingleFieldConstraint) person.getConstraint( 1 );
        assertEquals( BaseSingleFieldConstraint.TYPE_RET_VALUE,
                      cons.getConstraintValueType() );
        assertEquals( "age",
                      cons.getFieldName() );
        assertEquals( "<",
                      cons.getOperator() );
        assertEquals( "33 + 1",
                      cons.getValue() );

        cons = (SingleFieldConstraint) person.getConstraint( 2 );
        assertEquals( BaseSingleFieldConstraint.TYPE_PREDICATE,
                      cons.getConstraintValueType() );
        assertEquals( "age > 6",
                      cons.getValue() );

        assertEquals( person.isNegated(),
                      true );

        assertTrue( drl.indexOf( "not Person(" ) > 0 );

        // examine the second pattern
        FactPattern cheese = (FactPattern) rm.lhs[ 1 ];
        assertEquals( 1,
                      cheese.getConstraintList().getConstraints().length );
        cons = (SingleFieldConstraint) cheese.getConstraint( 0 );
        assertEquals( "type",
                      cons.getFieldName() );
        assertEquals( "==",
                      cons.getOperator() );
        assertEquals( "stilton",
                      cons.getValue() );
        assertEquals( BaseSingleFieldConstraint.TYPE_LITERAL,
                      cons.getConstraintValueType() );

        assertEquals( cheese.isNegated(),
                      false );

        assertTrue( drl.indexOf( "c : Cheese(" ) > 0 );

    }

    @Test
    public void multipleLHSNotPatternInclusion() {
        GuidedDTDRLPersistence p = new GuidedDTDRLPersistence();
        Object[] row = new Object[]{ "1", "desc", "mike", true, true };
        Object[][] data = new Object[ 1 ][];
        data[ 0 ] = row;

        List<BaseColumn> allColumns = new ArrayList<>();
        List<CompositeColumn<? extends BaseColumn>> allPatterns = new ArrayList<>();
        allColumns.add( new RowNumberCol52() );
        allColumns.add( new DescriptionCol52() );

        Pattern52 p1 = new Pattern52();
        p1.setBoundName( "p1" );
        p1.setFactType( "Person" );
        allPatterns.add( p1 );

        ConditionCol52 p1col = new ConditionCol52();
        p1col.setFactField( "name" );
        p1col.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        p1col.setOperator( "==" );
        p1.getChildColumns().add( p1col );
        allColumns.add( p1col );

        Pattern52 p2 = new Pattern52();
        p2.setBoundName( "" );
        p2.setNegated( true );
        p2.setFactType( "Cheese" );
        allPatterns.add( p2 );

        ConditionCol52 p2col = new ConditionCol52();
        p2col.setFactField( "this" );
        p2col.setOperator( "" );
        p2col.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        p2.getChildColumns().add( p2col );
        allColumns.add( p2col );

        Pattern52 p3 = new Pattern52();
        p3.setBoundName( "" );
        p3.setNegated( true );
        p3.setFactType( "Smurf" );
        allPatterns.add( p3 );

        ConditionCol52 p3col = new ConditionCol52();
        p3col.setFactField( "this" );
        p3col.setOperator( "!= null" );
        p3col.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        p3.getChildColumns().add( p3col );
        allColumns.add( p3col );

        List<DTCellValue52> rowData = DataUtilities.makeDataRowList( row );
        TemplateDataProvider rowDataProvider = new GuidedDTTemplateDataProvider( allColumns,
                                                                                 rowData );

        RuleModel rm = new RuleModel();
        rm.name = "r0";

        p.doConditions( allColumns,
                        allPatterns,
                        rowDataProvider,
                        rowData,
                        DataUtilities.makeDataLists( data ),
                        rm );

        final String actualRuleModelDrl = RuleModelDRLPersistenceImpl.getInstance().marshal( rm );

        final String expectedRuleModelDrl = "rule \"r0\"\n" +
                "  dialect \"mvel\"\n" +
                "  when\n" +
                "    p1 : Person( name == \"mike\" )\n" +
                "    not Cheese()\n" +
                "    not Smurf( this != null )\n" +
                "  then\n" +
                "end\n";

        assertEqualsIgnoreWhitespace( expectedRuleModelDrl,
                                      actualRuleModelDrl );

        final GuidedDecisionTable52 dt = new GuidedDecisionTable52();
        dt.setTableName( "dt" );

        dt.getConditions().add( p1 );
        dt.getConditions().add( p2 );
        dt.getConditions().add( p3 );

        dt.getData().addAll( DataUtilities.makeDataLists( data ) );

        final String actualDecisionTableDrl = GuidedDTDRLPersistence.getInstance().marshal( dt );

        final String expectedDecisionTableDrl = "//from row number: 1\n" +
                "//desc\n" +
                "rule \"Row1dt\"\n" +
                "  dialect \"mvel\"\n" +
                "  when\n" +
                "    p1 : Person( name == \"mike\" )\n" +
                "    not Cheese()\n" +
                "    not Smurf( this != null )\n" +
                "  then\n" +
                "end\n";

        assertEqualsIgnoreWhitespace( expectedDecisionTableDrl,
                                      actualDecisionTableDrl );
    }

    @Test
    public void testLHSOtherwisePatternBoolean() {
        GuidedDTDRLPersistence p = new GuidedDTDRLPersistence();
        String[][] row = new String[ 2 ][];
        String[][] data = new String[ 2 ][];
        row[ 0 ] = new String[]{ "1", "desc1", "true", "false" };
        List<DTCellValue52> rowDTModel0 = DataUtilities.makeDataRowList( row[ 0 ] );
        data[ 0 ] = row[ 0 ];

        row[ 1 ] = new String[]{ "3", "desc3", null, null };
        List<DTCellValue52> rowDTModel1 = DataUtilities.makeDataRowList( row[ 1 ] );
        rowDTModel1.get( 2 ).setOtherwise( true );
        rowDTModel1.get( 3 ).setOtherwise( true );
        data[ 1 ] = row[ 1 ];

        final List<List<DTCellValue52>> allDTData = new ArrayList<List<DTCellValue52>>() {{
            add( rowDTModel0 );
            add( rowDTModel1 );
        }};

        List<BaseColumn> allColumns = new ArrayList<BaseColumn>();
        List<CompositeColumn<? extends BaseColumn>> allPatterns = new ArrayList<CompositeColumn<? extends BaseColumn>>();
        allColumns.add( new RowNumberCol52() );
        allColumns.add( new DescriptionCol52() );

        Pattern52 p1 = new Pattern52();
        p1.setBoundName( "p1" );
        p1.setFactType( "Person" );
        allPatterns.add( p1 );

        ConditionCol52 col = new ConditionCol52();
        col.setFactField( "alive" );
        col.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        col.setFieldType( DataType.TYPE_BOOLEAN );
        col.setOperator( "==" );
        p1.getChildColumns().add( col );
        allColumns.add( col );

        Pattern52 p2 = new Pattern52();
        p2.setBoundName( "p2" );
        p2.setFactType( "Person" );
        allPatterns.add( p2 );

        ConditionCol52 col2 = new ConditionCol52();
        col2.setFactField( "alive" );
        col2.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        col2.setFieldType( DataType.TYPE_BOOLEAN );
        col2.setOperator( "!=" );
        p2.getChildColumns().add( col2 );
        allColumns.add( col2 );

        RuleModel rm = new RuleModel();

        TemplateDataProvider rowDataProvider0 = new GuidedDTTemplateDataProvider( allColumns,
                                                                                  rowDTModel0 );

        p.doConditions( allColumns,
                        allPatterns,
                        rowDataProvider0,
                        rowDTModel0,
                        allDTData,
                        rm );
        String drl0 = RuleModelDRLPersistenceImpl.getInstance().marshal( rm );

        assertEquals( 2,
                      rm.lhs.length );
        assertEquals( "Person",
                      ( (FactPattern) rm.lhs[ 0 ] ).getFactType() );
        assertEquals( "p1",
                      ( (FactPattern) rm.lhs[ 0 ] ).getBoundName() );

        assertEquals( "Person",
                      ( (FactPattern) rm.lhs[ 1 ] ).getFactType() );
        assertEquals( "p2",
                      ( (FactPattern) rm.lhs[ 1 ] ).getBoundName() );
        assertTrue( drl0.indexOf( "p1 : Person( alive == true )" ) > 0 );
        assertTrue( drl0.indexOf( "p2 : Person( alive != false )" ) > 0 );

        TemplateDataProvider rowDataProvider1 = new GuidedDTTemplateDataProvider( allColumns,
                                                                                  rowDTModel1 );

        p.doConditions( allColumns,
                        allPatterns,
                        rowDataProvider1,
                        rowDTModel1,
                        allDTData,
                        rm );
        String drl1 = RuleModelDRLPersistenceImpl.getInstance().marshal( rm );

        assertEquals( 2,
                      rm.lhs.length );
        assertEquals( "Person",
                      ( (FactPattern) rm.lhs[ 0 ] ).getFactType() );
        assertEquals( "p1",
                      ( (FactPattern) rm.lhs[ 0 ] ).getBoundName() );

        assertEquals( "Person",
                      ( (FactPattern) rm.lhs[ 1 ] ).getFactType() );
        assertEquals( "p2",
                      ( (FactPattern) rm.lhs[ 1 ] ).getBoundName() );
        assertTrue( drl1.indexOf( "p1 : Person( alive not in ( true )" ) > 0 );
        assertTrue( drl1.indexOf( "p2 : Person( alive in ( false )" ) > 0 );

    }

    @Test
    public void testLHSOtherwisePatternDate() {
        GuidedDTDRLPersistence p = new GuidedDTDRLPersistence();
        String[][] row = new String[ 3 ][];
        String[][] data = new String[ 3 ][];
        row[ 0 ] = new String[]{ "1", "desc1", "01-Jan-1980", "20-Jun-1985" };
        List<DTCellValue52> rowDTModel0 = DataUtilities.makeDataRowList( row[ 0 ] );
        data[ 0 ] = row[ 0 ];

        row[ 1 ] = new String[]{ "2", "desc2", "01-Feb-1981", "21-Jun-1986" };
        List<DTCellValue52> rowDTModel1 = DataUtilities.makeDataRowList( row[ 1 ] );
        data[ 1 ] = row[ 1 ];

        row[ 2 ] = new String[]{ "3", "desc3", null, null };
        List<DTCellValue52> rowDTModel2 = DataUtilities.makeDataRowList( row[ 2 ] );
        rowDTModel2.get( 2 ).setOtherwise( true );
        rowDTModel2.get( 3 ).setOtherwise( true );
        data[ 2 ] = row[ 2 ];

        final List<List<DTCellValue52>> allDTData = new ArrayList<List<DTCellValue52>>() {{
            add( rowDTModel0 );
            add( rowDTModel1 );
            add( rowDTModel2 );
        }};

        List<BaseColumn> allColumns = new ArrayList<BaseColumn>();
        List<CompositeColumn<? extends BaseColumn>> allPatterns = new ArrayList<CompositeColumn<? extends BaseColumn>>();
        allColumns.add( new RowNumberCol52() );
        allColumns.add( new DescriptionCol52() );

        Pattern52 p1 = new Pattern52();
        p1.setBoundName( "p1" );
        p1.setFactType( "Person" );
        allPatterns.add( p1 );

        ConditionCol52 col = new ConditionCol52();
        col.setFactField( "dateOfBirth" );
        col.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        col.setFieldType( DataType.TYPE_DATE );
        col.setOperator( "==" );
        p1.getChildColumns().add( col );
        allColumns.add( col );

        Pattern52 p2 = new Pattern52();
        p2.setBoundName( "p2" );
        p2.setFactType( "Person" );
        allPatterns.add( p2 );

        ConditionCol52 col2 = new ConditionCol52();
        col2.setFactField( "dateOfBirth" );
        col2.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        col2.setFieldType( DataType.TYPE_DATE );
        col2.setOperator( "!=" );
        p2.getChildColumns().add( col2 );
        allColumns.add( col2 );

        RuleModel rm = new RuleModel();

        TemplateDataProvider rowDataProvider0 = new GuidedDTTemplateDataProvider( allColumns,
                                                                                  rowDTModel0 );

        p.doConditions( allColumns,
                        allPatterns,
                        rowDataProvider0,
                        rowDTModel0,
                        allDTData,
                        rm );
        String drl0 = RuleModelDRLPersistenceImpl.getInstance().marshal( rm );

        assertEquals( 2,
                      rm.lhs.length );
        assertEquals( "Person",
                      ( (FactPattern) rm.lhs[ 0 ] ).getFactType() );
        assertEquals( "p1",
                      ( (FactPattern) rm.lhs[ 0 ] ).getBoundName() );

        assertEquals( "Person",
                      ( (FactPattern) rm.lhs[ 1 ] ).getFactType() );
        assertEquals( "p2",
                      ( (FactPattern) rm.lhs[ 1 ] ).getBoundName() );
        assertTrue( drl0.indexOf( "p1 : Person( dateOfBirth == \"01-Jan-1980\" )" ) > 0 );
        assertTrue( drl0.indexOf( "p2 : Person( dateOfBirth != \"20-Jun-1985\" )" ) > 0 );

        TemplateDataProvider rowDataProvider1 = new GuidedDTTemplateDataProvider( allColumns,
                                                                                  rowDTModel1 );

        p.doConditions( allColumns,
                        allPatterns,
                        rowDataProvider1,
                        rowDTModel1,
                        allDTData,
                        rm );
        String drl1 = RuleModelDRLPersistenceImpl.getInstance().marshal( rm );

        assertEquals( 2,
                      rm.lhs.length );
        assertEquals( "Person",
                      ( (FactPattern) rm.lhs[ 0 ] ).getFactType() );
        assertEquals( "p1",
                      ( (FactPattern) rm.lhs[ 0 ] ).getBoundName() );

        assertEquals( "Person",
                      ( (FactPattern) rm.lhs[ 1 ] ).getFactType() );
        assertEquals( "p2",
                      ( (FactPattern) rm.lhs[ 1 ] ).getBoundName() );
        assertTrue( drl1.indexOf( "p1 : Person( dateOfBirth == \"01-Feb-1981\" )" ) > 0 );
        assertTrue( drl1.indexOf( "p2 : Person( dateOfBirth != \"21-Jun-1986\" )" ) > 0 );

        TemplateDataProvider rowDataProvider2 = new GuidedDTTemplateDataProvider( allColumns,
                                                                                  rowDTModel2 );

        p.doConditions( allColumns,
                        allPatterns,
                        rowDataProvider2,
                        rowDTModel2,
                        allDTData,
                        rm );
        String drl2 = RuleModelDRLPersistenceImpl.getInstance().marshal( rm );

        assertEquals( 2,
                      rm.lhs.length );
        assertEquals( "Person",
                      ( (FactPattern) rm.lhs[ 0 ] ).getFactType() );
        assertEquals( "p1",
                      ( (FactPattern) rm.lhs[ 0 ] ).getBoundName() );

        assertEquals( "Person",
                      ( (FactPattern) rm.lhs[ 1 ] ).getFactType() );
        assertEquals( "p2",
                      ( (FactPattern) rm.lhs[ 1 ] ).getBoundName() );
        assertTrue( drl2.indexOf( "p1 : Person( dateOfBirth not in ( \"01-Jan-1980\", \"01-Feb-1981\" )" ) > 0 );
        assertTrue( drl2.indexOf( "p2 : Person( dateOfBirth in ( \"20-Jun-1985\", \"21-Jun-1986\" )" ) > 0 );

    }

    @Test
    public void testLHSOtherwisePatternNumeric() {
        GuidedDTDRLPersistence p = new GuidedDTDRLPersistence();
        String[][] row = new String[ 3 ][];
        String[][] data = new String[ 3 ][];
        row[ 0 ] = new String[]{ "1", "desc1", "1", "1" };
        List<DTCellValue52> rowDTModel0 = DataUtilities.makeDataRowList( row[ 0 ] );
        data[ 0 ] = row[ 0 ];

        row[ 1 ] = new String[]{ "2", "desc2", "2", "2" };
        List<DTCellValue52> rowDTModel1 = DataUtilities.makeDataRowList( row[ 1 ] );
        data[ 1 ] = row[ 1 ];

        row[ 2 ] = new String[]{ "3", "desc3", null, null };
        List<DTCellValue52> rowDTModel2 = DataUtilities.makeDataRowList( row[ 2 ] );
        rowDTModel2.get( 2 ).setOtherwise( true );
        rowDTModel2.get( 3 ).setOtherwise( true );
        data[ 2 ] = row[ 2 ];

        final List<List<DTCellValue52>> allDTData = new ArrayList<List<DTCellValue52>>() {{
            add( rowDTModel0 );
            add( rowDTModel1 );
            add( rowDTModel2 );
        }};

        List<BaseColumn> allColumns = new ArrayList<BaseColumn>();
        List<CompositeColumn<? extends BaseColumn>> allPatterns = new ArrayList<CompositeColumn<? extends BaseColumn>>();
        allColumns.add( new RowNumberCol52() );
        allColumns.add( new DescriptionCol52() );

        Pattern52 p1 = new Pattern52();
        p1.setBoundName( "p1" );
        p1.setFactType( "Person" );
        allPatterns.add( p1 );

        ConditionCol52 col = new ConditionCol52();
        col.setFactField( "age" );
        col.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        col.setFieldType( DataType.TYPE_NUMERIC_INTEGER );
        col.setOperator( "==" );
        p1.getChildColumns().add( col );
        allColumns.add( col );

        Pattern52 p2 = new Pattern52();
        p2.setBoundName( "p2" );
        p2.setFactType( "Person" );
        allPatterns.add( p2 );

        ConditionCol52 col2 = new ConditionCol52();
        col2.setFactField( "age" );
        col2.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        col2.setFieldType( DataType.TYPE_NUMERIC_INTEGER );
        col2.setOperator( "!=" );
        p2.getChildColumns().add( col2 );
        allColumns.add( col2 );

        RuleModel rm = new RuleModel();

        TemplateDataProvider rowDataProvider0 = new GuidedDTTemplateDataProvider( allColumns,
                                                                                  rowDTModel0 );

        p.doConditions( allColumns,
                        allPatterns,
                        rowDataProvider0,
                        rowDTModel0,
                        allDTData,
                        rm );
        String drl0 = RuleModelDRLPersistenceImpl.getInstance().marshal( rm );

        assertEquals( 2,
                      rm.lhs.length );
        assertEquals( "Person",
                      ( (FactPattern) rm.lhs[ 0 ] ).getFactType() );
        assertEquals( "p1",
                      ( (FactPattern) rm.lhs[ 0 ] ).getBoundName() );

        assertEquals( "Person",
                      ( (FactPattern) rm.lhs[ 1 ] ).getFactType() );
        assertEquals( "p2",
                      ( (FactPattern) rm.lhs[ 1 ] ).getBoundName() );
        assertTrue( drl0.indexOf( "p1 : Person( age == 1 )" ) > 0 );
        assertTrue( drl0.indexOf( "p2 : Person( age != 1 )" ) > 0 );

        TemplateDataProvider rowDataProvider1 = new GuidedDTTemplateDataProvider( allColumns,
                                                                                  rowDTModel1 );

        p.doConditions( allColumns,
                        allPatterns,
                        rowDataProvider1,
                        rowDTModel1,
                        allDTData,
                        rm );
        String drl1 = RuleModelDRLPersistenceImpl.getInstance().marshal( rm );

        assertEquals( 2,
                      rm.lhs.length );
        assertEquals( "Person",
                      ( (FactPattern) rm.lhs[ 0 ] ).getFactType() );
        assertEquals( "p1",
                      ( (FactPattern) rm.lhs[ 0 ] ).getBoundName() );

        assertEquals( "Person",
                      ( (FactPattern) rm.lhs[ 1 ] ).getFactType() );
        assertEquals( "p2",
                      ( (FactPattern) rm.lhs[ 1 ] ).getBoundName() );
        assertTrue( drl1.indexOf( "p1 : Person( age == 2 )" ) > 0 );
        assertTrue( drl1.indexOf( "p2 : Person( age != 2 )" ) > 0 );

        TemplateDataProvider rowDataProvider2 = new GuidedDTTemplateDataProvider( allColumns,
                                                                                  rowDTModel2 );

        p.doConditions( allColumns,
                        allPatterns,
                        rowDataProvider2,
                        rowDTModel2,
                        allDTData,
                        rm );
        String drl2 = RuleModelDRLPersistenceImpl.getInstance().marshal( rm );

        assertEquals( 2,
                      rm.lhs.length );
        assertEquals( "Person",
                      ( (FactPattern) rm.lhs[ 0 ] ).getFactType() );
        assertEquals( "p1",
                      ( (FactPattern) rm.lhs[ 0 ] ).getBoundName() );

        assertEquals( "Person",
                      ( (FactPattern) rm.lhs[ 1 ] ).getFactType() );
        assertEquals( "p2",
                      ( (FactPattern) rm.lhs[ 1 ] ).getBoundName() );
        assertTrue( drl2.indexOf( "p1 : Person( age not in ( 1, 2 )" ) > 0 );
        assertTrue( drl2.indexOf( "p2 : Person( age in ( 1, 2 )" ) > 0 );

    }

    @Test
    public void testLHSOtherwisePatternString() {
        GuidedDTDRLPersistence p = new GuidedDTDRLPersistence();
        String[][] row = new String[ 3 ][];
        String[][] data = new String[ 3 ][];
        row[ 0 ] = new String[]{ "1", "desc1", "Michael1", "Michael1" };
        List<DTCellValue52> rowDTModel0 = DataUtilities.makeDataRowList( row[ 0 ] );
        data[ 0 ] = row[ 0 ];

        row[ 1 ] = new String[]{ "2", "desc2", "Michael2", "Michael2" };
        List<DTCellValue52> rowDTModel1 = DataUtilities.makeDataRowList( row[ 1 ] );
        data[ 1 ] = row[ 1 ];

        row[ 2 ] = new String[]{ "3", "desc3", "", "" };
        List<DTCellValue52> rowDTModel2 = DataUtilities.makeDataRowList( row[ 2 ] );
        rowDTModel2.get( 2 ).setOtherwise( true );
        rowDTModel2.get( 3 ).setOtherwise( true );
        data[ 2 ] = row[ 2 ];

        final List<List<DTCellValue52>> allDTData = new ArrayList<List<DTCellValue52>>() {{
            add( rowDTModel0 );
            add( rowDTModel1 );
            add( rowDTModel2 );
        }};

        List<BaseColumn> allColumns = new ArrayList<BaseColumn>();
        List<CompositeColumn<? extends BaseColumn>> allPatterns = new ArrayList<CompositeColumn<? extends BaseColumn>>();
        allColumns.add( new RowNumberCol52() );
        allColumns.add( new DescriptionCol52() );

        Pattern52 p1 = new Pattern52();
        p1.setBoundName( "p1" );
        p1.setFactType( "Person" );
        allPatterns.add( p1 );

        ConditionCol52 col = new ConditionCol52();
        col.setFactField( "name" );
        col.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        col.setFieldType( DataType.TYPE_STRING );
        col.setOperator( "==" );
        p1.getChildColumns().add( col );
        allColumns.add( col );

        Pattern52 p2 = new Pattern52();
        p2.setBoundName( "p2" );
        p2.setFactType( "Person" );
        allPatterns.add( p2 );

        ConditionCol52 col2 = new ConditionCol52();
        col2.setFactField( "name" );
        col2.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        col2.setFieldType( DataType.TYPE_STRING );
        col2.setOperator( "!=" );
        p2.getChildColumns().add( col2 );
        allColumns.add( col2 );

        RuleModel rm = new RuleModel();

        TemplateDataProvider rowDataProvider0 = new GuidedDTTemplateDataProvider( allColumns,
                                                                                  rowDTModel0 );

        p.doConditions( allColumns,
                        allPatterns,
                        rowDataProvider0,
                        rowDTModel0,
                        allDTData,
                        rm );
        String drl0 = RuleModelDRLPersistenceImpl.getInstance().marshal( rm );

        assertEquals( 2,
                      rm.lhs.length );
        assertEquals( "Person",
                      ( (FactPattern) rm.lhs[ 0 ] ).getFactType() );
        assertEquals( "p1",
                      ( (FactPattern) rm.lhs[ 0 ] ).getBoundName() );

        assertEquals( "Person",
                      ( (FactPattern) rm.lhs[ 1 ] ).getFactType() );
        assertEquals( "p2",
                      ( (FactPattern) rm.lhs[ 1 ] ).getBoundName() );
        assertTrue( drl0.indexOf( "p1 : Person( name == \"Michael1\" )" ) > 0 );
        assertTrue( drl0.indexOf( "p2 : Person( name != \"Michael1\" )" ) > 0 );

        TemplateDataProvider rowDataProvider1 = new GuidedDTTemplateDataProvider( allColumns,
                                                                                  rowDTModel1 );

        p.doConditions( allColumns,
                        allPatterns,
                        rowDataProvider1,
                        rowDTModel1,
                        allDTData,
                        rm );
        String drl1 = RuleModelDRLPersistenceImpl.getInstance().marshal( rm );

        assertEquals( 2,
                      rm.lhs.length );
        assertEquals( "Person",
                      ( (FactPattern) rm.lhs[ 0 ] ).getFactType() );
        assertEquals( "p1",
                      ( (FactPattern) rm.lhs[ 0 ] ).getBoundName() );

        assertEquals( "Person",
                      ( (FactPattern) rm.lhs[ 1 ] ).getFactType() );
        assertEquals( "p2",
                      ( (FactPattern) rm.lhs[ 1 ] ).getBoundName() );
        assertTrue( drl1.indexOf( "p1 : Person( name == \"Michael2\" )" ) > 0 );
        assertTrue( drl1.indexOf( "p2 : Person( name != \"Michael2\" )" ) > 0 );

        TemplateDataProvider rowDataProvider2 = new GuidedDTTemplateDataProvider( allColumns,
                                                                                  rowDTModel2 );

        p.doConditions( allColumns,
                        allPatterns,
                        rowDataProvider2,
                        rowDTModel2,
                        allDTData,
                        rm );
        String drl2 = RuleModelDRLPersistenceImpl.getInstance().marshal( rm );

        assertEquals( 2,
                      rm.lhs.length );
        assertEquals( "Person",
                      ( (FactPattern) rm.lhs[ 0 ] ).getFactType() );
        assertEquals( "p1",
                      ( (FactPattern) rm.lhs[ 0 ] ).getBoundName() );

        assertEquals( "Person",
                      ( (FactPattern) rm.lhs[ 1 ] ).getFactType() );
        assertEquals( "p2",
                      ( (FactPattern) rm.lhs[ 1 ] ).getBoundName() );
        assertTrue( drl2.indexOf( "p1 : Person( name not in ( \"Michael1\", \"Michael2\" )" ) > 0 );
        assertTrue( drl2.indexOf( "p2 : Person( name in ( \"Michael1\", \"Michael2\" )" ) > 0 );

    }

    @Test
    public void testMetaData() {
        GuidedDTDRLPersistence p = new GuidedDTDRLPersistence();
        String[] row = new String[]{ "1", "desc", "bar", "" };

        List<BaseColumn> allColumns = new ArrayList<BaseColumn>();
        allColumns.add( new RowNumberCol52() );
        allColumns.add( new DescriptionCol52() );
        List<MetadataCol52> metadataCols = new ArrayList<MetadataCol52>();

        RuleModel rm = new RuleModel();
        RuleMetadata[] orig = rm.metadataList;
        // RuleAttribute[] orig = rm.attributes;
        p.doMetadata( allColumns,
                      metadataCols,
                      DataUtilities.makeDataRowList( row ),
                      rm );

        assertSame( orig,
                    rm.metadataList );

        MetadataCol52 col1 = new MetadataCol52();
        col1.setMetadata( "foo" );
        MetadataCol52 col2 = new MetadataCol52();
        col2.setMetadata( "foo2" );
        metadataCols.add( col1 );
        metadataCols.add( col2 );
        allColumns.addAll( metadataCols );

        p.doMetadata( allColumns,
                      metadataCols,
                      DataUtilities.makeDataRowList( row ),
                      rm );

        assertEquals( 1,
                      rm.metadataList.length );
        assertEquals( "foo",
                      rm.metadataList[ 0 ].getAttributeName() );
        assertEquals( "bar",
                      rm.metadataList[ 0 ].getValue() );

        row = new String[]{ "1", "desc", "bar1", "bar2" };
        p.doMetadata( allColumns,
                      metadataCols,
                      DataUtilities.makeDataRowList( row ),
                      rm );
        assertEquals( 2,
                      rm.metadataList.length );
        assertEquals( "foo",
                      rm.metadataList[ 0 ].getAttributeName() );
        assertEquals( "bar1",
                      rm.metadataList[ 0 ].getValue() );
        assertEquals( "foo2",
                      rm.metadataList[ 1 ].getAttributeName() );
        assertEquals( "bar2",
                      rm.metadataList[ 1 ].getValue() );

    }

    @Test
    public void testName() {
        GuidedDTDRLPersistence p = new GuidedDTDRLPersistence();
        assertEquals( "Row 42 XXX",
                      p.getName( "XXX",
                                 42 ) );
        assertEquals( "Row 42 YYY",
                      p.getName( "YYY",
                                 42 ) );
    }

    @Test
    public void testNoConstraints() {
        GuidedDecisionTable52 dt = new GuidedDecisionTable52();
        dt.setTableFormat( GuidedDecisionTable52.TableFormat.EXTENDED_ENTRY );
        dt.setTableName( "no-constraints" );

        Pattern52 p1 = new Pattern52();
        p1.setBoundName( "x" );
        p1.setFactType( "Context" );

        ConditionCol52 c = new ConditionCol52();
        c.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        c.setFieldType( DataType.TYPE_STRING );
        c.setFactField( "name" );
        c.setOperator( "==" );
        p1.getChildColumns().add( c );

        dt.getConditions().add( p1 );

        ActionSetFieldCol52 asf = new ActionSetFieldCol52();
        asf.setBoundName( "x" );
        asf.setFactField( "age" );
        asf.setType( DataType.TYPE_NUMERIC_INTEGER );

        dt.getActionCols().add( asf );

        dt.setData( DataUtilities.makeDataLists( new Object[][]{
                new Object[]{ "1", "desc", "Fred", 75l }
        } ) );

        String drl1 = GuidedDTDRLPersistence.getInstance().marshal( dt );
        String expected1 = "//from row number: 1\n" +
                "//desc\n" +
                "rule \"Row 1 no-constraints\"\n" +
                "  dialect \"mvel\"\n" +
                "  when\n" +
                "    x : Context( name == \"Fred\" )\n" +
                "  then\n" +
                "    x.setAge( 75 );\n" +
                "end";

        assertEqualsIgnoreWhitespace( expected1,
                                      drl1 );

        dt.setData( DataUtilities.makeDataLists( new Object[][]{
                new Object[]{ "1", "desc", null, 75l }
        } ) );

        String drl2 = GuidedDTDRLPersistence.getInstance().marshal( dt );
        String expected2 = "//from row number: 1\n" +
                "//desc\n" +
                "rule \"Row 1 no-constraints\"\n" +
                "  dialect \"mvel\"\n" +
                "  when\n" +
                "  then\n" +
                "    x.setAge( 75 );\n" +
                "end";

        assertEqualsIgnoreWhitespace( expected2,
                                      drl2 );
    }

    @Test
    public void testNoOperator() {
        GuidedDTDRLPersistence p = new GuidedDTDRLPersistence();
        String[] row = new String[]{ "1", "desc", "a", "> 42" };
        String[][] data = new String[ 1 ][];
        data[ 0 ] = row;

        List<BaseColumn> allColumns = new ArrayList<BaseColumn>();
        List<CompositeColumn<? extends BaseColumn>> allPatterns = new ArrayList<CompositeColumn<? extends BaseColumn>>();
        allColumns.add( new RowNumberCol52() );
        allColumns.add( new DescriptionCol52() );
        allColumns.add( new MetadataCol52() );

        Pattern52 p1 = new Pattern52();
        p1.setBoundName( "p1" );
        p1.setFactType( "Person" );
        allPatterns.add( p1 );

        ConditionCol52 col1 = new ConditionCol52();
        col1.setFactField( "age" );
        col1.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        col1.setOperator( "" );
        p1.getChildColumns().add( col1 );
        allColumns.add( col1 );

        RuleModel rm = new RuleModel();

        List<DTCellValue52> rowData = DataUtilities.makeDataRowList( row );
        TemplateDataProvider rowDataProvider = new GuidedDTTemplateDataProvider( allColumns,
                                                                                 rowData );

        p.doConditions( allColumns,
                        allPatterns,
                        rowDataProvider,
                        rowData,
                        DataUtilities.makeDataLists( data ),
                        rm );

        String drl = RuleModelDRLPersistenceImpl.getInstance().marshal( rm );
        assertTrue( drl.indexOf( "age > \"42\"" ) > 0 );

    }

    @Test
    public void testRHS() {
        GuidedDTDRLPersistence p = new GuidedDTDRLPersistence();
        String[] row = new String[]{ "1", "desc", "a", "a condition", "actionsetfield1", "actionupdatefield2", "retract", "actioninsertfact1", "actioninsertfact2" };

        List<BaseColumn> allColumns = new ArrayList<BaseColumn>();
        allColumns.add( new RowNumberCol52() );
        allColumns.add( new DescriptionCol52() );
        allColumns.add( new MetadataCol52() );
        allColumns.add( new ConditionCol52() );
        List<ActionCol52> cols = new ArrayList<ActionCol52>();

        ActionSetFieldCol52 asf1 = new ActionSetFieldCol52();
        asf1.setBoundName( "a" );
        asf1.setFactField( "field1" );

        asf1.setType( DataType.TYPE_STRING );
        cols.add( asf1 );

        ActionSetFieldCol52 asf2 = new ActionSetFieldCol52();
        asf2.setBoundName( "a" );
        asf2.setFactField( "field2" );
        asf2.setUpdate( true );
        asf2.setType( DataType.TYPE_NUMERIC_INTEGER );
        cols.add( asf2 );

        ActionRetractFactCol52 ret = new ActionRetractFactCol52();
        cols.add( ret );

        ActionInsertFactCol52 ins1 = new ActionInsertFactCol52();
        ins1.setBoundName( "ins" );
        ins1.setFactType( "Cheese" );
        ins1.setFactField( "price" );
        ins1.setType( DataType.TYPE_NUMERIC_INTEGER );
        cols.add( ins1 );

        ActionInsertFactCol52 ins2 = new ActionInsertFactCol52();
        ins2.setBoundName( "ins" );
        ins2.setFactType( "Cheese" );
        ins2.setFactField( "type" );
        ins2.setType( DataType.TYPE_NUMERIC_INTEGER );
        cols.add( ins2 );

        RuleModel rm = new RuleModel();
        allColumns.addAll( cols );

        List<DTCellValue52> rowData = DataUtilities.makeDataRowList( row );
        TemplateDataProvider rowDataProvider = new GuidedDTTemplateDataProvider( allColumns,
                                                                                 rowData );

        p.doActions( allColumns,
                     cols,
                     rowDataProvider,
                     rowData,
                     rm );
        assertEquals( 4,
                      rm.rhs.length );

        // examine the set field action that is produced
        ActionSetField a1 = (ActionSetField) rm.rhs[ 0 ];
        assertEquals( "a",
                      a1.getVariable() );
        assertEquals( 1,
                      a1.getFieldValues().length );

        assertEquals( "field1",
                      a1.getFieldValues()[ 0 ].getField() );
        assertEquals( "actionsetfield1",
                      a1.getFieldValues()[ 0 ].getValue() );
        assertEquals( DataType.TYPE_STRING,
                      a1.getFieldValues()[ 0 ].getType() );

        ActionSetField a2 = (ActionSetField) rm.rhs[ 1 ];
        assertEquals( "a",
                      a2.getVariable() );
        assertEquals( 1,
                      a2.getFieldValues().length );

        assertEquals( "field2",
                      a2.getFieldValues()[ 0 ].getField() );
        assertEquals( "actionupdatefield2",
                      a2.getFieldValues()[ 0 ].getValue() );
        assertEquals( DataType.TYPE_NUMERIC_INTEGER,
                      a2.getFieldValues()[ 0 ].getType() );

        // examine the retract
        ActionRetractFact a3 = (ActionRetractFact) rm.rhs[ 2 ];
        assertEquals( "retract",
                      a3.getVariableName() );

        // examine the insert
        ActionInsertFact a4 = (ActionInsertFact) rm.rhs[ 3 ];
        assertEquals( "Cheese",
                      a4.getFactType() );
        assertEquals( 2,
                      a4.getFieldValues().length );

        assertEquals( "price",
                      a4.getFieldValues()[ 0 ].getField() );
        assertEquals( "actioninsertfact1",
                      a4.getFieldValues()[ 0 ].getValue() );
        assertEquals( DataType.TYPE_NUMERIC_INTEGER,
                      a4.getFieldValues()[ 0 ].getType() );

        assertEquals( "type",
                      a4.getFieldValues()[ 1 ].getField() );
        assertEquals( "actioninsertfact2",
                      a4.getFieldValues()[ 1 ].getValue() );
        assertEquals( DataType.TYPE_NUMERIC_INTEGER,
                      a4.getFieldValues()[ 1 ].getType() );

    }

    @Test
    public void testUpdateModifySingleField() {
        GuidedDecisionTable52 dt = new GuidedDecisionTable52();

        Pattern52 p1 = new Pattern52();
        p1.setBoundName( "x" );
        p1.setFactType( "Context" );

        ConditionCol52 c = new ConditionCol52();
        c.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        p1.getChildColumns().add( c );
        dt.getConditions().add( p1 );

        ActionSetFieldCol52 asf = new ActionSetFieldCol52();
        asf.setBoundName( "x" );
        asf.setFactField( "age" );
        asf.setType( DataType.TYPE_NUMERIC_INTEGER );
        asf.setUpdate( true );

        dt.getActionCols().add( asf );

        dt.setData( DataUtilities.makeDataLists( new Object[][]{
                new Object[]{ "1", "desc", "y", "old" }
        } ) );

        String drl = GuidedDTDRLPersistence.getInstance().marshal( dt );

        assertTrue( drl.indexOf( "Context( )" ) > -1 );
        assertTrue( drl.indexOf( "modify( x ) {" ) > drl.indexOf( "Context( )" ) );
        assertTrue( drl.indexOf( "setAge(" ) > drl.indexOf( "modify( x ) {" ) );

        dt.setData( DataUtilities.makeDataLists( new Object[][]{
                new Object[]{ "1", "desc", null, "old" }
        } ) );

        drl = GuidedDTDRLPersistence.getInstance().marshal( dt );

        assertEquals( -1,
                      drl.indexOf( "Context( )" ) );

        assertTrue( drl.indexOf( "modify( x ) {" ) > -1 );
        assertTrue( drl.indexOf( "setAge(" ) > drl.indexOf( "modify( x ) {" ) );

        dt.setData( DataUtilities.makeDataLists( new Object[][]{
                new Object[]{ "1", "desc", null, null }
        } ) );

        drl = GuidedDTDRLPersistence.getInstance().marshal( dt );

        assertEquals( -1,
                      drl.indexOf( "Context( )" ) );

        assertEquals( -1,
                      drl.indexOf( "modify( x ) {" ) );
        assertEquals( -1,
                      drl.indexOf( "setAge(" ) );
    }

    @Test
    public void testUpdateModifyMultipleFields() {
        GuidedDecisionTable52 dt = new GuidedDecisionTable52();

        Pattern52 p1 = new Pattern52();
        p1.setBoundName( "x" );
        p1.setFactType( "Context" );

        ConditionCol52 c = new ConditionCol52();
        c.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        p1.getChildColumns().add( c );
        dt.getConditions().add( p1 );

        ActionSetFieldCol52 asf1 = new ActionSetFieldCol52();
        asf1.setBoundName( "x" );
        asf1.setFactField( "age" );
        asf1.setType( DataType.TYPE_NUMERIC_INTEGER );
        asf1.setUpdate( true );

        dt.getActionCols().add( asf1 );

        ActionSetFieldCol52 asf2 = new ActionSetFieldCol52();
        asf2.setBoundName( "x" );
        asf2.setFactField( "name" );
        asf2.setType( DataType.TYPE_STRING );
        asf2.setUpdate( true );

        dt.getActionCols().add( asf2 );

        dt.setData( DataUtilities.makeDataLists( new Object[][]{
                new Object[]{ "1", "desc", "x", 55l, "Fred" }
        } ) );

        String drl = GuidedDTDRLPersistence.getInstance().marshal( dt );
        final String expected1 = "//from row number: 1\n" +
                "//desc\n" +
                "rule \"Row 1 null\"\n" +
                "dialect \"mvel\"\n" +
                "when\n" +
                "  x : Context( )\n" +
                "then\n" +
                "  modify( x ) {\n" +
                "    setAge( 55 ), \n" +
                "    setName( \"Fred\" )\n" +
                "}\n" +
                "end\n";
        assertEqualsIgnoreWhitespace( expected1,
                                      drl );

        dt.setData( DataUtilities.makeDataLists( new Object[][]{
                new Object[]{ "1", "desc", "x", null, "Fred" }
        } ) );
        drl = GuidedDTDRLPersistence.getInstance().marshal( dt );
        final String expected2 = "//from row number: 1\n" +
                "//desc\n" +
                "rule \"Row 1 null\"\n" +
                "dialect \"mvel\"\n" +
                "when\n" +
                "  x : Context( )\n" +
                "then\n" +
                "  modify( x ) {\n" +
                "    setName( \"Fred\" )\n" +
                "}\n" +
                "end\n";
        assertEqualsIgnoreWhitespace( expected2,
                                      drl );

        dt.setData( DataUtilities.makeDataLists( new Object[][]{
                new Object[]{ "1", "desc", "x", 55l, null }
        } ) );
        drl = GuidedDTDRLPersistence.getInstance().marshal( dt );
        final String expected3 = "//from row number: 1\n" +
                "//desc\n" +
                "rule \"Row 1 null\"\n" +
                "dialect \"mvel\"\n" +
                "when\n" +
                "  x : Context( )\n" +
                "then\n" +
                "  modify( x ) {\n" +
                "    setAge( 55 ) \n" +
                "}\n" +
                "end\n";
        assertEqualsIgnoreWhitespace( expected3,
                                      drl );
    }

    @Test
    public void testUpdateModifyMultipleFieldsUpdateOneModifyTheOther() {
        GuidedDecisionTable52 dt = new GuidedDecisionTable52();

        Pattern52 p1 = new Pattern52();
        p1.setBoundName( "x" );
        p1.setFactType( "Context" );

        ConditionCol52 c = new ConditionCol52();
        c.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        p1.getChildColumns().add( c );
        dt.getConditions().add( p1 );

        ActionSetFieldCol52 asf1 = new ActionSetFieldCol52();
        asf1.setBoundName( "x" );
        asf1.setFactField( "age" );
        asf1.setType( DataType.TYPE_NUMERIC_INTEGER );
        asf1.setUpdate( true );

        dt.getActionCols().add( asf1 );

        ActionSetFieldCol52 asf2 = new ActionSetFieldCol52();
        asf2.setBoundName( "x" );
        asf2.setFactField( "name" );
        asf2.setType( DataType.TYPE_STRING );
        asf2.setUpdate( false );

        dt.getActionCols().add( asf2 );

        dt.setData( DataUtilities.makeDataLists( new Object[][]{
                new Object[]{ "1", "desc", "x", 55l, "Fred" }
        } ) );

        String drl = GuidedDTDRLPersistence.getInstance().marshal( dt );
        final String expected1 = "//from row number: 1\n" +
                "//desc\n" +
                "rule \"Row 1 null\"\n" +
                "dialect \"mvel\"\n" +
                "when\n" +
                "  x : Context( )\n" +
                "then\n" +
                "  modify( x ) {\n" +
                "    setAge( 55 ) \n" +
                "}\n" +
                "x.setName( \"Fred\" );\n" +
                "end\n";

        assertEqualsIgnoreWhitespace( expected1,
                                      drl );

        dt.setData( DataUtilities.makeDataLists( new Object[][]{
                new Object[]{ "1", "desc", "x", null, "Fred" }
        } ) );

        drl = GuidedDTDRLPersistence.getInstance().marshal( dt );
        final String expected2 = "//from row number: 1\n" +
                "//desc\n" +
                "rule \"Row 1 null\"\n" +
                "dialect \"mvel\"\n" +
                "when\n" +
                "  x : Context( )\n" +
                "then\n" +
                "x.setName( \"Fred\" );\n" +
                "end\n";

        assertEqualsIgnoreWhitespace( expected2,
                                      drl );

        dt.setData( DataUtilities.makeDataLists( new Object[][]{
                new Object[]{ "1", "desc", "x", 55l, "" }
        } ) );

        drl = GuidedDTDRLPersistence.getInstance().marshal( dt );
        final String expected3 = "//from row number: 1\n" +
                "//desc\n" +
                "rule \"Row 1 null\"\n" +
                "dialect \"mvel\"\n" +
                "when\n" +
                "  x : Context( )\n" +
                "then\n" +
                "  modify( x ) {\n" +
                "    setAge( 55 ) \n" +
                "}\n" +
                "end\n";

        assertEqualsIgnoreWhitespace( expected3,
                                      drl );
    }

    @Test
    public void testDefaultValue() {
        GuidedDecisionTable52 dt = new GuidedDecisionTable52();

        Pattern52 p1 = new Pattern52();
        p1.setBoundName( "$c" );
        p1.setFactType( "CheeseLover" );

        ConditionCol52 c = new ConditionCol52();
        c.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        c.setFactField( "favouriteCheese" );
        c.setDefaultValue( new DTCellValue52( "cheddar" ) );
        c.setOperator( "==" );
        p1.getChildColumns().add( c );
        dt.getConditions().add( p1 );

        //With provided getValue()
        String[][] data = new String[][]{
                new String[]{ "1", "desc", "edam" },
        };
        dt.setData( DataUtilities.makeDataLists( data ) );

        String drl = GuidedDTDRLPersistence.getInstance().marshal( dt );

        assertFalse( drl.indexOf( "$c : CheeseLover( favouriteCheese == \"edam\" )" ) == -1 );

        //Without provided getValue() #1
        data = new String[][]{
                new String[]{ "1", "desc", null },
        };
        dt.setData( DataUtilities.makeDataLists( data ) );

        drl = GuidedDTDRLPersistence.getInstance().marshal( dt );

        assertTrue( drl.indexOf( "$c : CheeseLover( favouriteCheese == \"cheddar\" )" ) == -1 );

        //Without provided getValue() #2
        data = new String[][]{
                new String[]{ "1", "desc", "" },
        };
        dt.setData( DataUtilities.makeDataLists( data ) );

        drl = GuidedDTDRLPersistence.getInstance().marshal( dt );

        assertTrue( drl.indexOf( "$c : CheeseLover( favouriteCheese == \"cheddar\" )" ) == -1 );

    }

    @Test
    public void testLimitedEntryAttributes() {
        GuidedDecisionTable52 dt = new GuidedDecisionTable52();
        dt.setTableFormat( GuidedDecisionTable52.TableFormat.LIMITED_ENTRY );
        dt.setTableName( "limited-entry" );

        AttributeCol52 attr = new AttributeCol52();
        attr.setAttribute( "salience" );
        dt.getAttributeCols().add( attr );

        dt.setData( DataUtilities.makeDataLists( new String[][]{
                new String[]{ "1", "desc", "100" },
                new String[]{ "2", "desc", "200" }
        } ) );

        GuidedDTDRLPersistence p = GuidedDTDRLPersistence.getInstance();
        String drl = p.marshal( dt );

        assertTrue( drl.indexOf( "salience 100" ) > -1 );
        assertTrue( drl.indexOf( "salience 200" ) > -1 );

    }

    @Test
    public void testLimitedEntryMetadata() {
        GuidedDecisionTable52 dt = new GuidedDecisionTable52();
        dt.setTableFormat( GuidedDecisionTable52.TableFormat.LIMITED_ENTRY );
        dt.setTableName( "limited-entry" );

        MetadataCol52 md = new MetadataCol52();
        md.setMetadata( "metadata" );
        dt.getMetadataCols().add( md );

        dt.setData( DataUtilities.makeDataLists( new String[][]{
                new String[]{ "1", "desc", "md1" },
                new String[]{ "2", "desc", "md2" }
        } ) );

        GuidedDTDRLPersistence p = GuidedDTDRLPersistence.getInstance();
        String drl = p.marshal( dt );

        assertTrue( drl.indexOf( "@metadata(md1)" ) > -1 );
        assertTrue( drl.indexOf( "@metadata(md2)" ) > -1 );

    }

    @Test
    public void testLimitedEntryConditionsNoConstraints() {
        GuidedDecisionTable52 dt = new GuidedDecisionTable52();
        dt.setTableFormat( GuidedDecisionTable52.TableFormat.LIMITED_ENTRY );
        dt.setTableName( "limited-entry" );

        Pattern52 p1 = new Pattern52();
        p1.setBoundName( "p1" );
        p1.setFactType( "Smurf" );
        dt.getConditions().add( p1 );

        // This is a hack consistent with how the Expanded Form decision table 
        // works. I wouldn't be too surprised if this changes at some time, but 
        // GuidedDTDRLPersistence.marshal does not support empty patterns at
        // present.
        LimitedEntryConditionCol52 cc1 = new LimitedEntryConditionCol52();
        cc1.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        cc1.setValue( new DTCellValue52( "y" ) );
        p1.getChildColumns().add( cc1 );

        dt.setData( DataUtilities.makeDataLists( new Object[][]{
                new Object[]{ 1l, "desc", true },
                new Object[]{ 2l, "desc", false }
        } ) );

        GuidedDTDRLPersistence p = GuidedDTDRLPersistence.getInstance();
        String drl = p.marshal( dt );

        int index = -1;
        index = drl.indexOf( "Smurf( )" );
        assertTrue( index > -1 );

        index = drl.indexOf( "Smurf( )",
                             index + 1 );
        assertFalse( index > -1 );

    }

    @Test
    public void testLimitedEntryConditionsConstraints1() {
        GuidedDecisionTable52 dt = new GuidedDecisionTable52();
        dt.setTableFormat( GuidedDecisionTable52.TableFormat.LIMITED_ENTRY );
        dt.setTableName( "limited-entry" );

        Pattern52 p1 = new Pattern52();
        p1.setBoundName( "p1" );
        p1.setFactType( "Smurf" );
        dt.getConditions().add( p1 );

        LimitedEntryConditionCol52 cc1 = new LimitedEntryConditionCol52();
        cc1.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        cc1.setFieldType( DataType.TYPE_STRING );
        cc1.setFactField( "name" );
        cc1.setOperator( "==" );
        cc1.setValue( new DTCellValue52( "Pupa" ) );
        p1.getChildColumns().add( cc1 );

        dt.setData( DataUtilities.makeDataLists( new Object[][]{
                new Object[]{ 1l, "desc", true },
                new Object[]{ 2l, "desc", false }
        } ) );

        GuidedDTDRLPersistence p = GuidedDTDRLPersistence.getInstance();
        String drl = p.marshal( dt );

        int index = -1;
        index = drl.indexOf( "Smurf( name == \"Pupa\" )" );
        assertTrue( index > -1 );

        index = drl.indexOf( "Smurf( name == \"Pupa\" )",
                             index + 1 );
        assertFalse( index > -1 );

    }

    @Test
    public void testLimitedEntryConditionsConstraints2() {
        GuidedDecisionTable52 dt = new GuidedDecisionTable52();
        dt.setTableFormat( GuidedDecisionTable52.TableFormat.LIMITED_ENTRY );
        dt.setTableName( "limited-entry" );

        Pattern52 p1 = new Pattern52();
        p1.setBoundName( "p1" );
        p1.setFactType( "Smurf" );
        dt.getConditions().add( p1 );

        LimitedEntryConditionCol52 cc1 = new LimitedEntryConditionCol52();
        cc1.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        cc1.setFieldType( DataType.TYPE_STRING );
        cc1.setFactField( "name" );
        cc1.setOperator( "==" );
        cc1.setValue( new DTCellValue52( "Pupa" ) );
        p1.getChildColumns().add( cc1 );

        LimitedEntryConditionCol52 cc2 = new LimitedEntryConditionCol52();
        cc2.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        cc2.setFieldType( DataType.TYPE_STRING );
        cc2.setFactField( "name" );
        cc2.setOperator( "==" );
        cc2.setValue( new DTCellValue52( "Smurfette" ) );
        p1.getChildColumns().add( cc2 );

        LimitedEntryConditionCol52 cc3 = new LimitedEntryConditionCol52();
        cc3.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        cc3.setFieldType( DataType.TYPE_STRING );
        cc3.setFactField( "colour" );
        cc3.setOperator( "==" );
        cc3.setValue( new DTCellValue52( "Blue" ) );
        p1.getChildColumns().add( cc3 );

        dt.setData( DataUtilities.makeDataLists( new Object[][]{
                new Object[]{ 1l, "desc", true, false, true },
                new Object[]{ 2l, "desc", false, true, true },
                new Object[]{ 3l, "desc", false, false, true }
        } ) );

        GuidedDTDRLPersistence p = GuidedDTDRLPersistence.getInstance();
        String drl = p.marshal( dt );

        int index = -1;
        index = drl.indexOf( "Smurf( name == \"Pupa\" , colour == \"Blue\" )" );
        assertTrue( index > -1 );

        index = drl.indexOf( "Smurf( name == \"Smurfette\" , colour == \"Blue\" )",
                             index + 1 );
        assertTrue( index > -1 );

        index = drl.indexOf( "Smurf( colour == \"Blue\" )",
                             index + 1 );
        assertTrue( index > -1 );

    }

    @Test
    public void testLimitedEntryActionSet() {
        GuidedDecisionTable52 dt = new GuidedDecisionTable52();
        dt.setTableFormat( GuidedDecisionTable52.TableFormat.LIMITED_ENTRY );
        dt.setTableName( "limited-entry" );

        Pattern52 p1 = new Pattern52();
        p1.setBoundName( "p1" );
        p1.setFactType( "Smurf" );
        dt.getConditions().add( p1 );

        LimitedEntryConditionCol52 cc1 = new LimitedEntryConditionCol52();
        cc1.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        cc1.setFieldType( DataType.TYPE_BOOLEAN );
        cc1.setFactField( "isSmurf" );
        cc1.setOperator( "==" );
        cc1.setValue( new DTCellValue52( "true" ) );
        p1.getChildColumns().add( cc1 );

        LimitedEntryActionSetFieldCol52 asf1 = new LimitedEntryActionSetFieldCol52();
        asf1.setBoundName( "p1" );
        asf1.setFactField( "colour" );
        asf1.setValue( new DTCellValue52( "Blue" ) );

        dt.getActionCols().add( asf1 );

        dt.setData( DataUtilities.makeDataLists( new Object[][]{
                new Object[]{ 1l, "desc", true, true },
                new Object[]{ 2l, "desc", true, false }
        } ) );

        GuidedDTDRLPersistence p = GuidedDTDRLPersistence.getInstance();
        String drl = p.marshal( dt );

        int index = -1;
        index = drl.indexOf( "Smurf( isSmurf == true )" );
        assertTrue( index > -1 );
        index = drl.indexOf( "p1.setColour( \"Blue\" )",
                             index + 1 );
        assertTrue( index > -1 );

        index = drl.indexOf( "Smurf( isSmurf == true )",
                             index + 1 );
        assertTrue( index > -1 );
        index = drl.indexOf( "p1.setColour( \"Blue\" )",
                             index + 1 );
        assertFalse( index > -1 );

    }

    @Test
    public void testLimitedEntryActionInsert() {
        GuidedDecisionTable52 dt = new GuidedDecisionTable52();
        dt.setTableFormat( GuidedDecisionTable52.TableFormat.LIMITED_ENTRY );
        dt.setTableName( "limited-entry" );

        Pattern52 p1 = new Pattern52();
        p1.setBoundName( "p1" );
        p1.setFactType( "Smurf" );
        dt.getConditions().add( p1 );

        LimitedEntryActionInsertFactCol52 asf1 = new LimitedEntryActionInsertFactCol52();
        asf1.setFactType( "Smurf" );
        asf1.setBoundName( "s1" );
        asf1.setFactField( "colour" );
        asf1.setValue( new DTCellValue52( "Blue" ) );

        dt.getActionCols().add( asf1 );

        dt.setData( DataUtilities.makeDataLists( new Object[][]{
                new Object[]{ 1l, "desc", true },
                new Object[]{ 2l, "desc", false }
        } ) );

        GuidedDTDRLPersistence p = GuidedDTDRLPersistence.getInstance();
        String drl = p.marshal( dt );

        int index = -1;
        index = drl.indexOf( "Smurf s1 = new Smurf();" );
        assertTrue( index > -1 );
        index = drl.indexOf( "s1.setColour( \"Blue\" );",
                             index + 1 );
        assertTrue( index > -1 );
        index = drl.indexOf( "insert( s1 );",
                             index + 1 );
        assertTrue( index > -1 );

        int indexRule2 = index;
        indexRule2 = drl.indexOf( "Smurf s1 = new Smurf();",
                                  index + 1 );
        assertFalse( indexRule2 > -1 );
        indexRule2 = drl.indexOf( "s1.setColour( \"Blue\" );",
                                  index + 1 );
        assertFalse( indexRule2 > -1 );
        indexRule2 = drl.indexOf( "insert(s1 );",
                                  index + 1 );
        assertFalse( indexRule2 > -1 );
    }

    @Test
    public void testLHSIsNullOperator() {
        GuidedDecisionTable52 dt = new GuidedDecisionTable52();
        dt.setTableFormat( GuidedDecisionTable52.TableFormat.EXTENDED_ENTRY );
        dt.setTableName( "extended-entry" );

        Pattern52 p1 = new Pattern52();
        p1.setBoundName( "p1" );
        p1.setFactType( "Smurf" );
        dt.getConditions().add( p1 );

        ConditionCol52 cc1 = new ConditionCol52();
        cc1.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        cc1.setFieldType( DataType.TYPE_STRING );
        cc1.setFactField( "name" );
        cc1.setOperator( "== null" );
        p1.getChildColumns().add( cc1 );

        ConditionCol52 cc2 = new ConditionCol52();
        cc2.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        cc2.setFieldType( DataType.TYPE_NUMERIC_INTEGER );
        cc2.setFactField( "age" );
        cc2.setOperator( "== null" );
        p1.getChildColumns().add( cc2 );

        ConditionCol52 cc3 = new ConditionCol52();
        cc3.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        cc3.setFieldType( DataType.TYPE_DATE );
        cc3.setFactField( "dateOfBirth" );
        cc3.setOperator( "== null" );
        p1.getChildColumns().add( cc3 );

        dt.setData( DataUtilities.makeDataLists( new Object[][]{
                new Object[]{ 1l, "desc", true, true, true },
                new Object[]{ 2l, "desc", false, false, false }
        } ) );

        GuidedDTDRLPersistence p = GuidedDTDRLPersistence.getInstance();
        String drl = p.marshal( dt );

        int index = -1;
        index = drl.indexOf( "Smurf( name == null , age == null , dateOfBirth == null )" );
        assertTrue( index > -1 );

        index = drl.indexOf( "Smurf( )",
                             index + 1 );
        assertFalse( index > -1 );
    }

    @Test
    public void testLHSIsNullOperatorWithNullValues() {
        GuidedDecisionTable52 dt = new GuidedDecisionTable52();
        dt.setTableFormat( GuidedDecisionTable52.TableFormat.EXTENDED_ENTRY );
        dt.setTableName( "extended-entry" );

        Pattern52 p1 = new Pattern52();
        p1.setBoundName( "p1" );
        p1.setFactType( "Smurf" );
        dt.getConditions().add( p1 );

        ConditionCol52 cc1 = new ConditionCol52();
        cc1.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        cc1.setFieldType( DataType.TYPE_STRING );
        cc1.setFactField( "name" );
        cc1.setOperator( "== null" );
        p1.getChildColumns().add( cc1 );

        ConditionCol52 cc2 = new ConditionCol52();
        cc2.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        cc2.setFieldType( DataType.TYPE_NUMERIC_INTEGER );
        cc2.setFactField( "age" );
        cc2.setOperator( "== null" );
        p1.getChildColumns().add( cc2 );

        ConditionCol52 cc3 = new ConditionCol52();
        cc3.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        cc3.setFieldType( DataType.TYPE_DATE );
        cc3.setFactField( "dateOfBirth" );
        cc3.setOperator( "== null" );
        p1.getChildColumns().add( cc3 );

        dt.setData( DataUtilities.makeDataLists( new Object[][]{
                new Object[]{ 1l, "desc", true, true, true },
                new Object[]{ 2l, "desc", null, null, null }
        } ) );

        GuidedDTDRLPersistence p = GuidedDTDRLPersistence.getInstance();
        String drl = p.marshal( dt );

        int index = -1;
        index = drl.indexOf( "Smurf( name == null , age == null , dateOfBirth == null )" );
        assertTrue( index > -1 );

        index = drl.indexOf( "Smurf( )",
                             index + 1 );
        assertFalse( index > -1 );
    }

    @Test
    public void testLHSIsNotNullOperator() {
        GuidedDecisionTable52 dt = new GuidedDecisionTable52();
        dt.setTableFormat( GuidedDecisionTable52.TableFormat.EXTENDED_ENTRY );
        dt.setTableName( "extended-entry" );

        Pattern52 p1 = new Pattern52();
        p1.setBoundName( "p1" );
        p1.setFactType( "Smurf" );
        dt.getConditions().add( p1 );

        ConditionCol52 cc1 = new ConditionCol52();
        cc1.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        cc1.setFieldType( DataType.TYPE_STRING );
        cc1.setFactField( "name" );
        cc1.setOperator( "!= null" );
        p1.getChildColumns().add( cc1 );

        ConditionCol52 cc2 = new ConditionCol52();
        cc2.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        cc2.setFieldType( DataType.TYPE_NUMERIC_INTEGER );
        cc2.setFactField( "age" );
        cc2.setOperator( "!= null" );
        p1.getChildColumns().add( cc2 );

        ConditionCol52 cc3 = new ConditionCol52();
        cc3.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        cc3.setFieldType( DataType.TYPE_DATE );
        cc3.setFactField( "dateOfBirth" );
        cc3.setOperator( "!= null" );
        p1.getChildColumns().add( cc3 );

        dt.setData( DataUtilities.makeDataLists( new Object[][]{
                new Object[]{ 1l, "desc", true, true, true },
                new Object[]{ 2l, "desc", false, false, false }
        } ) );

        GuidedDTDRLPersistence p = GuidedDTDRLPersistence.getInstance();
        String drl = p.marshal( dt );

        int index = -1;
        index = drl.indexOf( "Smurf( name != null , age != null , dateOfBirth != null )" );
        assertTrue( index > -1 );

        index = drl.indexOf( "Smurf( )",
                             index + 1 );
        assertFalse( index > -1 );
    }

    @Test
    public void testLHSIsNotNullOperatorWithNullValues() {
        GuidedDecisionTable52 dt = new GuidedDecisionTable52();
        dt.setTableFormat( GuidedDecisionTable52.TableFormat.EXTENDED_ENTRY );
        dt.setTableName( "extended-entry" );

        Pattern52 p1 = new Pattern52();
        p1.setBoundName( "p1" );
        p1.setFactType( "Smurf" );
        dt.getConditions().add( p1 );

        ConditionCol52 cc1 = new ConditionCol52();
        cc1.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        cc1.setFieldType( DataType.TYPE_STRING );
        cc1.setFactField( "name" );
        cc1.setOperator( "!= null" );
        p1.getChildColumns().add( cc1 );

        ConditionCol52 cc2 = new ConditionCol52();
        cc2.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        cc2.setFieldType( DataType.TYPE_NUMERIC_INTEGER );
        cc2.setFactField( "age" );
        cc2.setOperator( "!= null" );
        p1.getChildColumns().add( cc2 );

        ConditionCol52 cc3 = new ConditionCol52();
        cc3.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        cc3.setFieldType( DataType.TYPE_DATE );
        cc3.setFactField( "dateOfBirth" );
        cc3.setOperator( "!= null" );
        p1.getChildColumns().add( cc3 );

        dt.setData( DataUtilities.makeDataLists( new Object[][]{
                new Object[]{ 1l, "desc", true, true, true },
                new Object[]{ 2l, "desc", null, null, null }
        } ) );

        GuidedDTDRLPersistence p = GuidedDTDRLPersistence.getInstance();
        String drl = p.marshal( dt );

        int index = -1;
        index = drl.indexOf( "Smurf( name != null , age != null , dateOfBirth != null )" );
        assertTrue( index > -1 );

        index = drl.indexOf( "Smurf( )",
                             index + 1 );
        assertFalse( index > -1 );
    }

    @Test
    public void testLimitedEntryLHSIsNullOperator() {
        GuidedDecisionTable52 dt = new GuidedDecisionTable52();
        dt.setTableFormat( GuidedDecisionTable52.TableFormat.LIMITED_ENTRY );
        dt.setTableName( "limited-entry" );

        Pattern52 p1 = new Pattern52();
        p1.setBoundName( "p1" );
        p1.setFactType( "Smurf" );
        dt.getConditions().add( p1 );

        LimitedEntryConditionCol52 cc1 = new LimitedEntryConditionCol52();
        cc1.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        cc1.setFieldType( DataType.TYPE_STRING );
        cc1.setFactField( "name" );
        cc1.setOperator( "== null" );
        p1.getChildColumns().add( cc1 );

        LimitedEntryConditionCol52 cc2 = new LimitedEntryConditionCol52();
        cc2.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        cc2.setFieldType( DataType.TYPE_NUMERIC_INTEGER );
        cc2.setFactField( "age" );
        cc2.setOperator( "== null" );
        p1.getChildColumns().add( cc2 );

        LimitedEntryConditionCol52 cc3 = new LimitedEntryConditionCol52();
        cc3.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        cc3.setFieldType( DataType.TYPE_DATE );
        cc3.setFactField( "dateOfBirth" );
        cc3.setOperator( "== null" );
        p1.getChildColumns().add( cc3 );

        dt.setData( DataUtilities.makeDataLists( new Object[][]{
                new Object[]{ 1l, "desc", true, true, true },
                new Object[]{ 2l, "desc", false, false, false }
        } ) );

        GuidedDTDRLPersistence p = GuidedDTDRLPersistence.getInstance();
        String drl = p.marshal( dt );

        int index = -1;
        index = drl.indexOf( "Smurf( name == null , age == null , dateOfBirth == null )" );
        assertTrue( index > -1 );

        index = drl.indexOf( "Smurf( )",
                             index + 1 );
        assertFalse( index > -1 );
    }

    @Test
    public void testLimitedEntryLHSIsNullOperatorWithNullValues() {
        GuidedDecisionTable52 dt = new GuidedDecisionTable52();
        dt.setTableFormat( GuidedDecisionTable52.TableFormat.LIMITED_ENTRY );
        dt.setTableName( "limited-entry" );

        Pattern52 p1 = new Pattern52();
        p1.setBoundName( "p1" );
        p1.setFactType( "Smurf" );
        dt.getConditions().add( p1 );

        LimitedEntryConditionCol52 cc1 = new LimitedEntryConditionCol52();
        cc1.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        cc1.setFieldType( DataType.TYPE_STRING );
        cc1.setFactField( "name" );
        cc1.setOperator( "== null" );
        p1.getChildColumns().add( cc1 );

        LimitedEntryConditionCol52 cc2 = new LimitedEntryConditionCol52();
        cc2.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        cc2.setFieldType( DataType.TYPE_NUMERIC_INTEGER );
        cc2.setFactField( "age" );
        cc2.setOperator( "== null" );
        p1.getChildColumns().add( cc2 );

        LimitedEntryConditionCol52 cc3 = new LimitedEntryConditionCol52();
        cc3.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        cc3.setFieldType( DataType.TYPE_DATE );
        cc3.setFactField( "dateOfBirth" );
        cc3.setOperator( "== null" );
        p1.getChildColumns().add( cc3 );

        dt.setData( DataUtilities.makeDataLists( new Object[][]{
                new Object[]{ 1l, "desc", true, true, true },
                new Object[]{ 2l, "desc", null, null, null }
        } ) );

        GuidedDTDRLPersistence p = GuidedDTDRLPersistence.getInstance();
        String drl = p.marshal( dt );

        int index = -1;
        index = drl.indexOf( "Smurf( name == null , age == null , dateOfBirth == null )" );
        assertTrue( index > -1 );

        index = drl.indexOf( "Smurf( )",
                             index + 1 );
        assertFalse( index > -1 );
    }

    @Test
    public void testLimitedEntryLHSIsNotNullOperator() {
        GuidedDecisionTable52 dt = new GuidedDecisionTable52();
        dt.setTableFormat( GuidedDecisionTable52.TableFormat.LIMITED_ENTRY );
        dt.setTableName( "limited-entry" );

        Pattern52 p1 = new Pattern52();
        p1.setBoundName( "p1" );
        p1.setFactType( "Smurf" );
        dt.getConditions().add( p1 );

        LimitedEntryConditionCol52 cc1 = new LimitedEntryConditionCol52();
        cc1.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        cc1.setFieldType( DataType.TYPE_STRING );
        cc1.setFactField( "name" );
        cc1.setOperator( "!= null" );
        p1.getChildColumns().add( cc1 );

        LimitedEntryConditionCol52 cc2 = new LimitedEntryConditionCol52();
        cc2.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        cc2.setFieldType( DataType.TYPE_NUMERIC_INTEGER );
        cc2.setFactField( "age" );
        cc2.setOperator( "!= null" );
        p1.getChildColumns().add( cc2 );

        LimitedEntryConditionCol52 cc3 = new LimitedEntryConditionCol52();
        cc3.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        cc3.setFieldType( DataType.TYPE_DATE );
        cc3.setFactField( "dateOfBirth" );
        cc3.setOperator( "!= null" );
        p1.getChildColumns().add( cc3 );

        dt.setData( DataUtilities.makeDataLists( new Object[][]{
                new Object[]{ 1l, "desc", true, true, true },
                new Object[]{ 2l, "desc", false, false, false }
        } ) );

        GuidedDTDRLPersistence p = GuidedDTDRLPersistence.getInstance();
        String drl = p.marshal( dt );

        int index = -1;
        index = drl.indexOf( "Smurf( name != null , age != null , dateOfBirth != null )" );
        assertTrue( index > -1 );

        index = drl.indexOf( "Smurf( )",
                             index + 1 );
        assertFalse( index > -1 );
    }

    @Test
    public void testLimitedEntryLHSIsNotNullOperatorWithNullValues() {
        GuidedDecisionTable52 dt = new GuidedDecisionTable52();
        dt.setTableFormat( GuidedDecisionTable52.TableFormat.LIMITED_ENTRY );
        dt.setTableName( "limited-entry" );

        Pattern52 p1 = new Pattern52();
        p1.setBoundName( "p1" );
        p1.setFactType( "Smurf" );
        dt.getConditions().add( p1 );

        LimitedEntryConditionCol52 cc1 = new LimitedEntryConditionCol52();
        cc1.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        cc1.setFieldType( DataType.TYPE_STRING );
        cc1.setFactField( "name" );
        cc1.setOperator( "!= null" );
        p1.getChildColumns().add( cc1 );

        LimitedEntryConditionCol52 cc2 = new LimitedEntryConditionCol52();
        cc2.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        cc2.setFieldType( DataType.TYPE_NUMERIC_INTEGER );
        cc2.setFactField( "age" );
        cc2.setOperator( "!= null" );
        p1.getChildColumns().add( cc2 );

        LimitedEntryConditionCol52 cc3 = new LimitedEntryConditionCol52();
        cc3.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        cc3.setFieldType( DataType.TYPE_DATE );
        cc3.setFactField( "dateOfBirth" );
        cc3.setOperator( "!= null" );
        p1.getChildColumns().add( cc3 );

        dt.setData( DataUtilities.makeDataLists( new Object[][]{
                new Object[]{ 1l, "desc", true, true, true },
                new Object[]{ 2l, "desc", null, null, null }
        } ) );

        GuidedDTDRLPersistence p = GuidedDTDRLPersistence.getInstance();
        String drl = p.marshal( dt );

        int index = -1;
        index = drl.indexOf( "Smurf( name != null , age != null , dateOfBirth != null )" );
        assertTrue( index > -1 );

        index = drl.indexOf( "Smurf( )",
                             index + 1 );
        assertFalse( index > -1 );
    }

    @Test
    public void testLHSInOperator() {
        GuidedDecisionTable52 dt = new GuidedDecisionTable52();
        dt.setTableFormat( GuidedDecisionTable52.TableFormat.EXTENDED_ENTRY );
        dt.setTableName( "extended-entry" );

        Pattern52 p1 = new Pattern52();
        p1.setBoundName( "p1" );
        p1.setFactType( "Smurf" );
        dt.getConditions().add( p1 );

        ConditionCol52 cc1 = new ConditionCol52();
        cc1.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        cc1.setFieldType( DataType.TYPE_STRING );
        cc1.setFactField( "name" );
        cc1.setOperator( "in" );
        p1.getChildColumns().add( cc1 );

        ConditionCol52 cc2 = new ConditionCol52();
        cc2.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        cc2.setFieldType( DataType.TYPE_NUMERIC_INTEGER );
        cc2.setFactField( "age" );
        cc2.setOperator( "in" );
        p1.getChildColumns().add( cc2 );

        dt.setData( DataUtilities.makeDataLists( new Object[][]{
                new Object[]{ 1l, "desc", "Pupa, Brains", "55, 66" },
                new Object[]{ 2l, "desc", "", "" }
        } ) );

        GuidedDTDRLPersistence p = GuidedDTDRLPersistence.getInstance();
        String drl = p.marshal( dt );

        int index = -1;
        index = drl.indexOf( "Smurf( name in ( \"Pupa\", \"Brains\" ) , age in ( 55, 66 ) )" );
        assertTrue( index > -1 );

        index = drl.indexOf( "Smurf( )",
                             index + 1 );
        assertFalse( index > -1 );
    }

    @Test
    public void testLHSNotInOperator() {
        GuidedDecisionTable52 dt = new GuidedDecisionTable52();
        dt.setTableFormat( GuidedDecisionTable52.TableFormat.EXTENDED_ENTRY );
        dt.setTableName( "extended-entry" );

        Pattern52 p1 = new Pattern52();
        p1.setBoundName( "p1" );
        p1.setFactType( "Smurf" );
        dt.getConditions().add( p1 );

        ConditionCol52 cc1 = new ConditionCol52();
        cc1.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        cc1.setFieldType( DataType.TYPE_STRING );
        cc1.setFactField( "name" );
        cc1.setOperator( "not in" );
        p1.getChildColumns().add( cc1 );

        ConditionCol52 cc2 = new ConditionCol52();
        cc2.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        cc2.setFieldType( DataType.TYPE_NUMERIC_INTEGER );
        cc2.setFactField( "age" );
        cc2.setOperator( "not in" );
        p1.getChildColumns().add( cc2 );

        dt.setData( DataUtilities.makeDataLists( new Object[][]{
                new Object[]{ 1l, "desc", "Pupa, Brains", "55, 66" },
                new Object[]{ 2l, "desc", "", "" }
        } ) );

        GuidedDTDRLPersistence p = GuidedDTDRLPersistence.getInstance();
        String drl = p.marshal( dt );

        int index = -1;
        index = drl.indexOf( "Smurf( name not in ( \"Pupa\", \"Brains\" ) , age not in ( 55, 66 ) )" );
        assertTrue( index > -1 );

        index = drl.indexOf( "Smurf( )",
                             index + 1 );
        assertFalse( index > -1 );
    }

    @Test
    public void testLimitedEntryLHSInOperator() {
        GuidedDecisionTable52 dt = new GuidedDecisionTable52();
        dt.setTableFormat( GuidedDecisionTable52.TableFormat.LIMITED_ENTRY );
        dt.setTableName( "limited-entry" );

        Pattern52 p1 = new Pattern52();
        p1.setBoundName( "p1" );
        p1.setFactType( "Smurf" );
        dt.getConditions().add( p1 );

        LimitedEntryConditionCol52 cc1 = new LimitedEntryConditionCol52();
        cc1.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        cc1.setFieldType( DataType.TYPE_STRING );
        cc1.setFactField( "name" );
        cc1.setOperator( "in" );
        cc1.setValue( new DTCellValue52( "Pupa, Brains" ) );
        p1.getChildColumns().add( cc1 );

        LimitedEntryConditionCol52 cc2 = new LimitedEntryConditionCol52();
        cc2.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        cc2.setFieldType( DataType.TYPE_NUMERIC_INTEGER );
        cc2.setFactField( "age" );
        cc2.setOperator( "in" );
        cc2.setValue( new DTCellValue52( "55, 66" ) );
        p1.getChildColumns().add( cc2 );

        dt.setData( DataUtilities.makeDataLists( new Object[][]{
                new Object[]{ 1l, "desc", true, true },
                new Object[]{ 2l, "desc", false, false }
        } ) );

        GuidedDTDRLPersistence p = GuidedDTDRLPersistence.getInstance();
        String drl = p.marshal( dt );

        int index = -1;
        index = drl.indexOf( "Smurf( name in ( \"Pupa\", \"Brains\" ) , age in ( 55, 66 ) )" );
        assertTrue( index > -1 );

        index = drl.indexOf( "Smurf( )",
                             index + 1 );
        assertFalse( index > -1 );
    }

    @Test
    public void testLimitedEntryLHSNotInOperator() {
        GuidedDecisionTable52 dt = new GuidedDecisionTable52();
        dt.setTableFormat( GuidedDecisionTable52.TableFormat.LIMITED_ENTRY );
        dt.setTableName( "limited-entry" );

        Pattern52 p1 = new Pattern52();
        p1.setBoundName( "p1" );
        p1.setFactType( "Smurf" );
        dt.getConditions().add( p1 );

        LimitedEntryConditionCol52 cc1 = new LimitedEntryConditionCol52();
        cc1.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        cc1.setFieldType( DataType.TYPE_STRING );
        cc1.setFactField( "name" );
        cc1.setOperator( "not in" );
        cc1.setValue( new DTCellValue52( "Pupa, Brains" ) );
        p1.getChildColumns().add( cc1 );

        LimitedEntryConditionCol52 cc2 = new LimitedEntryConditionCol52();
        cc2.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        cc2.setFieldType( DataType.TYPE_NUMERIC_INTEGER );
        cc2.setFactField( "age" );
        cc2.setOperator( "not in" );
        cc2.setValue( new DTCellValue52( "55, 66" ) );
        p1.getChildColumns().add( cc2 );

        dt.setData( DataUtilities.makeDataLists( new Object[][]{
                new Object[]{ 1l, "desc", true, true },
                new Object[]{ 2l, "desc", false, false }
        } ) );

        GuidedDTDRLPersistence p = GuidedDTDRLPersistence.getInstance();
        String drl = p.marshal( dt );

        int index = -1;
        index = drl.indexOf( "Smurf( name not in ( \"Pupa\", \"Brains\" ) , age not in ( 55, 66 ) )" );
        assertTrue( index > -1 );

        index = drl.indexOf( "Smurf( )",
                             index + 1 );
        assertFalse( index > -1 );
    }

    @Test
    public void testRHSExecuteWorkItem() {
        GuidedDTDRLPersistence p = new GuidedDTDRLPersistence();
        String[] row = new String[]{ "1", "desc", "true" };

        List<BaseColumn> allColumns = new ArrayList<BaseColumn>();
        allColumns.add( new RowNumberCol52() );
        allColumns.add( new DescriptionCol52() );
        List<ActionCol52> cols = new ArrayList<ActionCol52>();

        ActionWorkItemCol52 awi = new ActionWorkItemCol52();
        PortableWorkDefinition pwd = new PortableWorkDefinition();
        pwd.setName( "work-item" );
        awi.setWorkItemDefinition( pwd );

        PortableBooleanParameterDefinition p1 = new PortableBooleanParameterDefinition();
        p1.setName( "BooleanParameter" );
        p1.setValue( Boolean.TRUE );
        pwd.addParameter( p1 );

        PortableFloatParameterDefinition p2 = new PortableFloatParameterDefinition();
        p2.setName( "FloatParameter" );
        p2.setValue( 123.456f );
        pwd.addParameter( p2 );

        PortableIntegerParameterDefinition p3 = new PortableIntegerParameterDefinition();
        p3.setName( "IntegerParameter" );
        p3.setValue( 123 );
        pwd.addParameter( p3 );

        PortableStringParameterDefinition p4 = new PortableStringParameterDefinition();
        p4.setName( "StringParameter" );
        p4.setValue( "hello" );
        pwd.addParameter( p4 );

        cols.add( awi );

        RuleModel rm = new RuleModel();
        allColumns.addAll( cols );

        List<DTCellValue52> rowData = DataUtilities.makeDataRowList( row );
        TemplateDataProvider rowDataProvider = new GuidedDTTemplateDataProvider( allColumns,
                                                                                 rowData );

        p.doActions( allColumns,
                     cols,
                     rowDataProvider,
                     rowData,
                     rm );
        assertEquals( 1,
                      rm.rhs.length );

        //Examine RuleModel action
        ActionExecuteWorkItem aw = (ActionExecuteWorkItem) rm.rhs[ 0 ];
        assertNotNull( aw );

        PortableWorkDefinition mpwd = aw.getWorkDefinition();
        assertNotNull( mpwd );

        assertEquals( 4,
                      mpwd.getParameters().size() );

        PortableBooleanParameterDefinition mp1 = (PortableBooleanParameterDefinition) mpwd.getParameter( "BooleanParameter" );
        assertNotNull( mp1 );
        assertEquals( Boolean.TRUE,
                      mp1.getValue() );

        PortableFloatParameterDefinition mp2 = (PortableFloatParameterDefinition) mpwd.getParameter( "FloatParameter" );
        assertNotNull( mp2 );
        assertEquals( new Float( 123.456f ),
                      mp2.getValue() );

        PortableIntegerParameterDefinition mp3 = (PortableIntegerParameterDefinition) mpwd.getParameter( "IntegerParameter" );
        assertNotNull( mp3 );
        assertEquals( new Integer( 123 ),
                      mp3.getValue() );

        PortableStringParameterDefinition mp4 = (PortableStringParameterDefinition) mpwd.getParameter( "StringParameter" );
        assertNotNull( mp4 );
        assertEquals( "hello",
                      mp4.getValue() );

    }

    @Test
    public void testRHSExecuteWorkItemWithBindings() {
        GuidedDTDRLPersistence p = new GuidedDTDRLPersistence();
        String[] row = new String[]{ "1", "desc", "true" };

        List<BaseColumn> allColumns = new ArrayList<BaseColumn>();
        allColumns.add( new RowNumberCol52() );
        allColumns.add( new DescriptionCol52() );
        List<ActionCol52> cols = new ArrayList<ActionCol52>();

        ActionWorkItemCol52 awi = new ActionWorkItemCol52();
        PortableWorkDefinition pwd = new PortableWorkDefinition();
        pwd.setName( "work-item" );
        awi.setWorkItemDefinition( pwd );

        PortableBooleanParameterDefinition p1 = new PortableBooleanParameterDefinition();
        p1.setName( "BooleanParameter" );
        p1.setValue( Boolean.TRUE );
        p1.setBinding( "$b" );
        pwd.addParameter( p1 );

        PortableFloatParameterDefinition p2 = new PortableFloatParameterDefinition();
        p2.setName( "FloatParameter" );
        p2.setValue( 123.456f );
        p2.setBinding( "$f" );
        pwd.addParameter( p2 );

        PortableIntegerParameterDefinition p3 = new PortableIntegerParameterDefinition();
        p3.setName( "IntegerParameter" );
        p3.setValue( 123 );
        p3.setBinding( "$i" );
        pwd.addParameter( p3 );

        PortableStringParameterDefinition p4 = new PortableStringParameterDefinition();
        p4.setName( "StringParameter" );
        p4.setValue( "hello" );
        p4.setBinding( "$s" );
        pwd.addParameter( p4 );

        cols.add( awi );

        RuleModel rm = new RuleModel();
        allColumns.addAll( cols );

        List<DTCellValue52> rowData = DataUtilities.makeDataRowList( row );
        TemplateDataProvider rowDataProvider = new GuidedDTTemplateDataProvider( allColumns,
                                                                                 rowData );

        p.doActions( allColumns,
                     cols,
                     rowDataProvider,
                     rowData,
                     rm );
        assertEquals( 1,
                      rm.rhs.length );

        //Examine RuleModel action
        ActionExecuteWorkItem aw = (ActionExecuteWorkItem) rm.rhs[ 0 ];
        assertNotNull( aw );

        PortableWorkDefinition mpwd = aw.getWorkDefinition();
        assertNotNull( mpwd );

        assertEquals( 4,
                      mpwd.getParameters().size() );

        PortableBooleanParameterDefinition mp1 = (PortableBooleanParameterDefinition) mpwd.getParameter( "BooleanParameter" );
        assertNotNull( mp1 );
        assertEquals( Boolean.TRUE,
                      mp1.getValue() );
        assertEquals( "$b",
                      mp1.getBinding() );

        PortableFloatParameterDefinition mp2 = (PortableFloatParameterDefinition) mpwd.getParameter( "FloatParameter" );
        assertNotNull( mp2 );
        assertEquals( new Float( 123.456f ),
                      mp2.getValue() );
        assertEquals( "$f",
                      mp2.getBinding() );

        PortableIntegerParameterDefinition mp3 = (PortableIntegerParameterDefinition) mpwd.getParameter( "IntegerParameter" );
        assertNotNull( mp3 );
        assertEquals( new Integer( 123 ),
                      mp3.getValue() );
        assertEquals( "$i",
                      mp3.getBinding() );

        PortableStringParameterDefinition mp4 = (PortableStringParameterDefinition) mpwd.getParameter( "StringParameter" );
        assertNotNull( mp4 );
        assertEquals( "hello",
                      mp4.getValue() );
        assertEquals( "$s",
                      mp4.getBinding() );

    }

    @Test
    //Test all Actions setting fields are correctly converted to RuleModel
    public void testRHSActionWorkItemSetFields1() {
        GuidedDTDRLPersistence p = new GuidedDTDRLPersistence();
        String[] row = new String[]{ "1", "desc", "true", "true", "true", "true", "true" };

        List<BaseColumn> allColumns = new ArrayList<BaseColumn>();
        allColumns.add( new RowNumberCol52() );
        allColumns.add( new DescriptionCol52() );
        List<ActionCol52> cols = new ArrayList<ActionCol52>();

        ActionWorkItemCol52 awi = new ActionWorkItemCol52();
        PortableWorkDefinition pwd = new PortableWorkDefinition();
        pwd.setName( "WorkItem" );
        awi.setWorkItemDefinition( pwd );

        PortableBooleanParameterDefinition p1 = new PortableBooleanParameterDefinition();
        p1.setName( "BooleanResult" );
        pwd.addResult( p1 );

        PortableFloatParameterDefinition p2 = new PortableFloatParameterDefinition();
        p2.setName( "FloatResult" );
        pwd.addResult( p2 );

        PortableIntegerParameterDefinition p3 = new PortableIntegerParameterDefinition();
        p3.setName( "IntegerResult" );
        pwd.addResult( p3 );

        PortableStringParameterDefinition p4 = new PortableStringParameterDefinition();
        p4.setName( "StringResult" );
        pwd.addResult( p4 );

        cols.add( awi );

        ActionWorkItemSetFieldCol52 asf1 = new ActionWorkItemSetFieldCol52();
        asf1.setBoundName( "$r" );
        asf1.setFactField( "ResultBooleanField" );
        asf1.setType( DataType.TYPE_BOOLEAN );
        asf1.setWorkItemName( "WorkItem" );
        asf1.setWorkItemResultParameterName( "BooleanResult" );
        asf1.setParameterClassName( Boolean.class.getName() );
        cols.add( asf1 );

        ActionWorkItemSetFieldCol52 asf2 = new ActionWorkItemSetFieldCol52();
        asf2.setBoundName( "$r" );
        asf2.setFactField( "ResultFloatField" );
        asf2.setType( DataType.TYPE_NUMERIC_FLOAT );
        asf2.setWorkItemName( "WorkItem" );
        asf2.setWorkItemResultParameterName( "FloatResult" );
        asf2.setParameterClassName( Float.class.getName() );
        cols.add( asf2 );

        ActionWorkItemSetFieldCol52 asf3 = new ActionWorkItemSetFieldCol52();
        asf3.setBoundName( "$r" );
        asf3.setFactField( "ResultIntegerField" );
        asf3.setType( DataType.TYPE_NUMERIC_INTEGER );
        asf3.setWorkItemName( "WorkItem" );
        asf3.setWorkItemResultParameterName( "IntegerResult" );
        asf3.setParameterClassName( Integer.class.getName() );
        cols.add( asf3 );

        ActionWorkItemSetFieldCol52 asf4 = new ActionWorkItemSetFieldCol52();
        asf4.setBoundName( "$r" );
        asf4.setFactField( "ResultStringField" );
        asf4.setType( DataType.TYPE_STRING );
        asf4.setWorkItemName( "WorkItem" );
        asf4.setWorkItemResultParameterName( "StringResult" );
        asf4.setParameterClassName( String.class.getName() );
        cols.add( asf4 );

        RuleModel rm = new RuleModel();
        allColumns.addAll( cols );

        List<DTCellValue52> rowData = DataUtilities.makeDataRowList( row );
        TemplateDataProvider rowDataProvider = new GuidedDTTemplateDataProvider( allColumns,
                                                                                 rowData );

        p.doActions( allColumns,
                     cols,
                     rowDataProvider,
                     rowData,
                     rm );
        assertEquals( 2,
                      rm.rhs.length );

        //Examine RuleModel actions
        ActionExecuteWorkItem aw = (ActionExecuteWorkItem) rm.rhs[ 0 ];
        assertNotNull( aw );

        ActionSetField asf = (ActionSetField) rm.rhs[ 1 ];
        assertNotNull( asf );

        //Check ActionExecuteWorkItem
        PortableWorkDefinition mpwd = aw.getWorkDefinition();
        assertNotNull( mpwd );

        assertEquals( 4,
                      mpwd.getResults().size() );

        PortableBooleanParameterDefinition mp1 = (PortableBooleanParameterDefinition) mpwd.getResult( "BooleanResult" );
        assertNotNull( mp1 );

        PortableFloatParameterDefinition mp2 = (PortableFloatParameterDefinition) mpwd.getResult( "FloatResult" );
        assertNotNull( mp2 );

        PortableIntegerParameterDefinition mp3 = (PortableIntegerParameterDefinition) mpwd.getResult( "IntegerResult" );
        assertNotNull( mp3 );

        PortableStringParameterDefinition mp4 = (PortableStringParameterDefinition) mpwd.getResult( "StringResult" );
        assertNotNull( mp4 );

        //Check ActionSetField
        assertEquals( asf.getVariable(),
                      "$r" );
        assertEquals( 4,
                      asf.getFieldValues().length );

        ActionFieldValue fv1 = asf.getFieldValues()[ 0 ];
        assertNotNull( fv1 );
        assertTrue( fv1 instanceof ActionWorkItemFieldValue );
        ActionWorkItemFieldValue wifv1 = (ActionWorkItemFieldValue) fv1;
        assertEquals( "ResultBooleanField",
                      wifv1.getField() );
        assertEquals( DataType.TYPE_BOOLEAN,
                      wifv1.getType() );
        assertEquals( "WorkItem",
                      wifv1.getWorkItemName() );
        assertEquals( "BooleanResult",
                      wifv1.getWorkItemParameterName() );
        assertEquals( Boolean.class.getName(),
                      wifv1.getWorkItemParameterClassName() );

        ActionFieldValue fv2 = asf.getFieldValues()[ 1 ];
        assertNotNull( fv2 );
        assertTrue( fv2 instanceof ActionWorkItemFieldValue );
        ActionWorkItemFieldValue wifv2 = (ActionWorkItemFieldValue) fv2;
        assertEquals( "ResultFloatField",
                      wifv2.getField() );
        assertEquals( DataType.TYPE_NUMERIC_FLOAT,
                      wifv2.getType() );
        assertEquals( "WorkItem",
                      wifv2.getWorkItemName() );
        assertEquals( "FloatResult",
                      wifv2.getWorkItemParameterName() );
        assertEquals( Float.class.getName(),
                      wifv2.getWorkItemParameterClassName() );

        ActionFieldValue fv3 = asf.getFieldValues()[ 2 ];
        assertNotNull( fv3 );
        assertTrue( fv3 instanceof ActionWorkItemFieldValue );
        ActionWorkItemFieldValue wifv3 = (ActionWorkItemFieldValue) fv3;
        assertEquals( "ResultIntegerField",
                      wifv3.getField() );
        assertEquals( DataType.TYPE_NUMERIC_INTEGER,
                      wifv3.getType() );
        assertEquals( "WorkItem",
                      wifv3.getWorkItemName() );
        assertEquals( "IntegerResult",
                      wifv3.getWorkItemParameterName() );
        assertEquals( Integer.class.getName(),
                      wifv3.getWorkItemParameterClassName() );

        ActionFieldValue fv4 = asf.getFieldValues()[ 3 ];
        assertNotNull( fv4 );
        assertTrue( fv4 instanceof ActionWorkItemFieldValue );
        ActionWorkItemFieldValue wifv4 = (ActionWorkItemFieldValue) fv4;
        assertEquals( "ResultStringField",
                      wifv4.getField() );
        assertEquals( DataType.TYPE_STRING,
                      wifv4.getType() );
        assertEquals( "WorkItem",
                      wifv4.getWorkItemName() );
        assertEquals( "StringResult",
                      wifv4.getWorkItemParameterName() );
        assertEquals( String.class.getName(),
                      wifv4.getWorkItemParameterClassName() );

    }

    @Test
    //Test only Actions set to "true" are correctly converted to RuleModel
    public void testRHSActionWorkItemSetFields2() {
        GuidedDTDRLPersistence p = new GuidedDTDRLPersistence();
        String[] row = new String[]{ "1", "desc", "true", "true", "false" };

        List<BaseColumn> allColumns = new ArrayList<BaseColumn>();
        allColumns.add( new RowNumberCol52() );
        allColumns.add( new DescriptionCol52() );
        List<ActionCol52> cols = new ArrayList<ActionCol52>();

        ActionWorkItemCol52 awi = new ActionWorkItemCol52();
        PortableWorkDefinition pwd = new PortableWorkDefinition();
        pwd.setName( "WorkItem" );
        awi.setWorkItemDefinition( pwd );

        PortableBooleanParameterDefinition p1 = new PortableBooleanParameterDefinition();
        p1.setName( "BooleanResult" );
        pwd.addResult( p1 );

        PortableFloatParameterDefinition p2 = new PortableFloatParameterDefinition();
        p2.setName( "FloatResult" );
        pwd.addResult( p2 );

        cols.add( awi );

        ActionWorkItemSetFieldCol52 asf1 = new ActionWorkItemSetFieldCol52();
        asf1.setBoundName( "$r" );
        asf1.setFactField( "ResultBooleanField" );
        asf1.setType( DataType.TYPE_BOOLEAN );
        asf1.setWorkItemName( "WorkItem" );
        asf1.setWorkItemResultParameterName( "BooleanResult" );
        asf1.setParameterClassName( Boolean.class.getName() );
        cols.add( asf1 );

        ActionWorkItemSetFieldCol52 asf2 = new ActionWorkItemSetFieldCol52();
        asf2.setBoundName( "$r" );
        asf2.setFactField( "ResultFloatField" );
        asf2.setType( DataType.TYPE_NUMERIC_FLOAT );
        asf2.setWorkItemName( "WorkItem" );
        asf2.setWorkItemResultParameterName( "FloatResult" );
        asf2.setParameterClassName( Float.class.getName() );
        cols.add( asf2 );

        RuleModel rm = new RuleModel();
        allColumns.addAll( cols );

        List<DTCellValue52> rowData = DataUtilities.makeDataRowList( row );
        TemplateDataProvider rowDataProvider = new GuidedDTTemplateDataProvider( allColumns,
                                                                                 rowData );

        p.doActions( allColumns,
                     cols,
                     rowDataProvider,
                     rowData,
                     rm );
        assertEquals( 2,
                      rm.rhs.length );

        //Examine RuleModel actions
        ActionExecuteWorkItem aw = (ActionExecuteWorkItem) rm.rhs[ 0 ];
        assertNotNull( aw );

        ActionSetField asf = (ActionSetField) rm.rhs[ 1 ];
        assertNotNull( asf );

        //Check ActionExecuteWorkItem
        PortableWorkDefinition mpwd = aw.getWorkDefinition();
        assertNotNull( mpwd );

        assertEquals( 2,
                      mpwd.getResults().size() );

        PortableBooleanParameterDefinition mp1 = (PortableBooleanParameterDefinition) mpwd.getResult( "BooleanResult" );
        assertNotNull( mp1 );

        PortableFloatParameterDefinition mp2 = (PortableFloatParameterDefinition) mpwd.getResult( "FloatResult" );
        assertNotNull( mp2 );

        //Check ActionSetField
        assertEquals( asf.getVariable(),
                      "$r" );
        assertEquals( 1,
                      asf.getFieldValues().length );

        ActionFieldValue fv1 = asf.getFieldValues()[ 0 ];
        assertNotNull( fv1 );
        assertTrue( fv1 instanceof ActionWorkItemFieldValue );
        ActionWorkItemFieldValue wifv1 = (ActionWorkItemFieldValue) fv1;
        assertEquals( "ResultBooleanField",
                      wifv1.getField() );
        assertEquals( DataType.TYPE_BOOLEAN,
                      wifv1.getType() );
        assertEquals( "WorkItem",
                      wifv1.getWorkItemName() );
        assertEquals( "BooleanResult",
                      wifv1.getWorkItemParameterName() );
        assertEquals( Boolean.class.getName(),
                      wifv1.getWorkItemParameterClassName() );

    }

    @Test
    //Test all Actions inserting Facts are correctly converted to RuleModel
    public void testRHSActionWorkItemInsertFacts1() {
        GuidedDTDRLPersistence p = new GuidedDTDRLPersistence();
        String[] row = new String[]{ "1", "desc", "true", "true", "true", "true", "true" };

        List<BaseColumn> allColumns = new ArrayList<BaseColumn>();
        allColumns.add( new RowNumberCol52() );
        allColumns.add( new DescriptionCol52() );
        List<ActionCol52> cols = new ArrayList<ActionCol52>();

        ActionWorkItemCol52 awi = new ActionWorkItemCol52();
        PortableWorkDefinition pwd = new PortableWorkDefinition();
        pwd.setName( "WorkItem" );
        awi.setWorkItemDefinition( pwd );

        PortableBooleanParameterDefinition p1 = new PortableBooleanParameterDefinition();
        p1.setName( "BooleanResult" );
        pwd.addResult( p1 );

        PortableFloatParameterDefinition p2 = new PortableFloatParameterDefinition();
        p2.setName( "FloatResult" );
        pwd.addResult( p2 );

        PortableIntegerParameterDefinition p3 = new PortableIntegerParameterDefinition();
        p3.setName( "IntegerResult" );
        pwd.addResult( p3 );

        PortableStringParameterDefinition p4 = new PortableStringParameterDefinition();
        p4.setName( "StringResult" );
        pwd.addResult( p4 );

        cols.add( awi );

        ActionWorkItemInsertFactCol52 asf1 = new ActionWorkItemInsertFactCol52();
        asf1.setBoundName( "$r" );
        asf1.setFactField( "ResultBooleanField" );
        asf1.setType( DataType.TYPE_BOOLEAN );
        asf1.setWorkItemName( "WorkItem" );
        asf1.setWorkItemResultParameterName( "BooleanResult" );
        asf1.setParameterClassName( Boolean.class.getName() );
        cols.add( asf1 );

        ActionWorkItemInsertFactCol52 asf2 = new ActionWorkItemInsertFactCol52();
        asf2.setBoundName( "$r" );
        asf2.setFactField( "ResultFloatField" );
        asf2.setType( DataType.TYPE_NUMERIC_FLOAT );
        asf2.setWorkItemName( "WorkItem" );
        asf2.setWorkItemResultParameterName( "FloatResult" );
        asf2.setParameterClassName( Float.class.getName() );
        cols.add( asf2 );

        ActionWorkItemInsertFactCol52 asf3 = new ActionWorkItemInsertFactCol52();
        asf3.setBoundName( "$r" );
        asf3.setFactField( "ResultIntegerField" );
        asf3.setType( DataType.TYPE_NUMERIC_INTEGER );
        asf3.setWorkItemName( "WorkItem" );
        asf3.setWorkItemResultParameterName( "IntegerResult" );
        asf3.setParameterClassName( Integer.class.getName() );
        cols.add( asf3 );

        ActionWorkItemInsertFactCol52 asf4 = new ActionWorkItemInsertFactCol52();
        asf4.setBoundName( "$r" );
        asf4.setFactField( "ResultStringField" );
        asf4.setType( DataType.TYPE_STRING );
        asf4.setWorkItemName( "WorkItem" );
        asf4.setWorkItemResultParameterName( "StringResult" );
        asf4.setParameterClassName( String.class.getName() );
        cols.add( asf4 );

        RuleModel rm = new RuleModel();
        allColumns.addAll( cols );

        List<DTCellValue52> rowData = DataUtilities.makeDataRowList( row );
        TemplateDataProvider rowDataProvider = new GuidedDTTemplateDataProvider( allColumns,
                                                                                 rowData );

        p.doActions( allColumns,
                     cols,
                     rowDataProvider,
                     rowData,
                     rm );
        assertEquals( 2,
                      rm.rhs.length );

        //Examine RuleModel actions
        ActionExecuteWorkItem aw = (ActionExecuteWorkItem) rm.rhs[ 0 ];
        assertNotNull( aw );

        ActionInsertFact aif = (ActionInsertFact) rm.rhs[ 1 ];
        assertNotNull( aif );

        //Check ActionExecuteWorkItem
        PortableWorkDefinition mpwd = aw.getWorkDefinition();
        assertNotNull( mpwd );

        assertEquals( 4,
                      mpwd.getResults().size() );

        PortableBooleanParameterDefinition mp1 = (PortableBooleanParameterDefinition) mpwd.getResult( "BooleanResult" );
        assertNotNull( mp1 );

        PortableFloatParameterDefinition mp2 = (PortableFloatParameterDefinition) mpwd.getResult( "FloatResult" );
        assertNotNull( mp2 );

        PortableIntegerParameterDefinition mp3 = (PortableIntegerParameterDefinition) mpwd.getResult( "IntegerResult" );
        assertNotNull( mp3 );

        PortableStringParameterDefinition mp4 = (PortableStringParameterDefinition) mpwd.getResult( "StringResult" );
        assertNotNull( mp4 );

        //Check ActionInsertFact
        assertEquals( aif.getBoundName(),
                      "$r" );
        assertEquals( 4,
                      aif.getFieldValues().length );

        ActionFieldValue fv1 = aif.getFieldValues()[ 0 ];
        assertNotNull( fv1 );
        assertTrue( fv1 instanceof ActionWorkItemFieldValue );
        ActionWorkItemFieldValue wifv1 = (ActionWorkItemFieldValue) fv1;
        assertEquals( "ResultBooleanField",
                      wifv1.getField() );
        assertEquals( DataType.TYPE_BOOLEAN,
                      wifv1.getType() );
        assertEquals( "WorkItem",
                      wifv1.getWorkItemName() );
        assertEquals( "BooleanResult",
                      wifv1.getWorkItemParameterName() );
        assertEquals( Boolean.class.getName(),
                      wifv1.getWorkItemParameterClassName() );

        ActionFieldValue fv2 = aif.getFieldValues()[ 1 ];
        assertNotNull( fv2 );
        assertTrue( fv2 instanceof ActionWorkItemFieldValue );
        ActionWorkItemFieldValue wifv2 = (ActionWorkItemFieldValue) fv2;
        assertEquals( "ResultFloatField",
                      wifv2.getField() );
        assertEquals( DataType.TYPE_NUMERIC_FLOAT,
                      wifv2.getType() );
        assertEquals( "WorkItem",
                      wifv2.getWorkItemName() );
        assertEquals( "FloatResult",
                      wifv2.getWorkItemParameterName() );
        assertEquals( Float.class.getName(),
                      wifv2.getWorkItemParameterClassName() );

        ActionFieldValue fv3 = aif.getFieldValues()[ 2 ];
        assertNotNull( fv3 );
        assertTrue( fv3 instanceof ActionWorkItemFieldValue );
        ActionWorkItemFieldValue wifv3 = (ActionWorkItemFieldValue) fv3;
        assertEquals( "ResultIntegerField",
                      wifv3.getField() );
        assertEquals( DataType.TYPE_NUMERIC_INTEGER,
                      wifv3.getType() );
        assertEquals( "WorkItem",
                      wifv3.getWorkItemName() );
        assertEquals( "IntegerResult",
                      wifv3.getWorkItemParameterName() );
        assertEquals( Integer.class.getName(),
                      wifv3.getWorkItemParameterClassName() );

        ActionFieldValue fv4 = aif.getFieldValues()[ 3 ];
        assertNotNull( fv4 );
        assertTrue( fv4 instanceof ActionWorkItemFieldValue );
        ActionWorkItemFieldValue wifv4 = (ActionWorkItemFieldValue) fv4;
        assertEquals( "ResultStringField",
                      wifv4.getField() );
        assertEquals( DataType.TYPE_STRING,
                      wifv4.getType() );
        assertEquals( "WorkItem",
                      wifv4.getWorkItemName() );
        assertEquals( "StringResult",
                      wifv4.getWorkItemParameterName() );
        assertEquals( String.class.getName(),
                      wifv4.getWorkItemParameterClassName() );

    }

    @Test
    //Test only Actions set to "true" are correctly converted to RuleModel
    public void testRHSActionWorkItemInsertFacts2() {
        GuidedDTDRLPersistence p = new GuidedDTDRLPersistence();
        String[] row = new String[]{ "1", "desc", "true", "true", "false" };

        List<BaseColumn> allColumns = new ArrayList<BaseColumn>();
        allColumns.add( new RowNumberCol52() );
        allColumns.add( new DescriptionCol52() );
        List<ActionCol52> cols = new ArrayList<ActionCol52>();

        ActionWorkItemCol52 awi = new ActionWorkItemCol52();
        PortableWorkDefinition pwd = new PortableWorkDefinition();
        pwd.setName( "WorkItem" );
        awi.setWorkItemDefinition( pwd );

        PortableBooleanParameterDefinition p1 = new PortableBooleanParameterDefinition();
        p1.setName( "BooleanResult" );
        pwd.addResult( p1 );

        PortableFloatParameterDefinition p2 = new PortableFloatParameterDefinition();
        p2.setName( "FloatResult" );
        pwd.addResult( p2 );

        cols.add( awi );

        ActionWorkItemInsertFactCol52 asf1 = new ActionWorkItemInsertFactCol52();
        asf1.setBoundName( "$r" );
        asf1.setFactField( "ResultBooleanField" );
        asf1.setType( DataType.TYPE_BOOLEAN );
        asf1.setWorkItemName( "WorkItem" );
        asf1.setWorkItemResultParameterName( "BooleanResult" );
        asf1.setParameterClassName( Boolean.class.getName() );
        cols.add( asf1 );

        ActionWorkItemInsertFactCol52 asf2 = new ActionWorkItemInsertFactCol52();
        asf2.setBoundName( "$r" );
        asf2.setFactField( "ResultFloatField" );
        asf2.setType( DataType.TYPE_NUMERIC_FLOAT );
        asf2.setWorkItemName( "WorkItem" );
        asf2.setWorkItemResultParameterName( "FloatResult" );
        asf2.setParameterClassName( Float.class.getName() );
        cols.add( asf2 );

        RuleModel rm = new RuleModel();
        allColumns.addAll( cols );

        List<DTCellValue52> rowData = DataUtilities.makeDataRowList( row );
        TemplateDataProvider rowDataProvider = new GuidedDTTemplateDataProvider( allColumns,
                                                                                 rowData );

        p.doActions( allColumns,
                     cols,
                     rowDataProvider,
                     rowData,
                     rm );
        assertEquals( 2,
                      rm.rhs.length );

        //Examine RuleModel actions
        ActionExecuteWorkItem aw = (ActionExecuteWorkItem) rm.rhs[ 0 ];
        assertNotNull( aw );

        ActionInsertFact aif = (ActionInsertFact) rm.rhs[ 1 ];
        assertNotNull( aif );

        //Check ActionExecuteWorkItem
        PortableWorkDefinition mpwd = aw.getWorkDefinition();
        assertNotNull( mpwd );

        assertEquals( 2,
                      mpwd.getResults().size() );

        PortableBooleanParameterDefinition mp1 = (PortableBooleanParameterDefinition) mpwd.getResult( "BooleanResult" );
        assertNotNull( mp1 );

        PortableFloatParameterDefinition mp2 = (PortableFloatParameterDefinition) mpwd.getResult( "FloatResult" );
        assertNotNull( mp2 );

        //Check ActionInsertFact
        assertEquals( aif.getBoundName(),
                      "$r" );
        assertEquals( 1,
                      aif.getFieldValues().length );

        ActionFieldValue fv1 = aif.getFieldValues()[ 0 ];
        assertNotNull( fv1 );
        assertTrue( fv1 instanceof ActionWorkItemFieldValue );
        ActionWorkItemFieldValue wifv1 = (ActionWorkItemFieldValue) fv1;
        assertEquals( "ResultBooleanField",
                      wifv1.getField() );
        assertEquals( DataType.TYPE_BOOLEAN,
                      wifv1.getType() );
        assertEquals( "WorkItem",
                      wifv1.getWorkItemName() );
        assertEquals( "BooleanResult",
                      wifv1.getWorkItemParameterName() );
        assertEquals( Boolean.class.getName(),
                      wifv1.getWorkItemParameterClassName() );

    }

    @Test
    //This test checks a Decision Table involving BRL columns is correctly converted into a RuleModel
    public void testLHSWithBRLColumn_ParseToRuleModel() {

        GuidedDecisionTable52 dtable = new GuidedDecisionTable52();

        GuidedDTDRLPersistence p = new GuidedDTDRLPersistence();

        // All 3 rows should render, as the code is now lower down for skipping columns with empty cells
        String[][] data = new String[][]{
                new String[]{ "1", "desc", "Gargamel", "Pupa", "50" },
                new String[]{ "2", "desc", "Gargamel", "", "50" },
                new String[]{ "3", "desc", "Gargamel", "Pupa", "" }
        };

        //Simple (mandatory) columns
        dtable.setRowNumberCol( new RowNumberCol52() );
        dtable.setDescriptionCol( new DescriptionCol52() );

        //Simple Condition
        Pattern52 p1 = new Pattern52();
        p1.setFactType( "Baddie" );

        ConditionCol52 con = new ConditionCol52();
        con.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        con.setFactField( "name" );
        con.setOperator( "==" );
        p1.getChildColumns().add( con );

        dtable.getConditions().add( p1 );

        //BRL Column
        BRLConditionColumn brl1 = new BRLConditionColumn();

        //BRL Column definition
        List<IPattern> brl1Definition = new ArrayList<IPattern>();
        FactPattern brl1DefinitionFactPattern1 = new FactPattern( "Smurf" );

        SingleFieldConstraint brl1DefinitionFactPattern1Constraint1 = new SingleFieldConstraint();
        brl1DefinitionFactPattern1Constraint1.setFieldType( DataType.TYPE_STRING );
        brl1DefinitionFactPattern1Constraint1.setConstraintValueType( SingleFieldConstraint.TYPE_TEMPLATE );
        brl1DefinitionFactPattern1Constraint1.setFieldName( "name" );
        brl1DefinitionFactPattern1Constraint1.setOperator( "==" );
        brl1DefinitionFactPattern1Constraint1.setValue( "$name" );
        brl1DefinitionFactPattern1.addConstraint( brl1DefinitionFactPattern1Constraint1 );

        SingleFieldConstraint brl1DefinitionFactPattern1Constraint2 = new SingleFieldConstraint();
        brl1DefinitionFactPattern1Constraint2.setFieldType( DataType.TYPE_NUMERIC_INTEGER );
        brl1DefinitionFactPattern1Constraint2.setConstraintValueType( SingleFieldConstraint.TYPE_TEMPLATE );
        brl1DefinitionFactPattern1Constraint2.setFieldName( "age" );
        brl1DefinitionFactPattern1Constraint2.setOperator( "==" );
        brl1DefinitionFactPattern1Constraint2.setValue( "$age" );
        brl1DefinitionFactPattern1.addConstraint( brl1DefinitionFactPattern1Constraint2 );

        brl1Definition.add( brl1DefinitionFactPattern1 );

        brl1.setDefinition( brl1Definition );

        //Setup BRL column bindings
        BRLConditionVariableColumn brl1Variable1 = new BRLConditionVariableColumn( "$name",
                                                                                   DataType.TYPE_STRING,
                                                                                   "Person",
                                                                                   "name" );
        brl1.getChildColumns().add( brl1Variable1 );
        BRLConditionVariableColumn brl1Variable2 = new BRLConditionVariableColumn( "$age",
                                                                                   DataType.TYPE_NUMERIC_INTEGER,
                                                                                   "Person",
                                                                                   "age" );
        brl1.getChildColumns().add( brl1Variable2 );

        dtable.getConditions().add( brl1 );

        //Now to test conversion
        RuleModel rm = new RuleModel();
        List<BaseColumn> allColumns = dtable.getExpandedColumns();
        List<CompositeColumn<? extends BaseColumn>> allPatterns = dtable.getConditions();
        List<List<DTCellValue52>> dtData = DataUtilities.makeDataLists( data );

        //Row 0
        List<DTCellValue52> dtRowData0 = DataUtilities.makeDataRowList( data[ 0 ] );
        TemplateDataProvider rowDataProvider0 = new GuidedDTTemplateDataProvider( allColumns,
                                                                                  dtRowData0 );
        p.doConditions( allColumns,
                        allPatterns,
                        rowDataProvider0,
                        dtRowData0,
                        dtData,
                        rm );

        assertEquals( 2,
                      rm.lhs.length );
        assertEquals( "Baddie",
                      ( (FactPattern) rm.lhs[ 0 ] ).getFactType() );
        assertEquals( "Smurf",
                      ( (FactPattern) rm.lhs[ 1 ] ).getFactType() );

        // examine the first pattern
        FactPattern result0Fp1 = (FactPattern) rm.lhs[ 0 ];
        assertEquals( 1,
                      result0Fp1.getConstraintList().getConstraints().length );

        SingleFieldConstraint result0Fp1Con1 = (SingleFieldConstraint) result0Fp1.getConstraint( 0 );
        assertEquals( BaseSingleFieldConstraint.TYPE_LITERAL,
                      result0Fp1Con1.getConstraintValueType() );
        assertEquals( "name",
                      result0Fp1Con1.getFieldName() );
        assertEquals( "==",
                      result0Fp1Con1.getOperator() );
        assertEquals( "Gargamel",
                      result0Fp1Con1.getValue() );

        // examine the second pattern
        FactPattern result0Fp2 = (FactPattern) rm.lhs[ 1 ];
        assertEquals( 2,
                      result0Fp2.getConstraintList().getConstraints().length );

        SingleFieldConstraint result0Fp2Con1 = (SingleFieldConstraint) result0Fp2.getConstraint( 0 );
        assertEquals( BaseSingleFieldConstraint.TYPE_TEMPLATE,
                      result0Fp2Con1.getConstraintValueType() );
        assertEquals( "name",
                      result0Fp2Con1.getFieldName() );
        assertEquals( "==",
                      result0Fp2Con1.getOperator() );
        assertEquals( "$name",
                      result0Fp2Con1.getValue() );

        SingleFieldConstraint result0Fp2Con2 = (SingleFieldConstraint) result0Fp2.getConstraint( 1 );
        assertEquals( BaseSingleFieldConstraint.TYPE_TEMPLATE,
                      result0Fp2Con2.getConstraintValueType() );
        assertEquals( "age",
                      result0Fp2Con2.getFieldName() );
        assertEquals( "==",
                      result0Fp2Con2.getOperator() );
        assertEquals( "$age",
                      result0Fp2Con2.getValue() );

        //Row 1
        List<DTCellValue52> dtRowData1 = DataUtilities.makeDataRowList( data[ 1 ] );
        TemplateDataProvider rowDataProvider1 = new GuidedDTTemplateDataProvider( allColumns,
                                                                                  dtRowData1 );
        p.doConditions( allColumns,
                        allPatterns,
                        rowDataProvider1,
                        dtRowData1,
                        dtData,
                        rm );

        assertEquals( 2,
                      rm.lhs.length );
        assertEquals( "Baddie",
                      ( (FactPattern) rm.lhs[ 0 ] ).getFactType() );

        // examine the first pattern
        FactPattern result1Fp1 = (FactPattern) rm.lhs[ 0 ];
        assertEquals( 1,
                      result1Fp1.getConstraintList().getConstraints().length );

        SingleFieldConstraint result1Fp1Con1 = (SingleFieldConstraint) result1Fp1.getConstraint( 0 );
        assertEquals( BaseSingleFieldConstraint.TYPE_LITERAL,
                      result1Fp1Con1.getConstraintValueType() );
        assertEquals( "name",
                      result1Fp1Con1.getFieldName() );
        assertEquals( "==",
                      result1Fp1Con1.getOperator() );
        assertEquals( "Gargamel",
                      result1Fp1Con1.getValue() );

        // examine the second pattern
        FactPattern result1Fp2 = (FactPattern) rm.lhs[ 1 ];
        assertEquals( 2,
                      result1Fp2.getConstraintList().getConstraints().length );

        SingleFieldConstraint result1Fp2Con1 = (SingleFieldConstraint) result1Fp2.getConstraint( 0 );
        assertEquals( BaseSingleFieldConstraint.TYPE_TEMPLATE,
                      result1Fp2Con1.getConstraintValueType() );
        assertEquals( "name",
                      result1Fp2Con1.getFieldName() );
        assertEquals( "==",
                      result1Fp2Con1.getOperator() );
        assertEquals( "$name",
                      result1Fp2Con1.getValue() );

        SingleFieldConstraint result1Fp2Con2 = (SingleFieldConstraint) result1Fp2.getConstraint( 1 );
        assertEquals( BaseSingleFieldConstraint.TYPE_TEMPLATE,
                      result1Fp2Con2.getConstraintValueType() );
        assertEquals( "age",
                      result1Fp2Con2.getFieldName() );
        assertEquals( "==",
                      result1Fp2Con2.getOperator() );
        assertEquals( "$age",
                      result1Fp2Con2.getValue() );

        //Row 2
        List<DTCellValue52> dtRowData2 = DataUtilities.makeDataRowList( data[ 2 ] );
        TemplateDataProvider rowDataProvider2 = new GuidedDTTemplateDataProvider( allColumns,
                                                                                  dtRowData2 );
        p.doConditions( allColumns,
                        allPatterns,
                        rowDataProvider2,
                        dtRowData2,
                        dtData,
                        rm );

        assertEquals( 2,
                      rm.lhs.length );
        assertEquals( "Baddie",
                      ( (FactPattern) rm.lhs[ 0 ] ).getFactType() );

        // examine the first pattern
        FactPattern result2Fp1 = (FactPattern) rm.lhs[ 0 ];
        assertEquals( 1,
                      result2Fp1.getConstraintList().getConstraints().length );

        SingleFieldConstraint result2Fp1Con1 = (SingleFieldConstraint) result2Fp1.getConstraint( 0 );
        assertEquals( BaseSingleFieldConstraint.TYPE_LITERAL,
                      result2Fp1Con1.getConstraintValueType() );
        assertEquals( "name",
                      result2Fp1Con1.getFieldName() );
        assertEquals( "==",
                      result2Fp1Con1.getOperator() );
        assertEquals( "Gargamel",
                      result2Fp1Con1.getValue() );

        // examine the second pattern
        FactPattern result2Fp2 = (FactPattern) rm.lhs[ 1 ];
        assertEquals( 2,
                      result2Fp2.getConstraintList().getConstraints().length );

        SingleFieldConstraint result2Fp2Con1 = (SingleFieldConstraint) result2Fp2.getConstraint( 0 );
        assertEquals( BaseSingleFieldConstraint.TYPE_TEMPLATE,
                      result2Fp2Con1.getConstraintValueType() );
        assertEquals( "name",
                      result2Fp2Con1.getFieldName() );
        assertEquals( "==",
                      result2Fp2Con1.getOperator() );
        assertEquals( "$name",
                      result2Fp2Con1.getValue() );

        SingleFieldConstraint result2Fp2Con2 = (SingleFieldConstraint) result2Fp2.getConstraint( 1 );
        assertEquals( BaseSingleFieldConstraint.TYPE_TEMPLATE,
                      result2Fp2Con2.getConstraintValueType() );
        assertEquals( "age",
                      result2Fp2Con2.getFieldName() );
        assertEquals( "==",
                      result2Fp2Con2.getOperator() );
        assertEquals( "$age",
                      result2Fp2Con2.getValue() );

    }

    @Test
    //This test checks a Decision Table involving BRL columns is correctly converted into DRL
    public void testLHSWithBRLColumn_ParseToDRL() {

        GuidedDecisionTable52 dtable = new GuidedDecisionTable52();

        // All 3 rows should render, as the code is now lower down for skipping columns with empty cells
        String[][] data = new String[][]{
                new String[]{ "1", "desc", "Gargamel", "Pupa", "50" },
                new String[]{ "2", "desc", "Gargamel", "", "50" },
                new String[]{ "3", "desc", "Gargamel", "Pupa", "" }
        };

        //Simple (mandatory) columns
        dtable.setRowNumberCol( new RowNumberCol52() );
        dtable.setDescriptionCol( new DescriptionCol52() );

        //Simple Condition
        Pattern52 p1 = new Pattern52();
        p1.setFactType( "Baddie" );

        ConditionCol52 con = new ConditionCol52();
        con.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        con.setFactField( "name" );
        con.setOperator( "==" );
        p1.getChildColumns().add( con );

        dtable.getConditions().add( p1 );

        //BRL Column
        BRLConditionColumn brl1 = new BRLConditionColumn();

        //BRL Column definition
        List<IPattern> brl1Definition = new ArrayList<IPattern>();
        FactPattern brl1DefinitionFactPattern1 = new FactPattern( "Smurf" );

        SingleFieldConstraint brl1DefinitionFactPattern1Constraint1 = new SingleFieldConstraint();
        brl1DefinitionFactPattern1Constraint1.setFieldType( DataType.TYPE_STRING );
        brl1DefinitionFactPattern1Constraint1.setConstraintValueType( SingleFieldConstraint.TYPE_TEMPLATE );
        brl1DefinitionFactPattern1Constraint1.setFieldName( "name" );
        brl1DefinitionFactPattern1Constraint1.setOperator( "==" );
        brl1DefinitionFactPattern1Constraint1.setValue( "$name" );
        brl1DefinitionFactPattern1.addConstraint( brl1DefinitionFactPattern1Constraint1 );

        SingleFieldConstraint brl1DefinitionFactPattern1Constraint2 = new SingleFieldConstraint();
        brl1DefinitionFactPattern1Constraint2.setFieldType( DataType.TYPE_NUMERIC_INTEGER );
        brl1DefinitionFactPattern1Constraint2.setConstraintValueType( SingleFieldConstraint.TYPE_TEMPLATE );
        brl1DefinitionFactPattern1Constraint2.setFieldName( "age" );
        brl1DefinitionFactPattern1Constraint2.setOperator( "==" );
        brl1DefinitionFactPattern1Constraint2.setValue( "$age" );
        brl1DefinitionFactPattern1.addConstraint( brl1DefinitionFactPattern1Constraint2 );

        brl1Definition.add( brl1DefinitionFactPattern1 );

        brl1.setDefinition( brl1Definition );

        //Setup BRL column bindings
        BRLConditionVariableColumn brl1Variable1 = new BRLConditionVariableColumn( "$name",
                                                                                   DataType.TYPE_STRING,
                                                                                   "Person",
                                                                                   "name" );
        brl1.getChildColumns().add( brl1Variable1 );
        BRLConditionVariableColumn brl1Variable2 = new BRLConditionVariableColumn( "$age",
                                                                                   DataType.TYPE_NUMERIC_INTEGER,
                                                                                   "Person",
                                                                                   "age" );
        brl1.getChildColumns().add( brl1Variable2 );

        dtable.getConditions().add( brl1 );
        dtable.setData( DataUtilities.makeDataLists( data ) );

        //Now to test conversion
        int ruleStartIndex;
        int pattern1StartIndex;
        int pattern2StartIndex;
        GuidedDTDRLPersistence p = GuidedDTDRLPersistence.getInstance();
        String drl = p.marshal( dtable );

        System.out.println( drl );

        //Row 0
        ruleStartIndex = drl.indexOf( "//from row number: 1" );
        assertFalse( ruleStartIndex == -1 );
        pattern1StartIndex = drl.indexOf( "Baddie( name == \"Gargamel\" )",
                                          ruleStartIndex );
        assertFalse( pattern1StartIndex == -1 );
        pattern2StartIndex = drl.indexOf( "Smurf( name == \"Pupa\" , age == 50 )",
                                          ruleStartIndex );
        assertFalse( pattern2StartIndex == -1 );

        //Row 1
        ruleStartIndex = drl.indexOf( "//from row number: 2" );
        assertFalse( ruleStartIndex == -1 );
        pattern1StartIndex = drl.indexOf( "Baddie( name == \"Gargamel\" )",
                                          ruleStartIndex );
        assertFalse( pattern1StartIndex == -1 );
        pattern2StartIndex = drl.indexOf( "Smurf( age == 50 )",
                                          ruleStartIndex );
        assertFalse( pattern2StartIndex == -1 );

        //Row 2
        ruleStartIndex = drl.indexOf( "//from row number: 3" );
        assertFalse( ruleStartIndex == -1 );
        pattern1StartIndex = drl.indexOf( "Baddie( name == \"Gargamel\" )",
                                          ruleStartIndex );
        assertFalse( pattern1StartIndex == -1 );
        pattern2StartIndex = drl.indexOf( "Smurf( name == \"Pupa\" )",
                                          ruleStartIndex );
        assertFalse( pattern2StartIndex == -1 );

    }

    @Test
    //This test checks a Decision Table involving BRL columns is correctly converted into DRL
    public void testLHSWithBRLColumn_ParseToDRL_MultiplePatterns() {

        GuidedDecisionTable52 dtable = new GuidedDecisionTable52();

        // All 3 rows should render, as the code is now lower down for skipping columns with empty cells
        String[][] data = new String[][]{
                new String[]{ "1", "desc", "Pupa", "50" },
                new String[]{ "2", "desc", "", "50" },
                new String[]{ "3", "desc", "Pupa", "" }
        };

        //Simple (mandatory) columns
        dtable.setRowNumberCol( new RowNumberCol52() );
        dtable.setDescriptionCol( new DescriptionCol52() );

        //BRL Column
        BRLConditionColumn brl1 = new BRLConditionColumn();

        //BRL Column definition
        List<IPattern> brl1Definition = new ArrayList<IPattern>();
        FactPattern brl1DefinitionFactPattern1 = new FactPattern( "Baddie" );

        SingleFieldConstraint brl1DefinitionFactPattern1Constraint1 = new SingleFieldConstraint();
        brl1DefinitionFactPattern1Constraint1.setFieldType( DataType.TYPE_STRING );
        brl1DefinitionFactPattern1Constraint1.setConstraintValueType( SingleFieldConstraint.TYPE_LITERAL );
        brl1DefinitionFactPattern1Constraint1.setFieldName( "name" );
        brl1DefinitionFactPattern1Constraint1.setOperator( "==" );
        brl1DefinitionFactPattern1Constraint1.setValue( "Gargamel" );
        brl1DefinitionFactPattern1.addConstraint( brl1DefinitionFactPattern1Constraint1 );

        brl1Definition.add( brl1DefinitionFactPattern1 );

        FactPattern brl1DefinitionFactPattern2 = new FactPattern( "Smurf" );

        SingleFieldConstraint brl1DefinitionFactPattern2Constraint1 = new SingleFieldConstraint();
        brl1DefinitionFactPattern2Constraint1.setFieldType( DataType.TYPE_STRING );
        brl1DefinitionFactPattern2Constraint1.setConstraintValueType( SingleFieldConstraint.TYPE_TEMPLATE );
        brl1DefinitionFactPattern2Constraint1.setFieldName( "name" );
        brl1DefinitionFactPattern2Constraint1.setOperator( "==" );
        brl1DefinitionFactPattern2Constraint1.setValue( "$name" );
        brl1DefinitionFactPattern2.addConstraint( brl1DefinitionFactPattern2Constraint1 );

        SingleFieldConstraint brl1DefinitionFactPattern2Constraint2 = new SingleFieldConstraint();
        brl1DefinitionFactPattern2Constraint2.setFieldType( DataType.TYPE_NUMERIC_INTEGER );
        brl1DefinitionFactPattern2Constraint2.setConstraintValueType( SingleFieldConstraint.TYPE_TEMPLATE );
        brl1DefinitionFactPattern2Constraint2.setFieldName( "age" );
        brl1DefinitionFactPattern2Constraint2.setOperator( "==" );
        brl1DefinitionFactPattern2Constraint2.setValue( "$age" );
        brl1DefinitionFactPattern2.addConstraint( brl1DefinitionFactPattern2Constraint2 );

        brl1Definition.add( brl1DefinitionFactPattern2 );

        brl1.setDefinition( brl1Definition );

        //Setup BRL column bindings
        BRLConditionVariableColumn brl1Variable1 = new BRLConditionVariableColumn( "$name",
                                                                                   DataType.TYPE_STRING,
                                                                                   "Person",
                                                                                   "name" );
        brl1.getChildColumns().add( brl1Variable1 );
        BRLConditionVariableColumn brl1Variable2 = new BRLConditionVariableColumn( "$age",
                                                                                   DataType.TYPE_NUMERIC_INTEGER,
                                                                                   "Person",
                                                                                   "age" );
        brl1.getChildColumns().add( brl1Variable2 );

        dtable.getConditions().add( brl1 );
        dtable.setData( DataUtilities.makeDataLists( data ) );

        //Now to test conversion
        int ruleStartIndex;
        int pattern1StartIndex;
        int pattern2StartIndex;
        GuidedDTDRLPersistence p = GuidedDTDRLPersistence.getInstance();
        String drl = p.marshal( dtable );

        System.out.println( drl );

        //Row 0
        ruleStartIndex = drl.indexOf( "//from row number: 1" );
        assertFalse( ruleStartIndex == -1 );
        pattern1StartIndex = drl.indexOf( "Baddie( name == \"Gargamel\" )",
                                          ruleStartIndex );
        assertFalse( pattern1StartIndex == -1 );
        pattern2StartIndex = drl.indexOf( "Smurf( name == \"Pupa\" , age == 50 )",
                                          ruleStartIndex );
        assertFalse( pattern2StartIndex == -1 );

        //Row 1
        ruleStartIndex = drl.indexOf( "//from row number: 2" );
        assertFalse( ruleStartIndex == -1 );
        pattern1StartIndex = drl.indexOf( "Baddie( name == \"Gargamel\" )",
                                          ruleStartIndex );
        assertFalse( pattern1StartIndex == -1 );
        pattern2StartIndex = drl.indexOf( "Smurf( age == 50 )",
                                          ruleStartIndex );
        assertFalse( pattern2StartIndex == -1 );

        //Row 2
        ruleStartIndex = drl.indexOf( "//from row number: 3" );
        assertFalse( ruleStartIndex == -1 );
        pattern1StartIndex = drl.indexOf( "Baddie( name == \"Gargamel\" )",
                                          ruleStartIndex );
        assertFalse( pattern1StartIndex == -1 );
        pattern2StartIndex = drl.indexOf( "Smurf( name == \"Pupa\" )",
                                          ruleStartIndex );
        assertFalse( pattern2StartIndex == -1 );

    }

    @Test
    //This test checks a Decision Table involving BRL columns is correctly converted into DRL
    public void testLHSWithBRLColumn_ParseToDRL_NoVariables() {

        GuidedDecisionTable52 dtable = new GuidedDecisionTable52();

        //Row 0 should become an IPattern in the resulting RuleModel as it contains getValue()s for all Template fields in the BRL Column
        //Row 1 should *NOT* become an IPattern in the resulting RuleModel as it does *NOT* contain getValue()s for all Template fields in the BRL Column
        Object[][] data = new Object[][]{
                new Object[]{ "1", "desc", Boolean.TRUE },
                new Object[]{ "2", "desc", Boolean.FALSE }
        };

        //Simple (mandatory) columns
        dtable.setRowNumberCol( new RowNumberCol52() );
        dtable.setDescriptionCol( new DescriptionCol52() );

        //BRL Column
        BRLConditionColumn brl1 = new BRLConditionColumn();

        //BRL Column definition
        List<IPattern> brl1Definition = new ArrayList<IPattern>();
        FactPattern brl1DefinitionFactPattern1 = new FactPattern( "Baddie" );

        SingleFieldConstraint brl1DefinitionFactPattern1Constraint1 = new SingleFieldConstraint();
        brl1DefinitionFactPattern1Constraint1.setFieldType( DataType.TYPE_STRING );
        brl1DefinitionFactPattern1Constraint1.setConstraintValueType( SingleFieldConstraint.TYPE_LITERAL );
        brl1DefinitionFactPattern1Constraint1.setFieldName( "name" );
        brl1DefinitionFactPattern1Constraint1.setOperator( "==" );
        brl1DefinitionFactPattern1Constraint1.setValue( "Gargamel" );
        brl1DefinitionFactPattern1.addConstraint( brl1DefinitionFactPattern1Constraint1 );

        brl1Definition.add( brl1DefinitionFactPattern1 );

        brl1.setDefinition( brl1Definition );

        //Setup BRL column bindings
        BRLConditionVariableColumn brl1Variable1 = new BRLConditionVariableColumn( "",
                                                                                   DataType.TYPE_BOOLEAN );
        brl1.getChildColumns().add( brl1Variable1 );

        dtable.getConditions().add( brl1 );
        dtable.setData( DataUtilities.makeDataLists( data ) );

        //Now to test conversion
        int ruleStartIndex;
        int pattern1StartIndex;
        GuidedDTDRLPersistence p = GuidedDTDRLPersistence.getInstance();
        String drl = p.marshal( dtable );

        //Row 0
        ruleStartIndex = drl.indexOf( "//from row number: 1" );
        assertFalse( ruleStartIndex == -1 );
        pattern1StartIndex = drl.indexOf( "Baddie( name == \"Gargamel\" )",
                                          ruleStartIndex );
        assertFalse( pattern1StartIndex == -1 );

        //Row 1
        ruleStartIndex = drl.indexOf( "//from row number: 2" );
        assertFalse( ruleStartIndex == -1 );
        pattern1StartIndex = drl.indexOf( "Baddie( name == \"Gargamel\" )",
                                          ruleStartIndex );
        assertTrue( pattern1StartIndex == -1 );

    }

    @Test
    //This test checks a Decision Table involving BRL columns is correctly converted into DRL
    public void testLHSWithBRLColumn_ParseToDRL_FreeFormLine() {

        GuidedDecisionTable52 dtable = new GuidedDecisionTable52();

        //Row 0 should become an IPattern in the resulting RuleModel as it contains values for all Template fields in the BRL Column
        //Row 1 should *NOT* become an IPattern in the resulting RuleModel as it does *NOT* contain values for all Template fields in the BRL Column
        //Row 2 should *NOT* become an IPattern in the resulting RuleModel as it does *NOT* contain values for all Template fields in the BRL Column
        //Row 3 should *NOT* become an IPattern in the resulting RuleModel as it does *NOT* contain values for all Template fields in the BRL Column
        String[][] data = new String[][]{
                new String[]{ "1", "desc", "Pupa", "50" },
                new String[]{ "2", "desc", "", "50" },
                new String[]{ "3", "desc", "Pupa", "" },
                new String[]{ "4", "desc", "", "" }
        };

        //Simple (mandatory) columns
        dtable.setRowNumberCol( new RowNumberCol52() );
        dtable.setDescriptionCol( new DescriptionCol52() );

        //BRL Column
        BRLConditionColumn brl1 = new BRLConditionColumn();

        //BRL Column definition
        List<IPattern> brl1Definition = new ArrayList<IPattern>();
        FreeFormLine brl1DefinitionFreeFormLine = new FreeFormLine();
        brl1DefinitionFreeFormLine.setText( "Smurf( name == \"@{name}\", age == @{age} )" );

        brl1Definition.add( brl1DefinitionFreeFormLine );

        brl1.setDefinition( brl1Definition );

        //Setup BRL column bindings
        BRLConditionVariableColumn brl1Variable1 = new BRLConditionVariableColumn( "name",
                                                                                   DataType.TYPE_STRING );
        BRLConditionVariableColumn brl1Variable2 = new BRLConditionVariableColumn( "age",
                                                                                   DataType.TYPE_NUMERIC_INTEGER );
        brl1.getChildColumns().add( brl1Variable1 );
        brl1.getChildColumns().add( brl1Variable2 );

        dtable.getConditions().add( brl1 );
        dtable.setData( DataUtilities.makeDataLists( data ) );

        //Now to test conversion
        int ruleStartIndex;
        int pattern1StartIndex;
        GuidedDTDRLPersistence p = GuidedDTDRLPersistence.getInstance();
        String drl = p.marshal( dtable );

        //Row 0
        ruleStartIndex = drl.indexOf( "//from row number: 1" );
        assertFalse( ruleStartIndex == -1 );
        pattern1StartIndex = drl.indexOf( "Smurf( name == \"Pupa\", age == 50 )",
                                          ruleStartIndex );
        assertFalse( pattern1StartIndex == -1 );

        //Row 1
        ruleStartIndex = drl.indexOf( "//from row number: 2" );
        assertFalse( ruleStartIndex == -1 );
        pattern1StartIndex = drl.indexOf( "Smurf(",
                                          ruleStartIndex );
        assertTrue( pattern1StartIndex == -1 );

        //Row 2
        ruleStartIndex = drl.indexOf( "//from row number: 3" );
        assertFalse( ruleStartIndex == -1 );
        pattern1StartIndex = drl.indexOf( "Smurf(",
                                          ruleStartIndex );
        assertTrue( pattern1StartIndex == -1 );

        //Row 3
        ruleStartIndex = drl.indexOf( "//from row number: 4" );
        assertFalse( ruleStartIndex == -1 );
        pattern1StartIndex = drl.indexOf( "Smurf(",
                                          ruleStartIndex );
        assertTrue( pattern1StartIndex == -1 );
    }

    @Test
    //This test checks a Decision Table involving BRL columns is correctly converted into a RuleModel
    public void testRHSWithBRLColumn_ParseToRuleModel() {

        GuidedDecisionTable52 dtable = new GuidedDecisionTable52();

        GuidedDTDRLPersistence p = new GuidedDTDRLPersistence();

        // All three rows are entered, some columns with optional data
        String[][] data = new String[][]{
                new String[]{ "1", "desc", "Gargamel", "Pupa", "50" },
                new String[]{ "2", "desc", "Gargamel", "", "50" },
                new String[]{ "3", "desc", "Gargamel", "Pupa", "" }
        };

        //Simple (mandatory) columns
        dtable.setRowNumberCol( new RowNumberCol52() );
        dtable.setDescriptionCol( new DescriptionCol52() );

        //Simple Action
        ActionInsertFactCol52 a1 = new ActionInsertFactCol52();
        a1.setBoundName( "$b" );
        a1.setFactType( "Baddie" );
        a1.setFactField( "name" );
        a1.setType( DataType.TYPE_STRING );

        dtable.getActionCols().add( a1 );

        //BRL Column
        BRLActionColumn brl1 = new BRLActionColumn();

        //BRL Column definition
        List<IAction> brl1Definition = new ArrayList<IAction>();
        ActionInsertFact brl1DefinitionAction1 = new ActionInsertFact( "Smurf" );
        ActionFieldValue brl1DefinitionAction1FieldValue1 = new ActionFieldValue( "name",
                                                                                  "$name",
                                                                                  DataType.TYPE_STRING );
        brl1DefinitionAction1FieldValue1.setNature( BaseSingleFieldConstraint.TYPE_TEMPLATE );
        brl1DefinitionAction1.addFieldValue( brl1DefinitionAction1FieldValue1 );
        ActionFieldValue brl1DefinitionAction1FieldValue2 = new ActionFieldValue( "age",
                                                                                  "$age",
                                                                                  DataType.TYPE_NUMERIC_INTEGER );
        brl1DefinitionAction1FieldValue2.setNature( BaseSingleFieldConstraint.TYPE_TEMPLATE );
        brl1DefinitionAction1.addFieldValue( brl1DefinitionAction1FieldValue2 );
        brl1Definition.add( brl1DefinitionAction1 );
        brl1.setDefinition( brl1Definition );

        //Setup BRL column bindings
        BRLActionVariableColumn brl1Variable1 = new BRLActionVariableColumn( "$name",
                                                                             DataType.TYPE_STRING,
                                                                             "Person",
                                                                             "name" );
        brl1.getChildColumns().add( brl1Variable1 );
        BRLActionVariableColumn brl1Variable2 = new BRLActionVariableColumn( "$age",
                                                                             DataType.TYPE_NUMERIC_INTEGER,
                                                                             "Person",
                                                                             "age" );
        brl1.getChildColumns().add( brl1Variable2 );

        dtable.getActionCols().add( brl1 );

        //Now to test conversion
        RuleModel rm = new RuleModel();
        List<BaseColumn> allColumns = dtable.getExpandedColumns();
        List<ActionCol52> allActions = dtable.getActionCols();

        //Row 0
        List<DTCellValue52> dtRowData0 = DataUtilities.makeDataRowList( data[ 0 ] );
        TemplateDataProvider rowDataProvider0 = new GuidedDTTemplateDataProvider( allColumns,
                                                                                  dtRowData0 );
        p.doActions( allColumns,
                     allActions,
                     rowDataProvider0,
                     dtRowData0,
                     rm );

        assertEquals( 2,
                      rm.rhs.length );
        assertEquals( "Baddie",
                      ( (ActionInsertFact) rm.rhs[ 0 ] ).getFactType() );
        assertEquals( "Smurf",
                      ( (ActionInsertFact) rm.rhs[ 1 ] ).getFactType() );

        // examine the first action
        ActionInsertFact result0Action1 = (ActionInsertFact) rm.rhs[ 0 ];
        assertEquals( 1,
                      result0Action1.getFieldValues().length );

        ActionFieldValue result0Action1FieldValue1 = (ActionFieldValue) result0Action1.getFieldValues()[ 0 ];
        assertEquals( DataType.TYPE_STRING,
                      result0Action1FieldValue1.getType() );
        assertEquals( "name",
                      result0Action1FieldValue1.getField() );
        assertEquals( "Gargamel",
                      result0Action1FieldValue1.getValue() );

        // examine the second action
        ActionInsertFact result0Action2 = (ActionInsertFact) rm.rhs[ 1 ];
        assertEquals( 2,
                      result0Action2.getFieldValues().length );

        ActionFieldValue result0Action2FieldValue1 = (ActionFieldValue) result0Action2.getFieldValues()[ 0 ];
        assertEquals( DataType.TYPE_STRING,
                      result0Action2FieldValue1.getType() );
        assertEquals( "name",
                      result0Action2FieldValue1.getField() );
        assertEquals( "$name",
                      result0Action2FieldValue1.getValue() );

        ActionFieldValue result0Action2FieldValue2 = (ActionFieldValue) result0Action2.getFieldValues()[ 1 ];
        assertEquals( DataType.TYPE_NUMERIC_INTEGER,
                      result0Action2FieldValue2.getType() );
        assertEquals( "age",
                      result0Action2FieldValue2.getField() );
        assertEquals( "$age",
                      result0Action2FieldValue2.getValue() );

        //Row 1
        List<DTCellValue52> dtRowData1 = DataUtilities.makeDataRowList( data[ 1 ] );
        TemplateDataProvider rowDataProvider1 = new GuidedDTTemplateDataProvider( allColumns,
                                                                                  dtRowData1 );
        p.doActions( allColumns,
                     allActions,
                     rowDataProvider1,
                     dtRowData1,
                     rm );

        assertEquals( 2,
                      rm.rhs.length );
        assertEquals( "Baddie",
                      ( (ActionInsertFact) rm.rhs[ 0 ] ).getFactType() );
        assertEquals( "Smurf",
                      ( (ActionInsertFact) rm.rhs[ 1 ] ).getFactType() );

        // examine the first action
        ActionInsertFact result1Action1 = (ActionInsertFact) rm.rhs[ 0 ];
        assertEquals( 1,
                      result1Action1.getFieldValues().length );

        ActionFieldValue result1Action1FieldValue1 = (ActionFieldValue) result1Action1.getFieldValues()[ 0 ];
        assertEquals( DataType.TYPE_STRING,
                      result1Action1FieldValue1.getType() );
        assertEquals( "name",
                      result1Action1FieldValue1.getField() );
        assertEquals( "Gargamel",
                      result1Action1FieldValue1.getValue() );

        // examine the second action
        ActionInsertFact result1Action2 = (ActionInsertFact) rm.rhs[ 1 ];
        assertEquals( 2,
                      result1Action2.getFieldValues().length );

        ActionFieldValue result1Action2FieldValue1 = (ActionFieldValue) result1Action2.getFieldValues()[ 0 ];
        assertEquals( DataType.TYPE_STRING,
                      result1Action2FieldValue1.getType() );
        assertEquals( "name",
                      result1Action2FieldValue1.getField() );
        assertEquals( "$name",
                      result1Action2FieldValue1.getValue() );

        ActionFieldValue result1Action2FieldValue2 = (ActionFieldValue) result1Action2.getFieldValues()[ 1 ];
        assertEquals( DataType.TYPE_NUMERIC_INTEGER,
                      result1Action2FieldValue2.getType() );
        assertEquals( "age",
                      result1Action2FieldValue2.getField() );
        assertEquals( "$age",
                      result1Action2FieldValue2.getValue() );

        //Row 2
        List<DTCellValue52> dtRowData2 = DataUtilities.makeDataRowList( data[ 2 ] );
        TemplateDataProvider rowDataProvider2 = new GuidedDTTemplateDataProvider( allColumns,
                                                                                  dtRowData2 );
        p.doActions( allColumns,
                     allActions,
                     rowDataProvider2,
                     dtRowData2,
                     rm );

        assertEquals( 2,
                      rm.rhs.length );
        assertEquals( "Baddie",
                      ( (ActionInsertFact) rm.rhs[ 0 ] ).getFactType() );
        assertEquals( "Smurf",
                      ( (ActionInsertFact) rm.rhs[ 1 ] ).getFactType() );

        // examine the first action
        ActionInsertFact result2Action1 = (ActionInsertFact) rm.rhs[ 0 ];
        assertEquals( 1,
                      result2Action1.getFieldValues().length );

        ActionFieldValue result2Action1FieldValue1 = (ActionFieldValue) result2Action1.getFieldValues()[ 0 ];
        assertEquals( DataType.TYPE_STRING,
                      result2Action1FieldValue1.getType() );
        assertEquals( "name",
                      result2Action1FieldValue1.getField() );
        assertEquals( "Gargamel",
                      result2Action1FieldValue1.getValue() );

        // examine the second action
        ActionInsertFact result2Action2 = (ActionInsertFact) rm.rhs[ 1 ];
        assertEquals( 2,
                      result2Action2.getFieldValues().length );

        ActionFieldValue result2Action2FieldValue1 = (ActionFieldValue) result2Action2.getFieldValues()[ 0 ];
        assertEquals( DataType.TYPE_STRING,
                      result2Action2FieldValue1.getType() );
        assertEquals( "name",
                      result2Action2FieldValue1.getField() );
        assertEquals( "$name",
                      result2Action2FieldValue1.getValue() );

        ActionFieldValue result3Action2FieldValue2 = (ActionFieldValue) result2Action2.getFieldValues()[ 1 ];
        assertEquals( DataType.TYPE_NUMERIC_INTEGER,
                      result3Action2FieldValue2.getType() );
        assertEquals( "age",
                      result3Action2FieldValue2.getField() );
        assertEquals( "$age",
                      result3Action2FieldValue2.getValue() );
    }

    @Test
    //This test checks a Decision Table involving BRL columns is correctly converted into DRL
    public void testRHSWithBRLColumn_ParseToDRL() {

        GuidedDecisionTable52 dtable = new GuidedDecisionTable52();

        // All three rows are entered, some columns with optional data
        String[][] data = new String[][]{
                new String[]{ "1", "desc", "Gargamel", "Pupa", "50" },
                new String[]{ "2", "desc", "Gargamel", "", "50" },
                new String[]{ "3", "desc", "Gargamel", "Pupa", "" }
        };

        //Simple (mandatory) columns
        dtable.setRowNumberCol( new RowNumberCol52() );
        dtable.setDescriptionCol( new DescriptionCol52() );

        //Simple Action
        ActionInsertFactCol52 a1 = new ActionInsertFactCol52();
        a1.setBoundName( "$b" );
        a1.setFactType( "Baddie" );
        a1.setFactField( "name" );
        a1.setType( DataType.TYPE_STRING );

        dtable.getActionCols().add( a1 );

        //BRL Column
        BRLActionColumn brl1 = new BRLActionColumn();

        //BRL Column definition
        List<IAction> brl1Definition = new ArrayList<IAction>();
        ActionInsertFact brl1DefinitionAction1 = new ActionInsertFact( "Smurf" );
        ActionFieldValue brl1DefinitionAction1FieldValue1 = new ActionFieldValue( "name",
                                                                                  "$name",
                                                                                  DataType.TYPE_STRING );
        brl1DefinitionAction1FieldValue1.setNature( BaseSingleFieldConstraint.TYPE_TEMPLATE );
        brl1DefinitionAction1.addFieldValue( brl1DefinitionAction1FieldValue1 );
        ActionFieldValue brl1DefinitionAction1FieldValue2 = new ActionFieldValue( "age",
                                                                                  "$age",
                                                                                  DataType.TYPE_NUMERIC_INTEGER );
        brl1DefinitionAction1FieldValue2.setNature( BaseSingleFieldConstraint.TYPE_TEMPLATE );
        brl1DefinitionAction1.addFieldValue( brl1DefinitionAction1FieldValue2 );
        brl1Definition.add( brl1DefinitionAction1 );
        brl1.setDefinition( brl1Definition );

        //Setup BRL column bindings
        BRLActionVariableColumn brl1Variable1 = new BRLActionVariableColumn( "$name",
                                                                             DataType.TYPE_STRING,
                                                                             "Person",
                                                                             "name" );
        brl1.getChildColumns().add( brl1Variable1 );
        BRLActionVariableColumn brl1Variable2 = new BRLActionVariableColumn( "$age",
                                                                             DataType.TYPE_NUMERIC_INTEGER,
                                                                             "Person",
                                                                             "age" );
        brl1.getChildColumns().add( brl1Variable2 );

        dtable.getActionCols().add( brl1 );
        dtable.setData( DataUtilities.makeDataLists( data ) );

        //Now to test conversion
        int ruleStartIndex;
        int action1StartIndex;
        int action2StartIndex;
        GuidedDTDRLPersistence p = GuidedDTDRLPersistence.getInstance();
        String drl = p.marshal( dtable );

        //Row 0
        ruleStartIndex = drl.indexOf( "//from row number: 1" );
        assertFalse( ruleStartIndex == -1 );
        action1StartIndex = drl.indexOf( "Baddie $b = new Baddie();",
                                         ruleStartIndex );
        assertFalse( action1StartIndex == -1 );
        action1StartIndex = drl.indexOf( "$b.setName( \"Gargamel\" );",
                                         action1StartIndex );
        assertFalse( action1StartIndex == -1 );
        action1StartIndex = drl.indexOf( "insert( $b );",
                                         action1StartIndex );
        assertFalse( action1StartIndex == -1 );

        action2StartIndex = drl.indexOf( "Smurf fact0 = new Smurf();",
                                         ruleStartIndex );
        assertFalse( action2StartIndex == -1 );
        action2StartIndex = drl.indexOf( "fact0.setName( \"Pupa\" );",
                                         action2StartIndex );
        assertFalse( action2StartIndex == -1 );
        action2StartIndex = drl.indexOf( "fact0.setAge( 50 );",
                                         action2StartIndex );
        assertFalse( action2StartIndex == -1 );
        action2StartIndex = drl.indexOf( "insert( fact0 );",
                                         action2StartIndex );
        assertFalse( action2StartIndex == -1 );

        //Row 1
        ruleStartIndex = drl.indexOf( "//from row number: 2" );
        int ruleEndIndex = drl.indexOf( "//from row number: 3" );
        assertFalse( ruleStartIndex == -1 );
        action1StartIndex = drl.indexOf( "Baddie $b = new Baddie();",
                                         ruleStartIndex );
        assertFalse( action1StartIndex == -1 );
        action1StartIndex = drl.indexOf( "$b.setName( \"Gargamel\" );",
                                         action1StartIndex );
        assertFalse( action1StartIndex == -1 );
        action1StartIndex = drl.indexOf( "insert( $b );",
                                         action1StartIndex );
        assertFalse( action1StartIndex == -1 );

        action2StartIndex = drl.indexOf( "Smurf fact0 = new Smurf();",
                                         ruleStartIndex );
        assertFalse( action2StartIndex == -1 );
        action2StartIndex = drl.indexOf( "fact0.setName(",
                                         ruleStartIndex );
        assertFalse( action2StartIndex < ruleEndIndex );
        action2StartIndex = drl.indexOf( "fact0.setAge( 50 );",
                                         ruleStartIndex );
        assertFalse( action2StartIndex == -1 );
        action2StartIndex = drl.indexOf( "insert( fact0 );",
                                         ruleStartIndex );
        assertFalse( action2StartIndex == -1 );

        //Row 2
        ruleStartIndex = drl.indexOf( "//from row number: 3" );
        assertFalse( ruleStartIndex == -1 );
        action1StartIndex = drl.indexOf( "Baddie $b = new Baddie();",
                                         ruleStartIndex );
        assertFalse( action1StartIndex == -1 );
        action1StartIndex = drl.indexOf( "$b.setName( \"Gargamel\" );",
                                         action1StartIndex );
        assertFalse( action1StartIndex == -1 );
        action1StartIndex = drl.indexOf( "insert( $b );",
                                         action1StartIndex );
        assertFalse( action1StartIndex == -1 );

        action2StartIndex = drl.indexOf( "Smurf fact0 = new Smurf();",
                                         ruleStartIndex );
        assertFalse( action2StartIndex == -1 );
        action2StartIndex = drl.indexOf( "fact0.setName( \"Pupa\" );",
                                         ruleStartIndex );
        assertFalse( action2StartIndex == -1 );
        action2StartIndex = drl.indexOf( "fact0.setAge( 50 );",
                                         ruleStartIndex );
        assertTrue( action2StartIndex == -1 );
        action2StartIndex = drl.indexOf( "insert( fact0 );",
                                         ruleStartIndex );
        assertFalse( action2StartIndex == -1 );
    }

    @Test
    //This test checks a Decision Table involving BRL columns is correctly converted into DRL
    public void testRHSWithBRLColumn_ParseToDRL_MultipleActions() {

        GuidedDecisionTable52 dtable = new GuidedDecisionTable52();

        // All three rows are entered, some columns with optional data
        String[][] data = new String[][]{
                new String[]{ "1", "desc", "Pupa", "50" },
                new String[]{ "2", "desc", "", "50" },
                new String[]{ "3", "desc", "Pupa", "" }
        };

        //Simple (mandatory) columns
        dtable.setRowNumberCol( new RowNumberCol52() );
        dtable.setDescriptionCol( new DescriptionCol52() );

        //BRL Column
        BRLActionColumn brl1 = new BRLActionColumn();

        //BRL Column definition
        List<IAction> brl1Definition = new ArrayList<IAction>();
        ActionInsertFact brl1DefinitionAction1 = new ActionInsertFact( "Baddie" );
        ActionFieldValue brl1DefinitionAction1FieldValue1 = new ActionFieldValue( "name",
                                                                                  "Gargamel",
                                                                                  DataType.TYPE_STRING );
        brl1DefinitionAction1FieldValue1.setNature( BaseSingleFieldConstraint.TYPE_LITERAL );
        brl1DefinitionAction1.addFieldValue( brl1DefinitionAction1FieldValue1 );
        brl1Definition.add( brl1DefinitionAction1 );

        ActionInsertFact brl1DefinitionAction2 = new ActionInsertFact( "Smurf" );
        ActionFieldValue brl1DefinitionAction2FieldValue1 = new ActionFieldValue( "name",
                                                                                  "$name",
                                                                                  DataType.TYPE_STRING );
        brl1DefinitionAction2FieldValue1.setNature( BaseSingleFieldConstraint.TYPE_TEMPLATE );
        brl1DefinitionAction2.addFieldValue( brl1DefinitionAction2FieldValue1 );
        ActionFieldValue brl1DefinitionAction2FieldValue2 = new ActionFieldValue( "age",
                                                                                  "$age",
                                                                                  DataType.TYPE_NUMERIC_INTEGER );
        brl1DefinitionAction2FieldValue2.setNature( BaseSingleFieldConstraint.TYPE_TEMPLATE );
        brl1DefinitionAction2.addFieldValue( brl1DefinitionAction2FieldValue2 );
        brl1Definition.add( brl1DefinitionAction2 );

        brl1.setDefinition( brl1Definition );

        //Setup BRL column bindings
        BRLActionVariableColumn brl1Variable1 = new BRLActionVariableColumn( "$name",
                                                                             DataType.TYPE_STRING,
                                                                             "Person",
                                                                             "name" );
        brl1.getChildColumns().add( brl1Variable1 );
        BRLActionVariableColumn brl1Variable2 = new BRLActionVariableColumn( "$age",
                                                                             DataType.TYPE_NUMERIC_INTEGER,
                                                                             "Person",
                                                                             "age" );
        brl1.getChildColumns().add( brl1Variable2 );

        dtable.getActionCols().add( brl1 );
        dtable.setData( DataUtilities.makeDataLists( data ) );

        //Now to test conversion
        int ruleStartIndex;
        int action1StartIndex;
        int action2StartIndex;
        GuidedDTDRLPersistence p = GuidedDTDRLPersistence.getInstance();
        String drl = p.marshal( dtable );

        //Row 0
        ruleStartIndex = drl.indexOf( "//from row number: 1" );
        assertFalse( ruleStartIndex == -1 );
        action1StartIndex = drl.indexOf( "Baddie fact0 = new Baddie();",
                                         ruleStartIndex );
        assertFalse( action1StartIndex == -1 );
        action1StartIndex = drl.indexOf( "fact0.setName( \"Gargamel\" );",
                                         action1StartIndex );
        assertFalse( action1StartIndex == -1 );
        action1StartIndex = drl.indexOf( "insert( fact0 );",
                                         action1StartIndex );
        assertFalse( action1StartIndex == -1 );

        action2StartIndex = drl.indexOf( "Smurf fact1 = new Smurf();",
                                         ruleStartIndex );
        assertFalse( action2StartIndex == -1 );
        action2StartIndex = drl.indexOf( "fact1.setName( \"Pupa\" );",
                                         action2StartIndex );
        assertFalse( action2StartIndex == -1 );
        action2StartIndex = drl.indexOf( "fact1.setAge( 50 );",
                                         action2StartIndex );
        assertFalse( action2StartIndex == -1 );
        action2StartIndex = drl.indexOf( "insert( fact1 );",
                                         action2StartIndex );
        assertFalse( action2StartIndex == -1 );

        //Row 1
        ruleStartIndex = drl.indexOf( "//from row number: 2" );
        int ruleEndIndex = drl.indexOf( "//from row number: 3" );
        assertFalse( ruleStartIndex == -1 );
        action1StartIndex = drl.indexOf( "Baddie fact0 = new Baddie();",
                                         ruleStartIndex );
        assertFalse( action1StartIndex == -1 );
        action1StartIndex = drl.indexOf( "fact0.setName( \"Gargamel\" );",
                                         action1StartIndex );
        assertFalse( action1StartIndex == -1 );
        action1StartIndex = drl.indexOf( "insert( fact0 );",
                                         action1StartIndex );
        assertFalse( action1StartIndex == -1 );

        action2StartIndex = drl.indexOf( "Smurf fact1 = new Smurf();",
                                         ruleStartIndex );
        assertFalse( action2StartIndex == -1 );
        action2StartIndex = drl.indexOf( "fact1.setName( \"Pupa\" );",
                                         ruleStartIndex );
        assertFalse( action2StartIndex < ruleEndIndex );
        action2StartIndex = drl.indexOf( "fact1.setAge( 50 );",
                                         ruleStartIndex );
        assertFalse( action2StartIndex == -1 );
        action2StartIndex = drl.indexOf( "insert( fact1 );",
                                         ruleStartIndex );
        assertFalse( action2StartIndex == -1 );

        //Row 2
        ruleStartIndex = drl.indexOf( "//from row number: 3" );
        assertFalse( ruleStartIndex == -1 );
        action1StartIndex = drl.indexOf( "Baddie fact0 = new Baddie();",
                                         ruleStartIndex );
        assertFalse( action1StartIndex == -1 );
        action1StartIndex = drl.indexOf( "fact0.setName( \"Gargamel\" );",
                                         action1StartIndex );
        assertFalse( action1StartIndex == -1 );
        action1StartIndex = drl.indexOf( "insert( fact0 );",
                                         action1StartIndex );
        assertFalse( action1StartIndex == -1 );

        action2StartIndex = drl.indexOf( "Smurf fact1 = new Smurf();",
                                         ruleStartIndex );
        assertFalse( action2StartIndex == -1 );
        action2StartIndex = drl.indexOf( "fact1.setName( \"Pupa\" );",
                                         ruleStartIndex );
        assertFalse( action2StartIndex == -1 );
        action2StartIndex = drl.indexOf( "fact1.setAge( 50 );",
                                         ruleStartIndex );
        assertTrue( action2StartIndex == -1 );
        action2StartIndex = drl.indexOf( "insert( fact1 );",
                                         ruleStartIndex );
        assertFalse( action2StartIndex == -1 );
    }

    @Test
    //This test checks a Decision Table involving BRL columns is correctly converted into DRL
    public void testRHSWithBRLColumn_ParseToDRL_NoVariables() {

        GuidedDecisionTable52 dtable = new GuidedDecisionTable52();

        Object[][] data = new Object[][]{
                new Object[]{ "1", "desc", Boolean.TRUE },
                new Object[]{ "2", "desc", Boolean.FALSE }
        };

        //Simple (mandatory) columns
        dtable.setRowNumberCol( new RowNumberCol52() );
        dtable.setDescriptionCol( new DescriptionCol52() );

        //BRL Column
        BRLActionColumn brl1 = new BRLActionColumn();

        //BRL Column definition
        List<IAction> brl1Definition = new ArrayList<IAction>();
        ActionInsertFact brl1DefinitionAction1 = new ActionInsertFact( "Baddie" );
        ActionFieldValue brl1DefinitionAction1FieldValue1 = new ActionFieldValue( "name",
                                                                                  "Gargamel",
                                                                                  DataType.TYPE_STRING );
        brl1DefinitionAction1FieldValue1.setNature( BaseSingleFieldConstraint.TYPE_LITERAL );
        brl1DefinitionAction1.addFieldValue( brl1DefinitionAction1FieldValue1 );
        brl1Definition.add( brl1DefinitionAction1 );

        brl1.setDefinition( brl1Definition );

        //Setup BRL column bindings
        BRLActionVariableColumn brl1Variable1 = new BRLActionVariableColumn( "",
                                                                             DataType.TYPE_BOOLEAN );
        brl1.getChildColumns().add( brl1Variable1 );

        dtable.getActionCols().add( brl1 );
        dtable.setData( DataUtilities.makeDataLists( data ) );

        //Now to test conversion
        int ruleStartIndex;
        int action1StartIndex;
        GuidedDTDRLPersistence p = GuidedDTDRLPersistence.getInstance();
        String drl = p.marshal( dtable );

        //Row 0
        ruleStartIndex = drl.indexOf( "//from row number: 1" );
        assertFalse( ruleStartIndex == -1 );
        action1StartIndex = drl.indexOf( "Baddie fact0 = new Baddie();",
                                         ruleStartIndex );
        assertFalse( action1StartIndex == -1 );
        action1StartIndex = drl.indexOf( "fact0.setName( \"Gargamel\" );",
                                         action1StartIndex );
        assertFalse( action1StartIndex == -1 );
        action1StartIndex = drl.indexOf( "insert( fact0 );",
                                         action1StartIndex );
        assertFalse( action1StartIndex == -1 );

        //Row 1
        ruleStartIndex = drl.indexOf( "//from row number: 2" );
        assertFalse( ruleStartIndex == -1 );
        action1StartIndex = drl.indexOf( "Baddie fact0 = new Baddie();",
                                         ruleStartIndex );
        assertTrue( action1StartIndex == -1 );
        action1StartIndex = drl.indexOf( "fact0.setName( \"Gargamel\" );",
                                         ruleStartIndex );
        assertTrue( action1StartIndex == -1 );
        action1StartIndex = drl.indexOf( "insert( fact0 );",
                                         ruleStartIndex );
        assertTrue( action1StartIndex == -1 );
    }

    @Test
    //This test checks a Decision Table involving BRL columns is correctly converted into DRL
    public void testRHSWithBRLColumn_ParseToDRL_FreeFormLine() {

        GuidedDecisionTable52 dtable = new GuidedDecisionTable52();

        //Row 0 should become an IAction in the resulting RuleModel as it contains values for all Template fields in the BRL Column
        //Row 1 should *NOT* become an IAction in the resulting RuleModel as it does *NOT* contain values for all Template fields in the BRL Column
        //Row 2 should *NOT* become an IAction in the resulting RuleModel as it does *NOT* contain values for all Template fields in the BRL Column
        //Row 3 should *NOT* become an IAction in the resulting RuleModel as it does *NOT* contain values for all Template fields in the BRL Column
        String[][] data = new String[][]{
                new String[]{ "1", "desc", "Pupa", "50" },
                new String[]{ "2", "desc", "", "50" },
                new String[]{ "3", "desc", "Pupa", "" },
                new String[]{ "4", "desc", "", "" }
        };

        //Simple (mandatory) columns
        dtable.setRowNumberCol( new RowNumberCol52() );
        dtable.setDescriptionCol( new DescriptionCol52() );

        //BRL Action
        BRLActionColumn brl1 = new BRLActionColumn();

        //BRL Action definition
        List<IAction> brl1Definition = new ArrayList<IAction>();
        FreeFormLine brl1DefinitionFreeFormLine = new FreeFormLine();
        brl1DefinitionFreeFormLine.setText( "System.out.println( \"name == @{name}, age == @{age}\" );" );

        brl1Definition.add( brl1DefinitionFreeFormLine );

        brl1.setDefinition( brl1Definition );

        //Setup BRL column bindings
        BRLActionVariableColumn brl1Variable1 = new BRLActionVariableColumn( "name",
                                                                             DataType.TYPE_STRING );
        BRLActionVariableColumn brl1Variable2 = new BRLActionVariableColumn( "age",
                                                                             DataType.TYPE_NUMERIC_INTEGER );
        brl1.getChildColumns().add( brl1Variable1 );
        brl1.getChildColumns().add( brl1Variable2 );

        dtable.getActionCols().add( brl1 );
        dtable.setData( DataUtilities.makeDataLists( data ) );

        //Now to test conversion
        int ruleStartIndex;
        int pattern1StartIndex;
        GuidedDTDRLPersistence p = GuidedDTDRLPersistence.getInstance();
        String drl = p.marshal( dtable );

        //Row 0
        ruleStartIndex = drl.indexOf( "//from row number: 1" );
        assertFalse( ruleStartIndex == -1 );
        pattern1StartIndex = drl.indexOf( "System.out.println( \"name == Pupa, age == 50\" );",
                                          ruleStartIndex );
        assertFalse( pattern1StartIndex == -1 );

        //Row 1
        ruleStartIndex = drl.indexOf( "//from row number: 2" );
        assertFalse( ruleStartIndex == -1 );
        pattern1StartIndex = drl.indexOf( "System.out.println(",
                                          ruleStartIndex );
        assertTrue( pattern1StartIndex == -1 );

        //Row 2
        ruleStartIndex = drl.indexOf( "//from row number: 3" );
        assertFalse( ruleStartIndex == -1 );
        pattern1StartIndex = drl.indexOf( "System.out.println(",
                                          ruleStartIndex );
        assertTrue( pattern1StartIndex == -1 );

        //Row 3
        ruleStartIndex = drl.indexOf( "//from row number: 4" );
        assertFalse( ruleStartIndex == -1 );
        pattern1StartIndex = drl.indexOf( "System.out.println(",
                                          ruleStartIndex );
        assertTrue( pattern1StartIndex == -1 );
    }

    @Test
    public void testPackageNameAndImports() throws Exception {
        GuidedDecisionTable52 dt = new GuidedDecisionTable52();
        dt.setPackageName( "org.drools.guvnor.models.guided.dtable.backend" );
        dt.getImports().addImport( new Import( "java.lang.String" ) );

        dt.setTableName( "michael" );

        Pattern52 p1 = new Pattern52();
        p1.setBoundName( "f1" );
        p1.setFactType( "Driver" );

        ConditionCol52 con = new ConditionCol52();
        con.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        con.setFactField( "age" );
        con.setHeader( "Driver f1 age" );
        con.setOperator( "==" );
        p1.getChildColumns().add( con );

        dt.getConditions().add( p1 );

        dt.setData( DataUtilities.makeDataLists( new String[][]{
                new String[]{ "1", "desc", "42" }
        } ) );

        GuidedDTDRLPersistence p = GuidedDTDRLPersistence.getInstance();
        String drl = p.marshal( dt );

        assertTrue( drl.indexOf( "package org.drools.guvnor.models.guided.dtable.backend;" ) == 0 );
        assertTrue( drl.indexOf( "import java.lang.String;" ) > 0 );
    }

    @Test
    public void testLHSNonEmptyStringValues() {
        GuidedDecisionTable52 dt = new GuidedDecisionTable52();
        dt.setTableFormat( GuidedDecisionTable52.TableFormat.EXTENDED_ENTRY );
        dt.setTableName( "extended-entry" );

        Pattern52 p1 = new Pattern52();
        p1.setBoundName( "p1" );
        p1.setFactType( "Smurf" );
        dt.getConditions().add( p1 );

        ConditionCol52 cc1 = new ConditionCol52();
        cc1.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        cc1.setFieldType( DataType.TYPE_STRING );
        cc1.setFactField( "name" );
        cc1.setOperator( "==" );
        p1.getChildColumns().add( cc1 );

        ConditionCol52 cc2 = new ConditionCol52();
        cc2.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        cc2.setFieldType( DataType.TYPE_NUMERIC_INTEGER );
        cc2.setFactField( "age" );
        cc2.setOperator( "==" );
        p1.getChildColumns().add( cc2 );

        dt.setData( DataUtilities.makeDataLists( new Object[][]{
                new Object[]{ 1l, "desc-row1", null, null },
                new Object[]{ 2l, "desc-row2", "   ", 35l },
                new Object[]{ 3l, "desc-row3", "", null },
                new Object[]{ 4l, "desc-row4", "", 35l },
        } ) );

        GuidedDTDRLPersistence p = GuidedDTDRLPersistence.getInstance();
        String drl = p.marshal( dt );

        final String expected = "//from row number: 1\n" +
                "//desc-row1\n" +
                "rule \"Row 1 extended-entry\"\n" +
                "  dialect \"mvel\"\n" +
                "  when\n" +
                "  then\n" +
                "end\n" +
                "//from row number: 2\n" +
                "//desc-row2\n" +
                "rule \"Row 2 extended-entry\"\n" +
                "  dialect \"mvel\"\n" +
                "  when\n" +
                "    p1 : Smurf( age == 35 )\n" +
                "  then\n" +
                "end\n" +
                "//from row number: 3\n" +
                "//desc-row3\n" +
                "rule \"Row 3 extended-entry\"\n" +
                "  dialect \"mvel\"\n" +
                "  when\n" +
                "  then\n" +
                "end\n" +
                "//from row number: 4\n" +
                "//desc-row4\n" +
                "rule \"Row 4 extended-entry\"\n" +
                "  dialect \"mvel\"\n" +
                "  when\n" +
                "    p1 : Smurf( age == 35 )\n" +
                "  then\n" +
                "end";

        assertEqualsIgnoreWhitespace( expected,
                                      drl );
    }

    @Test
    public void testLHSDelimitedNonEmptyStringValues() {
        GuidedDecisionTable52 dt = new GuidedDecisionTable52();
        dt.setTableFormat( GuidedDecisionTable52.TableFormat.EXTENDED_ENTRY );
        dt.setTableName( "extended-entry" );

        Pattern52 p1 = new Pattern52();
        p1.setBoundName( "p1" );
        p1.setFactType( "Smurf" );
        dt.getConditions().add( p1 );

        ConditionCol52 cc1 = new ConditionCol52();
        cc1.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        cc1.setFieldType( DataType.TYPE_STRING );
        cc1.setFactField( "name" );
        cc1.setOperator( "==" );
        p1.getChildColumns().add( cc1 );

        ConditionCol52 cc2 = new ConditionCol52();
        cc2.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        cc2.setFieldType( DataType.TYPE_NUMERIC_INTEGER );
        cc2.setFactField( "age" );
        cc2.setOperator( "==" );
        p1.getChildColumns().add( cc2 );

        dt.setData( DataUtilities.makeDataLists( new Object[][]{
                new Object[]{ 1l, "desc-row1", null, null },
                new Object[]{ 2l, "desc-row2", "\"   \"", 35l },
                new Object[]{ 3l, "desc-row3", "\"\"", null },
                new Object[]{ 4l, "desc-row4", "\"\"", 35l },
        } ) );

        GuidedDTDRLPersistence p = GuidedDTDRLPersistence.getInstance();
        String drl = p.marshal( dt );

        final String expected = "//from row number: 1\n" +
                "//desc-row1\n" +
                "rule \"Row 1 extended-entry\"\n" +
                "  dialect \"mvel\"\n" +
                "  when\n" +
                "  then\n" +
                "end\n" +
                "//from row number: 2\n" +
                "//desc-row2\n" +
                "rule \"Row 2 extended-entry\"\n" +
                "  dialect \"mvel\"\n" +
                "  when\n" +
                "    p1 : Smurf( name == \"   \", age == 35 )\n" +
                "  then\n" +
                "end\n" +
                "//from row number: 3\n" +
                "//desc-row3\n" +
                "rule \"Row 3 extended-entry\"\n" +
                "  dialect \"mvel\"\n" +
                "  when\n" +
                "    p1 : Smurf( name == \"\" )\n" +
                "  then\n" +
                "end\n" +
                "//from row number: 4\n" +
                "//desc-row4\n" +
                "rule \"Row 4 extended-entry\"\n" +
                "  dialect \"mvel\"\n" +
                "  when\n" +
                "    p1 : Smurf( name == \"\", age == 35 )\n" +
                "  then\n" +
                "end";

        assertEqualsIgnoreWhitespace( expected,
                                      drl );
    }

    @Test
    public void testRHSNonEmptyStringValues() {
        GuidedDecisionTable52 dt = new GuidedDecisionTable52();
        dt.setTableFormat( GuidedDecisionTable52.TableFormat.EXTENDED_ENTRY );
        dt.setTableName( "extended-entry" );

        ActionInsertFactCol52 ins1 = new ActionInsertFactCol52();
        ins1.setBoundName( "$f" );
        ins1.setFactType( "Smurf" );
        ins1.setFactField( "name" );
        ins1.setType( DataType.TYPE_STRING );
        dt.getActionCols().add( ins1 );

        ActionInsertFactCol52 ins2 = new ActionInsertFactCol52();
        ins2.setBoundName( "$f" );
        ins2.setFactType( "Smurf" );
        ins2.setFactField( "age" );
        ins2.setType( DataType.TYPE_NUMERIC_INTEGER );
        dt.getActionCols().add( ins2 );

        dt.setData( DataUtilities.makeDataLists( new Object[][]{
                new Object[]{ 1l, "desc-row1", null, null },
                new Object[]{ 2l, "desc-row2", "   ", 35l },
                new Object[]{ 3l, "desc-row3", "", null },
                new Object[]{ 4l, "desc-row4", "", 35l },
        } ) );

        GuidedDTDRLPersistence p = GuidedDTDRLPersistence.getInstance();
        String drl = p.marshal( dt );

        final String expected = "//from row number: 1\n" +
                "//desc-row1\n" +
                "rule \"Row 1 extended-entry\"\n" +
                "  dialect \"mvel\"\n" +
                "  when\n" +
                "  then\n" +
                "end\n" +
                "//from row number: 2\n" +
                "//desc-row2\n" +
                "rule \"Row 2 extended-entry\"\n" +
                "  dialect \"mvel\"\n" +
                "  when\n" +
                "  then\n" +
                "    Smurf $f = new Smurf();\n" +
                "    $f.setAge( 35 );\n" +
                "    insert( $f );\n" +
                "end\n" +
                "//from row number: 3\n" +
                "//desc-row3\n" +
                "rule \"Row 3 extended-entry\"\n" +
                "  dialect \"mvel\"\n" +
                "  when\n" +
                "  then\n" +
                "end\n" +
                "//from row number: 4\n" +
                "//desc-row4\n" +
                "rule \"Row 4 extended-entry\"\n" +
                "  dialect \"mvel\"\n" +
                "  when\n" +
                "  then\n" +
                "    Smurf $f = new Smurf();\n" +
                "    $f.setAge( 35 );\n" +
                "    insert( $f );\n" +
                "end";

        assertEqualsIgnoreWhitespace( expected,
                                      drl );
    }

    @Test
    public void testRHSDelimitedNonEmptyStringValues() {
        GuidedDecisionTable52 dt = new GuidedDecisionTable52();
        dt.setTableFormat( GuidedDecisionTable52.TableFormat.EXTENDED_ENTRY );
        dt.setTableName( "extended-entry" );

        ActionInsertFactCol52 ins1 = new ActionInsertFactCol52();
        ins1.setBoundName( "$f" );
        ins1.setFactType( "Smurf" );
        ins1.setFactField( "name" );
        ins1.setType( DataType.TYPE_STRING );
        dt.getActionCols().add( ins1 );

        ActionInsertFactCol52 ins2 = new ActionInsertFactCol52();
        ins2.setBoundName( "$f" );
        ins2.setFactType( "Smurf" );
        ins2.setFactField( "age" );
        ins2.setType( DataType.TYPE_NUMERIC_INTEGER );
        dt.getActionCols().add( ins2 );

        dt.setData( DataUtilities.makeDataLists( new Object[][]{
                new Object[]{ 1l, "desc-row1", null, null },
                new Object[]{ 2l, "desc-row2", "\"   \"", 35l },
                new Object[]{ 3l, "desc-row3", "\"\"", null },
                new Object[]{ 4l, "desc-row4", "\"\"", 35l },
        } ) );

        GuidedDTDRLPersistence p = GuidedDTDRLPersistence.getInstance();
        String drl = p.marshal( dt );

        final String expected = "//from row number: 1\n" +
                "//desc-row1\n" +
                "rule \"Row 1 extended-entry\"\n" +
                "  dialect \"mvel\"\n" +
                "  when\n" +
                "  then\n" +
                "end\n" +
                "//from row number: 2\n" +
                "//desc-row2\n" +
                "rule \"Row 2 extended-entry\"\n" +
                "  dialect \"mvel\"\n" +
                "  when\n" +
                "  then\n" +
                "    Smurf $f = new Smurf();\n" +
                "    $f.setName( \"   \" );\n" +
                "    $f.setAge( 35 );\n" +
                "    insert( $f );\n" +
                "end\n" +
                "//from row number: 3\n" +
                "//desc-row3\n" +
                "rule \"Row 3 extended-entry\"\n" +
                "  dialect \"mvel\"\n" +
                "  when\n" +
                "  then\n" +
                "    Smurf $f = new Smurf();\n" +
                "    $f.setName( \"\" );\n" +
                "    insert( $f );\n" +
                "end\n" +
                "//from row number: 4\n" +
                "//desc-row4\n" +
                "rule \"Row 4 extended-entry\"\n" +
                "  dialect \"mvel\"\n" +
                "  when\n" +
                "  then\n" +
                "    Smurf $f = new Smurf();\n" +
                "    $f.setName( \"\" );\n" +
                "    $f.setAge( 35 );\n" +
                "    insert( $f );\n" +
                "end";

        assertEqualsIgnoreWhitespace( expected,
                                      drl );
    }

    private void assertEqualsIgnoreWhitespace( final String expected,
                                               final String actual ) {
        final String cleanExpected = expected.replaceAll( "\\s+",
                                                          "" );
        final String cleanActual = actual.replaceAll( "\\s+",
                                                      "" );

        assertEquals( cleanExpected,
                      cleanActual );
    }

}
