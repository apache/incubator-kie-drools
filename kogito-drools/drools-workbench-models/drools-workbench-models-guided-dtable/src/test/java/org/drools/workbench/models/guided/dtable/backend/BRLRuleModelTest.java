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
package org.drools.workbench.models.guided.dtable.backend;

import org.drools.workbench.models.datamodel.oracle.DataType;
import org.drools.workbench.models.datamodel.rule.ActionFieldValue;
import org.drools.workbench.models.datamodel.rule.ActionInsertFact;
import org.drools.workbench.models.datamodel.rule.ActionUpdateField;
import org.drools.workbench.models.datamodel.rule.BaseSingleFieldConstraint;
import org.drools.workbench.models.datamodel.rule.FactPattern;
import org.drools.workbench.models.datamodel.rule.FieldConstraint;
import org.drools.workbench.models.datamodel.rule.FreeFormLine;
import org.drools.workbench.models.datamodel.rule.SingleFieldConstraint;
import org.drools.workbench.models.guided.dtable.backend.util.DataUtilities;
import org.drools.workbench.models.guided.dtable.shared.model.ActionInsertFactCol52;
import org.drools.workbench.models.guided.dtable.shared.model.BRLActionColumn;
import org.drools.workbench.models.guided.dtable.shared.model.BRLActionVariableColumn;
import org.drools.workbench.models.guided.dtable.shared.model.BRLConditionColumn;
import org.drools.workbench.models.guided.dtable.shared.model.BRLConditionVariableColumn;
import org.drools.workbench.models.guided.dtable.shared.model.BRLRuleModel;
import org.drools.workbench.models.guided.dtable.shared.model.ConditionCol52;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.models.guided.dtable.shared.model.Pattern52;
import org.drools.workbench.models.guided.dtable.shared.model.adaptors.ActionInsertFactCol52ActionInsertFactAdaptor;
import org.drools.workbench.models.guided.dtable.shared.model.adaptors.ActionInsertFactCol52ActionInsertLogicalFactAdaptor;
import org.drools.workbench.models.guided.dtable.shared.model.adaptors.Pattern52FactPatternAdaptor;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Tests relating to the extended function of Fact\Field bindings
 */
public class BRLRuleModelTest {

    @Test
    public void testOnlyDecisionTableColumns() {
        GuidedDecisionTable52 dt = new GuidedDecisionTable52();

        Pattern52 p1 = new Pattern52();
        p1.setFactType( "Driver" );
        p1.setBoundName( "$p1" );

        ConditionCol52 c1 = new ConditionCol52();
        c1.setFactField( "name" );
        c1.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        c1.setBinding( "$c1" );

        p1.getChildColumns().add( c1 );
        dt.getConditions().add( p1 );

        ActionInsertFactCol52 ins = new ActionInsertFactCol52();
        ins.setBoundName( "$ins" );
        ins.setFactField( "rating" );
        ins.setFactType( "Person" );
        ins.setType( DataType.TYPE_STRING );
        dt.getActionCols().add( ins );

        BRLRuleModel model = new BRLRuleModel( dt );

        assertNotNull( model.getAllVariables() );
        assertEquals( 3,
                      model.getAllVariables().size() );
        assertTrue( model.getAllVariables().contains( "$p1" ) );
        assertTrue( model.getAllVariables().contains( "$c1" ) );
        assertTrue( model.getAllVariables().contains( "$ins" ) );
    }

    @Test
    public void testDecisionTableColumnsWithLHS() {
        GuidedDecisionTable52 dt = new GuidedDecisionTable52();

        Pattern52 p1 = new Pattern52();
        p1.setFactType( "Driver" );
        p1.setBoundName( "$p1" );

        ConditionCol52 c1 = new ConditionCol52();
        c1.setFactField( "name" );
        c1.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        c1.setBinding( "$c1" );

        p1.getChildColumns().add( c1 );
        dt.getConditions().add( p1 );

        BRLConditionColumn brlCondition = new BRLConditionColumn();
        FactPattern fp = new FactPattern( "Driver" );
        fp.setBoundName( "$brl1" );

        SingleFieldConstraint sfc1 = new SingleFieldConstraint();
        sfc1.setFieldBinding( "$sfc1" );
        sfc1.setOperator( "==" );
        sfc1.setFactType( "Driver" );
        sfc1.setFieldName( "name" );
        sfc1.setFieldType( DataType.TYPE_STRING );

        fp.addConstraint( sfc1 );
        brlCondition.getDefinition().add( fp );
        dt.getConditions().add( brlCondition );

        ActionInsertFactCol52 ins = new ActionInsertFactCol52();
        ins.setBoundName( "$ins" );
        ins.setFactField( "rating" );
        ins.setFactType( "Person" );
        ins.setType( DataType.TYPE_STRING );
        dt.getActionCols().add( ins );

        BRLRuleModel model = new BRLRuleModel( dt );

        assertNotNull( model.getAllVariables() );
        assertEquals( 5,
                      model.getAllVariables().size() );
        assertTrue( model.getAllVariables().contains( "$p1" ) );
        assertTrue( model.getAllVariables().contains( "$c1" ) );
        assertTrue( model.getAllVariables().contains( "$ins" ) );
        assertTrue( model.getAllVariables().contains( "$brl1" ) );
        assertTrue( model.getAllVariables().contains( "$sfc1" ) );
    }

    @Test
    public void testDecisionTableColumnsWithLHSBoundFacts() {
        GuidedDecisionTable52 dt = new GuidedDecisionTable52();

        Pattern52 p1 = new Pattern52();
        p1.setFactType( "Driver" );
        p1.setBoundName( "$p1" );

        ConditionCol52 c1 = new ConditionCol52();
        c1.setFactField( "name" );
        c1.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        c1.setBinding( "$c1" );

        p1.getChildColumns().add( c1 );
        dt.getConditions().add( p1 );

        BRLConditionColumn brlCondition = new BRLConditionColumn();
        FactPattern fp = new FactPattern( "Driver" );
        fp.setBoundName( "$brl1" );

        SingleFieldConstraint sfc1 = new SingleFieldConstraint();
        sfc1.setFieldBinding( "$sfc1" );
        sfc1.setOperator( "==" );
        sfc1.setFactType( "Driver" );
        sfc1.setFieldName( "name" );
        sfc1.setFieldType( DataType.TYPE_STRING );

        fp.addConstraint( sfc1 );
        brlCondition.getDefinition().add( fp );
        dt.getConditions().add( brlCondition );

        ActionInsertFactCol52 ins = new ActionInsertFactCol52();
        ins.setBoundName( "$ins" );
        ins.setFactField( "rating" );
        ins.setFactType( "Person" );
        ins.setType( DataType.TYPE_STRING );
        dt.getActionCols().add( ins );

        BRLRuleModel model = new BRLRuleModel( dt );

        assertNotNull( model.getLHSBoundFacts() );
        assertEquals( 2,
                      model.getLHSBoundFacts().size() );
        assertTrue( model.getLHSBoundFacts().contains( "$p1" ) );
        assertTrue( model.getLHSBoundFacts().contains( "$brl1" ) );

        assertNotNull( model.getLHSBindingType( "$p1" ) );
        assertEquals( "Driver",
                      model.getLHSBindingType( "$p1" ) );
        assertNotNull( model.getLHSBindingType( "$brl1" ) );
        assertEquals( "Driver",
                      model.getLHSBindingType( "$brl1" ) );

        FactPattern r1 = model.getLHSBoundFact( "$p1" );
        assertNotNull( r1 );
        assertTrue( r1 instanceof Pattern52FactPatternAdaptor );
        Pattern52FactPatternAdaptor raif1 = (Pattern52FactPatternAdaptor) r1;
        assertEquals( "Driver",
                      raif1.getFactType() );

        FactPattern r2 = model.getLHSBoundFact( "$brl1" );
        assertNotNull( r2 );
        assertEquals( "Driver",
                      r2.getFactType() );
    }

