package org.drools.guvnor.modeldriven.dt;

import java.util.HashMap;

import junit.framework.TestCase;
import org.drools.guvnor.client.modeldriven.ModelField;

import org.drools.guvnor.client.modeldriven.SuggestionCompletionEngine;
import org.drools.guvnor.client.modeldriven.ModelField.FIELD_CLASS_TYPE;
import org.drools.guvnor.client.modeldriven.brl.ISingleFieldConstraint;
import org.drools.guvnor.client.modeldriven.dt.ActionInsertFactCol;
import org.drools.guvnor.client.modeldriven.dt.ActionSetFieldCol;
import org.drools.guvnor.client.modeldriven.dt.AttributeCol;
import org.drools.guvnor.client.modeldriven.dt.ConditionCol;
import org.drools.guvnor.client.modeldriven.dt.GuidedDecisionTable;

public class GuidedDecisionTableTest extends TestCase {

//	public void testGetCol() {
//		GuidedDecisionTable dt = new GuidedDecisionTable();
//		AttributeCol at1 = new AttributeCol();
//		ConditionCol condition1 = new ConditionCol();
//		ActionCol action1 = new ActionInsertFactCol();
//		dt.attributeCols.add(at1);
//		dt.conditionCols.add(condition1);
//		dt.actionCols.add(action1);
//
//		assertEquals(at1, dt.getColumnConfiguration(0));
//		assertEquals(condition1, dt.getColumnConfiguration(1));
//		assertEquals(action1, dt.getColumnConfiguration(2));
//
//
//		AttributeCol at2 = new AttributeCol();
//		dt.attributeCols.add(at2);
//		assertEquals(at1, dt.getColumnConfiguration(0));
//		assertEquals(at2, dt.getColumnConfiguration(1));
//		assertEquals(condition1, dt.getColumnConfiguration(2));
//		assertEquals(action1, dt.getColumnConfiguration(3));
//
//		ConditionCol condition2 = new ConditionCol();
//		dt.conditionCols.add(condition2);
//
//		assertEquals(at1, dt.getColumnConfiguration(0));
//		assertEquals(at2, dt.getColumnConfiguration(1));
//		assertEquals(condition1, dt.getColumnConfiguration(2));
//		assertEquals(condition2, dt.getColumnConfiguration(3));
//		assertEquals(action1, dt.getColumnConfiguration(4));
//
//		ActionCol action2 = new ActionInsertFactCol();
//		dt.actionCols.add(action2);
//		assertEquals(at1, dt.getColumnConfiguration(0));
//		assertEquals(at2, dt.getColumnConfiguration(1));
//		assertEquals(condition1, dt.getColumnConfiguration(2));
//		assertEquals(condition2, dt.getColumnConfiguration(3));
//		assertEquals(action1, dt.getColumnConfiguration(4));
//		assertEquals(action2, dt.getColumnConfiguration(5));
//
//
//
//		dt.attributeCols = new ArrayList();
//		assertEquals(condition1, dt.getColumnConfiguration(0));
//		assertEquals(condition2, dt.getColumnConfiguration(1));
//		assertEquals(action1, dt.getColumnConfiguration(2));
//		assertEquals(action2, dt.getColumnConfiguration(3));
//
//		dt.conditionCols = new ArrayList();
//		assertEquals(action1, dt.getColumnConfiguration(0));
//		assertEquals(action2, dt.getColumnConfiguration(1));
//
//
//
//
//
//
//	}
    public void testValueLists() {
        GuidedDecisionTable dt = new GuidedDecisionTable();

        //add cols for LHS
        ConditionCol c1 = new ConditionCol();
        c1.boundName = "c1";
        c1.factType = "Driver";
        c1.factField = "name";
        c1.constraintValueType = ISingleFieldConstraint.TYPE_LITERAL;
        dt.conditionCols.add(c1);

        ConditionCol c1_ = new ConditionCol();
        c1_.boundName = "c1";
        c1_.factType = "Driver";
        c1_.factField = "name";
        c1_.constraintValueType = ISingleFieldConstraint.TYPE_RET_VALUE;

        dt.conditionCols.add(c1_);

        ConditionCol c1__ = new ConditionCol();
        c1__.boundName = "c1";
        c1__.factType = "Driver";
        c1__.factField = "name";
        c1__.constraintValueType = ISingleFieldConstraint.TYPE_LITERAL;
        c1__.valueList = "one,two,three";
        dt.conditionCols.add(c1__);

        ConditionCol c2 = new ConditionCol();
        c2.boundName = "c2";
        c2.factType = "Driver";
        c2.factField = "nothing";
        c2.constraintValueType = ISingleFieldConstraint.TYPE_LITERAL;
        dt.conditionCols.add(c2);


        ActionSetFieldCol asf = new ActionSetFieldCol();
        asf.boundName = "c1";
        asf.factField = "name";
        dt.actionCols.add(asf);

        ActionInsertFactCol ins = new ActionInsertFactCol();
        ins.boundName = "x";
        ins.factField = "rating";
        ins.factType = "Person";
        dt.actionCols.add(ins);

        ActionInsertFactCol ins_ = new ActionInsertFactCol();
        ins_.boundName = "x";
        ins_.factField = "rating";
        ins_.factType = "Person";
        ins_.valueList = "one,two,three";
        dt.actionCols.add(ins_);

        ActionSetFieldCol asf_ = new ActionSetFieldCol();
        asf_.boundName = "c1";
        asf_.factField = "goo";
        dt.actionCols.add(asf_);

        ActionSetFieldCol asf__ = new ActionSetFieldCol();
        asf__.boundName = "c1";
        asf__.factField = "goo";
        asf__.valueList = "one,two,three";
        dt.actionCols.add(asf__);


        SuggestionCompletionEngine sce = new SuggestionCompletionEngine();
        sce.putDataEnumList("Driver.name", new String[]{"bob", "michael"});
        sce.putDataEnumList("Person.rating", new String[]{"1", "2"});




        String[] r = dt.getValueList(c1, sce);
        assertEquals(2, r.length);
        assertEquals("bob", r[0]);
        assertEquals("michael", r[1]);

        assertEquals(0, dt.getValueList(c1_, sce).length);

        r = dt.getValueList(c1__, sce);
        assertEquals(3, r.length);
        assertEquals("one", r[0]);
        assertEquals("two", r[1]);
        assertEquals("three", r[2]);

        assertEquals(0, dt.getValueList(c2, sce).length);

        r = dt.getValueList(asf, sce);
        assertEquals(2, r.length);
        assertEquals("bob", r[0]);
        assertEquals("michael", r[1]);

        r = dt.getValueList(ins, sce);
        assertEquals(2, r.length);
        assertEquals("1", r[0]);
        assertEquals("2", r[1]);

        r = dt.getValueList(ins_, sce);
        assertEquals(3, r.length);
        assertEquals("one", r[0]);
        assertEquals("two", r[1]);
        assertEquals("three", r[2]);

        assertEquals(0, dt.getValueList(asf_, sce).length);


        r = dt.getValueList(asf__, sce);
        assertEquals(3, r.length);
        assertEquals("one", r[0]);
        assertEquals("two", r[1]);
        assertEquals("three", r[2]);

        AttributeCol at = new AttributeCol();
        at.attr = "no-loop";
        dt.attributeCols.add(at);

        r = dt.getValueList(at, sce);
        assertEquals(2, r.length);
        assertEquals("true", r[0]);
        assertEquals("false", r[1]);

        at.attr = "enabled";
        assertEquals(2, dt.getValueList(at, sce).length);

        at.attr = "salience";
        assertEquals(0, dt.getValueList(at, sce).length);

    }

