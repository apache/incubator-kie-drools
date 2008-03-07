package org.drools.brms.server.util;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.drools.brms.client.modeldriven.SuggestionCompletionEngine;
import org.drools.brms.client.modeldriven.brl.FactPattern;
import org.drools.brms.client.modeldriven.brl.ISingleFieldConstraint;
import org.drools.brms.client.modeldriven.brl.RuleAttribute;
import org.drools.brms.client.modeldriven.brl.RuleModel;
import org.drools.brms.client.modeldriven.brl.SingleFieldConstraint;
import org.drools.brms.client.modeldriven.dt.ActionInsertFactCol;
import org.drools.brms.client.modeldriven.dt.ActionRetractFactCol;
import org.drools.brms.client.modeldriven.dt.ActionSetFieldCol;
import org.drools.brms.client.modeldriven.dt.AttributeCol;
import org.drools.brms.client.modeldriven.dt.ConditionCol;
import org.drools.brms.client.modeldriven.dt.GuidedDecisionTable;

public class GuidedDTBRLPersistenceTest extends TestCase {


	public void testOneRule() throws Exception {
		GuidedDecisionTable dt = new GuidedDecisionTable();
		dt.tableName = "michael";

		AttributeCol attr = new AttributeCol();
		attr.attr = "salience";
		dt.attributeCols.add(attr);

		ConditionCol con = new ConditionCol();
		con.boundName = "f1";
		con.constraintValueType = ISingleFieldConstraint.TYPE_LITERAL;
		con.factField = "age";
		con.factType = "Driver";
		con.header = "Driver f1 age";
		con.operator = "==";
		dt.conditionCols.add(con);

		ConditionCol con2 = new ConditionCol();
		con2.boundName = "f1";
		con2.constraintValueType = ISingleFieldConstraint.TYPE_LITERAL;
		con2.factField = "name";
		con2.factType = "Driver";
		con2.header = "Driver f1 name";
		con2.operator = "==";
		dt.conditionCols.add(con2);

		ConditionCol con3 = new ConditionCol();
		con3.boundName = "f1";
		con3.constraintValueType = ISingleFieldConstraint.TYPE_RET_VALUE;
		con3.factField = "rating";
		con3.factType = "Driver";
		con3.header = "Driver rating";
		con3.operator = "==";
		dt.conditionCols.add(con3);


		ConditionCol con4 = new ConditionCol();
		con4.boundName = "f2";
		con4.constraintValueType = ISingleFieldConstraint.TYPE_PREDICATE;
		con4.factType = "Driver";
		con4.header = "Driver 2 pimp";
		dt.conditionCols.add(con4);


		ActionInsertFactCol ins = new ActionInsertFactCol();
		ins.boundName = "ins";
		ins.type = "Cheese";
		ins.factField = "price";
		ins.type = SuggestionCompletionEngine.TYPE_NUMERIC;
		dt.actionCols.add(ins);

		ActionRetractFactCol ret = new ActionRetractFactCol();
		ret.boundName = "f2";
		dt.actionCols.add(ret);

		ActionSetFieldCol set = new ActionSetFieldCol();
		set.boundName = "f1";
		set.factField = "goo1";
		set.type = SuggestionCompletionEngine.TYPE_STRING;
		dt.actionCols.add(set);

		ActionSetFieldCol set2 = new ActionSetFieldCol();
		set2.boundName = "f1";
		set2.factField = "goo2";
		set2.type = SuggestionCompletionEngine.TYPE_STRING;
		dt.actionCols.add(set2);


		dt.data = new String[][] {
				new String[] {"1", "desc", "42", "33", "michael", "age * 0.2", "age > 7", "6.60", "true", "gooVal1", "gooVal2"},
				new String[] {"2", "desc", "", "39", "bob", "age * 0.3", "age > 7", "6.60", "", "gooVal1", "gooVal2"}
		};








	}

	public void testCellVal() {
		GuidedDTBRLPersistence p = new GuidedDTBRLPersistence();
		assertFalse(p.validCell(null));
		assertFalse(p.validCell(""));
		assertFalse(p.validCell("  "));

	}

	public void testName() {
		GuidedDTBRLPersistence p = new GuidedDTBRLPersistence();
		assertEquals("42_hey", p.getName("XXX", "42", "hey"));
		assertEquals("42_XXX", p.getName("XXX", "42", ""));
	}