    @Test
    public void testDecisionTableColumnsWithLHSBoundFields() {
        GuidedDecisionTable52 dt = new GuidedDecisionTable52();

        Pattern52 p1 = new Pattern52();
        p1.setFactType( "Driver" );
        p1.setBoundName( "$p1" );

        ConditionCol52 c1 = new ConditionCol52();
        c1.setFactField( "name" );
        c1.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        c1.setBinding( "$c1" );

        p1.getChildColumns().add( c1 );
        dt.getConditions().add( p1 );

        BRLConditionColumn brlCondition = new BRLConditionColumn();
        FactPattern fp = new FactPattern( "Driver" );
        fp.setBoundName( "$brl1" );

        SingleFieldConstraint sfc1 = new SingleFieldConstraint();
        sfc1.setFieldBinding( "$sfc1" );
        sfc1.setOperator( "==" );
        sfc1.setFactType( "Driver" );
        sfc1.setFieldName( "name" );
        sfc1.setFieldType( DataType.TYPE_STRING );

        fp.addConstraint( sfc1 );
        brlCondition.getDefinition().add( fp );
        dt.getConditions().add( brlCondition );

        ActionInsertFactCol52 ins = new ActionInsertFactCol52();
        ins.setBoundName( "$ins" );
        ins.setFactField( "rating" );
        ins.setFactType( "Person" );
        ins.setType( DataType.TYPE_STRING );
        dt.getActionCols().add( ins );

        BRLRuleModel model = new BRLRuleModel( dt );

        FieldConstraint fcr1 = model.getLHSBoundField( "$sfc1" );
        assertNotNull( fcr1 );
        assertTrue( fcr1 instanceof SingleFieldConstraint );
        SingleFieldConstraint fcr1sfc = (SingleFieldConstraint) fcr1;
        assertEquals( "name",
                      fcr1sfc.getFieldName() );
        assertEquals( DataType.TYPE_STRING,
                      fcr1sfc.getFieldType() );
    }

    @Test
    public void testDecisionTableColumnsWithRHS() {
        GuidedDecisionTable52 dt = new GuidedDecisionTable52();

        Pattern52 p1 = new Pattern52();
        p1.setFactType( "Driver" );
        p1.setBoundName( "$p1" );

        ConditionCol52 c1 = new ConditionCol52();
        c1.setFactField( "name" );
        c1.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        c1.setBinding( "$c1" );

        p1.getChildColumns().add( c1 );
        dt.getConditions().add( p1 );

        ActionInsertFactCol52 ins = new ActionInsertFactCol52();
        ins.setBoundName( "$ins" );
        ins.setFactField( "rating" );
        ins.setFactType( "Person" );
        ins.setType( DataType.TYPE_STRING );
        dt.getActionCols().add( ins );

        BRLActionColumn brlAction = new BRLActionColumn();
        ActionInsertFact aif = new ActionInsertFact( "Person" );
        aif.setBoundName( "$aif" );
        aif.addFieldValue( new ActionFieldValue( "rating",
                                                 null,
                                                 DataType.TYPE_STRING ) );
        aif.getFieldValues()[ 0 ].setNature( BaseSingleFieldConstraint.TYPE_LITERAL );

        brlAction.getDefinition().add( aif );
        dt.getActionCols().add( brlAction );

        BRLRuleModel model = new BRLRuleModel( dt );

        assertNotNull( model.getAllVariables() );
        assertEquals( 4,
                      model.getAllVariables().size() );
        assertTrue( model.getAllVariables().contains( "$p1" ) );
        assertTrue( model.getAllVariables().contains( "$c1" ) );
        assertTrue( model.getAllVariables().contains( "$ins" ) );
        assertTrue( model.getAllVariables().contains( "$aif" ) );

        assertNotNull( model.getRHSBoundFacts() );
        assertEquals( 2,
                      model.getRHSBoundFacts().size() );
        assertTrue( model.getRHSBoundFacts().contains( "$ins" ) );
        assertTrue( model.getRHSBoundFacts().contains( "$aif" ) );

        ActionInsertFact r1 = model.getRHSBoundFact( "$ins" );
        assertNotNull( r1 );
        assertTrue( r1 instanceof ActionInsertFactCol52ActionInsertFactAdaptor );
        ActionInsertFactCol52ActionInsertFactAdaptor raif1 = (ActionInsertFactCol52ActionInsertFactAdaptor) r1;
        assertEquals( "Person",
                      raif1.getFactType() );
        assertEquals( "rating",
                      raif1.getFieldValues()[ 0 ].getField() );
        assertEquals( DataType.TYPE_STRING,
                      raif1.getFieldValues()[ 0 ].getType() );
        assertNull( raif1.getFieldValues()[ 0 ].getValue() );
        assertEquals( BaseSingleFieldConstraint.TYPE_LITERAL,
                      raif1.getFieldValues()[ 0 ].getNature() );

        ActionInsertFact r2 = model.getRHSBoundFact( "$aif" );
        assertNotNull( r2 );
        assertTrue( r2 instanceof ActionInsertFact );
        ActionInsertFact raif2 = (ActionInsertFact) r2;
        assertEquals( "Person",
                      raif2.getFactType() );
        assertEquals( "rating",
                      raif2.getFieldValues()[ 0 ].getField() );
        assertEquals( DataType.TYPE_STRING,
                      raif2.getFieldValues()[ 0 ].getType() );
        assertNull( raif2.getFieldValues()[ 0 ].getValue() );
        assertEquals( BaseSingleFieldConstraint.TYPE_LITERAL,
                      raif2.getFieldValues()[ 0 ].getNature() );
    }

    @Test
    public void testDecisionTableColumnsWithRHSBoundFacts() {
        GuidedDecisionTable52 dt = new GuidedDecisionTable52();

        Pattern52 p1 = new Pattern52();
        p1.setFactType( "Driver" );
        p1.setBoundName( "$p1" );

        ConditionCol52 c1 = new ConditionCol52();
        c1.setFactField( "name" );
        c1.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        c1.setBinding( "$c1" );

        p1.getChildColumns().add( c1 );
        dt.getConditions().add( p1 );

        ActionInsertFactCol52 ins = new ActionInsertFactCol52();
        ins.setBoundName( "$ins" );
        ins.setFactField( "rating" );
        ins.setFactType( "Person" );
        ins.setType( DataType.TYPE_STRING );
        dt.getActionCols().add( ins );

        ActionInsertFactCol52 ins2 = new ActionInsertFactCol52();
        ins2.setInsertLogical( true );
        ins2.setBoundName( "$ins2" );
        ins2.setFactField( "rating2" );
        ins2.setFactType( "Person2" );
        ins2.setType( DataType.TYPE_STRING );
        dt.getActionCols().add( ins2 );

        BRLActionColumn brlAction = new BRLActionColumn();
        ActionInsertFact aif = new ActionInsertFact( "Person" );
        aif.setBoundName( "$aif" );
        aif.addFieldValue( new ActionFieldValue( "rating",
                                                 null,
                                                 DataType.TYPE_STRING ) );
        aif.getFieldValues()[ 0 ].setNature( BaseSingleFieldConstraint.TYPE_LITERAL );

        brlAction.getDefinition().add( aif );
        dt.getActionCols().add( brlAction );

        BRLRuleModel model = new BRLRuleModel( dt );

        assertNotNull( model.getRHSBoundFacts() );
        assertEquals( 3,
                      model.getRHSBoundFacts().size() );
        assertTrue( model.getRHSBoundFacts().contains( "$ins" ) );
        assertTrue( model.getRHSBoundFacts().contains( "$ins2" ) );
        assertTrue( model.getRHSBoundFacts().contains( "$aif" ) );

        ActionInsertFact r1 = model.getRHSBoundFact( "$ins" );
        assertNotNull( r1 );
        assertTrue( r1 instanceof ActionInsertFactCol52ActionInsertFactAdaptor );
        ActionInsertFactCol52ActionInsertFactAdaptor raif1 = (ActionInsertFactCol52ActionInsertFactAdaptor) r1;
        assertEquals( "Person",
                      raif1.getFactType() );
        assertEquals( "rating",
                      raif1.getFieldValues()[ 0 ].getField() );
        assertEquals( DataType.TYPE_STRING,
                      raif1.getFieldValues()[ 0 ].getType() );
        assertNull( raif1.getFieldValues()[ 0 ].getValue() );
        assertEquals( BaseSingleFieldConstraint.TYPE_LITERAL,
                      raif1.getFieldValues()[ 0 ].getNature() );

        ActionInsertFact r2 = model.getRHSBoundFact( "$ins2" );
        assertNotNull( r2 );
        assertTrue( r2 instanceof ActionInsertFactCol52ActionInsertLogicalFactAdaptor );
        ActionInsertFactCol52ActionInsertLogicalFactAdaptor raif2 = (ActionInsertFactCol52ActionInsertLogicalFactAdaptor) r2;
        assertEquals( "Person2",
                      raif2.getFactType() );
        assertEquals( "rating2",
                      raif2.getFieldValues()[ 0 ].getField() );
        assertEquals( DataType.TYPE_STRING,
                      raif2.getFieldValues()[ 0 ].getType() );
        assertNull( raif2.getFieldValues()[ 0 ].getValue() );
        assertEquals( BaseSingleFieldConstraint.TYPE_LITERAL,
                      raif2.getFieldValues()[ 0 ].getNature() );

        ActionInsertFact r3 = model.getRHSBoundFact( "$aif" );
        assertNotNull( r3 );
        assertTrue( r3 instanceof ActionInsertFact );
        ActionInsertFact raif3 = (ActionInsertFact) r3;
        assertEquals( "Person",
                      raif3.getFactType() );
        assertEquals( "rating",
                      raif3.getFieldValues()[ 0 ].getField() );
        assertEquals( DataType.TYPE_STRING,
                      raif3.getFieldValues()[ 0 ].getType() );
        assertNull( raif3.getFieldValues()[ 0 ].getValue() );
        assertEquals( BaseSingleFieldConstraint.TYPE_LITERAL,
                      raif3.getFieldValues()[ 0 ].getNature() );
    }