    public void testNumeric() {
        SuggestionCompletionEngine sce = new SuggestionCompletionEngine();

        sce.setFieldsForTypes(new HashMap<String, ModelField[]>() {
            {
                put("Driver",
                        new ModelField[]{
                            new ModelField("age", Integer.class.getName(), FIELD_CLASS_TYPE.REGULAR_CLASS, SuggestionCompletionEngine.TYPE_NUMERIC),
                            new ModelField("name", String.class.getName(), FIELD_CLASS_TYPE.REGULAR_CLASS, SuggestionCompletionEngine.TYPE_STRING)
                        });
            }
        });

        GuidedDecisionTable dt = new GuidedDecisionTable();

        AttributeCol at = new AttributeCol();
        at.attr = "salience";
        AttributeCol at_ = new AttributeCol();
        at_.attr = "enabled";

        dt.attributeCols.add(at);
        dt.attributeCols.add(at_);

        ConditionCol c1 = new ConditionCol();
        c1.boundName = "c1";
        c1.factType = "Driver";
        c1.factField = "name";
        c1.operator = "==";
        c1.constraintValueType = ISingleFieldConstraint.TYPE_LITERAL;
        dt.conditionCols.add(c1);

        ConditionCol c1_ = new ConditionCol();
        c1_.boundName = "c1";
        c1_.factType = "Driver";
        c1_.factField = "age";
        c1_.operator = "==";
        c1_.constraintValueType = ISingleFieldConstraint.TYPE_LITERAL;
        dt.conditionCols.add(c1_);

        ConditionCol c2 = new ConditionCol();
        c2.boundName = "c1";
        c2.factType = "Driver";
        c2.factField = "age";
        c2.constraintValueType = ISingleFieldConstraint.TYPE_LITERAL;
        dt.conditionCols.add(c2);

        ActionSetFieldCol a = new ActionSetFieldCol();
        a.boundName = "c1";
        a.factField = "name";
        dt.actionCols.add(a);

        ActionSetFieldCol a2 = new ActionSetFieldCol();
        a2.boundName = "c1";
        a2.factField = "age";
        dt.actionCols.add(a2);

        ActionInsertFactCol ins = new ActionInsertFactCol();
        ins.boundName = "x";
        ins.factType = "Driver";
        ins.factField = "name";
        dt.actionCols.add(ins);

        ActionInsertFactCol ins_ = new ActionInsertFactCol();
        ins_.boundName = "x";
        ins_.factType = "Driver";
        ins_.factField = "age";
        dt.actionCols.add(ins_);

        assertTrue(dt.isNumeric(at, sce));
        assertFalse(dt.isNumeric(at_, sce));
        assertFalse(dt.isNumeric(c1, sce));
        assertTrue(dt.isNumeric(c1_, sce));
        assertFalse(dt.isNumeric(a, sce));
        assertTrue(dt.isNumeric(a2, sce));
        assertFalse(dt.isNumeric(ins, sce));
        assertTrue(dt.isNumeric(ins_, sce));
        assertFalse(dt.isNumeric(c2, sce));



    }