	public void testAttribs() {
		GuidedDTBRLPersistence p = new GuidedDTBRLPersistence();
		String[] row = new String[] {"1", "desc", "a", ""};

		List<AttributeCol> attributeCols = new ArrayList<AttributeCol>();
		RuleModel rm = new RuleModel();
		RuleAttribute[] orig = rm.attributes;
		p.doAttribs(attributeCols, row, rm);

		assertSame(orig, rm.attributes);

		AttributeCol col1 = new AttributeCol();
		col1.attr = "salience";
		AttributeCol col2 = new AttributeCol();
		col2.attr = "agenda-group";
		attributeCols.add(col1);
		attributeCols.add(col2);

		p.doAttribs(attributeCols, row, rm);

		assertEquals(1, rm.attributes.length);
		assertEquals("salience", rm.attributes[0].attributeName);
		assertEquals("a", rm.attributes[0].value);

		row = new String[] {"1", "desc", "a", "b"};
		p.doAttribs(attributeCols, row, rm);
		assertEquals(2, rm.attributes.length);
		assertEquals("salience", rm.attributes[0].attributeName);
		assertEquals("a", rm.attributes[0].value);
		assertEquals("agenda-group", rm.attributes[1].attributeName);
		assertEquals("b", rm.attributes[1].value);

	}

	public void testLHS() {
		GuidedDTBRLPersistence p = new GuidedDTBRLPersistence();
		String[] row = new String[] {"1", "desc", "a", "mike", "33 + 1", "age > 6", "stilton"};

		List<ConditionCol> cols = new ArrayList<ConditionCol>();
		ConditionCol col = new ConditionCol();
		col.boundName = "p1";
		col.factType = "Person";
		col.factField = "name";
		col.constraintValueType = ISingleFieldConstraint.TYPE_LITERAL;
		col.operator = "==";
		cols.add(col);

		ConditionCol col2 = new ConditionCol();
		col2.boundName = "p1";
		col2.factType = "Person";
		col2.factField = "age";
		col2.constraintValueType = ISingleFieldConstraint.TYPE_RET_VALUE;
		col2.operator = "<";
		cols.add(col2);

		ConditionCol col3 = new ConditionCol();
		col3.boundName = "p1";
		col3.factType = "Person";
		col3.constraintValueType = ISingleFieldConstraint.TYPE_PREDICATE;
		cols.add(col3);

		ConditionCol col4 = new ConditionCol();
		col4.boundName = "c";
		col4.factType = "Cheese";
		col4.factField = "type";
		col4.operator = "==";
		col4.constraintValueType = ISingleFieldConstraint.TYPE_LITERAL;
		cols.add(col4);

		RuleModel rm = new RuleModel();

		p.doConditions(1, cols, row, rm);
		assertEquals(2, rm.lhs.length);

		assertEquals("Person", ((FactPattern)rm.lhs[0]).factType);
		assertEquals("p1", ((FactPattern)rm.lhs[0]).boundName);

		assertEquals("Cheese", ((FactPattern)rm.lhs[1]).factType);
		assertEquals("c", ((FactPattern)rm.lhs[1]).boundName);

		//examine the first pattern
		FactPattern person = (FactPattern) rm.lhs[0];
		assertEquals(3, person.constraintList.constraints.length);
		SingleFieldConstraint cons = (SingleFieldConstraint) person.constraintList.constraints[0];
		assertEquals(ISingleFieldConstraint.TYPE_LITERAL, cons.constraintValueType);
		assertEquals("name", cons.fieldName);
		assertEquals("==", cons.operator);
		assertEquals("mike", cons.value);

		cons = (SingleFieldConstraint) person.constraintList.constraints[1];
		assertEquals(ISingleFieldConstraint.TYPE_RET_VALUE, cons.constraintValueType);
		assertEquals("age", cons.fieldName);
		assertEquals("<", cons.operator);
		assertEquals("33 + 1", cons.value);

		cons = (SingleFieldConstraint) person.constraintList.constraints[2];
		assertEquals(ISingleFieldConstraint.TYPE_PREDICATE, cons.constraintValueType);
		assertEquals("age > 6", cons.value);


		//examine the second pattern
		FactPattern cheese = (FactPattern) rm.lhs[1];
		assertEquals(1, cheese.constraintList.constraints.length);
		cons = (SingleFieldConstraint) cheese.constraintList.constraints[0];
		assertEquals("type", cons.fieldName);
		assertEquals("==", cons.operator);
		assertEquals("stilton", cons.value);
		assertEquals(ISingleFieldConstraint.TYPE_LITERAL, cons.constraintValueType);
	}


}