    @Test
    public void testRuleModelLHSBoundFacts() {
        GuidedDecisionTable52 dt = new GuidedDecisionTable52();

        //Setup Decision Table columns
        Pattern52 p1 = new Pattern52();
        p1.setFactType( "Driver" );
        p1.setBoundName( "$p1" );

        ConditionCol52 c1 = new ConditionCol52();
        c1.setFactField( "name" );
        c1.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        c1.setBinding( "$c1" );

        p1.getChildColumns().add( c1 );
        dt.getConditions().add( p1 );

        //Setup RuleModel columns (new BRLConditionColumn being added)
        BRLRuleModel model = new BRLRuleModel( dt );
        FactPattern fp = new FactPattern( "Driver" );
        fp.setBoundName( "$brl1" );

        SingleFieldConstraint sfc1 = new SingleFieldConstraint();
        sfc1.setFieldBinding( "$sfc1" );
        sfc1.setOperator( "==" );
        sfc1.setFactType( "Driver" );
        sfc1.setFieldName( "name" );
        sfc1.setFieldType( DataType.TYPE_STRING );

        fp.addConstraint( sfc1 );
        model.addLhsItem( fp );

        //Checks
        assertNotNull( model.getLHSBoundFacts() );
        assertEquals( 2,
                      model.getLHSBoundFacts().size() );
        assertTrue( model.getLHSBoundFacts().contains( "$p1" ) );
        assertTrue( model.getLHSBoundFacts().contains( "$brl1" ) );

        assertNotNull( model.getLHSBindingType( "$p1" ) );
        assertEquals( "Driver",
                      model.getLHSBindingType( "$p1" ) );
        assertNotNull( model.getLHSBindingType( "$brl1" ) );
        assertEquals( "Driver",
                      model.getLHSBindingType( "$brl1" ) );

        FactPattern r1 = model.getLHSBoundFact( "$p1" );
        assertNotNull( r1 );
        assertTrue( r1 instanceof Pattern52FactPatternAdaptor );
        Pattern52FactPatternAdaptor raif1 = (Pattern52FactPatternAdaptor) r1;
        assertEquals( "Driver",
                      raif1.getFactType() );

        FactPattern r2 = model.getLHSBoundFact( "$brl1" );
        assertNotNull( r2 );
        assertEquals( "Driver",
                      r2.getFactType() );
    }

    @Test
    public void testRuleModelLHSBoundFacts_NoDuplicates() {
        GuidedDecisionTable52 dt = new GuidedDecisionTable52();

        //Setup Decision Table columns (with existing BRLConditionColumn)
        Pattern52 p1 = new Pattern52();
        p1.setFactType( "Driver" );
        p1.setBoundName( "$p1" );

        ConditionCol52 c1 = new ConditionCol52();
        c1.setFactField( "name" );
        c1.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        c1.setBinding( "$c1" );

        p1.getChildColumns().add( c1 );
        dt.getConditions().add( p1 );

        BRLConditionColumn brlCondition = new BRLConditionColumn();
        FactPattern fp1 = new FactPattern( "Driver" );
        fp1.setBoundName( "$brl1" );

        SingleFieldConstraint sfc1 = new SingleFieldConstraint();
        sfc1.setFieldBinding( "$sfc1" );
        sfc1.setOperator( "==" );
        sfc1.setFactType( "Driver" );
        sfc1.setFieldName( "name" );
        sfc1.setFieldType( DataType.TYPE_STRING );

        fp1.addConstraint( sfc1 );
        brlCondition.getDefinition().add( fp1 );
        dt.getConditions().add( brlCondition );

        //Setup RuleModel columns (existing BRLConditionColumn being edited)
        BRLRuleModel model = new BRLRuleModel( dt );
        FactPattern fp2 = new FactPattern( "Driver" );
        fp2.setBoundName( "$brl1" );

        SingleFieldConstraint sfc2 = new SingleFieldConstraint();
        sfc2.setFieldBinding( "$sfc1" );
        sfc2.setOperator( "==" );
        sfc2.setFactType( "Driver" );
        sfc2.setFieldName( "name" );
        sfc2.setFieldType( DataType.TYPE_STRING );

        fp2.addConstraint( sfc2 );
        model.addLhsItem( fp2 );

        //Checks
        assertNotNull( model.getLHSBoundFacts() );
        assertEquals( 2,
                      model.getLHSBoundFacts().size() );
        assertTrue( model.getLHSBoundFacts().contains( "$p1" ) );
        assertTrue( model.getLHSBoundFacts().contains( "$brl1" ) );

        assertNotNull( model.getLHSBindingType( "$p1" ) );
        assertEquals( "Driver",
                      model.getLHSBindingType( "$p1" ) );
        assertNotNull( model.getLHSBindingType( "$brl1" ) );
        assertEquals( "Driver",
                      model.getLHSBindingType( "$brl1" ) );

        FactPattern r1 = model.getLHSBoundFact( "$p1" );
        assertNotNull( r1 );
        assertTrue( r1 instanceof Pattern52FactPatternAdaptor );
        Pattern52FactPatternAdaptor raif1 = (Pattern52FactPatternAdaptor) r1;
        assertEquals( "Driver",
                      raif1.getFactType() );

        FactPattern r2 = model.getLHSBoundFact( "$brl1" );
        assertNotNull( r2 );
        assertEquals( "Driver",
                      r2.getFactType() );
    }