    public void testNoConstraintLists() {
        GuidedDecisionTable dt = new GuidedDecisionTable();

        //add cols for LHS
        ConditionCol c1 = new ConditionCol();
        c1.boundName = "c1";
        c1.factType = "Driver";
        c1.constraintValueType = ISingleFieldConstraint.TYPE_LITERAL;
        dt.conditionCols.add(c1);

        ConditionCol c2 = new ConditionCol();
        c2.boundName = "c2";
        c2.factType = "Driver";
        c2.constraintValueType = ISingleFieldConstraint.TYPE_LITERAL;
        c2.valueList = "a,b,c";
        dt.conditionCols.add(c2);


        SuggestionCompletionEngine sce = new SuggestionCompletionEngine();
        sce.putDataEnumList("Driver.name", new String[]{"bob", "michael"});

        assertEquals(0, dt.getValueList(c1, sce).length);
        assertEquals(3, dt.getValueList(c2, sce).length);


    }

    public void testNoConstraintsNumeric() {
        GuidedDecisionTable dt = new GuidedDecisionTable();

        //add cols for LHS
        ConditionCol c1 = new ConditionCol();
        c1.boundName = "c1";
        c1.factType = "Driver";
        c1.constraintValueType = ISingleFieldConstraint.TYPE_LITERAL;
        dt.conditionCols.add(c1);

        SuggestionCompletionEngine sce = new SuggestionCompletionEngine();

        sce.setFieldsForTypes(new HashMap<String, ModelField[]>() {
            {
                put("Driver",
                        new ModelField[]{
                            new ModelField("age", Integer.class.getName(), FIELD_CLASS_TYPE.REGULAR_CLASS, SuggestionCompletionEngine.TYPE_NUMERIC),
                            new ModelField("name", String.class.getName(), FIELD_CLASS_TYPE.REGULAR_CLASS, SuggestionCompletionEngine.TYPE_STRING)
                        });
            }
        });

        assertFalse(dt.isNumeric(c1, sce));

    }
}