    @Test
    public void testRuleModelRHSBoundFacts() {
        GuidedDecisionTable52 dt = new GuidedDecisionTable52();

        //Setup Decision Table columns
        ActionInsertFactCol52 ins = new ActionInsertFactCol52();
        ins.setBoundName( "$ins" );
        ins.setFactField( "rating" );
        ins.setFactType( "Person" );
        ins.setType( DataType.TYPE_STRING );
        dt.getActionCols().add( ins );

        //Setup RuleModel columns (new BRLActionColumn being added)
        BRLRuleModel model = new BRLRuleModel( dt );
        ActionInsertFact aif = new ActionInsertFact( "Person" );
        aif.setBoundName( "$aif" );
        aif.addFieldValue( new ActionFieldValue( "rating",
                                                 null,
                                                 DataType.TYPE_STRING ) );
        aif.getFieldValues()[ 0 ].setNature( BaseSingleFieldConstraint.TYPE_LITERAL );
        model.addRhsItem( aif );

        //Checks
        assertNotNull( model.getRHSBoundFacts() );
        assertEquals( 2,
                      model.getRHSBoundFacts().size() );
        assertTrue( model.getRHSBoundFacts().contains( "$ins" ) );
        assertTrue( model.getRHSBoundFacts().contains( "$aif" ) );

        ActionInsertFact r1 = model.getRHSBoundFact( "$ins" );
        assertNotNull( r1 );
        assertTrue( r1 instanceof ActionInsertFactCol52ActionInsertFactAdaptor );
        ActionInsertFactCol52ActionInsertFactAdaptor raif1 = (ActionInsertFactCol52ActionInsertFactAdaptor) r1;
        assertEquals( "Person",
                      raif1.getFactType() );
        assertEquals( "rating",
                      raif1.getFieldValues()[ 0 ].getField() );
        assertEquals( DataType.TYPE_STRING,
                      raif1.getFieldValues()[ 0 ].getType() );
        assertNull( raif1.getFieldValues()[ 0 ].getValue() );
        assertEquals( BaseSingleFieldConstraint.TYPE_LITERAL,
                      raif1.getFieldValues()[ 0 ].getNature() );

        ActionInsertFact r2 = model.getRHSBoundFact( "$aif" );
        assertNotNull( r2 );
        assertTrue( r2 instanceof ActionInsertFact );
        ActionInsertFact raif2 = (ActionInsertFact) r2;
        assertEquals( "Person",
                      raif2.getFactType() );
        assertEquals( "rating",
                      raif2.getFieldValues()[ 0 ].getField() );
        assertEquals( DataType.TYPE_STRING,
                      raif2.getFieldValues()[ 0 ].getType() );
        assertNull( raif2.getFieldValues()[ 0 ].getValue() );
        assertEquals( BaseSingleFieldConstraint.TYPE_LITERAL,
                      raif2.getFieldValues()[ 0 ].getNature() );
    }

    @Test
    public void testRuleModelRHSBoundFacts_NoDuplicates() {
        GuidedDecisionTable52 dt = new GuidedDecisionTable52();

        //Setup Decision Table columns (with existing BRLActionColumn)
        ActionInsertFactCol52 ins = new ActionInsertFactCol52();
        ins.setBoundName( "$ins" );
        ins.setFactField( "rating" );
        ins.setFactType( "Person" );
        ins.setType( DataType.TYPE_STRING );
        dt.getActionCols().add( ins );

        BRLActionColumn brlAction = new BRLActionColumn();
        ActionInsertFact aif1 = new ActionInsertFact( "Person" );
        aif1.setBoundName( "$aif" );
        aif1.addFieldValue( new ActionFieldValue( "rating",
                                                  null,
                                                  DataType.TYPE_STRING ) );
        aif1.getFieldValues()[ 0 ].setNature( BaseSingleFieldConstraint.TYPE_LITERAL );

        brlAction.getDefinition().add( aif1 );
        dt.getActionCols().add( brlAction );

        //Setup RuleModel columns (existing BRLActionColumn being edited)
        BRLRuleModel model = new BRLRuleModel( dt );
        ActionInsertFact aif2 = new ActionInsertFact( "Person" );
        aif2.setBoundName( "$aif" );
        aif2.addFieldValue( new ActionFieldValue( "rating",
                                                  null,
                                                  DataType.TYPE_STRING ) );
        aif2.getFieldValues()[ 0 ].setNature( BaseSingleFieldConstraint.TYPE_LITERAL );
        model.addRhsItem( aif2 );

        //Checks
        assertNotNull( model.getRHSBoundFacts() );
        assertEquals( 2,
                      model.getRHSBoundFacts().size() );
        assertTrue( model.getRHSBoundFacts().contains( "$ins" ) );
        assertTrue( model.getRHSBoundFacts().contains( "$aif" ) );

        ActionInsertFact r1 = model.getRHSBoundFact( "$ins" );
        assertNotNull( r1 );
        assertTrue( r1 instanceof ActionInsertFactCol52ActionInsertFactAdaptor );
        ActionInsertFactCol52ActionInsertFactAdaptor raif1 = (ActionInsertFactCol52ActionInsertFactAdaptor) r1;
        assertEquals( "Person",
                      raif1.getFactType() );
        assertEquals( "rating",
                      raif1.getFieldValues()[ 0 ].getField() );
        assertEquals( DataType.TYPE_STRING,
                      raif1.getFieldValues()[ 0 ].getType() );
        assertNull( raif1.getFieldValues()[ 0 ].getValue() );
        assertEquals( BaseSingleFieldConstraint.TYPE_LITERAL,
                      raif1.getFieldValues()[ 0 ].getNature() );

        ActionInsertFact r2 = model.getRHSBoundFact( "$aif" );
        assertNotNull( r2 );
        assertTrue( r2 instanceof ActionInsertFact );
        ActionInsertFact raif2 = (ActionInsertFact) r2;
        assertEquals( "Person",
                      raif2.getFactType() );
        assertEquals( "rating",
                      raif2.getFieldValues()[ 0 ].getField() );
        assertEquals( DataType.TYPE_STRING,
                      raif2.getFieldValues()[ 0 ].getType() );
        assertNull( raif2.getFieldValues()[ 0 ].getValue() );
        assertEquals( BaseSingleFieldConstraint.TYPE_LITERAL,
                      raif2.getFieldValues()[ 0 ].getNature() );
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

        BRLActionColumn brlAction1 = new BRLActionColumn();
        ActionUpdateField auf1 = new ActionUpdateField( "x" );
        auf1.addFieldValue( new ActionFieldValue( "age",
                                                  "$age",
                                                  DataType.TYPE_NUMERIC_INTEGER ) );
        auf1.getFieldValues()[ 0 ].setNature( BaseSingleFieldConstraint.TYPE_TEMPLATE );

        brlAction1.getDefinition().add( auf1 );
        brlAction1.getChildColumns().add( new BRLActionVariableColumn( "$age",
                                                                       DataType.TYPE_NUMERIC_INTEGER,
                                                                       "Context",
                                                                       "age" ) );
        dt.getActionCols().add( brlAction1 );

        BRLActionColumn brlAction2 = new BRLActionColumn();
        ActionUpdateField auf2 = new ActionUpdateField( "x" );
        auf2.addFieldValue( new ActionFieldValue( "name",
                                                  "$name",
                                                  DataType.TYPE_STRING ) );
        auf2.getFieldValues()[ 0 ].setNature( BaseSingleFieldConstraint.TYPE_TEMPLATE );

        brlAction2.getDefinition().add( auf2 );
        brlAction2.getChildColumns().add( new BRLActionVariableColumn( "$name",
                                                                       DataType.TYPE_STRING,
                                                                       "Context",
                                                                       "name" ) );
        dt.getActionCols().add( brlAction2 );

        dt.setData( DataUtilities.makeDataLists( new String[][]{
                new String[]{ "1", "desc", "x", "55", "Fred" }
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

        dt.setData( DataUtilities.makeDataLists( new String[][]{
                new String[]{ "1", "desc", "x", "", "Fred" }
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

        dt.setData( DataUtilities.makeDataLists( new String[][]{
                new String[]{ "1", "desc", "x", "55", "" }
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
    public void testUpdateModifyMultipleFieldsWithMultipleSkipped1() {
        GuidedDecisionTable52 dt = new GuidedDecisionTable52();

        Pattern52 p1 = new Pattern52();
        p1.setBoundName( "x" );
        p1.setFactType( "Context" );

        ConditionCol52 c = new ConditionCol52();
        c.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        p1.getChildColumns().add( c );
        dt.getConditions().add( p1 );

        BRLActionColumn brlAction1 = new BRLActionColumn();
        ActionUpdateField auf1 = new ActionUpdateField( "x" );
        auf1.addFieldValue( new ActionFieldValue( "f1",
                                                  "$f1",
                                                  DataType.TYPE_STRING ) );
        auf1.getFieldValues()[ 0 ].setNature( BaseSingleFieldConstraint.TYPE_TEMPLATE );

        brlAction1.getDefinition().add( auf1 );
        brlAction1.getChildColumns().add( new BRLActionVariableColumn( "$f1",
                                                                       DataType.TYPE_STRING,
                                                                       "Context",
                                                                       "f1" ) );
        ActionUpdateField auf2 = new ActionUpdateField( "x" );
        auf2.addFieldValue( new ActionFieldValue( "f2",
                                                  "$f2",
                                                  DataType.TYPE_STRING ) );
        auf2.getFieldValues()[ 0 ].setNature( BaseSingleFieldConstraint.TYPE_TEMPLATE );

        brlAction1.getDefinition().add( auf2 );
        brlAction1.getChildColumns().add( new BRLActionVariableColumn( "$f2",
                                                                       DataType.TYPE_STRING,
                                                                       "Context",
                                                                       "f2" ) );

        ActionUpdateField auf3 = new ActionUpdateField( "x" );
        auf3.addFieldValue( new ActionFieldValue( "f3",
                                                  "$f3",
                                                  DataType.TYPE_STRING ) );
        auf3.getFieldValues()[ 0 ].setNature( BaseSingleFieldConstraint.TYPE_TEMPLATE );

        brlAction1.getDefinition().add( auf3 );
        brlAction1.getChildColumns().add( new BRLActionVariableColumn( "$f3",
                                                                       DataType.TYPE_STRING,
                                                                       "Context",
                                                                       "f3" ) );

        dt.getActionCols().add( brlAction1 );

        dt.setData( DataUtilities.makeDataLists( new String[][]{
                new String[]{ "1", "desc", "x", "v1", "v2", "v3" }
        } ) );
        String drl = GuidedDTDRLPersistence.getInstance().marshal( dt );
        final String expected = "//from row number: 1\n" +
                "//desc\n" +
                "rule \"Row 1 null\"\n" +
                "dialect \"mvel\"\n" +
                "when\n" +
                "  x : Context( )\n" +
                "then\n" +
                "  modify( x ) {\n" +
                "    setF1( \"v1\" ), \n" +
                "    setF2( \"v2\" ),\n" +
                "    setF3( \"v3\" )\n" +
                "}\n" +
                "end\n";

        assertEqualsIgnoreWhitespace( expected,
                                      drl );
    }

    @Test
    public void testUpdateModifyMultipleFieldsWithMultipleSkipped2() {
        GuidedDecisionTable52 dt = new GuidedDecisionTable52();

        Pattern52 p1 = new Pattern52();
        p1.setBoundName( "x" );
        p1.setFactType( "Context" );

        ConditionCol52 c = new ConditionCol52();
        c.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        p1.getChildColumns().add( c );
        dt.getConditions().add( p1 );

        BRLActionColumn brlAction1 = new BRLActionColumn();
        ActionUpdateField auf1 = new ActionUpdateField( "x" );
        auf1.addFieldValue( new ActionFieldValue( "f1",
                                                  "$f1",
                                                  DataType.TYPE_STRING ) );
        auf1.getFieldValues()[ 0 ].setNature( BaseSingleFieldConstraint.TYPE_TEMPLATE );

        brlAction1.getDefinition().add( auf1 );
        brlAction1.getChildColumns().add( new BRLActionVariableColumn( "$f1",
                                                                       DataType.TYPE_STRING,
                                                                       "Context",
                                                                       "f1" ) );
        ActionUpdateField auf2 = new ActionUpdateField( "x" );
        auf2.addFieldValue( new ActionFieldValue( "f2",
                                                  "$f2",
                                                  DataType.TYPE_STRING ) );
        auf2.getFieldValues()[ 0 ].setNature( BaseSingleFieldConstraint.TYPE_TEMPLATE );

        brlAction1.getDefinition().add( auf2 );
        brlAction1.getChildColumns().add( new BRLActionVariableColumn( "$f2",
                                                                       DataType.TYPE_STRING,
                                                                       "Context",
                                                                       "f2" ) );

        ActionUpdateField auf3 = new ActionUpdateField( "x" );
        auf3.addFieldValue( new ActionFieldValue( "f3",
                                                  "$f3",
                                                  DataType.TYPE_STRING ) );
        auf3.getFieldValues()[ 0 ].setNature( BaseSingleFieldConstraint.TYPE_TEMPLATE );

        brlAction1.getDefinition().add( auf3 );
        brlAction1.getChildColumns().add( new BRLActionVariableColumn( "$f3",
                                                                       DataType.TYPE_STRING,
                                                                       "Context",
                                                                       "f3" ) );

        dt.getActionCols().add( brlAction1 );

        dt.setData( DataUtilities.makeDataLists( new String[][]{
                new String[]{ "1", "desc", "x", null, "v2", "v3" }
        } ) );
        String drl = GuidedDTDRLPersistence.getInstance().marshal( dt );
        final String expected = "//from row number: 1\n" +
                "//desc\n" +
                "rule \"Row 1 null\"\n" +
                "dialect \"mvel\"\n" +
                "when\n" +
                "  x : Context( )\n" +
                "then\n" +
                "  modify( x ) {\n" +
                "    setF2( \"v2\" ),\n" +
                "    setF3( \"v3\" )\n" +
                "}\n" +
                "end\n";

        assertEqualsIgnoreWhitespace( expected,
                                      drl );
    }

    @Test
    public void testUpdateModifyMultipleFieldsWithMultipleSkipped3() {
        GuidedDecisionTable52 dt = new GuidedDecisionTable52();

        Pattern52 p1 = new Pattern52();
        p1.setBoundName( "x" );
        p1.setFactType( "Context" );

        ConditionCol52 c = new ConditionCol52();
        c.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        p1.getChildColumns().add( c );
        dt.getConditions().add( p1 );

        BRLActionColumn brlAction1 = new BRLActionColumn();
        ActionUpdateField auf1 = new ActionUpdateField( "x" );
        auf1.addFieldValue( new ActionFieldValue( "f1",
                                                  "$f1",
                                                  DataType.TYPE_STRING ) );
        auf1.getFieldValues()[ 0 ].setNature( BaseSingleFieldConstraint.TYPE_TEMPLATE );

        brlAction1.getDefinition().add( auf1 );
        brlAction1.getChildColumns().add( new BRLActionVariableColumn( "$f1",
                                                                       DataType.TYPE_STRING,
                                                                       "Context",
                                                                       "f1" ) );
        ActionUpdateField auf2 = new ActionUpdateField( "x" );
        auf2.addFieldValue( new ActionFieldValue( "f2",
                                                  "$f2",
                                                  DataType.TYPE_STRING ) );
        auf2.getFieldValues()[ 0 ].setNature( BaseSingleFieldConstraint.TYPE_TEMPLATE );

        brlAction1.getDefinition().add( auf2 );
        brlAction1.getChildColumns().add( new BRLActionVariableColumn( "$f2",
                                                                       DataType.TYPE_STRING,
                                                                       "Context",
                                                                       "f2" ) );

        ActionUpdateField auf3 = new ActionUpdateField( "x" );
        auf3.addFieldValue( new ActionFieldValue( "f3",
                                                  "$f3",
                                                  DataType.TYPE_STRING ) );
        auf3.getFieldValues()[ 0 ].setNature( BaseSingleFieldConstraint.TYPE_TEMPLATE );

        brlAction1.getDefinition().add( auf3 );
        brlAction1.getChildColumns().add( new BRLActionVariableColumn( "$f3",
                                                                       DataType.TYPE_STRING,
                                                                       "Context",
                                                                       "f3" ) );

        dt.getActionCols().add( brlAction1 );

        dt.setData( DataUtilities.makeDataLists( new String[][]{
                new String[]{ "1", "desc", "x", null, null, "v3" }
        } ) );
        String drl = GuidedDTDRLPersistence.getInstance().marshal( dt );
        final String expected = "//from row number: 1\n" +
                "//desc\n" +
                "rule \"Row 1 null\"\n" +
                "dialect \"mvel\"\n" +
                "when\n" +
                "  x : Context( )\n" +
                "then\n" +
                "  modify( x ) {\n" +
                "    setF3( \"v3\" )\n" +
                "}\n" +
                "end\n";

        assertEqualsIgnoreWhitespace( expected,
                                      drl );
    }

    @Test
    public void testUpdateModifyMultipleFieldsWithMultipleSkipped4() {
        GuidedDecisionTable52 dt = new GuidedDecisionTable52();

        Pattern52 p1 = new Pattern52();
        p1.setBoundName( "x" );
        p1.setFactType( "Context" );

        ConditionCol52 c = new ConditionCol52();
        c.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        p1.getChildColumns().add( c );
        dt.getConditions().add( p1 );

        BRLActionColumn brlAction1 = new BRLActionColumn();
        ActionUpdateField auf1 = new ActionUpdateField( "x" );
        auf1.addFieldValue( new ActionFieldValue( "f1",
                                                  "$f1",
                                                  DataType.TYPE_STRING ) );
        auf1.getFieldValues()[ 0 ].setNature( BaseSingleFieldConstraint.TYPE_TEMPLATE );

        brlAction1.getDefinition().add( auf1 );
        brlAction1.getChildColumns().add( new BRLActionVariableColumn( "$f1",
                                                                       DataType.TYPE_STRING,
                                                                       "Context",
                                                                       "f1" ) );
        ActionUpdateField auf2 = new ActionUpdateField( "x" );
        auf2.addFieldValue( new ActionFieldValue( "f2",
                                                  "$f2",
                                                  DataType.TYPE_STRING ) );
        auf2.getFieldValues()[ 0 ].setNature( BaseSingleFieldConstraint.TYPE_TEMPLATE );

        brlAction1.getDefinition().add( auf2 );
        brlAction1.getChildColumns().add( new BRLActionVariableColumn( "$f2",
                                                                       DataType.TYPE_STRING,
                                                                       "Context",
                                                                       "f2" ) );

        ActionUpdateField auf3 = new ActionUpdateField( "x" );
        auf3.addFieldValue( new ActionFieldValue( "f3",
                                                  "$f3",
                                                  DataType.TYPE_STRING ) );
        auf3.getFieldValues()[ 0 ].setNature( BaseSingleFieldConstraint.TYPE_TEMPLATE );

        brlAction1.getDefinition().add( auf3 );
        brlAction1.getChildColumns().add( new BRLActionVariableColumn( "$f3",
                                                                       DataType.TYPE_STRING,
                                                                       "Context",
                                                                       "f3" ) );

        dt.getActionCols().add( brlAction1 );

        dt.setData( DataUtilities.makeDataLists( new String[][]{
                new String[]{ "1", "desc", "x", "v1", null, "v3" }
        } ) );
        String drl = GuidedDTDRLPersistence.getInstance().marshal( dt );
        final String expected = "//from row number: 1\n" +
                "//desc\n" +
                "rule \"Row 1 null\"\n" +
                "dialect \"mvel\"\n" +
                "when\n" +
                "  x : Context( )\n" +
                "then\n" +
                "  modify( x ) {\n" +
                "    setF1( \"v1\" ),\n" +
                "    setF3( \"v3\" )\n" +
                "}\n" +
                "end\n";

        assertEqualsIgnoreWhitespace( expected,
                                      drl );
    }

    @Test
    public void testLHSNonEmptyStringValues() {
        GuidedDecisionTable52 dt = new GuidedDecisionTable52();
        dt.setTableFormat( GuidedDecisionTable52.TableFormat.EXTENDED_ENTRY );
        dt.setTableName( "extended-entry" );

        BRLConditionColumn brlCondition = new BRLConditionColumn();
        FactPattern fp = new FactPattern( "Smurf" );
        fp.setBoundName( "p1" );

        SingleFieldConstraint sfc1 = new SingleFieldConstraint();
        sfc1.setOperator( "==" );
        sfc1.setFactType( "Smurf" );
        sfc1.setFieldName( "name" );
        sfc1.setFieldType( DataType.TYPE_STRING );
        sfc1.setConstraintValueType( SingleFieldConstraint.TYPE_TEMPLATE );
        sfc1.setValue( "$f1" );

        SingleFieldConstraint sfc2 = new SingleFieldConstraint();
        sfc2.setOperator( "==" );
        sfc2.setFactType( "Smurf" );
        sfc2.setFieldName( "age" );
        sfc2.setFieldType( DataType.TYPE_NUMERIC_INTEGER );
        sfc2.setConstraintValueType( SingleFieldConstraint.TYPE_TEMPLATE );
        sfc2.setValue( "$f2" );

        fp.addConstraint( sfc1 );
        fp.addConstraint( sfc2 );

        brlCondition.getDefinition().add( fp );
        brlCondition.getChildColumns().add( new BRLConditionVariableColumn( "$f1",
                                                                            DataType.TYPE_STRING,
                                                                            "Smurf",
                                                                            "name" ) );
        brlCondition.getChildColumns().add( new BRLConditionVariableColumn( "$f2",
                                                                            DataType.TYPE_NUMERIC_INTEGER,
                                                                            "Smurf",
                                                                            "age" ) );

        dt.getConditions().add( brlCondition );

        GuidedDTDRLPersistence p = GuidedDTDRLPersistence.getInstance();

        //Test 1
        dt.setData( DataUtilities.makeDataLists( new Object[][]{
                new Object[]{ 1l, "desc-row1", "Pupa", null },
        } ) );

        String drl1 = p.marshal( dt );
        final String expected1 = "//from row number: 1\n" +
                "//desc-row1\n" +
                "rule \"Row 1 extended-entry\"\n" +
                "  dialect \"mvel\"\n" +
                "  when\n" +
                "    p1 : Smurf( name == \"Pupa\" )\n" +
                "  then\n" +
                "end";

        assertEqualsIgnoreWhitespace( expected1,
                                      drl1 );

        //Test 2
        dt.setData( DataUtilities.makeDataLists( new Object[][]{
                new Object[]{ 2l, "desc-row2", null, 35l },
        } ) );

        String drl2 = p.marshal( dt );
        final String expected2 = "//from row number: 1\n" +
                "//desc-row2\n" +
                "rule \"Row 2 extended-entry\"\n" +
                "  dialect \"mvel\"\n" +
                "  when\n" +
                "    p1 : Smurf( age == 35 )\n" +
                "  then\n" +
                "end";

        assertEqualsIgnoreWhitespace( expected2,
                                      drl2 );

        //Test 3
        dt.setData( DataUtilities.makeDataLists( new Object[][]{
                new Object[]{ 3l, "desc-row3", "Pupa", 35l },
        } ) );

        String drl3 = p.marshal( dt );
        final String expected3 = "//from row number: 1\n" +
                "//desc-row3\n" +
                "rule \"Row 3 extended-entry\"\n" +
                "  dialect \"mvel\"\n" +
                "  when\n" +
                "    p1 : Smurf( name == \"Pupa\", age == 35 )\n" +
                "  then\n" +
                "end";

        assertEqualsIgnoreWhitespace( expected3,
                                      drl3 );

        //Test 4
        dt.setData( DataUtilities.makeDataLists( new Object[][]{
                new Object[]{ 4l, "desc-row4", null, null },
        } ) );

        String drl4 = p.marshal( dt );
        final String expected4 = "//from row number: 1\n" +
                "//desc-row4\n" +
                "rule \"Row 4 extended-entry\"\n" +
                "  dialect \"mvel\"\n" +
                "  when\n" +
                "  then\n" +
                "end";

        assertEqualsIgnoreWhitespace( expected4,
                                      drl4 );
    }

    @Test
    public void testLHSDelimitedNonEmptyStringValues() {
        GuidedDecisionTable52 dt = new GuidedDecisionTable52();
        dt.setTableFormat( GuidedDecisionTable52.TableFormat.EXTENDED_ENTRY );
        dt.setTableName( "extended-entry" );

        BRLConditionColumn brlCondition = new BRLConditionColumn();
        FactPattern fp = new FactPattern( "Smurf" );
        fp.setBoundName( "p1" );

        SingleFieldConstraint sfc1 = new SingleFieldConstraint();
        sfc1.setOperator( "==" );
        sfc1.setFactType( "Smurf" );
        sfc1.setFieldName( "name" );
        sfc1.setFieldType( DataType.TYPE_STRING );
        sfc1.setConstraintValueType( SingleFieldConstraint.TYPE_TEMPLATE );
        sfc1.setValue( "$f1" );

        SingleFieldConstraint sfc2 = new SingleFieldConstraint();
        sfc2.setOperator( "==" );
        sfc2.setFactType( "Smurf" );
        sfc2.setFieldName( "age" );
        sfc2.setFieldType( DataType.TYPE_NUMERIC_INTEGER );
        sfc2.setConstraintValueType( SingleFieldConstraint.TYPE_TEMPLATE );
        sfc2.setValue( "$f2" );

        fp.addConstraint( sfc1 );
        fp.addConstraint( sfc2 );

        brlCondition.getDefinition().add( fp );
        brlCondition.getChildColumns().add( new BRLConditionVariableColumn( "$f1",
                                                                            DataType.TYPE_STRING,
                                                                            "Smurf",
                                                                            "name" ) );
        brlCondition.getChildColumns().add( new BRLConditionVariableColumn( "$f2",
                                                                            DataType.TYPE_NUMERIC_INTEGER,
                                                                            "Smurf",
                                                                            "age" ) );

        dt.getConditions().add( brlCondition );

        GuidedDTDRLPersistence p = GuidedDTDRLPersistence.getInstance();

        //Test 1
        dt.setData( DataUtilities.makeDataLists( new Object[][]{
                new Object[]{ 1l, "desc-row1", null, null },
        } ) );

        String drl1 = p.marshal( dt );
        final String expected1 = "//from row number: 1\n" +
                "//desc-row1\n" +
                "rule \"Row 1 extended-entry\"\n" +
                "  dialect \"mvel\"\n" +
                "  when\n" +
                "  then\n" +
                "end";

        assertEqualsIgnoreWhitespace( expected1,
                                      drl1 );

        //Test 2
        dt.setData( DataUtilities.makeDataLists( new Object[][]{
                new Object[]{ 2l, "desc-row2", "\"   \"", 35l },
        } ) );

        String drl2 = p.marshal( dt );
        final String expected2 = "//from row number: 1\n" +
                "//desc-row2\n" +
                "rule \"Row 2 extended-entry\"\n" +
                "  dialect \"mvel\"\n" +
                "  when\n" +
                "    p1 : Smurf( name == \"   \", age == 35 )\n" +
                "  then\n" +
                "end";

        assertEqualsIgnoreWhitespace( expected2,
                                      drl2 );

        //Test 3
        dt.setData( DataUtilities.makeDataLists( new Object[][]{
                new Object[]{ 3l, "desc-row3", "\"\"", null },
        } ) );

        String drl3 = p.marshal( dt );
        final String expected3 = "//from row number: 1\n" +
                "//desc-row3\n" +
                "rule \"Row 3 extended-entry\"\n" +
                "  dialect \"mvel\"\n" +
                "  when\n" +
                "    p1 : Smurf( name == \"\" )\n" +
                "  then\n" +
                "end";

        assertEqualsIgnoreWhitespace( expected3,
                                      drl3 );

        //Test 4
        dt.setData( DataUtilities.makeDataLists( new Object[][]{
                new Object[]{ 4l, "desc-row4", "\"\"", 35l },
        } ) );

        String drl4 = p.marshal( dt );
        final String expected4 = "//from row number: 1\n" +
                "//desc-row4\n" +
                "rule \"Row 4 extended-entry\"\n" +
                "  dialect \"mvel\"\n" +
                "  when\n" +
                "    p1 : Smurf( name == \"\", age == 35 )\n" +
                "  then\n" +
                "end";

        assertEqualsIgnoreWhitespace( expected4,
                                      drl4 );
    }

    @Test
    public void testLHSNonEmptyStringValuesFreeFormLine() {
        GuidedDecisionTable52 dt = new GuidedDecisionTable52();
        dt.setTableFormat( GuidedDecisionTable52.TableFormat.EXTENDED_ENTRY );
        dt.setTableName( "extended-entry" );

        BRLConditionColumn brlCondition = new BRLConditionColumn();
        FreeFormLine ffl = new FreeFormLine();
        ffl.setText( "p1 : Smurf( name ==\"@{$f1}\", age == @{$f2} )" );

        brlCondition.getDefinition().add( ffl );
        brlCondition.getChildColumns().add( new BRLConditionVariableColumn( "$f1",
                                                                            DataType.TYPE_STRING,
                                                                            "Smurf",
                                                                            "name" ) );
        brlCondition.getChildColumns().add( new BRLConditionVariableColumn( "$f2",
                                                                            DataType.TYPE_NUMERIC_INTEGER,
                                                                            "Smurf",
                                                                            "age" ) );

        dt.getConditions().add( brlCondition );

        GuidedDTDRLPersistence p = GuidedDTDRLPersistence.getInstance();

        //Test 1
        dt.setData( DataUtilities.makeDataLists( new Object[][]{
                new Object[]{ 1l, "desc-row1", "Pupa", null },
        } ) );

        String drl1 = p.marshal( dt );
        final String expected1 = "//from row number: 1\n" +
                "//desc-row1\n" +
                "rule \"Row 1 extended-entry\"\n" +
                "  dialect \"mvel\"\n" +
                "  when\n" +
                "  then\n" +
                "end";

        assertEqualsIgnoreWhitespace( expected1,
                                      drl1 );

        //Test 2
        dt.setData( DataUtilities.makeDataLists( new Object[][]{
                new Object[]{ 2l, "desc-row2", null, 35l },
        } ) );

        String drl2 = p.marshal( dt );
        final String expected2 = "//from row number: 1\n" +
                "//desc-row2\n" +
                "rule \"Row 2 extended-entry\"\n" +
                "  dialect \"mvel\"\n" +
                "  when\n" +
                "  then\n" +
                "end";

        assertEqualsIgnoreWhitespace( expected2,
                                      drl2 );

        //Test 3
        dt.setData( DataUtilities.makeDataLists( new Object[][]{
                new Object[]{ 3l, "desc-row3", "Pupa", 35l },
        } ) );

        String drl3 = p.marshal( dt );
        final String expected3 = "//from row number: 1\n" +
                "//desc-row3\n" +
                "rule \"Row 3 extended-entry\"\n" +
                "  dialect \"mvel\"\n" +
                "  when\n" +
                "    p1 : Smurf( name == \"Pupa\", age == 35 )\n" +
                "  then\n" +
                "end";

        assertEqualsIgnoreWhitespace( expected3,
                                      drl3 );

        //Test 4
        dt.setData( DataUtilities.makeDataLists( new Object[][]{
                new Object[]{ 4l, "desc-row4", null, null },
        } ) );

        String drl4 = p.marshal( dt );
        final String expected4 = "//from row number: 1\n" +
                "//desc-row4\n" +
                "rule \"Row 4 extended-entry\"\n" +
                "  dialect \"mvel\"\n" +
                "  when\n" +
                "  then\n" +
                "end";

        assertEqualsIgnoreWhitespace( expected4,
                                      drl4 );
    }

    @Test
    public void testRHSNonEmptyStringValues() {
        GuidedDecisionTable52 dt = new GuidedDecisionTable52();
        dt.setTableFormat( GuidedDecisionTable52.TableFormat.EXTENDED_ENTRY );
        dt.setTableName( "extended-entry" );

        Pattern52 p1 = new Pattern52();
        p1.setBoundName( "p1" );
        p1.setFactType( "Smurf" );

        BRLActionColumn brlAction = new BRLActionColumn();
        ActionUpdateField auf1 = new ActionUpdateField( "p1" );
        auf1.addFieldValue( new ActionFieldValue( "name",
                                                  "$name",
                                                  DataType.TYPE_STRING ) );
        auf1.getFieldValues()[ 0 ].setNature( BaseSingleFieldConstraint.TYPE_TEMPLATE );

        ActionUpdateField auf2 = new ActionUpdateField( "p1" );
        auf2.addFieldValue( new ActionFieldValue( "age",
                                                  "$age",
                                                  DataType.TYPE_NUMERIC_INTEGER ) );
        auf2.getFieldValues()[ 0 ].setNature( BaseSingleFieldConstraint.TYPE_TEMPLATE );

        brlAction.getDefinition().add( auf1 );
        brlAction.getDefinition().add( auf2 );

        brlAction.getChildColumns().add( new BRLActionVariableColumn( "$name",
                                                                      DataType.TYPE_STRING,
                                                                      "Smurf",
                                                                      "name" ) );
        brlAction.getChildColumns().add( new BRLActionVariableColumn( "$age",
                                                                      DataType.TYPE_NUMERIC_INTEGER,
                                                                      "Smurf",
                                                                      "age" ) );

        dt.getActionCols().add( brlAction );

        //Test 1
        dt.setData( DataUtilities.makeDataLists( new Object[][]{
                new Object[]{ 1l, "desc-row1", null, null },
        } ) );

        String drl1 = GuidedDTDRLPersistence.getInstance().marshal( dt );
        final String expected1 = "//from row number: 1\n" +
                "//desc-row1\n" +
                "rule \"Row 1 extended-entry\"\n" +
                "  dialect \"mvel\"\n" +
                "  when\n" +
                "  then\n" +
                "end";

        assertEqualsIgnoreWhitespace( expected1,
                                      drl1 );

        //Test 2
        dt.setData( DataUtilities.makeDataLists( new Object[][]{
                new Object[]{ 2l, "desc-row2", "   ", 35l },
        } ) );

        String drl2 = GuidedDTDRLPersistence.getInstance().marshal( dt );
        final String expected2 = "//from row number: 1\n" +
                "//desc-row2\n" +
                "rule \"Row 2 extended-entry\"\n" +
                "  dialect \"mvel\"\n" +
                "  when\n" +
                "  then\n" +
                "    modify( p1 ) {\n" +
                "      setAge( 35 )\n" +
                "    }\n" +
                "end";

        assertEqualsIgnoreWhitespace( expected2,
                                      drl2 );

        //Test 3
        dt.setData( DataUtilities.makeDataLists( new Object[][]{
                new Object[]{ 3l, "desc-row3", "", null },
        } ) );

        String drl3 = GuidedDTDRLPersistence.getInstance().marshal( dt );
        final String expected3 = "//from row number: 1\n" +
                "//desc-row3\n" +
                "rule \"Row 3 extended-entry\"\n" +
                "  dialect \"mvel\"\n" +
                "  when\n" +
                "  then\n" +
                "end";

        assertEqualsIgnoreWhitespace( expected3,
                                      drl3 );

        //Test 4
        dt.setData( DataUtilities.makeDataLists( new Object[][]{
                new Object[]{ 4l, "desc-row4", "", 35l },
        } ) );

        String drl4 = GuidedDTDRLPersistence.getInstance().marshal( dt );
        final String expected4 = "//from row number: 1\n" +
                "//desc-row4\n" +
                "rule \"Row 4 extended-entry\"\n" +
                "  dialect \"mvel\"\n" +
                "  when\n" +
                "  then\n" +
                "    modify( p1 ) {\n" +
                "      setAge( 35 )\n" +
                "    }\n" +
                "end";

        assertEqualsIgnoreWhitespace( expected4,
                                      drl4 );
    }

    @Test
    public void testRHSDelimitedNonEmptyStringValues() {
        GuidedDecisionTable52 dt = new GuidedDecisionTable52();
        dt.setTableFormat( GuidedDecisionTable52.TableFormat.EXTENDED_ENTRY );
        dt.setTableName( "extended-entry" );

        Pattern52 p1 = new Pattern52();
        p1.setBoundName( "p1" );
        p1.setFactType( "Smurf" );

        BRLActionColumn brlAction = new BRLActionColumn();
        ActionUpdateField auf1 = new ActionUpdateField( "p1" );
        auf1.addFieldValue( new ActionFieldValue( "name",
                                                  "$name",
                                                  DataType.TYPE_STRING ) );
        auf1.getFieldValues()[ 0 ].setNature( BaseSingleFieldConstraint.TYPE_TEMPLATE );

        ActionUpdateField auf2 = new ActionUpdateField( "p1" );
        auf2.addFieldValue( new ActionFieldValue( "age",
                                                  "$age",
                                                  DataType.TYPE_NUMERIC_INTEGER ) );
        auf2.getFieldValues()[ 0 ].setNature( BaseSingleFieldConstraint.TYPE_TEMPLATE );

        brlAction.getDefinition().add( auf1 );
        brlAction.getDefinition().add( auf2 );

        brlAction.getChildColumns().add( new BRLActionVariableColumn( "$name",
                                                                      DataType.TYPE_STRING,
                                                                      "Smurf",
                                                                      "name" ) );
        brlAction.getChildColumns().add( new BRLActionVariableColumn( "$age",
                                                                      DataType.TYPE_NUMERIC_INTEGER,
                                                                      "Smurf",
                                                                      "age" ) );

        dt.getActionCols().add( brlAction );

        //Test 1
        dt.setData( DataUtilities.makeDataLists( new Object[][]{
                new Object[]{ 1l, "desc-row1", null, null },
        } ) );

        String drl1 = GuidedDTDRLPersistence.getInstance().marshal( dt );
        final String expected1 = "//from row number: 1\n" +
                "//desc-row1\n" +
                "rule \"Row 1 extended-entry\"\n" +
                "  dialect \"mvel\"\n" +
                "  when\n" +
                "  then\n" +
                "end";

        assertEqualsIgnoreWhitespace( expected1,
                                      drl1 );

        //Test 2
        dt.setData( DataUtilities.makeDataLists( new Object[][]{
                new Object[]{ 2l, "desc-row2", "\"   \"", 35l },
        } ) );

        String drl2 = GuidedDTDRLPersistence.getInstance().marshal( dt );
        final String expected2 = "//from row number: 1\n" +
                "//desc-row2\n" +
                "rule \"Row 2 extended-entry\"\n" +
                "  dialect \"mvel\"\n" +
                "  when\n" +
                "  then\n" +
                "    modify( p1 ) {\n" +
                "      setName( \"   \" ),\n" +
                "      setAge( 35 )\n" +
                "    }\n" +
                "end";

        assertEqualsIgnoreWhitespace( expected2,
                                      drl2 );

        //Test 3
        dt.setData( DataUtilities.makeDataLists( new Object[][]{
                new Object[]{ 3l, "desc-row3", "\"\"", null },
        } ) );

        String drl3 = GuidedDTDRLPersistence.getInstance().marshal( dt );
        final String expected3 = "//from row number: 1\n" +
                "//desc-row3\n" +
                "rule \"Row 3 extended-entry\"\n" +
                "  dialect \"mvel\"\n" +
                "  when\n" +
                "  then\n" +
                "    modify( p1 ) {\n" +
                "      setName( \"\" )\n" +
                "    }\n" +
                "end";

        assertEqualsIgnoreWhitespace( expected3,
                                      drl3 );

        //Test 4
        dt.setData( DataUtilities.makeDataLists( new Object[][]{
                new Object[]{ 4l, "desc-row4", "\"\"", 35l },
        } ) );

        String drl4 = GuidedDTDRLPersistence.getInstance().marshal( dt );
        final String expected4 = "//from row number: 1\n" +
                "//desc-row4\n" +
                "rule \"Row 4 extended-entry\"\n" +
                "  dialect \"mvel\"\n" +
                "  when\n" +
                "  then\n" +
                "    modify( p1 ) {\n" +
                "      setName( \"\" ),\n" +
                "      setAge( 35 )\n" +
                "    }\n" +
                "end";

        assertEqualsIgnoreWhitespace( expected4,
                                      drl4 );
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
